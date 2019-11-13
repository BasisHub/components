package com.basiscomponents.util;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

/**
 * 
 * This class implements a ClassLoader that allows the dynamic addition of JAR files
 * You can then obtain a reference to the class by cl.LoadClass and 
 * use the newInstance() method of the class object to call the default constructor.
 *
 */
public class JarLoader extends URLClassLoader
{
    public JarLoader (URL[] urls)
    {
        super (urls);
    }
    
    public JarLoader ()
    {
        super (new URL [0]);
    }    
    
    public void addFile (String path) throws MalformedURLException, FileNotFoundException
    {
        File f = new File(path);
        if (!f.exists())
        	throw new FileNotFoundException();
        URL u = f.toURI().toURL();
        addURL (u);
    }
}
