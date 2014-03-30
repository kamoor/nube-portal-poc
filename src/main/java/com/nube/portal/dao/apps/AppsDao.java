package com.nube.portal.dao.apps;

import com.mongodb.BasicDBObject;
import com.nube.core.exception.NubeException;

/**
 * Each app meta data
 * @author kamoorr
 *
 */
public interface AppsDao {
	
	public BasicDBObject findByContext(String context) throws NubeException;
	public BasicDBObject findByDomain(String domain) throws NubeException;
	public void save(String context, String urls, String version, String updatedBy);
	

}
