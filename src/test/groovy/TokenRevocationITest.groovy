import com.jayway.restassured.RestAssured
import it.lisik.springutils.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ActiveProfiles("test")
@IntegrationTest("server.port:0")
@SpringApplicationConfiguration(classes = TestApplication.class)
class TokenRevocationITest extends Specification {
    @Value("\${local.server.port}")
    private int serverPort;

    @Autowired
    TokenStore tokenStore;

    void setup() {
        RestAssured.port = serverPort;
    }

    def "Test Revocation of Access Token"() {
        given: "Client credentials"
        def specification = RestAssured.given().authentication().preemptive().basic("example", "example")

        and: "Valid Access Token"
        def accessToken = prepareToken('access_token')
        specification.formParameter("token", accessToken)

        when: "revoke endpoint is invoked"
        def response = specification.post("/oauth/revoke")

        then: "Response status is OK"
        response.statusCode == 200

        and: "Token is no longer valid"
        tokenStore.readAccessToken(accessToken) == null
    }

    def "Test Revocation of Refresh Token"() {
        def accessToken = prepareToken('access_token')


        given: "Client credentials"
        def specification = RestAssured.given().authentication().preemptive().basic("example", "example")

        and: "Valid Refresh Token"
        def refreshToken = prepareToken('refresh_token')
        specification.formParameter("token", refreshToken)

        and: "token type hint is set to refresh_token"
        specification.formParameter("token_type_hint", 'refresh_token')

        when: "revoke endpoint is invoked"
        def response = specification.post("/oauth/revoke")

        then: "Response status is OK"
        response.statusCode == 200

        and: "Token is no longer valid"
        tokenStore.readAccessToken(accessToken) == null
    }


    def prepareToken(String type) {
        def path = RestAssured.given().authentication().preemptive().basic("example", "example")
                .formParameter('username', 'Matt')
                .formParameter('password', '12345678')
                .formParameter('grant_type', 'password')
                .then().post("oauth/token").andReturn().jsonPath()


        path.prettyPrint()
        return path.getString(type)
    }

}