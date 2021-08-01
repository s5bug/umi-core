package tf.bug.umi

import cats.data._

import java.util.UUID

case class UmiRoomState(
  players: NonEmptyChain[UUID]
) {

  def addPlayer(playerId: UUID): UmiRoomState =
    UmiRoomState(players :+ playerId)

}
