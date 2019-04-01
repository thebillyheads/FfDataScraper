package dataproviders;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import data.Player;

public class DtLiveDataProvider extends DataProviderObservable {

	private ExecutorService executor;

	public DtLiveDataProvider(Observer[] observers) {
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
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.ALL);

		WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		webClient.getOptions().setJavaScriptEnabled(false);

		try {
			HtmlPage page = webClient.getPage("http://dtlive.com.au/afl/dataview.php");

			final HtmlTable table = page.getHtmlElementById("myTable");

			int rowCount = 0;

			for (final HtmlTableRow row : table.getRows()) {
				
				if (rowCount > 0) {
					List<HtmlTableCell> cells = row.getCells();
//					for (final HtmlTableCell cell : cells) {
//						System.out.println(" Found cell: " + cell.asText());
//					}
					if (cells.size() > 7) {
						String name = cells.get(1).asText();
						String position = cells.get(2).asText();
						int price = Integer.parseInt(cells.get(5).asText());
						int games = Integer.parseInt(cells.get(7).asText());
						int total = 0;
						Player player = new Player(rowCount, name, "", position, price, games, total);
						postData(player);
					}
				}
				rowCount++;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webClient.close();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
