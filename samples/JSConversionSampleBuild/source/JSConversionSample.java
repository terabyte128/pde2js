import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class JSConversionSample extends PApplet {

int uninitialized;
String[] initialized = new String[3];

int x = 3, y, z = 7;

public void setup() {
  z = z + x;

  
  uninitialized = 3;
  initialized[0] = "hello, world!";
  
  int local = 3;
  
  for (int i = 0; i < 10; i++) {
    local += i;
  }
  
  println(local);
}

public void draw() {
  //writeStuff(initialized[0]);
  returnStuff(frameCount);
  stringLength("foobar");
}

public void writeStuff(String s) {
  println(s);
}

public int returnStuff(int i) {
  return i + 1;
}

public int stringLength(String s) {
  return s.length();
}

public void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      println("up");
    } else if (keyCode == DOWN) {
      println("down");
    } else if (keyCode == LEFT) {
      println("left");
    } else if (keyCode == RIGHT) {
      println("right");
    }
  } else {
     println("key: " + key); 
  }
}
  public void settings() {  size(100, 100); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "JSConversionSample" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
