package allen.gc.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import allen.gc.ControlData;
import allen.gc.TimePrecision;
import allen.gc.util.DateUtil;
import allen.gc.util.FileUtil;

/**
 * SelectPanel.
 * 
 * @author xinzhi.zhang
 * */
public class SelectPanel extends JPanel {

    private static final long serialVersionUID   = 1L;
    private static String     DateFormat         = "yyyy-MM-dd [HH[:mm[:ss]]]";
    public static SelectPanel selectPanel        = new SelectPanel();

    private JButton           jButton            = new JButton("open log");

    private JLabel            startDateLabel     = new JLabel("start");
    private JTextField        startDate          = new JTextField(DateFormat,
                                                         20);
    private JLabel            endDateLabel       = new JLabel("end");
    private JTextField        endDate            = new JTextField(DateFormat,
                                                         20);

    private JLabel            timePrecisionLabel = new JLabel("timePrecision");
    private JComboBox         timePrecision      = new JComboBox(
                                                         TimePrecision.values());

    private JButton           submit             = new JButton("submit");

    private SelectPanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        init();
        addListeners();
    }

    private void init() {
        this.add(jButton);
        this.add(timePrecisionLabel);
        this.add(timePrecision);
        timePrecision.setSelectedItem(ControlData.timePrecision);

        this.add(startDateLabel);
        this.add(startDate);
        this.add(endDateLabel);
        this.add(endDate);

        this.add(submit);
    }

    private void addListeners() {

        jButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileUtil.selectFileForRead(SelectPanel.this);
                ControlData.logFile = file;

                GCViewerFrame.gcViewer.repaintGCViewer();
            }
        });

        timePrecision.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                ControlData.timePrecision = (TimePrecision) (timePrecision
                        .getSelectedItem());
                GCViewerFrame.gcViewer.repaintGCViewer();
            }
        });

        submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String startDateStr = startDate.getText().trim();
                if (DateFormat.equals(startDateStr)) {
                    ControlData.startDate = null;
                } else {
                    ControlData.startDate = DateUtil.parseDate(startDateStr);
                }

                String endDateStr = endDate.getText().trim();
                if (DateFormat.equals(endDateStr)) {
                    ControlData.endDate = null;
                } else {
                    ControlData.endDate = DateUtil.parseDate(endDateStr);
                }

                GCViewerFrame.gcViewer.repaintGCViewer();
            }
        });

    }
}
