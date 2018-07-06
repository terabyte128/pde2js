package com.samwolfson.pde2js;

import com.github.javaparser.ParseProblemException;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void setupServer() {
        // enable auto-refresh of static files during development
        if (System.getenv("DEVELOPMENT") != null) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public";
            staticFiles.externalLocation(projectDir + staticDir);
        } else {
            staticFiles.location("/public");
        }
    }

    public static String render(Map<String, Object> templateVars, String templatePath) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(templateVars, templatePath));
    }

    public static void main(String[] args) {


        String portStr = System.getenv("PORT");

        if (portStr != null) {
            try {
                port(Integer.parseInt(portStr));
                System.out.println("Listening on port " + portStr);
            } catch (NumberFormatException ignored) {

            }
        }

        setupServer();
        get("/", (req, res) -> render(null, "index.hbs"));
        post("/convert", (req, res) -> {
            res.type("application/json");

            if (req.body().isEmpty()) {
                return jsonResponse(true, null, "You need to paste in some code!");
            }

            List<String> precompileErrors = ProgramChecker.precompileCheck(req.body());
            if (precompileErrors.isEmpty()) {
                try {

                    ProcessingToP5Converter converter = new ProcessingToP5Converter(req.body());
                    return jsonResponse(false, converter.getJsCode(), null);

                } catch (ParseProblemException e) {
                    return jsonResponse(true, null,
                            String.join("\n", ProgramChecker.interpretParserErrors(e.getProblems())));
                }
            } else {
                return jsonResponse(true, null, String.join("\n", precompileErrors));
            }
        });
    }

    private static String jsonResponse(boolean hasErrors, String code, String errors) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("hasErrors", hasErrors);
        if (hasErrors) {
            builder.add("errors", errors);
        } else {
            builder.add("code", code);
        }

        return builder.build().toString();
    }
}
