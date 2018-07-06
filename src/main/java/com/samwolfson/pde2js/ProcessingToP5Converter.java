package com.samwolfson.pde2js;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.samwolfson.pde2js.visitors.*;
import spark.utils.IOUtils;

import java.io.*;
import java.util.regex.Pattern;

/**
 * Main class to convert Processing code to JS
 */
public class ProcessingToP5Converter {
    private String jsCode;

    // the prefix appended to functions that cause the parser to complain since they
    // have the same names as data types
    public static final String DATA_TYPE_CONFLICT_PREFIX = "___parse";
    public static final String CONFLICTING_DATA_TYPE_REGEX = "(boolean|byte|char|float|int)";

    private static Pattern datatypeFunctionRegex = Pattern.compile(CONFLICTING_DATA_TYPE_REGEX + "\\s*\\(");
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

        // in Java, boolean, byte, char, float, int are data types, so the Processing functions int(..), float(..) make
        // the compiler complain

        String javaCode = makeValidJava(processingCode);
        compile(javaCode);
    }

    /**
     * Convert Processing input code to code that can be parsed by the Java parser.
     * @param processingCode Processing input code.
     * @return Valid Java code.
     */
    private String makeValidJava(String processingCode) {
        /*
        There are a few differences between Processing code and Java code, but the only one that we
        care about is that Java code is enclosed in a class { ... }. Therefore, we wrap the Processing code with
        this so that the Java AST parser we're using doesn't complain.
         */
        String classCode = "public class Processing {" + processingCode + "}";

        // in Java, int, float, double, etc. are data types, so the Processing functions int(..), float(..) make
        // the parser complain. Change them to something with a different name.
        classCode = datatypeFunctionRegex.matcher(classCode).replaceAll( DATA_TYPE_CONFLICT_PREFIX + "$1(");

        return classCode;
    }

    public String getJsCode() {
        return jsCode;
    }

    private void compile(String javaCode) {
        // generate the AST
        CompilationUnit cu = JavaParser.parse(javaCode);

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

        // fix the method names that conflict with function names so they're back
        // to what they were originally
        cu.accept(new RenameConflictingMethodVisitor(), null);

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
        try (FileInputStream inputStream = new FileInputStream("./samples/word_guessing.pde")) {
            String content = IOUtils.toString(inputStream);

            ProcessingToP5Converter converter = new ProcessingToP5Converter(content);
            System.out.println(converter.getJsCode());
        }
    }
}
