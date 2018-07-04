package com.samwolfson.pde2js;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.samwolfson.pde2js.visitors.*;
import spark.utils.IOUtils;

import java.io.*;

/**
 * Main class to convert Processing code to JS
 */
public class ProcessingToP5Converter {
    private String processingCode;
    private String jsCode;

    /**
     *
     * @param processingCode the Processing source code
     */
    public ProcessingToP5Converter(String processingCode) {
        /*
        There are a few differences between Processing code and Java code, but the only one that we
        care about is that Java code is enclosed in a class { ... }. Therefore, we wrap the Processing code with
        this so that the Java AST parser we're using doesn't complain.
         */
        this.processingCode = "public class Processing {\n" + processingCode + "\n}";
        this.compile();
    }

    public String getJsCode() {
        return jsCode;
    }

    private void compile() {
        // generate the AST
        CompilationUnit cu = JavaParser.parse(processingCode);

        // discover the setup() method
        SetupMethodFinderVisitor setupMethodFinder = new SetupMethodFinderVisitor();
        cu.accept(setupMethodFinder, null);
        MethodDeclaration setupMethod = setupMethodFinder.getSetupMethod().orElseThrow(IllegalStateException::new);

        /*
        The following visitor passes all modify the generated AST to make it work as JS
         */

        // move variable initializations into setup()
        cu.accept(new InitializationInSetupVisitor(setupMethod), null);

        // rename call to size() to createCanvas()
        cu.accept(new SizeToCreateCanvasVisitor(), null);

        // replace .length() calls to .length member accesses
        cu.accept(new StringLengthVisitor(), null);

        // rename #{DIRECTION} to #{DIRECTION}_ARROW
        cu.accept(new RenameKeyPressedVisitor(), null);

        jsCode = getSource(cu);
    }

    private String getSource(CompilationUnit cu) {
        /*
            The JavaScriptPrinterVisitor is a modified version of the included PrettyPrintVisitor;
            it makes all the final changes from Java to JS syntax (`function` instead of return type, `var` instead
            of variable type, array initialization, etc.)
         */
        JavaScriptPrinterVisitor prettyPrintVisitor = new JavaScriptPrinterVisitor(new PrettyPrinterConfiguration());
        cu.accept(prettyPrintVisitor, null);
        return prettyPrintVisitor.getSource();
    }

    public static void main(String[] args) throws IOException {
        try (FileInputStream inputStream = new FileInputStream("./samples/JSConversionSample/JSConversionSample.pde")) {
            String content = IOUtils.toString(inputStream);

            ProcessingToP5Converter converter = new ProcessingToP5Converter(content);
            System.out.println(converter.getJsCode());
        }
    }
}
