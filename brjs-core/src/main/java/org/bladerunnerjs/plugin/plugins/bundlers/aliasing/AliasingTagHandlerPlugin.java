package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsTagHandlerPlugin;


public class AliasingTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	private ContentPlugin aliasingContentPlugin;

	@Override
	public void setBRJS(BRJS brjs)
	{
		aliasingContentPlugin = brjs.plugins().contentProvider( getTagName() );
	}
	
	@Override
	public String getTagName()
	{
		return "aliasing";
	}

	@Override
	public String getGroupName()
	{
		return "text/javascript";
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Arrays.asList(NamespacedJsTagHandlerPlugin.class.getCanonicalName());
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try {
			writeTagContent(bundleSet, aliasingContentPlugin.getValidDevContentPaths(bundleSet, locale), writer);
		}
		catch (ContentProcessingException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try {
			writeTagContent(bundleSet, aliasingContentPlugin.getValidProdContentPaths(bundleSet, locale), writer);
		}
		catch (ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
	private void writeTagContent(BundleSet bundleSet, List<String> requestPaths, Writer writer) throws IOException {
		for(String bundlerRequestPath : requestPaths) {
			writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
		}
	}


}
