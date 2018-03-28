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
        int[] esc = new int[128];
        int[] stdesc = CharacterEscapes.standardAsciiEscapesForJSON();
        for (int i=0; i< stdesc.length-1; i++){
        	esc[i]=stdesc[i];
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
    	if (ch==172){
    		return new SerializedString("\\u20AC"); 
    	}
		String s = "";
    	s+=(char) ch;
    	return  new SerializedString(StringEscapeUtils.escapeJavaScript(s));
    }
}
