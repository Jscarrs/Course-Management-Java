package group1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Course
 */
public class Course {
	final static String filePath = "./data/courses.csv";

	private String courseId;
	private String courseName;
	private int credits;
	private Instructor instructor;
	private List<Student> students;

	public Course(String courseId, String courseName, int credits) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.credits = credits;
		this.students = new ArrayList<>();
	}

	public String getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getCredits() {
		return credits;
	}

	public void assignInstructor(Instructor instructor) {
		this.instructor = instructor;
	}

	public void addStudent(Student student) {
		students.add(student);
	}

	public void displayCourseDetails() {
		System.out.println("Course ID: " + courseId);
		System.out.println("Course Name: " + courseName);
		System.out.println("Credits: " + credits);
		if (instructor != null) {
			System.out.println("\nInstructor Details:");
			instructor.displayDetails();
		} else {
			System.out.println("\nNo instructor assigned yet.");
		}

		System.out.println("\nEnrolled Students:");
		if (students.isEmpty()) {
			System.out.println("No students enrolled yet.");
		} else {
			for (Student student : students) {
				student.displayDetails();
				System.out.println();
			}
		}
	}

	public static ArrayList<Course> readCourses() throws Exception {
		ArrayList<Course> courses = new ArrayList<Course>();

		try (BufferedReader courseBr = new BufferedReader(new FileReader(filePath))) {
			courseBr.readLine(); // Skip header

			String line;
			while ((line = courseBr.readLine()) != null) {
				courses.add(parseCourse(line));
			}

			courseBr.close();

		} catch (InvalidCSVFormatException e) {
			// Custom exception - Incorrect csv format || Missing or invalid data
			throw e;
		} catch (FileNotFoundException e) {
			// File not found or can't open file (permission etc.)
			throw new FileNotFoundException("FileNotFoundException: File does not exists! (courses.csv)");
		} catch (IOException e) {
			// Any other IO exceptions
			throw new FileNotFoundException("IOException: Unable to read a file! (courses.csv)");
		}
		return courses;
	}

	private static Course parseCourse(String line) throws InvalidCSVFormatException {
		String[] data = line.split(",");
		if (data.length != 3) {
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Course data must have 3 fields!");
		}
		if (data[0] == null || data[0].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Course id is empty!");
		else if (data[1] == null || data[1].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Course name is empty!");
		else if (data[2] == null || data[2].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Course credit is empty!");
		try {
			int credits = Integer.parseInt(data[2]);
			return new Course(data[0], data[1], credits);
		} catch (NumberFormatException e) {
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Course credit is not a valid integer!");
		}
	}
}