package data_analysis;

public class FinancialInstrumentMessage {
	private String id;
	private Integer hourOfReceivedMessages;
	private Integer coincidences;
	
	public FinancialInstrumentMessage(String id, Integer hourOfReceivedMessages) {
		this.id = id;
		this.hourOfReceivedMessages = hourOfReceivedMessages;
		this.coincidences = 1;
	}
	
	public Integer getHourOfReceivedMessages() {
		return this.hourOfReceivedMessages;
	}
	
	public void increaseCoincidences() {
		this.coincidences++;
	}
	
	public Integer getCoincidences() {
		return this.coincidences;
	}
}
