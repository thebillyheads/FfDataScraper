import java.awt.EventQueue;
import java.util.Observer;

import dataproviders.TooSeriousDataProvider;
import gui.MainForm;
import interfaces.IDataProvider;
import utilities.ExcelAdapter;
import utilities.IntComparator;
import utilities.TakenPlayersReader;

public class Bootstrapper {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IntComparator intComparator = new IntComparator();
					MainForm frame = new MainForm(intComparator);

					@SuppressWarnings("unused")
					TakenPlayersReader takenPlayersReader = new TakenPlayersReader("C:\\Users\\tist\\Downloads\\DT\\Draft 2019\\SC Draft 2019 -1.0.xlsm", new Observer[] { frame });					
					
					@SuppressWarnings("unused")
					ExcelAdapter excelAdapter = new ExcelAdapter(frame.getTable());

					// Create the data provider
					IDataProvider dataProvider = new TooSeriousDataProvider(new Observer[] { frame });

					Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							dataProvider.close();
						}
					});

					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
