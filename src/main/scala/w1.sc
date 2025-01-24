val numbers: Seq[Int] = List(1, 2, 3, 4, 5)

// Sum of all numbers
val sum = numbers.foldLeft(0)(_ + _)
println(s"Sum of all numbers: $sum")
// Build a string by concatenating
val stringResult = numbers.foldLeft("Initial: ")((acc, num) => acc + num)
println(s"String result: $stringResult")

// Reverse a list using foldRight
val reversedList = numbers.foldRight(List.empty[Int])(_ :: _)
println(s"Reversed list: $reversedList")
// Complex transformation
val complexity = numbers.foldRight(1)(_ * _)
println(s"Complexity: $complexity")

// Subtraction shows left vs right order matters
val leftSubtract = numbers.foldLeft(0)(_ - _)   // ((((0-1)-2)-3)-4)-5
println(s"Left subtraction: $leftSubtract")
val rightSubtract = numbers.foldRight(0)(_ - _) // 1-(2-(3-(4-(5-0))))
println(s"Right subtraction: $rightSubtract")

val someIntValue = Some(10)
val someStringValue = Some("Ten")
val result = for {
  intValue <- someIntValue
  stringValue <- someStringValue
} yield {
  s"""$intValue is $stringValue"""
}

lazy val foo = {
  println("Initialized")
  1
}
//println(foo)
