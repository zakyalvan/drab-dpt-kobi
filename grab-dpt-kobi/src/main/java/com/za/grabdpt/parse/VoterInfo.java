package com.za.grabdpt.parse;

import java.util.Collections;
import java.util.Map;

import com.za.grabdpt.parse.ParseResult;

public class VoterInfo implements ParseResult {
	public static final String[] FIELDS = new String[] {"NIK", "NAMA", "TTL", "USIA", "STATUS", "KELAMIN", "ALAMAT", "RT", "RW", "DUSUN", "KETERANGAN"};
	
	private final Map<String, String> properties;
	
	public VoterInfo(Map<String, String> properties) {
		if(properties == null)
			throw new IllegalArgumentException("Parameter properties harus diberikan.");
		
		this.properties = Collections.unmodifiableMap(properties);
	}

	public Map<String, String> getProperties() {
		return properties;
	}
	
	public String toString() {
		return properties.toString();
	}
}
