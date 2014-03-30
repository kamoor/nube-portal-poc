package com.nube.portal.controller.consumer;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.gridfs.GridFSDBFile;
import com.nube.core.exception.NubeException;
import com.nube.portal.service.apps.AppRetriverService;

@Controller("defaultPortalController")
public class PortalController {
	
	
	@Autowired
	AppRetriverService appService;
	
	
	@PostConstruct
	public void postConstruct() {
		logger.info("Default Portal Controller initialized");
	}
	
	
	
	static Logger logger = Logger.getLogger(PortalController.class);
	
	
	/**
	 * Default Handler for any cloud resources. Only certain file types are allowed due to security reasons.
	 * URL will allow only: a-zA-Z0-9-_+.
	 * Allowed types: html|htm|js|css|jpg|gif|png
	 * @param 
	 * @return
	 * 
	 * @throws ServletException 
	 */
	// @RequestMapping({"/{begin:(?:[a-zA-Z0-9-_+.]+$)}/**.{ext:(?:txt|xml|html|htm|js|css|jpg|gif|png)}"})
	 @RequestMapping({"/{begin:(?:[a-zA-Z0-9-_+.]+$)}/**/**.{ext:(?:txt|xml|html|htm|js|css|jpg|gif|png)}"})
	 public void defaultRequest(@PathVariable("begin") String begin, @PathVariable("ext") String ext, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException{
		  this.handleAllRequest(ext,request,response);
	  }
	 
	 /**
	  * Home page request from cloud
	  * Allowed .html, .css .txt, .xml
	  * @param request
	  * @param response
	  * @return
	  * @throws IOException
	  * @throws ServletException
	  */
	 @RequestMapping({"/","/*.html","/*.htm","/*.txt","*.xml","*/","*"})
	 public void defaultHomeRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException{
		 this.handleAllRequest("html",request,response);
	  }
	 
	 /**
	  * This method should handle all the consumer request
	  * @param ext
	  * @param request
	  * @param response
	  * @return
	  * @throws IOException
	  * @throws ServletException
	  */
	 private @ResponseBody void handleAllRequest(String ext, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException{
			//logger.info("cloud request: "+ request.getRequestURL());
		//Need to find context;
			try{
				
				GridFSDBFile file = appService.read(request.getServerName(), request.getRequestURI());
				response.setContentType(file.getContentType());
			    file.writeTo(response.getOutputStream());
			    
			}catch(NubeException e){
				logger.error(e.getMessage());
				if(e.getErrorCode() == HttpServletResponse.SC_NOT_FOUND){
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}else if(e.getErrorCode() == HttpServletResponse.SC_MOVED_TEMPORARILY){
					response.sendRedirect(e.getMessage());
				}else{
					logger.error(e);
					response.sendError(e.getErrorCode(), e.getMessage());
				}
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong at server. Try later.");
			}finally{
				//logger.info("Mem "+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) /1024 / 1024 + " MB" );
			}
      }

}
