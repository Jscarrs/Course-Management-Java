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

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import model.Instructor;

public class InstructorService {
	private static final String FILE_PATH = "./data/instructors.dat";

	public static void addInstructor() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter Instructor ID: ");
			int instructorId = Integer.parseInt(reader.readLine());

			if (isInstructorExists(instructorId)) {
				System.out.println("Instructor ID already exists. Please enter a unique ID.");
				return;
			}

			System.out.print("Enter Instructor Name: ");
			String name = reader.readLine();
			System.out.print("Enter Instructor Email: ");
			String email = reader.readLine();
			System.out.print("Enter Department: ");
			String department = reader.readLine();

			Instructor newInstructor = new Instructor(instructorId, name, email, department);
			appendToBinaryFile(newInstructor);
			System.out.println("Instructor added successfully!");
		} catch (IOException | NumberFormatException e) {
			System.out.println("Error reading input! Ensure correct format.");
		}
	}

	public static void updateInstructor() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Instructor ID to update: ");
		int id = scanner.nextInt();
		scanner.nextLine();

		ArrayList<Instructor> instructors = readBinaryFile();
		boolean found = false;

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == id) {
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
			writeBinaryFile(instructors);
			System.out.println("Instructor updated successfully!");
		} else {
			System.out.println("Instructor with ID " + id + " not found.");
		}
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
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
			list = (ArrayList<Instructor>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No existing instructor records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Instructor> list) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(list);
		} catch (IOException e) {
			System.out.println("Error saving instructor data!");
		}
	}

	private static void appendToBinaryFile(Instructor instructor) {
		ArrayList<Instructor> instructors = readBinaryFile();
		instructors.add(instructor);
		writeBinaryFile(instructors);
	}

	private static boolean isInstructorExists(int instructorId) {
		if (instructorId < 1)
			return true;
		ArrayList<Instructor> instructors = readBinaryFile();
		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == instructorId) {
				return true;
			}
		}
		return false;
	}

	// Check before delete
	public static boolean checkInstructorHasRelation(int instructorId) throws DataNotFoundException {
		ArrayList<Instructor> instructors = readBinaryFile();
		boolean found = false;

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == instructorId) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Instructor with ID " + instructorId + " not found.");
			throw new DataNotFoundException("Instructor with ID " + instructorId + " not found.");
		}

		return CourseAssignService.checkIntructorHasCourse(instructorId);

	}

	public static boolean deleteInstructor(int instructorId) throws DataNotFoundException, CRUDFailedException {
		ArrayList<Instructor> instructors = readBinaryFile();

		// Delete relations
		CourseAssignService.deleteAllCourseFromInstructor(instructorId);

		// Delete record
		instructors.removeIf(instructor -> instructor.getInstructorId() == instructorId);
		writeBinaryFile(instructors);
		return true;
	}

	public static void searchInstructorById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Instructor ID: ");
		int instructorId = scanner.nextInt();

		ArrayList<Instructor> instructors = readBinaryFile();

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == instructorId) {
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
