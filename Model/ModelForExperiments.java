package Model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// to hold probe data on the homepage for each bead plates 
// pass and change data between homepage and add bead pages
/**
 *
 * @author feiping
 */

//TODO (if needed) : Get method of status 
public class ModelForExperiments {
    
    //defalut probes to two types of experiment. put them in the model so that user can save the change for future experiments. 
    List<List<String>> ProbesForExperimentType1 = new ArrayList<>();
    List<List<String>> defaultProbesForExperimentType2 = new ArrayList<>();
    
    
    private ObservableList<bead> analytes = FXCollections.observableArrayList();
    
    //initial probes list: used in AddBeadsPageController.java
    private ObservableList<probeTableData> probesToLoad = FXCollections.observableArrayList();
    // private final static Context instance = new Context();
    private final static ModelForExperiments instance = new ModelForExperiments();
    
    //probe data contains both bead class number(region number ) and analyte name.
    //private HashMap<Integer, HashMap<Integer,  ObservableList<bead>>> data = new HashMap<>(); 
    
    private HashMap<Integer, Experiment> experimentModel = new HashMap<>();
    
    private int currentExperiment;
    private int currentPlate;
    private int numberOfExperiments; // total number of experiments
    private List<String> fileNames = new ArrayList<>(); // xml files upload by users
    private Map<Integer, List<String>> XMLFileMap = new HashMap<>(); // xml files for each experiemnt, experiment nubmer is key.
    private String directory; //absolute  directory of xml files. 
    private ObservableList<Integer> experiments; // for choice box display # of experiments 
    
    /* Median value page 
    Median value Matrix data strucuture explanation.
    for each experiment and sample, HashTable<Integer, List<SampleData>> (key: number of experiment; value: list of sample data)
                   sample1  sample2
    experiment 1
    experiment 2
    
    Sampe data : List<List<List<HashMap<Integer,Double>>>>
    for each sample data : List<PlateData>
    for each Platedata List<probeData>
    for probe data: HashTable<Integer, double> (key: analyte region number, value: median value for that analyte)
               plate 1                        plate2         
               probe1    probe2   probe3      probe1     probe2    probe3
    Analyte1 
    Analyte2
    */
    HashMap<Integer, List<List<List<HashMap<Integer,Double>>>>> medianValueMatrix = new HashMap<>();
    
    // has orginal median value data for each wells. 
    //List<HashMap<Integer,Double>> contains orignal meidan value data of each wells 
    HashMap<Integer, List<List<List<List<HashMap<Integer,Double>>>>>> medianValueOriginalData = new HashMap<>(); 
    //HashMap<Integer,  List<HashMap<Integer, HashMap<Integer,  Double>>>> medianValueOriginalData = new HashMap<>(); 
  
    //orgianl data from xml files. HashMap<Integer,List<Integer> contains data from one plate/xml file. List<Integer> are the reporter data from xml file. 
    private HashMap<Integer, List< HashMap<Integer, HashMap<Integer,   List<Integer>>>>> orginalXMLData = new HashMap<>();
    private HashMap<Integer, List<List<List<HashMap<Integer,Double>>>>> ANCMatrix = new HashMap<>();
   
     // pass analyte, curPlate, curProbe when user click one median value to open a pop up page.
    private bead curAnalyte;
    private int curPlate =0;
    private int curProbe =0;
    private int curSample = 0;
    private int numberOfSamples = 0;
    private static String[] sampleNames;
    
    
    //Fold change Page
    private int largestSampleCount = 0; // the largest number of sample of all experiments. 
    private  HashMap<Integer, List<Integer>> mapOfSamplesNumbers = new HashMap<>(); // number of samples for each experiment
    
    public static ModelForExperiments getInstance() {
        return instance;
    }

    // read data from probes1.txt and save it into the ProbesForExperimentType1 list. 
    public void initializeProbesForExperimentType1() throws FileNotFoundException
    {
            // pass the path to the file as a parameter
        String currentLocation = System.getProperty("user.dir").toString();
        System.out.println("currLoc: "+ currentLocation);
        File file =new File( currentLocation +"/probes1.txt"); //tODO: Change File location and name
        Scanner sc = new Scanner(file); 
        while (sc.hasNextLine())
        {
            String str = sc.nextLine();
            List<String> probeList = Arrays.asList(str.split(","));
            ProbesForExperimentType1.add(probeList);
        }
        sc.close();
    }
    public void initializeProbesForPlate2() throws FileNotFoundException
    {
            // pass the path to the file as a parameter
        String currentLocation = System.getProperty("user.dir").toString();
        File file =new File( currentLocation +"/git probes2.txt"); //tODO: Change File location and name
        Scanner sc = new Scanner(file); 
        while (sc.hasNextLine())
        {
            String str = sc.nextLine();
            List<String> probeList = Arrays.asList(str.split(","));
            defaultProbesForExperimentType2.add(probeList);
        }
        sc.close();
    }
    
