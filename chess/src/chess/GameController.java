package chess;

import inputOutput.BufferedFileReader;
import inputOutput.ConsoleInput;
import inputOutput.Display;
import inputOutput.GuiDisplay;
import inputOutput.InteractiveConsoleDisplay;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

import model.Board;
import model.Color;
import model.Pieces.King;
import model.Pieces.Pawn;
import model.Pieces.Piece;
import model.Pieces.Rook;
import commands.Command;
import commands.MoveCMD;
import commands.Place;

public class GameController {
	private static final boolean GUI_DISPLAY = false;
	private Display display = GUI_DISPLAY ? new GuiDisplay() : new InteractiveConsoleDisplay();
	private Board board = new Board();
	private Deque<Command> moves = new ArrayDeque<Command>();
	private Color currentTurn = Color.white;
	private static final Scanner SCAN = new Scanner(System.in);
	
	public GameController(File commandFile) {
		BufferedFileReader reader = new BufferedFileReader(commandFile);
		while(reader.hasNext()) {
			Command command = InputParser.parseLine(reader.next());
			if(command != null) {
				moves.add(command);
			}
		}
		reader.close();
	}
	
	public GameController() {
		
	}
	
	private void setupBoard() {
		String[] placements = {"rda8", "ndb8", "bdc8", "qdd8", "kde8", "bdf8", "ndg8", "rdh8", 
							   "pda7", "pdb7", "pdc7", "pdd7", "pde7", "pdf7", "pdg7", "pdh7",
							   "pla2", "plb2", "plc2", "pld2", "ple2", "plf2", "plg2", "plh2",
							   "rla1", "nlb1", "blc1", "qld1", "kle1", "blf1", "nlg1", "rlh1" };
		for(String place : placements) {
			Command command = InputParser.parseLine(place);
			executeCommand(command);
		}	
	}
	
	public void doPlaceMoves() {
		boolean placed = false;
		Command command = null;
		do {
			command = moves.poll();
			if(command instanceof Place) {
				Place place = (Place) command;
				board.place(place.getPiece(), place.getLocation());
				System.out.println(command);
				placed = true;
			}
		}
		while(command != null && command instanceof Place);
		
		if(!placed) {
			setupBoard();
		}
		if(command != null) {
			moves.addFirst(command);
		}
	}
	
	public void driver() {
		doPlaceMoves();
		
		boolean running = true;
		Command command = null;
		do {
			command = moves.poll();
			
			updateAllPieceMoves();
			List<Location> piecesWithMoves = BoardTools.getPiecesWithMoves(currentTurn, board);
			running = piecesWithMoves.size() > 0; //playerCanMove(currentTurn);
			if(running) {
				
				display.displayBoard(board, null, piecesWithMoves);
				if(RuleChecks.isInCheck(currentTurn, board)) {
					System.out.println("Check!");
				}
				if(command == null) {
					command = promptForMove(piecesWithMoves);
				}
				executeCommand(command);
			}
			clearAllPiecesMoves();
		}
		while(command != null && running);
		
		if(!running) {
			display.displayBoard(board, null, new ArrayList<Location>());
			endGame();
		}
	}	
	
	private MoveCMD promptForMove(List<Location> piecesWithMoves) {
		Location from = ConsoleInput.getLocation(piecesWithMoves, "Select a piece", "Invalid piece!");
		List<Location> validMoves = board.pieceAt(from).getValidMoves();
		display.displayBoard(board, from, validMoves);
		Location to = ConsoleInput.getLocation(validMoves, "Select a move", "Invalid move!");
		return new MoveCMD(from, to);
	}

	public void endGame() {
		if(RuleChecks.isInCheck(currentTurn, board)) {
			System.out.println("Checkmate! " + (currentTurn == Color.black ? Color.white : Color.black) +  " wins!");
		}
		else {
			System.out.println("Stalemate");
		}
	}

	public void executeCommand(Command command) {
		if(command != null) System.out.println(command);
		if(command instanceof Place) {
			Place place = (Place) command;
			board.place(place.getPiece(), place.getLocation());
		}
		else if(command instanceof MoveCMD) {
			MoveCMD move = (MoveCMD) command;
			if(move.getFrom().toString().equalsIgnoreCase("e1") && move.getTo().toString().equalsIgnoreCase("c1")) {
				System.out.println("debug");
			}
			Piece p = board.pieceAt(move.getFrom());
			if(RuleChecks.isValidMove(move, currentTurn, board) && 
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
	
//	private boolean playerCanMove(Color color) {
//		boolean canMove = false;
//		List<Piece> pieces = board.getPieces(color);
//		for(int i = 0; i < pieces.size() && !canMove; i++) {
//			if(pieces.get(i).getValidMoves().size() > 0) {
//				canMove = true;
//			}
//		}
//		return canMove;
//	}

	private void updateAllPieceMoves() {
		List<Location> pieceLocs = board.getLocations(currentTurn);
		for(Location loc : pieceLocs) {
			Piece p = board.pieceAt(loc);
			updatePieceMoves(p, loc);
		}
	}	
	
	private void updatePieceMoves(Piece p, Location pieceLoc) {
		MoveRules rule = p.getMoveRule();
		List<Location> possibleMoves = LocationTools.getLocationsFromRule(rule, pieceLoc);
		
		if(rule.requiresClearPath()) {
			possibleMoves = BoardTools.removeBlockedMoves(pieceLoc, possibleMoves, board);
		}
		
		possibleMoves.addAll(BoardTools.validateSpecialMoves(p, pieceLoc, board));
		
		if(p instanceof Pawn) {
			possibleMoves = BoardTools.getValidPawnMoves((Pawn) p, pieceLoc, board);
		}
		possibleMoves = BoardTools.removeSameColorConflics(p, possibleMoves, board);
		possibleMoves = BoardTools.removeCheckMoves(pieceLoc, p.getColor(), possibleMoves, board);
		p.setValidMoves(possibleMoves);
	}
	
	private void clearAllPiecesMoves() {
		List<Location> pieceLocs = board.getLocations(currentTurn);
		for(Location loc : pieceLocs) {
			Piece p = board.pieceAt(loc);
			p.setValidMoves(new ArrayList<Location>());
		}
	}
}
