package root_servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import otherjava.*;

public class root_servlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public root_servlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws MalformedURLException, ServletException, IOException {
		long MAX_TIME = 45000;
		String fromCode;
		String searchArrivalAirport;
		String toCode;
		String fromDate;
		String searchArrivalTime;
		String choice0;
		String str="";
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type","charset=utf-8");
		String fromCity = request.getParameter("fromcity");
		String toCity = request.getParameter("tocity");
		searchArrivalTime = request.getParameter("searchArrivalTime");
		fromDate = request.getParameter("searchDepartureTime");
		choice0 = request.getParameter("choice0");
		PrintWriter out = response.getWriter();
		
		int choice = Integer.parseInt(choice0);

		/*new*/
		//后台处理
		int sleepTime = 2000;
		List<SelectedAnswer> allAnsList = new ArrayList<SelectedAnswer>();
		List<FlightData> finalAnsList = new ArrayList<FlightData>();
		if(SelectOperation.isAllChinaCity(fromCity, toCity)) {
			str +="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><list>"+"<total>"+"0"+"</total>"+"<content>"+"抱歉，本系统只针对国际航线的推荐！"+"</content>"+"</list>";
			out.println(str);
			out.flush();
			out.close();
			return;
		}
		if(false == SelectOperation.isSortChoiceCorrect(choice)) {
			return;
		}
		int siteNum = 3;
		Thread[] siteThread = new Thread[siteNum];
		GetLYData lyData = new GetLYData(fromCity, toCity, fromDate, allAnsList);
		GetCtripData ctripData = new GetCtripData(fromCity, toCity, fromDate, allAnsList);
		GetTuniuData tuniuData = new GetTuniuData(fromCity, toCity, fromDate, allAnsList);
		siteThread[0] = new Thread(lyData);
		siteThread[1] = new Thread(ctripData);
		siteThread[2] = new Thread(tuniuData);
		for(int i = 0; i <= siteNum - 1; i++) {
			siteThread[i].start();
		}
		long start = System.currentTimeMillis();
		
		while(true) {
			boolean over = true;
			for(int i = 0; i <= siteThread.length - 1; i++) {
				long end = System.currentTimeMillis();
				long costtime = end - start;
				if(MAX_TIME <= costtime) {
					over = true;
					break;
				}
				if(siteThread[i].isAlive()) {
					over = false;
					break;
				}
//				if(0 == allAnsList.size()) {
//					over = false;
//					break;
//				}
			}
			if(false == over) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				break;
			}
		}
		int minPriority = SelectOperation.noAnswerPriority;
		for(int i = 0; i <= allAnsList.size() - 1; i++) {
			if(null == allAnsList.get(i)) {
				continue;
			}
			if(minPriority > allAnsList.get(i).getSelectedPriority()) {
				minPriority = allAnsList.get(i).getSelectedPriority();
			}
		}
		for(int i = 0; i <= allAnsList.size() - 1; i++) {
			if(null == allAnsList.get(i)) {
				continue;
			}
			if(minPriority == allAnsList.get(i).getSelectedPriority()) {
				finalAnsList.addAll(allAnsList.get(i).getSelectedData());
			}
		}
		allAnsList.clear();
		SelectOperation.sortResultList(finalAnsList, choice);
		/*end of new*/
		
		
		
		
		if(0 == finalAnsList.size()) {
			str +="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><list>"+"<total>"+"0"+"</total>"+"<content>"+"未查询到航班信息   请确认输入正确并重新搜索"+"</content>"+"</list>";
			out.println(str);
		}
		else {
			
			str +="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><list>"+"<total>"+finalAnsList.size()+"</total>"+"<content>"+"航班满足的规则是：" + SelectOperation.resultUsedRule[minPriority]+"</content>";
			for(int i = 0; i <= finalAnsList.size() - 1; i++) 
			{
				FlightData temp = finalAnsList.get(i);
				str+="<result>";
				String NameCooperation = "";
	    		
	    		for(int j = 0; j <= temp.getTransferTime(); j++) {
	    			NameCooperation+= " "+temp.getCompany().get(j);
				}
	    		
	    		str+="<NameCooperation>"+"承运航空公司:"+NameCooperation+"</NameCooperation>";
	    		str += "<TimeDeparture>"+"出发时间:" + temp.getFromTime()+"</TimeDeparture>"
	    			+  "<LocationDeparture>"+"出发机场:" + temp.getFromPort()+"</LocationDeparture>"
	    			+"<TimeBetween>"+"总飞行时间:" + temp.getFlyTime()+"</TimeBetween>";
	    		String TransferCity="";
	    		for(int j = 0; j<temp.getTransferTime(); j++) 
	    		{
	    			TransferCity +=temp.getTransferCity().get(j)+"	中转机场:" + temp.getTransferPort().get(j);
			    }
	    		if(TransferCity=="")
	    			TransferCity="无";
	    		str+="<TransferCity>"+"中转城市:"+TransferCity+"</TransferCity>";
	    		str += "<TimeArrive>"+"到达时间:" + temp.getToTime()+"</TimeArrive>"
	    			+ "<LocationArrive>" +"到达机场:" + temp.getToPort()+"</LocationArrive>";
      			str+="<ChangePrice>"+"含税总价格：" + temp.getPrice()+"</ChangePrice>"
      			+"<FromSite>"+ temp.getFromSite() +"</FromSite>"
    			+"<TicketUrl>"+"<![CDATA["+temp.getUrl()+"]]>"+"</TicketUrl>";
	    		str+="</result>";	
	    	}
			str+="</list>";
			//str="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><list><total>1</total><content>以下为符合公务员乘坐标准的航班</content><result><NameCooperation>航空公司: 中国国航</NameCooperation><TimeDeparture>起飞时间:14:50</TimeDeparture><LocationDeparture>起飞机场:首都机场 PEK T3</LocationDeparture><TimeBetween>行程总时长:约10小时</TimeBetween><TransferCity>中转城市</TransferCity><TimeArrive>降落时间:18:40</TimeArrive><LocationArrive>降落机场:哥本哈根机场 CPH</LocationArrive><Tex>税款:</Tex><ChangePrice>票价:10110</ChangePrice><TicketUrl><![CDATA[http://flight.qunar.com/site/oneway_list_inter.htm?searchDepartureAirport=%E5%8C%97%E4%BA%AC&searchArrivalAirport=%E5%93%A5%E6%9C%AC%E5%93%88%E6%A0%B9&searchDepartureTime=2016-04-19&searchArrivalTime=2016-05-10&nextNDays=0&startSearch=true&fromCode=BJS&toCode=CPH&from=flight_int_search&lowestPrice=null&favoriteKey=&showTotalPr=0]]></TicketUrl></result></list>";
			out.println(str);
			out.flush();
			out.close();
		}
	
}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request,response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
