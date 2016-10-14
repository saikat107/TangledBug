/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.entropy.saikat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import edu.virginia.cs.entropy.saikat.DataObject.*;

/**
 *
 * @author sc2nf
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        ArrayList<DataObject> objectList = new ArrayList<>();
        readInputFile(objectList);

        HashMap<String , 
                HashMap<String , 
                        HashMap<String , 
                                ArrayList<DataObject>
                                >
                        >
                > mainMap = new HashMap<>();
        
        createDatabase(objectList, mainMap);
        
        
        Set<String> snapShots = mainMap.keySet();
        for(String snapShot : snapShots){
            File snapShotDir = new File("Output\\" + Configuration.projectName + "\\" +snapShot);
            if(!snapShotDir.exists()) {
                snapShotDir.mkdir();
            }       

            PrintWriter lineWriter = new PrintWriter(
                new File(snapShotDir.getAbsolutePath() + 
                        "\\filtered_zle_lineNo.csv"));
            lineWriter.println("Source Line,Token Line, File Name, bf_Sha");
            
            PrintWriter pr = new PrintWriter(
                new File(snapShotDir.getAbsolutePath() + "\\filtered_zle.csv"));
        
            pr.println(
                "FileName,Number Of Lines,Percentage,"
                        + "Average,Median,Standard Deviation");
            
            HashMap<String, HashMap<String , ArrayList<DataObject>>> 
                    perSnapShotMap = mainMap.get(snapShot);
            
            Set<String> bfShaSet = perSnapShotMap.keySet();
            
            //System.out.println(mainMap.get(snapShot).get("256286bb3548aae3b72eec996bf2f0f4e99482b6"));
            for(String bfSha : bfShaSet){
                HashMap<String , ArrayList<DataObject>> 
                        perSnapShotPerBFShaMap = perSnapShotMap.get(bfSha);
                
                Set<String> fileSet = perSnapShotPerBFShaMap.keySet();
                
                ArrayList<Double> percentageList = new ArrayList<Double>();               
                for(String fileName : fileSet){
                    
                    ArrayList<DataObject> 
                            changes = perSnapShotPerBFShaMap.get(fileName);
                    
                    Util.sortBasedOnentropy(changes);
                    
                    double totalEntropy = 0;
                    for(DataObject d: changes){
                        totalEntropy += d.entropy;
                    }
                    
                    double percentage = Configuration.percentageThreshold;
                    double thresHold = totalEntropy * percentage;
                    double zleSoFar = 0;
                    
                    for(int i = 0; i < changes.size(); i++){
                        zleSoFar += changes.get(i).entropy;
                        lineWriter.println(changes.get(i).sourceLine + "," + 
                                changes.get(i).tokenLine);
                        if(zleSoFar >= thresHold){
                            
                            pr.println(fileName + "," + changes.size() + "," + 
                                    (i+1.00)/changes.size());
                            percentageList.add((i + 1.00) / changes.size());
                            break;
                        }
                    }
                    lineWriter.println(",," + fileName);
                }
                double average = Util.getAverage(percentageList);
                double sd = Util.getStandardDeviation(percentageList);
                double median = Util.getMedian(percentageList);
                
                pr.println(bfSha+",,," + average + "," + median + "," + sd + "\n\n");
                lineWriter.println(",,," + bfSha + "\n\n");
            }
            pr.close();
            lineWriter.close();
        }
        
        
        
        
    }

    private static void createDatabase(
            ArrayList<DataObject> objectList, 
            HashMap<String, 
                    HashMap<String, 
                            HashMap<String, 
                                    ArrayList<DataObject>
                                   >
                            >
                    > mainMap) {
        for(int i = 0; i < objectList.size(); i++){
            DataObject object = objectList.get(i);
            HashMap<String, HashMap<String, ArrayList<DataObject>>>
                    snapShotMap = mainMap.get(object.snapShot);
            if(snapShotMap == null){
                snapShotMap = new HashMap<>();
            }
            HashMap<String, ArrayList<DataObject>> perCommitMap
                    = snapShotMap.get(object.bfSha);
            if(perCommitMap == null){
                perCommitMap = new HashMap<>();
            }
            ArrayList<DataObject> perCommitPerFileList
                    = perCommitMap.get(object.fileName);
            if(perCommitPerFileList == null){
                perCommitPerFileList = new ArrayList<>();
            }
            perCommitPerFileList.add(object);
            perCommitMap.put(object.fileName, perCommitPerFileList);
            snapShotMap.put(object.bfSha, perCommitMap);
            mainMap.put(object.snapShot, snapShotMap);
        }
    }

    private static void readInputFile(
            ArrayList<DataObject> objectList) 
            throws NumberFormatException, FileNotFoundException {
        Scanner atScan = new Scanner(
                new File(Configuration.inputFilePath));
        atScan.nextLine();
        while(atScan.hasNextLine()){
            String line = atScan.nextLine();
            StringTokenizer tok = new StringTokenizer(line,",");
            tok.nextToken();
            
            DataObject.DataObjectBuilder builder;
            builder = new DataObject.DataObjectBuilder();
            builder = builder.snapShot(tok.nextToken())
                    .fileName(tok.nextToken())
                    .sourceLine(Integer.parseInt(tok.nextToken()))
                    .tokenLine(Integer.parseInt(tok.nextToken()))
                    .astType(Integer.parseInt(tok.nextToken()))
                    .entropy(Double.parseDouble(tok.nextToken()));
            int isBug = Integer.parseInt(tok.nextToken());
            if(isBug == 0) continue;
            builder = builder.isBug(isBug)
                    .bugDuration(Integer.parseInt(tok.nextToken()))
                    .zle(Double.parseDouble(tok.nextToken()))
                    .percentBugginess(Double.parseDouble(tok.nextToken()))
                    .bfSha(tok.nextToken())
                    .biSha(tok.nextToken())
                    .bf_add(Integer.parseInt(tok.nextToken()))
                    .bf_del(Integer.parseInt(tok.nextToken()));
            objectList.add(builder.build());
        }
    }
    
    

}

class Util{

    static double getAverage(ArrayList<Double> percentageList) {
        double total = 0;
        for (Double d : percentageList) {
            total += d;
        }
        return total / percentageList.size();
    }

    static double getStandardDeviation(ArrayList<Double> percentageList) {
        double average = getAverage(percentageList);
        double total = 0;
        for (Double d : percentageList) {
            total += (d - average) * (d - average);
        }
        double sd = Math.sqrt(total / percentageList.size());
        return sd;
    }

    private static int[][] getArrayOfPair(ArrayList<pair> p) {
        int[][] ret = new int[p.size()][2];
        for (int i = 0; i < p.size(); i++) {
            ret[i][0] = p.get(i).totalLine;
            ret[i][1] = p.get(i).tLine;
        }
        return ret;
    }

    static double getMedian(ArrayList<Double> percentageList) {
        Object[] percentages = percentageList.toArray();
        Arrays.sort(percentages);
        if (percentages.length == 0) {
            return -1;
        }
        if (percentages.length % 2 == 0) {
            double a1 = (Double) percentages[percentages.length / 2];
            double a2 = (Double) percentages[percentages.length / 2 - 1];
            return (a1 + a2) / 2;
        } else {
            int idx = percentages.length / 2;
            return (Double) percentages[idx];
        }
    }

    static ArrayList<DataObject> sortBasedOnZle(ArrayList<DataObject> data) {
        int n = data.size();
        for (int c = 0; c < (n - 1); c++) {
            for (int d = 0; d < n - c - 1; d++) {
                if (data.get(d).zle < data.get(d + 1).zle) {
                    DataObject swap = data.get(d);
                    data.set(d, data.get(d + 1));
                    data.set(d + 1, swap);
                }
            }
        }
        return data;
    }

    static ArrayList<DataObject> sortBasedOnentropy(ArrayList<DataObject> data) {
        int n = data.size();
        for (int c = 0; c < (n - 1); c++) {
            for (int d = 0; d < n - c - 1; d++) {
                if (data.get(d).entropy < data.get(d + 1).entropy) {
                    DataObject swap = data.get(d);
                    data.set(d, data.get(d + 1));
                    data.set(d + 1, swap);
                }
            }
        }
        return data;
    }
    
}
class pair{
    int totalLine;
    int tLine;
    public pair(int to,int tl){
        totalLine = to;
        tLine = tl;
    }
}

class DataObject{
    String snapShot;
    String fileName;
    int sourceLine;
    int tokenLine;
    int astType;
    double entropy;
    int isBug;
    int bugDuration;
    double zle;
    double percentBugginess;
    String bfSha;
    String biSha;
    int bf_add;
    int bf_del;
    
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
