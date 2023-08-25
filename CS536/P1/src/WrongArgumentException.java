///////////////////////////////////////////////////////////////////////////////
// File:             P1 - WrongArgumentException.java
// Semester:         CS 536 Spring 2019
// Author:           Devin Johnson
// Email:            djohnson58@wisc.edu
// CS Login:         devinj
// Lecturer's Name:  Loris D'Antoni
///////////////////////////////////////////////////////////////////////////////

/**
 * An exception for when the incorrect argument is passed to a function
 *
 * <p>Bugs: None known
 *
 * @author Devin Johnson
 */
public class WrongArgumentException extends Exception {
    public WrongArgumentException(String message) {
        new Exception(message);
    }
}
