package model;

import java.io.Serializable;

public class CourseStudents implements Serializable {
	private static final long serialVersionUID = 1L;

	private int courseId;
	private int studentId;

	/**
	 * 
	 */
	public CourseStudents() {
		// TODO Auto-generated constructor stub
	}

	public CourseStudents(int courseId, int studentId) {
		this.courseId = courseId;
		this.studentId = studentId;
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
	 * @return the studentId
	 */
	public int getStudentId() {
		return studentId;
	}

	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	@Override
	public String toString() {
		return "CourseStudents [courseId=" + courseId + ", studentId=" + studentId + "]";
	}

}
