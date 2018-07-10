package com.samwolfson.pde2js.visitors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Assert;
import org.junit.Test;

public class SetupMethodFinderVisitorTest {
    private static final String TEST_CODE = "" +
            "public class Test {" +
            "public void setup() {}" +
            "public void draw() {}" +
            "public void doStuff() {}" +
            "}";

    @Test
    public void shouldFindSetupMethod() {
        CompilationUnit cu = JavaParser.parse(TEST_CODE);

        SetupMethodFinderVisitor visitor = new SetupMethodFinderVisitor();
        cu.accept(visitor, null);

        Assert.assertTrue(visitor.getSetupMethod().isPresent());
        Assert.assertEquals("setup", visitor.getSetupMethod().get().getNameAsString());
    }
}
