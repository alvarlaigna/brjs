package org.bladerunnerjs.testing.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.utility.ProcessLogger;

public class CommandRunner {
	private static final Runtime runTime = Runtime.getRuntime();
	
	public static void runCommand(BRJS brjs, List<String> command) throws CommandOperationException {
		try {
			Process process = runTime.exec(command.toArray(new String[0]));
			ProcessLogger processLogger = new ProcessLogger(brjs, process, null);
			int exitCode = waitForProcess(process, brjs.getConsoleWriter());
			processLogger.waitFor();
			
			if(exitCode != 0) throw new CommandOperationException("Error while running command '" + command + "' (" + exitCode + ")");
		}
		catch(IOException | InterruptedException e) {
			throw new CommandOperationException(e);
		}
	}
	
	private static int waitForProcess(Process process, ConsoleWriter out) throws IOException, InterruptedException {
		try(InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
		{
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				out.println(line);
			}
		}
		
		// TODO: this code looks like a bug -- investigate
		int exitCode = process.waitFor();
		process.waitFor();
		
		return exitCode;
	}
}
