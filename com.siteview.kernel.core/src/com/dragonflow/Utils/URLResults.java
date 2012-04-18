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
public class URLResults {

    private long status;

    private long totalDuration;

    private long totalBytes;

    private long lastModified;

    private long currentDate;

    private long dnsTime;

    private long connectTime;

    private long responseTime;

    private long downloadTime;

    private long daysUntilCertExpires;

    private long htmlTruncatedIfNonZero;

    private String errorMessage;

    public URLResults() {
        status = -1L;
        totalDuration = 0L;
        totalBytes = 0L;
        lastModified = 0L;
        currentDate = 0L;
        dnsTime = 0L;
        connectTime = 0L;
        responseTime = 0L;
        downloadTime = 0L;
        daysUntilCertExpires = -1L;
        htmlTruncatedIfNonZero = 0L;
    }

    public void setStatus(long l) {
        status = l;
    }

    public long getStatus() {
        return status;
    }

    public void setTotalDuration(long l) {
        totalDuration = l;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalBytes(long l) {
        totalBytes = l;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setLastModified(long l) {
        lastModified = l;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setCurrentDate(long l) {
        currentDate = l;
    }

    public long getCurrentDate() {
        return currentDate;
    }

    public void setDnsTime(long l) {
        dnsTime = l;
    }

    public long getDnsTime() {
        return dnsTime;
    }

    public void setConnectTime(long l) {
        connectTime = l;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public void setResponseTime(long l) {
        responseTime = l;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setDownloadTime(long l) {
        downloadTime = l;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public void setDaysUntilCertExpires(long l) {
        daysUntilCertExpires = l;
    }

    public long getDaysUntilCertExpires() {
        return daysUntilCertExpires;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String s) {
        errorMessage = s;
    }

    public long getHtmlTruncatedIfNonZero() {
        return htmlTruncatedIfNonZero;
    }

    public void setHtmlTruncatedIfNonZero(long l) {
        htmlTruncatedIfNonZero = l;
    }

    public long[] getResultsAsArray() {
        long al[] = new long[11];
        al[0] = status;
        al[1] = totalDuration;
        al[2] = totalBytes;
        al[3] = lastModified;
        al[4] = currentDate;
        al[5] = dnsTime;
        al[6] = connectTime;
        al[7] = responseTime;
        al[8] = downloadTime;
        al[9] = daysUntilCertExpires;
        al[10] = htmlTruncatedIfNonZero;
        return al;
    }
}
