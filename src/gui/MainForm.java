package gui;

import java.awt.BorderLayout;
import utilities.IntComparator;
import utilities.TakenPlayersReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
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
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.event.ActionListener;
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
	private ArrayList<String> takenPlayers = new ArrayList<String>();

	/**
	 * Create the frame.
	 */
	public MainForm(IntComparator intComparator) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 833, 602);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		// contentPane.setLayout(new GridLayout(2, 1));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		tableModel = new DefaultTableModel(new Object[] { "Id", "Name", "Position", "Price", "Average", "Team", "Games",
				"BE", "Points Last Round", "Taken" }, 0);

		TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(tableModel);
		trs.setComparator(0, intComparator);
		trs.setComparator(6, intComparator);
		trs.setComparator(7, intComparator);

	    // Filtering support for the Taken field.
		RowFilter<Object, Object> notTakenFilter = RowFilter.numberFilter(ComparisonType.EQUAL, 0, 9);
		// Filtering support for the zero points last game field.
		RowFilter<Object, Object> zeroPointsLastGameFilter = RowFilter.numberFilter(ComparisonType.NOT_EQUAL, 0, 8);
	    
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
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

		JPanel checkBoxPanel = new JPanel();
		JCheckBox filterNotTakenCheckBox = new JCheckBox("Filter Taken?");
		JCheckBox filterZeroPointsLastGameCheckBox = new JCheckBox("Filter Zero Points Last Game?");
		
		ActionListener applyFilters = e -> {
			List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
			
			if (filterNotTakenCheckBox.isSelected()) {
			    filters.add(notTakenFilter);
			}
			
			if (filterZeroPointsLastGameCheckBox.isSelected()) {
			    filters.add(zeroPointsLastGameFilter);
			}
			trs.setRowFilter(RowFilter.andFilter(filters));
		};
		
		filterNotTakenCheckBox.addActionListener(applyFilters);
		checkBoxPanel.add(filterNotTakenCheckBox);

		filterZeroPointsLastGameCheckBox.addActionListener(applyFilters);
		checkBoxPanel.add(filterZeroPointsLastGameCheckBox);

		contentPane.add(checkBoxPanel);
		contentPane.add(tabbedPane);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof DataProviderObservable || arg0 instanceof TakenPlayersReader) {
			if (arg1 instanceof Player) {
				Player player = (Player) arg1;
				tableModel.addRow(new Object[] { player.id, player.name, player.position, player.price,
						getPlayerAverageAsString(player), player.team, player.games, player.breakEven,
						player.pointsLastRound, isPlayerTaken(player) ? 1 : 0 });
			}

			if (arg1 instanceof String) {
				takenPlayers.add((String) arg1);
			}

			if (arg1 instanceof Log) {
				Log dataProviderLog = (Log) arg1;
				if (dataProviderLog.logType.ordinal() <= LOG_LEVEL.ordinal()) {
					log.append(dataProviderLog.logType + " - " + dataProviderLog.message + System.lineSeparator());
				}
			}
		}
	}

	private Boolean isPlayerTaken(Player player) {
		for (String playerName : takenPlayers) {
			if (player.name.equalsIgnoreCase(playerName)) {
				return true;
			}
		}

		return false;
	}

	private String getPlayerAverageAsString(Player player) {
		return String.format("%.2f", player.games == 0 ? 0 : player.total / player.games);
	}

	public JTable getTable() {
		return table;
	}

}
