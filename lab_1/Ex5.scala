@main def patternMatchingTest() =
    val coll = Seq(3, 54.7, "ЪЕЪ", 78.4, 8, 17.4, ())
    println(patternMatching(coll))

//Функция, сопоставляющая тип c элементом коллекции 
def patternMatching(n:Seq[Any]):Seq[String] =
    n.map{ i => i match
        case _:Int => "integer: " + i
        case _:Double => "double: " + i
        case _:String => "string:"  + i
        case _ => "Unknown" + i
    } 
