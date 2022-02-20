package view;
import java.awt.*;
import java.awt.event.*;

/**
 * Formular pro vyber adresy a portu pro pripojeni ke hre
 * 
 * @author petrij2
 */
public class PripojFrame extends Frame {
    public static Label lblAdresa=new Label("Adresa serveru:");
    public static Label lblPort=new Label("     Port:");
    public static Button btnOK=new Button("OK");
    public static Button btnStorno=new Button("Storno");
    public static TextField txfPort=new TextField("1234");
    public static TextField txfAdresa=new TextField("localhost");
    public PripojFrame()
    {
        this.setLayout(null);
        this.setSize(250, 130);
        lblAdresa.setBounds(10, 30,90,25);
        this.add(lblAdresa);
        txfAdresa.setBounds(100, 30, 120,25);
        this.add(txfAdresa);
        
        lblPort.setBounds(10, 60,75,25);
        this.add(lblPort);
        txfPort.setBounds(100, 60, 75,25);
        this.add(txfPort);
        
        
        btnOK.setBounds(10,90,75,25);
        this.add(btnOK);
        btnStorno.setBounds(100, 90,75,25);        
        this.add(btnStorno);
        
        this.setResizable(false);
        Dimension dimScreen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimScreen.width/2-this.getWidth()/2, dimScreen.height/2-this.getHeight()/2);        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            });
            
        btnOK.addActionListener(new controller.PripojActionListener());
        btnStorno.addActionListener(new controller.PripojActionListener());
    }
}
