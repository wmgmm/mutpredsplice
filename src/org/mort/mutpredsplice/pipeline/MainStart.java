/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import mps.common.Dataset;
import mps.common.DatasetFactory;
import mps.config.Config;
import mps.config.Log;
import mps.pipeline.mpsvone.scripts.ConfigMPS;
import mps.pipeline.predictor.DatasetPrediction;
import mps.pipeline.predictor.PredictorMPS;

//import org.mort.common.Files;
import org.mort.config.PropertyLoader;
import org.mort.mutpredsplice.config.ConfigRelease;
import org.mort.mutpredsplice.input.Adaptor;
import org.mort.mutpredsplice.input.PlainTextAdaptor;
import org.mort.mutpredsplice.input.VCFAdaptor;
import org.mort.mutpredsplice.view.PredictionResultsViewANNOVAR;

/**
 * Main Entry pont into the class
 *
 * @author wmgmm
 */
public class MainStart {

    private static PredictorMPS pred;

    public static void setupPredictor(Properties prop) throws Exception {

        //set mutpred splice properties
        prop.setProperty("predictRF", prop.getProperty("appDir") + "3_rscripts/RAN_FOREST_PREDICT.R");
        Config.setNewProperties(prop);
        ConfigMPS.setIteration(3);
        pred = new PredictorMPS(Config.getTrainedModelDir());

    }

    /**
     * arg[0] is input vcf arg[1] is final output file
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        String directory = System.getProperty("user.dir") + File.separator;

        if (args.length < 2 || args.length > 3) {
            System.out.println("USAGE: java -Xmx6g -jar MutPredSpliceRelease.jar INPUT_VCF_FILE OUTPUT_GENERIC_ANNOVAR_FILE");
            System.exit(1);
        }

        //set everything up
        Log.setFileLogOff();
        Log.setInfo(false);
        Log.setWarning(false);
        Log.setError(false);

        Properties prop = PropertyLoader.loadProperties("org.mort.mutpredsplice.config.myRelease");

        //in the release so overload with hardcoded path
        if (directory.endsWith("/dist/")) {
            directory = directory.replaceAll("/dist/", "/");
            prop.setProperty("appDir", directory);
            prop.setProperty("genomeDir", directory + "fasta/");
        }

        Path myPath = Files.createTempDirectory("");
        myPath.toFile().deleteOnExit();
        String tmp = String.valueOf(myPath.toAbsolutePath()) + File.separator;
        prop.setProperty("rWorkingDir", tmp);
        prop.setProperty("tmpDir", tmp);

        setupPredictor(prop);
        String input = args[0];//"/home/wmgmm/Dropbox/work/projects/data/VCF_data_files/cosmic_sample.vcf";//cosmic_sample.vcf";

        String output = args[1];//"/home/wmgmm/Desktop/releaseTest/output.tsv";

        System.out.println("Processing input file:" + input);

        org.mort.common.Files.delete(output);

        if (pred == null) {
            System.out.println("Unable to find predictor; exiting");
            System.exit(1);
        }

        boolean vcf = true;
        if (args.length == 3) { //switch to plain text mode
            vcf = false;
        }

        Adaptor v;

        if (vcf) {
            v = new VCFAdaptor(input, prop.getProperty("tmpDir"));
        } else {
            v = new PlainTextAdaptor(input, prop.getProperty("tmpDir"));
        }


        v.partion();

        List<String> inputData = v.getInputFiles();

        for (int i = 0; i < inputData.size(); i++) {
            String inputFile = inputData.get(i);
            try {
                DatasetPrediction preds = predictionsForDataset(inputFile);
                appendToResults(output, preds);
            } catch (Exception ex) {
                Logger.getLogger(MainStart.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        v.cleanUp();
        System.out.println("MutPred Splice Job Completed:" + output);

    }

    public static DatasetPrediction predictionsForDataset(String input) throws Exception {

        Dataset myData = DatasetFactory.loadDatasetPlainTxt(input);

        return pred.predictForDataset(myData);

    }

    public static void appendToResults(String outputFile, DatasetPrediction pred) {
        PredictionResultsViewANNOVAR view = new PredictionResultsViewANNOVAR(pred, false, false);
        view.save(outputFile);
    }
}
