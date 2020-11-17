/*
 * The HomepageController.java class handles the 'Input' menu shown
 * as the first window when running the program. This class deals 
 * with all of the 'Input' menu's functionality. This includes
 * selecting xml files for the experiments and visualizing the 
 * each bead plate table.
 */
package Controller;

import Util.StAXParser;
import Model.UserInputForBeadPlate;
import Model.bead;
import Model.probeTableData;
import Model.ModelForExperiments;
import Model.BeadPlate;
import Model.PlateStatus;
import Util.ErrorMsg;
import com.jfoenix.controls.JFXButton;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.T;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import static javax.swing.text.html.HTML.Tag.S;
import static jdk.nashorn.internal.runtime.regexp.joni.constants.AsmConstants.S;
import static jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.S;

/**
 *
 * @author feiping
 */
public class HomepageController implements Initializable {
    //upload xml files area
    @FXML
    private JFXButton uploadFiles;
    @FXML
    private JFXButton resetData;
    @FXML
    private JFXButton doneUploadFiles; // unused button
    @FXML
    private ListView filesList;
    private List<String> fileNames = new ArrayList<String>(); // store xml file names as string 
    private Map<Integer, List<String>> mapOfExperiments = new HashMap<>(); 
    
   //bead class table area 
    @FXML
    public TableView<bead> beadTable;  
    @FXML
    private TableColumn<bead, String> beadCol; 
    @FXML
    private TableColumn<bead, String> analyteCol;
    private TextField beadInput; // not needed
    private TextField analyteInput; // not needed
    private  ObservableList<bead> analytes;   //beads read from xml files
    
    
    //right-top area, display experiement info
    @FXML
    private ChoiceBox<Integer> DropdownExperimentsChoiceBox;
    ObservableList<Integer> experiments = FXCollections.observableArrayList(); // list for choice box choices 
    @FXML
    private Text totalNumberOfExperiments;
    @FXML
    private Text currentExperiementNumber;
    @FXML
    private Text XMLfilesNames;
    private int curExperiment = -1; //initiliaze to -1.
    private int curPlate = 0; //initiliaze to 0.
    @FXML
    private Button setUpExperiments; // click to open a new page to edit xml files for each experiment.
 
    // text boxes for each bead plate, only need (numReplicaInput, numProbeInput, sampleNamesInput)
    @FXML
    private TextField numSampleInput;
    @FXML
    private TextField numReplicaInput;
    @FXML
    private TextField numProbeInput;
    @FXML
    private TextField sampleNamesInput;

    
    // this is the 'Confirm Change' button
    @FXML
    private JFXButton checkLayout;
    
    // this button is invisble in the UI, unsure of its importance
    @FXML
    private JFXButton confirmInputBtn;    
    
    //List<UserInputForBeadPlate> userInputsForBeadPlate = new ArrayList<>(); // list for user input data
    //HashMap<Integer, List<UserInputForBeadPlate>> userInputsForBeadPlateMap= new HashMap<>();

   
    // choice box to switch between each plate
    @FXML
    private ChoiceBox<Integer> DropdownPlatesChoiceBox;
    
    // the grids displaying the samples under each tab (only need one that we update when changing experiment or user input)
    @FXML
    private GridPane beadPlateLayout;
    
    // maybe change from 'TextField' to 'Text' so user can't edit sample names
    List<TextField> layoutCellsList = new ArrayList<>(); // List to hold textfield in each gridpane cells. 
    
