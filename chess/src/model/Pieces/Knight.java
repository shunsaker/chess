package model.Pieces;

import model.Color;

public class Knight extends Piece{

	public Knight(Color color) {
		super(color, "knight");
	}
	
	@Override
	public char toChar() {
		return 'n';
	}

}
