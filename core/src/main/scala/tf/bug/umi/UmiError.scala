package tf.bug.umi

// These realistically shouldn't be thrown, but just in case...
sealed trait UmiError extends Exception
object UmiError {
  sealed trait PlayerJoinError extends UmiError
  case object RoomFull extends PlayerJoinError
  case object RoomDoesNotExist extends PlayerJoinError
  case object RoomGameInProgress extends PlayerJoinError
  case object RoomCantAcceptNonExistentPlayer extends PlayerJoinError

  sealed trait GameCommandError extends UmiError
}
