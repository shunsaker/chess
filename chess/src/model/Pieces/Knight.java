package model.Pieces;

import chess.MoveType;
import model.Color;

public class Knight extends Piece{

	public Knight(Color color) {
		super(color, "knight", MoveType.singleLShape);
	}
	
	@Override
	public char toChar() {
		return super.getColor() == Color.black ? 'N' :'n';
	}

}
