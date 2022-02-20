package view;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author petrij2
 */
public class NastaveniFrame extends Frame {
    static Label lblVelikost=new Label("Velikost hrací plochy:");
    static Label lblPocetRad=new Label("Počet řad na počátku:");
    public static Choice choVelikost=new Choice();
    public static Choice choPocetRad=new Choice();
    public static Checkbox chkLetajiciDamy=new Checkbox("Létající dámy");
    public static Checkbox chkPesakSkaceDozadu=new Checkbox("Pěšák skáče i dozadu");
    public static Button btnOK=new Button("OK");
    public static Button btnStorno=new Button("Storno");
    
    /** Zmeni seznam poctu rad v zavislosti na zvolene velikosti hraci plochy */
    public static void zmenitSeznam()
    {
        int index=choPocetRad.getSelectedIndex();
                choPocetRad.removeAll();
                for(int i=0;i<=choVelikost.getSelectedIndex()+1;i++)
                {
                 choPocetRad.add(""+(2+i));   
                }
                if(index<choPocetRad.getItemCount())
                    choPocetRad.select(index);
    }
    
    /** zajisteni zmeny seznamu poctu rad */
    class VelikostItemListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if(e.getSource()==choVelikost)
            {                
                zmenitSeznam();
            }
        }
    }
    
    /** zobrazi okno s nastavenim */
    public void zobrazit()
    {
        this.setVisible(true);
        choVelikost.select((model.Nastaveni.velikostDesky-8)/2);       
        zmenitSeznam();
        choPocetRad.select(model.Nastaveni.pocetRadNaStartu-2);
        chkLetajiciDamy.setState(model.Nastaveni.letajiciDamy);
        chkPesakSkaceDozadu.setState(model.Nastaveni.pesakSkaceDozadu);
        
    }
    
    public NastaveniFrame()
    {
        this.setLayout(null);
        lblVelikost.setBounds(10,30,120,25);
        choVelikost.setBounds(135, 32, 75, 25);
        choVelikost.add("8x8");
        choVelikost.add("10x10");
        choVelikost.add("12x12");
        choVelikost.addItemListener(new VelikostItemListener());
        this.add(lblVelikost);
        this.add(choVelikost);
        
        lblPocetRad.setBounds(10,60,120,25);
        choPocetRad.setBounds(135, 62, 75, 25);
        choPocetRad.add("2");
        choPocetRad.add("3");
        choPocetRad.select(1);
        this.add(lblPocetRad);
        this.add(choPocetRad);
        
        chkPesakSkaceDozadu.setBounds(50, 95, 150, 25);
        chkLetajiciDamy.setBounds(50, 125, 150, 25);
        chkLetajiciDamy.setState(true);
                
        this.add(chkLetajiciDamy);
        this.add(chkPesakSkaceDozadu);
        
        
        btnOK.setBounds(30,160,75,25);
        this.add(btnOK);
        btnStorno.setBounds(120, 160,75,25);        
        this.add(btnStorno);
        
        
        this.setSize(220, 200);
        this.setResizable(false);
        this.setTitle("Nastavení hry");
        
        Dimension dimScreen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimScreen.width/2-this.getWidth()/2, dimScreen.height/2-this.getHeight()/2);        
        
        
        btnOK.addActionListener(new controller.NastaveniActionListener());
        btnStorno.addActionListener(new controller.NastaveniActionListener());
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                View.nastaveniFrame.setVisible(false);
                View.uvodniFrame.setVisible(true);
            }
            });
    }
}
