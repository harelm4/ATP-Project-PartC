package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas implements IDisplayer {
        private StringProperty playerImage;
        private StringProperty wallImage;
        private Maze maze;
        private int playerRow ;
        private int playerCol;
        private boolean isWon;




    public void displayMaze(Maze maze) {
        this.playerImage = new SimpleStringProperty();
        this.wallImage = new SimpleStringProperty();
        this.maze= maze;
        playerCol=maze.getColSize();
        playerRow=maze.getRowSize();
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
    public void setPlayerPosition(int row,int col){
        playerRow=row;
        playerCol=col;
    }
    public boolean isValidMove(int row,int col){
        return maze.isPositionAWall(new Position(row,col)) || maze.isPositionOnEdges(new Position(row,col));
    }


    @Override
    public void Display() {
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rowSize = maze.getRowSize();
            int colsSize = maze.getColSize();

            double cellHeight = canvasHeight / rowSize;
            double cellWidth = canvasWidth / colsSize;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            DrawWalls(graphicsContext, cellHeight, cellWidth, rowSize, colsSize);
            DrawPlayer(graphicsContext, cellHeight, cellWidth);
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
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }


    private void DrawWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols){
            Image wallImage = null;
            try{
                wallImage = new Image(new FileInputStream(getWallImagePath()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no wall image file");

            }
            graphicsContext.setFill(Color.RED);
            for (int i = 0; i < maze.getRowSize(); i++) {
                for (int j = 0; j < maze.getColSize(); j++) {
                    if(maze.isPositionAWall(new Position(i,j))){
                        double x = j * cellWidth;
                        double y = i * cellHeight;
                        if(wallImage == null)
                            //draw green rectangle in this wall space
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        else
                            //draw image in this wall space
                            graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                    }
                }
            }
        }

    }
