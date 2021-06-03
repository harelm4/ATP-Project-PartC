package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    public void generateMaze();
    public Maze getMaze();
    public void updateCharacterLocation(String direction);
    public int getPlayerRow();
    public int getPlayerCol();
    public void assignObserver(Observer o);
    public void solveMaze();
    public Solution getSolution();

    public void setMaze(Maze maze);
    public void setRowSize(int rowSize);
    public void setColSize(int colSize);
    public int getRowSize();
    public int getColSize();


    public void refreshThreadPoolSize();



    public void stop();
}
