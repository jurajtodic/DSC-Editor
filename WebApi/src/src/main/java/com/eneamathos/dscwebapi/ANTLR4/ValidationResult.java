package com.eneamathos.dscwebapi.ANTLR4;

public class ValidationResult {
    private String hint = "";
    private int line = -1;
    private int charPositionInLine = -1;
    private String message = "";
    private boolean isValid = true;

    public void setHint(String _symbol) {
        this.hint = _symbol;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setCharPositionInLine(int charPositionInLine) {
        this.charPositionInLine = charPositionInLine;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIsValid(boolean succeeded) {
        this.isValid = succeeded;
    }

    public String getHint() {
        return hint;
    }

    public int getLine() {
        return line;
    }

    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsValid() {
        return isValid;
    }
}
