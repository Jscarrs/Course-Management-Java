
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
import model.CourseStudents;
import service.CourseAssignService;

public class EnrollCourseTab {

	public Tab getTab() {
		Tab assignStudentTab = new Tab("Enroll Course");
		assignStudentTab.setClosable(false);
		assignStudentTab.setContent(createTabContent());
		return assignStudentTab;
	}

	private VBox createTabContent() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Search Section
		GridPane searchPane = new GridPane();
		searchPane.setHgap(10);
		searchPane.setVgap(10);
		searchPane.setPadding(new Insets(10));

		Button refreshButton = new Button("Refresh");
		Button addButton = new Button("Enroll Course");

		searchPane.add(refreshButton, 0, 0);
		searchPane.add(addButton, 1, 0);

		// TableView Section
		TableView<CourseStudents> assignStudentTable = new TableView<>();
		setupTableColumns(assignStudentTable);

		// Load Data into TableView
		loadList(assignStudentTable);

		refreshButton.setOnAction(e -> loadList(assignStudentTable));
		addButton.setOnAction(e -> new EnrollCourseForm(assignStudentTable));

		vbox.getChildren().addAll(searchPane, assignStudentTable);
		return vbox;
	}

	@SuppressWarnings("unchecked")
	private void setupTableColumns(TableView<CourseStudents> assignStudentTable) {
		// Course ID
		TableColumn<CourseStudents, Integer> courseIdColumn = new TableColumn<>("Course ID");
		courseIdColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCourseId()).asObject());

		// Student ID
		TableColumn<CourseStudents, Integer> studentIdColumn = new TableColumn<>("Student ID");
		studentIdColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStudentId()).asObject());

		// Delete button
		TableColumn<CourseStudents, Void> actionColumn = new TableColumn<>("Actions");
		actionColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(event -> {
					CourseStudents courseStudent = getTableView().getItems().get(getIndex());
					handleDeleteCourse(courseStudent, assignStudentTable);
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

		assignStudentTable.getColumns().addAll(courseIdColumn, studentIdColumn, actionColumn);
	}

	private void loadList(TableView<CourseStudents> assignStudentTable) {
		try {
			List<CourseStudents> list = CourseAssignService.displayAllStudentAssignments();

			if (list == null || list.isEmpty()) {
				assignStudentTable.getItems().clear();
			} else {
				ObservableList<CourseStudents> assignList = FXCollections.observableArrayList(list);
				assignStudentTable.setItems(assignList);
			}
			assignStudentTable.refresh();

		} catch (DataNotFoundException e) {
			assignStudentTable.getItems().clear();
			assignStudentTable.refresh();
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void handleDeleteCourse(CourseStudents courseStudent, TableView<CourseStudents> assignStudentTable) {
		if (courseStudent == null) {
			AlertDialog.showWarning("No Selection", "Please select a course to delete.");
			return;
		}

		boolean isConfirmed = AlertDialog.showConfirm("Confirm Delete", "Are you sure you want to delete the Course: "
				+ courseStudent.getCourseId() + " - Student: " + courseStudent.getStudentId() + " assignment?");

		if (isConfirmed) {
			try {
				// Delete data
				boolean isSuccess = CourseAssignService.deleteCourseStudent(courseStudent.getCourseId(),
						courseStudent.getStudentId());
				if (isSuccess)
					AlertDialog.showSuccess("Success", "Successfully deleted course: " + courseStudent.getCourseId());

				// refresh table
				loadList(assignStudentTable);
			} catch (CRUDFailedException | IOException e) {
				AlertDialog.showWarning("Error", e.getMessage());
			}
		} else {
			AlertDialog.showWarning("Delete", "Deletion canceled");

		}

	}

}
