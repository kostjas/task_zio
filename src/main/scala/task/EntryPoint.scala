package task

import zio.{App, IO, ZIO}
import zio.console._
import Utils.readLn

object EntryPoint extends App {

  val secondTask = "second"
  val thirdTask = "third"

  /**
    * The world is unsafe and we have to protect ourself using IO monad
    * for input and output operations, also it can be easily composed
    *
    * The application processes file as a single batch, it expects that all records are correct
    * if some of the records are incorrect, it will reject the whole file, however it accumulates
    * all errors and report all of them
    *
    * Of course in case of huge files we need to use streaming approach, but it wasn't as requirement of this
    * task.
    */
  val process: ZIO[Console with SecondStrategy.CalcStrategy with ThirdStrategy.CalcStrategy, String, Unit] = (for {
    _ <- putStrLn(s"Please input task name ('$thirdTask' or '$secondTask') : ")
    input <- readLn
    _ <- processInput(input)
  } yield ())

  def processInput(input: String): ZIO[Console with SecondStrategy.CalcStrategy with ThirdStrategy.CalcStrategy, String, Unit] =
    input match {
      case `secondTask` => SecondStrategy.secondStrategy

      case `thirdTask` => ThirdStrategy.thirdStrategy

      case task =>
        IO.fail(s"The task name: '$task' is incorrect, only $thirdTask or $secondTask are expected!")
    }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    process.map(_ => 0)
      .provideSomeLayer(Console.live ++ Dependencies.secondStrategy ++ Dependencies.thirdStrategy)
      .catchAll(e => putStrLn(e).map(_ => 1))
}
