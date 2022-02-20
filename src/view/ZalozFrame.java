package view;
import java.awt.*;
import java.awt.event.*;

/**
 * Formular pro zadani portu pro zalozeni hry
 * 
 * @author petrij2
 */
public class ZalozFrame extends Frame {
    public static Label lblPort=new Label("Zadejte port:");
    public static Button btnOK=new Button("OK");
    public static Button btnStorno=new Button("Storno");
    public static TextField txfPort=new TextField("1234");
    public ZalozFrame()
    {
        this.setLayout(null);
        this.setSize(200, 100);
        lblPort.setBounds(10, 30,75,25);
        this.add(lblPort);
        btnOK.setBounds(10,60,75,25);
        this.add(btnOK);
        btnStorno.setBounds(100, 60,75,25);        
        this.add(btnStorno);
        txfPort.setBounds(100, 30, 75,25);
        this.add(txfPort);
        this.setResizable(false);
        Dimension dimScreen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimScreen.width/2-this.getWidth()/2, dimScreen.height/2-this.getHeight()/2);        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            });
            
        btnOK.addActionListener(new controller.ZalozActionListener());
        btnStorno.addActionListener(new controller.ZalozActionListener());
        this.setTitle("Založ hru");
    }
}
