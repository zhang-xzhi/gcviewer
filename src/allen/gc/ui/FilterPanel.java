package allen.gc.ui;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import allen.gc.ControlData;
import allen.gc.GCDataType;

/**
 * FilterPanel.
 * 
 * @author xinzhi.zhang
 * */
public class FilterPanel extends JPanel {

    private static final long   serialVersionUID  = 1L;

    private static List<Object> gcDataSubTypeList = GCDataType
                                                          .getGCDataSubTypeList();
    public static FilterPanel   filterPanel       = new FilterPanel();

    private FilterPanel() {

        this.setLayout(new GridLayout(gcDataSubTypeList.size(), 1));

        for (Object obj : gcDataSubTypeList) {

            FilterJCheckBox checkBox = new FilterJCheckBox(obj);

            checkBox.setSelected(ControlData.filterObjects.contains(obj));

            this.add(checkBox);

            checkBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    FilterJCheckBox checkBox = (FilterJCheckBox) (e
                            .getItemSelectable());
                    if (checkBox.isSelected()) {
                        ControlData.filterObjects.add(checkBox.filterObj);
                    } else {
                        ControlData.filterObjects.remove(checkBox.filterObj);
                    }

                    GCViewerFrame.gcViewer.repaintGCViewer();
                }
            });
        }
    }

    private static class FilterJCheckBox extends JCheckBox {
        private static final long serialVersionUID = 1L;
        Object                    filterObj;

        private FilterJCheckBox(Object filterObj) {
            super(filterObj.toString());
            this.filterObj = filterObj;
        }
    }
}
