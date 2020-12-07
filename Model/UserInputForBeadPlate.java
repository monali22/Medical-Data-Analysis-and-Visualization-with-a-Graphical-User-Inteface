/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * This class represents a single bead plate's details.
 */
public class UserInputForBeadPlate {
    //private int  numberOfSamples;
    private  int numberOfReplicas;
    private  int numberOfProbes;
    private  List<String> probeList;

    /*
    * constructor initializes instance variables to its default values.
    * these default values are really arbitrary as long the product of
    * replicas, probes and experiment samples don't exceed 96.
    */
    public UserInputForBeadPlate() {
        numberOfReplicas = 2;
        numberOfProbes = 10;
        probeList = new ArrayList<>(numberOfProbes);
    }
    
    public UserInputForBeadPlate(int replicas, int probes, List<String> probeList)
    {
        super();
        //this.numberOfSamples=samples;
        this.numberOfReplicas=replicas;
        this.numberOfProbes=probes;
        this.probeList=probeList;
                
    }
    
    public UserInputForBeadPlate(UserInputForBeadPlate copy) {
        super();
        //this.numberOfSamples= copy.numberOfSamples;
        this.numberOfReplicas= copy.numberOfReplicas;
        this.numberOfProbes= copy.numberOfProbes;
        this.probeList= copy.probeList;
    }
    
    public void updateProbe(String newProbe)
    {
        this.probeList.add(newProbe);       
    }
    
    /*
    public int getNumOfSamples()
    {
        return this.numberOfSamples;
    }
    */
    
    public int getNumOfReplicas()
    {
        return this.numberOfReplicas;
    }
    
    public int getNumOfProbes()
    {
        return this.numberOfProbes;
    }
    
    public  List<String> getProbeList()
    {
        return this.probeList;
    }
    
    public void updateProbeList( List<String> probeList)
    {
        this.probeList=probeList;
    }
    
    /*
    * toString() method for debugging purposes
    */
    @Override
    public String toString() {
        String inputInfo = "Number of Samples: " /*+ numberOfSamples*/;
        inputInfo += "\n Number of Replicas: " + numberOfReplicas;
        inputInfo += "\n Number of Probes: " + numberOfProbes;
        
        return inputInfo;
    }
}
