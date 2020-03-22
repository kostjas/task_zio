package task

import zio.ZIO
import zio.console.{Console, getStrLn, putStrLn}

object Utils {
  val readLn: ZIO[Console, String, String] = getStrLn.mapError(_.getMessage)
}
