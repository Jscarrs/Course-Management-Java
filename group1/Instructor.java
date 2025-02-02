package group1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Subclass Instructor
 */
public class Instructor extends Person implements Serializable{
	private static final long serialVersionUID = 1L;  
	final static String filePath = "./data/instructors.csv";

	private String instructorId;
	private String department;

	public Instructor(String instructorId, String name, String email, String department) {
		super(name, email);
		this.instructorId = instructorId;
		this.department = department;
	}

	public String getInstructorId() {
		return instructorId;
	}

	public void setInstructorId(String instructorId) {
		this.instructorId = instructorId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public void displayDetails() {
		System.out.println("Instructor ID: " + instructorId);
		System.out.println("Department: " + department);
		super.displayDetails();
	}

	public static ArrayList<Instructor> readInstructors() throws Exception {
		ArrayList<Instructor> instructors = new ArrayList<Instructor>();

		try (BufferedReader instructorBr = new BufferedReader(new FileReader(filePath))) {
			instructorBr.readLine(); // Skip header

			String line;
			while ((line = instructorBr.readLine()) != null) {
				instructors.add(parseInstructor(line));
			}

			instructorBr.close();
		} catch (InvalidCSVFormatException e) {
			// Custom exception - Incorrect csv format || Missing or invalid data
			throw e;
		} catch (FileNotFoundException e) {
			// File not found or can't open file (permission etc.)
			throw new FileNotFoundException("FileNotFoundException: File does not exists! (instructors.csv)");
		} catch (IOException e) {
			// Any other IO exceptions
			throw new FileNotFoundException("IOException: Unable to read a file! (instructors.csv)");
		}
		return instructors;
	}

	private static Instructor parseInstructor(String line) throws InvalidCSVFormatException {
		String[] data = line.split(",");

		// Incorrect CSV Format
		if (data.length != 4) {
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor data must have 4 fields!");
		}

		// Checking Missing or Invalid Data:
		if (data[0] == null || data[0].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor id is empty!");
		else if (data[1] == null || data[1].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor name is empty!");
		else if (data[2] == null || data[2].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor email is empty!");
		else if (data[2] == null || data[3].trim().isEmpty())
			throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor department is empty!");

		return new Instructor(data[0], data[1], data[2], data[3]);
	}

	
}
