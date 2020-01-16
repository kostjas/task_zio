package task

import task.Model.Ticket
import task.Model.{Advanced => Sys}
import task.Model.SystemTicket.allCombinations
import zio.{IO, ZIO}
import zio.console._
import task.Model.SystemTicket.SystemSingleTicketParser$

object SecondTask {

  val process: ZIO[Console, String, Unit] = for {
    _ <- putStrLn("Please input absolute path of your file with system ticket: ")
    filePath <- getStrLn.mapError(_.getMessage)
    ticket <- FileUtils.readSingleLineFile[Ticket[Sys]](filePath)
    r = allCombinations(ticket)
    _ <- putStrLn("Ticket distributions: ")
    _ <- putStrLn(r.map(t => s"${t.fields.mkString(",")} ${t.additionalFields.mkString(", ")}").mkString("\n"))
  } yield ()
}
