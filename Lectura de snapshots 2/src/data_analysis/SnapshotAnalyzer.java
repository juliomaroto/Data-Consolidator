package data_analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public class SnapshotAnalyzer {
	
	/*
	 * "SNAPSHOTS_FILES_DESCRIPTOR_PATH" constant: indicates the address of the properties file. 
	 * It indicates where the snapshots are located, at the time of being analyzed.
	 */
	
	private static final String SNAPSHOTS_FILES_DESCRIPTOR_PATH = "src/data_analysis/snapshots_path.properties";
	private static final String BASE_PATH = "src/snapshots/";
	
	private Map<String, FinancialInstrument> financialInstrumentMap;
	private static String[] filesInSnapshotsDir;
	
	private GZIPInputStream inputStream;
	private FileInputStream fis;
	private BufferedReader br;
	private Reader decoder;
	
	public SnapshotAnalyzer() {
		financialInstrumentMap = new TreeMap<String, FinancialInstrument>();
		File snapshotDir = new File(getSnapshotsPathName(SNAPSHOTS_FILES_DESCRIPTOR_PATH));
		filesInSnapshotsDir = readDirFiles(snapshotDir);
	}
	
	private String getSnapshotsPathName(String snapshotsFilePathDescriptor) {
		Properties properties = new Properties();
		String snapshotsPathName = "";

		try {
			properties.load(new FileInputStream(snapshotsFilePathDescriptor));
			snapshotsPathName = properties.getProperty("pathname");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return snapshotsPathName;
	}
	
	private String[] readDirFiles(File snapshotDir) {
		String [] fileArray = snapshotDir.list(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".gz");
		    }
		});

		if (null == fileArray) {
			System.out.println("There are no files in the directory.");
		} else {
			fileArray = orderStringArray(fileArray);
		}

		return fileArray;
	}
	
	private String[] orderStringArray(String fileArray[]) {
		Integer dirsArrayLength = fileArray.length;
		String aux;

		for (int i = 0; i < dirsArrayLength; i++) {
			for (int j = 1; j < i; j++) {
				if (-1 == fileArray[i].compareTo(fileArray[j])) {
					aux = fileArray[i];

					fileArray[i] = fileArray[j];
					fileArray[j] = aux;
				}
			}
		}

		return fileArray;
	}
	
	public void readFilesInSequence() {
		for (int i = 0; i < filesInSnapshotsDir.length; i++) {
			readFileAndFillMap(filesInSnapshotsDir[i].toString());
		}
	}

	private void readFileAndFillMap(String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(BASE_PATH);
		sb.append(fileName);

		try {
			fis = new FileInputStream(sb.toString());
			inputStream = new GZIPInputStream(fis);
			decoder = new InputStreamReader(inputStream);
			br = new BufferedReader(decoder);

			String record;

			while ((record = br.readLine()) != null) {
				String[] recordSplitted = record.split("\\|");
				fillMap(recordSplitted);
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillMap(String[] recordSplitted) {
		String key = recordSplitted[1].split("=")[1];

		if (null == financialInstrumentMap.get(key)) {
			financialInstrumentMap.put(key, new FinancialInstrument(key, 1));
		} else {
			financialInstrumentMap.get(key).increaseCoincidences();
		}
	}

	public void readFilesAndLoadRecordsAsMessages() {
		for (int i = 0; i < filesInSnapshotsDir.length; i++) {
			readFileAndLoadMessages(filesInSnapshotsDir[i].toString());
		}
	}

	private void readFileAndLoadMessages(String fileName) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(BASE_PATH);
		sb.append(fileName);

		try {
			fis = new FileInputStream(sb.toString());
			inputStream = new GZIPInputStream(fis);
			decoder = new InputStreamReader(inputStream);
			br = new BufferedReader(decoder);

			String record;

			while ((record = br.readLine()) != null) {
				String[] recordSplitted = record.split("\\|");
				String key = recordSplitted[1].split("=")[1];

				if (financialInstrumentMap.containsKey(key)) {

					FinancialInstrument finInstrument = financialInstrumentMap.get(key);
					Integer recordHour = Integer.parseInt((recordSplitted[0].trim()).split("\\s+")[1].split(":")[0]);
					ArrayList<FinancialInstrumentMessage> messageList = finInstrument.getFinancialInstrumentMessageList();

					Boolean found = false;

					for (FinancialInstrumentMessage finMessage : messageList) {
						if (recordHour == finMessage.getHourOfReceivedMessages()) {
							finMessage.increaseCoincidences();
							found = true;
						}
					}

					if (!found) {
						FinancialInstrumentMessage finMessage = new FinancialInstrumentMessage(key, recordHour);
						finInstrument.setMessageToInstrumentsList(finMessage);
					}

				}
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showFinancialInstrumentList() {
		try {
		    PrintWriter writer = new PrintWriter("snapshots_log.txt", "UTF-8");
		    
			for (Entry<String, FinancialInstrument> finInstrumentEntry : financialInstrumentMap.entrySet()) {
				FinancialInstrument finInstrument = finInstrumentEntry.getValue();
				
				writer.println(finInstrument);

				ArrayList<FinancialInstrumentMessage> finInstrumentMessageList = finInstrument.getFinancialInstrumentMessageList();

				for (FinancialInstrumentMessage finMessage : finInstrumentMessageList) {
					Integer hourOfReceivedMessages = finMessage.getHourOfReceivedMessages();
					
					StringBuilder sb = new StringBuilder();
					
					sb.append("\t\tSe recibieron ");
					sb.append(finMessage.getCoincidences());
					sb.append(" mensajes entre las ");
					sb.append(hourOfReceivedMessages);
					sb.append(" h y las ");
					sb.append((hourOfReceivedMessages + 1));
					sb.append(" h.");
					
					writer.println(sb);
				}
			}
		    
		    writer.close();
		} catch (IOException e) {
		   System.out.println("Error creating file. Check if your system permissions are correct for project file writing directory.");
		}
	}
}
