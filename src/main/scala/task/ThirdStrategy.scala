package task

import zio.{Has, ZIO}

object ThirdStrategy {
  type CalcStrategy = Has[Service]

  trait Service {
    def strategy(): ZIO[Any, String, Unit]
  }

  val thirdStrategy: ZIO[CalcStrategy, String, Unit] = ZIO.accessM[CalcStrategy](_.get.strategy())
}
