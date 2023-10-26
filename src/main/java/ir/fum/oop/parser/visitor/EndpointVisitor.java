package ir.fum.oop.parser.visitor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassInfo;

import java.util.Optional;


@RequiredArgsConstructor
public class EndpointVisitor extends VoidVisitorAdapter<Void> {
    private final ClassInfo classInfo;

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Void args) {

        Optional<AnnotationExpr> controller = classOrInterfaceDeclaration.getAnnotationByName("Controller");
        Optional<AnnotationExpr> restController = classOrInterfaceDeclaration.getAnnotationByName("RestController");

        String method = "";
        if (controller.isPresent() || restController.isPresent()) {
            Optional<AnnotationExpr> classRequestMapping = classOrInterfaceDeclaration.getAnnotationByName("RequestMapping");

            String baseRoute = "/";

            if (classRequestMapping.isPresent()) {
                NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) classRequestMapping.get();
                baseRoute =  normalAnnotation.getPairs().stream()
                        .filter(pair -> pair.getNameAsString().equals("value"))
                        .map(MemberValuePair::getValue)
                        .findFirst()
                        .map(Object::toString)
                        .orElse("");

            }


            for (MethodDeclaration methodDeclaration: classOrInterfaceDeclaration.getMethods()) {
                Optional<AnnotationExpr> getMapping = methodDeclaration.getAnnotationByName("GetMapping");
                Optional<AnnotationExpr> postMapping = methodDeclaration.getAnnotationByName("PostMapping");
                Optional<AnnotationExpr> putMapping = methodDeclaration.getAnnotationByName("PutMapping");
                Optional<AnnotationExpr> patchMapping = methodDeclaration.getAnnotationByName("PatchMapping");
                Optional<AnnotationExpr> deleteMapping = methodDeclaration.getAnnotationByName("DeleteMapping");
                Optional<AnnotationExpr> requestMapping = methodDeclaration.getAnnotationByName("RequestMapping");

                if (requestMapping.isPresent()) {
                    NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) classRequestMapping.get();
                    method = normalAnnotation.getPairs().stream()
                            .filter(pair -> pair.getNameAsString().equals("method"))
                            .map(MemberValuePair::getValue)
                            .findFirst()
                            .map(Object::toString)
                            .orElse("");

                }

                if (getMapping.isPresent()) {
                    method = "GET";
                }



            }


        }


    }


}
