package operator;

import java.util.List;

import buffer.BAMFile;
import buffer.FileBuffer;
import buffer.ReferenceFile;
import buffer.VCFFile;

/**
 * Uses the GATK to count the covariates
 * @author brendan
 *
 */
public class CountCovariates extends CommandOperator {

	public final String defaultMemOptions = " -Xms2048m -Xmx8g";
	public static final String PATH = "path";
	public static final String CREATE_INDEX = "createindex";
	protected String defaultGATKPath = "~/GenomeAnalysisTK/GenomeAnalysisTK.jar";
	protected String gatkPath = defaultGATKPath;
	protected int threads = 16;
	
	@Override
	protected String getCommand() {
		
		String path = properties.get(PATH);
		if (path != null) {
			gatkPath = path;
		}
		
		String reference = getInputBufferForClass(ReferenceFile.class).getAbsolutePath();
		String inputFile = getInputBufferForClass(BAMFile.class).getAbsolutePath();
		List<FileBuffer> knownSitesFiles = getAllInputBuffersForClass(VCFFile.class);
		
		StringBuffer knownSitesStr = new StringBuffer();
		for(FileBuffer buff : knownSitesFiles) {
			knownSitesStr.append("-knownSites " + buff.getAbsolutePath() + " ");
		}
		
		
		String csvOutput = outputBuffers.get(0).getAbsolutePath();
		
		String covariateList = "-cov QualityScoreCovariate -cov CycleCovariate -cov DinucCovariate";
		
		String command = "java " + defaultMemOptions + " -jar " + gatkPath + " -R " + reference + " -I " + inputFile + " -T CountCovariates -nt " + threads + " " + covariateList + " " + knownSitesStr.toString() + " -recalFile " + csvOutput;
		return command;
	}

}