    //colors list for set diffrent colors to each probe
    List<String> colors = new ArrayList<>(Arrays.asList("-fx-background-color:#B0E0E6;", "-fx-background-color:yellow;", "-fx-background-color:#CD5C5c;", "-fx-background-color:pink;", 
            "-fx-background-color:#ADFF2F;", "-fx-background-color:orange;", "-fx-background-color:#FFD700;", "-fx-background-color:#DDA0DD;", "-fx-background-color:AQUA;",
            "-fx-background-color:#87CEFA;", "-fx-background-color:#F5F5DC;", "-fx-background-color:BISQUE;", "-fx-background-color:brown;", "-fx-background-color:CORAL;"));
    
    
    //probe table area    
    @FXML
    private TableView<probeTableData> probeTable;
    @FXML
    private TableColumn<probeTableData,Integer> ProbeCol; 
    @FXML
    private TableColumn<probeTableData,String> BeadPlateProbeCol;        
    @FXML
    private JFXButton loadProbe;
    @FXML
    private JFXButton editProbesInBeadPlate;
    private ObservableList<probeTableData> probes = FXCollections.observableArrayList();
    //ObservableList<probeTableData> probesToLoad = FXCollections.observableArrayList();
    private HashMap<Integer, ObservableList<probeTableData>> probesToLoad = new HashMap<>();
    
    //bead plate set up status table
    @FXML
    private TableView<PlateStatus> beadPlateStatusTable = new TableView<PlateStatus>();
    @FXML
    private TableColumn<PlateStatus, String> beadPlateCol =  new TableColumn<PlateStatus, String> ();
    @FXML
    private TableColumn<PlateStatus, String> confirmedCol = new TableColumn<PlateStatus,String>();
    private  ObservableList<PlateStatus> status = FXCollections.observableArrayList();  

    //default data to user Input area 
    String namesInput = "WC, WK, KC, KK";
    String[] names = namesInput.split(",");
    UserInputForBeadPlate defaultUserInput = new UserInputForBeadPlate(2, namesInput, names,10, new ArrayList<String>());
    
    // Nerunaric and T-Cell options
    @FXML
    private Button experimentType1; // nerunaric experiment type: TODO
    @FXML 
    private Button experimentType2; // T-cell experiment type: currently the default and only type
     
