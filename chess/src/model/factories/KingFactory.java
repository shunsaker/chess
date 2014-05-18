package model.factories;

import model.Color;
import model.Pieces.King;
import model.Pieces.Piece;

public class KingFactory extends PieceFactory{

	@Override
	public Piece getInstance(Color color) {
		return new King(color);
	}
	
}