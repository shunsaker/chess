package chess;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		GameController controller = (args != null && args.length > 0) ? new GameController(new File(args[0])) : new GameController();
		controller.driver();
	}

}
