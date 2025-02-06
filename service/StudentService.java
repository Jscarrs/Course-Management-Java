package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import model.Student;

public class StudentService {
	private static final String FILE_PATH = "./data/students.dat";
	private static final String CSV_PATH = "./data/students.csv";

	public static void addStudent() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Student ID: ");
		int id = scanner.nextInt();
		scanner.nextLine();

		if (isStudentExists(id)) {
			System.out.println("Student ID already exists. Please enter a unique ID.");
			return;
		}

		System.out.print("Enter Student Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Student Email: ");
		String email = scanner.nextLine();

		Student newStudent = new Student(id, name, email);
		ArrayList<Student> students = readStudents();
		students.add(newStudent);
		saveStudents(students);

		appendToCSV(id, name, email);
		System.out.println("Student added successfully!");
	}

	public static void updateStudent() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Student ID to update: ");
		int id = scanner.nextInt();
		scanner.nextLine();

		ArrayList<Student> students = readStudents();
		boolean found = false;

		for (Student student : students) {
			if (student.getStudentId() == id) {
				System.out.print("Enter new Student Name: ");
				String name = scanner.nextLine();
				System.out.print("Enter new Student Email: ");
				String email = scanner.nextLine();

				student.setName(name);
				student.setEmail(email);
				found = true;
				break;
			}
		}

		if (found) {
			saveStudents(students);
			System.out.println("Student updated successfully!");
		} else {
			System.out.println("Student with ID " + id + " not found.");
		}
	}

	public static void listStudents() {
		ArrayList<Student> students = readStudents();
		if (students.isEmpty()) {
			System.out.println("No students found.");
		} else {
			for (Student student : students) {
				System.out.println("Student ID: " + student.getStudentId());
				System.out.println("Name: " + student.getName());
				System.out.println("Email: " + student.getEmail());
				System.out.println("-------------------");
			}
		}
	}

	public static void deleteStudent() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Student ID: ");
		int studentId = scanner.nextInt();
		scanner.nextLine();

		ArrayList<Student> students = readStudents();
		boolean found = false;

		for (Student student : students) {
			if (student.getStudentId() == studentId) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Student with ID " + studentId + " not found.");
			return;
		}

		boolean hasCourses = CourseAssignService.checkStudentHasCourse(studentId);
		if (hasCourses) {
			System.out.print(
					"This student is enrolled in courses. Do you want to delete those enrollments as well? (yes/no): ");
			String input = scanner.nextLine().trim().toLowerCase();

			if (!input.equals("yes")) {
				System.out.println("Deletion canceled.");
				return;
			}

			CourseAssignService.deleteAllAssignmentsByStudent(studentId);
		}

		students.removeIf(student -> student.getStudentId() == studentId);
		saveStudents(students);
		System.out.println("Student deleted successfully.");
	}

	public static void searchStudentById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Student ID: ");
		int studentId = scanner.nextInt();

		ArrayList<Student> students = readStudents();

		for (Student student : students) {
			if (student.getStudentId() == studentId) {
				System.out.println("\nStudent Found:");
				System.out.println("Student ID: " + student.getStudentId());
				System.out.println("Name: " + student.getName());
				System.out.println("Email: " + student.getEmail());
				return;
			}
		}
		System.out.println("Student with ID " + studentId + " not found.");
	}

	private static boolean isStudentExists(int studentId) {
		ArrayList<Student> students = readStudents();
		for (Student student : students) {
			if (student.getStudentId() == studentId) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Student> readStudents() {
		ArrayList<Student> students = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
			students = (ArrayList<Student>) ois.readObject();
		} catch (FileNotFoundException e) {
			return students;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return students;
	}

	public static void saveStudents(ArrayList<Student> students) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(students);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void convertCsvToBinary() {
		ArrayList<Student> students = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
			br.readLine();
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

	private static Student parseStudent(String line) {
		String[] data = line.split(",");
		if (data.length != 3) {
			throw new RuntimeException("Invalid CSV format: Each row must have 3 fields.");
		}
		return new Student(Integer.parseInt(data[0]), data[1], data[2]);
	}

	private static void appendToCSV(int id, String name, String email) {
		try (FileWriter fw = new FileWriter(CSV_PATH, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(id + "," + name + "," + email);
		} catch (IOException e) {
			System.out.println("Error appending student to CSV!");
		}
	}
}
