package chess;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import model.Board;
import model.Color;
import model.Pieces.King;
import model.Pieces.Pawn;
import model.Pieces.Piece;
import model.Pieces.Rook;
import view.ConsoleDisplay;
import view.Display;
import view.GuiDisplay;

import commands.Capture;
import commands.Command;
import commands.MoveCMD;
import commands.Place;

import fileIO.BufferedFileReader;

public class GameController {
	private static final boolean GUI_DISPLAY = false;
	private Display display = GUI_DISPLAY ? new GuiDisplay() : new ConsoleDisplay();
	private Board board = new Board();
	private Deque<Command> moves = new ArrayDeque<Command>();
	private Color currentTurn = Color.white;
	
	public GameController(File commandFile) {
		BufferedFileReader reader = new BufferedFileReader(commandFile);
		while(reader.hasNext()) {
			Command c = InputParser.parseLine(reader.next());
			if(c != null) {
				moves.add(c);
			}
		}
		reader.close();
	}
	
	private void setupBoard() {
		String[] placements = {"rda8", "ndb8", "bdc8", "qdd8", "kde8", "bdf8", "ndg8", "rdh8", 
							   "pda7", "pdb7", "pdc7", "pdd7", "pde7", "pdf7", "pdg7", "pdh7",
							   "pla2", "plb2", "plc2", "pld2", "ple2", "plf2", "plg2", "plh2",
							   "rla1", "nlb1", "blc1", "qld1", "kle1", "blf1", "nlg1", "rlh1" };
		for(String place : placements) {
			Command c = InputParser.parseLine(place);
			executeCommand(c);
		}	
	}
	
	public void doPlaceMoves() {
		boolean placed = false;
		Command c = null;
		do {
			c = moves.poll();
			if(c instanceof Place) {
				Place place = (Place) c;
				board.place(place.getPiece(), place.getLocation());
				System.out.println(c);
				placed = true;
			}
		}
		while(c != null && c instanceof Place);
		
		if(!placed) {
			setupBoard();
		}
		if(c != null) {
			moves.addFirst(c);
		}
	}
	
	public void driver() {
		doPlaceMoves();
		
		boolean running = true;
		Command c = null;
		do {
			display.displayBoard(board);
			c = moves.poll();
			if(c instanceof MoveCMD && ((MoveCMD) c).getFrom().toString().equalsIgnoreCase("H8") && ((MoveCMD) c).getTo().toString().equalsIgnoreCase("A8")) {
				System.out.println("Debug");
			}
			
			updateAllPieceMoves();
			running = takeTurn(c);
		}
		while(c != null && running);
		
		if(!running) {
			display.displayBoard(board);
		}
	}
	
	public boolean takeTurn(Command c) {
		boolean running = playerCanMove(currentTurn);
		if(running) {
			if(isInCheck(currentTurn)) {
				System.out.println("Check!");
			}
			executeCommand(c);
		}
		else {
			endGame();
		}
		return running;
	}
	
	public void endGame() {
		if(isInCheck(currentTurn)) {
			System.out.println("Checkmate! " + (currentTurn == Color.black ? Color.white : Color.black) +  " wins!");
		}
		else {
			System.out.println("Stalemate");
		}
	}

	public void executeCommand(Command c) {
		if(c != null) System.out.println(c);
		if(c instanceof Place) {
			Place place = (Place) c;
			board.place(place.getPiece(), place.getLocation());
		}
		else if(c instanceof MoveCMD) {
			MoveCMD move = (MoveCMD) c;
			if(move.getFrom().toString().equalsIgnoreCase("e1") && move.getTo().toString().equalsIgnoreCase("c1")) {
				System.out.println("debug");
			}
			Piece p = board.pieceAt(move.getFrom());
			if(validateMove(move) && 
					p.getValidMoves().contains(move.getTo())) {
				executeSpecialMove(p, move);
				board.capture(move.getFrom(), move.getTo());
				board.pieceAt(move.getTo()).moved();
				currentTurn = (currentTurn == Color.white) ? Color.black : Color.white;
			}
			else {
				System.err.println("Invalid Move!");
			}
		}
	}
	
	private boolean playerCanMove(Color color) {
		boolean canMove = false;
		List<Piece> pieces = board.getPieces(color);
		for(int i = 0; i < pieces.size() && !canMove; i++) {
			if(pieces.get(i).getValidMoves().size() > 0) {
				canMove = true;
			}
		}
		return canMove;
	}

	private void updateAllPieceMoves() {
		List<Location> pieceLocs = board.getLocations(currentTurn);
		for(Location loc : pieceLocs) {
			Piece p = board.pieceAt(loc);
			updatePieceMoves(p, loc);
		}
	}
	
	private void updatePieceMoves(Piece p, Location pieceLoc) {
		MoveRules rule = p.getMoveRule();
		List<Location> possibleMoves = getLocationsFromRule(rule, pieceLoc);
		
		if(rule.requiresClearPath()) {
			possibleMoves = removeBlockedMoves(pieceLoc, possibleMoves);
		}
		
		possibleMoves.addAll(validateSpecialMoves(p, pieceLoc));
		
		if(p instanceof Pawn) {
			possibleMoves = getValidPawnMoves((Pawn) p, pieceLoc);
		}
		possibleMoves = removeSameColorConflics(p, possibleMoves);
		possibleMoves = removeCheckMoves(pieceLoc, p.getColor(), possibleMoves);
		p.setValidMoves(possibleMoves);
	}
	
