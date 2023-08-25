import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Carrot program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************





// **********************************************************************
// %%%ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);
    


    // this method can be used by the unparse methods to do indenting
    protected void addIndent(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) p.print(" ");
    }
    
    public boolean getAnalysisStatus(){
	    return ErrMsg.getAnalysisStatus();
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {

    SymTable t;

    public ProgramNode(DeclListNode L) {
        myDeclList = L;
        this.t = new SymTable();
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode myDeclList;

    public void nameAnalysis(){
        myDeclList.nameAnalysis(t);
    }

}
///////////////////////////////////////////////////////////////////////////

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;

    public void nameAnalysis(SymTable t){
        for(DeclNode d : myDecls){
            d.nameAnalysis(t);
        }
    }

}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;

    public void nameAnalysis(SymTable t){
        for(FormalDeclNode f : myFormals){
            f.nameAnalysis(t);
        }
    }
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void nameAnalysis(SymTable t){
        myDeclList.nameAnalysis(t);
        myStmtList.nameAnalysis(t);
    }
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;

    public void nameAnalysis(SymTable t){
        for (StmtNode s : myStmts) {
            s.nameAnalysis(t);
        }
    }
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;

    public void nameAnalysis(SymTable t){
        for (ExpNode e : myExps) {
            e.nameAnalysis(t);
        }
    }
}





// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    abstract public void nameAnalysis(SymTable symTab);
}

// Needs work
class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;

    public void nameAnalysis(SymTable t){
        // Check if type is void
        String type = myType.getMyType();
        String idName = myId.getMyStrVal();
        if(type.equals("void")){
            ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Non-function declared void");
        }
        // Check if variable already declared in current scope
        Sym idSymbol = t.lookupLocal(idName);
        if(idSymbol != null){
            ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Multiply declared identifier");
        }
        // Check if it's a struct
        if(myType.equals("struct")){
            // Make sure struct name already defined and of struct type
            String structName = ((StructNode)myType).getMyId().getMyStrVal();
            Sym sym = t.lookupGlobal(structName);
            if(sym == null || !sym.getType().equals("structDecl")){
                ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Invalid name of struct type");
            }
            else{
                // Otherwise, add to symbol table and link the ID
                try{
                    Sym toAdd =  new Sym(myType.getMyType());
                    t.addDecl(idName, toAdd);
                    myId.setMySymbol(toAdd);
                }
                catch (DuplicateSymException e){
                    System.out.println(e.getStackTrace());
                    System.exit(-1);
                }
                catch (EmptySymTableException e){
                    System.out.println(e.getStackTrace());
                    System.exit(-1);
                }
                catch (WrongArgumentException e){
                    System.out.println(e.getStackTrace());
                    System.exit(-1);
                }
            }
        }



    }
}

