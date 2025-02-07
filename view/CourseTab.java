package view;

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
import javafx.util.converter.IntegerStringConverter;
import model.Course;
import service.CourseService;

public class CourseTab {

	public Tab getTab() {
		Tab courseTab = new Tab("Courses");
		courseTab.setClosable(false);
		courseTab.setContent(createCourseTabContent());
		return courseTab;
	}

	private VBox createCourseTabContent() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		// Search Section
		GridPane searchPane = new GridPane();
		searchPane.setHgap(10);
		searchPane.setVgap(10);
		searchPane.setPadding(new Insets(10));

		Label searchLabel = new Label("Search Course by ID:");
		TextField searchField = new TextField();
		Button searchButton = new Button("Search");
		Button addNewCourseButton = new Button("Add New Course");

		searchPane.add(searchLabel, 0, 0);
		searchPane.add(searchField, 1, 0);
		searchPane.add(searchButton, 2, 0);
		searchPane.add(addNewCourseButton, 3, 0);

		// TableView Section
		TableView<Course> courseTable = new TableView<>();
		setupTableColumns(courseTable);

		// Load Data into TableView
		loadCourses(courseTable);

		searchButton.setOnAction(e -> clickSearchButton(searchField, courseTable));
		addNewCourseButton.setOnAction(e -> new AddCourseForm(courseTable));

		vbox.getChildren().addAll(searchPane, courseTable);
		return vbox;
	}

	@SuppressWarnings("unchecked")
	private void setupTableColumns(TableView<Course> courseTable) {
		// Course ID
		TableColumn<Course, Integer> idColumn = new TableColumn<>("Course ID");
		idColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCourseId()).asObject());

		// Course Name - Editable
		TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nameColumn.setOnEditCommit(event -> handleEditCourse(event, courseTable));
		nameColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseName()));

		// Credits - Editable
		TableColumn<Course, String> creditsColumn = new TableColumn<>("Credits");
		creditsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		creditsColumn.setOnEditCommit(event -> handleEditCourse(event, courseTable));
		// Get numeric value as a String and then handle converting later
		// Otherwise can't catch NumberFormatException
		creditsColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getCredits())));

		// Delete button
		TableColumn<Course, Void> actionColumn = new TableColumn<>("Actions");
		actionColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(event -> {
					Course course = getTableView().getItems().get(getIndex());
					handleDeleteCourse(course, courseTable);
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

		courseTable.getColumns().addAll(idColumn, nameColumn, creditsColumn, actionColumn);
		courseTable.setEditable(true);
	}

	private void loadCourses(TableView<Course> courseTable) {
		List<Course> courses;
		try {
			courses = CourseService.listCourses();
			ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
			courseTable.setItems(courseList);
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}

	}

	private void clickSearchButton(TextField searchField, TableView<Course> courseTable) {
		String input = searchField.getText();
		if (input.isEmpty()) {
			loadCourses(courseTable); // Reload all courses
			return;
		}
		try {
			int courseId = Integer.parseInt(input);
			Course course = CourseService.searchCourseById(courseId);
			if (course != null) {
				ObservableList<Course> result = FXCollections.observableArrayList(course);
				courseTable.setItems(result);
			} else {
				AlertDialog.showWarning("Course Not Found", "No course found with ID: " + courseId);
			}
		} catch (NumberFormatException ex) {
			AlertDialog.showWarning("Error", "Please enter a valid numeric Course ID.");
		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Result not found", e.getMessage());
		}
	}

	private void handleEditCourse(TableColumn.CellEditEvent<Course, ?> event, TableView<Course> courseTable) {
		boolean isConfirmed = AlertDialog.showConfirm("Confirm Edit", "Are you sure you want to edit the course?");

		if (!isConfirmed) {
			AlertDialog.showWarning("Edit", "Edit canceled");
			return;

		}

		try {
			Course course = event.getRowValue();

			if (event.getTableColumn().getText().equals("Course Name")) {
				String newName = ((String) event.getNewValue()).trim();
				if (newName.isEmpty()) {
					AlertDialog.showWarning("Invalid Input", "Course Name cannot be empty.");
					courseTable.refresh();
					return;
				}
				course.setCourseName(newName);
			} else if (event.getTableColumn().getText().equals("Credits")) {
				try {
					System.out.println("Yu ch gesen orood irlee");
					String newCreditStr = event.getNewValue().toString().trim();
					int newCredits = Integer.parseInt(newCreditStr);
					if (newCredits <= 0) {
						AlertDialog.showWarning("Invalid Input", "Credits must be a positive number.");
						courseTable.refresh();
						return;
					}
					course.setCredits(newCredits);
				} catch (NumberFormatException ex) {
					AlertDialog.showWarning("Error", "Please enter a valid numeric Credit.");
					return;
				}
			}

			boolean isSuccess = CourseService.updateCourse(course);
			if (isSuccess)
				AlertDialog.showSuccess("Success", "Successfully edited course: " + course.getCourseId());

		} catch (DataNotFoundException e) {
			AlertDialog.showWarning("Not found", e.getMessage());
		} catch (Exception e) {
			AlertDialog.showWarning("Error", e.getMessage());
		}

	}

	private void handleDeleteCourse(Course course, TableView<Course> courseTable) {
		if (course == null) {
			AlertDialog.showWarning("No Selection", "Please select a course to delete.");
			return;
		}

		boolean isConfirmed = AlertDialog.showConfirm("Confirm Delete", "Are you sure you want to delete the course: "
				+ course.getCourseId() + " - " + course.getCourseName() + "?");

		if (isConfirmed) {

			boolean hasRel;
			try {
				hasRel = CourseService.checkCourseHasRelation(course.getCourseId());
				if (hasRel) {
					isConfirmed = AlertDialog.showConfirm("Confirm",
							"This course has assigned students/instructors. Do you want to delete them as well?");

					if (!isConfirmed) {
						AlertDialog.showWarning("Delete", "Deletion canceled");
						return;

					}
				}

				// Delete data
				boolean isSuccess = CourseService.deleteCourse(course.getCourseId());
				if (isSuccess)
					AlertDialog.showSuccess("Success", "Successfully deleted course: " + course.getCourseId());

				// refresh table
				loadCourses(courseTable);
			} catch (DataNotFoundException e) {
				AlertDialog.showWarning("Result not found", e.getMessage());
			} catch (CRUDFailedException e) {
				AlertDialog.showWarning("Error", e.getMessage());
			}
		} else {
			AlertDialog.showWarning("Delete", "Deletion canceled");

		}

	}

}
