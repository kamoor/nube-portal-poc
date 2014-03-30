package com.nube.portal.threads.apps;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.nube.core.exception.NubeException;
import com.nube.portal.service.apps.AppPackageProcessorService;

/**
 * Process app package uploaded by a developer.
 * @author kamoorr
 *
 */
@Service("appProcessorTask")
public class AppProcessorTask {

	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	AppPackageProcessorService appPackageProcessorService;
	
	
	@Value("${app.upload.temp-folder}")
	private String tempFolder;
	
	/**
	 * Call this method to start processing file
	 * @param file
	 */
	public void processFile(String file){
		taskExecutor.execute(new AppProcessorThread(file));
	}
	

	static Logger logger = Logger.getLogger(AppProcessorThread.class);

	/**
	 * This thread will process file which are uploaded by user
	 * 
	 * @author kamoorr
	 * 
	 */
	private class AppProcessorThread implements Runnable {

		private String fileToProcess;

		public AppProcessorThread(String file) {
			this.fileToProcess = file;
		}

		public void run(){
			try{
				logger.info("Processing file " + fileToProcess);
				appPackageProcessorService.processAppPackage(fileToProcess, tempFolder);
				
						
			}catch(IOException e){
				logger.error(e);
			}catch(NubeException e){
				logger.error(e);
			}
		
		}

		private String getName() {
			return Thread.currentThread().getName();
		}

	}
	
	

}
