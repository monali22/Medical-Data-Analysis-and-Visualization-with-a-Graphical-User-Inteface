/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.ModelForExperiments;
import Util.ErrorMsg;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author feiping
 * 
 * The MenuBarController class handles the navigation
 * from one menu to another, and ensures the ModelForExperiments 
 * passes the necessary checks to switch menus.
 */
public class MenuBarController implements Initializable {

    @FXML
    private Menu input;
    @FXML
    private Menu medianValue;
    @FXML
    private Menu FoldChange;
    @FXML
    private Menu ANC;
    @FXML
    private Menu CNA;
    @FXML
    private Menu PCA;
    @FXML
    private Menu NED;

    String[] samplesNames;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //activing a menue like a menu item 
        
        input.setGraphic(
                ButtonBuilder.create()
                        .text("Input")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToInput();
                            }
                        })
                        .build()
        );
        
        medianValue.setGraphic(
                ButtonBuilder.create()
                        .text("Median Value")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToMedianValue();
                            }
                        })
                        .build()
        );

        FoldChange.setGraphic(
                ButtonBuilder.create()
                        .text("Fold Change")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToFoldChange();
                            }

                        })
                        .build()
        );

        ANC.setGraphic(
                ButtonBuilder.create()
                        .text("ANC")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToANC();
                            }

                        })
                        .build()
        );

        CNA.setGraphic(
                ButtonBuilder.create()
                        .text("CNA")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToMedianValue();
                            }

                        })
                        .build()
        );

        PCA.setGraphic(
                ButtonBuilder.create()
                        .text("PCA")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToMedianValue();
                            }

                        })
                        .build()
        );

        NED.setGraphic(
                ButtonBuilder.create()
                        .text("NED")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                switchToMedianValue();
                            }

                        })
                        .build()
        );

    }

    private void switchToInput() {

        try {

            URL paneTwoUrl = getClass().getResource("/View/Homepage.fxml");
            AnchorPane paneTwo = FXMLLoader.load(paneTwoUrl);

            BorderPane border = Main.getRoot();
            border.setCenter(paneTwo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * precondition: the user has tried to naviagate away
    * from the Input menu.
    * postcondition: this method checks if the user has selected
    * xml files in the Input menu, if not, it will show an error 
    * message. 
    * User should not be able to leave input menu until this method
    * returns true.
    */
    public boolean isModelSet() {
        if (ModelForExperiments.getInstance().getExperiments() == null || ModelForExperiments.getInstance().getExperiments().isEmpty()) {
            ErrorMsg error = new ErrorMsg();
            error.showError("Please set up experiments first!");
            return false;
        }
        return true;
    }
    
    /*
    * precondition: the user has tried to navigate away from
    * the Input menu.
    * postcondition: this method checks if all experiments have
    * their data saved in ModelForExperiments.java, if not,
    * it will show an error message.
    */
    public boolean checkModelComplete() {
        if (!ModelForExperiments.getInstance().isModelComplete()) {
            ErrorMsg error = new ErrorMsg();
            error.showError("Please confirm all experiments first!");
            return false;
        }
        return true;
    }
    
    private void switchToMedianValue() {
        // when no experiments has been set up, median value has no data to show. 
        if (ModelForExperiments.getInstance().getExperiments() == null || ModelForExperiments.getInstance().getExperiments().isEmpty()) {
            ErrorMsg error = new ErrorMsg();
            error.showError("Please set up experiments first!");
            return;
        }
        
        // check if experiment data is stored and if all experiments are confirmed
        if(!isModelSet()) return;
        
        if(!checkModelComplete()) return;

        try {

            URL paneTwoUrl = getClass().getResource("/View/MedianValue.fxml");
            AnchorPane paneTwo = FXMLLoader.load(paneTwoUrl);

            BorderPane border = Main.getRoot();
            border.setCenter(paneTwo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToFoldChange() {
        
        if(ModelForExperiments.getInstance().getMapOfSamplesNumbers() == null || ModelForExperiments.getInstance().getMapOfSamplesNumbers().isEmpty()) {
            ErrorMsg error = new ErrorMsg();
            error.showError("Must calculate median value first!");
            return;
        }

        // check if experiment data is stored and if all experiments are confirmed
        if(!isModelSet()) return;
        
        if(!checkModelComplete()) return;
        
        try {

            URL paneTwoUrl = getClass().getResource("/View/FoldChange.fxml");
            AnchorPane paneTwo = FXMLLoader.load(paneTwoUrl);

            BorderPane border = Main.getRoot();
            border.setCenter(paneTwo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToANC() {

        if(ModelForExperiments.getInstance().getMapOfSamplesNumbers() == null || ModelForExperiments.getInstance().getMapOfSamplesNumbers().isEmpty()) {
            ErrorMsg error = new ErrorMsg();
            error.showError("Must calculate median value first!");
            return;
        }
        
        // check if experiment data is stored and if all experiments are confirmed    
        if(!isModelSet()) return;
        
        if(!checkModelComplete()) return;
        
        try {

            URL paneTwoUrl = getClass().getResource("/View/ANC.fxml");
            AnchorPane paneTwo = FXMLLoader.load(paneTwoUrl);

            BorderPane border = Main.getRoot();
            border.setCenter(paneTwo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getSampleNames() {
        return samplesNames;
    }

}
