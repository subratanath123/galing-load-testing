package springBootLoadTesting;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * This sample is based on our official tutorials:
 * <ul>
 *   <li><a href="https://gatling.io/docs/gatling/tutorials/quickstart">Gatling quickstart tutorial</a>
 *   <li><a href="https://gatling.io/docs/gatling/tutorials/advanced">Gatling advanced tutorial</a>
 * </ul>
 */
public class LoadTestingSimulation extends Simulation {

    FeederBuilder<String> feeder = csv("search.csv").random();

    ChainBuilder search =
            exec(http("DockerAPP")
                    .get("/"))
                    .pause(1)
                    .feed(feeder)
                    .exec(
                            http("Products")
                                    .get("/products/#{productId}")
                                    .check(
                                            jsonPath("").is("#{productId}")
                                    )
                    )
                    .pause(1);
    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http:/localhost:8080")
                    .acceptHeader("text/html,application/xhtml+xml,application/xml;application/json;q=0.9,*/*;q=0.8");

    ScenarioBuilder products = scenario("Products").exec(search);

    {
        setUp(
                products.injectOpen(rampUsers(10).during(10))
        ).protocols(httpProtocol);
    }
}
