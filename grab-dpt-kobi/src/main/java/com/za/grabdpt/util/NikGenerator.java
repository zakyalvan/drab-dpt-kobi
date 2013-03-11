package com.za.grabdpt.util;

import java.util.logging.Logger;

import com.za.grabdpt.Main;

/**
 * Utility untuk generate nik.
 * 
 * @author zakyalvan
 */
public class NikGenerator {
	private static final Logger LOGGER = Logger.getLogger(NikGenerator.class.getSimpleName());
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
	}
	
	private static NikGenerator instance = null;
	
	public static NikGenerator getInstance() {
		if(instance == null) {
			instance = new NikGenerator();
		}
		return instance;
	}

	private String lastGenerated;
	private int counter = 0;
	
	private NikGenerator() {
		
	}
	
	public synchronized String generate() throws GenerateNikFailedException {
		if(counter == 1)
			throw new GenerateNikFailedException();
		
		counter++;
		
		LOGGER.info("Start generate new nik");
		StringBuilder nikBuilder = new StringBuilder("5272020712850006");
		
		lastGenerated = nikBuilder.toString();
		
		LOGGER.info("Finish generate new nik (generated nik : " + nikBuilder.toString() + ").");
		return nikBuilder.toString();
	}
	
	public synchronized String getLastGenerated() {
		return lastGenerated;
	}
	
	public synchronized void persistLastState() {
		
	}
}