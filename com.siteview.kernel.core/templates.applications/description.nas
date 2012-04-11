nas	A private mib module for Netscape Application Server
netscape	netscape
nasKesEngConnRetries	The maximum number of times the administration  server will try to connect to ean engine
nasKesEngMaxRestart	The maximum number of times the administration server  will restart an engine after a failure
nasKesEngAutoStart	Start all the engines at startup of the administration   server
nasKesConfigHeartBeat	null
kesTable	The table of all the KES running on the host
kesEntry	The kes definition
nasKesId	The id of the KES this engine belongs to
nasKesMinThread	The default minimum number of threads per engine
nasKesMaxThread	The default maximum number of threads per engine
nasKesLoadBalancerDisable	Enable or Disable the load balancer service
nasKesCpuLoad	The total cpu usage on this host
nasKesDiskLoad	The total disk usage on this host
nasKesMemLoad	The total memory usage on this host
nasKesRequestLoad	The number of requests on this NAS
nasKesCpuLoadFactor	The relative importance of CPU usage in            computing the server load. This number																
nasKesDiskLoadFactor	The relative importance of Disk usage in            computing the server load. This number																
nasKesMemLoadFactor	The relative importance of Memory usage in            computing the server load. This number																
nasKesAppLogicsRunningFactor	The relative importance of the number of            times an AppLogic is run in computing the																
nasKesResultsCachedFactor	The relative importance of the cached            results of an AppLogic in computing																
nasKesAvgExecTimeFactor	The relative importance of the average            execution time of an AppLogic in computing																
nasKesLastExecTimeFactor	The relative importance of the last execution            time of an AppLogic in computing the																
nasKesHitsFactor	The relative importance of the number of            AppLogics running in computing the																
nasKesServerLoadFactor	The relative importance of the server            load (computed using the four server																
nasKesBroadcastInterval	"The length of time in seconds, between each            broadcast attempt from the load balancer"																
nasKesApplogicBroadcastInterval	"The length of time in seconds, between each            broadcast of AppLogics load information"																
nasKesServerBroadcastInterval	"The length of time in seconds, between each            broadcast of server load information acc"																
nasKesServerLoadUpdateInterval	The length of time in seconds between each             update of server load informations. A se																
nasKesCpuLoadUpdateInterval	"The length of time, in seconds, between each             sampling of CPU usage."																
nasKesDiskLoadUpdateInterval	"The length of time, in seconds, between each             sampling of disk usage."																
nasKesMemLoadUpdateInterval	"The length of time, in seconds, between each             sampling of memory thrashes."																
nasKesTotalReqsUpdateInterval	"The length of time, in seconds, between each             sampling of the number of requests."					
nasKesMaxHops	The maximum number of times a request can            be load-balanced to another server.					
nasKesODBCReqMinThread	The minimum number of threads reserved to process asynchronous requests					
nasKesODBCReqMaxThread	The maximum number of threads reserved to process asynchronous requests					
nasKesODBCCacheMaxConns	The maximum number of connections opened between NAS and the database					
nasKesODBCCacheFreeSlots	The minimum number of cached connections established between NAS and the database					
nasKesODBCCacheTimeout	The time after which an idle connection is dropped					
nasKesODBCCacheInterval	The interval in seconds at which the cahe cleaner will try to disconnect            connections		ady idle for longer than the specifie	d tim	eou	t
nasKesODBCConnGiveupTime	Maximum time the driver will try to connect to the database					
nasKesODBCCacheDebug	Turns on the connection cache debug information					
nasKesODBCResultSetInitRows	The number of rows fetched at once from the database					
nasKesODBCResultSetMaxRows	The maximum number of rows the cached result set can contain					
nasKesODBCResultSetMaxSize	The maximum size of result set the driver will cache					
nasKesODBCSqlDebug	Turns on sql debug information					
nasKesODBCEnableParser	Turns on SQL parsing					
nasKesORCLReqMinThread	The minimum number of threads reserved to process asynchronous requests					
nasKesORCLReqMaxThread	The maximum number of threads reserved to process asynchronous requests					
nasKesORCLCacheMaxConns	The maximum number of connections opened between NAS and the database					
nasKesORCLCacheFreeSlots	The minimum number of cached connections established between NAS and the database					
nasKesORCLCacheTimeout	The time after which an idle connection is dropped					
nasKesORCLCacheInterval	The interval in seconds at which the cahe cleaner will try to disconnect            connections		ady idle for longer than the specifie	d tim	eou	t
nasKesORCLConnGiveupTime	The maximum time the driver will spend trying to obtain a connection to Oracle					
nasKesORCLCacheDebug	Turns on the connection cache debug information					
nasKesORCLResultSetInitRows	The number of rows fetched at once from the database					
nasKesORCLResultSetMaxRows	The maximum number of rows the cached result set can contain					
nasKesORCLResultSetMaxSize	The maximum size of result set the driver will cache					
nasKesORCLSqlDebug	Turns on sql debug information					
nasKesSYBReqMinThread	The minimum number of threads reserved to process asynchronous requests					
nasKesSYBReqMaxThread	The maximum number of threads reserved to process asynchronous request					
nasKesSYBCacheMaxConns	The maximum number of connections opened between NAS and the database					
nasKesSYBCacheFreeSlots	The minimum number of cached connections established between NAS and the database					
nasKesSYBCacheTimeout	The time after which an idle connection is dropped					
nasKesSYBCacheInterval							
nasKesSYBConnGiveupTime	The maximum time the driver will spend trying to btain a connection             to Sybase befor		ing up				
nasKesSYBCacheDebug	Turns on the connection cache debug information						
nasKesSYBResultSetInitRows	The number of rows fetched at once from the database						
nasKesSYBResultSetMaxRows	The maximum number of rows the cached result set can contain						
nasKesSYBResultSetMaxSize	The maximum size of result set the driver will cache						
engineTable	The table of all the running engines						
engineEntry	The description of an engine and all the statistics available            on it.						
nasEngKesPort	The port of the KXS this engine serves. This is supplied as part of the             object id a		nnot be modified after creation				
nasEngPort	The TCP/IP port this engine is listening on. The port can only            be specified at the c		on of the engine. It is not allowed			t	o modify it.
nasEngType	"Type of the engine, executive(0), java(1000), c++(3000)"						
nasEngId	The id is an incremental number starting at 0. The id cannot be modified.						
nasEngName	"The name of this engine. This is an informational string that contains kcs, kxs ot kjs"						
nasEngNewConsole	Starts each engine in a new console window						
nasEngStatus	"The status column used to add, remove, enable or disable an engine.            To create an eng"		one needs to set             This fol	lows	rfc	1443	.
nasEngMinThread	The default minimum number of threads per engine						
nasEngMaxThread	The default maximum number of threads per engine
nasEngReqRate	The rate at which requests arrive
nasEngTotalReq	The total number of requests processed since engine startup
nasEngReqNow	The number of requests being processed
nasEngReqWait	The requests waiting to be serviced
nasEngReqReady	The requests that are ready to be serviced
nasEngAvgReqTime	The average request processing time
nasEngThreadNow	Number of threads in use by the request manager
nasEngThreadWait	The number of idle threads
nasEngWebReqQueue	The number of web requests that are queued
nasEngFailedReq	The number of requests that failed
nasEngTotalConn	The total number of connections opened
nasEngTotalConnNow	The total number of connections in use
nasEngTotalAccept	The total number of connections listening to incoming requests
nasEngTotalAcceptNow	The total number of connections listening to incoming connections in use
nasEngTotalSent	The total number of packets sent
nasEngTotalSentBytes	The total number of bytes sent
nasEngTotalRecv	The total number of packets received
nasEngTotalRecvBytes	The total number of bytes received
nasEngBindTotal	The number of AppLogic bound since startup
nasEngBindTotalCached	The number of AppLogic cached since startup
nasEngTotalThreads	Total number of threads created in this process
nasEngCurrentThreads	Total number of threads in use in this process
nasEngSleepingThreads	Number of threads sleeping in this process
nasEngDAETotalQuery	Total number of queries executed ssince startup
nasEngDAEQueryNow	The number of queries being processed
nasEngDAETotalConn	The number of logical connections created since startup
nasEngDAEConnNow	The number of logical connections in use
nasEngDAECacheCount	The number of caches
nasEngODBCQueryTotal	Total number of queries executed since startup
nasEngODBCPreparedQueryTotal	Total number of odbc prepared queries executed since startup
nasEngODBCConnTotal	Total number of connections opened since startup
nasEngODBCConnNow	Number of connections currently opened
nasEngORCLQueryTotal	total number of queries executed since startup
nasEngORCLPreparedQueryTotal	Total number of prepared queries executed since startup
nasEngORCLConnTotal	Total number of connections established with Oracle since startup
nasEngORCLConnNow	Number of connections opened with Oracle now
nasEngSYBQueryTotal	Total number of queries teh driver processed since startup
nasEngSYBPreparedQueryTotal	Total number of prepared queries proecssed since startup
nasEngSYBConnTotal	Total number of connections opened since startup
nasEngSYBConnNow	Number of SYB connections opened now
nasStatusTrapTable	null
nasStatusTrapEntry	The kes definition
nasTrapKesIpAddress	The IP Address of KES host
nasTrapKesPort	The port of the main engine of this NAS
nasTrapEngPort	The port of the engine generating this event
nasTrapEngState	The port of the engine generating this event
