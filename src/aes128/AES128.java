package aes128;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64.*;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class AES128 {
	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");			
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes("UTF-8"));
		} catch (Exception e) {
			showMessageDialog(null, e, "Error", ERROR_MESSAGE);
		}
		java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
		return new String(encoder.encodeToString(crypted));
	}

	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
			SecretKeySpec skey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(decoder.decode(input));
		} catch (Exception e) {
			showMessageDialog(null, e, "Error", ERROR_MESSAGE);
		}
		return new String(output);
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		
//		String key = "mvLBiZsiTbGwrfJB";
//		String data = "ABC";
//
//		System.out.println(aes.encrypt(data, key));
//		System.out.println(aes.decrypt(aes.encrypt(data, key), key));
//	}

}
