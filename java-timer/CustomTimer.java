import java.util.concurrent.*;
import java.util.*;

public class CustomTimer {
    private final PriorityQueue<TimerTask> taskQueue;
    private final ExecutorService executor;
    private volatile boolean running;

    public CustomTimer() {
        this.taskQueue = new PriorityQueue<>(
            Comparator.comparingLong(TimerTask::getExecuteTime)
        );
        this.executor = Executors.newFixedThreadPool(2);
        this.running = true;
        startDaemon();
    }

    public void schedule(Runnable command, long delay) {
        long executeTime = System.currentTimeMillis() + delay;
        TimerTask task = new TimerTask(command, executeTime);
        synchronized (taskQueue) {
            taskQueue.offer(task);
            taskQueue.notify();
        }
    }

    private void startDaemon() {
        executor.submit(() -> {
            while (running) {
                TimerTask task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() || taskQueue.peek().getExecuteTime() > System.currentTimeMillis()) {
                        try {
                            if (!taskQueue.isEmpty()) {
                                long waitTime = taskQueue.peek().getExecuteTime() - System.currentTimeMillis();
                                if (waitTime > 0) {
                                    taskQueue.wait(waitTime);
                                }
                            } else {
                                taskQueue.wait();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    task = taskQueue.poll();
                }
                if (task != null) {
                    executor.submit(task.getCommand());
                }
            }
        });
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }

    private static class TimerTask {
        private final Runnable command;
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

    public static void main(String[] args) throws InterruptedException {
        CustomTimer timer = new CustomTimer();

        timer.schedule(() -> System.out.println("Task 1 executed at " + System.currentTimeMillis()), 1000);
        timer.schedule(() -> System.out.println("Task 2 executed at " + System.currentTimeMillis()), 500);
        timer.schedule(() -> System.out.println("Task 3 executed at " + System.currentTimeMillis()), 2000);

        Thread.sleep(3000);
        timer.shutdown();
    }
}
