package org.millstone.base.ui;
import org.millstone.base.data.Property;
import org.millstone.base.data.Item;

/** Factory for creating new Field-instances based on type,
 *  datasource and/or context.
 *
 * @author IT Mill Ltd.
 * @version  @VERSION@
 * @since 3.1
 */
public interface FieldFactory {


	/** Creates field based on type of data.
	 *
	 *
	 * @param type The type of data presented in field
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 *	 
	 */
	Field createField(Class type, Component uiContext);
	
	/** Creates field based on the property datasource.
	 *
	 * @param property The property datasource.
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 */
	Field createField(Property property, Component uiContext);

	/** Creates field based on the item and property id.
	 * 
	 * @param item The item where the property belongs to.
	 * @param propertyId Id of the property.
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 */
	Field createField(Item item, Object propertyId, Component uiContext);
}