/*
 * 
 * Created on 2005-3-9 18:54:04
 *
 * LDAPSession.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.LDAP;

import java.util.Hashtable;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;

public class LDAPSession
{

 static final String ldapAnyTag = "[LDAP-ANY]";
 static final String ldap3Tag = "[LDAP-3]";
 static final String ldapSSLTag = "[LDAP-SSL]";
 public static final String LDAP_TRACE = "_ldapTrace";
 public static final String defaultContext = "defaultNamingContext";
 public static final String rootNamingContext = "rootDomainNamingContext";
 private java.util.Hashtable env;
 private javax.naming.directory.Attributes rootAttributes;
 private String url;
 java.lang.Object rootAttributeLock;

 public LDAPSession(String s, String s1, String s2, boolean flag, int i)
     throws javax.naming.NamingException
 {
     env = new Hashtable(11);
     rootAttributeLock = new Object();
     env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
     env.put("com.sun.jndi.ldap.connect.timeout", i + "");
     if(s1.length() > 0)
     {
         env.put("java.naming.security.authentication", "simple");
     } else
     {
         env.put("java.naming.security.authentication", "none");
     }
     if(s.length() > 0)
     {
         env.put("java.naming.security.principal", s);
     }
     env.put("java.naming.security.credentials", s1);
     if(s2.indexOf("ldaps") >= 0)
     {
         env.put("java.naming.security.protocol", "ssl");
     }
     if(flag)
     {
         env.put("com.sun.jndi.ldap.trace.ber", java.lang.System.err);
     }
     env.put("java.naming.provider.url", s2);
     url = s2;
 }

 public void close()
 {
 }

 public javax.naming.directory.Attributes performQuery(String s)
     throws javax.naming.NamingException
 {
     javax.naming.directory.InitialDirContext initialdircontext = new InitialDirContext(env);
     s = getFullPath(s);
     javax.naming.directory.Attributes attributes = initialdircontext.getAttributes(s);
     initialdircontext.close();
     return attributes;
 }

 public String getValue(String s, String s1)
     throws javax.naming.NamingException
 {
     javax.naming.directory.Attributes attributes = performQuery(s);
     if(attributes != null)
     {
         return getAttributeValue(attributes, s1);
     } else
     {
         return null;
     }
 }

 public String getFromRootCache(String s)
     throws javax.naming.NamingException
 {
     if(rootAttributes == null)
     {
         synchronized(rootAttributeLock)
         {
             if(rootAttributes == null)
             {
                 refreshRootAttributes();
             }
         }
     }
     return getAttributeValue(rootAttributes, s);
 }

 public String getFromRoot(String s)
     throws javax.naming.NamingException
 {
     java.util.Hashtable hashtable = new Hashtable(env);
     hashtable.put("java.naming.security.authentication", "none");
     Object obj = null;
     javax.naming.directory.Attributes attributes;
     try
     {
         javax.naming.directory.InitialDirContext initialdircontext = new InitialDirContext(hashtable);
         attributes = initialdircontext.getAttributes((String)hashtable.get("java.naming.provider.url"));
         initialdircontext.close();
     }
     catch(javax.naming.NamingException namingexception)
     {
         com.dragonflow.Log.LogManager.log("Error", "LDAP Session unable to create ROOTDSE context for  " + url + ". " + namingexception.getMessage());
         throw namingexception;
     }
     return getAttributeValue(attributes, s);
 }

 public String getDefaultContext()
     throws javax.naming.NamingException
 {
     return getFromRootCache("defaultNamingContext");
 }

 private void refreshRootAttributes()
 {
     java.util.Hashtable hashtable = new Hashtable(env);
     hashtable.put("java.naming.security.authentication", "none");
     Object obj = null;
     try
     {
         javax.naming.directory.InitialDirContext initialdircontext = new InitialDirContext(hashtable);
         rootAttributes = initialdircontext.getAttributes((String)hashtable.get("java.naming.provider.url"));
         initialdircontext.close();
     }
     catch(javax.naming.NamingException namingexception)
     {
         com.dragonflow.Log.LogManager.log("Error", "LDAP Session unable to create ROOTDSE context for  " + url + ". " + namingexception.getCause().getMessage());
     }
 }

 private String getAttributeValue(javax.naming.directory.Attributes attributes, String s)
     throws javax.naming.NamingException
 {
     if(attributes == null)
     {
         return null;
     } else
     {
         javax.naming.directory.Attribute attribute = attributes.get(s);
         return (String)attribute.get();
     }
 }

 private javax.naming.directory.DirContext getSubContext(String s)
     throws javax.naming.NamingException
 {
     java.util.Hashtable hashtable = new Hashtable(env);
     s = getFullPath(s);
     String s1 = (String)hashtable.get("java.naming.provider.url") + "/" + s;
     hashtable.put("java.naming.provider.url", s1);
     javax.naming.directory.InitialDirContext initialdircontext = new InitialDirContext(hashtable);
     return initialdircontext;
 }

 private String getFullPath(String s)
     throws javax.naming.NamingException
 {
     String s1 = getFromRootCache("rootDomainNamingContext");
     String s2 = getFromRootCache("defaultNamingContext");
     if(s1 == null || s2 == null || s == null)
     {
         return s;
     }
     int i = s1.length();
     int j = s.length();
     Object obj = null;
     if(j >= i)
     {
         String s3 = s.substring(j - i);
         if(!s3.equalsIgnoreCase(s1))
         {
             s = s + "," + s2;
         }
     } else
     {
         s = s + "," + s2;
     }
     return s;
 }

 /**
     * 
     * 
     * @param s
     * @return
     * @throws javax.naming.NamingException
     */
 public boolean contextExists(String s)
     throws javax.naming.NamingException
 {
     javax.naming.directory.InitialDirContext initialdircontext;
     s = getFullPath(s);
     initialdircontext = null;
     try {
     initialdircontext = new InitialDirContext(env);
     initialdircontext.getAttributes(s);
     initialdircontext.close();
     return true;
     }
     catch (javax.naming.NamingException namingexception) {
     if(initialdircontext != null)
     {
         initialdircontext.close();
     }
     return false;
     }
 }

 /**
     * 
     * 
     * @param s
     * @param attributes
     * @return
     * @throws javax.naming.NamingException
     */
 public javax.naming.directory.DirContext createSubcontext(String s, javax.naming.directory.Attributes attributes)
     throws javax.naming.NamingException
 {
     String s1 = getFullPath(s);
     int i = s1.indexOf(",");
     if(i != -1)
     {
         s = s1.substring(0, i);
         s1 = s1.substring(i + 1);
         javax.naming.directory.DirContext dircontext = getSubContext(s1);
         javax.naming.directory.DirContext dircontext1 = dircontext.createSubcontext(s, attributes);
         dircontext.close();
         return dircontext1;
     } else
     {
         return null;
     }
 }

 public void setAttribute(String s, String s1, String s2)
     throws javax.naming.NamingException
 {
     String s3 = getFullPath(s);
     int i = s3.indexOf(",");
     s = s3.substring(0, i);
     s3 = s3.substring(i + 1);
     javax.naming.directory.DirContext dircontext = getSubContext(s3);
     javax.naming.directory.ModificationItem amodificationitem[] = {
         new ModificationItem(2, new BasicAttribute(s1, s2))
     };
     dircontext.modifyAttributes(s, amodificationitem);
     dircontext.close();
 }

 public static void main(String args[])
     throws javax.naming.NamingException
 {
     int i = 0;
     String s = args[i++];
     String s1 = args[i++];
     String s2 = args[i++];
     String s3 = args[i++];
     String s4 = args[i++];
     com.dragonflow.Utils.LDAP.LDAPSession ldapsession = new LDAPSession(s1, s2, s, true, 5000);
     try
     {
         String s5 = ldapsession.getFromRootCache(s3);
         java.lang.System.out.println(s3 + "=" + s5);
         java.lang.System.out.println(s3 + "=" + ldapsession.getValue(s5, s4));
     }
     catch(javax.naming.NamingException namingexception)
     {
         namingexception.printStackTrace();
     }
 }

 public String getUrl()
 {
     return url;
 }
}
