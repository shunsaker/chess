package model.factories;

import model.Color;
import model.Pieces.Pawn;
import model.Pieces.Piece;

public class PawnFactory extends PieceFactory{

	@Override
	public Piece getInstance(Color color) {
		return new Pawn(color);
	}
	
}
