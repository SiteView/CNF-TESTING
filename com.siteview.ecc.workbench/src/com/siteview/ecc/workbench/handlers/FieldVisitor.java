package com.siteview.ecc.workbench.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;

public class FieldVisitor extends ASTVisitor {
	List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();

	@Override
	public boolean visit(FieldDeclaration node) {
		fields.add(node);
		return super.visit(node);
	}

	public List<FieldDeclaration> getFields() {
		return fields;
	}
}
