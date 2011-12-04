package operator.gatk;

import java.io.File;

import operator.MultiOperator;
import pipeline.Pipeline;
import pipeline.PipelineXMLConstants;
import buffer.BAMFile;
import buffer.BEDFile;
import buffer.FileBuffer;
import buffer.ReferenceFile;
import buffer.VCFFile;

/**
 * Parallel version of genotyper
 * @author brendan
 *
 */
public class MultiGenotype extends MultiOperator {

	public final String defaultMemOptions = " -Xms2048m -Xmx8g";
	public static final String PATH = "path";
	public static final String THREADS = "threads";
	public static final String JVM_ARGS="jvmargs";
	protected String defaultGATKPath = "~/GenomeAnalysisTK/GenomeAnalysisTK.jar";
	protected String gatkPath = defaultGATKPath;
	
	@Override
	protected String[] getCommand(FileBuffer inputBuffer) {
		
		Object propsPath = Pipeline.getPropertyStatic(PipelineXMLConstants.GATK_PATH);
		if (propsPath != null)
			gatkPath = propsPath.toString();
		
		String path = properties.get(PATH);
		if (path != null) {
			gatkPath = path;
		}
		
		//Additional args for jvm
		String jvmARGStr = properties.get(JVM_ARGS);
		if (jvmARGStr == null || jvmARGStr.length()==0) {
			jvmARGStr = (String) Pipeline.getPropertyStatic(JVM_ARGS);
		}
		//If it's still null then be sure to make it the empty string
		if (jvmARGStr == null || jvmARGStr.length()==0) {
			jvmARGStr = "";
		}
		
		String inputPath = inputBuffer.getAbsolutePath();
		FileBuffer dbsnpFile = getInputBufferForClass(VCFFile.class);
			
		FileBuffer bedFile = getInputBufferForClass(BEDFile.class);
		String bedFilePath = "";
		if (bedFile != null) {
			bedFilePath = bedFile.getAbsolutePath();
		}
		
		int index = inputPath.lastIndexOf(".");
		String prefix = inputPath;
		if (index>0)
			prefix = inputPath.substring(0, index);
		String outputVCFPath = prefix + ".vcf";
		FileBuffer vcfBuffer = new VCFFile(new File(outputVCFPath), inputBuffer.getContig());
		addOutputFile( vcfBuffer );
		
		String command = "java " + defaultMemOptions + " " + jvmARGStr + " -jar " + gatkPath;
		command = command + " -R " + reference.getAbsolutePath() + 
				" -I " + inputPath + 
				" -T UnifiedGenotyper";
		command = command + " -o " + outputVCFPath;
		if (dbsnpFile != null)
			command = command + " --dbsnp " + dbsnpFile.getAbsolutePath();
		command = command + " -glm BOTH";
		command = command + " -stand_call_conf 30.0";
		command = command + " -stand_emit_conf 10.0";
		if (inputBuffer.getContig() != null) {
			command = command + " -L " + inputBuffer.getContig() + " ";
		}
		if (inputBuffer.getContig() == null && bedFile != null)
			command = command + " -L:intervals,BED " + bedFilePath;
		return new String[]{command};
	}

	
}
