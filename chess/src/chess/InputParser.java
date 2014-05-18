package chess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commands.*;

import model.Color;
import model.PieceMap;
import model.Pieces.Piece;

public class InputParser {
	// board must be 8 x 8 for this regex to work
	private static Pattern movePattern = Pattern.compile("(?i)(?<place>(?<piece>[bkqnrp])(?<color>[ld])(?<placeLocation>([a-h][1-8])))|(?<move>((?<moveLocation1>[a-h][1-8]) (?<moveLocation2>[a-h][1-8])(?<capture>\\*?)(?<move2> (<moveLocation3>[a-h][1-8]) (<moveLocation4>[a-h][1-8]))?))");
	
	public static Command parseLine(String line) {
		String lowerCase = line.toLowerCase();
		Matcher m = movePattern.matcher(lowerCase);
		String plainEnglish = "invalid: " + lowerCase;
		Command command = null;
		
		if(m.matches()) {
			if(m.group("place") != null) {	
				Color color = (m.group("color").equalsIgnoreCase("l")) ? Color.white : Color.black;
				Piece p = PieceMap.getInstance(m.group("piece"), color);
				Location l = new Location(m.group("placeLocation"));
				command = new Place(p, l);
			}
			else { // m.group("move") != null
				Location from = new Location(m.group("moveLocation1"));
				Location to = new Location(m.group("moveLocation2"));
				command = (m.group("capture").equals("*")) ? new Capture(from, to) : new Move(from, to);
				//plainEnglish += (m.group("move2") != null) ? " and move whatever is at " + m.group("moveLocation3") + " to " + m.group("moveLocation4") : "";
			}
		}
		System.out.println(command == null ? "Invlid command: " + line : command);
		return command;
	}
}
