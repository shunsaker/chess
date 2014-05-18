package chess;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		if(args != null && args.length > 0) {
			new GameController(new File(args[0])).driver();;
			//Setup.parseFile(new File(args[0]));
		}
		else {
			System.out.println("Command line argument 'file destination' not found. Terminating program.");
		}
	}

}
