/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.HTTP;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class URLUTF8Encoder {

    static final String hex[] = { "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18", "%19", "%1a", "%1b", "%1c", "%1d",
            "%1e", "%1f", "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f", "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
            "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47", "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f", "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57", "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f", "%60", "%61",
            "%62", "%63", "%64", "%65", "%66", "%67", "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f", "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f", "%80", "%81", "%82", "%83",
            "%84", "%85", "%86", "%87", "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f", "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97", "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f", "%a0", "%a1", "%a2", "%a3", "%a4", "%a5",
            "%a6", "%a7", "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af", "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7", "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf", "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
            "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf", "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7", "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df", "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7", "%e8", "%e9",
            "%ea", "%eb", "%ec", "%ed", "%ee", "%ef", "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7", "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff" };

    public URLUTF8Encoder() {
    }

    public static String encode(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; j ++) {
            int k = s.charAt(j);
            if (65 <= k && k <= 90) {
                stringbuffer.append((char) k);
                continue;
            }
            if (97 <= k && k <= 122) {
                stringbuffer.append((char) k);
                continue;
            }
            if (48 <= k && k <= 57) {
                stringbuffer.append((char) k);
                continue;
            }
            if (k == 32) {
                stringbuffer.append('+');
                continue;
            }
            if (k == 45 || k == 95 || k == 46 || k == 33 || k == 126 || k == 42 || k == 39 || k == 47 || k == 40 || k == 41) {
                stringbuffer.append((char) k);
                continue;
            }
            if (k <= 127) {
                stringbuffer.append(hex[k]);
                continue;
            }
            if (k <= 2047) {
                stringbuffer.append(hex[0xc0 | k >> 6]);
                stringbuffer.append(hex[0x80 | k & 0x3f]);
            } else {
                stringbuffer.append(hex[0xe0 | k >> 12]);
                stringbuffer.append(hex[0x80 | k >> 6 & 0x3f]);
                stringbuffer.append(hex[0x80 | k & 0x3f]);
            }
        }

        return stringbuffer.toString();
    }

    public static String decode(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        byte byte0 = -1;
        int l = 0;
        int i1 = 0;
        int j1 = -1;
        for (; i1 < i; i1 ++) {
            int j;
            int k;
            switch (j = s.charAt(i1)) {
            case 37: // '%'
                j = s.charAt(++ i1);
                int k1 = (java.lang.Character.isDigit((char) j) ? j - 48 : (10 + java.lang.Character.toLowerCase((char) j)) - 97) & 0xf;
                j = s.charAt(++ i1);
                int l1 = (java.lang.Character.isDigit((char) j) ? j - 48 : (10 + java.lang.Character.toLowerCase((char) j)) - 97) & 0xf;
                k = k1 << 4 | l1;
                break;

            case 43: // '+'
                k = 32;
                break;

            default:
                k = j;
                break;
            }
            if ((k & 0xc0) == 128) {
                l = l << 6 | k & 0x3f;
                if (-- j1 == 0) {
                    stringbuffer.append((char) l);
                }
                continue;
            }
            if ((k & 0x80) == 0) {
                stringbuffer.append((char) k);
                continue;
            }
            if ((k & 0xe0) == 192) {
                l = k & 0x1f;
                j1 = 1;
                continue;
            }
            if ((k & 0xf0) == 224) {
                l = k & 0xf;
                j1 = 2;
                continue;
            }
            if ((k & 0xf8) == 240) {
                l = k & 7;
                j1 = 3;
                continue;
            }
            if ((k & 0xfc) == 248) {
                l = k & 3;
                j1 = 4;
            } else {
                l = k & 1;
                j1 = 5;
            }
        }

        return stringbuffer.toString();
    }

}
