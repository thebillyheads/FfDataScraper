package data;

public class Log {
	
	public LogLevel logType;
	public String message;
	
	public Log(LogLevel logType, String message) {
		super();
		this.logType = logType;
		this.message = message;
	}
	
}
