package otherjava;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/*ץȡЯ�̵Ĺ��ʻ�Ʊ����*/
/*ע��ʹ��Jsoupɸѡ����*/
public class GetCtripData implements Runnable {
	private String fromCity;
	private String toCity;
	private String fromDate;
	private List<SelectedAnswer> finalAns;
	
	private int MAX = 9999999;
	public final static String fromSite = "Я��������";
	private final static String url = "http://flights.ctrip.com/international/";/*��ʼ��ѯ��ҳ��url*/
	private final static long WAIT_TIME = 3000;/*������ҳʱ�ĵȴ�ʱ��*/
	private final static String formCityInputName = "homecity_name";/*������������Ԫ�ص�nameֵ*/
	private final static String toCityInputName = "destcity1_name";/*�����������Ԫ�ص�nameֵ*/
	private final static String fromDateInputName = "DDatePeriod1";/*������������Ԫ�ص�nameֵ*/
	private final static String submitId = "searchBtn";/*�ύ��ťԪ�ص�idֵ*/
	private final static String flightNumCssQuery = "span.loading-status";/*��ȷ����������select���*/
	private final static String blockClass = "flight-item";/*��ҳ�����ݿ��select���*/
	private final static String flightTimeCssQuery = "div.flight-detail-section";/*�ϻ�������select���*/
	/*����ת�޹ص�����*/
	private final static String fromTimeCssQuery = "div.flight-detail-depart div.flight-detail-time";/*ʼ���س���ʱ���select���*/
	private final static String toTimeCssQuery = "div.flight-detail-return div.flight-detail-time";/*Ŀ�ĵص���ʱ���select���*/
	private final static String fromPortCssQuery = "div.flight-detail-depart div.flight-detail-airport";/*ʼ���ػ�����select���*/
	private final static String toPortCssQuery = "div.flight-detail-return div.flight-detail-airport";/*Ŀ�ĵػ�����select���*/
	private final static String flyTimeCssQuery = "div.flight-total-time";/*�ܷ���ʱ���select���*/
	private final static String priceCssQuery = "div.seat-price";/*�۸��select���*/
	/*����ת�йص�����*/
	private final static String companyCssQuery = "p.section-flight-base";/*���˺��չ�˾��select���*/
	private final static String transferPortCssQuery = "span.section-airport";/*��ת������select���*/
	private final static String transferPortTerminalCssQuery = "span.section-terminal";/*��ת����1��select���*/
	private final static String transferCityCssQuery = "div.section-stop strong";/*��ת���е�select���*/
	/*���캯��*/
	public GetCtripData(String fromCity, String toCity, String fromDate, List<SelectedAnswer> finalAns) {
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.fromDate = fromDate;
		this.finalAns = finalAns;
	}
	/*������㡢�յ㡢���ʱ���ȡɸѡ��� 
	 *�޷���ȡ��ѯ��ҳ--����null*/
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
		String fromCity = "�Ϻ�";
		String toCity = "���׶�";
		String fromDate = "2016-06-06";
		if(SelectOperation.isAllChinaCity(fromCity, toCity)) {
			System.out.println("��Ǹ����ϵͳֻ��Թ��ʺ��ߵ��Ƽ���");
			return;
		}
		GetCtripData getLYData = new GetCtripData(fromCity, toCity, fromDate,finalAns);
		SelectedAnswer ans = getLYData.getSelectedData();
		if(null == ans) {
			System.out.println(GetCtripData.fromSite + "��ѯ��ҳ�޷���ȡ��");
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
			System.out.println("��˰�ܼ۸�" + temp.getPrice());
			for(int i = 0; i <= temp.getTransferTime() - 1; i++) {
				System.out.println(temp.getTransferCity().get(i) + "\t" + temp.getTransferPort().get(i));
			}
		}
	}
	/*������㡢�յ㡢���ʱ������ȡ��ѯ���ҳ��
	 *��ȡʧ��--����null*/
	private HtmlPage getPage(WebClient webClient) {
		//��ȡ��ѯҳ��
		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(WAIT_TIME);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null == page) {
			return null;
		}
		//��ȡ�����
		HtmlTextInput fromCityInput = page.getElementByName(formCityInputName);
		HtmlTextInput toCityInput = page.getElementByName(toCityInputName);
		HtmlTextInput fromDateInput = page.getElementByName(fromDateInputName);
		HtmlSubmitInput submit = (HtmlSubmitInput) page.getElementById(submitId);
		if(null == fromCityInput || null == toCityInput || null == fromDateInput || null == submit) {
			return null;
		}
		//Ϊ�����ֵ������ύ
		HtmlPage submitPage = null;
		try {
			fromCityInput.click();
			fromCityInput.setValueAttribute(fromCity);
			toCityInput.click();
			toCityInput.setValueAttribute(toCity);
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

	/*��ȡ��ѯ�����ҳ�����к�����Ϣ*/
	private List<FlightData> getDataFromCurrentPage(HtmlPage page) {
		List<FlightData> ans = new ArrayList<FlightData>();
		if(null == page) {
			return ans;
		}
		String url = page.getUrl().toString();
		Document doc = Jsoup.parse(page.asXml());//Document���ڲ����������
		String flightNumString = doc.select(flightNumCssQuery).get(0).text();//��ȷ��������
		System.out.println(flightNumString);
		Elements blockElems= doc.getElementsByClass(blockClass);//��λ�������ݿ�
		//��ȡÿ�����ݿ����ϸ��Ϣ
		for(int block = 0; block <= blockElems.size() - 1; block++) {
			FlightData localAns = new FlightData();
			Element blockElem = blockElems.get(block);
			//��ȡ���Ƿ���ת�޹ص�����
			String fromTime = blockElem.select(fromTimeCssQuery).get(0).text();//����ʱ��
			String toTime = blockElem.select(toTimeCssQuery).get(0).text();//����ʱ��
			String fromPort = blockElem.select(fromPortCssQuery).get(0).text();//��������
			String toPort = blockElem.select(toPortCssQuery).get(0).text();//�������
			String flyTime = blockElem.select(flyTimeCssQuery).get(0).text();//�ܷ���ʱ��
			flyTime = flyTime.substring(1,flyTime.indexOf("h")) + "Сʱ" + flyTime.substring(flyTime.indexOf("h") + 1,flyTime.indexOf("m")) + "��";
			
			Elements priceElems = blockElem.select(priceCssQuery);
			int priceAns = MAX;
			for(int pricePiece = 0; pricePiece <= priceElems.size() - 1; pricePiece++) {
				int priceTemp = Integer.parseInt(priceElems.get(pricePiece).text().substring(2));
				if(priceTemp < priceAns) {
					priceAns = priceTemp;
				}				
			}
			String price = Integer.toString(priceAns);//��˰�ܼ۸�
			//��ʼ������ת�йص�����
			List<String> company = new ArrayList<String>();//���˺��չ�˾
			List<String> transferPort = new ArrayList<String>();//��ת����
			List<String> transferCity = new ArrayList<String>();//��ת����
			//flightTime�ϻ�����
			Elements partElems = blockElem.select(flightTimeCssQuery);
			int flightTime = partElems.size();
			//��ȡ����Ϣ����
			String transferPortTemp = "";
			for(int part = 0; part <= flightTime - 1; part++) {
				Element partElem = partElems.get(part);
				//���˺��չ�˾
				String companyTemp = partElem.select(companyCssQuery).get(0).text();
				if(-1 != companyTemp.indexOf(" ")) companyTemp = companyTemp.substring(0, companyTemp.indexOf(" "));
				company.add(companyTemp);
				//��������
				if(0 != part) {
					String terminal = partElem.select(transferPortTerminalCssQuery).size() >= 1 ? partElem.select(transferPortTerminalCssQuery).get(0).text() : "";
					String temp = partElem.select(transferPortCssQuery).get(0).text() + terminal;
					transferPortTemp += " ת�� " + temp.substring(temp.indexOf(" ") + 1);
				}
				//��ת����
				if(0 != part) {
					transferPort.add(transferPortTemp);
					transferPortTemp = "";
				}
				//�������
				if(flightTime - 1 != part) {
					String terminal = partElem.select(transferPortTerminalCssQuery).size() >= 2 ? partElem.select(transferPortTerminalCssQuery).get(1).text() : "";
					String temp = partElem.select(transferPortCssQuery).get(1).text() + terminal;
					transferPortTemp += " �� " + temp.substring(temp.indexOf(" ") + 1);
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
			localAns.setUrl(GetCtripData.url);
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
			System.out.println(GetCtripData.fromSite + "��ѯ��ҳ�޷���ȡ��");
			return;
		}
		else {
			synchronized(finalAns) {
				finalAns.add(ans);
			}
			System.out.println(GetCtripData.fromSite + "�ĺ�����ɸѡ��ɣ�");
			return;
		}
	}
	
}