/**
 *
 * @author Ariunjargal Erdenebaatar
 * @created Feb 6, 2025
 */
package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

/**
 * 
 */
public class InstructorTab {

	public Tab getTab() {
		Tab instructorTab = new Tab("Instructors");
		instructorTab.setClosable(false);
		instructorTab.setContent(createStudentTabContent());
		return instructorTab;
	}

	private GridPane createStudentTabContent() {
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label instructorLabel = new Label("Hello Instructor!");
		gridPane.add(instructorLabel, 0, 0);

		return gridPane;
	}

}
