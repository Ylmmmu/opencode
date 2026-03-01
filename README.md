# Java Concurrent Data Structures

A collection of Java implementations for common concurrent data structures.

## Projects

### Blocking Queue
A thread-safe blocking queue implementation using `ReentrantLock` and `Condition`.

**Location**: `blocking-queue/`

**Features**:
- Thread-safe put/take operations
- Blocking when queue is full or empty
- Circular buffer implementation

**Run**:
```bash
cd blocking-queue && javac *.java && java BlockingQueueTest
```

---

### Custom Timer
A scheduled timer implementation using `PriorityQueue` and thread pool.

**Location**: `java-timer/`

**Features**:
- Task scheduling with delay
- Priority queue for execution order
- Daemon thread for task execution

**Run**:
```bash
cd java-timer && javac *.java && java CustomTimer
```

---

### Simple Thread Pool
A custom thread pool implementation with fixed worker threads.

**Location**: `java-thread-pool/`

**Features**:
- Fixed number of worker threads
- Task queue with blocking operations
- Graceful shutdown support

**Run**:
```bash
cd java-thread-pool && javac *.java && java SimpleThreadPool
```

---

## Requirements

- Java 8 or higher
- No build tools required (raw `javac` and `java`)

## Building

```bash
# Compile all projects
cd blocking-queue && javac *.java
cd java-timer && javac *.java
cd java-thread-pool && javac *.java
```

## Testing

Each project contains a main method for testing. Run with `java <ClassName>`.
