package com.neelesh.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mongodb.DBCollection;
import com.neelesh.pojo.MongoJSON;


@Service
public class DatabaseService {
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	private static final Logger logger = Logger.getLogger(DatabaseService.class);
	
	public String getDataById( String collection , String keyId ) throws Exception {
			// MongoOperations moration = getInstance() ; 
			logger.info( "Calling getDataByID Key : "+ keyId + " collection : " + collection ) ; 
			try { 
			MongoJSON mj = mongoTemplate.findById(keyId, MongoJSON.class, collection ) ;
			
			if ( mj != null ) 
				if ( mj.getJsondata().length() > 0 )
					return mj.getJsondata() ; 
			} catch ( Exception ex ) { 
				logger.error( ex.getLocalizedMessage()) ; 
				ex.printStackTrace(); 
			}
		
			logger.info( "No Data Found for collection  " + collection + " for key : " + keyId  ) ;
			throw new Exception( "No Data Found ") ; 
	}
	
	
	public void addDataById( String collection , String keyId , int rid , String json ) {
		//MongoOperations moration = getInstance() ; 
		logger.info( "Adding to collection  " + collection + " for key : " + keyId + " for retailer " + rid ) ; 
		try { 
			MongoJSON mj = new MongoJSON() ; 
			mj.setId( keyId ); 
			mj.setDatecached(new Date() );
			mj.setRetailerid( rid ); 
			mj.setJsondata(json);
			mongoTemplate.insert(mj, collection); 
		} catch ( Exception ex) { 
			logger.error( ex.getMessage() ) ; 
			ex.printStackTrace(); 
		}
		
	}
	
	public List<String[]> listCollectionCount() { 
		Set<String> set = mongoTemplate.getCollectionNames();
		List<String[]> retList = new ArrayList<String[]>() ; 
		logger.info( "Getting collection Names  ! " + set.size() ); 
		StringBuilder sb = new StringBuilder() ; 
		int i = 1 ; 
	    if (!CollectionUtils.isEmpty(set)) {
	        for (String collectionName : set) {
	            try {
	            	DBCollection collection = mongoTemplate.getCollection(collectionName);
	            	logger.info( i + "	:	" + collectionName + "	:	" + collection.count() ); 
	            	Query q = new Query().with(new Sort(Sort.Direction.DESC, "datecached"));
	            	MongoJSON element = mongoTemplate.findOne(q, MongoJSON.class,collectionName);	            	
	            	String[] st = new String[4] ; 
	            	st[0] = "" + i++ ; st[1] = collectionName ; st[2] = "" + collection.count() ; st[3] = element.getDatecached().toString() ; 
	            	retList.add(st) ; 
	            }
	            catch (Exception e) {
	                logger.error(e.getLocalizedMessage() , e ); 
	            }
	        }
	    }
	    return retList ; 
		
	}
	public String dropCollection(String collectionName ) {
		String returntext = "Collection : " + collectionName + " Successfully dropped.  !!!!!!!!!!!!!!!" ;
		logger.info("Request for dropping the collection received for collection --------> " + collectionName );
		try { 
			mongoTemplate.dropCollection(collectionName);
			logger.info("collection --------> " + collectionName + "    IS D R O P P E D !!! ");
		} catch ( Exception ex ) { 
			returntext = "Error : " + ex.getLocalizedMessage() ; 
			
		}
		return returntext ; 
	}
	
}
