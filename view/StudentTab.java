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
public class StudentTab {

	public Tab getTab() {
		Tab studentTab = new Tab("Students");
		studentTab.setClosable(false);
		studentTab.setContent(createStudentTabContent());
		return studentTab;
	}

	private GridPane createStudentTabContent() {
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label studentLabel = new Label("Hello Student!");
		gridPane.add(studentLabel, 0, 0);

		return gridPane;
	}

}
