package com.NamePending.piece;

import java.awt.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.MainSWT;

public class GreenBrickPiece extends StandardPiece
{
    public GreenBrickPiece()
    {
        super(new Color(42, 255, 42), 
				new Image(MainSWT.getDisplay(), SWTResourceManager.getImage(
						MainSWT.class, "/com/NamePending/res/GreenBrickPiece.png")
						.getImageData()));
    }
}
