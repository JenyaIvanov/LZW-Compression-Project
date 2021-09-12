package Compressor;

// Includes
import Compressor.includes.*;
import java.io.*;


public abstract class IO {
    protected final int[] COMPRESSION_MODE = {8,16,32,64}; // Modes of compression
    protected int userSelectedCompressionMode = COMPRESSION_MODE[0]; // Initialized to 8-Bit.
    protected int compressionSize;
    protected int READ_WRITE_CYCLES;
    protected final int BYTE = 8;

    // Abstract input fields
    FileInputStream inputStream;
    BitInputStream bitInputStream;
    String inputPath;

    // Abstract output fields
    String outputPath;
    FileOutputStream outputStream;
    BitOutputStream bitOutputStream;



}
