package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.model.AppConf;

public class AppConfBuilder extends SpecTest {
	private AppConf appConf;
	private BuilderChainer builderChainer;
	
	public AppConfBuilder(SpecTest specTest, AppConf appConf) {
		this.appConf = appConf;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer hasRequirePrefix(String requirePrefix) throws Exception {
		appConf.setRequirePrefix(requirePrefix);
		appConf.write();
		
		return builderChainer;
	}
}
