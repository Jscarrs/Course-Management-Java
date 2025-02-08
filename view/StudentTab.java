/**
 *
 * @author Ariunjargal Erdenebaatar
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
import model.Student; // 假設有 Student 模型
import service.StudentService; // 與 CourseTab 相同結構
import exceptions.DataNotFoundException;
import exceptions.CRUDFailedException;
/**
 * 
 */
public class StudentTab {

	public Tab getTab() {
		Tab studentTab = new Tab("Students");
		studentTab.setClosable(false);
		studentTab.setContent(createStudentTabContent());
		return studentTab;
	}

	private VBox createStudentTabContent() {
		VBox vbox = new VBox(10);


		GridPane searchPane = new GridPane();
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

		// 可再新增更多欄位 (Email、等)

		// 動作欄位 (刪除)
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
					StudentService.listStudents() // 來自 [`StudentService.listStudents()`](service/StudentService.java)
			);
			studentTable.setItems(studentList);
		} catch (Exception e) {
			// 錯誤處理略
		}
	}

	private void clickSearchButton(TextField searchField, TableView<Student> studentTable) {
		String input = searchField.getText();
		if (input.isEmpty()) {
			loadStudents(studentTable); // Reload all students
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
		// 與 AddCourseForm 類似，可彈出對話框或直接輸入
		// 新增後重新載入表格:
		// loadStudents(studentTable);
	}

	private void handleEditStudent(TableColumn.CellEditEvent<Student, ?> event, TableView<Student> studentTable) {
		Student s = event.getRowValue();
		if ("Name".equals(event.getTableColumn().getText())) {
			s.setName(event.getNewValue().toString());
			// 更新後呼叫 [`StudentService.updateStudent()`](service/StudentService.java)
		}
		if ("Email".equals(event.getTableColumn().getText())) {
			s.setEmail(event.getNewValue().toString());
			try {
				try {
					StudentService.updateStudent(s); // Similar to [`CourseService.updateCourse`](service/CourseService.java)
				} catch (DataNotFoundException e) {
					// Handle the exception, e.g., show an alert dialog
					AlertDialog.showWarning("Update Failed", e.getMessage());
				}
			} catch (CRUDFailedException e) {
				// 錯誤處理略
			}
		}
		// 更新表格
	}

	private void handleDeleteStudent(Student s, TableView<Student> studentTable) {
		if (s == null) return;
		try {
			StudentService.deleteStudent(s.getStudentId()); // [`StudentService.deleteStudent()`](service/StudentService.java)
			loadStudents(studentTable);
		} catch (Exception e) {
			// 錯誤處理略
		}
	}

}
