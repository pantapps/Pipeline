package operator;

import java.util.logging.Logger;

import pipeline.Pipeline;

import buffer.BAMFile;
import buffer.CSVFile;
import buffer.ReferenceFile;

/**
 * Performs table recalibration with the GATK
 * @author brendan
 *
 */
public class TableRecalibrator extends CommandOperator {

	public final String defaultMemOptions = " -Xms2048m -Xmx8g";
	public static final String PATH = "path";
	protected String defaultGATKPath = "~/GenomeAnalysisTK/GenomeAnalysisTK.jar";
	protected String gatkPath = defaultGATKPath;
	
	@Override
	protected String getCommand() {
		
		String path = properties.get(PATH);
		if (path != null) {
			gatkPath = path;
		}
		
		String reference = getInputBufferForClass(ReferenceFile.class).getAbsolutePath();
		String inputFile = getInputBufferForClass(BAMFile.class).getAbsolutePath();
		String recalDataFile = getInputBufferForClass(CSVFile.class).getAbsolutePath();
		
		
		String recalBamFile = outputBuffers.get(0).getAbsolutePath();
				
		String command = "java " + defaultMemOptions + " -jar " + gatkPath + " -R " + reference + " -I " + inputFile + " -T TableRecalibration -o " + recalBamFile + " -recalFile " + recalDataFile;
		return command;
	}

}