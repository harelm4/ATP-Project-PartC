import Model.*;
import ViewModel.*;
import View.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class main extends Application {
    IModel model;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MyView.fxml"));


        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
        primaryStage.setResizable(true);



        model = new MyModel();
        ViewModel viewModel = new ViewModel(model);
        MyViewController controller = fxmlLoader.getController();
        controller.setViewModel(viewModel);
        viewModel.addObserver(controller);

    }

    @Override
    public void stop() throws Exception {
        model.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
