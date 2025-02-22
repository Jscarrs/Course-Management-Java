package com.team2.service;

import java.util.List;

import com.team2.dao.StudentCourseDAO;
import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.StudentCourse;

public class StudentCourseService {

	public static List<StudentCourse> listAllEnrollments() throws DataNotFoundException {
		List<StudentCourse> studentsCourse = StudentCourseDAO.getAllEnrollments();
		if (studentsCourse.isEmpty()) {
			throw new DataNotFoundException("No enrollments found.");
		}
		return studentsCourse;
	}

	public static boolean enrollStudentInCourse(int courseId, int studentId) throws CRUDFailedException {
		boolean result = StudentCourseDAO.enrollStudent(courseId, studentId);
		if (!result) {
			throw new CRUDFailedException(
					"Failed to enroll student ID " + studentId + " in course ID " + courseId + ".");
		}
		return result;
	}

	public static boolean dropStudentFromCourse(int courseId, int studentId) throws CRUDFailedException {
		boolean result = StudentCourseDAO.dropStudentFromCourse(courseId, studentId);
		if (!result) {
			throw new CRUDFailedException(
					"Failed to remove student ID " + studentId + " from course ID " + courseId + ".");
		}
		return result;
	}
}
