@main def composeTest() = 
  val divIntoThree: Double => Int = x => (x / 3).toInt
  val toString: Int => String = x => x.toString
  println(compose(divIntoThree, toString)(79.61))

//Композиция функций
def compose(f1:Double => Int, f2:Int => String):Double=>String = 
  x => f2(f1(x))