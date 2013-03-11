package com.za.grabdpt.crawl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import com.za.grabdpt.Main;
import com.za.grabdpt.util.GenerateNikFailedException;
import com.za.grabdpt.util.NikGenerator;

public class NikBaseUrlQueueFeeder extends UrlQueueFeeder {
	public static final Logger LOGGER = Logger.getLogger(NikBaseUrlQueueFeeder.class.getSimpleName());
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
	}
	
	private NikGenerator nikGenerator;
	private String urlPattern;
	
	private String nikPlaceholder;
	
	public NikBaseUrlQueueFeeder(BlockingQueue<URL> urlQueue) {
		super(urlQueue);

		nikGenerator = NikGenerator.getInstance();
		urlPattern = "http://www.kpu-bimakota.go.id/dpt_online/index.php?NIK=[%NIK%]&cari";
		nikPlaceholder = "[%NIK%]";
	}

	@Override
	protected URL createNext() throws NoMoreUrlException {
		LOGGER.info("Create next url.");
		
		URL url = null;		
		try {
			String nik = nikGenerator.generate();
			String urlString = urlPattern.replace(nikPlaceholder, nik);
			url = new URL(urlString);
		}
		catch (GenerateNikFailedException e) {
			throw new NoMoreUrlException();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return url;
	}
}
