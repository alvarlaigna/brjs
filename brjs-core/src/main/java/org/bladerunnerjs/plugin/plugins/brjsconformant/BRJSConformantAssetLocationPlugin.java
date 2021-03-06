package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestAssetLocation;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.ThemeAssetLocation;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.WorkbenchResourcesAssetLocation;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;

public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin {
	public static List<String> getBundlableNodeThemes(BundlableNode bundlableNode) {
		Set<String> themeNames = new HashSet<>();
		
		for(AssetContainer assetContainer : bundlableNode.assetContainers()) {
			ResourcesAssetLocation resourceAssetLocation = (ResourcesAssetLocation) assetContainer.assetLocation("resources");
			
			if(resourceAssetLocation != null) {
				for(ThemeAssetLocation themeAssetLocation : resourceAssetLocation.themes()) {
					String themeName = themeAssetLocation.getThemeName();
					
					if(!themeName.equals("common")) {
						themeNames.add(themeName);
					}
					
				}
			}
		}
		
		List<String> themeNamesList = new ArrayList<>();
		themeNamesList.add("common");
		themeNamesList.addAll(themeNames);
		
		return themeNamesList;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return true;
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache)  
	{
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		try
		{
    		if (assetContainer instanceof TestPack)
    		{
				addTestPackAssetLocations(assetContainer, assetLocationCache, assetLocations);
    		}
    		else
    		{
    			addNonTestPackAssetLocations(assetContainer, assetLocationCache, assetLocations);
    		}
		}
		catch (ConfigException e)
		{
			throw new RuntimeException(e);
		}
    		
		return assetLocations;
	}

	private void addNonTestPackAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache, List<AssetLocation> assetLocations) throws ConfigException
	{
		if(!assetLocationCache.containsKey("resources")) {
			AssetLocation resourcesAssetLocation;
			if (assetContainer instanceof Workbench)
			{
				resourcesAssetLocation = new WorkbenchResourcesAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources"));
			}
			else
			{
				resourcesAssetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources"));				
			}
			assetLocationCache.put( "resources",  resourcesAssetLocation);
			assetLocationCache.put( "src", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src"), assetLocationCache.get("resources")) );
			assetLocationCache.put( "src-test", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src-test")) );
		}
		
		assetLocations.add(assetLocationCache.get("resources"));
		SourceAssetLocation srcAssetLocation = (SourceAssetLocation) assetLocationCache.get("src");
		assetLocations.add(srcAssetLocation);
		assetLocations.addAll(srcAssetLocation.getChildAssetLocations());
		SourceAssetLocation srcTestAssetLocation = (SourceAssetLocation) assetLocationCache.get("src-test");
		assetLocations.add(srcTestAssetLocation);
		assetLocations.addAll( srcTestAssetLocation.getChildAssetLocations() ) ;
	}
	
	private void addTestPackAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache, List<AssetLocation> assetLocations) throws ConfigException
	{
		TestPack testPack = (TestPack) assetContainer;
		
		if(!assetLocationCache.containsKey("resources")) {
			assetLocationCache.put( "resources", new ResourcesAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources")) );
			assetLocationCache.put( "src-test", new SourceAssetLocation(assetContainer.root(), assetContainer, testPack.testSource().dir()) );
			assetLocationCache.put( "tests", new TestAssetLocation(assetContainer.root(), assetContainer, testPack.tests().dir(), assetLocationCache.get("resources"), assetLocationCache.get("src-test")) );
		}
		
		TestAssetLocation tests = (TestAssetLocation) assetLocationCache.get("tests");
		SourceAssetLocation testSource = (SourceAssetLocation) assetLocationCache.get("src-test");
		
		assetLocations.add(assetLocationCache.get("resources"));
		assetLocations.add(tests);
		assetLocations.addAll(tests.getChildAssetLocations());
		assetLocations.add(testSource);
		assetLocations.addAll(testSource.getChildAssetLocations());
	}

}
