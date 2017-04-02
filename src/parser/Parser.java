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
	 * Verifies if the current token kind is the expected one
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
		accept(GrammarSymbols.EOF);
		
		return null;
	}

	public void parseProgram() throws SyntacticException, LexicalException {
		accept(GrammarSymbols.PROGRAM);
		accept(GrammarSymbols.ID);
		accept(GrammarSymbols.SEMICOLON);
		
		if (this.currentToken.getKind() == GrammarSymbols.VAR) {
			acceptIt();
			parseVarDec();
			accept(GrammarSymbols.SEMICOLON);
			while (this.currentToken.getKind() != GrammarSymbols.FUNCTION && 
					this.currentToken.getKind() != GrammarSymbols.PROCEDURE && 
					this.currentToken.getKind() != GrammarSymbols.BEGIN) {
				parseVarDec();
				accept(GrammarSymbols.SEMICOLON);
			}
		}
		
		while (this.currentToken.getKind() == GrammarSymbols.FUNCTION) {
			parseFuncDec();
		}
		
		while (this.currentToken.getKind() == GrammarSymbols.PROCEDURE) {
			parseProcDec();
		}
		
		accept(GrammarSymbols.BEGIN);
		
		while (this.currentToken.getKind() != GrammarSymbols.END) {
			parseCmd();
		}
		
		accept(GrammarSymbols.END);
		accept(GrammarSymbols.DOT);
	}

	private void parseVarDec() throws SyntacticException, LexicalException {
		accept(GrammarSymbols.ID);
		
		while (this.currentToken.getKind() == GrammarSymbols.COMMA) {
			acceptIt();
			accept(GrammarSymbols.ID);
		}
		
		accept(GrammarSymbols.COLON);
		
		if (this.currentToken.getKind() == GrammarSymbols.INTEGER) {
			acceptIt();
		} else {
			accept(GrammarSymbols.BOOLEAN);
		}
	}

	private void parseFuncDec() throws SyntacticException, LexicalException {
		accept(GrammarSymbols.FUNCTION);
		accept(GrammarSymbols.ID);
		accept(GrammarSymbols.LP);
		
		if (this.currentToken.getKind() != GrammarSymbols.RP) {
			parseParLst();
		}
		
		accept(GrammarSymbols.RP);
		accept(GrammarSymbols.COLON);
		
		if (this.currentToken.getKind() == GrammarSymbols.INTEGER) {
			acceptIt();
		} else {
			accept(GrammarSymbols.BOOLEAN);
		}
		
		accept(GrammarSymbols.SEMICOLON);
		
		if (this.currentToken.getKind() == GrammarSymbols.VAR) {
			acceptIt();
			parseVarDec();
			accept(GrammarSymbols.SEMICOLON);
			while (this.currentToken.getKind() != GrammarSymbols.BEGIN) {
				parseVarDec();
				accept(GrammarSymbols.SEMICOLON);
			}
		}
		
		accept(GrammarSymbols.BEGIN);

		while (this.currentToken.getKind() != GrammarSymbols.END) {
			parseCmd();
		}

		accept(GrammarSymbols.END);
		accept(GrammarSymbols.SEMICOLON);
	}
	
	private void parseProcDec() throws SyntacticException, LexicalException {
		accept(GrammarSymbols.PROCEDURE);
		accept(GrammarSymbols.ID);
		accept(GrammarSymbols.LP);
		
		if (this.currentToken.getKind() != GrammarSymbols.RP) {
			parseParLst();
		}
		
		accept(GrammarSymbols.RP);
		accept(GrammarSymbols.SEMICOLON);
		
		if (this.currentToken.getKind() == GrammarSymbols.VAR) {
			acceptIt();
			parseVarDec();
			accept(GrammarSymbols.SEMICOLON);
			while (this.currentToken.getKind() != GrammarSymbols.BEGIN) {
				parseVarDec();
				accept(GrammarSymbols.SEMICOLON);
			}
		}
		
		accept(GrammarSymbols.BEGIN);

		while (this.currentToken.getKind() != GrammarSymbols.END) {
			parseCmd();
		}

		accept(GrammarSymbols.END);
		accept(GrammarSymbols.SEMICOLON);
	}

	private void parseParLst() throws SyntacticException, LexicalException {
		parseVarDec();
		while (this.currentToken.getKind() == GrammarSymbols.SEMICOLON) {
			acceptIt();
			parseVarDec();
		}
	}
	
	private void parseCmd() throws SyntacticException, LexicalException {
		if (this.currentToken.getKind() == GrammarSymbols.ID) {
			acceptIt();
			if (this.currentToken.getKind() == GrammarSymbols.ATTR) {
				acceptIt();
				parseBexp();
			} else {
				accept(GrammarSymbols.LP);
				parseArgLst();
				accept(GrammarSymbols.RP);
			}
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.IF) {
			acceptIt();
			parseBexp();
			accept(GrammarSymbols.THEN);
			accept(GrammarSymbols.BEGIN);
			while (this.currentToken.getKind() != GrammarSymbols.END) {
				parseCmd();
			}
			accept(GrammarSymbols.END);
			accept(GrammarSymbols.SEMICOLON);
			if (this.currentToken.getKind() == GrammarSymbols.ELSE) {
				acceptIt();
				accept(GrammarSymbols.BEGIN);
				while (this.currentToken.getKind() != GrammarSymbols.END) {
					parseCmd();
				}
				accept(GrammarSymbols.END);
				accept(GrammarSymbols.SEMICOLON);
			}
		} else if (this.currentToken.getKind() == GrammarSymbols.WHILE) {
			acceptIt();
			parseBexp();
			accept(GrammarSymbols.DO);
			accept(GrammarSymbols.BEGIN);
			while (this.currentToken.getKind() != GrammarSymbols.END) {
				parseCmd();
			}
			accept(GrammarSymbols.END);
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.WRITE) {
			acceptIt();
			accept(GrammarSymbols.LP);
			parseBexp();
			accept(GrammarSymbols.RP);
			accept(GrammarSymbols.SEMICOLON);
		} else if (this.currentToken.getKind() == GrammarSymbols.BREAK) {
			acceptIt();
			accept(GrammarSymbols.SEMICOLON);
		} else {
			accept(GrammarSymbols.CONTINUE);
			accept(GrammarSymbols.SEMICOLON);
		}
	}
	
	private void parseBexp() throws SyntacticException, LexicalException {
		parseAexp();
		if (this.currentToken.getKind() == GrammarSymbols.EQUALS ||
			this.currentToken.getKind() == GrammarSymbols.NOTEQUALS ||
			this.currentToken.getKind() == GrammarSymbols.GT ||
			this.currentToken.getKind() == GrammarSymbols.LT ||
			this.currentToken.getKind() == GrammarSymbols.GE ||
			this.currentToken.getKind() == GrammarSymbols.LE) {
			acceptIt();
			parseAexp();
		}
	}	

	private void parseAexp() throws SyntacticException, LexicalException {
		parseTerm();
		while (this.currentToken.getKind() == GrammarSymbols.ADD ||
				this.currentToken.getKind() == GrammarSymbols.SUB) {
			acceptIt();
			parseTerm();
		}
	}	

	private void parseTerm() throws SyntacticException, LexicalException {
		parseFactor();
		while (this.currentToken.getKind() == GrammarSymbols.MUL ||
				this.currentToken.getKind() == GrammarSymbols.DIV) {
			acceptIt();
			parseFactor();
		}
	}
	
	private void parseFactor() throws SyntacticException, LexicalException {
		if (this.currentToken.getKind() == GrammarSymbols.ID) {
			acceptIt();
			if (this.currentToken.getKind() == GrammarSymbols.LP) {
				acceptIt();
				if (this.currentToken.getKind() != GrammarSymbols.RP) {
					parseArgLst();
					accept(GrammarSymbols.RP);
				} else {
					acceptIt();
				}
			}
		} else if (this.currentToken.getKind() == GrammarSymbols.NUM) {
			acceptIt();
		} else if (this.currentToken.getKind() == GrammarSymbols.TRUE) {
			acceptIt();
		} else if (this.currentToken.getKind() == GrammarSymbols.FALSE) {
			acceptIt();
		} else {
			accept(GrammarSymbols.LP);
			parseBexp();
			accept(GrammarSymbols.RP);
		}
	}

	private void parseArgLst() throws SyntacticException, LexicalException {
		parseBexp();
		while (this.currentToken.getKind() == GrammarSymbols.COMMA) {
			acceptIt();
			parseBexp();
		}
	}	
	
}
