package com.NamePending;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class GameComposite extends Composite {
	public ArrayList<TransitionableCanvas> lbls = new ArrayList<TransitionableCanvas>();
	private Image backgroundImage = null;
	private Point selector;
	
	public GameComposite(Composite parent, int style) {
		super(parent, style);

		selector = new Point(-1, -1);
		
		int xPos = GameSWT.BORDER_WIDTH;
		int yPos = GameSWT.BORDER_WIDTH;

		for (int y = 0; y < GameSWT.PIECES_PER_COL; y++)
		{
			for (int x = 0; x < GameSWT.PIECES_PER_ROW; x++)
			{
				TransitionableCanvas tl = new TransitionableCanvas(this, SWT.DOUBLE_BUFFERED, y * GameSWT.PIECES_PER_ROW + x);
				lbls.add(tl);
				tl.setBounds(xPos, yPos, GameSWT.PIECE_LENGTH, GameSWT.PIECE_LENGTH);
				xPos += GameSWT.PIECE_LENGTH;
			}
			yPos += GameSWT.PIECE_LENGTH;
			xPos = GameSWT.BORDER_WIDTH;
		}
		setSize(parent.getBounds().width, parent.getBounds().height);
		
		addPaintListener(new PaintListener()  { /* paint listener. */
			public void paintControl(final PaintEvent event) {
				//System.out.println("painting");
				if (backgroundImage != null)
				{
					event.gc.drawImage(backgroundImage, 0, 0);
				}
			}
		});
	}

	public Point getSelector() {
		return selector;
	}

	public void setSelector(Point selector) {
		this.selector = selector;
		for (int i = 0; i < lbls.size(); i++) {
			int x = i % GameSWT.PIECES_PER_ROW;
			int y = i / GameSWT.PIECES_PER_ROW;
			if (x == selector.x && y == selector.y)
				lbls.get(i).setSelector(TransitionableCanvas.SELECTED_LEFT);
			else if (x == selector.x+1 && y == selector.y)
				lbls.get(i).setSelector(TransitionableCanvas.SELECTED_RIGHT);
			else
				lbls.get(i).setSelector(TransitionableCanvas.NOT_SELECTED);
		}
	}
}
