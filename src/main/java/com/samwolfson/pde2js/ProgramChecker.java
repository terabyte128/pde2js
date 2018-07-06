package com.samwolfson.pde2js;

import com.github.javaparser.Problem;
import com.github.javaparser.TokenRange;

import java.util.ArrayList;
import java.util.List;

public class ProgramChecker {
    public static List<String> precompileCheck(String program) {
        List<String> errors = new ArrayList<>();

        if ( !( program.contains("setup") && program.contains("draw") ) ) {
            errors.add("p5.js does not support programs without setup() and draw().");
        }

        return errors;
    }

    public static List<String> interpretParserErrors(List<Problem> parseProblems) {
        List<String> errors = new ArrayList<>();

        parseProblems.forEach(problem -> {
            if (problem.getLocation().isPresent()) {
                TokenRange location = problem.getLocation().get();
                errors.add("Error found around " + location.getBegin().toString());
            } else {
                errors.add("Error found around ???");
            }
        });

        return errors;
    }
}
