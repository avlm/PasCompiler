package compiler;

import parser.Parser;
import parser.SyntacticException;
import scanner.LexicalException;
import util.AST.AST;
import util.symbolsTable.IdentificationTable;

/**
 * Compiler driver
 * @version 2010-september-04
 * @discipline Compiladores
 * @author Gustavo H P Carvalho
 * @email gustavohpcarvalho@ecomp.poli.br
 */
public class Compiler {
	
	// Compiler identification table
	public static IdentificationTable identificationTable = null;

	/**
	 * Compiler start point
	 * @param args - none
	 */
	public static void main(String[] args) {
		// Creates the parser object
		Parser p = null;
		// Creates the AST object
		AST ast = null;
		
		try {
			p = new Parser();
			ast = p.parse();
			
			System.out.println("\n-- AST STRUCTURE --");
			if ( ast != null ) {
				System.out.println(ast.toString(0));
			}
		} catch (SyntacticException e) {
			e.printStackTrace();
		} catch (LexicalException e) {
			e.printStackTrace();
		}
	}
	
}
