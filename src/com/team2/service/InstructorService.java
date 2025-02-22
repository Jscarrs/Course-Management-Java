package com.team2.service;

import java.util.List;

import com.team2.dao.InstructorDAO;
import com.team2.exceptions.CRUDFailedException;
import com.team2.exceptions.DataNotFoundException;
import com.team2.model.Instructor;

public class InstructorService {

	public static List<Instructor> listInstructors() throws DataNotFoundException {
		List<Instructor> instructors = InstructorDAO.getInstructors();
		if (instructors.isEmpty()) {
			throw new DataNotFoundException("No instructors found.");
		}

		return instructors;
	}

	public static Instructor searchInstructorById(int instructorId) throws DataNotFoundException {

		Instructor instructor = InstructorDAO.getInstructorById(instructorId);
		if (instructor != null) {
			return instructor;
		}

		throw new DataNotFoundException("Instructor with ID " + instructorId + " not found.");
	}

	public static boolean addInstructor(Instructor instructor) throws CRUDFailedException, DataNotFoundException {
		try {
			Instructor newInstructor = instructor;
			InstructorDAO.insertInstructor(newInstructor);

			return true;
		} catch (NumberFormatException e) {
			throw new CRUDFailedException("Error reading input! Ensure correct format.");
		}

	}

	public static boolean updateInstructor(Instructor newInstructor) throws DataNotFoundException, CRUDFailedException {
		boolean res = InstructorDAO.updateInstructor(newInstructor);

		if (!res) {
			throw new DataNotFoundException("Instructor with ID " + newInstructor.getInstructorId() + " not found.");
		}
		return res;
	}

	public static boolean deleteInstructor(int instructorId) throws CRUDFailedException {
		boolean res = InstructorDAO.deleteInstructor(instructorId);
		if (!res) {
			throw new CRUDFailedException("Instructor with ID " + instructorId + " not found.");
		}
		return res;
	}
}
