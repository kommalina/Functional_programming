@main def tupleOfCollectionsTest() =
    val coll = Seq(45, 23, 14, 6, 13, 7)
    println(tupleOfCollections(coll))

//Функция, возвращающая кортеж из 2х коллекций(четная, нечетная)
def tupleOfCollections(n:Seq[Int]):(Seq[Int], Seq[Int]) =
    val coll1 = n.filter(_%2 == 0)
    val coll2 = n.filter(_%2 != 0)
    (coll1, coll2) 
