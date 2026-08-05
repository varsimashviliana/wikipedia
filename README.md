# ვიკიპედიის ტესტირების ავტომატიზაცია — ფინალური პროექტი

**WEB/API ტესტირების ავტომატიზაციის კურსი** (Java, Selenium, RestAssured)

ავტომატიზირებული ტესტები ვიკიპედიისთვის (**Selenium + POM/PageFactory**) და
API ტესტები ტოკენით ავტორიზაციით (**RestAssured**).

---

## 📋 დავალების მოთხოვნები და სად არის შესრულებული

| # | მოთხოვნა | სად |
|---|----------|-----|
| 1 | დრაივერი ყველასთან უნდა იმუშაოს | [`DriverFactory.java`](src/main/java/ge/automation/driver/DriverFactory.java) — 3-ფენიანი მექანიზმი |
| 2 | POM (PageFactory) **ან** BDD | **POM + PageFactory** — [`pages/`](src/main/java/ge/automation/pages/) |
| 3 | მინიმუმ 5 ტესტი | **32 ტესტი** (21 ვები + 11 API) |
| 4 | რეგისტრაცია და ლოგინი | [`LoginTest`](src/test/java/ge/automation/tests/web/LoginTest.java), [`RegistrationTest`](src/test/java/ge/automation/tests/web/RegistrationTest.java) |
| 5 | GitHub-ზე ატვირთვა | ეს რეპოზიტორია |
| 6 | Configuration reader | [`ConfigReader.java`](src/main/java/ge/automation/config/ConfigReader.java) + [`config.properties`](src/test/resources/config.properties) |
| 7 | TestNG | [`testng.xml`](testng.xml) + ყველა ტესტი |
| 8 | RestAssured ტოკენით | [`AuthClient.java`](src/main/java/ge/automation/api/AuthClient.java), [`BookingApiTest`](src/test/java/ge/automation/tests/api/BookingApiTest.java) |

---

## 🚀 როგორ გავუშვათ

### წინაპირობები
- **Java 17+** (პროექტი Java 21-ზეა აწყობილი)
- **Maven 3.8+**
- **Google Chrome** (ან Firefox / Edge)

### ბრძანებები

```bash
# ყველა ტესტი
mvn clean test

# მხოლოდ API ტესტები (სწრაფია, ბრაუზერი არ სჭირდება)
mvn clean test -Dtest=BookingApiTest -Dsurefire.suiteXmlFiles=

# მხოლოდ smoke ჯგუფი
mvn clean test -Dgroups=smoke

# ბრაუზერის გარეშე (headless — CI-სთვის)
mvn clean test -Dheadless=true

# სხვა ბრაუზერით
mvn clean test -Dbrowser=firefox
```

> `-D` პარამეტრები `config.properties`-ს **გადაფარავს** — ფაილის შეცვლა არ გჭირდება.

---

## 📁 პროექტის სტრუქტურა

```
.
├── pom.xml                     Maven კონფიგურაცია და დამოკიდებულებები
├── testng.xml                  რომელი ტესტები გაეშვას და რა თანმიმდევრობით
│
└── src/
    ├── main/java/ge/automation/
    │   ├── config/
    │   │   └── ConfigReader.java        კითხულობს config.properties-ს
    │   ├── driver/
    │   │   └── DriverFactory.java       ქმნის ბრაუზერს (3-ფენიანი დრაივერი)
    │   ├── pages/                       ← Page Object Model
    │   │   ├── BasePage.java            abstract მშობელი: waits, Actions, Select
    │   │   ├── PortalPage.java          www.wikipedia.org — ენების dropdown
    │   │   ├── SearchResultsPage.java   ძებნის შედეგები — checkbox-ები
    │   │   ├── ArticlePage.java         სტატიის გვერდი
    │   │   ├── LoginPage.java           ლოგინის ფორმა
    │   │   └── CreateAccountPage.java   რეგისტრაციის ფორმა
    │   ├── api/
    │   │   └── AuthClient.java          ტოკენით ავტორიზაცია (RestAssured)
    │   ├── utils/
    │   │   ├── RandomDataGenerator.java დინამიური სატესტო მონაცემები
    │   │   └── ScreenshotUtil.java      სქრინშოტი ჩავარდნისას
    │   └── listeners/
    │       └── TestListener.java        რეპორტინგი (ITestListener)
    │
    └── test/
        ├── java/ge/automation/tests/
        │   ├── BaseTest.java                     ბრაუზერის გახსნა/დახურვა
        │   ├── web/SearchTest.java               5 ტესტი
        │   ├── web/LanguageDropdownTest.java     4 ტესტი
        │   ├── web/AdvancedSearchTest.java       4 ტესტი
        │   ├── web/LoginTest.java                8 ტესტი
        │   ├── web/RegistrationTest.java         6 ტესტი
        │   └── api/BookingApiTest.java           11 ტესტი
        └── resources/
            ├── config.properties                 ყველა პარამეტრი
            └── testdata/booking.json             სატესტო მონაცემები ფაილში
```

