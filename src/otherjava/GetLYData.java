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

/*ץȡͬ�ǵĹ��ʻ�Ʊ����*/
/*ע��ʹ��Jsoupɸѡ����*/
public class GetLYData implements Runnable {
	private String fromCity;
	private String toCity;
	private String fromDate;
	private List<SelectedAnswer> finalAns;
	
	public final static String fromSite = "ͬ��������";
	private final static String url = "http://www.ly.com/iflight/";/*��ʼ��ѯ��ҳ��url*/
	private final static long WAIT_TIME = 3000;/*������ҳʱ�ĵȴ�ʱ��*/
	private final static String formCityInputName = "iOrgPort";/*������������Ԫ�ص�nameֵ*/
	private final static String toCityInputName = "iArvPort";/*�����������Ԫ�ص�nameֵ*/
	private final static String fromDateInputName = "idtGoDate";/*������������Ԫ�ص�nameֵ*/
	private final static String submitId = "airplaneSubmit";/*�ύ��ťԪ�ص�idֵ*/
	private final static String flightNumCssQuery = "span#searchTxt";/*��ȷ����������select���*/
	private final static String blockClass = "iflight_box";/*��ҳ�����ݿ��select���*/
	private final static String flightTimeCssQuery = "div.view_more_box div.detail_info_box > table";/*�ϻ�������select���*/
	/*����ת�޹ص�����*/
	private final static String fromTimeCssQuery = "td.td_offtime span.time";/*ʼ���س���ʱ���select���*/
	private final static String toTimeCssQuery = "td.td_arrtime span.time";/*Ŀ�ĵص���ʱ���select���*/
	private final static String fromPortCssQuery = "td.td_offtime p";/*ʼ���ػ�����select���*/
	private final static String toPortCssQuery = "td.td_arrtime p";/*Ŀ�ĵػ�����select���*/
	private final static String flyTimeCssQuery = "span.fly_time.f_999";/*�ܷ���ʱ���select���*/
	private final static String priceCssQuery = "td.td_price em.price";/*�۸��select���*/
	/*����ת�йص�����*/
	private final static String companyCssQuery = "p.if_air span";/*���˺��չ�˾��select���*/
	private final static String transferPortCssQuery = "td.detail_airport p";/*��ת������select���*/
	private final static String transferCityCssQuery = "div.tran_line span.f_999";/*��ת���е�select���*/
	
	/*���캯��*/
	public GetLYData(String fromCity, String toCity, String fromDate, List<SelectedAnswer> finalAns) {
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
		String fromCity = "����";
		String toCity = "��̫��";
		String fromDate = "2016-06-06";
		if(SelectOperation.isAllChinaCity(fromCity, toCity)) {
			System.out.println("��Ǹ����ϵͳֻ��Թ��ʺ��ߵ��Ƽ���");
			return;
		}
		GetLYData getLYData = new GetLYData(fromCity, toCity, fromDate,finalAns);
		SelectedAnswer ans = getLYData.getSelectedData();
		if(null == ans) {
			System.out.println(GetLYData.fromSite + "��ѯ��ҳ�޷���ȡ��");
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
		HtmlPage page = HtmlUnitOperation.getPageByUrl(webClient, url);
		
		if(null == page) {
			return null;
		}
		//��ȡ�����
		HtmlTextInput fromCityInput = page.getElementByName(formCityInputName);
		HtmlTextInput toCityInput = page.getElementByName(toCityInputName);
		HtmlTextInput fromDateInput = page.getElementByName(fromDateInputName);
		HtmlButtonInput submit = (HtmlButtonInput) page.getElementById(submitId);
		if(null == fromCityInput || null == toCityInput || null == fromDateInput || null == submit) {
			return null;
		}
		//Ϊ�����ֵ������ύ
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
			String price = blockElem.select(priceCssQuery).get(0).text();//��˰�ܼ۸�
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
				company.add(partElem.select(companyCssQuery).get(0).text());
				//��������
				if(0 != part) {
					transferPortTemp += " ת�� " + partElem.select(transferPortCssQuery).get(0).text();
				}
				//��ת����
				if(0 != part) {
					transferPort.add(transferPortTemp);
					transferPortTemp = "";
				}
				//�������
				if(flightTime - 1 != part) {
					transferPortTemp += "�� " + partElem.select(transferPortCssQuery).get(1).text().substring(3);
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
			System.out.println(GetLYData.fromSite + "��ѯ��ҳ�޷���ȡ��");
			return;
		}
		else {
			synchronized(finalAns) {
				finalAns.add(ans);
			}
			System.out.println(GetLYData.fromSite + "�ĺ�����ɸѡ��ɣ�");
			return;
		}
	}
	
}