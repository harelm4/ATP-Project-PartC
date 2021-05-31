package View;

import Server.Configurations;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas implements IDisplayer {
    private StringProperty playerImage = new SimpleStringProperty();
    ;
    private StringProperty wallImage = new SimpleStringProperty();
    ;
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private boolean isWon;
    double cellHeight;
    double cellWidth;
    GraphicsContext graphicsContext;


    public void displayMaze(Maze maze) {
        this.maze = maze;

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

    public void setPlayerPosition(int row, int col) {
        //up
        if (row < playerRow) {
            setPlayerImage("./resources/player_up.png");
        }
        //down
        if (row > playerRow) {
            setPlayerImage("./resources/player_front.jpg");
        }
        //left
        if (col < playerCol) {
            setPlayerImage("./resources/player_left.png");
        }
        //right
        if (col > playerCol) {
            setPlayerImage("./resources/player_right.png");
        }
        playerRow = row;
        playerCol = col;

        Display();

        if (maze != null && playerRow == maze.getGoalPosition().getRowIndex() && playerCol == maze.getGoalPosition().getColumnIndex()) {
            drawWin();

        }
    }

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
            winImage = new Image(new FileInputStream("./resources/you win.jpg"));
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

    @Override
    public void Display() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rowSize = maze.getRowSize();
            int colsSize = maze.getColSize();

            cellHeight = canvasHeight / rowSize;
            cellWidth = canvasWidth / colsSize;

            graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            drawStartGoal(graphicsContext, cellHeight, cellWidth, rowSize, colsSize);
            DrawWalls(graphicsContext, cellHeight, cellWidth, rowSize, colsSize);
            DrawPlayer(graphicsContext, cellHeight, cellWidth);


        }
        else{
            System.out.println("display maze is null");
        }


    }

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


    private void DrawWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
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
                } else if (!(maze.getStartPosition().equals(new Position(i, j))) && !maze.getGoalPosition().equals(new Position(i, j))) {
                    Image c = null;
                    try {
                        c = new Image(new FileInputStream("./resources/road.png"));
                    } catch (FileNotFoundException e) {
                        System.out.println("no road image");
                    }
                    graphicsContext.drawImage(c, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawStartGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        double x = maze.getStartPosition().getColumnIndex() * cellWidth;
        double y = maze.getStartPosition().getRowIndex() * cellHeight;

        graphicsContext.setFill(Color.GREEN);
        //start
        Image startFlag = null;
        try {
            startFlag = new Image(new FileInputStream("./resources/start_flag.png"));
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
            goal = new Image(new FileInputStream("./resources/pizza.jpg"));
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
}
