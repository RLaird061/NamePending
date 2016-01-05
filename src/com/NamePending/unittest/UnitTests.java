package com.NamePending.unittest;

import java.awt.Color;

import com.NamePending.GameBoard;
import com.NamePending.piece.GreenBrickPiece;
import com.NamePending.piece.RedBrickPiece;

public class UnitTests {
	public GameBoard board;
	
	public UnitTests(GameBoard b) {
		board = b;
	}
	
	public void UnitTest1() {
		int x = 0;
		int y = 0;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		x = 1;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		x = 2;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());

		x = 0;
		y = 1;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		x = 1;
		board.set(GameBoard.XYtoIDX(x,y), new GreenBrickPiece());
		x = 2;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		
		x = 0;
		y = 2;
		board.set(GameBoard.XYtoIDX(x,y), new GreenBrickPiece());
		x = 1;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		x = 2;
		board.set(GameBoard.XYtoIDX(x,y), new GreenBrickPiece());
		
		//PrintBoard();
		// TODO: test board solver
		x = 0;
		y = 0;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		x = 1;
		board.set(GameBoard.XYtoIDX(x,y), new GreenBrickPiece());
		x = 2;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		
		x = 0;
		y = 1;
		board.set(GameBoard.XYtoIDX(x,y), new GreenBrickPiece());
		x = 1;
		board.set(GameBoard.XYtoIDX(x,y), new RedBrickPiece());
		x = 2;
		board.set(GameBoard.XYtoIDX(x,y), new GreenBrickPiece());
		// END board solver
		//PrintBoard();
		
		// confirm top 2 lines are 010, 101 / red green red, green red green
		x = 0;
		y = 0;
		Color red = new Color(255, 42, 42);
		Color green = new Color(42, 255, 42);
		
		if (board.get(GameBoard.XYtoIDX(x,y)).getCenterColor() != red)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 1;
		if (board.get(GameBoard.XYtoIDX(x,y)).getCenterColor() != green)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 2;
		if (board.get(GameBoard.XYtoIDX(x,y)).getCenterColor() != red)
			System.out.printf("UnitTest1 FAIL!\n");

		x = 0;
		y = 1;
		if (board.get(GameBoard.XYtoIDX(x,y)).getCenterColor() != green)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 1;
		if (board.get(GameBoard.XYtoIDX(x,y)).getCenterColor() != red)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 2;
		if (board.get(GameBoard.XYtoIDX(x,y)).getCenterColor() != green)
			System.out.printf("UnitTest1 FAIL!\n");
	}
}
