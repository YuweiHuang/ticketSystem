package otherjava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/*抓取途牛的国际机票数据*/
/*注：使用Jsoup筛选数据*/
public class GetTuniuData implements Runnable {
	private String fromCity;
	private String toCity;
	private String fromDate;
	private List<SelectedAnswer> finalAns;

	public final static String fromSite = "途牛旅游网";
	private final static String url = "http://www.tuniu.com/flight_intel/";/*起始查询网页的url*/
	private final static long WAIT_TIME = 3000;/*加载网页时的等待时间*/
	private final static String radioXpath = "//a[@class=\"eye-radio-icon\"]";
	private final static String formCityInputName = "departCityName";/*出发城市输入元素的name值*/
	private final static String toCityInputName = "destCityName";/*到达城市输入元素的name值*/
	private final static String fromDateInputName = "departDate";/*出发日期输入元素的name值*/
	private final static String submitId = "searchIntl";/*提交按钮元素的id值*/
	private final static String flightNumCssQuery = "span#loadingStatus";/*正确航班数量的select语句*/
	private final static String blockClass = "flight-item";/*网页中数据块的select语句*/
	private final static String flightTimeCssQuery = "div.flight-detail-item";/*上机次数的select语句*/
	/*与中转无关的数据元素*/
	private final static String fromTimeCssQuery = "div.flight-schedule-s-time";/*始发地出发时间的select语句*/
	private final static String toTimeCssQuery = "div.flight-schedule-e-time";/*目的地到达时间的select语句*/
	private final static String fromPortCssQuery = "div.flight-schedule-s-airport";/*始发地机场的select语句*/
	private final static String toPortCssQuery = "div.flight-schedule-e-airport";/*目的地机场的select语句*/
	private final static String flyTimeCssQuery = "div.flight-total-time";/*总飞行时间的select语句*/
	private final static String priceCssQuery = "span.price-info ";/*价格的select语句*/
	/*与中转有关的数据元素*/
	private final static String companyCssQuery = "p.section-flight-base";/*承运航空公司的select语句*/
	private final static String transferPortCssQuery = "span.col-airport";/*中转机场的select语句*/
	private final static String transferPortTerminalCssQuery = "span.col-terminal";/*中转机场1的select语句*/
	private final static String transferCityCssQuery = "div.flight-stop b";/*中转城市的select语句*/
	/*构造函数*/
	public GetTuniuData(String fromCity, String toCity, String fromDate, List<SelectedAnswer> finalAns) {
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.fromDate = fromDate;
		this.finalAns = finalAns;
	}
	/*根据起点、终点、起飞时间获取筛选结果 
	 *城市输入错误或无法获取查询网页--返回null*/
	private SelectedAnswer getSelectedData() {		
		WebClient webClient = HtmlUnitOperation.getWebClient();
		HtmlPage page = getPage(webClient);
		if(null == page) {
			return null;
		}
		else {
			List<FlightData> ans = getDataFromCurrentPage(page);
			SelectedAnswer finalAns = SelectOperation.getSelectedData(fromCity, toCity, ans);
			return finalAns;
		}
	}
	public static void main(String args[]) {
		List<SelectedAnswer> finalAns = new ArrayList<SelectedAnswer>();
		String fromCity = "上海";
		String toCity = "多伦多";
		String fromDate = "2016-06-06";
		if(SelectOperation.isAllChinaCity(fromCity, toCity)) {
			System.out.println("抱歉，本系统只针对国际航线的推荐！");
			return;
		}
		JDBC jdbc = new JDBC();
		String fromCode = jdbc.getCityCode(fromCity);
		String toCode = jdbc.getCityCode(toCity);
		jdbc.close();
		if(null == fromCode && null == toCode) {
			System.out.println("输入城市有误！");
			return;
		}
		GetTuniuData getLYData = new GetTuniuData(fromCity, toCity, fromDate,finalAns);
		SelectedAnswer ans = getLYData.getSelectedData();
		if(null == ans) {
			System.out.println(GetTuniuData.fromSite + "查询网页无法获取！");
			return;
		}
		List<FlightData> selectedAns = ans.getSelectedData();
		for(int j = 0; j <= selectedAns.size() - 1; j++) {
			FlightData temp = selectedAns.get(j);
			System.out.println("(" + (j + 1) + ")" + temp.getUrl() + "\t");
			for(int i = 0; i <= temp.getTransferTime(); i++) {
				System.out.print(temp.getCompany().get(i) + "\t");
			}
			System.out.print(temp.getFromTime() + "\t");
			System.out.print(temp.getToTime() + "\t");
			System.out.print(temp.getFromPort() + "\t");
			System.out.print(temp.getToPort() + "\t");
			System.out.print(temp.getFlyTime() + "\t");
			System.out.println("含税总价格：" + temp.getPrice());
			for(int i = 0; i <= temp.getTransferTime() - 1; i++) {
				System.out.println(temp.getTransferCity().get(i) + "\t" + temp.getTransferPort().get(i));
			}
		}
	}
	/*根据起点、终点、起飞时间来获取查询结果页面
	 *获取失败--返回null*/
	private HtmlPage getPage(WebClient webClient) {
		//获取查询页面
		HtmlPage page = HtmlUnitOperation.getPageByUrl(webClient, url);
		if(null == page) {
			return null;
		}
		//获取输入框
		List<HtmlAnchor> radioList = HtmlUnitOperation.getAnchorListByXpath(page, radioXpath);
		HtmlAnchor radio = radioList.get(0);
		HtmlTextInput fromCityInput = page.getElementByName(formCityInputName);
		HtmlTextInput toCityInput = page.getElementByName(toCityInputName);
		HtmlTextInput fromDateInput = page.getElementByName(fromDateInputName);
		HtmlAnchor submit = (HtmlAnchor) page.getElementById(submitId);
		if(null == fromCityInput || null == toCityInput || null == fromDateInput || null == submit) {
			return null;
		}
		//为输入框赋值并点击提交
		JDBC jdbc = new JDBC();
		String fromCode = jdbc.getCityCode(fromCity);
		String toCode = jdbc.getCityCode(toCity);
		jdbc.close();
		if(null == fromCode && null == toCode) {
			return null;
		}
		HtmlPage submitPage = null;
		try {
			radio.click();
			fromCityInput.click();
			fromCityInput.setValueAttribute(fromCity + "(" + fromCode + ")");
			toCityInput.click();
			toCityInput.setValueAttribute(toCity + "(" + toCode + ")");
			fromDateInput.click();
			fromDateInput.setValueAttribute(fromDate);
			submitPage = submit.click();
			webClient.waitForBackgroundJavaScript(WAIT_TIME);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return submitPage;
	}	

	/*获取查询结果当页的所有航班信息*/
	private List<FlightData> getDataFromCurrentPage(HtmlPage page) {
		List<FlightData> ans = new ArrayList<FlightData>();
		if(null == page) {
			return ans;
		}
		String url = page.getBaseURL().toString();
		Document doc = Jsoup.parse(page.asXml());//Document用于查找相关数据
		String flightNumString = doc.select(flightNumCssQuery).get(0).text();//正确航班数量
		System.out.println(flightNumString);
		Elements blockElems= doc.getElementsByClass(blockClass);//定位所有数据块
		//获取每个数据块的详细信息
		for(int block = 0; block <= blockElems.size() - 1; block++) {
			FlightData localAns = new FlightData();
			Element blockElem = blockElems.get(block);
			//获取与是否中转无关的数据
			String fromTime = blockElem.select(fromTimeCssQuery).get(0).text();//出发时间
			String toTime = blockElem.select(toTimeCssQuery).get(0).text();//到达时间
			String fromPort = blockElem.select(fromPortCssQuery).get(0).text();//出发机场
			String toPort = blockElem.select(toPortCssQuery).get(0).text();//到达机场
			String flyTime = blockElem.select(flyTimeCssQuery).get(0).text();//总飞行时间
			if(-1 == flyTime.indexOf("m")) {
				flyTime = flyTime.substring(0,flyTime.indexOf("h")) + "小时0分";
			}
			else {
				flyTime = flyTime.substring(0,flyTime.indexOf("h")) + "小时" + flyTime.substring(flyTime.indexOf("h") + 1,flyTime.indexOf("m")) + "分";
			}
			
			String price = blockElem.select(priceCssQuery).get(0).text();//含税总价格
			//初始化与中转有关的数据
			List<String> company = new ArrayList<String>();//承运航空公司
			List<String> transferPort = new ArrayList<String>();//中转机场
			List<String> transferCity = new ArrayList<String>();//中转城市
			//flightTime上机次数
			Elements partElems = blockElem.select(flightTimeCssQuery);
			int flightTime = partElems.size();
			//获取各信息数据
			String transferPortTemp = "";
			for(int part = 0; part <= flightTime - 1; part++) {
				Element partElem = partElems.get(part);
				//承运航空公司
				String companyTemp = partElem.select(companyCssQuery).get(0).text();
				if(-1 != companyTemp.indexOf(" ")) companyTemp = companyTemp.substring(0, companyTemp.indexOf(" "));
				company.add(companyTemp);
				//出发机场
				if(0 != part) {
					String terminal = partElem.select(transferPortTerminalCssQuery).size() >= 1 ? partElem.select(transferPortTerminalCssQuery).get(0).text() : "";
					String temp = partElem.select(transferPortCssQuery).get(0).text() + terminal;
					transferPortTemp += " 转到 " + temp.substring(temp.indexOf(" ") + 1);
				}
				//中转机场
				if(0 != part) {
					transferPort.add(transferPortTemp);
					transferPortTemp = "";
				}
				//到达机场
				if(flightTime - 1 != part) {
					String terminal = partElem.select(transferPortTerminalCssQuery).size() >= 2 ? partElem.select(transferPortTerminalCssQuery).get(1).text() : "";
					String temp = partElem.select(transferPortCssQuery).get(1).text() + terminal;
					transferPortTemp += " 从 " + temp.substring(temp.indexOf(" ") + 1);
				}
			}
			Elements transferCityElems = blockElem.select(transferCityCssQuery);
			if(flightTime - 1 != transferCityElems.size()) {
				continue;
			}
			else {
				for(int i = 0; i <= flightTime - 2; i++) {
					String transferCityTemp = transferCityElems.get(i).text();
					transferCity.add(transferCityTemp);
				}
			}
			localAns.setFromSite(fromSite);
			localAns.setCompany(company);
			localAns.setFromTime(fromTime);
			localAns.setToTime(toTime);
			localAns.setFromPort(fromPort);
			localAns.setToPort(toPort);
			localAns.setPrice(price);
			localAns.setFlyTime(flyTime);
			localAns.setTransferCity(transferCity);
			localAns.setTransferPort(transferPort);
			localAns.setUrl(GetTuniuData.url);
			ans.add(localAns);
		}
		return ans;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(SelectOperation.isAllChinaCity(fromCity, toCity)) {
			return;
		}
		SelectedAnswer ans = getSelectedData();
		if(null == ans) {
			System.out.println(GetTuniuData.fromSite + "查询网页无法获取！");
			return;
		}
		else {
			synchronized(finalAns) {
				finalAns.add(ans);
			}
			System.out.println(GetTuniuData.fromSite + "的航班结果筛选完成！");
			return;
		}
	}
	
}