package sandbox;

import io.gatling.javaapi.core.*;
import static io.gatling.javaapi.core.CoreDsl.*;

import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.http.HttpDsl.*;


/**
 * This sample is based on our official tutorials:
 * <ul>
 *   <li><a href="https://gatling.io/docs/gatling/tutorials/quickstart">Gatling quickstart tutorial</a>
 *   <li><a href="https://gatling.io/docs/gatling/tutorials/advanced">Gatling advanced tutorial</a>
 * </ul>
 */
public class SseSandboxGatlingJ extends Simulation {

    HttpProtocolBuilder httpProtocol =
        http.baseUrl("https://wil5k.sse.codesandbox.io");

    ChainBuilder sseConnect =
            exec(sse("01_connect").connect("/es")
                .await(10).on(
                        sse.checkMessage("checkCustom").check(substring("custom")))
            );
    
    ChainBuilder sseClose =
            exec(sse("02_Close").close());


    ScenarioBuilder users = scenario("Users").exec(sseConnect, sseClose);

    {
        setUp(users.injectOpen(rampUsers(1).during(10))
            ).protocols(httpProtocol);
    }
}
