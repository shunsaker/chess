package chess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import view.ConsoleDisplay;
import view.Display;
import view.GuiDisplay;
import commands.*;
import fileIO.BufferedFileReader;
import model.Board;
import model.Color;
import model.Pieces.Pawn;
import model.Pieces.Piece;

public class GameController {
	private static final boolean GUI_DISPLAY = false;
	private Display display = GUI_DISPLAY ? new GuiDisplay() : new ConsoleDisplay();
	private Board board = new Board();
	private List<Command> moves = new ArrayList<Command>();
	private Color currentTurn = Color.white;
	
	public GameController(File commandFile, boolean setup) {
		BufferedFileReader reader = new BufferedFileReader(commandFile);
		while(reader.hasNext()) {
			Command c = InputParser.parseLine(reader.next());
			if(c != null) {
				moves.add(c);
			}
		}
		reader.close();
		if(setup) {
			setupBoard();
		}
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
	
	public void driver() {
		System.out.println();
		for(Command c : moves) {
			//System.out.println(c);
			executeCommand(c);
			//display.displayBoard(board);
		}
		
		if(isInCheck(Color.white)) {
			System.out.println("White king in check");
		}
		else if(isInCheck(Color.black)){ 
			System.out.println("Black King in check");
		}
	}
	
	public void executeCommand(Command c) {
		if(c instanceof Place) {
			Place place = (Place) c;
			board.place(place.getPiece(), place.getLocation());
		}
		else if(c instanceof MoveCMD) {
			MoveCMD move = (MoveCMD) c;
			
			if(isMoveValid(move)){
				board.move(move.getFrom(), move.getTo());
				board.pieceAt(move.getTo()).moved();
				currentTurn = (currentTurn == Color.white) ? Color.black : Color.white;
			}
		}
	}
	
	private boolean isMoveValid(MoveCMD move) {
		return isMoveValid(move, true);
	}
	
	private boolean isMoveValidNoPrint(MoveCMD move) {
		return isMoveValid(move, false);
	}
	
	private boolean isMoveValid(MoveCMD move, boolean print) {
		Piece toMove = board.pieceAt(move.getFrom());
		Piece toCapture = board.pieceAt(move.getTo());
		boolean valid = false;
		String errorMessage = null;

		if(toMove == Board.EMPTY) {
			errorMessage = "No piece selected";
		}
		else if(toMove.getColor() != currentTurn) {
			errorMessage = "Out of turn play";
		}
		else if(toCapture.getColor() == toMove.getColor()) {
			errorMessage = "Space Occupided";
		}
		else {
			boolean capture = move instanceof Capture || toCapture != Board.EMPTY;
			MoveRules rule = (toMove instanceof Pawn && capture) ? ((Pawn)toMove).getCaptureRule() : toMove.getMoveRule();
			valid = isMoveLegal(move, rule);
			
			if(!valid) {
				errorMessage = "Illegal Move";
			}
		}
		if(errorMessage != null && print) {
			System.out.println(errorMessage);
		}
		return valid;
	}

	private boolean isMoveLegal(MoveCMD move, MoveRules rule) {
		List<Location> possibleMoves = getLocationsFromRule(rule, move.getFrom());
		if(rule.requiresClearPath()) {
			possibleMoves = removeBlockedMoves(move.getFrom(), possibleMoves);
		}
		return possibleMoves.contains(move.getTo());

	}
	
	private boolean isInCheck(Color kingColor) {
		boolean check = false;
		Location kingLoc = board.getKingLocation(kingColor);
		List<Location> dangerLocations = getLocationsFromRule(MoveType.allMove.getRule(), kingLoc);
		for(int i = 0; i < dangerLocations.size(); i++) {
			MoveCMD move = new MoveCMD(dangerLocations.get(i), kingLoc);
			if(isMoveValidNoPrint(move)) {
				check = true;
			}
		}
		return check;
	}

	private List<Location> removeBlockedMoves(Location from, List<Location> possibleMoves) {
		List<Location> moves = new ArrayList<Location>();
		for(Location to : possibleMoves) {
			if(board.isPathClear(from, to)) {
				moves.add(to);
			}
		}
		return moves;
	}
	
	private List<Location> getLocationsFromRule(MoveRules rule, Location loc) {
		Set<RelativeLocation> relativeMoves = rule.getAllOffsets(Board.SIZE);
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
	
//	private void printAllMoveTypes() {
//		System.out.println("\n");
//		for(MoveType mt : MoveType.values()) {
//			System.out.print(mt + " ");
//			Set<RelativeLocation> offsets = mt.getRule().getAllOffsets(Board.SIZE);
//			for(RelativeLocation o : offsets) {
//				System.out.print(o + " ");
//			}
//			System.out.println();
//		}
//	}
}
