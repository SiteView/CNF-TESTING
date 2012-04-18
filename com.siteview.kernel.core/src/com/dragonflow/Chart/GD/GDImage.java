/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Chart.GD;;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */


// Referenced classes of package com.dragonflow.Chart.GD:
// GDFont, GDPoint

public class GDImage
{

    static final int gdMaxColors = 256;
    short pixels[][];
    int sx;
    int sy;
    int colorsTotal;
    int red[];
    int green[];
    int blue[];
    int open[];
    int transparent;
    int polyInts[];
    int polyAllocated;
    com.dragonflow.Chart.GD.GDImage brush;
    com.dragonflow.Chart.GD.GDImage tile;
    int brushColorMap[];
    int tileColorMap[];
    int styleLength;
    int stylePos;
    int style[];
    boolean interlace;
    public static final int gdDashSize = 2;
    public static final int gdStyled = -2;
    public static final int gdBrushed = -3;
    public static final int gdStyledBrushed = -4;
    public static final int gdTiled = -5;
    public static final int gdTransparent = -6;
    public static final int gdLeftJustified = 0;
    public static final int gdCenterJustified = 1;
    public static final int gdRightJustified = 2;
    private final int costScale = 1024;
    private final int cost[] = {
        1024, 1023, 1023, 1022, 1021, 1020, 1018, 1016, 1014, 1011, 
        1008, 1005, 1001, 997, 993, 989, 984, 979, 973, 968, 
        962, 955, 949, 942, 935, 928, 920, 912, 904, 895, 
        886, 877, 868, 858, 848, 838, 828, 817, 806, 795, 
        784, 772, 760, 748, 736, 724, 711, 698, 685, 671, 
        658, 644, 630, 616, 601, 587, 572, 557, 542, 527, 
        512, 496, 480, 464, 448, 432, 416, 400, 383, 366, 
        350, 333, 316, 299, 282, 265, 247, 230, 212, 195, 
        177, 160, 142, 124, 107, 89, 71, 53, 35, 17, 
        0, -17, -35, -53, -71, -89, -107, -124, -142, -160, 
        -177, -195, -212, -230, -247, -265, -282, -299, -316, -333, 
        -350, -366, -383, -400, -416, -432, -448, -464, -480, -496, 
        -512, -527, -542, -557, -572, -587, -601, -616, -630, -644, 
        -658, -671, -685, -698, -711, -724, -736, -748, -760, -772, 
        -784, -795, -806, -817, -828, -838, -848, -858, -868, -877, 
        -886, -895, -904, -912, -920, -928, -935, -942, -949, -955, 
        -962, -968, -973, -979, -984, -989, -993, -997, -1001, -1005, 
        -1008, -1011, -1014, -1016, -1018, -1020, -1021, -1022, -1023, -1023, 
        -1024, -1023, -1023, -1022, -1021, -1020, -1018, -1016, -1014, -1011, 
        -1008, -1005, -1001, -997, -993, -989, -984, -979, -973, -968, 
        -962, -955, -949, -942, -935, -928, -920, -912, -904, -895, 
        -886, -877, -868, -858, -848, -838, -828, -817, -806, -795, 
        -784, -772, -760, -748, -736, -724, -711, -698, -685, -671, 
        -658, -644, -630, -616, -601, -587, -572, -557, -542, -527, 
        -512, -496, -480, -464, -448, -432, -416, -400, -383, -366, 
        -350, -333, -316, -299, -282, -265, -247, -230, -212, -195, 
        -177, -160, -142, -124, -107, -89, -71, -53, -35, -17, 
        0, 17, 35, 53, 71, 89, 107, 124, 142, 160, 
        177, 195, 212, 230, 247, 265, 282, 299, 316, 333, 
        350, 366, 383, 400, 416, 432, 448, 464, 480, 496, 
        512, 527, 542, 557, 572, 587, 601, 616, 630, 644, 
        658, 671, 685, 698, 711, 724, 736, 748, 760, 772, 
        784, 795, 806, 817, 828, 838, 848, 858, 868, 877, 
        886, 895, 904, 912, 920, 928, 935, 942, 949, 955, 
        962, 968, 973, 979, 984, 989, 993, 997, 1001, 1005, 
        1008, 1011, 1014, 1016, 1018, 1020, 1021, 1022, 1023, 1023
    };
    private final int sintScale = 1024;
    private final int sint[] = {
        0, 17, 35, 53, 71, 89, 107, 124, 142, 160, 
        177, 195, 212, 230, 247, 265, 282, 299, 316, 333, 
        350, 366, 383, 400, 416, 432, 448, 464, 480, 496, 
        512, 527, 542, 557, 572, 587, 601, 616, 630, 644, 
        658, 671, 685, 698, 711, 724, 736, 748, 760, 772, 
        784, 795, 806, 817, 828, 838, 848, 858, 868, 877, 
        886, 895, 904, 912, 920, 928, 935, 942, 949, 955, 
        962, 968, 973, 979, 984, 989, 993, 997, 1001, 1005, 
        1008, 1011, 1014, 1016, 1018, 1020, 1021, 1022, 1023, 1023, 
        1024, 1023, 1023, 1022, 1021, 1020, 1018, 1016, 1014, 1011, 
        1008, 1005, 1001, 997, 993, 989, 984, 979, 973, 968, 
        962, 955, 949, 942, 935, 928, 920, 912, 904, 895, 
        886, 877, 868, 858, 848, 838, 828, 817, 806, 795, 
        784, 772, 760, 748, 736, 724, 711, 698, 685, 671, 
        658, 644, 630, 616, 601, 587, 572, 557, 542, 527, 
        512, 496, 480, 464, 448, 432, 416, 400, 383, 366, 
        350, 333, 316, 299, 282, 265, 247, 230, 212, 195, 
        177, 160, 142, 124, 107, 89, 71, 53, 35, 17, 
        0, -17, -35, -53, -71, -89, -107, -124, -142, -160, 
        -177, -195, -212, -230, -247, -265, -282, -299, -316, -333, 
        -350, -366, -383, -400, -416, -432, -448, -464, -480, -496, 
        -512, -527, -542, -557, -572, -587, -601, -616, -630, -644, 
        -658, -671, -685, -698, -711, -724, -736, -748, -760, -772, 
        -784, -795, -806, -817, -828, -838, -848, -858, -868, -877, 
        -886, -895, -904, -912, -920, -928, -935, -942, -949, -955, 
        -962, -968, -973, -979, -984, -989, -993, -997, -1001, -1005, 
        -1008, -1011, -1014, -1016, -1018, -1020, -1021, -1022, -1023, -1023, 
        -1024, -1023, -1023, -1022, -1021, -1020, -1018, -1016, -1014, -1011, 
        -1008, -1005, -1001, -997, -993, -989, -984, -979, -973, -968, 
        -962, -955, -949, -942, -935, -928, -920, -912, -904, -895, 
        -886, -877, -868, -858, -848, -838, -828, -817, -806, -795, 
        -784, -772, -760, -748, -736, -724, -711, -698, -685, -671, 
        -658, -644, -630, -616, -601, -587, -572, -557, -542, -527, 
        -512, -496, -480, -464, -448, -432, -416, -400, -383, -366, 
        -350, -333, -316, -299, -282, -265, -247, -230, -212, -195, 
        -177, -160, -142, -124, -107, -89, -71, -53, -35, -17
    };

