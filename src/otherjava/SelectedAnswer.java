package otherjava;

import java.util.ArrayList;
import java.util.List;

/*ĳ��������վ�ĺ���ɸѡ���*/
public class SelectedAnswer {
	private List<FlightData> selectedDate;/*����ɸѡ�������*/
	private int selectedPriority;/*���ɸѡ��ʹ�õĹ���*/
	public SelectedAnswer(List<FlightData> selectedDate, int selectedPriority) {
		this.selectedDate = selectedDate;
		this.selectedPriority = selectedPriority;
	}
	public List<FlightData> getSelectedData() {
		return selectedDate;
	}
	public int getSelectedPriority() {
		return selectedPriority;
	}
}