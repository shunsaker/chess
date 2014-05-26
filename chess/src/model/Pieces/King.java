package model.Pieces;

import javax.swing.ImageIcon;

import chess.MoveType;
import model.Color;

public class King extends Piece{

	public King(Color color) {
		super(color, "king", MoveType.singleAny,  
			new ImageIcon(color == Color.black ? "assets/BlackKing.png" : "assets/WhiteKing.png"));
	}

}
