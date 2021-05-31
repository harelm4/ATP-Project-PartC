import Model.*;
import ViewModel.*;
import View.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View/MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();

        IModel model = new MyModel();
        ViewModel viewModel = new ViewModel(model);
        MyViewController controller = fxmlLoader.getController();
        controller.setViewModel(viewModel);
        viewModel.addObserver(controller);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
