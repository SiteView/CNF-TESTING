/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.util.Date;

import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Log:
// BaseFileLogger, FileWithPath, LogManager

public class FileLogger extends COM.dragonflow.Log.BaseFileLogger {

    static final String BUFFER_DURATION = "_logFileBufferDuration";

    static final long DEFAULT_BUFFER_DURATION = 0L;

    static final String MAX_BUFFER_SIZE = "_logFileMaxBufferSize";

    static final long DEFAULT_MAX_BUFFER_SIZE = 0L;

    java.io.File file;

    boolean writeDate;

    private COM.dragonflow.Log.FileWithPath currentFile;

    public FileLogger(String s, int i) throws java.io.IOException {
        this(s, i, 0);
    }

    public FileLogger(String s, int i, int j) throws java.io.IOException {
        this(s, i, j, 0L, 0);
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        bufferDuration = COM.dragonflow.Log.FileLogger.getLongSetting(hashmap, "_logFileBufferDuration", 0L);
        initBuffer((int) COM.dragonflow.Log.FileLogger.getLongSetting(hashmap, "_logFileMaxBufferSize", 0L));
    }

    public FileLogger(String s, int i, int j, long l, int k) throws java.io.IOException {
        super(l, k);
        writeDate = true;
        currentFile = new FileWithPath();
        file = new File(s);
        if (file.length() > (long) i) {
            java.io.File file1 = new File(file.getAbsolutePath() + ".old");
            file1.delete();
            if (!file.renameTo(file1)) {
                java.lang.System.out.println("RENAME ERROR:" + file.getAbsolutePath() + ", " + file1.getAbsolutePath());
            } else if (j > 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "Starting to roll log file: " + file.getName());
                java.util.Date date = new Date(COM.dragonflow.SiteView.Platform.timeMillis() - (long) (j * 24 * 60 * 60 * 1000));
                java.util.Date date1 = new Date(date.getYear(), date.getMonth(), date.getDate());
                long l1 = COM.dragonflow.Utils.FileUtils.findOffsetForDate(file1, date1);
                java.io.File file2 = new File(file.getAbsolutePath() + ".temp");
                if (!COM.dragonflow.Utils.FileUtils.copyFile(file1, file, l1)) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "COPY ERROR:" + file1.getAbsolutePath() + ", " + file.getAbsolutePath());
                }
                if (!COM.dragonflow.Utils.FileUtils.copyFile(file1, file2, 0L, l1)) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "COPY ERROR:" + file1.getAbsolutePath() + ", " + file2.getAbsolutePath());
                } else {
                    file1.delete();
                    if (!file2.renameTo(file1)) {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "RENAME ERROR:" + file2.getAbsolutePath() + ", " + file1.getAbsolutePath());
                    }
                }
                COM.dragonflow.Log.LogManager.log("RunMonitor", "Completed roll of " + file.getName());
            }
        }
    }

    public void flush(java.lang.Object obj) {
        String s = ((StringBuffer) obj).toString();
        if (s.length() <= 0) {
            return;
        }
        Object obj1 = null;
        try {
            String s1 = file.getPath();
            java.io.OutputStreamWriter outputstreamwriter = currentFile.getFile(s1, true);
            outputstreamwriter.write(s, 0, s.length());
            outputstreamwriter.flush();
        } catch (java.io.IOException ioexception) {
            java.lang.System.err.println("Could not open log file " + file + " Exception: " + ioexception.getMessage());
            ioexception.printStackTrace();
        }
    }

    public void log(String s, java.util.Date date, String s1) {
        StringBuffer stringbuffer = new StringBuffer();
        if (writeDate) {
            stringbuffer.append(COM.dragonflow.Log.FileLogger.dateToString(date)).append(FIELD_SEPARATOR);
        }
        stringbuffer.append(s1).append(COM.dragonflow.SiteView.Platform.FILE_NEWLINE);
        addToFileBuffer(stringbuffer);
    }

    public void setWriteDate(boolean flag) {
        writeDate = flag;
    }
}
