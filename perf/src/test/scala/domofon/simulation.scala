package domofon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import io.gatling.core.scenario.Simulation

object SimulationConfig {
  val httpConf = http.baseURL("http://127.0.0.1:8080")
  val injection = atOnceUsers(1)
}

class ContactSimulation
    extends Simulation
    with Scenario
    with Feeders
    with Generators {

  setUp(
    `create-contact-scenario`.
      inject(SimulationConfig.injection).
      protocols(SimulationConfig.httpConf)
  )
}
