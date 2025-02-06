package service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import model.Course;

public class CourseService {
	private static final String FILE_PATH = "./data/courses.dat";

	public static void addCourse() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter Course ID: ");
			int courseId = Integer.parseInt(reader.readLine());

			if (isCourseExists(courseId)) {
				System.out.println("Course ID already exists. Please enter a unique ID.");
				return;
			}

			System.out.print("Enter Course Name: ");
			String courseName = reader.readLine();
			System.out.print("Enter Credits: ");
			int credits = Integer.parseInt(reader.readLine());

			Course newCourse = new Course(courseId, courseName, credits);
			appendToBinaryFile(newCourse);

			System.out.println("Course added successfully!");
		} catch (IOException | NumberFormatException e) {
			System.out.println("Error reading input! Ensure correct format.");
		}
	}

	public static void updateCourse() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Course ID to update: ");
		int id = scanner.nextInt();
		scanner.nextLine();

		ArrayList<Course> courses = readBinaryFile();
		boolean found = false;

		for (Course course : courses) {
			if (course.getCourseId() == id) {
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
			writeBinaryFile(courses);
			System.out.println("Course updated successfully!");
		} else {
			System.out.println("Course with ID " + id + " not found.");
		}
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
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
			list = (ArrayList<Course>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No existing course records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Course> list) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(list);
		} catch (IOException e) {
			System.out.println("Error saving course data!");
		}
	}

	private static void appendToBinaryFile(Course course) {
		ArrayList<Course> courses = readBinaryFile();
		courses.add(course);
		writeBinaryFile(courses);
	}

	private static boolean isCourseExists(int courseId) {
		ArrayList<Course> courses = readBinaryFile();
		for (Course course : courses) {
			if (course.getCourseId() == courseId) {
				return true;
			}
		}
		return false;
	}

	public static void searchCourseById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Course ID: ");
		int courseId = scanner.nextInt();

		ArrayList<Course> courses = readBinaryFile();

		for (Course course : courses) {
			if (course.getCourseId() == courseId) {
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
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Course ID: ");
		int courseId = scanner.nextInt();
		scanner.nextLine();

		ArrayList<Course> courses = readBinaryFile();
		boolean found = false;

		for (Course course : courses) {
			if (course.getCourseId() == courseId) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Course with ID " + courseId + " not found.");
			return;
		}

		boolean hasStudents = CourseAssignService.checkStudentAssignedToCourse(courseId);
		boolean hasInstructor = CourseAssignService.checkIntructorAssignedToCourse(courseId);

		if (hasStudents || hasInstructor) {
			System.out.print(
					"This course has assigned students/instructors. Do you want to delete them as well? (yes/no): ");
			String input = scanner.nextLine().trim().toLowerCase();

			if (!input.equals("yes")) {
				System.out.println("Deletion canceled.");
				return;
			}

			CourseAssignService.deleteAllStudentFromCourse(courseId);
			CourseAssignService.deleteAllInstructorFromCourse(courseId);
		}

		courses.removeIf(course -> course.getCourseId() == courseId);
		writeBinaryFile(courses);
		System.out.println("Course deleted successfully.");
	}
}
