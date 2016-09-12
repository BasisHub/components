/*
 * This file is part of BasisComponents Package.
 * (c) Basis Europe <eu@basis.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.basiscomponents.bridge.proxygen.Javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.basiscomponents.bridge.proxygen.Method;
import com.basiscomponents.bridge.proxygen.MethodParameter;
import com.basiscomponents.bridge.proxygen.ParseEntity;

import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.getSpaces;
import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.joinify;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 *
 * @author Hyyan
 */
public class Polymer extends AbstractGenerator {

    private String bbjPolymerSrc;

    /**
     * Constructor
     */
    public Polymer() {
        super("git@git.storesandbox.de:hyyan/bbj-polymer.git#latest");
    }

    @Override
    public void generate()
            throws FileNotFoundException, UnsupportedEncodingException, IOException {

        Utilities.cleanDir(getOutput());
        
        String jsOutput = getOutput() + "/js/";
        String elmentsOutput = getOutput() + "/polymer/";
        
        File jsOutputFile = new File(jsOutput);
        File elmentsOutputFile = new File(elmentsOutput);
        
        jsOutputFile.mkdirs();
        elmentsOutputFile.mkdirs();

        Javascript frontendPackage = new Javascript();
        frontendPackage
                .setBBjSrc(getBBjSrc())
                .setClassesSrc(getClassesSrc())
                .setNamespace(getNamespace())
                .setOutput(jsOutput)
                .setVersion(getVersion())
                .generate();

        /**
         * remove bower.json in js output folder
         */
        new File(jsOutput + "/bower.json").delete();
       
        /** move README.md file */
        Files.move(
                new File(jsOutput + "/README.md").toPath(), 
                new File(getOutput() + "/README.md").toPath()
        );
        
        generateElements(elmentsOutput)
                .generateImportElement(
                        elmentsOutput, 
                        "../js/" + getNamespace() + ".js"
                )
                .generateBowerJson("polymer/");

    }
    
    /**
     * Generate bower json
     *
     * @param elmentsOutput
     * @return BowerPackage
     */
    protected Polymer generateBowerJson(String elmentsOutput)
            throws FileNotFoundException, UnsupportedEncodingException {

        String filename = getOutput() + "bower.json";
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        
        String[] main = new String[]{
            elmentsOutput + getNamespace().toLowerCase() + ".html"
        };
        
        Map<String, String> deps = new HashMap<String, String>();
        deps.put("bbj-polymer",getBBjSrc());
        
        String file = new BowerJsonBuilder()
                .setName(getNamespace())
                .setMain(main)
                .setDependencies(deps)
                .setPrivate(true)
                .generate();
        
        writer.println(file);
        writer.close();

        return this;
    }
    
    /**
     * Generate import element
     * 
     * @param elmentsOutput
     * @param mainJs
     * 
     * @return Polymer
     */
    protected Polymer generateImportElement(String elmentsOutput,String mainJs) 
            throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        
        String modules = elmentsOutput + "modules.html";
        String script = "<script src='" + mainJs + "'></script>\n";
        
        /** Generate modules file */
        joinify(elmentsOutput,modules , ".html");
        
        /** prepend script call in the modules file */
        String modulesContent = String.join(
                "\n",
                Files.readAllLines(Paths.get(modules))
        );
        PrintWriter writer = new PrintWriter(modules,"UTF-8");
        writer.print(script);
        writer.print(modulesContent);
        writer.close();
        
        /** Generate import html to import the main js file */
        String importName = getNamespace().toLowerCase() + "-import.html";
        
        writer = new PrintWriter(
                elmentsOutput + getNamespace().toLowerCase() + "-import.html",
                "UTF-8"
        );
        writer.print(script);
        writer.close();
        
        /** Generate the main html file */
        File[] fileList = new File(elmentsOutput).listFiles();
        writer = new PrintWriter(
                elmentsOutput + getNamespace().toLowerCase() + ".html", "UTF-8"
        );
        