    public int getSX()
    {
        return sx;
    }

    public int getSY()
    {
        return sy;
    }

    public int getColorsTotal()
    {
        return colorsTotal;
    }

    public int getRed(int i)
    {
        return red[i];
    }

    public int getGreen(int i)
    {
        return green[i];
    }

    public int getBlue(int i)
    {
        return blue[i];
    }

    public int getTransparent()
    {
        return transparent;
    }

    public boolean getInterlaced()
    {
        return interlace;
    }

    public GDImage(int i, int j)
    {
        red = new int[256];
        green = new int[256];
        blue = new int[256];
        open = new int[256];
        polyInts = null;
        brushColorMap = new int[256];
        tileColorMap = new int[256];
        pixels = new short[i][j];
        polyInts = null;
        polyAllocated = 0;
        brush = null;
        tile = null;
        style = null;
        sx = i;
        sy = j;
        colorsTotal = 0;
        transparent = -1;
        interlace = false;
    }

    public int[] getPixels()
    {
        return getPixels(new int[sx * sy]);
    }

    public int[] getPixels(int ai[])
    {
        int i = 0;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        for(int j = 0; j < sy; j++)
        {
            for(int k = 0; k < sx; k++)
            {
                short word0 = pixels[k][j];
                int l = 0x80000000;
                if(getTransparent() == word0)
                {
                    l = 0;
                } else
                {
                    l = 0xff000000;
                }
                if(word0 < 0)
                {
                    ai[i++] = 0;
                } else
                {
                    ai[i++] = l | red[word0] << 16 | green[word0] << 8 | blue[word0];
                }
            }

        }

        return ai;
    }

