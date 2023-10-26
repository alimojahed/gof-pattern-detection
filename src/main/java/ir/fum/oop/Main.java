package ir.fum.oop;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        ir.fum.oop.ProjectParser.parse("QuickUML", "1 - QuickUML 2001/src");
        ir.fum.oop.ProjectParser.parse("JRefactory", "3 - JRefactory v2.6.24");
//        ir.fum.oop.ProjectParser.parse("Lexi", "2 - Lexi v0.1.1 alpha");
        ir.fum.oop.ProjectParser.parse("JUnit", "5 - JUnit v3.7/src");
//        ir.fum.oop.ProjectParser.parse("MapperXML", "8 - MapperXML v1.9.7/src");
//        ir.fum.oop.ProjectParser.parse("Nutch", "10 - Nutch v0.4/src");
//        ir.fum.oop.ProjectParser.parse("PMD", "11 - PMD v1.8");
        ir.fum.oop.ProjectParser.parse("JHotDraw", "JHotDraw v5.1");
//        ir.fum.oop.ProjectParser.parse("NetBeans", "4 - Netbeans v1.0.x/src");
//        ir.fum.oop.ProjectParser.parse("spring-cloud-netflix", "spring-cloud-netflix-main");

    }
}
