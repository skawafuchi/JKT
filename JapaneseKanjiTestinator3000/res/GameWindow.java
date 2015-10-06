import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class GameWindow extends JFrame implements ActionListener, KeyListener, MouseListener {

	public boolean gameOver;
	public String buffer;
	public JMenuBar menuBar;
	GamePanel gPanel;
	JMenuItem fontSize, loadWordSet, volumeInc, volumeDec, play, about;
	JLabel musicLabel, masterVolumeLabel;
	public JRadioButtonMenuItem easy, medium, hard;
	JSlider musicVolumeSlider, masterVolumeSlider;
	public Timer refresh, rateOfWords;
	public int fSize;
	public int dSetting = 1;
	Clip music, incorrectSFX, correctSFX;
	Float musicVolLvl = 0.50f, SFXVolLvl = 1.0f;
	FloatControl musicGainControl, correctSFXGainControl, incorrectSFXGainControl;
	AboutWindow aboutWindow = new AboutWindow();

	int increment;

	public GameWindow() {

		super("Japanese Kanji Testinator 3000");

		try {
			music = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("test.wav"));
			music.open(inputStream);
			music.loop(Clip.LOOP_CONTINUOUSLY);
			musicGainControl = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));

			inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("correct.wav"));
			correctSFX = AudioSystem.getClip();
			correctSFX.open(inputStream);
			correctSFXGainControl = (FloatControl) correctSFX.getControl(FloatControl.Type.MASTER_GAIN);
			correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));

			inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("incorrect.wav"));
			incorrectSFX = AudioSystem.getClip();
			incorrectSFX.open(inputStream);
			incorrectSFXGainControl = (FloatControl) incorrectSFX.getControl(FloatControl.Type.MASTER_GAIN);
			incorrectSFXGainControl.setValue(SFXVolLvl);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		BufferedImage icon = null;
		try {
			icon = ImageIO.read(ResourceLoader.load("MyIcon.png"));
		} catch (Exception e) {
		}

		setIconImage(icon);
		setResizable(false);
		buffer = "";
		gameOver = true;
		increment = 0;
		fSize = 30;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();
		JMenu file, settings, help;

		// File Area
		file = new JMenu("File");
		loadWordSet = new JMenuItem("Load Word Set");
		loadWordSet.addActionListener(this);
		file.add(loadWordSet);
		menuBar.add(file);
		// End File Area

		// Settings Area
		settings = new JMenu("Settings");
		fontSize = new JMenuItem("Font Size");
		fontSize.addActionListener(this);
		settings.add(fontSize);
		settings.addSeparator();

		masterVolumeLabel = new JLabel("Master Volume");
		settings.add(masterVolumeLabel);
		masterVolumeSlider = new JSlider(0,100,100);
		masterVolumeSlider.addMouseListener(this);
		settings.add(masterVolumeSlider);

		musicLabel = new JLabel("Music Volume");
		settings.add(musicLabel);
		musicVolumeSlider = new JSlider(0,100,50);
		musicVolumeSlider.addMouseListener(this);
		settings.add(musicVolumeSlider);

		play = new JMenuItem("Play/Stop");
		play.addActionListener(this);
		play.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		settings.add(play);
		settings.addSeparator();

		ButtonGroup dSettingGroup = new ButtonGroup();
		easy = new JRadioButtonMenuItem("Easy");
		easy.setSelected(true);
		dSettingGroup.add(easy);
		easy.addActionListener(this);
		settings.add(easy);

		medium = new JRadioButtonMenuItem("Medium");
		dSettingGroup.add(medium);
		medium.addActionListener(this);
		settings.add(medium);

		hard = new JRadioButtonMenuItem("Hard");
		dSettingGroup.add(hard);
		hard.addActionListener(this);
		settings.add(hard);

		menuBar.add(settings);
		// End Settings Area

		// Help Area
		help = new JMenu("Help");
		about = new JMenuItem("About");
		about.addActionListener(this);
		help.add(about);
		menuBar.add(help);
		// End Help Area

		setJMenuBar(menuBar);
		gPanel = new GamePanel(this);
		this.setLayout(new BorderLayout());
		add(gPanel, BorderLayout.CENTER);

		pack();
		repaint();
		this.addKeyListener(this);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == fontSize) {

			try {
				String newFontSize = JOptionPane.showInputDialog(null, "Enter Font Size\nCurrently set to " + fSize,
						"Settings", JOptionPane.QUESTION_MESSAGE);

				// makes sure that the inputed value is a decimal number
				if (newFontSize != null) {
					int test = Integer.parseInt(newFontSize);
					if (test > 0 && test < 50) {
						fSize = test;
					} else {
						JOptionPane.showMessageDialog(null, "Not a valid input", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Not a valid input", "Error", JOptionPane.ERROR_MESSAGE);

			}

		} else if (event == refresh) {
			gPanel.update();
			gPanel.repaint();
			resetSound();
		} else if (event == rateOfWords) {
			gPanel.addWord();
			increment++;
			if (rateOfWords.getDelay() - 50 > 0 && increment == 5) {
				rateOfWords.setDelay(rateOfWords.getDelay() - 50);
				increment = 0;
			}
		} else if (event == loadWordSet) {
			WordDatabase.add();
		} else if (event == easy) {
			dSetting = 1;
		} else if (event == medium) {
			dSetting = 2;
		} else if (event == hard) {
			dSetting = 3;
		} else if (event == play) {
			if (music.isRunning()) {
				music.stop();
			} else {
				music.loop(Clip.LOOP_CONTINUOUSLY);
			}
		} else if (event == about) {
			aboutWindow.setVisible(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!gameOver) {
			if (e.getKeyCode() == KeyEvent.VK_A) {
				buffer += "a";
			} else if (e.getKeyCode() == KeyEvent.VK_B) {
				buffer += "b";
			} else if (e.getKeyCode() == KeyEvent.VK_C) {
				buffer += "c";
			} else if (e.getKeyCode() == KeyEvent.VK_D) {
				buffer += "d";
			} else if (e.getKeyCode() == KeyEvent.VK_E) {
				buffer += "e";
			} else if (e.getKeyCode() == KeyEvent.VK_F) {
				buffer += "f";
			} else if (e.getKeyCode() == KeyEvent.VK_G) {
				buffer += "g";
			} else if (e.getKeyCode() == KeyEvent.VK_H) {
				buffer += "h";
			} else if (e.getKeyCode() == KeyEvent.VK_I) {
				buffer += "i";
			} else if (e.getKeyCode() == KeyEvent.VK_J) {
				buffer += "j";
			} else if (e.getKeyCode() == KeyEvent.VK_K) {
				buffer += "k";
			} else if (e.getKeyCode() == KeyEvent.VK_L) {
				buffer += "l";
			} else if (e.getKeyCode() == KeyEvent.VK_M && !(e.getModifiers() == InputEvent.CTRL_MASK)) {
				buffer += "m";
			} else if (e.getKeyCode() == KeyEvent.VK_N) {
				buffer += "n";
			} else if (e.getKeyCode() == KeyEvent.VK_O) {
				buffer += "o";
			} else if (e.getKeyCode() == KeyEvent.VK_P) {
				buffer += "p";
			} else if (e.getKeyCode() == KeyEvent.VK_Q) {
				buffer += "q";
			} else if (e.getKeyCode() == KeyEvent.VK_R) {
				buffer += "r";
			} else if (e.getKeyCode() == KeyEvent.VK_S) {
				buffer += "s";
			} else if (e.getKeyCode() == KeyEvent.VK_T) {
				buffer += "t";
			} else if (e.getKeyCode() == KeyEvent.VK_U) {
				buffer += "u";
			} else if (e.getKeyCode() == KeyEvent.VK_V) {
				buffer += "v";
			} else if (e.getKeyCode() == KeyEvent.VK_W) {
				buffer += "w";
			} else if (e.getKeyCode() == KeyEvent.VK_X) {
				buffer += "x";
			} else if (e.getKeyCode() == KeyEvent.VK_Y) {
				buffer += "y";
			} else if (e.getKeyCode() == KeyEvent.VK_Z) {
				buffer += "z";
			} else if (e.getKeyCode() == KeyEvent.VK_QUOTE) {
				buffer += "'";
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				buffer += " ";
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				for (int i = 0; i < gPanel.charsOnScreen.size(); i++) {
					// if (buffer.equals(gPanel.charsOnScreen.get(i).myRomaji))
					// {
					if (gPanel.charsOnScreen.get(i).myRomaji.contains(buffer)) {
						gPanel.charsOnScreen.remove(i);
						gPanel.score += 1 + 1 * gPanel.combo;
						gPanel.combo++;
						if (correctSFX.isActive()) {
							correctSFX.setFramePosition(0);
							correctSFX.stop();
						}
						correctSFX.start();
						if (gPanel.combo > gPanel.highestCombo) {
							gPanel.highestCombo++;
						}
						break;
					}
					if (i == gPanel.charsOnScreen.size() - 1) {
						gPanel.combo = 0;
						gPanel.DOGE.stop();
						gPanel.displayComment = false;
						if (incorrectSFX.isActive()) {
							incorrectSFX.setFramePosition(0);
							incorrectSFX.stop();
						}
						incorrectSFX.start();
					}
				}
				buffer = "";
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (buffer.length() >= 1) {
					buffer = buffer.substring(0, buffer.length() - 1);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && e.isShiftDown()) {
				buffer = "";
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gPanel.endGame(true);
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Start game
		if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
			gPanel.lives = 3;
			gPanel.score = 0;
			gPanel.combo = 0;
			gPanel.highestCombo = 0;
			gPanel.missedWords.clear();
			gameOver = false;
			refresh = new Timer(40, this);
			refresh.start();
			rateOfWords = new Timer(4500 - dSetting * 1000, this);
			rateOfWords.start();
			increment = 0;
			for (int i = 0; i < 3; i++) {
				gPanel.addWord();
			}
			this.pack();
			this.repaint();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private void resetSound() {
		if (!correctSFX.isActive()) {
			correctSFX.setFramePosition(0);
		}
		if (!incorrectSFX.isActive()) {
			incorrectSFX.setFramePosition(0);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == masterVolumeSlider) {
			SFXVolLvl = ((JSlider) e.getSource()).getValue() * 0.01f;
			musicVolLvl = ((JSlider) e.getSource()).getValue() * 0.0001f * musicVolumeSlider.getValue();
			correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));

		} else if (e.getSource() == musicVolumeSlider) {
			musicVolLvl = ((JSlider) e.getSource()).getValue() * 0.0001f * masterVolumeSlider.getValue();
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));
		}

	}

}
