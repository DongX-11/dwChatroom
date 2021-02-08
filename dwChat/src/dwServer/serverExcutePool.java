package dwServer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class serverExcutePool {
	
	private ExecutorService executor;
	
	public serverExcutePool(int maxPoolSize, int queueSize) {
		/*
		 * Runtime.getRuntime().availableProcessors():JVM运行所能创建的最大线程数
		 * 空闲线程存活时间为120s
		 */
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,120L,TimeUnit.SECONDS,new ArrayBlockingQueue<java.lang.Runnable>(queueSize));
	}

	public void DoTask(java.lang.Runnable task) {
		executor.execute(task);
	}
}
