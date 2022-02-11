package it.unibo.disi.spaf.examples.utils;

public class Image {
	
	private final String filename;
	private final byte[] bytes;

	public Image(String filename, byte[] bytes) {
		super();
		this.filename = filename;
		this.bytes = bytes;
	}

	public String getFilename() {
		return filename;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
}