package com.NamePending;

import java.util.ArrayList;
import java.util.Arrays;

/*******************************************************************************
 * Copyright (c) 2010 Ahmed Mahran and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors:
 *     Ahmed Mahran - initial API and implementation
 *     
 *     rl - changed to MultiSlideUpTransition (many columns 
 *     		from cells in a grid moving up at the same time)
 *******************************************************************************/

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Applies a slide effect. The <i>from</i> control slides out and the the <i>to</i>
 * control slides in smoothly accelerating up then down after a while until it stops.
 *  
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class MultiSlideUpTransition extends org.eclipse.nebula.effects.stw.Transition {

    private int _w, _halfW, _h[], _halfH[];
    private double _a[], _x[], _y[], _x0[], _y0[], _v0[];
    private boolean _flag1;
    private Image _from[];
    private Image _to[];    
    private long _halfT, _t1;
    private ArrayList<Rectangle> inverted_rects;
    private ArrayList<Rectangle> rects;
    private ArrayList<Integer> heights;
    
    /**
     * This constructor creates a MultiSlideUpTransition with number of frames per second of {@link Transition#DEFAULT_FPS}
     * and total transition time of {@link Transition#DEFAULT_T} milliseconds. It is similar to 
     * new MultiSlideUpTransition(transitionManager, {@link Transition#DEFAULT_FPS}, {@link Transition#DEFAULT_T})
     * 
     * @param transitionManager the transition manager to be used to manage transitions
     */
    public MultiSlideUpTransition(TransitionManager transitionManager) {
        this(transitionManager, DEFAULT_FPS, DEFAULT_T, new ArrayList<Rectangle>(), new ArrayList<Integer>());
    }
    
    /**
     * This constructor creates a MultiSlideUpTransition with <i>fps</i> number of frames per
     * second and <i>T</i> total transition time in milliseconds.
     * 
     * @param transitionManager the transition manager to be used to manage transitions 
     * @param fps number of frames per second
     * @param T the total time the transition effect will take in milliseconds
     * @param rects different rectangle regions within the image to slide up
     * @param heights different heights to slide the rectangles up by
     */
    public MultiSlideUpTransition(TransitionManager transitionManager, long fps, long T, ArrayList<Rectangle> rects, ArrayList<Integer> heights) {
        super(transitionManager, fps, T);
        this.rects = rects;
        this.heights = heights;
        
        // very game specific way of inverting a list of rects TODO: fix me
        inverted_rects = new ArrayList<Rectangle>();
        for (int x = 0; x < GameSWT.PIECES_PER_ROW; x++)
        {
        	boolean intersects = false;
        	int h = GameSWT.PIECES_PER_COL * GameSWT.PIECE_LENGTH;
        	Rectangle r = new Rectangle(x * GameSWT.PIECE_LENGTH, 0,
        			GameSWT.PIECE_LENGTH, h);
        	
        	for (int i = 0; i < rects.size(); i++) {
        		if (rects.get(i).intersects(r)) {
        			intersects = true;
        			r.height = rects.get(i).y;
        		}
        	}
        	inverted_rects.add(r);
        }
    }
    
    @Override
    protected void initTransition(Image from, Image to, GC gc, double direction) {
        _halfT = (long) (_T / 2.0);
        ImageData df = from.getImageData();
        ImageData dt = to.getImageData();
        ImageData _fromData;
        ImageData _toData;
        _h = new int[rects.size()];
        _halfH = new int[rects.size()];
        _a = new double[rects.size()];
        _x = new double[rects.size()];
        _y = new double[rects.size()];
        _x0 = new double[rects.size()];
        _y0 = new double[rects.size()];
        _v0 = new double[rects.size()];
        
        _from = new Image[rects.size()];
        _to = new Image[rects.size()];
        
        for (int i = 0; i < rects.size(); i++) {
        	Rectangle r = rects.get(i);
        	//System.out.printf("%d %d %d %d\n", r.x, r.y, r.width, r.height);
        	_fromData = new ImageData(r.width, r.height, df.depth, df.palette);
        	_toData = new ImageData(r.width, r.height, dt.depth, dt.palette);        	
        	int[] pixels = new int[r.width];
        	byte[] alphas = new byte[r.width];
        	for(int y = r.y; y < (r.y + r.height); y++) {
        		df.getPixels(r.x, y, r.width, pixels, 0);
        		df.getAlphas(r.x, y, r.width, alphas, 0);
        		_fromData.setPixels(0, y-r.y, r.width, pixels, 0);
        		_fromData.setAlphas(0, y-r.y, r.width, alphas, 0);
        		
        		dt.getPixels(r.x, y, r.width, pixels, 0);
        		dt.getAlphas(r.x, y, r.width, alphas, 0); 
        		_toData.setPixels(0, y-r.y, r.width, pixels, 0);
        		_toData.setAlphas(0, y-r.y, r.width, alphas, 0);
        	}
        	_from[i] = new Image(MainSWT.getDisplay(), _fromData);
        	_to[i] = new Image(MainSWT.getDisplay(), _toData);
        	_h[i] = heights.get(i) * GameSWT.PIECE_LENGTH; // _fromData.height;
        	//System.out.printf("%d\n", _h[i]);
        	_halfH[i] = (int) (_h[i] / 2.0);
        	_a[i] = _h[i] / (double)(_halfT * _halfT);
        	_x[i] = r.x;
        	_y[i] = 0;
        }
       
        _flag1 = false;
    }

    @Override
    protected void stepTransition(long t, Image from, Image to, GC gc, double direction) {
    	// draw non-moving parts first
    	for (int i = 0; i < inverted_rects.size(); i++) {
    		Rectangle r = inverted_rects.get(i);
    		gc.drawImage(from, r.x, r.y, r.width, r.height, r.x, r.y, r.width, r.height);
    	}
    	// draw
    	for (int i = 0; i < rects.size(); i++) {
    		Rectangle r = rects.get(i);
    		
    		// draw moving rects
    		//gc.setClipping(r); // this causes a weird bug... not sure why!
    		gc.drawImage(_from[i], (int)_x[i], r.y + (int)_y[i]);
    		gc.drawImage(_to[i], (int)_x[i], r.y + (int)(_y[i] + _h[i]));

    		if( t <= _halfT ) {
    			_y[i] = Math.max(-0.5 * _a[i] * t * t, -_halfH[i]);
    		} else {
    			if(!_flag1) {
    				_y0[i] = _y[i];
    				_v0[i] = _a[i] * t;
    				_a[i] *= -1.0;
    				if (i == rects.size()-1)
    					_flag1 = true;
    			}
    			
    			if (i == 0)
    				_t1 = t - _halfT;
    			_y[i] = Math.max(_y0[i] - _v0[i] * _t1 - 0.5 * _a[i] * _t1 * _t1, -_h[i]);
    		}
    	}
    }

    @Override
    protected void endTransition(Image from, Image to, GC gc, double direction) {
    }
}