package com.nube.portal.dao.apps.impl;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.nube.core.constants.AppConstants;
import com.nube.core.exception.NubeException;
import com.nube.core.util.apps.MimeTypeUtil;
import com.nube.portal.dao.apps.AppFileDao;
import com.nube.portal.dao.mongo.MongoConnection;

@Repository
public class AppFileDaoImpl implements AppFileDao {
	
	
	
	@Autowired
	MongoConnection mongoConnection;
	
	GridFS gridFsForApps;
	
	static Logger logger = Logger.getLogger(AppFileDaoImpl.class);
	
	@PostConstruct
	public void init(){
		gridFsForApps = mongoConnection.getGridFSApps();
	}
	
	
	public void saveAppDocument(String context, String version, String rootFolder,  File f)throws IOException{
		
		String fileName = f.getAbsolutePath();
		
		//Remove root folder path
		if(rootFolder !=null){
			fileName = f.getAbsolutePath().substring(rootFolder.length() + 1, f.getAbsolutePath().length());
		}

		String docName =  context + AppConstants.SEPERATOR + version + AppConstants.SEPERATOR + fileName;
		this.save(docName, f);
	}
	
	
	

	/**
	 * Store file in GridFS
	 * @param name
	 * @param f
	 */
	public void save(String docName, File f)throws IOException {
		
		
		GridFSInputFile gridFile = gridFsForApps.createFile(f);
		gridFile.setFilename(docName);
		gridFile.setContentType(MimeTypeUtil.getMimeType(f.toPath()));
		gridFile.save();
		logger.info("Doc Saved: "+ docName);
		
	}

	/**
	 * Read a document. Usually stored as context/version/....file path
	 * Keep in mind filUri starts with /  Example : /images/a.gif
	 */
	public GridFSDBFile read(String context, String version, String fileUri) throws NubeException{
		
		String documentName = context + AppConstants.SEPERATOR + version +  fileUri;
		return this.read(documentName);
		
	}
	
	/**
	 * Read a document from document store 
	 */
	public GridFSDBFile read(String documentName) throws NubeException{
		logger.info("Read " + documentName);
		GridFSDBFile result = gridFsForApps.findOne(documentName);
		
		if(result == null){
			throw new NubeException(404, "Not found "+ documentName);
		}
		return result ;
	}

	/**
	 * Provide file path including wild card
	 */
	public void delete(String filePath) {
		// TODO Auto-generated method stub
		logger.info("Delete " + filePath);
		DBObject query = QueryBuilder.start("filename").is(Pattern.compile(filePath, 
                Pattern.CASE_INSENSITIVE)).get();
		gridFsForApps.remove(query);
		
	}

}
