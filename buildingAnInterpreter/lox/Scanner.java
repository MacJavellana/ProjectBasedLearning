package lox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lox.TokenType;
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0; // points at the first character in the lexeme being scanned
    private int current = 0; //points at character currently being considered
    private int line =1; //tracks what source line current is on
    Scanner(String source){
        this.source = source;
    }


    //add token until it runs out of character. adds EOF token at the end
    List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
    private void scanToken(){
        char c = advance();
        switch(c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
            default:
                Lox.error(line, "Unexpected character.");
                break;
        }
    }
    private char advance(){
        return source.charAt(current++);
    }
    private void addToken(TokenType type){
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text,literal, line));
    }
    private boolean isAtEnd(){
        return current >= source.length();
    }

}
