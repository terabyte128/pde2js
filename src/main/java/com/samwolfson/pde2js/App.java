package com.samwolfson.pde2js;

import com.github.javaparser.ParseProblemException;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import javax.json.Json;
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
                return Json.createObjectBuilder()
                        .add("hasErrors", true)
                        .add("errors", "You need paste in some code!")
                        .build()
                        .toString();
            }

            try {
                ProcessingToP5Converter converter = new ProcessingToP5Converter(req.body());
                return Json.createObjectBuilder()
                        .add("code", converter.getJsCode())
                        .add("hasErrors", false)
                        .build()
                        .toString();
            } catch (ParseProblemException e) {
                return Json.createObjectBuilder()
                        .add("hasErrors", true)
                        .add("errors", e.getMessage())
                        .build()
                        .toString();
            }
        });
    }

}
