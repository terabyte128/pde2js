/*
This should be correctly parsed to p5 even though the method calls int(), ... conflict with Java type names.
*/

void setup() {
	boolean b = boolean(false);
	byte by = byte(1);
	char c = char('c');
	float f = float(7);
	int x = int(7.33);
}

void draw() {

}