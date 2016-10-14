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
                        totalEntropy += d.getEntropy();
                    }
                    double percentage = Configuration.percentageThreshold;
                    double thresHold = totalEntropy * percentage;
                    double zleSoFar = 0;
                    
                    for(int i = 0; i < changes.size(); i++){
                        zleSoFar += changes.get(i).getEntropy();
                        lineWriter.println(changes.get(i).getSourceLine() + "," + 
                                changes.get(i).getTokenLine());
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
                    snapShotMap = mainMap.get(object.getSnapShot());
            if(snapShotMap == null){
                snapShotMap = new HashMap<>();
            }
            HashMap<String, ArrayList<DataObject>> perCommitMap
                    = snapShotMap.get(object.getBfSha());
            if(perCommitMap == null){
                perCommitMap = new HashMap<>();
            }
            ArrayList<DataObject> perCommitPerFileList
                    = perCommitMap.get(object.getFileName());
            if(perCommitPerFileList == null){
                perCommitPerFileList = new ArrayList<>();
            }
            perCommitPerFileList.add(object);
            perCommitMap.put(object.getFileName(), perCommitPerFileList);
            snapShotMap.put(object.getBfSha(), perCommitMap);
            mainMap.put(object.getSnapShot(), snapShotMap);
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


