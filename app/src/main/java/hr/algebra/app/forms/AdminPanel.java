package hr.algebra.app.forms;

import hr.algebra.app.background.AdminArticleLoadWorker;
import hr.algebra.app.background.AdminDeleteWorker;
import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;
import hr.algebra.dao.rss.RssImportService;
import hr.algebra.dao.rss.RssSource;
import hr.algebra.utilities.gui.DialogUtils;
import hr.algebra.utilities.gui.Messages;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminPanel extends JPanel {
    JPanel stack = new JPanel();

    RssImportService importService;

    public AdminPanel() {
        try {
            importService    = new RssImportService();
        }
        catch (AssetException exception) {
            DialogUtils.showError(this, exception.getMessage());
        }

        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        statusMsg = new ArrayList<>();
        listOfJComboBoxes = new ArrayList<>();

        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(deleteAllCard());
        stack.add(deleteSourceCard());
        stack.add(reloadSourceCard());
        stack.add(loadArticlesCard());

        add(stack, BorderLayout.NORTH);
    }


    private List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes;
    private List<JLabel> statusMsg;

    AdminDeleteWorker adminDeleteWorker;
    private Component deleteAllCard() {
        JPanel card = buildCustomPanel();

        card.add(buildTitle("Delete all data"));
        card.add(buildDescription(
                "Removes all articles, authors, categories and their images."
        ));

        JButton deleteAllBtn = new JButton("Delete all data");
        deleteAllBtn.putClientProperty("FlatLaf.style", "background: #8c2828; foreground: #fff; margin: 4,12,4,12");
        deleteAllBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel deleteAllSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deleteAllSection.setAlignmentX(LEFT_ALIGNMENT);
        deleteAllSection.setOpaque(false);
        deleteAllSection.add(deleteAllBtn);
        deleteAllSection.add(Box.createHorizontalStrut(50));

        JLabel statusLabelDeleteAll = new JLabel();
        deleteAllSection.add(statusLabelDeleteAll);
        statusMsg.add(statusLabelDeleteAll);

        deleteAllBtn.addActionListener(e -> {
            if(!DialogUtils.confirm(this, Messages.CONFIRM_DELETE_ALL)) {
                return;
            }

            deleteAllBtn.setEnabled(false);
            clearLabelsOfOtherCars(statusLabelDeleteAll);
            deleteAllLogic(statusLabelDeleteAll, deleteAllBtn);
        });

        card.add(deleteAllSection);

        return card;
    }

    private Component deleteSourceCard() {
        JPanel card = buildCustomPanel();

        card.add(buildTitle("Delete source"));
        card.add(buildDescription("Removes the selected source and all of its articles and images"));

        DefaultComboBoxModel<RssSource> sharedModel = new DefaultComboBoxModel<>(importService.getAllSources());
        JComboBox<RssSource> sourcesList = new JComboBox<>(sharedModel);

        Font font = sourcesList.getFont();
        sourcesList.setMaximumSize(new Dimension(250, sourcesList.getPreferredSize().height + 10));
        sourcesList.setFont(font.deriveFont(font.getSize() + 3.5f));

        listOfJComboBoxes.add(sharedModel);
        card.add(buildLabeledColumn(sourcesList));

        JButton deleteBtn = new JButton("Delete source");
        deleteBtn.putClientProperty("FlatLaf.style", "background: #8c2828; foreground: #fff; margin: 4,12,4,12");
        deleteBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel deleteSourceSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deleteSourceSection.setAlignmentX(LEFT_ALIGNMENT);
        deleteSourceSection.setOpaque(false);
        deleteSourceSection.add(deleteBtn);
        deleteSourceSection.add(Box.createHorizontalStrut(50));

        JLabel statusDeleteBySource = new JLabel();
        deleteSourceSection.add(statusDeleteBySource);
        statusMsg.add(statusDeleteBySource);

        deleteBtn.addActionListener(e -> {
            if(!DialogUtils.confirm(this, "This will delete source and all it's articles. Continue?")) {
                return;
            }

            deleteBtn.setEnabled(false);
            clearLabelsOfOtherCars(statusDeleteBySource);
            deleteBySourceLogic(
                    statusDeleteBySource,
                    deleteBtn,
                    (RssSource) sourcesList.getSelectedItem(),
                    listOfJComboBoxes
            );
        });

        card.add(deleteSourceSection);

        return card;
    }

    private Component reloadSourceCard() {
        JPanel card = buildCustomPanel();

        card.add(buildTitle("Reload sources"));
        card.add(buildDescription("Refreshes the source list from configuration and re-checks each feed URL."));

        JButton reloadBtn = new JButton("Reload sources");
        reloadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 4,12,4,12");
        reloadBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel reloadSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        reloadSection.setAlignmentX(LEFT_ALIGNMENT);
        reloadSection.setOpaque(false);
        reloadSection.add(reloadBtn);
        reloadSection.add(Box.createHorizontalStrut(50));

        JLabel statusReloadSources = new JLabel();
        reloadSection.add(statusReloadSources);
        statusMsg.add(statusReloadSources);

        reloadBtn.addActionListener(e -> {
            setStatusMsg(statusReloadSources, "Downloading..", null);
            clearLabelsOfOtherCars(statusReloadSources);
            reloadSourcesLogic();

            setStatusMsg(statusReloadSources, "Successfully reloaded sources.", Color.GREEN);
        });

        card.add(reloadSection);

        return card;
    }

    AdminArticleLoadWorker adminArticleLoadWorker;
    private Component loadArticlesCard() {
        JPanel card = buildCustomPanel();

        JLabel title = buildTitle("Load new articles");
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));

        card.add(title);

        JToggleButton allSourcesToggle = new JToggleButton("All sources");
        JToggleButton oneSourceToggle  = new JToggleButton("One source");

        allSourcesToggle.putClientProperty("FlatLaf.style",
                "margin: 5,5,5,5; selectedBackground: #2563eb; selectedForeground: #fff");
        oneSourceToggle.putClientProperty("FlatLaf.style",
                "margin: 5,5,5,5; selectedBackground: #2563eb; selectedForeground: #fff");

        ButtonGroup buttonGroup = new ButtonGroup();
        allSourcesToggle.setActionCommand("all");
        oneSourceToggle.setActionCommand("one");
        buttonGroup.add(allSourcesToggle);
        buttonGroup.add(oneSourceToggle);

        JPanel optionsSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optionsSection.setAlignmentX(LEFT_ALIGNMENT);
        optionsSection.setOpaque(false);
        optionsSection.add(allSourcesToggle);
        optionsSection.add(Box.createHorizontalStrut(8));
        optionsSection.add(oneSourceToggle);

        card.add(optionsSection);
        card.add(Box.createVerticalStrut(12));
        card.add(Box.createHorizontalStrut(5));

        DefaultComboBoxModel<RssSource> sharedModel = new DefaultComboBoxModel<>(importService.getAllSources());
        JComboBox<RssSource> sourcesList = new JComboBox<>(sharedModel);

        listOfJComboBoxes.add(sharedModel);

        Font font = sourcesList.getFont();
        sourcesList.setPreferredSize(new Dimension(250, sourcesList.getPreferredSize().height + 10));
        sourcesList.setFont(font.deriveFont(font.getSize() + 2.5f));

        JPanel comboBoxSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboBoxSection.setAlignmentX(LEFT_ALIGNMENT);
        comboBoxSection.setOpaque(false);
        comboBoxSection.add(buildLabeledColumn(sourcesList));
        comboBoxSection.add(Box.createVerticalStrut(65));

        card.add(comboBoxSection);

        JButton loadBtn = new JButton("Load articles");
        loadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 6,14,6,14");
        loadBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel loadBtnSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loadBtnSection.setAlignmentX(LEFT_ALIGNMENT);
        loadBtnSection.setOpaque(false);
        loadBtnSection.add(Box.createVerticalStrut(8));
        loadBtnSection.add(loadBtn);
        loadBtnSection.add(Box.createHorizontalStrut(50));

        JLabel loadArticles = new JLabel();
        loadBtnSection.add(loadArticles);
        statusMsg.add(loadArticles);

        card.add(loadBtnSection);
        card.add(Box.createVerticalStrut(12));

        oneSourceToggle.addActionListener(e -> {
            sourcesList.setEnabled(true);
        });

        allSourcesToggle.addActionListener(e -> {
            sourcesList.setEnabled(false);
        });
        allSourcesToggle.doClick();

        loadBtn.addActionListener(e -> {
            loadBtn.setEnabled(false);
            clearLabelsOfOtherCars(loadArticles);
            importLogic(
                    buttonGroup.getSelection(),
                    (RssSource) sourcesList.getSelectedItem(),
                    loadArticles,
                    loadBtn
            );
        });

        return card;
    }

    private void clearLabelsOfOtherCars(JLabel activeLabel) {
        for (JLabel label : statusMsg) {
            label.setVisible(Objects.equals(label, activeLabel));
        }
    }


    private JPanel buildCustomPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        result.setAlignmentX(LEFT_ALIGNMENT);
        result.add(Box.createVerticalStrut(10));

        return result;
    }

    private JLabel buildTitle(String title) {
        JLabel result = new JLabel(title);
        result.setFont(result.getFont().deriveFont(Font.BOLD));
        result.setAlignmentX(LEFT_ALIGNMENT);

        return result;
    }

    private JTextArea buildDescription(String description) {
        JTextArea result = new JTextArea(description);
        result.setRows(2);
        result.setEditable(false);
        result.setFocusable(false);
        result.setOpaque(false);
        result.setAlignmentX(Component.LEFT_ALIGNMENT);
        result.setMargin(new Insets(10, 0, 0, 0));

        return result;
    }

    private JPanel buildLabeledColumn(JComponent field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);

        JLabel label = new JLabel("Source");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        column.add(label);
        column.add(Box.createVerticalStrut(6));
        column.add(field);
        column.add(Box.createVerticalStrut(6));

        return column;
    }


    private void deleteAllLogic(JLabel statusLabel, JButton deleteAllBtn) {
        adminDeleteWorker = new AdminDeleteWorker(statusLabel, deleteAllBtn, null, null);
        try {
            setStatusMsg(statusLabel, "Loading...", null);
            adminDeleteWorker.execute();
        }
        catch (Exception exception) {
            setStatusMsg(statusLabel, "Error occurred!", Color.RED);
        }
    }

    private void deleteBySourceLogic(
            JLabel statusLabel, JButton deleteBtn, RssSource selectedItem, List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes
    ) {
        try {
            adminDeleteWorker = new AdminDeleteWorker(statusLabel, deleteBtn, selectedItem, listOfJComboBoxes);

            setStatusMsg(statusLabel, "Loading...", null);
            adminDeleteWorker.execute();
        }
        catch (Exception exception) {
            setStatusMsg(statusLabel, "Error occurred!", Color.RED);
        }
    }

    private void reloadSourcesLogic() {
        for(DefaultComboBoxModel<RssSource> jComboBox : listOfJComboBoxes) {
            jComboBox.removeAllElements();
            jComboBox.addAll(List.of(importService.getAllSources()));

            jComboBox.setSelectedItem(jComboBox.getElementAt(0));
        }
    }

    private void importLogic(ButtonModel selection, RssSource selectedItem, JLabel statusLabel, JButton loadBtn) {
        adminArticleLoadWorker = new AdminArticleLoadWorker(importService, statusLabel, loadBtn);
        try {
            setStatusMsg(statusLabel, "Downloading...", null);
            if (Objects.equals(selection.getActionCommand(), "all")) {
                adminArticleLoadWorker.execute();
            }
            else if (Objects.equals(selection.getActionCommand(), "one")) {
                SourceRepositoryImpl sourceRepository  = new SourceRepositoryImpl();

                List<Source> sources = sourceRepository.read();
                if(sources.contains(new Source(0, selectedItem.getName(), selectedItem.getFeedUrl()))) {
                    AdminDeleteWorker deleteSource = new AdminDeleteWorker(selectedItem.getFeedUrl());
                    deleteSource.execute();
                }

                adminArticleLoadWorker.setSource(selectedItem);
                adminArticleLoadWorker.execute();
            }
        }
        catch (Exception exception) {
            setStatusMsg(statusLabel, "Error occurred!", Color.RED);
        }
    }

    public static void setStatusMsg(JLabel label, String msg, Color color) {;
        Font fontStatusBold = label.getFont().deriveFont(Font.BOLD);
        Font fontStatusSize  = fontStatusBold.deriveFont(15f);

        label.setText(msg);
        label.setAlignmentX(RIGHT_ALIGNMENT);
        label.setFont(fontStatusSize);
        label.setForeground(color);
    }


    public static void removeSourceFromJComboBoxes(RssSource source, List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes ) {
        for(DefaultComboBoxModel<RssSource> jComboBox : listOfJComboBoxes) {
            jComboBox.removeElement(source);
        }
    }

}
