package commands;

import chess.Location;

public class Capture extends MoveCMD{
	
	public Capture(Location from, Location to) {
		super(from, to);
		log = "Move whatever is at " + from + " to " + to + " and capture the piece there";
	}
	
	public Capture(MoveCMD move) {
		this(move.getFrom(), move.getTo());
	}

}
