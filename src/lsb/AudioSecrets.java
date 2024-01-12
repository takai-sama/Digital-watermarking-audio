package lsb;

// Encodes a message from a text file or command line into a .wav file, and decoding a wav into a text file

// Remember that the audio length must be large enough to store the message. 2756 characters can be stored in every second of audio data.
// If you select the generate static option rather than selecting your own file, it will automatically make a file that's long enough.

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
 
public class AudioSecrets {
 
//        public static void main (String [] args) throws IOException
//        {
//                Scanner scan = new Scanner(System.in);
//                System.out.println("What would you like to do? (enter the number)");
//                int command = 0;
//               
//                do
//                {
//                        System.out.println("1 - Encode a text file into an audio file");
//                        System.out.println("2 - Decode an audio file ");
//                        System.out.print("> ");
//                        command = scan.nextInt();
//                        System.out.println();
//               
//                        if (command == 1)
//                                encode();
//                        if (command == 2)
//                                decode();
//                       
//                } while (command != 1 && command != 2);
//                       
//                System.out.println("The End.");
//                scan.close();
//        }
 
 
        public static void decode() throws IOException
        {
                Scanner scan = new Scanner(System.in);
               
                System.out.print("Enter audio file name that you want to decode (must be a wav file)\n> ");
                String audioToDecode = scan.nextLine();
                if (!audioToDecode.endsWith(".wav")) // Check if the filename ends with .wav and add the extension if it doesn't
                        audioToDecode += ".wav";
 
                byte[] audioEncoded = getAudioData(audioToDecode);
               
                byte[] decryptedData = LSBdecode(audioEncoded);
               
                String message = new String( byteArrayToCharArray(decryptedData) );
               
                System.out.print("Enter the name for a text file to write the data to\n> ");
                String outputName = scan.nextLine();
                if (!outputName.endsWith(".txt"))
                        outputName += ".txt";
               
                // write message to file
                BufferedWriter writer = null;
                writer = new BufferedWriter(new FileWriter(outputName));
                writer.write(message);
                writer.close();
                scan.close();
        }
       
//        public static void encode()
//        {
//                Scanner scan = new Scanner(System.in);
//               
//                byte[] message = null;          
//                System.out.print("Enter text file name that you want to encode (must be a plain text file)\n> ");
//                String textFileName = scan.next(); // The text file to read in
//                System.out.println();
//                if (!textFileName.endsWith(".txt")) // Check if the filename ends with .txt and add the extension if it doesn't
//                        textFileName += ".txt";        
// 
//                File file = new File(textFileName);
//                try {
//                        // The \\Z delimiter in combination with .next() will read input until there isn't any left
//                		//char[] msgarr= new Scanner(file).useDelimiter("\\Z").next().toCharArray();
//        				//String msg= new String(msgarr);
//        				//System.out.println("The message is : "+ msg);
//                        message = charArrayToByteArray( new Scanner(file).useDelimiter("\\Z").next().toCharArray() );
//                        
//                } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                }
//                
//               
//                System.out.println("Enter the audio file name that you want the text encoded into (must be a .wav file)");
//                String audioFileName = scan.next();
//                System.out.println();
//               
//                byte[] audioData;
//                audioData = getAudioData(audioFileName);       
//               
//               
//                // First encode the total number of bytes that are going to be encoded into the first 32 bits(one int) of the audio data.
//                // This allows the message length to be determined right when the audio file is read.
//               
//             
//                int messageLengthInt = message.length;
//                
//                byte[] messageLength = bit_conversion(messageLengthInt);
//                
//               // System.out.println("msg len int " + messageLengthInt);
//               
//                LSBencode(messageLength, audioData, 0);
//                LSBencode(message, audioData, 32);
//               
//                System.out.print("Enter a name for the output audio file with the text encoded in it\n> ");
//                String outputFile = scan.next();
//                System.out.println();
//                if (!outputFile.endsWith(".wav")) // Check if the filename ends with .wav and add the extension if it doesn't
//                        outputFile += ".wav";
//               
//                saveByteArrayAsAudioFile(audioData, outputFile);
//                scan.close();
//        }
       
