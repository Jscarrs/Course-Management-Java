import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/***
 * Main class - Entry point of application
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		Label label = new Label("Our tabs will be here");
		StackPane root = new StackPane(label);
		Scene scene = new Scene(root, 900, 600);
		primaryStage.setTitle("Course Management System");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}