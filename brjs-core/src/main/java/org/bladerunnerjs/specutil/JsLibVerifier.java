package org.bladerunnerjs.specutil;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.specutil.engine.VerifierChainer;


public class JsLibVerifier extends NodeVerifier<JsLib> {
	private final JsLib jsLib;

	private VerifierChainer verifierChainer;
	
	public JsLibVerifier(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
		verifierChainer = new VerifierChainer(modelTest);
	}
	
	public void nameIsValid() {
		assertTrue(jsLib.getName() + " is not a valid name" , jsLib.isValidName());
	}
	
	public void nameIsInvalid() {
		assertFalse(jsLib.getName() + " is a valid name, but shouldn't be" , jsLib.isValidName());
	}

	public VerifierChainer getAppReturns(App app)
	{
		assertSame(app, jsLib.getApp());
		
		return verifierChainer;
	}
}
