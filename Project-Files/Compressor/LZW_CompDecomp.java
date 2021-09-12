package Compressor;

import Compressor.includes.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class LZW_CompDecomp extends IO {

    // Compression Constructor
    LZW_CompDecomp(String i_inputPath, short i_bitsToRead) {
        this.inputPath = i_inputPath; // Assign file input path as a variable.
        this.outputPath = i_inputPath + ".LZW";
        this.userSelectedCompressionMode = i_bitsToRead;
        this.compressionSize = this.COMPRESSION_MODE[this.userSelectedCompressionMode];
        this.READ_WRITE_CYCLES = this.compressionSize / 8;

        // Initialize input stream.
        try {
            inputStream = new FileInputStream(inputPath);
        } catch (FileNotFoundException e) {
            System.out.printf("\nFile not found under the path:\"%s\".\n", inputPath);
            Thread.currentThread().interrupt();
        }

        // Initialize output stream.
        try {
            outputStream = new FileOutputStream(outputPath);
        } catch (FileNotFoundException e) {
            System.out.printf("\nFile not found under the path:\"%s\".\n", outputPath);
            Thread.currentThread().interrupt();
        }


        // Create bit readers and writers.
        bitInputStream = new BitInputStream(inputStream);
        bitOutputStream = new BitOutputStream(outputStream);

    }

    // Decompression constructor
    public LZW_CompDecomp(String i_inputPath) {
        this.inputPath = i_inputPath; // Assign file input path as a variable.
        this.outputPath = i_inputPath.substring(0, i_inputPath.length() - 4); // Assign file output path as a variable.

        // Initialize input stream.
        try {
            inputStream = new FileInputStream(inputPath);
        } catch (FileNotFoundException e) {
            System.out.printf("\nFile (input) not found under the path:\"%s\".\n", inputPath);
        }

        // Initialize output stream.
        try {
            outputStream = new FileOutputStream(outputPath);
        } catch (FileNotFoundException e) {
            System.out.printf("\nFile (output) not found under the path:\"%s\".\n", outputPath);
        }

        // Create bit readers and writers.
        bitInputStream = new BitInputStream(inputStream);
        bitOutputStream = new BitOutputStream(outputStream);

    }

    public void compressFile(){
        System.out.println("[SYSTEM] Starting compression.");

        // Declarations & Variables
        final int EOF = -1;
        final int NO_KNOWN_BYTES = -1;
        final int SAVE_OPERATION = 0;
        final int LOAD_OPERATION = 1;
        int currentDictionaryKey = 0;
        int knownPartOfTheCompression = -1;
        String nextBytes = "";
        String bytesFromFile = "";
        HashMap<String, Integer> dictionary = new HashMap<>();

        // Write the bit mode used to file.
        bitOutputStream.writeBits(2, this.userSelectedCompressionMode);

        while(true){
            System.out.println();
            nextBytes = readBytesFromFile();
            bytesFromFile = bytesFromFile.concat(nextBytes);

            if(bytesFromFile.length() == 0) // EOF CHECK
                break;

            if(dictionary.containsKey(bytesFromFile) && nextBytes.length() != 0){ // We already have the bytes in the dictionary.
                knownPartOfTheCompression = dictionary.get(bytesFromFile);
                //System.out.printf("[DEBUG] I know those bytes, their key is [%d], data is \"%s\".\n",knownPartOfTheCompression, nextBytes);

            } else { // We don't have the bytes in the dictionary.

                if(knownPartOfTheCompression != NO_KNOWN_BYTES){ // If there's known bytes write their key.
                    //System.out.printf("[DEBUG] Writing 'Load' Operation to file with key [%d].\n", knownPartOfTheCompression);
                    bitOutputStream.writeBits(1, LOAD_OPERATION);
                    writeKnownPartOfCompression(knownPartOfTheCompression);

                    // Reset variable
                    knownPartOfTheCompression = NO_KNOWN_BYTES;

                    // Add bytes sequence with key to dictionary.
                    dictionary.put(bytesFromFile, currentDictionaryKey++);

                    // Write new bytes to file.
                    writeUnknownPartOfCompression(nextBytes);

                    // Reset variable
                    bytesFromFile = "";
                    nextBytes = "";
                } else { // If theres no known bytes, write the saving operation.
                    bitOutputStream.writeBits(1, SAVE_OPERATION);

                    // Add bytes sequence with key to dictionary.
                    //System.out.printf("[DEBUG] Adding data \"%s\" with key [%d].\n", bytesFromFile, currentDictionaryKey);
                    dictionary.put(bytesFromFile, currentDictionaryKey++);

                    // Write new bytes to file.
                    writeUnknownPartOfCompression(bytesFromFile);

                    // Reset variable
                    bytesFromFile = "";
                    nextBytes = "";
                }

            }

        } // End of while loop

        System.out.println("[SYSTEM] ~ Reached EOF.");
        bitOutputStream.flush();
        bitOutputStream.close();
        bitInputStream.close();
    }

    private String readBytesFromFile() {
        // Variables & Declarations
        final int EOF = -1;
        final String ZERO_CHAR = "0";
        int currentByte = -1;
        String formattedByte = "";
        String parsedBytes = "";

        //System.out.println("[DEBUG] Reading from file.");
        // Read N amount of bytes from file
        for (int i = 0; i < READ_WRITE_CYCLES; i++) {
            try {
                currentByte = bitInputStream.readBits(BYTE);

                if(currentByte == EOF)
                    break;

                String nonFormattedByte = Integer.toBinaryString(currentByte);

                for(int j = 0; j < (BYTE - nonFormattedByte.length()); j++){
                    formattedByte = formattedByte.concat(ZERO_CHAR);
                }

                formattedByte = formattedByte.concat(nonFormattedByte);

                //System.out.printf("[DEBUG] Unformatted = \"%s\", Formatted = \"%s\".\n", nonFormattedByte, formattedByte);

                parsedBytes = parsedBytes.concat(formattedByte);

                formattedByte = "";

            } catch (IOException e) {
                System.out.println("[ERROR] Cannot read file.");
                Thread.currentThread().interrupt();
            }

        }

        //System.out.printf("[DEBUG] Entire formatted string \"%s\".\n",parsedBytes);
        return parsedBytes;
    }

    public void decompressFile(){
        System.out.println("[SYSTEM] Starting decompression.");

        // Declarations & Variables
        final int EOF = -1;
        final int NO_KNOWN_BYTES = -1;
        final int SAVE_OPERATION = 0;
        final int LOAD_OPERATION = 1;
        int currentDictionaryKey = 0;
        int operationFromFile = -1;
        String knownPartOfTheCompression = "";
        String bytesFromFile = "";
        HashMap<Integer, String> dictionary = new HashMap<>();

        // Read the bit mode used from file.
        try {
            this.userSelectedCompressionMode = bitInputStream.readBits(2);
            this.compressionSize = this.COMPRESSION_MODE[this.userSelectedCompressionMode];
            this.READ_WRITE_CYCLES = this.compressionSize / 8;
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read file.");
            Thread.currentThread().interrupt();
        }

        while(true){
            System.out.println();
            try {
                operationFromFile = bitInputStream.readBits(1);
            } catch (IOException e) {
                System.out.println("[ERROR] Cannot read file.");
                Thread.currentThread().interrupt();
            }

            bytesFromFile = readBytesFromFile();

            if(bytesFromFile.length() == 0)
                break;

            if(operationFromFile == LOAD_OPERATION){
                knownPartOfTheCompression = dictionary.get(Integer.parseInt(bytesFromFile,2));

                if(knownPartOfTheCompression == null){
                    System.out.println("[ERROR] Error #2 - Couldn't resolve bytes from key.");
                    Thread.currentThread().interrupt();
                }

                writeUnknownPartOfCompression(knownPartOfTheCompression); // Write already known bytes

                // Read new bytes
                bytesFromFile = readBytesFromFile();

                // Add new dictionary key.
                //System.out.printf("[DEBUG] Adding bytes \"%s\" to dictionary in position [%d].\n",knownPartOfTheCompression + bytesFromFile, currentDictionaryKey);
                dictionary.put(currentDictionaryKey++, knownPartOfTheCompression + bytesFromFile);

                // Refresh known part of the compression
                knownPartOfTheCompression = "";

                // Write new bytes to file
                writeUnknownPartOfCompression(bytesFromFile);

            } else if(operationFromFile == SAVE_OPERATION){
                // Add new dictionary key.
                dictionary.put(currentDictionaryKey++,bytesFromFile);

                // Write new bytes to file
                writeUnknownPartOfCompression(bytesFromFile);
            }

        } // End of while loop
        System.out.println("[SYSTEM] ~ Reached EOF.");
        bitInputStream.close();
        bitOutputStream.flush();
        bitOutputStream.close();


    }

    private void writeKnownPartOfCompression(int i_knownPartOfTheCompression) {
        final String ZERO_BYTE = "0";

        // Write the decoded position in dictionary.
        String binaryRepresentation = Integer.toBinaryString(i_knownPartOfTheCompression);

        // Format output fix
        String zeroedBytes = "";
        for(int i = 0; i < BYTE * READ_WRITE_CYCLES - binaryRepresentation.length(); i++){
            zeroedBytes = zeroedBytes.concat(ZERO_BYTE);
        }

        binaryRepresentation = zeroedBytes + binaryRepresentation;

        //System.out.printf("[DEBUG] Writing 'Load' operation with position [%s] (Binary) .\n", binaryRepresentation);

        // Write N amount of bytes to compressed file
        // Variables
        String dataByte = "";

        for (int i = 0; i < READ_WRITE_CYCLES; i++) {
            try {
                dataByte = binaryRepresentation.substring(0, 8);
            } catch (java.lang.StringIndexOutOfBoundsException e){
                dataByte = binaryRepresentation.substring(0, binaryRepresentation.length());
            }

            int dataByte_Parsed = Integer.parseInt(dataByte, 2);
            //System.out.printf("[DEBUG] dataByte = %s, parsed %d (Binary %s)\n",dataByte,
                    //dataByte_Parsed, Integer.toBinaryString(dataByte_Parsed));

            if(dataByte.length() == 0)
                System.out.println("~ [Error]");

            bitOutputStream.writeBits(BYTE, dataByte_Parsed);

            try {
                binaryRepresentation = binaryRepresentation.substring(8, binaryRepresentation.length());
            }
            catch (java.lang.StringIndexOutOfBoundsException e){
                System.out.println("~ [Error] Error #1 - Writing known part was not split to bytes correctly.");
                break;
            }

        }
    }

    private void writeUnknownPartOfCompression(String i_unknownPartOfTheCompression) {
        final String ZERO_BYTE = "0";
        String parsedByte = "";


        for(int i = 0; i < i_unknownPartOfTheCompression.length() / BYTE; i++) {
            int dataByte = Integer.parseInt(i_unknownPartOfTheCompression.substring(i * BYTE, BYTE * (i + 1)), 2);
            bitOutputStream.writeBits(BYTE, dataByte);
            //System.out.printf("[DEBUG] Writing data \"%s\" to file.\n", Integer.toBinaryString(i_unknownPartOfTheCompression.charAt(i)));
        }

    }

}