    public void dump()
    {
        for(int i = 0; i < sy; i++)
        {
            for(int j = 0; j < sx; j++)
            {
                java.lang.System.out.print(pixels[j][i]);
            }

            java.lang.System.out.println("");
        }

    }

    public int colorClosest(int i, int j, int k)
    {
        int i1 = -1;
        long l4 = 0L;
        for(int l = 0; l < colorsTotal; l++)
        {
            if(open[l] > 0)
            {
                continue;
            }
            long l1 = red[l] - i;
            long l2 = green[l] - j;
            long l3 = blue[l] - k;
            long l5 = l1 * l1 + l2 * l2 + l3 * l3;
            if(l == 0 || l5 < l4)
            {
                l4 = l5;
                i1 = l;
            }
        }

        return i1;
    }

    public int colorExact(int i, int j, int k)
    {
        for(int l = 0; l < colorsTotal; l++)
        {
            if(open[l] <= 0 && red[l] == i && green[l] == j && blue[l] == k)
            {
                return l;
            }
        }

        return -1;
    }

    public int colorAllocate(int i, int j, int k)
    {
        int i1 = -1;
        int l = 0;
        do
        {
            if(l >= colorsTotal)
            {
                break;
            }
            if(open[l] > 0)
            {
                i1 = l;
                break;
            }
            l++;
        } while(true);
        if(i1 == -1)
        {
            i1 = colorsTotal;
            if(i1 == 256)
            {
                return -1;
            }
            colorsTotal++;
        }
        red[i1] = i;
        green[i1] = j;
        blue[i1] = k;
        open[i1] = 0;
        return i1;
    }

    public void colorDeallocate(int i)
    {
        open[i] = 1;
    }

    public void colorTransparent(int i)
    {
        transparent = i;
    }

    public void setPixel(int i, int j, int k)
    {
        switch(k)
        {
        case -2: 
            if(style == null)
            {
                return;
            }
            int l = style[stylePos++];
            if(l != -6)
            {
                setPixel(i, j, l);
            }
            stylePos = stylePos % styleLength;
            break;

        case -4: 
            if(style == null)
            {
                return;
            }
            int i1 = style[stylePos++];
            if(i1 != -6 && i1 != 0)
            {
                setPixel(i, j, -3);
            }
            stylePos = stylePos % styleLength;
            break;

        case -3: 
            brushApply(i, j);
            break;

        case -5: 
            tileApply(i, j);
            break;

        default:
            if(boundsSafe(i, j))
            {
                pixels[i][j] = (short)k;
            }
            break;
        }
    }

    public void brushApply(int i, int j)
    {
        if(brush == null)
        {
            return;
        }
        int i1 = brush.getSY() / 2;
        int l1 = j - i1;
        int j2 = l1 + brush.getSY();
        int j1 = brush.getSX() / 2;
        int k1 = i - j1;
        int i2 = k1 + brush.getSX();
        int l2 = 0;
        for(int l = l1; l < j2; l++)
        {
            int k2 = 0;
            for(int k = k1; k < i2; k++)
            {
                int i3 = brush.getPixel(k2, l2);
                if(i3 != brush.getTransparent())
                {
                    setPixel(k, l, brushColorMap[i3]);
                }
                k2++;
            }

            l2++;
        }

    }

    public void tileApply(int i, int j)
    {
        if(tile == null)
        {
            return;
        }
        int k = i % tile.getSX();
        int l = j % tile.getSY();
        int i1 = tile.getPixel(k, l);
        if(i1 != tile.getTransparent())
        {
            setPixel(i, j, tileColorMap[i1]);
        }
    }

    public int getPixel(int i, int j)
    {
        if(boundsSafe(i, j))
        {
            return pixels[i][j];
        } else
        {
            return 0;
        }
    }

