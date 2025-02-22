package com.team2.main;

import com.team2.view.CourseTab;
import com.team2.view.EnrollCourseTab;
import com.team2.view.InstructorTab;
import com.team2.view.StudentTab;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
		tabPane.getTabs().addAll(new CourseTab().getTab(), new InstructorTab().getTab(), new StudentTab().getTab(),
				new EnrollCourseTab().getTab());
		root.setCenter(tabPane);

		// Scene and Stage
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setTitle("University Course Management System");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private MenuBar createMenuBar(Stage primaryStage) {
		MenuBar menuBar = new MenuBar();

		Menu helpMenu = new Menu("Menu");
		MenuItem aboutItem = new MenuItem("About");
		aboutItem.setOnAction(e -> showAboutDialog());
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> primaryStage.close());
		helpMenu.getItems().addAll(aboutItem, exitItem);

		menuBar.getMenus().addAll(helpMenu);
		return menuBar;
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