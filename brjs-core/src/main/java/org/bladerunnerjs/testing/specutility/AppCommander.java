package org.bladerunnerjs.testing.specutility;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.ValueCommand;


public class AppCommander extends NodeCommander<App> {
	private final App app;
	
	public AppCommander(SpecTest specTest, App app) {
		super(specTest, app);
		this.app = app;
	}
	
	public CommanderChainer populate(final String requirePrefix) {
		call(new Command() {
			public void call() throws Exception {
				app.populate(requirePrefix);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer deployApp() {
		call(new Command() {
			public void call() throws Exception {
				app.deploy();
			}
		});
		
		return commanderChainer;
	}
	
	public AppConfCommander appConf() throws Exception {
		AppConfCommander commander = call(new ValueCommand<AppConfCommander>() {
			public AppConfCommander call() throws Exception {
				return new AppConfCommander(specTest, app.appConf());
			}
		});
		
		return commander;
	}

	public CommanderChainer fileCreated(final String filePath)
	{
		call(new Command() {
			public void call() throws Exception {
				app.file(filePath).createNewFile();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceived(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				BladerunnerUri bladerunnerUri = new BladerunnerUri(app.root(), app.dir(), "/" + app.getName(), requestPath, null);
				ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
				app.getBundlableNode(bladerunnerUri).handleLogicalRequest(bladerunnerUri.logicalPath, responseOutput);
				response.append(responseOutput.toString(specTest.getActiveClientCharacterEncoding()));
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer fileDeleted(final String filePath)
	{
		call(new Command() {
			public void call() throws Exception {
				app.file(filePath).delete();
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer fileContentsChangeTo(final String filePath, final String fileContents)
	{
		call(new Command() {
			public void call() throws Exception {
				fileUtil.write(app.file(filePath), fileContents);
			}
		});
		
		return commanderChainer;
	}
}
