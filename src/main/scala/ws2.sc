sealed trait MyResult[A]
case class Success[A](value: A) extends MyResult[A]
case class Failure(message: String) extends MyResult[Nothing]

def divide(x: Double, y: Double): MyResult[Double] =
  if (y == 0)
    Failure("Division by zero")
  else
    Success(x / y)

print(divide(2, 0))
