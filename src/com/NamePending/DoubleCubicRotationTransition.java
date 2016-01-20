package com.NamePending;

/*******************************************************************************
 * Copyright (c) 2010 Ahmed Mahran and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors:
 *     Ahmed Mahran - initial API and implementation
 *     rl - DoubleCubicRotationTransition
 *******************************************************************************/

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * A cubic rotation effect. Showing two sides of a cube, the cube rotates from one
 * side to the other side.
 * 
 * @author Ahmed Mahran (ahmahran@gmail.com)
 * 
 * rl - Changed from nebula source: CubicRotationTransition to do two cubic rotations side by side 
 */
public class DoubleCubicRotationTransition extends Transition {

	private static final int NUM_CUBES = 2;
	private static final int FIRST_CUBE = 0;
	private static final int SECOND_CUBE = 1;
	
    private int _w[], _halfW[], _h[], _halfH[];
    private double _a1[], _a2[], _x[], _y[], _x0[], _y0[], _v0[];
    private boolean _flag1;
    private ImageData _fromData[];
    private long _halfT, _t1, _tSqrd;
    private double _dy1[], _dx1[], _dx2[], _dy2[]
                , _x1[], _y1[], _x2[], _y2[]
                , _destHeight[], _destWidth[]
                , _destHeight0[], _destWidth0[]
                , _destHeightV0[], _destWidthV0[]
                , _ratio1[], _ratio2[]
                , _remainedSize[];
    
    private double _quality = 100;//%
    
    /**
     * This constructor creates a DoubleCubicRotationTransition with number of frames per second of {@link Transition#DEFAULT_FPS}
     * and total transition time of {@link Transition#DEFAULT_T} milliseconds. It is similar to 
     * new DoubleCubicRotationTransition(transitionManager, {@link Transition#DEFAULT_FPS}, {@link Transition#DEFAULT_T})
     * 
     * @param transitionManager the transition manager to be used to manage transitions
     */
    public DoubleCubicRotationTransition(TransitionManager transitionManager) {
        this(transitionManager, DEFAULT_FPS, DEFAULT_T);
    }
    
    /**
     * This constructor creates a DoubleCubicRotationTransition with number of frames per second of <code>fps</code>
     * and total transition time of <code>T</code> milliseconds.
     * 
     * @param transitionManager the transition manager to be used to manage transitions 
     * @param fps number of frames per second
     * @param T the total time the transition effect will take in milliseconds
     */
    public DoubleCubicRotationTransition(TransitionManager transitionManager, long fps, long T) {
        super(transitionManager, fps, T);
    }

