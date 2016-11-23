package jerry.codetraining.ch1;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

/**
 * Created by jerryDev on 2016. 11. 22..
 */
public class SimpleTipCalculatorDriverGUI {

    private JPanel simpleTipCalculatorMainPanel;
    private JTextField billAmountTextField;
    private JTextArea outPutTextArea;
    private JSlider percentageSlider;
    private SimpleTipCalculator calculator;

    public SimpleTipCalculatorDriverGUI() {
        driverInit();
    }
    private void driverInit() {
        calculator = new SimpleTipCalculator();
        billAmountTextField.addActionListener((ActionEvent e)-> outPutTextArea.setText(calculator.inputBillAmount(Integer.parseInt(billAmountTextField.getText())).process().output()));
        percentageSlider.addChangeListener((ChangeEvent e)-> outPutTextArea.setText(calculator.inputPercentage(percentageSlider.getValue()).process().output()));

    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Simple Tip Calculator Driver GUI");
        frame.setContentPane(new SimpleTipCalculatorDriverGUI().simpleTipCalculatorMainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
