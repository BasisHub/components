/*
 * This file is part of BBjJsGenerator Package.
 * (c) Basis Europe <eu@basis.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.basiscomponents.bridge.proxygen.Javascript;

import com.basiscomponents.bridge.proxygen.Method;
import com.basiscomponents.bridge.proxygen.MethodParameter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;

/**
 * Utilities
 *
 * Generator utilities
 *
 * @author Hyyan Abo Fakher &lt;habofakher@basis.com&gt;
 */
public class Utilities {

    /**
     * Get spaces
     *
     * @param number number of spaces
     *
     * @return String empty string
     */
    public static String getSpaces(Integer number) {
        return String.join("", Collections.nCopies(number, " "));
    }

    /**
     * Clean given dir
     *
     * @param dir
     */
    public static void cleanDir(String dir) {
        new File(dir).mkdirs();
        File object = new File(dir);
        for (File file : object.listFiles()) {
            if (file.isDirectory()) {
                cleanDir(file.getPath());
            }
            file.delete();
        }
    }

    /**
     * Joinify
     *
     * Join files in the given source
     *
     * @param source
     * @param Output
     * @param type
     * 
     * @throws IOException
     */
    public static void joinify(String source, String Output,String type) throws IOException {
        File[] fileList = new File(source).listFiles();
        String content = "";
        String path = "";
        for (int i = 0; i < fileList.length; i++) {
            path = new String(fileList[i].getPath());
            if (path.endsWith(type)) {
                content += String.join("\n", Files.readAllLines(
                        Paths.get(path)
                ));
            }
        }
        PrintWriter writer = new PrintWriter(Output, "UTF-8");
        writer.print(content);
        writer.close();
    }
    
    
     /**
     * Get Method SignatureString
     *
     * @param method
     *
     * @return String
     */
    public static String getMethodSignatureString(Method method) {
        String sig = "";
        Iterator<MethodParameter> it2 = method.getParams().iterator();
        while (it2.hasNext()) {
            String t = it2.next().getType();
            switch (t) {
                case "String":
                    sig += 'S';
                    break;
                case "Number":
                    sig += 'N';
                    break;
                case "Boolean":
                    sig += 'B';
                    break;
                default:
                    sig += 'O';

            }
        }

        if (sig.isEmpty()) {
            sig = "VOID";
        }

        return sig;
    }
}
