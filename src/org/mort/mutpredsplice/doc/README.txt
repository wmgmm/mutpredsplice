============================================================================
 Please cite the MutPred Splice paper, if you use our tool in your research.
============================================================================

============================================================================
 News
============================================================================
MutPred Splice Version 1.3 released
Updated to latest RefSeq annotation set (GRCh37p10).

============================================================================
 Contact
============================================================================
Any problems or help, please contact wmgmm123 [at] gmail [dot] com OR mortm [at] cf.ac.uk

============================================================================
 Availability
============================================================================
1, On the web at http://mutdb.org/mutpredsplice  
2, Local install (http://mutdb.org/mutpredsplice/download.htm.) 
3, Precomputed MutPred Splice Predictions (available soon)

============================================================================
 Hardware Requirements
============================================================================
~350GB of disk space
6GB of RAM

============================================================================
 Software Requirements for local installation (option 2):
============================================================================
MySQL database server (v5 or above)
Java 1.7 JRE
Linux (tested on Ubuntu, CentOS)
Perl
R and RandomForest package 

============================================================================
 Third Party Data Dependencies
============================================================================
Human Reference hg19 fasta files
Phastcon (phastCons46way.placental.wigFix)
PhylopP  (phyloP46way.placental.wigFix)

============================================================================
 Directory structure overview
============================================================================
mutpred_splice_install/
                       fasta/   Put fasta files here
                       mysql/   Restore SQL data file from here
                       dist/    Start java app from here

============================================================================
 Running MutPred Splice, processing a VCF file
============================================================================

Navigate to dist directory:
java -Xmx6g -jar MutPredSpliceRelease.jar InputVCF outputTSV

e.g. 
java -Xmx6g -jar MutPredSpliceRelease.jar ../test_data/cosmic_sample.vcf ../TestResults.tsv
java -Xmx6g -jar MutPredSpliceRelease.jar ../test_data/test_case.vcf ../TestResults.tsv


============================================================================
 INSTALLATION
============================================================================

============================================================================
 MySQL set-up
============================================================================

Create mutpred_splice database;
Create user mutpred_user with select,insert and update privileges on mutpred_splice database
GRANT SELECT, INSERT, DELETE ON mutpred_splice.* TO mutpreduser@localhost IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
Restore the file mysql/mutpred_splice_tables.sql

============================================================================
 Third Party Data set-up
============================================================================
Into the fasta directory copy the hg19 fasta files, files should have "chr" prefix and a "fa" extension. e.g. chr18.fa
Download the 46 placental mamamals Phylop and PhastCons e.g. PhastCons46wayPlacentalchr10.phastCons46way.placental.wigFix  chr16.phyloP46way.placental.wigFix
Format: chr1	880902	0.25 

