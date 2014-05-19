package commands;

import chess.Location;

public class MoveCMD extends Command{
	private final Location FROM, TO;
	private MoveCMD submove;
	
	public MoveCMD(Location from, Location to) {
		FROM = from;
		TO = to;
		log = "Move whatever is at " + from + " to " + to;
	}
	
	public Location getFrom() {
		return FROM;
	}
	
	public Location getTo() {
		return TO;
	}
	
	public void setSubmove(MoveCMD sub) {
		submove = sub;
		log += " and move whatever is at " + sub.FROM + " to " + sub.TO;
	}
	
	public MoveCMD getSubmove() {
		return submove;
	}
}