    public HashMap<Integer, Experiment> getExperimentModel() {
        //return new HashMap(experimentModel);
        return experimentModel;
    }
    
    public List<List<String>>  getProbesForPlate2() throws FileNotFoundException
    {
        if(defaultProbesForExperimentType2.isEmpty())
            initializeProbesForPlate2();
        return defaultProbesForExperimentType2;
    }
    
    public List<List<String>>  getProbesForExperimentType1() throws FileNotFoundException
    {
        if(ProbesForExperimentType1.isEmpty())
            initializeProbesForExperimentType1();
        return ProbesForExperimentType1;
    }
            
    public void setDirectory(String  directory)
    {
        this.directory = directory;
    }
    
    public String  getDirectory()
    {
        return directory;
    }
    
    public void setAnalytes(ObservableList<bead> analytes)
    {
        this.analytes = analytes;
    }
    
    public ObservableList<bead>  getAnalytes()
    {
        return analytes;
    }
    
    public int getCurPlate()
    {
        return currentPlate;
    }
    
    public void setCurPlate(Integer plate)
    {
        currentPlate = plate;
    }
    
    // method called in AddBeadsPageController.java
     public ObservableList<probeTableData>  getProbesForLoad( )
   {
       generateBeadsToLoad(); // helper method
       return probesToLoad;
   }
    
   public void setProbesForLoad(ObservableList<probeTableData> probesToLoad )
   {
       this.probesToLoad = probesToLoad;
   }
   
   // helper method for getProbesForLoad()
    private void generateBeadsToLoad()
    {
        probesToLoad.add(new probeTableData(1, "TCR"));
        probesToLoad.add(new probeTableData(2, "CD"));
        probesToLoad.add(new probeTableData(3, "LAT"));
        probesToLoad.add(new probeTableData(4, "ZAP"));
        probesToLoad.add(new probeTableData(5, "SLP"));
    }
    
   public void swap(int experiment, int index1, int index2) {
        String s1 = XMLFileMap.get(experiment).get(index1);
        String s2 = XMLFileMap.get(experiment).get(index2);
        XMLFileMap.get(experiment).set(index2, s1);
        XMLFileMap.get(experiment).set(index1, s2);  
    }
   
   public void setUserInputsForBeadPlateMap( HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>> userInputsForBeadPlateMap)
   {
       for(int i = 0; i < experimentModel.size(); i++ ) { // iterate through each experiment
           for(int j = 0; j < experimentModel.get(i + 1).getNumPlates(); j++) { // iterate through each bead plate
               try {
                    experimentModel.get(i + 1).getBeadPlate(j + 1).setPlateDetails(userInputsForBeadPlateMap.get(i + 1).get(j + 1));
               }
               catch(IndexOutOfBoundsException e) {
                   System.out.println("Input map doesn't match size of experiment model.");
               }
               finally {
                   break; // iterate to next experiment
               }
           }
       }
       //this.userInputsForBeadPlateMap = userInputsForBeadPlateMap;
       
   }
      
   public void setUserInputsForOneExperiment( int experiment, HashMap<Integer, UserInputForBeadPlate> input)
   {
       for(int i = 0; i < experimentModel.get(experiment).getNumPlates(); i++) {
           try {
                experimentModel.get(experiment).getBeadPlate(i + 1).setPlateDetails(input.get(i+ 1));
           }
           catch(IndexOutOfBoundsException e) {
                   System.out.println("Input map doesn't match size of experiment model.");
           }
           finally {
                   break; // iterate to next experiment
            }
        }
   }
   
