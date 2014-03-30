package com.nube.portal.dao.apps;

import com.mongodb.DBCollection;

/**
 * All document Dao implement this
 * @author kamoorr
 *
 */
public interface DocumentDbDao {
	
	public DBCollection getCollection();

}
