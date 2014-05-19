package model.Pieces;

import chess.MoveType;
import model.Color;

public class Queen extends Piece{

	public Queen(Color color) {
		super(color, "queen", MoveType.manyAny);
	}
	
}