---

## 🧪 ტესტების მიმოხილვა

### ვებ ტესტები — Selenium (21)

| კლასი | რას ამოწმებს | ელემენტები |
|-------|--------------|-----------|
| `SearchTest` | ძებნა, შედეგები, სტატიის გახსნა, ნეგატიური ძებნა | input, Enter, ბმულები |
| `LanguageDropdownTest` | ენების არჩევა, ძებნა სხვა ენაზე | **`<select>` dropdown (77 ენა)** |
| `AdvancedSearchTest` | namespace ფილტრები | **checkbox (~28 ცალი)** |
| `LoginTest` | ლოგინი, შეცდომები, ვალიდაცია | ფორმა, checkbox, error message |
| `RegistrationTest` | რეგისტრაციის ფორმის ვალიდაცია | text/password/email ველები |

### API ტესტები — RestAssured (11)

| ტესტი | HTTP მეთოდი | რას ამოწმებს |
|-------|-------------|--------------|
| ტოკენის მიღება | `POST /auth` | ტოკენი ვალიდურია |
| არასწორი ავტორიზაცია | `POST /auth` | ტოკენი არ გაიცემა |
| ჯავშნების სია | `GET /booking` | 200, JSON, სია არაცარიელია |
| **ერთი ჯავშანი** | `GET /booking/{id}` | **200 + პასუხის ყველა ველი** |
| ფილტრაცია | `GET /booking?...` | query პარამეტრები |
| არარსებული ჯავშანი | `GET /booking/{id}` | 404 |
| შექმნა ფაილიდან | `POST /booking` | JSON ფაილიდან body |
| განახლება | `PUT /booking/{id}` | **ტოკენით** |
| განახლება ტოკენის გარეშე | `PUT /booking/{id}` | 403 — დაცვა მუშაობს |
| წაშლა | `DELETE /booking/{id}` | ტოკენით + წაშლის დადასტურება |
| health check | `GET /ping` | 201 + headers |

---

## 📚 სილაბუსის თემები კოდში

