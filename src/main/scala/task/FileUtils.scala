package task

import java.io.File
import java.net.URI

import task.Model.TicketMultiLineParser
import task.Model.SingleTicketParser

import scala.io.BufferedSource
import zio.{IO, ZIO, ZManaged}
import zio.console.{Console, putStrLn}
import ToBooleanOps._
import cats.implicits._
import zio.stream.ZStream

object FileUtils {

  private def readFile(path: URI): ZStream[Any, String, String] = {
    def openFile(file: File): ZIO[Any, String, BufferedSource] =
      IO.effect(scala.io.Source.fromFile(file, 1000)).mapError(_.getMessage)

    def closeSource(f: BufferedSource): ZIO[Any, Nothing, _] = IO.effect(f.close()).orDie

    def file(filePath: URI): ZIO[Any, String, File] =
      IO(new File(filePath)).mapError(_.getMessage).flatMap { file =>
        if (file.exists() && file.isFile && file.canRead) IO.succeed(file) else IO.fail("File cannot be read or doesn't exist!")
      }

    ZStream.fromIteratorManaged(
      ZManaged.make(file(path).flatMap(openFile))(closeSource).map(_.getLines())
    )
  }

  /**
    * This function parses file with multiple lines and return list of type T
    * Corresponding instance of type class TicketMultiLineParser should be present in implicit scope, based on type T
    */
  def readTicketFile[T](path: URI)(implicit parser: TicketMultiLineParser[T]): IO[String, List[T]] =
    for{
      lines <-  readFile(path).runCollect
      parsed <- {
        lines.nonEmpty ?
          ZIO.fromEither(lines.flatTraverse(parser.parse).toEither.leftMap(_.toList.mkString("\n"))) |
          IO.fail[String](s"File is empty $path")
      }
    } yield parsed



  /**
    * This function parses single line file and return instance of type T
    * Corresponding instance of type class SingleTicketParser should be present in implicit scope, based on type T
    */
  def readSingleLineFile[T](path: URI)(implicit parser: SingleTicketParser[T]): IO[String, T] =
    for{
      lines <-  readFile(path).runCollect
      parsed <- lines.isEmpty.?(IO.fail(s"File is empty $path"))
      .||(lines.length > 1)(IO.fail(s"File contains more than one line!"))
      .|(IO.fromEither(parser.parse(lines.head)))
     } yield parsed
}
