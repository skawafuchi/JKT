package javaFiles;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class AboutWindow extends JFrame {
	final int WINDOW_HEIGHT = 415, WINDOW_WIDTH = 500;

	public AboutWindow() {
		super("About Japanese Kanji Testinator 3000");
		this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		BufferedImage icon = null;
		try {
			icon = ImageIO.read(ResourceLoader.load("MyIcon.png"));
		} catch (Exception e) {
		}
		setIconImage(icon);
		setResizable(false);

		// TabbedPane with the help of
		// http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabbedPaneDemoProject/src/components/TabbedPaneDemo.java
		JTabbedPane tabbedPane = new JTabbedPane();

		JComponent aboutPanel = makeTextPanel(
				"<html><div style=\"text-align:center;\"><h1>Created By Steven Kawafuchi<br>Music by Eric Skiff - Chibi Ninja</h1><br><br>Created for students studying Japanese.<br>Developed in 2015</div></html>");
		tabbedPane.addTab("About JKT", aboutPanel);

		// change to table layout
		HashMap<String,String> hotkeys = new HashMap<String,String>();
		hotkeys.put("shift+backspace", "Clear Typing Buffer");
		hotkeys.put("esc","Stop Game");
		hotkeys.put("space","Start Game");
		hotkeys.put("enter", "Enter Word");
		hotkeys.put("ctrl+m","Toggle Music");
		JComponent hotkeyPanel = makeHotkeyPanel(hotkeys);
		tabbedPane.addTab("Hotkeys", hotkeyPanel);

		JComponent settingsPanel = makeTextPanel(
				"<html><div style=\"margin:0px 30px 0px 30px;\">The settings.JKTSettings file needs to be in the same directory as this executable or it won't load properly. Settings are saved to the settings file every time the executable is closed.<br><br> You can check if the settings were loaded"
						+ " correctly or not by clicking on the Settings tab and looking at the bottom.</div></html>");
		tabbedPane.addTab("About Settings", settingsPanel);

		JComponent vocabPanel = makeTextPanel(
				"<html><div style=\"margin:0px 30px 0px 30px;\"><h3>Encoding</h3>Files should be encoded with UTF-8.<h3>Format</h3>"
			+ "The basic format for adding a word to the vocab file is word:answer,answer,answer where word is the vocabulary word that will appear on the screen and answer is what you will type in the game! For example: "
			+ "<br>一：ichi,hito<br>万:man</div></html>");
		tabbedPane.addTab("About Making Vocabulary Set Files", vocabPanel);

		add(tabbedPane);
		pack();
		repaint();
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}
	protected JComponent makeHotkeyPanel(HashMap<String,String> text) {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(text.size()+1,2));
		JLabel title = new JLabel("<html><h1>Hotkey</h1></html>");
		title.setHorizontalAlignment(JLabel.CENTER);
		panel.add(title);
		panel.add(new JLabel("<html><h1>Action</h1></html>"));
		for (String key: text.keySet()){
			JLabel keyLabel = new JLabel(key);
			keyLabel.setHorizontalAlignment(JLabel.CENTER);
			panel.add(keyLabel);
			JLabel valueLabel = new JLabel(text.get(key));
			valueLabel.setHorizontalAlignment(JLabel.LEFT);
			panel.add(valueLabel);
		}
		return panel;
	}
}
