package com.nube.portal.service.apps;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFS;
import com.nube.core.constants.AppConstants;
import com.nube.core.exception.NubeException;
import com.nube.core.util.zip.ZipUtil;
import com.nube.portal.dao.apps.AppBundleDao;
import com.nube.portal.dao.apps.AppFileDao;
import com.nube.portal.dao.apps.AppsDao;
import com.nube.portal.dao.mongo.MongoConnection;
import com.nube.portal.validator.apps.AppPackageValidator;

@Service
public class AppPackageProcessorService {
	
	@Autowired
	private ZipUtil zipUtil;
	
	@Autowired
	AppPackageValidator packageValidator;
	
	@Autowired
	MongoConnection mongoConnection;
	
	@Autowired
	AppsDao appsDao;
	
	@Autowired
	AppBundleDao appBundleDao;
	
	@Autowired
	AppFileDao appFileDao;
	
	
	@Value("${app.lic.key.site-context}")
	String siteContextKey;
	
	@Value("${app.lic.key.pkg-version}")
	String pkgVersionKey;

	@Value("${app.lic.key.site-url}")
	String siteUrlKey;
	
	
	
	GridFS gridFS;

	
	static Logger logger = Logger.getLogger(AppPackageProcessorService.class);

	@PostConstruct
	public void init(){
		gridFS = mongoConnection.getGridFSApps();
	}

	
	/**
	 * Unpack, Validate and Store app
	 * Also cleanup temp folder
	 * 
	 * TODO Archive zipped package for future reference, atleast 30 days
	 * @param file
	 * @param tempFoler
	 * @throws IOException
	 * @throws NubeException
	 */
	public void processAppPackage(String zippedPackage, String tempFoler)throws IOException, NubeException{
		
		//Unzip and delete original 
		String pkgFolder = zipUtil.unzip(zippedPackage, tempFoler, true);
		logger.info("Extracted to folder: "+ pkgFolder);
		
		//Validate the package.
		Properties licProperties = packageValidator.validate(pkgFolder);
		
		//Store in Database or File
		File uzippedFolder = new File(pkgFolder);
		
		//Store app in document store, also update meta data
		this.storeApp(uzippedFolder, licProperties);
		
		//Cleanup temp folder
		uzippedFolder.delete();
		
		
		
	}
	
	
	/**
	 * Store package in document store
	 * @param toInsert
	 * @param licProps
	 * @return
	 * @throws IOException
	 */
	private boolean storeApp(File toInsert, Properties licProps)throws IOException{
			
	
		String context = licProps.getProperty(siteContextKey);
		String version =licProps.getProperty(pkgVersionKey);
		
		
		//Delete files if exists, and save each files in the bundle
		appFileDao.delete(context +  AppConstants.SEPERATOR + version +  AppConstants.SEPERATOR  + "*");
		this.saveAllContents(context, version,  toInsert.getAbsolutePath(), toInsert);
		
		//Delete (context + version) and save bundle meta data.
		appBundleDao.delete(context, version);
		appBundleDao.save(licProps);
	
		
		//Update App to newer version
		this.insertOrupdateAppInfo(licProps);
		
		
		return true;
			
	}
	
	/**
	 * Insert of update app info
	 * @param licProps
	 */
	private void insertOrupdateAppInfo(Properties licProps ){
		
		appsDao.save(licProps.getProperty(siteContextKey), licProps.getProperty(siteUrlKey) , licProps.getProperty(pkgVersionKey), "admin");
		logger.info("App Version updated");
		
	}

		
	/**
	 * Put all contents in mongo db Grid FS
	 * @param contextAndVersion Example: kamoorr/12/
	 * @param rootFolder Root folder name 
	 * @param toInsert files to be inserted
	 * @throws IOException
	 */
	private void saveAllContents(String context, String version, String rootFolder, File fileToStore)throws IOException{
		File listFiles[] = fileToStore.listFiles();
		for(File f: listFiles){
			if(f.isDirectory()){
				saveAllContents(context, version, rootFolder, f);
			}else{
				//Ignore hidden files
				if(f.isHidden()){
					continue;
				}
				//URL to be used to find files (URL will be like <context>/<version>
				appFileDao.saveAppDocument(context, version, rootFolder, f);
				
			}
		}
	}
	
	

}
