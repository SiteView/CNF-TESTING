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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Encoder
{

    private static final int BUFFER_SIZE = 900;
    private static byte encoding[] = {
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 43, 47, 61
    };
    java.io.InputStream in;
    java.io.OutputStream out;
    boolean stringp;

    private final int get1(byte abyte0[], int i)
    {
        return (abyte0[i] & 0xfc) >> 2;
    }

    private final int get2(byte abyte0[], int i)
    {
        return (abyte0[i] & 3) << 4 | (abyte0[i + 1] & 0xf0) >>> 4;
    }

    private final int get3(byte abyte0[], int i)
    {
        return (abyte0[i + 1] & 0xf) << 2 | (abyte0[i + 2] & 0xc0) >>> 6;
    }

    private static final int get4(byte abyte0[], int i)
    {
        return abyte0[i + 2] & 0x3f;
    }

    public void process()
        throws java.io.IOException
    {
        byte abyte0[] = new byte[900];
        int i = -1;
        int j = 0;
        int k = 0;
        while((i = in.read(abyte0, j, 900 - j)) > 0) 
        {
            for(; j + 3 <= i; j += 3)
            {
                int l = get1(abyte0, j);
                int j1 = get2(abyte0, j);
                int k1 = get3(abyte0, j);
                int l1 = com.dragonflow.Utils.Base64Encoder.get4(abyte0, j);
                switch(k)
                {
                case 73: // 'I'
                    out.write(encoding[l]);
                    out.write(encoding[j1]);
                    out.write(encoding[k1]);
                    out.write(10);
                    out.write(encoding[l1]);
                    k = 1;
                    break;

                case 74: // 'J'
                    out.write(encoding[l]);
                    out.write(encoding[j1]);
                    out.write(10);
                    out.write(encoding[k1]);
                    out.write(encoding[l1]);
                    k = 2;
                    break;

                case 75: // 'K'
                    out.write(encoding[l]);
                    out.write(10);
                    out.write(encoding[j1]);
                    out.write(encoding[k1]);
                    out.write(encoding[l1]);
                    k = 3;
                    break;

                case 76: // 'L'
                    out.write(10);
                    out.write(encoding[l]);
                    out.write(encoding[j1]);
                    out.write(encoding[k1]);
                    out.write(encoding[l1]);
                    k = 4;
                    break;

                default:
                    out.write(encoding[l]);
                    out.write(encoding[j1]);
                    out.write(encoding[k1]);
                    out.write(encoding[l1]);
                    k += 4;
                    break;
                }
            }

            for(int i1 = 0; i1 < 3; i1++)
            {
                abyte0[i1] = i1 >= i - j ? 0 : abyte0[j + i1];
            }

            j = i - j;
        }
        switch(j)
        {
        case 1: // '\001'
            out.write(encoding[get1(abyte0, 0)]);
            out.write(encoding[get2(abyte0, 0)]);
            out.write(61);
            out.write(61);
            break;

        case 2: // '\002'
            out.write(encoding[get1(abyte0, 0)]);
            out.write(encoding[get2(abyte0, 0)]);
            out.write(encoding[get3(abyte0, 0)]);
            out.write(61);
            break;
        }
    }

    public String processString()
    {
        if(!stringp)
        {
            throw new RuntimeException(getClass().getName() + "[processString]" + "invalid call (not a String)");
        }
        try
        {
            process();
        }
        catch(java.io.IOException ioexception) { }
        return ((java.io.ByteArrayOutputStream)out).toString();
    }

    public Base64Encoder(String s)
    {
        in = null;
        out = null;
        stringp = false;
        byte abyte0[] = new byte[s.length()];
        s.getBytes(0, abyte0.length, abyte0, 0);
        stringp = true;
        in = new ByteArrayInputStream(abyte0);
        out = new ByteArrayOutputStream();
    }

    public Base64Encoder(String s, String s1)
    {
        in = null;
        out = null;
        stringp = false;
        byte abyte0[] = new byte[0];
        if(s1 == null || s1.equals(""))
        {
            abyte0 = s.getBytes();
        } else
        {
            try
            {
                abyte0 = s.getBytes(s1);
            }
            catch(java.io.UnsupportedEncodingException unsupportedencodingexception)
            {
                abyte0 = s.getBytes();
            }
        }
        stringp = true;
        in = new ByteArrayInputStream(abyte0);
        out = new ByteArrayOutputStream();
    }

    public Base64Encoder(java.io.InputStream inputstream, java.io.OutputStream outputstream)
    {
        in = null;
        out = null;
        stringp = false;
        in = inputstream;
        out = outputstream;
        stringp = false;
    }

    public static void main(String args[])
    {
        if(args.length != 1)
        {
            java.lang.System.out.println("Base64Encoder <string>");
            java.lang.System.exit(0);
        }
        com.dragonflow.Utils.Base64Encoder base64encoder = new Base64Encoder(args[0]);
        java.lang.System.out.println("[" + base64encoder.processString() + "]");
    }

}
