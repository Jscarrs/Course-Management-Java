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

//created instructor manager class to manage insertion and listing of instructor data and also manage save changes to file
public class InstructorManager {
	private static final String INSTRUCTOR_FILE = "./data/instructors.dat";
	private static final String INSTRUCTOR_CSV = "./data/instructors.csv";

	public static void addInstructor() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter Instructor ID: ");
			String instructorId = reader.readLine();
			System.out.print("Enter Instructor Name: ");
			String name = reader.readLine();
			System.out.print("Enter Instructor Email: ");
			String email = reader.readLine();
			System.out.print("Enter Department: ");
			String department = reader.readLine();

			Instructor newInstructor = new Instructor(instructorId, name, email, department);

			ArrayList<Instructor> instructors = readBinaryFile();
			instructors.add(newInstructor);
			writeBinaryFile(instructors);

			// Append instructor to CSV file
			appendToCSV(instructorId, name, email, department);

			System.out.println("Instructor added successfully!");
		} catch (IOException e) {
			System.out.println("Error reading input!");
		}
	}

	public static void updateInstructor() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Instructor ID to update: ");
		String id = scanner.nextLine();

		// Load existing instructors from the binary file
		ArrayList<Instructor> instructors = readBinaryFile();
		boolean found = false;

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId().equals(id)) {
				System.out.print("Enter new Instructor Name: ");
				String name = scanner.nextLine();
				System.out.print("Enter new Instructor Email: ");
				String email = scanner.nextLine();
				System.out.print("Enter new Department: ");
				String department = scanner.nextLine();

				instructor.setName(name);
				instructor.setEmail(email);
				instructor.setDepartment(department);
				found = true;
				break;
			}
		}

		if (found) {
			// Save updated instructor list back to binary file
			writeBinaryFile(instructors);
			System.out.println("Instructor updated successfully!");
		} else {
			System.out.println("Instructor with ID " + id + " not found.");
		}
//        scanner.close();
	}

	public static void listInstructors() {
		ArrayList<Instructor> instructors = readBinaryFile();
		if (instructors.isEmpty()) {
			System.out.println("No instructors found.");
			return;
		}

		System.out.println("\nList of Instructors:");
		for (Instructor instructor : instructors) {
			System.out.println("Instructor ID: " + instructor.getInstructorId());
			System.out.println("Name: " + instructor.getName());
			System.out.println("Email: " + instructor.getEmail());
			System.out.println("Department: " + instructor.getDepartment());
			System.out.println("----------------------------");
		}
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Instructor> readBinaryFile() {
		ArrayList<Instructor> list = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INSTRUCTOR_FILE))) {
			list = (ArrayList<Instructor>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No existing instructor records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Instructor> list) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INSTRUCTOR_FILE))) {
			oos.writeObject(list);
		} catch (IOException e) {
			System.out.println("Error saving instructor data!");
		}
	}

	// Append new instructor to CSV file
	private static void appendToCSV(String instructorId, String name, String email, String department) {
		try (FileWriter fw = new FileWriter(INSTRUCTOR_CSV, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(); // Ensure newline before adding new record
			out.println(instructorId + "," + name + "," + email + "," + department); // Write new record
		} catch (IOException e) {
			System.out.println("Error appending instructor to CSV!");
		}
	}

	public static void deleteInstructor() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Instructor ID: ");
		int instructorId = scanner.nextInt();
	    scanner.nextLine();

		ArrayList<Instructor> instructors = readBinaryFile();
		boolean found = false;

		// Check if the instructor exists
		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId().equals(String.valueOf(instructorId))) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Instructor with ID " + instructorId + " not found.");
			return;
		}

		// Check if the instructor is assigned to any courses
		boolean hasCourses = CourseAssignManager.checkIntructorHasCourse(instructorId);

		if (hasCourses) {
			System.out.print(
					"This instructor is assigned to courses. Do you want to delete those assignments as well? (yes/no): ");
			String input = scanner.nextLine().trim().toLowerCase();

			if (!input.equals("yes")) {
				System.out.println("Deletion canceled.");
				return;
			}
			// Delete related course assignments before removing the instructor
			CourseAssignManager.deleteAllCourseFromInstructor(instructorId);
		}

		// Remove the instructor from the list
		instructors.removeIf(instructor -> instructor.getInstructorId().equals(String.valueOf(instructorId)));

		// Save updated list to binary file
		writeBinaryFile(instructors);
		System.out.println("Instructor deleted successfully.");
	}

	public static void searchInstructorById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Instructor ID: ");
		String instructorId = scanner.nextLine();

		ArrayList<Instructor> instructors = readBinaryFile();

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId().equals(instructorId)) {
				System.out.println("\nInstructor Found:");
				System.out.println("Instructor ID: " + instructor.getInstructorId());
				System.out.println("Name: " + instructor.getName());
				System.out.println("Email: " + instructor.getEmail());
				System.out.println("Department: " + instructor.getDepartment());
				return;
			}
		}
		System.out.println("Instructor with ID " + instructorId + " not found.");
	}
}
