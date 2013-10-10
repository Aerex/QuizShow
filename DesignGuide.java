import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Sat Nov 17 14:51:02 EST 2012
 */



/**
 * @author Something Something
 */
public class DesignGuide extends JFrame {
    public DesignGuide() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Something Something
        frame1 = new JFrame();
        panel2 = new JPanel();
        panel3 = new JPanel();
        layeredPane1 = new JLayeredPane();
        spinner1 = new JSpinner();

        //======== frame1 ========
        {
            Container frame1ContentPane = frame1.getContentPane();
            frame1ContentPane.setLayout(new BorderLayout());
            frame1.pack();
            frame1.setLocationRelativeTo(frame1.getOwner());
        }

        //======== panel2 ========
        {

            // JFormDesigner evaluation mark
            panel2.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    java.awt.Color.red), panel2.getBorder())); panel2.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

            panel2.setLayout(null);

            //======== panel3 ========
            {
                panel3.setBackground(new Color(204, 0, 0));
                panel3.setBorder(LineBorder.createBlackLineBorder());
                panel3.setLayout(null);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel3.getComponentCount(); i++) {
                        Rectangle bounds = panel3.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel3.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel3.setMinimumSize(preferredSize);
                    panel3.setPreferredSize(preferredSize);
                }
            }
            panel2.add(panel3);
            panel3.setBounds(new Rectangle(new Point(615, 135), panel3.getPreferredSize()));
            panel2.add(layeredPane1);
            layeredPane1.setBounds(new Rectangle(new Point(195, 160), layeredPane1.getPreferredSize()));

            panel2.setPreferredSize(new Dimension(779, 474));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Something Something
    private JFrame frame1;
    private JPanel panel2;
    private JPanel panel3;
    private JLayeredPane layeredPane1;
    private JSpinner spinner1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
