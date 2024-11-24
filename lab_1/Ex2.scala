//Вывод Hello несколько раз с условием 
@main def multyHelloTest() = 
  multyHello(10)

def multyHello(n:Int) =
  for i <- 1 to n do 
    if (i%2 == 0) println(s"Hello $i")  
    else println(s"Hello ${n-i}") 
    
