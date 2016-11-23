package jerry.codetraining.ch1;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

/**
 * Created by jerryDev on 2016. 11. 22..
 */
class SimpleTipCalculatorDriverGUI implements ActionListener,ChangeListener {
    private JPanel simpleTipCalculatorMainPanel;
    private JTextField billAmountTextField;
    private JTextArea outPutTextArea;
    private JSlider percentageSlider;
    private SimpleTipCalculator calculator;

    private SimpleTipCalculatorDriverGUI()  {
        driverInit();
    }
    private void driverInit() {
        Supplier<SimpleTipCalculator> simpleTipCalculatorSupplier  = SimpleTipCalculator::new;
        calculator = simpleTipCalculatorSupplier.get();
        billAmountTextField.addActionListener(this::actionPerformed);
        percentageSlider.addChangeListener(this::stateChanged);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
            try {
                outPutTextArea.setText(calculator.inputBillAmount(Integer.parseInt(billAmountTextField.getText())).process().toString());
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null,e1);
            }
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        try {
            outPutTextArea.setText(calculator.inputPercentage(percentageSlider.getValue()).process().toString());
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null,e1);
        }
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Simple Tip Calculator Driver GUI");
        frame.setContentPane(new SimpleTipCalculatorDriverGUI().simpleTipCalculatorMainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
