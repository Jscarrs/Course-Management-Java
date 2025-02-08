package view;

import java.io.IOException;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Student;
import service.StudentService;

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
		idCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStudentId()).asObject());

		TableColumn<Student, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(event -> handleEditStudent(event, studentTable));
		nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

		TableColumn<Student, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
		emailCol.setOnEditCommit(event -> handleEditStudent(event, studentTable));
		emailCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

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
			ObservableList<Student> studentList = FXCollections.observableArrayList(StudentService.listStudents());
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
		Student s = event.getRowValue();
		if ("Name".equals(event.getTableColumn().getText())) {
			s.setName(event.getNewValue().toString());
		}
		if ("Email".equals(event.getTableColumn().getText())) {
			s.setEmail(event.getNewValue().toString());
			try {
				try {
					StudentService.updateStudent(s);
				} catch (DataNotFoundException e) {
					AlertDialog.showWarning("Update Failed", e.getMessage());
				}
			} catch (CRUDFailedException e) {
				// Error handling
			}
		}
	}

	private void handleDeleteStudent(Student s, TableView<Student> studentTable) {
		if (s == null) {
			AlertDialog.showWarning("No Selection", "Please select a student to delete.");
			return;
		}

		boolean isConfirmed = AlertDialog.showConfirm("Confirm Delete",
				"Are you sure you want to delete the student: " + s.getStudentId() + " - " + s.getName() + "?");

		if (isConfirmed) {

			boolean hasRel;
			try {
				hasRel = StudentService.checkStudentHasRelation(s.getStudentId());
				if (hasRel) {
					isConfirmed = AlertDialog.showConfirm("Confirm",
							"This student has enrolled to course. Do you want to delete them as well?");

					if (!isConfirmed) {
						AlertDialog.showWarning("Delete", "Deletion canceled");
						return;

					}
				}

				boolean isSuccess = StudentService.deleteStudent(s.getStudentId());
				if (isSuccess) {
					AlertDialog.showSuccess("Success", "Successfully deleted student: " + s.getStudentId());
					loadStudents(studentTable);
				}

			} catch (DataNotFoundException e) {
				AlertDialog.showWarning("Result not found", e.getMessage());
			} catch (CRUDFailedException | IOException e) {
				AlertDialog.showWarning("Error", e.getMessage());
			}
		} else {
			AlertDialog.showWarning("Delete", "Deletion canceled");
		}
	}
}
