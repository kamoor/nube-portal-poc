package com.nube.portal.service.apps;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.nube.core.constants.AppConstants;
import com.nube.core.exception.NubeException;
import com.nube.core.util.apps.InetUtil;
import com.nube.core.util.string.StringUtil;
import com.nube.portal.dao.apps.AppBundleDao;
import com.nube.portal.dao.apps.AppFileDao;
import com.nube.portal.dao.apps.AppsDao;

@Service
public class AppRetriverService {
	
	@Autowired
	AppFileDao appFileDao;
	
	@Autowired
	AppsDao appsDao;
	
	@Autowired
	AppBundleDao appBundleDao;
	
	
	
	static Logger logger = Logger.getLogger(AppRetriverService.class);
	
	/**
	 * Read content of any URL from document store
	 * @param url
	 * @throws NubeException 
	 */
	public GridFSDBFile read(String domain, String uri) throws NubeException{
		
		
		//Find app info, current version etc, url etc
		BasicDBObject appInfo = this.findAppInfo(domain, uri);
		
		String currentVersion =  appInfo.getString("currVersion");
		String context = appInfo.getString("context");
	
		//Remove context and version
		GridFSDBFile file = appFileDao.read(context, currentVersion, this.makeFileUri(domain, uri, context, currentVersion, appInfo));
		
		
		return file;
		
		
	}
	
	/**
	 * create file name from url
	 * Format context/version/uri
	 * @param domain
	 * @param uri
	 * @param context
	 * @param currentVersion
	 * @param appInfo
	 * @return
	 * @throws NubeException
	 */
	private String makeFileUri(String domain, String uri, String context, String currentVersion, BasicDBObject appInfo)throws NubeException{
		
		String finalUri = null;
		if(InetUtil.isLocalHost(domain)){
			//Remove context from url, but dont run this if user didnt specify home page url
			finalUri = uri.replaceFirst(AppConstants.SEPERATOR + context, StringUtil.EMPTY);
			//Find home page if uri dont have home page url
			if(finalUri.length() <= 1){
				throw new NubeException(HttpServletResponse.SC_MOVED_TEMPORARILY, 
						  AppConstants.SEPERATOR + context + 
						  AppConstants.SEPERATOR + appBundleDao.findByContextAndVersion(context, currentVersion).getString("homePage"));
			}
			
		}else{
				//Find home page
				if(uri.length() <= 1){
					throw new NubeException(HttpServletResponse.SC_MOVED_TEMPORARILY, 
							  appBundleDao.findByContextAndVersion(context, currentVersion).getString("homePage"));
				}else{
					finalUri = uri;
				}
				
		}
		return finalUri;
	}
	
	/**
	 * Find site context, then find app info
	 * @param domain
	 * @param uri
	 * @return
	 */
	@Cacheable("appInfo")
	private BasicDBObject findAppInfo(String domain, String uri)throws NubeException{
		
		try{
			String context = null;
			BasicDBObject appObject = null;
			
			
			
			
			if(InetUtil.isLocalHost(domain)){
				//Localhost will need to use context in url
				//logger.info("[Dev] Local address found");	
				context =  uri.split(AppConstants.SEPERATOR)[1];
				appObject = appsDao.findByContext(context);
			}else{
				//Production, context will be found from domain
				appObject = appsDao.findByDomain(domain);
				context = appObject.getString("context");
			}

			if(appObject == null){
				throw new NubeException(404, "Resource not found");
			}
			
			
			return appObject;
		}catch (IndexOutOfBoundsException e) {
			throw new NubeException(500, "Context is required to use localhost. example http://localhost:<port>/<context>/<url>");
		}
		
	}
	
	

}
