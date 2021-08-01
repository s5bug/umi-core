package tf.bug.umi

import cats._
import cats.data._
import cats.effect._
import cats.effect.std.Queue
import cats.syntax.all._
import fs2._

import java.util.UUID

case class UmiRoom[F[_]](
  id: Long,
  roomState: Ref[F, UmiRoomState],
  playerEventQueues: Ref[F, Map[UUID, Queue[F, UmiGameEvent]]]
) {

  def accept(playerId: UUID)(implicit concurrent: Concurrent[F]): F[Either[UmiError.PlayerJoinError, UmiPlayer[F]]] =
    roomState.get.flatMap { urs =>
      if(urs.players.exists(_ == playerId)) {
        Queue.unbounded[F, UmiGameEvent].flatMap { peq =>
          playerEventQueues.update(_ + (playerId -> peq))
        }.as(UmiPlayer(playerId, this).asRight)
      } else {
        UmiError.RoomCantAcceptNonExistentPlayer.asLeft.pure[F].widen
      }
    }

  def add(playerId: UUID)(implicit concurrent: Concurrent[F]): F[Either[UmiError.PlayerJoinError, UmiPlayer[F]]] = for {
    _ <- roomState.update(_.addPlayer(playerId))
    player <- accept(playerId)
  } yield player

  def events(playerId: UUID)(implicit monad: Monad[F]): Stream[F, UmiGameEvent] = Stream.force(for {
    eventQueues <- playerEventQueues.get
    playerQueue = eventQueues(playerId)
  } yield Stream.fromQueueUnterminated(playerQueue))
  def sendCommand(playerId: UUID, command: UmiGameCommand)(implicit monad: Monad[F]): F[Either[UmiError.GameCommandError, Unit]] = command match {
    case UmiGameCommand.ChangeName(newName) =>
      for {
        eventQueues <- playerEventQueues.get
        allQueues = eventQueues.values.toVector
        result <- allQueues.traverse_(_.offer(UmiGameEvent.PlayerNameChange(playerId, newName)))
      } yield result.asRight
  }

}

object UmiRoom {

  def create[F[_]](id: Long, initialPlayer: UUID)(implicit concurrent: Concurrent[F], makeRef: Ref.Make[F]): F[(UmiRoom[F], UmiPlayer[F])] = for {
    roomStateRef <- Ref.of(UmiRoomState(NonEmptyChain(initialPlayer)))
    initialPlayerQueue <- Queue.unbounded[F, UmiGameEvent]
    playerEventQueueRef <- Ref.of(Map(initialPlayer -> initialPlayerQueue))
    room = UmiRoom(id, roomStateRef, playerEventQueueRef)
  } yield (room, UmiPlayer(initialPlayer, room))

}
