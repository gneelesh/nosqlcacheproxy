package com.neelesh.service;


import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.neelesh.pojo.MongoJSON;


@Service
public class DatabaseService {
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	private static final Logger logger = Logger.getLogger(DatabaseService.class);
	
	public String getDataById( String collection , String keyId ) throws Exception {
			// MongoOperations moration = getInstance() ; 
			System.out.println( "Calling getDataByID Key : "+ keyId ) ;
			try { 
			MongoJSON mj = mongoTemplate.findById(keyId, MongoJSON.class, collection ) ;
			
			if ( mj != null ) 
				if ( mj.getJsondata().length() > 0 )
					return mj.getJsondata() ; 
			} catch ( Exception ex ) { 
				System.out.println( ex.getLocalizedMessage()) ; 
				ex.printStackTrace(); 
			}
		
			logger.info( "No Data Found for collection  " + collection + " for key : " + keyId  ) ;
			throw new Exception( "No Data Found ") ; 
	}
	
	
	public void addDataById( String collection , String keyId , String rid , String json ) {
		//MongoOperations moration = getInstance() ; 
		logger.info( "Adding to collection  " + collection + " for key : " + keyId + " for retailer " + rid ) ; 
		try { 
			MongoJSON mj = new MongoJSON() ; 
			mj.setId( keyId ); 
			mj.setDatecached(new Date() );
			mj.setRetid( rid ); 
			mj.setJsondata(json);
			mongoTemplate.insert(mj, collection); 
		} catch ( Exception ex) { 
			System.out.println( ex.getMessage() ) ; 
			ex.printStackTrace(); 
		}
		
	}
}
