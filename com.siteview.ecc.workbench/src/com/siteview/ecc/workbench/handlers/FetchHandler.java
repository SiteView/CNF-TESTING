

package com.siteview.ecc.workbench.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to fetch the changes from the backend through RMI to update the workbench.  Based on different cope:
 * 
 *  <ul>
 * <li>Projects wide:</li>
 * <li>Package: there is a java class with the same name as the parent package, the group object is derived from this class.  Including its subpackages
 * <li>Java Class: take the key=value to obtain the monitor object</li>
 * </ul>
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class FetchHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public FetchHandler() {
	}

	List groups = new ArrayList();
	List monitors = new ArrayList();
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
//        System.out.println("Select " + currentSelection);
        
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) currentSelection;
			try {
				fetch(ss);
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;	
		
	}

	/**
	 * @param selection
	 * @throws Exception
	 */
	private void fetch(IStructuredSelection selection) throws Exception {
		groups.clear();
		monitors.clear();
		if (selection.size() == 1) {
    		if (selection.getFirstElement() instanceof IProject) {    			
    			System.out.println("seletion is project");
    			IPackageFragment[] packages = JavaCore.create((IProject)selection.getFirstElement()).getPackageFragments();
    			getGroups(packages);
    		}
    		if (selection.getFirstElement() instanceof IPackageFragmentRoot) {    			
    			System.out.println("seletion is IPackageFragmentRoot: " + ((IPackageFragmentRoot)selection.getFirstElement()).getElementName()) ;
    			IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) selection.getFirstElement();
    			List<IPackageFragment> packageList = new ArrayList<IPackageFragment>();
    		    IJavaElement[] allPackages =  packageFragmentRoot.getChildren();
    		    for (IJavaElement javaElement : allPackages) {
    		        	packageList.add((IPackageFragment) javaElement);
    		    }
    		    getGroups(packageList.toArray(new IPackageFragment[packageList.size()] ));
    		}
    		if (selection.getFirstElement() instanceof IPackageFragment) {    			
    			System.out.println("seletion is IPackageFragment: " + ((IPackageFragment)selection.getFirstElement()).getElementName()) ;

    			getGroups((IPackageFragment)selection.getFirstElement());
    		}
    		if (selection.getFirstElement() instanceof ICompilationUnit) {    			
    			System.out.println("seletion is ICompilationUnit: " + ((ICompilationUnit)selection.getFirstElement()).getElementName()) ;
    			setMonitor((ICompilationUnit)selection.getFirstElement());
    		}
		}
	}
	
	/**
	* Get the groups from a single package and its subpackages, using recursive
	* @return List[PackageFragment]
	*/
	private  void getGroups(IPackageFragment p) throws Exception {
		
		getGroups(new IPackageFragment[] {p});
		
		if (!p.getElementName().isEmpty() && p.hasSubpackages()) {
			for (IPackageFragment subp:  getSubPackages(p)) {
				getGroups(subp);			
			}
		}		
	}
	
	/**
	* Get all source packages and processing all java files.
	* @return List[PackageFragment]
	*/
	private  void getGroups(IPackageFragment[] packages) throws Exception {
		for (IPackageFragment p : packages) { 
			if (p.getKind() == IPackageFragmentRoot.K_SOURCE) {
				String packageName = p.getElementName();
				String[] temp;
				String delimiter = "\\.";
				temp = packageName.split(delimiter);
				packageName = temp.length==0 ? packageName: temp[temp.length-1];
				
				for (ICompilationUnit unit : p.getCompilationUnits()) {
						String monitorName = unit.getElementName();
						monitorName = monitorName.substring(0, monitorName.length()-5);
						if (packageName.equalsIgnoreCase(monitorName))  
							getGroup(unit);
						else 
							setMonitor(unit);
				} 
				

				
//				if (!p.getElementName().isEmpty() && p.hasSubpackages()) {
//					List<IPackageFragment> subpackages = JavaElementUtil.getSubPackages(p);
//					for (IPackageFragment subpackage : subpackages) {
//						System.out.println(p.getElementName() + "'s subpackages: " + subpackage.getElementName());
//					}
//	//				getGroups((IPackageFragment[]) subpackages.toArray(new IPackageFragment[subpackages.size()] ));
//				}			
			}
		}
		System.out.println("Number of package is: "+ groups.size() + ", Number of monitors is: "+ monitors.size() );
	}
	
	/**
	 * Read the default java file with the same name as its parent package name.  Also, insert the parent group info.
	 * @param unit
	 */
	private void getGroup(ICompilationUnit unit) {
		CompilationUnit parse = parse(unit);
		
		FieldVisitor filedvisitor = new FieldVisitor();
		parse.accept(filedvisitor);
		
		Map group = new HashMap();
		String packageName = getPackageFor(unit).getElementName();
		String [] temp;
		temp = packageName.split("\\.");
		String parent = temp.length==2 ? temp[0] : "";
		
		group.put("_parent", parent);
		for (FieldDeclaration field : filedvisitor.getFields()) {
			String name = ((VariableDeclarationFragment)field.fragments().get(0)).getName().getFullyQualifiedName();
			String value = ((VariableDeclarationFragment)field.fragments().get(0)).getInitializer().toString();
//			System.out.println("Field name: " + name + ",Value: " + value);
			group.put(name, value);			
		}
		groups.add(group);
		
	}

	/**
	 * Fetch the monitor map from the backend and set the java file content: replacing existing one or create new
	 * 
	 * @param unit
	 */
	private void setMonitor(ICompilationUnit unit) {
		CompilationUnit parse = parse(unit);
		AST ast = parse.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);
		
		//replacing existing
