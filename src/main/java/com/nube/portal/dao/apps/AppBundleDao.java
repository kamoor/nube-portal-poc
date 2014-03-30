package com.nube.portal.dao.apps;

import java.util.Properties;

import com.mongodb.BasicDBObject;
import com.nube.core.exception.NubeException;

/**
 * A bundle storage
 * @author kamoorr
 *
 */
public interface AppBundleDao {
	
	public void save(Properties licProps);
	
	public BasicDBObject findByContextAndVersion(String context, String version) throws NubeException;
	
	public void delete(String context, String version);

}
