package org.millstone.base.ui;

import java.util.Collection;
import java.util.Iterator;

import org.millstone.base.data.Item;
import org.millstone.base.data.Validator;
import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.PaintTarget;

/**
 * @author Sami Ekblad
 *
 */
public class FormField extends AbstractField {

	/** The form editor. */
	Form form;

	/** Tab index if this form */
	private int tabIndex;

	/** List of value change listeners */
	private Collection valueChangeListener;

	/** Is the value modified */
	private boolean propertyValueModified;

	/** Properties which are visible in the editor. */
	private Collection visibleProperties = null;
	private Object dataSourceObject;

	public FormField() {
		this(new GridLayout(2, 1));
	}
	
	public FormField(Layout formLayout) {
		form = new Form(formLayout);
		form.setStyle("formfield");
	}	

	/**
	 * @see org.millstone.base.ui.AbstractComponent#getTag()
	 */
	public String getTag() {
		return "component";
	}

	/**
	 * @see org.millstone.base.ui.AbstractField#getType()
	 */
	public Class getType() {
		if (getPropertyDataSource() != null)
			return getPropertyDataSource().getType();
		return Object.class;
	}

	/**
	 * @see org.millstone.base.data.Validatable#isValid()
	 */
	public boolean isValid() {
		return form.isValid();
	}

	/**
	 * @see org.millstone.base.data.Validatable#validate()
	 */
	public void validate() throws Validator.InvalidValueException {
		form.validate();

	}

	/**
	 * @see org.millstone.base.data.Validatable#isInvalidAllowed()
	 */
	public boolean isInvalidAllowed() {
		return false;
	}

	/**
	 * @see org.millstone.base.data.Validatable#setInvalidAllowed(boolean)
	 */
	public void setInvalidAllowed(boolean invalidValueAllowed)
		throws UnsupportedOperationException {
	}

	/**
	 * Method setFormDataSource.
	 * @param value
	 */
	protected void setFormDataSource(Object data, Collection properties) {
		
		// If data is an item use it.
		Item item = null;
		if (data instanceof Item) {
			item = (Item) data;
		} else if (data != null) {
			item = new BeanItem(data);
		}

		// Set the datasource to form
		if (item != null && properties != null) {
			// Show only given properties
			this.form.setItemDataSource(item, properties);
		} else {
			// Show all properties
			this.form.setItemDataSource(item);
		}
	}

	/**
	 * @see org.millstone.base.ui.Component.Focusable#focus()
	 */
	public void focus() {
		Field f = getFirstField();
		if (f != null) {
			f.focus();
		}
	}
	/**Get first field in form.
	 * @return Field
	 */
	private Field getFirstField() {
		Object id = null;
		if (this.form.getItemPropertyIds() != null) {
			id = this.form.getItemPropertyIds().iterator().next();
		}
		if (id != null)
			return this.form.getField(id);
		return null;
	}

	/**
	 * @see org.millstone.base.ui.Component.Focusable#getTabIndex()
	 */
	public int getTabIndex() {
		return this.tabIndex;
	}

	/**
	 * @see org.millstone.base.ui.Component.Focusable#setTabIndex(int)
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
		for (Iterator i = this.form.getItemPropertyIds().iterator();
			i.hasNext();
			)
			 (this.form.getField(i.next())).setTabIndex(tabIndex);
	}
	/**
	 * @see org.millstone.base.ui.AbstractComponent#paintContent(org.millstone.base.terminal.PaintTarget)
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		form.paint(target);
	}

	/** Get the form implementing the field editor.
	 * @return The form implementing the field editor.
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * Returns the visibleProperties.
	 * @return Collection
	 */
	public Collection getVisibleProperties() {
		return visibleProperties;
	}

	/**
	 * Sets the visibleProperties.
	 * @param visibleProperties The visibleProperties to set
	 */
	public void setVisibleProperties(Collection visibleProperties) {
		this.visibleProperties = visibleProperties;
		Object value = getValue();
		setFormDataSource(value, getVisibleProperties());
	}

	/**
	 * @see org.millstone.base.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	protected void setInternalValue(Object newValue) {
		super.setInternalValue(newValue);
		
		// Ignore form updating if data object has not changed. 
		if (this.dataSourceObject != newValue) { 
			setFormDataSource(newValue, getVisibleProperties());
			this.dataSourceObject = newValue;
		}
	}

	/**
	 * @see org.millstone.base.ui.Component#attach()
	 */
	public void attach() {
		super.attach();
		form.attach();
	}

	/**
	 * @see org.millstone.base.data.Buffered#commit()
	 */
	public void commit() throws SourceException {
		super.commit();
		form.commit();
	}

	/**
	 * @see org.millstone.base.ui.Component#detach()
	 */
	public void detach() {
		super.detach();
		form.detach();
	}

	/**
	 * @see org.millstone.base.data.Buffered#discard()
	 */
	public void discard() throws SourceException {
		super.discard();
		form.discard();
	}

	/**
	 * @see org.millstone.base.ui.Component#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		form.setReadOnly(readOnly);
	}

	/**
	 * @see org.millstone.base.data.Buffered#setReadThrough(boolean)
	 */
	public void setReadThrough(boolean readThrough) throws SourceException {
		super.setReadThrough(readThrough);
		form.setReadThrough(readThrough);
	}

	/**
	 * @see org.millstone.base.data.Buffered#setWriteThrough(boolean)
	 */
	public void setWriteThrough(boolean writeThrough) throws SourceException {
		super.setWriteThrough(writeThrough);
		form.setWriteThrough(writeThrough);
	}

}
