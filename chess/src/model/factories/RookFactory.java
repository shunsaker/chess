package model.factories;

import model.Color;
import model.Pieces.Rook;
import model.Pieces.Piece;

public class RookFactory extends PieceFactory{

	@Override
	public Piece getInstance(Color color) {
		return new Rook(color);
	}
	
}
