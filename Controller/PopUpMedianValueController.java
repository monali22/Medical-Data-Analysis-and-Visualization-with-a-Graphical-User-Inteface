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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author feiping
 */
public class PopUpMedianValueController implements Initializable {

    @FXML
    private GridPane medianValueGridPane;
    @FXML
    private Text analyteName;

    /**
     * Initializes the controller class.
     */
    private bead curAnalyte;
    private int curSamplePos = 0;
    private int plateIndex = 0;
    private int probeIndex = 0;
    private int numberOfExperiments = 0;
    private int numberOfSamples =0;
    private String[] sampleNames;
    private List<Integer> experimentsToShow = new ArrayList<>(); // experiments that contains cur sample and will be showed in the table 
    @FXML
    private Text probeName;
    @FXML
    private Text plateNumber;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        curAnalyte = ModelForExperiments.getInstance().getCurAnalyte();
        curSamplePos = ModelForExperiments.getInstance().getCurSample();
        plateIndex = ModelForExperiments.getInstance().getCurPlate();
        probeIndex = ModelForExperiments.getInstance().getCurProbe();
        numberOfExperiments = ModelForExperiments.getInstance().getNumberOfExperiments();
        numberOfSamples = ModelForExperiments.getInstance().getNumberOfSamples();
        sampleNames = ModelForExperiments.getInstance().getSampleNames();
       
        // calculate how many experiment contains that sample 
        HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>> userInputsForBeadPlateMap = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap();
        for( int i = 1; i <=numberOfExperiments; i++)
        {
            HashMap<Integer, UserInputForBeadPlate> inputList = userInputsForBeadPlateMap.get(i);
            for(/*UserInputForBeadPlate input :userInputsForBeadPlateMap.get(i)*/ int j = 0; j < userInputsForBeadPlateMap.get(i).size(); j++ )
            {
                if(ModelForExperiments.getInstance().getExperimentModel().get(i).getSamples() >= curSamplePos)
                {
                    experimentsToShow.add( i );
                    break;
                }
            }  
        }
        
        tableCol(experimentsToShow.size());
        tableRow( sampleNames );    

        
        analyteName.setText(curAnalyte.getAnalyte() + "(" +curAnalyte.getRegionNumber()+")" );
        plateNumber.setText(Integer.toString(plateIndex+1));
        //get probeName for 
        String name = ModelForExperiments.getInstance().getProbeListForPopulate().get(1).get(plateIndex+1).get(probeIndex).getProbeForPlate();
        probeName.setText(name);
    }    

    public  GridPane tableRow(String[] sampleNames ){
        int rows = sampleNames.length;
        for(int i=1; i<=rows; i++){
            //set width for cells of the cols
            ColumnConstraints column = new ColumnConstraints(30);
            medianValueGridPane.getColumnConstraints().add(column);
            Label label = new Label();
            String s =  "  Sample " + i;
            label.setText(s);    
            label.autosize();
            medianValueGridPane.add(label,0,i);
            
         }
        return medianValueGridPane;
        }
    
    /*
  public  GridPane tableCol( int col){
       for(int i=1; i<=col; i++){
        //set width for cells of the cols150
        ColumnConstraints column = new ColumnConstraints(150);
        medianValueGridPane.getColumnConstraints().add(column);
        Label label = new Label();
        label.setWrapText(true);
        label.setMinWidth(150);
        String s = "Experiment " + experimentsToShow.get(i-1);
        label.setText(s);    
        //textField.setAlignment(Pos.CENTER);
        //label.autosize();
        medianValueGridPane.add(label,i,0);
        showMedianValue(experimentsToShow.get(i-1), i);
     }
    return medianValueGridPane;
    }

    //experiment starts from 1
    private void showMedianValue(int experiment, int col) {
        int analyteRegionNumber = curAnalyte.getRegionNumber();
        double medianValue =0;
        String sampleName = "";
        String orginalData = "";
        for(int i = 0; i<sampleNames.length; i++)
        {
            Label label = new Label();
            if(ModelForExperiments.getInstance().getMedianValueMatrix().get(experiment).get(plateIndex).size() <= i ||
                ModelForExperiments.getInstance().getMedianValueMatrix().get(experiment).get(plateIndex).get(i).get(probeIndex)==null    )
            {
                break;         
            }
            else 
            {
                // get final median value for the cell 
                medianValue = ModelForExperiments.getInstance().getMedianValueMatrix().get(experiment).get(plateIndex).get(i).get(probeIndex).get(analyteRegionNumber);
                
                //get sample name, if sample does not exisit, print nothing. 
                String[] samples = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(col).get(plateIndex).getNames();
                if(samples.length < i+1) 
                    continue;
                else 
                    sampleName = samples[i];
                
                //get original median value for each wells
               orginalData = getOrginalMeidanValue(experiment,plateIndex,i,probeIndex);
              
                
                label.setText("   " +Double.toString(medianValue) + " " + sampleName + " " + orginalData );    
         
            }
            label.autosize();
            
            medianValueGridPane.add(label,col, i+1);       

        }       
    }
    */
