package View;

import Server.Configurations;
import ViewModel.ViewModel;
import algorithms.mazeGenerators.*;
import algorithms.search.AState;
import algorithms.search.Solution;
import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;


import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements IView, Observer {
     public MazeDisplayer mazeDisplayer;
     IMazeGenerator mazeGenerator;
     int rowSize;
     int colSize;
    public VBox menuBox;
    public Button exitButton;
    FileChooser fileChooser=new FileChooser();
    Maze maze;
    boolean isPropAlreadySet=false;
    MediaPlayer mediaPlayer;
    boolean isEnded=false;
     ViewModel viewModel;
     Solution sol;
    private int playerRow;
    private int playerCol;


    public void newButtonClick(ActionEvent actionEvent) {
        if (!isPropAlreadySet){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Properties alert");
            a.setContentText("maze properties has'nt been set yet\ngo to options->properties to set maze properties");
            a.show();
            return;
        }
        viewModel.generateMaze();
        setMaze();
        //unlock keys
        isEnded=false;
        //music set
        String uriString = new File("./resources/music.mp3").toURI().toString();
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
        mediaPlayer = new MediaPlayer(new Media(uriString));

        mediaPlayer.play();
        //focus on mazeDisplay
        mazeDisplayer.requestFocus();


    }

    public void saveButtonClick(ActionEvent actionEvent) {


        //setting initial diractory
        fileChooser.setInitialDirectory(new File("C:\\"));
        //getting main stage
        Window stage = menuBox.getScene().getWindow();
        //set titles and names
        fileChooser.setTitle("Save maze");
        fileChooser.setInitialFileName("mazeName");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("maze file","*.maze"));
        //getting the file and saving it
        File f = fileChooser.showSaveDialog(stage);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(maze);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void loadButtonClick(ActionEvent actionEvent) {
        //getting main stage
        Window stage = menuBox.getScene().getWindow();
        //set titles and names
        fileChooser.setTitle("Load maze");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("maze file","*.maze"));
        //getting the file and loading it
        File f = fileChooser.showOpenDialog(stage);

        FileInputStream fi = null;
        try {
            fi = new FileInputStream(f);
            ObjectInputStream oi = new ObjectInputStream(fi);
            maze = (Maze)oi.readObject();
            viewModel.setMaze(maze);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setMaze();
    }


    public void helpButtonClick(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Help");
        a.setContentText("this is a maze game\ngo to goal position to win !");
        a.show();
    }

    public void aboutButtonClick(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("About");
        a.setContentText("programmers: Liel Binyamin - ID:319081600\n   Harel Moshayof - ID:315073510\nalgorithm used:"+Configurations.getInstance().getMazeSearchingAlgorithm()
                        +"\ngenerating method: "+Configurations.getInstance().getMazeGeneratingAlgorithm());
        a.showAndWait();
    }

    public void exitBottonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) menuBox.getScene().getWindow();
        stage.close();
    }

   public void propOnClick(ActionEvent actionEvent) {
    // Create the custom dialog.
    Dialog<ButtonType> dialog = new Dialog<ButtonType>();

    dialog.setTitle("Properties");
    dialog.setHeaderText("Please set all game properties");
    // Set the button types.
    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);


