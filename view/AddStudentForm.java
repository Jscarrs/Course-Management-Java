package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Student;
import service.StudentService;
import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;

public class AddStudentForm {

    public AddStudentForm(TableView<Student> studentTable) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Student");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label idLabel = new Label("ID:");
        TextField idField = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Button addButton = new Button("Add");

        gridPane.add(idLabel, 0, 0);
        gridPane.add(idField, 1, 0);
        gridPane.add(nameLabel, 0, 1);
        gridPane.add(nameField, 1, 1);
        gridPane.add(emailLabel, 0, 2);
        gridPane.add(emailField, 1, 2);
        gridPane.add(addButton, 1, 3);

        addButton.setOnAction(e -> {
            handleAddStudent(idField, nameField, emailField, studentTable, stage);
        });

        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void handleAddStudent(TextField idField, TextField nameField, TextField emailField, TableView<Student> studentTable, Stage stage) {
        try {
            int StudentId = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String email = emailField.getText();

            if (StudentId<=0) {
                AlertDialog.showWarning("Invalid Input", "ID cannot be empty.");
                return;
            }

            if (name.isEmpty()) {
                AlertDialog.showWarning("Invalid Input", "Name cannot be empty.");
                return;
            }

            if (email.isEmpty()) {
                AlertDialog.showWarning("Invalid Input", "Email cannot be empty.");
                return;
            }

            Student newStudent = new Student();
            newStudent.setStudentId(StudentId);
            newStudent.setName(name);
            newStudent.setEmail(email);

            boolean isSuccess = StudentService.addStudent(newStudent);
            if (isSuccess) {			
				loadStudents(studentTable);
				AlertDialog.showSuccess("Success", "Student added successfully!");

				stage.close();}
            } catch (NumberFormatException e) {
                AlertDialog.showWarning("Invalid Input", "Student Id cannot be less than 1 or empty.");
            } catch (CRUDFailedException e) {
                AlertDialog.showWarning("CRUD Failed", e.getMessage());
            } catch (DataNotFoundException e) {
                AlertDialog.showWarning("Not found", e.getMessage());
            } catch (Exception e) {
                AlertDialog.showWarning("Error", e.getMessage());
            }
    }

    private void loadStudents(TableView<Student> studentTable) {
        try {
            ObservableList<Student> studentList = FXCollections.observableArrayList(
                    StudentService.listStudents());
            studentTable.setItems(studentList);
        } catch (DataNotFoundException e) {
            AlertDialog.showWarning("Not found", e.getMessage());
        } catch (Exception e) {
            AlertDialog.showWarning("Error", e.getMessage());
        }
    }
}