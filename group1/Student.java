package group1;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Subclass Student
 */
class Student extends Person implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	final static String csvFilePath = "./data/students.csv"; // For initial import
	final static String binaryFilePath = "./data/students.dat"; // For storage

	private String studentId;

	// Default constructor needed for deserialization
	public Student() {
		super("", ""); // Call parent class constructor with empty name and email
		this.studentId = "";
	}

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

	/** 🔹 Read students from binary file (students.dat) */
	@SuppressWarnings("unchecked")
	public static ArrayList<Student> readStudents() {
		ArrayList<Student> students = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binaryFilePath))) {
			Object obj = ois.readObject();
			if (obj instanceof ArrayList<?>) {
				students = (ArrayList<Student>) obj; // Safe cast after checking
			} else {
				System.out.println("Unexpected data type in binary file.");
			}
		} catch (FileNotFoundException e) {
			System.out.println("No existing student records found.");
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
		return students;
	}

	/** 🔹 Save students to binary file (students.dat) */
	public static void saveStudents(ArrayList<Student> students) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binaryFilePath))) {
			oos.writeObject(students);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 🔹 Convert CSV to binary (optional) */
	public static void convertCsvToBinary() {
		ArrayList<Student> students = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
			br.readLine(); // Skip header
			String line;
			while ((line = br.readLine()) != null) {
				students.add(parseStudent(line));
			}
			saveStudents(students);
			System.out.println("Converted students.csv to students.dat successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 🔹 Parse Student from CSV */
	private static Student parseStudent(String line) {
		String[] data = line.split(",");
		if (data.length != 3) {
			throw new RuntimeException("Invalid CSV format: Each row must have 3 fields.");
		}
		return new Student(data[0], data[1], data[2]);
	}


}
