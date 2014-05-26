package model.Pieces;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import model.Color;
import chess.Location;
import chess.MoveRules;
import chess.MoveType;

public abstract class Piece {
	private final Color COLOR;
	private final String NAME;
	private final MoveType RULE;
	private final ImageIcon IMAGE;
	private List<Location> validMoves = new ArrayList<Location>();
	protected boolean hasMoved = false;
	
	protected Piece(Color color, String name, MoveType rule, ImageIcon image) {
		COLOR = color;
		NAME = name;
		RULE = rule;
		IMAGE = image;
	}
	
	public Color getColor() {
		return COLOR;
	}
	
	public ImageIcon getImage() {
		return IMAGE;
	}
	
	public MoveRules getMoveRule() {
		return RULE.getRule();
	}
	
	public void moved() {
		hasMoved = true;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void setValidMoves(List<Location> moves) {
		validMoves = moves;
	}
	
	public List<Location> getValidMoves() {
		return validMoves;
	}
	
	@Override
	public String toString() {
		return NAME;
	}
	public char toChar() {
		String caseName = COLOR == Color.white ? NAME.toUpperCase() : NAME;
		return caseName.charAt(0);
	}

}
