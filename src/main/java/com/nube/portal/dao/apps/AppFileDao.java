package com.nube.portal.dao.apps;

import java.io.File;
import java.io.IOException;

import com.mongodb.gridfs.GridFSDBFile;
import com.nube.core.exception.NubeException;

/**
 * Each file a bundle will be stored here
 * @author kamoorr
 *
 */
public interface AppFileDao {

	/**
	 * Generic save document method
	 * @param name  should include full path
	 * @param file
	 * @throws IOException
	 */
	public void save(String documentName, File file)throws IOException;
	
	/**
	 * How to store files in an app package
	 * @param context
	 * @param version
	 * @param rootFolder
	 * @param f
	 * @throws IOException
	 */
	public void saveAppDocument(String context, String version, String rootFolder,  File f)throws IOException;
	/**
	 * Read a file
	 * @param context
	 * @param version
	 * @param file
	 * @return
	 * @throws NubeException
	 */
	public GridFSDBFile read(String context, String version, String fileUri) throws NubeException;
	
	/**
	 * 
	 * @param documentName
	 * @return
	 * @throws NubeException
	 */
	public GridFSDBFile read(String documentName) throws NubeException;
	
	/**
	 * Update a document
	 */
	public void delete(String filePath);
}