    public void drawLine(int i, int j, int k, int l, int i1)
    {
        int j1 = java.lang.Math.abs(k - i);
        int k1 = java.lang.Math.abs(l - j);
        if(k1 <= j1)
        {
            int l2 = 2 * k1 - j1;
            int l1 = 2 * k1;
            int j2 = 2 * (k1 - j1);
            int j3;
            int l3;
            int j4;
            byte byte1;
            if(i > k)
            {
                j3 = k;
                l3 = l;
                byte1 = -1;
                j4 = i;
            } else
            {
                j3 = i;
                l3 = j;
                byte1 = 1;
                j4 = k;
            }
            setPixel(j3, l3, i1);
            if((l - j) * byte1 > 0)
            {
                while(j3 < j4) 
                {
                    j3++;
                    if(l2 < 0)
                    {
                        l2 += l1;
                    } else
                    {
                        l3++;
                        l2 += j2;
                    }
                    setPixel(j3, l3, i1);
                }
            } else
            {
                while(j3 < j4) 
                {
                    j3++;
                    if(l2 < 0)
                    {
                        l2 += l1;
                    } else
                    {
                        l3--;
                        l2 += j2;
                    }
                    setPixel(j3, l3, i1);
                }
            }
        } else
        {
            int i3 = 2 * j1 - k1;
            int i2 = 2 * j1;
            int k2 = 2 * (j1 - k1);
            int k3;
            int i4;
            int k4;
            byte byte0;
            if(j > l)
            {
                i4 = l;
                k3 = k;
                k4 = j;
                byte0 = -1;
            } else
            {
                i4 = j;
                k3 = i;
                k4 = l;
                byte0 = 1;
            }
            setPixel(k3, i4, i1);
            if((k - i) * byte0 > 0)
            {
                while(i4 < k4) 
                {
                    i4++;
                    if(i3 < 0)
                    {
                        i3 += i2;
                    } else
                    {
                        k3++;
                        i3 += k2;
                    }
                    setPixel(k3, i4, i1);
                }
            } else
            {
                while(i4 < k4) 
                {
                    i4++;
                    if(i3 < 0)
                    {
                        i3 += i2;
                    } else
                    {
                        k3--;
                        i3 += k2;
                    }
                    setPixel(k3, i4, i1);
                }
            }
        }
    }

    public void drawDashedLine(int i, int j, int k, int l, int i1)
    {
        int l4 = 0;
        boolean flag = true;
        int j1 = java.lang.Math.abs(k - i);
        int k1 = java.lang.Math.abs(l - j);
        if(k1 <= j1)
        {
            int l2 = 2 * k1 - j1;
            int l1 = 2 * k1;
            int j2 = 2 * (k1 - j1);
            int j3;
            int l3;
            int j4;
            byte byte1;
            if(i > k)
            {
                j3 = k;
                l3 = l;
                byte1 = -1;
                j4 = i;
            } else
            {
                j3 = i;
                l3 = j;
                byte1 = 1;
                j4 = k;
            }
            if(++l4 == 2)
            {
                l4 = 0;
                flag = !flag;
            }
            if(flag)
            {
                setPixel(j3, l3, i1);
            }
            if((l - j) * byte1 > 0)
            {
                do
                {
                    if(j3 >= j4)
                    {
                        break;
                    }
                    j3++;
                    if(l2 < 0)
                    {
                        l2 += l1;
                    } else
                    {
                        l3++;
                        l2 += j2;
                    }
                    if(++l4 == 2)
                    {
                        l4 = 0;
                        flag = !flag;
                    }
                    if(flag)
                    {
                        setPixel(j3, l3, i1);
                    }
                } while(true);
            } else
            {
                do
                {
                    if(j3 >= j4)
                    {
                        break;
                    }
                    j3++;
                    if(l2 < 0)
                    {
                        l2 += l1;
                    } else
                    {
                        l3--;
                        l2 += j2;
                    }
                    if(++l4 == 2)
                    {
                        l4 = 0;
                        flag = !flag;
                    }
                    if(flag)
                    {
                        setPixel(j3, l3, i1);
                    }
                } while(true);
            }
        } else
        {
            int i3 = 2 * j1 - k1;
            int i2 = 2 * j1;
            int k2 = 2 * (j1 - k1);
            int k3;
            int i4;
            int k4;
            byte byte0;
            if(j > l)
            {
                i4 = l;
                k3 = k;
                k4 = j;
                byte0 = -1;
            } else
            {
                i4 = j;
                k3 = i;
                k4 = l;
                byte0 = 1;
            }
            if(++l4 == 2)
            {
                l4 = 0;
                flag = !flag;
            }
            if(flag)
            {
                setPixel(k3, i4, i1);
            }
            if((k - i) * byte0 > 0)
            {
                do
                {
                    if(i4 >= k4)
                    {
                        break;
                    }
                    i4++;
                    if(i3 < 0)
                    {
                        i3 += i2;
                    } else
                    {
                        k3++;
                        i3 += k2;
                    }
                    if(++l4 == 2)
                    {
                        l4 = 0;
                        flag = !flag;
                    }
                    if(flag)
                    {
                        setPixel(k3, i4, i1);
                    }
                } while(true);
            } else
            {
                do
                {
                    if(i4 >= k4)
                    {
                        break;
                    }
                    i4++;
                    if(i3 < 0)
                    {
                        i3 += i2;
                    } else
                    {
                        k3--;
                        i3 += k2;
                    }
                    if(++l4 == 2)
                    {
                        l4 = 0;
                        flag = !flag;
                    }
                    if(flag)
                    {
                        setPixel(k3, i4, i1);
                    }
                } while(true);
            }
        }
    }

