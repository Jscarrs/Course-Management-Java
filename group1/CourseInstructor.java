package group1;

import java.io.Serializable;

public class CourseInstructor implements Serializable {
	private static final long serialVersionUID = 1L;

	private int courseId;
	private int instructorId;

	public CourseInstructor(int courseId, int instructorId) {
		this.courseId = courseId;
		this.instructorId = instructorId;
	}

	/**
	 * @return the courseId
	 */
	public int getCourseId() {
		return courseId;
	}

	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	/**
	 * @return the instructorId
	 */
	public int getInstructorId() {
		return instructorId;
	}

	/**
	 * @param instructorId the instructorId to set
	 */
	public void setInstructorId(int instructorId) {
		this.instructorId = instructorId;
	}

	@Override
	public String toString() {
		return "CourseInstructor [courseId=" + courseId + ", instructorId=" + instructorId + "]";
	}
	
	

}
