package com.samwolfson.pde2js;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.JsonPrinter;
import com.github.javaparser.printer.PrettyPrintVisitor;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

import java.io.File;
import java.io.FileNotFoundException;

public class App {
    public static void main( String[] args ) throws FileNotFoundException {
        CompilationUnit cu = JavaParser.parse(new File("./samples/JSConversionSampleBuild/source/JSConversionSample.java"));

        SetupMethodFinderVisitor setupMethodFinder = new SetupMethodFinderVisitor();
        cu.accept(setupMethodFinder, null);

        MethodDeclaration setupMethod = setupMethodFinder.getSetupMethod().orElseThrow(IllegalStateException::new);

        cu.accept(new InitializationInSetupVisitor(setupMethod), null);

        PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor(new PrettyPrinterConfiguration());

        cu.accept(prettyPrintVisitor, null);

        System.out.println(prettyPrintVisitor.getSource());
    }
}
