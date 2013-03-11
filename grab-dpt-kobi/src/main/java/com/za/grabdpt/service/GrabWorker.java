package com.za.grabdpt.service;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.za.grabdpt.Main;
import com.za.grabdpt.crawl.DocumentFetcher;
import com.za.grabdpt.crawl.HttpDocumentFetcher;
import com.za.grabdpt.parse.DocumentParser;
import com.za.grabdpt.parse.ParseResultWriter;
import com.za.grabdpt.parse.VoterDocumentParser;
import com.za.grabdpt.parse.VoterInfo;
import com.za.grabdpt.parse.VoterInfoFileAccess;

public class GrabWorker implements Runnable {
	public static final Logger LOGGER = Logger.getLogger(GrabWorker.class.getSimpleName());
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
	}
	
	private final BlockingQueue<URL> urlQueue;
	private boolean finished = false;
	
	private DocumentFetcher fetcher;
	private DocumentParser<VoterInfo> parser;
	private ParseResultWriter<VoterInfo> voterDataWriter;
	
	public GrabWorker(BlockingQueue<URL> urlQueue) {
		if(urlQueue == null) {
			throw new IllegalArgumentException("Url-queue parameter should not be null.");
		}
			
		this.urlQueue = urlQueue;
		
		fetcher = new HttpDocumentFetcher();
		parser = new VoterDocumentParser();		
		voterDataWriter = new VoterInfoFileAccess();
	}
	
	public void finishWorker() {
		LOGGER.info("Finish worker (really waiting for url-queue to empty) which run on thread : " + Thread.currentThread().getName());
		finished = true;
	}

	public void run() {
		while(!(finished && urlQueue.isEmpty())) {
			try {
				URL nextUrl = urlQueue.poll(1, TimeUnit.MILLISECONDS);
				if(nextUrl != null) {
					String document = fetcher.fetch(nextUrl);
					if(parser.canParse(document)) {
						VoterInfo parseResult = parser.parse(document);
						voterDataWriter.write(parseResult);
					}
				}
			}
			catch (InterruptedException e) {
				// Abaikan saja.
			}
		}
		
		LOGGER.info("Worker finished.");
	}
}