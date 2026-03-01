# AGENTS.md - 代码规范

## 概述

**语言要求：所有交互和文档必须使用中文（简体）**

本仓库包含 Java 并发数据结构的实现：
- `blocking-queue/`：使用 ReentrantLock 和 Condition 实现的阻塞队列
- `java-timer/`：使用 PriorityQueue 和线程池实现的定时器

## 编译、Lint 和测试命令

### 编译

```bash
# 编译目录下的所有 Java 文件
cd blocking-queue && javac *.java
cd java-timer && javac *.java
cd java-thread-pool && javac *.java
```

### 运行测试

```bash
# 运行单个测试类
cd blocking-queue && java BlockingQueueTest
cd java-timer && java CustomTimer
cd java-thread-pool && java SimpleThreadPool
```

### 运行单个类

```bash
# 运行主类
cd blocking-queue && java BlockingQueueTest
cd java-timer && java CustomTimer
```

---

## 代码规范

### 基本原则

- 编写简洁、可读的代码，使用有意义的命名
- 保持方法短小专注（单一职责）
- 使用注释解释"为什么"，而不是"做什么"
- 避免不必要的复杂性

### 命名规范

| 元素 | 规范 | 示例 |
|---------|------------|---------|
| 类名 | PascalCase | `BlockingQueue`, `CustomTimer` |
| 方法 | camelCase | `put()`, `take()`, `schedule()` |
| 变量 | camelCase | `queue`, `taskQueue`, `executeTime` |
| 常量 | UPPER_SNAKE_CASE | `MAX_CAPACITY` |
| 包名 | lowercase | `com.example` |

### 格式化

- **缩进**：4 个空格（不使用 Tab）
- **行长度**：最多 100 个字符
- **大括号**：左括号在同一行，右括号在新行
- **空行**：逻辑部分之间使用单行空行

```java
// 良好示例
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

### 导入

- 按包类型分组导入：
  1. Java 标准库 (`java.*`)
  2. 第三方库 (`com.*`, `org.*`)
- 使用显式导入（不使用通配符 `*`)
- 组内按字母顺序排序

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
```

### 类型和泛型

- 使用泛型实现类型安全的集合：`BlockingQueue<T>`
- 优先使用接口而非实现：`List<T>` 而非 `ArrayList<T>`
- 泛型转换时谨慎使用 `@SuppressWarnings`

### 错误处理

- 使用具体异常：无效参数使用 `IllegalArgumentException`
- 始终恢复中断状态：`Thread.currentThread().interrupt()`
- 使用 try-finally 清理资源（锁、流）

```java
// 良好示例 - 支持中断的锁处理
public void put(T item) throws InterruptedException {
    lock.lockInterruptibly();
    try {
        // 临界区
    } finally {
        lock.unlock();
    }
}

// 良好示例 - 正确的中断处理
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    return;
}
```

### 并发

- 跨线程访问的标志使用 `volatile`
- 始终在 finally 块中释放锁
- 可中断操作使用 `lock.lockInterruptibly()`
- 状态变更后发送条件信号

### Javadoc

- 为公共 API 编写 Javadoc 文档
- 包含 `@param`、`@return`、`@throws` 标签
- 保持描述简洁

```java
/**
 * 将元素放入队列，如果队列满则阻塞
 * @param item 要放入的元素
 * @throws InterruptedException 如果线程被中断
 */
public void put(T item) throws InterruptedException {
```

### 测试

- 测试类命名：`<类名>Test`
- 简单测试使用 main 方法
- 测试成功和失败两种情况
- 根据需要包含时间/延迟

---

## 文件结构

```
opencode/
├── blocking-queue/
│   ├── BlockingQueue.java      # 主要实现
│   └── BlockingQueueTest.java  # 测试类
├── java-timer/
│   └── CustomTimer.java        # 定时器实现（含 main()）
└── java-thread-pool/
    └── SimpleThreadPool.java   # 线程池实现（含 main()）
```

---

## 常用模式

### Lock 和 Condition 模式

```java
private final ReentrantLock lock = new ReentrantLock();
private final Condition notEmpty = lock.newCondition();
private final Condition notFull = lock.newCondition();
```

### 带比较器的优先队列

```java
this.taskQueue = new PriorityQueue<>(
    Comparator.comparingLong(TimerTask::getExecuteTime)
);
```

### 线程池初始化

```java
this.executor = Executors.newFixedThreadPool(2);
```

---

## 注意事项

- 不使用构建工具（Maven/Gradle）- 直接使用 `javac` 和 `java`
- 代码使用中文注释（符合现有风格）
- 所有代码为单线程开发，手动测试
