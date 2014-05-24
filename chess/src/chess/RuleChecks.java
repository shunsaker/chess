package chess;

import java.util.List;

import model.Board;
import model.Color;
import model.Pieces.Pawn;
import model.Pieces.Piece;
import commands.Capture;
import commands.MoveCMD;

public class RuleChecks {

	public static boolean isValidNoPrint(MoveCMD move, Color currentTurn, Board copy) {
		return isValidMove(move, false, currentTurn, copy);
	}
	
	public static boolean isValidMove(MoveCMD move, Color currentTurn, Board context) {
		return isValidMove(move, true, currentTurn, context);
	}
	
	private static boolean isValidMove(MoveCMD move, boolean print, Color currentTurn, Board context) {
		Piece toMove = context.pieceAt(move.getFrom());
		Piece toCapture = context.pieceAt(move.getTo());
		String errorMessage = null;
		
		if(toMove == Board.EMPTY) {
			errorMessage = "No piece selected";
		}
		else if(toMove.getColor() != currentTurn && print) {
			errorMessage = "Out of turn play";
		}
		else if(toCapture.getColor() == toMove.getColor()) {
			errorMessage = "Space Occupided";
		}
		if(errorMessage != null && print) {
			System.err.println(errorMessage);
		}
		
		return errorMessage == null;
	}

	public static boolean isMoveLegal(MoveCMD move, Board context) {
		Piece toMove = context.pieceAt(move.getFrom());
		Piece toCapture = context.pieceAt(move.getTo());
		boolean capture = move instanceof Capture || toCapture != Board.EMPTY;
		MoveRules rule = (toMove instanceof Pawn && capture) ? ((Pawn)toMove).getCaptureRule() : toMove.getMoveRule();
		List<Location> possibleMoves = LocationTools.getLocationsFromRule(rule, move.getFrom());
		if(rule.requiresClearPath()) {
			possibleMoves = BoardTools.removeBlockedMoves(move.getFrom(), possibleMoves, context);
		} 
		return possibleMoves.contains(move.getTo());

	}	

	public static boolean isInCheck(Color kingColor, Board board) {
		boolean check = false;
		Location kingLoc = board.getKingLocation(kingColor);
		if(kingLoc != null) {
			List<Location> dangerZone = LocationTools.getLocationsFromRule(MoveType.allMove.getRule(), kingLoc);
			for(int i = 0; i < dangerZone.size() && !check; i++) {
				MoveCMD move = new MoveCMD(dangerZone.get(i), kingLoc);
				check = isValidNoPrint(move, kingColor, board) && isMoveLegal(move, board);
			}
		}
		return check;
	}
}
