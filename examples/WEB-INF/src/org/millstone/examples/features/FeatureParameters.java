package org.millstone.examples.features;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.DownloadStream;
import org.millstone.base.terminal.ExternalResource;
import org.millstone.base.terminal.ParameterHandler;
import org.millstone.base.terminal.URIHandler;
import org.millstone.base.ui.*;

public class FeatureParameters
	extends Feature
	implements URIHandler, ParameterHandler {

	private Label context = new Label();
	private Label relative = new Label();
	private Table params = new Table();

	public FeatureParameters() {
		super();
		params.addProperty("Values", String.class, "");
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label info =
			new Label(
				"To test this feature, try to "
					+ "add some get parameters to URL. For example if you have "
					+ "the feature browser installed in your local host, try url: ");
		info.setCaption("Usage info");
		l.addComponent(info);
		l.addComponent(new Link("http://localhost:8080/examples/features/test/uri?test=1&test=2",
			new ExternalResource("http://localhost:8080/examples/features/test/uri?test=1&test=2")));

		// URI 
		Panel p1 = new Panel("URI Handler");
		context.setCaption("Last URI handler context");
		p1.addComponent(context);
		relative.setCaption("Last relative URI");
		p1.addComponent(relative);
		l.addComponent(p1);

		// Parameters
		Panel p2 = new Panel("Parameter Handler");
		params.setCaption("Last parameters");
		params.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
		params.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
		p2.addComponent(params);
		l.addComponent(p2);

		return l;
	}

	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Parameters",
			"This is a demonstration of how URL parameters can be recieved and handled."
				+ "Parameters and URL:s can be received trough the windows by registering "
				+ "URIHandler and ParameterHandler classes window.",
			"parameters.jpg" };
	}

	protected String getExampleSrc() {
		return "This is a more advanced example, please see the source of this example, FeatureParameters.java,"+
		"as the complete class is a better demonstration then could be given here.";
	}

	/** Add URI and parametes handlers to window.
	 * @see org.millstone.base.ui.Component#attach()
	 */
	public void attach() {
		super.attach();
		getWindow().addURIHandler(this);
		getWindow().addParameterHandler(this);
	}

	/** Remove all handlers from window
	 * @see org.millstone.base.ui.Component#detach()
	 */
	public void detach() {
		super.detach();
		getWindow().removeURIHandler(this);
		getWindow().removeParameterHandler(this);
	}

	/** Update URI
	 * @see org.millstone.base.terminal.URIHandler#handleURI(URL, String)
	 */
	public DownloadStream handleURI(URL context, String relativeUri) {
		this.context.setValue(context.toString());
		this.relative.setValue(relativeUri);
		return null;
	}

	/** Update parameters table
	 * @see org.millstone.base.terminal.ParameterHandler#handleParameters(Map)
	 */
	public void handleParameters(Map parameters) {
		params.removeAllItems();
		for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
			String name = (String) i.next();
			String[] values = (String[]) parameters.get(name);
			String v = "";
			for (int j = 0; j < values.length; j++) {
				if (v.length() > 0)
					v += ", ";
				v += "'" + values[j] + "'";
			}
			params.addItem(new Object[] { v }, name);
		}
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */