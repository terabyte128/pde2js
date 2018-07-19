void setup() {

}

void draw() {

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

  if (1 == 2 || 2 == 3 || 3 == 4) {

  }
}