    public boolean boundsSafe(int i, int j)
    {
        return j >= 0 && j < sy && i >= 0 && i < sx;
    }

    public void drawChar(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, int k, int l)
    {
        j -= gdfont.ascent;
        int i1 = 0;
        int j1 = 0;
        if(k < gdfont.offset || k >= gdfont.offset + gdfont.nchars)
        {
            return;
        }
        int i2 = (k - gdfont.offset) * gdfont.h * gdfont.w;
        for(int l1 = j; l1 < j + gdfont.h; l1++)
        {
            for(int k1 = i; k1 < i + gdfont.w; k1++)
            {
                if(gdfont.data[i2 + j1 * gdfont.w + i1] > 0)
                {
                    setPixel(k1, l1, l);
                }
                i1++;
            }

            i1 = 0;
            j1++;
        }

    }

    public void drawCharUp(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, char c, int k)
    {
        i -= gdfont.ascent;
        int l = 0;
        int i1 = 0;
        if(c < gdfont.offset || c >= gdfont.offset + gdfont.nchars)
        {
            return;
        }
        int l1 = (c - gdfont.offset) * gdfont.h * gdfont.w;
        for(int k1 = j; k1 > j - gdfont.w; k1--)
        {
            for(int j1 = i; j1 < i + gdfont.h; j1++)
            {
                if(gdfont.data[l1 + i1 * gdfont.w + l] > 0)
                {
                    setPixel(j1, k1, k);
                }
                i1++;
            }

            i1 = 0;
            l++;
        }

    }

    public void drawString(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, String s, int k)
    {
        int i1 = s.length();
        for(int l = 0; l < i1; l++)
        {
            drawChar(gdfont, i, j, s.charAt(l), k);
            i += gdfont.w;
        }

    }

    public void drawStringUp(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, String s, int k)
    {
        int i1 = s.length();
        for(int l = 0; l < i1; l++)
        {
            drawCharUp(gdfont, i, j, s.charAt(l), k);
            j -= gdfont.w;
        }

    }

    public void drawStringRightJustified(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, String s, int k)
    {
        i -= gdfont.getStringWidth(s);
        drawString(gdfont, i, j, s, k);
    }

    public void drawStringCenterJustified(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, String s, int k)
    {
        i -= gdfont.getStringWidth(s) / 2;
        drawString(gdfont, i, j, s, k);
    }

    public void drawStringJustified(com.dragonflow.Chart.GD.GDFont gdfont, int i, int j, String s, int k, int l)
    {
        switch(l)
        {
        case 1: // '\001'
            drawStringCenterJustified(gdfont, i, j, s, k);
            break;

        case 2: // '\002'
            drawStringRightJustified(gdfont, i, j, s, k);
            break;

        default:
            drawString(gdfont, i, j, s, k);
            break;
        }
    }

    public void drawArc(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        int i2 = 0;
        int j2 = 0;
        int k2 = k / 2;
        int l2 = l / 2;
        for(; j1 < i1; j1 += 360) { }
        for(int l1 = i1; l1 <= j1; l1++)
        {
            int i3 = (int)(((long)cost[l1 % 360] * (long)k2) / 1024L) + i;
            int j3 = (int)(((long)sint[l1 % 360] * (long)l2) / 1024L) + j;
            if(l1 != i1)
            {
                drawLine(i2, j2, i3, j3, k1);
            }
            i2 = i3;
            j2 = j3;
        }

    }

