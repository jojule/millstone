package org.millstone.base.ui;

import org.millstone.base.data.BufferedValidatable;
import org.millstone.base.data.Property;
import org.millstone.base.ui.Component.Focusable;

/**
 * @author Sami Ekblad
 *
 */
public interface Field
	extends
		Component,
		BufferedValidatable,
		Property,
		Property.ValueChangeNotifier,
		Property.ValueChangeListener,
		Property.Editor,
		Focusable {

	void setCaption(String caption);

	String getDescription();
	
	void setDescription(String caption);

	/** An <code>Event</code> object specifying the Field whose value
	 * has been changed.
	 * @author IT Mill Ltd.
		 * @version @VERSION@
		 * @since 3.0
	 */
	public class ValueChangeEvent
		extends Component.Event
		implements Property.ValueChangeEvent {

		/** Constructs a new event object with the specified source
		 * field object.
		 *
		 * @param source the field that caused the event
		 */
		public ValueChangeEvent(Field source) {
			super(source);
		}

		/** Gets the Property which triggered the event.
		 *
		 * @return Source Property of the event.
		 */
		public Property getProperty() {
			return (Property) getSource();
		}
	}
}
