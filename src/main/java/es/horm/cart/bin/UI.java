package es.horm.cart.bin;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class UI extends JFrame {

    private Box contentBox;
    private JButton chooseFile;
    private JButton secondButton;

    private JComboBox<String> outputFieldCombo;

    private String[] categories = new String[0];
    private final JScrollPane scrollPane;

    public UI() {
        super();

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();




        contentBox = new Box(BoxLayout.Y_AXIS);

        chooseFile = new JButton("Choose File");
        chooseFile.addActionListener(actionEvent -> showFileSelector());
        contentBox.add(chooseFile);

        /*JLabel outputField = new JLabel("Output Field: ");
        contentBox.add(outputField);
        outputFieldCombo = new JComboBox<>(categories);
        contentBox.add(outputFieldCombo);*/

        scrollPane = new JScrollPane();

        for (int i = 0; i < 100; i++) {
            JCheckBox box = new JCheckBox("Test" + i);
            scrollPane.add(box);
//            contentBox.add(box);
        }

        contentBox.add(scrollPane);
        secondButton = new JButton("Temp");
        contentBox.add(secondButton);


        this.add(contentBox);
        this.setVisible(true);
        this.setSize(400, 400);
        this.setTitle("CART & Random Forests Example");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void showFileSelector() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(this);

        if(returnVal != JFileChooser.APPROVE_OPTION) return;

        try(BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
            String firstLine = reader.readLine();
            firstLine = firstLine.replace("\"", "");
            categories = firstLine.split(";");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s :
                categories) {
            System.out.println(s);
        }

        DefaultComboBoxModel<String> test = new DefaultComboBoxModel<>(categories);
        outputFieldCombo.setModel(test);
    }
}
