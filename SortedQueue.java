package com.bufan.demo.service.netty;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @date 2022/3/2 下午7:47
 **/
public class SortedQueue {

    final Lock lock = new ReentrantLock();
    final Condition canTake = lock.newCondition();
    private final AtomicInteger count = new AtomicInteger();
    private LinkedList<Data> list = new LinkedList<>();

    public void put(Data node) {
        lock.lock();
        boolean p = false;
        for (int i = 0; i < list.size(); i++) {
            if (node.getIndex() < list.get(i).getIndex()) {
                list.add(i, node);
                p = true;
                break;
            }
        }
        if (p == false) {
            list.add(node);
        }
        count.incrementAndGet();
        canTake.signal();
        lock.unlock();
    }

    public Data offer(int index) {
        lock.lock();
        try {
            while (list.isEmpty() || list.get(0).getIndex() != index) {
                canTake.await();
            }
            count.getAndDecrement();
            return list.pop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public int size() {
        return count.get();
    }

    public void clear() {
        list.clear();
        count.set(0);
    }
}
