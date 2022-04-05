/*
    +------
    | Grey Ramp - [ $version ]
    +------
    |
    |  width   [_ < int _]
    |  height  [_ < int _]
    |  depth   [_ < int _]
    |
    |  Generate_Ramp
    |
    +------
*/


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;

import ca.petrographic.SilLibrary;


public class Grey_Ramp extends PlugInFrame implements ActionListener {

    static String VERSION = "1.0.0";

    private ImagePlus imagePlus;

    Panel optionPanel;
    Panel executePanel;

    JTextField jtf_width;
    JTextField jtf_height;
    JTextField jtf_depth;

    Button jb_ok;

    public Grey_Ramp() {
        super("Grey Ramp - " + Grey_Ramp.VERSION);
        this.setLayout(new BorderLayout());

        optionPanel = new Panel();
        optionPanel.setLayout(new GridLayout(3, 2));

        optionPanel.add(new JLabel("Image Width :"));
        jtf_width = new JTextField("256", 4);
        optionPanel.add(jtf_width);
        optionPanel.add(new JLabel("Image Height :"));
        jtf_height = new JTextField("256", 4);
        optionPanel.add(jtf_height);
        optionPanel.add(new JLabel("Stack Depth :"));
        jtf_depth = new JTextField("256", 4);
        optionPanel.add(jtf_depth);

        this.add(optionPanel, BorderLayout.NORTH);

        executePanel = new Panel();
        executePanel.setLayout(new FlowLayout());
        jb_ok = new Button("Draw it!");
        jb_ok.addActionListener(this);
        executePanel.add(jb_ok);
        add(executePanel, BorderLayout.CENTER);

        this.pack();
        GUI.center(this);
        this.setVisible(true);
    }

    public Integer tryInt(JTextField text_field) {
        Integer val = null;
        try {
            val = Integer.parseInt(text_field.getText());
        } catch (NumberFormatException ex) {
            IJ.log("Error - need an integer, but got `"
                   +text_field.getText() +"`");
        }
        return val;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == jb_ok) {
            this.imagePlus = SilLibrary.makeGreyRamp(
                tryInt(jtf_width), tryInt(jtf_height), tryInt(jtf_depth));
            this.imagePlus.show();
        }
    }
}
