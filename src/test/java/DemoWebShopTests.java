import com.codeborne.selenide.WebDriverRunner;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

public class DemoWebShopTests {
    String email = "rutest@mail.ru";
    String password = "rutest";
    String cartCount;
    Map<String, String> authorizationCookie;

    @Test
    void addToCartWithCookieTest() {
        step("Делаем авторизацию и получаем куки", () -> {
            authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", email)
                            .formParam("Password", password)
                            .when()
                            .post("http://demowebshop.tricentis.com/login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookies();
        });
        step("Добавляем товар в корзину и сохраняем количество товара", () -> {
            Response responce =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .cookies(authorizationCookie)
                            .when()
                            .post("http://demowebshop.tricentis.com/addproducttocart/catalog/29/1/1")
                            .then()
                            .statusCode(200)
                            .body("success", is(true))
                            .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                            .extract().response();
            cartCount = responce.path("updatetopcartsectionhtml");
        });


        step("Проверим через UI, кол-во товара в корзине верное", () -> {
            open("http://demowebshop.tricentis.com/Themes/DefaultClean/Content/images/logo.png");
            HashMap<String, String> coockies = new HashMap<String, String>(authorizationCookie);
            for (Map.Entry<String, String> entry : coockies.entrySet()) {
                WebDriverRunner.getWebDriver().manage().addCookie(new Cookie(entry.getKey(), entry.getValue()));
            }
            open("http://demowebshop.tricentis.com");
            assertThat($(".cart-qty").getText()).isEqualTo(cartCount);
        });
    }

}





