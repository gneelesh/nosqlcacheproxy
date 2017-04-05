package com.neelesh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;


@Service
@PropertySource("classpath:applicationContext.properties")
public class CacheService {

	@Autowired
	private DatabaseService databaseService; 
	
	@Autowired
	private Environment env;
	
	private static final Logger logger = Logger.getLogger(CacheService.class);
	
	private String urlToCall ;
	private String fieldsToAccount ;
	private String collectionName ; 
	private String hashKey ; 
	private String addretaileridtocollection ; 
	
	private void setConfigDataForSegment( String segment ) throws Exception { 
		urlToCall = env.getProperty("" + segment + ".url") ; 
		fieldsToAccount = env.getProperty("" + segment + ".fields") ;
		addretaileridtocollection = env.getProperty("" + segment + ".addretaileridtocollection") ;
		collectionName = env.getProperty("" + segment + ".collection") ;
		if ( urlToCall == null || fieldsToAccount == null || collectionName == null )
			throw new Exception("Fields not proper") ; 
	}
	
	private void createHashKey( String message ) throws NoSuchAlgorithmException, InvalidKeyException { 
		hashKey = getHashKey( message ) ; 
	}
	
	private String getHashKey( String message ) throws NoSuchAlgorithmException, InvalidKeyException { 
		String secret = "secret";
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
		return hash ; 
	}
	
	private String getMongoData( String collectionname , String keyid  ) throws Exception {
		return databaseService.getDataById(collectionname , keyid ) ; 
	}
 
	private String getDataFromURL( String requestJson ) {
			RestTemplate restTemplate = new RestTemplate() ; 
			return  restTemplate.postForObject( urlToCall , requestJson , String.class);
	}
	
	private void saveDataToMongo( String responseJson ) { 
		JSONObject jsonObject = new JSONObject(responseJson) ;
		String retid = jsonObject.getString("retailerid") ;
		try { 
			databaseService.addDataById(collectionName, hashKey, retid, responseJson);
		} catch ( Exception ex ) { System.out.println( " not able to save : " + ex.getLocalizedMessage())  ; } 
	}
	
	public String getData( String segment , String requestJson  )   {
		// get property based on segment.
		logger.info("Got request for segment : " + segment ) ;
		String returnMessage = "" ; 
		try { 
			setConfigDataForSegment( segment ) ;
			JSONObject jsonObject = new JSONObject(requestJson) ;
			String retid = jsonObject.getString("retailerid") ;
			if ( addretaileridtocollection.equalsIgnoreCase("yes")) 
				this.collectionName = this.collectionName + retid ; 
			createHashKey(requestJson) ;
			logger.info("Created Hashkey : " + hashKey + "  Collection Name : " + collectionName + " Retailer ID : " + retid ) ; 
			return getMongoData( collectionName , hashKey ) ;
		} catch (Exception ex ) { 
			// data not found or some exception - execute main url .... 
			returnMessage = getDataFromURL( requestJson ) ;
			saveDataToMongo( returnMessage) ; 
		}
		return returnMessage ; 
	}

}
