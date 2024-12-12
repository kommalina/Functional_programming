@main def integralTest() = 
    val i = integral(f, 1.4, 3, 100)
    println(i)

def f(x:Double):Double = 
    math.pow(x,2)*(math.log(x)/math.log(10))

def integral(f:Double => Double, l:Double, r:Double, steps:Int):Double = 
    val h = (r-l)/steps
    (1 to steps).map(i => h*f(l+i*h)).sum