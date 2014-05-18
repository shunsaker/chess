package commands;

import chess.Location;

public class Capture extends Command{
	private final Location FROM, TO;
	
	public Capture(Location from, Location to) {
		FROM = from;
		TO = to;
		log = "Move whatever is at " + from + " to " + to + " and capture the piece there";
	}
	
	public Location getFrom() {
		return FROM;
	}
	
	public Location getTO() {
		return TO;
	}
}
