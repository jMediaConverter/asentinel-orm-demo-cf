package com.asentinel.orm.demo.cf.domain;

import com.asentinel.common.orm.FetchType;
import com.asentinel.common.orm.RelationType;
import com.asentinel.common.orm.mappers.Child;
import com.asentinel.common.orm.mappers.Column;
import com.asentinel.common.orm.mappers.PkColumn;
import com.asentinel.common.orm.mappers.Table;

import java.util.Collections;
import java.util.List;

@Table("CarManufacturers")
public class CarManufacturer {
	
	@PkColumn("id")
	private int id;
	
	@Column("name")
	private String name;
	
	@Child(parentRelationType = RelationType.MANY_TO_ONE, 
			fkName = CarModel.COL_CAR_MANUFACTURER, 
			fetchType = FetchType.LAZY)
	private List<CarModel> models = Collections.emptyList();
	
	
	// ORM constructor
	protected CarManufacturer() {
		
	}

	public CarManufacturer(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CarModel> getModels() {
		return models;
	}

	public void setModels(List<CarModel> models) {
		this.models = models;
	}
	
	@Override
	public String toString() {
		return "CarManufacturer [id=" + id +
					", name=" + name + "]";
	}
}
