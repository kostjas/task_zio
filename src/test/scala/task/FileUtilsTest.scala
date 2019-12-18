package task

import java.io.File

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import task.Model.{Simple, SystemTicket, Ticket, Winning, WinningTicket, Advanced => Sys}
import zio._
import task.Model.SystemTicket.SystemSingleTicketParser$
import task.Model.WinningTicket.WinningSingleTicketParser$
import zio.Exit.Failure

class FileUtilsTest extends AnyWordSpec with DefaultRuntime with Matchers {

  "FileUtils" should {
    "read file with all tickets" in {
      val file = new File(getClass.getResource("/all_tickets.txt").toURI)
      val result = unsafeRunSync(FileUtils.readTicketFile[Ticket[Simple]](file))
      result.map(_.size) shouldBe Exit.succeed(57)
    }

    "read failure file with tickets" in {
      val file = new File(getClass.getResource("failure_all_tickets.txt").toURI)
      val result = unsafeRunSync(FileUtils.readTicketFile[Ticket[Simple]](file))
      result shouldBe Exit.fail(
        "requirement failed: TicketType can be either standard or system!\n" +
          "requirement failed: Line must contain three elements, separated by space!"
      )
    }

    "read file with single SystemTicket" in {
      val file = new File(getClass.getResource("ticket.txt").toURI)
      val result = unsafeRunSync(FileUtils.readSingleLineFile[Ticket[Sys]](file))
      result shouldBe Exit.succeed(SystemTicket(Set(5, 1, 2, 32, 34, 45),Set(1, 3, 5)))
    }

    "read file with single WinningTicket" in {
      val file = new File(getClass.getResource("winner_ticket.txt").toURI)
      val result = unsafeRunSync(FileUtils.readSingleLineFile[Ticket[Winning]](file))
      result shouldBe Exit.succeed(WinningTicket(Set(5, 1, 2, 32, 34),Set(1, 3)))
    }
  }
}