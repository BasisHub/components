package com.basiscomponents;

import java.sql.Timestamp;

/**
 * Provides static methods and member variables to retrieve the BBj build information and this project's 
 * build information(time,date, etc...).
 */
public class VersionInfo{

    public static final String BBJ_BUILD_ID = "1516661628";
    public static final String BBJ_BUILD_NAME = "${BBjBuildName}";
    public static final String BBJ_BUILD_REVISION = "REV 17.12";
    public static final String BBJ_BUILD_TIMESTAMP_STRING = "01/22/2018 15:53";

    public static final long COMPONENTS_BUILD_TIME_MILLIS = new Long("1521019269637");
    public static final Timestamp COMPONENTS_BUILD_TIMESTAMP = new Timestamp(COMPONENTS_BUILD_TIME_MILLIS);
    public static final String COMPONENTS_BUILD_TIMESTAMP_STRING = "14-03-2018 10:21:09";

    /**
     * Returns the ID of the BBj Build which has been used to build 
     * this project's jar.
     * 
     * @return bbjBuildID The BBj build's ID
     */
    public static String getBBjBuildID(){
        return BBJ_BUILD_ID;
    }

    /**
     * Returns the name of the BBj Version which has been used to build
     * this project's jar.
     * 
     * @return name The BBj build's name
     */
    public static String getBBjBuildName(){
        return BBJ_BUILD_NAME;
    }

    /**
     * Returns the Revision of the BBj Build which has been used to build 
     * this project's jar.
     * 
     * @return revision The BBj build's revision.
     */
    public static String getBBjBuildRevision(){
        return BBJ_BUILD_REVISION;
    }

    /**
     * Returns the timestamp of the BBj Build which has been used to build 
     * this project's jar as String object.
     * 
     * @return timestamp The BBj build's timestamp.
     */
    public static String getBBjBuildTimestampString(){
        return BBJ_BUILD_TIMESTAMP_STRING;
    }

    /**
     * Returns this project's build time in milliseconds as String object.
     * 
     * @return buildTime This project's build time in milliseconds.
     */
    public static long getComponentsBuildTimeMillis(){
        return COMPONENTS_BUILD_TIME_MILLIS;
    }

    /**
     * Returns this project's build Timestamp.
     * 
     * @return timestamp This project's build timestamp.
     */
    public static Timestamp getComponentsBuildTimestamp(){
        return COMPONENTS_BUILD_TIMESTAMP;
    }

    /**
     * Returns this project's build Timestamp as String object.
     * 
     * @return timestamp This project's build timestamp.
     */
    public static String getComponentsBuildTimestampString(){
        return COMPONENTS_BUILD_TIMESTAMP_STRING;
    }
    
    /**
     * Returns a String object with all the build information.
     * 
     * @return buildInfo A String with the build information.
     */
    public static String getBuildInfoString(){
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("BBj Build ID: " + BBJ_BUILD_ID + "\n");
    	sb.append("BBj Build Name: " + BBJ_BUILD_NAME + "\n");
    	sb.append("BBj Build Revision: " + BBJ_BUILD_REVISION + "\n");
    	sb.append("BBj Build Timestamp: " + BBJ_BUILD_TIMESTAMP_STRING + "\n");
    	sb.append("\n");
    	sb.append("Components Build Time Millis: " + COMPONENTS_BUILD_TIME_MILLIS + "\n");
    	sb.append("Components Build Timestamp: " + COMPONENTS_BUILD_TIMESTAMP);
    	
    	return sb.toString();
    }
}
