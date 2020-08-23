package de.eschoenawa.lanchat.ui.settings;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.helper.ServiceLocator;
import de.eschoenawa.lanchat.helper.Texts;
import de.eschoenawa.lanchat.util.ErrorHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SettingsUi extends JFrame {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 700;

    private Config config;
    private SettingsTableModel tableModel;

    private JPanel contentPane;
    private JScrollPane scrollPane;

    public static void main(String[] args) {
        //TODO remove main from settings ui
        EventQueue.invokeLater(() -> {
            try {
                SettingsUi frame = new SettingsUi(ServiceLocator.getConfig());
                frame.setVisible(true);
            } catch (Exception e) {
                ErrorHandler.reportError(e);
            }
        });
    }

    public SettingsUi(Config config) {
        this.config = config;
        setupWindow();
        setupContentPane();
        setupScrollPane();
        setupButtons();
        setupTable();
    }

    private void setupWindow() {
        setTitle(Texts.Settings.SETTINGS_UI_TITLE);
        setResizable(false); //TODO maybe allow this?
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dim.width / 2 - WIDTH / 2, dim.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
    }

    private void setupContentPane() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new GridBagLayout());
    }

    private void setupScrollPane() {
        scrollPane = new JScrollPane();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.9;
        c.weightx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        contentPane.add(scrollPane, c);
    }

    private void setupButtons() {
        JPanel spaceLeftOfButtons = new JPanel();
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 1;
        c1.weightx = 0.9;
        contentPane.add(spaceLeftOfButtons, c1);

        JButton buttonOk = new JButton(Texts.General.OK);
        buttonOk.addActionListener(actionEvent -> saveAndClose());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 1;
        c2.gridy = 1;
        c2.weightx = 0.05;
        c2.insets = new Insets(10, 5, 5, 5);
        c2.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(buttonOk, c2);

        JButton buttonCancel = new JButton(Texts.General.CANCEL);
        buttonCancel.addActionListener(actionEvent -> cancelAndClose());
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 2;
        c3.gridy = 1;
        c3.weightx = 0.05;
        c3.insets = new Insets(10, 5, 5, 5);
        c3.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(buttonCancel, c3);
    }

    private void setupTable() {
        this.tableModel = new SettingsTableModel(config);
        JTable table = new JTable(this.tableModel) {
            private static final long serialVersionUID = 1L;
            private Class<?> editingClass;

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    Class<?> rowClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultRenderer(rowClass);
                } else {
                    return super.getCellRenderer(row, column);
                }
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    editingClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultEditor(editingClass);
                } else {
                    return super.getCellEditor(row, column);
                }
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return editingClass != null ? editingClass : super.getColumnClass(column);
            }
        };
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);
        this.scrollPane.setViewportView(table);
    }

    private void saveAndClose() {
        //TODO check if restart required and show appropriate dialog
        this.tableModel.applyChanges();
        this.dispose();
    }

    private void cancelAndClose() {
        this.tableModel.cancelChanges();
        this.dispose();
    }
}
