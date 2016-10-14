/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.entropy.saikat;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author sc2nf
 */
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
                if (data.get(d).getZle() < data.get(d + 1).getZle()) {
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
                if (data.get(d).getEntropy() < data.get(d + 1).getEntropy()) {
                    DataObject swap = data.get(d);
                    data.set(d, data.get(d + 1));
                    data.set(d + 1, swap);
                }
            }
        }
        return data;
    }
    
}
