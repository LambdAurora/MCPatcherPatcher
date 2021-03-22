/*
 * Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lambdaurora.mcpatcherpatcher;

import me.lambdaurora.mcpatcherpatcher.image.BufferedImageProvider;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCPatcherPatcherApplication extends JFrame
{
    private static final MCPatcherPatcher patcherInterface = new MCPatcherPatcher(new BufferedImageProvider());

    private JLabel inputLabel;
    private JLabel outputLabel;
    private JScrollPane consoleLogScrollPane;
    private JTextArea consoleLog;
    private JTextField inputTextField;
    private JTextField outputTextField;
    private JButton browseInput;
    private JButton browseOutput;
    private JButton convert;
    private JFileChooser fileChooser;

    private File inputDirectory;
    private File outputDirectory;

    public MCPatcherPatcherApplication()
    {
        this.setPreferredSize(new Dimension(550, 400));
        this.initComponents();
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);
        this.setupComponents();
        this.loadComponents();
        this.setTitle("MCPatcherPatcher");
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void initComponents()
    {
        this.inputLabel = new JLabel();
        this.outputLabel = new JLabel();
        this.consoleLog = new JTextArea();
        this.inputTextField = new JTextField();
        this.outputTextField = new JTextField();
        this.browseInput = new JButton();
        this.browseOutput = new JButton();
        this.convert = new JButton();
        this.fileChooser = new JFileChooser();
        this.inputDirectory = new File(System.getProperty("user.dir") + File.separator + "input");
        this.outputDirectory = new File(System.getProperty("user.dir") + File.separator + "output");
        this.createDirectoryIfNotExist(this.inputDirectory);
        this.createDirectoryIfNotExist(this.outputDirectory);
        PrintStream printStream = new PrintStream(new CustomOutputStream(this.consoleLog));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void setupComponents()
    {
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font consoleLogFont = new Font("Consolas", Font.PLAIN, 12);

        Dim2i inputLabelDim = new Dim2i(10, 5, 50, 30);
        this.inputLabel.setBounds(inputLabelDim.getOriginX(), inputLabelDim.getOriginY(), inputLabelDim.getWidth(), inputLabelDim.getHeight());
        this.inputLabel.setFont(labelFont);
        this.inputLabel.setText("Input");

        Dim2i inputTextFieldDim = new Dim2i(inputLabelDim.getLimitX() + 5, inputLabelDim.getOriginY(), (int) this.getPreferredSize().getWidth() - 120, inputLabelDim.getHeight());
        this.inputTextField.setBounds(inputTextFieldDim.getOriginX(), inputTextFieldDim.getOriginY(), inputTextFieldDim.getWidth(), inputTextFieldDim.getHeight());
        this.inputTextField.setEditable(false);
        this.inputTextField.setText(this.inputDirectory.getAbsolutePath());

        Dim2i browseInputDim = new Dim2i(inputTextFieldDim.getLimitX() + 5, inputLabelDim.getOriginY(), inputLabelDim.getHeight(), inputLabelDim.getHeight());
        this.browseInput.setBounds(browseInputDim.getOriginX(), browseInputDim.getOriginY(), browseInputDim.getWidth(), browseInputDim.getHeight());
        this.browseInput.setText("...");

        Dim2i outputLabelDim = new Dim2i(inputLabelDim.getOriginX(), browseInputDim.getLimitY() + 5, 50, inputLabelDim.getHeight());
        this.outputLabel.setBounds(outputLabelDim.getOriginX(), outputLabelDim.getOriginY(), outputLabelDim.getWidth(), outputLabelDim.getHeight());
        this.outputLabel.setFont(labelFont);
        this.outputLabel.setText("Output");

        Dim2i outputTextFieldDim = new Dim2i(outputLabelDim.getLimitX() + 5, outputLabelDim.getOriginY(), (int) this.getPreferredSize().getWidth() - 120, outputLabelDim.getHeight());
        this.outputTextField.setBounds(outputTextFieldDim.getOriginX(), outputTextFieldDim.getOriginY(), outputTextFieldDim.getWidth(), outputTextFieldDim.getHeight());
        this.outputTextField.setEditable(false);
        this.outputTextField.setText(this.outputDirectory.getAbsolutePath());

        Dim2i browseOutputDim = new Dim2i(outputTextFieldDim.getLimitX() + 5, outputLabelDim.getOriginY(), outputLabelDim.getHeight(), outputLabelDim.getHeight());
        this.browseOutput.setBounds(browseOutputDim.getOriginX(), browseOutputDim.getOriginY(), browseOutputDim.getWidth(), browseOutputDim.getHeight());
        this.browseOutput.setText("...");

        Dim2i consoleLogDim = new Dim2i(10, browseOutputDim.getLimitY() + 10, (int) this.getPreferredSize().getWidth() - 30,
                (int) this.getPreferredSize().getHeight() - browseOutputDim.getLimitY() - 85);
        this.consoleLog.setBounds(consoleLogDim.getOriginX(), consoleLogDim.getOriginY(), consoleLogDim.getWidth(), consoleLogDim.getHeight());
        this.consoleLog.setFont(consoleLogFont);
        this.consoleLog.setEditable(false);
        this.consoleLogScrollPane = new JScrollPane(consoleLog);
        this.consoleLogScrollPane.setBounds(consoleLogDim.getOriginX(), consoleLogDim.getOriginY(), consoleLogDim.getWidth(), consoleLogDim.getHeight());
        this.consoleLogScrollPane.setBorder(new LineBorder(Color.GRAY));

        Dim2i convertDim = new Dim2i(10, consoleLogDim.getLimitY() + 5, (int) this.getPreferredSize().getWidth() - 30, 30);
        this.convert.setBounds(convertDim.getOriginX(), convertDim.getOriginY(), convertDim.getWidth(), convertDim.getHeight());
        this.convert.setText("Convert");

        this.browseInput.addActionListener(e -> {
            if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.inputDirectory = this.fileChooser.getSelectedFile();
                this.inputTextField.setText(this.inputDirectory.getAbsolutePath());
                System.out.printf("Input Selected: %s%n", this.inputDirectory.getAbsolutePath());
            }
        });

        this.browseOutput.addActionListener(e -> {
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.outputDirectory = this.fileChooser.getSelectedFile();
                this.outputLabel.setText(this.outputDirectory.getAbsolutePath());
                System.out.printf("Output Selected: %s%n", this.outputDirectory.getAbsolutePath());
            }
        });

        this.convert.addActionListener(e -> {
            if (this.inputTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Input Directory not selected!");
                return;
            }
            if (this.outputTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Output Directory not selected!");
                return;
            }
            if (this.inputTextField.getText().equals(this.outputTextField.getText())) {
                JOptionPane.showMessageDialog(this, "Output Directory can not be the same as Input Directory!");
            }

            List<File> validResourcePacks = new ArrayList<>();
            // Does not fully verify if zip is valid resource pack
            Arrays.stream(this.inputDirectory.listFiles()).filter(file -> file.isFile() && file.getName().endsWith(".zip")).forEach(validResourcePacks::add);

            new Thread(() -> {
                validResourcePacks.forEach(resourcePack -> {
                    System.out.println("Converting " + resourcePack.getName());
                    try {
                        patcherInterface.convert(resourcePack, new File(this.outputDirectory.getAbsolutePath() + File.separator + resourcePack.getName()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    System.out.println("Converted " + resourcePack.getName());
                });
            }).start();

            //Yeet, FS gets mad if I don't
            validResourcePacks.clear();
        });
    }

    private void loadComponents()
    {
        this.add(this.inputLabel);
        this.add(this.outputLabel);
        this.add(this.consoleLogScrollPane);
        this.add(this.inputTextField);
        this.add(this.outputTextField);
        this.add(this.browseInput);
        this.add(this.browseOutput);
        this.add(this.convert);
    }

    public static void main(String[] args) throws IOException
    {
        if (Arrays.asList(args).isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                MCPatcherPatcherApplication mcPatcherPatcher = new MCPatcherPatcherApplication();
                mcPatcherPatcher.setVisible(true);
            });
        } else {
            String singleRuntimeOptionLine = String.join(" ", args);
            String[] options = singleRuntimeOptionLine.split("--");
            if (options.length == 0) {
                System.out.println("No");
            } else {
                File inputFile = null;
                File outputFile = null;

                for (String option : options) {
                    option = option.trim();
                    if (option.toLowerCase().startsWith("input") && !option.equalsIgnoreCase("input") && !option.equalsIgnoreCase("input ")) {
                        inputFile = new File(option.substring("input ".length()));
                    } else if (option.toLowerCase().startsWith("output") && !option.equalsIgnoreCase("output") && !option.equalsIgnoreCase("output ")) {
                        outputFile = new File(option.substring("output ".length()));
                    }
                }
                if (inputFile == null) {
                    System.out.println("Missing Input File Path!");
                    return;
                }
                if (outputFile == null) {
                    System.out.println("Missing Output File Path!");
                    return;
                }
                if (!inputFile.getAbsolutePath().endsWith(".zip")) {
                    System.out.println("Invalid Input File!");
                    return;
                }
                if (!outputFile.getAbsolutePath().endsWith(".zip")) {
                    System.out.println("Invalid Output File!");
                    return;
                }

                patcherInterface.convert(inputFile, outputFile);
                System.out.printf("Output File: %s%n", outputFile.getAbsolutePath());
            }
        }
    }

    private boolean createDirectoryIfNotExist(File file) {
        if (!file.exists())
            return file.mkdirs();
        else
            return file.isDirectory();
    }

    private static class CustomOutputStream extends OutputStream
    {
        private final JTextArea textArea;

        public CustomOutputStream(JTextArea textArea)
        {
            this.textArea = textArea;
        }

        @Override
        public void write(int b)
        {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    private static class Dim2i
    {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        public Dim2i(int x, int y, int width, int height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getOriginX()
        {
            return this.x;
        }

        public int getOriginY()
        {
            return this.y;
        }

        public int getWidth()
        {
            return this.width;
        }

        public int getHeight()
        {
            return this.height;
        }

        public int getLimitX()
        {
            return this.x + this.width;
        }

        public int getLimitY()
        {
            return this.y + this.height;
        }
    }
}
