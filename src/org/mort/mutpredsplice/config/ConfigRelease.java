/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mort.mutpredsplice.config;

import java.util.Properties;
import org.mort.config.PropertyLoader;

/**
 * Stores the MutPred Splice configuration info:
 *
 * @author wmgmm
 */
public class ConfigRelease {

    private int partionSize;
    private String tmpDir;
    private static final ConfigRelease instance = new ConfigRelease();

    /**
     * private to defeat instantiation.
     */
    private ConfigRelease() {
        init();
    }

    private void init() {
        Properties prop = PropertyLoader.loadProperties("org.mort.mutpredsplice.config.myRelease");
        //set mutpred splice properties
        this.partionSize = Integer.parseInt(prop.getProperty("partionSize", "10"));
        this.tmpDir = prop.getProperty("tmpDir", "/tmp/");
    }

    public int getPartionSize() {
        return partionSize;
    }

    public String getTmpDir() {
        return this.tmpDir;
    }

    public static ConfigRelease getInstance() {
        return instance;
    }
}
