package model.factories;

import model.Color;
import model.Pieces.Knight;
import model.Pieces.Piece;

public class KnightFactory extends PieceFactory{

	@Override
	public Piece getInstance(Color color) {
		return new Knight(color);
	}
	
}
