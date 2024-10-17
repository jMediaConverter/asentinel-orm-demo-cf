package com.asentinel.orm.demo.cf.domain;

import java.util.HashMap;
import java.util.Map;

import com.asentinel.common.orm.mappers.dynamic.DefaultDynamicColumn;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumn;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumnsEntity;

public class CustomFieldsCarManufacturer extends CarManufacturer 
	implements DynamicColumnsEntity<DynamicColumn> {
	
	private final Map<DynamicColumn, Object> customFields = new HashMap<>();

	// ORM constructor
	protected CustomFieldsCarManufacturer() {
		
	}

	public CustomFieldsCarManufacturer(String name, Map<DynamicColumn, Object> customFields) {
		super(name);
		this.customFields.putAll(customFields);
	}

	@Override
	public void setValue(DynamicColumn column, Object value) {
		customFields.put(column, value);
		
	}

	@Override
	public Object getValue(DynamicColumn column) {
		return customFields.get(column);
	}

	@Override
	public String toString() {
		return super.toString() + " [customFields=" + customFields + "]";
	}
	
	
}
