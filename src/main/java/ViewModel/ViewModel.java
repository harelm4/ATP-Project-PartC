package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {
    private IModel model;
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private int colSize;
    private int rowSize;
    private int numberOfThreads;
    private Solution sol;


    public ViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
    }
    public void setModel(IModel model){
        this.model=model;
    }


    public Maze getMaze() {
        return maze;
    }


    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel)
        {
            if(maze == null)//generateMaze
            {
                rowSize=model.getRowSize();
                colSize=model.getColSize();
                this.maze = model.getMaze();
            }
            else {
                Maze maze = model.getMaze();

                if (maze.hashCode() == this.maze.hashCode())//No change in maze
                {
                    int row = model.getPlayerRow();
                    int col = model.getPlayerCol();
                    if(this.playerCol == col && this.playerRow == row)//Solve Maze
                    {

                        sol=model.getSolution();

                    }
                    else//Update location
                    {
                        this.playerCol = col;
                        this.playerRow = row;
                    }


                }
                else//GenerateMaze or setMaze or updating row/column size
                {
                    this.rowSize=model.getRowSize();
                    this.colSize=model.getColSize();
                    this.maze = maze;
                }
            }

            setChanged();
            notifyObservers();
        }
    }


    public void generateMaze()
    {
        this.model.generateMaze();
    }

    /**
     * Depending on the event that came from the keyEvent we will change the direction and send it to the model to change the position of the player
     * @param keyEvent The key event
     */
    public void moveCharacter(KeyEvent keyEvent)
    {
        String direction = "";

        switch (keyEvent.getCode()) {
            //up
            case  NUMPAD8 :
            case  W:
                direction="up";
                break;
            //down
            case NUMPAD2:
            case X:
                direction="down";
                break;
            //right
            case NUMPAD6:
            case D:
                direction="right";
                break;

            //left
            case NUMPAD4:
            case A:
                direction="left";
                break;
            //left-up
            case NUMPAD7:
            case Q:
                direction="left-up";
                break;
            //right-up
            case NUMPAD9:
            case E:
                direction="right-up";
                break;
            //right-down
            case NUMPAD3:
            case C:
                direction="right-down";
                break;
            //left-down
            case NUMPAD1:
            case Z:
                direction="left-down";
                break;


        }

        model.updateCharacterLocation(direction);
        setChanged();
        notifyObservers();
    }

    public void solveMaze()
    {
        model.solveMaze();
    }

    public Solution getSolution()
    {
        return model.getSolution();
    }

    public void setMaze(Maze maze) {
        model.setMaze(maze);
    }

    public int getColSize() {
        return colSize;
    }

    public void setColSize(int colSize) {
        model.setColSize(colSize);
    }

    public int getRowSize() {
        return rowSize;
    }

    public void setRowSize(int rowSize) {
        model.setRowSize(rowSize);
    }


    public void refreshThreadPoolSize() {
        model.refreshThreadPoolSize();
    }

    public void stop() {
        model.stop();
    }

    public void movePlayerDrag(int mouseX,int mouseY){

            String direction="";
            if(mouseX>playerCol && mouseY==playerRow){
                direction="right";
            }
            if(mouseX<playerCol && mouseY==playerRow){
                direction="left";
            }
            if(mouseX==playerCol && mouseY<playerRow){
                direction="up";
            }
            if(mouseX==playerCol && mouseY>playerRow){
                direction="down";
            }
            if(mouseX>playerCol && mouseY>playerRow){
                direction="right-down";
            }
            if(mouseX>playerCol && mouseY<playerRow){
                direction="right-up";
            }
            if(mouseX<playerCol && mouseY>playerRow){
                direction="left-down";
            }
            if(mouseX<playerCol && mouseY<playerRow){
                direction="left-up";
            }
            model.updateCharacterLocation(direction);
            setChanged();
            notifyObservers();


    }

}
