package com.team2.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.Student;
import com.team2.service.StudentService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddStudentForm {

	public AddStudentForm(TableView<Student> studentTable) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Add New Student");

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label nameLabel = new Label("Name:");
		TextField nameField = new TextField();
		Label emailLabel = new Label("Email:");
		TextField emailField = new TextField();
		Button addButton = new Button("Add");

		gridPane.add(nameLabel, 0, 1);
		gridPane.add(nameField, 1, 1);
		gridPane.add(emailLabel, 0, 2);
		gridPane.add(emailField, 1, 2);
		gridPane.add(addButton, 1, 3);

		addButton.setOnAction(e -> {
			handleAddStudent(nameField, emailField, studentTable, stage);
		});

		Scene scene = new Scene(gridPane, 300, 200);
		stage.setScene(scene);
		stage.setTitle("Add Student");
		stage.show();
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void handleAddStudent(TextField nameField, TextField emailField, TableView<Student> studentTable,
			Stage stage) {
		try {
			String name = nameField.getText();
			String email = emailField.getText();

			if (name.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Name cannot be empty.");
				return;
			}

			if (email.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Email cannot be empty.");
				return;
			}

			if (!isValidEmail(email)) {
				AlertDialog.showWarning("Invalid Input", "Email format is invalid.");
				return;
			}

			Student newStudent = new Student();
			newStudent.setName(name);
			newStudent.setEmail(email);

			boolean isSuccess = StudentService.addStudent(newStudent);
			if (isSuccess) {
				loadStudents(studentTable);
				AlertDialog.showSuccess("Success", "Student added successfully!");

				stage.close();
			}
		} catch (NumberFormatException e) {
			AlertDialog.showWarning("Invalid Input", "Student Id cannot be less than 1 or empty.");
		} catch (CRUDFailedException e) {
			AlertDialog.showWarning("CRUD Failed", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void loadStudents(TableView<Student> studentTable) {
		try {
			ObservableList<Student> studentList = FXCollections.observableArrayList(StudentService.listStudents());
			studentTable.setItems(studentList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}
}