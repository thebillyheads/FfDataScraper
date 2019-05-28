package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import data.Log;
import data.LogLevel;

public class TakenPlayersReader extends Observable {

	String[][] data = null;
	private Observer[] observers;

	public TakenPlayersReader(String fullFilePath, Observer[] observers) {

		this.observers = observers;

		FileInputStream file = null;
		XSSFWorkbook workbook = null;
		try {
			postLog(new Log(LogLevel.Info, "Searching " + fullFilePath + " for taken players"));
			
			file = new FileInputStream(new File(fullFilePath));
			workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet("TAKEN PLAYERS");

			int playerCount = 0;

			int firstRow = sheet.getFirstRowNum();
			int lastRow = sheet.getLastRowNum();
			for (int rownum = firstRow + 1; rownum < lastRow; rownum++) {
				Row row = sheet.getRow(rownum);

				Cell cell = row.getCell(0);
				String playerName = cell.getStringCellValue();

				postPlayerName(playerName);

				playerCount++;
			}

			postLog(new Log(LogLevel.Info, "Found " + playerCount + " taken players"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
				file.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void postLog(Log log) {
		for (Observer observer : observers) {
			observer.update(this, log);
		}
	}

	private void postPlayerName(String playerName) {
		for (Observer observer : observers) {
			observer.update(this, playerName);
		}
	}
}
