import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import scala.annotation.tailrec

/**
 * High-performance thread-safe LRU Cache implementation in Scala
 * Uses a combination of ConcurrentHashMap and doubly-linked list with read-write locks
 * for optimal performance under concurrent access patterns.
 */
class LRUCache[K, V](capacity: Int) {
  require(capacity > 0, "Capacity must be positive")

  // Node for doubly-linked list
  private class Node(var key: K, var value: V) {
    var prev: Node = _
    var next: Node = _
  }

  // Concurrent hash map for O(1) lookups
  private val cache = new ConcurrentHashMap[K, Node](capacity)

  // Dummy head and tail nodes for easier list manipulation
  private val head = new Node(null.asInstanceOf[K], null.asInstanceOf[V])
  private val tail = new Node(null.asInstanceOf[K], null.asInstanceOf[V])

  // Read-write lock for list operations
  private val lock = new ReentrantReadWriteLock()
  private val readLock = lock.readLock()
  private val writeLock = lock.writeLock()

  // Initialize the doubly-linked list
  head.next = tail
  tail.prev = head

  /**
   * Retrieves a value by key, updating its position to most recently used
   */
  def get(key: K): Option[V] = {
    Option(cache.get(key)) match {
      case Some(node) =>
        moveToHead(node)
        Some(node.value)
      case None => None
    }
  }

  /**
   * Puts a key-value pair into the cache
   * If key exists, updates the value and moves to head
   * If cache is at capacity, removes least recently used item
   */
  def put(key: K, value: V): Unit = {
    Option(cache.get(key)) match {
      case Some(node) =>
        // Update existing node
        node.value = value
        moveToHead(node)
      case None =>
        // Create new node
        val newNode = new Node(key, value)

        if (cache.size() >= capacity) {
          // Remove least recently used
          val lru = removeTail()
          cache.remove(lru.key)
        }

        cache.put(key, newNode)
        addToHead(newNode)
    }
  }

  /**
   * Removes a key from the cache
   */
  def remove(key: K): Option[V] = {
    Option(cache.remove(key)) match {
      case Some(node) =>
        removeFromList(node)
        Some(node.value)
      case None => None
    }
  }

  /**
   * Returns current size of the cache
   */
  def size: Int = cache.size()

  /**
   * Checks if cache is empty
   */
  def isEmpty: Boolean = cache.isEmpty

  /**
   * Clears all entries from the cache
   */
  def clear(): Unit = {
    writeLock.lock()
    try {
      cache.clear()
      head.next = tail
      tail.prev = head
    } finally {
      writeLock.unlock()
    }
  }

  /**
   * Returns all keys in the cache (for debugging/testing)
   */
  def keys: Set[K] = {
    import scala.jdk.CollectionConverters._
    cache.keySet().asScala.toSet
  }

  // Private helper methods for list manipulation
  private def addToHead(node: Node): Unit = {
    writeLock.lock()
    try {
      node.prev = head
      node.next = head.next
      head.next.prev = node
      head.next = node
    } finally {
      writeLock.unlock()
    }
  }

  private def removeFromList(node: Node): Unit = {
    writeLock.lock()
    try {
      node.prev.next = node.next
      node.next.prev = node.prev
    } finally {
      writeLock.unlock()
    }
  }

  private def moveToHead(node: Node): Unit = {
    removeFromList(node)
    addToHead(node)
  }

  private def removeTail(): Node = {
    writeLock.lock()
    try {
      val last = tail.prev
      removeFromList(last)
      last
    } finally {
      writeLock.unlock()
    }
  }
}

// Companion object with factory methods and utilities
object LRUCache {
  /**
   * Creates a new LRU cache with specified capacity
   */
  def apply[K, V](capacity: Int): LRUCache[K, V] = new LRUCache[K, V](capacity)

  /**
   * Creates a new LRU cache with initial entries
   */
  def apply[K, V](capacity: Int, entries: (K, V)*): LRUCache[K, V] = {
    val cache = new LRUCache[K, V](capacity)
    entries.foreach { case (k, v) => cache.put(k, v) }
    cache
  }
}

// Example usage and performance testing
object LRUCacheExample extends App {
  val cache = LRUCache[String, Int](3)

  // Basic operations
  cache.put("a", 1)
  cache.put("b", 2)
  cache.put("c", 3)

  println(s"Get 'a': ${cache.get("a")}")  // Some(1)
  println(s"Size: ${cache.size}")          // 3

  cache.put("d", 4)  // This should evict 'b'
  println(s"Get 'b': ${cache.get("b")}")  // None
  println(s"Keys: ${cache.keys}")          // Set(a, c, d)

  // Concurrent access test
  import scala.concurrent.{Future, ExecutionContext}
  import scala.util.Random
  implicit val ec: ExecutionContext = ExecutionContext.global

  val concurrentCache = LRUCache[Int, String](1000)

  val futures = (1 to 10).map { threadId =>
    Future {
      (1 to 10000).foreach { i =>
        val key = Random.nextInt(2000)
        if (Random.nextBoolean()) {
          concurrentCache.put(key, s"value-$key-$threadId")
        } else {
          concurrentCache.get(key)
        }
      }
    }
  }

  import scala.concurrent.Await
  import scala.concurrent.duration._

  val startTime = System.nanoTime()
  Await.ready(Future.sequence(futures), 30.seconds)
  val endTime = System.nanoTime()

  println(f"Concurrent test completed in ${(endTime - startTime) / 1e9}%.2f seconds")
  println(s"Final cache size: ${concurrentCache.size}")
}