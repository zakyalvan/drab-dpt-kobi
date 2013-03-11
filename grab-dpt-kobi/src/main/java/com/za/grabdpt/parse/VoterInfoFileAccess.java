package com.za.grabdpt.parse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.za.grabdpt.Main;

/**
 * Default voter list data writer. Menyimpan seluruh data voter (yang berhasil di parse) dalam format csv.
 * 
 * @author zakyalvan
 */
public class VoterInfoFileAccess implements ParseResultWriter<VoterInfo>, ParseResultReader<VoterInfo> {
	public static final Logger LOGGER = Logger.getLogger(VoterInfoFileAccess.class.getSimpleName());
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static final String DEFAULT_FILE_PATH;
	
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
		
		String homeDirectoryPath = System.getProperty("user.home");
		String dataDirectoryPath = homeDirectoryPath + FILE_SEPARATOR + "DPTMentah";
		
		DEFAULT_FILE_PATH = dataDirectoryPath + FILE_SEPARATOR + new Date() + " -- " + System.nanoTime() + ".csv";	
	}
	
	private String filePath;
	
	private Set<VoterInfo> pendingWrite;
	
	public VoterInfoFileAccess() {
		this(DEFAULT_FILE_PATH);
	}
	public VoterInfoFileAccess(String filePath) {
		if(filePath == null || filePath.length() == 0) {
			throw new IllegalArgumentException("Parameter filepath harus diberikan.");
		}
		
		this.filePath = filePath;
		pendingWrite = new HashSet<VoterInfo>();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("Execute write pending list (Yang tertunda karena exception misalnya)");
				
				List<VoterInfo> pendingList = new ArrayList<VoterInfo>(pendingWrite);
				write(pendingList);
			}
		}));
	}
	
	@Override
	public void write(VoterInfo parseResult) {
		LOGGER.info("Write voter-info.");
		
		List<VoterInfo> voterInfos = new ArrayList<VoterInfo>(pendingWrite);
		pendingWrite.clear();
		voterInfos.add(parseResult);
		write(voterInfos);
	}
	
	@Override
	public void write(List<VoterInfo> parseResults) {
		LOGGER.info("Write parse result (voter-info)");
		
		if(pendingWrite.size() != 0) {
			parseResults.addAll(pendingWrite);
			pendingWrite.clear();
		}
		
		FileWriter fileWriter = null;
		try {
			File file = new File(filePath);
			if(!file.exists()) {
				file.createNewFile();
			}
			
			fileWriter = new FileWriter(file, true);
			
			StringBuilder writableBuilder = new StringBuilder();
			for(VoterInfo voterInfo : parseResults) {
				for(int i = 0; i < VoterInfo.FIELDS.length; i++) {
					writableBuilder.append(voterInfo.getProperties().get(VoterInfo.FIELDS[i]));
					
					if(i < VoterInfo.FIELDS.length-1) {
						writableBuilder.append("; ");
					}
				}
				writableBuilder.append(LINE_SEPARATOR);
			}
			
			fileWriter.append(writableBuilder);
			fileWriter.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
			pendingWrite.addAll(parseResults);
		}
		finally {
			if(fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<VoterInfo> read() {
		return null;
	}
}