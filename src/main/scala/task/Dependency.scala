package task

import java.net.URI

import task.Model.SystemTicket.allCombinations
import task.Model.Ticket
import zio.{Has, ZIO, ZLayer}
import zio.console.Console
import task.Model.{Advanced => Sys}
import task.Model.SystemTicket.SystemSingleTicketParser$

object Dependency {

  val envBuilder = ZLayer.fromService[Console.Service, SecondStrategy.Service](console =>
    new SecondStrategy.Service {
        override def strategy(): ZIO[Any, String, Unit] = for {
          _ <- console.putStrLn("Please input absolute path of your file with system ticket: ")
          filePath <- console.getStrLn.mapError(_.getMessage)
          uri <- ZIO.effect(URI.create(filePath)).mapError(_.getMessage)
          ticket <- FileUtils.readSingleLineFile[Ticket[Sys]](uri)
          r = allCombinations(ticket)
          _ <- console.putStrLn("Ticket distributions: ")
          _ <- console.putStrLn(r.map(t => s"${t.fields.mkString(",")} ${t.additionalFields.mkString(", ")}").mkString("\n"))
        } yield ()
      }
    )

}
