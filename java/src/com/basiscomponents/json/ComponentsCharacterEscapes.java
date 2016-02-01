package com.basiscomponents.json;

import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

public class ComponentsCharacterEscapes extends CharacterEscapes
{
    private static final long serialVersionUID = 1L;
    private final int[] asciiEscapes;

    public ComponentsCharacterEscapes()
    {
        // start with set of characters known to require escaping (double-quote, backslash etc)
        int[] esc = new int[256];
        int[] stdesc = CharacterEscapes.standardAsciiEscapesForJSON();
        for (int i=0; i< stdesc.length-1; i++){
        	esc[i]=stdesc[i];
        }
        for (int i=stdesc.length; i<esc.length-1; i++){
        	esc[i]=CharacterEscapes.ESCAPE_STANDARD;;
        }

        // and force escaping of a few others:
        esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
        esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
        esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
        esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes = esc;

    }
    // this method gets called for character codes 0 - 127
    @Override public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }
    // and this for others; we don't need anything special here
    @Override public SerializableString getEscapeSequence(int ch) {
    	String s = new String();
    	s+=(char) ch;
    	return  new SerializedString(StringEscapeUtils.escapeJavaScript(s));
        //return null;
    }
}