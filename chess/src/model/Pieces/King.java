package model.Pieces;

import chess.MoveType;
import model.Color;

public class King extends Piece{

	public King(Color color) {
		super(color, "king", MoveType.singleAny);
	}

}
