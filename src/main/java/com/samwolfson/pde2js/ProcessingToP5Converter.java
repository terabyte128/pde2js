package com.samwolfson.pde2js;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.samwolfson.pde2js.visitors.*;
import spark.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Main class to convert Processing code to JS
 */
public class ProcessingToP5Converter {
    private String jsCode;
    private List<String> warnings = new ArrayList<>();

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

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    private void compile(String javaCode) {
        // generate the AST
        CompilationUnit cu = JavaParser.parse(javaCode);

        // discover the setup() method
        SetupMethodFinderVisitor setupMethodFinder = new SetupMethodFinderVisitor();
        cu.accept(setupMethodFinder, null);
        MethodDeclaration setupMethod = setupMethodFinder.getSetupMethod().orElseThrow(IllegalStateException::new);


        Node classNode = setupMethod.getParentNode().orElseThrow(() -> new IllegalStateException("Setup method must have a parent"));

        if (!((classNode instanceof ClassOrInterfaceDeclaration) && !((ClassOrInterfaceDeclaration) classNode).isInterface())) {
            throw new IllegalStateException("Parent of setup method must be a class declaration");
        }

        // the class containing all of the Processing code
        ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) classNode;

        /*
        The following visitor passes all modify the generated AST to make it work as JS
         */

        // move variable initializations into a new method, initializeFields()
        MethodDeclaration initializeFieldsMethod = classDecl.addMethod("initializeFields");
        initializeFieldsMethod.setBody(new BlockStmt());

        cu.accept(new InitializationInSetupVisitor(initializeFieldsMethod), null);

        // call initializeFields() at the beginning of setup()
        setupMethod.getBody().orElseThrow(IllegalStateException::new).addStatement(0, new MethodCallExpr("initializeFields"));

        // rename call to size() to createCanvas()
        cu.accept(new SizeToCreateCanvasVisitor(), null);

        // replace .length() calls to .length member accesses
        cu.accept(new StringLengthVisitor(), null);

        // rename #{DIRECTION} to #{DIRECTION}_ARROW
        cu.accept(new RenameKeyPressedVisitor(), null);

        // fix the method names that conflict with function names so they're back
        // to what they were originally
        cu.accept(new RenameConflictingMethodVisitor(), null);

        // replace println() with print()
        cu.accept(new PrintlnToPrintVisitor(), null);

        // collect all of the calls to functions that load stuff
        // in JS, must be moved to the preload() function
        CollectLoadMethodsVisitor collectLoadMethodsVisitor = new CollectLoadMethodsVisitor();
        cu.accept(collectLoadMethodsVisitor, null);

        List<Expression> loadMethodCalls = collectLoadMethodsVisitor.getLoadMethodCalls();
        createPreloadMethod(classDecl, loadMethodCalls);

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

    /**
     * Create a new method called preload() in the class, and add a comment that the following expressions should
     * be moved there. (We can't move the expressions there directly because they might intermingle with other
     * variable assignments).
     * @param klass Class in which to create the method
     * @param expressions Expressions to put in the comment.
     */
    private void createPreloadMethod(ClassOrInterfaceDeclaration klass, List<Expression> expressions) {
        if (expressions.isEmpty()) {
            return;
        }

        warnings.add("Manual changes required. See preload() method in P5.js code (it's probably at the bottom)");

        MethodDeclaration preloadMethod = klass.getMethods().stream()
                .filter(m -> m.getNameAsString().equals("preload"))
                .findFirst().orElse(klass.addMethod("preload"));

        if (!preloadMethod.getBody().isPresent()) {
            preloadMethod.setBody(new BlockStmt());
        }

        BlockStmt preloadMethodBody = preloadMethod.getBody().get();

        preloadMethodBody.addOrphanComment(new LineComment("TODO: put method calls that load from files into this method"));
        preloadMethodBody.addOrphanComment(new LineComment("I found the following calls that you should move here:"));

        expressions.forEach(e -> {
            int lineNumber = -1;
            if (e.getRange().isPresent()) {
                lineNumber = e.getRange().get().begin.line;
            }

            preloadMethodBody.addOrphanComment(
                    new LineComment("- on line " + (lineNumber == -1 ? "??" : lineNumber) + ": " + e)
            );
        });

        preloadMethodBody.addOrphanComment(new LineComment("(note that line numbers are from your Processing code)"));
    }

    public static void main(String[] args) throws IOException {
        try (FileInputStream inputStream = new FileInputStream("./samples/JSConversionSample.pde")) {
            String content = IOUtils.toString(inputStream);

            ProcessingToP5Converter converter = new ProcessingToP5Converter(content);
            System.out.println(converter.getJsCode());
        }
    }
}
