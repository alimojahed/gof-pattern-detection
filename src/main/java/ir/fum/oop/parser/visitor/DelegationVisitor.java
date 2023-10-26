package ir.fum.oop.parser.visitor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassInfo;
import ir.fum.oop.model.FieldOrParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class DelegationVisitor extends VoidVisitorAdapter<Void> {
    private final ClassInfo classInfo;


    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {

        for (FieldDeclaration field : n.getFields()) {

            List<FieldOrParameter> fields = classInfo.getFields();

            for (MethodDeclaration method : n.getMethods()) {

                List<FieldOrParameter> parameters = getParameters(method);
                List<FieldOrParameter> variables = getVariables(method);

                for (MethodCallExpr methodCall : method.findAll(MethodCallExpr.class)) {
                    if (methodCall.getScope().isPresent()) {
                        String scope = methodCall.getScope().get().toString();
                        boolean find = false;
                        for (FieldOrParameter var : variables) {
                            if (scope.equals(var.getName())) {
                                classInfo.getDelegation().add(var.getType());
                                find = true;
                            }
                        }

                        if (!find) {
                            for (FieldOrParameter var : parameters) {
                                if (scope.equals(var.getName())) {
                                    classInfo.getDelegation().add(var.getType());
                                    find = true;
                                }
                            }
                        }

                        if (!find) {
                            for (FieldOrParameter var : fields) {
                                if (scope.equals(var.getName())) {
                                    classInfo.getDelegation().add(var.getType());
                                }
                            }
                        }


                    }
                }
            }
            super.visit(n, arg);
        }
    }

    private List<FieldOrParameter> getVariables(MethodDeclaration methodDeclaration) {
        List<FieldOrParameter> variables = new ArrayList<>();

        if (methodDeclaration != null) {
            BlockStmt blockStmt = methodDeclaration.getBody().orElse(null);
            if (blockStmt != null) {
                for (VariableDeclarationExpr variableDeclarationExpr : blockStmt.findAll(VariableDeclarationExpr.class)) {
                    String variableType = variableDeclarationExpr.getElementType().toString();
                    for (VariableDeclarator variableDeclarator : variableDeclarationExpr.getVariables()) {
                        String variableName = variableDeclarator.getNameAsString();
                        variables.add(new FieldOrParameter(variableType, variableName, false));
                    }
                }
            }
        }

        return variables;
    }

    private List<FieldOrParameter> getParameters(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getParameters()
                .stream()
                .map(parameter -> new FieldOrParameter(parameter.getTypeAsString(), parameter.getNameAsString(), false))
                .collect(Collectors.toList());
    }

}
