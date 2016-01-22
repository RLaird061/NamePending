package com.NamePending.piece;

import java.awt.Color;
import java.util.Arrays;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.GameSWT;
import com.NamePending.MainSWT;

public class NullPiece extends Piece
{
	private static Image getNullImage()
	{
		ImageData tmp = SWTResourceManager.getImage(MainSWT.class, "/com/NamePending/res/BlueBrickPiece.png").getImageData();
		ImageData d = new ImageData(GameSWT.PIECE_LENGTH, GameSWT.PIECE_LENGTH, tmp.depth, tmp.palette);
		int[] pixels = new int[GameSWT.PIECE_LENGTH];
		byte[] alphas = new byte[GameSWT.PIECE_LENGTH];
		Arrays.fill(pixels, (int)0xFF000000);
		Arrays.fill(alphas, (byte)0);
		for (int y = 0; y < GameSWT.PIECE_LENGTH; y++) {
			d.setPixels(0, y, GameSWT.PIECE_LENGTH, pixels, 0);
			d.setAlphas(0, y, GameSWT.PIECE_LENGTH, alphas, 0);
		}
		return new Image(MainSWT.getDisplay(), d);
	}
    public NullPiece()
    {
        super(Color.BLACK, getNullImage());
    }
}
