package service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import model.Instructor;

public class InstructorService {
	private static final String FILE_PATH = "./data/instructors.dat";

	public static boolean addInstructor(Instructor instructor) throws CRUDFailedException, DataNotFoundException {
		try {
			if (isInstructorExists(instructor.getInstructorId())) {
				throw new CRUDFailedException("Instructor ID already exists. Please enter a unique ID.");
			}

			appendToBinaryFile(instructor);
			return true;
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	public static boolean updateInstructor(Instructor newInstructor) throws DataNotFoundException, CRUDFailedException {
		ArrayList<Instructor> instructors = readBinaryFile();
		boolean found = false;

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == newInstructor.getInstructorId()) {
				instructor.setName(newInstructor.getName());
				instructor.setEmail(newInstructor.getEmail());
				instructor.setDepartment(newInstructor.getDepartment());
				found = true;
				break;
			}
		}

		if (found) {
			writeBinaryFile(instructors);
			return true;
		} else {
			throw new DataNotFoundException("Instructor with ID " + newInstructor.getInstructorId() + " not found.");
		}
	}

	public static ArrayList<Instructor> listInstructors() throws DataNotFoundException {
		ArrayList<Instructor> instructors = readBinaryFile();
		if (instructors.isEmpty()) {
			throw new DataNotFoundException("No instructors found.");
		}

		return instructors;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Instructor> readBinaryFile() throws DataNotFoundException {
		ArrayList<Instructor> list = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
			list = (ArrayList<Instructor>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new DataNotFoundException("No existing instructor records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Instructor> list) throws CRUDFailedException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(list);
		} catch (IOException e) {
			throw new CRUDFailedException(("Error saving instructor data!"));
		}
	}

	private static void appendToBinaryFile(Instructor instructor) throws CRUDFailedException, DataNotFoundException {
		ArrayList<Instructor> instructors = readBinaryFile();
		instructors.add(instructor);
		writeBinaryFile(instructors);
	}

	private static boolean isInstructorExists(int instructorId) throws DataNotFoundException {
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
	public static boolean checkInstructorHasRelation(int instructorId) throws DataNotFoundException, IOException {
		ArrayList<Instructor> instructors = readBinaryFile();
		boolean found = false;

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == instructorId) {
				found = true;
				break;
			}
		}

		if (!found) {
			throw new DataNotFoundException("Instructor with ID " + instructorId + " not found.");
		}

		return CourseAssignService.checkIntructorHasCourse(instructorId);

	}

	public static boolean deleteInstructor(int instructorId)
			throws DataNotFoundException, CRUDFailedException, IOException {
		ArrayList<Instructor> instructors = readBinaryFile();

		// Delete relations
		CourseAssignService.deleteAllCourseFromInstructor(instructorId);

		// Delete record
		instructors.removeIf(instructor -> instructor.getInstructorId() == instructorId);
		writeBinaryFile(instructors);
		return true;
	}

	public static Instructor searchInstructorById(int instructorId) throws DataNotFoundException {

		ArrayList<Instructor> instructors = readBinaryFile();

		for (Instructor instructor : instructors) {
			if (instructor.getInstructorId() == instructorId) {
				return instructor;
			}
		}
		throw new DataNotFoundException("Instructor with ID " + instructorId + " not found.");
	}

}
