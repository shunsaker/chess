package model.Pieces;

import chess.MoveRules;
import chess.MoveType;
import model.Color;

public abstract class Piece {
	private final Color COLOR;
	private final String NAME;
	private final MoveType RULE;
	protected boolean hasMoved = false;
	
	protected Piece(Color color, String name, MoveType rule) {
		COLOR = color;
		NAME = name;
		RULE = rule;
	}
	
	public Color getColor() {
		return COLOR;
	}
	
	public MoveRules getMoveRule() {
		return RULE.getRule();
	}
	
	public void moved() {
		hasMoved = true;
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
