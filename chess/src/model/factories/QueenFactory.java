package model.factories;

import model.Color;
import model.Pieces.Queen;
import model.Pieces.Piece;

public class QueenFactory extends PieceFactory{

	@Override
	public Piece getInstance(Color color) {
		return new Queen(color);
	}
	
}
