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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
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
    public AnchorPane mazePane;
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
    Logger logger = LogManager.getLogger(Logger.class);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Configurator.setRootLevel(Level.ALL);
        mazeDisplayer.heightProperty().bind(mPane.heightProperty());
        mazeDisplayer.widthProperty().bind(mPane.widthProperty());
        newButton.setDisable(true);
        saveButton.setDisable(true);
        loadButton.setDisable(true);
        solButton.setDisable(true);
        vUpButton.setDisable(true);
        vDownButton.setDisable(true);
        muteButton.setDisable(true);

    }

    public void newButtonClick(ActionEvent actionEvent) {


        viewModel.generateMaze();
        setMaze();
        //unlock keys
        isEnded=false;
        saveButton.setDisable(false);
        loadButton.setDisable(false);
        //music set
        String uriString = new File("./resources/music.mp3").toURI().toString();
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
        mediaPlayer = new MediaPlayer(new Media(uriString));
        mediaPlayer.setVolume(0.5);
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
                viewModel.setMaze(maze);
            }

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

    public void exitButtonClick(ActionEvent actionEvent) {
        viewModel.stop();
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
        solButton.setDisable(false);

        viewModel.refreshThreadPoolSize();
    }





   }
   private void setMaze(){
       mazeDisplayer.setPlayerImage("./resources/player_front.jpg");
       mazeDisplayer.setWallImage("./resources/wall.png");
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


    public void Zoom(ScrollEvent scrollEvent) {
        double m_zoom;
        if (scrollEvent.isControlDown()) {
            m_zoom = 1.5;
            if (scrollEvent.getDeltaY() > 0) {
                m_zoom = 1.1*m_zoom;

            } else if (scrollEvent.getDeltaY() < 0) {
                m_zoom = 1.1/ m_zoom;
            }
            if (mazeDisplayer.getScaleX() * m_zoom <0.9)
            {
                mazeDisplayer.setScaleX(1);
                mazeDisplayer.setScaleY(1);
                mazeDisplayer.setTranslateX(0);
                mazeDisplayer.setTranslateY(0);
            }
            else
            {
                mazeDisplayer.zoom(m_zoom, scrollEvent.getSceneX(), scrollEvent.getSceneY());
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * m_zoom);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * m_zoom);
            }
            scrollEvent.consume();     // event handling from the root
            mazeDisplayer.requestFocus();
        }
    }

    public void vUpClick(ActionEvent actionEvent) {
        mediaPlayer.volumeProperty().set(mediaPlayer.getVolume()+0.1);
        mazeDisplayer.requestFocus();
    }

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
        mediaPlayer.volumeProperty().set(mediaPlayer.getVolume()-0.1);
        mazeDisplayer.requestFocus();
    }
}
