package model;

import java.io.Serializable;

/**
 * Subclass Instructor
 */
public class Instructor extends Person implements Serializable {
	private static final long serialVersionUID = 1L;

	private int instructorId;
	private String department;

	public Instructor() {
		super();
	}

	public Instructor(int instructorId, String name, String email, String department) {
		super(name, email);
		this.instructorId = instructorId;
		this.department = department;
	}

	public int getInstructorId() {
		return instructorId;
	}

	public void setInstructorId(int instructorId) {
		this.instructorId = instructorId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public void displayDetails() {
		System.out.println("Instructor ID: " + instructorId);
		System.out.println("Department: " + department);
		super.displayDetails();
	}

}
