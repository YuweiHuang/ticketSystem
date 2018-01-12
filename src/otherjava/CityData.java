package otherjava;

//CityData类为城市信息类
class CityData {
	private String city, country, code, continent;
	public String getCity() {
		return city;
	}
	public String getCountry() {
		return country;
	}
	public String getCode() {
		return code;
	}
	public String getContinent() {
		return continent;
	}
	
	CityData(String city, String country, String code, String continent) {
		this.city = city;
		this.country = country;
		this.code = code;
		this.continent = continent;
	}
}