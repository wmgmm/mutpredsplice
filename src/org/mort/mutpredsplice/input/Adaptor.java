/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.mort.common.FileStartsWithFilter;
import org.mort.genome.Chromosome;

/**
 * Base adaptor to format input fields
 *
 * @author wmgmm
 */
public abstract class Adaptor {

    private final static String SEP = "_";
    private final static String INPUT = "INPUT";
    private final static String OUTPUT = "OUTPUT";
    private final static String EXT = ".tsv";

    public abstract void cleanUp();

    public abstract List<String> getInputFiles();

    public abstract void partion() throws IOException;

    public static String getInputFileName(String uuid, Chromosome chr, int num) {

        return uuid + SEP + INPUT + SEP + chr.name() + SEP + num + EXT;
    }

    public static String getOutputFileName(String uuid, Chromosome chr, int num) {

        return uuid + SEP + OUTPUT + SEP + chr.name() + SEP + num + EXT;
    }

    public static void cleanInputFiles(String uuid, String tmpDir) {

        FileStartsWithFilter f = new FileStartsWithFilter(uuid + SEP + INPUT + SEP);

        File dir = new File(tmpDir);

        for (File s : dir.listFiles(f)) {
            
            s.delete();
        }

    }

    public static List<String> getAllInputFiles(String uuid, String tmpDir) {

        FileStartsWithFilter f = new FileStartsWithFilter(uuid + SEP + INPUT + SEP);

        List<String> allFiles = new ArrayList<>();
        File dir = new File(tmpDir);

        for (File s : dir.listFiles(f)) {

            allFiles.add(s.getAbsolutePath());
        }
        return allFiles;


    }

    public static void cleanOutputFiles(String uuid, String tmpDir) {

        FileStartsWithFilter f = new FileStartsWithFilter(uuid + SEP + OUTPUT + SEP);

        File dir = new File(tmpDir);

        for (File s : dir.listFiles(f)) {
           
            s.delete();
        }

    }
    
}
