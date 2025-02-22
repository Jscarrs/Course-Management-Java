package com.team2.service;

import java.util.List;

import com.team2.dao.StudentDAO;
import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.ConstraintException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.Student;

public class StudentService {

	public static List<Student> listStudents() throws DataNotFoundException {
		List<Student> students = StudentDAO.getStudents();
		if (students.isEmpty()) {
			throw new DataNotFoundException("No students found.");
		}
		return students;
	}

	public static Student getStudentById(int studentId) throws DataNotFoundException {
		Student student = StudentDAO.getStudentById(studentId);
		if (student != null) {
			return student;
		}

		throw new DataNotFoundException("Student with ID " + studentId + " not found.");
	}

	public static boolean addStudent(Student student) throws CRUDFailedException {
		try {
			Student newStudent = student;
			StudentDAO.insertStudent(newStudent);

			return true;
		} catch (NumberFormatException e) {
			throw new CRUDFailedException("Error reading input! Ensure correct format.");
		}
	}

	public static boolean updateStudent(Student newStudent) throws CRUDFailedException {
		boolean res = StudentDAO.updateStudent(newStudent);
		if (!res) {
			throw new CRUDFailedException("Student with ID " + newStudent.getStudentId() + " not found.");
		}
		return res;
	}

	public static boolean deleteStudent(int studentId) throws CRUDFailedException, ConstraintException {
		boolean res = StudentDAO.deleteStudent(studentId);
		if (!res) {
			throw new CRUDFailedException("Student with ID " + studentId + " not found.");
		}
		return res;
	}
}
