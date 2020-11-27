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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

//import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author feiping
 */
public class FoldChangeController implements Initializable {

    @FXML
    private ScrollPane sp1;
    @FXML
    private GridPane gridPane;
    @FXML
    private GridPane platesGridPane;
    @FXML
    private ScrollPane spForFoldChange;
    int largestSamples = 0;
    int experiments = 0;
    private  HashMap<Integer, List<Integer>> mapOfSamplesNumbers = new HashMap<>();
    ObservableList<bead> analytes = FXCollections.observableArrayList();
    //List<List<Integer>> choosenSamples = new ArrayList<>(); 
    
    //ToggleGroup group = new ToggleGroup(); // For manage radio buttons for users to select. 
    List<RadioButton> experimentButtons = new ArrayList<>(); // list of buttons to select an experiment
    //List<RadioButton> radioButtons = new ArrayList<RadioButton>();
    List<ArrayList<RadioButton>> radioButtons = new ArrayList<ArrayList<RadioButton>>();

    List<RadioButton> selectedRBs = new ArrayList<>();
    List<RadioButton> selectedEBs = new ArrayList<>();
    int plates = 0; // samlleset plate count for the two slected samples 
    int probes = 0; // samlleset probes for the two slected samples. 
    String sample1 ="";
    String sample2 = "";
    String[] sampleNames; // names of samples for button labels

    @FXML
    private Text sample1Name;
    @FXML
    private Text sample2Name;
    private double defaultCutOffValue = 0;
    @FXML
    private TextField cutOffValueBox;
    List<List<HashMap<Integer,Double>>> foldChangeMatrix ;
    String[] red = {" -fx-background-color: rgb(255, 235, 230);"," -fx-background-color: rgb(255, 214, 204);",
        " -fx-background-color: rgb(255, 133, 102);"," -fx-background-color: rgb(255, 92, 51);"};
    
