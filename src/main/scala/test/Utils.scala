package test

import zio.{ZEnv, ZIO}
import zio.console.getStrLn

object Utils {
  val readLn: ZIO[ZEnv, String, String] = getStrLn.mapError(_.getMessage)
}
