package domofon.mock.akka.entities

sealed trait OperationResult {
  def status: String
}

case object OperationSuccessful extends OperationResult {
  override def status: String = "OK"
}