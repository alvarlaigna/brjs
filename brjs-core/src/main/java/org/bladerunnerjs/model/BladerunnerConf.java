package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlBladerunnerConf;

public class BladerunnerConf extends ConfFile<YamlBladerunnerConf> {
	public BladerunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlBladerunnerConf.class, brjs.file("conf/bladerunner.conf"));
	}
	
	public int getJettyPort() throws ConfigException {
		reloadConfIfChanged();
		return conf.jettyPort;
	}
	
	public void setJettyPort(int jettyPort) throws ConfigException {
		conf.jettyPort = jettyPort;;
		verifyAndAutoWrite();
	}
	
	public String getDefaultFileCharacterEncoding() throws ConfigException {
		reloadConfIfChanged();
		return conf.defaultFileCharacterEncoding;
	}
	
	public void setDefaultFileCharacterEncoding(String defaultFileCharacterEncoding) throws ConfigException {
		conf.defaultFileCharacterEncoding = defaultFileCharacterEncoding;
		verifyAndAutoWrite();
	}
	
	public String getBrowserCharacterEncoding() throws ConfigException {
		reloadConfIfChanged();
		return conf.browserCharacterEncoding;
	}
	
	public void setBrowserCharacterEncoding(String browserCharacterEncoding) throws ConfigException {
		conf.browserCharacterEncoding = browserCharacterEncoding;
		verifyAndAutoWrite();
	}

	public String getLoginRealm() throws ConfigException
	{
		reloadConfIfChanged();
		return conf.loginRealm;
	}

	public String getLoginModuleName() throws ConfigException
	{
		reloadConfIfChanged();
		return conf.LOGIN_MODULE_NAME;
	}
}
