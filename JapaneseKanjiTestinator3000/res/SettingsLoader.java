import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class SettingsLoader {
	FileInputStream settingsStream;
	public HashMap<String,String> settings = new HashMap<String,String>();
	public boolean successfulLoad;
	SettingsLoader(){
		load();
	}
	
	void load(){
		try {
			//for jar export
			//settingsStream = new FileInputStream("settings.JKTSettings");
			
			//for development
			settingsStream = new FileInputStream(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "settings.JKTSettings");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(settingsStream, "UTF-8"));
			StringTokenizer st;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				st = new StringTokenizer(line,":");
				settings.put(st.nextToken(), st.nextToken());
			}
			successfulLoad = true;
		} catch (Exception e){
			successfulLoad = false;
			e.printStackTrace();
		}
	}
	
	void saveSetting(String setting,String value){
		
	}
	
}
