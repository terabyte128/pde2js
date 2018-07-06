/*  word_guessing.pde
 Samuel Wolfson
 
 Word guessing game (like Word Mastermind)
 */

int stage;        // the current stage of the game
//  - stage 0: mastermind enters secret phrase
//  - stage 1: player tries to guess secret phrase
//  - stage 2: player won game
int count;        // the number of guesses made so far

String guess;     // stores user's keyboard inputs
String answer;    // stores secret phrase
String message;   // stores messages to display to player


void setup() {
  size(600, 300);  // drawing canvas size of your choice
  textSize(30);   // text size  of your choice
  fill(0);        // text color of your choice
  reset();
}


void draw() {
  background(255);
  if (stage==0) {
    // YOUR CODE BLOCK 1
    text("Enter secret phrase:", 10, 40);
    text(guess, 10, 100);
  } else if (stage == 1) {
    // YOUR CODE BLOCK 2
    text("Guess the secret phrase (" + answer.length() + " letters):", 10, 40);
    text(guess, 10, 100);
    text(message, 10, 160);
  } else if (stage == 2) {
    text("Nice job, you correctly guessed\n`" + answer + "`!!", 10, 40);
    text("It took you " + count + " tries.", 10, 150);
  }
}


// record user keystrokes
void keyPressed() {
  if (keyCode == ENTER || keyCode == RETURN) {
    // user pressed [Enter]
    // - behavior should depend on game stage
    if (stage == 0 && guess.length() > 0) {
      answer = guess;
      guess = "";
      ++stage;
    } else if (stage == 1) {
      // YOUR CODE BLOCK 5
      count++;
      if (guessing()) {
        ++stage;   
      }
      guess = "";
    } else if (stage == 2) {
      reset();
      // YOUR CODE BLOCK 6
    }
  } else if ( (keyCode == BACKSPACE || keyCode == DELETE) && guess.length() > 0 ) {
    // remove last character from guess
    guess = guess.substring(0, guess.length() - 1);
  } else if ( key >= ' ' && key <= '~' ) {
    // add key to guess string
    guess += key;
  }
}


// called when the user enters a guess
// - returns true or false based on if guess matches the secret phrase
boolean guessing() {
  int correct = 0;

  if (answer.length() != guess.length()) {
    message = "Incorrect number of letters!!!1!";
    return false;
  }
  
  for (int i = 0; i < answer.length(); i++) {
    if (guess.charAt(i) == answer.charAt(i)) {
      correct++;
    }
  }
  
  if (correct < answer.length()) {
    message = "Not quite, but " + correct + " letters are correct!!";
  }
  
  return correct == answer.length();
}


// function to reset the game
void reset() {
  stage = count = 0;
  guess = answer = message = "";
}