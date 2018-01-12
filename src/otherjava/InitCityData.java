package otherjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//该类初始化数据库中城市信息表与中国航空公司信息表,只在系统初次配置时使用
public class InitCityData {
	//我国航空公司txt的路径
	private String ChinaFlightCompanyPos = "D:\\item_file\\flightsystem_file\\company.txt";
	/*根据同程旅游网提供的信息更新数据库中的城市信息*/
	public void updateCityInfoInDB() {
		JDBC jdbc = new JDBC();
		String fileName = "D:\\item_file\\flightsystem_file\\cityInfo.txt";
		File file = new File(fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rowCityInfo = null;
		try {
			rowCityInfo = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> cityList = new ArrayList<String>();
		int pos = 0; 
		while(pos <= rowCityInfo.length() - 1) {
			int nextPos = rowCityInfo.indexOf("#", pos);
			cityList.add(rowCityInfo.substring(pos, nextPos));
			pos = nextPos + 1;
		}
		System.out.println(cityList.size());
		for(int i = 0; i <= cityList.size() - 1; i++) {
			String perCity = cityList.get(i);
			int first = perCity.indexOf("|", 0);
			int second = perCity.indexOf("|", first + 1);
			int third = perCity.indexOf("|", second + 1);
			int fourth = perCity.indexOf("|", third + 1);
			int fifth = perCity.indexOf("|", fourth + 1);
			int sixth = perCity.indexOf("|", fifth + 1);
			String city = perCity.substring(0, first);
			String country = perCity.substring(first + 1, second);
			String code = perCity.substring(fifth + 1, sixth);
			jdbc.insertCityCode(city, code, country);
		}
	}
	
	//获取所有城市的名称及编码 存入数据库
	private boolean getAllCityIntoMySQL(JDBC jdbc) {
		String continentOfcountry = "D:\\item_file\\flightsystem_file\\continentOfcountry.txt";
		File infile = new File(continentOfcountry);
		try {
			BufferedReader br = new BufferedReader(new FileReader(infile));
			int middle = 0;
			String perLine = "";
			String country = "";
			String continent = "";
			while(null != (perLine = br.readLine())) {
				middle = perLine.indexOf(" ");
				country = perLine.substring(0, middle);
				continent = perLine.substring(middle + 1);
				jdbc.insertContinent(country, continent);
			}
			br.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}	
	//将我国航空公司存入数据库
	private void getChinaComponyIntoMySQL(JDBC jdbc) {
		File infile = new File(ChinaFlightCompanyPos);
		try {
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String companies = br.readLine();
			int pos = 0;
			String company = "";
			while(pos <= companies.length() - 1) {
				int nextPos = companies.indexOf(" ", pos);
				company = companies.substring(pos, nextPos);
				jdbc.insertCompany(company);
				pos = nextPos + 1;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		JDBC jdbc = new JDBC();
		InitCityData icd = new InitCityData();
		//将我国航空公司存入数据库
		icd.getChinaComponyIntoMySQL(jdbc);
		//获取所有城市的名称及编码 存入数据库
		icd.getAllCityIntoMySQL(jdbc);
		jdbc.close();
	}
}