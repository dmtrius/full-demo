object Foobar {
  def main(args: Array[String]): Unit = {
    val n = 100
    foobar(n)
  }

  private def foobar(n: Int): Unit = {
    for (i <- 1 to n) {
      val divisibleBy3 = i % 3 == 0
      val divisibleBy5 = i % 5 == 0

      if (divisibleBy3 && divisibleBy5) {
        println("Foobar")
      } else if (divisibleBy3) {
        println("Foo")
      } else if (divisibleBy5) {
        println("Bar")
      } else {
        println(i)
      }
    }
  }
}