    String[] blue = {" -fx-background-color: rgb(235, 235, 250);"," -fx-background-color: rgb(214,214,245);",
     " -fx-background-color: rgb(173,173,245);"," -fx-background-color: rgb(112,112,219);"};
    
    
    private HashMap<Integer,List<List<HashMap<Integer,Double>>>> getfoldChange = new HashMap<>();
    private ArrayList<HashMap<Integer,List<HashMap<Integer,Double>>>> foldChangeANC = new ArrayList();
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

        
        tableRow(experiments);    
        tableCol(largestSamples);
        fillRadioButton();
        sp1.setContent(gridPane); // add scroll bars to the grid pane. 
        spForFoldChange.setContent(platesGridPane);
        

        
    }

    // updates sample names from HomepageController.java
    public void sampleNameRetriever(String[] names)
    {
        sampleNames = names;
        
        // Amman: 'added print statements for debugging purposes'
        for(int i = 0; i < sampleNames.length; i++) {
            System.out.println("Sample " + i + ": " + sampleNames[i]);
        }
    }

   //dynamically add rows for gridpane base on previous user input. 
    public  GridPane tableRow(int rows){
    for(int i=1; i<=rows; i++){
        //set width for cells of the cols
        ColumnConstraints column = new ColumnConstraints(100);
        gridPane.getColumnConstraints().add(column);
        Label label = new Label();
        String s = "  Experiment " + i;
        //label.setText(s);    
        //textField.setAlignment(Pos.CENTER);
        label.autosize();
        gridPane.add(label,0,i);
     }
    return gridPane;
    }

     //dynamically add cols for gridpane base on previous user input. 
    public  void tableCol(int col){
        
    for(int i=1; i<=col; i++){
        //set width for cells of the cols
        ColumnConstraints column = new ColumnConstraints(100);
        gridPane.getColumnConstraints().add(column);        
        Label label = new Label();
        String s = "Sample  " + i;
        label.setText(s);    
        label.autosize();
        //add them to the GridPane
        gridPane.add(label,i,0);
        // margins are up to your preference
        GridPane.setMargin(label, new Insets(5));
     }
    }  
    
    /*
    * precondtions: two radio buttons have been clicked on.
    * helper method for fillRadioButton() that checks if two
    * radio buttons that are part of the selectedRBs list are
    * actually filled in.
    *
    * NOTE: this is necessary to make the two samples being compared
    * more clear, as it used to be possible to compare two samples
    * when one of their radio buttons wasn't actually filled in,
    * this may have looked confusing to the end users.
    */
    private boolean radioButtonsSelected() {
        return selectedRBs.get(0).isSelected() && selectedRBs.get(1).isSelected();
    }
    
    // testing method
    private String buttonsSelected() {
        return  selectedRBs.get(0).isSelected() + ", " + selectedRBs.get(1).isSelected();
    }
       
    /*
    * This function deals with creating the radio buttons for each experiment and
    * their samples. It ensures that only two samples from the same experiment can
    * be compared.
    */
    private void fillRadioButton() {
        
        // loop through each experiment
        for(int i = 1; i <= experiments; i++)
        {
            radioButtons.add(new ArrayList<RadioButton>()); // allocate space for a list of buttons
            
            RadioButton expBtn = new RadioButton(); // expBtn is for selecting an experiment
            expBtn.setText("Exp. " + i); // abbreviated experiment so double digit numbers are visible
            expBtn.setAlignment(Pos.CENTER); // centers text
            experimentButtons.add(expBtn); // add the button to a list of radio buttons
            gridPane.add(expBtn, 0, i);
            GridPane.setMargin(expBtn, new Insets(10));
            
            // loop through each sample in experiment i
            for(int j = 1; j <=getLargestSampleCountForOneExperiment(i); j++)
            {
                //curSample = j;
                RadioButton btn = new RadioButton(); // btn is for selecting a sample
                btn.setDisable(true); // buttons should only be enabled when corresponding experiment button is selected
                // sampleNameRetriever(sampleNames);
                btn.setText(sampleNames[j - 1]); //TODO***********************************************
                btn.setAlignment(Pos.CENTER);
                       
                // call this function when a sample radio button is clicked on
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(btn.isSelected()){
                            
                            // if 1 or no sample buttons are already selected, evaluates to true
                            if(selectedRBs.size() < 2)
                            {
                                    selectedRBs.add(btn); // add to list
                                    //int row = gridPane.getRowIndex(btn);
                                    //int col = gridPane.getColumnIndex(btn);
                                    //choosenSamples.add(new ArrayList<Integer>(Arrays.asList(row,col)));
                                }
                            // if two sample buttons are already selected, must deselect last added button
                            else
                            {
                                selectedRBs.get(0).setSelected(false); // set the 1st element unselected
                                selectedRBs.remove(0); // remove 1st element from the selected list
                                //choosenSamples.remove(0);
                                selectedRBs.add(btn); // add newly selected button
                            }
                            
                            // perform calculations if two sample buttons are selected
                            if(selectedRBs.size() == 2 /*&& radioButtonsSelected()*/)
                                {
                                    defaultCutOffValue = Double.parseDouble(cutOffValueBox.getText());
                                    showFoldChange(defaultCutOffValue);
                                }
                        }
                        
                        // in case button has been deselected, we want to remove it from the list
                        else {
                            selectedRBs.remove(btn); // returns false if the button wasn't in the list to begin with
                        }
                    }
                });
                
                radioButtons.get(i-1).add(btn); // adds sample button to 2D arraylist
                gridPane.add(btn, j, i);
                GridPane.setMargin(btn, new Insets(10));
            }
            
            // calls this function when an experiment button is clicked on 
            expBtn.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    if(expBtn.isSelected()) {
                        
                        // add an experiment button if none have been selected yet
                        if(selectedEBs.isEmpty()) {
                            selectedEBs.add(expBtn);
                        }
                        
                        // unselect previously selected experiment button and select newly clicked on button
                        else {
                            selectedEBs.get(0).setSelected(false); // unselect the first element
                            selectedEBs.remove(0); // remove 1st element from the selected list
                            selectedEBs.add(expBtn);
                        }
                    }
                    else {
                        selectedEBs.remove(expBtn);
                    }
                    
                    /*
                    * loop through each experiment button and disable corresponding sample buttons,
                    * only if that experiment button is not currently selected. Otherwise, we enable
                    * the button.
                    */
                    for(int i = 0; i < experiments; i++) {
                        
                        // if an experiment button is selected, we would enable corresponding sample buttons
                        if(experimentButtons.get(i).isSelected()) {
                            enableAllButtons(i); // helper method for enabling sample buttons
                        }
                        else {
                            disableAllButtons(i); // helper method for disabling sample buttons
                        }
                    }
                }
            });
        }
    }

    /*
    * precondition: radioButtons arraylist is already filled
    * disables all sample radio buttons for given row,
    * the row represents the samples under a given experiment.
    */
    private void disableAllButtons(int row) {
        for(int i = 0; i < radioButtons.get(row).size(); i++) {
            radioButtons.get(row).get(i).setDisable(true);
            radioButtons.get(row).get(i).setSelected(false);
            selectedRBs.remove(radioButtons.get(row).get(i));
        }
    }
    
    /*
    * precondition: radioButtons arraylist is already filled
    * enables all sample radio buttons for given row,
    * the row represents the samples under a given experiment.
    */
    private void enableAllButtons(int row) {
        for(int i = 0; i < radioButtons.get(row).size(); i++) {
            radioButtons.get(row).get(i).setDisable(false);
            radioButtons.get(row).get(i).setSelected(false);
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
    
    
    private void showFoldChange(double cutOff) {
        int  experimementPos1 = gridPane.getRowIndex(selectedRBs.get(0));
        int sampleIndex1 = gridPane.getColumnIndex(selectedRBs.get(0)) -1;
        int experimementPos2 = gridPane.getRowIndex(selectedRBs.get(1));
        int sampleIndex2 = gridPane.getColumnIndex(selectedRBs.get(1)) -1;
        sample1 = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experimementPos1).get(1).getNames()[sampleIndex1];
        sample2 = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experimementPos2).get(1).getNames()[sampleIndex2];
       // display sample name on the top of the table
       sample1Name.setText("Experiment " + Integer.toString(gridPane.getRowIndex(selectedRBs.get(0))) + " " + sample1 + ", ");
       sample2Name.setText("Experiment " + Integer.toString(gridPane.getRowIndex(selectedRBs.get(1))) + " " + sample2);
       
      foldChangeMatrix = calculateFoldChange(experimementPos1,sampleIndex1,experimementPos2,sampleIndex2);
      
      platesGridPane.getChildren().clear();
        
       

       
        //diaply analyte information on the 1st colomn
        for(int i = 1; i <= analytes.size();i++)
        {
            //set width for cells of the cols
            ColumnConstraints column = new ColumnConstraints(70);
            platesGridPane.getColumnConstraints().add(column);            
            Label label = new Label();
            String s = analytes.get(i-1).getAnalyte() + "(" + analytes.get(i-1).getRegionNumber() + ")";
            label.setText(s);    
            label.autosize();
            platesGridPane.add(label,0,i+1);
        }
        

    //display plate information on the 1st row & probes infor on the 2nd row
      //  int curExperiment = selectedRBs.get(0).
      //  HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment); 
      //  int countsOfPlates = probesListForCurExperiment.size() -1; // initilize probelistForCurexperiment contains key=0 value, which is never used and is empty
        // display probes 
        int pos = 1; // for put plate 1/2 at the right position. 
        for(int i = 1; i <= plates; i++)
        {
            System.out.println("Plate #" + i);
            //set width for cells of the cols
            ColumnConstraints column = new ColumnConstraints(70);
            platesGridPane.getColumnConstraints().add(column);                
            Label label = new Label();
            String s = "Plate " + i;
            label.setText(s);   
            label.autosize();         
            //add them to the GridPane
            platesGridPane.add(label,pos,0);
         
            ObservableList<probeTableData> probes = ModelForExperiments.getInstance().getProbeListForPopulate(experimementPos1, i);
            for(int j = 0 ; j < probes.size();j++)
            {
                System.out.println("Probe #" + j);
                platesGridPane.getColumnConstraints().add(column);    
                Label label1  = new Label();
                s = probes.get(j).getProbeForPlate(); // get probe's name (analyte)
                label1.setText(s);    
                label1.autosize();                          
                platesGridPane.add(label1,pos,1); //add them to the GridPane
                pos++;                
                //showMedianValueDataInPlace(i,j,probes,medianValueOriginalData); 
                //getOneProbeDataForMedianValue(int experimentPos, int plateIndex,  int sampleIndex, int probeIndex)
                 displayFoldChangeforOneProbe(i, j, foldChangeMatrix.get(i-1).get(j),cutOff);
            }            
        }       
    }   
 

    //helper function to get fold change matrix
    private List<List<HashMap<Integer, Double>>> calculateFoldChange(int experimementPos1, int sampleIndex1, int experimementPos2, int sampleIndex2) {

       List<HashMap<Integer,Double>> mv1ForOnePlate = new ArrayList<>();
       List<HashMap<Integer,Double>>  mv2ForOnePlate  = new ArrayList<>();
       
       
       
      List<List<HashMap<Integer,Double>>> foldChange = new ArrayList<>();
      plates = Math.min(ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos1).size(), 
              ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos2).size()); //get the samller size of plates 
      probes = getSamllestProbes(experimementPos1,experimementPos2,plates);
       for(int i = 0; i < plates; i++)
       {
           List<HashMap<Integer,Double>> plate = new ArrayList<>();
           mv1ForOnePlate = ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos1).get(i).get(sampleIndex1);
           mv2ForOnePlate = ModelForExperiments.getInstance().getMedianValueMatrix().get(experimementPos2).get(i).get(sampleIndex2);
           for(int k = 0; k< probes; k++)
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
       // Only store the requested fold change plates
       //TODO: make this more flexible by asking users to input which combinations they want 
       if(experimementPos1 == experimementPos2){
               if(experimementPos1 == 1 && ((sampleIndex1 == 0 && sampleIndex2 ==2) || (sampleIndex1 == 2 && sampleIndex2 == 0))){
                   ModelForExperiments.getInstance().setFoldChangeMatrixforANC(experimementPos1,foldChange); 
                   //getfoldChange.put(experimementPos1, foldChange);
               }else if(experimementPos1 == 2 && ((sampleIndex1 == 0 && sampleIndex2 ==2) || (sampleIndex1 == 2 && sampleIndex2 == 0)) ) {
                   ModelForExperiments.getInstance().setFoldChangeMatrixforANC(experimementPos1,foldChange);
               }else if(experimementPos1 == 3 && ((sampleIndex1 == 1 && sampleIndex2 ==3) || (sampleIndex1 == 3 && sampleIndex2 == 1))){
                   ModelForExperiments.getInstance().setFoldChangeMatrixforANC(experimementPos1,foldChange);
               }else if(experimementPos1 == 4 && ((sampleIndex1 == 1 && sampleIndex2 ==3) || (sampleIndex1 == 3 && sampleIndex2 == 1))){
                   ModelForExperiments.getInstance().setFoldChangeMatrixforANC(experimementPos1,foldChange);
               }
       }
       
       return foldChange;
    }
    /*
    public List<List<HashMap<Integer,Double>>> getfoldChange(int experimementPos1, int sampleIndex1, int experimementPos2, int sampleIndex2)
    {
        //getfoldChange calculateFoldChange(experimementPos1,sampleIndex1,experimementPos2,sampleIndex2);
        return getfoldChange;
    }
   */
    private int getSamllestProbes(int experimementPos1, int experimementPos2, int plates)
    {
         HashMap<Integer, UserInputForBeadPlate> userInputs1 = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experimementPos1);
         HashMap<Integer, UserInputForBeadPlate> userInputs2 = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experimementPos2);
         int res = userInputs1.get(1).getNumOfProbes();
         for(int i = 2 ; i <= plates;i++)
         {
             res = Math.min(res, userInputs1.get(i).getNumOfProbes());
             res = Math.min(res, userInputs2.get(i).getNumOfProbes());
         }
        
        return res;
    }

    private void displayFoldChangeforOneProbe(int platePos, int probeIndex, HashMap<Integer, Double> data, double cutOff) {
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
            platesGridPane.getColumnConstraints().add(column);            
            Label label = new Label();
            platesGridPane.setRowIndex(label, i);
            platesGridPane.setColumnIndex(label,probeIndex);
            double foldChange = data.get(analytes.get(i-1).getRegionNumber());
            label.setText(Double.toString(foldChange));   
            colorCode(label,foldChange,cutOff);
            label.autosize();            
            platesGridPane.add(label,pos,i+1);
           // GridPane.setMargin(label, new Insets(0));
        }
    }

    private int getPreProbes(int platePos) {
        int preProbes = 0; 
        HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(gridPane.getRowIndex(selectedRBs.get(0)));             
        for(int i = 1; i< platePos;i++ ) // don't count the current plate
         {
             preProbes += probesListForCurExperiment.get(i).size();
         }            
        return preProbes;
    }

    // 0.25+ for different shades of red, -0.25- for different shades of blue. 
    private void colorCode(Label label, double foldChange, double cutOff) {
        // red color
        if(foldChange >= cutOff)
        {
            int times = (int) (foldChange/cutOff);
            if(foldChange%cutOff !=0)
                times++;
            if(times <= red.length)
                label.setStyle(red[times-1]);
            else
                label.setStyle(red[red.length-1]);
        }
        // blue color 
        else if (foldChange <= -cutOff)
        {
            int times = (int) (-foldChange/cutOff);
            if(-foldChange%cutOff !=0)
                times++;
            if(times <= blue.length)
                label.setStyle(blue[times-1]);
            else
                label.setStyle(blue[blue.length-1]);
            
        }
        else
            return;
            
        
    }

    @FXML
    private void changeCutOffValueEvent(ActionEvent event) {
        
        double cutOff = Double.parseDouble(cutOffValueBox.getText());
        showFoldChange(cutOff);
    }

    private void resetColorCodeForOneProbe(int platePos, int probeIndex, HashMap<Integer, Double> data) {
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
            double foldChange = data.get(analytes.get(i-1).getRegionNumber());
            Label label = (Label) platesGridPane.getChildren().get(i+1);
            colorCode(label,foldChange,defaultCutOffValue);
            label.autosize();            
            platesGridPane.add(label,pos,i+1);
           // GridPane.setMargin(label, new Insets(0));
        }
    }
    
}
