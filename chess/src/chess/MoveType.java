package chess;

public enum MoveType {	
	//rows, cols, allDirections, flipRowCol, multiMove, needsClearPath
	singleUp(-1, 0, false, false, false, false),
	doubleUp(-2, 0, false, false, false, true, singleUp.getRule()),
	singleDown(1, 0, false, false, false, false),
	doubleDown(2, 0, false, false, false, true, singleDown.getRule()),
	singleLShape(2, 1, true, true, false, false),
	manyDiagonal(1, 1, true, false, true, true),
	manyStright(1, 0, true, true, true, true),
	manyAny(manyDiagonal.getRule(), manyStright.getRule()),
	
		singleDiagonal(1, 1, true, false, false, false),
		singleStraight(1, 0, true, true, false, false),
	singleAny(singleStraight.getRule(), singleDiagonal.getRule()),
	
		singleDiagonalUpLeft(-1, -1, false, false, false, false),
		singleDiagonalUpRight(-1, 1, false, false, false, false),
	singleDiagonalUp(singleDiagonalUpLeft.getRule(), singleDiagonalUpRight.getRule()),
	
		singleDiagonalDownLeft(1, -1, false, false, false, false),
		singleDiagonalDownRight(1, 1, false, false, false, false),
	singleDiagonalDown(singleDiagonalDownLeft.getRule(), singleDiagonalDownRight.getRule());	
	
	private final MoveRules RULE;
	
	MoveType(int rows, int cols, boolean allDirections, boolean flipRowCol, boolean multiMove, boolean needsClearPath) {
		this(rows, cols, allDirections, flipRowCol, multiMove, needsClearPath, null);
	}
	
	MoveType(int rows, int cols, boolean allDirections, boolean flipRowCol, boolean multiMove, boolean needsClearPath, MoveRules compound) {
		RULE = new MoveRules(rows, cols, allDirections, flipRowCol, multiMove, needsClearPath, compound);
	}
	
	MoveType(MoveRules rule, MoveRules compound) {
		RULE = new MoveRules(rule, compound);
	}
	
	public MoveRules getRule() {
		return RULE;
	}
	
}
