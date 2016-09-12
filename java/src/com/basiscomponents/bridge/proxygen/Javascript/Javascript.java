/*
 * This file is part of BasisComponents Package.
 * (c) Basis Europe <eu@basis.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.basiscomponents.bridge.proxygen.Javascript;

import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.joinify;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.io.IOException;

import com.basiscomponents.bridge.proxygen.Method;
import com.basiscomponents.bridge.proxygen.MethodParameter;
import com.basiscomponents.bridge.proxygen.ParseEntity;

import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.cleanDir;
import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.getMethodSignatureString;
import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.getSpaces;
import java.util.Map;

/**
 * Bower Package AbstractGenerator

 Generate javascript files in bower bundle style
 *
 * @author Hyyan Abo Fakher <habofakher@basis.com>
 */
public class Javascript extends AbstractGenerator{
    
    /**
     * Constructor
     */
    public Javascript() {
        super("git@git.storesandbox.de:hyyan/bbj-js.git#latest");
    }
    
    /**
     * Generate
     *
     * Generate the bower package
     *
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    @Override
    public void generate()
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        
       new File(getOutput()).mkdirs();
       cleanDir(getOutput());
       generateClasses().generateBowerJson().generateReadme();
       joinify(getOutput(),getOutput() + "/" + getNamespace() + ".js",".js");
                
    }
    
    /**
     * Generate Readme
     * 
     * @return Javascript
     * 
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    protected Javascript generateReadme() throws FileNotFoundException, UnsupportedEncodingException{
        
        String filename = getOutput() + "README.md";
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        
        writer.println("# " + getNamespace() + " - " + getVersion());
        writer.println("This Package is auto generated.");
        writer.println("Please **DO NOT** change this code directly.");
        writer.close();
        
        return this;
    }
    
    /**
     * Generate bower json
     *
     * @return BowerPackage
     */
    protected Javascript generateBowerJson()
            throws FileNotFoundException, UnsupportedEncodingException {

        String filename = getOutput() + "bower.json";
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        
        String[] main = new String[]{getNamespace() + ".js"};
        Map<String, String> deps = new HashMap<String, String>();
        deps.put("bbj-js",getBBjSrc());
        
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
     * Generate classes
     *
     * @return BowerPackage
     *
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    protected Javascript generateClasses() throws FileNotFoundException, UnsupportedEncodingException {

        HashMap<String, ParseEntity> classes = getClassesSrc();
        String namespace = getNamespace();
        String outputfolder = getOutput();
        String classfileprefix = getClassFilePrefix();
        String classfilesuffix = getClassFileSuffix();

        Set<String> ks = classes.keySet();
        Iterator<String> itfiles = ks.iterator();

        /**
         * Generate javascript classes
         */
        while (itfiles.hasNext()) {

            String classname = itfiles.next();
            HashSet<String> overloadedMethods = new HashSet<>();
            ParseEntity pe = classes.get(classname);
            String filename = outputfolder + classname + ".js";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");

            writer.println("/*");
            writer.printf("* This file is part of %s Package.\n", namespace);
            writer.println("* (c) Basis Europe <eu@basis.com>");
            writer.println("*");
            writer.println("* For the full copyright and license information, please view the LICENSE");
            writer.println("* file that was distributed with this source code.");
            writer.println("*");
            writer.println("* Code is auto generated using BasisComponents Javascript Generator.");
            writer.println("*/");
            writer.println();

            /**
             * print class wrapper
             */
            writer.printf("(function(%s,BBj){\n\n", namespace)
                    /**
                     * class constrcutor
                     */
                    .printf("%s%s.%s = function(session){\n",getSpaces(2), namespace, classname)
                    .printf("%sthis.session = session;\n", getSpaces(4))
                    .printf("%sthis.id = this.session.guid();\n", getSpaces(4));
            if (pe.getClassname().contains(".")) {
                writer.printf("%sthis.session.create(this.id,'%s');\n", getSpaces(4), pe.getClassname());
            } else {
                writer.printf(
                        "%sthis.session.create(this.id,'::%s%s%s::%s');\n",
                        getSpaces(4),
                        classfileprefix,
                        pe.getClassname(),
                        classfilesuffix,
                        pe.getClassname()
                );
            }
            writer.printf("%s};\n\n",getSpaces(2));
            writer.printf(
                    "%s%s.%s.prototype = {\n\n",
                   getSpaces(2),
                    namespace,
                    classname
            );
            writer.printf(
                    "%sconstructor : %s.%s,\n\n",
                    getSpaces(4),
                    namespace,
                    classname
            );
            
            /**
             * Session wrap methods
             */
            
            writer.printf("%spushVar : function(){\n",getSpaces(4));
            writer.printf("%sthis.session.pushVar.apply(this.session,arguments);\n",getSpaces(6));
            writer.printf("%s},\n\n",getSpaces(4));
            
            writer.printf("%sgetResult : function(){\n",getSpaces(4));
            writer.printf("%sreturn this.session.result;\n",getSpaces(6));
            writer.printf("%s},\n\n",getSpaces(4));
            
            writer.printf("%slog : function(){\n",getSpaces(4));
            writer.printf("%sthis.session.log.apply(this.session,arguments);\n",getSpaces(6));
            writer.printf("%s},\n\n",getSpaces(4));
            
            writer.printf("%spromise : function(){\n",getSpaces(4));
            writer.printf("%sreturn this.session.promise.apply(this.session,arguments);\n",getSpaces(6));
            writer.printf("%s},\n\n",getSpaces(4));
            
            writer.printf("%sexec : function(){\n",getSpaces(4));
            writer.printf("%sreturn this.session.exec.apply(this.session,arguments);\n",getSpaces(6));
            writer.printf("%s},\n\n",getSpaces(4));
            
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
                    overloadedMethods.add(m.getName());

                    // writer.print("/**\n");
                    // writer.printf("%s* \n", getSpaces(1), m.getName())
                    //         .printf("%s* Overloaded method of %s\n", getSpaces(1), m.getName())
                    //        .printf("%s* ", getSpaces(1), m.getName())
                    //         .printf("%s* @private", getSpaces(1))
                    //         .printf("%s*/ ", getSpaces(1));
                    writer.printf(
                            "%s_%s : function(",
                            getSpaces(4),
                            m.getName() + getMethodSignatureString(m)
                    );
                } else {
                    writer.printf(
                            "%s%s : function(",
                            getSpaces(4),
                            m.getName()
                    );
                }

                it2 = m.getParams().iterator();
                Boolean second = false;
                while (it2.hasNext()) {
                    MethodParameter mp = it2.next();
                    if (second) {
                        writer.print(",");
                    } else {
                        second = true;
                    }
                    if (!m.getIsOverloaded()) {
                        writer.print(mp.getName());
                    } else {
                        writer.print(mp.getType() + "_" + mp.getName());
                    }
                }

                writer.println("){");

                // prepare parameters into session
                if (!m.getParams().isEmpty()) {
                    writer.printf("%svar args = [];\n", getSpaces(6));
                }

                it2 = m.getParams().iterator();
                while (it2.hasNext()) {
                    MethodParameter mp = it2.next();
                    if (!m.getIsOverloaded()) {
                        writer.printf("%sargs.push(%s);\n", getSpaces(6), mp.getName());
                    } else {
                        writer.printf(
                                "%sargs.push(%s);\n",
                                getSpaces(6),
                                mp.getType() + "_" + mp.getName()
                        );
                    }
                }

                // create return variable name
                String rv_name;
                if (m.getReturnType().equals("void")) {
                    rv_name = "'void'";
                } else {
                    rv_name = "'r_" + classname + "_" + m.getName() + "_retvar'";
                }

                // now invoke
                writer.printf(
                        "%sthis.session.invoke(this.id,%s,'%s'",
                        getSpaces(6),
                        rv_name,
                        m.getName()
                );
                if (!m.getParams().isEmpty()) {
                    writer.print(",args");
                }
                writer.println(");");

                // return value
                String tmp = m.getReturnType();
                if (!tmp.equals("void")) {
                    writer.printf("%sthis.session.pushRet(%s);\n", getSpaces(6), rv_name);
                };

                writer.printf("%s},\n\n", getSpaces(4));
            }

            //now generate the parent function to dispatch all the overloaded versions
            Iterator<String> it = overloadedMethods.iterator();
            while (it.hasNext()) {
                String methodname = it.next();
                writer.printf(
                        "%s%s = function(){",
                       getSpaces(2),
                        methodname
                );

                Iterator<Method> itm = pe.getMethods().iterator();
                while (itm.hasNext()) {
                    Method m = itm.next();

                    if (m.getName().equals(methodname)) {
                        Integer i = 0;
                        writer.printf("%sif(\n", getSpaces(4));

                        Iterator<MethodParameter> it2 = m.getParams().iterator();
                        while (it2.hasNext()) {
                            MethodParameter mp = it2.next();
                            writer.printf(
                                    "%sthis.session.getTypeof(arguments[%s]) == '",
                                    getSpaces(6),
                                    i.toString()
                            );
                            switch (mp.getType()) {
                                case "String":
                                    writer.print("str");
                                    break;
                                case "Number":
                                    writer.print("num");
                                    break;
                                case "Boolean":
                                    writer.print("bool");
                                    break;
                                default:
                                    writer.print("dr");
                                    break;
                            }
                            writer.printf("' &&\n%s", getSpaces(8));
                            i++;
                        }
                        writer.printf(
                                "%stypeof(arguments[%s]) == 'undefined'){",
                                getSpaces(6),
                                i.toString()
                        );

                        writer.printf(
                                "%sthis._%s.apply(this,arguments);",
                                getSpaces(6),
                                methodname + getMethodSignatureString(m)
                        );
                        writer.printf("%s};", getSpaces(6));
                    }
                }
                writer.printf("%s},\n", getSpaces(4));
            }

            // end of prototype
            writer.printf("%s};\n\n",getSpaces(2));

            // end class wrapper
            writer.printf(
                    "})(window.%s = window.%s || {},BBj);\n\n\n",
                    namespace,
                    namespace
            );
            writer.close();
        }

        return this;
    }
}
