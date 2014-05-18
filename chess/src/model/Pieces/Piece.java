package model.Pieces;

import model.Color;

public abstract class Piece {
	private final Color COLOR;
	private final String NAME;
	
	protected Piece(Color color, String name) {
		COLOR = color;
		NAME = name;
	}
	
	public Color getColor() {
		return COLOR;
	}
	
	@Override
	public String toString() {
		return NAME;
	}
	public char toChar() {
		String caseName = COLOR == Color.black ? NAME.toUpperCase() : NAME;
		return caseName.charAt(0);
	}

}
