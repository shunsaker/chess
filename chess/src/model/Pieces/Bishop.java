package model.Pieces;

import chess.MoveType;
import model.Color;

public class Bishop extends Piece{

	public Bishop(Color color) {
		super(color, "bishop", MoveType.manyDiagonal);
	}
}
