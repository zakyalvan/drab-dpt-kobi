package com.za.grabdpt.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.za.grabdpt.Main;
import com.za.grabdpt.crawl.NikBaseUrlQueueFeeder;
import com.za.grabdpt.crawl.UrlQueueFeeder;
import com.za.grabdpt.crawl.UrlQueueFeeder.FinishHandler;

public class DefaultGrabService implements GrabService {
	public static final Logger LOGGER = Logger.getLogger(DefaultGrabService.class.getSimpleName());
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
	}

	private static final int DEFAULT_EXECUTOR_POOL_SIZE = 10;
	
	private ThreadPoolExecutor threadPoolExecutor;
	private int executorPoolSize;
	
	private List<GrabWorker> workerList = new ArrayList<GrabWorker>();
	
	private UrlQueueFeeder urlQueueFeeder;
	private BlockingQueue<URL> urlQueue;
	
	public DefaultGrabService() {
		this(DEFAULT_EXECUTOR_POOL_SIZE);
	}
	public DefaultGrabService(int executorPoolSize) {
		LOGGER.info("First create thread-pool-executor (With " +  executorPoolSize + " core pool size");
		
		this.executorPoolSize = executorPoolSize;
		threadPoolExecutor = new ThreadPoolExecutor(this.executorPoolSize, this.executorPoolSize, 1, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());	
		
		LOGGER.info("Create url queue.");
		urlQueue = new ArrayBlockingQueue<URL>(200);
		
		LOGGER.info("Create url queue feeder");
		urlQueueFeeder = new NikBaseUrlQueueFeeder(urlQueue);
		urlQueueFeeder.setFinishHandler(new FinishHandler() {
			@Override
			public void handle() {
				finishWorkers();
			}
		});
		
		for(int i = 0; i < executorPoolSize; i++) {
			GrabWorker worker = new GrabWorker(urlQueue);
			workerList.add(worker);
			threadPoolExecutor.execute(worker);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				urlQueueFeeder.stopFeed();
			}
		});
	}
	
	private void finishWorkers() {
		LOGGER.info("Finish all workers (threads).");
		for(GrabWorker worker : workerList) {
			worker.finishWorker();
		}
		
		workerList.clear();
		
		threadPoolExecutor.shutdown();
		while(!threadPoolExecutor.isTerminated());
	}
	
	public void startGrab() {
		LOGGER.info("Start grab document.");
		urlQueueFeeder.startFeed();
	}
}