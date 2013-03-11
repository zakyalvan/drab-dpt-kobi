package com.za.grabdpt;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.za.grabdpt.service.DefaultGrabService;
import com.za.grabdpt.service.GrabService;

/**
 * Main kelas aplikasi ini.
 * 
 * @author zakyalvan
 */
public class Main {
	public static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());
	
	public static void main(String[] args) throws Exception {
		LOGGER.setLevel(Level.INFO);
		
//		LogFormatter logFormatter = new LogFormatter();
//		ConsoleHandler consoleHandler = new ConsoleHandler();
//		consoleHandler.setFormatter(logFormatter);
//		
//		LOGGER.addHandler(consoleHandler);
		
		LOGGER.info("java.home : " + System.getProperty("java.home"));
		
		if(args.length < 1) {
			throw new IllegalArgumentException("Lokasi file konfigurasi belum diberikan.");
		}
		
		LOGGER.info("Parsing file konfigurasi (" + args[0] + ")");
		
		String configLocation = args[0];
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(configLocation)));
		
		LOGGER.info("Start grab service.");
		GrabService service = new DefaultGrabService();
		service.startGrab();
	}
}