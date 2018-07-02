int uninitialized;
String[] initialized = new String[3];

int[] someArray = { 1, 2, 3, 4 + 5};

int x = 3, y, z = 7;

void setup() {
  size(500, 500);

  z = z + x;
  String zz = "hello";

  size(100, 100);
  uninitialized = 3;
  initialized[0] = "hello, world!";
  
  int local = initialized[0].length();

  for (int i = 0; i < 10; i++) {
    local += i;
  }
  
  println(local);

  if (true) {
     println("true!");
  }
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

int arrayLength(int[] a) {
  return a.length;
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