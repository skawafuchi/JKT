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
	public boolean gameOver;
	public String buffer;
	public JMenuBar menuBar;
	GamePanel gPanel;
	JMenuItem loadWordSet, volumeInc, volumeDec, play, about;
	JLabel musicLabel, sfxLabel, masterVolumeLabel, fontSizeLabel, settingsAutoLoadedLabel, difficultyLabel;
	public JRadioButtonMenuItem easy, medium, hard;
	JSlider musicVolumeSlider, masterVolumeSlider, sfxVolumeSlider, fontSizeSlider;
	public Timer refresh, rateOfWordsTimer, addWordTimer;
	public int fontSize;
	public int difficultySetting = 1;
	Clip music, incorrectSFX, correctSFX;
	Float musicVolLvl = 1.0f, SFXVolLvl = 1.0f, masterVolLvl = 1.0f;
	FloatControl musicGainControl, correctSFXGainControl, incorrectSFXGainControl;
	AboutWindow aboutWindow = new AboutWindow();
	int increment;
	SettingsLoader settingsLoader = new SettingsLoader();

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
				fontSize = Integer.parseInt(settingsLoader.settings.get("fontSize"));
				fontSizeSlider.setValue(fontSize);
				
				masterVolLvl = Float.parseFloat(settingsLoader.settings.get("masterVolume"));
				musicVolLvl = Float.parseFloat(settingsLoader.settings.get("musicVolume"));				
				SFXVolLvl = Float.parseFloat(settingsLoader.settings.get("SFXVolume"));
				
				masterVolumeSlider.setValue((int) (masterVolLvl * 100));
				musicVolumeSlider.setValue((int) (musicVolLvl * 100));
				sfxVolumeSlider.setValue((int) (SFXVolLvl * 100));

				
				musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));
				correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
				incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + incorrectSFXGainControl.getMaximum()) - 50.0f));
				
				fontSizeSlider.setValue(fontSize);
				if (settingsLoader.settings.get("musicToggled").equals("On")){
					music.loop(Clip.LOOP_CONTINUOUSLY);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fontSize = 30;
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
			WordDatabase.add();
		} else if (event == easy) {
			difficultySetting = 1;
		} else if (event == medium) {
			difficultySetting = 2;
		} else if (event == hard) {
			difficultySetting = 3;
		} else if (event == about) {
			aboutWindow.setVisible(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_M && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
			if (music.isRunning()) {
				music.stop();
			} else {
				music.loop(Clip.LOOP_CONTINUOUSLY);
			}
		}
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
			} else if (e.getKeyCode() == KeyEvent.VK_M && ((e.getModifiers() & InputEvent.CTRL_MASK) == 0)) {
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
					if (gPanel.charsOnScreen.get(i).myRomaji.contains(buffer)) {
						gPanel.fireworks.add(new Firework(gPanel.charsOnScreen.get(i).myXPos,
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
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0)) {
				buffer = "";
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (buffer.length() >= 1) {
					buffer = buffer.substring(0, buffer.length() - 1);
				}

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
			rateOfWordsTimer = new Timer(10000 - difficultySetting * 1000, this);
			rateOfWordsTimer.start();
			addWordTimer = new Timer(1500, this);
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
			SFXVolLvl = ((JSlider) e.getSource()).getValue() * 0.0001f * sfxVolumeSlider.getValue();
			musicVolLvl = ((JSlider) e.getSource()).getValue() * 0.0001f * musicVolumeSlider.getValue();
			correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));

		} else if (e.getSource() == sfxVolumeSlider) {
			SFXVolLvl = ((JSlider) e.getSource()).getValue() * 0.0001f * masterVolumeSlider.getValue();
			correctSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));
			incorrectSFXGainControl.setValue((SFXVolLvl * (50.0f + correctSFXGainControl.getMaximum()) - 50.0f));

		} else if (e.getSource() == musicVolumeSlider) {
			musicVolLvl = ((JSlider) e.getSource()).getValue() * 0.0001f * masterVolumeSlider.getValue();
			musicGainControl.setValue((musicVolLvl * (50.0f + musicGainControl.getMaximum()) - 50.0f));

		} else if (e.getSource() == fontSizeSlider) {
			fontSize = ((JSlider) e.getSource()).getValue();
		}

	}

}
