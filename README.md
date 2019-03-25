components
==========

The BASIS Components project, a Java library that is meant to build a bridge between a Java environment - like JDBC data sources, REST web services etc - and BBj.

building
--------
To set up the components project in your IDE, you will need some dependencies. The simplest way is to grab a current build ob BBj and use the lib folder:

1. download BBj here: http://www.basis.com/bbj-download (the latest development build will be fine for the master branch, or the according  release matching the branch name in this repository)

2. create a Java project (most of us use Eclipse). The Java source root is in src\main\java\ of this repo.

3. Add the following external JAR dependencies from the <bbj>/lib folder:
- all poi-*.jar files 
- BBj.jar
- BBjUtil.jar
- BBjThinClient.jar
- BBjFilesystem.jar
- netty-all-xxx.jar
- all commons-*.jar
- gson-xxx.jar
- jackson-all-xxx.jar
- jasperreports-javaflow-xxx.jar
- lucene-*.jar (caution - BBj 19 ships 5 and 7)

4. Use https://github.com/BasisHub/components/issues to report and track your issues. We're looking forward to your pull requests!

