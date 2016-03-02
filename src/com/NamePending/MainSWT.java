package com.NamePending;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.AlphaEffect;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.unittest.UnitTests;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MainSWT {
	public static GameSWT gameFrame = null;
	private static Display display = null;
	private static Shell shell = null;
	private static GameOptions gameOpt = null;
	
	private static boolean shellMoving = false;
	private static Point shellPos;

/*! \brief main entry point of program.
 *         main(String[] args) no arguments handled currently
 *
 *  Detailed description of main...
 */	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display, SWT.NONE);
		shellPos = new Point(0, 0);
		shell.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (shellMoving)
					shell.setLocation(shell.getLocation().x+(e.x-shellPos.x),shell.getLocation().y+(e.y-shellPos.y));
			}
		});
		shell.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shellMoving = true;
				shellPos.x = e.x;
				shellPos.y = e.y;
			}
			@Override
			public void mouseUp(MouseEvent e) {
				shellMoving = false;
			}
		});
		shell.setAlpha(0);
		shell.setSize(500, 500);

		Image img = new Image(display, SWTResourceManager.getImage(
				MainSWT.class, "/com/NamePending/res/lolpoison.png")
				.getImageData());
		shell.setBackgroundImage(img);

		AnimationRunner animationRunner = new AnimationRunner();
		// fade out                    2.9 secs
		AlphaEffect.fadeOnClose(shell, 2900, new ExpoOut(), animationRunner);

		// fade in                             160 alpha, 1.9 secs
		shell.open();
		AlphaEffect.setAlpha(animationRunner, shell, 255, 1900, new ExpoOut(), null, null);
		
		Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// open GameSWT window and maybe close this window
				
				// TODO: move all this to a seperate process???
				try {
					Shell gameShell = new Shell(display, SWT.CLOSE);
					gameFrame = new GameSWT(gameShell, SWT.DOUBLE_BUFFERED);
					
					gameShell.setText("_NamePendingGame_");
					Image img = new Image(display, SWTResourceManager.getImage(
							MainSWT.class, "/com/NamePending/res/Icon.png")
							.getImageData());
					gameShell.setImage(img);
					
					gameShell.setLocation(shell.getBounds().x, shell.getBounds().y);
					gameShell.pack();
					gameShell.open();
					
					MainSWT.shell.setVisible(false);
					
					gameFrame.addGameTimer();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnPlay.setText("Play");
		btnPlay.setBounds(398, 382, 92, 32);
		
		Button btnOpt = new Button(shell, SWT.NONE);	
		btnOpt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// opens GameOptions window
				
				// TODO: make good-looking options window
				gameOpt = new GameOptions();
				gameOpt.setVisible(true);
			}
		});	
		btnOpt.setText("Options");
		btnOpt.setBounds(398, 420, 92, 32);
		
		Button btnExit = new Button(shell, SWT.NONE);
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (gameFrame != null)
					gameFrame.dispose();
				shell.close();
			}
		});
		btnExit.setBounds(398, 458, 92, 32);
		btnExit.setText("Exit");
		
		try {
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) 
					display.sleep();
			}
			display.dispose();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}

	public static Display getDisplay() {
		return display;
	}

	public static void setDisplay(Display display) {
		MainSWT.display = display;
	}

	public static Shell getShell() {
		return shell;
	}

	public static void setShell(Shell shell) {
		MainSWT.shell = shell;
	}

	public static GameSWT getGameFrame() {
		return gameFrame;
	}

	public static void setGameFrame(GameSWT gameFrame) {
		MainSWT.gameFrame = gameFrame;
	}
}
