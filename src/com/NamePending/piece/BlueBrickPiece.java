package com.NamePending.piece;

import java.awt.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.MainSWT;

public class BlueBrickPiece extends StandardPiece
{
	/*! \brief BlueBrickPiece()
	 *         Calls superclass making a new Image for BlueBalloon.png
	 *
	 *  Calls superclass making a new Image for BlueBalloon.png. Using SWT Image.
	 */	
    public BlueBrickPiece()
    {
        super(new Color(42, 42, 255), 
				new Image(MainSWT.getDisplay(), SWTResourceManager.getImage(
						MainSWT.class, "/com/NamePending/res/BlueBalloon.png")
						.getImageData()));
    }
}
