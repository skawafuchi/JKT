import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

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

		JComponent panel1 = makeTextPanel(
				"<html><div style=\"text-align:center;\">Created By Steven Kawafuchi<br>Music by Eric Skiff - Chibi Ninja<br><br>Created for students studying languages.</div></html>");
		tabbedPane.addTab("About JKT", panel1);

		JComponent panel2 = makeTextPanel("Panel 2");
		tabbedPane.addTab("Troubleshoot", panel2);

		//change to table layout
		JComponent panel3 = makeTextPanel("<html>ctrl+m : Toggle Music<br>"
				+ "shift+backspace : Clear Typing Buffer<br>"
				+ "esc : Stop Game <br>"
				+ "space : Start Game<br>"
				+ "enter : Enter Word</html>");
		tabbedPane.addTab("Hotkeys", panel3);

		JComponent panel4 = makeTextPanel("");
		tabbedPane.addTab("About Settings", panel4);

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
}
