package data_analysis;

import java.util.ArrayList;

public class FinancialInstrument {
	private String id;
	private Integer coincidences;
	private ArrayList<FinancialInstrumentMessage> financialInstrumentsMessageList;
	
	public FinancialInstrument(String id, Integer coincidences) {
		this.id = id;
		this.coincidences = coincidences;
		financialInstrumentsMessageList = new ArrayList<FinancialInstrumentMessage>();
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setMessageToInstrumentsList(FinancialInstrumentMessage financialMessage) {
		financialInstrumentsMessageList.add(financialMessage);
	}
	
	public ArrayList<FinancialInstrumentMessage> getFinancialInstrumentMessageList() {
		ArrayList<FinancialInstrumentMessage> financialInstrumentsMessageList = null;
		
		if (this.financialInstrumentsMessageList != null) {
			financialInstrumentsMessageList = this.financialInstrumentsMessageList;
		}
		
		return financialInstrumentsMessageList;
	}
	
	public void increaseCoincidences() {
		this.coincidences++;
	}
	
	public String toString() {
		return this.coincidences + "\tID=" + this.id;
	}
}
