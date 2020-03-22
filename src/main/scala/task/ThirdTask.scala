package task

import java.net.URI

import zio.ZIO
import zio.console._
import task.Model.{Simple, Ticket, Winning}
import task.Model.WinningTicket.WinningSingleTicketParser$
import Utils.readLn

import scala.util.Try


object ThirdTask {

  val process: ZIO[Console, String, Unit] = for {
    _ <- putStrLn("Please input absolute path of your file with numbers of tickets: ")
    ticketsFilePath <- readLn
    ticketsURI <- ZIO.effect(URI.create(ticketsFilePath)).mapError(_.getMessage)
    simpleTickets <- FileUtils.readTicketFile[Ticket[Simple]](ticketsURI)
    _ <- putStrLn("Please input absolute path of your file with numbers of winning tickets: ")
    winningTicketsFilePath <- readLn
    winningTicketsURI <- ZIO.effect(URI.create(winningTicketsFilePath)).mapError(_.getMessage)
    winningTicket <- FileUtils.readSingleLineFile[Ticket[Winning]](winningTicketsURI)
    result = WinningClasses.findWinningTickets(simpleTickets, winningTicket)
    _ <- putStrLn(
      result.map { case (winclazz, amount) => s"Winning class $winclazz - number of winning tickets $amount" }.mkString("\n")
    )
  } yield ()
}
