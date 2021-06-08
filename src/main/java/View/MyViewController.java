package View;

import Server.Configurations;
import ViewModel.ViewModel;
import algorithms.mazeGenerators.*;
import algorithms.search.Solution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class MyViewController implements IView, Observer, Initializable {

     public MazeDisplayer mazeDisplayer;
     int rowSize;
     int colSize;
    public MenuBar menuBox;
    public Button exitButton;
    FileChooser fileChooser=new FileChooser();
    Maze maze;
    MediaPlayer mediaPlayer;
    boolean isEnded=false;
     ViewModel viewModel;
     Solution sol;
    private int playerRow;
    private int playerCol;
    public AnchorPane mPane;
    public ScrollPane mazePane;
    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem loadButton;
    public Button solButton;
    boolean wantMute=true;
    boolean isMute=false;
    boolean startedMute=false;
    public Button muteButton;
    public Button vUpButton;
    public Button vDownButton;
    public Boolean isCellOfPlayer=false;
    double volumeLevel=0.5;
    double zoomRatio=1;
    int mouseCurX=-1;
    int mouseCurY=-1;

    Logger logger = LogManager.getLogger(Logger.class);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Configurator.setRootLevel(Level.ALL);
//        Configurator.initialize()
        mazeDisplayer.heightProperty().bind(mPane.heightProperty());
        mazeDisplayer.widthProperty().bind(mPane.widthProperty());
        //disable buttons
        newButton.setDisable(true);
        saveButton.setDisable(true);
        solButton.setDisable(true);
        vUpButton.setDisable(true);
        vDownButton.setDisable(true);
        muteButton.setDisable(true);
        //set initial image
        mazeDisplayer.setPlayerImage("./resources/player_front.jpg");
        //set player face images
        mazeDisplayer.setDownFacePath("./resources/player_front.jpg");
        mazeDisplayer.setUpFacePath("./resources/player_up.jpg");
        mazeDisplayer.setLeftFacePath("./resources/player_left.jpg");
        mazeDisplayer.setRightFacePath("./resources/player_right.jpg");
        //set solution image
        mazeDisplayer.setSolutionImagePath("./resources/path.jpg");
        //set Win image
        mazeDisplayer.setWinImagePath("./resources/you win.jpg");
        //set wall image
        mazeDisplayer.setWallImage("./resources/wall.jpg");
        //set start image
        mazeDisplayer.setStartImagePath("./resources/start_flag.jpg");
        //set end image
        mazeDisplayer.setEndImagePath("./resources/pizza.jpg");
        //set road image
        mazeDisplayer.setRoadImage("./resources/road.jpeg");
        //set control plus scroll strategy
        mazeDisplayer.setControlPlusScrollStrategy(new ZoomOnMaze());
        //make scroll var disappear
        mazePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mazePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //initialize thread number to 0
        Configurations.getInstance().setThreadPoolSize("1");






    }


    /**
     * Create a new maze when you press the new button
     * @param actionEvent The event of a click of a button
     */
    public void newButtonClick(ActionEvent actionEvent) {
        viewModel.generateMaze();

        mazeDisplayer.displayMaze(maze);


        //unlock keys
        isEnded=false;
        saveButton.setDisable(false);
        solButton.setDisable(false);
        //music set
        String uriString = new File("./resources/music.mp3").toURI().toString();
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
        mediaPlayer = new MediaPlayer(new Media(uriString));
        mediaPlayer.setVolume(volumeLevel);
        if (!isMute){
            mediaPlayer.play();
            startedMute=true;
        }

        //focus on mazeDisplay
        mazeDisplayer.requestFocus();

        vUpButton.setDisable(false);
        vDownButton.setDisable(false);
        muteButton.setDisable(false);


    }

    /**
     * Save the current maze to disk
     * @param actionEvent The event of a click of a button
     */
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
            if(f!=null){
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream o = new ObjectOutputStream(fos);
                o.writeObject(maze);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Loading a maze from the disk
     * @param actionEvent The event of a click of a button
     */
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
            if(f!=null){
                fi = new FileInputStream(f);
                ObjectInputStream oi = new ObjectInputStream(fi);
                maze = (Maze)oi.readObject();
                mazeDisplayer.setMaze(maze);
                viewModel.setMaze(maze);


            }
            else
                return;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mazeDisplayer.displayMaze(maze);
        //set all buttons
        isEnded=false;
        saveButton.setDisable(false);
        solButton.setDisable(false);
        String uriString = new File("./resources/music.mp3").toURI().toString();
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
        mediaPlayer = new MediaPlayer(new Media(uriString));
        mediaPlayer.setVolume(volumeLevel);
        if (!isMute){
            mediaPlayer.play();
            startedMute=true;
        }

        //focus on mazeDisplay
        mazeDisplayer.requestFocus();

        vUpButton.setDisable(false);
        vDownButton.setDisable(false);
        muteButton.setDisable(false);

    }


    /**
     * An information message showing how to play
     * @param actionEvent The event of a click of a button
     */
    public void helpButtonClick(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Help");
        a.setContentText("this is a maze game\ngo to goal position to win !\nUse the number pad to move\n" +
                "1:left-down\n2:down\n3:right-down\n4:left\n6:right\n7:left-up\n8:up\n9:right-up\n" +
                "or by dragging the player with your mouse.\n");
        a.show();
    }

    /**
     * An information message showing information about the programmers
     * @param actionEvent The event of a click of a button
     */
    public void aboutButtonClick(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("About");
        a.setContentText("programmers: Liel Binyamin - ID:319081600\nHarel Moshayof - ID:315073510\nalgorithm used:"+Configurations.getInstance().getMazeSearchingAlgorithm()
                        +"\ngenerating method: "+Configurations.getInstance().getMazeGeneratingAlgorithm());
        a.showAndWait();
    }

    public void exitButtonClick(ActionEvent actionEvent) {
        viewModel.stop();
        Stage stage = (Stage) menuBox.getScene().getWindow();
        stage.close();
    }

    /**
     * View the properties
     * @param actionEvent The event of a click of a button
     */
   public void propOnClick(ActionEvent actionEvent) {

    // Create the custom dialog.
    Dialog<ButtonType> dialog = new Dialog<ButtonType>();

    dialog.setTitle("Properties");
    dialog.setHeaderText("Please set all game properties");
    // Set the button types.
    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);



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
    grid.add(new Label("Thread pool size:"), 0, 0);
    grid.add(numberOfClients, 1, 0);
    grid.add(new Label("row Size:"), 0, 1);
    grid.add(rowSize, 1, 1);
    grid.add(new Label("column Size:"), 0, 2);
    grid.add(colSize, 1, 2);

    grid.add(new Label("current value:"+Configurations.getInstance().getThreadPoolSize()), 2, 0);
       grid.add(new Label("current value: "+this.rowSize), 2, 1);
       grid.add(new Label("current value: "+this.colSize), 2, 2);
       grid.add(new Label("current value: "+Configurations.getInstance().getMazeGeneratingAlgorithm()), 2, 3);
       grid.add(new Label("current value: "+Configurations.getInstance().getMazeSearchingAlgorithm()), 2, 4);


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
        newButton.setDisable(false);


        viewModel.refreshThreadPoolSize();
    }





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
            playerWonHandle();
            }
        }

    /**
     * When the player reaches the goal, win music will be played
     */
    private void playerWonHandle() {
        if (playerRow==maze.getGoalPosition().getRowIndex()&&playerCol==maze.getGoalPosition().getColumnIndex()){
            isEnded=true;
            mediaPlayer.pause();
            String uriString = new File("./resources/winMusic.mp3").toURI().toString();
            mediaPlayer = new MediaPlayer(new Media(uriString));
            if(!isMute){
                mediaPlayer.setVolume(volumeLevel);
                mediaPlayer.play();
            }

        }


}

    public void mouseClicked(MouseEvent mouseEvent) {

        mazeDisplayer.requestFocus();

    }


    public void showSolution(ActionEvent actionEvent) {
        viewModel.solveMaze();
        mazeDisplayer.drawSolution(sol);
        mazeDisplayer.requestFocus();
    }


    /**
     *  Update the maze when there is a change like changing the player position or creating a new maze
     */
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof ViewModel)
        {
            if(maze == null)//generateMaze
            {
                rowSize=viewModel.getRowSize();
                colSize=viewModel.getColSize();
                this.maze = viewModel.getMaze();
                mazeDisplayer.setMaze(this.maze);

            }
            else {
                Maze maze = viewModel.getMaze();

                if (maze.toString().equals(this.maze.toString()))//Not generateMaze
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
                    this.maze = viewModel.getMaze();
                    mazeDisplayer.setMaze(this.maze);

                }
            }
        }
    }
    public void setViewModel(ViewModel vm){
        viewModel=vm;
        vm.addObserver(this);
    }


    public void Zoom(ScrollEvent scrollEvent) {
        mazeDisplayer.controlPlusScrollHandle(scrollEvent);
        scrollEvent.consume();     // event handling from the root
        mazeDisplayer.requestFocus();

    }

    public void vUpClick(ActionEvent actionEvent) {
        volumeLevel+=0.1;
        mediaPlayer.volumeProperty().set(volumeLevel);
        mazeDisplayer.requestFocus();
    }

    /**
     * @param actionEvent The event of a click of a button
     * Music mute button
     */
    public void muteButton(ActionEvent actionEvent) {
        if(wantMute){
            mediaPlayer.setMute(true);
            wantMute=false;
            muteButton.setText("unMute");
            isMute=true;
        }
        else {
            mediaPlayer.setMute(false);
            wantMute=true;
            muteButton.setText("Mute");
            isMute=false;
            mediaPlayer.setMute(false);
            if(startedMute){
                mediaPlayer.play();
            }

        }

        mazeDisplayer.requestFocus();

    }

    public void vDownClick(ActionEvent actionEvent) {
        volumeLevel-=0.1;
        mediaPlayer.volumeProperty().set(volumeLevel);
        mazeDisplayer.requestFocus();

    }

    /**
     * Dragging the player with the mouse
     * @param mouseEvent The event of dragging with the mouse
     */
    public void MoveWithDrag(MouseEvent mouseEvent) {
        if (maze==null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You are trying to drag but the maze is null\nplease create a maze to enable this action");
            alert.showAndWait();
            return;
        }
        if(!isEnded){
            int mouseX = (int)(mouseEvent.getSceneX()*mazeDisplayer.getScaleX()/mazeDisplayer.cellWidth);
            int mouseY = (int)((mouseEvent.getSceneY()-30)*mazeDisplayer.getScaleY()/mazeDisplayer.cellHeight);

            if(mouseCurY==-1 && mouseCurY==-1){
                mouseCurY=mouseY;
                mouseCurX=mouseX;
            }

            if(!(mouseX-mouseCurX==0 && mouseY-mouseCurY==0)){
                if (mouseY-mouseCurY==0 &&mouseX-mouseCurX!=0 ){
                    viewModel.movePlayerDrag(playerCol+(mouseX-mouseCurX),playerRow);
                }
                if (mouseY-mouseCurY!=0 && mouseX-mouseCurX==0){
                    viewModel.movePlayerDrag(playerCol,playerRow+(mouseY-mouseCurY));
                }
                if (mouseY-mouseCurY!=0 && mouseX-mouseCurX!=0 ){
                    viewModel.movePlayerDrag(playerCol+(mouseX-mouseCurX),playerRow+(mouseY-mouseCurY));
                }
                mazeDisplayer.setPlayerPosition(playerRow, playerCol);
                mouseCurX=-1;
                mouseCurY=-1;
            }
            playerWonHandle();

        }
        mazeDisplayer.requestFocus();
        mouseEvent.consume();


    }


    public void initStartPoint(MouseEvent mouseEvent) {
        mouseCurX=-1;
        mouseCurY=-1;
    }

}
