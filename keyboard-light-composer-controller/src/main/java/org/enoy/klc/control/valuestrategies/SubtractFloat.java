package org.enoy.klc.control.valuestrategies;

import org.enoy.klc.common.factories.FactoryGenericType;
import org.enoy.klc.common.factories.Name;
import org.enoy.klc.common.properties.KlcPropertyContainer;
import org.enoy.klc.common.properties.KlcWritableProperty;
import org.enoy.klc.common.properties.primitive.FloatKlcProperty;
import org.enoy.klc.common.properties.valuestrategy.ValueStrategy;

@FactoryGenericType(Float.class)
@Name("Subtract Float")
public class SubtractFloat implements ValueStrategy<Float>, KlcPropertyContainer {

	private static final long serialVersionUID = 2154436004080599672L;
	
	private FloatKlcProperty a;
	private FloatKlcProperty b;

	public SubtractFloat() {
		a = new FloatKlcProperty("A", "A Value. A - B", true, 1f);
		b = new FloatKlcProperty("B", "B Value. A - B", true, 1f);
	}

	@Override
	public Float getValue() {
		return a.getValue() - b.getValue();
	}

	@Override
	public KlcWritableProperty<?>[] getProperties() {
		return new KlcWritableProperty<?>[] { a, b };
	}

}