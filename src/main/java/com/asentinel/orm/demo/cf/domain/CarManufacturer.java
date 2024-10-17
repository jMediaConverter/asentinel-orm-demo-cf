package com.asentinel.orm.demo.cf.domain;

import static java.util.Collections.emptyList;

import java.util.List;

import com.asentinel.common.orm.FetchType;
import com.asentinel.common.orm.RelationType;
import com.asentinel.common.orm.mappers.Child;
import com.asentinel.common.orm.mappers.Column;
import com.asentinel.common.orm.mappers.PkColumn;
import com.asentinel.common.orm.mappers.Table;

@Table("CarManufacturers")
public class CarManufacturer {
	
	public static final String COL_NAME = "name";

	@PkColumn("id")
	private int id;
	
	@Column(COL_NAME)
	private String name;
	
	@Child(parentRelationType = RelationType.MANY_TO_ONE, 
			fkName = CarModel.COL_CAR_MANUFACTURER, 
			fetchType = FetchType.LAZY)
	private List<CarModel> models = emptyList();
	
	
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
		return "CarManufacturer [id=" + id + ", name=" + name + "]";
	}
}
