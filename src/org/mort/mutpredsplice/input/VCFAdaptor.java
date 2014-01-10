/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mps.common.DatasetFactory;
import org.mort.common.Files;
import org.mort.compbio.DNATools;
import org.mort.compbio.GenomicSub;
import org.mort.genome.Chromosome;
import org.mort.genome.vcf.VCF;
import org.mort.genome.vcf.VCFReader;
import org.mort.genome.vcf.VCFReaderInMemory;
import org.mort.genome.vcf.VariantVCF;
import org.mort.mutpredsplice.config.ConfigRelease;

/**
 * Loads a VCF and partions it into small jobs
 *
 * @author wmgmm
 */
public class VCFAdaptor extends Adaptor {

    private final String input;
    private final String outputDir;
    private final String myUUID;

    public VCFAdaptor(String input, String outputDir) {
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
    public void cleanUp(){
        Adaptor.cleanInputFiles(myUUID, outputDir);
    }
    
    @Override
    public List<String> getInputFiles(){
        return Adaptor.getAllInputFiles(myUUID, outputDir);
    }
    
    /**
     * Partion the data into by chromosome with a fixed size
     *
     * @throws IOException
     */
    @Override
    public void partion() throws IOException {
        VCFReader reader = new VCFReaderInMemory();
        VCF data = reader.parseVCF(input);

        List<GenomicSub> output = new ArrayList<>();

        int mutCount=0;
        int count = 1; //partion 
        Chromosome currentChr = null;
        for (VariantVCF v : data.getVariants()) {

            if (currentChr == null) {
                currentChr = v.getCoord().getChr();
            }

            String ref = v.getRef();
            String alt = v.getAlt();

            if (ref.length() != 1 || alt.length() != 1) {
                continue;
            }

            if (!DNATools.isValidDNA(ref) || !DNATools.isValidDNA(alt)) {
                continue;
            }

            mutCount++;
            //where we add
            //is on the same chromosome or exceeds the max outlined
            if(currentChr.equals(v.getCoord().getChr()) && output.size() < ConfigRelease.getInstance().getPartionSize()){
                output.add(v.asSub());
              //  System.out.println("Adding variant to partion "+count);
            }else { //update chr 
                //dump the partion to disk
                //then update the chr
                //add this variant
               
                String outputFile = outputDir+Adaptor.getInputFileName(myUUID, currentChr, count);
               //  System.out.println("Saving variant to partion "+count+" "+outputFile);
                
                DatasetFactory.saveDatasetPlainTxtForSubs(output,outputFile);
                
                //now update
                count++;
                currentChr = v.getCoord().getChr();
                output.clear();
                //now add for the next lot
                output.add(v.asSub());
            }
    
        }
         if(!output.isEmpty()){           
                String outputFile = outputDir+Adaptor.getInputFileName(myUUID, currentChr, count);
                
                 DatasetFactory.saveDatasetPlainTxtForSubs(output,outputFile);
            }
        
        System.out.println(mutCount+" Substitutions found");
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String input = "";
        String outputDir = "";
        VCFAdaptor v = new VCFAdaptor(input,outputDir);
        v.partion();
        v.cleanUp();
    }
}
