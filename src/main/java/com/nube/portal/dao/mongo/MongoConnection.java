package com.nube.portal.dao.mongo;

import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.nube.core.exception.NubeException;
import com.nube.portal.controller.consumer.PortalController;

@Repository
public class MongoConnection {
	
	
	@Value("${docdb.url:localhost}")
	private String url;
	
	@Value("${docdb.port:27017}")
	private int port;
	
	@Value("${docdb.username}")
	private String username;
	
	@Value("${docdb.pwd}")
	private String password;
	
	@Value("${docdb.db}")
	private String db;
	
	@Value("${docdb.auth-enabled:false}")
	private boolean isAuthRequired;
	
	@Value("${docdb.test-on-startup:false}")
	private boolean isTestOnStartUp;
	
	MongoClient mongoClient = null;
	
	DB mongoDb =null;
	
	GridFS gridFSForAppFiles = null;
	
	static Logger logger = Logger.getLogger(MongoConnection.class);
	
	
	@PostConstruct
	
	public void init()throws UnknownHostException, NubeException{
		
		mongoClient = new MongoClient(url , port );
		mongoDb = mongoClient.getDB(db);
		if(isAuthRequired){
			boolean auth = mongoDb.authenticate(username, password.toCharArray());
			if(auth == false){
				throw new NubeException("Authentication failed at database");
			}
		}
		if(isTestOnStartUp){
			this.insert("nube-connection-test", new BasicDBObject("date", new Date().toString()));
			logger.info("Document database test insert successful");
		}
		gridFSForAppFiles = new GridFS(mongoDb, "files");
		
		logger.info(String.format("Document database store initialized [%s:%s]", url, port));
		
		
	}


	public DB getDB(){
		return mongoDb;
	}
	
	
	public GridFS getGridFSApps(){
		return gridFSForAppFiles;
	}

	/**
	 * Get collectin from mongo db
	 * @param name
	 * @return
	 */
	public DBCollection getCollection(String name){
		return mongoDb.getCollection(name);
	}
	
	/**
	 * insert a document to collection
	 * @param collection
	 * @param document
	 */
	public void insert(String collection, BasicDBObject document){
		getCollection(collection).insert(document);
	}
	
	
	/**
	 * Find collection
	 * @param collection
	 * @param searchQuery
	 * @return
	 */
	public DBObject[] findBy(String collection, BasicDBObject searchQuery){
		
		DBCursor cursor = getCollection(collection).find(searchQuery);
		DBObject[] results = null;
		if(cursor.hasNext()){
			results = new DBObject[cursor.count()];
		}
		try {
		   int i =0;
		   while(cursor.hasNext()) {
			   results[i++] = cursor.next();
		   }
		} finally {
		   cursor.close();
		}
		return results;
	}

}
