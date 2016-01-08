package com.NamePending;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.swt.graphics.Image;

import com.NamePending.piece.Piece;

public class GameSWT extends JFrame
{
	// Constants
	private final static int OUTLINE_WIDTH = 2;
	private final static int BORDER_WIDTH = 7;
	private final static int PIECES_PER_ROW = 6;
	private final static int PIECES_PER_COL = 12;
	private final static int PIECE_LENGTH = 45;
	private final static int SELECTOR_WIDTH = 90;
	private final static int SELECTOR_HEIGHT = 45;
	
	// Member Variables
	private BufferedImage image;
	private int selectorX = 0;
	private int selectorY = 0;
	private int cursorX = 0;
	private int cursorY = 0;

	private GameBoard gb;

	// Member Functions
	public GameSWT(int x, int y)
	{
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(300, 590);
		this.setContentPane(new ImagePanel());
		this.gb = new GameBoard(PIECES_PER_ROW, PIECES_PER_COL);

		KeyListener listener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// Handle ARROW keys and SWAP(space)
				if (KeyEvent.getKeyText(e.getKeyCode()) == "Up") {
					selectorY = (selectorY > 0) ? selectorY-1 : selectorY;
				} else if (KeyEvent.getKeyText(e.getKeyCode()) == "Right") {
					selectorX = (selectorX < PIECES_PER_ROW-2) ? selectorX+1 : selectorX;
				} else if (KeyEvent.getKeyText(e.getKeyCode()) == "Down") {
					selectorY = (selectorY < PIECES_PER_COL-1) ? selectorY+1 : selectorY;
				} else if (KeyEvent.getKeyText(e.getKeyCode()) == "Left") {
					selectorX = (selectorX > 0) ? selectorX-1 : selectorX;					
				} else if (KeyEvent.getKeyText(e.getKeyCode()) == "Space") {
					// TODO: swap and recompute board!
				}
				repaint();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("keyReleased="+KeyEvent.getKeyText(e.getKeyCode()));
			}
		};
		addKeyListener(listener);
		setFocusable(true); // you need this for KeyListener to work or the code won't work >:c
	}

	public void setImage(BufferedImage bufferedImage)
	{
		this.image = bufferedImage;
		this.repaint(); // TODO: is this needed?
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
	}

	private class ImagePanel extends JPanel
	{
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			if (image != null)
			{
				g.drawImage(image, 0, 0, this);
			}

			if (gb != null)
			{
				for(int y = 0; y < PIECES_PER_COL; y++)
				{
					for(int x = 0; x < PIECES_PER_ROW; x++)
					{
						Piece p = gb.get(y * PIECES_PER_ROW + x);
					
						int xPos = BORDER_WIDTH + x * PIECE_LENGTH;
						int yPos = BORDER_WIDTH + y * PIECE_LENGTH;

						Color origColor = p.getCenterColor();
						BufferedImage img = p.getImage();
							
						g.drawImage(img, xPos, yPos, null);
						
						// uncomment below to have border around pieces
						//drawOutlineRect(g, origColor, xPos, yPos, 
						//		PIECE_LENGTH, PIECE_LENGTH, 20);
					}
				}
			}
			
			if (selectorX > -1)
			{
				g.setColor(new Color(255, 255, 0, 120));
				int xPos = BORDER_WIDTH + selectorX * PIECE_LENGTH;
				int yPos = BORDER_WIDTH + selectorY * PIECE_LENGTH;
				g.fillRect(xPos, yPos, 
						SELECTOR_WIDTH, SELECTOR_HEIGHT);
				drawOutlineRect(g, new Color(255,255,0), xPos, yPos,
						SELECTOR_WIDTH, SELECTOR_HEIGHT, 20);
			}

			if (cursorX > 0 && cursorY > 0)
			{
				g.setColor(Color.GREEN);
				g.fillOval(cursorX, cursorY, 3, 3);
			}
		}
	}

	public void setSelectorPosition(Point point)
	{
		if (point != null)
		{
			selectorX = point.x;
			selectorY = point.y;
		}
		else
		{
			selectorX = 0;
			selectorY = 0;
		}
		this.repaint(); // TODO: is this needed?
	}

	public synchronized void setCursorPos(int x, int y)
	{
		this.cursorX = x;
		this.cursorY = y;
		this.repaint(); // TODO: is this needed?
	}

	private static void drawOutlineRect(Graphics g, Color color, int x, int y, int width, int height, int opacity)
	{
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
		g.fillRect(x, y, width, height);

		g.setColor(color);
		g.fillRect(x, y, width, OUTLINE_WIDTH);
		g.fillRect(x + width - OUTLINE_WIDTH, y, OUTLINE_WIDTH, height);
		g.fillRect(x, y + height - OUTLINE_WIDTH, width, OUTLINE_WIDTH);
		g.fillRect(x, y, OUTLINE_WIDTH, height);
	}
}
