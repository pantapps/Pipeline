package buffer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pipeline.ObjectCreationException;
import pipeline.Pipeline;
import pipeline.PipelineObject;

/**
 * This MultiFileBuffer contains all files matching the regexp given in the filename attribute
 * For instance, if filename="*.bam" is an attribute, this thing will contain all files in PROJECT_HOME
 * that end in .bam
 * @author brendan
 *
 */
public class GlobFileBuffer extends MultiFileBuffer {

	@Override
	public void initialize(NodeList children) {
		final String pattern = properties.get(FILENAME_ATTR);
		Logger logger = Logger.getLogger(Pipeline.primaryLoggerName);
		
		String projHome = System.getProperty("user.dir");
		if (! pattern.startsWith("/")) {
			String projHomeAttr = properties.get(Pipeline.PROJECT_HOME);
			if (projHomeAttr != null) {
				projHome = projHomeAttr;
			}
		}
		
		File projDir = new File(projHome);
		System.out.println("pattern is: " + pattern);
		FileMatcher fileMatcher = new FileMatcher( Pattern.compile(pattern));
		File[] listing = projDir.listFiles(fileMatcher);
	
		if (listing.length == 0) {
			logger.warning("GlobFileBuffer with pattern " + pattern + " matched zero files!");
		}
		
		for(int i=0; i<listing.length; i++) {
			FileBuffer buffer = FileTypeGuesser.GuessFileType( listing[i]);
			System.out.println("Glob buffer is adding : " + listing[i].getAbsolutePath());
			addFile( buffer );
		}
		
	}
	
	
	public class FileMatcher implements FilenameFilter {
		
		Pattern pattern;
		public FileMatcher(Pattern pattern) {
			this.pattern = pattern;
		}
		
		@Override
		public boolean accept(File dir, String name) {
			Matcher matcher = pattern.matcher(name);
//			if (matcher.matches()) {
//				System.out.println("Matcher matches name: " + name);
//			}
//			else {
//				System.out.println("Matcher does NOT match name: " + name);
//			}
			return matcher.matches();
		}
		
	}

}