    public HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>>  getUserInputsForBeadPlateMap( )
    {
        HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>> userInputsForBeadPlateMap = new HashMap<>();
        for(int i = 0; i < experimentModel.size(); i++) {
            HashMap<Integer, UserInputForBeadPlate> inputs = new HashMap<>();
            for(int j = 0; j < experimentModel.get(i + 1).getNumPlates(); j++) {
               inputs.put(j + 1, experimentModel.get(i + 1).getBeadPlate(j + 1).getPlateDetails());
            }
            userInputsForBeadPlateMap.put(i + 1, inputs);
        }
        return userInputsForBeadPlateMap;
    }
    
    public HashMap<Integer, UserInputForBeadPlate> getUserInputsForOneExperiment(int curExperiment) {
        HashMap<Integer, UserInputForBeadPlate> userInputsForExperiment = new HashMap<>();
        
        for(int i = 1; i <= experimentModel.get(curExperiment).getNumPlates(); i++) {
            userInputsForExperiment.put(i, experimentModel.get(curExperiment).getBeadPlate(i).getPlateDetails());
        }
        
        return userInputsForExperiment;
    }
    
    /*
    * method for initializing the experiment model.
    * should be called whenever the xml file map is 
    * altered, this is because we need to set the number
    * of experiments and plates in each experiment accordingly.
    */
    public void initializeProbeListForPopulate()
    {
        //if not empty clear it first. (for manually set up experiments) 
        if(!experimentModel.isEmpty()) {
           experimentModel.clear();
        }
        
        if(XMLFileMap.isEmpty()) {
            System.out.println("XML MAP IS EMPTY!!!!!!!!!!!!!!!!!!");
        }
       
        // for each (key, value) pair
        for(int i = 1; i <= numberOfExperiments; i++) {
            Experiment tmpExp = new Experiment();          
            for(int j = 0; j < XMLFileMap.get(i).size(); j++) {
                BeadPlate plate = new BeadPlate();
                tmpExp.addBeadPlate(j + 1, plate);
            }
            
            experimentModel.put(i, tmpExp); //add experiment to hash map
            
            // need to put a bead plate for each 
        }
    }
   
    public void setProbeListForPopulate(HashMap<Integer, HashMap<Integer, ObservableList<probeTableData>>> probeLists)
    {
        for(int i = 0; i < experimentModel.size(); i++) {
            for(int j = 0; j < experimentModel.get(i).getNumPlates(); j++) {
                try {
                    experimentModel.get(i + 1).getBeadPlate(j + 1).setPlateTable(probeLists.get(i + 1).get(j + 1));
                }
                catch(IndexOutOfBoundsException e) {
                   System.out.println("Input map doesn't match size of experiment model.");
                }
                finally {
                   break; // iterate to next experiment
                }
            }
        }
    }
    
    public void setProbeListForOnePlate(int experiment, int beadPlate, ObservableList<probeTableData> probes)
    {
        experimentModel.get(experiment).getBeadPlate(beadPlate).setPlateTable(probes);
    }
    
    public HashMap<Integer, HashMap<Integer, ObservableList<probeTableData>>> getProbeListForPopulate()
    {
        HashMap<Integer, HashMap<Integer, ObservableList<probeTableData>>> probesListForPopulate = new HashMap<>();
        for(int i = 0; i < experimentModel.size(); i++) {
            HashMap<Integer, ObservableList<probeTableData>> probes = new HashMap<>();
            for(int j = 0; j < experimentModel.get(i + 1).getNumPlates(); j++) {
                probes.put(j + 1, experimentModel.get(i + 1).getBeadPlate(j + 1).getPlateTable());
            }
            probesListForPopulate.put(i + 1, probes);
        }
        
        return probesListForPopulate;
    }

    public void setCurrentExperiment(Integer experiement) {
        currentExperiment = experiement;
    }
    
    public int getCurrentExperiment() {
         return currentExperiment;
    }
  
    //initialize the probe list for the experiment
    public void initializeProbeListMap(int experiment)
    {
        //probesListForPopulate.put(experiment, new HashMap<Integer, ObservableList<probeTableData>>());
        //List<String> s = XMLFileMap.get(experiment);
        //int size = s.size();
        for(int i = 1; i <= XMLFileMap.get(experiment).size();i++)
        {
            ObservableList<probeTableData> probes = FXCollections.observableArrayList();
            experimentModel.get(experiment).getBeadPlate(i).setPlateTable(probes);
        }
    }
    
