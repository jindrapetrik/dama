package view;
import java.awt.*;
import java.awt.event.*;
/**
 * Formular cekani na druheho hrace. (Po zalozeni hry)
 * 
 * @author petrij2
 */
public class CekaciFrame extends Frame implements ActionListener {
    static Label lblCekani=new Label("Čekám na druhého hráče...");       
    static Button btnStorno=new Button("Storno");
            
    public void actionPerformed(ActionEvent e) {
        model.ModelHry.konecHry();
        this.setResizable(false);
        this.setVisible(false);
        view.View.uvodniFrame.setVisible(true);
    }
    
    public CekaciFrame()
    {        
        this.setLayout(null);
        this.setSize(200, 100);       
        this.setResizable(false);
        lblCekani.setBounds(10, 30, 150, 25);
        btnStorno.setBounds(100,60,75,25);
        this.add(lblCekani);
        this.add(btnStorno);
        
        Dimension dimScreen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimScreen.width/2-this.getWidth()/2, dimScreen.height/2-this.getHeight()/2);        
        
        
        btnStorno.addActionListener(this);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {                
                /*model.ModelHry.konecHry();
                View.cekaciFrame.setVisible(false);
                view.View.uvodniFrame.setVisible(true);*/
                actionPerformed(null);
            }
            });
            
    }
}
