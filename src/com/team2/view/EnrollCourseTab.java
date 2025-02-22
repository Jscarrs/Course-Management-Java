
package com.team2.view;

import java.util.List;

import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.StudentCourse;
import com.team2.service.StudentCourseService;

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

public class EnrollCourseTab {

	public Tab getTab() {
		Tab assignStudentTab = new Tab("Course Enrollment");
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
		TableView<StudentCourse> enrollmentTable = new TableView<>();
		setupTableColumns(enrollmentTable);

		// Load Data into TableView
		loadEnrollments(enrollmentTable);

		refreshButton.setOnAction(e -> loadEnrollments(enrollmentTable));
		addButton.setOnAction(e -> new EnrollCourseForm(enrollmentTable));

		vbox.getChildren().addAll(searchPane, enrollmentTable);
		return vbox;
	}

	@SuppressWarnings("unchecked")
	private void setupTableColumns(TableView<StudentCourse> enrollmentTable) {
		// Student ID Column
		TableColumn<StudentCourse, Integer> studentIdColumn = new TableColumn<>("Student ID");
		studentIdColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStudent().getStudentId())
						.asObject());

		// Student Name Column
		TableColumn<StudentCourse, String> studentNameColumn = new TableColumn<>("Student Name");
		studentNameColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudent().getName()));

		// Course ID Column
		TableColumn<StudentCourse, Integer> courseIdColumn = new TableColumn<>("Course ID");
		courseIdColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCourse().getCourseId())
						.asObject());

		// Course Name Column
		TableColumn<StudentCourse, String> courseNameColumn = new TableColumn<>("Course Name");
		courseNameColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse().getCourseName()));

		// Delete button
		TableColumn<StudentCourse, Void> actionColumn = new TableColumn<>("Actions");
		actionColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Drop course");

			{
				deleteButton.setOnAction(event -> {
					StudentCourse enrollment = getTableView().getItems().get(getIndex());
					handleDropStudent(enrollment, enrollmentTable);
				});
			}

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

		enrollmentTable.getColumns().addAll(studentIdColumn, studentNameColumn, courseIdColumn, courseNameColumn,
				actionColumn);
	}

	private void loadEnrollments(TableView<StudentCourse> enrollmentTable) {
		try {
			List<StudentCourse> enrollments = StudentCourseService.listAllEnrollments();

			if (enrollments == null || enrollments.isEmpty()) {
				enrollmentTable.getItems().clear();
			} else {
				ObservableList<StudentCourse> enrollmentList = FXCollections.observableArrayList(enrollments);
				enrollmentTable.setItems(enrollmentList);
			}
			enrollmentTable.refresh();

		} catch (DataNotFoundException e) {
			enrollmentTable.getItems().clear();
			enrollmentTable.refresh();
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}
	}

	private void handleDropStudent(StudentCourse enrollment, TableView<StudentCourse> enrollmentTable) {
		if (enrollment == null) {
			AlertDialog.showWarning("No Selection", "Please select an enrollment to drop.");
			return;
		}

		boolean isConfirmed = AlertDialog.showConfirm("Confirm Drop",
				"Are you sure you want to remove Student: " + enrollment.getStudent().getName()
						+ " from Course: " + enrollment.getCourse().getCourseName() + "?");

		if (isConfirmed) {
			try {
				boolean isSuccess = StudentCourseService.dropStudentFromCourse(enrollment.getCourse().getCourseId(),
						enrollment.getStudent().getStudentId());

				if (isSuccess)
					AlertDialog.showSuccess("Success", "Successfully dropped student from course.");

				loadEnrollments(enrollmentTable);
			} catch (CRUDFailedException e) {
				AlertDialog.showWarning("Error", e.getMessage());
			}
		} else {
			AlertDialog.showWarning("Drop Canceled", "Dropping course canceled.");
		}
	}
}
