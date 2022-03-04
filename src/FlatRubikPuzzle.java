/*  -------------------------------------------------------
 *  FlatRubikPuzzle.java
 *  -------------------------------------------------------
 *  Author:  Matthew Ferlaino
 *  Course:	 COSC2006A
 *  ID:      169657520
 *  Email:   mferlaino@algomau.ca
 *  Date:	 October 9th, 2019
 *  ------------------------------------------------------- */

// Imports
import java.util.ArrayList;
import java.util.Stack;

public class FlatRubikPuzzle {
	/*
	 * -- Node Class --
	 * This is the Node class 
	 * as a private class built into the 
	 * FlatRubikPuzzle class. More elegant to 
	 * incorporate Node class into one class.
	 */
	private class Node {
		// Variables
		private int outOfPlace;
		private Object[][] puzzleState;
		
		// Double-arg constructor
		Node(Object[][] puzzleState, int outOfPlace) {
			this.outOfPlace = outOfPlace;
			this.puzzleState = carbonCopy(puzzleState);
		}
	}
	
	// FlatRubikPuzzle Variables
	private final int UP = 1;
	private final int DOWN = 2;
	private final int LEFT = 3;
	private final int RIGHT = 4;
	
	private ArrayList<Object[][]> prevBestMovesList;
	private ArrayList<Node> puzzleMoves;
	private Stack<ArrayList<Node>> stackOfMoveLists;

	private Object[][] puzzle;
	private Object[][] solvedPuzzle;
	
	private int moveCount = 0;
	private boolean proceed = true;
	
	// No-arg Constructor
	FlatRubikPuzzle() {
		construct();
	}
	
	/* --- Methods ---
	 * 1. construct()
	 * 2. print()
	 * 3. performShift()
	 * 4. solve()
	 * 5. findBestMoveNode()
	 * 6. outOfPlace()
	 * 7. carbonCopy()
	 * 8. areEqual()
	 * 9. contains()
	 */
	
	// construct()
	private void construct() {
		Object[][] puzzle = {{"R","G","G"},
						     {"G","B","R"},
						     {"B","R","B"}};

		Object[][] solvedPuzzle = {{"R","R","R"},
				 				   {"G","G","G"},
				 				   {"B","B","B"}};
		// Assign
		this.puzzle = puzzle;
		this.solvedPuzzle = solvedPuzzle;
		
		// Instantiate
		puzzleMoves = new ArrayList<Node>();
		prevBestMovesList = new ArrayList<Object[][]>();
		stackOfMoveLists = new Stack<ArrayList<Node>>();
	}
	
	// print()
	private void print(Object[][] puzzle) {
		// Print Top Border
		for (int i = 0; i < 3; i++) 
			System.out.print("= = ");
		System.out.println();
			
		// Print Board Contents
		for (int i = 0; i < puzzle.length; i++) {
			System.out.print("|  ");
			
			for (int j = 0; j < puzzle.length; j++) 
				System.out.print(puzzle[i][j] + " ");
			
			System.out.print(" |");
			System.out.println();
		}
		
		// Print Bottom Border
		for (int i = 0; i < 3; i++) 
			System.out.print("= = ");
		System.out.println();
	}
	
	// performShift()
	public void performShift(Object[][] puzzle, int direction, int index) throws RuntimeException {
		// Error Check
		if (direction > 4 || direction < 1 || index > 2 || index < 0) 
			throw new RuntimeException("Runtime Exception: Invalid shift operation.");
		
		// Variables
		Object[][] shiftedCube = puzzle;
	
		Object zeroColItem = this.puzzle[0][index];
		Object firstColItem = this.puzzle[1][index];
		Object thirdColItem = this.puzzle[2][index];
		
		Object zeroRowItem = this.puzzle[index][0];
		Object firstRowItem = this.puzzle[index][1];
		Object secondRowItem = this.puzzle[index][2];
		
		// Shift elements
		switch (direction) {
			case UP: 
				shiftedCube[0][index] = firstColItem; 
				shiftedCube[1][index] = thirdColItem; 
				shiftedCube[2][index] = zeroColItem; 
				break;
				
			case DOWN:
				shiftedCube[0][index] = thirdColItem; 
				shiftedCube[1][index] = zeroColItem; 
				shiftedCube[2][index] = firstColItem; 
				break; 
				
			case LEFT: 
				shiftedCube[index][0] = firstRowItem; 
				shiftedCube[index][1] = secondRowItem; 
				shiftedCube[index][2] = zeroRowItem; 
				break;
				
			case RIGHT:
				shiftedCube[index][0] = secondRowItem;
				shiftedCube[index][1] = zeroRowItem; 
				shiftedCube[index][2] = firstRowItem; 
				break;
		}
	}
	
