package group1;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class
 */
public class CourseManagementSystem {

	static ArrayList<Course> courses;
	static ArrayList<Instructor> instructors;
	static ArrayList<Student> students;

	public static void main(String[] args) {
		showMenu();
	}

	private static void showMenu() {
		Scanner scan = new Scanner(System.in);

		// Test CSV to Binary Conversion
		System.out.println("Would you like to convert CSV files to binary format? (yes/no)");
		String response = scan.nextLine();

		if ("yes".equalsIgnoreCase(response)) {
			System.out.println("Starting conversion...");
			CSVToBinaryConverter.convertCsvToBinary("./data/courses.csv", "data/courses.dat", Course.class);
			CSVToBinaryConverter.convertCsvToBinary("./data/instructors.csv", "data/instructors.dat", Instructor.class);
			CSVToBinaryConverter.convertCsvToBinary("./data/students.csv", "data/students.dat", Student.class);
		} else {
			System.out.println("CSV to Binary conversion skipped.");
		}

		// menu for inserting and listing data
		int choice;
		do {
			System.out.println("\nCourse Management System");
			System.out.println("1. Add Student");
			System.out.println("2. Add Course");
			System.out.println("3. Add Instructor");
			System.out.println("4. List Students");
			System.out.println("5. List Courses");
			System.out.println("6. List Instructors");
			System.out.println("7. Update Student");
			System.out.println("8. Update Course");
			System.out.println("9. Update Instructor");
			System.out.println("10. Assign Instructor to Course");
			System.out.println("11. Assign Student to Course");
			System.out.println("12. List Course - Instructors");
			System.out.println("13. List Course - Students");

			System.out.println("14. Exit");
			System.out.print("Enter your choice: ");
			choice = scan.nextInt();
			scan.nextLine();

			switch (choice) {
			case 1:
				StudentManager.addStudent();
				break;
			case 2:
				CourseManager.addCourse();
				break;
			case 3:
				InstructorManager.addInstructor();
				break;
			case 4:
				StudentManager.listStudents();
				break;
			case 5:
				CourseManager.listCourses();
				break;
			case 6:
				InstructorManager.listInstructors();
				break;
			case 7:
				StudentManager.updateStudent();
				break;
			case 8:
				CourseManager.updateCourse();
				break;
			case 9:
				InstructorManager.updateInstructor();
				break;
			case 10:
				CourseAssignManager.assignInstructorToCourse();
				break;
			case 11:
				CourseAssignManager.assignStudentToCourse();
				break;
			case 12:
				CourseAssignManager.displayAllInstructorAssignments();
				break;
			case 13:
				CourseAssignManager.displayAllStudentAssignments();
				break;
			case 14:
				System.out.println("Exiting system...");
				break;
			default:
				System.out.println("Invalid choice! Try again.");
			}
		} while (choice != 14);

		scan.close();

	}

}
