package allen.gc.util;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * FileUtil
 * 
 * @author xinzhi.zhang
 * */
public class FileUtil {

    /**
     * Select a file for read opt. return null when user cancel it.
     * */
    public static File selectFileForRead(Component parent) {
        // Create a file chooser
        final JFileChooser fc = new JFileChooser(".");

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // In response to a button click:
        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        throw new RuntimeException("file exception when select file.");
    }

    /**
     * Read file to list of string.
     * */
    public static List<String> readLineFromFile(File file) {
        List<String> result = new ArrayList<String>();
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), Charset
                            .defaultCharset().name())));
            for (String line = lnr.readLine(); line != null; line = lnr
                    .readLine()) {
                result.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lnr != null) {
                try {
                    lnr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}
