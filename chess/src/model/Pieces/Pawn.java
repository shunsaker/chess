package model.Pieces;

import chess.MoveRules;
import chess.MoveType;
import model.Color;

public class Pawn extends Piece{
	private final MoveType CAPTURE_RULE, FIRST_RULE;

	public Pawn(Color color) {
		super(color, "pawn",
				(color == Color.black) ? MoveType.singleDown : MoveType.singleUp);
		CAPTURE_RULE = (color == Color.black) ? MoveType.singleDiagonalDown : MoveType.singleDiagonalUp;
		FIRST_RULE = (color == Color.black) ? MoveType.doubleDown : MoveType.doubleUp;
	}
	
	public MoveRules getCaptureRule() {
		return CAPTURE_RULE.getRule();
	}
	
	@Override
	public MoveRules getMoveRule() {
		return hasMoved ? super.getMoveRule() : FIRST_RULE.getRule();
	}
	
}
