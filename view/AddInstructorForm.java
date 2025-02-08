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
import model.Instructor;
import service.InstructorService;

public class AddInstructorForm {
	private final Stage stage;
	private final TableView<Instructor> instructorTable;

	public AddInstructorForm(TableView<Instructor> instructorTable) {
		this.stage = new Stage();
		this.instructorTable = instructorTable;
		createForm();
	}

	private void createForm() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		Label instructorIdLabel = new Label("Instructor ID:");
		TextField instructorIdField = new TextField();

		Label instructorNameLabel = new Label("Instructor Name:");
		TextField instructorNameField = new TextField();

		Label instructorDepLabel = new Label("Department:");
		TextField instructorDepField = new TextField();

		Label instructorEmailLabel = new Label("Email:");
		TextField instructorEmailField = new TextField();

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			handleAddInstructor(instructorIdField, instructorNameField, instructorDepField, instructorEmailField);
		});

		vbox.getChildren().addAll(instructorIdLabel, instructorIdField, instructorNameLabel, instructorNameField,
				instructorDepLabel, instructorDepField, instructorEmailLabel, instructorEmailField, saveButton);
		Scene scene = new Scene(vbox, 300, 400);
		stage.setScene(scene);
		stage.setTitle("Add Instructor");
		stage.show();
	}

	private void handleAddInstructor(TextField instructorIdField, TextField instructorNameField,
			TextField instructorDepField, TextField instructorEmailField) {
		try {
			int instructorId = Integer.parseInt(instructorIdField.getText());
			String instructorName = instructorNameField.getText();
			String department = instructorDepField.getText();
			String email = instructorEmailField.getText();

			if (instructorId <= 0) {
				AlertDialog.showWarning("Invalid Input", "Instructor Id cannot be less than 1 or empty.");
				return;
			}

			if (instructorName.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Instructor Name cannot be empty.");
				return;
			}

			if (department.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Department cannot be empty.");
				return;
			}

			if (email.isEmpty()) {
				AlertDialog.showWarning("Invalid Input", "Email cannot be empty.");
				return;
			} else if (!isValidEmail(email)) {
				AlertDialog.showWarning("Invalid Input", "Email format is invalid.");
				return;
			}

			Instructor newInstructor = new Instructor();
			newInstructor.setInstructorId(instructorId);
			newInstructor.setName(instructorName);
			newInstructor.setEmail(email);
			newInstructor.setDepartment(department);
			boolean isSuccess = InstructorService.addInstructor(newInstructor);
			if (isSuccess) {
				// Refreshing table
				loadInstructors();
				AlertDialog.showSuccess("Success", "Instructor added successfully!");

				// Closing automatically when succeed
				stage.close();
			}
		} catch (NumberFormatException e) {
			AlertDialog.showWarning("Invalid Input", "Instructor Id cannot be less than 1 or empty.");
		} catch (CRUDFailedException e) {
			AlertDialog.showWarning("CRUD Failed", e.getMessage());
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void loadInstructors() {
		List<Instructor> instructors;
		try {
			instructors = InstructorService.listInstructors();
			ObservableList<Instructor> instructorList = FXCollections.observableArrayList(instructors);
			instructorTable.setItems(instructorList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.matches(emailRegex);
	}
}
