import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AboutWindow extends JFrame {
	AboutPanel aboutPanel = new AboutPanel();
	public AboutWindow() {
		super("About Japanese Kanji Testinator 3000");
		add(aboutPanel);
		BufferedImage icon = null;
		try {
			icon = ImageIO.read(ResourceLoader.load("MyIcon.png"));
		} catch (Exception e) {
		}
		setIconImage(icon);
		setResizable(false);
		pack();
		repaint();
	}

}
