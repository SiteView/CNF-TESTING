package com.siteview.ecc.workbench.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CommitHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public CommitHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        System.out.println("Select " + currentSelection);
        
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) currentSelection;
			try {
				submit(ss);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return null;	
		
	}
	



	private void submit(IStructuredSelection selection) throws JavaModelException, CoreException {
		if (selection.size() == 1) {
    		if (selection.getFirstElement() instanceof IProject) {
    			System.out.println("seletion is project");
    			IPackageFragment[] packages = JavaCore.create((IProject)selection.getFirstElement()).getPackageFragments();
    			for (IPackageFragment group : packages) { 
	    			for (ICompilationUnit unit : group.getCompilationUnits()) {
	    				// Now create the AST for the ICompilationUnits
	    				CompilationUnit parse = parse(unit);
	    				MethodVisitor methodvisitor = new MethodVisitor();
	    				parse.accept(methodvisitor);
	
	    				for (MethodDeclaration method : methodvisitor.getMethods()) {
	    					System.out.print("Method name: " + method.getName() + " Return type: " + method.getReturnType2());
	    				}
	    				
	    				FieldVisitor filedvisitor = new FieldVisitor();
	    				parse.accept(filedvisitor);
	
	    				for (FieldDeclaration field : filedvisitor.getFields()) {
	    					System.out.print("Field name: " + " Type: " + field.getType());
	    				}
	    			} 				
    			}    			
    		}
		}
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
}
