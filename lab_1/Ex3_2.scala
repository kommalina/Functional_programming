//Использование reduce
@main def maxElemOfCollectionTest1() =
    val coll = Seq(15, 987, 4, 76, 465, 2, 40)
    println(maxElemOfCollection1(coll))

//Функция, возвращающая максимальный элемент из коллекции
def maxElemOfCollection1(n:Seq[Int]):Int = 
    val maxElem = n.reduce(_ max _)
    maxElem


    
//Использование filter  
@main def maxElemOfCollectionTest2() =
    val coll = Seq(15, 987, 4, 76, 465, 2, 40)
    println(maxElemOfCollection2(coll))

//Функция, возвращающая максимальный элемент из коллекции 
def maxElemOfCollection2(n:Seq[Int]):Int = 
    val maxElem = n.filter(_ == n.max).head
    maxElem
