package fileIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class BufferedFileReader implements Iterator<String>{
	private final BufferedReader reader;
	String line = null;

	public BufferedFileReader(File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
			line = reader.readLine();
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		return line != null;
	}

	@Override
	public String next() {
		String nextLine = line;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return nextLine;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
