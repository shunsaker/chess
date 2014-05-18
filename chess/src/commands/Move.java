package commands;

import model.Pieces.Piece;
import chess.Location;

public class Move extends Command{
	private final Location FROM, TO;
	
	public Move(Location from, Location to) {
		FROM = from;
		TO = to;
		log = "Move whatever is at " + from + " to " + to;
	}
	
	public Location getFrom() {
		return FROM;
	}
	
	public Location getTO() {
		return TO;
	}
}
