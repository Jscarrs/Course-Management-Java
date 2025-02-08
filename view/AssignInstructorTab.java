
package view;

import java.io.IOException;
import java.util.List;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.CourseInstructor;
import service.CourseAssignService;

public class AssignInstructorTab {

	public Tab getTab() {
		Tab assignInstTab = new Tab("Assign Instructor to Course");
		assignInstTab.setClosable(false);
		assignInstTab.setContent(createAssignInstructorTabContent());
		return assignInstTab;
	}

	private VBox createAssignInstructorTabContent() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Search Section
		GridPane searchPane = new GridPane();
		searchPane.setHgap(10);
		searchPane.setVgap(10);
		searchPane.setPadding(new Insets(10));

		Button refreshButton = new Button("Refresh");
		Button addButton = new Button("Assign Instructor");

		searchPane.add(refreshButton, 0, 0);
		searchPane.add(addButton, 1, 0);

		// TableView Section
		TableView<CourseInstructor> assignInstTable = new TableView<>();
		setupTableColumns(assignInstTable);

		// Load Data into TableView
		loadList(assignInstTable);

		refreshButton.setOnAction(e -> loadList(assignInstTable));
		addButton.setOnAction(e -> new AssignInstructorForm(assignInstTable));

		vbox.getChildren().addAll(searchPane, assignInstTable);
		return vbox;
	}

	@SuppressWarnings("unchecked")
	private void setupTableColumns(TableView<CourseInstructor> assignInstTable) {
		// Course ID
		TableColumn<CourseInstructor, Integer> courseIdColumn = new TableColumn<>("Course ID");
		courseIdColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCourseId()).asObject());

		// Instructor ID
		TableColumn<CourseInstructor, Integer> instIDColumn = new TableColumn<>("Instructor ID");
		instIDColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getInstructorId()).asObject());

		// Delete button
		TableColumn<CourseInstructor, Void> actionColumn = new TableColumn<>("Actions");
		actionColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(event -> {
					CourseInstructor courseInst = getTableView().getItems().get(getIndex());
					handleDeleteCourse(courseInst, assignInstTable);
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

		assignInstTable.getColumns().addAll(courseIdColumn, instIDColumn, actionColumn);
	}

	private void loadList(TableView<CourseInstructor> assignInstTable) {
		try {
			List<CourseInstructor> list = CourseAssignService.displayAllInstructorAssignments();

			if (list == null || list.isEmpty()) {
				assignInstTable.getItems().clear();
			} else {
				ObservableList<CourseInstructor> assignList = FXCollections.observableArrayList(list);
				assignInstTable.setItems(assignList);
			}
			assignInstTable.refresh();

		} catch (DataNotFoundException e) {
			assignInstTable.getItems().clear();
			assignInstTable.refresh();
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void handleDeleteCourse(CourseInstructor courseInst, TableView<CourseInstructor> assignInstTable) {
		if (courseInst == null) {
			AlertDialog.showWarning("No Selection", "Please select a course to delete.");
			return;
		}

		boolean isConfirmed = AlertDialog.showConfirm("Confirm Delete", "Are you sure you want to delete the Course: "
				+ courseInst.getCourseId() + " - Instrucutor: " + courseInst.getInstructorId() + " assignment?");

		if (isConfirmed) {
			try {
				// Delete data
				boolean isSuccess = CourseAssignService.deleteCourseInstructor(courseInst.getCourseId(),
						courseInst.getInstructorId());
				if (isSuccess)
					AlertDialog.showSuccess("Success", "Successfully deleted course: " + courseInst.getCourseId());

				// refresh table
				loadList(assignInstTable);
			} catch (CRUDFailedException | IOException e) {
				AlertDialog.showWarning("Error", e.getMessage());
			}
		} else {
			AlertDialog.showWarning("Delete", "Deletion canceled");

		}

	}

}
