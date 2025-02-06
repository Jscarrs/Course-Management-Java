package model;

import java.io.Serializable;

/**
 * Subclass Student
 */
public class Student extends Person implements Serializable {

	private static final long serialVersionUID = 1L;

	private int studentId;

	public Student() {
		super();
	}

	public Student(int studentId, String name, String email) {
		super(name, email);
		this.studentId = studentId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	@Override
	public void displayDetails() {
		System.out.println("Student ID: " + studentId);
		super.displayDetails();
	}

}
