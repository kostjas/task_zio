package task

import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor3}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import task.Model.SimpleTicket
import task.Model.WinningTicket

class WinningClassesSpec extends AnyWordSpec with Matchers with TableDrivenPropertyChecks {

  val winningClassesSuccesses: TableFor3[Int, Int, Int] =
    Table(
      ("fields", "additionalFields", "winclass"),
      //Winning class 1               5 correct numbers + 2 correct additional numbers
      (5, 2, 1),
      //Winning class 2               5 correct numbers + 1 correct additional number
      (5, 1, 2),
      //Winning class 3               5 correct numbers
      (5, 0, 3),
      //Winning class 4               4 correct numbers + 2 correct additional numbers
      (4, 2, 4),
      //Winning class 5               4 correct numbers + 1 correct additional number
      (4, 1, 5),
      //Winning class 6               4 correct numbers
      (4, 0, 6),
      //Winning class 7               3 correct numbers + 2 correct additional numbers
      (3, 2, 7),
      //Winning class 8               2 correct numbers + 2 correct additional numbers
      (2, 2, 8),
      //Winning class 9               3 correct numbers + 1 correct additional number
      (3, 1, 9),
      //Winning class 10              3 correct numbers
      (3, 0, 10),
      //Winning class 11              1 correct number  + 2 correct additional numbers
      (1, 2, 11),
      //Winning class 12              2 correct numbers + 1 correct additional number
      (2, 1, 12),
      //Winning class 13              2 correct numbers
      (2, 0, 13)
    )

  val winningClassesFailures: TableFor2[Int, Int] =
    Table(
      ("fields", "additionalFields"),
      (1, 0),
      (0, 1),
      (0, 0)
    )

  val winSimpleTickets: TableFor3[List[Model.Ticket[Model.Simple]], Model.Ticket[Model.Winning], Map[_ <: Int, Int]] =
    Table(
      ("simpleTickets", "winTicket", "result"),
      (List(
        SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), SimpleTicket(Set(2, 3, 32, 34, 5), Set(2, 6))
      ),
        WinningTicket(Set(1, 2, 33, 23, 5), Set(2, 3)),
        Map(1 -> 1, 12 -> 1)
      ),
      (List(
        SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3))
      ),
        WinningTicket(Set(1, 2, 33, 23, 5), Set(2, 3)),
        Map(1 -> 2)
      ),
      (List(
        SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3))
      ),
        WinningTicket(Set(11, 2, 44, 50, 5), Set(4, 5)),
        Map(13 -> 2)
      ),
      (List(
        SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), SimpleTicket(Set(1, 2, 33, 23, 5), Set(2, 3))
      ),
        WinningTicket(Set(11, 22, 44, 50, 5), Set(4, 5)),
        Map()
      ),
      (List(), WinningTicket(Set(11, 22, 44, 50, 5), Set(4, 5)), Map())
    )

  "WinningClasses" should {
    forAll(winningClassesSuccesses) { (fields, additionalFields, winclass) =>
      s"finds all winning classes for $fields and $additionalFields of class $winclass" in {
        WinningClasses.findWinningClass(fields, additionalFields) should not be None
      }
    }

    forAll(winningClassesFailures) { (fields, additionalFields) =>
      s"shouldn't find any failure winning classes for $fields and $additionalFields" in {
        WinningClasses.findWinningClass(fields, additionalFields) shouldBe None
      }
    }

    forAll(winSimpleTickets) { (simpleTickets, winTicket, result) =>
      s"finds all winning tickets for $simpleTickets of winTicket $winTicket -- $result" in {
        WinningClasses.findWinningTickets(simpleTickets, winTicket) shouldBe result
      }
    }
  }
}