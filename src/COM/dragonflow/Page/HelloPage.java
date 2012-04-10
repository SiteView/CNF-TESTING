package COM.dragonflow.Page;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import COM.dragonflow.SiteView.Platform;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

// visit: http://localhost:9999/SiteView/cgi/go.exe/SiteView?page=Hello

public class HelloPage extends COM.dragonflow.Page.CGI
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
        Template template = cfg.getTemplate("hello.html");
//
//        /* Create a data-model */
        Map<String,String> root = new HashMap();
        root.put("name", "SiteView");


        /* Merge data-model with template */
        
        template.process(root, outputStream);
        outputStream.flush();
//    	outputStream.println("hell world");
    }
    
    public static void main(String[] args) throws Exception {
        
        /* ------------------------------------------------------------------- */    
        /* You should do this ONLY ONCE in the whole application life-cycle:   */    
    
        /* Create and adjust the configuration */
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(
                new File(Platform.getRoot()+"/templates.freemarker"));
        cfg.setObjectWrapper(new DefaultObjectWrapper());

        /* ------------------------------------------------------------------- */    
        /* You usually do these for many times in the application life-cycle:  */    
                
        /* Get or create a template */
        Template temp = cfg.getTemplate("hello.html");

        /* Create a data-model */
        Map root = new HashMap();
        root.put("name", "Big Joe");
        Map latest = new HashMap();
        root.put("latestProduct", latest);
        latest.put("url", "products/greenmouse.html");
        latest.put("name", "green mouse");

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(System.out);
        temp.process(root, out);
        out.flush();
    }

}