	// solve()
	public boolean solve() {
		do {
			// Perform all possible shifts
			while (proceed == true) {
				for (int i = 1; i < 5; i++) {
					for (int j = 0; j < 3; j++) {
						performShift(puzzle, i, j);
						Node node = new Node(puzzle, outOfPlace(puzzle));
						
						if (prevBestMovesList.size() != 0 && contains(prevBestMovesList, puzzle)) 
							continue;	
						
						else 
							puzzleMoves.add(node);		
						
						// Reset puzzle to original state
						performShift(puzzle, i, j);
						performShift(puzzle, i, j);
					}
				}
				break;
			}
			
			/***** Parsing Info Stage ******/
			Node bestMoveNode = findBestMoveNode(puzzleMoves);
			Object[][] bestMovePuzzle = bestMoveNode.puzzleState;
			
			// First best move found
			if (prevBestMovesList.size() == 0) {
				prevBestMovesList.add(bestMovePuzzle);
				
				puzzleMoves.remove(bestMoveNode);
				stackOfMoveLists.push(puzzleMoves);
			}	
						
			/***** Backtracking Stage ******/
			else if (contains(prevBestMovesList, bestMovePuzzle)) {
				for (Object[][] aPuzzle : prevBestMovesList) {
					if (areEqual(aPuzzle, bestMovePuzzle)) {
						prevBestMovesList.remove(aPuzzle);
						puzzleMoves = stackOfMoveLists.pop();
						proceed = false;
						break;
					}
				}
			}
			
			/***** Checking Stage ******/
			else if (!areEqual(bestMovePuzzle, solvedPuzzle)) {
				stackOfMoveLists.push(puzzleMoves);

				// Override puzzleState with our newly shifted Puzzle
				puzzle = carbonCopy(bestMovePuzzle);
				proceed = true;

				// Other
				prevBestMovesList.add(bestMovePuzzle);
				puzzleMoves.remove(bestMoveNode);
			}
			
			// Solved cube
			else {
				prevBestMovesList.add(bestMovePuzzle);
				for (Object[][] puzzle : prevBestMovesList) {
					System.out.println("Move #" + ++moveCount);
					print(puzzle);
				}

				return true;
			}
		} while(true);
	}
	
	// findBestMoveNode()
	private Node findBestMoveNode(ArrayList<Node> shiftsList) {	
		// Information 
		Node leastMoves = shiftsList.get(0);
		
		// Find best move to make
		for (Node node : shiftsList) 
			if (node.outOfPlace < leastMoves.outOfPlace) 
				leastMoves = node;
		return leastMoves;
	}
	
	// outOfPlace
	private int outOfPlace(Object[][] puzzle) {
		// Find out of place count
		int count = 0;
		
		for (int i = 0; i < puzzle.length; i++) 
			for (int j = 0; j < 3; j++) 
				if (!puzzle[i][j].equals(solvedPuzzle[i][j])) 
					count++;
				
		return count;
	}
	
	// carbonCopy()
	private Object[][] carbonCopy(Object[][] puzzle) {
		Object[][] newPuzzle = new Object[LEFT][LEFT];
		
		for (int x = 0; x < puzzle.length; x++) 
			for (int y = 0; y < puzzle.length; y++) 
				newPuzzle[x][y] = puzzle[x][y];
		
		return newPuzzle;
	}
	
	// areEqual()
	private boolean areEqual(Object[][] puzzleA, Object[][] puzzleB) {
		for (int i = 0; i < puzzleA.length; i++) 
			for (int j = 0; j < puzzleA.length; j++) 
				if (!puzzleA[i][j].equals(puzzleB[i][j])) 
					return false;
		return true;
	}
	
	// contains()
	private boolean contains(ArrayList<Object[][]> puzzleList, Object[][] puzzle) {
		for (Object[][] aPuzzle : puzzleList) 
			if (areEqual(aPuzzle, puzzle)) 
				return true;	
		return false;
	}
}