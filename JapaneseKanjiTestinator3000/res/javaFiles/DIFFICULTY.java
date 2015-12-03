package javaFiles;

public enum DIFFICULTY {
	EASY(1), MEDIUM(2), HARD(3);
	private int value;

	private DIFFICULTY(int val) {
		this.value = val;
	}
	
	public int getValue(){
		return value;
	}
};