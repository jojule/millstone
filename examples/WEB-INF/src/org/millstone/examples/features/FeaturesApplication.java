package org.millstone.examples.features;

import java.util.Locale;

import org.millstone.base.ui.*;
import org.millstone.base.data.util.MethodProperty;

public class FeaturesApplication
	extends org.millstone.base.Application {

	public void init() {
		Window main = new Window("Millstone Features Tour");
		setMainWindow(main);
		main.addComponent(new FeatureBrowser());
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */