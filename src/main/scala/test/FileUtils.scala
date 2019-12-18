package test

import java.io.File

import test.Model.TicketMultiLineParser
import test.Model.SingleTicketParser

import scala.io.BufferedSource
import zio.{IO, ZIO}
import zio.console.Console.Live.console.putStrLn

import ToBooleanOps._

import cats._
import cats.data._
import cats.implicits._

object FileUtils {

  private def openFile(file: File): ZIO[Any, String, BufferedSource] =
    IO.effect(scala.io.Source.fromFile(file, 1000)).catchAll(e => IO.fail(e.getMessage))

  private def closeSource(f: BufferedSource): ZIO[Any, Nothing, _] =
    IO.effect(f.close()).catchAll(e => putStrLn(s"Cannot close BufferedSource correctly: ${e.getMessage}"))

  /**
    * This function parses file with multiple lines and return list of type T
    * Corresponding instance of type class TicketMultiLineParser should be present in implicit scope, based on type T
    */
  def readTicketFile[T](file: File)(implicit parser: TicketMultiLineParser[T]): IO[String, List[T]] = {
    openFile(file).bracket(bf => closeSource(bf)){ bf =>
      val lines = bf.getLines().toList
      def parsedLinesResult(lines: List[String]): ZIO[Any, String, List[T]] =
        lines.traverse(parser.parse).map(_.flatten).toEither.leftMap(_.toList.mkString("\n")).fold(IO.fail, IO.succeed)

      lines.nonEmpty ? parsedLinesResult(lines) | IO.fail[String](s"File is empty ${file.getAbsolutePath}")
    }
  }

  def checkFile(filePath: String): ZIO[Any, String, File] = IO(new File(filePath)).catchAll(e => IO.fail(e.getMessage)).flatMap { file =>
    if (file.exists() && file.isFile && file.canRead) IO.succeed(file) else IO.fail("File cannot be read or doesn't exist!")
  }

  /**
    * This function parses single line file and return instance of type T
    * Corresponding instance of type class SingleTicketParser should be present in implicit scope, based on type T
    */
  def readSingleLineFile[T](file: File)(implicit parser: SingleTicketParser[T]): IO[String, T] = {
    openFile(file).bracket.apply(bf => closeSource(bf)){ bf => {
      val lines = bf.getLines().toList
      lines.isEmpty.?(IO.fail(s"File is empty ${file.getAbsolutePath}"))
        .||(lines.length > 1)(IO.fail(s"File contains more than one line!"))
        .|(IO.fromEither(parser.parse(lines.head)))
    }}
  }
}
