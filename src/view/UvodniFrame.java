package view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.BoxLayout;

/**
 * Uvodni formular hry
 * 
 * @author petrij2
 */
public class UvodniFrame extends Frame {
    
    public static Button btnZalozit=new Button("Založit síťovou hru");
    public static Button btnPripojit=new Button("Připojit se k síťové hře");
    public static Button btnSam=new Button("Hrát Proti PC");
    public static Button btnUkoncit=new Button("Ukončit");
    public static DamaCanvas canObrazek=new DamaCanvas();
    
    /** Obrazek s xichtikem damy */
    public static class DamaCanvas extends Canvas
    {
        @Override
        public void paint(Graphics g)
        {
            g.setColor(new Color(240,192,192));
            g.fillRect(0, 0, 500, 500);
            g.setColor(Color.WHITE);
            g.fillOval(75, 60, 120, 120);
            g.setColor(Color.BLACK);
            g.drawOval(75, 60, 120, 120);
            g.drawOval(78, 63, 114, 114);
            g.drawArc(95,75,80,80,0,-180);
            g.drawOval(110, 90, 10,10);
            g.drawOval(150, 90, 10,10);
            g.setFont(new Font(Font.SERIF,Font.BOLD,40));
            g.drawString("Dáma", 90, 40);
        }
    }
    
    public UvodniFrame()
    {
        this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        
        canObrazek.setSize(0, 200);
        this.add(canObrazek);
        this.add(btnSam);
        this.add(btnZalozit);
        this.add(btnPripojit);        
        this.add(btnUkoncit);
        this.setSize(300,325);
        
        Dimension dimScreen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimScreen.width/2-this.getWidth()/2, dimScreen.height/2-this.getHeight()/2);
        this.setResizable(false);
        this.setTitle("Dáma v1.1");
        btnZalozit.addActionListener(controller.UvodniActionListener.instance);
        btnPripojit.addActionListener(controller.UvodniActionListener.instance);
        btnSam.addActionListener(controller.UvodniActionListener.instance);
        btnUkoncit.addActionListener(controller.UvodniActionListener.instance);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            });        
    }
}
