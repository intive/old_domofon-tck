package domofon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import io.gatling.core.scenario.Simulation

trait Config {
  lazy val uri = System.getenv("DEST_URI")
}

object SimulationConfig extends Config {
  val httpConf = http.baseURL(s"http://$uri")
  val injection = atOnceUsers(1)
}

class ContactSimulation
    extends Simulation
    with Scenario
    with Feeders
    with Generators {

  setUp(
    `update-contact-scenario`.
      inject(SimulationConfig.injection).
      protocols(SimulationConfig.httpConf)
  )
}
