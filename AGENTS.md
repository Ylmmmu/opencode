# AGENTS.md - Coding Guidelines for This Repository

## Overview
使用中文

This repository contains Java implementations of concurrent data structures:
- `blocking-queue/`: A blocking queue implementation using ReentrantLock and Condition
- `java-timer/`: A custom timer implementation using PriorityQueue and thread pool

## Build, Lint, and Test Commands

### Compilation

```bash
# Compile all Java files in a directory
cd blocking-queue && javac *.java
cd java-timer && javac *.java
```

### Running Tests

```bash
# Run a single test class
cd blocking-queue && java BlockingQueueTest
cd java-timer && java CustomTimer
```

### Running Individual Classes

```bash
# Run main class
cd blocking-queue && java BlockingQueueTest
cd java-timer && java CustomTimer
```

---

## Code Style Guidelines

### General Principles

- Write clean, readable code with meaningful names
- Keep methods short and focused (single responsibility)
- Use comments to explain "why", not "what"
- Avoid unnecessary complexity

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `BlockingQueue`, `CustomTimer` |
| Methods | camelCase | `put()`, `take()`, `schedule()` |
| Variables | camelCase | `queue`, `taskQueue`, `executeTime` |
| Constants | UPPER_SNAKE_CASE | `MAX_CAPACITY` |
| Packages | lowercase | `com.example` |

### Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 100 characters
- **Braces**: Opening brace on same line, closing brace on new line
- **Blank lines**: Single blank line between logical sections

```java
// Good
public void put(T item) throws InterruptedException {
    lock.lockInterruptibly();
    try {
        while (count == items.length) {
            notFull.await();
        }
        // ...
    } finally {
        lock.unlock();
    }
}
```

### Imports

- Group imports by package type:
  1. Java standard library (`java.*`)
  2. Third-party libraries (`com.*`, `org.*`)
- Use explicit imports (no wildcard `*`)
- Sort alphabetically within groups

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
```

### Types and Generics

- Use generics for type-safe collections: `BlockingQueue<T>`
- Prefer interfaces over implementations: `List<T>` instead of `ArrayList<T>`
- Use `@SuppressWarnings` sparingly when casting generics

### Error Handling

- Use specific exceptions: `IllegalArgumentException` for invalid parameters
- Always restore interrupt status: `Thread.currentThread().interrupt()`
- Use try-finally for resource cleanup (locks, streams)

```java
// Good - proper lock handling with interrupt support
public void put(T item) throws InterruptedException {
    lock.lockInterruptibly();
    try {
        // critical section
    } finally {
        lock.unlock();
    }
}

// Good - proper interrupt handling
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    return;
}
```

### Concurrency

- Use `volatile` for flags accessed across threads
- Always release locks in finally blocks
- Use `lock.lockInterruptibly()` for interruptible operations
- Signal conditions after state changes

### Javadoc

- Document public APIs with Javadoc
- Include `@param`, `@return`, `@throws` tags
- Keep descriptions concise

```java
/**
 * 将元素放入队列，如果队列满则阻塞
 * @param item 要放入的元素
 * @throws InterruptedException 如果线程被中断
 */
public void put(T item) throws InterruptedException {
```

### Testing

- Name test classes: `<ClassName>Test`
- Use main method for simple tests
- Test both success and failure cases
- Include timing/delays where needed

---

## File Structure

```
opencode/
├── blocking-queue/
│   ├── BlockingQueue.java      # Main implementation
│   └── BlockingQueueTest.java  # Test class
└── java-timer/
    └── CustomTimer.java        # Timer implementation with main()
```

---

## Common Patterns

### Lock and Condition Pattern

```java
private final ReentrantLock lock = new ReentrantLock();
private final Condition notEmpty = lock.newCondition();
private final Condition notFull = lock.newCondition();
```

### Priority Queue with Comparator

```java
this.taskQueue = new PriorityQueue<>(
    Comparator.comparingLong(TimerTask::getExecuteTime)
);
```

### Thread Pool Initialization

```java
this.executor = Executors.newFixedThreadPool(2);
```

---

## Notes

- No build tools (Maven/Gradle) - use raw `javac` and `java`
- Code uses Chinese comments (matches existing style)
- All code is single-threaded development, tested manually
