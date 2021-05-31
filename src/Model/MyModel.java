package Model;

import Server.Configurations;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.media.Media;


import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    private Maze maze;
    private Solution sol;
    private int playerRow;
    private int playerCol;
    private IMazeGenerator mazeGenerator;
    ISearchingAlgorithm solvingAlgorithm;
    private int colSize;
    private int rowSize;



    @Override
    public void generateMaze() {
        String gen= Configurations.getInstance().getMazeGeneratingAlgorithm();
        if(gen.equals("MyMazeGenerator")){
            mazeGenerator= new MyMazeGenerator();
        }
        else if(gen.equals("SimpleMazeGenerator")){

            mazeGenerator= new SimpleMazeGenerator();
        }
        else {
            mazeGenerator = new EmptyMazeGenerator();
        }


        maze = mazeGenerator.generate(rowSize,colSize);
        setChanged();
        notifyObservers();
        playerCol=maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
        playerRow=maze.getStartPosition().getRowIndex();
        setChanged();
        notifyObservers();
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void updateCharacterLocation(String direction) {
        int prevCol=playerCol;
        int prevRow=playerRow;
        switch (direction){
                //up
            case  "up" :
                playerRow-=1;
                break;
            //down
            case "down":
                playerRow += 1;
                break;
            //right
            case "right":
                playerCol += 1;
                break;

            //left
            case "left":
                playerCol -= 1;
                break;
            //left-up
            case "left-up":
                playerCol -= 1;
                playerRow--;
                break;
            //right-up
            case "right-up":
                playerCol += 1;
                playerRow--;
                break;
            //right-down
            case "right-down":
                playerCol += 1;
                playerRow++;
                break;
            //left-down
            case "left-down":
                playerCol -= 1;
                playerRow++;
                break;
            case "control":

                break;
        }

        //if its not a valid move go back to where you came from
        if(!isMoveValid(playerRow,playerCol)){
            playerRow=prevRow;
            playerCol=prevCol;
        }

        setChanged();
        notifyObservers();
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() {

        String conf = Configurations.getInstance().getMazeSearchingAlgorithm();
        if (conf.equals("DFS")) {
            solvingAlgorithm = new DepthFirstSearch();
        } else if (conf.equals("BFS")) {
            solvingAlgorithm = new BestFirstSearch();
        } else {
            solvingAlgorithm = new BestFirstSearch();
        }
        sol = solvingAlgorithm.solve(new SearchableMaze(maze));
        setChanged();
        notifyObservers();
    }

    @Override
    public Solution getSolution() {
        return sol;

    }

    @Override
    public void setMaze(Maze maze) {
        this.maze=maze;
        setChanged();
        notifyObservers();
    }

    @Override
    public void setRowSize(int rowSize) {
        this.rowSize=rowSize;
        setChanged();
        notifyObservers();
    }

    @Override
    public void setColSize(int colSize) {
        this.colSize=colSize;
        setChanged();
        notifyObservers();
    }



    @Override
    public int getRowSize() {
        return rowSize;
    }

    @Override
    public int getColSize() {
        return colSize;
    }

    private boolean isMoveValid(int row,int col){
        return row>=0&&col>=0&&row<maze.getRowSize()&&col<maze.getColSize()&&!maze.isPositionAWall(new Position(row,col));
    }

}
