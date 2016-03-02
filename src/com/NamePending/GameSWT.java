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
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

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
	public final static int MAX_HIGHSCORESTOSHOW = 10;
	
	// Member Variables
	private GameBoard gameboard;    // holds game logic
	private GameComposite gamecomp; // game drawing
	private Composite compGameOver;
	private Composite compGameOverHighScoreList;
	private Label lblScore; // game screen score
	private Label lblGameOverScore; // score on game over screen
	private Text txtGameOverName; // initials
	private ProgressBar pbGameOverTimer; // timer (see below)
	private List listHighScores;	// top 10 list
	
	public boolean gameOverMoveMade = false;
	public int gameOverSelection = 100;
	public int gameOverDecreaseAmt = 2;
	public int gameOverTimeout = 250;
	
	// Member Functions
	public GameSWT(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				// make main shell visible after closing game shell
				// via any means
				MainSWT.getShell().setVisible(true);
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
				// Handle ARROW keys and SWAP(space)
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

		setSize(415, 554);
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
		
		pbGameOverTimer = new ProgressBar(gamecomp, SWT.NONE);
		pbGameOverTimer.setSelection(gameOverSelection);
		pbGameOverTimer.setBounds(284, 46, 125, 17);
		
		compGameOver = new Composite(this, SWT.BORDER);
		compGameOver.setBounds(6, 140, 273, 257);
		compGameOver.setLayout(new GridLayout(1, false));
		compGameOver.setVisible(false);

		Label lblGameOver = new Label(compGameOver, SWT.CENTER);
		lblGameOver.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblGameOver.setBounds(0, 0, 55, 15);
		lblGameOver.setText("GAME OVER!");
		lblGameOver.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));		
		
		Label lblGameOverScorePrompt = new Label(compGameOver, SWT.CENTER);
		lblGameOverScorePrompt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblGameOverScorePrompt.setText("Your score is: ");
		lblGameOverScorePrompt.setFont(SWTResourceManager.getFont("Arial", 10, SWT.BOLD));
		
		lblGameOverScore = new Label(compGameOver, SWT.CENTER);
		lblGameOverScore.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblGameOverScore.setText("0");
		lblGameOverScore.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		
		Label lblGameOverNamePrompt = new Label(compGameOver, SWT.CENTER);
		lblGameOverNamePrompt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblGameOverNamePrompt.setText("Please enter your initials: ");
		lblGameOverNamePrompt.setFont(SWTResourceManager.getFont("Arial", 10, SWT.BOLD));
		
		txtGameOverName = new Text(compGameOver, SWT.BORDER | SWT.CENTER);
		txtGameOverName.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		txtGameOverName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (txtGameOverName.getText().length() >= 3) {
					txtGameOverName.setEnabled(false); // disable player name input
					txtGameOverName.redraw();
					
					if (gameOverSelection == -1)
						gameOverSelection = -2;
				}
			}
		});
		txtGameOverName.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		
		compGameOverHighScoreList = new Composite(this, SWT.BORDER);
		compGameOverHighScoreList .setBounds(6, 140, 273, 257);
		compGameOverHighScoreList .setLayout(new GridLayout(1, false));
		compGameOverHighScoreList .setVisible(false);
		
		Label lblHighScoresPrompt = new Label(compGameOverHighScoreList, SWT.CENTER);
		lblHighScoresPrompt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblHighScoresPrompt.setText("Top 10 High Scores:");
		lblHighScoresPrompt.setFont(SWTResourceManager.getFont("Arial", 10, SWT.BOLD));
		
		listHighScores = new List(compGameOverHighScoreList, SWT.BORDER);
		listHighScores.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listHighScores.setBounds(0, 0, 71, 68);
		
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
			if (gameOverSelection >= gameOverDecreaseAmt && !gameOverMoveMade)
				gameOverSelection -= gameOverDecreaseAmt;

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (pbGameOverTimer.isDisposed())
						return;
					pbGameOverTimer.setSelection(gameOverSelection);
					pbGameOverTimer.redraw();

					// game is OVER! you timed out
					if (gameOverSelection == 0) {
						gameOverSelection = -1; // run below once!
						
						lblGameOverScore.setText(lblScore.getText());
						
						compGameOver.setVisible(true);
						compGameOver.layout();
						compGameOver.moveAbove(gamecomp);
						txtGameOverName.setFocus();
						
						txtGameOverName.setText("");
					}
					if (gameOverSelection == -2) {
						gameOverSelection = -3; // run below once!
						try {
							// wait 3/4 of a second before
							// showing highscore list 
							Thread.sleep(750);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						GameDatabase db = new GameDatabase();
						try {
							int score = Integer.parseInt(lblScore.getText());
							db.writeHighScore(txtGameOverName.getText(), score);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						try {
							db.readHighScore();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						if (db.highscores != null && db.highscores.size() > 0)
						{
							// empty listHighScores
							listHighScores.removeAll();
							
							// fill high score list
							String str;
							for (int i = 0; i < db.highscores.size() && i < MAX_HIGHSCORESTOSHOW; i++) {
								str = db.highscores.get(i).getName();
								str += "  ";
								str += db.highscores.get(i).getScore();
								
								listHighScores.add(str);
							}
						}
						
						compGameOver.setVisible(false);
						compGameOverHighScoreList.setVisible(true);
						compGameOverHighScoreList.layout();
						compGameOverHighScoreList.moveAbove(gamecomp);
					}
				}
			});			
		}
	};
}