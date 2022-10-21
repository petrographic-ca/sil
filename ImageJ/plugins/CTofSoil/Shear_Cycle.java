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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Shear_Cycle extends PlugInFrame
    implements ActionListener, WindowListener {

  static String VERSION = "1.0.0";
  static String INIT_INPUT_FIELD = "(click on an open stack to select it)";

  private ImagePlus input;
  private boolean ui_enabled;

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

    jtf_input = new JTextField(INIT_INPUT_FIELD, 20);
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

    this.addWindowListener(this);
    this.enable_gui();

    this.pack();
    GUI.center(this);
    this.setVisible(true);
  }

  private synchronized boolean sync_ui_enabled(boolean set, boolean enabled) {
    if(set) {
      jtf_xy.setEnabled(enabled);
      jtf_yx.setEnabled(enabled);
      jtf_xz.setEnabled(enabled);
      jtf_zx.setEnabled(enabled);
      jtf_yz.setEnabled(enabled);
      jtf_zy.setEnabled(enabled);
      jb_ok.setEnabled(enabled);
      this.ui_enabled = enabled;
    }
    return this.ui_enabled;
  }

  public boolean is_enabled() {return sync_ui_enabled(false, false); }
  public void disable_gui() {sync_ui_enabled(true, false); }
  public void enable_gui() {sync_ui_enabled(true, true); }

  public class ShearCycleThread extends Thread {
    Shear_Cycle frame;
    ImagePlus input;
    double xy;
    double yx;
    double xz;
    double zx;
    double yz;
    double zy;

    public ShearCycleThread(
        Shear_Cycle frame, ImagePlus input,
        double xy, double yx, double xz, double zx, double yz, double zy) {
      this.frame = frame;
      this.input = input;
      this.xy = xy;
      this.yx = yx;
      this.xz = xz;
      this.zx = zx;
      this.yz = yz;
      this.zy = zy;
    }

    public void run() {
      IJ.log("entering shear cycle logic here");
      ImagePlus output = SilLibrary.makeShearCycle(
        this.input, this.xy, this.yx, this.xz, this.zx, this.yz, this.zy);
      IJ.log("exiting shear cycle logic here");

      // TODO: migrate any GUI methods back to GUI thread!
      output.show();
      this.frame.enable_gui();
    }
  }

  public static Double tryDouble(JTextField text_field) {
    Double val = null;
    String raw = text_field.getText();
    try {
        val = Double.parseDouble(raw);
    } catch (NumberFormatException ex) {
        IJ.log(
          "Error - need a double (floating point value), but got `" +raw +"`");
    }
    return val;
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == jb_ok && this.is_enabled()) {
      this.disable_gui();
      IJ.log("button pressed");
      if(this.input == null) {
        IJ.log("input disappeared -- nothing to do");
        this.enable_gui();
        return;
      }
      Thread thread = new ShearCycleThread(
        this,
        this.input,
        tryDouble(this.jtf_xy), tryDouble(this.jtf_yx),
        tryDouble(this.jtf_xz), tryDouble(this.jtf_zx),
        tryDouble(this.jtf_yz), tryDouble(this.jtf_zy));
      thread.start();  // TODO: make this safer -- .join() inside an executor
    }
  }

  public void windowActivated(WindowEvent e) {
    IJ.log("window focus");
    if(this.is_enabled()) {
      this.input = WindowManager.getCurrentImage();
      if(this.input == null) {
        this.jtf_input.setText(INIT_INPUT_FIELD);
      } else {
        this.jtf_input.setText(this.input.getTitle());
      }
    }
  }
}
