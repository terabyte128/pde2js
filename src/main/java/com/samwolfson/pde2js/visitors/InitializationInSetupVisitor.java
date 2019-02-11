package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Move all variable initializations from the top of the class into the setup() method.
 */
public class InitializationInSetupVisitor extends VoidVisitorAdapter<Void> {
    private BlockStmt setupMethodBody;

    /**
     *
     * @param setupMethod Processing's setup() method
     */
    public InitializationInSetupVisitor(MethodDeclaration setupMethod) {
        super();
        this.setupMethodBody = setupMethod.getBody()
                .orElseThrow(() -> new IllegalStateException("setup() must have a body"));
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        super.visit(n, arg);

        n.getVariables().forEach(v -> {
            if (v.getInitializer().isPresent()) {
                Expression initializer = v.getInitializer().get();
                setupMethodBody.addStatement(new AssignExpr(v.getNameAsExpression(), initializer, AssignExpr.Operator.ASSIGN));
                v.removeInitializer();
            } else if (v.getType().isPrimitiveType()) {
                // for primitive fields that are declared but not initialized, give them the default value in Java
                switch (v.getTypeAsString()) {
                    case "int":
                        setupMethodBody.addStatement(new AssignExpr(v.getNameAsExpression(), new IntegerLiteralExpr(0), AssignExpr.Operator.ASSIGN));
                        break;
                    case "float":
                    case "long":
                    case "double":
                        setupMethodBody.addStatement(new AssignExpr(v.getNameAsExpression(), new LongLiteralExpr(0), AssignExpr.Operator.ASSIGN));
                        break;
                    case "char":
                        setupMethodBody.addStatement(new AssignExpr(v.getNameAsExpression(), new CharLiteralExpr("\\0"), AssignExpr.Operator.ASSIGN));
                        break;
                    case "boolean":
                        setupMethodBody.addStatement(new AssignExpr(v.getNameAsExpression(), new BooleanLiteralExpr(false), AssignExpr.Operator.ASSIGN));
                        break;
                }
            } else {
                // for reference fields, give them null
                setupMethodBody.addStatement(new AssignExpr(v.getNameAsExpression(), new NullLiteralExpr(), AssignExpr.Operator.ASSIGN));
            }
        });
    }
}
