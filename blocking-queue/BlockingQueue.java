import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BlockingQueue - 基于 ReentrantLock 和 Condition 的阻塞队列实现
 * 
 * 特性：
 * - 线程安全：使用 ReentrantLock 保证原子性
 * - 阻塞操作：队列满时 put() 阻塞，队列空时 take() 阻塞
 * - 循环缓冲：使用数组实现环形缓冲区
 * 
 * @param <T> 队列元素类型
 */
public class BlockingQueue<T> {
    /** 存储元素的数组 */
    private final Object[] items;
    /** 当前元素数量 */
    private int count;
    /** 取出元素的位置索引 */
    private int takeIndex;
    /** 放入元素的位置索引 */
    private int putIndex;
    /** 可重入锁 */
    private final ReentrantLock lock = new ReentrantLock();
    /** 条件变量：队列非空 */
    private final Condition notEmpty = lock.newCondition();
    /** 条件变量：队列非满 */
    private final Condition notFull = lock.newCondition();

    /**
     * 构造指定容量的阻塞队列
     * @param capacity 队列容量，必须大于0
     * @throws IllegalArgumentException 如果容量小于等于0
     */
    public BlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.items = new Object[capacity];
    }

    /**
     * 将元素放入队列，如果队列满则阻塞
     * @param item 要放入的元素
     * @throws InterruptedException 如果线程被中断
     */
    public void put(T item) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            // 如果队列满，等待
            while (count == items.length) {
                notFull.await();
            }
            // 放入元素
            items[putIndex] = item;
            // 更新放入位置（环形）
            putIndex = (putIndex + 1) % items.length;
            count++;
            // 唤醒等待取出的线程
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从队列取出元素，如果队列空则阻塞
     * @return 取出的元素
     * @throws InterruptedException 如果线程被中断
     */
    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            // 如果队列空，等待
            while (count == 0) {
                notEmpty.await();
            }
            // 取出元素
            @SuppressWarnings("unchecked")
            T item = (T) items[takeIndex];
            // 帮助 GC
            items[takeIndex] = null;
            // 更新取出位置（环形）
            takeIndex = (takeIndex + 1) % items.length;
            count--;
            // 唤醒等待放入的线程
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取队列当前大小
     * @return 队列中的元素数量
     */
    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
