package chess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.Board;

public class LocationTools {
	public static List<Location> getLocationsFromRule(MoveRules rule, Location loc) {
		Set<RelativeLocation> relativeMoves = rule.getAllOffsets();
		return relativeToActualLocations(loc, relativeMoves);
	}


	public static List<Location> relativeToActualLocations(Location loc, Set<RelativeLocation> rels) {
		List<Location> locations = new ArrayList<Location>();
		for(RelativeLocation rel : rels) {
			Location l = new Location(loc, rel);
			if(isLocationOnBoard(l)) {
				locations.add(l);
			}
		}
		
		return locations;
	}
	
	public static boolean isLocationOnBoard(Location loc) {
		boolean rowOnBoard = 0 <= loc.getRow() && loc.getRow() < Board.SIZE;
		boolean colOnBoard = 0 <= loc.getCol() && loc.getCol() < Board.SIZE;
		return  rowOnBoard && colOnBoard;
	}
	
	public static boolean locationInList(Location loc, List<Location> list) {
		Iterator<Location> it = list.iterator();
		boolean inList = false;
		while(it.hasNext() && !inList) {
			Location next = it.next();
			inList = loc.equals(next);
		}
		return inList;
	}

}