    public void fillToBorder(int i, int j, int k, int l)
    {
        int k1 = -1;
        if(k < 0)
        {
            return;
        }
        for(int i2 = i; i2 >= 0 && getPixel(i2, j) != k; i2--)
        {
            setPixel(i2, j, l);
            k1 = i2;
        }

        if(k1 == -1)
        {
            return;
        }
        int l1 = i;
        for(int j2 = i + 1; j2 < sx && getPixel(j2, j) != k; j2++)
        {
            setPixel(j2, j, l);
            l1 = j2;
        }

        if(j > 0)
        {
            int i1 = 1;
            for(int k2 = k1; k2 <= l1; k2++)
            {
                int i3 = getPixel(k2, j - 1);
                if(i1 > 0)
                {
                    if(i3 != k && i3 != l)
                    {
                        fillToBorder(k2, j - 1, k, l);
                        i1 = 0;
                    }
                    continue;
                }
                if(i3 == k || i3 == l)
                {
                    i1 = 1;
                }
            }

        }
        if(j < sy - 1)
        {
            int j1 = 1;
            for(int l2 = k1; l2 <= l1; l2++)
            {
                int j3 = getPixel(l2, j + 1);
                if(j1 > 0)
                {
                    if(j3 != k && j3 != l)
                    {
                        fillToBorder(l2, j + 1, k, l);
                        j1 = 0;
                    }
                    continue;
                }
                if(j3 == k || j3 == l)
                {
                    j1 = 1;
                }
            }

        }
    }

    public void fill(int i, int j, int k)
    {
        int j1 = getPixel(i, j);
        if(k == -5)
        {
            if(tile == null)
            {
                return;
            }
            if(tile.getTransparent() != -1)
            {
                return;
            }
            int i4 = i % tile.getSX();
            int j4 = j % tile.getSY();
            int i3 = tile.getPixel(i4, j4);
            int l3 = tileColorMap[i3];
            if(j1 == l3)
            {
                return;
            }
        } else
        if(j1 == k)
        {
            return;
        }
        int k1 = -1;
        for(int i2 = i; i2 >= 0 && getPixel(i2, j) == j1; i2--)
        {
            setPixel(i2, j, k);
            k1 = i2;
        }

        if(k1 == -1)
        {
            return;
        }
        int l1 = i;
        for(int j2 = i + 1; j2 < sx && getPixel(j2, j) == j1; j2++)
        {
            setPixel(j2, j, k);
            l1 = j2;
        }

        if(j > 0)
        {
            int l = 1;
            for(int k2 = k1; k2 <= l1; k2++)
            {
                int j3 = getPixel(k2, j - 1);
                if(l > 0)
                {
                    if(j3 == j1)
                    {
                        fill(k2, j - 1, k);
                        l = 0;
                    }
                    continue;
                }
                if(j3 != j1)
                {
                    l = 1;
                }
            }

        }
        if(j < sy - 1)
        {
            int i1 = 1;
            for(int l2 = k1; l2 <= l1; l2++)
            {
                int k3 = getPixel(l2, j + 1);
                if(i1 > 0)
                {
                    if(k3 == j1)
                    {
                        fill(l2, j + 1, k);
                        i1 = 0;
                    }
                    continue;
                }
                if(k3 != j1)
                {
                    i1 = 1;
                }
            }

        }
    }

    public void drawRectangle(int i, int j, int k, int l, int i1)
    {
        drawLine(i, j, k, j, i1);
        drawLine(i, l, k, l, i1);
        drawLine(i, j, i, l, i1);
        drawLine(k, j, k, l, i1);
    }

    public void drawFilledRectangle(int i, int j, int k, int l, int i1)
    {
        for(int k1 = j; k1 <= l; k1++)
        {
            for(int j1 = i; j1 <= k; j1++)
            {
                setPixel(j1, k1, i1);
            }

        }

    }

