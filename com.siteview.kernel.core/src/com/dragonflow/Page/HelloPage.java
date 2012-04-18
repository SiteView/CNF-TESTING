package com.dragonflow.Page;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.dragonflow.SiteView.Platform;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

// visit: http://localhost:9999/SiteView/cgi/go.exe/SiteView?page=Hello

public class HelloPage extends CGI
{
    public HelloPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
    	
        /* Get or create a template */
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(Platform.getRoot()+"/templates.freemarker"));
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        Template template = cfg.getTemplate("hello.ftl");
//
//        /* Create a data-model */
        Map<String,String> root = new HashMap();
        root.put("name", "SiteView");


        /* Merge data-model with template */
        
        template.process(root, outputStream);
        outputStream.flush();
    }

}
