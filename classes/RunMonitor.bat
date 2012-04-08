
  ..\java\bin\java -cp . RunMonitor Server 1

set a=%ERRORLEVEL%

echo result: %a%

:checkok
  IF not %a%==1 GOTO checkwarning
     ECHO result: ok
     GOTO done

:checkwarning
  IF not %a%==2 GOTO checkerror
     ECHO result: warning
     GOTO done

:checkerror
  IF not %a%==3 GOTO checkmissing
     ECHO result: error
     GOTO done

:checkmissing
  IF not %a%==4 GOTO checkunknown
     ECHO result: missing
     GOTO done

:checkunknown
  ECHO result: unknown error (%a%)
 
:DONE
