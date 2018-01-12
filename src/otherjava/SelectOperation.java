package otherjava;

import java.util.ArrayList;
import java.util.List;
/*��Խ����ɸѡ�йصĲ���*/
public class SelectOperation {
	/*����ѡ��*/
	public final static String[] sortOperationString = {"���ʱ��", "�ܷ���ʱ��", "�ܼ۸�"};
	public final static int sortByDepTime = 0;  /*���ʱ��*/
	public final static int sortByFlyTime = 1;  /*�ܷ���ʱ��*/
	public final static int sortByPrice = 2;    /*�ܼ۸�*/
	/*��ѯ���ʹ�õĹ���*/
	public final static String[] resultUsedRule = {
			"ֱ��--���ҹ����չ�˾������ֱ��",
			"1��ת��--��ת������--���ҹ����չ�˾����",
			"1��ת��--��ת��Ŀ�ĵ����ڹ���--��תǰ�������ҹ����չ�˾����",
			"1��ת��--��ת��Ŀ�ĵ�������--��תǰ�������ҹ����չ�˾����",
			"������м�ֱ��",
			"2��ת��--��1��ת������--��2��ת��Ŀ�ĵ����ڹ���--ǰ���������ҹ����չ�˾����",
			"2��ת��--��1��ת������--��2��ת��Ŀ�ĵ����ڴ���--ǰ���������ҹ����չ�˾����",
			"û�з��ϡ�����Ա��Ʊ����ԭ���Ҳ�������"};
	public final static int noTransferPriority = 0;/*����ת����*/
	public final static int oneTransferInChinaPriority = 1;
	public final static int oneTransferInToCityCountryPriority = 2;
	public final static int oneTransferInToCityContinentPriority = 3;
	public final static int outToOutPriority = 4;
	public final static int twoTransferInToCityCountryPriority = 5;
	public final static int twoTransferInToCityContinentPriority = 6;
	public final static int noAnswerPriority = 7;/*�޽��*/
	/*���ݿ�����ʵ��*/	
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
		String hourString = "Сʱ";
		String minuteString = "��";
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
	/*��ɸѡ�������ѡ������*/
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
	/*�ж����������ѡ���Ƿ���ȷ*/
	public static boolean isSortChoiceCorrect(int choice) {
		if(sortOperationString.length - 1 >= choice && 0 <= choice) {
			return true;
		}
		else {
			return false;
		}
	}
	/*�ж����������Ƿ�Ϊ���ڳ���*/
	public static boolean isAllChinaCity(String city1, String city2) {
		if(jdbc.isChinaCity(city1) && jdbc.isChinaCity(city2)) {
    		return true;
    	}
		else {
			return false;
		}
	}
	/* ���ܣ�ɸѡ������
	 * ���룺��ѯ���ĺ�����
	 * �����ɸѡ��ĺ�����Ϣ*/
	public static SelectedAnswer getSelectedData(String fromCity, String toCity, List<FlightData> rowData){
		List<FlightData> ans = new ArrayList<FlightData>();//��ѯ��� 	
		int ansPriority = noAnswerPriority;//��ѯ��������ȼ�
    	//�ӹ��ڵ�����
    	if(jdbc.isChinaCity(fromCity) && !jdbc.isChinaCity(toCity)) {
    		for(int i = 0; i <= rowData.size() - 1; i++) {
    			FlightData temp = rowData.get(i);
    			//ɸѡ���ҹ����չ�˾������ֱ��ĺ���
    			if(ansPriority >= noTransferPriority && 0 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
    				if(ansPriority > noTransferPriority) {
    					ansPriority = noTransferPriority;
    					ans.clear();
    				}
    				ans.add(temp);
    			}
    			//ɸѡ����һ��ת������ת����Ϊ�ҹ������ҵڶ�������Ϊ�ҹ����չ�˾���˵ĺ���
    			else if(ansPriority >= oneTransferInChinaPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isChinaCity(temp.getTransferCity().get(0))
            			&& jdbc.isChinaFlight(temp.getCompany().get(1))) {
            		if(ansPriority > oneTransferInChinaPriority) {
    					ansPriority = oneTransferInChinaPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//ɸѡ����ת���ҵ�һ����Ϊ�ҹ����չ�˾/��ת������Ŀ�ĵ�Ϊͬһ����
    			else if(ansPriority >= oneTransferInToCityCountryPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameCountry(temp.getTransferCity().get(0), toCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
            		if(ansPriority > oneTransferInToCityCountryPriority) {
    					ansPriority = oneTransferInToCityCountryPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//ɸѡ����ת���ҵ�һ����Ϊ�ҹ����չ�˾/��ת������Ŀ�ĵ�Ϊͬһ����
    			else if(ansPriority >= oneTransferInToCityContinentPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameContinent(temp.getTransferCity().get(0), toCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
            		if(ansPriority > oneTransferInToCityContinentPriority) {
    					ansPriority = oneTransferInToCityContinentPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    			//ɸѡ����2��ת����ǰ������Ϊ�ҹ����չ�˾/��һ��ת����Ϊ�ҹ�����/�ڶ���ת������Ŀ�ĵ�Ϊͬһ����
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
    			//ɸѡ����2��ת����ǰ������Ϊ�ҹ����չ�˾/��һ��ת����Ϊ�ҹ�����/�ڶ���ת������Ŀ�ĵ�Ϊͬһ����
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
    	//�ӹ���ع���
    	else if(!jdbc.isChinaCity(fromCity) && jdbc.isChinaCity(toCity)) {
    		for(int i = 0; i <= rowData.size() - 1; i++) {
    			FlightData temp = rowData.get(i);
    			//ɸѡ���ҹ����չ�˾������ֱ��ĺ���
    			if(ansPriority >= noTransferPriority && 0 == temp.getTransferTime() 
    					&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
    				if(ansPriority > noTransferPriority) {
    					ansPriority = noTransferPriority;
    					ans.clear();
    				}
    				ans.add(temp);
    			}
    			//ɸѡ����һ��ת������ת����Ϊ�ҹ������ҵ�һ������Ϊ�ҹ����չ�˾���˵ĺ���
    			else if(ansPriority >= oneTransferInChinaPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isChinaCity(temp.getTransferCity().get(0))
            			&& jdbc.isChinaFlight(temp.getCompany().get(0))) {
            		if(ansPriority > oneTransferInChinaPriority) {
    					ansPriority = oneTransferInChinaPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//ɸѡ����ת���ҵڶ�����Ϊ�ҹ����չ�˾/��ת��������ʼ��Ϊͬһ����
    			else if(ansPriority >= oneTransferInToCityCountryPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameCountry(temp.getTransferCity().get(0), fromCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(1))) {
            		if(ansPriority > oneTransferInToCityCountryPriority) {
    					ansPriority = oneTransferInToCityCountryPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
            	//ɸѡ����ת���ҵڶ�����Ϊ�ҹ����չ�˾/��ת��������ʼ��Ϊͬһ����
    			else if(ansPriority >= oneTransferInToCityContinentPriority && 1 == temp.getTransferTime() 
    					&& jdbc.isInSameContinent(temp.getTransferCity().get(0), fromCity)
            			&& jdbc.isChinaFlight(temp.getCompany().get(1))) {
            		if(ansPriority > oneTransferInToCityContinentPriority) {
    					ansPriority = oneTransferInToCityContinentPriority;
    					ans.clear();
    				}
    				ans.add(temp);
            	}
    			//ɸѡ����2��ת���Һ�������Ϊ�ҹ����չ�˾/�ڶ���ת����Ϊ�ҹ�����/��һ��ת��������ʼ��Ϊͬһ����
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
    			//ɸѡ����2��ת���Һ�������Ϊ�ҹ����չ�˾/�ڶ���ת����Ϊ�ҹ�����/��һ��ת��������ʼ��Ϊͬһ����
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
    	//�ڹ���֮�����
    	else if(jdbc.isChinaCity(fromCity) && !jdbc.isChinaCity(toCity)) {
    		for(int i = 0; i <= rowData.size() - 1; i++) {
    			FlightData temp = rowData.get(i);
    			//ɸѡ���ҹ����չ�˾������ֱ��ĺ���
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
