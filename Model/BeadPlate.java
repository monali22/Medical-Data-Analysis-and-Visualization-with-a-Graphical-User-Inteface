/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;
import javafx.collections.FXCollections;

/**
 *
 * @author Amman Nega
 */
public class BeadPlate {
    private ObservableList<probeTableData> plateTable;
    private UserInputForBeadPlate plateDetails;
    
    public BeadPlate() {
        plateTable = FXCollections.observableArrayList();
        plateDetails = new UserInputForBeadPlate();
    }
    
    public BeadPlate(ObservableList<probeTableData> plateTable, UserInputForBeadPlate plateDetails) {
        super();
        this.plateTable = plateTable;
        this.plateDetails = plateDetails;
    }
    
    public BeadPlate(BeadPlate copy) {
        super();
        this.plateTable = copy.plateTable; // doesn't avoid privacy leak
        this.plateDetails = new UserInputForBeadPlate(copy.plateDetails);
    }
    
    public UserInputForBeadPlate getPlateDetails() {
        //return new UserInputForBeadPlate(plateDetails); // avoids privacy leaks 
        return plateDetails;
    }
    
    public void setPlateDetails(UserInputForBeadPlate input) {
        plateDetails = input;
    }
    
    public ObservableList<probeTableData> getPlateTable() {
        return plateTable; // doesn't avoid privacy leak
    }
    
    public void setPlateTable(ObservableList<probeTableData> table) {
        plateTable = table;
    }
    
    public boolean isProbeListEmpty() {
        return plateTable.isEmpty();
    }
    
    public void printList() {
        for(int i = 0; i < plateTable.size(); i++ ) {
            System.out.println(plateTable.get(i));
        }
    }
}
