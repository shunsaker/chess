package chess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commands.*;

import model.Color;
import model.PieceMap;
import model.Pieces.Piece;

public class InputParser {
	// board must be 8 x 8 for this regex to work
	private static Pattern movePattern = Pattern.compile("(?i)(?<place>(?<piece>[bkqnrp])(?<color>[ld])(?<placeLocation>([a-h][1-8])))|(?<move>((?<moveLocation1>[a-h][1-8]) (?<moveLocation2>[a-h][1-8])(?<capture>\\*?)(?<move2> (?<moveLocation3>[a-h][1-8]) (?<moveLocation4>[a-h][1-8]))?))");
	
	private static Command placeParcer(Matcher m) {
		Color color = (m.group("color").equalsIgnoreCase("l")) ? Color.white : Color.black;
		Piece p = PieceMap.getInstance(m.group("piece"), color);
		Location l = new Location(m.group("placeLocation"));
		return new Place(p, l);
	}
	
	private static Command moveParcer(Matcher m) {
		MoveCMD command;
		Location from = new Location(m.group("moveLocation1"));
		Location to = new Location(m.group("moveLocation2"));
		command = (m.group("capture").equals("*")) ? new Capture(from, to) : new MoveCMD(from, to);
		if((m.group("move2") != null)) {
			Location subFrom = new Location(m.group("moveLocation3"));
			Location subTo =new Location( m.group("moveLocation4"));
			command.setSubmove(new MoveCMD(subFrom, subTo));
		}
		return command;
	}
	
	public static Command parseLine(String line) {
		String lowerCase = line.toLowerCase();
		Matcher m = movePattern.matcher(lowerCase);
		Command command = null;
		
		if(m.matches()) {
			if(m.group("place") != null) {	
				command = placeParcer(m);
			}
			else { // m.group("move") != null
				command = moveParcer(m);
			}
		}
		//System.out.println(command == null ? "Syntactically invlid command: " + line : command);
		return command;
	}
}
