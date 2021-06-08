package View;


import algorithms.mazeGenerators.Maze;

import algorithms.mazeGenerators.Position;
import algorithms.search.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.core.appender.nosql.DefaultNoSqlObject;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas implements IDisplayer {
    private StringProperty playerImage = new SimpleStringProperty();

    private StringProperty wallImage = new SimpleStringProperty();

    private Maze maze;
    private int playerRow;
    private int playerCol;
    private boolean isWon;
    double cellHeight;
    double cellWidth;
    GraphicsContext graphicsContext;
    private IControlPlusScroll controlPlusScrollStrategy;
    private StringProperty leftFacePath=new SimpleStringProperty();
    private StringProperty rightFacePath=new SimpleStringProperty();
    private StringProperty upFacePath=new SimpleStringProperty();
    private StringProperty downFacePath=new SimpleStringProperty();
    private StringProperty solutionImagePath=new SimpleStringProperty();
    private StringProperty winImagePath=new SimpleStringProperty();
    private StringProperty startImagePath=new SimpleStringProperty();
    private StringProperty endImagePath=new SimpleStringProperty();
    private StringProperty roadImage=new SimpleStringProperty();

    public void setSolutionImagePath(String solutionImagePath) {
        this.solutionImagePath.set(solutionImagePath);
    }


    public void setStartImagePath(String startImagePath) {
        this.startImagePath.set(startImagePath);
    }

    public void setEndImagePath(String endImagePath) {
        this.endImagePath.set(endImagePath);
    }

    public void setWinImagePath(String winImagePath) {
        this.winImagePath.set(winImagePath);
    }

    public void setLeftFacePath(String leftFacePath) {
        this.leftFacePath.set( leftFacePath);
    }

    public void setRightFacePath( String rightFacePath) {
        this.rightFacePath.set(rightFacePath);
    }

    public void setUpFacePath(String upFacePath) {
        this.upFacePath.set( upFacePath);
    }

    public void setDownFacePath(String downFacePath) {
        this.downFacePath.set(downFacePath);
    }

    public Maze getMaze() {
        return maze;
    }

    /**
     * Drawing the maze / picture of win when you reach the goal
     */
    public MazeDisplayer(){
        this.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(!isWon){
                    Display();
                }
                else{
                    drawWin();
                }
            }
        }

                );
        this.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(!isWon){
                    Display();
                }
                else{
                    drawWin();
                }
            }
        });
    }

    public void displayMaze(Maze maze) {
        this.maze = maze;
        playerCol=maze.getStartPosition().getColumnIndex();
        playerRow=maze.getStartPosition().getRowIndex();
        Display();
    }

    public void setPlayerImage(String player) {
        this.playerImage.set(player);
    }

    public String getPlayerImagePath() {
        return playerImage.get();
    }

    public String getWallImagePath() {
        return wallImage.get();
    }

    public void setWallImage(String wallImage) {
        this.wallImage.set(wallImage);
    }

    /**
     * At each movement of the player a set is made to the corresponding image respectively
     */
    public void setPlayerPosition(int row, int col) {
        if(maze==null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You are trying to move the player but the maze is null\nplease create a maze to enable this action");
            alert.showAndWait();
            return;
        }
        //up
        if (row < playerRow) {
            setPlayerImage(upFacePath.get());
        }
        //down
        if (row > playerRow) {
            setPlayerImage(downFacePath.get());
        }
        //left
        if (col < playerCol) {
            setPlayerImage(leftFacePath.get());
        }
        //right
        if (col > playerCol) {
            setPlayerImage(rightFacePath.get());
        }
        playerRow = row;
        playerCol = col;

        Display();

        if (maze != null && playerRow == maze.getGoalPosition().getRowIndex() && playerCol == maze.getGoalPosition().getColumnIndex()) {
            isWon=true;
            drawWin();

        }
    }

    /**
     * That the player has reached the goal a suitable image will be displayed
     */
    private void drawWin() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        int rowSize = maze.getRowSize();
        int colsSize = maze.getColSize();
        graphicsContext = getGraphicsContext2D();
        //clear the canvas:
        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        Image winImage = null;
        try {
            winImage = new Image(new FileInputStream(winImagePath.get()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no win image file");
        }
        if (winImage == null)
            graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        else
            graphicsContext.drawImage(winImage, 0, 0, canvasWidth, canvasHeight);
    }


    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    /**
     * Drawing the maze
     */
    @Override
    public void Display() {
        if (maze != null) {


            double canvasHeight = getHeight()-25;
            double canvasWidth = getWidth();
            int rowSize = maze.getRowSize();
            int colsSize = maze.getColSize();

            cellHeight = canvasHeight / rowSize;
            cellWidth = canvasWidth / colsSize;

            graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            long t = System.currentTimeMillis();
            DrawRoad(graphicsContext, cellHeight, cellWidth);
            drawStartGoal(graphicsContext, cellHeight, cellWidth);


            DrawWalls(graphicsContext, cellHeight, cellWidth);


            DrawPlayer(graphicsContext, cellHeight, cellWidth);




        }


    }
    public String getRoadImagePath() {
        return roadImage.get();
    }


    public void setRoadImage(String roadImage) {
        this.roadImage.set(roadImage);
    }


    /**
     * Draw the cells in the maze that the player can go through
     * @param graphicsContext the canvas of the maze
     * @param cellHeight cell height
     * @param cellWidth cellWidth
     */
    private void DrawRoad(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image roadImage = null;
        try {
            roadImage = new Image(new FileInputStream(getRoadImagePath()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no road image file");

        }
        graphicsContext.setFill(Color.GREEN);
        for (int i = 0; i < maze.getRowSize(); i++) {
            for (int j = 0; j < maze.getColSize(); j++) {
                double x = j * cellWidth;
                double y = i * cellHeight;
                if (!maze.isPositionAWall(new Position(i, j))) {
                    if (roadImage == null)
                        //draw green rectangle in this road space
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        //draw image in this road space
                        graphicsContext.drawImage(roadImage, x, y, cellWidth, cellHeight);
                } else if (!(maze.getStartPosition().equals(new Position(i, j))) && !maze.getGoalPosition().equals(new Position(i, j))) {
                    Image c = null;
                    graphicsContext.drawImage(c, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    /**
     * Drawing the actor in the maze
     */
    private void DrawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {

        double x = playerCol * cellWidth;
        double y = playerRow * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getPlayerImagePath()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if (playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }


    /**
     * Painting the walls in a maze
     */
    private void DrawWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image wallImage = null;
        try {
            wallImage = new Image(new FileInputStream(getWallImagePath()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");

        }
        graphicsContext.setFill(Color.RED);
        for (int i = 0; i < maze.getRowSize(); i++) {
            for (int j = 0; j < maze.getColSize(); j++) {
                double x = j * cellWidth;
                double y = i * cellHeight;
                if (maze.isPositionAWall(new Position(i, j))) {
                    if (wallImage == null)
                        //draw green rectangle in this wall space
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        //draw image in this wall space
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }
    public void controlPlusScrollHandle(ScrollEvent scrollEvent) {
        try{

            controlPlusScrollStrategy.Activate(scrollEvent,this);
        }
        catch (NullPointerException e){
            System.out.println("please set controlPlusScroll strategy");
        }

    }



    private void drawStartGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = maze.getStartPosition().getColumnIndex() * cellWidth;
        double y = maze.getStartPosition().getRowIndex() * cellHeight;

        graphicsContext.setFill(Color.GREEN);
        //start
        Image startFlag = null;
        try {
            startFlag = new Image(new FileInputStream(startImagePath.get()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no start image file");
        }
        if (startFlag == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(startFlag, x, y, cellWidth, cellHeight);


        x = maze.getGoalPosition().getColumnIndex() * cellWidth;
        y = maze.getGoalPosition().getRowIndex() * cellHeight;
        //goal
        Image goal = null;
        try {
            goal = new Image(new FileInputStream(endImagePath.get()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }
        if (goal == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(goal, x, y, cellWidth, cellHeight);
    }

    public void drawSolution(Solution sol) {
        ArrayList<Position> list= new ArrayList<Position>();
        for (AState aState:
                sol.getSolutionPath()) {

            list.add(aState2Position(aState));
        }
        Image SolutionImage = null;
        try {
            SolutionImage = new Image(new FileInputStream("./resources/path.jpg"));
        } catch (FileNotFoundException e) {
            System.out.println("There is no solution image file");

        }
        graphicsContext.setFill(Color.CORAL);
        for (int i = 0; i < maze.getRowSize(); i++) {
            for (int j = 0; j < maze.getColSize(); j++) {
                if(i==playerRow && j==playerCol){
                    continue;
                }
                double x = j * cellWidth;
                double y = i * cellHeight;
                Position curPos = new Position(i, j);

                if (list.contains(curPos)) {
                    if (SolutionImage == null)
                        //draw green rectangle in this wall space
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        //draw image in this wall space
                        graphicsContext.drawImage(SolutionImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }


    private Position aState2Position(AState aState){
        String str1="",str2="";
        boolean is2=false;
        for (char c:
             aState.toString().toCharArray()) {
            if(c==','){
                is2=true;
                continue;
            }
            if(!is2&&c!='{'){
                str1+=c;
            }
            if(is2&&c!='}'){
                str2+=c;
            }
        }
        return new Position(Integer.valueOf(str1),Integer.valueOf(str2));
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double v) {
        return this.getWidth();
    }

    @Override
    public double prefHeight(double v) {
        return this.getHeight();
    }


    public void setControlPlusScrollStrategy(ZoomOnMaze zoomOnMaze) {
        controlPlusScrollStrategy=zoomOnMaze;
    }
}


