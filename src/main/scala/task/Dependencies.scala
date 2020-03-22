package task

import java.net.URI

import task.Model.SystemTicket.allCombinations
import task.Model.{Simple, Ticket, Winning, Advanced => Sys}
import zio.{ZIO, ZLayer}
import zio.console.Console
import task.Model.SystemTicket.SystemSingleTicketParser$
import task.Model.WinningTicket.WinningSingleTicketParser$

object Dependencies {

  val secondStrategy = ZLayer.fromService[Console.Service, SecondStrategy.Service](console =>
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

  val thirdStrategy = ZLayer.fromService[Console.Service, ThirdStrategy.Service](console =>
    new ThirdStrategy.Service {
      override def strategy(): ZIO[Any, String, Unit] = for {
        _ <- console.putStrLn("Please input absolute path of your file with numbers of tickets: ")
        ticketsFilePath <- console.getStrLn.mapError(_.getMessage)
        ticketsURI <- ZIO.effect(URI.create(ticketsFilePath)).mapError(_.getMessage)
        simpleTickets <- FileUtils.readTicketFile[Ticket[Simple]](ticketsURI)
        _ <- console.putStrLn("Please input absolute path of your file with numbers of winning tickets: ")
        winningTicketsFilePath <- console.getStrLn.mapError(_.getMessage)
        winningTicketsURI <- ZIO.effect(URI.create(winningTicketsFilePath)).mapError(_.getMessage)
        winningTicket <- FileUtils.readSingleLineFile[Ticket[Winning]](winningTicketsURI)
        result = WinningClasses.findWinningTickets(simpleTickets, winningTicket)
        _ <- console.putStrLn(
          result.map { case (winclazz, amount) => s"Winning class $winclazz - number of winning tickets $amount" }.mkString("\n")
        )
      } yield ()
    }
  )
}
