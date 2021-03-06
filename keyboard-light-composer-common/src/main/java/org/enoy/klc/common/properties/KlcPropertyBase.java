package org.enoy.klc.common.properties;

import org.enoy.klc.common.Deletable;
import org.enoy.klc.common.updatables.Dependent;

import java.io.Serializable;

public interface KlcPropertyBase<T extends Serializable> extends Deletable, Dependent, Serializable  {

	public String getName();
	public String getDescription();
	public boolean isValueStrategyAllowed();
	public T getValue();
	public KlcReadOnlyPropertyValue<T> getPropertyValue();
	public T getDefaultValue();
	public Class<T> getPropertyType();
	public boolean isValueStrategyPresent();
	
}
