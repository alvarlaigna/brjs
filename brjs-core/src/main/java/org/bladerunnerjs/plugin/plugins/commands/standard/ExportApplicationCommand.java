package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filefilter.ExcludeDirFileFilter;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class ExportApplicationCommand extends ArgsParsingCommandPlugin
{
	private final static String DISCLAIMER = "Do not edit this file; edits will be lost after upgrades.";
	
	private static final String DISCLAIMER_PREFIX = "/*\n* ";
	private static final String DISCLAIMER_SUFFIX = "\n*/\n\n";
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the name of the application to be exported"));
		argsParser.registerParameter(new UnflaggedOption("disclaimer").setDefault(DISCLAIMER).setRequired(false).setHelp("a disclaimer that will be added to every exported class"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		logger = brjs.logger(LoggerType.APP_SERVER, this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "export-app";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create an importable zip for a given application.";
	}
	
	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException 
	{
		String appName = parsedArgs.getString("app-name");
		String disclaimer = DISCLAIMER_PREFIX + parsedArgs.getString("disclaimer") + DISCLAIMER_SUFFIX;
		App app = brjs.app(appName);
		
		if(!app.dirExists()) throw new CommandArgumentsException("Could not find application '" + appName + "'", this);
		
		File destinationZipLocation = new File(brjs.storageDir("exported-app").getAbsolutePath() + "/" + appName + ".zip");

		try 
		{
			File temporaryExportDir = FileUtility.createTemporaryDirectory(appName);
			
			IOFileFilter excludeUserLibraryTestsFilter = createExcludeUserLibsTestsFilter(appName);
			NotFileFilter brjsJarFilter = new NotFileFilter(new AndFileFilter(new PrefixFileFilter("brjs-"), new SuffixFileFilter(".jar")));
			IOFileFilter combinedFilter = new AndFileFilter(new ExcludeDirFileFilter("js-test-driver", "bundles"), brjsJarFilter);
			
			combinedFilter = new AndFileFilter(combinedFilter, excludeUserLibraryTestsFilter);
			
			createResourcesFromSdkTemplate(app.dir(), temporaryExportDir, combinedFilter);
			includeDisclaimerInDirectoryClasses(new File(temporaryExportDir, "libs"), disclaimer);
			FileUtility.zipFolder(temporaryExportDir, destinationZipLocation, false);
		}
		catch (Exception e)
		{
			throw new CommandOperationException("Could not create application zip for application '" + appName + "'", e);  
		}

		logger.info("Successfully exported application '" + appName + "'");
		logger.info(" " + destinationZipLocation.getAbsolutePath());
	}

	
	private void createResourcesFromSdkTemplate(File templateDir, File targetDir, FileFilter fileFilter) throws IOException
	{
		ArrayList<File> addList = new ArrayList<File>();
		recurseIntoSubfoldersAndAddAllFilesMatchingFilter(addList, templateDir, fileFilter);
		
		if (!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		
		for (File f : addList)
		{			
			String relativePathFromTemplateDir = f.getAbsolutePath().replace(templateDir.getAbsolutePath(), "");
			File newResourceToAdd = new File(targetDir, relativePathFromTemplateDir);

			if (f.isDirectory() == true)
			{
				newResourceToAdd.mkdirs();
			}
			else 
			{
				createFile(f, newResourceToAdd);
			}
		}
	}
	
	private void createFile(File source, File newFileLocation) throws IOException
	{
		if (source.exists() == true)
		{
			if (newFileLocation.exists() == false)
			{
				if (newFileLocation.getParentFile().exists() == false)
				{
					newFileLocation.getParentFile().mkdirs();
				}
				
				FileUtils.copyFile(source, newFileLocation);
			}
		}
	}

	private ArrayList<File> recurseIntoSubfoldersAndAddAllFilesMatchingFilter(ArrayList<File> addList, File file, FileFilter filter)
	{
		if(file.isDirectory())
		{
			for(File r : file.listFiles(filter))
			{
				if (!r.getName().startsWith("."))
				{
					recurseIntoSubfoldersAndAddAllFilesMatchingFilter(addList, r, filter);
				}
			}
		}
		
		if(filter.accept(file) && !file.getName().startsWith("."))
		{
			addList.add(file);
		}
		
		return addList;
	}
	
	private IOFileFilter createExcludeUserLibsTestsFilter(String appName) 
	{
		IOFileFilter excludeDirFilter = new ExcludeDirFileFilter("");
		
		for (JsLib jsLib : brjs.app(appName).jsLibs())
		{
			if (jsLib.parentNode() instanceof App)
			{
				excludeDirFilter = new AndFileFilter (excludeDirFilter, new ExcludeDirFileFilter(jsLib.getName(), "test"));
			}
		}
		
		return excludeDirFilter;
	}
	
	private void includeDisclaimerInDirectoryClasses(File dir, String disclaimer) throws IOException, ConfigException 
	{
		String[] extensions = {"js"};
		
		if (dir.exists())
		{
			for (File file : FileUtils.listFiles(dir, extensions, true))
			{
				includeDisclaimer(file, disclaimer);
			}
		}
	}

	private void includeDisclaimer(File file, String disclaimer) throws ConfigException, IOException 
	{
		String fileContent = FileUtils.readFileToString(file, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
	
		FileUtils.writeStringToFile(file, disclaimer + fileContent, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
	}
}
