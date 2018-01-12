package otherjava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/*抓取同城的国际机票数据*/
/*注：使用Jsoup筛选数据*/
public class GetLYData implements Runnable {
	private String fromCity;
	private String toCity;
	private String fromDate;
	private List<SelectedAnswer> finalAns;
	
	public final static String fromSite = "同城旅游网";
	private final static String url = "http://www.ly.com/iflight/";/*起始查询网页的url*/
	private final static long WAIT_TIME = 3000;/*加载网页时的等待时间*/
	private final static String formCityInputName = "iOrgPort";/*出发城市输入元素的name值*/
	private final static String toCityInputName = "iArvPort";/*到达城市输入元素的name值*/
	private final static String fromDateInputName = "idtGoDate";/*出发日期输入元素的name值*/
	private final static String submitId = "airplaneSubmit";/*提交按钮元素的id值*/
	private final static String flightNumCssQuery = "span#searchTxt";/*正确航班数量的select语句*/
	private final static String blockClass = "iflight_box";/*网页中数据块的select语句*/
	private final static String flightTimeCssQuery = "div.view_more_box div.detail_info_box > table";/*上机次数的select语句*/
	/*与中转无关的数据*/
	private final static String fromTimeCssQuery = "td.td_offtime span.time";/*始发地出发时间的select语句*/
	private final static String toTimeCssQuery = "td.td_arrtime span.time";/*目的地到达时间的select语句*/
	private final static String fromPortCssQuery = "td.td_offtime p";/*始发地机场的select语句*/
	private final static String toPortCssQuery = "td.td_arrtime p";/*目的地机场的select语句*/
	private final static String flyTimeCssQuery = "span.fly_time.f_999";/*总飞行时间的select语句*/
	private final static String priceCssQuery = "td.td_price em.price";/*价格的select语句*/
	/*与中转有关的数据*/
	private final static String companyCssQuery = "p.if_air span";/*承运航空公司的select语句*/
	private final static String transferPortCssQuery = "td.detail_airport p";/*中转机场的select语句*/
	private final static String transferCityCssQuery = "div.tran_line span.f_999";/*中转城市的select语句*/
	
	/*构造函数*/
	public GetLYData(String fromCity, String toCity, String fromDate, List<SelectedAnswer> finalAns) {
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.fromDate = fromDate;
		this.finalAns = finalAns;
	}
	/*根据起点、终点、起飞时间获取筛选结果 
	 *无法获取查询网页--返回null*/
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
		String fromCity = "北京";
		String toCity = "渥太华";
		String fromDate = "2016-06-06";
		if(SelectOperation.isAllChinaCity(fromCity, toCity)) {
			System.out.println("抱歉，本系统只针对国际航线的推荐！");
			return;
		}
		GetLYData getLYData = new GetLYData(fromCity, toCity, fromDate,finalAns);
		SelectedAnswer ans = getLYData.getSelectedData();
		if(null == ans) {
			System.out.println(GetLYData.fromSite + "查询网页无法获取！");
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
		HtmlTextInput fromCityInput = page.getElementByName(formCityInputName);
		HtmlTextInput toCityInput = page.getElementByName(toCityInputName);
		HtmlTextInput fromDateInput = page.getElementByName(fromDateInputName);
		HtmlButtonInput submit = (HtmlButtonInput) page.getElementById(submitId);
		if(null == fromCityInput || null == toCityInput || null == fromDateInput || null == submit) {
			return null;
		}
		//为输入框赋值并点击提交
		fromCityInput.setValueAttribute(fromCity);
		toCityInput.setValueAttribute(toCity);
		fromDateInput.setValueAttribute(fromDate);
		HtmlPage submitPage = null;
		try {
			submitPage = submit.click();
			webClient.waitForBackgroundJavaScript(WAIT_TIME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return submitPage;
	}	

	/*获取查询结果当页的所有航班信息*/
	private List<FlightData> getDataFromCurrentPage(HtmlPage page) {
		List<FlightData> ans = new ArrayList<FlightData>();
		if(null == page) {
			return ans;
		}
		String url = page.getUrl().toString();
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
				company.add(partElem.select(companyCssQuery).get(0).text());
				//出发机场
				if(0 != part) {
					transferPortTemp += " 转到 " + partElem.select(transferPortCssQuery).get(0).text();
				}
				//中转机场
				if(0 != part) {
					transferPort.add(transferPortTemp);
					transferPortTemp = "";
				}
				//到达机场
				if(flightTime - 1 != part) {
					transferPortTemp += "从 " + partElem.select(transferPortCssQuery).get(1).text().substring(3);
				}
			}
			Elements transferCityElems = blockElem.select(transferCityCssQuery);
			if(flightTime - 1 != transferCityElems.size()) {
				continue;
			}
			else {
				for(int i = 0; i <= flightTime - 2; i++) {
					String transferCityTemp = transferCityElems.get(i).text();
					transferCity.add(transferCityTemp.substring(0, transferCityTemp.indexOf(" ")));
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
			localAns.setUrl(url);
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
			System.out.println(GetLYData.fromSite + "查询网页无法获取！");
			return;
		}
		else {
			synchronized(finalAns) {
				finalAns.add(ans);
			}
			System.out.println(GetLYData.fromSite + "的航班结果筛选完成！");
			return;
		}
	}
	
}