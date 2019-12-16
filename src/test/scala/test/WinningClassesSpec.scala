package test

import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}
import org.scalatest.Matchers
import org.scalatest.WordSpec
import test.Model.EuroTicket
import test.Model.WinningTicket

class WinningClassesSpec extends WordSpec with Matchers with TableDrivenPropertyChecks {

  val winningClassesSuccesses: TableFor3[Int, Int, Int] =
    Table(
      ("fields", "starFields", "winclass"),
      //Winning class 1               5 correct numbers + 2 correct star numbers
      (5, 2, 1),
      //Winning class 2               5 correct numbers + 1 correct star number
      (5, 1, 2),
      //Winning class 3               5 correct numbers
      (5, 0, 3),
      //Winning class 4               4 correct numbers + 2 correct star numbers
      (4, 2, 4),
      //Winning class 5               4 correct numbers + 1 correct star number
      (4, 1, 5),
      //Winning class 6               4 correct numbers
      (4, 0, 6),
      //Winning class 7               3 correct numbers + 2 correct star numbers
      (3, 2, 7),
      //Winning class 8               2 correct numbers + 2 correct star numbers
      (2, 2, 8),
      //Winning class 9               3 correct numbers + 1 correct star number
      (3, 1, 9),
      //Winning class 10              3 correct numbers
      (3, 0, 10),
      //Winning class 11              1 correct number  + 2 correct star numbers
      (1, 2, 11),
      //Winning class 12              2 correct numbers + 1 correct star number
      (2, 1, 12),
      //Winning class 13              2 correct numbers
      (2, 0, 13)
    )

  val winningClassesFailures =
    Table(
      ("fields", "starFields"),
      (1, 0),
      (0, 1),
      (0, 0)
    )

  val winEuroTickets: TableFor3[List[Model.Ticket[Model.Euro]], Model.Ticket[Model.Winning], Map[_ <: Int, Int]] =
    Table(
      ("euroTickets", "winTicket", "result"),
      (List(
        EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), EuroTicket(Set(2, 3, 32, 34, 5), Set(2, 6))
      ),
        WinningTicket(Set(1, 2, 33, 23, 5), Set(2, 3)),
        Map(1 -> 1, 12 -> 1)
      ),
      (List(
        EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3))
      ),
        WinningTicket(Set(1, 2, 33, 23, 5), Set(2, 3)),
        Map(1 -> 2)
      ),
      (List(
        EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3))
      ),
        WinningTicket(Set(11, 2, 44, 50, 5), Set(4, 5)),
        Map(13 -> 2)
      ),
      (List(
        EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3)), EuroTicket(Set(1, 2, 33, 23, 5), Set(2, 3))
      ),
        WinningTicket(Set(11, 22, 44, 50, 5), Set(4, 5)),
        Map()
      ),
      (List(), WinningTicket(Set(11, 22, 44, 50, 5), Set(4, 5)), Map())
    )

  "WinningClasses" should {
    forAll(winningClassesSuccesses) { (fields, starFields, winclass) =>
      s"finds all winning classes for $fields and $starFields of class $winclass" in {
        WinningClasses.findWinningClass(fields, starFields) should not be None
      }
    }

    forAll(winningClassesFailures) { (fields, starFields) =>
      s"shouldn't find any failure winning classes for $fields and $starFields" in {
        WinningClasses.findWinningClass(fields, starFields) shouldBe None
      }
    }

    forAll(winEuroTickets) { (euroTickets, winTicket, result) =>
      s"finds all winning tickets for $euroTickets of winTicket $winTicket -- $result" in {
        WinningClasses.findWinningTickets(euroTickets, winTicket) shouldBe result
      }
    }
  }
}