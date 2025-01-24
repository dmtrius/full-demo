object FoldFunctions {
  // Basic list for demonstrations
  val numbers: Seq[Int] = List(1, 2, 3, 4, 5)

  // FoldLeft: Accumulate from left to right
  def demonstrateFoldLeft(): Unit = {
    // Sum of all numbers
    val sum = numbers.foldLeft(0)(_ + _)

    // Build a string by concatenating
    val stringResult = numbers.foldLeft("Initial: ")((acc, num) => acc + num)
  }

  // FoldRight: Accumulate from right to left
  def demonstrateFoldRight(): Unit = {
    // Reverse a list using foldRight
    val reversedList = numbers.foldRight(List.empty[Int])(_ :: _)

    // Complex transformation
    val complexity = numbers.foldRight(1)(_ * _)
  }

  // Advanced example showing difference
  def showFoldDifference(): Unit = {
    // Subtraction shows left vs right order matters
    val leftSubtract = numbers.foldLeft(0)(_ - _)   // ((((0-1)-2)-3)-4)-5
    val rightSubtract = numbers.foldRight(0)(_ - _) // 1-(2-(3-(4-(5-0))))
  }
}