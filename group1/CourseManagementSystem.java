package group1;

import java.util.ArrayList;

/**
 * Main class
 */
public class CourseManagementSystem {

	static ArrayList<Course> courses;
	static ArrayList<Instructor> instructors;
	static ArrayList<Student> students;

	public static void main(String[] args) {

		// Read Course Details
		try {
			courses = Course.readCourses();
			// Read Instructor Details
			instructors = Instructor.readInstructors();
			// Read Student Details
			students = Student.readStudents();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Assigning by array index - Needs to change
		assignInstructorToCourse(0, 0);
		assignCourseToStudent(0, 0);
		assignCourseToStudent(0, 1);
		assignCourseToStudent(0, 2);

		// Display Course Details by index - Needs to change
		courses.get(0).displayCourseDetails();

	}

	private static boolean assignInstructorToCourse(int courseId, int instructorId) {
		// TODO: Needs to check if both records exists
		Instructor instructor = instructors.get(instructorId);
		courses.get(courseId).assignInstructor(instructor);
		return false;
	}

	private static boolean assignCourseToStudent(int courseId, int studentId) {
		// TODO: Needs to check if both records exists
		Student student = students.get(studentId);
		courses.get(courseId).addStudent(student);
		return false;
	}
}