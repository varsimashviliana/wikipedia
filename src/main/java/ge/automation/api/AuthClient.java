package ge.automation.api;

import ge.automation.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthClient — API-ს ავტორიზაციის კლასი.
 *
 * <p><b>დავალების მე-8 პუნქტი:</b> "შექმენით კლასი სადაც ტოკენი იქნება ავტორიზებული,
 * გამოიყენეთ restassured მეთოდები და მისი გამოყენებით გაუშვით get მეთოდები".</p>
 *
 * <p><b>რას აკეთებს?</b>
 * <ol>
 *   <li>აგზავნის <code>POST /auth</code>-ს მომხმარებლით და პაროლით</li>
 *   <li>პასუხიდან იღებს <code>token</code>-ს</li>
 *   <li>ინახავს ტოკენს და აწვდის სხვა ტესტებს</li>
 * </ol>
 * </p>
 *
 * <p><b>რატომ restful-booker და არა reqres.in?</b><br>
 * დავალებაში reqres.in იყო შემოთავაზებული, მაგრამ ის შეიცვალა და
 * ახლა პირად API key-ს ითხოვს (რეგისტრაციით app.reqres.in-ზე).
 * restful-booker იგივე პრინციპით მუშაობს (ტოკენით ავტორიზაცია),
 * უფასოა და რეგისტრაციას არ საჭიროებს — ამიტომ პროექტი ყველასთან გაეშვება.</p>
 */
public class AuthClient {

    /**
     * Accept header-ის მნიშვნელობა.
     *
     * <p><b>⚠️ მნიშვნელოვანი გაკვეთილი (რეალური პრობლემა, რომელსაც წავაწყდით):</b><br>
     * თუ RestAssured-ში დაწერ <code>.accept(ContentType.JSON)</code>,
     * ის სერვერს <u>ოთხ</u> მნიშვნელობას უგზავნის:
     * <code>application/json, application/javascript, text/javascript, text/json</code>.
     * <br><br>
     * restful-booker ასეთ header-ს ვერ ცნობს და პასუხად აბრუნებს
     * <code>418 I'm a teapot</code>-ს — რაც ძალიან დამაბნეველი შეცდომაა.
     * <br><br>
     * ამიტომ Accept-ს ხელით ვწერთ, ერთი მნიშვნელობით.
     * ეს კარგი მაგალითია იმისა, თუ რატომაა საჭირო რეალური
     * მოთხოვნა/პასუხის ლოგირება (<code>.log().all()</code>) გამართვისას.</p>
     */
    private static final String ACCEPT_JSON = "application/json";

    /** ბაზისური მისამართი — config.properties-იდან. */
    private final String baseUrl;

    /** ავტორიზაციის ტოკენი. private — გარედან პირდაპირ ვერ შეცვლი (ინკაფსულაცია). */
    private String token;

    public AuthClient() {
        this.baseUrl = ConfigReader.get("api.base.url");
        RestAssured.baseURI = this.baseUrl;
    }

    /**
     * ავტორიზაცია — იღებს ტოკენს და ინახავს.
     *
     * @return თვითონ ობიექტი, რომ შეიძლებოდეს ჯაჭვური გამოძახება
     * @throws IllegalStateException თუ ტოკენი ვერ მივიღეთ
     */
    public AuthClient authorize() {
        String username = ConfigReader.get("api.username");
        String password = ConfigReader.get("api.password");

        // მოთხოვნის სხეული (body) — Map-იდან RestAssured თვითონ გააკეთებს JSON-ს
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Response response = RestAssured
                .given()
                    .baseUri(baseUrl)
                    .contentType(ContentType.JSON)      // ვეუბნებით, რომ JSON-ს ვგზავნით
                    .header("Accept", ACCEPT_JSON)      // ვთხოვთ, რომ JSON დაგვიბრუნოს
                    .body(credentials)
                .when()
                    .post(ConfigReader.get("api.auth.path"))
                .then()
                    .extract().response();

        // შევამოწმოთ, რომ სერვერმა 200 დაგვიბრუნა
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "ავტორიზაცია ვერ მოხერხდა. სტატუს კოდი: " + response.statusCode()
                            + ", პასუხი: " + response.asString());
        }

        this.token = response.jsonPath().getString("token");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "პასუხში ტოკენი ვერ ვიპოვე. პასუხი: " + response.asString());
        }

        return this;
    }

    /**
     * აბრუნებს ტოკენს.
     *
     * @throws IllegalStateException თუ ჯერ authorize() არ გამოგვიძახებია
     */
    public String getToken() {
        if (token == null) {
            throw new IllegalStateException(
                    "ტოკენი ჯერ არ არის მიღებული — ჯერ authorize() გამოიძახე.");
        }
        return token;
    }

    /** აბრუნებს ბაზისურ URL-ს. */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * მზა მოთხოვნა, რომელშიც ტოკენი უკვე ჩადებულია.
     *
     * <p>restful-booker ტოკენს <b>Cookie</b> header-ში ელოდება:
     * <code>Cookie: token=abc123</code></p>
     *
     * <p>ამ მეთოდს ვიყენებთ PUT / DELETE მოთხოვნებისთვის,
     * რომლებსაც ავტორიზაცია სჭირდებათ.</p>
     */
    public RequestSpecification authorizedRequest() {
        return RestAssured
                .given()
                    .baseUri(baseUrl)
                    .contentType(ContentType.JSON)
                    .header("Accept", ACCEPT_JSON)
                    .header("Cookie", "token=" + getToken());
    }

    /**
     * მოთხოვნა ავტორიზაციის გარეშე — GET-ებისთვის,
     * რომლებსაც ტოკენი არ სჭირდებათ.
     */
    public RequestSpecification publicRequest() {
        return RestAssured
                .given()
                    .baseUri(baseUrl)
                    .header("Accept", ACCEPT_JSON);
    }
}
