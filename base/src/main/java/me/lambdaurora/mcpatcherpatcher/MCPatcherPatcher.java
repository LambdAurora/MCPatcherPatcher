/*
 *  Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lambdaurora.mcpatcherpatcher;

import org.apache.commons.lang3.Validate;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.Arrays;

public class MCPatcherPatcher extends JFrame
{
    private JLabel       inputLabel;
    private JLabel       outputLabel;
    private JScrollPane  consoleLogScrollPane;
    private JTextArea    consoleLog;
    private JTextField   inputTextField;
    private JTextField   outputTextField;
    private JButton      browseInput;
    private JButton      browseOutput;
    private JButton      convert;
    private JFileChooser fileChooser;

    public MCPatcherPatcher()
    {
        this.setPreferredSize(new Dimension(800, 400));
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
        PrintStream printStream = new PrintStream(new CustomOutputStream(this.consoleLog));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void setupComponents()
    {
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
        this.fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIP (Compressed File Format)", "zip"));

        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font consoleLogFont = new Font("Consolas", Font.PLAIN, 14);

        Dim2i inputLabelDim = new Dim2i(10, 5, (int) this.getPreferredSize().getWidth() - 20, 30);
        this.inputLabel.setBounds(inputLabelDim.getOriginX(), inputLabelDim.getOriginY(), inputLabelDim.getWidth(), inputLabelDim.getHeight());
        this.inputLabel.setFont(labelFont);
        this.inputLabel.setText("Input File");

        Dim2i inputTextFieldDim = new Dim2i(10, inputLabelDim.getLimitY() + 5, (int) this.getPreferredSize().getWidth() - 60, 30);
        this.inputTextField.setBounds(inputTextFieldDim.getOriginX(), inputTextFieldDim.getOriginY(), inputTextFieldDim.getWidth(), inputTextFieldDim.getHeight());
        this.inputTextField.setEditable(false);

        Dim2i browseInputDim = new Dim2i(inputTextFieldDim.getLimitX() + 5, inputLabelDim.getLimitY() + 5, 30, 30);
        this.browseInput.setBounds(browseInputDim.getOriginX(), browseInputDim.getOriginY(), browseInputDim.getWidth(), browseInputDim.getHeight());
        this.browseInput.setText("...");

        Dim2i outputLabelDim = new Dim2i(10, browseInputDim.getLimitY() + 5, (int) this.getPreferredSize().getWidth() - 20, 30);
        this.outputLabel.setBounds(outputLabelDim.getOriginX(), outputLabelDim.getOriginY(), outputLabelDim.getWidth(), outputLabelDim.getHeight());
        this.outputLabel.setFont(labelFont);
        this.outputLabel.setText("Output File");

        Dim2i outputTextFieldDim = new Dim2i(10, outputLabelDim.getLimitY() + 5, (int) this.getPreferredSize().getWidth() - 60, 30);
        this.outputTextField.setBounds(outputTextFieldDim.getOriginX(), outputTextFieldDim.getOriginY(), outputTextFieldDim.getWidth(), outputTextFieldDim.getHeight());
        this.outputTextField.setEditable(false);

        Dim2i browseOutputDim = new Dim2i(outputTextFieldDim.getLimitX() + 5, outputLabelDim.getLimitY() + 5, 30, 30);
        this.browseOutput.setBounds(browseOutputDim.getOriginX(), browseOutputDim.getOriginY(), browseOutputDim.getWidth(), browseOutputDim.getHeight());
        this.browseOutput.setText("...");

        Dim2i consoleLogDim = new Dim2i(10, browseOutputDim.getLimitY() + 10, (int) this.getPreferredSize().getWidth() - 25,
                (int) this.getPreferredSize().getHeight() - browseOutputDim.getLimitY() - 85);
        this.consoleLog.setBounds(consoleLogDim.getOriginX(), consoleLogDim.getOriginY(), consoleLogDim.getWidth(), consoleLogDim.getHeight());
        this.consoleLog.setFont(consoleLogFont);
        this.consoleLog.setEditable(false);
        this.consoleLogScrollPane = new JScrollPane(consoleLog);
        this.consoleLogScrollPane.setBounds(consoleLogDim.getOriginX(), consoleLogDim.getOriginY(), consoleLogDim.getWidth(), consoleLogDim.getHeight());
        this.consoleLogScrollPane.setBorder(new LineBorder(Color.GRAY));

        Dim2i convertDim = new Dim2i(10, consoleLogDim.getLimitY() + 5, (int) this.getPreferredSize().getWidth() - 25, 30);
        this.convert.setBounds(convertDim.getOriginX(), convertDim.getOriginY(), convertDim.getWidth(), convertDim.getHeight());
        this.convert.setText("Convert");

        this.browseInput.addActionListener(e -> {
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String selected = this.fileChooser.getSelectedFile().getAbsolutePath();
                this.inputTextField.setText(selected);
                System.out.printf("Input Selected: %s%n", selected);
            }
        });

        this.browseOutput.addActionListener(e -> {
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            if (this.fileChooser.getSelectedFile() != null)
                this.fileChooser.setSelectedFile(new File(this.fileChooser.getSelectedFile(), String.format("[Patched] %s", this.fileChooser.getSelectedFile().getName())));
            if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String selected = this.fileChooser.getSelectedFile().getAbsolutePath();
                this.outputTextField.setText(selected);
                System.out.printf("Output Selected: %s%n", selected);
            }
        });

        this.convert.addActionListener(e -> {
            if (this.inputTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Input File not selected!");
                return;
            }
            if (this.outputTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Output File not selected!");
                return;
            }
            if (this.inputTextField.getText().equals(this.outputTextField.getText())) {
                JOptionPane.showMessageDialog(this, "Output File can not be the same as Input File!");
            }

            MCPatcherPatcherInterface patcherInterface = new MCPatcherPatcherInterface();
            try {
                patcherInterface.convert(new File(this.inputTextField.getText()), new File(this.outputTextField.getText()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.printf("Output File: %s%n", this.outputTextField.getText());
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

    private class Dim2i
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

    private class CustomOutputStream extends OutputStream
    {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea)
        {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException
        {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
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
                MCPatcherPatcher mcPatcherPatcher = new MCPatcherPatcher();
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
                    if (option.toLowerCase().startsWith("input")) {
                        inputFile = new File(option.substring("input ".length()));
                    } else if (option.toLowerCase().startsWith("output")) {
                        outputFile = new File(option.substring("output ".length()));
                    }
                }
                Validate.notNull(inputFile, "Missing Input File Path!");
                Validate.notNull(outputFile, "Missing Output File Path!");
                Validate.isTrue(inputFile.getPath().endsWith(".zip"), "Invalid Input File!");
                Validate.isTrue(outputFile.getPath().endsWith(".zip"), "Invalid Output File!");

                MCPatcherPatcherInterface patcherInterface = new MCPatcherPatcherInterface();
                patcherInterface.convert(inputFile, outputFile);
                System.out.printf("Output File: %s%n", outputFile.getAbsolutePath());
            }
        }
    }
}
