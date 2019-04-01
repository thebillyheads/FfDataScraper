package gui;

import java.awt.BorderLayout;
import utilities.IntComparator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import data.Log;
import data.LogLevel;
import data.Player;
import dataproviders.DataProviderObservable;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.ScrollPaneConstants;
import javax.swing.JTabbedPane;

public class MainForm extends JFrame implements Observer {

	private static final LogLevel LOG_LEVEL = LogLevel.Info;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextArea log;
	private JPanel panel;
	private JTabbedPane tabbedPane;

	/**
	 * Create the frame.
	 */
	public MainForm(IntComparator intComparator) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 833, 602);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		tableModel = new DefaultTableModel(
				new Object[] { "Id", "Price", "Average", "Name", "Position", "Team", "Games" }, 0);

		TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(tableModel);
		trs.setComparator(0, intComparator);
		trs.setComparator(6, intComparator);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);
		table = new JTable(tableModel);
		table.setColumnSelectionAllowed(true);
		table.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
		table.setCellSelectionEnabled(true);
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setFont(new Font("SansSerif", Font.ITALIC, 12));
		table.setRowSorter(trs);

		JScrollPane scrollPane = new JScrollPane(table);
		tabbedPane.addTab("Stats", null, scrollPane, null);

		panel = new JPanel();
		tabbedPane.addTab("Logs", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));

		log = new JTextArea();
		JScrollPane scroll = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		log.setEditable(false);
		log.setWrapStyleWord(true);
		log.setLineWrap(true);
		panel.add(scroll);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof DataProviderObservable) {
			if (arg1 instanceof Player) {
				Player player = (Player) arg1;
				tableModel.addRow(new Object[] { player.id, player.price, getPlayerAverageAsString(player), player.name,
						player.position, player.team, player.games });
			}

			if (arg1 instanceof Log) {
				Log dataProviderLog = (Log) arg1;
				if (dataProviderLog.logType.ordinal() <= LOG_LEVEL.ordinal()) {
					log.append(dataProviderLog.logType + " - " + dataProviderLog.message + System.lineSeparator());
				}
			}
		}
	}

	private String getPlayerAverageAsString(Player player) {
		return String.format("%.2f", player.games == 0 ? 0 : player.total / player.games);
	}

	public JTable getTable() {
		return table;
	}

}
