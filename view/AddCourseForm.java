package view;

import java.util.List;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Course;
import service.CourseService;

public class AddCourseForm {
	private final Stage stage;
	private final TableView<Course> courseTable;

	public AddCourseForm(TableView<Course> courseTable) {
		this.stage = new Stage();
		this.courseTable = courseTable;
		createForm();
	}

	private void createForm() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		Label courseIdLabel = new Label("Course ID:");
		TextField courseIdField = new TextField();

		Label courseNameLabel = new Label("Course Name:");
		TextField courseNameField = new TextField();

		Label courseCreditsLabel = new Label("Credits:");
		TextField courseCreditsField = new TextField();

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			handleAddCourse(courseIdField, courseNameField, courseCreditsField);
		});

		vbox.getChildren().addAll(courseIdLabel, courseIdField, courseNameLabel, courseNameField, courseCreditsLabel,
				courseCreditsField, saveButton);
		Scene scene = new Scene(vbox, 300, 250);
		stage.setScene(scene);
		stage.setTitle("Add Course");
		stage.show();
	}

	private void handleAddCourse(TextField courseIdField, TextField courseNameField, TextField courseCreditsField) {
		try {
			int courseId = Integer.parseInt(courseIdField.getText());
			String courseName = courseNameField.getText();
			int credits = Integer.parseInt(courseCreditsField.getText());

			if (courseName.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Course Name cannot be empty.");
				return;
			}

			Course newCourse = new Course(courseId, courseName, credits);
			boolean isSuccess = CourseService.addCourse(newCourse);
			if (isSuccess) {
				// Refreshing table
				loadCourses();
				AlertDialog.showSuccess("Success", "Course added successfully!");
				
				//Closing automatically when succeed
				stage.close();
			}
		} catch (NumberFormatException ex) {
			AlertDialog.showWarning("Invalid Input", "Please enter valid numeric values for Course ID and Credits.");
		} catch (CRUDFailedException e) {
			AlertDialog.showWarning("CRUD Failed", e.getMessage());
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void loadCourses() {
		List<Course> courses;
		try {
			courses = CourseService.listCourses();
			ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
			courseTable.setItems(courseList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}
}