// Needs work
class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;

    public void nameAnalysis(SymTable t){
        // Make sure it's not already in this scope (local lookup)
        String idName = myId.getMyStrVal();
        Sym idSymbolInTable = t.lookupLocal(idName);
        if(idSymbolInTable != null){
            ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Multiply declared identifier");
        }
        try{
            // Add to the symbol table and link the ID
            Sym toAdd =  new Sym(myType.getMyType());
            t.addDecl(idName, toAdd);
            myId.setMySymbol(toAdd);

            // Add a new scope
            t.addScope();

            // Do the formals and inside of function
            myFormalsList.nameAnalysis(t);
            myBody.nameAnalysis(t);

            // Remove scope since done
            t.removeScope();
        }
        catch(DuplicateSymException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
        catch (EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
        catch (WrongArgumentException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
    }
}

// Needs work
class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;

    public void nameAnalysis(SymTable t){
        // Make sure it's not already in this scope (local lookup)
        String idName = myId.getMyStrVal();
        Sym idSymbolInTable = t.lookupLocal(idName);
        if(idSymbolInTable != null){
            ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Multiply declared identifier");
        }

        // Make sure the type isn't void
        if(myType.getMyType().equals("void")){
            ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Non-function declared void");
        }

        // Add to the symbol table and link the ID
        try{
            Sym toAdd =  new Sym(myType.getMyType());
            t.addDecl(idName, toAdd);
            myId.setMySymbol(toAdd);
        }
        catch (DuplicateSymException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
        catch (EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
        catch (WrongArgumentException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
    }
}

// Needs work
class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("};\n");

    }

    // 2 kids
    private IdNode myId;
    private DeclListNode myDeclList;

    public void nameAnalysis(SymTable t){
        // Identifier must not be used in scope
        String idName = myId.getMyStrVal();
        Sym sym = t.lookupLocal(idName);
        if(sym != null){
            ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Multiply declared identifier");
        }
        else{
            // Make a new symbol table for the struct and name analyze the fields according to that table
            SymTable s = new SymTable();
            myDeclList.nameAnalysis(s);
            try{
                // Add struct decl symbol to our original symbol table
                Sym structDeclSym = new Sym("structDecl");
                t.addDecl(idName, structDeclSym);
                myId.setMySymbol(structDeclSym);
            }
            catch (DuplicateSymException e){
                System.out.println(e.getStackTrace());
                System.exit(-1);
            }
            catch (EmptySymTableException e){
                System.out.println(e.getStackTrace());
                System.exit(-1);
            }
            catch (WrongArgumentException e){
                System.out.println(e.getStackTrace());
                System.exit(-1);
            }
        }

    }
}





// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    abstract String getMyType();
}

class IntNode extends TypeNode {
    private String myType;

    public IntNode(){this.myType = "int";}

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }

    public String getMyType(){
        return this.myType;
    }

}

class BoolNode extends TypeNode {
    private String myType;

    public BoolNode(){this.myType = "bool";}

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
    public String getMyType() {
        return this.myType;
    }
}

class VoidNode extends TypeNode {
    private String myType;
    public VoidNode() {this.myType = "void";}

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

    public String getMyType(){
        return this.myType;
    }
}

class StructNode extends TypeNode {

    private String myType;

    public StructNode(IdNode id) {
        myId = id;
        this.myType = "struct";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    public IdNode getMyId(){
        return this.myId;
    }

    public String getMyType(){
        return this.myType;
    }

    // 1 kid
    private IdNode myId;
}





// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    abstract public void nameAnalysis(SymTable symTab);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    // 1 kid
    private AssignNode myAssign;

