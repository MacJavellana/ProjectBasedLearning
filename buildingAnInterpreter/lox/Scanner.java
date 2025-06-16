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

    private static final Map<String, TokenType> keywords;
    static{
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }
    


    Scanner(String source){
        this.source = source;
    }


    //add token until it runs out of character. adds EOF token at the end
    List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }
    private void scanToken(){
        char c = advance();
        switch(c){
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '/':
                if(match('/')){
                    while(peek() != '\n' && !isAtEnd()) advance();
                }else{
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if(isDigit(c)){
                    number();
                }else if(isAlpha(c)){
                    identifier();
                }
                else{
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier(){
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }
    //main function: consume if we know we are dealing with integers/floats
    private void number(){
        while(isDigit(peek())) advance(); //consume integer part of the literal

        //consume fractional part, which is a decimal point followed by at least one digit
        if (peek() == '.' && isDigit(peekNext())){
            advance();

            while(isDigit(peek())) advance();
        }

        //users java's Double type to represent numbers so we produce a value of that type.
        addToken(TokenType.NUMBER, 
            Double.parseDouble(source.substring(start,current)));
    }

    private void string(){
        while(peek() != '"' && !isAtEnd() ){
            if(peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()){
            Lox.error(line, "Unterminated string");
            return;
        }

        // the closing "
        advance();

        //trim the surrounding quotes
        String value = source.substring(start +1, current -1);
        addToken(TokenType.STRING, value);
    }
    //determine if we're on a '!=' or merely a '!'. determine if we are on a '==' or merely a '=';
    //we need to recognize these lexemes in two stages .
    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    //like the advance() function but doesn't consume the character which we call lookahead
    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }


    //Helper function: check first if after the decimal is a digit
    private char peekNext(){
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current+ 1);
    }
    private boolean isAlpha(char c){
        return (c >= 'a' && c <='z')||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }

    //Helper to check if its a number
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }
    
    

    //consume next character in source file and returns it
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
