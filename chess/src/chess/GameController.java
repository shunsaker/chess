package chess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import commands.*;
import fileIO.BufferedFileReader;
import model.Board;

public class GameController {
	private static final boolean GUI_DISPLAY = false;
	private Display display = GUI_DISPLAY ? new GuiDisplay() : new ConsoleDisplay();
	private Board board = new Board();
	private List<Command> moves = new ArrayList<Command>();
	
	public GameController(File commandFile) {
		BufferedFileReader reader = new BufferedFileReader(commandFile);
		while(reader.hasNext()) {
			Command c = InputParser.parseLine(reader.next());
			if(c != null) {
				moves.add(c);
			}
		}
		reader.close();
	}
	
	public void driver() {
		System.out.println();
		for(Command c : moves) {
			System.out.println(c);
			executeCommand(c);
		}
		
		display.displayBoard(board);
	}
	
	public void executeCommand(Command c) {
		if(c instanceof Place) {
			Place pCom = (Place) c;
			board.place(pCom.getPiece(), pCom.getLocation());
		}
	}
	
}
