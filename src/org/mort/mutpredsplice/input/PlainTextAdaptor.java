/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import mps.common.DatasetFactory;
import org.mort.common.Files;
import org.mort.compbio.DNATools;
import org.mort.compbio.GenomicSub;
import org.mort.genome.Chromosome;
import org.mort.genome.Coordinate;
import org.mort.mutpredsplice.config.ConfigRelease;

/**
 * Loads a plain text file in this format:
 * 1,chr5,70247787,+,A,T,NM_000344.3,SMN1 RefSeq may or may not be present
 *
 * @author wmgmm
 */
public class PlainTextAdaptor extends Adaptor {

    private final String input;
    private final String outputDir;
    private final String myUUID;

    public PlainTextAdaptor(String input, String outputDir) {
        this.input = input;
        this.outputDir = outputDir;
        this.myUUID = String.valueOf(UUID.randomUUID());
        check();
    }

    public final void check() {

        if (!Files.fileExists(input)) {
            throw new IllegalArgumentException("Input file does not exist:" + input);
        } else if (!Files.fileExists(outputDir)) {
            throw new IllegalArgumentException("Output dir does not exist:" + outputDir);
        } else if (!Files.isFileDir(outputDir)) {
            throw new IllegalArgumentException("Output dir is not a directory!:" + outputDir);
        }
    }

    @Override
    public void cleanUp() {
        Adaptor.cleanInputFiles(myUUID, outputDir);
    }

    @Override
    public List<String> getInputFiles() {
        return Adaptor.getAllInputFiles(myUUID, outputDir);
    }

    /**
     * Partion the data into by chromosome with a fixed size
     * 1,chr5,70247787,+,A,T,NM_000344.3,SMN1
     *
     * @throws IOException
     */
    @Override
    public void partion() throws IOException {

        List<GenomicSub> outputIn = new ArrayList<>();

        List<String> myFile = Files.fileToList(input);

        for (int i = 0; i < myFile.size(); i++) {

            String[] t = myFile.get(i).split(",");

            GenomicSub gs = new GenomicSub(t[0], "[" + t[4] + "/" + t[5] + "]");
            //System.out.println(myFile.get(i));

            gs.setStart(new Coordinate(t[1] + "," + t[2] + "," + t[3]));

            if (t[6].startsWith("NM_")) {
                String refseq = t[6];
                gs.setRefSeq(refseq);
            }

            System.out.println(gs);
            outputIn.add(gs);
        }

        Collections.sort(outputIn); //sort it so partioner works

        int count = 1; //partion 
        Chromosome currentChr = null;

        List<GenomicSub> output = new ArrayList<>();

        for (GenomicSub v : outputIn) {

            if (currentChr == null) {
                currentChr = v.getChr();
            }

            String ref = v.getWildtypeVariation();
            String alt = v.getMutantVariation();

            if (ref.length() != 1 || alt.length() != 1) {
                continue;
            }

            if (!DNATools.isValidDNA(ref) || !DNATools.isValidDNA(alt)) {
                continue;
            }

            //where we add
            //is on the same chromosome or exceeds the max outlined
            if (currentChr.equals(v.getChr()) && output.size() < ConfigRelease.getInstance().getPartionSize()) {
                output.add(v);

            } else { //update chr 
                //dump the partion to disk
                //then update the chr
                //add this variant

                String outputFile = outputDir + Adaptor.getInputFileName(myUUID, currentChr, count);
                // System.out.println("Saving variant to partion "+count+" "+outputFile);
                DatasetFactory.saveDatasetPlainTxtForSubs(output, outputFile);
                //now update
                count++;
                currentChr = v.getChr();
                output.clear();
                //now add for the next lot
                output.add(v);
            }

        }
        if (!output.isEmpty()) {
            String outputFile = outputDir + Adaptor.getInputFileName(myUUID, currentChr, count);
            DatasetFactory.saveDatasetPlainTxtForSubs(output, outputFile);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String input = "";
        String outputDir = "";
        PlainTextAdaptor v = new PlainTextAdaptor(input, outputDir);
        v.partion();
        v.cleanUp();
    }
}
