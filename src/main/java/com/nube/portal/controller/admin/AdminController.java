package com.nube.portal.controller.admin;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nube.core.exception.NubeException;
import com.nube.portal.service.apps.UploaderService;
import com.nube.portal.threads.apps.AppProcessorTask;
/**
 * Admin controller 
 * @author kamoorr
 */
@Controller("adminController")
public class AdminController {

	@Autowired
	UploaderService uploaderService;
	
	@Autowired
	AppProcessorTask appProcessorTask;
	
	@Value("${app.filetype}")
	String fileType;

	@PostConstruct
	public void postConstruct() {
		logger.info("Portal Admin Controller initialized");
	}

	static Logger logger = Logger.getLogger(AdminController.class);

	/**
	 * Admin home page
	 * @param user
	 * @return
	 */
	@RequestMapping("/admin")
	public ModelAndView defaultAdminRequest(final HttpServletRequest request) {
		logger.info("admin request");
		return new ModelAndView("admin");
	}

	/**
	 * Upload a file
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/admin/app/upload", method = RequestMethod.POST)
	public ModelAndView handleFileUpload(
			@RequestParam("app") MultipartFile file) {
		logger.info("App Upload request");
		String msg = "File upload completed. It may take few minutes to make it live.";
		try {
			String destFile = uploaderService.save(file, file.getOriginalFilename(), fileType);
			appProcessorTask.processFile(destFile);
			logger.info("Upload completed, but not processed yet");
		} catch (NubeException e) {
			msg = "Upload failed, error: " + e.getMessage();
		}

		return new ModelAndView("admin").addObject("msg", msg);
	}

}
