package edu.kuliah.kripto.tubessatu.nomerdua;

public interface Steganography {
	public byte[] embed(byte[] message, byte[] image, int offset);
	public byte[] extract(byte[] image);
}
