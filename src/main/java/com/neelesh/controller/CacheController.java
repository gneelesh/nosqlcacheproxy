package com.neelesh.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.neelesh.service.CacheService ; 

@RestController
public class CacheController {
	
	@Autowired
	protected CacheService cacheService  ;  
	
	private static final Logger logger = Logger.getLogger(CacheService.class);
	
	@RequestMapping(value = "/collections", method = RequestMethod.GET)
	public ModelAndView printCollections() {
		List<String[]> clist = cacheService.getCollectionData() ;
		ModelAndView model = new ModelAndView("collection");
		model.addObject("clist" , clist) ; 
		return model ; 
	}


	@RequestMapping(value = "/dropcollection/{collectionname}", method = RequestMethod.GET)
	public String dropCollection(@PathVariable("collectionname") String collectionname) {
		return  cacheService.dropCollection(collectionname) ; 
	}
	
	
	
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public String printWelcome() {
		return ("Cache Conrolled is running fine @ " + new java.util.Date() );
	}
	
	@RequestMapping(value = "/checkjson", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String checkJSON(@RequestBody String addJson) {
		logger.info( "Got the to Main SERVICE request :  " + new java.util.Date() );
		JSONObject json;
		json = new JSONObject(addJson);
		json.put("date", new java.util.Date()) ;
		//simulating getting data ... 
		try {
			Thread.sleep(2000) ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println( "Replied Request :  " + new java.util.Date() );
		return ( json.toString() ); 
	}
	
	
	@RequestMapping(value = "/cached/{segment}", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String cacheRequest(@PathVariable("segment") String segment, @RequestBody String addJson) {
		logger.info( "Request received for segment " + segment ); 
		return cacheService.getData(segment , addJson) ; 
	}
	
	

}