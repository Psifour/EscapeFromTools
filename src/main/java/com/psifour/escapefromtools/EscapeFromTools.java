package com.psifour.escapefromtools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.psifour.escapefromtools.json.ItemData;
import com.psifour.escapefromtools.json.ItemLocale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class EscapeFromTools {
    private JComboBox<ItemEntry> foundItems;
    private JTextField searchBox;
    private JButton searchButton;
    private JPanel escapeFromToolsPanel;
    private JTextArea itemDefinition;
    private JScrollPane scroller;

    public static File DB_DIRECTORY;
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private HashMap<String, ItemLocale> locales = new HashMap<String, ItemLocale>();

    public EscapeFromTools(String[] args) {
        // TODO: Handle command line arguments
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                search(searchBox.getText());
            }
        });
        foundItems.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                try {
                    itemSelected(itemEvent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void itemSelected(ItemEvent itemEvent) throws FileNotFoundException {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            ItemEntry item = (ItemEntry) itemEvent.getItem();
            File itemFile = new File(String.format(Constants.ITEMS_PATH, DB_DIRECTORY.getAbsolutePath(), item.getKey()));
            ItemData itemData = GSON.fromJson(new FileReader(itemFile), ItemData.class);
            itemDefinition.setText(GSON.toJson(itemData));
            itemDefinition.setCaretPosition(0);
        }
    }

    private void search(String query) {
        foundItems.removeAllItems();
        for(String key : locales.keySet()) {
            if(key.toLowerCase().contains(query.toLowerCase())) {
                foundItems.addItem(new ItemEntry(key));
            }
        }
        foundItems.setEnabled(true);
    }

    public static void main(String[] args) throws FileNotFoundException {
        EscapeFromTools tools = new EscapeFromTools(args);
        JFrame mainWindow = new JFrame(Constants.WINDOW_NAME);
        mainWindow.setContentPane(tools.escapeFromToolsPanel);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setPreferredSize(new Dimension(640, 480));
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);

        JFileChooser dbChooser = new JFileChooser();
        dbChooser.setDialogTitle(Constants.DIALOG_FILECHOOSER_DB);
        dbChooser.setCurrentDirectory(new File(".")); // TODO: REMOVE THIS BEFORE SHIPPING
        dbChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(dbChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            DB_DIRECTORY = dbChooser.getSelectedFile();
        }
        tools.loadLocales();
        tools.searchButton.setEnabled(true);
    }


    private void loadLocales() {
        // Repeat for every file in db/locales/en/templates
        ItemLocale locale;
        File localeFolder = new File(String.format(Constants.LOCALE_PATH, DB_DIRECTORY.getAbsolutePath()));
        File[] localeFiles = localeFolder.listFiles();
        assert localeFiles != null;
        for(File localeFile : localeFiles) {
            try {
                locale = GSON.fromJson(new FileReader(localeFile), ItemLocale.class);
                locale.setTemplateID(localeFile.getName());
                locales.put(String.format(Constants.LOCALE_KEY_STRING, locale.templateID, locale.Name), locale);
            } catch (FileNotFoundException e) {
                System.out.println(localeFile.getName()); // TODO: Log the item we failed on!?!
                e.printStackTrace();
            }
        }
    }
}
