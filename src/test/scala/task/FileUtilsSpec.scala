package task

import java.io.File
import task.Model.{Simple, SystemTicket, Ticket, Winning, WinningTicket, Advanced => Sys}
import zio._
import task.Model.SystemTicket.SystemSingleTicketParser$
import task.Model.WinningTicket.WinningSingleTicketParser$
import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

object FileUtilsSpec extends DefaultRunnableSpec(
  suite("FileUtilsSpec")(
    testM("read file with all tickets") {
      val uri = getClass.getResource("/all_tickets.txt").toURI
      val file = new File(uri)
      FileUtils.readTicketFile[Ticket[Simple]](file).map { result =>
        assert(result.size, equalTo(57))
      }
    },
    testM("read failure file with tickets") {
      val uri = getClass.getResource("/failure_all_tickets.txt").toURI
      val file = new File(uri)
      FileUtils.readTicketFile[Ticket[Simple]](file).flip.map { result =>
        assert(result, equalTo(
          "requirement failed: TicketType can be either standard or system!\n" +
            "requirement failed: Line must contain three elements, separated by space!"))
      }
    },
    testM("read file with single SystemTicket") {
      val res = getClass.getResource("/ticket.txt").toURI
      val file = new File(res)
      FileUtils.readSingleLineFile[Ticket[Sys]](file).map { result =>
        assert(result, equalTo(SystemTicket(Set(5, 1, 2, 32, 34, 45), Set(1, 3, 5))))
      }
    },
    testM("read file with single WinningTicket") {
      val res = getClass.getResource("/winner_ticket.txt").toURI
      val file = new File(res)
      FileUtils.readSingleLineFile[Ticket[Winning]](file).map { result =>
        assert(result, equalTo(WinningTicket(Set(5, 1, 2, 32, 34), Set(1, 3))))
      }
    }
  )
)