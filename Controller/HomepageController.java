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
import javafx.scene.control.CheckBox;
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
    @FXML
    private Text totalNumberOfExperiments;
    @FXML
    private Text currentExperimentNumber;
    @FXML
    private Text XMLfilesNames;
    private int curExperiment = -1; //initiliaze to -1.
    private int curPlate = 0; //initiliaze to 0.
    @FXML
    private Button setUpExperiments; // press to open a new page to edit xml files for each experiment.
 
    @FXML
    private CheckBox experimentComplete = new CheckBox();
    
    // text boxes for each bead plate, only need (numReplicaInput, numProbeInput, sampleNamesInput)
    @FXML
    private Text numSampleInput;
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
    
    @FXML
    private CheckBox plateComplete = new CheckBox();
    
    // the grid displaying the samples for each plate
    @FXML
    private GridPane beadPlateLayout;
    
    List<TextField> layoutCellsList = new ArrayList<>(); // List to hold textfield in each gridpane cells. 
    
    //colors list for set diffrent colors to each probe
    private List<String> colors = new ArrayList<>(Arrays.asList("-fx-background-color:#B0E0E6;", "-fx-background-color:yellow;", "-fx-background-color:#CD5C5c;", "-fx-background-color:pink;", 
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
    UserInputForBeadPlate defaultUserInput = new UserInputForBeadPlate(2, 10, new ArrayList<String>());
    
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
        // initialize checkboxes to be unfilled
        experimentComplete.setSelected(false);
        plateComplete.setSelected(false);

        loadProbes(); // collect probe table data from txt files
        
        DropdownExperimentsChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::itemChanged); 
        DropdownPlatesChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::plateChanged);  

        // populate analytes data. 
        beadCol.setCellValueFactory(new PropertyValueFactory<bead,String>("RegionNumber"));
        analyteCol.setCellValueFactory(new PropertyValueFactory<bead,String>("Analyte"));
        
        // if the ModelForExperiments data members aren't empty, we must reload the data into the Input menu
        if(ModelForExperiments.getInstance().getExperimentsXMLFileMap().size() != 0) {
            reload();
        }
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
    * precondition: ModelForExperiments data members aren't empty.
    * postcondition: if the user leaves the Input menu and then decided 
    * to navigate back to it, all the data in the grid pane and tables
    * will be empty. this method makes sure the data is repopulated. 
    */
    private void reload() {
        fileNames = ModelForExperiments.getInstance().getXMLFiles();
        analytes = ModelForExperiments.getInstance().getAnalytes();
        beadTable.setItems(analytes);
        for(int i = 0; i < fileNames.size(); i++) {
            filesList.getItems().add(fileNames.get(i));
        }
        mapOfExperiments = ModelForExperiments.getInstance().getExperimentsXMLFileMap();
        
        updateExperimentsInfo();
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
                                //int size = Integer.parseInt(s);
                                int size = ModelForExperiments.getInstance().getUserInputsForOneExperiment(curExperiment).get(curPlate).getNumOfProbes();
                                int index = (item.getProbeCount()-1) % size;
                                setStyle(colors.get(index % colors.size()));                    
                            }

                    }
                }
        }
        );
        probeTable.setEditable(true);
        BeadPlateProbeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }
   
    /*
    * this method is called when the user presses the 'Select'
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
            ModelForExperiments.getInstance().setExperiments(FXCollections.observableList(counts));
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
    
    //read analytes from the first xml file and save it to data model. 
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
    

    // called when the 'Reset' button is pressed
    @FXML
    private void resetDataEvent(ActionEvent event) {
        try {
            // uncheck checkboxes upon resetting
            experimentComplete.setSelected(false);
            plateComplete.setSelected(false);
            filesList.getItems().clear();
            uploadFiles.setDisable(false);
            fileNames.clear();

            // xml files map clear previous data associated with xml files, do it in the set up experiment page
            ModelForExperiments.getInstance().getExperimentModel().clear();
            ModelForExperiments.getInstance().getXMLFiles().clear();
            ModelForExperiments.getInstance().getExperimentsXMLFileMap().clear();
            
            // clear choice box for experiment and plate
            ModelForExperiments.getInstance().getExperiments().clear();
            DropdownPlatesChoiceBox.getItems().clear();

            //clear analytes table 
            analytes.clear();
            ModelForExperiments.getInstance().getAnalytes().clear();
            beadTable.refresh();

            //clear text
            totalNumberOfExperiments.setText("0");
            currentExperimentNumber.setText("0");
            XMLfilesNames.setText("null");
            curExperiment = -1;
            curPlate = 0;
            ModelForExperiments.getInstance().setCurrentExperiment(curExperiment);
            ModelForExperiments.getInstance().setCurPlate(curPlate);
            
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
        catch(NullPointerException e) {
            System.out.println("No data to reset.");
        }
    }
    
    // choice box listener for experiments dropdown
    public void itemChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
    {
        if(newValue == null)
            return;
        
        int val = (int)newValue; // curExperiment -1  e.g. 0
        try {
            curExperiment = ModelForExperiments.getInstance().getExperiments().get(val - 1); // get current Experiment number e.g 1 
        }
        catch(IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
        curPlate = 1; // default
        ModelForExperiments.getInstance().setCurPlate(curPlate);
        // change information on the top experiment area 
        currentExperimentNumber.setText(String.valueOf(curExperiment));
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

       userInputsForBeadPlateMap = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap();

       displayUserInput(userInputsForBeadPlateMap.get(curExperiment));

       // reset choicebox for bead plates based on number of bead plates in current experiment
       initPlateChoiceBox(); 
       setBeadsTable();
       
       // set the checkboxes based on if experiment/plate are complete
       if(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).isExpComplete) {
            experimentComplete.setSelected(true);
            plateComplete.setSelected(true);
       }
       else {
            experimentComplete.setSelected(false);
            if(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getBeadPlate(1).isPlateComplete) {
                plateComplete.setSelected(true);
            }
            else
                plateComplete.setSelected(false);
       }
       
        // display bead plate layout and probe tables. 
        //checkLayoutEventHelper();
        displayBeadsPlateLayout(curExperiment);   
        displayProbeTable();
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
        //checkLayoutEventHelper(); 
        
        // set plate checkbox based on if current plate is completed
        if(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getBeadPlate(curPlate).isPlateComplete) {
            plateComplete.setSelected(true);
        }
        else {
            plateComplete.setSelected(false);   
        }
        
        displayBeadsPlateLayout(curExperiment);
        displayProbeTable();
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
        if(experimentsNew == null || experimentsNew.size() == 0) {
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
        currentExperimentNumber.setText(String.valueOf(defaultExperiment));
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
        try
        {
            updateExperimentsInfo();        
        }
        catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    public String[] getSampleNames()
    {
        return names;
    }

    public void clearUserInputForBeadPlate()
    {
        numSampleInput.setText("");
        numReplicaInput.clear();
        sampleNamesInput.clear();
        numProbeInput.clear();
    }  
    
    /*
    * clears the grid pane by deleting contents in each
    * cell one at a time.
    */
    private void clearLayout()
    {
        layoutCellsList = getCells(beadPlateLayout);
        for(int j =0; j <layoutCellsList.size(); j++)
        {
            layoutCellsList.get(j).clear();
            layoutCellsList.get(j).setStyle("-fx-background-color:white;"); // set color of each cell to white
            layoutCellsList.get(j).setEditable(false);
        }
    }

    /*
    * precondition: have a UserInputForBeadPlate initialized for the current
    * bead plate.
    * postcondition: set text fields to contain corresponding values in
    * current bead plates user input values.
    */
    private void displayUserInput(HashMap<Integer, UserInputForBeadPlate> data) {
        // need to get current bead plate of current experiment
        curPlate = ModelForExperiments.getInstance().getCurPlate();
        numSampleInput.setText(Integer.toString(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples()));
        numReplicaInput.setText(Integer.toString(data.get(curPlate).getNumOfReplicas()));
        numProbeInput.setText(Integer.toString(data.get(curPlate).getNumOfProbes()));
        sampleNamesInput.setText(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getNameInput()); 
    }

    // called when user presses the 'Confirm Change' button
    @FXML
    private void checkLayoutEvent(ActionEvent event) {

        // error msg for user has not slected an experiment
       if(curExperiment == -1)
       {
                ErrorMsg error = new ErrorMsg();
                error.showError("choose an experiment first!"); 
                return;
       }
       checkLayoutEventHelper(); // call helper method
    }
    
    /*
    * 
    */
    private void checkLayoutEventHelper()
    {  
        // gather users' input and put it in to map base on size of XML files. 
        UserInputForBeadPlate userInputsForBeadPlate = new UserInputForBeadPlate();
        curExperiment = ModelForExperiments.getInstance().getCurrentExperiment();
        //int size = ModelForExperiments.getInstance().getExperimentsXMLFileMap().get(curExperiment).size();
        
        // add user input to model, return true if successful
        if(!getUserInputforPlate(userInputsForBeadPlate)) {
            return;
        }
     
        //find current experiment, put the experiement into the map associated with curexperiment number. 
        displayBeadsPlateLayout(curExperiment);        
        displayProbeTable();
        //HashMap<Integer, HashMap<Integer, ObservableList<probeTableData>>> probesListForPopulate  = ModelForExperiments.getInstance().getProbeMapForPopulate(); // debug
    }
    
    // display probe table to UI. 
    private void displayProbeTable()
    {
       loadProbeHelper(); // calls method that loads probe table for current bead plate  
    }
    
    /*
    * this method displays the current bead plates contents in a gridpane.
    * it first clears the previous grid pane's contents and checks if the
    * current experiment isn't null before populating the grid pane.
    */
    private void displayBeadsPlateLayout(int curExperiment)
    { 
        clearLayout(); // clear the bead plate layout so we can repopulate it with current plate data 
        HashMap<Integer, HashMap<Integer, UserInputForBeadPlate>> userInputsForBeadPlateMap = ModelForExperiments.getInstance().getUserInputsForBeadPlateMap();
        
        // if experiment is null, there is nothing to display so we return
        if(userInputsForBeadPlateMap.get(curExperiment) == null) return;
        //List<UserInputForBeadPlate> inputs = userInputsForBeadPlateMap.get(curExperiment);
        displayLayout(userInputsForBeadPlateMap.get(curExperiment).get(curPlate)); // display grid pane to UI
    }

    /*
    * populate the grid pane with the contents of the 
    * current bead plate. the contents are based on the 
    * plates number of replicates, probes, samples,
    * and sample names.
    */
    private void displayLayout(UserInputForBeadPlate data) {

         //fill cells with colors and values base on users' inputs
        layoutCellsList = getCells(beadPlateLayout);
        int cellsCount =0;
        String[] nameList = ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getNames();
        int cellsToFill = ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples() * data.getNumOfReplicas()*data.getNumOfProbes(); // total cells need to fill in in the grid pane

        int numberOfProbes=data.getNumOfProbes();
        int numberOfSamples = ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples();
        int numberOfReps = data.getNumOfReplicas();
        while(cellsCount<cellsToFill)
        {
            // set all cells color and text values for the UI
            for(int i = 0; i < numberOfProbes; i++)
            {                    
                String color = colors.get(i % colors.size());  // choose different color to each probe

                for(int j = 0; j < numberOfSamples * numberOfReps; j++)
                {
                    layoutCellsList.get(cellsCount).setStyle(color);  // set color to those cells.
                    layoutCellsList.get(cellsCount).setText(nameList[cellsCount %(nameList.length)] + "." + ((j)/numberOfSamples+1)); //set probe text to those cells
                    layoutCellsList.get(cellsCount).setEditable(false);
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
    private void editBeadForPlateEvent(ActionEvent event) {
        if(curExperiment == -1)
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("choose an Experiment first!");     
            return;
        }
        if(numProbeInput.getText() == null || Integer.parseInt(numProbeInput.getText()) <=0 ) 
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("invalid input for number of probe!");  
            return;
        }
        if(probes ==null || probes.size() == 0)
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("please load probe first!");   
            return;
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
    
    /*
    * loads probes to the probe table in the UI, based on number of probes
    * specified in the appropriate text field.
    */
    private void loadProbeHelper() { 
        if(probes.size()!=0)  // if not empty, clear previous data first. 
            probes.clear();

        // if no map generate for current experiment probes, initialize it. 
        if(ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment) == null)
            ModelForExperiments.getInstance().initializeProbeListMap(curExperiment);
        //HashMap<Integer, ObservableList<probeTableData>> map = ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment);
        
        // check if probe text has no input or input is 0, produce error message
        if(numProbeInput.getText() == null || Integer.parseInt(numProbeInput.getText()) == 0 )
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("Please set up probe numbers before load probe!");
        }
        else // load # of probles base on user input 
        {
            ObservableList<probeTableData> probesList = FXCollections.observableArrayList();
            
            // load as many probes to table as inputted in the probes text field
            for(int i = 0; i < /*Integer.parseInt(numProbeInput.getText())*/ ModelForExperiments.getInstance().getUserInputsForOneExperiment(curExperiment).get(curPlate).getNumOfProbes();i++ )
            {
                if(i >= probesToLoad.get(curPlate).size()) break; // text file has limit to how many probes are on each line, so we must account for this
                probeTableData probe = probesToLoad.get(/*ModelForExperiments.getInstance().getCurPlate()*/ curPlate).get(i);
                probesList.add(probe);
            }
            
            //add updated table to the model
            ModelForExperiments.getInstance().addOneProbeListForPopulate(curExperiment,/*ModelForExperiments.getInstance().getCurPlate()*/ curPlate,probesList);
            HashMap<Integer, ObservableList<probeTableData>> probesListForCurExperiment = ModelForExperiments.getInstance().getProbeMapForPopulate().get(curExperiment); 
        
            probes.addAll(ModelForExperiments.getInstance().getProbeListForPopulate(curExperiment, curPlate));
            probeTable.refresh();
        }
    }     
   
    //gather user inputs for bead plate form each text fields 
    //save the userInputsForBeadPlate in to the list passed in
    private boolean getUserInputforPlate(UserInputForBeadPlate userInputsForBeadPlate) {
        String samples = numSampleInput.getText();
        String reps = numReplicaInput.getText();
        String probes = numProbeInput.getText();

        
        if(hasErrorsForUserInput(samples, reps, probes, curPlate)) return false; // validate user input 
        
        // we must check if input is valid for every plate, as changing the number of samples affects entire experiment
        for(int i = 1; i <= ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getNumPlates(); i++) {
            if(i == curPlate)
                continue; // redundant to check for errors, as we already do this before the for loop
            
            UserInputForBeadPlate tmp = ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getBeadPlate(i).getPlateDetails();
            if(hasErrorsForUserInput(samples, String.valueOf(tmp.getNumOfReplicas()), String.valueOf(tmp.getNumOfProbes()), i)) return false;
        }
        
        // convert strings into integers
        int numberOfReps = Integer.parseInt(reps);
        int numberOfProbes =  Integer.parseInt(probes);

        String names = sampleNamesInput.getText();
        String[] nameList = names.split(",");

        //ModelForExperiments.getInstance().setSampleNames(nameList);

        List<String> probeList = new ArrayList<>();
        
        // combine user inputs and probeList into a UserInputForBeadPlate object
        userInputsForBeadPlate = new UserInputForBeadPlate(numberOfReps,numberOfProbes, probeList);
        ModelForExperiments.getInstance().addOneUserInputForPopulate(curExperiment, curPlate, userInputsForBeadPlate); // add user input to model
        ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).setSamples(names); // set sample size for entire experiment
        numSampleInput.setText(Integer.toString(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getSamples()));

        // set boolean variable for plate to true after adding data to ModelForExperiments.java
        ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).getBeadPlate(curPlate).isPlateComplete = true;
        plateComplete.setSelected(true); // set checkbox to true, gives visual indicator that plate data is saved to model
        
        // do a check to see if the entire experiments set of plates have been added to the model
        ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).isExperimentComplete();
        
        // check if experiment data has been stored in model
        if(ModelForExperiments.getInstance().getExperimentModel().get(curExperiment).isExpComplete)
            experimentComplete.setSelected(true); // set checkbox to true, gives visual indicator that experiment data is saved to model
        
        return true;
    }

    /*
    * Method checks that the values entered in the the text fields on the UI are valid values.
    * This includes making sure that samples, replicates, and probes are proper integers,
    * that they aren't empty strings, and that the product of these values doesn't exceed the 
    * number of cells in the gridpane (96 cells).
    */
    private boolean hasErrorsForUserInput(String samples, String reps, String probes, int plateNum)
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
        if( Integer.parseInt(probes) > probesToLoad.get(plateNum).size()) {
            ErrorMsg error = new ErrorMsg();
            error.showError("number of probes exceeds amount available in text file!");
            return true;
        }
        int numberOfSamples = Integer.parseInt(samples);
        int numberOfReps = Integer.parseInt(reps);
        int numberOfProbes =  Integer.parseInt(probes);  
        //check whether input is valid 
        
        // product of values must NOT exceed 96, 96 is the number of cells in our plate grid pane.
        if(numberOfReps * numberOfProbes * numberOfSamples > 96) // this if statement may need to be changed
        {
            ErrorMsg error = new ErrorMsg();
            error.showError("user input for Experiment " + curExperiment + " bead plate " + plateNum + " exceeds 96!");
            return true;
        }
        return false;
    }

    // called when user presses the experiment choicebox.
    @FXML
    private void changeExperimentEvent(MouseEvent event) {
        // ensures user inputs save when switching between experiments
        //checkLayoutEventHelper();
        if(ModelForExperiments.getInstance().getExperimentModel().isEmpty()) {
            return;
        }
        displayBeadsPlateLayout(curExperiment);        
        displayProbeTable();
    }

    // called when user presses the plate choicebox.
    @FXML
    private void changePlateEvent(MouseEvent event) {
        // ensures user inputs save when switching between plates of the same experiment
        //checkLayoutEventHelper();
        if(ModelForExperiments.getInstance().getExperimentModel().isEmpty()) {
            return;
        }
        displayBeadsPlateLayout(curExperiment);        
        displayProbeTable();
    }

    @FXML
    private void editProbeCellEvent(CellEditEvent<probeTableData, String> edittedCell) {
        probeTableData probeSelected = probeTable.getSelectionModel().getSelectedItem();
        probeSelected.setProbeForPlate(edittedCell.getNewValue().toString());
    } 
}
