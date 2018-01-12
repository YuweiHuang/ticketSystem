package otherjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//JDBC类链接了系统中的MySQL数据库,定义了数据插入、读取的方法
class JDBC {
	//connection与数据库的链接
	private Connection connection = null;
	//statement对数据库访问的语句实例
	private Statement statement = null;
	//城市信息表
	private String citytable = "citytable";
	//中国航空公司信息表
	private String chinacompanytable = "chinacompanytable";
	//构造函数
	JDBC() {
		try {
			String driverName="com.mysql.jdbc.Driver";
		    String userName="root";
		    String userPasswd="";
		    //String userPasswd="";
		    String dbName="flightsystem";
		    String url="jdbc:mysql://:3306/" + dbName + "?useUnicode=true&characterEncoding=utf-8&useSSL=false";//"+dbName+"?user="+userName+"&password="+userPasswd;

		    Class.forName(driverName);//实例化MySql数据库驱动程序(建立中间件)
		    connection=DriverManager.getConnection(url,userName,userPasswd);//连接数据库，查找合适的驱动程序
		    statement = connection.createStatement();//提交sql语句,创建一个Statement对象来将SQL语句发送到数据库  
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*获取城市编码,若数据库中不存在该城市返回Null*/
	String getCityCode(String city) {
		String sqlQuery = "select * from " + citytable + " where name='" + city + "';";
		ResultSet rs = null;
		String code = null;
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 != rs.getRow()) {
				rs = statement.executeQuery(sqlQuery);
				rs.next();
				code = rs.getString("code");
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	/*插入国家所在大洲*/
	void insertContinent(String country, String continent) {
		String sqlInsert = "update " + citytable + " set continent='" + continent + "' where country='" + country + "';"; 
		try {
			statement.executeUpdate(sqlInsert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*插入城市编码、城市名称、城市所在国家*/
	void insertCityCode(String cityName, String cityCode, String country) {
		String sqlQuery = "select * from " + citytable + " where code ='" + cityCode + "';";
		String sqlInsert = "insert into " + citytable + "(code,name,country) values('" + cityCode + 
				"','" + cityName + "','" + country + "')";
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 != rs.getRow()) {
				System.out.print("重复：");
				rs = statement.executeQuery(sqlQuery);
				while(rs.next()) {
					System.out.println(cityName + cityCode + country);
					System.out.println("与" + rs.getString("name") + rs.getString("code") + rs.getString("country"));
				}
			}
			else {
				statement.executeUpdate(sqlInsert);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//插入中国航空公司信息
	void insertCompany(String company) {
		String sqlQuery = "select * from " + chinacompanytable + " where company='" + company + "';";
		String sqlInsert = "insert into chinacompanytable values('" + company + "');";
		try {
			ResultSet rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 == rs.getRow()) {
				statement.executeUpdate(sqlInsert);
			}	
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//从数据库中获取城市信息
	List<CityData> getCityFromDB() {
		ResultSet rs = null;
		List<CityData> dc = new ArrayList<CityData>();
		String sqlQuery = "select * from " + citytable + ";";
		try {
			rs = statement.executeQuery(sqlQuery);
			while(rs.next()) {
				String city = rs.getString("name");
				String code = rs.getString("code");
				String country = rs.getString("country");
				String continent = rs.getString("continent");
				dc.add(new CityData(city, country, code, continent));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dc;
	}
	
	//判断city是否为中国城市
	synchronized boolean isChinaCity(String city) {
		ResultSet rs = null;
		String sqlQuery = "select * from " + citytable + " where country='中国' and name='" +
				city + "';";
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	//判断company是否为中国航空公司
	synchronized boolean isChinaFlight(String company) {
		ResultSet rs = null;
		String sqlQuery = "select * from " + chinacompanytable + " where company='" +
				company + "';";
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	//判断两城市是否属于同一国家
	synchronized boolean isInSameCountry(String city1,String city2) {
		ResultSet rs = null;
		String sqlQuery1 = "select * from " + citytable + " where name='" +
				city1 + "';";
		String sqlQuery2 = "select * from " + citytable + " where name='" +
				city2 + "';";
		try {
			rs = statement.executeQuery(sqlQuery1);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			String country = rs.getString("country");
			rs.close();
			rs = statement.executeQuery(sqlQuery2);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			if(country.equals(rs.getString("country"))) {
				return true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//判断两城市是否属于同一大洲
	synchronized boolean isInSameContinent(String city1,String city2) {
		ResultSet rs = null;
		String sqlQuery1 = "select * from " + citytable + " where name='" +
				city1 + "';";
		String sqlQuery2 = "select * from " + citytable + " where name='" +
				city2 + "';";
		try {
			rs = statement.executeQuery(sqlQuery1);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			String continent = rs.getString("continent");
			rs.close();
			rs = statement.executeQuery(sqlQuery2);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			if(continent.equals(rs.getString("continent"))) {
				return true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//释放数据库连接
	void close() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