        writer.printf("<link rel='import' href='%s'>\n",importName);
        for (int i = 0; i < fileList.length; i++) {
            String name  = fileList[i].getName();
            if (
                    name.endsWith(".html") &&
                    !(name.contains("import") || name.contains("modules"))
               ) 
            {
                writer.printf("<link rel='import' href='%s'>\n",name);
            }
        }
        writer.close();
        return this;
    }
    
    /**
     * Generate elements
     *
     * Generate the polymer elements
     *
     * @param elmentsOutput
     *
     * @return
     */
    protected Polymer generateElements(String elmentsOutput)
            throws UnsupportedEncodingException, IOException {

        HashMap<String, ParseEntity> classes = getClassesSrc();
        String namespace = getNamespace();
        String outputfolder = elmentsOutput;
        String classfileprefix = getClassFilePrefix();
        String classfilesuffix = getClassFileSuffix();

        Set<String> ks = classes.keySet();
        Iterator<String> itfiles = ks.iterator();

        /**
         * Generate javascript classes
         */
        while (itfiles.hasNext()) {

            String classname = itfiles.next();
            ParseEntity pe = classes.get(classname);
            String id = (namespace + "-" + classname).toLowerCase();
            String filename = outputfolder + id + ".html";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            
            writer.println("<!--");
            writer.printf("This file is part of %s Package.\n", namespace);
            writer.println("(c) Basis Europe <eu@basis.com>");
            writer.println("");
            writer.println("For the full copyright and license information, please view the LICENSE");
            writer.println("file that was distributed with this source code.");
            writer.println("");
            writer.println("Code is auto generated using Basis Components Polymer Generator.");
            writer.println("-->");
            writer.println();

            writer.printf("<dom-module id='%s'>\n",id);
            
            writer.printf("%s<script>\n\n",getSpaces(2));
            writer.printf("%s'use strict';\n\n",getSpaces(2));
            writer.printf("%sPolymer({\n",getSpaces(2));
                
                /** id */
                writer.printf("%sis : '%s', \n",getSpaces(4),id);
                /** behaviors */
                writer.printf("%sbehaviors: [BBj.Polymer.SessionBehavior],\n",getSpaces(4));
                /** properties */
                writer.printf("%sproperties: {\n",getSpaces(4));
                    writer.printf("%smodel: {\n",getSpaces(6));
                        writer.printf("%stype: Object, \n",getSpaces(8));
                        writer.printf("%snotify: true, \n",getSpaces(8));
                        writer.printf("%scomputed: '_computedModel(session)'\n",getSpaces(8));
                    writer.printf("%s}\n",getSpaces(6));
                writer.printf("%s},\n\n",getSpaces(4));
                
                /** _computedModel */
                writer.printf("%s_computedModel : function(session){\n",getSpaces(4));
                    writer.printf(
                            "%sreturn new %s(session); \n",
                            getSpaces(6),
                            namespace + "." + classname
                    );
                writer.printf("%s},\n\n",getSpaces(4));
                
                /**
                 * Session wrap methods
                 */
                writer.printf("%spushVar : function(){\n", getSpaces(4));
                writer.printf(
                        "%sthis.model.session.pushVar.apply(this.model.session,arguments);\n",
                        getSpaces(6)
                );
                writer.printf("%s},\n\n", getSpaces(4));

                writer.printf("%sgetResult : function(){\n", getSpaces(4));
                writer.printf("%sreturn this.model.session.result;\n", getSpaces(6));
                writer.printf("%s},\n\n", getSpaces(4));

                writer.printf("%slog : function(){\n", getSpaces(4));
                writer.printf(
                        "%sthis.model.session.log.apply(this.model.session,arguments);\n"
                        , getSpaces(6)
                );
                writer.printf("%s},\n\n", getSpaces(4));

                writer.printf("%spromise : function(){\n", getSpaces(4));
                writer.printf(
                        "%sreturn this.model.session.promise.apply(this.model.session,arguments)"
                                + ".finally(function(){\n",
                        getSpaces(6)
                );
                if (pe.getClassname().contains(".")) {
                    writer.printf(
                            "%sthis.session.create(this.model.id,'%s');\n",
                            getSpaces(8),
                            pe.getClassname()
                    );
                } else {
                    writer.printf(
                            "%sthis.session.create(this.model.id,'::%s%s%s::%s');\n",
                            getSpaces(8),
                            classfileprefix,
                            pe.getClassname(),
                            classfilesuffix,
                            pe.getClassname()
                    );
                }
                writer.printf("%s}.bind(this));\n", getSpaces(6));
                writer.printf("%s},\n\n", getSpaces(4));

                //writer.printf("%sexec : function(){\n", getSpaces(4));
                writer.printf(
                        "%sreturn this.model.session.exec.apply(this.model.session,arguments);\n",
                        getSpaces(6)
                );
                writer.printf("%s},\n\n", getSpaces(4));

            /**
             * class methods
             */
            Iterator<Method> it1 = pe.getMethods().iterator();
            while (it1.hasNext()) {

                Method m = it1.next();
                if (m.getIsConstructor()) {
                    continue;
                }

                // first check if all concerned data types are valid
                Boolean OK = true;
                if (!AllowedTypes.contains(m.getReturnType())) {
                    OK = false;
                }

                Iterator<MethodParameter> it2 = m.getParams().iterator();
                while (it2.hasNext()) {
                    if (!AllowedTypes.contains(it2.next().getType())) {
                        OK = false;
                        break;
                    }

                }

                if (!OK) {
                    continue;
                }

                if (m.getIsOverloaded()) {
                    continue;
                } else {
                    writer.printf(
                            "%s%s : function(){\n",
                            getSpaces(4),
                            m.getName()
                    ).printf(
                            "%sreturn this.model.%s.apply(this.model,arguments);\n",
                            getSpaces(6),
                            m.getName()
                    ).printf("%s},\n\n",getSpaces(4));
                }
            }
            
            writer.printf("%s});\n",getSpaces(2));
            writer.printf("%s</script>\n",getSpaces(2));
            writer.printf("</dom-module>\n",id);
            writer.close();
        }
        return this;
    }

}
