import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class WordDatabase {
	public static ArrayList<JapaneseChar> wordBank = new ArrayList<JapaneseChar>();

	public WordDatabase() {
		wordBank.add(new JapaneseChar("馬鹿", new HashSet<String>() {
			{
				add("baka");
			}
		}));
	}

	public static void add() {
		wordBank.clear();
		JFileChooser fc = new JFileChooser();
		int ret = fc.showOpenDialog(null);

		if (ret == JFileChooser.APPROVE_OPTION) {
			File open = fc.getSelectedFile();

			try {
				FileInputStream fis = new FileInputStream(open);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				StringTokenizer st;
				HashSet<String> romaji;
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					try {
						st = new StringTokenizer(line, " 　");
						String kanji = st.nextToken();
						romaji = new HashSet<String>();
						st = new StringTokenizer(st.nextToken(), ",");
						while (st.hasMoreTokens()) {
							romaji.add(st.nextToken());
						}
						JapaneseChar attempt = new JapaneseChar(kanji, romaji);
						if (!wordBank.contains(attempt)) {
							wordBank.add(attempt);
						}
						
					//blank lines
					} catch (NoSuchElementException e) {
						continue;
					}
				}

				br.close();
				JOptionPane.showMessageDialog(null, "Successfully loaded file: " + open.getName(), "Success!",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error Loading File\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				wordBank.clear();
				wordBank.add(new JapaneseChar("馬鹿", new HashSet<String>() {
					{
						add("baka");
					}
				}));
			}

		}

	}

}
