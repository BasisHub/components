/*
 * This file is part of BasisComponents Package.
 * (c) Basis Europe <eu@basis.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.basiscomponents.bridge.proxygen.Javascript;

import com.basiscomponents.bridge.proxygen.ParseEntity;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * AbstractGenerator
 * 
 * AbstractGenerator abstract class
 *
 * @author Hyyan Abo Fakher &lt;habofakher@basis.com&gt;
 */
abstract public class AbstractGenerator {

    public static final String AllowedTypes = "DataRow ResultSet BBjNumber BBjString Boolean Integer Double void";
    private String namespace;
    private HashMap<String, ParseEntity> classes;
    private String output;
    private String prefix;
    private String suffix;
    private String bbjSrc;
    private String version;

    /**
     * Constructor
     *
     * @param bbjSrc
     */
    public AbstractGenerator(String bbjSrc) {
        this.setBBjSrc(bbjSrc);
    }
    
    /** 
     * Set Version
     * 
     * @param version
     * @return AbstractGenerator
     */
    public AbstractGenerator setVersion(String version){
        this.version = version;
        return this;
    }
    
    /**
     * Get version
     * 
     * @return  version
     */
    public String getVersion(){
        return this.version;
    }
    
    /**
     * Set Namespace
     *
     * @param namespace
     *
     * @return BowerPackage
     */
    public AbstractGenerator setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Get namespace
     *
     * @return namespace
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Set Classes source
     *
     * @param classes
     *
     * @return BowerPackage
     */
    public AbstractGenerator setClassesSrc(HashMap<String, ParseEntity> classes) {
        this.classes = classes;
        return this;
    }

    /**
     * Get getClassesSrc
     *
     * @return classes
     */
    public HashMap<String, ParseEntity> getClassesSrc() {
        return this.classes;
    }

    /**
     * Set output
     *
     * @param output
     *
     * @return BowerPackage
     */
    public AbstractGenerator setOutput(String output) {
        this.output = output;
        return this;
    }

    /**
     * Get output
     *
     * @return output
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Set class file prefix
     *
     * @param prefix
     *
     * @return BowerPackage
     */
    public AbstractGenerator setClassFilePrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Get class file prefix
     *
     * @return prefix
     */
    public String getClassFilePrefix() {
        return this.prefix;
    }

    /**
     * Set class file suffix
     *
     * @param suffix
     *
     * @return BowerPackage
     */
    public AbstractGenerator setClassFileSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    /**
     * Get class file suffix
     *
     * @return prefix
     */
    public String getClassFileSuffix() {
        return this.suffix;
    }

    /**
     * Set bbj source
     *
     * @param bbjSrc
     *
     * @return BowerPackage
     */
    public AbstractGenerator setBBjSrc(String bbjSrc) {
        this.bbjSrc = bbjSrc;
        return this;
    }

    /**
     * Get BBj Src
     *
     * @return bbjSrc
     */
    public String getBBjSrc() {
        return this.bbjSrc;
    }

    /**
     * Generate package
     *
     */
    public abstract void generate()
            throws FileNotFoundException, UnsupportedEncodingException, IOException;

}
