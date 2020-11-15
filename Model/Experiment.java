/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;


import java.util.*;

/**
 *
 * @author Amman Nega
 */
public class Experiment {
    private Map<Integer, BeadPlate> beadPlates;
    private int numSamples;
    
    public Experiment() {
        beadPlates = new HashMap<>();
        numSamples = 4;
    }
    
    public Experiment(HashMap<Integer, BeadPlate> plates, int samples) {
        beadPlates = plates;
        numSamples = samples;
    }
    
    public Map<Integer, BeadPlate> getBeadPlates() {
            return beadPlates;
    }
    
    public void setBeadPlates(Map<Integer, BeadPlate> beadPlates) {
        this.beadPlates = beadPlates;
    }
    
    // returns specified bead plate of experiment
    public BeadPlate getBeadPlate(int plateNum) {
        //return new BeadPlate(beadPlates.get(plateNum)); // avoids privacy leak
        return beadPlates.get(plateNum);
    }
    
    // sets specified bead plate of experiment
    public void addBeadPlate(int plateNum, BeadPlate plate) {
        beadPlates.put(plateNum, plate);
    }
    
    public int getSamples() {
        return numSamples;
    }
    
    public void setSamples(int samples) {
        this.numSamples = samples;
    }
    
    public int getNumPlates() {
        return beadPlates.size();
    }
    
    /*
    * add function that can add a specific sample from an experiment
    * parameter should be (String sampleName).
    * append to string array names in UserInputForBeadPlate for each plate
    * in the experiment.
    * also must increment numSamples.
    */
    
    /*
    * add function that can remove a sample from an experiment
    * no parameters ().
    * should simply remove the last sample from names array,
    * also must decrement numSamples.
    * CHECK IF numSamples = 1! prevent removal in this case.
    */
    
    // check if all bead plates probe lists are empty
    public boolean areProbeListsEmpty() {
        for(int i = 0; i < beadPlates.size(); i++) {
            if(!beadPlates.get(i).isProbeListEmpty()) {
                return false;
            }
        }
        
        return true;
    }
}
