package com.samwolfson.pde2js;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.printer.PrettyPrintVisitor;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.samwolfson.pde2js.visitors.*;

import java.io.File;
import java.io.FileNotFoundException;

public class App {
    public static void main( String[] args ) throws FileNotFoundException {
        CompilationUnit cu = JavaParser.parse(new File("./samples/JSConversionSampleBuild/source/JSConversionSample.java"));

        SetupMethodFinderVisitor setupMethodFinder = new SetupMethodFinderVisitor();
        cu.accept(setupMethodFinder, null);

        MethodDeclaration setupMethod = setupMethodFinder.getSetupMethod().orElseThrow(IllegalStateException::new);

        // add call to settings() from setup() method
        setupMethod.getBody().ifPresent(body -> {
            // call settings() from setup()
            body.addStatement(0, new MethodCallExpr("settings"));
        });

        cu.accept(new InitializationInSetupVisitor(setupMethod), null);
        cu.accept(new SizeToCreateCanvasVisitor(), null);
        cu.accept(new StringLengthVisitor(), null);
        cu.accept(new RenameKeyPressedVisitor(), null);

        printSource(cu);
    }

    public static void printSource(CompilationUnit cu) {
        JavaScriptConverterVisitor prettyPrintVisitor = new JavaScriptConverterVisitor(new PrettyPrinterConfiguration());
        cu.accept(prettyPrintVisitor, null);
        System.out.println(prettyPrintVisitor.getSource());
    }
}
