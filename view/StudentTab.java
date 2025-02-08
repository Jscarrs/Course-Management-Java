/**
 *
 * @author Ariunjarg
 * @created Feb 6, 2025
 */
package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import model.Student; 
import service.StudentService; 
import exceptions.DataNotFoundException;
import exceptions.CRUDFailedException;
import java.io.IOException;
import view.AddStudentForm;

public class StudentTab {

    public Tab getTab() {
        Tab studentTab = new Tab("Students");
        studentTab.setClosable(false);
        studentTab.setContent(createStudentTabContent());
        return studentTab;
    }

    private VBox createStudentTabContent() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        GridPane searchPane = new GridPane();
        searchPane.setHgap(10);
        searchPane.setVgap(10);
        searchPane.setPadding(new Insets(10));

        Label searchLabel = new Label("Search Student by ID:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        Button addNewStudentButton = new Button("Add New Student");
        searchPane.add(searchLabel, 0, 0);
        searchPane.add(searchField, 1, 0);
        searchPane.add(searchButton, 2, 0);
        searchPane.add(addNewStudentButton, 3, 0);

        TableView<Student> studentTable = new TableView<>();
        setupTableColumns(studentTable);
        loadStudents(studentTable);

        searchButton.setOnAction(e -> clickSearchButton(searchField, studentTable));
        addNewStudentButton.setOnAction(e -> handleAddStudent(studentTable));

        vbox.getChildren().addAll(searchPane, studentTable);
        return vbox;
    }

    private void setupTableColumns(TableView<Student> studentTable) {
        TableColumn<Student, Integer> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStudentId()).asObject());

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> handleEditStudent(event, studentTable));
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setOnEditCommit(event -> handleEditStudent(event, studentTable));
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Student, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    handleDeleteStudent(s, studentTable);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        studentTable.getColumns().addAll(idCol, nameCol, emailCol, actionCol);
        studentTable.setEditable(true);
    }

    private void loadStudents(TableView<Student> studentTable) {
        try {
            ObservableList<Student> studentList = FXCollections.observableArrayList(
                    StudentService.listStudents()
            );
            studentTable.setItems(studentList);
        } catch (Exception e) {
            // Error handling
        }
    }

    private void clickSearchButton(TextField searchField, TableView<Student> studentTable) {
        String input = searchField.getText();
        if (input.isEmpty()) {
            loadStudents(studentTable);
            return;
        }
        try {
            int studentId = Integer.parseInt(input);
            Student student = StudentService.searchStudentById(studentId);
            if (student != null) {
                ObservableList<Student> result = FXCollections.observableArrayList(student);
                studentTable.setItems(result);
            } else {
                AlertDialog.showWarning("Student Not Found", "No student found with ID: " + studentId);
            }
        } catch (NumberFormatException ex) {
            AlertDialog.showWarning("Error", "Please enter a valid numeric Student ID.");
        } catch (DataNotFoundException e) {
            AlertDialog.showWarning("Result not found", e.getMessage());
        }
    }

    private void handleAddStudent(TableView<Student> studentTable) {
        new AddStudentForm(studentTable);
    }

    private void handleEditStudent(TableColumn.CellEditEvent<Student, ?> event, TableView<Student> studentTable) {
        boolean isConfirmed = AlertDialog.showConfirm("Confirm Edit", "Are you sure you want to edit the student?");

        if (!isConfirmed) {
            AlertDialog.showWarning("Edit", "Edit canceled");
            return;
        }

        try {
            Student student = event.getRowValue();

            if (event.getTableColumn().getText().equals("Name")) {
                String newName = ((String) event.getNewValue()).trim();
                if (newName.isEmpty()) {
                    AlertDialog.showWarning("Invalid Input", "Name cannot be empty.");
                    studentTable.refresh();
                    return;
                }
                student.setName(newName);
            } else if (event.getTableColumn().getText().equals("Email")) {
                String newEmail = ((String) event.getNewValue()).trim();
                if (newEmail.isEmpty()) {
                    AlertDialog.showWarning("Invalid Input", "Email cannot be empty.");
                    studentTable.refresh();
                    return;
                } else if (!isValidEmail(newEmail)) {
                    AlertDialog.showWarning("Invalid Input", "Email format is invalid.");
                    studentTable.refresh();
                    return;
                }
                student.setEmail(newEmail);
            }

            boolean isSuccess = StudentService.updateStudent(student);
            if (isSuccess)
                AlertDialog.showSuccess("Success", "Successfully updated student: " + student.getStudentId());

        } catch (DataNotFoundException e) {
            AlertDialog.showWarning("Not found", e.getMessage());
        } catch (Exception e) {
            AlertDialog.showWarning("Error", e.getMessage());
        }
    }

    private void handleDeleteStudent(Student s, TableView<Student> studentTable) {
        if (s == null) {
            AlertDialog.showWarning("No Selection", "Please select a student to delete.");
            return;
        }

        boolean isConfirmed = AlertDialog.showConfirm("Confirm Delete", "Are you sure you want to delete the student: "
                + s.getStudentId() + " - " + s.getName() + "?");

        if (isConfirmed) {
            try {
                try {
                    boolean isSuccess = StudentService.deleteStudent(s.getStudentId());
                    if (isSuccess) {
                        AlertDialog.showSuccess("Success", "Successfully deleted student: " + s.getStudentId());
                        loadStudents(studentTable);
                    }
                } catch (IOException e) {
                    AlertDialog.showWarning("Error", "Failed to delete student due to an IO error.");
                }
            } catch (DataNotFoundException e) {
                AlertDialog.showWarning("Result not found", e.getMessage());
            } catch (CRUDFailedException e) {
                AlertDialog.showWarning("Error", e.getMessage());
            }
        } else {
            AlertDialog.showWarning("Delete", "Deletion canceled");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
