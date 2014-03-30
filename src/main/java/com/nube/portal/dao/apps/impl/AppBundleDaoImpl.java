package com.nube.portal.dao.apps.impl;

import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.nube.core.exception.NubeException;
import com.nube.portal.dao.apps.AppBundleDao;
import com.nube.portal.dao.apps.DocumentDbDao;
import com.nube.portal.dao.mongo.MongoConnection;

/**
 * Store app bundle meta data
 * @author kamoorr
 *
 */
@Repository
public class AppBundleDaoImpl implements AppBundleDao, DocumentDbDao{
	
	@Autowired
	MongoConnection mongoConnection;
	
	
	@Value("${app.lic.key.site-context}")
	String siteContextKey;
	
	@Value("${app.lic.key.pkg-version}")
	String pkgVersionKey;
	
	@Value("${app.lic.key.site-home}")
	String homePage;
	
	@Value("${app.default-site-home}")
	String defaultHomePage;
	
	static final String COLLECTION_BUNDLE="bundles";
	
	static Logger logger = Logger.getLogger(AppBundleDaoImpl.class);
	
	public DBCollection getCollection() {
		return mongoConnection.getCollection(COLLECTION_BUNDLE);
	}

	/**
	 * Save bundle meta data
	 */
	public void save(Properties licProps){
		getCollection().save(new BasicDBObject()
		.append("context", licProps.get(siteContextKey))
		.append("version", licProps.get(pkgVersionKey))
		.append("homePage", licProps.get(homePage) ==null?defaultHomePage:licProps.get(homePage))
		.append("updatedBy", "admin")
		);
	}
	
	/**
	 * Find entry by context and version
	 */
	public BasicDBObject findByContextAndVersion(String context, String version) throws NubeException {
		
		BasicDBObject result = (BasicDBObject) getCollection().findOne(
					new BasicDBObject().append("context", context).append("version", version));
		
		if (result == null) {
			throw new NubeException(500, "Unable to find app bundle for " + context +"/" + version);
		}
		return result;

	}
	
	/**
	 * Delete context/version if exists
	 */
	public void delete(String context, String version){
		logger.info(String.format("Delete Bundle context: %s, version: %s", context, version));
		getCollection().remove(new BasicDBObject().append("context", context).append("version", version));
	}

	
}
