package data_analysis;

public class Application {
	public static void main(String[] args) {
		SnapshotAnalyzer sa = new SnapshotAnalyzer();

		sa.readFilesInSequence();
		sa.readFilesAndLoadRecordsAsMessages();
		sa.showFinancialInstrumentList();
	}
}
