import scala.compiletime.ops.string
//Задание 2.1 
@main def goodEnoughPassword1Test() = 
    val password1 = "Ab03c" 
    val password2 = "Ph234435/"
    println(goodEnoughPassword1(password1))
    println(goodEnoughPassword1(password2))

def goodEnoughPassword1(password:String):Boolean = {
    val checks: Seq[Boolean] = Seq(
        password.length >=8,
        password.exists(_.isUpper),
        password.exists(_.isLower),
        password.exists(_.isDigit),
        password.exists("!$%^&*_+?/|".contains(_)),
    )
    checks.reduce(_ && _)
}
    
