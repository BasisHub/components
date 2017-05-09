In a context in <BBjHome>/cfg/jetty.xml:

<bbjsp-servlet class="RestBridge" config="/path/to/config.bbx" mapping="/rest/*" source="/path/to/RestBridge.bbj"/>


In the config.bbx referenced above:

#BBJSP RESTful bridge settings

#required working dir for finding the BC classes, e.g. D:\beu\ChileCompany
SET REST_WD=

#required adapter program, e.g. D:\beu\components\bridge\RestBCAdapter.bbj
SET REST_ADAPTERPGM=bridge/RestBCAdapter.bbj

#optional terminal alias (defaults to current terminal alias)
#SET REST_ADAPTERTERM=

#prefix/suffix to use in eval("new ::"+prefix$+<name from URI>+"BC"+suffix$+"::"+<name from URI>+"BC()")
SET REST_PGM_PREFIX=
SET REST_PGM_SUFFIX=.bbj

#optional timeout (default 60 secs)
#used in requestSemaphore!.tryAcquire(1,timeout,java.util.concurrent.TimeUnit.SECONDS)
#SET REST_TIMEOUT=60

#DEBUG 0=no, 1=sysout, 2=trace (currently not used in RESTful programs; used in components/bridge/Bridge.bbj)
#SET DEBUG=0 
