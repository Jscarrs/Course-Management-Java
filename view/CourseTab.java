package view;

import java.util.ArrayList;

import exceptions.DataNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Course;
import service.CourseService;

public class CourseTab {

	public Tab getTab() {
		Tab courseTab = new Tab("Courses");
		courseTab.setClosable(false);
		courseTab.setContent(createCourseTabContent());
		return courseTab;
	}

	private VBox createCourseTabContent() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Search Section
		GridPane searchPane = new GridPane();
		searchPane.setHgap(10);
		searchPane.setVgap(10);
		searchPane.setPadding(new Insets(10));

		Label searchLabel = new Label("Search Course by ID:");
		TextField searchField = new TextField();
		Button searchButton = new Button("Search");

		searchPane.add(searchLabel, 0, 0);
		searchPane.add(searchField, 1, 0);
		searchPane.add(searchButton, 2, 0);

		// TableView Section
		TableView<Course> courseTable = new TableView<>();
		setupTableColumns(courseTable);

		// Load Data into TableView
		loadCourses(courseTable);

		searchButton.setOnAction(e -> clickSearchButton(searchField, courseTable));

		vbox.getChildren().addAll(searchPane, courseTable);
		return vbox;
	}

	@SuppressWarnings("unchecked")
	private void setupTableColumns(TableView<Course> courseTable) {
		TableColumn<Course, Integer> idColumn = new TableColumn<>("Course ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));

		TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

		TableColumn<Course, Integer> creditsColumn = new TableColumn<>("Credits");
		creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
		


		courseTable.getColumns().addAll(idColumn, nameColumn, creditsColumn);
	}

	private void loadCourses(TableView<Course> courseTable) {
		ArrayList<Course> courses = CourseService.readBinaryFile();
		ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
		courseTable.setItems(courseList);
	}

	private void clickSearchButton(TextField searchField, TableView<Course> courseTable) {
		String input = searchField.getText();
		if (input.isEmpty()) {
			loadCourses(courseTable); // Reload all courses
			return;
		}
		try {
			int courseId = Integer.parseInt(input);
			Course course = CourseService.searchCourseById(courseId);
			if (course != null) {
				ObservableList<Course> result = FXCollections.observableArrayList(course);
				courseTable.setItems(result);
			} else {
				showAlert("Course Not Found", "No course found with ID: " + courseId);
			}
		} catch (NumberFormatException ex) {
			showAlert("Error", "Please enter a valid numeric Course ID.");
		} catch (DataNotFoundException e) {
			showAlert("Result not found", e.getMessage());
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