//		rewriter.replace(node, replacement, editGroup)

		//create new if no existing key
		
		FieldVisitor filedvisitor = new FieldVisitor();
		parse.accept(filedvisitor);

		Map monitor = new HashMap();
		for (FieldDeclaration field : filedvisitor.getFields()) {
			String name = ((VariableDeclarationFragment)field.fragments().get(0)).getName().getFullyQualifiedName();
			String value = ((VariableDeclarationFragment)field.fragments().get(0)).getInitializer().toString();
//			System.out.println("Field name: " + name + ",Value: " + value);
			monitor.put(name, value);			
		}
		monitors.add(monitor);
		
	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 * 
	 * @param unit
	 * @return
	 */
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
	
	  /**
	   * If we call <code>IPackageFragment.getChildren()</code>
	   * we do NOT get sub packages!<br>
	   * This is a workaround. We calculate sub packages by going to the
	   * parent of code>IPackageFragment</code> 
	   * which is a <code>IPackageFragmentRoot</code> and call
	   * <code>IPackageFragmentRoot.getChildren()</code>
	   * When a name of a subpackage starts with package name + "." it is a
	   * subpackage
	   * 
	   * @return a list of all sub packages for the input package. For example input 
	   * <pre>org.ucdetector.cycle</pre>
	   * return a list with:
	   * <ul>
	   * <li><code>org.ucdetector.cycle.model</code></li>
	   * <li><code>org.ucdetector.cycle.search</code></li>
	   * </ul>
	   */
	  public static List<IPackageFragment> getSubPackages(
	      IPackageFragment packageFragment) throws CoreException {
	    List<IPackageFragment> subPackages = new ArrayList<IPackageFragment>();
	    IJavaElement[] allPackages = ((IPackageFragmentRoot) packageFragment
	        .getParent()).getChildren();
	    for (IJavaElement javaElement : allPackages) {
	      IPackageFragment pakage = (IPackageFragment) javaElement;
	      String startPackagenName = packageFragment.getElementName() + "."; //$NON-NLS-1$
	      if (packageFragment.isDefaultPackage()
	          || pakage.getElementName().startsWith(startPackagenName)) {
	        subPackages.add(pakage);
	      }
	    }
	    return subPackages;
	  }
	  
	  /**
	   * @return the package for an class, method, or field
	   */
	  public static IPackageFragment getPackageFor(IJavaElement javaElement) {
	    IJavaElement parent = javaElement.getParent();
	    while (true) {
	      if (parent instanceof IPackageFragment || parent == null) {
	        return (IPackageFragment) parent;
	      }
	      parent = parent.getParent();
	    }
	  }
}
