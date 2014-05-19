package chess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	public GameController(File commandFile) {
		BufferedFileReader reader = new BufferedFileReader(commandFile);
		while(reader.hasNext()) {
			Command c = InputParser.parseLine(reader.next());
			if(c != null) {
				moves.add(c);
			}
		}
		reader.close();
		setupBoard();
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
			System.out.println(c);
			executeCommand(c);
			display.displayBoard(board);
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
		Piece toMove = board.pieceAt(move.getFrom());
		Piece toCapture = board.pieceAt(move.getTo());
		boolean valid = false;

		if(toMove == Board.EMPTY) {
			System.out.println("No piece selected");
		}
		else if(toMove.getColor() != currentTurn) {
			System.out.println("Out of turn play");
		}
		else if(toCapture.getColor() == toMove.getColor()) {
			System.out.println("Space Occupided");
		}
		else {
			boolean capture = move instanceof Capture || toCapture != Board.EMPTY;
			MoveRules rule = (toMove instanceof Pawn && capture) ? ((Pawn)toMove).getCaptureRule() : toMove.getMoveRule();
			valid = isMoveLegal(move, rule);
			
			if(!valid) {
				System.out.println("Illegal Move");
			}
		}
		return valid;
	}
	
	private boolean isMoveLegal(MoveCMD move, MoveRules rule) {
		RelativeLocation[] relativeMoves = rule.getAllOffsets(Board.SIZE);
		
		List<Location> possibleMoves = relativeToActualLocations(move.getFrom(), relativeMoves);
		if(rule.requiresClearPath()) {
			possibleMoves = removeBlockedMoves(move.getFrom(), possibleMoves);
		}
		return possibleMoves.contains(move.getTo());

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

	private List<Location> relativeToActualLocations(Location loc, RelativeLocation[] rels) {
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
//			RelativeLocation[] offsets = mt.getRule().getAllOffsets(Board.SIZE);
//			for(RelativeLocation o : offsets) {
//				System.out.print(o + " ");
//			}
//			System.out.println();
//		}
//	}
}
