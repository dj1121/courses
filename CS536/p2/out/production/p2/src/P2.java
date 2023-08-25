import java.util.*;
import java.io.*;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java_cup.runtime.*;  // defines Symbol
import jdk.nashorn.internal.parser.Token;

/**
 * This program is to be used to test the Carrot scanner.
 * This version is set up to test all tokens, but more code is needed to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens).
 */
public class P2 {
    public static void main(String[] args) throws IOException {

        // Test tokens like int, true, false, etc.
        testAllTokens();
        CharNum.num = 1;

        // Should produce errors and only output good values
        testStrings();
        CharNum.num = 1;

        // Should produce errors and only output good values
        testIntegers();
        CharNum.num = 1;

        // Test line counting, write line and char number of each token (mix of good and bad and comments)
        testCounting();
        CharNum.num = 1;

    }

    private static void testCounting() throws IOException{
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("counts.in");
            outFile = new PrintWriter(new FileWriter("counts.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("In file not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("Out file cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            if(my_token.sym == sym.INTLITERAL){
                outFile.print(((TokenVal)my_token.value).linenum + ":" + ((TokenVal)my_token.value).charnum + " ");
                outFile.println(((IntLitTokenVal)my_token.value).intVal);
            }
            else if(my_token.sym == sym.STRINGLITERAL){
                outFile.print(((TokenVal)my_token.value).linenum + ":" + ((TokenVal)my_token.value).charnum + " ");
                outFile.println(((StrLitTokenVal)my_token.value).strVal);
            }
            else if(my_token.sym == sym.BOOL) {
                outFile.print(((TokenVal)my_token.value).linenum + ":" + ((TokenVal)my_token.value).charnum + " ");
                outFile.println("bool");
            }
            else if(my_token.sym == sym.INT) {
                outFile.print(((TokenVal)my_token.value).linenum + ":" + ((TokenVal)my_token.value).charnum + " ");
                outFile.println("int");
            }
            my_token = my_scanner.next_token();
        }
        outFile.close();
    }

    private static void testIntegers() throws IOException{
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("ints.in");
            outFile = new PrintWriter(new FileWriter("ints.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("In file not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("Out file cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            if(my_token.sym == sym.INTLITERAL){
                outFile.println(((IntLitTokenVal)my_token.value).intVal);
            }
            my_token = my_scanner.next_token();
        }
        outFile.close();
    }

    private static void testStrings() throws IOException{
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("strings.in");
            outFile = new PrintWriter(new FileWriter("strings.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("In file not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("Out file cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            if(my_token.sym == sym.STRINGLITERAL){
                outFile.println(((StrLitTokenVal)my_token.value).strVal);
            }
            my_token = my_scanner.next_token();
        }
        outFile.close();
    }

    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("In file not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("Out file cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            switch (my_token.sym) {
            case sym.BOOL:
                outFile.println("bool");
                break;
			case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true");
                break;
            case sym.FALSE:
                outFile.println("false");
                break;
            case sym.STRUCT:
                outFile.println("struct");
                break;
            case sym.CIN:
                outFile.println("cin");
                break;
            case sym.COUT:
                outFile.println("cout");
                break;
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)my_token.value).idVal);
                break;
            case sym.INTLITERAL:
                outFile.println(((IntLitTokenVal)my_token.value).intVal);
                break;
            case sym.STRINGLITERAL:
                outFile.println(((StrLitTokenVal)my_token.value).strVal);
                break;
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
			case sym.ASSIGN:
                outFile.println("=");
                break;
			default:
				outFile.println("UNKNOWN TOKEN");
            } // end switch

            my_token = my_scanner.next_token();
        } // end while
        outFile.close();
    }
}
