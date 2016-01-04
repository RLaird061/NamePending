package com.NamePending;

public class UnitTests {
	public GameBoard board;
	
	public UnitTests(GameBoard b) {
		board = b;
	}
	
	public void UnitTest1() {
		int x = 0;
		int y = 0;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		x = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		x = 2;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);

		x = 0;
		y = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		x = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(1);
		x = 2;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		
		x = 0;
		y = 2;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(1);
		x = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		x = 2;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(1);
		
		//PrintBoard();
		// TODO: test board solver
		x = 0;
		y = 0;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		x = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(1);
		x = 2;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		
		x = 0;
		y = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(1);
		x = 1;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(0);
		x = 2;
		board.get(GameBoard.XYtoIDX(x,y)).setPiece_color(1);
		// END board solver
		//PrintBoard();
		
		// confirm top 2 lines are 010, 101
		x = 0;
		y = 0;
		if (board.get(GameBoard.XYtoIDX(x,y)).getPiece_color() != 0)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 1;
		if (board.get(GameBoard.XYtoIDX(x,y)).getPiece_color() != 1)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 2;
		if (board.get(GameBoard.XYtoIDX(x,y)).getPiece_color() != 0)
			System.out.printf("UnitTest1 FAIL!\n");

		x = 0;
		y = 1;
		if (board.get(GameBoard.XYtoIDX(x,y)).getPiece_color() != 1)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 1;
		if (board.get(GameBoard.XYtoIDX(x,y)).getPiece_color() != 0)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 2;
		if (board.get(GameBoard.XYtoIDX(x,y)).getPiece_color() != 1)
			System.out.printf("UnitTest1 FAIL!\n");
	}
}
