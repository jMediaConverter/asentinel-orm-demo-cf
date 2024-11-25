package com.asentinel.orm.demo.cf;

import com.asentinel.common.jdbc.DefaultObjectFactory;
import com.asentinel.common.orm.AutoEagerLoader;
import com.asentinel.common.orm.OrmOperations;
import com.asentinel.common.orm.mappers.dynamic.DefaultDynamicColumn;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumn;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumnsEntityNodeCallback;
import com.asentinel.common.orm.persist.UpdateSettings;
import com.asentinel.orm.demo.cf.domain.CarManufacturer;
import com.asentinel.orm.demo.cf.domain.CarModel;
import com.asentinel.orm.demo.cf.domain.CarType;
import com.asentinel.orm.demo.cf.domain.CustomFieldsCarManufacturer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CfRunner {

	private final OrmOperations orm;

    public CfRunner(OrmOperations orm) {
        this.orm = orm;
    }

    @PostConstruct
	private void run() throws IOException {
		List<DynamicColumn> dynamicColumns = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			for (;;) {
				System.out.println("Input the column name (hit enter to stop):");
				String name = reader.readLine();
				if (name.isBlank()) {
					break;
				}

				System.out.println("Input the column type, only varchar and int are supported (hit enter to stop):");
				String type = reader.readLine();
				if (type.isBlank()) {
					break;
				}

				orm.getSqlQuery()
						.getJdbcOperations()
						.update("alter table CarManufacturers add column " +  name + " " + type );

				dynamicColumns.add(new DefaultDynamicColumn(name, columnType(type)));
			}

			System.out.println(dynamicColumns);
			
			
			// save the 2 manufacturers in the DB
			CarManufacturer mazda0 = saveManufacturer(reader, "Mazda", dynamicColumns);
			CarModel mx5 = new CarModel("MX5", CarType.CAR, mazda0);
			CarModel cx60 = new CarModel("CX60", CarType.SUV, mazda0);
			orm.update(mx5, cx60);
			
			saveManufacturer(reader, "Honda", dynamicColumns);
		}

		// load the mazda manufacturer from the db
		CustomFieldsCarManufacturer mazda1 = orm.newSqlBuilder(CustomFieldsCarManufacturer.class)
				.select(
						AutoEagerLoader.forPath(CarManufacturer.class, CarModel.class),
						new DynamicColumnsEntityNodeCallback<>(
								new DefaultObjectFactory<>(CustomFieldsCarManufacturer.class),
								dynamicColumns
					))
				.where().column("name").eq("Mazda")
				.execForEntity();
		System.out.println("Loaded manufacturer: " + mazda1);

		System.out.println("Manufacturer car models: ");
		mazda1.getModels()
				.forEach(System.out::println);
	}
	
	private CarManufacturer saveManufacturer(BufferedReader reader,
											 String name, List<DynamicColumn> dynamicColumns) throws IOException {
		Map<DynamicColumn, Object> dynamicColumnsValues = new HashMap<>();
		for (DynamicColumn dynamicColumn : dynamicColumns) {
			System.out.println("Input value for the dynamic column " + dynamicColumn + " for : " + name);
			String rawValue = reader.readLine();

			if (String.class == dynamicColumn.getDynamicColumnType()) {
				dynamicColumnsValues.put(dynamicColumn, rawValue); 
			} else if (Integer.class == dynamicColumn.getDynamicColumnType()) {
				dynamicColumnsValues.put(dynamicColumn, Integer.parseInt(rawValue));
			} else {
				throw new RuntimeException("Unsupported column type.");
			}
		}
		
		CustomFieldsCarManufacturer manufacturer = new CustomFieldsCarManufacturer(name, dynamicColumnsValues);
		orm.update(manufacturer,
				new UpdateSettings<>(dynamicColumns, null));

		return manufacturer;
	}
		
	private static Class<?> columnType(String type) {
        return switch (type.toLowerCase()) {
            case "varchar" -> String.class;
            case "int" -> Integer.class;
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
	}
}
