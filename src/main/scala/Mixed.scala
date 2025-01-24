object Mixed {
  def main(args: Array[String]): Unit = {
    println("hello")

    println(isPrime(17)) // true
    println(isPrime(25)) // false

    test(isPrime)
    test(isPrimeStream)
  }

  def test(f: (n: Int) => Unit): Unit = {
    // For performance testing
    val start = System.nanoTime
    (1 to 1000000).foreach(_ => f(104729))
    val end = System.nanoTime
    println(s"Time taken: ${(end - start) / 1000000.0} ms")
  }

  def isPrime(n: Int): Boolean = {
    @scala.annotation.tailrec
    def checkDivisors(i: Int, sqrt: Int): Boolean = {
      if (i > sqrt) true
      else if (n % i == 0 || n % (i + 2) == 0) false
      else checkDivisors(i + 6, sqrt)
    }

    if (n < 2) false
    else if (n == 2 || n == 3) true
    else if ((n & 1) == 0) false // Bitwise check for even numbers
    else if (n % 3 == 0) false
    else checkDivisors(5, math.sqrt(n).toInt)
  }

  private def isPrimeStream(n: Int): Boolean = n match {
    case n if n < 2 => false
    case 2 | 3 => true
    case n if (n & 1) == 0 => false
    case n if n % 3 == 0 => false
    case n =>
      val sqrt = math.sqrt(n).toInt
      LazyList.iterate(5)(_ + 6)
        .takeWhile(_ <= sqrt)
        .forall(i => n % i != 0 && n % (i + 2) != 0)
  }
}
