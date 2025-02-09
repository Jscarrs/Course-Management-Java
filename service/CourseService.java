package service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import model.Course;
import view.AlertDialog;

public class CourseService {
	private static final String FILE_PATH = "./data/courses.dat";

	public static boolean addCourse(Course course) throws CRUDFailedException, DataNotFoundException {
		try {
			if (isCourseExists(course.getCourseId())) {
				throw new CRUDFailedException("Course ID already exists. Please enter a unique ID.");
			}

			Course newCourse = course;
			appendToBinaryFile(newCourse);

			return true;
		} catch (NumberFormatException e) {
			throw new CRUDFailedException("Error reading input! Ensure correct format.");
		}
	}

	public static boolean updateCourse(Course newCourse) throws CRUDFailedException, DataNotFoundException {
		ArrayList<Course> courses = readBinaryFile();
		boolean found = false;

		for (Course course : courses) {
			if (course.getCourseId() == newCourse.getCourseId()) {
				course.setCourseName(newCourse.getCourseName());
				course.setCredits(newCourse.getCredits());
				found = true;
				break;
			}
		}

		if (found) {
			writeBinaryFile(courses);
			return true;
		} else {
			throw new CRUDFailedException("Course with ID " + newCourse.getCourseId() + " not found.");
		}
	}

	public static List<Course> listCourses() throws DataNotFoundException {
		ArrayList<Course> courses = readBinaryFile();
		if (courses.isEmpty()) {
			throw new DataNotFoundException("No courses found.");
		}
		return courses;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Course> readBinaryFile() throws DataNotFoundException {
		ArrayList<Course> list = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
			list = (ArrayList<Course>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new DataNotFoundException("No existing course records found.");
		}
		return list;
	}

	private static void writeBinaryFile(ArrayList<Course> list) throws CRUDFailedException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(list);
		} catch (IOException e) {
			throw new CRUDFailedException("Error saving course data!");
		}
	}

	private static void appendToBinaryFile(Course course) throws DataNotFoundException, CRUDFailedException {
		ArrayList<Course> courses = readBinaryFile();
		courses.add(course);
		writeBinaryFile(courses);
	}

	private static boolean isCourseExists(int courseId) throws DataNotFoundException {
		if (courseId < 1)
			return true;
		ArrayList<Course> courses = readBinaryFile();
		for (Course course : courses) {
			if (course.getCourseId() == courseId) {
				return true;
			}
		}
		return false;
	}

	public static Course searchCourseById(int courseId) throws DataNotFoundException {

		ArrayList<Course> courses = readBinaryFile();

		for (Course course : courses) {
			if (course.getCourseId() == courseId) {
				return course;
			}
		}

		throw new DataNotFoundException("Course with ID " + courseId + " not found.");
	}

	// Check before delete - for UI confirm
	public static boolean checkCourseHasRelation(int courseId) throws DataNotFoundException, IOException {

		ArrayList<Course> courses = readBinaryFile();

		boolean found = false;

		for (Course course : courses) {
			if (course.getCourseId() == courseId) {
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Course with ID " + courseId + " not found.");
			throw new DataNotFoundException("Course with ID " + courseId + " not found.");
		}

		boolean hasStudents = CourseAssignService.checkStudentAssignedToCourse(courseId);
		boolean hasInstructor = CourseAssignService.checkIntructorAssignedToCourse(courseId);
		return hasStudents && hasInstructor;
	}

	public static boolean deleteCourse(int courseId) throws DataNotFoundException, CRUDFailedException, IOException {

		ArrayList<Course> courses = readBinaryFile();

		// Deleting all relations - if exist
		if (CourseAssignService.checkStudentAssignedToCourse(courseId))
			CourseAssignService.deleteAllStudentFromCourse(courseId);

		if (CourseAssignService.checkIntructorAssignedToCourse(courseId))
			CourseAssignService.deleteAllInstructorFromCourse(courseId);

		// Deleting main record
		courses.removeIf(course -> course.getCourseId() == courseId);
		writeBinaryFile(courses);
		return true;
	}
}
