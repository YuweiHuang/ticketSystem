package otherjava;

import java.util.ArrayList;
import java.util.List;

/*某个旅游网站的航班筛选结果*/
public class SelectedAnswer {
	private List<FlightData> selectedDate;/*航班筛选结果链表*/
	private int selectedPriority;/*结果筛选所使用的规则*/
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