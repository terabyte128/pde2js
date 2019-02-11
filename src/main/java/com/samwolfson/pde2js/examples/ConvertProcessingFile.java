package com.samwolfson.pde2js.examples;

import com.samwolfson.pde2js.ProcessingToP5Converter;
import spark.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class ConvertProcessingFile {
    public static void main(String[] args) throws IOException {
        // read a file into a String
        try (FileInputStream inputStream = new FileInputStream("./samples/null_characters.pde")) {
            String content = IOUtils.toString(inputStream);

            // create a new instance of the converter
            ProcessingToP5Converter converter = new ProcessingToP5Converter(content);

            // get the code!
            System.out.println(converter.getJsCode());
        }
    }
}
