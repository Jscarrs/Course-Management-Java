package model;

import java.io.Serializable;

/**
 * Class Course
 */
public class Course implements Serializable {
	private static final long serialVersionUID = 1L;

	private int courseId;
	private String courseName;
	private int credits;

	/**
	 * 
	 */
	public Course() {
		// TODO Auto-generated constructor stub
	}

	public Course(int courseId, String courseName, int credits) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.credits = credits;
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

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getCredits() {
		return credits;
	}
}