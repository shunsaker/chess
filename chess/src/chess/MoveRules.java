package chess;

import java.util.LinkedHashSet;
import java.util.Set;

public class MoveRules {
	private final int ROWS, COLS;
	private final boolean ALL_DIRECTIONS, FLIP_ROW_COL, MULTIMOVE, REQUIRES_CLEAR_PATH;
	private final MoveRules COMPOUND;
	
	public MoveRules(MoveRules move, MoveRules compound) {
		this(move.ROWS, move.COLS, move.ALL_DIRECTIONS, move.FLIP_ROW_COL, move.MULTIMOVE, move.REQUIRES_CLEAR_PATH, compound);
	}
	
	public MoveRules(int rows, int cols, boolean allDirections, boolean flipRowCol, boolean multiMove, boolean needsClearPath, MoveRules compound) {
		ROWS = rows;
		COLS = cols;
		ALL_DIRECTIONS = allDirections;
		FLIP_ROW_COL = flipRowCol;
		MULTIMOVE = multiMove;
		REQUIRES_CLEAR_PATH = needsClearPath;
		COMPOUND = compound;
	}
	
	/**
	 * Is used to indicate to the gameController if a piece can jump pieces or not
	 */
	public boolean requiresClearPath() {
		return REQUIRES_CLEAR_PATH;
	}
	
	public Set<RelativeLocation> getAllOffsets(int maxTimes) {
		Set<RelativeLocation> offsets = flipRowCol();
		
		if(ALL_DIRECTIONS) {
			offsets = allDirections(offsets);
		}
		
		if(MULTIMOVE) {
			offsets = multiMove(offsets, maxTimes);
		}
		
		if(COMPOUND != null) {
			addCompound(offsets, maxTimes);
		}
		
		return offsets;
	}
	
	/**
	 * If FLIP_ROW_COL:
	 *    Allows the row and column to be reversed for example (1, 2) and also (2, 1)
	 */
	private Set<RelativeLocation> flipRowCol() {
		Set<RelativeLocation> offsets = new LinkedHashSet<RelativeLocation>();
		offsets.add(new RelativeLocation(ROWS, COLS));
		if(FLIP_ROW_COL) {
			offsets.add(new RelativeLocation(COLS, ROWS));
		}
		return offsets;
	}
	
	/**
	 * Applies all directions to the current offsets
	 * all combinations of negative and positive [(-,-)(-,+)(+,-)(++)]
	 */
	private Set<RelativeLocation> allDirections(Set<RelativeLocation> offsets) {
		// Applies all directions to the current offsets
		// all combinations of negative and positive [(-,-)(-,+)(+,-)(++)]
		Set<RelativeLocation> s = new LinkedHashSet<RelativeLocation>(); 
		for(RelativeLocation o : offsets) {
			for(int i = -1; i <= 1; i+=2) {
				for(int j = -1; j <= 1; j+=2) {
					s.add(new RelativeLocation(o.getRow() * i, o.getCol() * j));
				}
			}
		}
		return s;
	}
	
	/**
	 * 
	 * Applies a scalar to the current offsets (usually the size of the board - 1)
	 * for example a piece moving diagonally may move (1,1)
	 * after the scalar is applied this would be:
	 *    [(1,1)(2,2)(3,3)(4,4)(5,5)(6,6)(7,7)]
	 */
	private Set<RelativeLocation> multiMove(Set<RelativeLocation> offsets, int maxTimes) {
		 
		Set<RelativeLocation> s = new LinkedHashSet<RelativeLocation>();
		for(RelativeLocation o : offsets) {
			for(int mult = 1; mult < maxTimes; mult++) {
				s.add(new RelativeLocation(o.getRow() * mult, o.getCol() * mult));
			}
		}
		return s;
	}
	
	/**
	 * Recursively calls getAllOffsets for the sub/compound move
	 * And then adds the two sets together
	 */
	private void addCompound(Set<RelativeLocation> offsets, int maxTimes) {
		Set<RelativeLocation> comp = COMPOUND.getAllOffsets(maxTimes);
		offsets.addAll(comp);
	}

}
