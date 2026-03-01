import java.util.concurrent.*;
import java.util.*;

/**
 * CustomTimer - 基于优先级队列和线程池的定时器实现
 * 
 * 工作原理：
 * 1. 使用优先级队列按执行时间排序任务
 * 2. 使用线程池执行到期任务
 * 3. 使用 wait/notify 实现阻塞等待
 */
public class CustomTimer {
    /** 任务队列，按执行时间排序 */
    private final PriorityQueue<TimerTask> taskQueue;
    /** 线程池，用于执行定时任务 */
    private final ExecutorService executor;
    /** 运行状态标志 */
    private volatile boolean running;

    /**
     * 构造一个自定义定时器
     */
    public CustomTimer() {
        // 初始化优先级队列，按执行时间排序
        this.taskQueue = new PriorityQueue<>(
            Comparator.comparingLong(TimerTask::getExecuteTime)
        );
        // 创建固定大小为2的线程池
        this.executor = Executors.newFixedThreadPool(2);
        this.running = true;
        // 启动守护线程
        startDaemon();
    }

    /**
     * 调度一个任务，在指定延迟后执行
     * @param command 要执行的任务
     * @param delay 延迟时间（毫秒）
     */
    public void schedule(Runnable command, long delay) {
        // 计算任务的执行时间
        long executeTime = System.currentTimeMillis() + delay;
        // 创建任务对象
        TimerTask task = new TimerTask(command, executeTime);
        // 同步访问任务队列
        synchronized (taskQueue) {
            // 添加任务到队列
            taskQueue.offer(task);
            // 唤醒等待线程检查新任务
            taskQueue.notify();
        }
    }

    /**
     * 启动守护线程，负责监控任务队列并执行到期任务
     */
    private void startDaemon() {
        executor.submit(() -> {
            while (running) {
                TimerTask task;
                synchronized (taskQueue) {
                    // 循环检查直到有任务到期
                    while (taskQueue.isEmpty() || taskQueue.peek().getExecuteTime() > System.currentTimeMillis()) {
                        try {
                            if (!taskQueue.isEmpty()) {
                                // 计算等待时间
                                long waitTime = taskQueue.peek().getExecuteTime() - System.currentTimeMillis();
                                if (waitTime > 0) {
                                    // 等待直到任务到期
                                    taskQueue.wait(waitTime);
                                }
                            } else {
                                // 队列为空，无限等待
                                taskQueue.wait();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // 取出到期的任务
                    task = taskQueue.poll();
                }
                // 执行任务
                if (task != null) {
                    executor.submit(task.getCommand());
                }
            }
        });
    }

    /**
     * 关闭定时器，不再接受新任务
     */
    public void shutdown() {
        running = false;
        executor.shutdown();
    }

    /**
     * 定时任务内部类
     */
    private static class TimerTask {
        /** 要执行的命令 */
        private final Runnable command;
        /** 任务执行时间戳 */
        private final long executeTime;

        public TimerTask(Runnable command, long executeTime) {
            this.command = command;
            this.executeTime = executeTime;
        }

        public Runnable getCommand() {
            return command;
        }

        public long getExecuteTime() {
            return executeTime;
        }
    }

    /**
     * 测试主方法
     */
    public static void main(String[] args) throws InterruptedException {
        CustomTimer timer = new CustomTimer();

        // 调度三个任务，延迟分别为 1000ms, 500ms, 2000ms
        timer.schedule(() -> System.out.println("Task 1 executed at " + System.currentTimeMillis()), 1000);
        timer.schedule(() -> System.out.println("Task 2 executed at " + System.currentTimeMillis()), 500);
        timer.schedule(() -> System.out.println("Task 3 executed at " + System.currentTimeMillis()), 2000);

        // 等待所有任务执行完成
        Thread.sleep(3000);
        timer.shutdown();
    }
}
