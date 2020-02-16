components
==========

The BASIS Components project, a Java library that is meant to build a bridge between a Java environment - like JDBC data sources, REST web services etc - and BBj.

building
--------
To set up the components project in your IDE, you will need some dependencies. The simplest way is to grab a current build ob BBj and use the lib folder:

1. download BBj here: http://www.basis.com/bbj-download (the latest development build will be fine for the master branch, or the according  release matching the branch name in this repository)
2. install it to C:/bbj/ or /opt/bbj on linux
3. Import the Project as Mavan Project. select the Maven profile you want to use (winbbjlib/unixbbjlib) if your bbj installation is on a different part change the pom.xml profile, but make sure not to commit this change.
5. Use https://github.com/BasisHub/components/issues to report and track your issues. We're looking forward to your pull requests!

conventions
--------
1. From now on commits should be written as [conventional commits](https://www.conventionalcommits.org/en/v1.0.0-beta.2/)
2. The branching should be done via [git flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

JavaDoc
-------
https://basishub.github.io/components/javadoc/
