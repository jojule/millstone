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

		// Create locale selector
		Select selector = new Select("Application Locale");
		selector.addProperty("name",String.class,"");
		selector.setItemCaptionPropertyId("name");
		Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++) {
			selector.addItem(locales[i]).getProperty("name").setValue(locales[i].getDisplayName());		
		}
		selector.setImmediate(true);
		selector.setPropertyDataSource(new MethodProperty(this,"locale"));
		main.addComponent(selector);

	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */