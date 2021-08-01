package tf.bug.umi

import cats._
import fs2._

import java.util.UUID

case class UmiPlayer[F[_]](
  uuid: UUID,
  room: UmiRoom[F],
) {

  def roomEvents(implicit monad: Monad[F]): Stream[F, UmiGameEvent] =
    room.events(uuid)
  def sendCommand(command: UmiGameCommand)(implicit monad: Monad[F]): F[Either[UmiError.GameCommandError, Unit]] =
    room.sendCommand(uuid, command)

}
