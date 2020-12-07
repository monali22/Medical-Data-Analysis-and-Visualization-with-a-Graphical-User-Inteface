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
    private String namesInput;
    private String[] names;
    public boolean isExpComplete; // used to check if plate data is stored in ModelForExperiments.java
    
    public Experiment() {
        beadPlates = new HashMap<>();
        namesInput = "WC, WK, KC, KK";
        names = namesInput.split(",");
        numSamples = names.length;
        isExpComplete = false;
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
    
    public void setSamples(String samples) {
        this.namesInput = samples;
        names = namesInput.split(",");
        numSamples = names.length;
    }
    
    public String getNameInput()
    {
        return this.namesInput;
    }
    public String[] getNames()
    {
        return this.names;
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
        for(int i = 1; i <= beadPlates.size(); i++) {
            if(!beadPlates.get(i).isProbeListEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /*
    * method checks if all bead plates data in this experiment
    * have been added to the ModelForExperiments.java.
    * If so, we set this experiments boolean value to true,
    * otherwise set to false.
    */
    public void isExperimentComplete() {
        for(int i = 1; i <= beadPlates.size(); i++) {
            if(beadPlates.get(i).isPlateComplete)
                continue;
            else {
                isExpComplete = false;
                return;
            }
        }
        isExpComplete = true;
    }
}