    // add probe list to certain plate. 
    public void addOneProbeListForPopulate(int experiment, int plate, ObservableList<probeTableData> probes)
    {
        //probesListForPopulate.get(experiment).put(plate, probes);    
        experimentModel.get(experiment).getBeadPlate(plate).setPlateTable(probes);
    }
    
    public void addOneUserInputForPopulate(int experiment, int plate, UserInputForBeadPlate input) {
        experimentModel.get(experiment).getBeadPlate(plate).setPlateDetails(input);
    }
    
    // seems to be redundant having this method, getProbeListForPopulate() does the exact same thing
    public  HashMap<Integer, HashMap<Integer, ObservableList<probeTableData>>> getProbeMapForPopulate()
    {
        return getProbeListForPopulate();
        //return probesListForPopulate;
    }
    
    // get probe list for certain plate. 
    public  ObservableList<probeTableData> getProbeListForPopulate(int experiment, int plate)
    {
        //if(!probesListForPopulate.containsKey(experiment)  || !probesListForPopulate.get(experiment).containsKey(plate)) return null;
        //return probesListForPopulate.get(experiment).get(plate);
        if(!experimentModel.containsKey(experiment) || !experimentModel.get(experiment).getBeadPlates().containsKey(plate)) return null;
        
        return experimentModel.get(experiment).getBeadPlate(plate).getPlateTable();
    }
    
    public boolean isModelComplete() {
        for(int i = 1; i <= experimentModel.size(); i++) {
            if(experimentModel.get(i).isExpComplete)
                continue;
            else {
                return false; // all experiments are not complete
            } 
        }
        return true;
    }
    
    // for all xmlfiles user uploaded
    public void setXMLFiles(List<String> list)
    {
        fileNames = list;
    }
    //get all xml files user uploaded
    public List<String> getXMLFiles()
    {
        return fileNames;
    }
    
    public void setNumberOfExperiments(int n)
    {
        numberOfExperiments = n;
    }
    public int getNumberOfExperiments()
    {
        return numberOfExperiments;
    }
    
    public void setExperiments( ObservableList<Integer> experiments)
    {
        this.experiments = experiments;
    }
    
    public  ObservableList<Integer>  getExperiments( )
    {
        return experiments;
    }
    //put xml files map in to model 
    public void setExperimentsMap(Map<Integer, List<String>> map)
    {
        this.XMLFileMap = map;
    }
    
    // replace xml file list for one experiment
    public void setXMLfileListForOneExperiment(int experiment, List<String> list)
    {
        XMLFileMap.put(experiment, list);

    }
    public Map<Integer, List<String>>  getExperimentsXMLFileMap( )
    {
        return XMLFileMap;
    }
    
    public HashMap<Integer, List<List<List<HashMap<Integer,Double>>>>>    getMedianValueMatrix()
    {
        return medianValueMatrix;
    }
    
    public void setMedianValueMatrix(HashMap<Integer, List<List<List<HashMap<Integer,Double>>>>> medianValueMatrix)
    {
        this.medianValueMatrix = medianValueMatrix;
    }   

    //experiment starts from 1, sampleIndex, PlateIndex, probeIndex start from 0
    public  void setOneProbeDataForMedianValue(int experimentPos, int plateIndex,  int sampleIndex,  HashMap<Integer,Double> meidanValueDataForOneProbe)
    {
        // when median value matrix is empty, initialize it. 
        if(medianValueMatrix.isEmpty() || !medianValueMatrix.containsKey(experimentPos) ) 
        {
            List<List<List<HashMap<Integer,Double>>>> experiment= new ArrayList<>();
            medianValueMatrix.put(experimentPos, experiment);
        }
        if(medianValueMatrix.get(experimentPos).isEmpty() || medianValueMatrix.get(experimentPos).size() < (plateIndex+1))
        {
            List<List<HashMap<Integer,Double>>> plate = new ArrayList<>();
            medianValueMatrix.get(experimentPos).add(plate);
        }
        if(medianValueMatrix.get(experimentPos).get(plateIndex).isEmpty() || medianValueMatrix.get(experimentPos).get(plateIndex).size() < (sampleIndex+1))
        {
            List<HashMap<Integer,Double>> sample = new ArrayList<>();
            medianValueMatrix.get(experimentPos).get(plateIndex).add(sample);
        }
        //sample starts from 1. sampleIndex = sample -1;  //plate starts from 1. plateIndex = plate -1;
        medianValueMatrix.get(experimentPos).get(plateIndex).get(sampleIndex).add(meidanValueDataForOneProbe);
    }   
    
