package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.Configurations;
import Server.*;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;


import java.io.*;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    private Maze maze;
    private Solution sol;
    private int playerRow;
    private int playerCol;
    private int colSize;
    private int rowSize;
    private Server generateServer;
    private Server solvingServer;

    public MyModel(){
        generateServer=new Server(4000,1000,new ServerStrategyGenerateMaze());
        solvingServer=new Server(4001,1000,new ServerStrategySolveSearchProblem());
        generateServer.start();
        solvingServer.start();
    }

    /**
     *When we update the thread pool size in properties we will stop the server and run again with the new size
     */
    public void refreshThreadPoolSize(){
        generateServer.stop();
        solvingServer.stop();
        generateServer=new Server(4000,1000,new ServerStrategyGenerateMaze());
        solvingServer=new Server(4001,1000,new ServerStrategySolveSearchProblem());
        generateServer.start();
        solvingServer.start();
    }
    public void stop(){
        generateServer.stop();
        solvingServer.stop();
    }

    /**
     * Create a new client for a new maze
     */
    @Override
    public void generateMaze() {

        try {
            new Client(InetAddress.getLocalHost(), 4000, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        int[] mazeDimensions = new int[]{rowSize, colSize};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[rowSize*colSize+rowSize]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes

                        maze = new Maze(decompressedMaze);
                    }catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
            ).communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setChanged();
        notifyObservers();
        playerCol=maze.getStartPosition().getColumnIndex();
        playerRow=maze.getStartPosition().getRowIndex();
        setChanged();
        notifyObservers();

    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    /**
     * Update player position
     * @param direction a string that provides the movement of the player
     */
    @Override
    public void updateCharacterLocation(String direction) {
        if(maze==null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You are trying to move the player but the maze is null\nplease create a maze to enable this action");
            return;
        }
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

    /**
     * Create a new client for solving the maze
     */
    @Override
    public void solveMaze() {
        try {
            new Client(InetAddress.getLocalHost(), 4001, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        toServer.flush();
                        MyMazeGenerator mg = new MyMazeGenerator();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        sol= (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                    } catch (Exception e) {

                    }
                }
            }).communicateWithServer();
        } catch (Exception e) {


        }

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
        this.playerCol=maze.getStartPosition().getColumnIndex();

        setChanged();
        notifyObservers();
        this.playerRow=maze.getStartPosition().getRowIndex();
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
