package ge.automation.api;

import ge.automation.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class AuthClient {

    private static final String ACCEPT_JSON = "application/json";

    private final String baseUrl;

    private String token;

    public AuthClient() {
        this.baseUrl = ConfigReader.get("api.base.url");
        RestAssured.baseURI = this.baseUrl;
    }

    public AuthClient authorize() {
        String username = ConfigReader.get("api.username");
        String password = ConfigReader.get("api.password");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Response response = RestAssured
                .given()
                    .baseUri(baseUrl)
                    .contentType(ContentType.JSON)
                    .header("Accept", ACCEPT_JSON)
                    .body(credentials)
                .when()
                    .post(ConfigReader.get("api.auth.path"))
                .then()
                    .extract().response();

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

    public String getToken() {
        if (token == null) {
            throw new IllegalStateException(
                    "ტოკენი ჯერ არ არის მიღებული — ჯერ authorize() გამოიძახე.");
        }
        return token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public RequestSpecification authorizedRequest() {
        return RestAssured
                .given()
                    .baseUri(baseUrl)
                    .contentType(ContentType.JSON)
                    .header("Accept", ACCEPT_JSON)
                    .header("Cookie", "token=" + getToken());
    }

    public RequestSpecification publicRequest() {
        return RestAssured
                .given()
                    .baseUri(baseUrl)
                    .header("Accept", ACCEPT_JSON);
    }
}
