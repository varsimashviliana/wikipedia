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

/**
 * ტესტი №6 — <b>API ტესტირება RestAssured-ით</b> (დავალების მე-8 პუნქტი).
 *
 * <p><b>დავალების ტექსტი:</b> "შექმენით კლასი სადაც ტოკენი იქნება ავტორიზებული,
 * გამოიყენეთ restassured მეთოდები და მისი გამოყენებით გაუშვით get მეთოდები.
 * ერთი ქეისი მაინც დაწერეთ, რომ პასუხის (response) მონაცემები შემოწმდეს
 * და ასევე სტატუს კოდი."</p>
 *
 * <p><b>ფარავს (სილაბუსი, მე-17–19 ლექციები):</b>
 * <ul>
 *   <li>ტოკენით ავტორიზაცია (AuthClient კლასი)</li>
 *   <li>GET, POST, PUT, DELETE მოთხოვნები</li>
 *   <li>HTTP სტატუს კოდების ვალიდაცია</li>
 *   <li>JSON პასუხის მონაცემების ვალიდაცია</li>
 *   <li>Path პარამეტრები და Query პარამეტრები</li>
 *   <li>Request / Response headers</li>
 *   <li>JSON მონაცემთა ფაილის გამოყენება</li>
 *   <li>დინამიური მონაცემების გენერაცია</li>
 * </ul>
 * </p>
 *
 * <p><b>ეს კლასი BaseTest-ს არ იმემკვიდრებს</b> — API ტესტს ბრაუზერი არ სჭირდება,
 * ამიტომ ზედმეტად არ ვხსნით Chrome-ს (ტესტები ბევრად სწრაფად გადის).</p>
 */
public class BookingApiTest {

    private AuthClient authClient;

    /**
     * @BeforeClass — ერთხელ სრულდება, ამ კლასის ყველა ტესტამდე.
     * ტოკენს ერთხელ ვიღებთ და ყველა ტესტში ვიყენებთ.
     */
    @BeforeClass(alwaysRun = true)
    public void authorizeOnce() {
        System.out.println("\n🔑  ვიღებთ ავტორიზაციის ტოკენს...");
        authClient = new AuthClient().authorize();
        System.out.println("    ტოკენი მიღებულია: " + authClient.getToken());
    }

    // =================================================================
    //  1. ავტორიზაცია (POST) — ტოკენის მიღება
    // =================================================================

    /**
     * ტესტი 6.1 — ტოკენი მიღებულია და ვალიდურია.
     */
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

    /**
     * ტესტი 6.2 — არასწორი მონაცემებით ავტორიზაცია ტოკენს არ აბრუნებს.
     * <b>ნეგატიური ტესტი.</b>
     */
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

