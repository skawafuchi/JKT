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
	public ArrayList<JapaneseChar> wordBank = new ArrayList<JapaneseChar>();
	File lastDirectory;
	SettingsLoader settingsLoader;

	@SuppressWarnings("serial")
	public WordDatabase(SettingsLoader sl) {
		settingsLoader = sl;
		if (settingsLoader.successfulLoad) {
			lastDirectory = new File(settingsLoader.settings.get("vocabDirectory"));
		}
		wordBank.add(new JapaneseChar("Add Vocab Set", new HashSet<String>() {
			{
				add("error");
			}
		}));
	}

	@SuppressWarnings("serial")
	public void loadWordSet() {
		JFileChooser fc = new JFileChooser();
		if (lastDirectory != null) {
			fc.setCurrentDirectory(lastDirectory);
		}
		int ret = fc.showOpenDialog(null);

		if (ret == JFileChooser.APPROVE_OPTION) {
			lastDirectory = fc.getCurrentDirectory();
			settingsLoader.changeSetting("vocabDirectory", lastDirectory.toString());
			File open = fc.getSelectedFile();
			wordBank.clear();
			try {
				FileInputStream fis = new FileInputStream(open);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				StringTokenizer st;
				HashSet<String> romaji;
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					try {
						// st = new StringTokenizer(line, " ");
						st = new StringTokenizer(line, "：:");
						String kanji = st.nextToken();
						romaji = new HashSet<String>();
						st = new StringTokenizer(st.nextToken(), ",");
						String temp = "";
						while (st.hasMoreTokens()) {
							temp = st.nextToken();
							//remove space if accidentally added to beginning
							if (temp.charAt(0) == ' ') {
								temp = temp.substring(1);
							}
							//remove space at end if not empty string after first space removed
							if (temp.length() > 0 && temp.charAt(temp.length() - 1) == ' ') {
								temp = temp.substring(0, temp.length() - 1);
							}
							
							if (temp.length() > 0) {
								romaji.add(temp);
							}
						}
						JapaneseChar attempt = new JapaneseChar(kanji, romaji);
						if (!wordBank.contains(attempt)) {
							wordBank.add(attempt);
						}

						// blank lines
					} catch (NoSuchElementException e) {
						continue;
					}
				}
				br.close();
				isr.close();
				fis.close();
				JOptionPane.showMessageDialog(null, "Successfully loaded file: " + open.getName(), "Success!",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error Loading File\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				wordBank.clear();
				wordBank.add(new JapaneseChar("No Vocabulary Set Added", new HashSet<String>() {
					{
						add("error");
					}
				}));
			}
		}
	}

}
