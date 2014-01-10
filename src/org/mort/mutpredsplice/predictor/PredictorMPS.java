/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.predictor;

import java.io.File;
import java.util.Map;
import mps.common.Dataset;
import mps.common.TrainSet;
import mps.config.ConfigPaper;
import mps.config.Log;
import mps.learning.EnsembleModelPredict;
import mps.learning.PredictionResult;
import mps.learning.VotingStrategy;
import mps.pipeline.mpsvone.scripts.Main1_AnnotateWithFeatures;
import mps.pipeline.predictor.AverageAllModels;

/**
 * MutPred Splice Predictor
 *
 * @author wmgmm
 */
public class PredictorMPS {

    private VotingStrategy strat = new AverageAllModels();// AverageAllModelsRemoveLowest();//new MajorityVote();//new AverageAllModels();//new MajorityVote();
    private EnsembleModelPredict pred;
    private String dir;

    public PredictorMPS(String dir) throws Exception {
        this.dir = dir;
        init();
        Log.logInfo("Starting predictor New:" + dir);

        pred = EnsembleModelPredict.fetchPredictorRF(dir);

    }

    //check for errors
    private void init() {
        File f = new File(this.dir);
        if (f.canRead() && f.isDirectory()) {
        } else {
            throw new IllegalArgumentException("ModelDirNotFound!");
        }
    }

    public String getModelDir() {
        return dir;
    }

    /**
     * Just used by the paper
     *
     * @param s
     * @throws Exception
     */
    public PredictorMPS(TrainSet s) throws Exception {

        this(ConfigPaper.getTrainedModelDir(s));

    }

    public void setVotingStrategy(VotingStrategy v) {
        this.strat = v;
    }

    /**
     * This is then wrapped in a dataset prediction object
     *
     * @param d
     * @return
     * @throws Exception
     */
    private Map<String, PredictionResult> predict(Dataset d) throws Exception {

        //annotate if required
        if (d.hasFeatures() == false) {
            Log.logInfo("Predictor.predict Annotating dataset with features " + d);
            try {
                Main1_AnnotateWithFeatures.annotate(d);
            } catch (Exception e) {
                Log.logError("in PredictorMPS.predict " + e.toString());
            }
            Log.logInfo("Annotating dataset with features " + d);
        }

        return pred.predictMap(d.getInstances(false), strat);
    }
}
