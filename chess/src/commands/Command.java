package commands;

public abstract class Command {
	protected String log;
	
	@Override
	public String toString() {
		return log;
	}
}
