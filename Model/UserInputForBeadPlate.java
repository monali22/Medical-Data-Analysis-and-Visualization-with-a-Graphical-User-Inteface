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
 * This class represents a single bead plate details.
 */
public class UserInputForBeadPlate {
    //private int  numberOfSamples;
    private  int numberOfReplicas;
    private String namesInput;
    private String[] names;
    private  int numberOfProbes;
    private  List<String> probeList;

    public UserInputForBeadPlate() {
        numberOfReplicas = 2;
        namesInput = "WC, WK, KC, KK";
        names = namesInput.split(",");
        numberOfProbes = 10;
        probeList = new ArrayList<>(numberOfProbes);
    }
    
    public  UserInputForBeadPlate(int replicas, String namesInput, String[] names, 
            int probes, List<String> probeList)
    {
        super();
        //this.numberOfSamples=samples;
        this.numberOfReplicas=replicas;
        this.namesInput=namesInput;
        this.names=names;
        this.numberOfProbes=probes;
        this.probeList=probeList;
                
    }
    
    public UserInputForBeadPlate(UserInputForBeadPlate copy) {
        super();
        //this.numberOfSamples= copy.numberOfSamples;
        this.numberOfReplicas= copy.numberOfReplicas;
        this.namesInput= copy.namesInput;
        this.names= copy.names;
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
    public String getNameInput()
    {
        return this.namesInput;
    }
    public String[] getNames()
    {
        return this.names;
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
    
    @Override
    public String toString() {
        String inputInfo = "Number of Samples: " /*+ numberOfSamples*/;
        inputInfo += "\n Number of Replicas: " + numberOfReplicas;
        inputInfo += "\n Number of Probes: " + numberOfProbes;
        inputInfo += "\n Sample Names: " + namesInput;
        
        return inputInfo;
    }
}
