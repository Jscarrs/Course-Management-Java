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
import model.CourseStudents;
import service.CourseAssignService;

public class EnrollCourseForm {
	private final Stage stage;
	private final TableView<CourseStudents> courseTable;

	public EnrollCourseForm(TableView<CourseStudents> listTable) {
		this.stage = new Stage();
		this.courseTable = listTable;
		createForm();
	}

	private void createForm() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		Label courseIdLabel = new Label("Course ID:");
		TextField courseIdField = new TextField();

		Label studentLabel = new Label("Student ID:");
		TextField studentField = new TextField();

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			handleAdd(courseIdField, studentField);
		});

		vbox.getChildren().addAll(courseIdLabel, courseIdField, studentLabel, studentField, saveButton);
		Scene scene = new Scene(vbox, 300, 250);
		stage.setScene(scene);
		stage.setTitle("Enroll Student");
		stage.show();
	}

	private void handleAdd(TextField courseIdField, TextField studentField) {
		try {
			int courseId = Integer.parseInt(courseIdField.getText());
			int studentId = Integer.parseInt(studentField.getText());

			if (courseId <= 0) {
				AlertDialog.showWarning("Invalid Input", "Course Id cannot be less than 1 or empty.");
				return;
			}

			if (studentId <= 0) {
				AlertDialog.showWarning("Invalid Input", "Student ID cannot be less than 1 or empty.");
				return;
			}

			boolean isSuccess = CourseAssignService.assignStudentToCourse(courseId, studentId);
			if (isSuccess) {
				// Refreshing table
				loadCourses();
				AlertDialog.showSuccess("Success", "Successfully assigned!");

				// Closing automatically when succeed
				stage.close();
			}
		} catch (NumberFormatException ex) {
			AlertDialog.showWarning("Invalid Input", "Please enter valid numeric values for ID fields.");
		} catch (CRUDFailedException e) {
			AlertDialog.showWarning("CRUD Failed", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void loadCourses() {
		List<CourseStudents> courses;
		try {
			courses = CourseAssignService.displayAllStudentAssignments();
			ObservableList<CourseStudents> courseList = FXCollections.observableArrayList(courses);
			courseTable.setItems(courseList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}
}