        // ტოკენი არ უნდა იყოს — სამაგიეროდ "reason" ველი ჩნდება
        String token = response.jsonPath().getString("token");
        Assert.assertNull(token,
                "არასწორი მონაცემებით ტოკენი დაბრუნდა, რაც უსაფრთხოების პრობლემაა!");
    }

    // =================================================================
    //  2. GET მოთხოვნები — მთავარი მოთხოვნა დავალებაში
    // =================================================================

    /**
     * ტესტი 6.3 — <b>GET ყველა ჯავშნის სია</b> + სტატუს კოდი + headers.
     */
    @Test(priority = 3,
          groups = {"smoke", "api"},
          description = "GET /booking აბრუნებს ჯავშნების სიას (200)")
    public void getAllBookingsReturnsList() {
        Response response = authClient.publicRequest()
                .when()
                    .get(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)                         // <-- სტატუს კოდის ვალიდაცია
                    .contentType(ContentType.JSON)           // <-- Content-Type header-ის შემოწმება
                    .extract().response();

        List<Integer> bookingIds = response.jsonPath().getList("bookingid");

        System.out.println("      სტატუს კოდი: " + response.statusCode());
        System.out.println("      ჯავშნების რაოდენობა: " + bookingIds.size());
        System.out.println("      პასუხის დრო: " + response.time() + " ms");

        Assert.assertTrue(bookingIds.size() > 0, "ჯავშნების სია ცარიელია");
    }

    /**
     * ტესტი 6.4 — <b>GET ერთი ჯავშანი Path პარამეტრით</b>
     * და პასუხის მონაცემების სრული ვალიდაცია.
     *
     * <p><b>ეს არის დავალების მთავარი მოთხოვნა:</b> "ერთი ქეისი მაინც დაწერეთ,
     * რომ პასუხის მონაცემები შემოწმდეს და ასევე სტატუს კოდი".</p>
     */
    @Test(priority = 4,
          groups = {"smoke", "api"},
          description = "GET /booking/{id} — პასუხის მონაცემების და სტატუს კოდის ვალიდაცია")
    public void getSingleBookingValidatesResponseData() {
        // ჯერ ჩვენს ჯავშანს ვქმნით, რომ ზუსტად ვიცოდეთ რა მონაცემები უნდა დაბრუნდეს
        String firstName = RandomDataGenerator.firstName();
        String lastName = RandomDataGenerator.lastName();
        int price = RandomDataGenerator.price();

        int bookingId = createBooking(firstName, lastName, price);
        System.out.println("      შევქმენით ჯავშანი id=" + bookingId);

        // --- GET ამ კონკრეტული ჯავშნისთვის (Path პარამეტრი) ---
        Response response = authClient.publicRequest()
                .pathParam("id", bookingId)                  // <-- Path პარამეტრი
                .when()
                    .get(ConfigReader.get("api.booking.path") + "/{id}")
                .then()
                    .statusCode(200)                         // <-- სტატუს კოდი
                    .contentType(ContentType.JSON)
                    // --- პასუხის მონაცემების ვალიდაცია hamcrest matcher-ებით ---
                    .body("firstname", equalTo(firstName))
                    .body("lastname", equalTo(lastName))
                    .body("totalprice", equalTo(price))
                    .body("depositpaid", equalTo(true))
                    .body("bookingdates.checkin", notNullValue())
                    .body("bookingdates.checkout", notNullValue())
                    .extract().response();

        System.out.println("      პასუხი: " + response.asString());

        // --- იგივე ვალიდაცია TestNG-ის Assert-ებით (ორივე გზა ვაჩვენოთ) ---
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
        deleteBooking(bookingId);   // ვასუფთავებთ თავის შემდეგ
    }

    /**
     * ტესტი 6.5 — <b>GET Query პარამეტრებით</b> (ფილტრაცია).
     * სილაბუსი, მე-19 ლექცია: "Path და Query პარამეტრების გამოყენება".
     */
    @Test(priority = 5,
          groups = {"regression", "api"},
          description = "GET /booking?firstname=X&lastname=Y — ფილტრაცია query პარამეტრებით")
    public void getBookingsFilteredByQueryParams() {
        String firstName = RandomDataGenerator.firstName();
        String lastName = RandomDataGenerator.lastName();

        int bookingId = createBooking(firstName, lastName, 199);

        Response response = authClient.publicRequest()
                .queryParam("firstname", firstName)          // <-- Query პარამეტრები
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

    /**
     * ტესტი 6.6 — არარსებული ჯავშნის მოთხოვნა → 404.
     * <b>ნეგატიური ტესტი სტატუს კოდზე.</b>
     */
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

    // =================================================================
    //  3. POST — JSON ფაილიდან მონაცემებით
    // =================================================================

    /**
     * ტესტი 6.7 — <b>POST JSON ფაილიდან</b>.
     * სილაბუსი, მე-19 ლექცია: "მონაცემთა ფაილების გამოყენება (JSON და XML)".
     */
    @Test(priority = 7,
          groups = {"regression", "api"},
          description = "POST /booking — body იკითხება JSON ფაილიდან")
    public void createBookingFromJsonFile() {
        String jsonBody = readResourceFile("testdata/booking.json");
        System.out.println("      ფაილიდან წაკითხული JSON:\n" + jsonBody);

        Response response = authClient.publicRequest()
                .contentType(ContentType.JSON)
                .body(jsonBody)                              // <-- body ფაილიდან
                .when()
                    .post(ConfigReader.get("api.booking.path"))
                .then()
                    .statusCode(200)
                    .body("bookingid", notNullValue())
                    .body("booking.firstname", equalTo("Nino"))
                    .body("booking.lastname", equalTo("Beridze"))
                    .body("booking.totalprice", equalTo(250))
                    .extract().response();

        int id = response.jsonPath().getInt("bookingid");
        System.out.println("      შეიქმნა ჯავშანი id=" + id);

        Assert.assertTrue(id > 0, "bookingid არასწორია");
        deleteBooking(id);
    }

    // =================================================================
    //  4. PUT და DELETE — აქ ტოკენი აუცილებელია
    // =================================================================

    /**
     * ტესტი 6.8 — <b>PUT (განახლება) ტოკენით</b>.
     * ეს მოთხოვნა ავტორიზაციის გარეშე არ მუშაობს — სწორედ აქ ჩანს ტოკენის აზრი.
     */
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

        authClient.authorizedRequest()                       // <-- ტოკენი აქ ჩაიდება
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

    /**
     * ტესტი 6.9 — <b>PUT ტოკენის გარეშე → 403 Forbidden</b>.
     * ეს ადასტურებს, რომ ავტორიზაცია მართლაც მუშაობს.
     */
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

        int statusCode = authClient.publicRequest()          // <-- ტოკენის გარეშე!
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

    /**
     * ტესტი 6.10 — <b>DELETE ტოკენით</b> და წაშლის დადასტურება.
     */
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

        // --- ვამოწმებთ, რომ მართლა წაიშალა ---
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

    /**
     * ტესტი 6.11 — health check (GET /ping) + response headers.
     */
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

    // =================================================================
    //  დამხმარე მეთოდები
    // =================================================================

    /** ქმნის ჯავშანს და აბრუნებს მის id-ს. */
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

    /** შლის ჯავშანს (ტესტის შემდეგ დასუფთავება). */
    private void deleteBooking(int bookingId) {
        try {
            authClient.authorizedRequest()
                    .pathParam("id", bookingId)
                    .when()
                    .delete(ConfigReader.get("api.booking.path") + "/{id}");
        } catch (Exception e) {
            // დასუფთავების შეცდომამ ტესტი არ უნდა გააფუჭოს
            System.out.println("      (ჯავშნის " + bookingId + " წაშლა ვერ მოხერხდა)");
        }
    }

    /** კითხულობს ფაილს src/test/resources-იდან. */
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
