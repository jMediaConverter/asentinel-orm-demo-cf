package com.asentinel.orm.demo.cf;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.asentinel.common.jdbc.SqlQuery;
import com.asentinel.common.jdbc.SqlQueryTemplate;
import com.asentinel.common.jdbc.flavors.CustomArgumentPreparedStatementSetter;
import com.asentinel.common.jdbc.flavors.JdbcFlavor;
import com.asentinel.common.jdbc.flavors.h2.H2JdbcFlavor;
import com.asentinel.common.orm.OrmOperations;
import com.asentinel.common.orm.OrmTemplate;
import com.asentinel.common.orm.ed.tree.DefaultEntityDescriptorTreeRepository;
import com.asentinel.common.orm.ed.tree.EntityDescriptorTreeRepository;
import com.asentinel.common.orm.jql.DefaultSqlBuilderFactory;
import com.asentinel.common.orm.jql.SqlBuilderFactory;
import com.asentinel.common.orm.persist.SimpleUpdater;
import com.asentinel.common.orm.query.DefaultSqlFactory;
import com.asentinel.common.orm.query.SqlFactory;

@SpringBootApplication
public class DemoCf {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoCf.class, args);
	}
	
	
	@Bean
	public DataSource dataSource() {
		return new SingleConnectionDataSource("jdbc:h2:mem:testdb", "sa", "", false);
	}

    @Bean
    public JdbcFlavor jdbcFlavor() {
        return new H2JdbcFlavor();
    }
	
	@Bean
	public JdbcOperations jdbcOperations(DataSource dataSource, JdbcFlavor jdbcFlavor) {
		return new JdbcTemplate(dataSource) {
			
			/*
			 * add support for byte[], InputStream and Enum params
			 */
			@Override
			protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) {
				return new CustomArgumentPreparedStatementSetter(jdbcFlavor, args);
			}
		};
	}
	
    @Bean
    public SqlQuery sqlQuery(JdbcFlavor jdbcFlavor, JdbcOperations jdbcOps) {
        return new SqlQueryTemplate(jdbcFlavor, jdbcOps);
    }

    @Bean
    public SqlFactory sqlFactory(JdbcFlavor jdbcFlavor) {
        return new DefaultSqlFactory(jdbcFlavor);
    }

    @Bean
    public DefaultEntityDescriptorTreeRepository entityDescriptorTreeRepository(SqlBuilderFactory sqlBuilderFactory) {
        DefaultEntityDescriptorTreeRepository treeRepository = new DefaultEntityDescriptorTreeRepository();
        treeRepository.setSqlBuilderFactory(sqlBuilderFactory);
        return treeRepository;
    }

    @Bean
    public DefaultSqlBuilderFactory sqlBuilderFactory(@Lazy EntityDescriptorTreeRepository entityDescriptorTreeRepository,
                                                      SqlFactory sqlFactory,
													  SqlQuery sqlQuery) {
        DefaultSqlBuilderFactory sqlBuilderFactory = new DefaultSqlBuilderFactory(sqlFactory, sqlQuery);
        sqlBuilderFactory.setEntityDescriptorTreeRepository(entityDescriptorTreeRepository);
        return sqlBuilderFactory;
    }

    @Bean
    public OrmOperations orm(SqlBuilderFactory sqlBuilderFactory,
							 JdbcFlavor jdbcFlavor, SqlQuery sqlQuery) {
        return new OrmTemplate(sqlBuilderFactory, new SimpleUpdater(jdbcFlavor, sqlQuery));
    }
}
