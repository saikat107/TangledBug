/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.entropy.saikat;

/**
 *
 * @author sc2nf
 */
public class DataObject{
    private String snapShot;
    private String fileName;
    private int sourceLine;
    private int tokenLine;
    private int astType;
    private double entropy;
    private int isBug;
    private int bugDuration;
    private double zle;
    private double percentBugginess;
    private String bfSha;
    private String biSha;
    private int bf_add;
    private int bf_del;

    /**
     * @return the snapShot
     */
    public String getSnapShot() {
        return snapShot;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the sourceLine
     */
    public int getSourceLine() {
        return sourceLine;
    }

    /**
     * @return the tokenLine
     */
    public int getTokenLine() {
        return tokenLine;
    }

    /**
     * @return the astType
     */
    public int getAstType() {
        return astType;
    }

    /**
     * @return the entropy
     */
    public double getEntropy() {
        return entropy;
    }

    /**
     * @return the isBug
     */
    public int getIsBug() {
        return isBug;
    }

    /**
     * @return the bugDuration
     */
    public int getBugDuration() {
        return bugDuration;
    }

    /**
     * @return the zle
     */
    public double getZle() {
        return zle;
    }

    /**
     * @return the percentBugginess
     */
    public double getPercentBugginess() {
        return percentBugginess;
    }

    /**
     * @return the bfSha
     */
    public String getBfSha() {
        return bfSha;
    }

    /**
     * @return the biSha
     */
    public String getBiSha() {
        return biSha;
    }

    /**
     * @return the bf_add
     */
    public int getBf_add() {
        return bf_add;
    }

    /**
     * @return the bf_del
     */
    public int getBf_del() {
        return bf_del;
    }
    
    static class DataObjectBuilder{
        DataObject obj;
        public DataObjectBuilder(){
            obj = new DataObject();
        }
        public DataObjectBuilder snapShot(String sl){
            obj.snapShot = sl;
            return this;
        }
        public DataObjectBuilder fileName(String sl){
            obj.fileName = sl;
            return this;
        }
        public DataObjectBuilder sourceLine(int sl){
            obj.sourceLine = sl;
            return this;
        }
        public DataObjectBuilder tokenLine(int sl){
            obj.tokenLine = sl;
            return this;
        }
        public DataObjectBuilder astType(int sl){
            obj.astType = sl;
            return this;
        }
        public DataObjectBuilder entropy(double sl){
            obj.entropy = sl;
            return this;
        }
        public DataObjectBuilder isBug(int sl){
            obj.isBug = sl;
            return this;
        }
        public DataObjectBuilder bugDuration(int sl){
            obj.bugDuration = sl;
            return this;
        }
        public DataObjectBuilder zle(double sl){
            obj.zle = sl;
            return this;
        }
        public DataObjectBuilder percentBugginess(double sl){
            obj.percentBugginess = sl;
            return this;
        }
        public DataObjectBuilder bfSha(String sl){
            obj.bfSha = sl;
            return this;
        }
        public DataObjectBuilder biSha(String sl){
            obj.biSha = sl;
            return this;
        }
        public DataObjectBuilder bf_add(int sl){
            obj.bf_add = sl;
            return this;
        }
        public DataObjectBuilder bf_del(int sl){
            obj.bf_del = sl;
            return this;
        }
        public DataObject build(){
            return obj;
        }
    }
}
