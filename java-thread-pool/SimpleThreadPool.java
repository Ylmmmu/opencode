import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * SimpleThreadPool - A simple thread pool implementation
 *
 * Features:
 * - Fixed number of worker threads
 * - Task queue for pending tasks
 * - Graceful shutdown support
 *
 * @param poolSize the size of the thread pool
 * @throws IllegalArgumentException if poolSize <= 0
 */
public class SimpleThreadPool {
    private final int poolSize;
    private final List<WorkerThread> workers;
    private final BlockingQueue<Runnable> taskQueue;
    private volatile boolean shutdown;

    /**
     * Create a thread pool with specified size
     * @param poolSize the number of worker threads, must be positive
     */
    public SimpleThreadPool(int poolSize) {
        if (poolSize <= 0) throw new IllegalArgumentException("Pool size must be positive");
        this.poolSize = poolSize;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();
        this.shutdown = false;

        for (int i = 0; i < poolSize; i++) {
            WorkerThread worker = new WorkerThread();
            worker.start();
            workers.add(worker);
        }
    }

    /**
     * Submit a task to the thread pool
     * @param task the task to execute
     * @throws IllegalStateException if the pool is shut down
     * @throws NullPointerException if task is null
     */
    public void submit(Runnable task) {
        if (shutdown) {
            throw new IllegalStateException("Pool is shut down");
        }
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }
        taskQueue.offer(task);
    }

    /**
     * Shutdown the thread pool, no new tasks accepted but remaining tasks will be executed
     */
    public void shutdown() {
        shutdown = true;
        for (WorkerThread worker : workers) {
            worker.stopWorking();
        }
    }

    /**
     * Get the thread pool size
     * @return the number of threads in the pool
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * Get the number of pending tasks in the queue
     * @return the number of tasks in the queue
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    private class WorkerThread extends Thread {
        private volatile boolean running = true;

        public void run() {
            while (running && !shutdown) {
                Runnable task;
                try {
                    // Use poll with timeout to avoid losing tasks on interrupt
                    task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        task.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        public void stopWorking() {
            running = false;
            interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool pool = new SimpleThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            pool.submit(() -> {
                System.out.println("Task " + taskId + " started by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task " + taskId + " completed");
            });
        }

        Thread.sleep(3000);
        pool.shutdown();
        System.out.println("Pool shut down");
    }
}
