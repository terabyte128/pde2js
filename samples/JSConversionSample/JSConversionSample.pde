int uninitialized;
String[] initialized = new String[3];

int x = 3, y, z = 7;

void setup() {
  z = z + x;

  size(100, 100);
  uninitialized = 3;
  initialized[0] = "hello, world!";
  
  int local = 3;
  
  for (int i = 0; i < 10; i++) {
    local += i;
  }
  
  println(local);
}

void draw() {
  //writeStuff(initialized[0]);
  returnStuff(frameCount);
  stringLength("foobar");
}

void writeStuff(String s) {
  println(s);
}

int returnStuff(int i) {
  return i + 1;
}

int stringLength(String s) {
  return s.length();
}

void keyPressed() {
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