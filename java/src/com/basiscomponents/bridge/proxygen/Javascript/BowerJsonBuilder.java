/*
 * This file is part of BasisComponents Package.
 * (c) Basis Europe <eu@basis.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.basiscomponents.bridge.proxygen.Javascript;

import static com.basiscomponents.bridge.proxygen.Javascript.Utilities.getSpaces;
import java.util.Formatter;
import java.util.Map;

/**
 * BowerJsonBuilder

 Generator Bower Json File
 *
 * @author Hyyan Abo Fakher <habofakher@basis.com>
 */
public class BowerJsonBuilder {

    private String name;
    private Boolean isPrivate;
    private String[] main;
    private Map<String, String> dependencies;

    /**
     * Set name
     *
     * @param name
     * @return
     */
    public BowerJsonBuilder setName(String name) {
        this.name = name.toLowerCase();
        return this;
    }

    /**
     * Get name
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set Private
     *
     * @param isPrivate
     * @return BowerJsonBuilder
     */
    public BowerJsonBuilder setPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    /**
     * Get Private
     *
     * @return Boolean
     */
    public Boolean IsPrivate() {
        return this.isPrivate;
    }

    /**
     * Set main
     *
     * @param main
     * @return BowerJsonBuilder
     */
    public BowerJsonBuilder setMain(String[] main) {
        this.main = main;
        return this;
    }

    /**
     * Get name
     *
     * @return
     */
    public String[] getMain() {
        return this.main;
    }

    /**
     * Set dependencies
     *
     * @param dependencies
     * @return BowerJsonBuilder
     */
    public BowerJsonBuilder setDependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    /**
     * Get dependencies
     *
     * @return
     */
    public Map<String, String> getDependencies() {
        return this.dependencies;
    }
    
    /**
     * Generate 
     * 
     * Generate the json file
     * 
     * @return String
     */
    public String generate() {

        Formatter formatter = new Formatter(new StringBuilder());

        formatter.format("{\n");
        formatter.format("%s\"name\":\"%s\",\n", getSpaces(2), getName());
        formatter.format("%s\"private\":\"%s\",\n", getSpaces(2), IsPrivate());
        formatter.format("%s\"main\":[\n", getSpaces(2));

        String mains[] = getMain();
        for (String file : mains) {
            formatter.format("%s\"%s\"\n", getSpaces(4), file);
        }
        formatter.format("%s],\n", getSpaces(2));
        formatter.format("%s\"dependencies\":{\n",getSpaces(2));
        
        Map<String, String> deps = getDependencies();
        int count = 0;
        for (Map.Entry<String, String> entry : deps.entrySet()) {
            
            if(count == deps.size()){
                formatter.format(
                        "%s\"%s\":\"%s\",\n",
                        getSpaces(4),
                        entry.getKey(),
                        entry.getValue()
                );
            }else{
                formatter.format(
                        "%s\"%s\":\"%s\"\n",
                        getSpaces(4),
                        entry.getKey(),
                        entry.getValue()
                );
            }
            
            count++;
        }
        
        formatter.format("%s}\n",getSpaces(2));
        formatter.format("}");

        return formatter.toString();
    }

}
