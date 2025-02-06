package utils;

import java.util.ArrayList;
import java.util.Scanner;

import model.Course;
import model.Instructor;
import model.Student;
import service.CourseAssignService;
import service.CourseService;
import service.InstructorService;
import service.StudentService;

/**
 * Main class
 */
public class CourseManagementSystem {

	public static ArrayList<Course> courses;
	public static ArrayList<Instructor> instructors;
	public static ArrayList<Student> students;
	static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) {
		showMenu(true);
	}

	private static void showMenu(boolean isConvertFile) {
		if (isConvertFile) {
			// Test CSV to Binary Conversion
			System.out.println("Would you like to convert CSV files to binary format? (yes/no)");
			String response = scan.nextLine();

			if ("yes".equalsIgnoreCase(response)) {
				System.out.println("Starting conversion...");
				CSVToBinaryConverter.convertAllCsvToBinary();
			} else {
				System.out.println("CSV to Binary conversion skipped.");
			}
		}

		// menu for inserting and listing data
		int choice;
		do {
			System.out.println("\nCourse Management System");
			System.out.println("1. Add Student");
			System.out.println("2. Add Course");
			System.out.println("3. Add Instructor");
			System.out.println("4. Update Student");
			System.out.println("5. Update Course");
			System.out.println("6. Update Instructor");
			System.out.println("7. Assign Instructor to Course");
			System.out.println("8. Assign Student to Course");
			System.out.println("9. List Students");
			System.out.println("10. List Courses");
			System.out.println("11. List Instructors");
			System.out.println("12. List Course - Instructors");
			System.out.println("13. List Course - Students");
			System.out.println("14. Find student by ID");
			System.out.println("15. Find instructor by ID");
			System.out.println("16. Find course by ID");
			System.out.println("17. Delete records");
			System.out.println("0. Exit");
			System.out.print("Enter your choice: ");
			choice = scan.nextInt();
			scan.nextLine();

			switch (choice) {
			case 1:
				StudentService.addStudent();
				break;
			case 2:
				CourseService.addCourse();
				break;
			case 3:
				InstructorService.addInstructor();
				break;
			case 4:
				StudentService.updateStudent();
				break;
			case 5:
				CourseService.updateCourse();
				break;
			case 6:
				InstructorService.updateInstructor();
				break;
			case 7:
				CourseAssignService.assignInstructorToCourse();
				break;
			case 8:
				CourseAssignService.assignStudentToCourse();
				break;
			case 9:
				StudentService.listStudents();
				break;
			case 10:
				CourseService.listCourses();
				break;
			case 11:
				InstructorService.listInstructors();
				break;
			case 12:
				CourseAssignService.displayAllInstructorAssignments();
				break;
			case 13:
				CourseAssignService.displayAllStudentAssignments();
				break;
			case 14:
				StudentService.searchStudentById();
				break;
			case 15:
				InstructorService.searchInstructorById();
				break;
			case 16:
				CourseService.searchCourseById();
				break;
			case 17:
				showDeleteMenu();
				break;
			case 0:
				System.out.println("Exiting system...");
				break;
			default:
				System.out.println("Invalid choice! Try again.");
			}
		} while (choice != 0);

		scan.close();
	}

	private static void showDeleteMenu() {
		int choice;
		do {
			System.out.println("\nDelete records");
			System.out.println("1. Delete Student");
			System.out.println("2. Delete Course");
			System.out.println("3. Delete Instructor");
			System.out.println("4. Delete Course - Instructor");
			System.out.println("5. Delete Course - Student");

			System.out.println("0. Back to main menu");
			System.out.print("Enter your choice: ");
			choice = scan.nextInt();
			scan.nextLine();

			switch (choice) {
			case 1:
				StudentService.deleteStudent();
				break;
			case 2:
				CourseService.deleteCourse();
				break;
			case 3:
				InstructorService.deleteInstructor();
				break;
			case 4:
				CourseAssignService.deleteCourseInstructor();
				break;
			case 5:
				CourseAssignService.deleteCourseStudent();
				break;
			case 0:
				System.out.println("Exiting system...");
				break;
			default:
				System.out.println("Invalid choice! Try again.");
			}
		} while (choice != 0);

		showMenu(false);
	}
}
