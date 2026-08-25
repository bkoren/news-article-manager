package hr.algebra.app.panels;

import hr.algebra.dao.models.SearchParams;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

public class BasePanel<T extends SearchParams>  extends JPanel {
    protected JPanel      searchBar;
    protected JTextField  searchField;
    protected JButton     searchBtn;

    protected JPanel      btnSection;
    protected JButton     viewBtn;
    protected JButton     newBtn;
    protected JButton     editBtn;
    protected JButton     deleteBtn;

    protected List<T> mainEntityList;
    protected List<T> filteredEntityList;

    protected JTableHeader      tableHeader;
    protected DefaultTableModel tableModel;
    protected JScrollPane       displayTable;
    protected JTable            entityData;
    protected JLabel            mainMessageLabel;

    protected String searchQuery = "";

    protected BasePanel() {
        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        btnSection = buildBtnSection();
        searchBar  = buildSearchBar();

        topSection.add(searchBar, BorderLayout.WEST);
        topSection.add(btnSection, BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        mainMessageLabel = buildMainMessageLabel();
    }

    protected JTableHeader buildTableHeader() {
        JTableHeader header = entityData.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setFocusable(false);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));

        TableCellRenderer original = header.getDefaultRenderer();
        header.setDefaultRenderer((t, value, sel, focus, row, col) -> {
            Component component = original.getTableCellRendererComponent(t, value, sel, focus, row, col);
            if (component instanceof JLabel label) {
                int alignment = switch (col) {
                    case 1  -> SwingConstants.CENTER;
                    default -> SwingConstants.LEFT;
                };
                label.setHorizontalAlignment(alignment);
            }
            return component;
        });
        return header;
    }

    protected JScrollPane buildTable(String[] columnNames) {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        entityData = new JTable(tableModel);
        entityData.setFillsViewportHeight(true);
        entityData.setAutoCreateRowSorter(false);
        entityData.setFont(entityData.getFont().deriveFont(14f));
        entityData.setRowHeight(32);
        entityData.setRowMargin(4);
        entityData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entityData.removeColumn(entityData.getColumnModel().getColumn(0));

        tableHeader = entityData.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(true);
        tableHeader.setFocusable(false);
        tableHeader.setFont(tableHeader.getFont().deriveFont(Font.BOLD, 16f));

        TableCellRenderer original = tableHeader.getDefaultRenderer();

        tableHeader.setDefaultRenderer((t, value, sel, focus, row, col) -> {
            Component component = original.getTableCellRendererComponent(t, value, sel, focus, row, col);
            if (component instanceof JLabel label) {
                int alignment = switch (col) {
                    case 1, 2 -> SwingConstants.CENTER;
                    default -> SwingConstants.LEFT;
                };
                label.setHorizontalAlignment(alignment);
            }
            return component;
        });

        return new JScrollPane(entityData);
    }

    protected JDialog buildDialog(String message) {
        JDialog result = new JDialog(SwingUtilities.getWindowAncestor(this), message);
        result.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        result.setLayout(new FlowLayout());
        result.setMinimumSize(new Dimension(400, 100));
        result.setLocationRelativeTo(this);

        return result;
    }

    private JPanel buildBtnSection() {
        btnSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        newBtn    = new JButton("New");
        editBtn   = new JButton("Edit");
        deleteBtn = new JButton("Delete");

        btnSection.add(Box.createVerticalStrut(40));
        btnSection.add(newBtn);
        btnSection.add(editBtn);
        btnSection.add(deleteBtn);
        btnSection.add(Box.createHorizontalStrut(10));

        return btnSection;
    }

    private JPanel buildSearchBar() {
        searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        searchField = new JTextField(20);
        searchBtn = new JButton("Search");

        searchBar.add(Box.createVerticalStrut(40));
        searchBar.add(searchField);
        searchBar.add(searchBtn);

        return searchBar;
    }

    private JLabel buildMainMessageLabel() {
        JLabel result  = new JLabel();

        result.setFont(new Font("SansSerif", Font.BOLD, 48));
        result.setForeground(Color.RED);
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setVerticalAlignment(SwingConstants.CENTER);

        return result;
    }


    protected void modifyColumns() {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column = entityData.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column.setResizable(false);
        column.setCellRenderer(center);

        JTableHeader header = buildTableHeader();

        for (MouseListener l : header.getMouseListeners()) {
            header.removeMouseListener(l);
        }
        for (MouseMotionListener l : header.getMouseMotionListeners()) {
            header.removeMouseMotionListener(l);
        }
    }


    protected void refreshUi() {
        filteredEntityList = searchQuery.isEmpty()
                ? List.copyOf(mainEntityList)
                : mainEntityList.stream()
                    .filter(item -> item.getSearchParam().toLowerCase().contains(searchQuery))
                    .toList();
    }

    protected void displayMainMessage(String message) {
        mainMessageLabel.setText(message);

        if(displayTable != null) {
            displayTable.setVisible(false);
        }

        remove(mainMessageLabel);

        add(mainMessageLabel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }
}
