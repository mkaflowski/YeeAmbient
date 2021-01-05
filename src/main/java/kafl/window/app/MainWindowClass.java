package kafl.window.app;

import service.YeeService;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class MainWindowClass {
    static YeeService yeeService = new YeeService("");
    private static SourcePane dataPene;
    public static final String IP_PREF = "ip_pref";
    private static Preferences prefs;

    public static void main(String[] args) {
        JFrame f = new JFrame();//creating instance of JFrame
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        prefs = Preferences.userNodeForPackage(MainWindowClass.class);

        Container contentPane = f.getContentPane();
        contentPane.setLayout(new GridBagLayout());


        contentPane.setDropTarget(new DropTarget());

        dataPene = new SourcePane();
        f.add(dataPene);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;


        JButton button;
        f.add(button = new JButton("START"), gbc);
        button.setBackground(Color.green);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProperties();

                button.setText("STOP");

                if (!yeeService.getIp().equals(dataPene.ipField.getText())) {
                    yeeService.stop();
                    yeeService = new YeeService(dataPene.ipField.getText());
                }

                if(!yeeService.isWorking()){
                    button.setText("STOP");
                    yeeService.start();
                } else{
                    button.setText("START");
                    yeeService.stop();
                }

            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;


        f.pack();
        f.setTitle("YeeAmbient (by Mateusz Kaflowski)");
//        f.setSize(500, 760);//400 width and 500 height
//        f.setLayout(null);//using no layout managers
        centreWindow(f);
        f.setVisible(true);//making the frame visible
    }

    private static void saveProperties() {
        prefs.put(IP_PREF, dataPene.ipField.getText());
    }

    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static class SourcePane extends JPanel {
        private JTextField ipField;
//        private JTextField gielda;
//        private JTextField rok;
//        private JTextField prowizja;

        public SourcePane() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;

            add(new JLabel("LED IP: "), gbc);
//            gbc.gridy++;
//            add(new JLabel("Giełda [GPW,NASDAQ] (puste = wszystkie): "), gbc);
//            gbc.gridy++;
//            add(new JLabel("Rok (puste = wszystkie): "), gbc);
//            gbc.gridy++;
//            add(new JLabel("Prowizja: "), gbc);
//
            gbc.gridx++;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
//
            add((ipField = new JTextField(30)), gbc);
//            gbc.gridy++;
//            add((gielda = new JTextField(10)), gbc);
//            gbc.gridy++;
//            add((rok = new JTextField(10)), gbc);
//            gbc.gridy++;
//            add((prowizja = new JTextField(10)), gbc);
//            prowizja.setText("0.39");

            ipField.setText(prefs.get(IP_PREF, "192.168.0.40"));
        }

    }
}
