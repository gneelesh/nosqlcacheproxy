package com.neelesh.pojo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoJSON {
	
	@Id
	private String id ; 
	private Date datecached ;
	private String retailerid ;
	private String jsondata ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDatecached() {
		return datecached;
	}
	public void setDatecached(Date datecached) {
		this.datecached = datecached;
	}
	public String getRetid() {
		return retailerid;
	}
	public void setRetid(String retid) {
		this.retailerid = retid;
	}
	public String getJsondata() {
		return jsondata;
	}
	public void setJsondata(String jsondata) {
		this.jsondata = jsondata;
	} 
	
	

}
