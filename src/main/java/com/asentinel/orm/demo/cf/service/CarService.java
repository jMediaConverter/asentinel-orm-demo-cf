package com.asentinel.orm.demo.cf.service;

import com.asentinel.common.jdbc.DefaultObjectFactory;
import com.asentinel.common.orm.AutoEagerLoader;
import com.asentinel.common.orm.OrmOperations;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumn;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumnsEntityNodeCallback;
import com.asentinel.common.orm.persist.UpdateSettings;
import com.asentinel.orm.demo.cf.domain.CarManufacturer;
import com.asentinel.orm.demo.cf.domain.CarModel;
import com.asentinel.orm.demo.cf.domain.CustomFieldsCarManufacturer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarService {

    private final OrmOperations orm;

    public CarService(OrmOperations orm) {
        this.orm = orm;
    }

    @Transactional
    public void addManufacturerAttribute(String name, String type) {
        orm.getSqlQuery()
                .update("alter table CarManufacturers add column " +  name + " " + type);
    }

    @Transactional
    public void createManufacturer(CustomFieldsCarManufacturer manufacturer, List<DynamicColumn> attributes) {
        orm.update(manufacturer,
                new UpdateSettings<>(attributes, null));
    }

    @Transactional(readOnly = true)
    public CarManufacturer findManufacturerByName(String name, List<DynamicColumn> attributes) {
        return orm.newSqlBuilder(CustomFieldsCarManufacturer.class)
                .select(
                        AutoEagerLoader.forPath(CarManufacturer.class, CarModel.class),
                        new DynamicColumnsEntityNodeCallback<>(
                                new DefaultObjectFactory<>(CustomFieldsCarManufacturer.class),
                                attributes
                        )
                )
                .where().column("name").eq(name)
            .execForEntity();
    }

    @Transactional
    public void createModels(CarModel... models) {
        orm.update(models);
    }
}