| თემა | სად ნახავ |
|------|-----------|
| **OOP** — abstract კლასი | `BasePage`, `BaseTest` |
| **OOP** — მემკვიდრეობა | ყველა Page კლასი `extends BasePage` |
| **OOP** — ინკაფსულაცია | `private` ველები + `public` მეთოდები |
| **OOP** — პოლიმორფიზმი | `isPageOpened()` — თითოეული გვერდი თავისებურად |
| **Exceptions** (try/catch) | `ConfigReader`, `DriverFactory`, `ScreenshotUtil` |
| **Selenium** — სელექტორები | `@FindBy(id / css / xpath)` — Page კლასებში |
| **Selenium** — Implicit Wait | `DriverFactory.createDriver()` |
| **Selenium** — Explicit Wait | `BasePage.waitUntilVisible()` |
| **Selenium** — Fluent Wait | `BasePage.fluentWaitFor()` |
| **Selenium** — Actions | `BasePage.hoverOver()`, `typeAndEnter()` |
| **Selenium** — Select (dropdown) | `BasePage.selectByVisibleText()` |
| **TestNG** — ანოტაციები | `BaseTest` — `@BeforeSuite/@BeforeMethod/@AfterMethod` |
| **TestNG** — Assert | ყველა ტესტში |
| **TestNG** — SoftAssert | `LanguageDropdownTest`, `RegistrationTest` |
| **TestNG** — ჯგუფები | `groups = {"smoke", "regression"}` |
| **TestNG** — გამოტოვება | `LoginTest.loginWithValidCredentials()` — `SkipException` |
| **TestNG** — DataProvider | `SearchTest`, `LoginTest` |
| **რეპორტინგი** | `TestListener` + `ScreenshotUtil` |
| **API** — სტატუს კოდები | `BookingApiTest` — 200 / 201 / 403 / 404 |
| **RestAssured** — ტოკენი | `AuthClient` |
| **RestAssured** — Path/Query params | `BookingApiTest` |
| **RestAssured** — Headers | `AuthClient.authorizedRequest()` |
| **RestAssured** — JSON ფაილი | `BookingApiTest.createBookingFromJsonFile()` |
| **დინამიური მონაცემები** | `RandomDataGenerator` |

---

## ⚠️ მნიშვნელოვანი შენიშვნები (გულახდილად)

### 1. რეგისტრაცია ბოლომდე ვერ დასრულდება — CAPTCHA

ვიკიპედიის რეგისტრაციის გვერდზე ჩაშენებულია **hCaptcha**. ეს ნიშნავს, რომ
ავტომატიზაციას რეალური ანგარიშის შექმნა **ფიზიკურად არ შეუძლია** — CAPTCHA
ზუსტად ამისთვის არსებობს.

ამიტომ `RegistrationTest`-ში ვწერთ **ვალიდაციის ტესტებს**: ველების არსებობა,
`required` ატრიბუტები, ველების ტიპები, მონაცემების შეყვანა და CAPTCHA-ს
არსებობის დადასტურება. ეს რეალურ QA პრაქტიკაში სტანდარტული მიდგომაა.

### 2. ლოგინის პოზიტიური ტესტი გამოტოვებულია

`loginWithValidCredentials()` საჭიროებს რეალურ ვიკიპედიის ანგარიშს.
თუ `config.properties`-ში `valid.username` / `valid.password` ცარიელია,
ტესტი **SKIPPED** სტატუსით გამოტოვდება (და არა FAILED) — `SkipException`-ის მეშვეობით.

> ⚠️ თუ რეალურ პაროლს ჩაწერ, **GitHub-ზე ნუ ატვირთავ**.

### 3. radio button ვიკიპედიაზე არ არსებობს

დავალებაში ნახსენები იყო radio button. ვიკიპედიის საჯარო გვერდები
შემოწმდა — radio button-ს არ იყენებს. სამაგიეროდ პროექტი ფარავს:
`<select>` dropdown, checkbox, text / password / email input, textarea,
ღილაკები და ბმულები.

### 4. API — reqres.in-ის ნაცვლად restful-booker

დავალებაში შემოთავაზებული იყო `reqres.in`. ის შეიცვალა და ახლა
**პირად API key-ს ითხოვს** (რეგისტრაციით `app.reqres.in`-ზე):

```json
{"error":"missing_api_key",
 "message":"The x-api-key header is required for this endpoint."}
```

ამიტომ გამოყენებულია **`restful-booker.herokuapp.com`** — იგივე პრინციპით
მუშაობს (ტოკენით ავტორიზაცია), უფასოა და რეგისტრაციას არ საჭიროებს,
ანუ პროექტი ყველასთან გაეშვება.

---

## 🔧 ცნობილი პრობლემები და გადაწყვეტები

### „PKIX path building failed" — SSL შეცდომა API ტესტებში

**მიზეზი:** ზოგიერთ ქსელში (ოფისი, უნივერსიტეტი, ანტივირუსი) დგას მოწყობილობა,
რომელიც HTTPS ტრაფიკს ამოწმებს. Windows მის სერტიფიკატს ენდობა,
Java-ს კი თავისი ცალკე საცავი აქვს.

