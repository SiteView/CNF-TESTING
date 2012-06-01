package com.siteview.ecc.workbench.editors;


import java.io.StringWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
//import org.eclipse.ui.examples.javaeditor;
//import org.eclipse.ui.forms.article.rcp.FreeFormPage;
//import org.eclipse.ui.forms.article.rcp.MasterDetailsPage;
//import org.eclipse.ui.forms.article.rcp.PageWithSubPages;
//import org.eclipse.ui.forms.article.rcp.SecondPage;
//import org.eclipse.ui.forms.article.rcp.ThirdPage;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.ide.IDE;

import com.siteview.ecc.workbench.handlers.FieldVisitor;
import com.siteview.ecc.workbench.handlers.MethodVisitor;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class SiteviewPageEditor extends FormEditor implements IResourceChangeListener{

	/** The text editor used in page 0. */
//	private TextEditor editor;
	private CompilationUnitEditor editor;
//	private JavaEditor editor;

	/** The font chosen in page 1. */
	private Font font;

	/** The text widget used in page 2. */
	private StyledText text;
	/**
	 * Creates a multi-page editor example.
	 */
	
	private SourcePage src;
	
	List groups = new ArrayList();
	public List monitors = new ArrayList();
	
	public SiteviewPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage0() {
		try {
//			editor = new TextEditor();			
			editor = new CompilationUnitEditor();
//			editor = new JavaEditor();

			int index = addPage(editor, getEditorInput());
//			 IJavaElement inputJavaElement = 
			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage3() {
		try {
			
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
			IFile file = input.getFile();
			IProject project = file.getProject();		
			ICompilationUnit unit =  JavaCore.createCompilationUnitFrom(file);
			
			CompilationUnit p2 =  parse(unit);
			
			getMonitor(unit);
			
			FormEditorInput forminput = new FormEditorInput("SiteviewPageEditor");
			forminput.setGroups(groups);
			forminput.setMonitors(monitors);	
			forminput.setUnit(p2);
			src = new SourcePage(this);
			int index = addPage(src, forminput);
			setPageText(index, src.getTitle());
			
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}	
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	void createPage1() {

		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		Button fontButton = new Button(composite, SWT.NONE);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		fontButton.setLayoutData(gd);
		fontButton.setText("Change Font...");
		
		fontButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setFont();
			}
		});

		int index = addPage(composite);
		setPageText(index, "Properties");
	}
	/**
	 * Creates page 2 of the multi-page editor,
	 * which shows the sorted text.
	 */
	void createPage2() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "Preview");
	}
	
	void createPageother() {
		// TODO Auto-generated method stub
		try {
			int index = addPage(src, getEditorInput());
//			setPageText(index, src.getTitle());			
			addPage(new FreeFormPage(this), getEditorInput());

			addPage(new SecondPage(this), getEditorInput());
//			int index = addPage(new Composite(getContainer(), SWT.NULL));
//			setPageText(index, "Composite"); //$NON-NLS-1$
			addPage(new ThirdPage(this), getEditorInput());
			addPage(new MasterDetailsPage(this), getEditorInput());
			addPage(new PageWithSubPages(this), getEditorInput());	
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		createPage3();
//		createPageother();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor1 = getEditor(0);		
		editor1.doSaveAs();
		setPageText(0, editor1.getTitle());
		setInput(editor1.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 2) {
			sortWords();
			sortWords1();
		}
		else if (newPageIndex == 3) {
			sortWords1();
		}	
		else
		{
			
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
	/**
	 * Sets the font related data to be applied to the text in page 2.
	 */
	void setFont() {
		FontDialog fontDialog = new FontDialog(getSite().getShell());
		fontDialog.setFontList(text.getFont().getFontData());
		FontData fontData = fontDialog.open();
		if (fontData != null) {
			if (font != null)
				font.dispose();
			font = new Font(text.getDisplay(), fontData);
			text.setFont(font);
		}
	}
	/**
	 * Sorts the words in page 0, and shows them in page 2.
	 */
	void sortWords() {

		String editorText =
			editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();	
		
		editor.getDocumentProvider().getDocument(editor.getEditorInput());
		
		StringTokenizer tokenizer =
			new StringTokenizer(editorText, " \t\n\r\f!@#\u0024%^&*()-_=+`~[]{};:'\",.<>/?|\\");
		ArrayList editorWords = new ArrayList();
		while (tokenizer.hasMoreTokens()) {
			editorWords.add(tokenizer.nextToken());
		}

		Collections.sort(editorWords, Collator.getInstance());
		StringWriter displayText = new StringWriter();
		for (int i = 0; i < editorWords.size(); i++) {
			displayText.write(((String) editorWords.get(i)));
			displayText.write(System.getProperty("line.separator"));
		}
		text.setText(displayText.toString());
	}
	
	/**
	 * Sorts the words in page 0, and shows them in page 2.
	 */
	void sortWords1() {
		String editorText =
			editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
//		if(src.text != null)
//			src.text.setText(editorText);
		
//		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
//		IFile file = input.getFile();
//		IProject project = file.getProject();
//
//		IPackageFragment[] packages;
//		try {
//			packages = JavaCore.create(project).getPackageFragments();
//			getGroups(packages);
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
//		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
//		IFile file = input.getFile();
//		IProject project = file.getProject();		
//		ICompilationUnit unit =  JavaCore.createCompilationUnitFrom(file);
//		getMonitor(unit);
//		
//		FormEditorInput forminput = new FormEditorInput("SiteviewPageEditor");
//		forminput.setGroups(groups);
//		forminput.setMonitors(monitors);
		
//		FormEditorInput input = (FormEditorInput)this.getEditorInput();
//		String dd = input.getGroups().toArray().toString() + input.getMonitors().toArray().toString();
//		text.setText(dd);
	}	
	
	@Override
	protected void addPages() {
		
	}

	/**
	 * Read the java file and add the content of the java file to a monitor map
	 * @param unit
	 */
	private void getMonitor(ICompilationUnit unit) {
		monitors.clear();
		CompilationUnit parse = parse(unit);
		
		MethodVisitor methodvisitor = new MethodVisitor();
		parse.accept(methodvisitor);

		for (MethodDeclaration method : methodvisitor.getMethods()) {
//			System.out.println("Method name: " + method.getName() + " Return type: " + method.getReturnType2());
		}
		
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
	
//	/**
//	* Get the groups from a single package and its subpackages, using recursive
//	* @return List[PackageFragment]
//	*/
//	private  void getGroups(IPackageFragment p) throws Exception {
//		
//		getGroups(new IPackageFragment[] {p});
//		
//		if (!p.getElementName().isEmpty() && p.hasSubpackages()) {
//			for (IPackageFragment subp:  getSubPackages(p)) {
//				getGroups(subp);			
//			}
//		}		
//	}
//	
//	/**
//	* Get all source packages and processing all java files.
//	* @return List[PackageFragment]
//	*/
//	private  void getGroups(IPackageFragment[] packages) throws Exception {
//		for (IPackageFragment p : packages) { 
//			if (p.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				String packageName = p.getElementName();
//				String[] temp;
//				String delimiter = "\\.";
//				temp = packageName.split(delimiter);
//				packageName = temp.length==0 ? packageName: temp[temp.length-1];
//				
//				for (ICompilationUnit unit : p.getCompilationUnits()) {
//						String monitorName = unit.getElementName();
//						monitorName = monitorName.substring(0, monitorName.length()-5);
//						if (packageName.equalsIgnoreCase(monitorName))  
//							getGroup(unit);
//						else 
//							getMonitor(unit);
//				} 
//				
//
//				
////				if (!p.getElementName().isEmpty() && p.hasSubpackages()) {
////					List<IPackageFragment> subpackages = JavaElementUtil.getSubPackages(p);
////					for (IPackageFragment subpackage : subpackages) {
////						System.out.println(p.getElementName() + "'s subpackages: " + subpackage.getElementName());
////					}
////	//				getGroups((IPackageFragment[]) subpackages.toArray(new IPackageFragment[subpackages.size()] ));
////				}			
//			}
//		}
//		System.out.println("Number of package is: "+ groups.size() + ", Number of monitors is: "+ monitors.size() );
//	}
//	
//	/**
//	 * Read the default java file with the same name as its parent package name.  Also, insert the parent group info.
//	 * @param unit
//	 */
//	private void getGroup(ICompilationUnit unit) {
//		CompilationUnit parse = parse(unit);
//		
//		
//		FieldVisitor filedvisitor = new FieldVisitor();
//		parse.accept(filedvisitor);
//		
//		Map group = new HashMap();
//		String packageName = getPackageFor(unit).getElementName();
//		String [] temp;
//		temp = packageName.split("\\.");
//		String parent = temp.length==2 ? temp[0] : "";
//		
//		group.put("_parent", parent);
//		for (FieldDeclaration field : filedvisitor.getFields()) {
//			String name = ((VariableDeclarationFragment)field.fragments().get(0)).getName().getFullyQualifiedName();
//			String value = ((VariableDeclarationFragment)field.fragments().get(0)).getInitializer().toString();
////			System.out.println("Field name: " + name + ",Value: " + value);
//			group.put(name, value);			
//		}
//		groups.add(group);
//		
//	}	
//	
//	  /**
//	   * If we call <code>IPackageFragment.getChildren()</code>
//	   * we do NOT get sub packages!<br>
//	   * This is a workaround. We calculate sub packages by going to the
//	   * parent of code>IPackageFragment</code> 
//	   * which is a <code>IPackageFragmentRoot</code> and call
//	   * <code>IPackageFragmentRoot.getChildren()</code>
//	   * When a name of a subpackage starts with package name + "." it is a
//	   * subpackage
//	   * 
//	   * @return a list of all sub packages for the input package. For example input 
//	   * <pre>org.ucdetector.cycle</pre>
//	   * return a list with:
//	   * <ul>
//	   * <li><code>org.ucdetector.cycle.model</code></li>
//	   * <li><code>org.ucdetector.cycle.search</code></li>
//	   * </ul>
//	   */
//	  public static List<IPackageFragment> getSubPackages(
//	      IPackageFragment packageFragment) throws CoreException {
//	    List<IPackageFragment> subPackages = new ArrayList<IPackageFragment>();
//	    IJavaElement[] allPackages = ((IPackageFragmentRoot) packageFragment
//	        .getParent()).getChildren();
//	    for (IJavaElement javaElement : allPackages) {
//	      IPackageFragment pakage = (IPackageFragment) javaElement;
//	      String startPackagenName = packageFragment.getElementName() + "."; //$NON-NLS-1$
//	      if (packageFragment.isDefaultPackage()
//	          || pakage.getElementName().startsWith(startPackagenName)) {
//	        subPackages.add(pakage);
//	      }
//	    }
//	    return subPackages;
//	  }
//	  
//	  /**
//	   * @return the package for an class, method, or field
//	   */
//	  public static IPackageFragment getPackageFor(IJavaElement javaElement) {
//	    IJavaElement parent = javaElement.getParent();
//	    while (true) {
//	      if (parent instanceof IPackageFragment || parent == null) {
//	        return (IPackageFragment) parent;
//	      }
//	      parent = parent.getParent();
//	    }
//	  }	
}
