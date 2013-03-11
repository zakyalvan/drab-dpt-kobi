package com.za.grabdpt.crawl;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import com.za.grabdpt.Main;

public abstract class UrlQueueFeeder implements Runnable {
	public static final Logger LOGGER = Logger.getLogger(UrlQueueFeeder.class.getSimpleName());
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
	}
	
	private final BlockingQueue<URL> urlQueue;
	
	private boolean started = false;
	private boolean finished = false;
	private boolean active = false;
	
	private FinishHandler finishHandler;
	
	public UrlQueueFeeder(BlockingQueue<URL> urlQueue) {
		if(urlQueue == null)
			throw new IllegalArgumentException("Parameter url queue harus diberikan.");
		
		this.urlQueue = urlQueue;
	}
	
	public void setFinishHandler(FinishHandler finishHandler) {
		if(finishHandler == null)
			throw new IllegalArgumentException("Argumen finish handler harus diberikan");
		
		this.finishHandler = finishHandler;
	}

	public final void run() {
		while(!finished) {
			active = true;
			try {
				URL nextUrl = createNext();
				LOGGER.info("Put new url " + nextUrl.toString() + " to queue.");
				urlQueue.put(nextUrl);
			}
			catch(NoMoreUrlException e) {
				finished = true;
				finishHandler.handle();
			}
			catch(InterruptedException e) {
				// Abaikan saja.
			}
			active = false;
		}
	}
	
	public final synchronized void startFeed() {
		if(started)
			throw new RuntimeException("Cant start already started feeder.");
		
		Thread feedThread = new Thread(this);
		feedThread.start();
		started = true;
	}
	public final synchronized void stopFeed() {
		if(!started)
			throw new RuntimeException("Can't stop not started feeder.");
		
		finished = true;
		finishHandler.handle();
		
		while(active);
		
		/**
		 * TODO Persist current url-queue content (Yang belum di eksekusi).
		 */
		
		started = false;
	}
	
	protected abstract URL createNext() throws NoMoreUrlException;
	
	public interface FinishHandler {
		void handle();
	}
}