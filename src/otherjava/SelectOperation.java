package otherjava;

import java.util.ArrayList;
import java.util.List;
/*与对结果的筛选有关的操作*/
public class SelectOperation {
	/*排序选项*/
	public final static String[] sortOperationString = {"起飞时间", "总飞行时间", "总价格"};
	public final static int sortByDepTime = 0;  /*起飞时间*/
	public final static int sortByFlyTime = 1;  /*总飞行时间*/
	public final static int sortByPrice = 2;    /*总价格*/
	/*查询结果使用的规则*/
	public final static String[] resultUsedRule = {
			"直达--由我国航空公司承运且直达",
			"1次转机--中转至国内--由我国航空公司承运",
			"1次转机--中转至目的地所在国家--中转前航班由我国航空公司承运",
			"1次转机--中转至目的地所在洲--中转前航班由我国航空公司承运",
			"国外城市间直达",
			"2次转机--第1中转至国内--第2中转至目的地所在国家--前两航班由我国航空公司承运",
			"2次转机--第1中转至国内--第2中转至目的地所在大洲--前两航班由我国航空公司承运",
			"没有符合《公务员机票报销原则》且不需审批"};
	public final static int noTransferPriority = 0;/*无中转航班*/
	public final static int oneTransferInChinaPriority = 1;
	public final static int oneTransferInToCityCountryPriority = 2;
	public final static int oneTransferInToCityContinentPriority = 3;
	public final static int outToOutPriority = 4;
	public final static int twoTransferInToCityCountryPriority = 5;
	public final static int twoTransferInToCityContinentPriority = 6;
	public final static int noAnswerPriority = 7;/*无结果*/
	/*数据库连接实例*/	
	private static JDBC jdbc = new JDBC();
	private static int comparePrice(FlightData data1, FlightData data2) {
		if(Integer.parseInt(data1.getPrice()) > Integer.parseInt((data2.getPrice()))) {
			return 1;
		}
		else if(Integer.parseInt(data1.getPrice()) == Integer.parseInt((data2.getPrice()))) {
			return 0;
		}
		else {
			return -1;
		}
	}
	private static int compareDepTime(FlightData data1, FlightData data2) {
		if(0 < data1.getFromTime().compareTo(data2.getFromTime())) {
			return 1;
		}
		else if(0 == data1.getFromTime().compareTo(data2.getFromTime())) {
			return 0;
		}
		else {
			return -1;
		}
	}
	private static int compareFlyTime(FlightData data1, FlightData data2) {
		String hourString = "小时";
		String minuteString = "分";
		String flyTime1 = data1.getFlyTime();
		int hourPos1 = flyTime1.indexOf(hourString);
		int minutePos1 = flyTime1.indexOf(minuteString);
		int hour1 = Integer.parseInt(flyTime1.substring(0, hourPos1));
		int minute1 = 0;
		if(0 <= minutePos1) {
			minute1 = Integer.parseInt(flyTime1.substring(hourPos1 + hourString.length(), minutePos1));
		}
		String flyTime2 = data2.getFlyTime();
		int hourPos2 = flyTime2.indexOf(hourString);
		int minutePos2 = flyTime2.indexOf(minuteString);
		int hour2 = Integer.parseInt(flyTime2.substring(0, hourPos2));
		int minute2 = 0;
		if(0 <= minutePos2) {
			minute2 = Integer.parseInt(flyTime2.substring(hourPos2 + hourString.length(), minutePos2));
		}
		if(hour1 > hour2 || (hour1 == hour2 && minute1 > minute2)) {
			return 1;
		}
		else if(hour1 == hour2 && minute1 == minute2) {
			return 0;
		}
		else {
			return -1;
		}
	}
	/*对筛选结果按照选项排序*/
	public static void sortResultList(List<FlightData> rowDataList, int sortOperation) {
		if(sortByDepTime == sortOperation) {
			for(int pass = 1; pass <= rowDataList.size() - 1; pass++) {
				for(int i = 0; i <= rowDataList.size() - pass - 1; i++) {
					if(0 < compareDepTime(rowDataList.get(i), rowDataList.get(i + 1))) {
						FlightData temp = rowDataList.get(i);
						rowDataList.set(i, rowDataList.get(i + 1));
						rowDataList.set(i + 1, temp);
					}
					else if(0 == compareDepTime(rowDataList.get(i), rowDataList.get(i + 1))) {
						if(0 < compareFlyTime(rowDataList.get(i), rowDataList.get(i + 1))) {
							FlightData temp = rowDataList.get(i);
							rowDataList.set(i, rowDataList.get(i + 1));
							rowDataList.set(i + 1, temp);
						}
						else if(0 == compareFlyTime(rowDataList.get(i), rowDataList.get(i + 1))) {
							if(0 < comparePrice(rowDataList.get(i), rowDataList.get(i + 1))) {
								FlightData temp = rowDataList.get(i);
								rowDataList.set(i, rowDataList.get(i + 1));
								rowDataList.set(i + 1, temp);
							}
							
						}
					}
				}
			}
		}
		else if(sortByFlyTime == sortOperation){
			for(int pass = 1; pass <= rowDataList.size() - 1; pass++) {
				for(int i = 0; i <= rowDataList.size() - pass - 1; i++) {
					if(0 < compareFlyTime(rowDataList.get(i), rowDataList.get(i + 1))) {
						FlightData temp = rowDataList.get(i);
						rowDataList.set(i, rowDataList.get(i + 1));
						rowDataList.set(i + 1, temp);
					}
					else if(0 == compareFlyTime(rowDataList.get(i), rowDataList.get(i + 1))) {
						if(0 < compareDepTime(rowDataList.get(i), rowDataList.get(i + 1))) {
							FlightData temp = rowDataList.get(i);
							rowDataList.set(i, rowDataList.get(i + 1));
							rowDataList.set(i + 1, temp);
						}
						else if(0 == compareDepTime(rowDataList.get(i), rowDataList.get(i + 1))) {
							if(0 < comparePrice(rowDataList.get(i), rowDataList.get(i + 1))) {
								FlightData temp = rowDataList.get(i);
								rowDataList.set(i, rowDataList.get(i + 1));
								rowDataList.set(i + 1, temp);
							}
							
						}
					}
				}
			}
		} 
		else if(sortByPrice == sortOperation){
			for(int pass = 1; pass <= rowDataList.size() - 1; pass++) {
				for(int i = 0; i <= rowDataList.size() - pass - 1; i++) {
					if(0 < comparePrice(rowDataList.get(i), rowDataList.get(i + 1))) {
						FlightData temp = rowDataList.get(i);
						rowDataList.set(i, rowDataList.get(i + 1));
						rowDataList.set(i + 1, temp);
					}
					else if(0 == comparePrice(rowDataList.get(i), rowDataList.get(i + 1))) {
						if(0 < compareDepTime(rowDataList.get(i), rowDataList.get(i + 1))) {
							FlightData temp = rowDataList.get(i);
							rowDataList.set(i, rowDataList.get(i + 1));
							rowDataList.set(i + 1, temp);
						}
						else if(0 == compareDepTime(rowDataList.get(i), rowDataList.get(i + 1))) {
							if(0 < compareFlyTime(rowDataList.get(i), rowDataList.get(i + 1))) {
								FlightData temp = rowDataList.get(i);
								rowDataList.set(i, rowDataList.get(i + 1));
								rowDataList.set(i + 1, temp);
							}
							
						}
					}
				}
			}
		} 
	}
	/*判断输入的排序选项是否正确*/
	public static boolean isSortChoiceCorrect(int choice) {
		if(sortOperationString.length - 1 >= choice && 0 <= choice) {
			return true;
		}
		else {
			return false;
		}
	}
	/*判断两个城市是否都为国内城市*/
	public static boolean isAllChinaCity(String city1, String city2) {
		if(jdbc.isChinaCity(city1) && jdbc.isChinaCity(city2)) {
    		return true;
    	}
		else {
			return false;
		}
	}
	/* 功能：筛选航班结果
	 * 输入：查询到的航班结果
	 * 输出：筛选后的航班信息*/
	public static SelectedAnswer getSelectedData(String fromCity, String toCity, List<FlightData> rowData){
		List<FlightData> ans = new ArrayList<FlightData>();//查询结果 	
		int ansPriority = noAnswerPriority;//查询结果的优先级
    	//从国内到国外
    	if(jdbc.isChinaCity(fromCity) && !jdbc.isChinaCity(toCity)) {
    		for(int i = 0; i <= rowData.size() - 1; i++) {
    			FlightData temp = rowData.get(i);
    			//筛选出我国航空公司承运且直达的航班
    			if(ansPriority >= noTransferPriority && 0 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
    				if(ansPriority > noTransferPriority) {
    					ansPriority = noTransferPriority;
    					ans.clear();
    				}
    				ans.add(temp);
    			}
    			//筛选出需一次转机，中转城市为我国城市且第二个航班为我国航空公司承运的航班
    			else if(ansPriority >= oneTransferInChinaPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isChinaCity(temp.getTransferCity().get(0))
            			&& jdbc.isChinaFlight(temp.getCompany().get(1))) {
            		if(ansPriority > oneTransferInChinaPriority) {
    					ansPriority = oneTransferInChinaPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//筛选出需转机且第一航班为我国航空公司/中转城市与目的地为同一国家
    			else if(ansPriority >= oneTransferInToCityCountryPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameCountry(temp.getTransferCity().get(0), toCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
            		if(ansPriority > oneTransferInToCityCountryPriority) {
    					ansPriority = oneTransferInToCityCountryPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//筛选出需转机且第一航班为我国航空公司/中转城市与目的地为同一大洲
    			else if(ansPriority >= oneTransferInToCityContinentPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameContinent(temp.getTransferCity().get(0), toCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
            		if(ansPriority > oneTransferInToCityContinentPriority) {
    					ansPriority = oneTransferInToCityContinentPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    			//筛选出需2次转机且前两航班为我国航空公司/第一中转城市为我国城市/第二中转城市与目的地为同一国家
    			else if(ansPriority >= twoTransferInToCityCountryPriority && 2 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0)) && jdbc.isChinaFlight(temp.getCompany().get(1))
    					&& jdbc.isChinaCity(temp.getTransferCity().get(0)) 
    					&& jdbc.isInSameCountry(temp.getTransferCity().get(1), toCity)) {
            		if(ansPriority > twoTransferInToCityCountryPriority) {
    					ansPriority = twoTransferInToCityCountryPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    			//筛选出需2次转机且前两航班为我国航空公司/第一中转城市为我国城市/第二中转城市与目的地为同一大洲
    			else if(ansPriority >= twoTransferInToCityContinentPriority && 2 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0)) && jdbc.isChinaFlight(temp.getCompany().get(1))
    					&& jdbc.isChinaCity(temp.getTransferCity().get(0)) 
    					&& jdbc.isInSameContinent(temp.getTransferCity().get(1), toCity)) {
            		if(ansPriority > twoTransferInToCityContinentPriority) {
    					ansPriority = twoTransferInToCityContinentPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    		}
    	}
    	//从国外回国内
    	else if(!jdbc.isChinaCity(fromCity) && jdbc.isChinaCity(toCity)) {
    		for(int i = 0; i <= rowData.size() - 1; i++) {
    			FlightData temp = rowData.get(i);
    			//筛选出我国航空公司承运且直达的航班
    			if(ansPriority >= noTransferPriority && 0 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
    				if(ansPriority > noTransferPriority) {
    					ansPriority = noTransferPriority;
    					ans.clear();
    				}
    				ans.add(temp);
    			}
    			//筛选出需一次转机，中转城市为我国城市且第一个航班为我国航空公司承运的航班
    			else if(ansPriority >= oneTransferInChinaPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isChinaCity(temp.getTransferCity().get(0))
            			&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
            		if(ansPriority > oneTransferInChinaPriority) {
    					ansPriority = oneTransferInChinaPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//筛选出需转机且第二航班为我国航空公司/中转城市与起始地为同一国家
    			else if(ansPriority >= oneTransferInToCityCountryPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameCountry(temp.getTransferCity().get(0), fromCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(1))) {
            		if(ansPriority > oneTransferInToCityCountryPriority) {
    					ansPriority = oneTransferInToCityCountryPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//筛选出需转机且第二航班为我国航空公司/中转城市与起始地为同一大洲
    			else if(ansPriority >= oneTransferInToCityContinentPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameContinent(temp.getTransferCity().get(0), fromCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(1))) {
            		if(ansPriority > oneTransferInToCityContinentPriority) {
    					ansPriority = oneTransferInToCityContinentPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    			//筛选出需2次转机且后两航班为我国航空公司/第二中转城市为我国城市/第一中转城市与起始地为同一国家
    			else if(ansPriority >= twoTransferInToCityCountryPriority && 2 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(1)) && jdbc.isChinaFlight(temp.getCompany().get(2))
    					&& jdbc.isChinaCity(temp.getTransferCity().get(1)) 
    					&& jdbc.isInSameCountry(temp.getTransferCity().get(0), fromCity)) {
            		if(ansPriority > twoTransferInToCityCountryPriority) {
    					ansPriority = twoTransferInToCityCountryPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    			//筛选出需2次转机且后两航班为我国航空公司/第二中转城市为我国城市/第一中转城市与起始地为同一大洲
    			else if(ansPriority >= twoTransferInToCityContinentPriority && 2 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(1)) && jdbc.isChinaFlight(temp.getCompany().get(2))
    					&& jdbc.isChinaCity(temp.getTransferCity().get(1)) 
    					&& jdbc.isInSameContinent(temp.getTransferCity().get(0), fromCity)) {
            		if(ansPriority > twoTransferInToCityContinentPriority) {
    					ansPriority = twoTransferInToCityContinentPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    		}
    	}
    	//在国外之间飞行
    	else if(jdbc.isChinaCity(fromCity) && !jdbc.isChinaCity(toCity)) {
    		for(int i = 0; i <= rowData.size() - 1; i++) {
    			FlightData temp = rowData.get(i);
    			//筛选出我国航空公司承运且直达的航班
    			if(ansPriority >= noTransferPriority && 0 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
    				if(ansPriority > noTransferPriority) {
    					ansPriority = noTransferPriority;
    					ans.clear();
    				}
    				ans.add(temp);
    			}
    			else if(ansPriority >= outToOutPriority) {
            		if(ansPriority > outToOutPriority) {
    					ansPriority = outToOutPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    		}
    	}
    	if(ansPriority == noAnswerPriority) {
    		ans = rowData;
    	}
    	SelectedAnswer selectedAns = new SelectedAnswer(ans, ansPriority);
        return selectedAns;     
	}
}
