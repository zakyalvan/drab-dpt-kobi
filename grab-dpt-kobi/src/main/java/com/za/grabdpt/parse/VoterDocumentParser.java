package com.za.grabdpt.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.za.grabdpt.Main;


public class VoterDocumentParser implements DocumentParser<VoterInfo> {
	public static final Logger LOGGER = Logger.getLogger(VoterDocumentParser.class.getSimpleName());
	
	private static final Pattern VALIDATE_CONTENT_PATTERN;
	private static final Pattern CAPTURE_CONTENT_PATTERN;
	
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
		
		StringBuilder validatePatternBuilder = new StringBuilder();
		validatePatternBuilder.append(".+?");
		validatePatternBuilder.append("<tr style=\"border-bottom-style:dotted\">");
		validatePatternBuilder.append(".+?");
		validatePatternBuilder.append("</tr>");
		validatePatternBuilder.append(".+");
		VALIDATE_CONTENT_PATTERN = Pattern.compile(validatePatternBuilder.toString());
		
		StringBuilder patternBuilder = new StringBuilder();
		patternBuilder.append("<td bgcolor=\"#00CCCC\"><div align=\"center\">");
		patternBuilder.append("[^a-zA-Z0-9_,-]{0,1}(.*?)");
		patternBuilder.append("</div></td>");
		
		CAPTURE_CONTENT_PATTERN = Pattern.compile(patternBuilder.toString());
	}
	
	@Override
	public boolean canParse(String documentContent) {
		if(documentContent == null) {
			throw new NullPointerException("Konten dokumen harus diberikan.");
		}
		
		Matcher matcher = VALIDATE_CONTENT_PATTERN.matcher(documentContent);
		boolean valid = false;
		while(matcher.find()) {
			valid = true;
		}
		return valid;
	}

	@Override
	public VoterInfo parse(String documentContent) throws ParsingException {
		if(!canParse(documentContent)) {
			throw new ParsingException();
		}
		
		Matcher matcher = CAPTURE_CONTENT_PATTERN.matcher(documentContent);
		
		Map<String, String> fieldsMap = new HashMap<String, String>();
		int fieldPosition = 0;
		while(matcher.find()) {
			fieldsMap.put(VoterInfo.FIELDS[fieldPosition], matcher.group(1));
			fieldPosition++;
		}
		
		VoterInfo voterInfo = new VoterInfo(fieldsMap);
		return voterInfo;
	}
}
