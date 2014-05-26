package inputOutput;

import java.util.List;
import java.util.Observable;

import chess.Location;
import model.Board;
import model.Pieces.Piece;

public abstract class Display extends Observable{
	public abstract void displayBoard(Board board, Location selectedPiece, List<Location> validMoves);
	public abstract void notifyCheck(String color);
	public abstract void notifyEndofGame(String Message);
	public abstract Piece getPawnPromotion();
}
