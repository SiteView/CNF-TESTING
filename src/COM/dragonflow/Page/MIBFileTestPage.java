/**
 * 
 */
package COM.dragonflow.Page;

/**
 * @author Administrator
 *
 */
import java.io.*;
import com.netaphor.mibcompiler.*;
/*/SiteView/cgi/go.exe/SiteView?page=MIBFileTest&account=administrator*/
public class MIBFileTestPage extends CGI {

	private String content="";
	public void printBody() throws Exception {
		 printBodyHeader("MIB file Test");
        if(request.isPost())
        {
            doPost();
        } else
        {
			doGet();
        }
		printFooter(outputStream);
	}
	public void doGet()
	{
       
        outputStream.println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=gb2312'><title>请粘贴MIB文件内容</title></head><body>\n");
		outputStream.println("<form method='POST' action='/SiteView/cgi/go.exe/SiteView'>\n");
		outputStream.println("<input type='hidden' name='page' value='MIBFileTest'>\n");
		outputStream.println("<input type='hidden' name='account' value='administrator'>");
		outputStream.println("<input type='hidden' name='operation' value='check'>");
		outputStream.println("<p>请粘贴MIB文件内容</p>");
		outputStream.println("<p><textarea rows='36' name='content' cols='68'>");
		outputStream.println(content);
		outputStream.println("</textarea><br>");
		outputStream.println("<input type='submit' value='提交' name='B1'><input type='reset' value='重置' name='B2'></p>");
		outputStream.println("</form></body></html>");
	}
	public void doPost() throws Exception
	{
		content=request.getValue("content");
		
		MIBCompiler mibc=new MIBCompiler();
		File f=new File("/mibtest.txt");
		FileOutputStream fos=new java.io.FileOutputStream(f);
		fos.write(content.getBytes());
		try{
			mibc.compileFromFile("/mibtest.txt");
			
			outputStream.println("MIB 文件内容语法检验通过!");
			
		}catch(com.netaphor.mibcompiler.SyntaxError e)
		{
			outputStream.println("MIB 文件内容语法错误,错误码为："+e.getErrorCode()+"!");
		}catch(Exception err)
		{
			outputStream.println("MIB 文件内容检查失败，错误为："+err.getMessage()+"!");
		}
		finally
		{
			doGet();
			outputStream.flush();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
