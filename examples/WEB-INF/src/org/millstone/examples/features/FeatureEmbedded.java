package org.millstone.examples.features;

import java.util.Hashtable;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.Sizeable;
import org.millstone.base.ui.*;

public class FeatureEmbedded extends Feature {

	public FeatureEmbedded() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Embedded component");
		Embedded emb = new Embedded("Embedded Caption");
		emb.setClassId("clsid:F08DF954-8592-11D1-B16A-00C0F0283628");
		emb.setWidth(100);
		emb.setHeight(50);
		emb.setParameter("BorderStyle", "1");
		emb.setParameter("MousePointer", "1");
		emb.setParameter("Enabled", "1");
		emb.setParameter("Min", "1");
		emb.setParameter("Max", "10");

		show.addComponent(emb);
		l.addComponent(show);

		Hashtable alternateEditors = new Hashtable();
		
		Select s = new Select("heightUnits");
		s.addProperty("name", String.class, "");
		s.setItemCaptionPropertyId("name");
		for (int i = 0; i < Sizeable.UNIT_SYMBOLS.length; i++) {
			s.addItem(new Integer(i)).getProperty("name").setValue(
				Sizeable.UNIT_SYMBOLS[i]);

		}
		alternateEditors.put("heightUnits",s);

		s = new Select("widthUnits");
		s.addProperty("name", String.class, "");
		s.setItemCaptionPropertyId("name");
		for (int i = 0; i < Sizeable.UNIT_SYMBOLS.length; i++) {
			s.addItem(new Integer(i)).getProperty("name").setValue(
				Sizeable.UNIT_SYMBOLS[i]);

		}
		
		alternateEditors.put("widthUnits", s);

		// Configuration
		l.addComponent(
				createPropertyPanel(emb,
					new String[] {
						"type",
						"classId",
						"width",
						"height",
						"widthUnits",
						"heightUnits" },alternateEditors));

		

		return l;
	}

	protected String getExampleSrc() {
		return "Embedded emb = new Embedded(\"Caption\");\n"
			+ "emb.setClassId(\"clsid:F08DF954-8592-11D1-B16A-00C0F0283628\");\n"
			+ "emb.setWidth(100);\n"
			+ "emb.setHeight(50);\n"
			+ "\n//Set optional object parameters\n"
			+ "emb.setParameter(\"BorderStyle\",\"1\");\n"
			+ "emb.setParameter(\"MousePointer\",\"1\");\n"
			+ "emb.setParameter(\"Enabled\",\"1\");\n"
			+ "emb.setParameter(\"Min\",\"1\");\n"
			+ "emb.setParameter(\"Max\",\"10\");\n";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Embedded",
			"The embedding feature allows for adding multimedia and other non-specified content to your application. "
			+ "The feature has provisions for embedding both applets and Active X controls. "
			+ "Actual support for embedded media types is left to the terminal."
			+ "<br/>"
			+ "On the demo tab you can try out how the different properties affect the presentation "
			+ "of the component.","embedded.jpg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */