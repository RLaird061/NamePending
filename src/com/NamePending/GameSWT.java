package com.NamePending;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class GameSWT extends Composite
{
	// Constants
	public final static int OUTLINE_WIDTH = 2;
	public final static int BORDER_WIDTH = 7;
	public final static int PIECES_PER_ROW = 6;
	public final static int PIECES_PER_COL = 12;
	public final static int PIECE_LENGTH = 45;
	public final static int SELECTOR_WIDTH = 90;
	public final static int SELECTOR_HEIGHT = 45;
	
	public final static int BI_UP = 0;
	public final static int BI_DOWN = 1;
	public final static int BI_LEFT = 2;
	public final static int BI_RIGHT = 3;
	public final static int BI_SPACE = 4;
	
	// Member Variables
	private GameBoard gameboard;    // holds game logic
	private GameComposite gamecomp; // game drawing
	private Label lblScore;
	private Text txtGameOverName;
	private ProgressBar pbGameOverTimer;
	
	public boolean gameOverDirty = false;
	public int gameOverSelection = 100;
	public int gameOverDecreaseAmt = 5;
	public int gameOverTimeout = 5000;
	
	// Member Functions
	public GameSWT(Composite parent, int style) {
		super(parent, style);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
				// Handle ARROW keys and SWAP(space)
				//System.out.println(e.toString());
				Point p = gamecomp.getSelector();
				int selX = p.x;
				int selY = p.y;
				if (e.keyCode == 0x1000001) { // Up
					selY = (selY > 0) ? selY-1 : selY;
				} else if (e.keyCode == 0x1000004) { // Down
					selX = (selX < PIECES_PER_ROW-2) ? selX+1 : selX;
				} else if (e.keyCode == 0x1000002) { // Right
					selY = (selY < PIECES_PER_COL-1) ? selY+1 : selY;
				} else if (e.keyCode == 0x1000003) { // Left
					selX = (selX > 0) ? selX-1 : selX;					
				} else if (e.keyCode == 0x20) { // Space
					gameboard.swap(selY * PIECES_PER_ROW + selX);
				}
				gamecomp.setSelector(new Point(selX, selY));
				redraw(0, 0, parent.getBounds().width, parent.getBounds().height, true);
			}
		});

		setSize(415, 
				554);
		setLayout(null);
		
		gamecomp = new GameComposite(this, SWT.DOUBLE_BUFFERED);
		gamecomp.setBounds(0, 0, 415, 554);
		gamecomp.setSelector(new Point(0,0)); // start selector at top left
		gameboard = new GameBoard(PIECES_PER_ROW, PIECES_PER_COL);
		gameboard.setComposite(gamecomp);
		
		lblScore = new Label(gamecomp, SWT.CENTER);
		lblScore.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		lblScore.setBounds(284, 10, 125, 30);
		lblScore.setText("0");
		
		// TODO: remove me!
//		Button btnGameover = new Button(gamecomp, SWT.NONE);
//		btnGameover.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseUp(MouseEvent e) {
//				GameDatabase db = new GameDatabase();
//				try {
//					int score = Integer.parseInt(lblScore.getText());
//					db.writeHighScore(txtName.getText(), score);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}				
//			}
//		});
//		btnGameover.setBounds(430, 381, 75, 25);
//		btnGameover.setText("GameOver");
		
		pbGameOverTimer = new ProgressBar(gamecomp, SWT.NONE);
		pbGameOverTimer.setSelection(gameOverSelection);
		pbGameOverTimer.setBounds(284, 46, 125, 17);
		
		btnTesting = new Button(gamecomp, SWT.NONE);
		btnTesting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
			}
		});
		btnTesting.setBounds(284, 519, 75, 25);
		btnTesting.setText("Testing");
		
		Composite compGameOver = new Composite(this, SWT.NONE);
		compGameOver.setBounds(GameSWT.BORDER_WIDTH-1, 140, 
				GameSWT.PIECES_PER_ROW*GameSWT.PIECE_LENGTH + 3, 85);
		compGameOver.setLayout(new GridLayout(1, false));
		
		lblGameOverScore = new Label(compGameOver, SWT.CENTER);
		lblGameOverScore.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblGameOverScore.setText("0");
		lblGameOverScore.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		compGameOver.setVisible(false);
		
		txtGameOverName = new Text(compGameOver, SWT.BORDER);
		txtGameOverName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		txtGameOverName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (txtGameOverName.getText().length() > 3)
					txtGameOverName.setText(txtGameOverName.getText().substring(0, 3));
			}
		});
		txtGameOverName.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		
		Shell newShell = new Shell(MainSWT.getDisplay(), SWT.NONE);
		
		setFocus();
	}
	
	public void changeScore(int score) {
		String str = String.format("%d", score);
		lblScore.setText(str);
	}
	
	public void addToScore(int score) {
		int prev = Integer.parseInt(lblScore.getText());
		String str = String.format("%d", score+prev);
		lblScore.setText(str);
	}
	
	public void addGameTimer() {
		Timer myTimer = new Timer(gameOverTimeout, myAction);
		myTimer.start();
	}

	private Action myAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("action");
			if (gameOverSelection >= gameOverDecreaseAmt && !gameOverDirty)
				gameOverSelection -= gameOverDecreaseAmt;

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					pbGameOverTimer.setSelection(gameOverSelection);
					pbGameOverTimer.redraw();

					// game is OVER! you timed out
					if (gameOverSelection == 0) {
						GameDatabase db = new GameDatabase();
						try {
							int score = Integer.parseInt(lblScore.getText());
							db.writeHighScore(txtGameOverName.getText(), score);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});			
		}
	};
	private Label lblGameOverScore;
	private Button btnTesting;
}