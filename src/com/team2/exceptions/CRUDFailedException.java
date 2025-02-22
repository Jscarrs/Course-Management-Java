package com.team2.exceptions;

/**
 * 
 */

@SuppressWarnings("serial")
public class CRUDFailedException extends Exception {
	public CRUDFailedException(String m) {
		super(m);
	}
}