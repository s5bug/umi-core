package tf.bug.umi

import cats.effect._

class UmiTests extends munit.CatsEffectSuite {

  test("umi rooms start with their initial player") {
    for {
      umi <- Umi.of[IO]
      newUmiPlayer <- umi.createRoom
      roomState <- newUmiPlayer.room.roomState.get
      roomPlayer = roomState.players.head
    } yield assertEquals(roomPlayer, newUmiPlayer.uuid)
  }

}
