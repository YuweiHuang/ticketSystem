package otherjava;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//HtmlUnitOperation�������HtmlUnit����ز���(��Ϊ��̬����)
class HtmlUnitOperation {
	private static final long WAIT_TIME = 1000;
	//��ȡģ���޽��������
	synchronized static WebClient getWebClient() {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setAppletEnabled(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());	
		return webClient;
	}
	//����url��ȡҳ��
	synchronized static HtmlPage getPageByUrl(WebClient webClient, String url) {
		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(WAIT_TIME);
		} catch (FailingHttpStatusCodeException e) {
			System.out.println("����ҳ����:" + url);
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("����ҳ����:" + url);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("����ҳ����:" + url);
			e.printStackTrace();
		}
		return page;
	}
	//ͨ��Xpath��ȡAnchorList
	static List<HtmlAnchor> getAnchorListByXpath(HtmlPage page, String Xpath) {
		@SuppressWarnings("unchecked")
		List<HtmlAnchor> anchorList = (List<HtmlAnchor>) page.getByXPath(Xpath);
		return anchorList;
	}	
	//ͨ��Xpath��ȡԪ��,����ȡʧ���򷵻�null
	static HtmlElement getElemByXpath(HtmlPage page, String Xpath) {
		@SuppressWarnings("unchecked")
		List<HtmlElement> elemList = (List<HtmlElement>) page.getByXPath(Xpath);
		if(0 == elemList.size()) {
			elemList.clear();
			return null;
		}
		else {
			HtmlElement ans = elemList.get(0);
			elemList.clear();
			return ans;
		}
	}	
	//ͨ��Xpath��ȡElementList,����ȡʧ�ܷ���sizeΪ0��List<HtmlElement>
	static List<HtmlElement> getElemListByXpath(HtmlPage page, String Xpath) {
		@SuppressWarnings("unchecked")
		List<HtmlElement> elemList = (List<HtmlElement>) page.getByXPath(Xpath);
		return elemList;
	}	
	
	
}
