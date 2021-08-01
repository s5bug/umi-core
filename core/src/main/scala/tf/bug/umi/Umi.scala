package tf.bug.umi

import cats._
import cats.effect._
import cats.effect.std.UUIDGen
import cats.syntax.all._

case class Umi[F[_]](
  nextRoomId: Ref[F, Long],
  rooms: Ref[F, Map[Long, UmiRoom[F]]],
) {

  def createRoom(implicit concurrent: Concurrent[F], uuidGen: UUIDGen[F], makeRef: Ref.Make[F]): F[UmiPlayer[F]] = for {
    roomId <- nextRoomId.getAndUpdate(_ + 1L)
    playerId <- uuidGen.randomUUID
    (newRoom, playerConnection) <- UmiRoom.create[F](roomId, playerId)
    _ <- rooms.update(_ + (roomId -> newRoom))
  } yield playerConnection

  def joinRoom(roomId: Long)(implicit concurrent: Concurrent[F], uuidGen: UUIDGen[F]): F[Either[UmiError.PlayerJoinError, UmiPlayer[F]]] = for {
    playerId <- uuidGen.randomUUID
    theRooms <- rooms.get
    theRoom = theRooms(roomId)
    playerConnection <- theRoom.add(playerId)
  } yield playerConnection

}

object Umi {

  def of[F[_]](implicit monad: Monad[F], makeRef: Ref.Make[F]): F[Umi[F]] = for {
    roomIdRef <- Ref.of[F, Long](0L)
    roomsRef <- Ref.of[F, Map[Long, UmiRoom[F]]](Map())
  } yield Umi(roomIdRef, roomsRef)

}