//       dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//todo: add icon here


 // Create labels and fields.
     GridPane grid = new GridPane();
     grid.setHgap(10);
     grid.setVgap(10);
     grid.setPadding(new Insets(20, 150, 10, 10));

    TextField numberOfClients = new TextField();
    numberOfClients.setPromptText("enter client number");
    TextField rowSize = new TextField();
    rowSize.setPromptText("enter row Size");
    TextField colSize = new TextField();
    colSize.setPromptText("enter column Size");

    ArrayList<String> generatorChoice = new ArrayList<String>();
    ArrayList<String> algorithmChoice = new ArrayList<String>();
    //add compo box options
    generatorChoice.add("MyMazeGenerator");
    generatorChoice.add("SimpleMazeGenerator");
    generatorChoice.add("EmptyMazeGenerator");

    algorithmChoice.add("BFS");
    algorithmChoice.add("DFS");
    algorithmChoice.add("Best First Search");



   //adding elements to the grid
    grid.add(new Label("client number:"), 0, 0);
    grid.add(numberOfClients, 1, 0);
    grid.add(new Label("row Size:"), 0, 1);
    grid.add(rowSize, 1, 1);
    grid.add(new Label("column Size:"), 0, 2);
    grid.add(colSize, 1, 2);

    ObservableList<String> generatorOptions = FXCollections.observableArrayList(generatorChoice);
    ComboBox<String> generatorComboBox = new ComboBox<String>(generatorOptions);
    grid.add(new Label("Generator type:"),0,3);
    grid.add(generatorComboBox, 1, 3);

    ObservableList<String> algorithmOptions = FXCollections.observableArrayList(algorithmChoice);
    ComboBox<String> algorithmComboBox = new ComboBox<String>(algorithmOptions);
    grid.add(new Label("solving algorithm"),0,4);
    grid.add(algorithmComboBox, 1, 4);
    algorithmComboBox.setPromptText("enter solving algorithm");
    generatorComboBox.setPromptText("enter maze generating method");

       // Enable/Disable button.

       Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
       okButton.disableProperty().bind(numberOfClients.textProperty().isEmpty()
               .or(rowSize.textProperty().isEmpty()).or(colSize.textProperty().isEmpty()).or(algorithmComboBox.valueProperty().isNull())
               .or(generatorComboBox.valueProperty().isNull()));



       //setting dialog box content and display
    dialog.getDialogPane().setContent(grid);
    Optional<ButtonType> choosed = dialog.showAndWait();
    if( choosed.isPresent() && choosed.get() == okButtonType){
        if(!isNumeric(rowSize.getText()) || !isNumeric(colSize.getText()) || !isNumeric(numberOfClients.getText())){

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("One of the text fields is not a number");
            a.showAndWait();
            return;
        }

        viewModel.setRowSize(Integer.parseInt(rowSize.getText()));
        viewModel.setColSize(Integer.parseInt(colSize.getText()));

        int numberOfClientsInt = Integer.parseInt(numberOfClients.getText());
        String generatorType = generatorComboBox.getValue();
        String algorithmType = algorithmComboBox.getValue();


        Configurations.getInstance().setThreadPoolSize(String.valueOf(numberOfClientsInt));
        Configurations.getInstance().setMazeGeneratingAlgorithm(generatorType);
        Configurations.getInstance().setMazeSearchingAlgorithm(algorithmType);
        isPropAlreadySet=true;
    }





   }
   private void setMaze(){
       mazeDisplayer.setPlayerImage("./resources/player_front.jpg");
       mazeDisplayer.setWallImage("./resources/wall.jpg");
       mazeDisplayer.setPlayerPosition(playerRow,playerCol);
       mazeDisplayer.displayMaze(maze);


   }
    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void keyPressed(KeyEvent keyEvent) {

        if(!isEnded){
            viewModel.moveCharacter(keyEvent);

            mazeDisplayer.setPlayerPosition(playerRow, playerCol);

            keyEvent.consume();
            //lock keys in case of win
            if (playerRow==maze.getGoalPosition().getRowIndex()&&playerCol==maze.getGoalPosition().getColumnIndex()){
                isEnded=true;
                mediaPlayer.pause();
                String uriString = new File("./resources/winMusic.mp3").toURI().toString();
                mediaPlayer = new MediaPlayer(new Media(uriString));
                mediaPlayer.play();
            }
        }


    }

    public void mouseClicked(MouseEvent mouseEvent) {

        mazeDisplayer.requestFocus();
        if(mouseEvent.isDragDetect()){
//todo
        }
    }


    public void showSolution(ActionEvent actionEvent) {
        viewModel.solveMaze();
        mazeDisplayer.drawSolution(sol);
        mazeDisplayer.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof ViewModel)
        {
            if(maze == null)//generateMaze
            {
                rowSize=viewModel.getRowSize();
                colSize=viewModel.getColSize();
                this.maze = viewModel.getMaze();

            }
            else {
                Maze maze = viewModel.getMaze();

                if (maze.hashCode() == this.maze.hashCode())//Not generateMaze
                {
                    int pRow = mazeDisplayer.getPlayerRow();
                    int pCol = mazeDisplayer.getPlayerCol();
                    int rowFromViewModel = viewModel.getPlayerRow();
                    int colFromViewModel = viewModel.getPlayerCol();

                    if(rowFromViewModel == pRow && colFromViewModel == pCol)//Solve Maze
                    {
                        sol = viewModel.getSolution();

                    }
                    else//Update location
                    {
                        playerRow=viewModel.getPlayerRow();
                        playerCol=viewModel.getPlayerCol();
                        mazeDisplayer.setPlayerPosition(rowFromViewModel,colFromViewModel);

                    }


                }
                else//GenerateMaze or setMaze
                {
                    rowSize=viewModel.getRowSize();
                    colSize=viewModel.getColSize();
                    this.maze = maze;

                }
            }
        }
    }
    public void setViewModel(ViewModel vm){
        viewModel=vm;
    }
}
