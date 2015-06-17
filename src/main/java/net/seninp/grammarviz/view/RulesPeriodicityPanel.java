package net.seninp.grammarviz.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.hawaii.jmotif.grammarviz.logic.MotifChartData;
import edu.hawaii.jmotif.grammarviz.view.table.CellDoubleRenderer;
import edu.hawaii.jmotif.grammarviz.view.table.PeriodicityTableModel;
import edu.hawaii.jmotif.grammarviz.view.table.PrunedRulesTableColumns;

/**
 * 
 * handling the chart panel and sequitur rules table
 * 
 * 
 */

public class RulesPeriodicityPanel extends JPanel implements ListSelectionListener,
    PropertyChangeListener {

  /** Fancy serial. */
  private static final long serialVersionUID = -2710973854572981568L;

  public static final String FIRING_PROPERTY_PERIOD = "selectedRow_periodicity";

  private PeriodicityTableModel periodicityTableModel = new PeriodicityTableModel();

  private JXTable periodicityTable;

  private MotifChartData chartData;

  private JScrollPane periodicityRulesPane;

  private String selectedRule;

  private boolean acceptListEvents;

  // the logger business
  //
  private static Logger consoleLogger;
  private static Level LOGGING_LEVEL = Level.DEBUG;
  static {
    consoleLogger = (Logger) LoggerFactory.getLogger(RulesPeriodicityPanel.class);
    consoleLogger.setLevel(LOGGING_LEVEL);
  }

  /**
   * Constructor.
   */
  public RulesPeriodicityPanel() {
    super();
    this.periodicityTableModel = new PeriodicityTableModel();
    this.periodicityTable = new JXTable() {

      private static final long serialVersionUID = 2L;

      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JXTableHeader(columnModel) {
          private static final long serialVersionUID = 1L;

          @Override
          public void updateUI() {
            super.updateUI();
            // need to do in updateUI to survive toggling of LAF
            if (getDefaultRenderer() instanceof JLabel) {
              ((JLabel) getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

            }
          }
        };
      }

    };
    this.periodicityTable.setModel(periodicityTableModel);
    this.periodicityTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.periodicityTable.setShowGrid(false);
    this.periodicityTable.setDefaultRenderer(Double.class, new CellDoubleRenderer());

    this.periodicityTable.getSelectionModel().addListSelectionListener(this);

    @SuppressWarnings("unused")
    org.jdesktop.swingx.renderer.DefaultTableRenderer renderer = (org.jdesktop.swingx.renderer.DefaultTableRenderer) periodicityTable
        .getDefaultRenderer(String.class);

    TableRowSorter<PeriodicityTableModel> sorter = new TableRowSorter<PeriodicityTableModel>(
        periodicityTableModel);
    periodicityTable.setRowSorter(sorter);
    // sorter.setComparator(PackedTableColumns.CLASS_NUMBER.ordinal(),
    // expandedRuleComparator);

    this.periodicityRulesPane = new JScrollPane(periodicityTable);
  }

  /**
   * Set the new data.
   * 
   * @param chartData the new data.
   */
  public void setChartData(MotifChartData chartData) {

    this.acceptListEvents = false;

    // save the data
    this.chartData = chartData;

    // update
    periodicityTableModel.update(this.chartData.getGrammarRules());

    // put new data on show
    resetPanel();

    this.acceptListEvents = true;
  }

  /**
   * create the panel with the sequitur rules table
   * 
   * @return sequitur panel
   */
  public void resetPanel() {
    // cleanup all the content
    this.removeAll();
    this.add(periodicityRulesPane);
    this.validate();
    this.repaint();
  }

  /**
   * @return packed table model
   */
  public PeriodicityTableModel getPeriodicityTableModel() {
    return periodicityTableModel;
  }

  /**
   * @return sequitur table
   */
  public JTable getPeriodicityTable() {
    return periodicityTable;
  }

  @Override
  public void valueChanged(ListSelectionEvent arg) {

    if (!arg.getValueIsAdjusting() && this.acceptListEvents) {
      int col = periodicityTable.getSelectedColumn();
      int row = periodicityTable.getSelectedRow();
      String rule = String.valueOf(periodicityTable.getValueAt(row,
          PrunedRulesTableColumns.CLASS_NUMBER.ordinal()));
      consoleLogger.debug("Selected ROW: " + row + " - COL: " + col + "; rule: " + rule);
      this.firePropertyChange(FIRING_PROPERTY_PERIOD, this.selectedRule, rule);
      this.selectedRule = rule;
    }

  }

  @Override
  public void propertyChange(PropertyChangeEvent arg0) {
    // TODO Auto-generated method stub
  }

  /**
   * Clears the panel.
   */
  public void clear() {
    this.acceptListEvents = false;
    this.removeAll();
    this.chartData = null;
    periodicityTableModel.update(null);
    this.validate();
    this.repaint();
    this.acceptListEvents = true;
  }

}
