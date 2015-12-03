package javaFiles;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class SettingsLoader {

	HashMap<String, String> settings = new HashMap<String, String>();
	public boolean successfulLoad;

	SettingsLoader() {
		load();
	}

	String getSetting(String settingToGet) throws Exception{
		String toReturn = settings.get(settingToGet);
		if (toReturn == null){
			throw new Exception("There was no setting " + settingToGet);
		}

		return toReturn;
	}
	
	
	void load() {
		try {
			// for jar export
//			FileInputStream settingsStream = new FileInputStream("settings.JKTSettings");

			// for development
			 FileInputStream settingsStream = new FileInputStream(
			 this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
			 + "settings.JKTSettings");

			BufferedReader br = new BufferedReader(new InputStreamReader(settingsStream, "UTF-8"));
			StringTokenizer st;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				st = new StringTokenizer(line, ":");
				String key = st.nextToken(), value = st.nextToken();
				if (st.countTokens() > 0) {
					value += ":" + st.nextToken();
				}
				settings.put(key, value);
			}
			successfulLoad = true;
			br.close();
			settingsStream.close();
		} catch (Exception e) {
			successfulLoad = false;
			e.printStackTrace();
		}
	}

	void changeSetting(String setting, String value) {
		settings.put(setting, value);
	}

	void saveSettings() {
		if (successfulLoad) {
			try {
//				FileWriter fileWriter = new FileWriter("settings.JKTSettings");

				 FileWriter fileWriter = new FileWriter(
				 this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
				 + "settings.JKTSettings");

				for (String key : settings.keySet()) {
					fileWriter.write(key + ":" + settings.get(key));
					fileWriter.write(System.getProperty("line.separator"));
				}
				fileWriter.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
