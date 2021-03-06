package scanner;

import java.util.HashMap;
import java.util.Map;

import compiler.Properties;
import parser.GrammarSymbols;
import util.Arquivo;

/**
 * Scanner class
 * @version 2010-september-04
 * @discipline Compiladores
 * @author Gustavo H P Carvalho
 * @email gustavohpcarvalho@ecomp.poli.br
 */
public class Scanner {

	// The file object that will be used to read the source code
	private Arquivo file;
	// The last char read from the source code
	private char currentChar;
	// The kind of the current token
	private GrammarSymbols currentKind;
	// Buffer to append characters read from file
	private StringBuffer currentSpelling;
	// Current line and column in the source file
	private int line, column;
	// Keywords
	private Map<String,GrammarSymbols> keywords;
	
	/**
	 * Default constructor
	 */
	public Scanner() {
		this.file = new Arquivo(Properties.sourceCodeLocation);		
		this.line = 0;
		this.column = 0;
		this.currentChar = this.file.readChar();
		this.currentSpelling = new StringBuffer();
		
		keywords = new HashMap<String,GrammarSymbols>();
		keywords.put("program", GrammarSymbols.PROGRAM);
		keywords.put("var", GrammarSymbols.VAR);
		keywords.put("function", GrammarSymbols.FUNCTION);
		keywords.put("procedure", GrammarSymbols.PROCEDURE);
		keywords.put("true", GrammarSymbols.TRUE);
		keywords.put("false", GrammarSymbols.FALSE);
		keywords.put("begin", GrammarSymbols.BEGIN);
		keywords.put("end", GrammarSymbols.END);
		keywords.put("if", GrammarSymbols.IF);
		keywords.put("then", GrammarSymbols.THEN);
		keywords.put("else", GrammarSymbols.ELSE);
		keywords.put("while", GrammarSymbols.WHILE);
		keywords.put("do", GrammarSymbols.DO);
		keywords.put("break", GrammarSymbols.BREAK);
		keywords.put("continue", GrammarSymbols.CONTINUE);
		keywords.put("write", GrammarSymbols.WRITE);
		keywords.put("boolean", GrammarSymbols.BOOLEAN);
		keywords.put("integer", GrammarSymbols.INTEGER);
	}
	
	/**
	 * Returns the next token
	 * @return
	 * @throws LexicalException
	 */
	public Token getNextToken() throws LexicalException {
		while (this.isSeparator(this.currentChar)) {
			this.scanSeparator();
		}
		
		currentSpelling.delete(0, currentSpelling.length());
		GrammarSymbols kind = this.scanToken();
		
		return new Token(kind,
				currentSpelling.toString(),
				line, column);
	}
	
	/**
	 * Returns if a character is a separator
	 * @param c
	 * @return
	 */
	private boolean isSeparator(char c) {
		if ( c == '#' || c == ' ' || c == '\n' || c == '\t' ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reads (and ignores) a separator
	 * @throws LexicalException
	 */
	private void scanSeparator() {
		if ( this.currentChar == '#' ) {
			while (this.currentChar != '\n') {
				this.getNextChar();
			}
		} else {
			this.getNextChar();
		}
	}
	
	/**
	 * Gets the next char
	 */
	private void getNextChar() {
		// Appends the current char to the string buffer
		this.currentSpelling.append(this.currentChar);
		// Reads the next one
		this.currentChar = this.file.readChar();
		// Increments the line and column
		this.incrementLineColumn();
	}
	
	/**
	 * Increments line and column
	 */
	private void incrementLineColumn() {
		// If the char read is a '\n', increments the line variable and assigns 0 to the column
		if ( this.currentChar == '\n' ) {
			this.line++;
			this.column = 0;
		// If the char read is not a '\n' 
		} else {
			// If it is a '\t', increments the column by 4
			if ( this.currentChar == '\t' ) {
				this.column = this.column + 4;
			// If it is not a '\t', increments the column by 1
			} else {
				this.column++;
			}
		}
	}
	
	/**
	 * Returns if a char is a digit (between 0 and 9)
	 * @param c
	 * @return
	 */
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	/**
	 * Returns if a char is a letter (between a and z or between A and Z)
	 * @param c
	 * @return
	 */
	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * Scans the next token
	 * Simulates the DFA that recognizes the language described by the lexical grammar
	 * @return
	 * @throws LexicalException
	 */
	private GrammarSymbols scanToken() throws LexicalException {
		int estado = 0;
		
		while (true) {
			switch(estado) {
			case 0:
				if (currentChar == ';') {
					estado = 1;
				} else if (currentChar == ':' ) {
					estado = 2;
				} else if (isLetter(currentChar)) {
					estado = 3;
				} else if (currentChar == '(') {
					estado = 4;
				} else if (currentChar == ')') {
					estado = 5;
				} else if (currentChar == '-') {
					estado = 6;
				} else if (currentChar == '/') {
					estado = 7;
				} else if (currentChar == ',') {
					estado = 8;
				} else if (currentChar == '=') {
					estado = 9;
				} else if (currentChar == '+') {
					estado = 10;
				} else if (currentChar == '*') {
					estado = 11;
				} else if (isDigit(currentChar)) {
					estado = 12;
				} else if (currentChar == '\000') {
					estado = 13;
					break;
				} else if (currentChar == '.') {
					estado = 17;
				} else if (currentChar == '<') {
					estado = 16;
				} else if (currentChar == '>') {
					estado = 18;
				} else {
					estado = 14;
					break;
				}
				getNextChar();
				break;
				
			case 1:
				return GrammarSymbols.SEMICOLON;
			
			case 2:
				if (currentChar == '=') {
					estado = 15;
					getNextChar();
				} else {
					return GrammarSymbols.COLON;
				}
				break;
				
			case 3:
				while (isLetter(currentChar) || isDigit(currentChar)) {
					getNextChar();
				}
				
				if (keywords.containsKey(currentSpelling.toString())) {
					return keywords.get(currentSpelling.toString());
				} else {
					return GrammarSymbols.ID;
				}

			case 4:
				return GrammarSymbols.LP;
				
			case 5:
				return GrammarSymbols.RP;
				
			case 6:
				return GrammarSymbols.SUB;

			case 7:
				return GrammarSymbols.DIV;
				
			case 8:
				return GrammarSymbols.COMMA;
				
			case 9:
				return GrammarSymbols.EQUALS;
				
			case 10:
				return GrammarSymbols.ADD;
				
			case 11:
				return GrammarSymbols.MUL;
				
			case 12:
				while (isDigit(currentChar)) {
					getNextChar();
				}
				
				return GrammarSymbols.NUM;

			case 13:
				return GrammarSymbols.EOF;
			
			case 14:
				throw new LexicalException(
						"I found a lexical error!",
						this.currentChar, this.line, this.column);
			
			case 15:
				return GrammarSymbols.ATTR;
				
			case 16:
				if (currentChar == '=') {
					estado = 19;
					getNextChar();
				} else if(currentChar == '>') {
					estado = 20;
					getNextChar();
				} else {
					return GrammarSymbols.LT;
				}
				break;
				
			case 17:
				return GrammarSymbols.DOT;

			case 18:
				if (currentChar == '=') {
					estado = 21;
					getNextChar();
				} else {
					return GrammarSymbols.GT;
				}
				break;

			case 19:
				return GrammarSymbols.LE;

			case 20:
				return GrammarSymbols.NOTEQUALS;

			case 21:
				return GrammarSymbols.GE;

			}
		}
	}
	
	
}
