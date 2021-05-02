package dev.dash.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.DashboardConfigRepository;
import dev.dash.dao.PanelConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.dao.TabConfigRepository;
import dev.dash.execute.QueryExecutorService;
import dev.dash.setup.DefaultDataService;

import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootApplication
@ComponentScan(basePackages = { "dev.dash.*" })
@EntityScan("dev.dash.*")   
@EnableJpaRepositories("dev.dash.dao")
public class DevDashApplication implements CommandLineRunner {

    @Autowired
    DashboardConfigRepository dashboardConfigRepository;

    @Autowired
    TabConfigRepository tabConfigRepository;

    @Autowired
    PanelConfigRepository panelConfigRepository;

    @Autowired
    SchemaConfigRepository schemaConfigRepository;    
    
    @Autowired
    QueryConfigRepository queryConfigRepository; 

    @Autowired
    ConnectionConfigRepository connectionConfigRepository;

    @Autowired
    QueryExecutorService queryExecutorService;

    @Autowired
    DefaultDataService defaultDataService;

    public static void main(String[] args) {
        SpringApplication.run(DevDashApplication.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception 
    {       
        defaultDataService.setupAllData();
        // Optional<EmployeeEntity> emp = repository.findById(2L);
 
        // logger.info("Employee id 2 -> {}", emp.get());
        //part2();

        //JSONArray jsonArray = queryExecutorService.processQuery("SelectAllTutorials", "tutorialdbOne", null);
        //System.out.println(jsonArray.toString());
    }


    // @Bean
	// public WebMvcConfigurer corsConfigurer() {
	// 	return new WebMvcConfigurer() {
	// 		@Override
	// 		public void addCorsMappings(CorsRegistry registry) {
	// 			registry.addMapping("*").allowedOrigins("*");
	// 		// 	registry.addMapping("/devdash/*").allowedOrigins("http://localhost:4200");
	// 		// 	registry.addMapping("/devdash/query/*").allowedOrigins("http://localhost:4200");
	// 		}
	// 	};
    // }
    

	public void part2() throws ClassNotFoundException {
		
		System.out.println("-------- MySQL JDBC Connection Demo ------------");
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !!");
            return;
        }
        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tutorial?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "Monkey2020$");
            System.out.println("SQL Connection to database established!");

            viewTable(connection,"tutorial_tbl");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        } finally {
            try
            {
                if(connection != null)
                    connection.close();
                System.out.println("Connection closed !!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //System.exit(0);

		// MysqlDataSource mysqlDS = null;
		// try {
		// 	mysqlDS = new MysqlDataSource();
		// 	mysqlDS.setURL("");
		// 	//mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
		// 	//mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
	}
	// I want to be able to connect to databases on the fly, exe querires and view the results
    //https://stackoverflow.com/questions/2839321/connect-java-to-a-mysql-database
    

    //https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html
    public static void viewTable(Connection con, String dbName)
        throws SQLException {

        Statement stmt = null;
        String query = "select * FROM tutorials_tbl";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            JSONArray jsonArray = jsonify(rs);
            System.out.println(jsonArray.toString());
            // while (rs.next()) {
            //     String coffeeName = rs.getString("COF_NAME");
            //     int supplierID = rs.getInt("SUP_ID");
            //     float price = rs.getFloat("PRICE");
            //     int sales = rs.getInt("SALES");
            //     int total = rs.getInt("TOTAL");
            //     System.out.println(coffeeName + "\t" + supplierID +
            //                     "\t" + price + "\t" + sales +
            //                     "\t" + total);
            // }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    // https://stackoverflow.com/questions/6514876/most-efficient-conversion-of-resultset-to-json
    public static JSONArray jsonify(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, rs.getObject(column_name));
            }
            json.put(obj);
        }
        return json;
    }
}