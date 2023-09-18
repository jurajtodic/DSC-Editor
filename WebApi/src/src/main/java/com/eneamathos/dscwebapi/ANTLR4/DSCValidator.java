package com.eneamathos.dscwebapi.ANTLR4;

import com.eneamathos.dscwebapi.DSCLexer;
import com.eneamathos.dscwebapi.DSCParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DSCValidator {

    private DSCLexer _lexer;
    private DSCParser _parser;
    private CommonTokenStream _tokens;
    private DSCErrorListener _errorListener;

    public List<ValidationResult> validate(byte[] bytes) throws IOException {

        init(bytes);

        DSCParser.DscContext tree = _parser.dsc();
        com.eneamathos.dscwebapi.DSCListener listener = new com.eneamathos.dscwebapi.ANTLR4.DSCListener();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        return _errorListener.getValidationResultList();
    }

    private void init(byte[] bytes) throws IOException {

        CharStream charStream = CharStreams.fromStream(new ByteArrayInputStream(bytes));

        _lexer = new DSCLexer(charStream);
        _tokens = new CommonTokenStream(_lexer);
        _parser = new DSCParser(_tokens);


        _errorListener = new DSCErrorListener();
        _parser.addErrorListener(_errorListener);
    }
}
