package com.NamePending;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.nebula.effects.stw.transitions.FadeTransition;
import org.eclipse.nebula.effects.stw.transitions.SlideTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TransitionableCanvas extends Canvas implements Transitionable {
	public static final int NOT_SELECTED = 0;	 // not selected
	public static final int SELECTED_RIGHT = 1;  // has right part of selector
	public static final int SELECTED_LEFT = 2;   // has left part of selector
	private int index;
	private int selected = NOT_SELECTED;
	private static int selected_idx = -1;
	private Image pieceImage;
	private Image oldImage;
	private Image tempImage;
	private TransitionManager tm = null;
	private TransitionableCanvas neighbor = null; 
	private SlideTransition st = null;
	private FadeTransition ft = null;
	private CubicRotationTransition ct = null;
	private DoubleCubicRotationTransition dct = null;
	private MultiSlideUpTransition msut = null; // Don't use for swaps!
	
	public TransitionableCanvas(Composite parent, int style, int idx) {
		super(parent, style);
		index = idx;
		if (tm == null) {
			tm = new TransitionManager(this);
			tm.addTransitionListener(new TransitionListener() {
				public void transitionFinished(TransitionManager tm) {
					System.out.println("done");
					transitionDone();
				}
			});
		}
		st = new SlideTransition(tm, 60, 500);
		ft = new FadeTransition(tm, 60, 500);
		ct = new CubicRotationTransition(tm, 60, 500);
		dct = new DoubleCubicRotationTransition(tm, 60, 500);
		
		addPaintListener(new PaintListener()  { /* paint listener. */
			public void paintControl(final PaintEvent event) {
				if (pieceImage != null) {
					int xPos = selected_idx % GameSWT.PIECES_PER_ROW;
					int yPos = selected_idx / GameSWT.PIECES_PER_COL;
					xPos *= GameSWT.PIECE_LENGTH;
					yPos *= GameSWT.PIECE_LENGTH;
					if (oldImage != null) {
						event.gc.drawImage(oldImage, 0, 0);
						drawOutlineRect(event.gc, new Color(MainSWT.getDisplay(), 255, 255, 0), xPos, yPos,
								GameSWT.SELECTOR_WIDTH, GameSWT.SELECTOR_HEIGHT, 25, true);						
						oldImage = null;
					} else {
						event.gc.drawImage(pieceImage, 0, 0);
						Point p = getSize();
						if (p.x > GameSWT.PIECE_LENGTH)
							drawOutlineRect(event.gc, new Color(MainSWT.getDisplay(), 255, 255, 0), xPos, yPos,
									GameSWT.SELECTOR_WIDTH, GameSWT.SELECTOR_HEIGHT, 25, true);						
					}
				}

				if (selected > NOT_SELECTED)
				{
					// draw half of a selector part
					drawOutlineRect(event.gc, new Color(MainSWT.getDisplay(), 255, 255, 0), 0, 0,
							GameSWT.SELECTOR_WIDTH/2, GameSWT.SELECTOR_HEIGHT, 25, false);						
				}
			}
		});
	}

	public void transitionDone() {
		// TODO: prevent any other keyboard input and/or transitions before we are done
		// because accepting them causes strange bugs that are gunna be super stupid to debug
		for (int idx = 0; idx < (GameSWT.PIECES_PER_ROW * GameSWT.PIECES_PER_COL); idx++) {
			TransitionableCanvas tc = ((GameComposite)getParent()).lbls.get(idx);
			tc.setVisible(true);
		}
		setSize(GameSWT.PIECE_LENGTH, GameSWT.PIECE_LENGTH);         // size back to normal
		// copy back proper piece after transition effect
		if (tempImage != null)
			pieceImage = new Image(MainSWT.getDisplay(), tempImage, SWT.IMAGE_COPY);
	}
	
	public void setSelector(int selected_side) {
		selected = selected_side;
		if (selected == SELECTED_LEFT)
			selected_idx = index;
	}
	
	public Image getPieceImage() {
		return pieceImage;
	}

	public void setPieceImage(Image pieceImage) {
		this.pieceImage = pieceImage;
	}
	
	public void doSwapAnim() {
		// temporary expand this piece to take up two piece space and do transition effect
		// such that both pieces look like they are getting transitioned at the same time
		neighbor = ((GameComposite)getParent()).lbls.get(index+1);		
		neighbor.setVisible(false);
		Point p = getSize();
		setSize(p.x*2, p.y);
		
		tempImage = new Image(MainSWT.getDisplay(), pieceImage, SWT.IMAGE_COPY);
		ImageData d1 = tempImage.getImageData();
		ImageData d2 = neighbor.getPieceImage().getImageData();
		ImageData data = new ImageData(d1.width*2, d1.height, d1.depth, d1.palette);
		int[] pixels = new int[d1.width];
		byte[] alphas = new byte[d1.width];
		for (int y = 0; y < tempImage.getBounds().height; y++)
		{
			d1.getPixels(0, y, d1.width, pixels, 0);
			data.setPixels(0, y, d1.width, pixels, 0);
			d1.getAlphas(0, y, d1.width, alphas, 0);
			data.setAlphas(0, y, d1.width, alphas, 0);
			
			d2.getPixels(0, y, d1.width, pixels, 0);
			data.setPixels(d1.width, y, d1.width, pixels, 0);
			d2.getAlphas(0, y, d1.width, alphas, 0);
			data.setAlphas(d1.width, y, d1.width, alphas, 0);
		}
		oldImage = new Image(MainSWT.getDisplay(), data);
		for (int y = 0; y < tempImage.getBounds().height; y++)
		{
			d2.getPixels(0, y, d1.width, pixels, 0);
			data.setPixels(0, y, d1.width, pixels, 0);
			d2.getAlphas(0, y, d1.width, alphas, 0);
			data.setAlphas(0, y, d1.width, alphas, 0);
			
			d1.getPixels(0, y, d1.width, pixels, 0);
			data.setPixels(d1.width, y, d1.width, pixels, 0);
			d1.getAlphas(0, y, d1.width, alphas, 0);
			data.setAlphas(d1.width, y, d1.width, alphas, 0);
		}		
		pieceImage = new Image(MainSWT.getDisplay(), data);
		
		
		GC gc = new GC(this, SWT.NONE);
		dct.start(oldImage, pieceImage, gc, getDirection(0,0));
		gc.dispose();
		
		
		transitionDone();
//		tm.setTransition(dct);
//		tm.startTransition(0, 0, getDirection(0,0));
	}
	
	public void doSlideUpAnimStep1(ArrayList<Rectangle> rects, ArrayList<Integer> heights)
	{
		msut = new MultiSlideUpTransition(tm, 60, 500, rects, heights); // setup timing and rects and heights

		// Temporary expand this piece to take up whole game board
		// then slide all columns (defined in rects) up
		tempImage = new Image(MainSWT.getDisplay(), pieceImage, SWT.IMAGE_COPY);
		int w = GameSWT.PIECES_PER_ROW * GameSWT.PIECE_LENGTH;
		int h = GameSWT.PIECES_PER_COL * GameSWT.PIECE_LENGTH;
		setSize(w, h);
		ImageData data = new ImageData(w, h, getPieceImage().getImageData().depth, getPieceImage().getImageData().palette); 
		int[] pixels = new int[GameSWT.PIECE_LENGTH];
		byte[] alphas = new byte[GameSWT.PIECE_LENGTH];
		
		// draw all pieces into this data
		for (int idx = 0; idx < (GameSWT.PIECES_PER_ROW * GameSWT.PIECES_PER_COL); idx++) {
			int _x = idx % GameSWT.PIECES_PER_ROW;
			int _y = idx / GameSWT.PIECES_PER_ROW;
			TransitionableCanvas tc = ((GameComposite)getParent()).lbls.get(idx);
			if (idx > 0)
				tc.setVisible(false);
			
			int x = _x * GameSWT.PIECE_LENGTH;
			int y = _y * GameSWT.PIECE_LENGTH;

			Image pi = tc.getPieceImage();
			if (pi != null) {
				ImageData tmp = tc.getPieceImage().getImageData();
				for (int tmpy = 0; tmpy < GameSWT.PIECE_LENGTH; tmpy++) {
					tmp.getPixels(0, tmpy, GameSWT.PIECE_LENGTH, pixels, 0);
					tmp.getAlphas(0, tmpy, GameSWT.PIECE_LENGTH, alphas, 0);
					data.setPixels(x, y+tmpy, GameSWT.PIECE_LENGTH, pixels, 0);
					data.setAlphas(x, y+tmpy, GameSWT.PIECE_LENGTH, alphas, 0);				
				}
			} else {
				for (int tmpy = 0; tmpy < GameSWT.PIECE_LENGTH; tmpy++) {
					Arrays.fill(pixels, (int) 0); 
					Arrays.fill(alphas, (byte) 0);
					data.setPixels(x, y+tmpy, GameSWT.PIECE_LENGTH, pixels, 0);
					data.setAlphas(x, y+tmpy, GameSWT.PIECE_LENGTH, alphas, 0);				
				}
			}
		}
		oldImage = new Image(MainSWT.getDisplay(), data);
	}

	public void doSlideUpAnimStep2()
	{
		int w = GameSWT.PIECES_PER_ROW * GameSWT.PIECE_LENGTH;
		int h = GameSWT.PIECES_PER_COL * GameSWT.PIECE_LENGTH;
		ImageData data = new ImageData(w, h, pieceImage.getImageData().depth, pieceImage.getImageData().palette); 
		int[] pixels = new int[GameSWT.PIECE_LENGTH];
		byte[] alphas = new byte[GameSWT.PIECE_LENGTH];
		
		// draw all pieces into this data
		for (int idx = 0; idx < (GameSWT.PIECES_PER_ROW * GameSWT.PIECES_PER_COL); idx++) {
			int _x = idx % GameSWT.PIECES_PER_ROW;
			int _y = idx / GameSWT.PIECES_PER_ROW;
			TransitionableCanvas tc = ((GameComposite)getParent()).lbls.get(idx);
			if (idx > 0)
				tc.setVisible(false);
			
			int x = _x * GameSWT.PIECE_LENGTH;
			int y = _y * GameSWT.PIECE_LENGTH;
			
			for (int tmpy = 0; tmpy < GameSWT.PIECE_LENGTH; tmpy++) {
				tc.getPieceImage().getImageData().getPixels(0, tmpy, GameSWT.PIECE_LENGTH, pixels, 0);
				tc.getPieceImage().getImageData().getAlphas(0, tmpy, GameSWT.PIECE_LENGTH, alphas, 0);
				data.setPixels(x, y+tmpy, GameSWT.PIECE_LENGTH, pixels, 0);
				data.setAlphas(x, y+tmpy, GameSWT.PIECE_LENGTH, alphas, 0);				
			}
		}
		pieceImage = new Image(MainSWT.getDisplay(), data);
		
		GC gc = new GC(this, SWT.DOUBLE_BUFFERED);
		msut.start(oldImage, pieceImage, gc, getDirection(0,0));
		gc.dispose();
		
		transitionDone();
//		tm.setTransition(msut);
//		tm.startTransition(0, 0, Transition.DIR_UP);		
	}
	
	@Override
	protected void checkSubclass() {}

	@Override
	public void addSelectionListener(SelectionListener listener) {}

	@Override
	public Composite getComposite() {
		return this.getParent();
	}

	@Override
	public Control getControl(int arg0) {
		return this;
	}

	@Override
	public double getDirection(int arg0, int arg1) {
		if (selected == this.SELECTED_LEFT)
			return Transition.DIR_RIGHT;
		else
			return Transition.DIR_LEFT;
	}

	@Override
	public int getSelection() {
		return 0;
	}

	@Override
	public void setSelection(int arg0) {
	}

	private void drawOutlineRect(GC gc, Color color, int x, int y, int width, int height, int opacity, boolean bothsides)
	{
		// draw an entire background color with some alpha/opacity
		gc.setBackground(new Color(MainSWT.getDisplay(), color.getRed(), color.getGreen(), color.getBlue()));
		gc.setAlpha(opacity);
		gc.fillRectangle(x, y, width, height);

		// draw part of selector rect (either right or left side) with no alpha/opacity
		gc.setBackground(color);
		gc.setAlpha(255);
		gc.fillRectangle(x, y, width, GameSWT.OUTLINE_WIDTH);                                      // top
		if (bothsides || selected == SELECTED_RIGHT)
			gc.fillRectangle(x + width - GameSWT.OUTLINE_WIDTH, y, GameSWT.OUTLINE_WIDTH, height); // right
		gc.fillRectangle(x, y + height - GameSWT.OUTLINE_WIDTH, width, GameSWT.OUTLINE_WIDTH);     // bottom
		if (bothsides || selected == SELECTED_LEFT)
			gc.fillRectangle(x, y, GameSWT.OUTLINE_WIDTH, height);                                 // left
	}	
}
