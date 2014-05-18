package model.factories;

import model.Color;
import model.Pieces.Piece;

public abstract class PieceFactory {
	abstract public Piece getInstance(Color color);
}
