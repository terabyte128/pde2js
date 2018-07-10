# `pde2js`: Processing to P5.js Converter

This project is intended to help new programmers convert their Processing code from Java into JavaScript, using the [p5.js](https://p5js.org) library. It does so by using the [JavaParser](https://github.com/javaparser/javaparser) library to build an AST over the Java-like source code, makes various transformations over the tree to make the code compatible with JS, then finally uses a modified pretty-print visitor to write out the code as JS.

It can be used as web application with [Spark](http://sparkjava.com) or standalone, with the `ProcessingToP5Convertor` class.

#### Try it out at [pde2js.herokuapp.com](https://pde2js.herokuapp.com).

## Installation
1. Clone the repository onto your local machine.
2. We manage dependencies with `maven`. Run `mvn install` to install dependencies and run the tests. (You may need to install `maven` if it's not already on your local machine.)

## Usage
Using the `ProcessingToP5Converter` class is very simple:
```java
String code = ...; // give your code as input here

ProcessingToP5Converter converter = new ProcessingToP5Converter(code);
System.out.println(converter.getJsCode());
```

A more thorough example is available in `examples/ConvertProcessingFile.java`

If you'd like to run a web server instead, you can run `App.java`, which will start a web server on `localhost:4567`. 

## More Details
`pde2js` is implemented as series of visitor passes over the generated AST. These visitors can be found in the `visitors/` directory. `ProcessingToP5Convertor` calls each of these visitors to transform the AST in a way that makes it JS-friendly. (Each visitor has a class comment that should explain what it does; and by splitting up transformations into multiple passes, all of the visitors are quite short.) 

The final pass, `JavaScriptPrinterVisitor`, does not transform the tree; rather, it prints out the code as JS (e.g. replace type names with `var`, function return types with `function`, etc.)

It's definitely nowhere close to perfect, but the first goal is to help new programmers avoid the headache and weird errors that occur when trying to manually transform their code for use on the Web. More complicated transformations can be added in the future.

Sample Processing files are given in the `samples/` directory, which should help to illustrate how each transformation over the AST works.

## Contributions
Contributions are welcome! Please let me know if you've found a bug, or have a feature request, by creating an issue. Pull requests are also welcome.