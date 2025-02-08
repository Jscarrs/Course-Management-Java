package service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import model.Course;
import model.Instructor;
import model.Student;

public class StudentService {
	private static final String FILE_PATH = "./data/students.dat";

	// public static void addStudent() {
	// 	Scanner scanner = new Scanner(System.in);

	// 	System.out.print("Enter Student ID: ");
	// 	int id = scanner.nextInt();
	// 	scanner.nextLine();

	// 	if (isStudentExists(id)) {
	// 		System.out.println("Student ID already exists. Please enter a unique ID.");
	// 		return;
	// 	}

	// 	System.out.print("Enter Student Name: ");
	// 	String name = scanner.nextLine();
	// 	System.out.print("Enter Student Email: ");
	// 	String email = scanner.nextLine();

	// 	Student newStudent = new Student(id, name, email);
	// 	ArrayList<Student> students = readStudents();
	// 	students.add(newStudent);
	// 	saveStudents(students);

	// 	System.out.println("Student added successfully!");
	// }
	public static boolean addStudent(Student student) throws CRUDFailedException, DataNotFoundException {
		try {
			if (isStudentExists(student.getStudentId())) {
				throw new CRUDFailedException("Course ID already exists. Please enter a unique ID.");
			}

			Student newStudent = student;
			appendToBinaryFile(newStudent);

			return true;
		} catch (NumberFormatException e) {
			throw new CRUDFailedException("Error reading input! Ensure correct format.");
		}
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

	public static List<Student> listStudents() throws DataNotFoundException {
		ArrayList<Student> students = readStudents();
		if (students.isEmpty()) {
			throw new DataNotFoundException("No students found.");
		}
		return students;
	}

	// Check before delete
	public static boolean checkStudentHasRelation(int studentId) throws DataNotFoundException, IOException {

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
			throw new DataNotFoundException("Student with ID " + studentId + " not found.");
		}

		return CourseAssignService.checkStudentHasCourse(studentId);

	}

	public static void deleteStudent(int studentId) throws DataNotFoundException, CRUDFailedException, IOException {
		ArrayList<Student> students = readStudents();
		// Delete relations
		CourseAssignService.deleteAllCourseFromStudent(studentId);

		// Delete record
		students.removeIf(student -> student.getStudentId() == studentId);
		saveStudents(students);
		System.out.println("Student deleted successfully.");
	}

	public static Student searchStudentById(int studentId) throws DataNotFoundException {
		ArrayList<Student> students = readStudents();

		for (Student student : students) {
			if (student.getStudentId() == studentId) {
				return student;
			}
		}

		throw new DataNotFoundException("Student with ID " + studentId + " not found.");
	}

	private static boolean isStudentExists(int studentId) {
		if (studentId < 1)
			return true;
		ArrayList<Student> students = readStudents();
		for (Student student : students) {
			if (student.getStudentId() == studentId) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Student> readStudents() {
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

	private static void saveStudents(ArrayList<Student> students) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(students);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private static ArrayList<Student> readBinaryFile() throws DataNotFoundException {
		ArrayList<Student> list = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
			list = (ArrayList<Student>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new DataNotFoundException("No existing student records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Student> list) throws CRUDFailedException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(list);
		} catch (IOException e) {
			throw new CRUDFailedException("Error saving student data!");
		}
	}
	private static void appendToBinaryFile(Student student) throws DataNotFoundException, CRUDFailedException {
		ArrayList<Student> students = readBinaryFile();
		students.add(student);
		writeBinaryFile(students);
	}
}