///*
    public  GridPane tableCol( int col){
       for(int i=1; i<=col*2; i=i+2){
        //set width for cells of the cols90
        ColumnConstraints column = new ColumnConstraints(90);
        medianValueGridPane.getColumnConstraints().add(column);
        Label label = new Label();
        label.setWrapText(true);
        label.setMinWidth(90);
        String s = "Experiment " + experimentsToShow.get(i/2);
        label.setText(s);    
        //textField.setAlignment(Pos.CENTER);
        //label.autosize();
        medianValueGridPane.add(label,i,0);
        showMedianValue(experimentsToShow.get(i/2), i);
     }
    return medianValueGridPane;
    }

    //experiment starts from 1
    private void showMedianValue(int experiment, int col) {
        int analyteRegionNumber = curAnalyte.getRegionNumber();
        double medianValue =0;
        String sampleName = "";
        String orginalData = "";
        for(int i = 0; i<sampleNames.length; i++)
        {
            Label label1 = new Label();
            Label label2 = new Label();
            TextField text1 = new TextField();
            TextField text2 = new TextField();
            if(ModelForExperiments.getInstance().getMedianValueMatrix().get(experiment).get(plateIndex).size() <= i ||
                ModelForExperiments.getInstance().getMedianValueMatrix().get(experiment).get(plateIndex).get(i).get(probeIndex)==null    )
            {
                break;         
            }
            else 
            {
                // get final median value for the cell 
                medianValue = ModelForExperiments.getInstance().getMedianValueMatrix().get(experiment).get(plateIndex).get(i).get(probeIndex).get(analyteRegionNumber);
                
                //get sample name, if sample does not exisit, print nothing. 
                String[] samples = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(experiment).get(plateIndex).getNames();
                if(samples.length < i+1) 
                    continue;
                else 
                    sampleName = samples[i];
                
                //get original median value for each wells
               orginalData = getOrginalMeidanValue(experiment,plateIndex,i,probeIndex);
              
                
                label1.setText("   " +Double.toString(medianValue) + " " + sampleName  );    
                label2.setText(orginalData);
                text1.setText("   " +Double.toString(medianValue) + " " + sampleName );    
                text2.setText(orginalData);
         
            }
            
            medianValueGridPane.add(text1,col, i+1);    
            medianValueGridPane.add(text2,col+1, i+1);    

        }       
    }
//*/
    private String getOrginalMeidanValue(int experiment, int plateIndex, int sampleIndex, int probeIndex) {
        String res = "("; 
        List< HashMap<Integer,  Double>> orignalData= ModelForExperiments.getInstance().getOneProbeDataForOrignalMedianValue(experiment, plateIndex, sampleIndex, probeIndex);
        for(HashMap<Integer,  Double> well : orignalData)
        {
           res += well.get(curAnalyte.getRegionNumber()) + ", ";
        }
                
        return res.substring(0, res.length()-2) + ")";         
    }


}