# Java 并发数据结构

Java 常用并发数据结构的实现集合。

## 项目

### 阻塞队列
使用 `ReentrantLock` 和 `Condition` 实现的线程安全阻塞队列。

**位置**：`blocking-queue/`

**特性**：
- 线程安全的 put/take 操作
- 队列满或为空时阻塞
- 循环缓冲区实现

**运行**：
```bash
cd blocking-queue && javac *.java && java BlockingQueueTest
```

---

### 自定义定时器
使用 `PriorityQueue` 和线程池实现的定时任务调度器。

**位置**：`java-timer/`

**特性**：
- 延迟任务调度
- 优先队列保证执行顺序
- 守护线程执行任务

**运行**：
```bash
cd java-timer && javac *.java && java CustomTimer
```

---

### 简单线程池
固定工作线程数的自定义线程池实现。

**位置**：`java-thread-pool/`

**特性**：
- 固定数量的工作线程
- 带阻塞操作的任务队列
- 支持优雅关闭

**运行**：
```bash
cd java-thread-pool && javac *.java && java SimpleThreadPool
```

---

## 需求

- Java 8 或更高版本
- 无需构建工具（直接使用 `javac` 和 `java`）

## 编译

```bash
# 编译所有项目
cd blocking-queue && javac *.java
cd java-timer && javac *.java
cd java-thread-pool && javac *.java
```

## 测试

每个项目都包含用于测试的 main 方法。运行方式为 `java <类名>`。
