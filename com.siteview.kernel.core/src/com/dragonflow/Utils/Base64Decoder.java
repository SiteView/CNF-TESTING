 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package com.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

// Referenced classes of package com.dragonflow.Utils:
// Base64FormatException

public class Base64Decoder
{

    private static final int BUFFER_SIZE = 1024;
    String in;
    String out;
    boolean stringp;

    private void printHex(int i)
    {
        int j = (i & 0xf0) >> 4;
        int k = i & 0xf;
        java.lang.System.out.print((new Character((char)(j <= 9 ? 48 + j : (65 + j) - 10))).toString() + (new Character((char)(k <= 9 ? 48 + k : (65 + k) - 10))).toString());
    }

    private void printHex(byte abyte0[], int i, int j)
    {
        while(i < j) 
        {
            printHex(abyte0[i++]);
            java.lang.System.out.print(" ");
        }
        java.lang.System.out.println("");
    }

    private void printHex(String s)
    {
        byte abyte0[] = new byte[s.length()];
        s.getBytes(0, abyte0.length, abyte0, 0);
        printHex(abyte0, 0, abyte0.length);
    }

    private char getBase64Digit(char c)
    {
        if(c >= 'A' && c <= 'Z')
        {
            return (char)(c - 65);
        }
        if(c >= 'a' && c <= 'z')
        {
            return (char)((c - 97) + 26);
        }
        if(c >= '0' && c <= '9')
        {
            return (char)((c - 48) + 52);
        }
        switch(c)
        {
        case 61: // '='
            return 'A';

        case 43: // '+'
            return '>';

        case 47: // '/'
            return '?';
        }
        return '\uFFFF';
    }

    public char[] process()
        throws java.io.IOException, com.dragonflow.Utils.Base64FormatException
    {
        char ac[] = new char[in.length()];
        char ac1[] = new char[4];
        int i = 0;
        for(int j = 0; j <= in.length() - 4; j += 4)
        {
            for(int k = 0; k < 4; k++)
            {
                ac1[k] = getBase64Digit(in.charAt(j + k));
                if(ac1[k] == '\uFFFF')
                {
                    throw new Base64FormatException("Found:'" + in.charAt(j + k) + "' at[" + j + k + "]");
                }
                if(ac1[k] == 'A')
                {
                    break;
                }
                if(k == 1)
                {
                    ac[i++] = (char)((ac1[k - 1] & 0x3f) << 2 | (ac1[k] & 0x30) >>> 4);
                }
                if(k == 2)
                {
                    ac[i++] = (char)((ac1[k - 1] & 0xf) << 4 | (ac1[k] & 0x3c) >>> 2);
                }
                if(k == 3)
                {
                    ac[i++] = (char)((ac1[k - 1] & 3) << 6 | ac1[k] & 0x3f);
                }
            }

        }

        char ac2[] = new char[i];
        for(int l = 0; l < i; l++)
        {
            ac2[l] = ac[l];
        }

        return ac2;
    }

    public String processString()
        throws com.dragonflow.Utils.Base64FormatException
    {
        String s = "";
        if(!stringp)
        {
            throw new RuntimeException(getClass().getName() + "[processString]" + "invalid call (not a String)");
        }
        try
        {
            char ac[] = process();
            s = new String(ac);
        }
        catch(java.io.IOException ioexception) { }
        return s;
    }

    public byte[] processBytes()
        throws java.lang.Exception
    {
        char ac[] = process();
        byte abyte0[] = new byte[ac.length];
        for(int i = 0; i < ac.length; i++)
        {
            abyte0[i] = (byte)ac[i];
        }

        return abyte0;
    }

    public Base64Decoder(String s)
    {
        in = null;
        out = null;
        stringp = false;
        in = s.replaceAll("\n|\r", "");
        stringp = true;
    }

    public static void main(String args[])
    {
        if(args.length == 1)
        {
            try
            {
                com.dragonflow.Utils.Base64Decoder base64decoder = new Base64Decoder(args[0]);
                java.lang.System.out.println("[" + base64decoder.processString() + "]");
            }
            catch(com.dragonflow.Utils.Base64FormatException base64formatexception)
            {
                java.lang.System.out.println("Invalid Base64 format !");
                java.lang.System.exit(1);
            }
        } else
        {
            java.lang.System.out.println("Base64Decoder [strong] [-f file]");
        }
        java.lang.System.exit(0);
    }
}