    /*
    * Intializer collects probe table data and adds a listener
    * to the experiment and plates choice box that will be called
    * whenever the user selects a different item. Finally, the 
    * bead class and probe tables are initialized.
    */
    @Override
    public void initialize(URL url, ResourceBundle rb)   {
        loadProbes(); // collect probe table data from txt files
        
        DropdownExperimentsChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::itemChanged); 
        DropdownPlatesChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::plateChanged);  

        // populate analytes data. 
        beadCol.setCellValueFactory(new PropertyValueFactory<bead,String>("RegionNumber"));
        analyteCol.setCellValueFactory(new PropertyValueFactory<bead,String>("Analyte"));
        
        /*
        if(ModelForExperiments.getInstance().getAnalytes()!=null) 
        {
            analytes = ModelForExperiments.getInstance().getAnalytes();
        }
        System.out.println("analytes size: " + analytes.size());
        beadTable.setItems(analytes);*/
                      
        //table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
        //setBeadsTable(1);
    } 
    
    /*
    * this method initializes the choice box for switching between
    * bead plates, it will be called every time the user switches
    * experiments.
    */
    private void initPlateChoiceBox() {
        curPlate = 1; // default value
        ModelForExperiments.getInstance().setCurPlate(curPlate);
        //DropdownPlatesChoiceBox = new ChoiceBox<>(); // instantiate object
        
        List<Integer> platesList = new ArrayList<>();
        
        // fill list with integers for the number of plates that the current experiment has
        for(int i = 1; i <= ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getNumPlates(); i++) {
            platesList.add(i);
        }
        
        // create an observable list containing the number of plates
        ObservableList<Integer> platesChoices = FXCollections.observableList(platesList);
        DropdownPlatesChoiceBox.setItems(platesChoices); // choice box now has number of plates set
        DropdownPlatesChoiceBox.setValue(curPlate); // default value is always the first plate
    }
    
    /*
    * reads from probes.txt and probes2.txt and fills hashmap 
    * probesToLoad with lists of probe data. each key of the 
    * hashmap, maps to a separate line from the text files.
    */
    private void loadProbes() {
        List<List<String>> probeLists = null;
        List<List<String>> probeLists2 = null;
        
        // probeLists and probeLists2 extract probe data from probes1.txt and probes2.txt respectively
        try {
            probeLists = ModelForExperiments.getInstance().getProbesForExperimentType1();
            probeLists2 = ModelForExperiments.getInstance().getProbesForPlate2();                        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // loop through each line of each probes text file
        for(int i = 0; i < probeLists.size(); i++)
        {
            // extract line of probes from probes1.txt
            List<String> probesForTable = probeLists.get(i);
            List<String> probesFortwoTable = probeLists2.get(i);
            
            ObservableList probeValues = FXCollections.observableArrayList();
            
            // loop through each probe in a single line from probes text file
            for(int j = 1; j <= probesForTable.size();j++)
            {
                if(i == 0) { // checks if line being read is the first line of text file
                    // add probe to observable list as a probeTableData object
                    probeValues.add(new probeTableData(j, probesForTable.get(j-1)));
                }
                else if (i == 1) { // checks if line being read is the second line of text file
                    // add probe to observable list as a probeTableData object
                    probeValues.add(new probeTableData(j, probesForTable.get(j-1)));
                }
                else{
                    probeValues.add(new probeTableData(j, probesForTable.get(j-1))); 
                }
                
                // probes1.txt has a third line but we never read from it
            }
            probesToLoad.put(i + 1, probeValues); // store observable list of probes into hashmap
        }
    }
        
    /*
    * initializes the plate table.
    */
    private void setBeadsTable() { 
        ProbeCol.setCellValueFactory(new PropertyValueFactory<probeTableData,Integer>("probeCount"));
        BeadPlateProbeCol.setCellValueFactory(new PropertyValueFactory<probeTableData,String>("probeForPlate"));

        if(curExperiment >0 && ModelForExperiments.getInstance().getProbeListForPopulate(curExperiment, curPlate)!=null) // populate probe data into probe tables
        {
            probes = ModelForExperiments.getInstance().getProbeListForPopulate(curExperiment, curPlate);
        }
        
        probeTable.setItems(probes);
        probeTable.setRowFactory(row -> new TableRow<probeTableData>(){ // set color code to probe table 1
                @Override
                public void updateItem(probeTableData item, boolean empty){
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {                        
                        //need get size of probes of that plate and then update the color correctly 
                        String s = numProbeInput.getText();
                        if(s!= null && !s.equals(""))
                            {
                                int size = Integer.parseInt(s);
                                int index = (item.getProbeCount()-1) % size;
                                setStyle(colors.get(index));                    
                            }

                    }
                }
        }
        );
        probeTable.setEditable(true);
        BeadPlateProbeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }
   
    /*
    * this method is called when the user clicks the 'Select'
    * button. deals with taking in the input xml files.
    */
    @FXML
    private void selectFilesEvent(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new ExtensionFilter("XML Files", "*.xml"));
        
        List<File> selectedFiles = fc.showOpenMultipleDialog(null);

        if(selectedFiles!= null)
        {
            for(int i =0; i<selectedFiles.size(); i++)
            {
                //get absolute path of the file on users' computer
                // ?? save the absolute path
                File file = selectedFiles.get(i);
                String path = "";
                path += file.getAbsolutePath();
                String directory = file.getParentFile().getAbsolutePath();
                ModelForExperiments.getInstance().setDirectory(directory); // save directory to model
                
                String filename = selectedFiles.get(i).getName();
                if(analytes==null || analytes.isEmpty())
                {
                    getAnalytes(path);
                    beadTable.setItems(analytes);
                }            
                filesList.getItems().add(filename); // display on the listview fileList. 
                fileNames.add(filename);
            } 
            ModelForExperiments.getInstance().setXMLFiles(fileNames);
            int count = fileNames.size() %2 == 1 ? fileNames.size() /2+1 : fileNames.size() /2;
            ModelForExperiments.getInstance().setNumberOfExperiments(count);
            
            //set value to experiements dropbox base on # of experiments.
            List<Integer> counts = new ArrayList<Integer>();    
            int index = 0;
            
            for(int i = 1; i <=count; i++)
            {
                counts.add(i);                
                mapOfExperiments.put(i, new ArrayList<String>());                
                mapOfExperiments.get(i).add(fileNames.get(index++));                           
                if(index == fileNames.size()) break; // exceeds range 
                mapOfExperiments.get(i).add(fileNames.get(index++));
            }
            ModelForExperiments.getInstance().setExperimentsMap(mapOfExperiments);// put it in model 
            experiments = FXCollections.observableList(counts); // for choice box drop down menue
            ModelForExperiments.getInstance().setExperiments(experiments);
            //create probe list map for each experiment
            ModelForExperiments.getInstance().initializeProbeListForPopulate();  // initialize experiment model          
        }
        else
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("Files Invalid!");
        }  
        
        try {
            updateExperimentsInfo();
        }
        catch(NullPointerException e) {
            System.out.println("Haven't selected any files.");
        }
    }
    
    //read analystes from the first xml file and save it to data model. 
    private void getAnalytes(String filePath )
    {
        StAXParser parser = new StAXParser();
        analytes =  parser.getAnalytes(filePath);
        ModelForExperiments.getInstance().setAnalytes(analytes);        
    }
    
    // initialize plate stauts after user uploads xml files or manually set up experiment
    public void initializePlateStatus()
    {
        //clear status list if it is not empty
        if(!status.isEmpty())
            status.clear();
        
        int experiments = ModelForExperiments.getInstance().getNumberOfExperiments();
        for(int i = 1; i <=experiments; i++)
        {
            String experiment = "Experiment " + i; 
            List<String> lists = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(i);
            for(int j = 1 ; j <=lists.size(); j++)
            {
                String plate = experiment + " plate " + j;
                status.add(new PlateStatus(plate, "false"));
            }
        }
        beadPlateCol.setCellValueFactory(new PropertyValueFactory<PlateStatus,String>("plate"));
        confirmedCol.setCellValueFactory(new PropertyValueFactory<PlateStatus,String>("status"));
        beadPlateStatusTable.setItems(status);    
        //numSampleInput1.setText("2");
    }

    public UserInputForBeadPlate getBeadInput()
    {
        return defaultUserInput;
    }
    
   // change plate status to Yes after user click "confirm input " button 
    @FXML
    private void confirmInputEvent(ActionEvent event) {
        Map<Integer, List<String>> map = ModelForExperiments.getInstance().getExperimentsXMLFileMap();
        int index = 0; 
        // calculate start index in the status list
        if(curExperiment!=0)
        {
            for(int i = 1; i <curExperiment;i++ )
            {
                index+=map.get(i).size();
            }
        }
        for( int i = index; i < index + map.get(curExperiment).size(); i++)
        {
            status.get(i).setStatus("True");
        }
        beadPlateStatusTable.refresh();
    }
    

    @FXML
    private void resetDataEvent(ActionEvent event) {
        filesList.getItems().clear();
        uploadFiles.setDisable(false);
        
        // xml files map clear previous data associated with xml files, do it in the set up experiment page
        ModelForExperiments.getInstance().getXMLFiles().clear();
        ModelForExperiments.getInstance().getExperimentsXMLFileMap().clear();
        ModelForExperiments.getInstance().getExperiments().clear();
        
        //clear analytes table 
        analytes.clear();
        ModelForExperiments.getInstance().getAnalytes().clear();
        beadTable.refresh();
        
        //clear choice box and other text
        experiments.clear();
        totalNumberOfExperiments.setText("0");
        currentExperiementNumber.setText("");
        XMLfilesNames.setText("");
        curExperiment = -1;
        ModelForExperiments.getInstance().setCurrentExperiment(curExperiment);
        
        // clear status table
        status.clear();
        beadPlateStatusTable.refresh();
        
        //clear probe tables
        ModelForExperiments.getInstance().getProbeListForPopulate().clear();
        probes.clear();
        probeTable.refresh();
       
        // clear user input
       ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().clear();
       clearUserInputForBeadPlate();
       //clearUserInputForBeadPlate2();
       //clearUserInputForBeadPlate3();
       
       //clear bead plate layout
       clearLayout();     
       
       //clear median value data matrix 
       ModelForExperiments.getInstance().getMedianValueMatrix().clear();
    }
    
    // choice box listener for experiments dropdown
    public void itemChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
    {
        int val = (int)newValue; // curExperiment -1  e.g. 0
        curExperiment = experiments.get(val - 1); // get current Experiment number e.g 1
        curPlate = 1; // default
        ModelForExperiments.getInstance().setCurPlate(curPlate);
        // change information on the top experiment area 
        currentExperiementNumber.setText(String.valueOf(curExperiment));
        ModelForExperiments.getInstance().setCurrentExperiment(curExperiment);
        HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>> userInputsForBeadPlateMap = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap();
        List<String> files = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment);
        XMLfilesNames.setText(generateXMLFilesString(files));      
        // clear previous input
        //clearUserInput(3);  
        
        //clearBeadsPlateLayout();
        clearLayout();

        //enable & disable plate tab, bead , user input base on the # of xml files of current experiment
        //refresh probe table
        int fileSize = files.size();
        //setUpExeprimentBaseOnSizeOfXMLFiles(fileSize);
                
        // display user set up information on the bead plate. 
        // change layout and user input infor for the new expriment if there are user inputs for current experiments
        // if no user input or less then set up default inputs 
        if(userInputsForBeadPlateMap.get(curExperiment)==null)
        {
               HashMap<Integer, UserInputForBeadPlate> defaultInputs = new HashMap<>(); //??
               ModelForExperiments.getInstance().setUserInputsForOneExperiment(curExperiment, defaultInputs); 
        }

        int size = userInputsForBeadPlateMap.get(curExperiment).size();
        
        if(size != fileSize)
        {
            HashMap<Integer, UserInputForBeadPlate> inputs = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap().get(curExperiment);
               while(size!=fileSize)
               {
                   inputs.put(ModelForExperiments.getInstance().getCurPlate() + 1, defaultUserInput);
                   size++;
               }
               ModelForExperiments.getInstance().setUserInputsForOneExperiment(curExperiment,inputs);            
        }

       userInputsForBeadPlateMap = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap();

       displayUserInput(userInputsForBeadPlateMap.get(curExperiment));

       // reset choicebox for bead plates based on number of bead plates in current experiment
       initPlateChoiceBox(); 
       setBeadsTable();
       
        // display bead plate layout and probe tables. 
        checkLayoutEventHelper();
    }
    
    // choice box listener for bead plates dropdown
    public void plateChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        // consequence of resetting the plates choice box when switching experiments
        if(newValue == null) 
            return;
        curPlate = (int)newValue;
        ModelForExperiments.getInstance().setCurPlate(curPlate);
        setBeadsTable();
        displayUserInput(ModelForExperiments.getInstance().getUserInputsForOneExperiment(curExperiment));
        checkLayoutEventHelper();        
    }      

    //update left upper experiments info after user upload xml files or manually set up xml files for experiments
    private void updateExperimentsInfo() throws NullPointerException {
        //display # of experiements
        int count = ModelForExperiments.getInstance().getNumberOfExperiments();
        totalNumberOfExperiments.setText(Integer.toString(count));
      
        // update # of experiment in drop down choice box        
        ObservableList<Integer> experimentsNew = ModelForExperiments.getInstance().getExperiments();
        //List<Integer> platesList = new ArrayList<>();
        
        //experiments.clear();
        //experiments.addAll(experimentsNew);
        if(experimentsNew == null) {
            throw new NullPointerException("Haven't selected files.");
        }
        
        DropdownExperimentsChoiceBox.setItems(experimentsNew);   
        int defaultExperiment = 1;
        DropdownExperimentsChoiceBox.setValue(defaultExperiment);
        
        /*
        for(int i = 1; i < ModelForExperiments.getInstance().getExperimentModel().get(defaultExperiment).getNumPlates(); i++) {
            platesList.add(i);
        }
        ObservableList<Integer> platesChoices = FXCollections.observableList(platesList);
        DropdownPlatesChoiceBox.setItems(platesChoices);
        DropdownPlatesChoiceBox.setValue(1);
        */
        currentExperiementNumber.setText(String.valueOf(defaultExperiment));
        ModelForExperiments.getInstance().setCurrentExperiment(defaultExperiment);
        List<String> files = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(defaultExperiment);
        XMLfilesNames.setText(generateXMLFilesString(files));    
        //nable & disable plate tab, bead , user input base on the # of xml files of current experiment
        int fileSize = files.size();
        //setUpExeprimentBaseOnSizeOfXMLFiles(fileSize);

        // update bead plate status table 
        //initializePlateStatus();   
    }
    
    // helper function :
    //when user change experiment, generate xml files string for the new experiment base on the list of files name. 
    private String generateXMLFilesString( List<String> files)
    {
        String s = "";
        for(int i = 0; i <files.size(); i++)
        {
            if(i==files.size() -1) 
                s += files.get(i);
            else 
                s += files.get(i) + ",";
        }
        return s;
    }
    
    // open a new page for user to manually to edit xml files for each experiment. 
    @FXML
    private void setUpExperimentsAction(ActionEvent event) throws MalformedURLException {
         //clear 
         URL url = Paths.get("./src/View/SetUpExperiments.fxml").toUri().toURL();
         Parent root ;
               try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                root = fxmlLoader.load(url);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));  
                stage.showAndWait();

        //stage.show();
        } catch(Exception e) {
           e.printStackTrace();
          }
        updateExperimentsInfo();        
    }

    public String[] getSampleNames()
    {
        return names;
    }

    public void clearUserInputForBeadPlate()
    {
        numReplicaInput.clear();
        sampleNamesInput.clear();
        numProbeInput.clear();

    }  
    
    //heleper function clear the bead plate layout. However, it also clears grid lines. 
    private void clearLayout()
    {
        layoutCellsList = getCells(beadPlateLayout);
        for(int j =0; j <layoutCellsList.size(); j++)
        {
            layoutCellsList.get(j).clear();
            layoutCellsList.get(j).setStyle("-fx-background-color:white;");
        }
    }

    private void displayUserInput(HashMap<Integer, UserInputForBeadPlate> data) {
        // need to get current bead plate of current experiment
        curPlate = ModelForExperiments.getInstance().getCurPlate();
        numSampleInput.setText(Integer.toString(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples()));
        numReplicaInput.setText(Integer.toString(data.get(curPlate).getNumOfReplicas()));
        numProbeInput.setText(Integer.toString(data.get(curPlate).getNumOfProbes()));
        sampleNamesInput.setText(data.get(curPlate).getNameInput()); 
    }

    //for display layout button. click to diaplay bead plate layout on each bead plate tab. 
    @FXML
    private void checkLayoutEvent(ActionEvent event) {

        // error msg for user has not slected an experiment
       if(curExperiment == -1)
       {
                ErrorMsg error = new ErrorMsg();
                error.showError("choose an experiment first!"); 
                return;
       }
       checkLayoutEventHelper();
    }
    
   
    private void checkLayoutEventHelper()
    {  
        // gather users' input and put it in to map base on size of XML files. 
        UserInputForBeadPlate userInputsForBeadPlate = new UserInputForBeadPlate();
        curExperiment = ModelForExperiments.getInstance().getCurrentExperiment();
        //int size = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment).size();
       
        if(!getUserInputforPlate(userInputsForBeadPlate)) {
            return;
        }
     
        //find current experiment, put the experiement into the map associated with curexperiment number. 
        displayBeadsPlateLayout(curExperiment);        
        displayProbeTable();
        //HashMap<Integer, HashMap<Integer, ObservableList<probeTableData>>> probesListForPopulate  = ModelForExperiments.getInstance().getProbeMapForPopulate(); // debug
    }
    
    // load probe and diaply probes on to probe tables base on xml files size. 
    private void displayProbeTable()
    {
       loadProbeHelper();       
    }
    
    //get data of current experiement and display the bead plate layout. 
    private void displayBeadsPlateLayout(int curExperiment)
    { 
        clearLayout(); // in case the new input of totoal number beads is less than previous input 
        HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>> userInputsForBeadPlateMap = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap();
        if(userInputsForBeadPlateMap.get(curExperiment) == null) return;
        //List<UserInputForBeadPlate> inputs = userInputsForBeadPlateMap.get(curExperiment);
        displayLayout(userInputsForBeadPlateMap.get(curExperiment).get(curPlate));
    }

    // display layout in the gridpane
    // index: index of the bead plate 
    // data: user input data for that bead plate   
    private void displayLayout(UserInputForBeadPlate data) {
        
         //fill cells with colors and values base on users' inputs
        layoutCellsList = getCells(beadPlateLayout);
        int cellsCount =0;
        String[] nameList = data.getNames();
        int cellsToFill = ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples() * data.getNumOfReplicas()*data.getNumOfProbes(); // totoal cells need to fill in in the grid pane

        int numberOfProbes=data.getNumOfProbes();
        int numberOfSamples = ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples();
        int numberOfReps = data.getNumOfReplicas();
        while(cellsCount<cellsToFill)
        {
            for(int i = 0; i < numberOfProbes; i++)
            {                    

                String color = colors.get(i % colors.size());  // choose different color to each probe

                for(int j = 0; j < numberOfSamples * numberOfReps; j++)
                {
                    layoutCellsList.get(cellsCount).setStyle(color);  // set color to those cells.
                    layoutCellsList.get(cellsCount).setText(nameList[cellsCount %(nameList.length)] + "." + ((j)/numberOfSamples+1)); //set probe text to those cells
                    cellsCount++;
                }
            }
        }       
    }

    public List<TextField> getCells(GridPane gridPane)
    {
        List<TextField> cells = new ArrayList<>();
            for(Node currentNode : gridPane.getChildren())
            {
                if (currentNode instanceof TextField)
                {
                    cells.add((TextField)currentNode);
                }
            }
        return cells;
    }
            
    public Node getNodeByRowColumnIndex (final int column, final int row, GridPane gridPane) {
        System.out.println("enter getNode function" );    
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        System.out.println("leave getNode function" );    
        return result;
    }

    // open a open up page to edit beads for each bead plate,
    private void loadAddBeadsPage() {
        
        AddBeadsPageController beadsController = null;
        try {
           
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("/View/addBeads.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));  
            stage.showAndWait();
        
            //pass beads from homepage the editbeads page
            beadsController = fxmlLoader.getController();

            
        } catch(Exception e) {
           e.printStackTrace();
        }

    }

    @FXML
    private void editBeadForPlate1Event(ActionEvent event) {
        if(curExperiment == -1)
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("choose an Experiment first!");                   
        }
        if(numProbeInput.getText() == null || Integer.parseInt(numProbeInput.getText()) <=0 ) 
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("invalid input for number of probe!");           
        }
        if(probes ==null || probes.size() == 0)
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("please load probe first!");                
        }
        ModelForExperiments.getInstance().setCurPlate(1);
        loadAddBeadsPage();        
        curExperiment=ModelForExperiments.getInstance().getCurrentExperiment();    
        ObservableList<probeTableData> probesForPlate1 = ModelForExperiments.getInstance().getProbeListForPopulate(curExperiment, 1); //selected data for the bead plate
        probeTable.getItems().clear();
        probeTable.getItems().addAll(probesForPlate1);
    }

    @FXML
    private void loadProbeEvent(ActionEvent event) { 
        loadProbeHelper();
    }
    
    private void loadProbeHelper() {      
        if(probes.size()!=0)  // if not empty, clear previous data first. 
            probes.clear();

        // if no map generate for current experiment probles, initiliaze it. 
        if(ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment) == null)
            ModelForExperiments.getInstance().initializeProbeListMap(curExperiment);
        //HashMap<Integer, ObservableList<probeTableData>> map = ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment);
            // when no input or input is 0, error
        if(numProbeInput.getText() == null || Integer.parseInt(numProbeInput.getText()) == 0 )
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("Please set up probe numbers before load probe!");
        }
        else // load # of probles base on user input 
        {
            ObservableList<probeTableData> probesList = FXCollections.observableArrayList();
            for(int i = 0; i < Integer.parseInt(numProbeInput.getText());i++ )
            {
                probeTableData probe = probesToLoad.get(/*ModelForExperiments.getInstance().getCurPlate()*/ curPlate).get(i);
                probesList.add(probe);
            }
            
            ModelForExperiments.getInstance().addOneProbeListForPopulate(curExperiment,/*ModelForExperiments.getInstance().getCurPlate()*/ curPlate,probesList);
            HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment); 
        
            probes.addAll(ModelForExperiments.getInstance().getProbeListForPopulate(curExperiment, curPlate));
            probeTable.refresh();

        }
    }     
   
    //gather user inputs for bead plate1 form each text fileds 
    //save the userInputsForBeadPlate in to the list passed in
    private boolean getUserInputforPlate(UserInputForBeadPlate userInputsForBeadPlate) {
        String samples = numSampleInput.getText();
        String reps = numReplicaInput.getText();
        String probes = numProbeInput.getText();

        if(hasErrorsForUserInput(samples, reps, probes)) return false; // if input valid return; 
        
        int numberOfSamples = Integer.parseInt(samples);
        int numberOfReps = Integer.parseInt(reps);
        int numberOfProbes =  Integer.parseInt(probes);  

        String names = sampleNamesInput.getText();
        String[] nameList = names.split(",");

        ModelForExperiments.getInstance().setSampleNames(nameList);

        List<String> probeList = new ArrayList<>();
        //combine user inputs and probeList into a UserInputForBeadPlate object
        userInputsForBeadPlate = new UserInputForBeadPlate(numberOfReps, names, nameList, numberOfProbes, probeList);
        ModelForExperiments.getInstance().addOneUserInputForPopulate(curExperiment, curPlate, userInputsForBeadPlate);
        ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).setSamples(numberOfSamples);
        return true;
    }

    // check errors for user inputs and remind users
    private boolean hasErrorsForUserInput(String samples, String reps, String probes)
    {
        if(samples==null || samples.equals("") || reps==null || reps.equals("") || probes==null || probes.equals("") )
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("user input for Experiment incomplete! ");  
            return true;        
        }
        if(!samples.chars().allMatch( Character::isDigit ))
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("user input for Experiment is not correct format" + " number of samples is numbers only ");
            return true;
        }
        if ( !reps.chars().allMatch( Character::isDigit ) )
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("user input for Experiment is not correct format" + " number of replicas is numbers only ");   
            return true;
        }
        if ( !probes.chars().allMatch( Character::isDigit ) )
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("user input for Experiment is not correct format" + " number of probes is numbers only ");    
            return true;
        }    
        int numberOfSamples = Integer.parseInt(samples);
        int numberOfReps = Integer.parseInt(reps);
        int numberOfProbes =  Integer.parseInt(probes);  
        //check whether input is valid 
        if(numberOfReps * numberOfProbes * numberOfSamples > 96) // this if statement may need to be changed
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("user input for Experiment " + curExperiment + " bead plate " + curPlate + " exceeds 96!");
            return true;
        }
        return false;
    }

    @FXML
    private void changeExperimentEvent(MouseEvent event) {
        // ensures user inputs save when switching between experiments
        checkLayoutEventHelper(); 
    }

    @FXML
    private void changePlateEvent(MouseEvent event) {
        // ensures user inputs save when switching between plates of the same experiment
        checkLayoutEventHelper();
    }

    @FXML
    private void editProbeCellEvent(CellEditEvent<probeTableData, String> edittedCell) {
        probeTableData probeSelected = probeTable.getSelectionModel().getSelectedItem();
        probeSelected.setProbeForPlate(edittedCell.getNewValue().toString());
    } 
}
