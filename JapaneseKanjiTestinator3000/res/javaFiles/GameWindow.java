package javaFiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GameWindow extends JFrame implements ActionListener, KeyListener, MouseListener {
	boolean gameOver;
	String buffer;
	JMenuBar menuBar;
	GamePanel gPanel;
	JMenuItem loadWordSet, volumeInc, volumeDec, play, about;
	JLabel musicLabel, sfxLabel, masterVolumeLabel, fontSizeLabel, settingsAutoLoadedLabel, difficultyLabel,
			gameModeLabel;
	JRadioButtonMenuItem easy, medium, hard, summer, winter;
	JSlider musicVolumeSlider, masterVolumeSlider, sfxVolumeSlider, fontSizeSlider;
	Timer refresh, rateOfWordsTimer, addWordTimer;
	int fontSize;
	Clip music, incorrectSFX, correctSFX;
	Float musicVolLvl = 1.0f, SFXVolLvl = 1.0f, masterVolLvl = 1.0f;
	FloatControl musicGainControl, correctSFXGainControl, incorrectSFXGainControl;
	AboutWindow aboutWindow = new AboutWindow();
	int increment;
	SettingsLoader settingsLoader = new SettingsLoader();

	// Summer = falling words / matsuri / hanabi
	// Winter = single word / Mikan?

	GAME_MODE gameMode = GAME_MODE.SUMMER;
	DIFFICULTY difficultySetting = DIFFICULTY.EASY;

	public GameWindow() {

		super("Japanese Kanji Testinator 3000");

		try {
			music = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("test.wav"));
			music.open(inputStream);
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
			incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));

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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				settingsLoader.saveSettings();
				setVisible(false);
				dispose();
				System.exit(0);
			}

			@Override
			public void windowActivated(WindowEvent arg0) {

			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {

			}

			@Override
			public void windowIconified(WindowEvent arg0) {

			}

			@Override
			public void windowOpened(WindowEvent arg0) {

			}
		});

		menuBar = new JMenuBar();
		JMenu file, settings, help;

		// File Area
		file = new JMenu("File");
		loadWordSet = new JMenuItem("Load Vocabulary Set");
		loadWordSet.addActionListener(this);
		file.add(loadWordSet);
		menuBar.add(file);
		// End File Area

		// Settings Area
		settings = new JMenu("Settings");
		fontSizeLabel = new JLabel("Font Size");
		settings.add(fontSizeLabel);
		fontSizeSlider = new JSlider(1, 50, 30);
		fontSizeSlider.addMouseListener(this);
		settings.add(fontSizeSlider);
		settings.addSeparator();

		masterVolumeLabel = new JLabel("Master Volume");
		settings.add(masterVolumeLabel);
		masterVolumeSlider = new JSlider(0, 100, 100);
		masterVolumeSlider.addMouseListener(this);
		settings.add(masterVolumeSlider);

		sfxLabel = new JLabel("Sound Effects Volume");
		settings.add(sfxLabel);
		sfxVolumeSlider = new JSlider(0, 100, 100);
		sfxVolumeSlider.addMouseListener(this);
		settings.add(sfxVolumeSlider);

		musicLabel = new JLabel("Music Volume");
		settings.add(musicLabel);
		musicVolumeSlider = new JSlider(0, 100, 100);
		musicVolumeSlider.addMouseListener(this);
		settings.add(musicVolumeSlider);

		settings.addSeparator();

		ButtonGroup difficultySettingGroup = new ButtonGroup();
		difficultyLabel = new JLabel("Difficulty");
		settings.add(difficultyLabel);
		easy = new JRadioButtonMenuItem("Easy");
		easy.setSelected(true);
		difficultySettingGroup.add(easy);
		easy.addActionListener(this);
		settings.add(easy);

		medium = new JRadioButtonMenuItem("Medium");
		difficultySettingGroup.add(medium);
		medium.addActionListener(this);
		settings.add(medium);

		hard = new JRadioButtonMenuItem("Hard");
		difficultySettingGroup.add(hard);
		hard.addActionListener(this);
		settings.add(hard);
		settings.addSeparator();

		// start game mode
		ButtonGroup gameModeSettingsGroup = new ButtonGroup();
		gameModeLabel = new JLabel("Game Mode");
		settings.add(gameModeLabel);
		summer = new JRadioButtonMenuItem("Summer");
		summer.setSelected(true);
		gameModeSettingsGroup.add(summer);
		summer.addActionListener(this);
		settings.add(summer);

		winter = new JRadioButtonMenuItem("Winter");
		gameModeSettingsGroup.add(winter);
		winter.addActionListener(this);
		settings.add(winter);
		settings.addSeparator();
		// end game mode

		settingsAutoLoadedLabel = settingsLoader.successfulLoad ? new JLabel("Loaded Settings")
				: new JLabel("Settings Load Failed");
		settingsAutoLoadedLabel.setOpaque(true);
		settingsAutoLoadedLabel
				.setBackground(settingsLoader.successfulLoad ? new Color(152, 251, 152) : new Color(240, 128, 128));
		settings.add(settingsAutoLoadedLabel);

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

		try {
			if (settingsLoader.successfulLoad) {
				fontSize = Integer.parseInt(settingsLoader.getSetting("fontSize"));
				fontSizeSlider.setValue(fontSize);

				masterVolLvl = Float.parseFloat(settingsLoader.getSetting("masterVolume"));
				musicVolLvl = Float.parseFloat(settingsLoader.getSetting("musicVolume"));
				SFXVolLvl = Float.parseFloat(settingsLoader.getSetting("SFXVolume"));

				masterVolumeSlider.setValue((int) (masterVolLvl * 100));
				musicVolumeSlider.setValue((int) (musicVolLvl * 100));
				sfxVolumeSlider.setValue((int) (SFXVolLvl * 100));

				musicVolLvl = masterVolumeSlider.getValue() * 0.0001f * musicVolumeSlider.getValue();
				SFXVolLvl = masterVolumeSlider.getValue() * 0.0001f * sfxVolumeSlider.getValue();
				musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));
				correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
				incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + incorrectSFXGainControl.getMaximum()) - 50.0f));

				fontSizeSlider.setValue(fontSize);
				if (settingsLoader.getSetting("musicToggled").equals("On")) {
					music.loop(Clip.LOOP_CONTINUOUSLY);
				}
				switch (settingsLoader.getSetting("difficulty")) {
				case "easy":
					easy.setSelected(true);
					difficultySetting = DIFFICULTY.EASY;
					break;
				case "medium":
					medium.setSelected(true);
					difficultySetting = DIFFICULTY.MEDIUM;
					break;
				case "hard":
					hard.setSelected(true);
					difficultySetting = DIFFICULTY.HARD;
					break;
				}

				switch (settingsLoader.getSetting("gameMode")) {
				case "summer":
					summer.setSelected(true);
					gameMode = GAME_MODE.SUMMER;
					break;
				case "winter":
					winter.setSelected(true);
					gameMode = GAME_MODE.WINTER;
					break;
				}

			} else {
				throw new Exception("SETTINGS DIDN'T LOAD YO");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			settingsAutoLoadedLabel.setText("Settings Load Failed");
			settingsAutoLoadedLabel.setBackground(new Color(240, 128, 128));
			music.loop(Clip.LOOP_CONTINUOUSLY);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == refresh) {
			gPanel.update();
			gPanel.repaint();
		} else if (event == rateOfWordsTimer) {
			increment++;
			if (rateOfWordsTimer.getDelay() - 50 > 0 && increment == 5) {
				rateOfWordsTimer.setDelay(rateOfWordsTimer.getDelay() - 50);
				increment = 0;
			}
		} else if (event == addWordTimer) {
			gPanel.addWord();
		} else if (event == loadWordSet) {
			gPanel.wordDatabase.loadWordSet();
		} else if (event == easy) {
			difficultySetting = DIFFICULTY.EASY;
			settingsLoader.changeSetting("difficulty", "easy");
		} else if (event == medium) {
			difficultySetting = DIFFICULTY.MEDIUM;
			settingsLoader.changeSetting("difficulty", "medium");
		} else if (event == hard) {
			difficultySetting = DIFFICULTY.HARD;
			settingsLoader.changeSetting("difficulty", "hard");
		} else if (event == about) {
			aboutWindow.setVisible(true);
			aboutWindow.setLocation(this.getLocation());
		} else if (event == summer) {
			gameMode = GAME_MODE.SUMMER;
			settingsLoader.changeSetting("gameMode", "summer");
		} else if (event == winter) {
			gameMode = GAME_MODE.WINTER;
			settingsLoader.changeSetting("gameMode", "winter");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_M && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
			if (music.isRunning()) {
				music.stop();
				settingsLoader.changeSetting("musicToggled", "Off");
			} else {
				music.loop(Clip.LOOP_CONTINUOUSLY);
				settingsLoader.changeSetting("musicToggled", "On");
			}
		}
		if (!gameOver) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				buffer += "a";
				break;
			case KeyEvent.VK_B:
				buffer += "b";
				break;
			case KeyEvent.VK_C:
				buffer += "c";
				break;
			case KeyEvent.VK_D:
				buffer += "d";
				break;
			case KeyEvent.VK_E:
				buffer += "e";
				break;
			case KeyEvent.VK_F:
				buffer += "f";
				break;
			case KeyEvent.VK_G:
				buffer += "g";
				break;
			case KeyEvent.VK_H:
				buffer += "h";
				break;
			case KeyEvent.VK_I:
				buffer += "i";
				break;
			case KeyEvent.VK_J:
				buffer += "j";
				break;
			case KeyEvent.VK_K:
				buffer += "k";
				break;
			case KeyEvent.VK_L:
				buffer += "l";
				break;
			case KeyEvent.VK_M:
				if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0) {
					buffer += "m";
				}
				break;
			case KeyEvent.VK_N:
				buffer += "n";
				break;
			case KeyEvent.VK_O:
				buffer += "o";
				break;
			case KeyEvent.VK_P:
				buffer += "p";
				break;
			case KeyEvent.VK_Q:
				buffer += "q";
				break;
			case KeyEvent.VK_R:
				buffer += "r";
				break;
			case KeyEvent.VK_S:
				buffer += "s";
				break;
			case KeyEvent.VK_T:
				buffer += "t";
				break;
			case KeyEvent.VK_U:
				buffer += "u";
				break;
			case KeyEvent.VK_V:
				buffer += "v";
				break;
			case KeyEvent.VK_W:
				buffer += "w";
				break;
			case KeyEvent.VK_X:
				buffer += "x";
				break;
			case KeyEvent.VK_Y:
				buffer += "y";
				break;
			case KeyEvent.VK_Z:
				buffer += "z";
				break;
			case KeyEvent.VK_QUOTE:
				buffer += "'";
				break;
			case KeyEvent.VK_SPACE:
				buffer += " ";
				break;
			case KeyEvent.VK_ENTER:
				for (int i = 0; i < gPanel.charsOnScreen.size(); i++) {
					if (gPanel.charsOnScreen.get(i).myRomaji.contains(buffer)) {
						gPanel.fireworks.add(new Firework(
								gPanel.charsOnScreen.get(i).myXPos
										+ ((gPanel.charsOnScreen.get(i).myKanji.length() * fontSize) / 2),
								gPanel.charsOnScreen.get(i).myYPos, 500));
						gPanel.charsOnScreen.remove(i);
						gPanel.score += 1 + 1 * gPanel.combo;
						gPanel.combo++;
						if (correctSFX.isRunning()) {
							correctSFX.stop();
						}
						correctSFX.setFramePosition(0);
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
						if (incorrectSFX.isRunning()) {
							incorrectSFX.stop();
						}
						incorrectSFX.setFramePosition(0);
						incorrectSFX.start();
					}
				}
				buffer = "";
				break;
			case KeyEvent.VK_BACK_SPACE:
				if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
					buffer = "";
				}
				if (buffer.length() >= 1) {
					buffer = buffer.substring(0, buffer.length() - 1);
				}
				break;
			case KeyEvent.VK_ESCAPE:
				gPanel.endGame(true);
				break;
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Start game
		if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
			for (int i = 0; i < difficultySetting.getValue(); i++) {
				gPanel.addWord();
			}
			gPanel.lives = gPanel.MAX_LIVES;
			gPanel.score = 0;
			gPanel.combo = 0;
			gPanel.highestCombo = 0;
			gPanel.missedWords.clear();
			gameOver = false;
			refresh = new Timer(40, this);
			refresh.start();
			rateOfWordsTimer = new Timer(10000 - difficultySetting.getValue() * 1000, this);
			rateOfWordsTimer.start();
			addWordTimer = new Timer(7500 - (difficultySetting.getValue() * 2000), this);
			addWordTimer.start();
			increment = 0;
			this.pack();
			this.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == masterVolumeSlider) {
			SFXVolLvl = masterVolumeSlider.getValue() * 0.0001f * sfxVolumeSlider.getValue();
			musicVolLvl = masterVolumeSlider.getValue() * 0.0001f * musicVolumeSlider.getValue();
			correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));
			settingsLoader.changeSetting("masterVolume", Float.toString((masterVolumeSlider.getValue() / 100.0f)));
		} else if (e.getSource() == sfxVolumeSlider) {
			SFXVolLvl = sfxVolumeSlider.getValue() * 0.0001f * masterVolumeSlider.getValue();
			correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			settingsLoader.changeSetting("SFXVolume", Float.toString((sfxVolumeSlider.getValue() / 100.0f)));
		} else if (e.getSource() == musicVolumeSlider) {
			musicVolLvl = musicVolumeSlider.getValue() * 0.0001f * masterVolumeSlider.getValue();
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));
			settingsLoader.changeSetting("musicVolume", Float.toString((musicVolumeSlider.getValue() / 100.0f)));
		} else if (e.getSource() == fontSizeSlider) {
			fontSize = fontSizeSlider.getValue();
			settingsLoader.changeSetting("fontSize", Integer.toString(fontSizeSlider.getValue()));
		}

	}

}
