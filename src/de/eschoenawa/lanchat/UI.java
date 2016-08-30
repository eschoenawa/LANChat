package de.eschoenawa.lanchat;

public interface UI {

	public void println(String... line);

	public void addValue(String value, String ip);

	public void discover();

	public void receive(String received);
	
	public void showUI();
	
	public boolean isShown();
	
	public void setShowNotifications(boolean show);
	
	public boolean areNotificationsShown();

}
