package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

/**
 * 
 */
public class EnrollCourseTab {

	public Tab getTab() {
		Tab studentTab = new Tab("Enroll Course");
		studentTab.setClosable(false);
		studentTab.setContent(createTabContent());
		return studentTab;
	}

	private GridPane createTabContent() {
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label studentLabel = new Label("Hello Student & Course!");
		gridPane.add(studentLabel, 0, 0);

		return gridPane;
	}

}
