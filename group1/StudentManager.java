package group1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//created student manager class to manage insertion and listing of student data and also manage save changes to file
public class StudentManager {

	public static void addStudent() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Student ID: ");
		String id = scanner.nextLine();

		System.out.print("Enter Student Name: ");
		String name = scanner.nextLine();

		System.out.print("Enter Student Email: ");
		String email = scanner.nextLine();

		Student newStudent = new Student(id, name, email);

		// Load existing students from the binary file
		ArrayList<Student> students = Student.readStudents();
		students.add(newStudent);

		// Update global list in CourseManagementSystem
		CourseManagementSystem.students = students;

		// Save updated student list back to binary file
		Student.saveStudents(students);

		// Append new student to `students.csv`
		try (FileWriter fw = new FileWriter("./data/students.csv", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(id + "," + name + "," + email);
			out.flush();
			System.out.println("Student added to students.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Student added successfully!");
		// scanner.close();
	}

	public static void updateStudent() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Student ID to update: ");
		String id = scanner.nextLine();

		// Load existing students from the binary file
		ArrayList<Student> students = Student.readStudents();
		boolean found = false;

		for (Student student : students) {
			if (student.getStudentId().equals(id)) {
				System.out.print("Enter new Student Name: ");
				String name = scanner.nextLine();
				System.out.print("Enter new Student Email: ");
				String email = scanner.nextLine();
//                scanner.close();

				student.setName(name);
				student.setEmail(email);
				found = true;
				break;
			}
		}

		if (found) {
			// Save updated student list back to binary file
			Student.saveStudents(students);
			System.out.println("Student updated successfully!");
		} else {
			System.out.println("Student with ID " + id + " not found.");
		}

	}

	public static void listStudents() {
		ArrayList<Student> students = Student.readStudents();
		if (students.isEmpty()) {
			System.out.println("No students found.");
		} else {
			for (Student student : students) {
				student.displayDetails();
				System.out.println("-------------------");
			}
		}
	}

	public static void deleteStudent() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Student ID: ");
		int studentId = scanner.nextInt();

		ArrayList<Student> students = Student.readStudents();
		boolean found = false;

		// Check if the student exists
		for (Student student : students) {
			if (student.getStudentId().equals(String.valueOf(studentId))) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Student with ID " + studentId + " not found.");
			return;
		}

		// Check if the student has assigned courses
		boolean hasCourses = CourseAssignManager.checkStudentHasCourse(studentId);

		if (hasCourses) {
			System.out.print(
					"This student is enrolled in courses. Do you want to delete those enrollments as well? (yes/no): ");
			String input = scanner.nextLine().trim().toLowerCase();

			if (!input.equals("yes")) {
				System.out.println("Deletion canceled.");
				return;
			}
			// Delete related course enrollments before removing the student
			CourseAssignManager.deleteAllCourseFromStudent(studentId);
		}

		// Remove the student from the list
		students.removeIf(student -> student.getStudentId().equals(String.valueOf(studentId)));

		// Save updated list to binary file
		Student.saveStudents(students);
		System.out.println("Student deleted successfully.");
	}
	
	public static void searchStudentById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Student ID: ");
		String studentId = scanner.nextLine();

		ArrayList<Student> students = Student.readStudents();

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
}
