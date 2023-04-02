package sandbox

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.action.sse._

class SseSandboxGatling extends Simulation {

  val sseCheckPushmessages = sse.checkMessage("checkPushmessages")
    .matching(substring("message"))
    .check(regex("""message","id":"(.+?)""""))

  val httpProtocol = http
    .baseUrl("https://wil5k.sse.codesandbox.io")

  val scn = scenario("ServerSentEvents-DEMO")
    .exec(
      sse("01_connect").connect("/es")
        .await(50)(
          sse.checkMessage("checkEventCustom")
            .check(substring("custom"))
        )
    )
    .exec(
        var acc: SseSetCheckBuilder = sse("SetCheck").setCheck
        for (_ <- 0 to 200) {
          acc = acc.await(60)(sseCheckPushmessages)
        }
        // in Scala, no need for "return",
        // the result of the last operation in a block is returned
        acc
    )
    .exec(sse("SetCheck").setCheck
      .await(60)(sseCheckPushmessages)
    )
    .pause(10)
    .exec(sse("02_Close").close)

  setUp(scn.inject(rampUsers(1).during(10))).protocols(httpProtocol)
}



