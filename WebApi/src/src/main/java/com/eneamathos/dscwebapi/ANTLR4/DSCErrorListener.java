package com.eneamathos.dscwebapi.ANTLR4;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DSCErrorListener extends BaseErrorListener {

    private final String offendingSymbolClassName = "org.antlr.v4.runtime.CommonToken";

    private RecognitionException exception;
    private ValidationResult result = new ValidationResult();
    private List<ValidationResult> resultList = new ArrayList<ValidationResult>();


    public DSCErrorListener()
    {

    }

    public ValidationResult getValidationResult() {
        return result;
    }

    public List<ValidationResult> getValidationResultList() {
        return resultList;
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e)
    {
        ValidationResult res = new ValidationResult();
        res.setIsValid(false);
        res.setMessage(msg);
        res.setLine(line);
        res.setCharPositionInLine(charPositionInLine);

        if(offendingSymbol.getClass().getName() == offendingSymbolClassName)
        {
            CommonToken token = (CommonToken) offendingSymbol;
            res.setHint(token.getText());
        }
        resultList.add(res);
        exception = e;
    };
}
