///////////////////////////////////////////////////////////////////////////////
// File:             P1 - P1.java
// Semester:         CS 536 Spring 2019
// Author:           Devin Johnson
// Email:            djohnson58@wisc.edu
// CS Login:         devinj
// Lecturer's Name:  Loris D'Antoni
///////////////////////////////////////////////////////////////////////////////

/**
 * The main class of the program used to test the functions of the symbol table.
 * <p>Bugs: None known
 * @author Devin Johnson
 */
public class P1 {
    public static void main(String[] args) {

        SymTable t = new SymTable();
        int errors = 0;

        ///////////// ADDING AND REMOVING SCOPES /////////////

        // Test that the list starts with at least one scope (should be able to remove it)
        try{
            t.removeScope();
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: List doesn't start at size 1.");
            errors++;
        }

        // Add some scopes
        for (int i = 0; i < 4; i++) {
            t.addScope();
        }

        // Remove added scopes
        try{
            for(int i = 0; i < 4; i++){
                t.removeScope();
            }
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Failed to remove 4 added scopes, cannot remove from empty list.");
            errors++;
        }

        // Try removing again from the now empty list, should throw exception
        try{
            t.removeScope();
        }
        catch (EmptySymTableException ex){

        }

        ///////////// ADDING AND REMOVING SYMBOLS TO SCOPES /////////////

        // Make some symbols
        Sym x = new Sym("x");
        Sym y = new Sym("y");
        Sym a = new Sym("a");
        Sym b = new Sym("b");

        // Make sure symbol types match the type passed in and that toString works
        if(!x.getType().equals("x")){
            System.out.println("Error: Symbol type doesn't match type passed to constructor.");
            errors++;
        }
        if(!x.getType().equals(x.toString())){
            System.out.println("Error: Symbol type doesn't match toString() output.");
            errors++;
        }

        // Try adding a symbol to the first scope (when list is empty)
        try {
            t.addDecl("x", x);
            System.out.println("Error: Symbol was added to first scope but no scopes exist.");
            errors++;
        }
        catch (EmptySymTableException ex) {
        }
        catch (DuplicateSymException ex) {
            System.out.println("Error: Duplicate symbol exception when adding symbol to first scope (empty list)");
            errors++;
        }
        catch (WrongArgumentException ex){
            System.out.println("Error: Wrong argument exception when adding symbol to first scope (empty list)");
            errors++;
        }

        // Add a scope
        t.addScope();

        // Add a null symbol
        try{
            t.addDecl(null, null);
        }
        catch (DuplicateSymException ex){
            System.out.println("Error: Adding threw duplicate exception");
            errors++;
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Adding threw empty table exception");
            errors++;
        }
        catch (WrongArgumentException ex){
        }

        // Try adding a symbol to that first scope
        try {
            t.addDecl("x", x);
        }
        catch (EmptySymTableException ex) {
            System.out.println("Error: Empty table exception when adding symbol to first scope but list shouldn't be empty.");
            errors++;
        }
        catch (DuplicateSymException ex) {
            System.out.println("Error: Duplicate symbol exception when adding symbol to first scope but should be no dups.");
            errors++;
        }
        catch (WrongArgumentException ex){
            System.out.println("Error: Wrong argument exception when adding symbol to first scope but shouldn't be");
            errors++;
        }

        // Lookup local and global
        try{
            t.lookupLocal("x");
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Local lookup of x threw empty table exception");
            errors++;
        }
        try{
            t.lookupGlobal("x");
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Global lookup of x threw empty table exception");
            errors++;
        }

        // Lookup global of nonexisiting (should return null)
        try{
            if(t.lookupLocal("z") != null){
                System.out.println("Error: Didn't return null when looking for symbol that doesn't exist");
            }
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Global lookup of z threw empty table exception");
            errors++;
        }

        // Add duplicate
        try{
            t.addDecl("x", x);
        }
        catch (DuplicateSymException ex){

        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Adding duplicate to x threw empty table exception");
            errors++;
        }
        catch (WrongArgumentException ex){
            System.out.println("Error: Adding duplicate to x threw wrong argument exception");
            errors++;
        }

        // Add multiple non-duplicate
        try{
            t.addDecl("y", y);
            t.addDecl("a", a);
            t.addDecl("b", b);
        }
        catch (DuplicateSymException ex){
            System.out.println("Error: Adding non-duplicates threw duplicate exception");
            errors++;
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Adding non-duplicates threw empty table exception");
            errors++;
        }
        catch (WrongArgumentException ex){
            System.out.println("Error: Adding non-duplicates threw wrong argument exception");
            errors++;
        }

        // Add another scope
        t.addScope();

        // Add symbols to that scope
        try{
            t.addDecl("x", x);
            t.addDecl("y", y);
            t.addDecl("b", b);
        }
        catch (DuplicateSymException ex){
            System.out.println("Error: Adding non-duplicates threw duplicate exception");
            errors++;
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Adding non-duplicates threw empty table exception");
            errors++;
        }
        catch (WrongArgumentException ex){
            System.out.println("Error: Adding non-duplicates threw wrong argument exception");
            errors++;
        }

        // Lookup global vs local over scopes
        try{
            if(t.lookupGlobal("a") == null){
                System.out.println("Error: Global lookup didn't find a");
                errors++;
            }
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Global lookup threw empty table exception");
            errors++;
        }

        // Lookup local of one "a" (should ret null since "a" not in first hashmap)
        try{
            if(t.lookupLocal("a") != null){
                System.out.println("Error: Local lookup supposedly found a");
                errors++;
            }
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Local lookup threw empty table exception");
            errors++;
        }

        ///////////// PRINTING /////////////
        // First print multiple scopes, then remove one scope and print again, then remove another scope and print
        try{
            t.print();
            t.removeScope();
            t.print();
            t.removeScope();
            t.print();
        }
        catch (EmptySymTableException ex){
            System.out.println("Error: Removing scope threw empty table exception.");
            errors++;
        }

        // Print number of errors
        System.out.println("----- Errors: "+ errors + " -----");
    }
}