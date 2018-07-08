String[] temp;
int[] month, day, rank;
int[] dayTotal = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};

int R_HEIGHT = 18, R_WIDTH = 38, OFFSET = 24;
int KEY_Y = 640, KEY_X = 180, KEY_HEIGHT = 30;

void setup() {
  size(550, 700);

  temp = loadStrings("month.csv");  // read file data into array of Strings
  month = int(split(temp[0], ','));  // convert 1st line of file (temp[0]) into integer array

  temp = loadStrings("day.csv");
  day = int(split(temp[0], ','));

  temp = loadStrings("rank.csv");
  rank = int(split(temp[0], ','));

  println("foo");
}

void draw() {
  background(255);
  makeGrid();
  makeKey();
  displayValues();
}

void makeGrid() {
  stroke(0);
  for (int i = 0; i < month.length; i++) {
    int cRank = 160 - rank[i] * 160 / 366;

    fill(252 - cRank, 247 - cRank, 197 - cRank);

    // start drawing at 0 so we can manually set offsets
    int x = month[i], y = day[i];  
    rect(x * R_WIDTH + OFFSET, y * R_HEIGHT + OFFSET, R_WIDTH, R_HEIGHT);
  }

  fill(0);
  for (int i = 1; i <= 31; i++) {
    text(i, OFFSET + 5, OFFSET + (R_HEIGHT * i) + 14);
  }

  text("Jan    Feb    Mar     Apr     May     Jun     Jul     Aug    Sep     Oct     Nov     Dec", 3 * OFFSET, OFFSET + 10);
}

void makeKey() {
  for (int i = 0; i < 160; i++) {
    stroke(252 - i, 247 - i, 197 - i);     // set color going from light (low i) to dark (high i) from left-to-right
    line(KEY_X + i, KEY_Y, KEY_X + i, KEY_Y + KEY_HEIGHT); // draw vertical line
  }

  text("Less Common", KEY_X - 90, KEY_Y + KEY_HEIGHT / 2 + 5);
  text("More Common", KEY_X + 170, KEY_Y + KEY_HEIGHT / 2 + 5);
}

void displayValues() {
  int mIndex = (mouseX - OFFSET) / R_WIDTH;
  int dIndex = (mouseY - OFFSET) / R_HEIGHT;

  if (mIndex > 0 && mIndex <= 12 && dIndex > 0 && dIndex <= 31) {
    int rIndex = dayTotal[mIndex - 1] + dIndex - 1;

    int cRank = 160 - rank[rIndex] * 160 / 366;

    // fill with inverse color
    fill(255 - (252 - cRank), 255 - (247 - cRank), 255 - (197 - cRank));

    text(rank[rIndex], mouseX, mouseY);
    //println(rank[rIndex]);
  }
}

void aksdjf() {
// comment!!
/*
comment
*/
}