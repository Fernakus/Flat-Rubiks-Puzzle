/*  -------------------------------------------------------
 *  main.java
 *  -------------------------------------------------------
 *  Author:  Matthew Ferlaino
 *  Course:	 COSC2006A
 *  ID:      169657520
 *  Email:   mferlaino@algomau.ca
 *  Date:	 Sept 16th, 2019
 *  ------------------------------------------------------- */

public class main {
	public static void main(String[] args) {
		// Variables
		FlatRubikPuzzle cube = new FlatRubikPuzzle();
			
		System.out.println("----- Steps To Solve Puzzle -----");
		System.out.println("Start");
		cube.solve();
	}
}