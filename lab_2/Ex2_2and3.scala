import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

//Задание 2.2 на использование Try
@main def goodEnoughPassword2Test() =
  val password1 = "Ab03c" 
  val password2 = "Ph234435/"
  println(goodEnoughPassword2(password1))
  println(goodEnoughPassword2(password2))

def goodEnoughPassword2(password:String): Either[Boolean, String] = Try{
    val checks = Seq(
        (password.length >=8) -> "Длина пароля должны быть >=8",
        password.exists(_.isUpper) -> "В пароле нет заглавных букв",
        password.exists(_.isLower) -> "В пароле нет букв в нижнем регистре",
        password.exists(_.isDigit) -> "В пароле нет цифр",
        password.exists("!$%^&*_+?/|".contains(_)) -> "В пароле нет ни одного специального символа",
    )
    // Сбор ошибок
    val errors = checks.filterNot(_._1).map(_._2)

    errors match {
      case Nil => Left(true)               // Если список ошибок пуст, то пароль верный
      case  _  => Right(errors.toString()) // Список ошибок
    }
} match{
  case Success(res) => res
  case Failure(_) => Right("Произошла ошибка")  
}

// Задание 2.3 на использование Future
 @main def printPassword() = 
  val passwordFuture = readPassword()
  val resultPassword = Await.result(passwordFuture, Duration.Inf)
  println("Пароль верный")

 def readPassword():Future[String] = Future{
  println("Введите пароль: ")
  val password = readLine()
  
  goodEnoughPassword2(password) match {
    case Left(_) => password  
    case Right(errors) => 
      println(errors)  
      Await.result(readPassword(), Duration.Inf)
  }
}

