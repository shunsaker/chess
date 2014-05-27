package chess;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import inputOutput.*;
import model.*;
import model.Pieces.*;
import commands.*;

public class GameController implements Observer{
	private static final boolean GUI_DISPLAY = true;
	private static final int TURN_LENGTH = 250;
	private Display display = GUI_DISPLAY ? new GuiDisplay() : new InteractiveConsoleDisplay();
	private Board board = new Board();
	private Deque<Command> moves = new ArrayDeque<Command>();
	private Color currentTurn = Color.white;
	private boolean sleep;
	
	
	public GameController(File commandFile) {
		try{
			BufferedFileReader reader = new BufferedFileReader(commandFile);
			while(reader.hasNext()) {
				Command command = InputParser.parseLine(reader.next());
				if(command != null) {
					moves.add(command);
				}
			}
			reader.close();
		}
		catch(RuntimeException e) {
			System.err.println("File not found");
		}
		
		if(GUI_DISPLAY) {
			setupGui();
		};
		sleep = true;
	}
	
	public GameController() {
		if(GUI_DISPLAY) {
			setupGui();
		}
		sleep = false;
	}
	
	private void setupGui() {
		display.addObserver(this);
		display.displayBoard(board, null, new ArrayList<Location>());
		JFrame frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(((GuiDisplay) display).getBoardPanel());
		frame.pack();
		frame.setVisible(true);
		frame.getContentPane().repaint();
	}
	
	private void setupBoard() {
		String[] placements = {"rda8", "ndb8", "bdc8", "qdd8", "kde8", "bdf8", "ndg8", "rdh8", 
							   "pda7", "pdb7", "pdc7", "pdd7", "pde7", "pdf7", "pdg7", "pdh7",
							   "pla2", "plb2", "plc2", "pld2", "ple2", "plf2", "plg2", "plh2",
							   "rla1", "nlb1", "blc1", "qld1", "kle1", "blf1", "nlg1", "rlh1"};
		for(String cmd : placements) {
			Place place =  (Place) InputParser.parseLine(cmd);
			board.place(place.getPiece(), place.getLocation());
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
	
	public synchronized void driver() {
		doPlaceMoves();
		
		boolean running = true;
		Command command = null;
		do {
			command = moves.poll();
			BoardTools.updateAllPieceMoves(currentTurn, board);
			List<Location> piecesWithMoves = BoardTools.getPiecesWithMoves(currentTurn, board);
			running = piecesWithMoves.size() > 0;
			if(running) {
				takeTurn(piecesWithMoves, command);
			}
			BoardTools.clearAllPiecesMoves(board);
		}
		while(running);
		
		if(!running) {
			display.displayBoard(board, null, new ArrayList<Location>());
			endGame();
		}
	}	
	
	private void takeTurn(List<Location> piecesWithMoves, Command command) {
		display.displayBoard(board, null, piecesWithMoves);
		if(RuleChecks.isInCheck(currentTurn, board)) {
			display.notifyCheck();
		}
		if(command == null) {
			command = getCommand(piecesWithMoves);
			sleep = false;
		}
		else {
			if(sleep) {
				try {
					TimeUnit.MILLISECONDS.sleep(TURN_LENGTH);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		executeCommand(command);
	}

	private Command getCommand(List<Location> piecesWithMoves) {
		Command command = null;
		if(GUI_DISPLAY) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else {
			command = promptForMove(piecesWithMoves);
		}
		return command;
	}

	private MoveCMD promptForMove(List<Location> piecesWithMoves) {
		Location from = ConsoleInput.getLocation(piecesWithMoves, "Select a piece", "Invalid piece!");
		List<Location> validMoves = board.pieceAt(from).getValidMoves();
		display.displayBoard(board, from, validMoves);
		Location to = ConsoleInput.getLocation(validMoves, "Select a move", "Invalid move!");
		return new MoveCMD(from, to);
	}

	public void endGame() {
		String message;
		if(RuleChecks.isInCheck(currentTurn, board)) {
			message = "Checkmate! " + (currentTurn == Color.black ? Color.white : Color.black) +  " wins!";
		}
		else {
			message = "Stalemate";
		}
		display.notifyEndofGame(message);
	}

	public void executeCommand(Command command) {
		if(command != null && !GUI_DISPLAY) System.out.println(command);
		if(command instanceof Place) {
			Place place = (Place) command;
			board.place(place.getPiece(), place.getLocation());
		}
		else if(command instanceof MoveCMD) {
			MoveCMD move = (MoveCMD) command;
			Piece p = board.pieceAt(move.getFrom());
			if(RuleChecks.isValidMove(move, currentTurn, board) && 
					p.getValidMoves().contains(move.getTo())) {
				board.capture(move.getFrom(), move.getTo());
				executeSpecialMove(p, move);
				board.pieceAt(move.getTo()).moved();
				currentTurn = (currentTurn == Color.white) ? Color.black : Color.white;
			}
			else {
				if(!GUI_DISPLAY) {
					System.err.println("Invalid Move!");
				}
			}
		}
	}
	
	private void executeSpecialMove(Piece p, MoveCMD m) {
		if(p instanceof King) { //castling
			BoardTools.castling(m, board);
		}
		if(p instanceof Pawn) { // pawn promotion
			Location moveTo = m.getTo();
			if(moveTo.getRow() == 0  || moveTo.getRow() == Board.SIZE - 1) {
				Piece promotion= display.getPawnPromotion(p.getColor());
				board.place(promotion, moveTo);
			}
		}
	}
	
	@Override
	public synchronized void update(Observable o, Object arg) {
		sleep = false;
		notifyAll();
		moves.add((Command)arg);
	}
	
}
