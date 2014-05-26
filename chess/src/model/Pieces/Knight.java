package model.Pieces;

import javax.swing.ImageIcon;

import chess.MoveType;
import model.Color;

public class Knight extends Piece{

	public Knight(Color color) {
		super(color, "knight", MoveType.singleLShape, 
			new ImageIcon(color == Color.black ? "assets/BlackKnight.png" : "assets/WhiteKnight.png"));
	}
	
	@Override
	public char toChar() {
		return super.getColor() == Color.white ? 'N' :'n';
	}

}
