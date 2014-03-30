package com.nube.portal.validator.apps;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nube.core.exception.NubeException;

@Component
public class AppPackageValidator {

	@Value("${app.lic.file-name}")
	String licFileKey;

	/**
	 * home page set by user
	 */
	@Value("${app.lic.key.site-home}")
	String homePageUrlkey;

	/**
	 * Default home page for any app
	 */
	@Value("${app.default-site-home}")
	String defaultHomePage;

	/**
	 * Return lic key entries
	 * 
	 * @param appFolder
	 * @return
	 * @throws NubeException
	 * @throws IOException
	 */
	public Properties validate(String appFolder) throws NubeException,
			IOException {

		File pkgFolder = new File(appFolder);

		if (!pkgFolder.isDirectory()) {
			throw new NubeException(
					"Package should be folder before validation begins");
		}

		// Lic Validator
		Properties licProps = validateLicKey(pkgFolder);

		// Atlest one file apart from lic.key
		if (pkgFolder.list().length == 2) {
			throw new NubeException(String.format("Package is almost empty"));
		}

		// TODO: Check for more stuff.
		// Validate site token, home page etc

		return licProps;

	}

	private Properties validateLicKey(File pkgFolder) throws IOException,
			NubeException {

		File licFile = new File(pkgFolder.getAbsolutePath() + File.separator
				+ licFileKey);

		// Check for license key
		if (!licFile.exists()) {
			throw new NubeException(String.format(
					"Package doesnt not contain license file (%s)", licFile));
		}

		Properties licProperties = new Properties();
		licProperties.load(new FileReader(licFile));

		String homePage = licProperties.getProperty(homePageUrlkey) == null ? defaultHomePage
				: licProperties.getProperty(homePageUrlkey);

		if (!new File(pkgFolder.getAbsolutePath() + File.separator + homePage)
				.exists()) {
			throw new NubeException(
					String.format(
							"Package does not contain home page file %s, or license file do not have property (%s) set",
							homePage, homePageUrlkey));
		}

		return licProperties;

	}

}
