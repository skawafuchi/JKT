import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AboutPanel extends JPanel {
	final int WINDOW_HEIGHT = 415, WINDOW_WIDTH = 500;
	int fontSize = 25;

	public AboutPanel() {
		this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setBackground(Color.BLACK); 
		JLabel credits = new JLabel("<html><body style='width: " + (WINDOW_WIDTH  - fontSize*5) + "px;color:white'> Created By Steven Kawafuchi "
				+ "Music by Eric Skiff - Chibi Ninja</body> </html>");
		credits.setFont(new Font("Verdana", 1, fontSize));
		add(credits);
		JLabel directions = new JLabel("<html><body style='width: " + (WINDOW_WIDTH  - 15*9) + "px;color:white'/>Directions: Create a .txt file with the appropriate kanji and "
				+ "romaji with a space separating them. <br> Save the text file with encoding UTF-8. Put the pair on their own line. For example: "
				+ "<br>馬鹿 baka<br>阿呆 ahou<br>A couple of example .txt files have been provided for you."
				+ "Once you start the game just type the words as you have typed them in romaji in your .txt file and press enter.<br><br> "
				+ "Hotkeys: <br>"
				+ "(+) Increase Music Volume <br>"
				+ "(-) Decrease Music Volume <br>"
				+ "(ctrl+m) Toggle Music On or Off<br>"
				+ "(esc) Stop Game"
				+ "</html>");
		directions.setFont(new Font("Verdana", 1, 15));
		add(directions);
		
		setVisible(true);
	}

}
