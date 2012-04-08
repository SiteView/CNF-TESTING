/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import sun.audio.AudioStream;
import COM.dragonflow.Properties.ScalarProperty;

public class Sound extends COM.dragonflow.SiteView.Action
{

    public static COM.dragonflow.Properties.StringProperty pSound;
    protected static int audioSleepTime;

    public Sound()
    {
    }

    public void initializeFromArguments(jgl.Array array, jgl.HashMap hashmap)
    {
        if(array.size() > 0)
        {
            setProperty(pSound, array.at(0));
        }
    }

    public String getActionString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Sound");
        if(getProperty(pSound).length() > 0)
        {
            stringbuffer.append(" " + getProperty(pSound));
        }
        return stringbuffer.toString();
    }

    public String getActionDescription()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String)getClassProperty("label"));
        if(getProperty(pSound).length() > 0)
        {
            stringbuffer.append(" \"" + getProperty(pSound) + "\"");
        }
        return stringbuffer.toString();
    }

    public boolean defaultsAreSet(jgl.HashMap hashmap)
    {
        return true;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap)
    {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest, COM.dragonflow.Page.CGI cgi)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        if(scalarproperty == pSound)
        {
            java.io.File file = new File(COM.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.sound", httprequest.getAccount()));
            java.util.Vector vector = new Vector();
            String as[] = file.list();
            if(as != null)
            {
                for(int i = 0; i < as.length; i++)
                {
                    String s = as[i];
                    if(s.endsWith(".au"))
                    {
                        s = s.substring(0, s.length() - 3);
                    }
                    vector.addElement(s);
                    vector.addElement(s);
                }

            }
            return vector;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public boolean execute()
    {
        boolean flag = false;
        String s = "";
        String s1 = "Default";
        if(args.length > 0)
        {
            s1 = args[0];
        }
        try
        {
            String s2 = COM.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.sound", monitor.getProperty(COM.dragonflow.SiteView.Monitor.pGroupID)) + java.io.File.separator + s1;
            java.io.File file = new File(s2);
            if(!file.exists())
            {
                file = new File(s2 + ".au");
            }
            java.io.FileInputStream fileinputstream = new FileInputStream(file);
            sun.audio.AudioStream audiostream = new AudioStream(fileinputstream);
            sun.audio.AudioPlayer.player.start(audiostream);
            flag = true;
            s = s1;
            if(audioSleepTime > 0)
            {
                java.lang.Thread.sleep(audioSleepTime);
                sun.audio.AudioPlayer.player.stop();
            }
        }
        catch(java.lang.Exception exception)
        {
            s = exception.toString();
            if(s.equals("Unknown error"))
            {
                flag = true;
            }
        }
        String s3 = "Sound alert played";
        if(!flag)
        {
            s3 = "SOUND ALERT NOT PLAYED";
        }
        messageBuffer.append(s3 + ", " + s);
        logAlert(baseAlertLogEntry(s3, s, flag) + " alert-sound: " + s1 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE);
        return flag;
    }

    public static void main(String args[])
    {
        java.lang.System.out.println("\n-------------------------");
        java.lang.System.out.println("  Testing Sound Actions");
        java.lang.System.out.println("-------------------------\n");
        COM.dragonflow.StandardAction.Sound sound = new Sound();
        sound.args = new String[0];
        sound.messageBuffer = new StringBuffer();
        sound.execute();
        try
        {
            java.lang.Thread.sleep(1000L);
        }
        catch(java.lang.InterruptedException interruptedexception) { }
        java.lang.System.out.println("\n--------");
        java.lang.System.out.println("  done");
        java.lang.System.out.println("--------\n");
    }

    static 
    {
        pSound = new ScalarProperty("_sound", "Default");
        pSound.setDisplayText("Sound File", "the sound to be played (from templates.sound directory)");
        pSound.setParameterOptions(true, 1, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = {
            pSound
        };
        COM.dragonflow.StandardAction.Sound.addProperties("COM.dragonflow.StandardAction.Sound", astringproperty);
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "description", "Plays an alert sound on the machine where SiteView is running.");
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "help", "AlertSound.htm");
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "title", "Play Sound");
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "label", "Play sound");
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "name", "Sound");
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "class", "Sound");
        COM.dragonflow.StandardAction.Sound.setClassProperty("COM.dragonflow.StandardAction.Sound", "prefs", "");
        jgl.HashMap hashmap = SiteViewMain.SiteViewSupport.CopyDefaultConfig();
        audioSleepTime = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_AudioSleepTime"));
        if(audioSleepTime == 0)
        {
            audioSleepTime = 3000;
        }
    }
}
