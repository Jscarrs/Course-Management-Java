package service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.CRUDFailedException;
import exceptions.DataNotFoundException;
import model.CourseInstructor;
import model.CourseStudents;

public class CourseAssignService implements Serializable {

	private static final long serialVersionUID = 1L;
	final static String filePathStudents = "./data/course_students.dat";
	final static String filePathInstructor = "./data/course_instructor.dat";

	private static void isFileExists(String filePath) throws IOException {
		File file = new File(filePath);

		if (!file.exists()) {
			file.createNewFile();
		}

	}

	public static boolean assignInstructorToCourse(int courseId, int instructorId)
			throws IOException, CRUDFailedException, DataNotFoundException {

		isFileExists(filePathInstructor);

		if (isIntructorAssignedToCourse(courseId)) {
			return false;
		}

		// Checking if Instructor exists - If not throw exception
		InstructorService.searchInstructorById(instructorId);
		CourseService.searchCourseById(courseId);

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "rw")) {
			file.seek(file.length());
			file.writeInt(courseId);
			file.writeInt(instructorId);
			return true;
		} catch (IOException e) {
			throw e;
		}
	}

	public static boolean assignStudentToCourse(int courseId, int studentId)
			throws IOException, CRUDFailedException, DataNotFoundException {
		isFileExists(filePathStudents);

		if (isStudentCourseAssigned(courseId, studentId)) {
			return false;
		}

		// Checking if both exists - If not throw exception
		StudentService.searchStudentById(studentId);
		CourseService.searchCourseById(courseId);

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "rw")) {
			file.seek(file.length());
			file.writeInt(courseId);
			file.writeInt(studentId);
			return true;
		} catch (IOException e) {
			throw e;
		}
	}

	/*
	 * One course only can have one instructor, but instructor can have multiple
	 * courses
	 */
	private static boolean isIntructorAssignedToCourse(int pcourseId) throws CRUDFailedException, IOException {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instId = file.readInt();
				if (courseId == pcourseId) {
					throw new CRUDFailedException(
							"Course " + pcourseId + " has already assigned to instructor: " + instId);
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return false;
	}

	// Used in delete functions
	protected static boolean checkIntructorAssignedToCourse(int pcourseId) throws IOException {

		if (!isDeletePathExist(filePathInstructor))
			return false;

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				file.readInt();// skipping
				if (courseId == pcourseId) {
					return true;
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return false;
	}

	// Used in delete functions
	protected static boolean checkIntructorHasCourse(int pinstructorId) throws IOException {

		if (!isDeletePathExist(filePathInstructor))
			return false;

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				file.readInt(); // skipping
				int instId = file.readInt();
				if (instId == pinstructorId) {
					return true;
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return false;
	}

	// Used in delete functions
	protected static boolean checkStudentAssignedToCourse(int pcourse) throws IOException {

		if (!isDeletePathExist(filePathStudents))
			return false;

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				file.readInt();// skipping
				if (courseId == pcourse) {
					return true;
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return false;
	}

	// Used in delete functions
	protected static boolean checkStudentHasCourse(int pstudentId) throws IOException {

		if (!isDeletePathExist(filePathStudents))
			return false;

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				file.readInt(); // skipping
				int studentId = file.readInt();
				if (studentId == pstudentId) {
					return true;
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return false;
	}

	/*
	 * Course and Student has many to many relation but we need to avoid duplicate
	 * records
	 */
	private static boolean isStudentCourseAssigned(int pcourseId, int pstudentId)
			throws IOException, CRUDFailedException {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (courseId == pcourseId && studentId == pstudentId) {
					throw new CRUDFailedException(
							"Course " + pcourseId + " has already assigned to student: " + pstudentId);
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return false;
	}

	private static List<CourseInstructor> getAllInstructorAssignments() throws IOException {
		isFileExists(filePathInstructor);
		List<CourseInstructor> assignments = new ArrayList<>();
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instructorId = file.readInt();
				assignments.add(new CourseInstructor(courseId, instructorId));
			}
		} catch (IOException e) {
			throw e;
		}
		return assignments;
	}

	private static List<CourseStudents> getAllStudentAssignments() throws IOException {
		isFileExists(filePathStudents);
		List<CourseStudents> students = new ArrayList<>();
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				students.add(new CourseStudents(courseId, studentId));
			}
		} catch (IOException e) {
			throw e;
		}
		return students;
	}

	public static List<CourseInstructor> displayAllInstructorAssignments() throws IOException, DataNotFoundException {
		List<CourseInstructor> assignments = getAllInstructorAssignments();
		if (assignments.isEmpty()) {
			throw new DataNotFoundException("No instructor assignments found.");
		}
		return assignments;
	}

	public static List<CourseStudents> displayAllStudentAssignments() throws IOException, DataNotFoundException {
		List<CourseStudents> students = getAllStudentAssignments();
		if (students.isEmpty()) {
			throw new DataNotFoundException("No student assignments found.");
		}
		return students;
	}

//	DELETE FUNCTIONS:

	private static boolean isDeletePathExist(String filePath) {
		if (new File(filePath).exists())
			return true;
		else
			return false;
	}

	// Delete specific course - student relation
	public static boolean deleteCourseStudent(int pcourseId, int pstudentId) throws CRUDFailedException, IOException {
		if (isDeletePathExist(filePathStudents)) {
			List<CourseStudents> keepingList = new ArrayList<>();
			boolean isDeletedRec = false;

			try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
				while (file.getFilePointer() < file.length()) {
					int courseId = file.readInt();
					int studentId = file.readInt();
					if (courseId == pcourseId && studentId == pstudentId) {
						isDeletedRec = true;
					} else {
						keepingList.add(new CourseStudents(courseId, studentId));
					}
				}
			} catch (IOException e) {
				throw e;
			}

			if (!isDeletedRec) {
				throw new CRUDFailedException("No matching record found to delete.");
			}
			return overrideCourseStudentFile(keepingList);
		}
		return false;
	}

	// Delete all courses from student
	// Used in delete
	protected static boolean deleteAllCourseFromStudent(int pstudentId) throws IOException, CRUDFailedException {
		if (isDeletePathExist(filePathStudents)) {

			List<CourseStudents> keepingList = new ArrayList<>();
			boolean isDeletedRec = false;

			try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
				while (file.getFilePointer() < file.length()) {
					int courseId = file.readInt();
					int studentId = file.readInt();
					if (studentId == pstudentId) {
						isDeletedRec = true;
					} else {
						keepingList.add(new CourseStudents(courseId, studentId));
					}
				}
			} catch (IOException e) {
				throw e;
			}

			if (!isDeletedRec) {
				throw new CRUDFailedException("Course - No matching records found to delete.");
			}

			return overrideCourseStudentFile(keepingList);
		}
		return false;
	}

	// Delete all students from given course
	// Used in Delete function
	protected static boolean deleteAllStudentFromCourse(int pcourseId) throws IOException, CRUDFailedException {
		if (isDeletePathExist(filePathStudents)) {

			List<CourseStudents> keepingList = new ArrayList<>();
			boolean isDeletedRec = false;

			try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
				while (file.getFilePointer() < file.length()) {
					int courseId = file.readInt();
					int studentId = file.readInt();
					if (courseId == pcourseId) {
						isDeletedRec = true;
					} else {
						keepingList.add(new CourseStudents(courseId, studentId));
					}
				}
			} catch (IOException e) {
				throw e;
			}
			if (!isDeletedRec) {
				throw new CRUDFailedException("No matching student records found to delete.");
			}

			return overrideCourseStudentFile(keepingList);
		}
		return false;
	}

	// Delete specific course - instructor relation
	public static boolean deleteCourseInstructor(int pcourseId, int pintructorId)
			throws CRUDFailedException, IOException {
		if (isDeletePathExist(filePathInstructor)) {

			List<CourseInstructor> keepingList = new ArrayList<>();
			boolean isDeletedRec = false;

			try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
				while (file.getFilePointer() < file.length()) {
					int courseId = file.readInt();
					int instructorId = file.readInt();
					if (courseId == pcourseId && instructorId == pintructorId) {
						isDeletedRec = true;
					} else {
						keepingList.add(new CourseInstructor(courseId, instructorId));
					}
				}
			} catch (IOException e) {
				throw new CRUDFailedException(e.getMessage());
			}

			if (!isDeletedRec) {
				throw new CRUDFailedException("No matching record found to delete.");
			}

			return overrideCourseInstructorFile(keepingList);
		}
		return false;
	}

	// Delete all assigned Courses from Instructor
	// Used in delete function
	protected static boolean deleteAllCourseFromInstructor(int pinstructorId) throws CRUDFailedException, IOException {
		if (isDeletePathExist(filePathInstructor)) {

			List<CourseInstructor> keepingList = new ArrayList<>();
			boolean isDeletedRec = false;

			try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
				while (file.getFilePointer() < file.length()) {
					int courseId = file.readInt();
					int instructorId = file.readInt();
					if (instructorId == pinstructorId) {
						isDeletedRec = true;
					} else {
						keepingList.add(new CourseInstructor(courseId, instructorId));
					}
				}
			} catch (IOException e) {
				throw new CRUDFailedException(e.getMessage());
			}

			if (!isDeletedRec) {
				throw new CRUDFailedException("No matching records found to delete.");
			}

			return overrideCourseInstructorFile(keepingList);
		}
		return false;
	}

	// Delete all assigned Instructors from Course
	// Used in Delete function
	protected static boolean deleteAllInstructorFromCourse(int pcourseId) throws CRUDFailedException, IOException {
		if (isDeletePathExist(filePathInstructor)) {

			List<CourseInstructor> keepingList = new ArrayList<>();
			boolean isDeletedRec = false;

			try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
				while (file.getFilePointer() < file.length()) {
					int courseId = file.readInt();
					int instructorId = file.readInt();
					if (courseId == pcourseId) {
						isDeletedRec = true;
					} else {
						keepingList.add(new CourseInstructor(courseId, instructorId));
					}
				}
			} catch (IOException e) {
				throw e;
			}

			if (!isDeletedRec) {
				throw new CRUDFailedException("Instructor - No matching records found to delete.");
			}

			return overrideCourseInstructorFile(keepingList);
		}
		return false;
	}

	private static boolean overrideCourseStudentFile(List<CourseStudents> keepingList) throws IOException {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "rw")) {
			file.setLength(0);
			for (CourseStudents record : keepingList) {
				file.writeInt(record.getCourseId());
				file.writeInt(record.getStudentId());
			}

			return true;
		} catch (IOException e) {
			throw e;
		}
	}

	private static boolean overrideCourseInstructorFile(List<CourseInstructor> keepingList) throws IOException {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "rw")) {
			file.setLength(0);
			for (CourseInstructor record : keepingList) {
				file.writeInt(record.getCourseId());
				file.writeInt(record.getInstructorId());
			}
			return true;
		} catch (IOException e) {
			throw e;
		}
	}
}
