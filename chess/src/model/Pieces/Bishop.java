package model.Pieces;

import javax.swing.ImageIcon;

import chess.MoveType;
import model.Color;

public class Bishop extends Piece{

	public Bishop(Color color) {
		super(color, "bishop", MoveType.manyDiagonal,
			new ImageIcon(color == Color.black ? "assets/BlackBishop.png" : "assets/WhiteBishop.png"));
	}
}
