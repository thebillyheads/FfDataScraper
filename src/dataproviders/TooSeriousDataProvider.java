package dataproviders;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.w3c.dom.Node;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import data.Log;
import data.LogLevel;
import data.Player;

public class TooSeriousDataProvider extends DataProviderObservable {

	private ExecutorService executor;

	public TooSeriousDataProvider(Observer[] observers) {
		super(observers);
		this.observers = observers;

		// Create a new thread
		executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			scrape();
		});
	}

	private void scrape() {
		/* turn off annoying htmlunit warnings */
		java.util.logging.Logger.getLogger("com.gargoylesoftware").addHandler(new java.util.logging.ConsoleHandler());
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

		WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		webClient.getOptions().setJavaScriptEnabled(false);

		try {
			int totalRowCount = 0;

			for (int pageNumber = 1; pageNumber < 18; pageNumber++) {

				HtmlPage page = webClient.getPage(getTablePageUrl(pageNumber));

				final HtmlTable table = page.getHtmlElementById("main");

				int rowCount = 0;
				for (final HtmlTableRow row : table.getRows()) {

					if (rowCount > 0) {
						if (processRow(totalRowCount, row)) {
							totalRowCount++;
						}
					}
					rowCount++;
				}
			}

			// Add Matthew Cottrell MID,FWD CAR 102400 because for some reason Too Serious
			// doesn't have him anymore.
			// postLog(new Log(LogLevel.Info, "Adding Matthew Cottrell because TooSerious doesn't have him listed."));
			// postData(new Player(totalRowCount + 1, "Matthew Cottrell", "CAR", "MID,FWD", 102400, 0, 0));

		} catch (Exception e) {
			postLog(new Log(LogLevel.Error, "Error processing TooSerious data " + e.getMessage()));
		}
		webClient.close();
	}

	private boolean processRow(int totalRowCount, final HtmlTableRow row) {
		try {
			List<HtmlTableCell> cells = row.getCells();

			// for (final HtmlTableCell cell : cells) {
			// postLog(new Log(LogLevel.Debug, "Found cell: " + cell.asText()));
			// }

			if (cells.size() > 12) {
				String name = cells.get(4).asText().trim();
				String position = convertToRdtFormat(cells.get(1).asText().trim());
				String team = getTeam(cells.get(2)).trim();
				int price = Integer.parseInt(cells.get(5).asText().replace(",", "").trim());
				int games = Integer.parseInt(cells.get(7).asText().trim());
				int total = Integer.parseInt(cells.get(6).asText().trim());
				Player player = new Player(totalRowCount + 1, name, team, position, price, games, total);

				player.name = adjustNamesToMatchTheStupicSpreadSheet(player);

				if (postData(player)) {
					return true;
				}
			}
		} catch (Exception e) {
			postLog(new Log(LogLevel.Error, "Error processing player: " + e.getMessage()));
		}
		return false;
	}

	private String adjustNamesToMatchTheStupicSpreadSheet(Player player) {
		if (player.name.equalsIgnoreCase("Mitchell Mcgovern") && player.team.equalsIgnoreCase("CAR")) {
			postLog(new Log(LogLevel.Info, "Changing Mitchell Mcgovern (CAR) to Mitch Mcgovern (CAR)"));
			return "Mitch Mcgovern";
		}

		// Give Tom Lynch from GCS a middle name so he can be distinguished from Tom
		// Lynch from ADE
		if (player.name.equalsIgnoreCase("Tom Lynch") && player.team.equalsIgnoreCase("RIC")) {
			postLog(new Log(LogLevel.Info, "Changing Tom Lynch (RIC) to Tom J. Lynch (RIC)"));
			return "Tom J. Lynch";
		}

		return player.name;
	}

	private String convertToRdtFormat(String position) {
		switch (position) {
		case "DEF":
			return "BAC";
		case "M/F":
			return "MID,FWD";
		case "M/D":
			return "BAC,MID";
		case "F/D":
			return "BAC,FWD";
		case "R/F":
			return "FWD,RUC";
		case "R/D":
			return "BAC,RUC";
		}
		return position;
	}

	private String getTeam(HtmlTableCell htmlTableCell) {
		DomNodeList<DomNode> childNodes = htmlTableCell.getChildNodes();
		DomNode domNode = childNodes.get(1);
		Node item = domNode.getAttributes().item(0);
		String url = item.toString();
		int indexOf = url.indexOf("team=");
		String team = url.substring(indexOf + 5, indexOf + 8);
		return team.toUpperCase();
	}

	private String getTablePageUrl(int pageNumber) {
		return "http://tooserious.net/forum/stats.php?&sort=name&asc=asc&year=2019&comp=SC&team=ADE,BRL,CAR,COL,ESS,FRE,GCS,GEE,GWS,HAW,MEL,NTH,PTA,RIC,STK,SYD,WBD,WCE&pos=DEF,FWD,MID,RUC&salary=0&page num="
				+ pageNumber;
	}

	@Override
	public void close() {
		executor.shutdownNow();
	}

}