    @Override
    protected void initTransition(Image from, Image to, GC gc, double direction) {

        _halfT = (long) (_T / 2.0);
        _fromData = new ImageData[NUM_CUBES];
        ImageData d = from.getImageData();
        int[] pixels = new int[d.width];
        byte[] alphas = new byte[d.width];
        
        if (direction == DIR_RIGHT || direction == DIR_LEFT) {
        	_fromData[FIRST_CUBE] = new ImageData(d.width/2, d.height, d.depth, d.palette);
        	_fromData[SECOND_CUBE] = new ImageData(d.width/2, d.height, d.depth, d.palette);
        	for (int y = 0; y < d.y; y++) {
        		d.getPixels(0, y, d.width, pixels, 0);
        		d.getAlphas(0,  y, d.width, alphas, 0);
        		_fromData[FIRST_CUBE].setPixels(0, y, d.width/2, pixels, 0);
        		_fromData[SECOND_CUBE].setPixels(0, y, d.width/2, pixels, d.width/2);
        		_fromData[FIRST_CUBE].setAlphas(0, y, d.width/2, alphas, 0);
        		_fromData[SECOND_CUBE].setAlphas(0, y, d.width/2, alphas, d.width/2);
        	}
        } else {
        	_fromData[FIRST_CUBE] = new ImageData(d.width, d.height/2, d.depth, d.palette);
        	_fromData[SECOND_CUBE] = new ImageData(d.width, d.height/2, d.depth, d.palette);
        	for (int y = 0; y < d.y/2; y++) {
        		d.getPixels(0, y, d.width, pixels, 0);
        		_fromData[FIRST_CUBE].setPixels(0, y, d.width, pixels, 0);
        		_fromData[FIRST_CUBE].setAlphas(0, y, d.width, alphas, 0);
        	}
        	for (int y = d.y/2; y < d.y; y++) {
        		d.getPixels(0, y, d.width, pixels, 0);
        		_fromData[SECOND_CUBE].setPixels(0, y, d.width, pixels, 0);
        		_fromData[SECOND_CUBE].setAlphas(0, y, d.width, alphas, 0);
        	}
        }
        _w = new int[NUM_CUBES];
        _h = new int[NUM_CUBES];
        _w[FIRST_CUBE] = _fromData[FIRST_CUBE].width;
        _h[FIRST_CUBE] = _fromData[FIRST_CUBE].height;
        _w[SECOND_CUBE] = _fromData[SECOND_CUBE].width;
        _h[SECOND_CUBE] = _fromData[SECOND_CUBE].height;
        _halfW = new int[NUM_CUBES];
        _halfH = new int[NUM_CUBES];
        _halfW[FIRST_CUBE] = (int) (_w[FIRST_CUBE] / 2.0);
        _halfH[FIRST_CUBE] = (int) (_h[FIRST_CUBE] / 2.0);
        _halfW[SECOND_CUBE] = (int) (_w[SECOND_CUBE] / 2.0);
        _halfH[SECOND_CUBE] = (int) (_h[SECOND_CUBE] / 2.0);
        
        _a1 = new double[NUM_CUBES];
        _a2 = new double[NUM_CUBES];
        _x = new double[NUM_CUBES];
        _y = new double[NUM_CUBES];
        _destHeight = new double[NUM_CUBES];
        _destWidth = new double[NUM_CUBES];        
        _dx1 = new double[NUM_CUBES];
        _dx2 = new double[NUM_CUBES];
        _dy1 = new double[NUM_CUBES];
        _dy2 = new double[NUM_CUBES];
        _remainedSize = new double[NUM_CUBES];
        
        switch((int)direction) {
        
        case (int)DIR_RIGHT:
        	// first cube rotates right second rotates left
            _a1[FIRST_CUBE] = _w[FIRST_CUBE] / (double)(_halfT * _halfT);
            _a2[FIRST_CUBE] = _h[FIRST_CUBE] / (double)(_halfT * _halfT);
            _x[FIRST_CUBE] = 0;
            _destHeight[FIRST_CUBE] = 0;
            _dx1[FIRST_CUBE] = _dx2[FIRST_CUBE] = _w[FIRST_CUBE] - _quality * (_w[FIRST_CUBE] - 1) / 100.0;
            _remainedSize[FIRST_CUBE] = _w[FIRST_CUBE] - ((int)(_w[FIRST_CUBE] / _dx1[FIRST_CUBE]) * _dx1[FIRST_CUBE]);
            
            _a1[SECOND_CUBE] = _w[SECOND_CUBE] / (double)(_halfT * _halfT);
            _a2[SECOND_CUBE] = _h[SECOND_CUBE] / (double)(_halfT * _halfT);
            _x[SECOND_CUBE] = _w[SECOND_CUBE];
            _destHeight[SECOND_CUBE] = _h[SECOND_CUBE];
            _dx1[SECOND_CUBE] = _dx2[SECOND_CUBE] = _w[SECOND_CUBE] - _quality * (_w[SECOND_CUBE] - 1) / 100.0;
            _remainedSize[SECOND_CUBE] = _w[SECOND_CUBE] - ((int)(_w[SECOND_CUBE] / _dx1[SECOND_CUBE]) * _dx1[SECOND_CUBE]);            
            break;
        
        case (int)DIR_LEFT:
        	// first cube rotates left second rotates right
            _a1[FIRST_CUBE] = _w[FIRST_CUBE] / (double)(_halfT * _halfT);
            _a2[FIRST_CUBE] = _h[FIRST_CUBE] / (double)(_halfT * _halfT);
            _x[FIRST_CUBE] = _w[FIRST_CUBE];
            _destHeight[FIRST_CUBE] = _h[FIRST_CUBE];
            _dx1[FIRST_CUBE] = _dx2[FIRST_CUBE] = _w[FIRST_CUBE] - _quality * (_w[FIRST_CUBE] - 1) / 100.0;
            _remainedSize[FIRST_CUBE] = _w[FIRST_CUBE] - ((int)(_w[FIRST_CUBE] / _dx1[FIRST_CUBE]) * _dx1[FIRST_CUBE]);
            
            _a1[SECOND_CUBE] = _w[SECOND_CUBE] / (double)(_halfT * _halfT);
            _a2[SECOND_CUBE] = _h[SECOND_CUBE] / (double)(_halfT * _halfT);
            _x[SECOND_CUBE] = 0;
            _destHeight[SECOND_CUBE] = 0;
            _dx1[SECOND_CUBE] = _dx2[SECOND_CUBE] = _w[SECOND_CUBE] - _quality * (_w[SECOND_CUBE] - 1) / 100.0;
            _remainedSize[SECOND_CUBE] = _w[SECOND_CUBE] - ((int)(_w[SECOND_CUBE] / _dx1[SECOND_CUBE]) * _dx1[SECOND_CUBE]);            
            break;
        
        case (int)DIR_UP:
            _a1[FIRST_CUBE] = _h[FIRST_CUBE] / (double)(_halfT * _halfT);
            _a2[FIRST_CUBE] = _w[FIRST_CUBE] / (double)(_halfT * _halfT);
            _y[FIRST_CUBE] = _h[FIRST_CUBE];
            _destWidth[FIRST_CUBE] = _w[FIRST_CUBE];
            _dy1[FIRST_CUBE] = _dy2[FIRST_CUBE] = _h[FIRST_CUBE] - _quality * (_h[FIRST_CUBE] - 1) / 100.0;
            _remainedSize[FIRST_CUBE] = _h[FIRST_CUBE] - ((int)(_h[FIRST_CUBE] / _dy1[FIRST_CUBE]) * _dy1[FIRST_CUBE]);
            break;
            
        case (int)DIR_DOWN:
            _a1[FIRST_CUBE] = _h[FIRST_CUBE] / (double)(_halfT * _halfT);
            _a2[FIRST_CUBE] = _w[FIRST_CUBE] / (double)(_halfT * _halfT);
            _y[FIRST_CUBE] = 0;
            _destWidth[FIRST_CUBE] = 0;
            _dy1[FIRST_CUBE] = _dy2[FIRST_CUBE] = _h[FIRST_CUBE] - _quality * (_h[FIRST_CUBE] - 1) / 100.0;
            _remainedSize[FIRST_CUBE] = _h[FIRST_CUBE] - ((int)(_h[FIRST_CUBE] / _dy1[FIRST_CUBE]) * _dy1[FIRST_CUBE]);
            break;
        }
   
        _ratio1 = new double[NUM_CUBES];
        _ratio2 = new double[NUM_CUBES];
        _x0 = new double[NUM_CUBES];
        _y0 = new double[NUM_CUBES];
        _v0 = new double[NUM_CUBES];
        _x1 = new double[NUM_CUBES];
        _y1 = new double[NUM_CUBES];
        _x2 = new double[NUM_CUBES];
        _y2 = new double[NUM_CUBES];
        _destHeight0 = new double[NUM_CUBES];
        _destWidth0 = new double[NUM_CUBES];
        _destHeightV0 = new double[NUM_CUBES];
        _destWidthV0 = new double[NUM_CUBES];
        
        _flag1 = false;
    }

