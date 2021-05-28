package View;

import Server.Configurations;
import algorithms.mazeGenerators.*;
import algorithms.search.AState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jdk.internal.org.jline.reader.Widget;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyViewController implements IView {
   public MazeDisplayer mazeDisplayer;
   IMazeGenerator mazeGenerator;


    public void newButtonClick(ActionEvent actionEvent) {
     String gen=Configurations.getInstance().getMazeGeneratingAlgorithm();
     if(gen.equals("MyMazeGenerator")){
      mazeGenerator= new MyMazeGenerator();
     }
     else if(gen.equals("SimpleMazeGenerator")){
      mazeGenerator= new SimpleMazeGenerator();
     }
     else {
      mazeGenerator = new EmptyMazeGenerator();
     }
     Maze maze = mazeGenerator.generate(1,2);//todo -fix row col

    }

    public void saveButtonClick(ActionEvent actionEvent) {
    }

    public void loadButtonClick(ActionEvent actionEvent) {
    }


    public void helpButtonClick(ActionEvent actionEvent) {
    }

    public void aboutButtonClick(ActionEvent actionEvent) {
    }

    public void exitBottonClick(ActionEvent actionEvent) {

    }

   public void propOnClick(ActionEvent actionEvent) {
    // Create the custom dialog.
    Dialog<ArrayList<String>> dialog = new Dialog<ArrayList<String>>();
    dialog.setTitle("Properties");
    dialog.setHeaderText("Set the flowing properties please");
    // Set the button types.
    ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
 // Create labels and fields.
     GridPane grid = new GridPane();
     grid.setHgap(10);
     grid.setVgap(10);
     grid.setPadding(new Insets(20, 150, 10, 10));

    TextField numberOfClients = new TextField();
    numberOfClients.setPromptText("client number");
    TextField rowSize = new TextField();
    rowSize.setPromptText("row Size");
    TextField colSize = new TextField();
    colSize.setPromptText("row Size");

    ArrayList<String> generatorChoice = new ArrayList<String>();
    ArrayList<String> algorithmChoice = new ArrayList<String>();

    grid.add(new Label("client number:"), 0, 0);
    grid.add(numberOfClients, 1, 0);
    grid.add(new Label("row Size:"), 0, 1);
    grid.add(rowSize, 1, 1);
    grid.add(new Label("row Size:"), 0, 2);
    grid.add(rowSize, 1, 2);

    ObservableList<String> generatorOptions = FXCollections.observableArrayList(generatorChoice);
    ComboBox<String> generatorComboBox = new ComboBox<String>(generatorOptions);
    grid.add(new Label("Generator type:"),0,3);
    grid.add(generatorComboBox, 1, 3);

    ObservableList<String> algorithmOptions = FXCollections.observableArrayList(generatorChoice);
    ComboBox<String> algorithmComboBox = new ComboBox<String>(generatorOptions);
    grid.add(new Label("solving algorithm"),0,4);
    grid.add(generatorComboBox, 1, 4);

    dialog.showAndWait();



   }
}
