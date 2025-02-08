
// Main.java
import java.io.IOException;

import exceptions.InvalidCSVFormatException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.CSVToBinaryConverter;
import view.AlertDialog;
import view.CourseTab;
import view.InstructorTab;
import view.StudentTab;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		// Main Layout
		BorderPane root = new BorderPane();

		// Menu Bar
		MenuBar menuBar = createMenuBar(primaryStage);
		root.setTop(menuBar);

		// Tab Pane
		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(new CourseTab().getTab(), new InstructorTab().getTab(), new StudentTab().getTab());
		root.setCenter(tabPane);

		// Scene and Stage
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setTitle("University Course Management System");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private MenuBar createMenuBar(Stage primaryStage) {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		MenuItem uploadItem = new MenuItem("Import CSV file");
		uploadItem.setOnAction(e -> showUpload());
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> primaryStage.close());
		fileMenu.getItems().addAll(uploadItem, exitItem);

		Menu helpMenu = new Menu("Help");
		MenuItem aboutItem = new MenuItem("About");
		aboutItem.setOnAction(e -> showAboutDialog());
		helpMenu.getItems().add(aboutItem);

		menuBar.getMenus().addAll(fileMenu, helpMenu);
		return menuBar;
	}

	private void showUpload() {
		boolean isConfirmed = AlertDialog.showConfirm("Confirm",
				"Importing a new file will delete all existing data and override it. Are you sure you want to proceed?");
		if (isConfirmed) {
			try {
				CSVToBinaryConverter.convertAllCsvToBinary();
				AlertDialog.showSuccess("File upload", "Successfully imported data.");

			} catch (InvalidCSVFormatException | IOException e) {
				AlertDialog.showWarning("Error", e.getMessage());
			}
		} else {
			AlertDialog.showWarning("File upload", "File upload cancelled");
		}
	}

	private void showAboutDialog() {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("Developed by Team 2");
		alert.setContentText("Ari\nSamuel\nScarlett");
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