    @Override
    protected void stepTransition(long t, Image from, Image to, GC gc,
            double direction) {
        
        switch((int)direction) {
        
        case (int)DIR_RIGHT:
            
        	// first cube right
            _ratio1[FIRST_CUBE] = (_w[FIRST_CUBE] - _x[FIRST_CUBE]) / _w[FIRST_CUBE];
            _ratio2[FIRST_CUBE] = (_x[FIRST_CUBE]) / _w[FIRST_CUBE];
            
            _dy1[FIRST_CUBE] = _dx1[FIRST_CUBE] * (_destHeight[FIRST_CUBE]) / (2.0 * _w[FIRST_CUBE]);
            _dy2[FIRST_CUBE] = _dx2[FIRST_CUBE] * (_h[FIRST_CUBE] - _destHeight[FIRST_CUBE]) / (2.0 * _w[FIRST_CUBE]);
            _x1[FIRST_CUBE] = 0; _y1[FIRST_CUBE] = 0; _x2[FIRST_CUBE] = 0; _y2[FIRST_CUBE] = (_h[FIRST_CUBE] - _destHeight[FIRST_CUBE]) / 2.0;
            // second cube left
            _ratio1[SECOND_CUBE] = (_x[SECOND_CUBE]) / _w[SECOND_CUBE];
            _ratio2[SECOND_CUBE] = (_w[SECOND_CUBE] - _x[SECOND_CUBE]) / _w[SECOND_CUBE];
            
            _dy1[SECOND_CUBE] = _dx1[SECOND_CUBE] * (_h[SECOND_CUBE] - _destHeight[SECOND_CUBE]) / (2.0 * _w[SECOND_CUBE]);
            _dy2[SECOND_CUBE] = _dx2[SECOND_CUBE] * (_destHeight[SECOND_CUBE]) / (2.0 * _w[SECOND_CUBE]);
            _x1[SECOND_CUBE] = 0; _y1[SECOND_CUBE] = (_h[SECOND_CUBE] - _destHeight[SECOND_CUBE]) / 2.0; _x2[SECOND_CUBE] = 0; _y2[SECOND_CUBE] = 0;
                      
            // first cube right
            for (; _x1[FIRST_CUBE] < _w[FIRST_CUBE]; _x1[FIRST_CUBE] += _dx1[FIRST_CUBE]) {
                try {
                    _x2[FIRST_CUBE] = _x1[FIRST_CUBE];
                    gc.drawImage(from, (int) _x1[FIRST_CUBE], 0, (int) _dx1[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x[FIRST_CUBE] + _x1[FIRST_CUBE] * _ratio1[FIRST_CUBE]), (int) _y1[FIRST_CUBE]
                            , (int) _dx1[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y1[FIRST_CUBE] - _y1[FIRST_CUBE]));
                    gc.drawImage(to, (int) _x2[FIRST_CUBE], 0, (int) _dx2[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x2[FIRST_CUBE] * _ratio2[FIRST_CUBE]), (int) _y2[FIRST_CUBE]
                            , (int) _dx2[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y2[FIRST_CUBE] - _y2[FIRST_CUBE]));
                    _y1[FIRST_CUBE] += _dy1[FIRST_CUBE];
                    _y2[FIRST_CUBE] -= _dy2[FIRST_CUBE];
                } catch (Exception e) {
                    gc.drawImage(from, (int) _x1[FIRST_CUBE], 0, (int) _remainedSize[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x[FIRST_CUBE] + _x1[FIRST_CUBE] * _ratio1[FIRST_CUBE]), (int) _y1[FIRST_CUBE]
                            , (int) _remainedSize[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y1[FIRST_CUBE] - _y1[FIRST_CUBE]));
                    gc.drawImage(to, (int) _x2[FIRST_CUBE], 0, (int) _remainedSize[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x2[FIRST_CUBE] * _ratio2[FIRST_CUBE]), (int) _y2[FIRST_CUBE]
                            , (int) _remainedSize[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y2[FIRST_CUBE] - _y2[FIRST_CUBE]));
                }
            }
            // second cube left
            for (; _x1[SECOND_CUBE] < _w[SECOND_CUBE]; _x1[SECOND_CUBE] += _dx1[SECOND_CUBE]) {
                try {
                    gc.drawImage(to, (int) _x1[SECOND_CUBE], 0, (int) _dx1[SECOND_CUBE], _h[SECOND_CUBE],
                            (int) (_x1[SECOND_CUBE] * _ratio1[SECOND_CUBE]) + _w[FIRST_CUBE], (int) _y1[SECOND_CUBE]
                            , (int) _dx1[SECOND_CUBE], (int) (_h[SECOND_CUBE] - _y1[SECOND_CUBE] - _y1[SECOND_CUBE]));
                    _y1[SECOND_CUBE] -= _dy1[SECOND_CUBE];
                } catch (Exception e) {
                    gc.drawImage(to, (int) _x1[SECOND_CUBE], 0, (int) _remainedSize[SECOND_CUBE], _h[SECOND_CUBE],
                            (int) (_x1[SECOND_CUBE] * _ratio1[SECOND_CUBE]) + _w[FIRST_CUBE], (int) _y1[SECOND_CUBE]
                            , (int) _remainedSize[SECOND_CUBE], (int) (_h[SECOND_CUBE] - _y1[SECOND_CUBE] - _y1[SECOND_CUBE]));
                }
            }
            for (; _x2[SECOND_CUBE] < _w[SECOND_CUBE]; _x2[SECOND_CUBE] += _dx2[SECOND_CUBE]) {
                try {
                    gc.drawImage(from, (int) _x2[SECOND_CUBE], 0, (int) _dx2[SECOND_CUBE], _h[SECOND_CUBE],
                            (int) (_x[SECOND_CUBE] + _x2[SECOND_CUBE] * _ratio2[SECOND_CUBE]) + _w[FIRST_CUBE], (int) _y2[SECOND_CUBE]
                            , (int) _dx2[SECOND_CUBE], (int) (_h[SECOND_CUBE] - _y2[SECOND_CUBE] - _y2[SECOND_CUBE]));
                    _y2[SECOND_CUBE] += _dy2[SECOND_CUBE];
                } catch (Exception e) {
                    gc.drawImage(from, (int) _x2[SECOND_CUBE], 0, (int) _remainedSize[SECOND_CUBE], _h[SECOND_CUBE],
                            (int) (_x[SECOND_CUBE] + _x2[SECOND_CUBE] * _ratio2[SECOND_CUBE]) + _w[FIRST_CUBE], (int) _y2[SECOND_CUBE]
                            , (int) _remainedSize[SECOND_CUBE], (int) (_h[SECOND_CUBE] - _y2[SECOND_CUBE] - _y2[SECOND_CUBE]));
                }
            }            
            
            if( t <= _halfT ) {
            	// first cube right
                _tSqrd = t * t;
                _x[FIRST_CUBE] = Math.min(0.5 * _a1[FIRST_CUBE] * _tSqrd, _halfW[FIRST_CUBE]);
                _destHeight[FIRST_CUBE] = Math.min(0.5 * _a2[FIRST_CUBE] * _tSqrd, _halfH[FIRST_CUBE]);
                // second cube left
                _x[SECOND_CUBE] = _w[SECOND_CUBE] - Math.min(0.5 * _a1[SECOND_CUBE] * _tSqrd, _halfW[SECOND_CUBE]);
                _destHeight[SECOND_CUBE] = _h[SECOND_CUBE] - Math.min(0.5 * _a2[SECOND_CUBE] * _tSqrd, _halfH[SECOND_CUBE]);                
            } else {
                if(!_flag1) {
                	// first cube right
                    _x0[FIRST_CUBE] = _x[FIRST_CUBE]; _destHeight0[FIRST_CUBE] = _destHeight[FIRST_CUBE];
                    _v0[FIRST_CUBE] = _a1[FIRST_CUBE] * t; _destHeightV0[FIRST_CUBE] = _a2[FIRST_CUBE] * t;
                    _a1[FIRST_CUBE] *= -1.0; _a2[FIRST_CUBE] *= -1.0;
                    // second cube left
                    _x0[SECOND_CUBE] = _w[SECOND_CUBE] - _x[SECOND_CUBE]; _destHeight0[SECOND_CUBE] = _h[SECOND_CUBE] - _destHeight[SECOND_CUBE];
                    _v0[SECOND_CUBE] = _a1[SECOND_CUBE] * t; _destHeightV0[SECOND_CUBE] = _a2[SECOND_CUBE] * t;
                    _a1[SECOND_CUBE] *= -1.0; _a2[SECOND_CUBE] *= -1.0;
                    _flag1 = true;
                }
                // first cube right
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _x[FIRST_CUBE] = Math.min(_x0[FIRST_CUBE] + _v0[FIRST_CUBE] * _t1 + 0.5 * _a1[FIRST_CUBE] * _tSqrd, _w[FIRST_CUBE]);
                _destHeight[FIRST_CUBE] = Math.min(_destHeight0[FIRST_CUBE] + _destHeightV0[FIRST_CUBE] * _t1 + 0.5 * _a2[FIRST_CUBE] * _tSqrd, _h[FIRST_CUBE]);
                // second cube left
                _x[SECOND_CUBE] = _w[SECOND_CUBE] - Math.min(_x0[SECOND_CUBE] + _v0[SECOND_CUBE] * _t1 + 0.5 * _a1[SECOND_CUBE] * _tSqrd, _w[SECOND_CUBE]);
                _destHeight[SECOND_CUBE] = _h[SECOND_CUBE] - Math.min(_destHeight0[SECOND_CUBE] + _destHeightV0[SECOND_CUBE] * _t1 + 0.5 * _a2[SECOND_CUBE] * _tSqrd, _h[SECOND_CUBE]);
            }
            break;
            
        case (int)DIR_LEFT:
            
            _ratio1[FIRST_CUBE] = (_x[FIRST_CUBE]) / _w[FIRST_CUBE];
            _ratio2[FIRST_CUBE] = (_w[FIRST_CUBE] - _x[FIRST_CUBE]) / _w[FIRST_CUBE];
            
            _dy1[FIRST_CUBE] = _dx1[FIRST_CUBE] * (_h[FIRST_CUBE] - _destHeight[FIRST_CUBE]) / (2.0 * _w[FIRST_CUBE]);
            _dy2[FIRST_CUBE] = _dx2[FIRST_CUBE] * (_destHeight[FIRST_CUBE]) / (2.0 * _w[FIRST_CUBE]);
            _x1[FIRST_CUBE] = 0; _y1[FIRST_CUBE] = (_h[FIRST_CUBE] - _destHeight[FIRST_CUBE]) / 2.0; _x2[FIRST_CUBE] = 0; _y2[FIRST_CUBE] = 0;
            
            for (; _x1[FIRST_CUBE] < _w[FIRST_CUBE]; _x1[FIRST_CUBE] += _dx1[FIRST_CUBE]) {
                try {
                    gc.drawImage(from, (int) _x1[FIRST_CUBE], 0, (int) _dx1[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x1[FIRST_CUBE] * _ratio1[FIRST_CUBE]), (int) _y1[FIRST_CUBE]
                            , (int) _dx1[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y1[FIRST_CUBE] - _y1[FIRST_CUBE]));
                    _y1[FIRST_CUBE] -= _dy1[FIRST_CUBE];
                } catch (Exception e) {
                    gc.drawImage(from, (int) _x1[FIRST_CUBE], 0, (int) _remainedSize[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x1[FIRST_CUBE] * _ratio1[FIRST_CUBE]), (int) _y1[FIRST_CUBE]
                            , (int) _remainedSize[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y1[FIRST_CUBE] - _y1[FIRST_CUBE]));
                }
            }
            for (; _x2[FIRST_CUBE] < _w[FIRST_CUBE]; _x2[FIRST_CUBE] += _dx2[FIRST_CUBE]) {
                try {
                    gc.drawImage(to, (int) _x2[FIRST_CUBE], 0, (int) _dx2[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x[FIRST_CUBE] + _x2[FIRST_CUBE] * _ratio2[FIRST_CUBE]), (int) _y2[FIRST_CUBE]
                            , (int) _dx2[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y2[FIRST_CUBE] - _y2[FIRST_CUBE]));
                    _y2[FIRST_CUBE] += _dy2[FIRST_CUBE];
                } catch (Exception e) {
                    gc.drawImage(to, (int) _x2[FIRST_CUBE], 0, (int) _remainedSize[FIRST_CUBE], _h[FIRST_CUBE],
                            (int) (_x[FIRST_CUBE] + _x2[FIRST_CUBE] * _ratio2[FIRST_CUBE]), (int) _y2[FIRST_CUBE]
                            , (int) _remainedSize[FIRST_CUBE], (int) (_h[FIRST_CUBE] - _y2[FIRST_CUBE] - _y2[FIRST_CUBE]));
                }
            }
        
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _x[FIRST_CUBE] = _w[FIRST_CUBE] - Math.min(0.5 * _a1[FIRST_CUBE] * _tSqrd, _halfW[FIRST_CUBE]);
                _destHeight[FIRST_CUBE] = _h[FIRST_CUBE] - Math.min(0.5 * _a2[FIRST_CUBE] * _tSqrd, _halfH[FIRST_CUBE]);
                
            } else {
                
                if(!_flag1) {
                    
                    _x0[FIRST_CUBE] = _w[FIRST_CUBE] - _x[FIRST_CUBE]; _destHeight0[FIRST_CUBE] = _h[FIRST_CUBE] - _destHeight[FIRST_CUBE];
                    _v0[FIRST_CUBE] = _a1[FIRST_CUBE] * t; _destHeightV0[FIRST_CUBE] = _a2[FIRST_CUBE] * t;
                    _a1[FIRST_CUBE] *= -1.0; _a2[FIRST_CUBE] *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _x[FIRST_CUBE] = _w[FIRST_CUBE] - Math.min(_x0[FIRST_CUBE] + _v0[FIRST_CUBE] * _t1 + 0.5 * _a1[FIRST_CUBE] * _tSqrd, _w[FIRST_CUBE]);
                _destHeight[FIRST_CUBE] = _h[FIRST_CUBE] - Math.min(_destHeight0[FIRST_CUBE] + _destHeightV0[FIRST_CUBE] * _t1 + 0.5 * _a2[FIRST_CUBE] * _tSqrd, _h[FIRST_CUBE]);
                
            }
            break;
        
        case (int)DIR_UP:
            
            _ratio1[FIRST_CUBE] = (_y[FIRST_CUBE]) / _h[FIRST_CUBE];
            _ratio2[FIRST_CUBE] = (_h[FIRST_CUBE] - _y[FIRST_CUBE]) / _h[FIRST_CUBE];
            
            _dx1[FIRST_CUBE] = _dy1[FIRST_CUBE] * (_w[FIRST_CUBE] - _destWidth[FIRST_CUBE]) / (2.0 * _h[FIRST_CUBE]);
            _dx2[FIRST_CUBE] = _dy2[FIRST_CUBE] * (_destWidth[FIRST_CUBE]) / (2.0 * _h[FIRST_CUBE]);
            _y1[FIRST_CUBE] = 0; _x1[FIRST_CUBE] = (_w[FIRST_CUBE] - _destWidth[FIRST_CUBE]) / 2.0; _y2[FIRST_CUBE] = 0; _x2[FIRST_CUBE] = 0;
            
            for (; _y1[FIRST_CUBE] < _h[FIRST_CUBE]; _y1[FIRST_CUBE] += _dy1[FIRST_CUBE]) {
                try {
                    gc.drawImage(from, 0, (int) _y1[FIRST_CUBE], _w[FIRST_CUBE], (int) _dy1[FIRST_CUBE]
                            , (int) _x1[FIRST_CUBE], (int) (_y1[FIRST_CUBE] * _ratio1[FIRST_CUBE]) 
                            , (int) (_w[FIRST_CUBE] - _x1[FIRST_CUBE] - _x1[FIRST_CUBE]), (int) _dy1[FIRST_CUBE]);
                    _x1[FIRST_CUBE] -= _dx1[FIRST_CUBE];
                } catch (Exception e) {
                    gc.drawImage(from, 0, (int) _y1[FIRST_CUBE], _w[FIRST_CUBE], (int) _remainedSize[FIRST_CUBE]
                            , (int) _x1[FIRST_CUBE], (int) (_y1[FIRST_CUBE] * _ratio1[FIRST_CUBE]) 
                            , (int) (_w[FIRST_CUBE] - _x1[FIRST_CUBE] - _x1[FIRST_CUBE]), (int) _remainedSize[FIRST_CUBE]);
                }
            }
            for (; _y2[FIRST_CUBE] < _h[FIRST_CUBE]; _y2[FIRST_CUBE] += _dy2[FIRST_CUBE]) {
                try {
                    gc.drawImage(to, 0, (int) _y2[FIRST_CUBE], _w[FIRST_CUBE], (int) _dy2[FIRST_CUBE]
                            , (int) _x2[FIRST_CUBE], (int) (_y[FIRST_CUBE] + _y2[FIRST_CUBE] * _ratio2[FIRST_CUBE])
                            , (int) (_w[FIRST_CUBE] - _x2[FIRST_CUBE] - _x2[FIRST_CUBE]), (int) _dy2[FIRST_CUBE]);
                    _x2[FIRST_CUBE] += _dx2[FIRST_CUBE];
                } catch (Exception e) {
                    gc.drawImage(to, 0, (int) _y2[FIRST_CUBE], _w[FIRST_CUBE], (int) _remainedSize[FIRST_CUBE]
                            , (int) _x2[FIRST_CUBE], (int) (_y[FIRST_CUBE] + _y2[FIRST_CUBE] * _ratio2[FIRST_CUBE])
                            , (int) (_w[FIRST_CUBE] - _x2[FIRST_CUBE] - _x2[FIRST_CUBE]), (int) _remainedSize[FIRST_CUBE]);
                }
            }
        
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _y[FIRST_CUBE] = _h[FIRST_CUBE] - Math.min(0.5 * _a1[FIRST_CUBE] * _tSqrd, _halfH[FIRST_CUBE]);
                _destWidth[FIRST_CUBE] = _w[FIRST_CUBE] - Math.min(0.5 * _a2[FIRST_CUBE] * _tSqrd, _halfW[FIRST_CUBE]);
                
            } else {
                
                if(!_flag1) {
                    
                    _y0[FIRST_CUBE] = _h[FIRST_CUBE] - _y[FIRST_CUBE]; _destWidth0[FIRST_CUBE] = _w[FIRST_CUBE] - _destWidth[FIRST_CUBE];
                    _v0[FIRST_CUBE] = _a1[FIRST_CUBE] * t; _destWidthV0[FIRST_CUBE] = _a2[FIRST_CUBE] * t;
                    _a1[FIRST_CUBE] *= -1.0; _a2[FIRST_CUBE] *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _y[FIRST_CUBE] = _h[FIRST_CUBE] - Math.min(_y0[FIRST_CUBE] + _v0[FIRST_CUBE] * _t1 + 0.5 * _a1[FIRST_CUBE] * _tSqrd, _h[FIRST_CUBE]);
                _destWidth[FIRST_CUBE] = _w[FIRST_CUBE] - Math.min(_destWidth0[FIRST_CUBE] + _destWidthV0[FIRST_CUBE] * _t1 + 0.5 * _a2[FIRST_CUBE] * _tSqrd, _w[FIRST_CUBE]);
                
            }
            break;
        
        case (int)DIR_DOWN:
            
            _ratio1[FIRST_CUBE] = (_h[FIRST_CUBE] - _y[FIRST_CUBE]) / _h[FIRST_CUBE];
            _ratio2[FIRST_CUBE] = (_y[FIRST_CUBE]) / _h[FIRST_CUBE];
            
            _dx1[FIRST_CUBE] = _dy1[FIRST_CUBE] * (_destWidth[FIRST_CUBE]) / (2.0 * _h[FIRST_CUBE]);
            _dx2[FIRST_CUBE] = _dy2[FIRST_CUBE] * (_w[FIRST_CUBE] - _destWidth[FIRST_CUBE]) / (2.0 * _h[FIRST_CUBE]);
            _y1[FIRST_CUBE] = 0; _x1[FIRST_CUBE] = 0; _y2[FIRST_CUBE] = 0; _x2[FIRST_CUBE] = (_w[FIRST_CUBE] - _destWidth[FIRST_CUBE]) / 2.0;
            
            for (; _y1[FIRST_CUBE] < _h[FIRST_CUBE]; _y1[FIRST_CUBE] += _dy1[FIRST_CUBE]) {
                try {
                    _y2[FIRST_CUBE] = _y1[FIRST_CUBE];
                    gc.drawImage(from, 0, (int) _y1[FIRST_CUBE], _w[FIRST_CUBE], (int) _dy1[FIRST_CUBE]
                            , (int) _x1[FIRST_CUBE], (int) (_y[FIRST_CUBE] + _y1[FIRST_CUBE] * _ratio1[FIRST_CUBE])
                            , (int) (_w[FIRST_CUBE] - _x1[FIRST_CUBE] - _x1[FIRST_CUBE]), (int) _dy1[FIRST_CUBE]);
                    gc.drawImage(to, 0, (int) _y2[FIRST_CUBE], _w[FIRST_CUBE], (int) _dy2[FIRST_CUBE]
                            , (int) _x2[FIRST_CUBE], (int) (_y2[FIRST_CUBE] * _ratio2[FIRST_CUBE])
                            , (int) (_w[FIRST_CUBE] - _x2[FIRST_CUBE] - _x2[FIRST_CUBE]), (int) _dy2[FIRST_CUBE]);
                    _x1[FIRST_CUBE] += _dx1[FIRST_CUBE];
                    _x2[FIRST_CUBE] -= _dx2[FIRST_CUBE];
                } catch (Exception e) {
                    gc.drawImage(from, 0, (int) _y1[FIRST_CUBE], _w[FIRST_CUBE], (int) _remainedSize[FIRST_CUBE]
                            , (int) _x1[FIRST_CUBE], (int) (_y[FIRST_CUBE] + _y1[FIRST_CUBE] * _ratio1[FIRST_CUBE])
                            , (int) (_w[FIRST_CUBE] - _x1[FIRST_CUBE] - _x1[FIRST_CUBE]), (int) _remainedSize[FIRST_CUBE]);
                    gc.drawImage(to, 0, (int) _y2[FIRST_CUBE], _w[FIRST_CUBE], (int) _remainedSize[FIRST_CUBE]
                            , (int) _x2[FIRST_CUBE], (int) (_y2[FIRST_CUBE] * _ratio2[FIRST_CUBE])
                            , (int) (_w[FIRST_CUBE] - _x2[FIRST_CUBE] - _x2[FIRST_CUBE]), (int) _remainedSize[FIRST_CUBE]);
                }
            }
            
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _y[FIRST_CUBE] = Math.min(0.5 * _a1[FIRST_CUBE] * _tSqrd, _halfH[FIRST_CUBE]);
                _destWidth[FIRST_CUBE] = Math.min(0.5 * _a2[FIRST_CUBE] * _tSqrd, _halfW[FIRST_CUBE]);
                
            } else {
                
                if(!_flag1) {
                    
                    _y0[FIRST_CUBE] = _y[FIRST_CUBE]; _destWidth0[FIRST_CUBE] = _destWidth[FIRST_CUBE];
                    _v0[FIRST_CUBE] = _a1[FIRST_CUBE] * t; _destWidthV0[FIRST_CUBE] = _a2[FIRST_CUBE] * t;
                    _a1[FIRST_CUBE] *= -1.0; _a2[FIRST_CUBE] *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _y[FIRST_CUBE] = Math.min(_y0[FIRST_CUBE] + _v0[FIRST_CUBE] * _t1 + 0.5 * _a1[FIRST_CUBE] * _tSqrd, _h[FIRST_CUBE]);
                _destWidth[FIRST_CUBE] = Math.min(_destWidth0[FIRST_CUBE] + _destWidthV0[FIRST_CUBE] * _t1 + 0.5 * _a2[FIRST_CUBE] * _tSqrd, _w[FIRST_CUBE]);
                
            }
            break;
        
        }
        
    }
    
    @Override
    protected void endTransition(Image from, Image to, GC gc, double direction) {

    }
    
    /**
     * Sets the quality of image slicing as a percentage in
     * the interval from 0 to 100 inclusive
     * 
     * @param quality is a percentage from 0 to 100 inclusive
     */
    public void setQuality(double quality) {
        if(quality >= 0.0 && quality <= 100.0)
            _quality = quality;
    }
    
    
    /**
     * Returns a percentage representing the quality of image slicing
     * @return a percentage representing the quality of image slicing
     */
    public double getQuality() {
        return _quality;
    }

}
