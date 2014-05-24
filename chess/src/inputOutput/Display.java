package inputOutput;

import java.util.List;

import chess.Location;
import model.Board;

public abstract interface Display {
	public void displayBoard(Board board, Location selectedPiece, List<Location> validMoves);
}
