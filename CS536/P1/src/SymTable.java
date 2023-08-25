///////////////////////////////////////////////////////////////////////////////
// File:             P1 - SymTable.java
// Semester:         CS 536 Spring 2019
// Author:           Devin Johnson
// Email:            djohnson58@wisc.edu
// CS Login:         devinj
// Lecturer's Name:  Loris D'Antoni
///////////////////////////////////////////////////////////////////////////////

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * SymTable represents a symbol table object which
 * stores the identifiers declared in the program being compiled
 *
 * <p>Bugs: None known
 *
 * @author Devin Johnson
 */
public class SymTable {

    // List of hashmaps which match strings to symbols
    private List<HashMap<String,Sym>> scopes;

    public SymTable(){
        this.scopes = new LinkedList<>();
        this.scopes.add(new HashMap<>());
    }

    /**
     * Add idName and sym to the first hashmap of the list.
     *
     * @param idName Identifier name
     * @param sym Symbol corresponding to identifier
     */
    public void addDecl(String idName, Sym sym) throws DuplicateSymException, EmptySymTableException, WrongArgumentException{
        // If the table is empty
        if(this.scopes.size() == 0){
            throw new EmptySymTableException();
        }
        // If both idName and sym arguments are null
        if(idName == null && sym == null){
            throw new WrongArgumentException("Id name and sym are null");
        }
        // If just the idName is null
        if(idName == null && sym != null){
            throw new WrongArgumentException("Id name is null");
        }
        // If just the sym is null
        if(idName != null && sym == null){
            throw new WrongArgumentException("Sym is null");
        }
        // If the symbol is already in the scope
        if(this.scopes.get(0).containsKey(sym.getType())){
            throw new DuplicateSymException();
        }
        // Otherwise, add to first hashmap
        this.scopes.get(0).put(idName,sym);
    }

    /**
     * Add a new hashmap to the front of the list
     */
    public void addScope() {
        this.scopes.add(0, new HashMap<>());
    }

    /**
     * Get the first symbol of the first hashmap in the list
     *
     * @param name Identifier name
     * @return The symbol of which corresponds to the identifier from the first hashmap in the list (or null)
     */
    public Sym lookupLocal(String name) throws EmptySymTableException {
        // Empty table
        if(this.scopes.size() == 0){
            throw new EmptySymTableException();
        }
        // Return the Sym
        return this.scopes.get(0).get(name);
    }

    /**
     * Go over all hashmaps in the list and get the symbol as soon as it's found
     *
     * @param name Identifier name
     * @return The symbol of which corresponds to the identifier (or null)
     */
    public Sym lookupGlobal(String name) throws EmptySymTableException {
        // Empty table
        if(this.scopes.size() == 0){
            throw new EmptySymTableException();
        }
        // For each scope in the list
        for (HashMap<String, Sym> scope : this.scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        // Not found
        return null;
    }

    /**
     * Remove a hashmap from the front of the list
     */
    public void removeScope() throws EmptySymTableException {
        // Empty table
        if(this.scopes.size() == 0){
            throw new EmptySymTableException();
        }
        // Remove hashmap from front of list
        this.scopes.remove(0);
    }

    /**
     * Print out the list (sym table)
     */
    public void print() {
        System.out.println("\n=== Sym Table ===\n");
        // For each scope, print it out
        for (HashMap<String, Sym> scope: this.scopes) {
            System.out.println(scope.toString());
        }
        // One more new line
        System.out.println();
    }
}
