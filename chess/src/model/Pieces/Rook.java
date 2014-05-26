package model.Pieces;

import javax.swing.ImageIcon;

import chess.MoveType;
import model.Color;

public class Rook extends Piece{

	public Rook(Color color) {
		super(color, "rook", MoveType.manyStright,
			new ImageIcon(color == Color.black ? "assets/BlackRook.png" : "assets/WhiteRook.png"));
	}

}
