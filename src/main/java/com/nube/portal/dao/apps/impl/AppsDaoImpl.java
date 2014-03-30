package com.nube.portal.dao.apps.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.nube.core.exception.NubeException;
import com.nube.portal.dao.apps.AppsDao;
import com.nube.portal.dao.apps.DocumentDbDao;
import com.nube.portal.dao.mongo.MongoConnection;

@Repository
public class AppsDaoImpl implements AppsDao, DocumentDbDao {

	@Autowired
	MongoConnection mongoConnection;

	final static String COLLECTION_APPS = "apps";
	
	
	public DBCollection getCollection() {
		return mongoConnection.getCollection(COLLECTION_APPS);
	}
	
	/**
	 * TODO
	 * This needs to be revisited.
	 * How to map multiple url to one context
	 * How to find current version if context is given (localhost)
	 * Save document
	 */
	public void save(String context, String urls, String version, String updatedBy) {
		//getCollection().save
		getCollection().update(new BasicDBObject().append("urls", urls).append("context", context),
		 new BasicDBObject()
		.append("context", context)
		.append("urls", urls)
		.append("currVersion", version)
		.append("updatedBy", updatedBy),
		true, false
		);

	}

	public BasicDBObject findByContext(String context) throws NubeException {
		
		BasicDBObject result = (BasicDBObject) getCollection() .findOne(new BasicDBObject().append("context", context));
		
		if (result == null) {
			throw new NubeException(500, "Unable to find app with context " + context);
		}
		return result;
	}
	
	/**
	 * Find app by url
	 * TODO Supports only one url at a time
	 * @param domain
	 * @return
	 * @throws NubeException
	 */
	public BasicDBObject findByDomain(String domain) throws NubeException {
		
		BasicDBObject result = (BasicDBObject) getCollection() .findOne(new BasicDBObject().append("urls", domain));
		
		if (result == null) {
			throw new NubeException(500, "Unable to find app with context " + domain);
		}
		return result;
	}

	

}
