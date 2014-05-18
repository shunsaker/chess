package model.factories;

import model.Color;
import model.Pieces.Bishop;
import model.Pieces.Piece;

public class BishopFactory extends PieceFactory{

	@Override
	public Piece getInstance(Color color) {
		return new Bishop(color);
	}
	
}
