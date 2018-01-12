package otherjava;

import java.util.ArrayList;
import java.util.List;

public class FlightData {
	private String fromSite;/*旅游网站的名称*/
	private String fromTime = "";/*出发时间*/
	private String toTime = "";/*到达时间*/
	private String fromPort = "";/*出发机场*/
	private String toPort = "";/*到达机场*/
	private String flyTime = "";/*总飞行时间*/
	private String price = "";/*含税总价格*/
	private List<String> company = new ArrayList<String>();/*承运航空公司*/
	private List<String> transferPort = new ArrayList<String>();/*中转机场*/
	private List<String> transferCity = new ArrayList<String>();/*中转城市*/
	private String url = "";/*航班所在网页的url*/
	public int getTransferTime() {
		return transferPort.size();
	}
	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
	public String getFromPort() {
		return fromPort;
	}
	public void setFromPort(String fromPort) {
		this.fromPort = fromPort;
	}
	public String getToPort() {
		return toPort;
	}
	public void setToPort(String toPort) {
		this.toPort = toPort;
	}
	public String getFlyTime() {
		return flyTime;
	}
	public void setFlyTime(String flyTime) {
		this.flyTime = flyTime;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public List<String> getCompany() {
		return company;
	}
	public void setCompany(List<String> company) {
		this.company = company;
	}
	public List<String> getTransferPort() {
		return transferPort;
	}
	public void setTransferPort(List<String> transferPort) {
		this.transferPort = transferPort;
	}
	public List<String> getTransferCity() {
		return transferCity;
	}
	public void setTransferCity(List<String> transferCity) {
		this.transferCity = transferCity;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFromSite() {
		return fromSite;
	}
	public void setFromSite(String fromSite) {
		this.fromSite = fromSite;
	}
}