    public static void copy(com.dragonflow.Chart.GD.GDImage gdimage, com.dragonflow.Chart.GD.GDImage gdimage1, int i, int j, int k, int l, int i1, int j1)
    {
        int ai[] = new int[256];
        for(int l2 = 0; l2 < 256; l2++)
        {
            ai[l2] = -1;
        }

        int k2 = j;
        for(int i2 = l; i2 < l + j1; i2++)
        {
            int j2 = i;
            for(int l1 = k; l1 < k + i1; l1++)
            {
                int k1 = gdimage1.getPixel(l1, i2);
                if(gdimage1.getTransparent() == k1)
                {
                    j2++;
                    continue;
                }
                if(ai[k1] == -1)
                {
                    int i3;
                    if(gdimage == gdimage1)
                    {
                        i3 = k1;
                    } else
                    {
                        i3 = gdimage.colorExact(gdimage1.red[k1], gdimage1.green[k1], gdimage1.blue[k1]);
                    }
                    if(i3 == -1)
                    {
                        i3 = gdimage.colorAllocate(gdimage1.red[k1], gdimage1.green[k1], gdimage1.blue[k1]);
                        if(i3 == -1)
                        {
                            i3 = gdimage.colorClosest(gdimage1.red[k1], gdimage1.green[k1], gdimage1.blue[k1]);
                        }
                    }
                    ai[k1] = i3;
                }
                gdimage.setPixel(j2, k2, ai[k1]);
                j2++;
            }

            k2++;
        }

    }

    public static void copyResized(com.dragonflow.Chart.GD.GDImage gdimage, com.dragonflow.Chart.GD.GDImage gdimage1, int i, int j, int k, int l, int i1, int j1, 
            int k1, int l1)
    {
        int ai[] = new int[256];
        int ai1[] = new int[k1];
        int ai2[] = new int[l1];
        double d = 0.0D;
        for(int k3 = 0; k3 < k1; k3++)
        {
            d += (double)i1 / (double)k1;
            int k4 = (int)java.lang.Math.floor(d);
            ai1[k3] = k4;
            d -= k4;
        }

        d = 0.0D;
        for(int l3 = 0; l3 < l1; l3++)
        {
            d += (double)j1 / (double)l1;
            int l4 = (int)java.lang.Math.floor(d);
            ai2[l3] = l4;
            d -= l4;
        }

        for(int i4 = 0; i4 < 256; i4++)
        {
            ai[i4] = -1;
        }

        int i3 = j;
        for(int k2 = l; k2 < l + l1; k2++)
        {
            for(int j3 = 0; j3 < ai2[k2 - l]; j3++)
            {
                int l2 = i;
                for(int j2 = k; j2 < k + k1; j2++)
                {
                    if(ai1[j2 - k] == 0)
                    {
                        continue;
                    }
                    int i2 = gdimage1.getPixel(j2, k2);
                    if(gdimage1.getTransparent() == i2)
                    {
                        l2 += ai1[j2 - k];
                        continue;
                    }
                    if(ai[i2] == -1)
                    {
                        int i5;
                        if(gdimage == gdimage1)
                        {
                            i5 = i2;
                        } else
                        {
                            i5 = gdimage.colorExact(gdimage1.red[i2], gdimage1.green[i2], gdimage1.blue[i2]);
                        }
                        if(i5 == -1)
                        {
                            i5 = gdimage.colorAllocate(gdimage1.red[i2], gdimage1.green[i2], gdimage1.blue[i2]);
                            if(i5 == -1)
                            {
                                i5 = gdimage.colorClosest(gdimage1.red[i2], gdimage1.green[i2], gdimage1.blue[i2]);
                            }
                        }
                        ai[i2] = i5;
                    }
                    for(int j4 = 0; j4 < ai1[j2 - k]; j4++)
                    {
                        gdimage.setPixel(l2, i3, ai[i2]);
                        l2++;
                    }

                }

                i3++;
            }

        }

    }

    public void drawPolygon(com.dragonflow.Chart.GD.GDPoint agdpoint[], int i)
    {
        int i1 = agdpoint.length;
        if(i1 == 0)
        {
            return;
        }
        int k = agdpoint[0].x;
        int l = agdpoint[0].y;
        drawLine(k, l, agdpoint[i1 - 1].x, agdpoint[i1 - 1].y, i);
        for(int j = 1; j < i1; j++)
        {
            drawLine(k, l, agdpoint[j].x, agdpoint[j].y, i);
            k = agdpoint[j].x;
            l = agdpoint[j].y;
        }

    }

