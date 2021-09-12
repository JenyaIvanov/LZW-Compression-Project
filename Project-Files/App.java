import Compressor.Runner;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {

    // Fields
    private String path = "";
    private boolean selectedFile = false;
    private short selectedMode = 0; // Defaults to 8-Bit.

    // Components
    private JPanel mainPanel;
    private JLabel backgroundImage;

    // Constructor
    public App(String i_appTitle){
        super(i_appTitle);

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }

    public static void main(String[] args) {
        // Variables
        final String APP_TITLE = "LZW Compression";

        // Frame
        JFrame frame = new App(APP_TITLE);
        frame.setVisible(true);

    }


    // Custom components
    private void createUIComponents() {
        // Variables
        JButton btn_compress = new JButton("");
        JButton btn_decompress = new JButton("");
        JButton btn_8BIT = new JButton("●");
        JButton btn_16BIT = new JButton("");
        JButton btn_32BIT = new JButton("");
        JButton btn_64BIT = new JButton("");
        JLabel lbl_console = new JLabel("- LZW Compressor by Jenya Ivanov & Dor Nakashe.");
        JButton btn_fileChooser = new JButton("Please Select File");
        JFileChooser fileChooser = new JFileChooser();

        // Arrays
        JButton[] bitSelectors = {btn_8BIT,btn_16BIT,btn_32BIT,btn_64BIT};


        // Design
        backgroundImage = new JLabel(new ImageIcon("src/Compressor/Final_BG.png"));

        // Setting buttons location
        final int BTN_COMPDE_XPOS = 760;
        final int BTN_CMPRS_YPOS = 465;
        final int BTN_DECMPRS_YPOS = BTN_CMPRS_YPOS + 55;

            // Console label
        final int LBL_CONSOLE_XPOS = 580;
        final int LBL_CONSOLE_YPOS = 580;
        final int LBL_CONSOLE_WIDTH = 483;
        final int LBL_CONSOLE_HEIGHT = 65;

            // File Chooser
        final int BTN_CHOOSER_XPOS = 693;
        final int BTN_CHOOSER_YPOS = 335;
        final int BTN_CHOOSER_WIDTH = 290;
        final int BTN_CHOOSER_HEIGHT = 30;

            // Checkmark buttons
        final int BTN_CHK_SIZE = 30;
        final int BTN_CHK_YPOS = 395;
        final int BTN_CHK_OFFSET = 65;
        final int BTN_8BIT_XPOS = 680;
        final int BTN_16BIT_XPOS = BTN_8BIT_XPOS + BTN_CHK_OFFSET + 18;
        final int BTN_32BIT_XPOS = BTN_16BIT_XPOS + BTN_CHK_OFFSET + 27;
        final int BTN_64BIT_XPOS = BTN_32BIT_XPOS + BTN_CHK_OFFSET + 25;

        // Remove layout
        btn_compress.setLayout(null);
        btn_decompress.setLayout(null);
        btn_8BIT.setLayout(null);
        btn_16BIT.setLayout(null);
        btn_32BIT.setLayout(null);
        btn_64BIT.setLayout(null);
        lbl_console.setLayout(null);
        btn_fileChooser.setLayout(null);


            // Compress & Decompress
        btn_compress.setBounds(BTN_COMPDE_XPOS,BTN_CMPRS_YPOS,110,40);
        btn_decompress.setBounds(BTN_COMPDE_XPOS,BTN_DECMPRS_YPOS, 110, 40);

                // Make buttons transparent
        btn_compress.setOpaque(false);
        btn_compress.setContentAreaFilled(false);
        btn_compress.setBorderPainted(false);
        btn_decompress.setOpaque(false);
        btn_decompress.setContentAreaFilled(false);
        btn_decompress.setBorderPainted(false);

            // Bit selectors
        btn_8BIT.setBounds(BTN_8BIT_XPOS,BTN_CHK_YPOS, BTN_CHK_SIZE, BTN_CHK_SIZE);
        btn_16BIT.setBounds(BTN_16BIT_XPOS,BTN_CHK_YPOS, BTN_CHK_SIZE, BTN_CHK_SIZE);
        btn_32BIT.setBounds(BTN_32BIT_XPOS,BTN_CHK_YPOS, BTN_CHK_SIZE, BTN_CHK_SIZE);
        btn_64BIT.setBounds(BTN_64BIT_XPOS,BTN_CHK_YPOS, BTN_CHK_SIZE, BTN_CHK_SIZE);

                // Bit Selector - Graphics
        btn_8BIT.setFont(new Font("Arial", Font.PLAIN, 40));
        btn_16BIT.setFont(new Font("Arial", Font.PLAIN, 40));
        btn_32BIT.setFont(new Font("Arial", Font.PLAIN, 40));
        btn_64BIT.setFont(new Font("Arial", Font.PLAIN, 40));
        btn_8BIT.setOpaque(false);
        btn_8BIT.setContentAreaFilled(false);
        btn_16BIT.setOpaque(false);
        btn_16BIT.setContentAreaFilled(false);
        btn_32BIT.setOpaque(false);
        btn_32BIT.setContentAreaFilled(false);
        btn_64BIT.setOpaque(false);
        btn_64BIT.setContentAreaFilled(false);


        btn_8BIT.setForeground(Color.RED);
        btn_16BIT.setForeground(Color.RED);
        btn_32BIT.setForeground(Color.RED);
        btn_64BIT.setForeground(Color.RED);

            // Labels
        lbl_console.setBounds(LBL_CONSOLE_XPOS, LBL_CONSOLE_YPOS, LBL_CONSOLE_WIDTH, LBL_CONSOLE_HEIGHT);
        lbl_console.setForeground(Color.white);

            // File Chooser
        btn_fileChooser.setBounds(BTN_CHOOSER_XPOS, BTN_CHOOSER_YPOS, BTN_CHOOSER_WIDTH, BTN_CHOOSER_HEIGHT);
        btn_fileChooser.setBorderPainted(false);
        btn_fileChooser.setContentAreaFilled(false);
        btn_fileChooser.setOpaque(false);


        // File Chooser Action Listener
        btn_fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    int r = jFileChooser.showOpenDialog(null);

                    if (r == JFileChooser.APPROVE_OPTION) {
                        selectedFile = true;
                        path = jFileChooser.getSelectedFile().getAbsolutePath();
                        btn_fileChooser.setText(path);
                    }

            }
        });

        // Compress Action Listener
        btn_compress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedFile){
                    short debugMode = 1;
                    lbl_console.setText("- Compression in progress...");
                    Runner compressor = new Runner(path, selectedMode);
                    lbl_console.setText("- File compressed successfully.");
                } else {
                    lbl_console.setText("- File not selected.");
                }
            }
        });

        // Decompress Action Listener
        btn_decompress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedFile){
                    if(path.substring(path.length()-4,path.length()).equals(".LZW")){
                        lbl_console.setText("- Decompression in progress...");
                        Runner decompressor = new Runner(path);
                        lbl_console.setText("- File decompressed successfully.");
                    }else
                        lbl_console.setText("- Non LZW compressed file selected, please select LZW compressed file.");

                } else {
                    lbl_console.setText("- File not selected.");
                }
            }
        });

        // Bit selector logic
        btn_8BIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedMode = 0;
                btn_8BIT.setText("X");

                btn_16BIT.setText("");
                btn_32BIT.setText("");
                btn_64BIT.setText("");
            }
        });

        btn_16BIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedMode = 1;

                btn_16BIT.setText("X");

                btn_8BIT.setText("");
                btn_32BIT.setText("");
                btn_64BIT.setText("");
            }
        });

        btn_32BIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedMode = 2;

                btn_32BIT.setText("X");

                btn_8BIT.setText("");
                btn_16BIT.setText("");
                btn_64BIT.setText("");
            }
        });

        btn_64BIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedMode = 3;

                btn_64BIT.setText("X");

                btn_8BIT.setText("");
                btn_32BIT.setText("");
                btn_16BIT.setText("");
            }
        });

        // Adding buttons to background image
        backgroundImage.setLayout(null);
        backgroundImage.add(btn_compress,new FlowLayout());
        backgroundImage.add(btn_decompress);
        backgroundImage.add(btn_8BIT);
        backgroundImage.add(btn_16BIT);
        backgroundImage.add(btn_32BIT);
        backgroundImage.add(btn_64BIT);
        backgroundImage.add(lbl_console);
        backgroundImage.add(btn_fileChooser);


    }
}
