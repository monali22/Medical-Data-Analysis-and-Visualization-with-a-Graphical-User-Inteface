/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.ModelForExperiments;
import Model.ModelForExperiments;
import Model.XMLFile;
import Model.bead;
import Model.probeTableData;
import Util.ErrorMsg;
import Util.StAXParser;
import java.net.URL;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author feiping
 */
public class SetUpExperimentsController implements Initializable {

    @FXML
    private Button confirmExperiment;
    @FXML
    private Button addFileBtn;

    @FXML
    private Button Delete;
    @FXML
    private Button moveUpBtn;
    @FXML
    private Button moveDownBtn;
    @FXML
    private TextField numberOfExperiments;
    @FXML
    private ListView<String> xmlFilesListView; // left list view hold all xml files 
    private ListProperty<String> XMLfileList = new SimpleListProperty<>();
    @FXML
    private Text numberOfExperimentsText;
    @FXML
    private Text curExperimentText;
    private int curExperiment = -1; // -1 means no experiment has been choosen 
    ObservableList<String> allXMLfiles = FXCollections.observableArrayList(); // for all xml files    
    ObservableList<String> XMLFilesForOneExperiment =FXCollections.observableArrayList (); // for selected xml files

    @FXML
    private ListView<String> filesForOneExperimentListView;
    Map<Integer, ObservableList<String>> map = new HashMap<>(); // to hold xml files info for each experiment
    @FXML
    private ChoiceBox<Integer> chooseExperimentChoiceBox;
    ObservableList<Integer> experiments = FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        List<String> files = ModelForExperiments.getInstance().getXMLFiles();
        allXMLfiles.addAll(files);
        xmlFilesListView.setItems(allXMLfiles);
        
        filesForOneExperimentListView.setItems(XMLFilesForOneExperiment);
        //ModelForExperiments.getInstance().setExperimentsMap(new HashMap<Integer, List<String>>()); // clear previous data set up for experiments           
        
        chooseExperimentChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::itemChanged);
    }    
    
    public void itemChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
    {
        int val = (int)newValue;
        curExperiment = experiments.get(val-1);
        curExperimentText.setText(String.valueOf(curExperiment));
        //clear previous data in list view for one experiemnt.
        XMLFilesForOneExperiment.clear();
        // display previous data from user input. 
        Map<Integer, List<String>> XMLFileMap = ModelForExperiments.getInstance().getExperimentsXMLFileMap();
        List<String> previousXMLfiles = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment);
        if(previousXMLfiles.size()!=0)
        {
            for(String s: previousXMLfiles)
                XMLFilesForOneExperiment.add(s);
        }

    }

    // update number of experiments and create empty list for each expriments. 
    // update information in choice box and related text area. 
    @FXML
    private void setNumberofExperimentsEvent(ActionEvent event) {
        int number = Integer.parseInt(numberOfExperiments.getText());
        int preNumberOfExperiment = ModelForExperiments.getInstance().getNumberOfExperiments();
        numberOfExperimentsText.setText(String.valueOf(number));
        ModelForExperiments.getInstance().setNumberOfExperiments(number);
        //set experiments to choice box. 
        ModelForExperiments.getInstance().getProbeListForPopulate().clear();
        List<Integer> counts = new ArrayList<Integer>();
        for(int i = 1; i <=number; i++)
            {
                counts.add(i);
            } 
        experiments = FXCollections.observableList(counts);
        ModelForExperiments.getInstance().setExperiments(experiments);// put it into model for homepage display
        chooseExperimentChoiceBox.setItems(experiments); 
        
        ModelForExperiments.getInstance().initializeProbeListForPopulate();// update probe lists

        // if new number of experiment less than previous input, remove extra experiment data. 
        if(number < preNumberOfExperiment)
        {
            for(int i = number+1; i<=preNumberOfExperiment; i++)
            {
                ModelForExperiments.getInstance().getExperimentsXMLFileMap().remove(i);
            }
        }
        // if new nubmer of experiment more than previous input, add empty list into the data. 
        else if (number > preNumberOfExperiment)
        {            
            for(int i = preNumberOfExperiment+1; i<=number; i++)
            {
                ModelForExperiments.getInstance().getExperimentsXMLFileMap().put(i, new ArrayList<String>());
            }
        }
            Map<Integer, List<String>> XMLFileMap  = ModelForExperiments.getInstance().getExperimentsXMLFileMap();
             List<String> XMLfiles = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(number);
             
        //set defalut experiment to 1 
        int defaultExperiment = 1;
        chooseExperimentChoiceBox.setValue(defaultExperiment);
        curExperimentText.setText(String.valueOf(defaultExperiment));
    }

    @FXML
    private void addXmlFileEvent(ActionEvent event) {
       String file =  xmlFilesListView.getSelectionModel().getSelectedItem();
       //System.out.println("Add button clicked");
       if(curExperiment == -1)
       {
            ErrorMsg error = new ErrorMsg();
            error.showError("choose an experiment to add File !" );                   
       }
       else if(XMLFilesForOneExperiment.size()==0 )
       {
           XMLFilesForOneExperiment.add(file);
           ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment).add(file);
           ModelForExperiments.getInstance().initializeProbeListForPopulate();// update probe lists
       }
       else if(XMLFilesForOneExperiment.contains(file))
       {
            ErrorMsg error = new ErrorMsg();
            error.showError("File " + file + " already choosen!");           
       }
       else if(ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment).size()>=3)
       {
            ErrorMsg error = new ErrorMsg();
            error.showError("Files for one experiment should not more than three!");
       }
       else
       {
           XMLFilesForOneExperiment.add(file);
           ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment).add(file);
           ModelForExperiments.getInstance().initializeProbeListForPopulate();// update probe lists
           //ModelForExperiments.getInstance().seXMLfileListForOneExperiment(curExperiment, XMLFilesForOneExperiment);
       }
    }

    @FXML
    private void deleteEvent(ActionEvent event) {
        String file =  filesForOneExperimentListView.getSelectionModel().getSelectedItem();
        XMLFilesForOneExperiment.remove(file);
        ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment).remove(file);
    }

    @FXML
    private void moveUpEvent(ActionEvent event) {
          String file =  filesForOneExperimentListView.getSelectionModel().getSelectedItem();
          int index = getIndex(XMLFilesForOneExperiment,file);
          if(index>0) // if the selected item is the top item, no move
          {
                swap(XMLFilesForOneExperiment, index, index-1);
                //change sequence in the model as well
                ModelForExperiments.getInstance().swap(curExperiment, index, index-1);
          }
          else 
          {
                ErrorMsg error = new ErrorMsg();
                error.showError("can not move up the top item!");
          }
    }
    
    @FXML
    private void moveDownEvent(ActionEvent event) {
        String file =  filesForOneExperimentListView.getSelectionModel().getSelectedItem();
        int index = getIndex(XMLFilesForOneExperiment,file);
        if(index < XMLFilesForOneExperiment.size()-1) 
        {
            swap(XMLFilesForOneExperiment, index, index+1);
            ModelForExperiments.getInstance().swap(curExperiment, index, index+1);
        }
        else
        {
                ErrorMsg error = new ErrorMsg();
                error.showError("can not move down the bottom item!");
        }       
    }

    private int getIndex(ObservableList<String> listOfNumber, String file) {
        for(int i = 0; i<XMLFilesForOneExperiment.size();i++ )
        {
            if(XMLFilesForOneExperiment.get(i) == file)
                return i;
        }
        return -1;
    }

    private void swap(ObservableList<String> items2, int index1, int index2) {
        String s1 = items2.get(index1);
        String s2 = items2.get(index2);
        items2.set(index2, s1);
        items2.set(index1, s2);
        
    
    }    
}