    public HashMap<Integer,Double> getOneProbeDataForMedianValue(int experimentPos, int plateIndex,  int sampleIndex, int probeIndex)
    {
        //System.out.println("matrix size: " + medianValueMatrix.get(experimentPos).size());
        return medianValueMatrix.get(experimentPos).get(plateIndex).get(sampleIndex).get(probeIndex);
    }

    
    public HashMap<Integer, List<List<List<List<HashMap<Integer,Double>>>>>> getMedianValueOriginalData ()
    {
        return medianValueOriginalData;
    }
    
    public void setMedianValueOriginalData(HashMap<Integer, List<List<List<List<HashMap<Integer,Double>>>>>> medianValueOriginalData)
    {
        this.medianValueOriginalData = medianValueOriginalData;
    }   

    //experiment starts from 1, sampleIndex, PlateIndex, probeIndex start from 0
    public  void setOneProbeDataForMedianValueOriginalData(int experimentPos, int plateIndex,  int sampleIndex, 
                         List< HashMap<Integer,  Double>>  OriginalDataForOneProbe)
    {
        //    HashMap<Integer, List<List<List<List<HashMap<Integer,Double>>>>>> medianValueOriginalData = new HashMap<>(); 
        // when median value matrix is empty, initialize it. 
        if(medianValueOriginalData.isEmpty() || !medianValueOriginalData.containsKey(experimentPos))
        {
            List<List<List<List<HashMap<Integer,Double>>>>> experiment= new ArrayList<>();
            medianValueOriginalData.put(experimentPos, experiment);
        }
        if(medianValueOriginalData.get(experimentPos).isEmpty() || medianValueOriginalData.get(experimentPos).size() < (plateIndex+1))
        {
            List<List<List<HashMap<Integer,Double>>>> plate = new ArrayList<>();
            medianValueOriginalData.get(experimentPos).add(plate);
        }
        if(medianValueOriginalData.get(experimentPos).get(plateIndex).isEmpty() || 
                medianValueOriginalData.get(experimentPos).get(plateIndex).size() < (sampleIndex+1))
        {
            List<List<HashMap<Integer,Double>>> sample = new ArrayList<>();
            medianValueOriginalData.get(experimentPos).get(plateIndex).add(sample);
        }
        /*
        if(medianValueOriginalData.get(experimentPos).get(plateIndex).get(sampleIndex).isEmpty() ||
           medianValueOriginalData.get(experimentPos).get(plateIndex).get(sampleIndex).size() < (probeIndex +1 ) )
        {
            List<HashMap<Integer,Double>> probe = new ArrayList<>();
            medianValueOriginalData.get(experimentPos).get(plateIndex).get(sampleIndex).add(probe);
        }*/
        //sample starts from 1. sampleIndex = sample -1;  //plate starts from 1. plateIndex = plate -1;
        medianValueOriginalData.get(experimentPos).get(plateIndex).get(sampleIndex).add(OriginalDataForOneProbe);
    }   
    
    public List< HashMap<Integer,  Double>> getOneProbeDataForOrignalMedianValue(int experimentPos, int plateIndex,  int sampleIndex, int probeIndex)
    {
        return medianValueOriginalData.get(experimentPos).get(plateIndex).get(sampleIndex).get(probeIndex);
    }    
    
    public bead getCurAnalyte()
    {
        return curAnalyte;
    }
    
    public void setCurAnalyte(bead curAnalyte)
    {
        this.curAnalyte = curAnalyte;
    }

        
    public int getCurProbe()
    {
        return curProbe;
    }
    
    public void setCurProbe(int curProbe)
    {
        this.curProbe = curProbe;
    }

    public int getNumberOfSamples()
    {
        return numberOfSamples;
    }
    
    public void setNumberOfSamples(int numberOfSamples)
    {
        this.numberOfSamples = numberOfSamples;
    }
    
    public int getCurSample()
    {
        return curSample;
    }
    
    public void setCurSample(int curSample)
    {
        this.curSample = curSample;
    }
    
    
    public String[]  getSampleNames()
    {
        return sampleNames;
    }
    
    public void setSampleNames(String[] sampleNames)
    {
        this.sampleNames = sampleNames;
    }
    
    public int getLargestSampleCount()
    {
        return largestSampleCount;
    }
    
