package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Move all variable initializations from the top of the class into the setup() method.
 */
public class InitializationInSetupVisitor extends VoidVisitorAdapter<Void> {
    private MethodDeclaration setupMethod;
    private BlockStmt setupMethodBody;

    /**
     *
     * @param setupMethod Processing's setup() method
     */
    public InitializationInSetupVisitor(MethodDeclaration setupMethod) {
        super();
        this.setupMethod = setupMethod;
        this.setupMethodBody = setupMethod.getBody()
                .orElseThrow(() -> new IllegalStateException("setup() must have a body"));
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        n.getVariables().forEach(v -> {
            if (v.getInitializer().isPresent()) {
                Expression initializer = v.getInitializer().get();

                setupMethodBody.addStatement(0, new AssignExpr(v.getNameAsExpression(), initializer, AssignExpr.Operator.ASSIGN));
                v.removeInitializer();
            }
        });
    }
}
