This solution was done using sbt as build tool,
scala as programming language and scalatest as testing library

it's a standalone console interactive application
that covers tasks from the case study.

All assumptions regarding behavior and descriptions of design desicions
are present in the code in form of comments.

All files as examples are present in "PROJECT_PATH/src/main/resources" folder

How to use it:
First of all you need to assemble working jar:

sbt clean assembly

As result of previous command sbt spawns an artifact with name task.jar

Run application:

bash-3.2$ java -jar task.jar
Please input task name ('third' or 'second') :

third

Please input absolute path of your file with numbers of tickets:

/Users/kostyantyn/projects/second_task/src/main/resources/all_tickets.txt

Please input absolute path of your file with numbers of winning tickets:

/Users/kostyantyn/projects/second_task/src/main/resources/winner_ticket.txt

Winning class 5 - number of winning tickets 20
Winning class 10 - number of winning tickets 12
Winning class 6 - number of winning tickets 17
Winning class 9 - number of winning tickets 2
Winning class 2 - number of winning tickets 4
Winning class 3 - number of winning tickets 2


