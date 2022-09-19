package com.neelesh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
	
	private static String urlToCall ;
	private static String fieldsToAccount ;
	private static String collectionName ; 
	private static boolean addretaileridtocollection ;
	private static boolean passthrough ; 
	private static boolean addretailerdatetokey ; 

	public List<String[]> getCollectionData(){
		return databaseService.listCollectionCount() ; 
		
	}
	
	public String dropCollection( String collectionName ) { 
		return databaseService.dropCollection(collectionName) ; 
	}
	
	
	private void setConfigDataForSegment( String segment ) throws Exception { 
		if ( urlToCall == null || urlToCall.length() == 0 ) { 
			urlToCall = env.getProperty("" + segment + ".url") ; 
			fieldsToAccount = env.getProperty("" + segment + ".fields") ;
			addretaileridtocollection = env.getProperty("" + segment + ".addretaileridtocollection").equalsIgnoreCase("yes") ;
			addretailerdatetokey = env.getProperty("" + segment + ".addretailerdatetokey").equalsIgnoreCase("yes") ;
			collectionName = env.getProperty("" + segment + ".collection") ;
			passthrough = env.getProperty("" + segment + ".passthrough").equalsIgnoreCase("yes") ;  
			if ( urlToCall == null || fieldsToAccount == null || collectionName == null )
				throw new Exception("Fields not proper") ; 
		}
	}
	
/*	private String createHashKey( String message ) throws NoSuchAlgorithmException, InvalidKeyException { 
		return getHashKey( message ) ; 
	}*/
	
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
			logger.info( "Getting data from URL : " + urlToCall ); 
			RestTemplate restTemplate = new RestTemplate() ;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<String>(requestJson ,headers);			
			return  restTemplate.postForObject( urlToCall , entity , String.class );
	}
	
	
	private void saveDataToMongo( String responseJson , String local_collectioname , String hashKey , int retailerid) {
		logger.debug("Response JSON Length before minify : " + responseJson.length()); 
		JSONObject jsonObject = new JSONObject(responseJson) ;
		responseJson = jsonObject.toString() ; 
		logger.debug("Response JSON Length after minify : " + responseJson.length());
		try { 
			databaseService.addDataById(local_collectioname, hashKey, retailerid , responseJson);
		} catch ( Exception ex ) { System.out.println( " not able to save : " + ex.getLocalizedMessage())  ; } 
	}
	
	
/*	private void saveDataToMongo( String responseJson  ) { 
		try { 
			databaseService.addDataById(collectionName, hashKey, fieldvalue , responseJson);
		} catch ( Exception ex ) { System.out.println( " not able to save : " + ex.getLocalizedMessage())  ; } 
	}*/
	
	public String getData( String segment , String requestJson  )   {
		// get property based on segment.
		if ( passthrough ) {
			logger.info( "Passthrough request received for url : " + urlToCall );
			return getDataFromURL( requestJson ) ;
		}
		
		String local_collectioname = collectionName ;
		String hashKey = "" ; 
		int retailerid = 0  ; 
		logger.info("Got request for segment : " + segment + " Request JSON Length : " + requestJson.length() ) ;
		String returnMessage = "" ; 
		try { 
			// get configuration data from .properties file. 
			setConfigDataForSegment( segment ) ;
			// convert request into small minify json
			JSONObject jsonObject = new JSONObject(requestJson) ;
			retailerid = jsonObject.getInt(fieldsToAccount) ;
			local_collectioname = ( addretaileridtocollection ? collectionName + "_"+ retailerid : collectionName ) ;
			hashKey = getHashKey( jsonObject.toString() + ( addretailerdatetokey ? getDateStringMiliseconds(retailerid ) : "" )) ;
			logger.debug( "Request JSON length after minify : " + jsonObject.length() + " json -> " + jsonObject.toString() ) ; 
			logger.info("Created Hashkey : " + hashKey + "  Collection Name : " + collectionName + " Retailer ID : " + retailerid ) ; 
			return getMongoData( local_collectioname , hashKey ) ;
		} catch (Exception ex ) { 
			// data not found or some exception - execute main url .... 
			returnMessage = getDataFromURL( requestJson ) ;
			try { 
				saveDataToMongo( returnMessage , local_collectioname , hashKey , retailerid ) ;
			} catch ( Exception exl ) {  
					logger.error( "Error saving data returning the response as is " + exl.getLocalizedMessage(),exl) ; 
			}  
			
		}
		return returnMessage ; 
	}
	
	private String getDateStringMiliseconds( int retailerid ) { 
		return "" ; 
	}

}
