package inputOutput;

import java.util.List;

import model.Board;
import model.Pieces.Piece;
import chess.Location;
import chess.LocationTools;

public class InteractiveConsoleDisplay extends ConsoleDisplay{
	private final static String SEPERATOR = " |___|___|___|___|___|___|___|___|";
	private final static String LABEL = "   a   b   c   d   e   f   g   h";

	@Override	
	public void displayBoard(Board board, Location selectedPiece, List<Location> focusLocations) {
		int i = 0;
		System.out.println("\n" + LABEL);
		System.out.println("  ___ ___ ___ ___ ___ ___ ___ ___");
		for(Piece piece : board) {
			int rowLabel = Board.SIZE-(i / Board.SIZE);
			int col = i % Board.SIZE;
			int row = i / Board.SIZE;
			if(col == 0) { // end of line
				if(i != 0) { 
					System.out.println();
					System.out.println(SEPERATOR);
				}
				System.out.print( rowLabel+ "|");
			}
			String pieceName = " " + piece.toChar();
			Location loc = new Location(row, col);
			if(loc.equals(selectedPiece)) {
				pieceName = "[" + piece.toChar() + "]";
			}
			else {
				pieceName += LocationTools.locationInList(loc, focusLocations) ? "*" : " ";
			}
			System.out.print(pieceName + "|");
			if(col == Board.SIZE - 1) {
				System.out.print(rowLabel);
			}
			i++;
		}
		System.out.println("\n" + SEPERATOR + "\n" + LABEL);
	}

}