    public void setLargestSampleCount(int largestSampleCount)
    {
        this.largestSampleCount = largestSampleCount;
    }       
    
    public  HashMap<Integer, List<Integer>> getMapOfSamplesNumbers()
    {
        return mapOfSamplesNumbers;
    }
    
    public void setMapOfSamplesNumbers(HashMap<Integer, List<Integer>> mapOfSamplesNumbers )
    {
        this.mapOfSamplesNumbers = mapOfSamplesNumbers;
    }   
    
    public HashMap<Integer, List< HashMap<Integer, HashMap<Integer,   List<Integer>>>>>  getOriginalXMLData()
    {
        return orginalXMLData;
    }
    
    public void setOriginalXMLData(HashMap<Integer, List< HashMap<Integer, HashMap<Integer,  List<Integer>>>>>   map)
    {
        orginalXMLData = map;
    }
    
    public void setOriginalXMLDataForOnePlate( HashMap<Integer, HashMap<Integer,   List<Integer>>> orginalDataForOnePlate, int experimentPos)
    {
        if(orginalXMLData.isEmpty() ||  !orginalXMLData.containsKey(experimentPos))
        {
            List< HashMap<Integer, HashMap<Integer,   List<Integer>>>> plate = new ArrayList<>();
            orginalXMLData.put(experimentPos, plate);
        }
            orginalXMLData.get(experimentPos).add(orginalDataForOnePlate);
        
    }
    
    public List< HashMap<Integer, HashMap<Integer,   List<Integer>>>>  getOriginalXMLDataForOneExperiment( int experimentPos)
    {
        return orginalXMLData.get(experimentPos);
    }
    
    public HashMap<Integer, List<List<List<HashMap<Integer,Double>>>>>    getANCMatrix()
    {
        return ANCMatrix;
    }   
    public void setANCMatrix(HashMap<Integer, List<List<List<HashMap<Integer,Double>>>>> ANCMatrix)
    {
        this.ANCMatrix = ANCMatrix;
    }   
    public  void setOneProbeDataForANC(int experimentPos, int plateIndex,  int sampleIndex,  HashMap<Integer,Double> ANCForOneProbe)
    {
        // when median value matrix is empty, initialize it. 
        if(ANCMatrix.isEmpty() || !ANCMatrix.containsKey(experimentPos) ) 
        {
            List<List<List<HashMap<Integer,Double>>>> experiment= new ArrayList<>();
            ANCMatrix.put(experimentPos, experiment);
        }
        if(ANCMatrix.get(experimentPos).isEmpty() || ANCMatrix.get(experimentPos).size() < (plateIndex+1))
        {
            List<List<HashMap<Integer,Double>>> plate = new ArrayList<>();
            ANCMatrix.get(experimentPos).add(plate);
        }
        if(ANCMatrix.get(experimentPos).get(plateIndex).isEmpty() || ANCMatrix.get(experimentPos).get(plateIndex).size() < (sampleIndex+1))
        {
            List<HashMap<Integer,Double>> sample = new ArrayList<>();
            ANCMatrix.get(experimentPos).get(plateIndex).add(sample);
        }
        //sample starts from 1. sampleIndex = sample -1;  //plate starts from 1. plateIndex = plate -1;
        ANCMatrix.get(experimentPos).get(plateIndex).get(sampleIndex).add(ANCForOneProbe);
    }   
    
    public HashMap<Integer,Double> getOneProbeDataForANC(int experimentPos, int plateIndex,  int sampleIndex, int probeIndex)
    {
        return ANCMatrix.get(experimentPos).get(plateIndex).get(sampleIndex).get(probeIndex);
    }

    private HashMap<Integer,List<List<HashMap<Integer,Double>>>> getfoldChange = new HashMap<>();
    public void setFoldChangeMatrixforANC(int pos, List<List<HashMap<Integer,Double>>>  ANCMatrix)
    {
        if(!getfoldChange.containsKey(pos)){
            this.getfoldChange.put(pos, ANCMatrix);
        }
    }  
    public HashMap<Integer,List<List<HashMap<Integer,Double>>>> getfoldChangeforANC()
    {
        return getfoldChange;
    }
    
    public List<List<String>> getPlate1(){
        return ProbesForExperimentType1;
    }
    public List<List<String>> getPlate2(){
        return defaultProbesForExperimentType2;
    }
}