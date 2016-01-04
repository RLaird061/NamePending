package com.NamePending;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.AlphaEffect;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class MainSWT {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.NO_TRIM);
		shell.setAlpha(0);
		shell.setSize(500, 500);

		Image img = new Image(display, SWTResourceManager.getImage(
				MainSWT.class, "/com/NamePending/res/LauncherScreen.png")
				.getImageData());
		shell.setBackgroundImage(img);

		AnimationRunner animationRunner = new AnimationRunner();
		// fade out                    2.9 secs
		AlphaEffect.fadeOnClose(shell, 2900, new ExpoOut(), animationRunner);

		// fade in                             160 alpha, 1.9 secs
		shell.open();
		AlphaEffect.setAlpha(animationRunner, shell, 160, 1900, new ExpoOut(), null, null);
		
		Button btnExit = new Button(shell, SWT.NONE);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				shell.close();
			}
		});
		btnExit.setBounds(395, 10, 95, 31);
		btnExit.setText("EXIT");
		
		GameBoard gb = new GameBoard();
		UnitTests ut = new UnitTests(gb);
		ut.UnitTest1();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		display.dispose();		
	}
}
