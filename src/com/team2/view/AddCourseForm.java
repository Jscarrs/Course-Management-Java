package com.team2.view;

import java.util.List;

import com.team2.dao.InstructorDAO;
import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.Course;
import com.team2.model.Instructor;
import com.team2.service.CourseService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddCourseForm {
	private final Stage stage;
	private final TableView<Course> courseTable;
	private ComboBox<Instructor> instructorComboBox;

	public AddCourseForm(TableView<Course> courseTable) {
		this.stage = new Stage();
		this.courseTable = courseTable;
		createForm();
	}

	private void createForm() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		Label courseNameLabel = new Label("Course Name:");
		TextField courseNameField = new TextField();

		Label courseCreditsLabel = new Label("Credits:");
		TextField courseCreditsField = new TextField();

		Label instructorLabel = new Label("Instructor:");
		instructorComboBox = new ComboBox<>();
		loadInstructors();

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			handleAddCourse(courseNameField, courseCreditsField);
		});

		vbox.getChildren().addAll(courseNameLabel, courseNameField, courseCreditsLabel, courseCreditsField,
				instructorLabel, instructorComboBox, saveButton);
		Scene scene = new Scene(vbox, 300, 250);
		stage.setScene(scene);
		stage.setTitle("Add Course");
		stage.show();
	}

	private void handleAddCourse(TextField courseNameField, TextField courseCreditsField) {
		try {
			String courseName = courseNameField.getText();
			int credits = Integer.parseInt(courseCreditsField.getText());

			if (credits <= 0) {
				AlertDialog.showWarning("Invalid Input", "Credits cannot be less than 1 or empty.");
				return;
			}

			if (courseName.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Course Name cannot be empty.");
				return;
			}

			Instructor selectedInstructor = instructorComboBox.getValue();

			Course newCourse = new Course();
			newCourse.setCourseName(courseName);
			newCourse.setCredits(credits);
			newCourse.setInstructor(selectedInstructor);

			boolean isSuccess = CourseService.addCourse(newCourse);
			if (isSuccess) {
				// Refreshing table
				loadCourses();
				AlertDialog.showSuccess("Success", "Course added successfully!");

				// Closing automatically when succeed
				stage.close();
			}
		} catch (NumberFormatException ex) {
			AlertDialog.showWarning("Invalid Input", "Please enter valid numeric values for Course ID and Credits.");
		} catch (CRUDFailedException e) {
			AlertDialog.showWarning("CRUD Failed", e.getMessage());
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

	private void loadInstructors() {
		try {
			List<Instructor> instructors = InstructorDAO.getInstructors();
			ObservableList<Instructor> instructorList = FXCollections.observableArrayList(instructors);
			instructorComboBox.setItems(instructorList);

			instructorComboBox.setPromptText("Select Instructor (Optional)");
		} catch (Exception e) {
			AlertDialog.showWarning("Error", "Failed to load instructors.");
		}
	}

}
