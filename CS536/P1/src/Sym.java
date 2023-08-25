///////////////////////////////////////////////////////////////////////////////
// File:             P1 - Sym.java
// Semester:         CS 536 Spring 2019
// Author:           Devin Johnson
// Email:            djohnson58@wisc.edu
// CS Login:         devinj
// Lecturer's Name:  Loris D'Antoni
///////////////////////////////////////////////////////////////////////////////

/**
 * Sym represents a sym object with methods to print, get, and assign a type.
 * <p>Bugs: None known
 * @author Devin Johnson
 */
public class Sym {

    // The type of symbol
    private String type;

    public Sym(String type){
        this.type = type;
    }

    /**
     * Get the type of symbol
     * @return The symbol type
     */
    public String getType(){
        return this.type;
    }

    /**
     * Instead of usual toString, return the symbol type
     * @return The symbol type
     */
    @Override
    public String toString(){
        return getType();
    }
}
