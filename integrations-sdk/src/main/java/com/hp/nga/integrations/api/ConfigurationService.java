package com.hp.nga.integrations.api;

import com.hp.nga.integrations.SDKServicePublic;
import com.hp.nga.integrations.dto.configuration.CIProxyConfiguration;
import com.hp.nga.integrations.dto.configuration.NGAConfiguration;
import com.hp.nga.integrations.dto.connectivity.NGAResponse;

import java.io.IOException;

public interface ConfigurationService extends SDKServicePublic {

	/**
	 * Builds configuration object from raw data, usually supplied from UI or storage
	 *
	 * @param rawUrl
	 * @param apiKey
	 * @param secret
	 * @return
	 */
	NGAConfiguration buildConfiguration(String rawUrl, String apiKey, String secret) throws IllegalArgumentException;

	/**
	 * Tests connectivity to the NGA server with the supplied configuration
	 *
	 * @param configuration
	 * @return
	 * @throws IOException in case of connection failure
	 */
	NGAResponse validateConfiguration(NGAConfiguration configuration) throws IOException;

	/**
	 * Notify SDK notification on NGA configuration change
	 *
	 * @param newConfiguration
	 */
	void notifyChange(NGAConfiguration newConfiguration);

	/**
	 * Notify SDK about proxy configuration change of the hosting CI Server
	 *
	 * @param newConfiguration
	 */
	void notifyChange(CIProxyConfiguration newConfiguration);
}
