package tf.bug.umi

sealed trait UmiGameCommand
object UmiGameCommand {

  case class ChangeName(newName: String) extends UmiGameCommand

}
