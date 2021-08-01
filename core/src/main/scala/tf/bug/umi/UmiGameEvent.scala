package tf.bug.umi

import java.util.UUID

sealed trait UmiGameEvent
object UmiGameEvent {
  case class PlayerNameChange(player: UUID, newName: String) extends UmiGameEvent
}
