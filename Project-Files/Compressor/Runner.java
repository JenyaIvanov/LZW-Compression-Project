package Compressor;

import java.util.Scanner;

public class Runner {

    // Compressor
    public Runner(String i_inputPath, short i_mode){
        LZW_CompDecomp LZW_Compressor = new LZW_CompDecomp(i_inputPath, i_mode);
        LZW_Compressor.compressFile();
    }

    // Decompressor
    public Runner(String i_inputPath){
        LZW_CompDecomp LZW_Decompressor = new LZW_CompDecomp(i_inputPath);
        LZW_Decompressor.decompressFile();
    }

}
