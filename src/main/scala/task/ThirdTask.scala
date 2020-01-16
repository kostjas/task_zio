package task

import zio.ZIO
import zio.console._
import task.Model.{Simple, Ticket, Winning}
import task.Model.WinningTicket.WinningSingleTicketParser$
import Utils.readLn


object ThirdTask {

  val process: ZIO[Console, String, Unit] = for {
    _ <- putStrLn("Please input absolute path of your file with numbers of tickets: ")
    ticketsFilePath <- readLn
    simpleTickets <- FileUtils.readTicketFile[Ticket[Simple]](ticketsFilePath)
    _ <- putStrLn("Please input absolute path of your file with numbers of winning tickets: ")
    winningTicketsFilePath <- readLn
    winningTicket <- FileUtils.readSingleLineFile[Ticket[Winning]](winningTicketsFilePath)
    result = WinningClasses.findWinningTickets(simpleTickets, winningTicket)
    _ <- putStrLn(
      result.map { case (winclazz, amount) => s"Winning class $winclazz - number of winning tickets $amount" }.mkString("\n")
    )
  } yield ()
}
