import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener {
	final int WINDOW_HEIGHT = 750, WINDOW_WIDTH = 1000, COMBO_IMAGE_START = 100, COMBO_ANIMATION_START = 110;
	String[] dogeComment = new String[] { "wow", "such combo", "many speed", "so nihongo", "very amaze" };
	public ArrayList<JapaneseChar> charsOnScreen;
	ArrayList<Firework> fireworks, fireworksToRemove;
	WordDatabase wordBank;
	boolean displayComment = false, forceQuit = true;
	public int lives, score, combo, highestCombo;
	int shake = -5, dogeWord, dogePos, dogeColor;
	Random random;
	Image doge;
	float opacity;
	public Timer DOGE;
	String missed = "";
	ArrayList<String> missedWords = new ArrayList<String>(lives);
	GameWindow gameWindow;

	Color[] commentColor = new Color[] { Color.BLUE, Color.pink, Color.red, Color.MAGENTA, Color.CYAN, Color.green };
	Point[] commentPos = new Point[] { new Point(50, 50), new Point(400, 100), new Point(400, 300), new Point(50, 500),
			new Point(350, 350) };

	public GamePanel(GameWindow window) {
		gameWindow = window;
		DOGE = new Timer(1000, this);

		try {
			doge = ImageIO.read(ResourceLoader.load("doge.png"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		random = new Random();
		wordBank = new WordDatabase();
		score = 0;
		lives = 3;
		combo = 0;
		opacity = 0f;

		charsOnScreen = new ArrayList<JapaneseChar>();
		fireworks = new ArrayList<Firework>();
		fireworksToRemove = new ArrayList<Firework>();
		this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
	}

	public void update() {

		for (Firework f : fireworks) {
			f.step();
			if (f.alpha < 0.0f) {
				fireworksToRemove.add(f);
			}
		}

		if (!fireworksToRemove.isEmpty()) {
			for (Firework rm : fireworksToRemove) {
				fireworks.remove(rm);
			}
			fireworksToRemove.clear();
		}

		for (int i = 0; i < charsOnScreen.size(); i++) {
			charsOnScreen.get(i).posUpdate();
			if (charsOnScreen.get(i).myYPos > WINDOW_HEIGHT) {
				missed = charsOnScreen.get(i).myKanji + "  " + charsOnScreen.get(i).myRomaji;
				missedWords.add(charsOnScreen.get(i).myKanji + "  " + charsOnScreen.get(i).myRomaji);
				charsOnScreen.remove(i);
				lives--;
				combo = 0;
				opacity = 0f;
				displayComment = false;
				DOGE.stop();
				if (lives == 0) {
					endGame(false);
				}
			}
		}
	}

	public void endGame(boolean forceQuit) {
		this.forceQuit = forceQuit;
		gameWindow.gameOver = true;
		gameWindow.refresh.stop();
		gameWindow.rateOfWordsTimer.stop();
		gameWindow.addWordTimer.stop();
		charsOnScreen.clear();
		fireworks.clear();
		opacity = 0f;
		DOGE.stop();
		displayComment = false;
		missed = "";
		gameWindow.buffer = "";
		this.repaint();
	}

	public void addWord() {
		int index = random.nextInt(WordDatabase.wordBank.size());
		charsOnScreen.add(new JapaneseChar(WordDatabase.wordBank.get(index).myKanji,
				WordDatabase.wordBank.get(index).myRomaji, random.nextInt(WINDOW_WIDTH - 50), random.nextInt(30) + 10,
				gameWindow.difficultySetting + random.nextInt(gameWindow.difficultySetting)));
	}

	public void paintComponent(Graphics g) {

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

		Graphics2D g2d = (Graphics2D) g;
		if (combo > COMBO_IMAGE_START && combo < COMBO_ANIMATION_START) {
			opacity = (combo - COMBO_IMAGE_START) * 0.025f;
		}
		if (combo >= COMBO_ANIMATION_START) {
			shake *= -1;
			if (!DOGE.isRunning()) {
				DOGE.start();
				displayComment = true;
			}
		}
		if (!gameWindow.gameOver) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
			g2d.drawImage(doge, -20 + shake, -20, (int) (opacity * 2000), (int) (opacity * 2000), null);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		}

		g.setColor(commentColor[dogeColor]);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
		if (displayComment) {
			g.drawString(dogeComment[dogeWord], commentPos[dogePos].x, commentPos[dogePos].y);
		}

		g.setColor(Color.white);
		g.setFont(new Font("MS Mincho (Body Asian)", Font.BOLD, 12));
		g.drawString(gameWindow.buffer, (this.getWidth() - (gameWindow.buffer.length() * 4)) / 2, 740);

		// Main Screen
		if (gameWindow.gameOver) {
			g.drawString("Press Space to Start!", this.getWidth() / 2 - 60, this.getHeight() / 2);
			if (!forceQuit) {
				g.drawString("Game Over!", this.getWidth() / 2 - 60, (this.getHeight() + 30) / 2);
				g.drawString("Score: " + score, this.getWidth() / 2 - 60, (this.getHeight() + 60) / 2);
				g.drawString("Highest Combo: " + highestCombo, this.getWidth() / 2 - 60, (this.getHeight() + 90) / 2);
				g.drawString("Words Missed:", this.getWidth() / 2 - 60, (this.getHeight() + 120) / 2);

				for (int i = 0; i < missedWords.size(); i++) {
					g.drawString(missedWords.get(i), this.getWidth() / 2 - 60, (this.getHeight() + 150 + (30 * i)) / 2);
				}

			}
		}

		if (!gameWindow.gameOver) {
			g.drawString("Combo: " + combo, 10, WINDOW_HEIGHT - 25);
			g.drawString("Score: " + score, 10, WINDOW_HEIGHT - 10);
			g.drawString("Lives: " + lives, WINDOW_WIDTH - 50, WINDOW_HEIGHT - 10);
			if (missed.length() != 0) {
				g.drawString("Just Missed: " + missed, this.getWidth() / 2 - 60, (this.getHeight() - 30));
			}
		}

		g.setFont(new Font("MS Mincho (Body Asian)", Font.BOLD, gameWindow.fontSize));
		for (int i = 0; i < charsOnScreen.size(); i++) {
			g.drawString(charsOnScreen.get(i).myKanji, charsOnScreen.get(i).myXPos, charsOnScreen.get(i).myYPos);
		}

		for (Firework f : fireworks) {
			f.draw(g);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == DOGE) {
			int lastPos = dogePos;
			int lastWord = dogeWord;
			int lastColor = dogeColor;
			while (lastWord == dogeWord) {
				dogeWord = random.nextInt(dogeComment.length);
			}
			while (lastPos == dogePos) {
				dogePos = random.nextInt(commentPos.length);
			}
			while (lastColor == dogeColor) {
				dogeColor = random.nextInt(commentColor.length);
			}
		}

	}

}
