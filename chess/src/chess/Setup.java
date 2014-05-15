package chess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Setup {
	// board must be 8 x 8 for this regex to work
	private static Pattern movePattern = Pattern.compile("(?i)(?<place>(?<piece>[bkqnrp])(?<color>[ld])(?<placeLocation>([a-h][1-8])))|(?<move>((?<moveLocation1>[a-h][1-8]) (?<moveLocation2>[a-h][1-8])(?<capture>\\*?)(?<move2> (<moveLocation3>[a-h][1-8]) (<moveLocation4>[a-h][1-8]))?))");
	private static final Map<String, String> PIECE_MAP = new HashMap<String, String>();
	
	static {
		PIECE_MAP.put("r", "Rook");
		PIECE_MAP.put("n", "Knight");
		PIECE_MAP.put("q", "Queen");
		PIECE_MAP.put("b", "Bishop");
		PIECE_MAP.put("p", "Pawn");
		PIECE_MAP.put("k", "King");
	}
	
	public static void parseLine(String line) {
		Matcher m = movePattern.matcher(line);
		String plainEnglish = "invalid: " + line;
		if(m.matches()) {
			line = line.toLowerCase();
			if(m.group("place") != null) {	
				String color = (m.group("color").equalsIgnoreCase("l")) ? "White" : "Black";
				plainEnglish = "Place " + color + " " + PIECE_MAP.get(m.group("piece")) + " at " + m.group("placeLocation");
			}
			else { // m.group("move") != null
				plainEnglish = "Move whatever is at " + m.group("moveLocation1") + " to " + m.group("moveLocation2");
				plainEnglish += (m.group("capture").equals("*")) ? " and capture the piece there" : "";
				plainEnglish += (m.group("move2") != null) ? " and move whatever is at " + m.group("moveLocation3") + " to " + m.group("moveLocation4") : "";
			}
		}
		System.out.println(plainEnglish);
	}
	
	public static void driver(String fileAddress) {
		try(BufferedReader br = new BufferedReader(new FileReader(fileAddress))) {
			String line =  null; 
			do {
				line = br.readLine();
				if(line != null)
					parseLine(line);
			}
			while (line != null);
		}
		catch(IOException e) {
			System.out.println("Invlid file, exiting program.");
			e.printStackTrace();
		}
	}
	
	
}
