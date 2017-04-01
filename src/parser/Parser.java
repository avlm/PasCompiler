package parser;

import scanner.LexicalException;
import scanner.Scanner;
import scanner.Token;
import util.AST.AST;

/**
 * Parser class
 * @version 2010-august-29
 * @discipline Projeto de Compiladores
 * @author Gustavo H P Carvalho
 * @email gustavohpcarvalho@ecomp.poli.br
 */
public class Parser {

	// The current token
	private Token currentToken = null;
	// The scanner
	private Scanner scanner = null;
	
	/**
	 * Parser constructor
	 * @throws LexicalException 
	 */
	public Parser() throws LexicalException {
		// Initializes the scanner object
		this.scanner = new Scanner();
		this.currentToken = this.scanner.getNextToken();
	}
	
	/**
	 * Veririfes if the current token kind is the expected one
	 * @param kind
	 * @throws SyntacticException
	 * @throws LexicalException 
	 */
	private void accept(GrammarSymbols kind) throws SyntacticException, LexicalException {
		if (this.currentToken.getKind() == kind) {
			this.acceptIt();
		} else {
			throw new SyntacticException("Syntactic error: expecting " + kind + ", but found " + this.currentToken.getKind(), this.currentToken);
		}
	}
	
	/**
	 * Gets next token
	 * @throws LexicalException 
	 */
	private void acceptIt() throws LexicalException {
		this.currentToken = this.scanner.getNextToken();
	}

	/**
	 * Verifies if the source program is syntactically correct
	 * @throws SyntacticException
	 * @throws LexicalException 
	 */
	public AST parse() throws SyntacticException, LexicalException {
		this.parseProgram();
		accept(GrammarSymbols.EOT);
		
		return null;
	}

	public void parseProgram() throws SyntacticException, LexicalException {
		while (this.currentToken.getKind() != GrammarSymbols.EOT) {
			if (this.currentToken.getKind() == GrammarSymbols.VAR) {
				parseVariableDeclaration();
				accept(GrammarSymbols.SEMICOLON);
			} else {
				parseFunctionDeclaration();
			}
		}
	}

	private void parseVariableDeclaration() throws SyntacticException, LexicalException {
		accept(GrammarSymbols.VAR);
		accept(GrammarSymbols.INT);
		accept(GrammarSymbols.DOUBLE_COLON);
		accept(GrammarSymbols.ID);
	}

	private void parseFunctionDeclaration() throws SyntacticException, LexicalException {
		accept(GrammarSymbols.ID);
		accept(GrammarSymbols.DOUBLE_COLON);
		accept(GrammarSymbols.LP);
		if (this.currentToken.getKind() != GrammarSymbols.RP) {
			parseParemetersList();
		}
		accept(GrammarSymbols.RP);
		accept(GrammarSymbols.ARROW);
		accept(GrammarSymbols.INT);
		accept(GrammarSymbols.AT);
		while (this.currentToken.getKind() != GrammarSymbols.ENDFUN) {
			parseCommands();
		}
		acceptIt();
	}

	private void parseParemetersList() throws SyntacticException, LexicalException {
		parseVariableDeclaration();
		while (this.currentToken.getKind() == GrammarSymbols.COMMA) {
			acceptIt();
			parseVariableDeclaration();
		}
	}
	
	private void parseCommands() throws SyntacticException, LexicalException {
		if (this.currentToken.getKind() == GrammarSymbols.VAR) {
			parseVariableDeclaration();
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.RUN) {
			acceptIt();
			accept(GrammarSymbols.ID);
			accept(GrammarSymbols.LP);
			if (this.currentToken.getKind() != GrammarSymbols.RP) {
				parseListArguments();
			}
			accept(GrammarSymbols.RP);
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.ASSIGN) {
			acceptIt();
			parseArithmeticExpression();
			accept(GrammarSymbols.TO);
			accept(GrammarSymbols.ID);
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.RETURN) {
			acceptIt();
			parseArithmeticExpression();
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.LOOP) {
			acceptIt();
			accept(GrammarSymbols.LP);
			parseExpression();
			accept(GrammarSymbols.RP);
			accept(GrammarSymbols.AT);
			while (this.currentToken.getKind() != GrammarSymbols.ENDLOOP) {
				parseCommands();
			}
			acceptIt();
		} else {
			accept(GrammarSymbols.SHOW);
			accept(GrammarSymbols.LP);
			parseArithmeticExpression();
			accept(GrammarSymbols.RP);
			accept(GrammarSymbols.SEMICOLON);
		}
	}
	
	private void parseExpression() throws SyntacticException, LexicalException {
		parseArithmeticExpression();
		if (this.currentToken.getKind() == GrammarSymbols.EQUALS) {
			acceptIt();
		} else {
			accept(GrammarSymbols.NEQUALS);
		}
		parseArithmeticExpression();
	}	

	private void parseArithmeticExpression() throws SyntacticException, LexicalException {
		parseTermExpression();
		while (this.currentToken.getKind() == GrammarSymbols.ADD) {
			acceptIt();
			parseTermExpression();
		}
	}	

	private void parseTermExpression() throws SyntacticException, LexicalException {
		parseFactorExpression();
		while (this.currentToken.getKind() == GrammarSymbols.MUL) {
			acceptIt();
			parseFactorExpression();
		}
	}
	
	private void parseFactorExpression() throws SyntacticException, LexicalException {
		if (this.currentToken.getKind() == GrammarSymbols.ID) {
			acceptIt();
		} else if (this.currentToken.getKind() == GrammarSymbols.NUM) {
			acceptIt();
		} else if (this.currentToken.getKind() == GrammarSymbols.LP) {
			acceptIt();
			parseArithmeticExpression();
			accept(GrammarSymbols.RP);
		} else {
			accept(GrammarSymbols.RUN);
			accept(GrammarSymbols.ID);
			accept(GrammarSymbols.LP);
			if (this.currentToken.getKind() != GrammarSymbols.RP) {
				parseListArguments();
			}
			accept(GrammarSymbols.RP);
		}
	}

	private void parseListArguments() throws SyntacticException, LexicalException {
		parseArithmeticExpression();
		while (this.currentToken.getKind() == GrammarSymbols.COMMA) {
			acceptIt();
			parseArithmeticExpression();
		}
	}	
	
}
