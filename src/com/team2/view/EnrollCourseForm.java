package com.team2.view;

import java.util.List;

import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.Course;
import com.team2.model.Student;
import com.team2.model.StudentCourse;
import com.team2.service.CourseService;
import com.team2.service.StudentCourseService;
import com.team2.service.StudentService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EnrollCourseForm {
	private final Stage stage;
	private final TableView<StudentCourse> enrollmentTable;
	private final ComboBox<Student> studentDropdown;
	private final ComboBox<Course> courseDropdown;

	public EnrollCourseForm(TableView<StudentCourse> listTable) {
		this.stage = new Stage();
		this.enrollmentTable = listTable;

		// Initialize dropdowns
		studentDropdown = new ComboBox<>();
		courseDropdown = new ComboBox<>();

		createForm();
	}

	private void createForm() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Labels
		Label studentLabel = new Label("Select Student:");
		Label courseLabel = new Label("Select Course:");

		// Load dropdowns with data
		loadStudentDropdown();
		loadCourseDropdown();

		// Save Button
		Button saveButton = new Button("Enroll");
		saveButton.setOnAction(e -> handleAdd());

		vbox.getChildren().addAll(studentLabel, studentDropdown, courseLabel, courseDropdown, saveButton);
		Scene scene = new Scene(vbox, 300, 250);
		stage.setScene(scene);
		stage.setTitle("Enroll Student");
		stage.show();
	}

	private void loadStudentDropdown() {
		try {
			List<Student> students = StudentService.listStudents();
			ObservableList<Student> studentList = FXCollections.observableArrayList(students);
			studentDropdown.setItems(studentList);

		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void loadCourseDropdown() {
		try {
			List<Course> courses = CourseService.listCourses();
			ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
			courseDropdown.setItems(courseList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void handleAdd() {
		try {
			Student selectedStudent = studentDropdown.getValue();
			Course selectedCourse = courseDropdown.getValue();

			if (selectedStudent == null) {
				AlertDialog.showWarning("Invalid Input", "Please select a student.");
				return;
			}

			if (selectedCourse == null) {
				AlertDialog.showWarning("Invalid Input", "Please select a course.");
				return;
			}

			boolean isSuccess = StudentCourseService.enrollStudentInCourse(selectedCourse.getCourseId(),
					selectedStudent.getStudentId());

			if (isSuccess) {
				loadEnrollments(); // Refresh table
				AlertDialog.showSuccess("Success", "Student successfully enrolled!");
				stage.close();
			}
		} catch (CRUDFailedException e) {
			AlertDialog.showWarning("CRUD Failed", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void loadEnrollments() {
		try {
			List<StudentCourse> enrollments = StudentCourseService.listAllEnrollments();
			ObservableList<StudentCourse> enrollmentList = FXCollections.observableArrayList(enrollments);
			enrollmentTable.setItems(enrollmentList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}
}
