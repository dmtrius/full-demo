case class _Purchase(amount: Double, category: String)

object LoyaltyPointsCalculator {
  // Loyalty points calculation rules
  def calculateLoyaltyPoints(purchases: List[_Purchase]): Int = {
    purchases.foldLeft(0) { (totalPoints, purchase) =>
      // Different point multipliers based on purchase category
      val categoryMultiplier = purchase.category match {
        case "Electronics" => 3
        case "Clothing"    => 2
        case "Groceries"   => 1
        case _             => 1
      }

      // Calculate points: 1 point per dollar, multiplied by category bonus
      val purchasePoints = (purchase.amount * categoryMultiplier).toInt

      // Accumulate total points
      totalPoints + purchasePoints
    }
  }

  def main(args: Array[String]): Unit = {
    val customerPurchases = List(
      _Purchase(100.0, "Electronics"),
      _Purchase(50.0, "Clothing"),
      _Purchase(200.0, "Groceries")
    )

    val totalLoyaltyPoints = calculateLoyaltyPoints(customerPurchases)
    println(s"Total Loyalty Points: $totalLoyaltyPoints")
  }
}
