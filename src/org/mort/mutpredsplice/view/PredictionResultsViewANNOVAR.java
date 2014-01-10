/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mps.common.Dataset;
import mps.common.TrainSet;
import mps.input.SampleDataset;
import mps.learning.PredictionResult;
import mps.pipeline.mpsvone.scripts.Main6_PredictWithModel;
import mps.pipeline.predictor.DatasetPrediction;
import mps.pipeline.predictor.VariantPrediction;
import mps.views.PredictionResultsViewAll;
import mps.views.PredictionResultsViewSAVsOnly;
import mps.views.View;
import org.mort.common.Files;

/**
 * predictions liste
 *
 * actualClass, predicted, blah
 *
 * OPTION for highest result or all results
 *
 * id
 *
 * @author wmgmm
 */
public class PredictionResultsViewANNOVAR implements View {

    protected final boolean allResults;
    protected final DatasetPrediction results;
    protected static final String D = "\t";
    protected static final String M = "?";
    protected final boolean header;

    /**
     *
     * @param results
     * @param allResults if true all results will be shown for the dataset not
     * if header false then it appends just the highest
     */
    public PredictionResultsViewANNOVAR(DatasetPrediction results, boolean allResults, boolean header) {
        this.allResults = allResults;
        this.results = results;
        this.header = header;

    }

    protected String getHeader() {

        return "Chr" + D + "Start" + D + "Stop" + D + "REF" + D + "ALT" + D + "MutPred_General_Score" + D + "Hypothesis";

    }
    /**
     * if all true then all results returned
     *
     * @param vp
     * @return
     */
    protected List<String> formatVariant(VariantPrediction vp) {

        List<String> output = new ArrayList<>();

        //if not scored then not present in the file
        if (vp.isScored() == false) {   
            return output;
        }
        
        if (allResults) {

            Map<String, PredictionResult> hg2res = vp.getResults();
            for (String hgvs : hg2res.keySet()) {
                PredictionResult pr = hg2res.get(hgvs);
                String tmp = vp.getCoordinate().getChr().getChrNumberWithoutPrefix() + D + vp.getCoordinate().getCoordinate() + D + vp.getCoordinate().getCoordinate() + D + vp.getVariant().getWildtypeVariationPosStrand() + D + vp.getVariant().getMutantVariationPosStrand() + D + pr.getGeneralScore()+D+hgvs;
                output.add(tmp);
            }

        } else {

            Map.Entry<String, PredictionResult> largestResult = vp.getLargestResult();
            String hgvs = largestResult.getKey();
            PredictionResult pr = largestResult.getValue();
            String tmp = vp.getCoordinate().getChr().getChrNumberWithoutPrefix() + D + vp.getCoordinate().getCoordinate() + D + vp.getCoordinate().getCoordinate() + D + vp.getVariant().getWildtypeVariationPosStrand() + D + vp.getVariant().getMutantVariationPosStrand() + D + pr.getGeneralScore() + D + hgvs;
            output.add(tmp);
        }

        return output;
    }

    @Override
    public void save(String file) {
        if (header) {
            Files.delete(file);
            Files.appendText(file, getHeader());
        }

        List<VariantPrediction> pred = this.results.getAllPredictions();

        for (VariantPrediction v : pred) {
            
            List<String> lines = formatVariant(v);

            for (String line : lines) {
                Files.appendText(file, line);
            }
        }
    }

 
}