	private List<Location> getValidPawnMoves(Pawn p, Location pieceLoc) {
		List<Location> pawnMoves = new ArrayList<Location>();
		for(int i = 0; i < 2; i++) {
			MoveRules rule = i == 0 ? p.getCaptureRule() : p.getMoveRule();
			List<Location> potentialCaptures = getLocationsFromRule(rule, pieceLoc);
			for(Location toLoc : potentialCaptures) {
				MoveCMD move = new MoveCMD(pieceLoc, toLoc);
				if(validateMoveNoPrint(move, board) && isMoveLegal(move)) {
					pawnMoves.add(toLoc);
				}
			}
		}
		return pawnMoves;
	}

	private List<Location> validateSpecialMoves(Piece p, Location pieceLoc) {
		List<Location> specialMoves = new ArrayList<Location>();
		if(p instanceof King) {
			specialMoves = castleValidation((King) p, pieceLoc);
		}
		return specialMoves;
	}
	

	private List<Location> castleValidation(King king, Location kingLoc) {
		List<Location> castleMoves = new ArrayList<Location>(); 
		if(!king.hasMoved() && !isInCheck(king.getColor())) {
			List<Location> locations = board.getLocations(king.getColor());
			for(Location loc : locations) {
				Piece p = board.pieceAt(loc);
				if(p instanceof Rook && !p.hasMoved() && board.isPathClear(kingLoc, loc)) {
					int rowDiff = 0;
					int colDiff = loc.getCol() - kingLoc.getCol();
					RelativeLocation towardsRook = new RelativeLocation(rowDiff,								
							colDiff == 0 ? colDiff : colDiff / Math.abs(colDiff)); 
					Location skippedSpot = new Location(kingLoc, towardsRook);
					Board testBoard = board.getCopy();
					testBoard.move(kingLoc, skippedSpot);
					if(!isInCheck(king.getColor(), testBoard)) {
						castleMoves.add(new Location(skippedSpot, towardsRook));
					}
				}
			}
		}
		return castleMoves;
	}		
	

	private void executeSpecialMove(Piece p, MoveCMD m) {
		if(p instanceof King) { //castling
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
				Location rookLocation = board.pieceAt(shortCastle) instanceof Rook ? shortCastle : longCastle;
				board.move(rookLocation, new Location(m.getFrom(), offset));
			}
		}
		
	}

	private boolean validateMoveNoPrint(MoveCMD move, Board copy) {
		return validateMove(move, false, copy);
	}
	

	private boolean validateMove(MoveCMD move) {
		return validateMove(move, true, board);
	}
	

	private boolean validateMove(MoveCMD move, boolean print, Board board) {
		Piece toMove = board.pieceAt(move.getFrom());
		Piece toCapture = board.pieceAt(move.getTo());
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

	private boolean isMoveLegal(MoveCMD move) {
		return isMoveLegal(move, board);
	}


	private boolean isMoveLegal(MoveCMD move, Board board) {
		Piece toMove = board.pieceAt(move.getFrom());
		Piece toCapture = board.pieceAt(move.getTo());
		boolean capture = move instanceof Capture || toCapture != Board.EMPTY;
		MoveRules rule = (toMove instanceof Pawn && capture) ? ((Pawn)toMove).getCaptureRule() : toMove.getMoveRule();
		List<Location> possibleMoves = getLocationsFromRule(rule, move.getFrom());
		if(rule.requiresClearPath()) {
			possibleMoves = removeBlockedMoves(move.getFrom(), possibleMoves, board);
		} 
		return possibleMoves.contains(move.getTo());

	}
	

	private boolean isInCheck(Color kingColor) {
		return isInCheck(kingColor, this.board);
	}
	

	private boolean isInCheck(Color kingColor, Board board) {
		boolean check = false;
		Location kingLoc = board.getKingLocation(kingColor);
		if(kingLoc != null) {
			List<Location> dangerZone = getLocationsFromRule(MoveType.allMove.getRule(), kingLoc);
			for(int i = 0; i < dangerZone.size() && !check; i++) {
				MoveCMD move = new MoveCMD(dangerZone.get(i), kingLoc);
				if(validateMoveNoPrint(move, board) && isMoveLegal(move, board)) {
					check = true;
				}
			}
		}
		return check;
	}
	
	private List<Location> removeBlockedMoves(Location from, List<Location> possibleMoves) {
		return removeBlockedMoves(from, possibleMoves, board);
	}


	private List<Location> removeBlockedMoves(Location from, List<Location> possibleMoves, Board board) {
		List<Location> moves = new ArrayList<Location>();
		for(Location to : possibleMoves) {
			if(board.isPathClear(from, to)) {
				moves.add(to);
			}
		}
		return moves;
	}
	

	private List<Location> removeSameColorConflics(Piece p, List<Location> possibleMoves) {
		List<Location> moves = new ArrayList<Location>();
		for(Location to : possibleMoves) {
			Piece test = board.pieceAt(to);
			if(p.getColor() != test.getColor()) {
				moves.add(to);
			}
		}
		return moves;
	}
	

	private List<Location> removeCheckMoves(Location pieceLoc, Color color, List<Location> possibleMoves) {
		List<Location> validMoves = new ArrayList<Location>();
		for(Location toLocation : possibleMoves) {
			Board copy = board.getCopy();
			copy.capture(pieceLoc, toLocation);
			if(!isInCheck(color, copy)) {
				validMoves.add(toLocation);
			}
		}
		return validMoves;
	}
	

	private List<Location> getLocationsFromRule(MoveRules rule, Location loc) {
		Set<RelativeLocation> relativeMoves = rule.getAllOffsets();
		return relativeToActualLocations(loc, relativeMoves);
	}


	private List<Location> relativeToActualLocations(Location loc, Set<RelativeLocation> rels) {
		List<Location> locations = new ArrayList<Location>();
		for(RelativeLocation rel : rels) {
			Location l = new Location(loc, rel);
			if(l.isOnBoard()) {
				locations.add(l);
			}
		}
		
		return locations;
	}
	
}
