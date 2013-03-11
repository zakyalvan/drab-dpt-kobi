package com.za.grabdpt.crawl;

import java.net.URL;

public interface DocumentFetcher {
	String fetch(URL documentUrl) throws DocumentFecthException;
}
