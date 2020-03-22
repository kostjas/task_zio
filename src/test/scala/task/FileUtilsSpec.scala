package task

import task.Model.{Simple, SystemTicket, Ticket, Winning, WinningTicket, Advanced => Sys}
import task.Model.SystemTicket.SystemSingleTicketParser$
import task.Model.WinningTicket.WinningSingleTicketParser$
import zio.test._
import zio.test.Assertion._

object FileUtilsSpec extends DefaultRunnableSpec {
  val suites = suite("FileUtilsSpec")(
    testM("read file with all tickets") {
      val uri = getClass.getResource("/all_tickets.txt").toURI
      FileUtils.readTicketFile[Ticket[Simple]](uri).map { result =>
        assert(result.size)(equalTo(57))
      }
    },
    testM("read failure file with tickets") {
      val uri = getClass.getResource("/failure_all_tickets.txt").toURI
      FileUtils.readTicketFile[Ticket[Simple]](uri).flip.map { result =>
        assert(result)(equalTo(
          "requirement failed: TicketType can be either standard or system!\n" +
            "requirement failed: Line must contain three elements, separated by space!"))
      }
    },
    testM("read file with single SystemTicket") {
      val uri = getClass.getResource("/ticket.txt").toURI
      FileUtils.readSingleLineFile[Ticket[Sys]](uri).map { result =>
        assert(result)(equalTo(SystemTicket(Set(5, 1, 2, 32, 34, 45), Set(1, 3, 5))))
      }
    },
    testM("read file with single WinningTicket") {
      val uri = getClass.getResource("/winner_ticket.txt").toURI
      FileUtils.readSingleLineFile[Ticket[Winning]](uri).map { result =>
        assert(result)( equalTo(WinningTicket(Set(5, 1, 2, 32, 34), Set(1, 3))))
      }
    }
  )

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suites
}