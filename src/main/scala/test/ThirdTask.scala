package test

import zio.{ZEnv, ZIO}
import zio.console._
import test.Model.{Simple, Ticket, Winning}
import test.Model.WinningTicket.WinningSingleTicketParser$
import Utils.readLn


object ThirdTask {

  val process: ZIO[ZEnv, String, Unit] = for {
    _ <- putStrLn("Please input absolute path of your file with numbers of tickets: ")
    ticketsFilePath <- readLn
    ticketsFile <- FileUtils.checkFile(ticketsFilePath)
    simpleTickets <- FileUtils.readTicketFile[Ticket[Simple]](ticketsFile)
    _ <- putStrLn("Please input absolute path of your file with numbers of winning tickets: ")
    winningTicketsFilePath <- readLn
    winningsTicketFile <- FileUtils.checkFile(winningTicketsFilePath)
    winningTicket <- FileUtils.readSingleLineFile[Ticket[Winning]](winningsTicketFile)
    result = WinningClasses.findWinningTickets(simpleTickets, winningTicket)
    _ <- putStrLn(
      result.map { case (winclazz, amount) => s"Winning class $winclazz - number of winning tickets $amount" }.mkString("\n")
    )
  } yield ()
}
