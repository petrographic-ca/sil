/*
    +------
    | Shear Cycle - [ $version ]
    +------
    |
    |  input image stack   [ $most_recent_window ]
    |
    |  shear-xy   [_ < double _]   shear-xy   [_ < double _]
    |  shear-xz   [_ < double _]   shear-zx   [_ < double _]
    |  shear-yz   [_ < double _]   shear-zy   [_ < double _]
    |
    |  Run
    |
    +------
*/

import ca.petrographic.SilLibrary;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Shear_Cycle extends PlugInFrame implements ActionListener {

  static String VERSION = "1.0.0";

  private ImagePlus input;
  private ImagePlus output;

  Panel optionPanel;
  Panel executePanel;

  JTextField jtf_input;
  JTextField jtf_xy;
  JTextField jtf_yx;
  JTextField jtf_xz;
  JTextField jtf_zx;
  JTextField jtf_yz;
  JTextField jtf_zy;

  Button jb_ok;

  public Shear_Cycle() {
    super("Shear Cycle - " + Shear_Cycle.VERSION);
    this.setLayout(new BorderLayout());

    jtf_input = new JTextField("(open stack before opening this plugin)", 20);
    jtf_input.setEnabled(false);
    this.add(jtf_input, BorderLayout.NORTH);

    optionPanel = new Panel();
    optionPanel.setLayout(new GridLayout(3, 4));

    jtf_xy = new JTextField("0", 6);
    jtf_yx = new JTextField("0", 6);
    jtf_xz = new JTextField("0", 6);
    jtf_zx = new JTextField("0", 6);
    jtf_yz = new JTextField("0", 6);
    jtf_zy = new JTextField("0", 6);

    optionPanel.add(new JLabel("shear-xy"));
    optionPanel.add(jtf_xy);
    optionPanel.add(new JLabel("shear-yx"));
    optionPanel.add(jtf_yx);
    optionPanel.add(new JLabel("shear-xz"));
    optionPanel.add(jtf_xz);
    optionPanel.add(new JLabel("shear-zx"));
    optionPanel.add(jtf_zx);
    optionPanel.add(new JLabel("shear-yz"));
    optionPanel.add(jtf_yz);
    optionPanel.add(new JLabel("shear-zy"));
    optionPanel.add(jtf_zy);

    this.add(optionPanel, BorderLayout.CENTER);

    executePanel = new Panel();
    executePanel.setLayout(new FlowLayout());
    jb_ok = new Button("Run it!");
    jb_ok.addActionListener(this);
    executePanel.add(jb_ok);
    this.add(executePanel, BorderLayout.SOUTH);

    this.pack();
    GUI.center(this);
    this.setVisible(true);

    input = WindowManager.getCurrentImage();
    if(input == null) {
      this.disable_gui();
    } else {
      jtf_input.setText(input.getTitle());
    }
  }

  public void disable_gui() {
    jtf_xy.setEnabled(false);
    jtf_yx.setEnabled(false);
    jtf_xz.setEnabled(false);
    jtf_zx.setEnabled(false);
    jtf_yz.setEnabled(false);
    jtf_zy.setEnabled(false);
    jb_ok.setEnabled(false);
  }

  public Double tryDouble(JTextField text_field) {
        Double val = null;
        String raw = text_field.getText();
        try {
            val = Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            IJ.log("Error - need a double (floating point value), but got `"
                   +raw +"`");
        }
        return val;
    }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == jb_ok) {
      this.output = SilLibrary.makeShearCycle(
        this.input,
        tryDouble(this.jtf_xy), tryDouble(this.jtf_yx),
        tryDouble(this.jtf_xz), tryDouble(this.jtf_zx),
        tryDouble(this.jtf_yz), tryDouble(this.jtf_zy));
      this.output.show();
    }
  }
}
