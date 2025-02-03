package group1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//created course manager class to manage insertion and listing of course data and also manage save changes to file
public class CourseManager {
	private static final String COURSE_FILE = "./data/courses.dat";
	private static final String COURSE_CSV = "./data/courses.csv";

	public static void addCourse() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter Course ID: ");
			String courseId = reader.readLine();
			System.out.print("Enter Course Name: ");
			String courseName = reader.readLine();
			System.out.print("Enter Credits: ");
			int credits = Integer.parseInt(reader.readLine());

			Course newCourse = new Course(courseId, courseName, credits);
			ArrayList<Course> courses = readBinaryFile();
			courses.add(newCourse);
			writeBinaryFile(courses);

			// Append course to CSV file
			appendToCSV(courseId, courseName, credits);

			System.out.println("Course added successfully!");
		} catch (IOException e) {
			System.out.println("Error reading input!");
		}
	}

	public static void updateCourse() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Course ID to update: ");
		String id = scanner.nextLine();

		// Load existing courses from the binary file
		ArrayList<Course> courses = readBinaryFile();
		boolean found = false;

		for (Course course : courses) {
			if (course.getCourseId().equals(id)) {
				System.out.print("Enter new Course Name: ");
				String name = scanner.nextLine();
				System.out.print("Enter new Credits: ");
				int credits = Integer.parseInt(scanner.nextLine());

				course.setCourseName(name);
				course.setCredits(credits);
				found = true;
				break;
			}
		}

		if (found) {
			// Save updated course list back to binary file
			writeBinaryFile(courses);
			System.out.println("Course updated successfully!");
		} else {
			System.out.println("Course with ID " + id + " not found.");
		}
//        scanner.close();
	}

	public static void listCourses() {
		ArrayList<Course> courses = readBinaryFile();
		if (courses.isEmpty()) {
			System.out.println("No courses found.");
			return;
		}

		System.out.println("\nList of Courses:");
		for (Course course : courses) {
			System.out.println("Course ID: " + course.getCourseId());
			System.out.println("Course Name: " + course.getCourseName());
			System.out.println("Credits: " + course.getCredits());
			System.out.println("----------------------------");
		}
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Course> readBinaryFile() {
		ArrayList<Course> list = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COURSE_FILE))) {
			list = (ArrayList<Course>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No existing course records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Course> list) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COURSE_FILE))) {
			oos.writeObject(list);
		} catch (IOException e) {
			System.out.println("Error saving course data!");
		}
	}

	private static void appendToCSV(String courseId, String courseName, int credits) {
		try (FileWriter fw = new FileWriter(COURSE_CSV, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {

			out.println(courseId + "," + courseName + "," + credits); // Write new record

		} catch (IOException e) {
			System.out.println("Error appending course to CSV!");
		}
	}

	public static void searchCourseById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Course ID: ");
		String courseId = scanner.nextLine();

		ArrayList<Course> courses = readBinaryFile();

		for (Course course : courses) {
			if (course.getCourseId().equals(String.valueOf(courseId))) {
				System.out.println("\nCourse Found:");
				System.out.println("Course ID: " + course.getCourseId());
				System.out.println("Course Name: " + course.getCourseName());
				System.out.println("Credits: " + course.getCredits());
				return;
			}
		}
		System.out.println("Course with ID " + courseId + " not found.");
	}

	public static void deleteCourse() {
		Scanner scannerd = new Scanner(System.in);
		System.out.print("Enter Course ID: ");
		int courseId = scannerd.nextInt();
	    scannerd.nextLine();

		ArrayList<Course> courses = readBinaryFile();
		boolean found = false;

		// Check if the course exists
		for (Course course : courses) {
			if (course.getCourseId().equals(String.valueOf(courseId))) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Course with ID " + courseId + " not found.");
			return;
		}

		// Check if the course has student or instructor assignments
		boolean hasStudents = CourseAssignManager.checkStudentAssignedToCourse(courseId);
		boolean hasInstructor = CourseAssignManager.checkIntructorAssignedToCourse(courseId);

		if (hasStudents || hasInstructor) {
			System.out.print(
					"This course has assigned students/instructors. Do you want to delete them as well? (yes/no): ");
			String input = scannerd.nextLine().trim().toLowerCase();

			if (!input.equals("yes")) {
				System.out.println("Deletion canceled.");
				return;
			}

			// Delete related assignments
			CourseAssignManager.deleteAllStudentFromCourse(courseId);
			CourseAssignManager.deleteAllInstructorFromCourse(courseId);
		}

		// Remove the course from the list
		courses.removeIf(course -> course.getCourseId().equals(String.valueOf(courseId)));

		// Save updated list to binary file
		writeBinaryFile(courses);
		System.out.println("Course deleted successfully.");
	}

}
