package group1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Subclass Student
 */
class Student extends Person {
	final static String filePath = "./data/students.csv";

	private String studentId;

	public Student(String studentId, String name, String email) {
		super(name, email);
		this.studentId = studentId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	@Override
	public void displayDetails() {
		System.out.println("Student ID: " + studentId);
		super.displayDetails();
	}

	public static ArrayList<Student> readStudents() throws Exception {
		ArrayList<Student> students = new ArrayList<Student>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			br.readLine(); // Skip header

			String line;
			while ((line = br.readLine()) != null) {
				students.add(parseStudent(line));
			}
			br.close();
		} catch (InvalidCSVFormatException e) {
			// Custom exception - Incorrect csv format || Missing or invalid data
			throw e;
		} catch (FileNotFoundException e) {
			// File not found or can't open file (permission etc.)
			throw new FileNotFoundException("FileNotFoundException: File does not exists! (students.csv)");
		} catch (IOException e) {
			// Any other IO exceptions
			throw new FileNotFoundException("IOException: Unable to read a file! (students.csv)");
		}
		return students;
	}

	private static Student parseStudent(String line) throws InvalidCSVFormatException {
		String[] data = line.split(",");

		// Incorrect CSV Format
		if (data.length != 3) {
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Student data must have 3 fields!");
		}

		// Checking Missing or Invalid Data:
		if (data[0] == null || data[0].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Student id is empty!");
		else if (data[1] == null || data[1].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Student name is empty!");
		else if (data[2] == null || data[2].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Student email is empty!");

		return new Student(data[0], data[1], data[2]);
	}
}