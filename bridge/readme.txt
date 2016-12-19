in jetty.xml
<bbjsp-servlet class="RestBridge" config="/path/to/config.bbx" mapping="/rest/*" source="/path/to/RestBridge.bbj"/>

in the config.bbx:



#bbjsp restful bridge settings
#working dir
SET REST_WD=D:/appl/STD/DEV/LIVE/PGM/

# adapter program
SET REST_ADAPTERPGM=bridge/RestBCAdapter.bbj

#timeout
SET REST_TIMEOUT=60

#DEBUG 0=no, 1=sysout, 2=trace
SET DEBUG=0 
