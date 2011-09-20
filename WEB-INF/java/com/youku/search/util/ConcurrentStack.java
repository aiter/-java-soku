package com.youku.search.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 此类实现nonblocking的线程安全Stack，引自IBM DeveloperWorks网站 <br>
 * 相比使用lock，这种方法使用CAS算法避免锁的争用 <br>
 * 但要注意在资源争用严重的情况下，会使CPU消耗增加（因为是不断while循环判断） <br>
 * 
 * @see http://www.ibm.com/developerworks/java/library/j-jtp04186/index.html
 * @param <E>
 * 
 * @author gaosong
 */
public class ConcurrentStack<E> {
    AtomicReference<Node<E>> head = new AtomicReference<Node<E>>();
    
    AtomicInteger size = new AtomicInteger(0);
    
    public void push(E item) {
        Node<E> newHead = new Node<E>(item);
        Node<E> oldHead;
        do {
            oldHead = head.get();
            newHead.next = oldHead;
        } while (!head.compareAndSet(oldHead, newHead));
        
        size.incrementAndGet();
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = head.get();
            if (oldHead == null) 
                return null;
            newHead = oldHead.next;
        } while (!head.compareAndSet(oldHead,newHead));
        
        size.decrementAndGet();
        
        return oldHead.item;
    }
    
    public int size(){
    	return size.get();
    }
    
    static class Node<E> {
        final E item;
        Node<E> next;

        public Node(E item) { this.item = item; }
    }
}
