package org.millstone.examples.features;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.millstone.base.ui.*;
import org.millstone.base.data.*;

public class FeatureBrowser
	extends CustomComponent
	implements Property.ValueChangeListener {

	private Tree features;
	private Feature currentFeature = null;
	private GridLayout layout;
	private static final String WELCOME_TEXT =
			"In this application you may view a demonstration of each Millstone UI component <br/>"
			+ "and all the layouts. Each component has a description, demonstration and code-example"
			+ "associated with it.<br/><br/>Please send comments and suggestions to <a href=\"mailto:dev@millstone.org\">dev@millstone.org</a>.";

	public FeatureBrowser() {

		// Configure tree
		features = new Tree();
		features.setStyle("menu");
		features.addProperty("name", String.class, "");
		features.addProperty("feature", Feature.class, null);
		features.setItemCaptionPropertyId("name");
		features.addListener(this);
		features.setImmediate(true);
		features.setCaption("Millstone Components");

		// Configure component layout
		layout = new GridLayout(2, 1);
		setCompositionRoot(layout);
		layout.addComponent(features, 0, 0, 0, 0);
		layout.addComponent(
			new Label(WELCOME_TEXT, Label.CONTENT_XHTML),
			1,
			0,
			1,
			0);

		// Test component
		registerFeature("/Field/TextField", new FeatureTextField());
		registerFeature("/Field/DateField", new FeatureDateField());
		registerFeature("/Container/Table", new FeatureTable());
		registerFeature("/Container/Tree", new FeatureTree());
		registerFeature("/Container/Panel", new FeaturePanel());
		registerFeature("/Container/TabSheet", new FeatureTabSheet());
		registerFeature("/Control/Select", new FeatureSelect());
		registerFeature("/Control/Button", new FeatureButton());
		registerFeature("/Basic/Label", new FeatureLabel());
		registerFeature("/Basic/Link", new FeatureLink());
		registerFeature("/Basic/Embedded", new FeatureEmbedded());
		registerFeature("/Layouts/OrderedLayout", new FeatureOrderedLayout());
		registerFeature("/Layouts/GridLayout", new FeatureGridLayout());
		registerFeature("/io/File transfers", new FeatureFileTransfer());
		registerFeature("/io/Parameters", new FeatureParameters());
		registerFeature("/Basic/Window", new FeatureWindow());

		// By default expand all
		for (Iterator i = features.rootItemIds().iterator(); i.hasNext();) {
			features.expandItem(i.next());
		}

	}

	public void registerFeature(String path, Feature feature) {
		StringTokenizer st = new StringTokenizer(path, "/");
		String id = "";
		String parentId = null;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			id += "/" + token;
			if (!features.containsId(id)) {
				features.addItem(id);
				features.setChildrenAllowed(id, false);
			}
			features.getProperty(id, "name").setValue(token);
			if (parentId != null) {
				features.setChildrenAllowed(parentId, true);
				features.setParent(id, parentId);
			}
			if (!st.hasMoreTokens())
				features.getProperty(id, "feature").setValue(feature);
			parentId = id;
		}
	}

	public void valueChange(Property.ValueChangeEvent event) {

		// Change feature
		if (event.getProperty() == features) {
			Object id = features.getValue();
			if (id != null) {
				if (features.areChildrenAllowed(id))
					features.expandItem(id);
				Property p = features.getProperty(id, "feature");
				Feature feature = p != null ? ((Feature) p.getValue()) : null;
				if (feature != null) {
					if (currentFeature != null)
						layout.removeComponent(currentFeature);
					currentFeature = feature;
					layout.removeComponent(1, 0);
					layout.addComponent(currentFeature, 1, 0, 1, 0);
				} else {
					layout.removeComponent(1, 0);
					layout.addComponent(
						new Label(WELCOME_TEXT, Label.CONTENT_XHTML),
						1,
						0,
						1,
						0);

				}
			}
		}
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */
