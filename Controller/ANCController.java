/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.ModelForExperiments;
import Model.UserInputForBeadPlate;
import Model.bead;
import Model.probeTableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.beans.value.ObservableValue;
/**
 * FXML Controller class
 *
 * @author feiping
 */
public class ANCController implements Initializable {
    @FXML
    private ScrollPane spForSamples;
    @FXML
    private GridPane gpForSamples;
    
    @FXML
    private ScrollPane scrollANC;
    
    @FXML
    private GridPane gridANC;
    
    @FXML
    private ChoiceBox<String> unionExperiments;
    
    private int numUnions = 1;
    int largestSamples = 0;
    int experiments = 0;
    private  HashMap<Integer, List<Integer>> mapOfSamplesNumbers = new HashMap<>();
    ObservableList<bead> analytes = FXCollections.observableArrayList();
    int curSample =0;
    int curExperiment = 0;
    //ToggleGroup group = new ToggleGroup(); // For manage radio buttons for users to select. 
    @FXML
    private Text sampleName;
    String[] red = {" -fx-background-color: rgb(255, 235, 230);"," -fx-background-color: rgb(255, 214, 204);",
        " -fx-background-color: rgb(255, 133, 102);"," -fx-background-color: rgb(255, 92, 51);"};
    
    String[] blue = {" -fx-background-color: rgb(235, 235, 250);"," -fx-background-color: rgb(214,214,245);",
     " -fx-background-color: rgb(173,173,245);"," -fx-background-color: rgb(112,112,219);"};
    
    double[] ProbeNums; //number of probes in each experiment
    double IPNums; //number of IP
    double[] IPProbesNums; //total number of PPI combinations 
    double NRepValues = 0;
    int numExperiments = 0; //number of experiments 
    int numSamples = 0;
    int numPlates = 0;//number of plates
    String[] sampleNames; //names of the samples for sample selection
    
    ArrayList<ArrayList<Double>> coEfList;
    ArrayList<Integer> toalNonZero;
    ArrayList<Double> valuesList;
    ArrayList<ArrayList<Double>> PValueList;
    ArrayList<ArrayList<ArrayList<Double>>> testList;
    ArrayList<ArrayList<Double>> pOutList;
    ArrayList<ArrayList<Double>> uniqueCoeff; 
    ArrayList<ArrayList<ArrayList<Double>>> pOutValList; //List of Pvalues after Binom calculations 
    ArrayList<String> BadProbeCell;
    ArrayList<ArrayList<HashMap<Integer, List<Double>>>> ListOriComboData;  
    ArrayList<HashMap<Integer, List<Double>>> OriComboData;  
    ArrayList<ArrayList<HashMap<Integer, List<Double>>>> ListComboData1; 
    ArrayList<ArrayList<HashMap<Integer, List<Double>>>> ListComboData2; 
    
    List<CheckBox> slectedRBs = new ArrayList<>();
    List<CheckBox> radioButtons = new ArrayList<>();
    
    List<List<String>> probes1;
    List<List<String>> probes2;
    ArrayList<String> finalProbesList;
    int fcplates = 0; // samlleset plate count for the two slected samples 
    int fcprobes = 0; // samlleset probes for the two slected samples. 
    HashMap<Integer,int[]> comboList = new HashMap();
    String fileName = "ALL.xlsx";
    
    
    ArrayList<String> IPPHits;
    ArrayList<ArrayList<double[]>> PValueCellHits;
    ArrayList<ArrayList<double[]>> FCValueCellHits;
    ArrayList<ArrayList<Integer>> agreeCellHits;
    
    private boolean calculated = false; 
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        experiments = ModelForExperiments.getInstance().getNumberOfExperiments();
        largestSamples =  ModelForExperiments.getInstance().getLargestSampleCount();
        mapOfSamplesNumbers = ModelForExperiments.getInstance().getMapOfSamplesNumbers();
        analytes = ModelForExperiments.getInstance().getAnalytes();
        sampleNames = ModelForExperiments.getInstance().getSampleNames();
        
        ProbeNums = new double[experiments];
        IPProbesNums = new double[experiments];
        
        Arrays.fill(ProbeNums,0);
        tableRow(experiments);    
        tableCol(largestSamples);
        //calcaluateANCMatrix();
        fillRadioButton();
        
        // loop through each fraction of the experiments (ex. n-1/n, n-2/n, n-3/n,...)
        for(int i = experiments-1; i > 0; i--) {
            if(((double)i / (double)experiments) >= 0.70) {
                numUnions++; // we want a tab each time the fraction is greater or equal to 0.70
            }
            else {
                break; // exit loop if fraction is less than 0.70
            }
        }

        // call functions that initialize our grid and scroll panes
        //initGridList();
        //initScrollList();
        scrollANC.setContent(gridANC);
        
        spForSamples.setContent(gpForSamples); // add scroll bars to the grid pane. 
        
        fillChoiceBox(numUnions); // fill dropdown with necessary items
        // Amman: 'commented out hardcoding'
        //spForANC.setContent(gpForANC);
        //spForANC2.setContent(gpForANC2);
        //spForANC3.setContent(gpForANC3);

        IPPHits = new ArrayList<String>();
        PValueCellHits = new ArrayList<ArrayList<double[]>>();
        FCValueCellHits = new ArrayList<ArrayList<double[]>>();
        agreeCellHits = new ArrayList<ArrayList<Integer>>();
        //listOfGrid = new ArrayList();

        /*
        // display right content and remove previous slection of radio button. 
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (group.getSelectedToggle() != null) 
                {
                    curExperiment = gpForSamples.getRowIndex((Node) newValue);
                    curSample = gpForSamples.getColumnIndex((Node) newValue);
                    ModelForExperiments.getInstance().setCurSample(curSample);
            
                    oldValue.setSelected(false);
                    //showANC();
                }
            }
        });
        */
        
    }    
    
    /*
    * precondition: have experimements already selected.
    * fills in dropdown with all experiment unions.
    * i.e if 4 experiments, dropdown items are (4o4, 4o4U3o4).
    */
    private void fillChoiceBox(int numUnions) {
        int curExp = experiments; // need experiment number when create strings in choice box, i.e. ("4o4U3o4")
        String expItem = curExp + "o" + curExp; // first item in choice box is created
        unionExperiments.getItems().add(expItem); // add first item to choice box
        
        // loop through number of unions that keeps ratio at .70 or greater
        for(int i = 1; i < numUnions; i++) {
            curExp--;
            if(i == 1) {
                // hard code the second item in choice box so user can identify pattern as they go down list
                unionExperiments.getItems().add(expItem + "U" + curExp + "o" + experiments);
            }
            else {
                // put ellipsis in string to indicate that string is too long to show on screen
                unionExperiments.getItems().add(expItem + "U...U" + curExp + "o" + experiments);
            }
        }
        
        unionExperiments.setValue(expItem); // set first value to expItem
        unionExperiments.getSelectionModel().selectedIndexProperty().addListener(this::itemChanged);
    }
    
    //dynamically add rows for gridpane base on previous user input. 
    public GridPane tableRow(int rows){
        for(int i=1; i<=rows; i++){
            //set width for cells of the cols
            ColumnConstraints column = new ColumnConstraints(100);
            gpForSamples.getColumnConstraints().add(column);
            Label label = new Label();
            String s = "  Experiment " + i;
            label.setText(s);    
            //textField.setAlignment(Pos.CENTER);
            label.autosize();
            gpForSamples.add(label,0,i);
        }
        return gpForSamples;
    }

     //dynamically add cols for gridpane base on previous user input. 
    public  void tableCol(int col){
        
    for(int i=1; i<=col; i++){
        //set width for cells of the cols
        ColumnConstraints column = new ColumnConstraints(100);
        gpForSamples.getColumnConstraints().add(column);        
        Label label = new Label();
        String s = "Sample  " + i;
        label.setText(s);    
        label.autosize();
        //add them to the GridPane
        gpForSamples.add(label,i,0);
        // margins are up to your preference
        GridPane.setMargin(label, new Insets(5));
     }
    }    

    //create radio button for user to choose from. 
    private void fillRadioButton() {
        
        int btnSize = experiments *2;
        for(int i = 1; i <= experiments; i++)
        {
            for(int j = 1; j <=getLargestSampleCountForOneExperiment(i); j++)
            {
                curSample = j;
                CheckBox  btn = new CheckBox ();
                btn.setText(sampleNames[j - 1]);
                btn.setAlignment(Pos.CENTER);
                
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(btn.isSelected()){
                            if(slectedRBs.size()< btnSize)
                            {
                                    slectedRBs.add(btn);
                                    //int row = gridPane.getRowIndex(btn);
                                    //int col = gridPane.getColumnIndex(btn);
                                    //choosenSamples.add(new ArrayList<Integer>(Arrays.asList(row,col)));
                                }
                            else
                            {
                                slectedRBs.get(0).setSelected(false); // set the 1st element unselected
                                slectedRBs.remove(0); // remove 1st element from the selected list
                                //choosenSamples.remove(0);
                                slectedRBs.add(btn);
                               // System.out.println("No more than two buttons! ");
                            }
                            
                            if(slectedRBs.size() == btnSize)
                                {
                                    //defaultCutOffValue = Double.parseDouble(cutOffValueBox.getText());
                                    //showFoldChange(defaultCutOffValue);
                                    
                                    btn.setSelected(true);
                                    
                            }
                        }
                    }


                });
                
                curExperiment = i; 
                
                //if(group.getToggles().isEmpty())  // set the 1st button defalt to be selected 
                //{
                    
                    
                   // curExperiment = i; 
                   // btn.setSelected(true);
                   // if(slectedRBs.size() ==2){
                   //     showANC();
                   // }
                    
                    
                //}
                //btn.setToggleGroup(group);
                radioButtons.add(btn);
                gpForSamples.add(btn, j, i);
                GridPane.setMargin(btn, new Insets(10));
            }
        }
   
    }
    
         HashMap<Integer, ArrayList<ArrayList<String>>> dataToDisplay = new HashMap<Integer, ArrayList<ArrayList<String>>>();
    @FXML
     private void calculateDataEvent(ActionEvent event) throws IOException {
         calculateANCMatrix();
         calculated = true;
         
         if(calculated == true){
            int sizeOfColumns = experiments*2 +2;
            //int pos = 0; // for put plate 1/2 at the right position. 
            if(!IPPHits.isEmpty()){     
                for(int num = 0; num <agreeCellHits.size()-1; num ++){ 
                    ArrayList<ArrayList<String>> ListofHits = new ArrayList<ArrayList<String>>();
                    for(int ind = 0; ind < agreeCellHits.get(num).size(); ind++){
                        ArrayList<String> IP = new ArrayList<String>();
                        int key = agreeCellHits.get(num).get(ind);
                        IP.add(IPPHits.get(key)); //Put name of IPProbe combo 
                        for(int i= 0; i < PValueCellHits.size();i++){
                            //Round the pvalues for the display
                            //String PValuetemp = String.format("%.03f", PValueCellHits.get(i).get(0)[key]);
                            
                            IP.add("  "  + PValueCellHits.get(i).get(0)[key]+ "  ");
                            
                        }
                        for(int k = 0; k < FCValueCellHits.size(); k++ ){
                                
                                IP.add("  "  +FCValueCellHits.get(k).get(0)[key] + "  ");
                        }
                        ListofHits.add(IP);
                    }
                    dataToDisplay.put(num,ListofHits);
                }


            }
        }
        
        // check that we don't try to read from uninitialized 
        try {
            displayHitsToScreen(getIndexOfChoiceBox());
        } 
        catch(IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
     }
     
    private int getLargestSampleCountForOneExperiment(int i ) {
        int res = 0;
        //initilizeMapOfSample(); 
        for(Integer n : mapOfSamplesNumbers.get(i))
        {
            res = Math.max(res, n);
        }
         return res;       
    }  
    
    /*
    * precondition: have global variable unionExperiments 
    * already initialized.
    * returns the index of the currently selected value in the choicebox,
    * assumes that first index is 1, not 0 like typical array syntax.
    */
    private int getIndexOfChoiceBox() {
        ObservableList<String> items = unionExperiments.getItems();
        String selectedItem = unionExperiments.getValue();
        // find index of selected item in choice box and pass index to displayHitsToScreen()
        for(int i = 0; i < items.size(); i++) {
            if(selectedItem.equals(items.get(i))) {
                return i + 1; // add 1 as we don't use the array syntax where first index is 0
            }
        }
        
        // we should never reach this point in the method
        throw new IndexOutOfBoundsException("unionExperiments list is empty or not initialized.");
    }
    
    public void itemChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        displayHitsToScreen((int)newValue + 1);
    }   
    
    @FXML
    private void changeTabEvent(MouseEvent event) {
        
        /*
        if(beadPlate1Tab.isSelected()){
            //selectedTab =1;
            displayHitsToScreen(1);
        }else if(beadPlate2Tab.isSelected()){
            //selectedTab =2;
            displayHitsToScreen(2);
        }else if(beadPlate3Tab.isSelected()){
            //selectedTab =3;
            displayHitsToScreen(3);
        }
        */
        
    }
    
    //@FXML
    //private Text Text; 
    private void displayHitsToScreen(int selectedTab) {

        if(!dataToDisplay.isEmpty()){
            Label labelPPI  = new Label();
        Label labelPV  = new Label();
        Label labelFC  = new Label();
        labelPPI.setMinSize(150, 5);
        labelPV.setMinSize(150, 5);
        labelFC.setMinSize(150, 5);
        labelPPI.setText(" PPI"); 
        labelPV.setText(" P-value"); 
        labelFC.setText(" Fold Change"); 
        gridANC.getChildren().retainAll(gridANC.getChildren().get(0)); // clears grid
        gridANC.add(labelPPI,0,0); 
        gridANC.add(labelPV,1,0);
        gridANC.add(labelFC,5,0);

            for(int i = 0; i < dataToDisplay.get(selectedTab - 1).size(); i++)
            {
                
                for(int j = 0 ; j < dataToDisplay.get(selectedTab - 1).get(i).size();j++)
                {   
                    Label label1  = new Label();
                    //label1.autosize();

                    label1.setMinSize(150, 5);

                    //label1.resize(label1.getWidth() +100, label1.getHeight()+100);
                    //double width = label1.getWidth();
                    
                    //Format data to fit into screen
                    String dataToScreen = dataToDisplay.get(selectedTab - 1).get(i).get(j); 
                    if (dataToScreen.contains("E")){
                        //System.out.println(dataToScreen);
                        int index = dataToScreen.indexOf('E');
                        dataToScreen = dataToScreen.substring(0,7) + dataToScreen.substring(index, dataToScreen.length());
                    }
                    else{
                        dataToScreen = dataToScreen.substring(0, 7);
                    }
                    label1.setText(" " + dataToScreen);// dataToDisplay.get(selectedTab - 1).get(i).get(j)); 
                    gridANC.add(label1,j,i+1); 
                    //gpForANC.add(label1,j,i); 
                }            
            }    
            
            
        }

    }

    private void calculateANCMatrix() throws IOException {
        
       //TO DO this is where the experiment and samples are hard coded  
       int[] c1 = {2,0};
       int[] c2 = {0,2};
       int[] c3 = {3,1};
       int[] c4 = {1,3};
       comboList.put(1, c1);
       comboList.put(2, c2);
       comboList.put(3, c3);
       comboList.put(4, c4);
       
       IPNums = analytes.size(); 
       OriComboData = new ArrayList<HashMap<Integer, List<Double>>>();
       ListOriComboData = new ArrayList<ArrayList<HashMap<Integer, List<Double>>>>();
       ListComboData1= new ArrayList<ArrayList<HashMap<Integer, List<Double>>>>();
       ListComboData2= new ArrayList<ArrayList<HashMap<Integer, List<Double>>>>();
       if(!ModelForExperiments.getInstance().getANCMatrix().isEmpty()) return;
       int temp1 = 0;
       HashMap<Integer, List< HashMap<Integer, HashMap<Integer,   List<Integer>>>>>   orginalXMLData = ModelForExperiments.getInstance().getOriginalXMLData();
        for(int i = 1; i <=experiments;i++)
        {
            ArrayList<HashMap<Integer, List<Double>>> ComboData = new ArrayList<HashMap<Integer, List<Double>>>();;
            List< HashMap<Integer, HashMap<Integer,   List<Integer>>>> orignalDataForOneExperiment = orginalXMLData.get(i); 
            HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(i); 
            List<UserInputForBeadPlate> inputs =  ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(i);
            int numberOfPlates = inputs.size();
            numSamples = numberOfPlates; 
            //System.out.println("experiment is " + i ); // for debug
            for(int j = 0; j < numberOfPlates; j++)
            {
                UserInputForBeadPlate input = inputs.get(j);
                int numberOfSamples  = input.getNumOfSamples();
                ObservableList<probeTableData> ProbesForOnePlate = probesListForCurExperiment.get(j+1);
                int numberOfProbes = input.getNumOfProbes();
                ProbeNums[i-1] = ProbeNums[i-1] + numberOfProbes;
                for(int k = 0; k<numberOfSamples; k++ )
                {
                  
                     for(int x = 0; x<numberOfProbes; x++)
                     {
                        //System.out.println("probe Index is " + x ); 
                        HashMap<Integer, Double> finalANCForOneProbe  = getANCForOneProbe(i,j,k,x, ProbesForOnePlate, orignalDataForOneExperiment, input);
                        ModelForExperiments.getInstance().setOneProbeDataForANC(i, j, k, finalANCForOneProbe);
                        HashMap<Integer, List<Double>> finalANCForTwoProbe  = getANCForTwoProbe(i,j,k,x, ProbesForOnePlate, orignalDataForOneExperiment, input);
                        ComboData.add(finalANCForTwoProbe);
                        OriComboData.add(finalANCForTwoProbe);
                     }
                     
                }

            }       
            IPProbesNums[i-1] =  IPNums * ProbeNums[i-1];
            NRepValues = Double.valueOf(experiments) * Double.valueOf(numberOfPlates) * IPProbesNums[0];
            ListOriComboData.add(ComboData);
        }
        //Debugging values System.out.println("IPProbe: " + IPProbesNums[0] + " NRepVal: " + NRepValues );
       
        
        //Gets the two plates probe names and combines them into one 
        probes1 = ModelForExperiments.getInstance().getPlate1();
        probes2 = ModelForExperiments.getInstance().getPlate2();
        finalProbesList = new ArrayList<String>();
        for(int i = 0; i < numSamples; i++){
            for(int j = 0; j< ProbeNums[0]/2; j++){
                if(i == 0){
                    finalProbesList.add(probes1.get(0).get(j));
                }else{
                    finalProbesList.add(probes2.get(0).get(j));
                }
                    
            }
        }
        
        //System.out.println(finalProbesList);
        //System.out.print(probes2);
        
        
        //Calculate KS Test per experiment KEEP
        int tempsize = (int)ProbeNums[0];//hard coded temparay size
        CreatePValueCells(tempsize, tempsize/2);
        ArrayList<ArrayList<HashMap<Integer, Double>>> PValueCell = new ArrayList();
        for(int i = 0; i <ListComboData1.size(); i++ ){
            ArrayList<HashMap<Integer, Double>> ANCMatrix = new ArrayList();
            for(int j = 0; j < ListComboData1.get(i).size(); j++ ){
                HashMap<Integer, List<Double>> data1 = ListComboData1.get(i).get(j);
                HashMap<Integer, List<Double>> data2 = ListComboData2.get(i).get(j);
                HashMap<Integer, Double> ANCForTwoProbe  = CombinePValue(data1,data2);
                ANCMatrix.add(ANCForTwoProbe);
            }
            PValueCell.add(ANCMatrix);
        }
        
        //Calculate Emperical Alpha Cell KEEP
        EmpAdjPValueList(experiments, 0.05/IPProbesNums[0],NRepValues);
        
        //System.out.println("Calculation completed" + temp1);

        /**
         * Get the values calculated and combine them all in one ArrayList to calculate //This may not be needed as list is combined later on
         */
        ArrayList<ArrayList<ArrayList<Double>>> listOfRepPVal = new ArrayList<ArrayList<ArrayList<Double>>>();//List of values
        for(int i = 1; i <=experiments;i++)
        {
            ArrayList<ArrayList<Double>> repPvaluePerExp = new ArrayList<ArrayList<Double>> ();
            //List< HashMap<Integer, HashMap<Integer,   List<Integer>>>> orignalDataForOneExperiment = orginalXMLData.get(i); 
            HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(i); 
            List<UserInputForBeadPlate> inputs =  ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(i);
            int numberOfPlates = inputs.size();
            UserInputForBeadPlate input = inputs.get(0);//Used to get the first plate index this is hard coded witht he assumption that the plate number sizes does not change with experiments 
        
            int numberOfSamples  = input.getNumOfSamples();
            for(int k = 0; k<numberOfSamples; k++ )
            {   
                     
                ArrayList<Double> repPvalCell = new ArrayList<Double>();
                for(int a = 0; a < analytes.size();a++){
                    for(int j = 0; j < numberOfPlates; j++)
                    {

                        int numberOfProbes = input.getNumOfProbes();
                        ObservableList<probeTableData> ProbesForOnePlate = probesListForCurExperiment.get(j+1);
                            //System.out.println("sample Index is " + k); 
                        for(int x = 0; x<numberOfProbes; x++)
                        {
                            //
                            HashMap<Integer, Double> finalANCForOneProbe  = ModelForExperiments.getInstance().getOneProbeDataForANC(i, j,  k, x);
                            //HashMap<Integer, Double> ANCForOneProbe  =  ModelForExperiments.getInstance().getOneProbeDataForANC(i , j, k , x);
                            //double tempvalue = finalANCForOneProbe.get();
                            //double probe = analytes.get(a).getRegionNumber();
                            double valueToCal = finalANCForOneProbe.get(analytes.get(a).getRegionNumber());
                            repPvalCell.add(valueToCal);
                            //System.out.println("probe Index is " +analytes.get(x).getRegionNumber()); 
                         }
                    }
                     
                }
                repPvaluePerExp.add(repPvalCell);
 
            }
            listOfRepPVal.add(repPvaluePerExp);
        }
        
        //Create a combined Pvalue cell 
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>>> finallistOfChangePVal = new ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>>> (); 
        finallistOfChangePVal = CreateCombinedPValueCells(finallistOfChangePVal);
        
        //Combine all the data together per experiment 
        ArrayList<ArrayList<Double>> combinedList = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> sortedCombinedList = new ArrayList<Double>();
        for(int i = 0; i < finallistOfChangePVal.size(); i ++ ){
            ArrayList<Double> combinedArrayList = combineList(finallistOfChangePVal.get(i));
            combinedList.add(combinedArrayList);
            
        }
        //combine all the data 
        ArrayList<Double> combinedAllArray = combineAll(combinedList);
       
        //Clone and sort data from smallest to largest
        ArrayList<Double> clonedCombinedList = cloneArrayList(combinedAllArray);
        Collections.sort(clonedCombinedList);
        
        //Polyfit and PolyVal 
        ArrayList<ArrayList<ArrayList<Double>>> FinalCutOffList = new ArrayList<ArrayList<ArrayList<Double>>>();
        for(int i = 0; i < pOutValList.size(); i ++){// i = 1:length(POutputCell)
            ArrayList<ArrayList<Double>> PCutOffList = new ArrayList<ArrayList<Double>>();
            for(int j = 0; j < pOutValList.get(i).size(); j++){
                ArrayList<Double> cutoffList = new ArrayList<Double>();
                for(int k = 0; k < pOutValList.get(i).get(j).size(); k++){
                    double cutoff = EMPEnding(clonedCombinedList,pOutValList.get(i).get(j).get(k));
                    cutoffList.add(cutoff);
                }
                PCutOffList.add(cutoffList);
            }
            FinalCutOffList.add(PCutOffList);
        }
        //System.out.println(FinalCutOffList);
        
        
        //List<List<HashMap<Integer,Double>>> foldChangeMatrix = new ArrayList<List<HashMap<Integer,Double>>>();
        
        
        //Gets thhe Fold Change cells 
        HashMap<Integer,List<List<HashMap<Integer,Double>>>> foldchange = getFoldChange();//ModelForExperiments.getInstance().getfoldChangeforANC();
        //System.out.println(foldchange.size());
        
        HashMap<Integer,List<HashMap<Integer,Double>>> combinedFoldChange = combineFoldChange(foldchange); 
        for(int i = 0; i < experiments; i++){
            //System.out.println(combinedFoldChange.get(i+1));
        }
        GlobalHits(PValueCell, combinedFoldChange, FinalCutOffList);
   
    }
    
    private void GlobalHits(ArrayList<ArrayList<HashMap<Integer, Double>>> PValueCell, HashMap<Integer,List<HashMap<Integer,Double>>> FCCell,  ArrayList<ArrayList<ArrayList<Double>>> PCutoffCell) throws IOException{

        //// hardcoded version
//            double[][][] PerAgreeCell = {{{1}},
//                                        {{2}, {2,1}},
//                                        {{3}, {3,2}, {3,2,1}},
//                                        {{4}, {4,3}, {4,3,2}, {4,3,2,1}},
//                                        {{5},{5,4},{5,4,3},{5,4,3,2},{5,4,3,2,1}},
//                                        {{6},{6,5},{6,5,4},{6,5,4,3},{6,5,4,3,2},{6,5,4,3,2,1}},
//                                        {{7},{7,6}, {7,6,5},{7,6,5,4},{7,6,5,4,3},{7,6,5,4,3,2},{7,6,5,4,3,2,1}},
//                                        {{8},{8,7},{8,7,6},{8,7,6,5},{8,7,6,5,4},{8,7,6,5,4,3}, {8,7,6,5,4,3,2},{8,7,6,5,4,3,2,1}}
//                                        };


         ////// Creates a jagged 3D array matrix of descending numbers based on # of experiments (example above)
        double[][][] perAgreeCell = new double[experiments][][];

        for(int i = 0; i < experiments; i++)
        {
            int tracer = i;
            int counter = 0;
            perAgreeCell[i] = new double[i + 1][];
            for(int j = 0; j<= i; j++)
            {
                double[] temp = new double[j+1];
                for(int k = i; k >= 0 && k-tracer >= 0; k--, counter++)
                {
                    temp [counter] = (double)(k+1);
                }
                perAgreeCell[i][j] = temp;
                tracer--;
                counter = 0;
            }
        }


        singlehit(PValueCell,FCCell,PCutoffCell,perAgreeCell);
        //System.out.println(FCCell.size());
        
    }
    
    Map<Integer, List<LinkedList<Integer>>> powerset = new HashMap<>();
    
    private double[][] getPMatrix(ArrayList<ArrayList<HashMap<Integer, Double>>> PValueCell){
        int size = (int)ProbeNums[0]*analytes.size();
        //System.out.println(size);
        double[][][] totalPvalue = new double[experiments][(int)ProbeNums[0]][analytes.size()]; 
        
        for(int i = 0;i < PValueCell.size();i++){
            for(int j = 0 ; j < PValueCell.get(i).size();j++){
                for(int k = 0; k < PValueCell.get(i).get(j).size();k++){
                       int key = analytes.get(k).getRegionNumber();
                       totalPvalue[i][j][k] = PValueCell.get(i).get(j).get(key);
                }
            }
        }
        
        double [][] fPvalueCell = new double[experiments][size];
        for(int i = 0;i < totalPvalue.length;i++){
            int index = 0;
            for(int k = 0; k < analytes.size();k++){
                for(int j = 0 ; j < totalPvalue[i].length;j++){
                    //int key = analytes.get(k).getRegionNumber();
                    fPvalueCell[i][index] = totalPvalue[i][j][k];
                    index++;
                }
            }
        }
            
        return fPvalueCell;
    }
       
    private double[][] getFCMatrix(HashMap<Integer,List<HashMap<Integer,Double>>> FCCell){
        int size = (int)ProbeNums[0]*analytes.size();
        //System.out.println(size);
        double[][][] totalPvalue = new double[experiments][(int)ProbeNums[0]][analytes.size()]; 
        
        for(int i = 0;i < FCCell.size();i++){
            for(int j = 0 ; j < FCCell.get(i+1).size();j++){
                for(int k = 0; k < FCCell.get(i+1).get(j).size();k++){
                       int key = analytes.get(k).getRegionNumber();
                       totalPvalue[i][j][k] = FCCell.get(i+1).get(j).get(key);
                }
            }
        }
        
        double [][] fPvalueCell = new double[experiments][size];
        for(int i = 0;i < totalPvalue.length;i++){
            int index = 0;
            for(int k = 0; k < analytes.size();k++){
                for(int j = 0 ; j < totalPvalue[i].length;j++){
                    //int key = analytes.get(k).getRegionNumber();
                    fPvalueCell[i][index] = totalPvalue[i][j][k];
                    index++;
                }
            }
        }
            
        return fPvalueCell;
    }
    
    private double[][] getCutMatrix(ArrayList<ArrayList<Double>>  PCutoffCell){
        int size = (int)ProbeNums[0]*analytes.size();
        double[][] totalPvalue = new double[PCutoffCell.size()][]; 
        
        for(int i = 0;i < PCutoffCell.size();i++){
            double[] newArray = new  double[PCutoffCell.get(i).size()];
            for(int j = 0 ; j < PCutoffCell.get(i).size();j++){
                       newArray[j]= PCutoffCell.get(i).get(j);
            }
            totalPvalue[i] =newArray;
        }

        return totalPvalue;
    }
    
    private ArrayList<Integer> unionHelper(ArrayList<Integer> a, ArrayList<Integer> b){
        ArrayList<Integer> c = new ArrayList();
        if(a == null){
            return b;
        }
       
        for(int i = 0; i < a.size() ; i++){
            Integer aValue = a.get(i);
            if(!c.contains(aValue)){         
                c.add(aValue);
            }        
        }
        
        
        for(int j = 0; j < b.size(); j++){
                
                Integer bValue = b.get(j);
                if(!c.contains(bValue)){
                    c.add(bValue);
                }
        }
        Collections.sort(c);
        return c;
    }
    
    private Integer[] sumhelperNeg(Integer[] temp, double agree ){
        Integer[] hit = new Integer[temp.length];
        for(int i = 0; i < temp.length; i++){
            if(temp[i] <= agree){
                hit[i] =1;
            }
            else{
                hit[i] = 0;
            }
        }
        return hit;
    }
    
    
    private Integer[] sumhelper(Integer[] temp, double agree ){
        Integer[] hit = new Integer[temp.length];
        for(int i = 0; i < temp.length; i++){
            if(temp[i] >= agree){
                hit[i] =1;
            }
            else{
                hit[i] = 0;
            }
        }
        return hit;
    }
    
    static ArrayList<Integer> find(Integer[] agreedFC){
        ArrayList<Integer> hits = new ArrayList<Integer>();
        for(int i = 0; i< agreedFC.length;i++){
            if(agreedFC[i] ==1){
                hits.add(i);
            }
        }
        
        return hits;
    }
    
     private Integer[] sumArray (Integer[][] a ){
        Integer[] total= new Integer[a[0].length];
        
        for(int j = 0; j < a[0].length; j++){
            Integer init = 0;
            for(int row = 0; row < a.length;row++){
            
            init += a[row][j];
            }
            //System.out.println("total = " + init);
            total[j] = init;
        }
        //System.out.println("total = " + total);
        return total;
    }
     
    private ArrayList<String> IPProbe = new ArrayList<>();
    
    public void sh(ArrayList<double[]> FCCell, ArrayList<double[]> Pvalue,  LinkedList<Integer> cbk, ArrayList<double[][]> pCutoffCell1, ArrayList<double[][]> PerAgree1,
            ArrayList<List<ArrayList<ArrayList<ArrayList<Integer>>>>> fListSigList,
            ArrayList<ArrayList<ArrayList<ArrayList<String>>>> fListSigID,
            ArrayList<ArrayList< ArrayList<ArrayList<Integer>>>> fListSigNum,
            ArrayList<List<ArrayList<ArrayList<ArrayList<Integer>>>>> fListSigListComb
            ){

            ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> finalSigList = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
            ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> finalSigListComb = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
            ArrayList<ArrayList<ArrayList<Integer>>> finalSigNumComb = new ArrayList<ArrayList<ArrayList<Integer>>>();
            ArrayList<ArrayList<ArrayList<String>>> finalSigID = new ArrayList<ArrayList<ArrayList<String>>>();
        
        
            ArrayList<Integer[]> FC10 = new ArrayList<Integer[]>();
            int sizeArray = FCCell.get(0).length;
            
            for(int i = 0; i < FCCell.size(); i++){
                
                Integer[] tempFC = new Integer[FCCell.get(i).length]; 
                
                for(int j = 0; j < FCCell.get(i).length; j++){
                    if(FCCell.get(i)[j] > 0){
                        tempFC[j] = 1;
                    }else{
                        tempFC[j] = 0;
                    }
                }
                FC10.add(tempFC);
            }
            
            
        for(int i = 0; i < pCutoffCell1.size(); i++){
            
            ArrayList<ArrayList<ArrayList<Integer>>> tSigList = new ArrayList<ArrayList<ArrayList<Integer>>>();
            ArrayList<ArrayList<ArrayList<Integer>>> tSigListComb = new ArrayList<ArrayList<ArrayList<Integer>>>();
            ArrayList<ArrayList<Integer>> tSigNumComb = new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<String>> tSigID = new ArrayList<ArrayList<String>>();
            
            ArrayList<Integer> SigListCombpointer = new ArrayList<Integer>();
            int pointerint = 0; 
            int pointerint2 = 0; 
            
           for(int j = 0; j < pCutoffCell1.get(i).length; j++){
            ArrayList<ArrayList<Integer>> SigList = new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<Integer>> SigListComb = new ArrayList<ArrayList<Integer>>();
            ArrayList<Integer> SigNumComb = new ArrayList<Integer>();
            ArrayList<String> SigID = new ArrayList<>();
                //FCPerAgreePos = sum(FC10,2)>=PerAgree{i}(j);
               Integer[] FCPerAgreePos = new Integer[sizeArray];
               Integer[] newFC10 = new Integer[sizeArray];// = new Integer[FC10[0].length];
               Integer[] FCPerAgreeNeg = new Integer[sizeArray];
               newFC10 = sumArray(FC10);
               //ArrayList<Integer> prevSigListtemp = new ArrayList<Integer>();
               
                   //ArrayList<Integer> SigListCombpointer = new ArrayList<Integer>();

               for(int k = 0; k < pCutoffCell1.get(i)[j].length; k++){
                   ArrayList<Integer> SigListCombtemp = new ArrayList<Integer>();
                   ArrayList<Integer> SigListtemp = new ArrayList<Integer>();
                   
                   ArrayList<Integer> PAgree4FCHits = new ArrayList<Integer>();
              
                   ArrayList<Integer> FCPerAgreePosIndex = new ArrayList<Integer> ();
                   ArrayList<Integer> FCPerAgreeNegIndex = new ArrayList<Integer> ();
                   ArrayList<Integer> PAgree4FCPosHits = new ArrayList<Integer>();
                   ArrayList<Integer> PAgree4FCNegHits = new ArrayList<Integer>();
                   
                   for(int l = 0; l <sizeArray; l++){
                        int posNeg = 0;
                        if(newFC10[l] >= PerAgree1.get(i)[j][k]){
                           posNeg =1;
                        }else{
                            posNeg=0;
                        }
                        FCPerAgreePos[l] = posNeg;
                    }

                    
                    FCPerAgreePosIndex = find(FCPerAgreePos);
                    
                    for(int alpha = 0; alpha <FCPerAgreePosIndex.size(); alpha++){
                        
                        int pindex = FCPerAgreePosIndex.get(alpha);
                        int FCSize = FC10.size();
                        
                        ArrayList<Integer> FCalphaAgreeIndex = new ArrayList<Integer>();
                        FCalphaAgreeIndex = getlogical(FC10, pindex);
                                
                        ArrayList<Double> P_I = new ArrayList<Double>();
                        ArrayList<Integer> Pi_NumSig = new ArrayList<Integer>();

                        P_I = getPIValue(Pvalue, pindex, FCalphaAgreeIndex);
                        Pi_NumSig = getPi_NumSig(P_I, pCutoffCell1.get(i)[j][k]);
                        int sumPi_num = sumPI(Pi_NumSig);  
                        if(sumPi_num == PerAgree1.get(i)[j][k]){
                            PAgree4FCPosHits.add(pindex);
                        }else{
                            PAgree4FCPosHits.add(0);
                        }
                        
                    }
                    
                    ArrayList<Integer> newPAgree4FCPosHits = new ArrayList<Integer>();
                    for(int m = 0; m <PAgree4FCPosHits.size(); m++ ){
                        if(PAgree4FCPosHits.get(m) != 0){
                            newPAgree4FCPosHits.add(PAgree4FCPosHits.get(m));
                        }
                    }
                  //Negative hits 
                  int N =  pCutoffCell1.get(i).length;
                  
                   for(int l = 0; l <sizeArray; l++){
                        int posNeg = 0;
                        if(newFC10[l] <= N - PerAgree1.get(i)[j][k]){
                           posNeg =1;
                        }else{
                            posNeg=0;
                        }
                        FCPerAgreeNeg[l] = posNeg;
                    }
                   
                   FCPerAgreeNegIndex = find(FCPerAgreeNeg);
                   
                   for(int alpha = 0; alpha <FCPerAgreeNegIndex.size(); alpha++){
                        
                        int pindex = FCPerAgreeNegIndex.get(alpha);
                        int FCSize = FC10.size();
                        
                        ArrayList<Integer> FCalphaNegIndex = new ArrayList<Integer>();
                        FCalphaNegIndex = getNeglogical(FC10, pindex);
                                
                        ArrayList<Double> P_I = new ArrayList<Double>();
                        ArrayList<Integer> Pi_NumSig = new ArrayList<Integer>();

                        
                        P_I = getPIValue(Pvalue, pindex, FCalphaNegIndex);
                        Pi_NumSig = getPi_NumSig(P_I, pCutoffCell1.get(i)[j][k]);
                        int sumPi_num = sumPI(Pi_NumSig);  
                        if(sumPi_num >= PerAgree1.get(i)[j][k]){
                            PAgree4FCNegHits.add(pindex);
                        }else{
                            PAgree4FCNegHits.add(0);
                        }
                        
                    }
                   
                    ArrayList<Integer> newPAgree4FCNegHits = new ArrayList<Integer>();
                    for(int m = 0; m <PAgree4FCNegHits.size(); m++ ){
                        if(PAgree4FCNegHits.get(m) != 0){
                            newPAgree4FCNegHits.add(PAgree4FCNegHits.get(m));
                        }
                    }
                    if(cbk.size() == 4){
                        System.out.println();
                    }
                    PAgree4FCHits = unionHelper(newPAgree4FCPosHits,newPAgree4FCNegHits );
                    SigListtemp = unionHelper(newPAgree4FCPosHits,newPAgree4FCNegHits );
                    if(SigListComb.size() >= k && SigListComb.size() >= 1){
                        SigListCombtemp = unionHelper(SigListComb.get(k-1),PAgree4FCHits);
                    }
                    else{
                        SigListCombtemp = PAgree4FCHits;
                    }
                    
                    SigList.add(SigListtemp);
                    
                    
                    if(!tSigListComb.isEmpty() && tSigListComb.size() >= j){
                        if(!tSigListComb.get(j-1).isEmpty() && tSigListComb.get(j-1).size() > k){
                            SigListCombpointer = tSigListComb.get(j-1).get(k);
                            if(SigListCombpointer.size() >SigListCombtemp.size() )
                            {
                                SigListCombtemp = SigListCombpointer;
                            }
                        }
                    }
                    SigListComb.add(k,SigListCombtemp );
                    //prevSigListtemp = SigListCombtemp;
                    
                    
                    
                    SigNumComb.add(k,SigList.size());
                    
                    for(int omega = 0; omega < SigListComb.size(); omega++){
                        for(int beta = 0 ; beta < SigListComb.get(omega).size(); beta++){
                                SigID.add(IPProbe.get(SigListComb.get(omega).get(beta)));
                        }
                    }
                  
                    //System.out.println("test"); 
               }
                tSigList.add(SigList);
                tSigListComb.add(SigListComb);
                tSigNumComb.add(SigNumComb);
                tSigID.add(SigID);
                
           }
            finalSigList.add(tSigList);
            finalSigID.add(tSigID);
            finalSigNumComb.add(tSigNumComb);
            finalSigListComb.add(tSigListComb);
            //SigListCombpointer.clear();
            
        }
            fListSigList.add(finalSigList);
            fListSigID.add(finalSigID);
            fListSigNum.add(finalSigNumComb);
            fListSigListComb.add(finalSigListComb);
            
    }
        public static int sumPI(ArrayList<Integer> a){
        int sumtotal = 0;
        for(int i = 0; i < a.size(); i++){
            sumtotal+= a.get(i);
        }
        return sumtotal;
    }
    
    public static ArrayList<Integer> getPi_NumSig(ArrayList<Double> PI, double pCutoffCell1){
        ArrayList<Integer> newPi_NumSig = new ArrayList<Integer>();
        
        for(int i = 0; i < PI.size(); i++){
            if(PI.get(i)<pCutoffCell1){
                newPi_NumSig.add(1);
            }else{
                newPi_NumSig.add(0);
            }
        }


        return newPi_NumSig;
    }
    
    public static ArrayList<Integer> getNeglogical(ArrayList<Integer[]> FArray, int index){
        ArrayList<Integer> newlogic = new ArrayList<Integer>();
        for(int i = 0; i < FArray.size(); i++){
            int pos = FArray.get(i)[index];
            int newpos = 0;
            if(pos == 0){
               newpos = 1;
            }
            newlogic.add(newpos);
        }

        return newlogic;
    }
    
    public static ArrayList<Integer> getlogical(ArrayList<Integer[]> FArray, int index){
        ArrayList<Integer> newlogic = new ArrayList<Integer>();
        for(int i = 0; i < FArray.size(); i++){
            newlogic.add(FArray.get(i)[index]);
        }

        return newlogic;
    }
    public static ArrayList<Double> getPIValue(ArrayList<double[]> PCell, int a, ArrayList<Integer> calLogic){
        ArrayList<Double> newPvalue = new ArrayList<Double>();
        
        for(int i = 0; i < PCell.size(); i++){
            if(calLogic.get(i) == 1){
                newPvalue.add(PCell.get(i)[a]);
            }
        }

        return newPvalue;
    }
    

    
    public static Integer[] sumArray(ArrayList<Integer[]> FC){
        Integer[] newFC10 = new Integer[FC.get(0).length];
        Arrays.fill(newFC10,0);
        for(int i = 0; i < FC.size(); i++){
            for(int j = 0; j < FC.get(i).length; j++){
                newFC10[j] += FC.get(i)[j];
            }
        }
        return newFC10;
    }

    
    /**
     * Find all the values that are hits
     * @param PValueCell
     * @param FCCell
     * @param PCutoffCell
     * @param PerAgree
     */
    private void singlehit(ArrayList<ArrayList<HashMap<Integer, Double>>> PValueCell, HashMap<Integer,List<HashMap<Integer,Double>>> FCCell,  ArrayList<ArrayList<ArrayList<Double>>> PCutoffCell,double[][][] PerAgree) throws IOException{
        
        double[][] pcell = getPMatrix(PValueCell);
        double[][] FCCell2 = getFCMatrix(FCCell);
        //double[][] pCutoffCell1 = getCutMatrix(PCutoffCell);
         powerset(Arrays.asList(1, 2, 3, 4));// get the list of combinations
        //IPNums = analytes.size();
        HashMap<Integer,String> IPN = new HashMap(); 
        for(int i = 0; i < analytes.size(); i++){
             IPN.put(analytes.get(i).getRegionNumber(), analytes.get(i).getAnalyte());
        }
          
        for(int i = 0; i < analytes.size(); i++){
            String name;
            for(int j = 0; j < finalProbesList.size(); j++){
                name = analytes.get(i).getAnalyte();
                String p = "_" + finalProbesList.get(j);
                name += p;
                IPProbe.add(name);
            }
        }
        //Rearrange data  to fit requirements per sample and experiment 
         ArrayList<ArrayList<ArrayList<double[]>>> ListOfFCell = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<double[]>>> ListOfPValueCell = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<double[][]>>> ListOfpCut = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<double[][]>>> ListOfPAgree = new ArrayList<>();
        for(int key = 1; key< experiments+1; key++){
            ArrayList<ArrayList<double[]>> LofFC = new ArrayList();
            ArrayList<ArrayList<double[]>> LofPV = new ArrayList();
            ArrayList<ArrayList<double[][]>> LofPC = new ArrayList();
            ArrayList<ArrayList<double[][]>> LofPA = new ArrayList();
            int index = 0;
            for(int i = 0; i < powerset.get(key).size(); i++){
                ArrayList<double[]> init = new ArrayList();
                ArrayList<double[]> initPV = new ArrayList();
                ArrayList<double[][]> initPC = new ArrayList();
                ArrayList<double[][]> initPA = new ArrayList();
                for(int j =0; j < powerset.get(key).get(i).size(); j++){
                    //System.out.println(powerset.get(key).get(i));
                    int id = powerset.get(key).get(i).get(j) -1;
                    //System.out.println("key " + get);
                    init.add(FCCell2[id]);
                    initPV.add(pcell[id]);
                    double[][] pCutoffCell1 = getCutMatrix(PCutoffCell.get(key-1));
                    initPC.add(pCutoffCell1);
                    initPA.add(PerAgree[key-1]);
                }
                LofFC.add(init);
                LofPV.add(initPV);
                LofPC.add(initPC);
                LofPA.add(initPA);
            }
            index++;
            ListOfFCell.add(LofFC);
            ListOfPValueCell.add(LofPV);
            ListOfpCut.add(LofPC);
            ListOfPAgree.add(LofPA);
        }
        
        
         List<List<List<ArrayList<ArrayList<ArrayList<Integer>>>>>> expfListSigList = new ArrayList();
         List<List<ArrayList<ArrayList<ArrayList<String>>>>> expfListSigID = new ArrayList();
         List<List<ArrayList< ArrayList<ArrayList<Integer>>>>> expfListSigNum = new ArrayList();
         List<List<List<ArrayList<ArrayList<ArrayList<Integer>>>>>> expfListSigListComb = new ArrayList();
        for(int i = 0; i < experiments; i++ ){
            
             ArrayList<List<ArrayList<ArrayList<ArrayList<Integer>>>>> fListSigList = new ArrayList();
            ArrayList<ArrayList<ArrayList<ArrayList<String>>>> fListSigID = new ArrayList();
            ArrayList<ArrayList< ArrayList<ArrayList<Integer>>>> fListSigNum = new ArrayList();
            ArrayList<List<ArrayList<ArrayList<ArrayList<Integer>>>>> fListSigListComb = new ArrayList();
            
            
            for(int j = 0; j < powerset.get(i+1).size(); j++){
                sh(ListOfFCell.get(i).get(j), ListOfPValueCell.get(i).get(j),powerset.get(i+1).get(j), ListOfpCut.get(i).get(j), ListOfPAgree.get(i).get(j), fListSigList,fListSigID,fListSigNum,fListSigListComb);
            
            }
            //System.out.println();
            //System.out.println();
            //System.out.println("New experiment");
            expfListSigList.add(fListSigList);
            expfListSigID.add(fListSigID);
            expfListSigNum.add(fListSigNum);
            expfListSigListComb.add(fListSigListComb);
            
        }
        createALLSpreadSheet(IPProbe, ListOfPValueCell.get(0), ListOfFCell.get(0), expfListSigListComb.get(3).get(0).get(0).get(3));
        
        IPPHits = IPProbe ;
        PValueCellHits = ListOfPValueCell.get(0);
        FCValueCellHits = ListOfFCell.get(0);
        agreeCellHits = expfListSigListComb.get(3).get(0).get(0).get(3);
        
        
        
        
        
        
        
        //System.out.println("IPN" + IPN);
        List<List<List<Integer>>> ListFCperAgreeIndexperExp = new ArrayList<>();
        List<List<List<Integer>>> ListFCPerAgreeNegperExp = new ArrayList<>();
        List<List< ArrayList<HashMap<Integer, Integer>>>> CobinedList = new ArrayList<>();
        List<Integer> PAgree4FCNegHits = new ArrayList<Integer>();
        List<Integer> PAgree4FCPosHits = new ArrayList<Integer>();
        boolean hitornot = false;
        
        
        
        //Logical array to decide if value is positive or negative
        for(int i = 0; i < FCCell.size(); i ++){
            List<List<Integer>> ListFCperAgreeIndex = new ArrayList<>();
            List<List<Integer>> ListFCPerAgreeNeg = new ArrayList<>();
            List< ArrayList<HashMap<Integer, Integer>>> CombinedFC = new ArrayList<>();
            for(int j = 0; j < FCCell.get(i+1).size();j++){
                //for(int l = 0; l < FCCell.get(i+1).get(j).size(); l++){
                ArrayList<HashMap<Integer, Integer>> ListFC = new ArrayList();
                List<Integer> FCperAgreeIndex = new ArrayList<Integer>();
                List<Integer> FCPerAgreeNeg = new ArrayList<Integer>();
                
                    for(int k = 0; k < analytes.size();k++){
                        
                        int key = analytes.get(k).getRegionNumber();
                        HashMap<Integer, Integer> hit = new HashMap();
                        double value = FCCell.get(i+1).get(j).get(key);
                        //System.out.println("Testing Value " + value);
                        if(FCCell.get(i+1).get(j).get(key) > 0){
                            hit.put(key, 1);
                            hitornot = true;
                            FCperAgreeIndex.add(key);//Find the position of where there are 1
                            //System.out.println(" Hit: 1 ");
                        }else{
                            hitornot = false;
                            hit.put(key, 0);
                            FCPerAgreeNeg.add(key);
                            //System.out.println(" Miss: 0 ");
                        }
                        
                        ListFC.add(hit);
                    }
                CombinedFC.add(ListFC);    
                ListFCperAgreeIndex.add(FCperAgreeIndex);
                ListFCPerAgreeNeg.add(FCPerAgreeNeg);
                //}
                
            }
            ListFCperAgreeIndexperExp.add(ListFCperAgreeIndex);
            ListFCPerAgreeNegperExp.add(ListFCPerAgreeNeg);
            CobinedList.add(CombinedFC);
        }
        
        
        //Put all the Hits into an Array to create spreadsheet
        ArrayList<String> allHits = new ArrayList();
        for(int i = 0; i < ListFCperAgreeIndexperExp.size();i++){
            for(int j = 0; j < ListFCperAgreeIndexperExp.get(i).size(); j++){
                for(int k = 0; k < ListFCperAgreeIndexperExp.get(i).get(j).size();k++){
                    int key = ListFCperAgreeIndexperExp.get(i).get(j).get(k);
                    
                    String IPP = IPN.get(key) +"_"+ finalProbesList.get(j);
                    ArrayList<Double> value = PCutoffCell.get(0).get(0);
                    allHits.add(IPP);
                }
            }
        }
        ArrayList<String> FCCellPrint = new ArrayList();
        for(int i = 0; i < FCCell.size();i++){
            String IPP = " ";
            for(int temp = 0; temp < analytes.size(); temp++){
                for(int k = 0; k < PValueCell.get(i).size();k++){
                    int key = analytes.get(temp).getRegionNumber();
                    //int key = FCCell.get(i).get(j).get(k);
                    IPP = IPP + PValueCell.get(i).get(k).get(key) + ",";
                    //String IPP = IPN.get(key) +"_"+ finalProbesList.get(j);
                    //ArrayList<Double> value = PCutoffCell.get(0).get(0);
                    
                }
            }
            FCCellPrint.add(IPP);
        }
        /*
        System.out.println(cList);
        System.out.println(cList.size() + " " + cList.get(0).size() + " " + cList.get(0).get(0).size());
        System.out.println(FCCell.size() + " " + FCCell.get(1).size() + " " + FCCell.get(1).get(0).size());
        System.out.println(PValueCell.size() + " " + PValueCell.get(0).size() + " " + PValueCell.get(1).get(0).size());
        */
        
        for(int i = 0; i < CobinedList.size(); i ++){
            int exp = i +1;
            for(int temp = 0; temp < analytes.size(); temp++){
                for(int j = 0; j < CobinedList.get(i).size(); j++){
                    //for(int k = 0; k < CobinedList.get(i).get(j).size();k++){
                       int key = analytes.get(temp).getRegionNumber();
                       String IPP = IPN.get(key) +"_"+ finalProbesList.get(j); 
                       int hitorMiss = CobinedList.get(i).get(j).get(temp).get(key); 
                       double num = FCCell.get(exp).get(j).get(key);
                       //System.out.println(IPP + num + " " + hitorMiss);
                    //}
                }
            }
        }
        //Create spreadsheet with all the Hits and Misses
        //createSpreadSheet(FCCellPrint);
        
        
        //System.out.println("List of Agreed Final index" + allHits);//ListFCperAgreeIndexperExp);
        //Sum the columns? 
        //Check to see if it is logical at Index is a 1 // might not needed as it's just a one dimentional array but this might change..
       //System.out.println(FCPerAgreeNeg);
        //PAgree4FCPosHits = new double[FCperAgreeIndex.size()];

             for(int i = 0; i < PCutoffCell.size();i++){
                for(int j = 0; j < PCutoffCell.get(i).size();j++){
                    for(int m = 0 ; m < PCutoffCell.get(i).get(j).size(); m++){
                        double pCut = PCutoffCell.get(i).get(j).get(m);
                        
                        for(int p = 0; p <ListFCperAgreeIndexperExp.get(i).size(); p++){
                            for(int k = 0; k < ListFCperAgreeIndexperExp.get(i).get(p).size();k++){
                                //int pos = ListFCperAgreeIndexperExp.get(i).get(p).
                                int key = ListFCperAgreeIndexperExp.get(i).get(p).get(k);
                                
                                double PI = PValueCell.get(i).get(p).get(key);
                                boolean PIBool = (Double)PI < pCut;

                                int PI_Numsig = PIBool ? 1:0;
                                double sum = PI_Numsig;
                                if(sum == PerAgree[i][j][m]){
                                    PAgree4FCPosHits.add(ListFCperAgreeIndexperExp.get(i).get(p).get(k));
                            }
                        }
                    }
                } 
            }
        }
 
        //System.out.println("List of Adjusted index" + PAgree4FCPosHits);
        //Line 65 Single Hits
        //PAgree4FCPosHits(PAgree4FCPosHits==0)=[];
        //Sum the two arrays togehter? FCPerAgreeNeg = sum(FC10,2)<= N-PerAgree{i}(j);
            //Switches the zeros and ones?
            
        /**Function repeated for the zero hits need to revise    
        
        */
        if(!ListFCPerAgreeNegperExp.isEmpty()){
        for(int i = 0; i < PCutoffCell.size();i++){
                for(int j = 0; j < PCutoffCell.get(i).size();j++){
                    for(int m = 0 ; m < PCutoffCell.get(i).get(j).size(); m++){
                        double pCut = PCutoffCell.get(i).get(j).get(m);
                        
                        for(int p = 0; p <ListFCPerAgreeNegperExp.get(i).size(); p++){
                            for(int k = 0; k < ListFCPerAgreeNegperExp.get(i).get(p).size();k++){
                                //int pos = ListFCperAgreeIndexperExp.get(i).get(p).
                                int key = ListFCPerAgreeNegperExp.get(i).get(p).get(k);
                                
                                double PI = PValueCell.get(i).get(p).get(key);
                                boolean PIBool = (Double)PI < pCut;

                                int PI_Numsig = PIBool ? 1:0;
                                double sum = PI_Numsig;
                                if(sum >= PerAgree[i][j][m]){
                                    PAgree4FCNegHits.add(ListFCPerAgreeNegperExp.get(i).get(p).get(k));
                            }
                        }
                    }
                } 
            }
        }
        }
       
        
        List<Integer> CombinedList = new ArrayList<Integer>();
        
        //Order combined list from smallest to largest TODO!!!!!!!!
        for(int i = 0; i < PAgree4FCPosHits.size(); i++){
            for(int j = 0; j< PAgree4FCNegHits.size();j++){
                if(PAgree4FCNegHits.get(j) < PAgree4FCPosHits.get(i) && !CombinedList.contains(PAgree4FCNegHits.get(j) )){
                    CombinedList.add(PAgree4FCNegHits.get(j));
                }else if(PAgree4FCNegHits.get(j) > PAgree4FCPosHits.get(i) && !CombinedList.contains(PAgree4FCPosHits.get(i))){
                    CombinedList.add(PAgree4FCPosHits.get(i));
                }
            }
        }
        //PAgree4FCPosHits.addAll(PAgree4FCNegHits);
        //System.out.println("Final agreed List" + CombinedList);
        //PAgree4FCNegHits;
    }
    
        private void powerset(List<Integer>  src) {
         powerset(new LinkedList<>(), src);
    }
    
    
private void powerset(LinkedList<Integer> copyArray, List<Integer> src) {
    if (src.size() > 0) {
        copyArray = new LinkedList<>(copyArray); //create a copy 
        src = new LinkedList<>(src); //copy
        Integer curr = src.remove(0); //get the number to be added 
        combk(copyArray, curr);
        powerset(copyArray, src);
        copyArray.add(curr);
        powerset(copyArray, src);
    }
}

private void combk(LinkedList<Integer> copyArray, Integer curr) {
    copyArray = new LinkedList<>(copyArray); //copy
    copyArray.add(curr);
    List<LinkedList<Integer>> addTo;
    if (powerset.get(copyArray.size()) == null) {
        List<LinkedList<Integer>> newList = new LinkedList<>();
        addTo = newList;
    } else {
        addTo = powerset.get(copyArray.size());
    }
    addTo.add(copyArray);
    powerset.put(copyArray.size(), addTo);
}
private void createALLSpreadSheet(ArrayList<String> IPP, ArrayList<ArrayList<double[]>> PValueCell, ArrayList<ArrayList<double[]>> FCValueCell, ArrayList<ArrayList<Integer>> agreeCell) throws IOException{
         String filenam = "HOMERAll.xlsx";
         SpreadSheetController creator = new SpreadSheetController();
        String excelFilePath = System.getProperty("user.dir").toString() + "/" + filenam ;
        String excelFilePath2 = System.getProperty("user.dir").toString() + "/HomerHits.xlsx" ;
        creator.writeExcel(excelFilePath, IPProbe, PValueCell, FCValueCell, agreeCell);
        creator.writeHitsExcel(excelFilePath2, IPProbe, PValueCell, FCValueCell, agreeCell);
        
     }    

    private double sum(double a, double b){
         return a + b;
    }
    
    /**
     * Helper function to calculate the emperical alpha
     * @param sortedCombinedArray
     * @param pcutoff
     * @return 
     */
    private double EMPEnding(ArrayList<Double> sortedCombinedArray, double pcutoff){
        int arraySize = sortedCombinedArray.size();
        int Sort_Index = (int)Math.round(pcutoff *arraySize); 
        int SIndex_m2 =Math.min(Math.max(Sort_Index - 2, 1),(arraySize-4)) - 1;
        int SIndex_p2 = SIndex_m2+4 + 1;
        
        ArrayList<Double> PredictedFPR = new ArrayList<Double>();

        for(int i = 0; i < arraySize; i++){
            double value = (i+1.0)/arraySize;// must have the 1.0 to make it into a double
            PredictedFPR.add(value); 
        }
        
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i = SIndex_m2; i < SIndex_p2; i++){
            obs.add(Math.log10(PredictedFPR.get(i)),Math.log10(sortedCombinedArray.get(i)));

        }
        
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
        //TODO: DOUBLE CHECK FITTER NUMBERS BECAUSE NUMBERS ARE SWAPPER
        double[] coeff = fitter.fit(obs.toList());

        
        double x = Math.log10(pcutoff);
        double z;
        

        
         z = Math.pow(10,filter(x,coeff)); // Temp use for polyval (MATLAB uses a combination of filter and honer's algorithem but only filter is givving correct value atm)
         return z;
    }
    
    /**
     * Helper function to solve polyfit and polysolve
     * @param hpfPole
     * @param oriX
     * @return 
     */
    public  double filter(double hpfPole, double[] oriX){
         
         double[] b = new double[]{1,0};
         double[] a = new double[]{1, -hpfPole};
         double[] x = reverse(oriX);
         double[] y = new double[x.length];

         for (int n = 0; n < y.length; n++) {
             if(n-1 < 0){
                 y[n] = b[0]*x[n];
             }else{
                y[n]= b[0]*x[n]+b[1]*x[n-1]-a[1]*y[n-1]; 
             }

         }
         return y[x.length-1];

     }
    
    /**
     * Swap the coeff values
     * @param ori
     * @return 
     */
    private double[] reverse(double[] ori){
        double[] swapped = new double[ori.length];
        //double placeholder = 0;
        for(int i = 0; i < ori.length; i++){
            swapped[i] = ori[ori.length - 1 - i];
        }
        return swapped;
    } 
    private ArrayList<Double> combineList(ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>> listOfChangePVal){
        //int tempcounter = 0;
        ArrayList<Double> combinedList = new ArrayList<Double>(); 
        for(int i = 0; i < listOfChangePVal.size(); i ++){
            for(int j = 0; j < listOfChangePVal.get(i).size();j++){
                for(int k = 0; k < listOfChangePVal.get(i).get(j).size(); k++){
                    for(int l = 0; l < listOfChangePVal.get(i).get(j).get(k).size(); l++){
                        for(int m = 0; m < listOfChangePVal.get(i).get(j).get(k).get(l).size(); m ++){
                        //System.out.println(listOfChangePVal.get(i).get(j).get(k).get(l));
                            combinedList.add(listOfChangePVal.get(i).get(j).get(k).get(l).get(m));
                            //tempcounter++;
                        }
                    }
                }
            }
        }
        //System.out.println(tempcounter + " " + listOfChangePVal.size() + " " + listOfChangePVal.get(0).size() + " " + listOfChangePVal.get(0).get(0).size() + " " +listOfChangePVal.get(0).get(0).get(0).size()+ " " +listOfChangePVal.get(0).get(0).get(0).get(0).size());
        return combinedList;
    }
    
    /**
     * Get the median values 
     * @param repPvalCell
     * @return 
     */
    private double getMedian(ArrayList<Double> repPvalCell){
        //sort list by columns instead of rows - temp solution, will need to reorder with full data
        //double[] temp = new double[] {0.0107082492071139,0.7103777383848,0.00227928946768836,0.496897459699094,0.122722551529073,0.917547040519757,0.257240059239913,0.721076375137033,0.0235350767985618,0.528909423992798};
        // sizes of the arrays 20 by 17
        ArrayList<Double> clone = cloneArrayList(repPvalCell);
        
        Collections.sort(clone);
        double prob_medError = 0;
        if (repPvalCell.size() % 2 == 0){
             prob_medError = ((double)clone.get(clone.size()/2) + (double)clone.get(clone.size()/2 - 1))/2;
        }else{
             prob_medError = (double) clone.get(clone.size()/2);
        }
        //Get the medium of all the valuess in the first experiment
        //System.out.println("Median is : " + prob_medError);
        return prob_medError;
    }
    
    /**
     * Deep copy of ArrayList
     * @param originalArrayList
     * @return 
     */
    private ArrayList<Double> cloneArrayList (ArrayList<Double> originalArrayList){
        ArrayList<Double> clone = new ArrayList<Double>();
        for(int i = 0; i < originalArrayList.size(); i ++ ){
            clone.add(originalArrayList.get(i));
        }
        return clone;
    }

    
    /**
     * Calculate the EMPADJPvalue
     * @param experiments
     * @param MHpvalue
     * @param NRepValues 
     */
    private void EmpAdjPValueList(int experiments, double MHpvalue,double NRepValues){
        //Binom Coeff Cell 
        double[][][] BCC =
                         {{{1,0,0},{0,0,1}},
                          {{2,0,0},{0,0,2},{1,1,0},{1,0,1},{0,1,1}},
                          {{3,0,0},{0,0,3},{2,1,0},{2,0,1},{0,1,2},{1,0,2},{1,2,0},{1,1,1},{0,2,1}},
                          {{4,0,0},{0,0,4},{3,1,0},{3,0,1},{0,1,3},{1,0,3},{2,2,0},{2,1,1},{2,0,2},{0,2,2},{1,1,2},{1,3,0},{1,2,1},{0,3,1}},
                          {{5,0,0},{0,0,5},{4,1,0},{4,0,1},{0,1,4},{1,0,4},{3,2,0},{3,1,1},{3,0,2},{0,2,3},{1,1,3},{2,0,3},{2,3,0},{2,2,1},{2,1,2},{1,2,2},{0,3,2},{1,4,0},{1,3,1},{0,4,1}},
                          {{6,0,0},{0,0,6},{5,1,0},{5,0,1},{0,1,5},{1,0,5},{4,2,0},{4,1,1},{4,0,2},{0,2,4},{1,1,4},{2,0,4},{3,3,0},{3,2,1},{3,1,2},{3,0,3},{0,3,3},{1,2,3},{2,1,3},{2,4,0},{2,3,1},{2,2,2},{0,4,2},{1,3,2},{1,5,0},{1,4,1},{0,5,1}},
                          {{7,0,0},{0,0,7},{6,1,0},{6,0,1},{0,1,6},{1,0,6},{5,2,0},{5,1,1},{5,0,2},{0,2,5},{1,1,5},{2,0,5},{4,3,0},{4,2,1},{4,1,2},{4,0,3},{0,3,4},{1,2,4},{2,1,4},{3,0,4},{3,4,0},{3,3,1},{3,2,2},{3,1,3},{0,4,3},{1,3,3},{2,2,3},{2,5,0},{2,4,1},{2,3,2},{0,5,2},{1,4,2},{1,6,0},{1,5,1},{0,6,1}},
                          {{8,0,0},{0,0,8},{7,1,0},{7,0,1},{0,1,7},{1,0,7},{6,2,0},{6,1,1},{6,0,2},{0,2,6},{1,1,6},{2,0,6},{5,3,0},{5,2,1},{5,1,2},{5,0,3},{0,3,5},{1,2,5},{2,1,5},{3,0,5},{4,4,0},{4,3,1},{4,2,2},{4,1,3},{4,0,4},{1,3,4},{2,2,4},{3,1,4},{3,5,0},{3,4,1},{3,3,2},{3,2,3},{0,5,3},{1,4,3},{2,6,0},{2,5,1},{2,4,2},{0,6,2},{1,5,2},{1,7,0},{1,6,1},{0,7,1}},
                          };

        //uses bCCHelper and permute helper methods
        double[][][] binomCoeffCell = new double[experiments][][]; //replaces hardcode above

        for(int i = 0; i < experiments; i++)
        {
            binomCoeffCell[i] = new double[i + 1][];
            binomCoeffCell[i] = bCCHelper(i+1);
        }

        pOutValList = new ArrayList<ArrayList<ArrayList<Double>>>();
        //BinomPvalue Calulation 
        for(int i = 0; i < experiments; i++){
            pOutValList.add(BinomPValue(BCC[i],i+1, MHpvalue));
       }
    }


    ////// helper method that fills the binomial coefficient cell (binomCoeffCell)
    public static double[][] bCCHelper(double operand)
    {
        Vector<double[]> growingStorage = new Vector<>(1); //stores non-duplicate array permutations
        double[] biCoeff = {operand,0,0}; //first zero-remainder permutation
        double[] biCoeff2 = {0.0, 0.0, operand}; //second zero-remainder permutation
        double[] lastBiCoeff = biCoeff2.clone(); //stores last attempted permutation
        double decOperand = operand; //operand value that decrements until zero
        double remainder = 0; // the difference between decOperand and operand
        boolean goRight = false; //keeps track of permutation direction

        //adds zero remainder permutations to storage vector
        growingStorage.add(biCoeff);
        growingStorage.add(biCoeff2);

        while(operand != remainder)//permute and fill storage vector
        {
            double[] biCoeff3; //stores previous permuted sequence

            if(decOperand == operand)
            {
                biCoeff3 = biCoeff2.clone();
            }
            else
            {
                biCoeff3 = lastBiCoeff.clone();
            }

            // method call to permute binomial coefficient
            Pair<double[], Boolean> returned = permute(biCoeff3, operand, remainder, goRight);

            if(returned == null) //invalid permutation sequence
            {
                break;
            }
            goRight = returned.getValue();
            biCoeff3 = returned.getKey();
            lastBiCoeff = biCoeff3.clone();

            if(goRight)
            {
                remainder = biCoeff3[1] + biCoeff3[2];
            }
            else
            {
                remainder = biCoeff3[0] + biCoeff3[1];
            }

            //check for duplicates here before adding to growing storage.
            boolean duplicate = false;
            double[] arrayDuplicates;

            for(int i = 0; i < growingStorage.size(); i++)
            {
                arrayDuplicates = growingStorage.get(i).clone();
                if( Arrays.equals(arrayDuplicates, biCoeff3))
                {
                    duplicate = true;
                    break;
                }
            }

            if(!duplicate) //no duplicates
            {
                growingStorage.add(biCoeff3);
            }
            decOperand--;
        }

        //change to array form for return
        double[][] returnArray = new double[growingStorage.size()][];
        growingStorage.toArray(returnArray);

        return returnArray;
    }


    // permutes binomial coefficient values to be added to the sequence of the binomial coefficient cell
    public static Pair<double[], Boolean> permute(double[] biCoeff, double operand, double remainder, boolean goRight)
    {
        if(operand <= 0 ) ////check for invalid operand
            return null;

        double operandBase = operand - remainder;
        double left = biCoeff[0];
        double center = biCoeff[1];
        double right = biCoeff[2];

        if(left == 0 && right == 1) // end of sequence for the operand
        {
            return null;
        }

        if(center == 0) // flips direction and values
        {
            if(goRight)
            {
                center = right;
                right = left;
                left = 0;
                goRight = false;
            }
            else
            {
                left = operandBase - 1;
                remainder++;
                center = remainder;
                right = 0;
                goRight = true;

                if(left <= 0) //end of this sequence
                {
                    return null;
                }
            }
        }
        else //permutation
        {
            center--;

            if(goRight)
            {
                right++;
            }
            else
            {
                left++;
            }
        }
        double[] returnArray = {left, center, right};
        return new Pair<>(returnArray, goRight);
    }





   
       
    /**
     * BinomPvalue calculation
     * @param BCC
     * @param N
     * @param value_1
     * @return 
     */
     private ArrayList<ArrayList<Double>> BinomPValue(double[][] BCC, double N, double value_1 ){
        pOutList = new ArrayList<ArrayList<Double>>();
        testList = new ArrayList<ArrayList<ArrayList<Double>>>();
        toalNonZero = new ArrayList<Integer>(BCC.length);
        uniqueCoeff = new ArrayList<ArrayList<Double>>();
        ArrayList F_J = new ArrayList<Double>();
        PValueList = new ArrayList<ArrayList<Double>>();
        for(int i = 0; i < N ; i++){

            coEfList = new ArrayList<ArrayList<Double>>();
            //Inclue the previous CoEff that were non zeros 
            if(!uniqueCoeff.isEmpty()){
                for(int temp = 0; temp < uniqueCoeff.size(); temp++){
                    if(!coEfList.contains(uniqueCoeff.get(temp))){
                        coEfList.add(uniqueCoeff.get(temp));
                
                    }
                }
            }
            
            valuesList = new ArrayList<Double>();
            for(int j = 0; j <= i; j++){ 
                ArrayList<Integer> nonZeroRows = new ArrayList<Integer>(BCC.length);
                
                
                //Find all the rows that match N
                ArrayList nF_J = find(nonZeroRows, BCC, N, j);
                //someitimes it will return an empty list, only add to list if it isn't empty
                if(!nF_J.isEmpty()){         
                    F_J = nF_J; 
                }
                //Only add list if it isn't already in Array
                if(!coEfList.contains(F_J)){
                    coEfList.add(F_J);
                    uniqueCoeff.add(F_J);
                }

                valuesList.add(value_1/(i+1));
            }
            
            testList.add(coEfList);
            PValueList.add(valuesList);
            
        }

        double total =0 ;
        
        for(int alpha = 0; alpha < testList.size(); alpha++){
            ArrayList<Double> totalList = new ArrayList<Double>();
            for(int beta = 0; beta < testList.get(alpha).size(); beta++){
                ArrayList C_i = coEfList.get(beta);
                //Solve polynomial 
                total = solveit(C_i, BCC,N, PValueList.get(alpha).get(beta));
                totalList.add(total);
            }
            pOutList.add(totalList);
        }
        
       return pOutList;
    }
     double solved = 0;
     int index = 0;
     /**
      * Brute force method of solving polynomials 
      * @param CI
      * @param BCC
      * @param N
      * @param value_1
      * @return 
      */
    private double solveit (ArrayList<Integer> CI, double[][] BCC, double N, double value_1 ) {
        BrentSolver solver = new BrentSolver();
 
        UnivariateFunction f = new UnivariateFunction() {
             
            @Override
            public double value(double x) {
                double total = 0;
                
                //Start at given BCC location 
                int gamma = CI.get(0);
                for(int i = 0; i < CI.size(); i++){
                    if(i == 0){
                        total = (Math.pow(x/2, BCC[gamma][0])*Math.pow(1-x, BCC[gamma][1])*Math.pow(x/2, BCC[gamma][2])* (factorial(N)/(factorial(BCC[gamma][0]) * factorial(BCC[gamma][1]) * factorial(BCC[gamma][2]) )));
                    }
                    else{
                        total = (total + (Math.pow(x/2, BCC[gamma][0])*Math.pow(1-x, BCC[gamma][1])*Math.pow(x/2, BCC[gamma][2])* (factorial(N)/(factorial(BCC[gamma][0]) * factorial(BCC[gamma][1]) * factorial(BCC[gamma][2]) ))));
                    }
                    gamma++;
                }

                   total =  total - (value_1);          
                return total;
            }
        };

        //Brute force solution of solving for X - cannot change interval size as it will not be as accurate 
        double intervalStart = -10000;
        double intervalSize = 0.01;
        ArrayList<Double> listOfX = new ArrayList();
        while (intervalStart < 10000) {
            intervalStart+= intervalSize;
            if(Math.signum(f.value(intervalStart)) != Math.signum(f.value(intervalStart+intervalSize))) {
                solved = solver.solve(1000, f, intervalStart, intervalStart+intervalSize);

                //Add solution only if the value is positive 
                if(solved >=0 ){
                    listOfX.add(solved);
                }
            }
        }

        //returns the min out the all the possible solutions 
        try {
         solved =  Collections.min(listOfX);
        }
      catch (ClassCastException e) {
         
      }
        return solved;
    }
 
    /**
     * Helper factorial function
     * 
     */
    private static double factorial(double n){
        if(n ==0){
            return 1;
        }
        return n * factorial(n-1);
    }
    /*
        Helper function to find the max value
    */
    private static double findMax(double[][] BCC){
        double max = 0;
        for(int i = 0 ; i < BCC.length; i++){
            for(int j = 0; j < BCC[i].length; j++){
                
                if(BCC[i][j] > max){
                    max = BCC[i][j];
                }
            }
        }
        return max;
    }
      /**
     * Helper function to find all Zero in array?
     * @param A
     * @return 
     */
    public  ArrayList find(ArrayList nonZeroRows, double[][]BCC, double N, int start){
        N = N-1;

        for (int i = 0; i < BCC.length; i++){
                //Look at all the rows in column 1 and column 3 of BinomCoef
                
                for(int j = 0; j <= BCC[i].length; j++){

                    if(BCC[i][0] == N + 1 - start || BCC[i][2] == N + 1 - start)
                    {
                        if(!nonZeroRows.contains(i) && !toalNonZero.contains(i)){
                            toalNonZero.add(i);
                            nonZeroRows.add(i);
                        }
                    }
                }
            }
        return nonZeroRows;
    }
    
    /**
     * Return the two selected values to calculate pValue
     * @param experimentPos
     * @param plateIndex
     * @param sampleIndex
     * @param probeIndex
     * @param ProbesForOnePlate
     * @param orignalDataForOneExperiment
     * @param userInput
     * @return 
     */
     private HashMap<Integer, List<Double>> getANCForTwoProbe(int experimentPos, int plateIndex, int sampleIndex, int probeIndex, 
        ObservableList<probeTableData> ProbesForOnePlate,  List< HashMap<Integer, HashMap<Integer,   List<Integer>>>> orignalDataForOneExperiment, 
        UserInputForBeadPlate userInput) {
         
      HashMap<Integer, HashMap<Integer,   List<Integer>>> dataForOnePlate = orignalDataForOneExperiment.get(plateIndex);
        int numberOfSamples = userInput.getNumOfSamples();
        int numberOfReplicas = userInput.getNumOfReplicas();
        
       // System.out.println("experiment pos is " + experimentPos + ". plateIndex is  " +plateIndex + ". sampleIndex is " +sampleIndex +". probeIndex is " +  probeIndex);
        int probePos = probeIndex;
        int samplePos = sampleIndex+1;
        int prevWells = probePos *(numberOfSamples * numberOfReplicas); // previous wells to get right number of wells to start
        samplePos = prevWells + samplePos; //  well of sample
        int wellNo = getWellNoInXmlFiles(samplePos);
       // System.out.println("sample wellno is " +wellNo );
        HashMap<Integer,List<Integer>> sampleData = dataForOnePlate.get(wellNo);
        

        for(int j = 1; j < numberOfReplicas; j++)
        {
            int replicaPos = samplePos + j*numberOfSamples;
            wellNo = getWellNoInXmlFiles(replicaPos);
           // wellsForCalculate.add(dataMap.get(wellNo)); // well of relicas 
        }
        //System.out.println("replica wellno is " +wellNo );
        HashMap<Integer,List<Integer>> replicaData = dataForOnePlate.get(wellNo);
        
        HashMap<Integer, List<Double>> ANCForTwoProbe = CombineList(sampleData,replicaData);
        

       return ANCForTwoProbe;
     }
     
     /**
      * Combien the probes to calculate Pvalue
      * @param sampleData
      * @param replicaData
      * @return 
      */
    private HashMap<Integer, List<Double>> CombineList(HashMap<Integer, List<Integer>> sampleData, HashMap<Integer, List<Integer>> replicaData) {
         HashMap<Integer, List<Double>> res = new HashMap<>();
         for(int i = 0; i <analytes.size();i++)
         {
             int key = analytes.get(i).getRegionNumber();
             List<Double> sample = convertToDouble(sampleData.get(key));

             List<Double> replica = convertToDouble(replicaData.get(key));

             //Combine samples with replica
             List<Double> newList1 = new ArrayList<Double>(sample); 
             newList1.addAll(replica);
             res.put(key, newList1);


         }

         return res;
    }

 
    private HashMap<Integer, Double> getANCForOneProbe(int experimentPos, int plateIndex, int sampleIndex, int probeIndex, 
        ObservableList<probeTableData> ProbesForOnePlate,  List< HashMap<Integer, HashMap<Integer,   List<Integer>>>> orignalDataForOneExperiment, 
        UserInputForBeadPlate userInput) {
         HashMap<Integer, HashMap<Integer,   List<Integer>>> dataForOnePlate = orignalDataForOneExperiment.get(plateIndex);
        int numberOfSamples = userInput.getNumOfSamples();
        int numberOfReplicas = userInput.getNumOfReplicas();
        
       // System.out.println("experiment pos is " + experimentPos + ". plateIndex is  " +plateIndex + ". sampleIndex is " +sampleIndex +". probeIndex is " +  probeIndex);
        int probePos = probeIndex;
        int samplePos = sampleIndex+1;
        int prevWells = probePos *(numberOfSamples * numberOfReplicas); // previous wells to get right number of wells to start
        samplePos = prevWells + samplePos; //  well of sample
        int wellNo = getWellNoInXmlFiles(samplePos);
       // System.out.println("sample wellno is " +wellNo );
        HashMap<Integer,List<Integer>> sampleData = dataForOnePlate.get(wellNo);
        

        for(int j = 1; j < numberOfReplicas; j++)
        {
            int replicaPos = samplePos + j*numberOfSamples;
            wellNo = getWellNoInXmlFiles(replicaPos);
          
        }
        HashMap<Integer,List<Integer>> replicaData = dataForOnePlate.get(wellNo);
        
        HashMap<Integer, Double> ANCForOneProbe  = PValue(sampleData,replicaData);

       return ANCForOneProbe;
    }

        //helper function: calculate right well no in xml files. 
    // wells count in this project from col to col, wells count in xml files from row to row. 
    private int getWellNoInXmlFiles(int n)
    {
        int row  = n%8;
        int col = n/8 +1;
        if(row == 0) // the 8th row elements
        {
            row = 8;
            col = n/8;
        }        
        int res = (row -1)*12 + col;
        return res;
    }
    
    /**
     * Rearrage the Pvalues to combine the matrix
     * @param sampleData
     * @param replicaData
     * @return 
     */
private HashMap<Integer, Double>  CombinePValue(HashMap<Integer, List<Double>> sampleData, HashMap<Integer, List<Double>> replicaData) {
         HashMap<Integer, Double> res = new HashMap<>();
         for(int i = 0; i <analytes.size();i++)
         {
             int key = analytes.get(i).getRegionNumber();
             List<Double> sample = sampleData.get(key);

             List<Double> replica = replicaData.get(key);
            
             double pValue = getPValue( sample,  replica);
             
             //Combine samples with replica
             List<Double> newList1 = new ArrayList<Double>(sample); 
             newList1.addAll(replica);        
             
             double tempPvalue = printPValue(sample, replica);
             
             res.put(key,tempPvalue);
         }
         return res;
    }

    private HashMap<Integer, Double>  PValue(HashMap<Integer, List<Integer>> sampleData, HashMap<Integer, List<Integer>> replicaData) {
         HashMap<Integer, Double> res = new HashMap<>();
         for(int i = 0; i <analytes.size();i++)
         {
             int key = analytes.get(i).getRegionNumber();
             List<Double> sample = convertToDouble(sampleData.get(key));

             List<Double> replica = convertToDouble(replicaData.get(key));

             double pValue = getPValue( sample,  replica);
             
             //Combine samples with replica
             List<Double> newList1 = new ArrayList<Double>(sample); 
             newList1.addAll(replica);
             
             
             double tempPvalue = printPValue(sample, replica);
             
             res.put(key,tempPvalue);
         }
         return res;
    }

    private List<Double> convertToDouble(List<Integer> get) {
        List<Double> res = new ArrayList<>();
        for(int n : get)
            res.add((double)n);
        return res;
    }

    private double printPValue(List<Double> x1, List<Double> x2){
        KolmogorovSmirnovTest ks = new KolmogorovSmirnovTest();
        double[] data1 = new double[x1.size()];
        double[] data2 = new double[x2.size()];
        double pValue = 0;
        for(int i = 0; i < x1.size(); i++){
            data1[i] = x1.get(i);
        }
        for(int i = 0; i < x2.size(); i++){
            data2[i] = x2.get(i);
        }
        
        double KSstat = ks.kolmogorovSmirnovStatistic(data1, data2);
        double lam = getLambda(x1, x2, KSstat);
        pValue = calPValue(lam);
        //System.out.println(p );
        //System.out.println(p);
        return pValue;
    }
    
    //x1: sample, x2: replica
    private double getPValue(List<Double> x1, List<Double> x2) {
        List<Double> binEdges = getBinEdges(x1,x2);
        List<Double> binCounts1 = Histc(x1,binEdges,1);
        List<Double> binCounts2 = Histc(x2,binEdges,1);
        List<Double> sumCounts1 = getSumCounts(binCounts1);
        List<Double> sumCounts2 = getSumCounts(binCounts2);
        List<Double> deltaCDF = getDeltaCDF(sumCounts1 ,sumCounts2);
        double KSstatistic = Collections.max(deltaCDF);
        //System.out.println(KSstatistic);
        //System.out.println(deltaCDF);
        double lambda = getLambda(x1,x2,KSstatistic);
        double pValue = calPValue(lambda);
        pValue = Math.round(pValue*10000)/10000.0d;
        //System.out.println("PValue is : " + pValue);
        return pValue;
    }

    private List<Double> getBinEdges(List<Double> x1, List<Double> x2) {
        List<Double> res = new ArrayList<>();
        List<Double> merge = new ArrayList<>();
        merge.addAll(x1);
        merge.addAll(x2);
        Collections.sort(merge);
       res.add(Double.NEGATIVE_INFINITY);
       res.addAll(merge);
       res.add(Double.POSITIVE_INFINITY);
       return res;
    }

    private List<Double> Histc(List<Double> x, List<Double> binEdges, int n) {
        List<Double> res = new ArrayList<>();
        Collections.sort(x);
        
        double count = 0; // how many cur in x
        double cur = -1; // set cur to cur val if x contains multiple cur val. 
        for(int i = 0; i < binEdges.size(); i++)
        {
            if(cur != -1 )
            {
                if(i+1 < binEdges.size()  && binEdges.get(i+1)==cur)
                    res.add(0.0);
                else
                {
                    res.add(count);
                    count = 0;
                    cur = -1;
                }
            }
            else
            {
                double curVal= binEdges.get(i);
                if(x.contains(binEdges.get(i)))
                {
                    count = countVal(x,curVal);
                    if(countVal(binEdges,curVal) == 1)
                    {
                        res.add(count);
                    }
                    else
                    {
                        res.add(0.0);
                        cur = curVal;
                    }
                }
                else
                    res.add(0.0);

            }
        }
        return res;
    }

    private List<Double> getSumCounts(List<Double> x) {
           List<Double> res = new ArrayList<>();
           List<Double> cumsum = new ArrayList<>();
           
           //get cumsum
           double sum = 0;
           for(int i = 0; i <x.size();i++)
           {
               sum += x.get(i);
               cumsum.add(sum);
           }
           
           ////cumsum(binCounts1)./sum(binCounts1);
           for(int i = 0; i< x.size();i++)
           {
               double val = cumsum.get(i) / sum;
               res.add(val);
           }
           return res;
    }

    private List<Double> getDeltaCDF(List<Double> sumCounts1, List<Double> sumCounts2) {
          List<Double> res = new ArrayList<>();
          //calculate base on sampleCDFs
          for(int i = 0; i <sumCounts1.size()-1; i++)
          {
              res.add(Math.abs(sumCounts1.get(i)-sumCounts2.get(i)));
          }
          return res;
    }

    private double getLambda(List<Double> x1, List<Double> x2, double KSstatistic) {
         double n1 = x1.size();
         double n2 = x2.size();
         double n = (n1 * n2) / (n1 + n2);
         double sqrt = Math.sqrt(n) ;
         double ll =  (Math.sqrt(n) + 0.12 + 0.11/Math.sqrt(n))*KSstatistic;
         double lambda = Math.max((Math.sqrt(n) + 0.12 + 0.11/Math.sqrt(n))*KSstatistic, 0.0);
         return lambda;
    }

    private double calPValue(double lambda) {
        //j.^2
         List<Double> j = new ArrayList<>();
         List<Double> toBeExp = new ArrayList<>();
         List<Double> exp = new ArrayList<>(); //exp(-2*lambda*lambda*j.^2));
         List<Double> negJmenusOne = new ArrayList<>();
         List<Double> toBeSum = new ArrayList<>();
         
        double sum =0.0;
        for(double i = 1; i<=101; i++ )
        {
            j.add(i);
            toBeExp.add((-2)*lambda*lambda* (i*i));
            exp.add(Math.exp(toBeExp.get((int) (i-1))));
            
            //(-1).^(j-1)
            if((i-1)%2 == 0)
                negJmenusOne.add(1.0);
            else
                negJmenusOne.add(-1.0);
            
            //(-1).^(j-1).*exp(-2*lambda*lambda*j.^2)
            toBeSum.add(negJmenusOne.get((int) (i-1)) * exp.get((int) (i-1)));
            sum += toBeSum.get((int) (i-1));
        }
        double p = Math.min(Math.max(2*sum, 0), 1);
        return p;
    }

    private double countVal(List<Double> x, double n) {
        double count =1;
        int lastIndex = x.lastIndexOf(n);
        while( lastIndex!=0 && x.get(lastIndex-1) == n )
        {
            lastIndex--;
            count++;
        }
        return count;
    }

    private void displayANCforOneProbe(int platePos, int probeIndex, HashMap<Integer, Double> ANCForOneProbe) {
        //calclulate the col number to populate median value of this probe  = previous plate + cur Platepos
        int preProbes = 0; // if not platePos is not 1, it means it has previous probes of previous plates. 
        if(platePos>1)
        {
           preProbes = getPreProbes(platePos);      
        }
        int pos = preProbes + (probeIndex +1); 
        
       // final TextField[] textfields = new TextField[analytes.size()]; 
        for(int i = 1; i <= analytes.size();i++)
        {
            //set width for cells of the cols
            ColumnConstraints column = new ColumnConstraints(70);
            //gpForANC.getColumnConstraints().add(column);            
            Label label = new Label();
            //gpForANC.setRowIndex(label, i);
            //gpForANC.setColumnIndex(label,probeIndex);
            double ANC = ANCForOneProbe.get(analytes.get(i-1).getRegionNumber());
            label.setText(Double.toString(ANC));   
            colorCode(label,ANC);

            label.autosize();            
            //gpForANC.add(label,pos,i+1);
           // GridPane.setMargin(label, new Insets(0));
        }
    }

    private int getPreProbes(int platePos)
    {
        int preProbes = 0; 
        HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment);             
        for(int i = 1; i< platePos;i++ ) // don't count the current plate
         {
             preProbes += probesListForCurExperiment.get(i).size();
         }            
        return preProbes;
    }

    private void colorCode(Label label, double pVal) {
        // red color for pval<0.05
        double cutOff = 0.05;
        if(pVal < cutOff && pVal!=0)
        {
            int times = (int) (cutOff/pVal);
            if(times <= red.length)
                label.setStyle(red[times-1]);
            else
                label.setStyle(red[red.length-1]);
        }
        
         // blue color for pval>0.2
        double cutOff2 = 0.2;
        if(pVal > cutOff2)
        {
            int times = (int) ( pVal/ cutOff2);
            if(times <= blue.length)
                label.setStyle(blue[times-1]);
            else
                label.setStyle(blue[blue.length-1]);
        }
    }
    /**
         * used for Debugging to print out all the data 
         */
    private void displayCombinedData(){
        
        
        for(int i = 0; i < OriComboData.size(); i++){
             System.out.println("index " + i + " " +OriComboData.get(i).get(12).size() +" "+ OriComboData.get(i).get(8).size() + " " +OriComboData.get(i).get(13).size() +" "+ OriComboData.get(i).get(26).size() + " "+ OriComboData.get(i).get(28).size() +" "+ OriComboData.get(i).get(77).size() +" "+OriComboData.get(i).get(43).size() +" "+ OriComboData.get(i).get(30).size() +" "+OriComboData.get(i).get(15).size() +" "+ OriComboData.get(i).get(64).size() +" "+OriComboData.get(i).get(68).size() +" "+ OriComboData.get(i).get(85).size() +" "+OriComboData.get(i).get(45).size() +" "+ OriComboData.get(i).get(89).size() +" "+OriComboData.get(i).get(91).size() +" "+ OriComboData.get(i).get(47).size() +" "+OriComboData.get(i).get(96).size());
             if(i % 39 == 0){
                 System.out.println("New Experiment ");
             }
        }
        
        //Testing purposes to print out combined lists 
        
        for(int i = 0; i < ListComboData1.size();i++){
            for(int j = 0; j < ListComboData1.get(i).size(); j++ ){
             System.out.println(ListComboData1.get(i).get(j).get(12).size() +" "+ ListComboData1.get(i).get(j).get(8).size() + " " +ListComboData1.get(i).get(j).get(13).size() +" "+ ListComboData1.get(i).get(j).get(26).size() + " "+ ListComboData1.get(i).get(j).get(28).size() +" "+ ListComboData1.get(i).get(j).get(77).size() +" "+ListComboData1.get(i).get(j).get(43).size() +" "+ ListComboData1.get(i).get(j).get(30).size() +" "+ListComboData1.get(i).get(j).get(15).size() +" "+ ListComboData1.get(i).get(j).get(64).size() +" "+ListComboData1.get(i).get(j).get(68).size() +" "+ ListComboData1.get(i).get(j).get(85).size() +" "+ListComboData1.get(i).get(j).get(45).size() +" "+ ListComboData1.get(i).get(j).get(89).size() +" "+ListComboData1.get(i).get(j).get(91).size() +" "+ ListComboData1.get(i).get(j).get(47).size() +" "+ListComboData1.get(i).get(j).get(96).size());
            }
            System.out.println("New Experiment ");
        }
        
        for(int i = 0; i < ListComboData2.size();i++){
            for(int j = 0; j < ListComboData2.get(i).size(); j++ ){
             System.out.println(ListComboData2.get(i).get(j).get(12).size() +" "+ ListComboData2.get(i).get(j).get(8).size() + " " +ListComboData2.get(i).get(j).get(13).size() +" "+ ListComboData2.get(i).get(j).get(26).size() + " "+ ListComboData2.get(i).get(j).get(28).size() +" "+ ListComboData2.get(i).get(j).get(77).size() +" "+ListComboData2.get(i).get(j).get(43).size() +" "+ ListComboData2.get(i).get(j).get(30).size() +" "+ListComboData2.get(i).get(j).get(15).size() +" "+ ListComboData2.get(i).get(j).get(64).size() +" "+ListComboData2.get(i).get(j).get(68).size() +" "+ ListComboData2.get(i).get(j).get(85).size() +" "+ListComboData2.get(i).get(j).get(45).size() +" "+ ListComboData2.get(i).get(j).get(89).size() +" "+ListComboData2.get(i).get(j).get(91).size() +" "+ ListComboData2.get(i).get(j).get(47).size() +" "+ListComboData2.get(i).get(j).get(96).size());
            }
            System.out.println("New Experiment ");
        }
        
        System.out.println("Size of Experiment" + ListComboData1.get(1).size());
        
    }
    
    /**
     * Return fold change values 
     * @return 
     */
    private HashMap<Integer,List<List<HashMap<Integer,Double>>>> getFoldChange(){
        HashMap<Integer,List<List<HashMap<Integer,Double>>>> fc = new HashMap<Integer,List<List<HashMap<Integer,Double>>>>();
        
        for(int i = 0; i < experiments; i ++){
            int[] combo = comboList.get(i+1);
             fc.put(i+1, calculateFoldChange(i+1,combo[0],i+1,combo[1]));
        }
        
        return fc;
    }
    /**
       * Get fold change values to calculate ANC 
       */ 
    private List<List<HashMap<Integer, Double>>> calculateFoldChange(int experimementPos1, int sampleIndex1, int experimementPos2, int sampleIndex2) {

       List<HashMap<Integer,Double>> mv1ForOnePlate = new ArrayList<>();
       List<HashMap<Integer,Double>>  mv2ForOnePlate  = new ArrayList<>();
       
       
      
      List<List<HashMap<Integer,Double>>> foldChange = new ArrayList<>();
      fcplates = Math.min(ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos1).size(), 
              ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos2).size()); //get the samller size of plates 
      fcprobes = getSmallestProbes(experimementPos1,experimementPos2,fcplates);
       for(int i = 0; i < fcplates; i++)
       {
           List<HashMap<Integer,Double>> plate = new ArrayList<>();
           mv1ForOnePlate = ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos1).get(i).get(sampleIndex1);
           mv2ForOnePlate = ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos2).get(i).get(sampleIndex2);
           for(int k = 0; k< fcprobes; k++)
           {
                HashMap<Integer,Double>  probe = new HashMap<>();
                for(int j = 0; j <analytes.size();j++)
                {
                    int regionNumber = analytes.get(j).getRegionNumber();
                    double mv1 = mv1ForOnePlate.get(k).get(regionNumber);
                    double mv2 = mv2ForOnePlate.get(k).get(regionNumber);                   
                    double fc = Math.log(mv1ForOnePlate.get(k).get(regionNumber)) / Math.log(2)  - Math.log(mv2ForOnePlate.get(k).get(regionNumber)) / Math.log(2);
                    probe.put(regionNumber, Math.round(fc*1000.0)/1000.0);
                }      
                plate.add(probe);
           }
           //ModelForExperiments.getInstance().setFoldChangeMatrixforANC(foldChangeANC);
           foldChange.add(plate);

       }
             
       return foldChange;
    }
    
    /**
     * Helper function to get fold change values
     * @param experimementPos1
     * @param experimementPos2
     * @param plates
     * @return 
     */
    private int getSmallestProbes(int experimementPos1, int experimementPos2, int plates)
    {
         List<UserInputForBeadPlate> userInputs1 = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experimementPos1);
         List<UserInputForBeadPlate> userInputs2 = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experimementPos2);
         int res = userInputs1.get(0).getNumOfProbes();
         for(int i  = 0 ; i <plates;i++)
         {
             res = Math.min(res, userInputs1.get(i).getNumOfProbes());
             res = Math.min(res, userInputs2.get(i).getNumOfProbes());
         }
        
        return res;
    }
    
    /**
     * Select the two fold change matrix to be combined
     * @param foldchange
     * @return 
     */
    private HashMap<Integer,List<HashMap<Integer,Double>>> combineFoldChange(HashMap<Integer,List<List<HashMap<Integer,Double>>>> foldchange){
        HashMap<Integer,List<HashMap<Integer,Double>>> combinedAll = new HashMap<>();
        for(int i = 0; i < foldchange.size(); i++)
        {
            List<HashMap<Integer,Double>> comList = new ArrayList();
            for(int j = 0 ; j < foldchange.get(i+1).size(); j++)
            {
                
                for(int k = 0; k < foldchange.get(i+1).get(j).size();k++){
                    comList.add(foldchange.get(i+1).get(j).get(k));
                }
            }
            combinedAll.put(i+1,comList);
        }
        return combinedAll;
    }
    
    /*
    Cobine separate arrays for the fold change values 
    */
    private ArrayList<Double> combineAll(ArrayList<ArrayList<Double>> separateArray){
        ArrayList<Double> combinedAll = new ArrayList<Double>();
        for(int i = 0; i < separateArray.size();i++)
        {
            for(int j = 0; j < separateArray.get(i).size(); j++){
                double value = separateArray.get(i).get(j);
                combinedAll.add(value);
            }
        }
        return combinedAll;
    }
    
    /**
     * create Pvalue cells 
     * @param plateSize
     * @param conditionSize 
     */
    private void CreatePValueCells(int plateSize, int conditionSize){
        
        int[] combo;
        /**Combination of all the data
         * Hard coded to combine the following:
         * Experiment 1 Sample 1(0-10, 50-60) and 3(20-30,70-80)
         * Experiment 2 Sample 3 and 1
         * Experiment 3 Sample 2 and 4 (170-180, 210-220) and (190-200,230-240)
         * Experiment 4 Sample 4 and 2 (270-280,310-320) and (250-260, 290-300)
        */
      
        int plate = 1;
        int sample = 1;
        for(int e = 0; e < ListOriComboData.size(); e++){
            combo = comboList.get(e+1);
            ArrayList<HashMap<Integer, List<Double>>> ComboData1= new ArrayList<HashMap<Integer, List<Double>>>();
            ArrayList<HashMap<Integer, List<Double>>> ComboData2= new ArrayList<HashMap<Integer, List<Double>>>();
            for(int i = 0; i < ListOriComboData.get(e).size();i++)
            {
               switch(e) {
                   case 0:
                       if(plate <= plateSize){
                        if(sample <=conditionSize ) {
                             ComboData1.add(ListOriComboData.get(e).get(i));
                              //System.out.println("index " + i + " sample " + sample + " " +ListOriComboData.get(e).get(i).get(12).size() +" "+ ListOriComboData.get(e).get(i).get(8).size() + " " +ListOriComboData.get(e).get(i).get(13).size() +" "+ ListOriComboData.get(e).get(i).get(26).size() + " "+ ListOriComboData.get(e).get(i).get(28).size() +" "+ ListOriComboData.get(e).get(i).get(77).size() +" "+ListOriComboData.get(e).get(i).get(43).size() +" "+ ListOriComboData.get(e).get(i).get(30).size() +" "+ListOriComboData.get(e).get(i).get(15).size() +" "+ ListOriComboData.get(e).get(i).get(64).size() +" "+ListOriComboData.get(e).get(i).get(68).size() +" "+ ListOriComboData.get(e).get(i).get(85).size() +" "+ListOriComboData.get(e).get(i).get(45).size() +" "+ ListOriComboData.get(e).get(i).get(89).size() +" "+ListOriComboData.get(e).get(i).get(91).size() +" "+ ListOriComboData.get(e).get(i).get(47).size() +" "+ListOriComboData.get(e).get(i).get(96).size());
                            }
                        }
                        else {
                        if(sample <=conditionSize ) {
                             ComboData2.add(ListOriComboData.get(e).get(i));
                            }
                        }
                    break;
                   
                   case 1:
                    if(plate <= plateSize){
                        if(sample <=conditionSize ){
                             ComboData2.add(ListOriComboData.get(e).get(i));
                        }
                    }
                    else{
                        if(sample <=conditionSize ){
                             ComboData1.add(ListOriComboData.get(e).get(i));
                        }
                    }  
                    break;
                   
                   case 2:
                    if(plate <= plateSize){
                        if(sample > conditionSize ){
                             ComboData1.add(ListOriComboData.get(e).get(i));
                              //System.out.println("index " + i + " sample " + sample + " " +ListOriComboData.get(e).get(i).get(12).size() +" "+ ListOriComboData.get(e).get(i).get(8).size() + " " +ListOriComboData.get(e).get(i).get(13).size() +" "+ ListOriComboData.get(e).get(i).get(26).size() + " "+ ListOriComboData.get(e).get(i).get(28).size() +" "+ ListOriComboData.get(e).get(i).get(77).size() +" "+ListOriComboData.get(e).get(i).get(43).size() +" "+ ListOriComboData.get(e).get(i).get(30).size() +" "+ListOriComboData.get(e).get(i).get(15).size() +" "+ ListOriComboData.get(e).get(i).get(64).size() +" "+ListOriComboData.get(e).get(i).get(68).size() +" "+ ListOriComboData.get(e).get(i).get(85).size() +" "+ListOriComboData.get(e).get(i).get(45).size() +" "+ ListOriComboData.get(e).get(i).get(89).size() +" "+ListOriComboData.get(e).get(i).get(91).size() +" "+ ListOriComboData.get(e).get(i).get(47).size() +" "+ListOriComboData.get(e).get(i).get(96).size());
                        }
                    }
                    else{
                        if(sample > conditionSize ){
                             ComboData2.add(ListOriComboData.get(e).get(i));
                        }
                    }   
                    break;
                   
                   case 3:
                    if(plate <= plateSize){
                        if(sample > conditionSize ){
                             ComboData2.add(ListOriComboData.get(e).get(i));
                              //System.out.println("index " + i + " sample " + sample + " " +ListOriComboData.get(e).get(i).get(12).size() +" "+ ListOriComboData.get(e).get(i).get(8).size() + " " +ListOriComboData.get(e).get(i).get(13).size() +" "+ ListOriComboData.get(e).get(i).get(26).size() + " "+ ListOriComboData.get(e).get(i).get(28).size() +" "+ ListOriComboData.get(e).get(i).get(77).size() +" "+ListOriComboData.get(e).get(i).get(43).size() +" "+ ListOriComboData.get(e).get(i).get(30).size() +" "+ListOriComboData.get(e).get(i).get(15).size() +" "+ ListOriComboData.get(e).get(i).get(64).size() +" "+ListOriComboData.get(e).get(i).get(68).size() +" "+ ListOriComboData.get(e).get(i).get(85).size() +" "+ListOriComboData.get(e).get(i).get(45).size() +" "+ ListOriComboData.get(e).get(i).get(89).size() +" "+ListOriComboData.get(e).get(i).get(91).size() +" "+ ListOriComboData.get(e).get(i).get(47).size() +" "+ListOriComboData.get(e).get(i).get(96).size());
                        }
                    }
                    else{
                        if(sample > conditionSize ){
                             ComboData1.add(ListOriComboData.get(e).get(i));
                             //System.out.println(sample);
                        }
                    }
                    break;
               }

                plate++;
                sample++;
                if(sample == (conditionSize*2 +1)){
                    sample = 1;
                }
                if(plate == (plateSize*2 +1)){    
                    plate = 1;

                }       
            }
                ListComboData1.add(ComboData1);
                ListComboData2.add(ComboData2);
        }
        
        
    }
    
    /**
     * Combine the values of the final changed list of Pvalues
     * @param finallistOfChangePVal
     * @return 
     */
    private ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>>> CreateCombinedPValueCells( ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>>> finallistOfChangePVal){
         /**Combination of all the data
         * Hard coded to combine the following:
         * Experiment 1 Sample 1 and 3
         * Experiment 2 Sample 3 and 1
         * Experiment 3 Sample 2 and 4
         * Experiment 4 Sample 4 and 2
        */

        for(int i = 1; i <=experiments;i++)
        {
            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>> listOfChangePVal = new ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>>();//List of values per experiment
            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>> tempChangePVal = new ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>>(); //Used to hold the temp array that will be added on later 
            
            List<UserInputForBeadPlate> inputs =  ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(i);
            int numberOfPlates = inputs.size();
            UserInputForBeadPlate input = inputs.get(0);//Used to get the first plate index this is hard coded witht he assumption that the plate number sizes does not change with experiments 
        
            int numberOfSamples  = input.getNumOfSamples();
            for(int k = 0; k<numberOfSamples; k++ )
            {   
                
                ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> repPvaluePerExp = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();     
                 
                
                    for(int j = 0; j < numberOfPlates; j++)
                    {
                       ArrayList<ArrayList<ArrayList<Double>>> listOfplates = new ArrayList<ArrayList<ArrayList<Double>>> ();
                        int numberOfProbes = input.getNumOfProbes(); 
                        for(int x = 0; x<numberOfProbes; x++)
                        {
                            ArrayList<ArrayList<Double>>  repvaluePlates = new ArrayList<ArrayList<Double>> ();
                            ArrayList<Double> repPvalCell = new ArrayList<Double>();
                            for(int a = 0; a < analytes.size();a++){
                                //
                                HashMap<Integer, Double> finalANCForOneProbe  = ModelForExperiments.getInstance().getOneProbeDataForANC(i, j,  k, x);
                                double valueToCal = finalANCForOneProbe.get(analytes.get(a).getRegionNumber());
                                repPvalCell.add(valueToCal);
                                //System.out.println(repPvalCell); 
                            }
                            double prob_medError = getMedian(repPvalCell);
                            //Filter out the list 
                            if (prob_medError > 0.05/IPNums){
                                //Flitered list is the first column copied
                                  repvaluePlates.add(repPvalCell);

                            }else{
                                    String errorMed = "Exp" + i +  "_Cond" + j + "_Probe#" + x + "_MedianError=" + prob_medError; 
                                    BadProbeCell.add(errorMed);
                             }

                             listOfplates.add(repvaluePlates);
                        }
                        
                         repPvaluePerExp.add(listOfplates);
                    }                    
                /**
                 * TODO change logic to apply to samples and experiment ordering  
                 */
                if(i == 1 && (k == 0 || k == 2)){
                    listOfChangePVal.add(repPvaluePerExp);
                }else if(i == 2){
                    if(k == 0){
                        tempChangePVal.add(repPvaluePerExp);
                    }else if(k == 2){
                        listOfChangePVal.add(repPvaluePerExp);
                        listOfChangePVal.add(tempChangePVal.get(0));
                    }
                }else if(i == 3 && (k == 1 || k == 3)){
                    listOfChangePVal.add(repPvaluePerExp);
                }else if(i == 4){
                    if(k == 1){
                        tempChangePVal.add(repPvaluePerExp);
                    }else if(k == 3){
                        listOfChangePVal.add(repPvaluePerExp);
                        listOfChangePVal.add(tempChangePVal.get(0));
                    }
                }
                
            }
                finallistOfChangePVal.add(listOfChangePVal);
                
        }
        return finallistOfChangePVal;
    }

}
