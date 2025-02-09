package view;

import java.io.IOException;
import java.util.List;

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
import model.Instructor;
import service.InstructorService;

public class InstructorTab {

	public Tab getTab() {
		Tab instructorTab = new Tab("Instructors");
		instructorTab.setClosable(false);
		instructorTab.setContent(createInstructorTabContent());
		return instructorTab;
	}

	private VBox createInstructorTabContent() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Search Section
		GridPane searchPane = new GridPane();
		searchPane.setHgap(10);
		searchPane.setVgap(10);
		searchPane.setPadding(new Insets(10));

		Label searchLabel = new Label("Search Instructor by ID:");
		TextField searchField = new TextField();
		Button searchButton = new Button("Search");
		Button addNewInstructorButton = new Button("Add New Instructor");

		searchPane.add(searchLabel, 0, 0);
		searchPane.add(searchField, 1, 0);
		searchPane.add(searchButton, 2, 0);
		searchPane.add(addNewInstructorButton, 3, 0);

		// TableView Section
		TableView<Instructor> instructorTable = new TableView<>();
		setupTableColumns(instructorTable);

		// Load Data into TableView
		loadInstructors(instructorTable);

		searchButton.setOnAction(e -> clickSearchButton(searchField, instructorTable));
		addNewInstructorButton.setOnAction(e -> new AddInstructorForm(instructorTable));

		vbox.getChildren().addAll(searchPane, instructorTable);
		return vbox;
	}

	@SuppressWarnings("unchecked")
	private void setupTableColumns(TableView<Instructor> instTable) {
		// Instructor ID
		TableColumn<Instructor, Integer> idColumn = new TableColumn<>("Instructor ID");
		idColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getInstructorId()).asObject());

		// Instructor Name - Editable
		TableColumn<Instructor, String> nameColumn = new TableColumn<>("Instructor Name");
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nameColumn.setOnEditCommit(event -> handleEditInstructor(event, instTable));
		nameColumn
				.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

		// Department - Editable
		TableColumn<Instructor, String> departmentColumn = new TableColumn<>("Department");
		departmentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		departmentColumn.setOnEditCommit(event -> handleEditInstructor(event, instTable));
		departmentColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDepartment()));

		// Email - Editable
		TableColumn<Instructor, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		emailColumn.setOnEditCommit(event -> handleEditInstructor(event, instTable));
		emailColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

		// Delete button
		TableColumn<Instructor, Void> actionColumn = new TableColumn<>("Actions");
		actionColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(event -> {
					Instructor instructor = getTableView().getItems().get(getIndex());
					handleDeleteInstructor(instructor, instTable);
				});
			}

			// Not to show button in empty row
			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(deleteButton);
				}
			}
		});

		instTable.getColumns().addAll(idColumn, nameColumn, departmentColumn, emailColumn, actionColumn);
		instTable.setEditable(true);
	}

	private void loadInstructors(TableView<Instructor> instructorTable) {
		List<Instructor> instructors;
		try {
			instructors = InstructorService.listInstructors();
			ObservableList<Instructor> instList = FXCollections.observableArrayList(instructors);
			instructorTable.setItems(instList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}

	}

	private void clickSearchButton(TextField searchField, TableView<Instructor> instructorTable) {
		String input = searchField.getText();
		if (input.isEmpty()) {
			loadInstructors(instructorTable); // Reload all instructors
			return;
		}
		try {
			int instructorId = Integer.parseInt(input);
			Instructor instructor = InstructorService.searchInstructorById(instructorId);
			if (instructor != null) {
				ObservableList<Instructor> result = FXCollections.observableArrayList(instructor);
				instructorTable.setItems(result);
			} else {
				AlertDialog.showWarning("Instructor Not Found", "No instructor found with ID: " + instructorId);
			}
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Result not found", e.getMessage());
		}
	}

	private void handleEditInstructor(TableColumn.CellEditEvent<Instructor, ?> event,
			TableView<Instructor> instructorTable) {
		boolean isConfirmed = AlertDialog.showConfirm("Confirm Edit", "Are you sure you want to edit the instructor?");

		if (!isConfirmed) {
			AlertDialog.showWarning("Edit", "Edit canceled");
			return;

		}

		try {
			Instructor instructor = event.getRowValue();

			if (event.getTableColumn().getText().equals("Instructor Name")) {
				String newName = ((String) event.getNewValue()).trim();
				if (newName.isEmpty()) {
					AlertDialog.showWarning("Invalid Input", "Instructor Name cannot be empty.");
					instructorTable.refresh();
					return;
				}
				instructor.setName(newName);
			}
			if (event.getTableColumn().getText().equals("Department")) {
				String newDep = ((String) event.getNewValue()).trim();
				if (newDep.isEmpty()) {
					AlertDialog.showWarning("Invalid Input", "Department cannot be empty.");
					instructorTable.refresh();
					return;
				}
				instructor.setDepartment(newDep);
			} else if (event.getTableColumn().getText().equals("Email")) {
				String newEmail = ((String) event.getNewValue()).trim();
				if (newEmail.isEmpty()) {
					AlertDialog.showWarning("Invalid Input", "Email cannot be empty.");
					instructorTable.refresh();
					return;
				} else if (!isValidEmail(newEmail)) {
					AlertDialog.showWarning("Invalid Input", "Email format is invalid.");
					instructorTable.refresh();
					return;
				}
				instructor.setEmail(newEmail);
			}

			boolean isSuccess = InstructorService.updateInstructor(instructor);
			if (isSuccess)
				AlertDialog.showSuccess("Success", "Successfully updated instructor: " + instructor.getInstructorId());

		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}

	}

	private void handleDeleteInstructor(Instructor instructor, TableView<Instructor> instructorTable) {
		if (instructor == null) {
			AlertDialog.showWarning("No Selection", "Please select a instructor to delete.");
			return;
		}

		boolean isConfirmed = AlertDialog.showConfirm("Confirm Delete",
				"Are you sure you want to delete the instructor: " + instructor.getInstructorId() + " - "
						+ instructor.getName() + "?");

		if (isConfirmed) {

			boolean hasRel;
			try {
				hasRel = InstructorService.checkInstructorHasRelation(instructor.getInstructorId());
				if (hasRel) {
					isConfirmed = AlertDialog.showConfirm("Confirm",
							"This instructor has assigned course. Do you want to delete assignment as well?");

					if (!isConfirmed) {
						AlertDialog.showWarning("Delete", "Deletion canceled");
						return;

					}
				}

				// Delete data
				boolean isSuccess = InstructorService.deleteInstructor(instructor.getInstructorId());
				if (isSuccess)
					AlertDialog.showSuccess("Success",
							"Successfully deleted instructor: " + instructor.getInstructorId());

				// refresh table
				loadInstructors(instructorTable);
			} catch (DataNotFoundException e) {
				AlertDialog.showWarning("Result not found", e.getMessage());
			} catch (CRUDFailedException | IOException e) {
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