        // Note that the start index is in *bits* (since one bit is stored in each audio byte)
        //              so if you encode an int at 0, then the next start index is 32.
        public static void LSBencode(byte[] message, byte[] audioData, int startIndex)
        {
             
        	
        	if (message.length + startIndex > audioData.length) {
    			throw new IllegalArgumentException("File not long enough!");
    		}

    		// loop through each addition byte
    		for (int i = 0; i < message.length; ++i) {
    			// loop through the 8 bits of each byte
    			int add = message[i];
    			for (int bit = 7; bit >= 0; --bit, ++startIndex) // ensure the new
    															// offset value
    															// carries on
    															// through both
    															// loops
    			{
    				// assign an integer to b, shifted by bit spaces AND 1
    				// a single bit of the current byte
    				int b = (add >>> bit) & 1;
    			

    				// assign the bit by taking: [(previous byte value) AND 0xfe] OR
    				// bit to add

    				// changes the last bit of the byte in the image to be the bit
    				// of addition

    				audioData[startIndex] = (byte) ((audioData[startIndex] & 0xFE) | b);

    			}

    		}

 
        }
 
        public static byte[] LSBdecode(byte[] data)
        {
                // read message length which is an int stored in the first 32 bytes of the audioData
                // messageLength tells the number of bytes that are encrypted in the audio data(not including itself)
               
        	
        	int length = 0;
    		int offset = 32;
    		// loop through 32 bytes of data to determine text length
    		for (int i = 0; i < 32; ++i) // i=24 will also work, as only the 4th
    										// byte contains real data
    		{
    			length = (length << 1) | (data[i] & 1);
    		}
    		//System.out.println("The extracted length is: " + length);

    		byte[] result = new byte[length];

    		// loop through each byte of text
                
                try{
    		for (int b = 0; b < result.length; ++b)

    		{

    			// loop through each bit within a byte of text

    			for (int i = 0; i < 8; ++i, ++offset)

    			{

    				// assign bit: [(new byte value) << 1] OR [(text byte) AND 1]

    				result[b] = (byte) ((result[b] << 1) | (data[offset] & 1));

    			}

    		}
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "File bukan berkas stego \n"+e , "Error", ERROR_MESSAGE);
                }

    		return result;
        }
       
       
        // Returns the audio data given the name of a file
        public static byte[] getAudioData(String filename) {
                byte[] audioData = null;
                AudioInputStream ais = null;
               
                // Check if the filename ends with .wav and add the extension if it doesn't
                if (!filename.endsWith(".wav"))
                {      
                        filename += ".wav";
                }
               
                try {
                	// try to read from file
                	File file = new File(filename);
                	if (file.exists()) {
                		ais = AudioSystem.getAudioInputStream(file);
                		audioData = new byte[ais.available()];
                		ais.read(audioData);
                	}
                }
                catch (Exception e) {
                	System.out.println(e.getMessage());
                	throw new RuntimeException("Could not read " + filename);
                }
               
                return audioData;
        }
       
        public static void saveByteArrayAsAudioFile(byte[] data, String filename)
        {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            AudioInputStream ais = new AudioInputStream(bais, format, data.length);
            if (filename.toLowerCase().endsWith(".wav")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            }
            else if (filename.toLowerCase().endsWith(".au")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
            }
            else {
                throw new RuntimeException("File format not supported: " + filename);
            }
            
        }
        catch (Exception e) {
            
        }
        }
       
      /*  public static int getAudioDuration(byte[] data)
        {
                // 44100 bytes of data per second so just divide to get the total length in seconds
                return data.length / 44100;
        }
       */
       
        // Char to Byte Array and reversal - safer than using the built in String methods because these will always use UTF-16
        public static byte[] charArrayToByteArray(char[] chars)
        {
                byte[] bytes = new byte[chars.length*2];
                for(int i=0;i<chars.length;i++) {
                   bytes[i*2] = (byte) (chars[i] >> 8);
                   bytes[i*2+1] = (byte) chars[i];
                }
                return bytes;
        }      
        public static char[] byteArrayToCharArray(byte[] bytes)
        {
                char[] chars = new char[bytes.length / 2];
                for(int i=0;i<chars.length;i++)
                        chars[i] = (char) ((bytes[i*2] << 8) + bytes[i*2+1]);
 
                return chars;
        }
        
        public static byte[] bit_conversion(int i) {

    		return (new byte[] { 0, 0, 0, (byte) (i & 0x000000FF) });

    	}
}