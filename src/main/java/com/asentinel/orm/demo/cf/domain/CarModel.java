package com.asentinel.orm.demo.cf.domain;

import com.asentinel.common.orm.FetchType;
import com.asentinel.common.orm.mappers.Child;
import com.asentinel.common.orm.mappers.Column;
import com.asentinel.common.orm.mappers.PkColumn;
import com.asentinel.common.orm.mappers.Table;

@Table("CarModels")
public class CarModel {
	
	public static final String COL_CAR_MANUFACTURER = "CarManufacturer";
	
	@PkColumn("id")
	private int id;
	
	@Column("name")
	private String name;
	
	@Column("type")
	private CarType type;
	
	@Child(fkName = COL_CAR_MANUFACTURER, fetchType = FetchType.LAZY)
	private CarManufacturer carManufacturer;
	
	// ORM constructor
	protected CarModel() {
		
	}

	public CarModel(String name, CarType type, CarManufacturer carManufacturer) {
		this.name = name;
		this.type = type;
		this.carManufacturer = carManufacturer;
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

	public CarType getType() {
		return type;
	}

	public void setType(CarType type) {
		this.type = type;
	}
	
	public CarManufacturer getCarManufacturer() {
		return carManufacturer;
	}

	public void setCarManufacturer(CarManufacturer carManufacturer) {
		this.carManufacturer = carManufacturer;
	}

	@Override
	public String toString() {
		return "CarModel [id=" + id +
				", name=" + name +
				", type=" + type +
				", carManufacturer=" + carManufacturer
				+ "]";
	}
}
