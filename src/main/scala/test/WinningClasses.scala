package test

import test.Model.{Simple, Ticket, Winning}

object WinningClasses {

  case class WinningClass private (correctFields: Int, correctAddFields: Int, winCLass: Int) {
    lazy val message: String =
      s"Winning class $winCLass : $correctFields correct numbers + $correctAddFields correct additional numbers"
  }

  private lazy val winningClasses = Set(
    //Winning class 1               5 correct numbers + 2 correct additional numbers
    WinningClass(5, 2, 1),
    //Winning class 2               5 correct numbers + 1 correct additional number
    WinningClass(5, 1, 2),
    //Winning class 3               5 correct numbers
    WinningClass(5, 0, 3),
    //Winning class 4               4 correct numbers + 2 correct additional numbers
    WinningClass(4, 2, 4),
    //Winning class 5               4 correct numbers + 1 correct additional number
    WinningClass(4, 1, 5),
    //Winning class 6               4 correct numbers
    WinningClass(4, 0, 6),
    //Winning class 7               3 correct numbers + 2 correct additional numbers
    WinningClass(3, 2, 7),
    //Winning class 8               2 correct numbers + 2 correct additional numbers
    WinningClass(2, 2, 8),
    //Winning class 9               3 correct numbers + 1 correct additional number
    WinningClass(3, 1, 9),
    //Winning class 10              3 correct numbers
    WinningClass(3, 0, 10),
    //Winning class 11              1 correct number  + 2 correct additional numbers
    WinningClass(1, 2, 11),
    //Winning class 12              2 correct numbers + 1 correct additional number
    WinningClass(2, 1, 12),
    //Winning class 13              2 correct numbers
    WinningClass(2, 0, 13)
  )

  def findWinningClass(amountOfWinningFields: Int, amountOfWinningAddFields: Int): Option[WinningClass] =
    winningClasses.find(p => p.correctAddFields == amountOfWinningAddFields && p.correctFields == amountOfWinningFields)

  def findWinningTickets(tickets: List[Ticket[Simple]], winningTicket: Ticket[Winning]): Map[Int, Int] = {
    val winningFieldNumbers = winningTicket.fields
    val winningAddFieldNumbers = winningTicket.additionalFields


    tickets.foldLeft(Map.empty[Int, Int]){(acc, ticket) =>
      val winningNumbers = ticket.fields.count(winningFieldNumbers.contains)
      val winningAdditionalNumbers = ticket.additionalFields.count(winningAddFieldNumbers.contains)

      (winningNumbers, winningAdditionalNumbers) match {
        case (0, 0) => acc
        case (winFields, winAdditionalFields) =>
          findWinningClass(winFields, winAdditionalFields)
            .fold(acc)(w => acc.get(w.winCLass).fold(acc.+((w.winCLass, 1)))(amount => acc.+((w.winCLass, amount + 1))))
      }
    }
  }
}
