package test

import zio.{App, IO, ZEnv, ZIO}
import zio.console._

object EntryPoint extends App {

  val secondTask = "second"
  val thirdTask = "third"

  val readLn: ZIO[ZEnv, String, String] = getStrLn.mapError(_.getMessage)
  /**
    * The world is unsafe and we have to protect ourself using IO monad
    * for input and output operations, also it can be easily composed
    *
    * Also I am using EitherT monad-transformer here, because there are
    * two effects: IO operations and errors in the context of disjunction \/
    * The application processes file as a single batch, it expects that all records are correct
    * if some of the records are incorrect, it will reject the whole file, however it accumulates
    * all errors and report all of them
    *
    * Of course in case of huge files we need to use streaming approach, but it wasn't as requirement of this
    * task.
    */
  val process: ZIO[ZEnv, String, Unit] = for {
    _ <- putStrLn(s"Please input task name ('$thirdTask' or '$secondTask') : ")
    input <- readLn
    _ <- processInput(input)
  } yield ()

  def processInput(input: String): ZIO[ZEnv, String, Unit] =
    input match {
      case task if task == secondTask => SecondTask.process

      case task if task == thirdTask => ThirdTask.process

      case task =>
        IO.fail(s"The task name: '$task' is incorrect, only $thirdTask or $secondTask are expected!")
    }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = process.map(_ => 0).catchAll(e => putStrLn(e).map(_ => 1))
}
