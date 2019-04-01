package dataproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import data.Log;
import data.LogLevel;
import data.Player;
import interfaces.IDataProvider;

public abstract class DataProviderObservable extends Observable implements IDataProvider {

	protected Observer[] observers;
	private List<Player> playerList = new ArrayList<Player>();

	public DataProviderObservable(Observer[] observers) {
		this.observers = observers;
	}
	
	protected boolean postData(Player player) {
		// Check for duplicates
		if (!playerList.contains(player)) {
			playerList.add(player);
			for (Observer observer : observers) {
				observer.update(this, player);
			}
			return true;
		}
		
		postLog(new Log(LogLevel.Info, "Duplicated player found: " + player.name));
		return false;
	}
	
	protected void postLog(Log log) {
		for (Observer observer : observers) {
			observer.update(this, log);
		}		
	}
	
}
