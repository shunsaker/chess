package model.Pieces;

import javax.swing.ImageIcon;

import chess.MoveType;
import model.Color;

public class Queen extends Piece{

	public Queen(Color color) {
		super(color, "queen", MoveType.manyAny,
				new ImageIcon(color == Color.black ? "assets/BlackQueen.png" : "assets/WhiteQueen.png"));
	}
	
}