    public void drawFilledPolygon(com.dragonflow.Chart.GD.GDPoint agdpoint[], int i)
    {
        int i2 = agdpoint.length;
        if(i2 == 0)
        {
            return;
        }
        if(polyInts == null)
        {
            polyInts = new int[i2];
            polyAllocated = i2;
        }
        int j1 = agdpoint[0].y;
        int k1 = agdpoint[0].y;
        for(int j = 1; j < i2; j++)
        {
            if(agdpoint[j].y < j1)
            {
                j1 = agdpoint[j].y;
            }
            if(agdpoint[j].y > k1)
            {
                k1 = agdpoint[j].y;
            }
        }

        for(int i1 = j1; i1 <= k1; i1++)
        {
            int j2 = 0;
            int k2 = 0;
            boolean flag = true;
            int l1 = 0;
            for(int k = 0; k <= i2; k++)
            {
                int j4 = 0;
                int l3;
                int i4;
                if(k == i2 || k == 0)
                {
                    l3 = i2 - 1;
                    i4 = 0;
                } else
                {
                    l3 = k - 1;
                    i4 = k;
                }
                int j3 = agdpoint[l3].y;
                int k3 = agdpoint[i4].y;
                int l2;
                int i3;
                byte byte0;
                if(j3 < k3)
                {
                    j3 = agdpoint[l3].y;
                    k3 = agdpoint[i4].y;
                    l2 = agdpoint[l3].x;
                    i3 = agdpoint[i4].x;
                    byte0 = -1;
                } else
                if(j3 > k3)
                {
                    k3 = agdpoint[l3].y;
                    j3 = agdpoint[i4].y;
                    i3 = agdpoint[l3].x;
                    l2 = agdpoint[i4].x;
                    byte0 = 1;
                } else
                {
                    drawLine(agdpoint[l3].x, j3, agdpoint[i4].x, j3, i);
                    continue;
                }
                if(i1 < j3 || i1 > k3)
                {
                    continue;
                }
                int k4 = ((i1 - j3) * (i3 - l2)) / (k3 - j3) + l2;
                if(!flag)
                {
                    if(agdpoint[l3].y == agdpoint[j4].y && agdpoint[l3].x != agdpoint[j4].x && byte0 == k2)
                    {
                        if(k4 > j2)
                        {
                            polyInts[l1] = k4;
                        }
                        continue;
                    }
                    if(k4 == j2 && byte0 == k2)
                    {
                        continue;
                    }
                }
                if(k > 0)
                {
                    polyInts[l1++] = k4;
                }
                j4 = k;
                k2 = byte0;
                j2 = k4;
                flag = false;
            }

            for(int l = 0; l < l1 - 1; l += 2)
            {
                drawLine(polyInts[l], i1, polyInts[l + 1], i1, i);
            }

        }

        polyInts = null;
    }

    public void setStyle(int ai[])
    {
        style = new int[ai.length];
        for(int i = 0; i < ai.length; i++)
        {
            style[i] = ai[i];
        }

        stylePos = 0;
    }

    public void setBrush(com.dragonflow.Chart.GD.GDImage gdimage)
    {
        brush = gdimage;
        for(int i = 0; i < gdimage.getColorsTotal(); i++)
        {
            int j = colorExact(gdimage.getRed(i), gdimage.getGreen(i), gdimage.getBlue(i));
            if(j == -1)
            {
                j = colorAllocate(gdimage.getRed(i), gdimage.getGreen(i), gdimage.getBlue(i));
                if(j == -1)
                {
                    j = colorClosest(gdimage.getRed(i), gdimage.getGreen(i), gdimage.getBlue(i));
                }
            }
            brushColorMap[i] = j;
        }

    }

    public void setTile(com.dragonflow.Chart.GD.GDImage gdimage)
    {
        tile = gdimage;
        for(int i = 0; i < gdimage.getColorsTotal(); i++)
        {
            int j = colorExact(gdimage.getRed(i), gdimage.getGreen(i), gdimage.getBlue(i));
            if(j == -1)
            {
                j = colorAllocate(gdimage.getRed(i), gdimage.getGreen(i), gdimage.getBlue(i));
                if(j == -1)
                {
                    j = colorClosest(gdimage.getRed(i), gdimage.getGreen(i), gdimage.getBlue(i));
                }
            }
            tileColorMap[i] = j;
        }

    }

    public void setInterlace(boolean flag)
    {
        interlace = flag;
    }
}
