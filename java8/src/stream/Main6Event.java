package stream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 11-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main6Event {

    public static void main(String[] args) {
        JFrame jFrame = new ComboBoxFrame();
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private static class ComboBoxFrame extends JFrame {
        private static final String[] NAMES = {
                "bug1.gif", "bug2.gif", "travelbug.gif", "buganim.gif"
        };

        private final Icon[] ICONS;
        private final JComboBox<String> mComboBox;
        private final JLabel mLabel;

        public ComboBoxFrame() {
            super("Testing JComboBox");
            setLayout(new FlowLayout());

            ICONS = new Icon[NAMES.length];
            Path path = Paths.get("./data");
            for (int i = 0; i < ICONS.length; i++) {
                ICONS[i] = new ImageIcon(path.toString() + "\\" + NAMES[i]);
            }
            mComboBox = new JComboBox<String>(NAMES);
            mComboBox.setMaximumRowCount(3);
            add(mComboBox);
            mLabel = new JLabel(ICONS[0]);
            add(mLabel);

            mComboBox.addItemListener(item -> {
                if (item.getStateChange() == ItemEvent.SELECTED) {
                    mLabel.setIcon(ICONS[mComboBox.getSelectedIndex()]);
                }
                ;
            });


        }
    }
}
