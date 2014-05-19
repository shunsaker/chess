package model.Pieces;

import chess.MoveType;
import model.Color;

public class Rook extends Piece{

	public Rook(Color color) {
		super(color, "rook", MoveType.manyStright);
	}

}
