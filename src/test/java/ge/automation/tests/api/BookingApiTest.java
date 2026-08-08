package ge.automation.tests.api;

import ge.automation.api.AuthClient;
import ge.automation.config.ConfigReader;
import ge.automation.utils.RandomDataGenerator;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class BookingApiTest {

    private AuthClient authClient;

    @BeforeClass(alwaysRun = true)
    public void authorizeOnce() {
        System.out.println("\n🔑  ვიღებთ ავტორიზაციის ტოკენს...");
        authClient = new AuthClient().authorize();
        System.out.println("    ტოკენი მიღებულია: " + authClient.getToken());
    }

    @Test(priority = 1,
          groups = {"smoke", "api"},
          description = "POST /auth აბრუნებს ვალიდურ ტოკენს")
    public void authReturnsValidToken() {
        String token = authClient.getToken();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(token, "ტოკენი null-ია");
        softAssert.assertFalse(token.isBlank(), "ტოკენი ცარიელია");
        softAssert.assertTrue(token.length() >= 10,
                "ტოკენი ეჭვიანად მოკლეა: '" + token + "'");
        softAssert.assertAll();

        System.out.println("      ტოკენის სიგრძე: " + token.length());
    }

    @Test(priority = 2,
          groups = {"regression", "api"},
          description = "არასწორი მონაცემებით POST /auth ტოკენს არ იძლევა")
    public void authWithWrongCredentialsReturnsNoToken() {
        Map<String, String> wrongCredentials = new HashMap<>();
        wrongCredentials.put("username", "wrongUser");
        wrongCredentials.put("password", "wrongPassword");

        Response response = io.restassured.RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .contentType(ContentType.JSON)
                    .body(wrongCredentials)
                .when()
                    .post(ConfigReader.get("api.auth.path"))
                .then()
                    .extract().response();

        System.out.println("      სტატუს კოდი: " + response.statusCode());
        System.out.println("      პასუხი: " + response.asString());

        String token = response.jsonPath().getString("token");
        Assert.assertNull(token,
                "არასწორი მონაცემებით ტოკენი დაბრუნდა, რაც უსაფრთხოების პრობლემაა!");
    }

    @Test(priority = 3,
          groups = {"smoke", "api"},
          description = "GET /booking აბრუნებს ჯავშნების სიას (200)")
    public void getAllBookingsReturnsList() {
        Response response = authClient.publicRequest()
                .when()
                    .get(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract().response();

        List<Integer> bookingIds = response.jsonPath().getList("bookingid");

        System.out.println("      სტატუს კოდი: " + response.statusCode());
        System.out.println("      ჯავშნების რაოდენობა: " + bookingIds.size());
        System.out.println("      პასუხის დრო: " + response.time() + " ms");

        Assert.assertTrue(bookingIds.size() > 0, "ჯავშნების სია ცარიელია");
    }

    @Test(priority = 4,
          groups = {"smoke", "api"},
          description = "GET /booking/{id} — პასუხის მონაცემების და სტატუს კოდის ვალიდაცია")
    public void getSingleBookingValidatesResponseData() {
        String firstName = RandomDataGenerator.firstName();
        String lastName = RandomDataGenerator.lastName();
        int price = RandomDataGenerator.price();

        int bookingId = createBooking(firstName, lastName, price);
        System.out.println("      შევქმენით ჯავშანი id=" + bookingId);

        Response response = authClient.publicRequest()
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
                    .body("bookingdates.checkout", notNullValue())
                    .extract().response();

        System.out.println("      პასუხი: " + response.asString());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200, "სტატუს კოდი არასწორია");
        softAssert.assertEquals(response.jsonPath().getString("firstname"), firstName,
                "firstname არ ემთხვევა");
        softAssert.assertEquals(response.jsonPath().getString("lastname"), lastName,
                "lastname არ ემთხვევა");
        softAssert.assertEquals(response.jsonPath().getInt("totalprice"), price,
                "totalprice არ ემთხვევა");
        softAssert.assertAll();

        System.out.println("      ყველა ველი ვალიდურია ✓");
        deleteBooking(bookingId);
    }

    @Test(priority = 5,
          groups = {"regression", "api"},
          description = "GET /booking?firstname=X&lastname=Y — ფილტრაცია query პარამეტრებით")
    public void getBookingsFilteredByQueryParams() {
        String firstName = RandomDataGenerator.firstName();
        String lastName = RandomDataGenerator.lastName();

        int bookingId = createBooking(firstName, lastName, 199);

        Response response = authClient.publicRequest()
                .queryParam("firstname", firstName)
                .queryParam("lastname", lastName)
                .when()
                    .get(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)
                    .extract().response();

        List<Integer> ids = response.jsonPath().getList("bookingid");
        System.out.println("      '" + firstName + " " + lastName
                + "' — ნაპოვნია " + ids.size() + " ჯავშანი");

        Assert.assertTrue(ids.contains(bookingId),
                "ფილტრმა ჩვენი ჯავშანი (id=" + bookingId + ") ვერ იპოვა");

        deleteBooking(bookingId);
    }

    @Test(priority = 6,
          groups = {"regression", "api"},
          description = "GET არარსებული id → 404 Not Found")
    public void getNonExistentBookingReturns404() {
        int response = authClient.publicRequest()
                .pathParam("id", 99999999)
                .when()
                    .get(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .extract().statusCode();

        System.out.println("      სტატუს კოდი: " + response);
        Assert.assertEquals(response, 404,
                "არარსებულ ჯავშანზე 404 უნდა დაბრუნებულიყო");
    }

    @Test(priority = 7,
          groups = {"regression", "api"},
          description = "POST /booking — body იკითხება JSON ფაილიდან")
    public void createBookingFromJsonFile() {
        String jsonBody = readResourceFile("testdata/booking.json");
        System.out.println("      ფაილიდან წაკითხული JSON:\n" + jsonBody);

        Response response = authClient.publicRequest()
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
                    .extract().response();

        int id = response.jsonPath().getInt("bookingid");
        System.out.println("      შეიქმნა ჯავშანი id=" + id);

        Assert.assertTrue(id > 0, "bookingid არასწორია");
        deleteBooking(id);
    }

    @Test(priority = 8,
          groups = {"regression", "api"},
          description = "PUT /booking/{id} ტოკენით — ჯავშნის განახლება")
    public void updateBookingWithToken() {
        int bookingId = createBooking("Old", "Name", 100);

        String newFirstName = RandomDataGenerator.firstName();
        String newLastName = RandomDataGenerator.lastName();
        int newPrice = RandomDataGenerator.price();

        Map<String, Object> updated = new HashMap<>();
        updated.put("firstname", newFirstName);
        updated.put("lastname", newLastName);
        updated.put("totalprice", newPrice);
        updated.put("depositpaid", false);
        Map<String, String> dates = new HashMap<>();
        dates.put("checkin", RandomDataGenerator.today());
        dates.put("checkout", RandomDataGenerator.daysFromNow(5));
        updated.put("bookingdates", dates);
        updated.put("additionalneeds", "Late checkout");

        authClient.authorizedRequest()
                .pathParam("id", bookingId)
                .body(updated)
                .when()
                    .put(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .statusCode(200)
                    .body("firstname", equalTo(newFirstName))
                    .body("lastname", equalTo(newLastName))
                    .body("totalprice", equalTo(newPrice))
                    .body("depositpaid", equalTo(false));

        System.out.println("      ჯავშანი " + bookingId + " განახლდა → "
                + newFirstName + " " + newLastName + ", " + newPrice);

        deleteBooking(bookingId);
    }

    @Test(priority = 9,
          groups = {"regression", "api"},
          description = "PUT ტოკენის გარეშე → 403 (ავტორიზაცია მუშაობს)")
    public void updateWithoutTokenIsForbidden() {
        int bookingId = createBooking("Protected", "Booking", 120);

        Map<String, Object> body = new HashMap<>();
        body.put("firstname", "Hacker");
        body.put("lastname", "Attempt");
        body.put("totalprice", 1);
        body.put("depositpaid", false);
        Map<String, String> dates = new HashMap<>();
        dates.put("checkin", RandomDataGenerator.today());
        dates.put("checkout", RandomDataGenerator.daysFromNow(1));
        body.put("bookingdates", dates);

        int statusCode = authClient.publicRequest()
                .contentType(ContentType.JSON)
                .pathParam("id", bookingId)
                .body(body)
                .when()
                    .put(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .extract().statusCode();

        System.out.println("      ტოკენის გარეშე PUT → სტატუს კოდი " + statusCode);

        Assert.assertEquals(statusCode, 403,
                "ტოკენის გარეშე განახლება უნდა აკრძალულიყო (403)");

        deleteBooking(bookingId);
    }

    @Test(priority = 10,
          groups = {"regression", "api"},
          description = "DELETE /booking/{id} ტოკენით და წაშლის შემოწმება")
    public void deleteBookingWithToken() {
        int bookingId = createBooking("ToBe", "Deleted", 75);
        System.out.println("      შევქმენით ჯავშანი id=" + bookingId);

        int deleteStatus = authClient.authorizedRequest()
                .pathParam("id", bookingId)
                .when()
                    .delete(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .extract().statusCode();

        System.out.println("      DELETE → სტატუს კოდი " + deleteStatus);
        Assert.assertEquals(deleteStatus, 201,
                "restful-booker წაშლისას 201-ს აბრუნებს");

        int getStatus = authClient.publicRequest()
                .pathParam("id", bookingId)
                .when()
                    .get(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .extract().statusCode();

        System.out.println("      წაშლის შემდეგ GET → " + getStatus);
        Assert.assertEquals(getStatus, 404,
                "წაშლილი ჯავშანი ისევ ხელმისაწვდომია");
    }

    @Test(priority = 11,
          groups = {"smoke", "api"},
          description = "GET /ping — სერვისი ცოცხალია, headers ვალიდურია")
    public void pingReturnsCreated() {
        Response response = authClient.publicRequest()
                .when()
                    .get("/ping")
                .then()
                    .statusCode(201)
                    .extract().response();

        System.out.println("      სტატუს კოდი: " + response.statusCode());
        System.out.println("      Server header: " + response.header("Server"));
        System.out.println("      პასუხის დრო: " + response.time() + " ms");

        Assert.assertTrue(response.time() < ConfigReader.getInt("api.timeout.ms"),
                "სერვისი ძალიან ნელია: " + response.time() + " ms");
    }

    private int createBooking(String firstName, String lastName, int price) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("firstname", firstName);
        booking.put("lastname", lastName);
        booking.put("totalprice", price);
        booking.put("depositpaid", true);
        Map<String, String> dates = new HashMap<>();
        dates.put("checkin", RandomDataGenerator.today());
        dates.put("checkout", RandomDataGenerator.daysFromNow(7));
        booking.put("bookingdates", dates);
        booking.put("additionalneeds", "Breakfast");

        return authClient.publicRequest()
                .contentType(ContentType.JSON)
                .body(booking)
                .when()
                    .post(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)
                    .extract().jsonPath().getInt("bookingid");
    }

    private void deleteBooking(int bookingId) {
        try {
            authClient.authorizedRequest()
                    .pathParam("id", bookingId)
                    .when()
                    .delete(ConfigReader.get("api.booking.path") + "/{id}");
        } catch (Exception e) {
            System.out.println("      (ჯავშნის " + bookingId + " წაშლა ვერ მოხერხდა)");
        }
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
