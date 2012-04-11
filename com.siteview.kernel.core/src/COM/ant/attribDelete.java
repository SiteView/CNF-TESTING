// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-2-22 15:13:39
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package COM.ant;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;

public class attribDelete extends Task
{

    public attribDelete()
    {
        filesets = new Vector();
        includeEmpty = false;
        verbosity = 3;
        quiet = false;
        failonerror = true;
    }

    public void addFileset(FileSet fileset)
    {
        filesets.addElement(fileset);
    }

    public void setMessage(String s)
    {
        msg = s;
    }

    public void addTask(Task task)
    {
    }

    public void execute()
        throws BuildException
    {
        for(int i = 0; i < filesets.size();i++)/*dingbing.xu add i++*/
        {
            FileSet fileset = (FileSet)filesets.elementAt(i);
            try
            {
                DirectoryScanner directoryscanner = fileset.getDirectoryScanner(project);
                String as[] = directoryscanner.getIncludedFiles();
                String as1[] = directoryscanner.getIncludedDirectories();
                removeFiles(fileset.getDir(project), as, as1);
                continue;
            }
            catch(BuildException buildexception)
            {
                if(failonerror)
                    throw buildexception;
                log(buildexception.getMessage(), quiet ? 3 : 1);
                //i++;  /*dingbing.xu delete i++*/
            }
        }

    }

    protected void removeFiles(File file, String as[], String as1[])
    {
        boolean flag = false;
        if(as.length > 0)
        {
            log("Deleting " + as.length + " files from " + file.getAbsolutePath());
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(file, as[i]);
                boolean flag1 = false;
                if(file1.getPath().indexOf("freshtech") >= 0)
                    flag1 = true;
                if(file1.canWrite() || flag1)
                {
                    log("Deleting " + file1.getPath() + file1.getName(), verbosity);
                    if(file1.delete())
                        continue;
                    String s = "Unable to delete file " + file1.getAbsolutePath();
                    if(failonerror)
                        throw new BuildException(s);
                    log(s, quiet ? 3 : 1);
                } else
                {
                    log("Not Deleting: " + file1.getPath() + file1.getName(), verbosity);
                }
            }

        }
        if(as1.length > 0 && includeEmpty)
        {
            int j = 0;
            for(int k = as1.length - 1; k >= 0; k--)
            {
                File file2 = new File(file, as1[k]);
                String as2[] = file2.list();
                if(as2 != null && as2.length != 0)
                    continue;
                log("Deleting " + file2.getAbsolutePath(), verbosity);
                if(!file2.delete())
                {
                    String s1 = "Unable to delete directory " + file2.getAbsolutePath();
                    if(failonerror)
                        throw new BuildException(s1);
                    log(s1, quiet ? 3 : 1);
                } else
                {
                    j++;
                }
            }

            if(j > 0)
                log("Deleted " + j + " director" + (j != 1 ? "ies" : "y") + " from " + file.getAbsolutePath());
        }
    }

    private String msg;
    protected Vector filesets;
    protected boolean includeEmpty;
    private int verbosity;
    private boolean quiet;
    private boolean failonerror;
}