    public void nameAnalysis(SymTable t){
        myAssign.nameAnalysis(t);
    }
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    // 1 kid
    private ExpNode myExp;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
    }
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    // 1 kid
    private ExpNode myExp;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
    }
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
    }
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
    }
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void nameAnalysis(SymTable t){
        // If expression
        myExp.nameAnalysis(t);
        // Then
        t.addScope();
        myDeclList.nameAnalysis(t);
        myStmtList.nameAnalysis(t);
        try{
            t.removeScope();
        }
        catch(EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
    }
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
        addIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");        
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;

    public void nameAnalysis(SymTable t){
        // If expression
        myExp.nameAnalysis(t);
        // Then
        t.addScope();
        myThenDeclList.nameAnalysis(t);
        myThenStmtList.nameAnalysis(t);
        try{
            t.removeScope();
        }
        catch(EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
        // Else
        t.addScope();
        myElseDeclList.nameAnalysis(t);
        myElseStmtList.nameAnalysis(t);
        try{
            t.removeScope();
        }
        catch(EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
    }
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
    
    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
        t.addScope();
        myDeclList.nameAnalysis(t);
        myStmtList.nameAnalysis(t);
        try{
            t.removeScope();
        }
        catch(EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
    }
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
	addIndent(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
        t.addScope();
        myDeclList.nameAnalysis(t);
        myStmtList.nameAnalysis(t);
        try{
            t.removeScope();
        }
        catch(EmptySymTableException e){
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
    }
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    // 1 kid
    private CallExpNode myCall;

    public void nameAnalysis(SymTable t) {
        myCall.nameAnalysis(t);
    }
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp; // possibly null

    public void nameAnalysis(SymTable t) {
        // Since it may be null
        if (myExp != null) {
            myExp.nameAnalysis(t);
        }
    }
}





// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    public void nameAnalysis(SymTable t) { }
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if(mySymbol != null){
            p.print("(" + mySymbol + ")");
        }
    }

    public Sym getMySymbol(){
        return this.mySymbol;
    }

    public String getMyStrVal(){return this.myStrVal;}

    public int getMyLineNum(){return this.myLineNum;}

    public int getMyCharNum(){return this.myCharNum;}

    public void setMySymbol(Sym mySymbol){this.mySymbol = mySymbol;}

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private Sym mySymbol;

    public void nameAnalysis(SymTable t){
        // Try to find the symbol in any scope
        Sym sym = t.lookupGlobal(myStrVal);
        // Id has been declared, so link this id to it
        if (sym != null) {
            mySymbol = sym;
        }
        // Use of an undeclared identifier
        else{
            ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
        }
    }
}

// Needs work
class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;    
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        myId.unparse(p, 0);
    }

    public Sym getMySymbol() {
        return this.mySymbol;
    }

    public int getMyLineNum(){
        return this.myLineNum;
    }

    public int getMyCharNum(){
        return this.myCharNum;
    }

    // 2 kids
    private ExpNode myLoc;    
    private IdNode myId;
    private Sym mySymbol;
    private int myLineNum;
    private int myCharNum;


    public void nameAnalysis(SymTable t){
        // Make sure myLoc is name analyzed
        myLoc.nameAnalysis(t);

        // If it's of the form id
        if(myLoc instanceof IdNode){
            // Convert to IdNode and get its symbol
            IdNode id = (IdNode)myLoc;
            Sym sym = id.getMySymbol();

            // Look in symbol table and make sure it corresponds to a struct
            if(!sym.getType().equals("struct") || !sym.getType().equals("structDecl")){
                ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Dot-access of non-struct type");
            }
        }
        // Otherwise it's of form loc.id
        else if(myLoc instanceof DotAccessExpNode){
            // Convert get its symbol
            DotAccessExpNode loc = (DotAccessExpNode)myLoc;
            Sym sym = loc.getMySymbol();
            if(sym == null){
                ErrMsg.fatal(loc.getMyLineNum(), loc.getMyCharNum(),"Dot-access of non-struct type");
            }
            // Look in symbol table and make sure it corresponds to a struct
            if(!sym.getType().equals("struct") || !sym.getType().equals("structDecl")){
                ErrMsg.fatal(loc.getMyLineNum(), loc.getMyCharNum(), "Dot-access of non-struct type");
            }
            // Get symbol table of struct
            else{


            }
        }
        // Neither, must be invalid then
        else{
            System.err.println("Unexpected node type in LHS of dot-access");
            System.exit(-1);
        }

//        // Do RHS
//        sym = structSymTab.lookupGlobal(myId.name()); // lookup
//        if (sym == null) { // not found - RHS is not a valid field name
//            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
//                    "Invalid struct field name");
//            badAccess = true;
//        }
//
//        else {
//            myId.link(sym);  // link the symbol
//            // if RHS is itself as struct type, link the symbol for its struct
//            // type to this dot-access node (to allow chained dot-access)
//            if (sym instanceof StructSym) {
//                mySym = ((StructSym)sym).getStructType().sym();
//            }
//        }
    }
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myExp;

    public void nameAnalysis(SymTable t){
        myLhs.nameAnalysis(t);
        myExp.nameAnalysis(t);
    }
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null

    public void nameAnalysis(SymTable t){
        myId.nameAnalysis(t);
        myExpList.nameAnalysis(t);
    }
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    // one child
    protected ExpNode myExp;

    public void nameAnalysis(SymTable t){
        myExp.nameAnalysis(t);
    }
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;

    public void nameAnalysis(SymTable t){
        myExp1.nameAnalysis(t);
        myExp2.nameAnalysis(t);
    }
}





// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
}





// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}
