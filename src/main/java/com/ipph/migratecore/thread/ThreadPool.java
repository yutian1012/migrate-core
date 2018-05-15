package com.ipph.migratecore.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class ThreadPool {
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamedThreadFactory("migration"));
	/**
	 * 向线程池中放入执行线程
	 * @param task
	 */
	public void addTask(Runnable task){
		threadPool.execute(task);
	}
}

class NamedThreadFactory implements ThreadFactory {
	
	static final AtomicInteger poolNumber = new AtomicInteger(1);
	
	final AtomicInteger threadNumber = new AtomicInteger(1);
	final ThreadGroup group;
	final String namePrefix;
	final boolean isDaemon;
	
	public NamedThreadFactory() {
		this("pool");
	}
	
	public NamedThreadFactory(String name) {
		this(name, false);
	}
	
	public NamedThreadFactory(String preffix, boolean daemon) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = preffix + "-" + poolNumber.getAndIncrement() + "-thread-";
		isDaemon = daemon;
	}
	
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		t.setDaemon(isDaemon);
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
	
}


