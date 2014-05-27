package chess;

import java.util.ArrayList;
import java.util.List;

import commands.MoveCMD;
import model.*;
import model.Pieces.*;

public class BoardTools {
	
	public static boolean isPathClear(Board context, Location from, Location to) {
		boolean clear = true;
		RelativeLocation increment = getIncrementer(from, to);
		Location loc = to;
		do {
			loc = new Location(loc, increment);
			if(context.pieceAt(loc) != Board.EMPTY && !loc.equals(from)) {
				clear = false;
			}
		}
		while(!loc.equals(from));
		return clear;
	}

	private static RelativeLocation getIncrementer(Location from, Location to) {
		int rowDist = from.getRow() - to.getRow();
		int colDist = from.getCol() - to.getCol();
		
		int rowIncrement = 0;
		if(rowDist != 0) {
			rowIncrement = rowDist / Math.abs(rowDist);
		}
		
		int colIncrement = 0;
		if(colDist != 0) {
			colIncrement = colDist / Math.abs(colDist);
		}
		
		return new RelativeLocation(rowIncrement, colIncrement);
		
	}

	public static List<Location> validateSpecialMoves(Piece p, Location pieceLoc, Board context) {
		List<Location> specialMoves = new ArrayList<Location>();
		if(p instanceof King) {
			specialMoves = castleValidation((King) p, pieceLoc, context);
		}
		return specialMoves;
	}
	
	public static List<Location> castleValidation(King king, Location kingLoc, Board context) {
		List<Location> castleMoves = new ArrayList<Location>(); 
		if(!king.hasMoved() && !RuleChecks.isInCheck(king.getColor(), context)) {
			List<Location> locations = context.getLocations(king.getColor());
			for(Location loc : locations) {
				Piece p = context.pieceAt(loc);
				if(p instanceof Rook && !p.hasMoved() && BoardTools.isPathClear(context, kingLoc, loc)) {
					int rowDiff = 0;
					int colDiff = loc.getCol() - kingLoc.getCol();
					RelativeLocation towardsRook = new RelativeLocation(rowDiff,								
							colDiff == 0 ? colDiff : colDiff / Math.abs(colDiff)); 
					Location skippedSpot = new Location(kingLoc, towardsRook);
					Board testBoard = context.getCopy();
					testBoard.move(kingLoc, skippedSpot);
					if(!RuleChecks.isInCheck(king.getColor(), testBoard)) {
						castleMoves.add(new Location(skippedSpot, towardsRook));
					}
				}
			}
		}
		return castleMoves;
	}		

	public static List<Location> removeBlockedMoves(Location from, List<Location> possibleMoves, Board context) {
		List<Location> moves = new ArrayList<Location>();
		for(Location to : possibleMoves) {
			if(BoardTools.isPathClear(context, from, to)) {
				moves.add(to);
			}
		}
		return moves;
	}
	
	public static List<Location> removeSameColorConflics(Piece p, List<Location> possibleMoves, Board context) {
		List<Location> moves = new ArrayList<Location>();
		for(Location to : possibleMoves) {
			Piece test = context.pieceAt(to);
			if(p.getColor() != test.getColor()) {
				moves.add(to);
			}
		}
		return moves;
	}

	public static List<Location> getValidPawnMoves(Pawn p, Location pieceLoc, Board context) {
		List<Location> pawnMoves = new ArrayList<Location>();
		for(int i = 0; i < 2; i++) {
			MoveRules rule = i == 0 ? p.getCaptureRule() : p.getMoveRule();
			List<Location> potentialCaptures = LocationTools.getLocationsFromRule(rule, pieceLoc);
			for(Location toLoc : potentialCaptures) {
				MoveCMD move = new MoveCMD(pieceLoc, toLoc);
				if(RuleChecks.isValidNoPrint(move, p.getColor(), context) && RuleChecks.isMoveLegal(move, context)) {
					pawnMoves.add(toLoc);
				}
			}
		}
		return pawnMoves;
	}

	public static List<Location> removeCheckMoves(Location pieceLoc, Color turn, List<Location> possibleMoves, Board context) {
		List<Location> validMoves = new ArrayList<Location>();
		for(Location toLocation : possibleMoves) {
			Board copy = context.getCopy();
			copy.capture(pieceLoc, toLocation);
			if(!RuleChecks.isInCheck(turn, copy)) {
				validMoves.add(toLocation);
			}
		}
		return validMoves;
	}

	public static List<Location> getPiecesWithMoves(Color turn, Board context) {
		List<Location> piecesWithMoves = new ArrayList<Location>();
		List<Location> pieces = context.getLocations(turn);
		for(int i = 0; i < pieces.size(); i++) {
			Location loc = pieces.get(i);
			Piece p = context.pieceAt(loc);
			if(p.getValidMoves().size() > 0) {
				piecesWithMoves.add(loc);
			}
		}
		return piecesWithMoves;
	}

	public static void clearAllPiecesMoves(Board context) {
		List<Location> pieceLocs = context.getLocations(null);
		for(Location loc : pieceLocs) {
			Piece p = context.pieceAt(loc);
			p.setValidMoves(new ArrayList<Location>());
		}
	}

	public static void updateAllPieceMoves(Color turn, Board context) {
		List<Location> pieceLocs = context.getLocations(turn);
		for(Location loc : pieceLocs) {
			Piece p = context.pieceAt(loc);
			updatePieceMoves(p, loc, context);
		}
	}	
	
	private static void updatePieceMoves(Piece p, Location pieceLoc, Board context) {
		MoveRules rule = p.getMoveRule();
		List<Location> possibleMoves = LocationTools.getLocationsFromRule(rule, pieceLoc);
		
		if(rule.requiresClearPath()) {
			possibleMoves = BoardTools.removeBlockedMoves(pieceLoc, possibleMoves, context);
		}
		
		possibleMoves.addAll(BoardTools.validateSpecialMoves(p, pieceLoc, context));
		
		if(p instanceof Pawn) {
			possibleMoves = BoardTools.getValidPawnMoves((Pawn) p, pieceLoc, context);
		}
		possibleMoves = BoardTools.removeSameColorConflics(p, possibleMoves, context);
		possibleMoves = BoardTools.removeCheckMoves(pieceLoc, p.getColor(), possibleMoves, context);
		p.setValidMoves(possibleMoves);
	}
	
	public static void castling(MoveCMD m, Board context) {
		int rowDiff = m.getTo().getRow() - m.getFrom().getRow();
		int colDiff = m.getTo().getCol() - m.getFrom().getCol();
		if(rowDiff == 0 && Math.abs(colDiff) == 2) {
			int shortDist = 3;
			int longDist = 4;
			RelativeLocation offset = new RelativeLocation(rowDiff, 
					colDiff/Math.abs(colDiff));
			Location shortCastle = new Location(m.getFrom(), 
					new RelativeLocation(offset.getRow(), offset.getCol() * shortDist));
			Location longCastle = new Location(m.getFrom(), 
					new RelativeLocation(offset.getRow(), offset.getCol() * longDist));
			Location rookLocation = context.pieceAt(shortCastle) instanceof Rook ? shortCastle : longCastle;
			context.move(rookLocation, new Location(m.getFrom(), offset));
		}
	}
}
