package demoContract.test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;


public class FirstTest extends TestNGCitrusTestRunner {

    @Autowired
    private HttpClient restClient;
    private TestContext context;

    @Test()
    @CitrusTest
    public void getUserInfo() {
        this.context = citrus.createTestContext();
        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .send()
                .get("users/2")
        );

        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .receive()
                .response()
                .payload("{\n" +
                        "   \"data\":{\n" +
                        "      \"id\":2,\n" +
                        "      \"email\":\"janet.weaver@reqres.in\",\n" +
                        "      \"first_name\":\"Janet\",\n" +
                        "      \"last_name\":\"Weaver\",\n" +
                        "      \"avatar\":\"https://reqres.in/img/faces/2-image.jpg\"\n" +
                        "   },\n" +
                        "   \"support\":{\n" +
                        "      \"url\":\"https://reqres.in/#support-heading\",\n" +
                        "      \"text\":\"To keep ReqRes free, contributions towards server costs are appreciated!\"\n" +
                        "   }\n" +
                        "}")
                .status(HttpStatus.OK)
        );
    }

    @Test()
    @CitrusTest
    public void registerUser() {
        this.context = citrus.createTestContext();
        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .send()
                .post("register")
                .payload("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"pistol\"\n" +
                        "}")

        );

        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .receive()
                .response()
                .payload("{\n" +
                        "    \"id\": 4,\n" +
                        "    \"token\": \"QpwL5tke4Pnpja7X4\"\n" +
                        "}")
                .status(HttpStatus.OK)
        );
    }

    @Test()
    @CitrusTest
    public void deleteUser() {
        this.context = citrus.createTestContext();
        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .send()
                .delete("user/2")
        );

        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .receive()
                .response()
                .status(HttpStatus.NO_CONTENT));
    }

    @Test()
    @CitrusTest
    public void loginUserUnsuccessful() {
        this.context = citrus.createTestContext();
        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .send()
                .post("login")
                .payload("{\n" +
                        "    \"email\": \"peter@klaven\"\n" +
                        "}")
        );

        http(httpActionBuilder -> httpActionBuilder
                .client(restClient)
                .receive()
                .response()
                .payload("{\n" +
                        "    \"error\": \"Missing password\"\n" +
                        "}")
                .status(HttpStatus.BAD_REQUEST));
    }
}
