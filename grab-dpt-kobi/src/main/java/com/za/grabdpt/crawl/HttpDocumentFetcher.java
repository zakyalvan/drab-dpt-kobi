package com.za.grabdpt.crawl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.za.grabdpt.Main;

/**
 * Fetch document melalui http (Menggunakan library http component).
 * 
 * @author zakyalvan
 */
public class HttpDocumentFetcher implements DocumentFetcher {
	public static final Logger LOGGER = Logger.getLogger(HttpDocumentFetcher.class.getSimpleName());
	
	private static final HttpClient DEFAULT_HTTP_CLIENT;
	
	static {
		LOGGER.setParent(Main.LOGGER);
		LOGGER.setUseParentHandlers(true);
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		
		ClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry);
		DEFAULT_HTTP_CLIENT = new DefaultHttpClient(connectionManager);
	}
	
	private HttpClient httpClient;
	
	public HttpDocumentFetcher() {
		httpClient = DEFAULT_HTTP_CLIENT;		
	}
	
	public String fetch(URL documentUrl) throws DocumentFecthException {
		LOGGER.info("Start fetch document from : " + documentUrl.toString());
		
		HttpGet request = new HttpGet(documentUrl.toString());
		BufferedReader contentReader = null;
		StringBuilder documentBuilder = new StringBuilder();
		
		try {
			HttpResponse response = httpClient.execute(request);		
			HttpEntity responseEntity = response.getEntity();
			
			if(responseEntity != null) {
				contentReader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));				
				
				String content = null;
				while((content = contentReader.readLine()) != null) {
					documentBuilder.append(content);
				}
			}
			
			EntityUtils.consume(responseEntity);
		}
		catch(ClientProtocolException e) {
			throw new DocumentFecthException();
		}
		catch(IOException e) {
			throw new DocumentFecthException();
		}
		finally {
			if(contentReader != null) {
				try {
					contentReader.close();
				}
				catch (IOException e) {
					// Abaikan saja.
				}
			}
		}
		return documentBuilder.toString();
	}
}