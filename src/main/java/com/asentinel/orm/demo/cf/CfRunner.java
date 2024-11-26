package com.asentinel.orm.demo.cf;

import com.asentinel.common.orm.mappers.dynamic.DefaultDynamicColumn;
import com.asentinel.common.orm.mappers.dynamic.DynamicColumn;
import com.asentinel.orm.demo.cf.domain.CarManufacturer;
import com.asentinel.orm.demo.cf.domain.CarModel;
import com.asentinel.orm.demo.cf.domain.CarType;
import com.asentinel.orm.demo.cf.domain.CustomFieldsCarManufacturer;
import com.asentinel.orm.demo.cf.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CfRunner implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(CfRunner.class);

	private final CarService carService;

    public CfRunner(CarService carService) {
        this.carService = carService;
    }

    @Override
	public void run(String... args) throws IOException {
		List<DynamicColumn> dynamicColumns = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			for (;;) {
				System.out.println("---> Input the column name (hit enter to stop):");
				String name = reader.readLine();
				if (name.isBlank()) {
					break;
				}

				System.out.println("---> Input the column type, only 'varchar' and 'int' are supported (hit enter to stop):");
				String type = reader.readLine();
				if (type.isBlank()) {
					break;
				}

				carService.addManufacturerField(name, type);

				dynamicColumns.add(new DefaultDynamicColumn(name, columnType(type)));
			}

			LOG.info("Car Manufacturer dynamic columns: \n{}", dynamicColumns);

			// create the 2 manufacturers and 2 models
			CarManufacturer mazda0 = addManufacturer(reader, "Mazda", dynamicColumns);
			CarModel mx5 = new CarModel("MX5", CarType.CAR, mazda0);
			CarModel cx60 = new CarModel("CX60", CarType.SUV, mazda0);
			carService.createModels(mx5, cx60);
			
			addManufacturer(reader, "Honda", dynamicColumns);
		}

		// read mazda manufacturer
		CarManufacturer mazda1 = carService.findManufacturerByName("Mazda", dynamicColumns);
		LOG.info("Loaded manufacturer: {}", mazda1);
		LOG.info("Manufacturer car models: {}", mazda1.getModels());
	}
	
	private CarManufacturer addManufacturer(BufferedReader reader,
											String name, List<DynamicColumn> dynamicColumns) throws IOException {
		Map<DynamicColumn, Object> dynamicColumnsValues = new HashMap<>();
		for (DynamicColumn dynamicColumn : dynamicColumns) {
			System.out.println("---> Input value for the dynamic column '" + dynamicColumn + "' for : " + name);
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
		carService.createManufacturer(manufacturer, dynamicColumns);
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
