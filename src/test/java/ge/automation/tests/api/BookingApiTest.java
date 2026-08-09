package ge.automation.tests.api;

import ge.automation.api.AuthClient;
import ge.automation.config.ConfigReader;
import ge.automation.utils.RandomDataGenerator;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BookingApiTest {

    private AuthClient authClient;

    @BeforeClass(alwaysRun = true)
    public void authorizeOnce() {
        authClient = new AuthClient().authorize();
        System.out.println("\n🔑  ტოკენი მიღებულია: " + authClient.getToken());
    }

    @Test(priority = 1,
          groups = {"smoke", "api"},
          description = "POST /auth აბრუნებს ვალიდურ ტოკენს")
    public void authReturnsValidToken() {
        String token = authClient.getToken();

        Assert.assertNotNull(token, "ტოკენი null-ია");
        Assert.assertTrue(token.length() >= 10, "ტოკენი ეჭვიანად მოკლეა: " + token);

        System.out.println("      ტოკენის სიგრძე: " + token.length());
    }

    @Test(priority = 2,
          groups = {"smoke", "api"},
          description = "GET /booking/{id} — პასუხის მონაცემების და სტატუს კოდის ვალიდაცია")
    public void getSingleBookingValidatesResponseData() {
        String firstName = RandomDataGenerator.firstName();
        String lastName = RandomDataGenerator.lastName();
        int price = RandomDataGenerator.price();

        int bookingId = createBooking(firstName, lastName, price);

        authClient.publicRequest()
                .pathParam("id", bookingId)
                .when()
                    .get(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("firstname", equalTo(firstName))
                    .body("lastname", equalTo(lastName))
                    .body("totalprice", equalTo(price))
                    .body("depositpaid", equalTo(true))
                    .body("bookingdates.checkin", notNullValue())
                    .body("bookingdates.checkout", notNullValue());

        System.out.println("      ჯავშანი " + bookingId + " — ყველა ველი ვალიდურია ✓");
        deleteBooking(bookingId);
    }

    @Test(priority = 3,
          groups = {"regression", "api"},
          description = "POST /booking — body იკითხება JSON ფაილიდან")
    public void createBookingFromJsonFile() {
        String jsonBody = readResourceFile("testdata/booking.json");

        int id = authClient.publicRequest()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                    .post(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)
                    .body("bookingid", notNullValue())
                    .body("booking.firstname", equalTo("Ana"))
                    .body("booking.lastname", equalTo("Varsimashvili"))
                    .body("booking.totalprice", equalTo(250))
                    .extract().jsonPath().getInt("bookingid");

        System.out.println("      ფაილიდან შეიქმნა ჯავშანი id=" + id);

        Assert.assertTrue(id > 0, "bookingid არასწორია");
        deleteBooking(id);
    }

    @Test(priority = 4,
          groups = {"regression", "api"},
          description = "PUT /booking/{id} ტოკენით — ჯავშნის განახლება")
    public void updateBookingWithToken() {
        int bookingId = createBooking("Old", "Name", 100);

        String newFirstName = RandomDataGenerator.firstName();
        String newLastName = RandomDataGenerator.lastName();
        int newPrice = RandomDataGenerator.price();

        authClient.authorizedRequest()
                .pathParam("id", bookingId)
                .body(bookingBody(newFirstName, newLastName, newPrice))
                .when()
                    .put(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .statusCode(200)
                    .body("firstname", equalTo(newFirstName))
                    .body("lastname", equalTo(newLastName))
                    .body("totalprice", equalTo(newPrice));

        System.out.println("      ჯავშანი " + bookingId + " განახლდა → "
                + newFirstName + " " + newLastName + ", " + newPrice);

        deleteBooking(bookingId);
    }

    @Test(priority = 5,
          groups = {"regression", "api"},
          description = "DELETE /booking/{id} ტოკენით და წაშლის შემოწმება")
    public void deleteBookingWithToken() {
        int bookingId = createBooking("ToBe", "Deleted", 75);

        authClient.authorizedRequest()
                .pathParam("id", bookingId)
                .when()
                    .delete(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .statusCode(201);

        int getStatus = authClient.publicRequest()
                .pathParam("id", bookingId)
                .when()
                    .get(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .extract().statusCode();

        System.out.println("      წაშლის შემდეგ GET → " + getStatus);

        Assert.assertEquals(getStatus, 404, "წაშლილი ჯავშანი ისევ ხელმისაწვდომია");
    }

    private Map<String, Object> bookingBody(String firstName, String lastName, int price) {
        Map<String, String> dates = new HashMap<>();
        dates.put("checkin", RandomDataGenerator.today());
        dates.put("checkout", RandomDataGenerator.daysFromNow(7));

        Map<String, Object> booking = new HashMap<>();
        booking.put("firstname", firstName);
        booking.put("lastname", lastName);
        booking.put("totalprice", price);
        booking.put("depositpaid", true);
        booking.put("bookingdates", dates);
        booking.put("additionalneeds", "Breakfast");
        return booking;
    }

    private int createBooking(String firstName, String lastName, int price) {
        return authClient.publicRequest()
                .contentType(ContentType.JSON)
                .body(bookingBody(firstName, lastName, price))
                .when()
                    .post(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)
                    .extract().jsonPath().getInt("bookingid");
    }

    private void deleteBooking(int bookingId) {
        authClient.authorizedRequest()
                .pathParam("id", bookingId)
                .when()
                .delete(ConfigReader.get("api.booking.path") + "/{id}");
    }

    private String readResourceFile(String path) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("ფაილი ვერ მოიძებნა: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("ფაილის წაკითხვა ვერ მოხერხდა: " + path, e);
        }
    }
}