**გადაწყვეტა:** უკვე ჩაშენებულია `pom.xml`-ში — Windows-ზე ავტომატურად
ირთვება პროფილი `-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT`.

### „Unable to establish loopback connection" — ვებ ტესტები არ იშვება

**სიმპტომი:** API ტესტები მუშაობს, ვებ ტესტები კი ჩავარდება შეცდომით:

```
java.io.UncheckedIOException: java.io.IOException: Unable to establish loopback connection
    at java.net.http/jdk.internal.net.http.HttpClientBuilderImpl.build(...)
Caused by: java.net.SocketException: Invalid argument: connect
    at java.base/sun.nio.ch.PipeImpl$Initializer$LoopbackConnector.run(...)
```

**მიზეზი:** კომპიუტერზე დგას EDR ტიპის უსაფრთხოების აგენტი
(მაგ. **SentinelOne**, CrowdStrike), რომელიც სოკეტების შექმნას აკონტროლებს
და ბლოკავს იმ ოპერაციას, რომელსაც Java-ს `Selector.open()` იყენებს.
Selenium 4 ბრაუზერს `java.net.http.HttpClient`-ით ესაუბრება, ის კი
სწორედ `Selector`-ს ეყრდნობა.

**როგორ დავრწმუნდეთ, რომ ეს არის მიზეზი** — გაუშვი ეს პატარა პროგრამა:

```java
// java Check.java
import java.nio.channels.Selector;
public class Check {
    public static void main(String[] a) {
        try (Selector s = Selector.open()) { System.out.println("OK — Selenium იმუშავებს"); }
        catch (Throwable t) { System.out.println("FAIL — EDR ბლოკავს: " + t); }
    }
}
```

**გადაწყვეტა:**
1. სხვა კომპიუტერზე/ქსელზე გაშვება (სახლის კომპიუტერი) — უმარტივესია
2. IT-სგან `java.exe`-ს დაშვების მოთხოვნა უსაფრთხოების აგენტში

> ეს **კოდის პრობლემა არაა** — ტესტები ნებისმიერ ჩვეულებრივ კომპიუტერზე გაეშვება.
> RestAssured (API ტესტები) იმიტომ მუშაობს, რომ სხვა HTTP კლიენტს იყენებს.

### „Connection reset" / „Unable to obtain chromedriver"

**მიზეზი:** ქსელი ბლოკავს დრაივერების ჩამოტვირთვის სერვერებს
(`storage.googleapis.com`, `msedgedriver.azureedge.net`).

**გადაწყვეტა:** ჩამოტვირთე `chromedriver.exe` ხელით
[Chrome for Testing](https://googlechromelabs.github.io/chrome-for-testing/)
საიტიდან (Chrome-ის შენი ვერსიისთვის) და მიუთითე `config.properties`-ში:

```properties
driver.path=C:/WebDriver/chromedriver.exe
```

სხვა ქსელზე ეს საჭირო არ არის — დრაივერი ავტომატურად ჩამოიტვირთება.

---

## 🛠️ გამოყენებული ტექნოლოგიები

| ბიბლიოთეკა | ვერსია | დანიშნულება |
|------------|--------|-------------|
| Java | 21 | პროგრამირების ენა |
| Maven | 3.9 | build ავტომატიზაცია |
| Selenium | 4.46.0 | ბრაუზერის მართვა |
| WebDriverManager | 6.3.4 | დრაივერის ავტომატური ჩამოტვირთვა |
| TestNG | 7.12.0 | ტესტ-ფრეიმვორქი |
| RestAssured | 5.5.7 | API ტესტირება |

---

## 📊 რეპორტები

ტესტების გაშვების შემდეგ:

- **TestNG რეპორტი:** `target/surefire-reports/index.html`
- **Surefire რეპორტი:** `target/surefire-reports/`
- **სქრინშოტები (ჩავარდნისას):** `target/screenshots/`
