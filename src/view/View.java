package view;
import java.awt.Color;
import model.*;
/**
 * Cast View pro zobrazovani dat
 *
 * @author petrij2
 */
public class View {
    
    public static HlavniFrame hlavniFrame;
    public static UvodniFrame uvodniFrame=new UvodniFrame();
    public static ZalozFrame zalozFrame=new ZalozFrame();
    public static PripojFrame pripojFrame=new PripojFrame();
    public static NastaveniFrame nastaveniFrame=new NastaveniFrame();
    public static CekaciFrame cekaciFrame=new CekaciFrame();
    
    public static final int X_DESKY=8;
    public static final int Y_DESKY=40;
    
    
    public static final int SIRKA_POLICKA=40;
    public static final int VYSKA_POLICKA=40;    
    public static final int PRUMER_KAMENU=30;
    public static final int PRUMER_KRUHU_DAMY=20;
    static Color BARVA_SVETLA=Color.WHITE;
    public static Color BARVA_TMAVA=new Color(130,52,0);
    static Color BARVA_OKRAJE=Color.BLACK;
    public static Color BARVY_HRACU[]={Color.WHITE,Color.GRAY};
    static Color BARVA_VYBRANE=new Color(0,0,192);
    static Color BARVA_VYBRANE_CIZI=new Color(192,0,0);
    static Color BARVA_KRUZNICE=Color.BLACK;
    static Color BARVA_OZNACENEHO=new Color(64,64,255);
    static Color BARVA_OZNACENEHO_CIZIHO=new Color(255,64,64);
    public static String jmenaHracu[]={"bílý","černý"};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Nastaveni.nacist();
        uvodniFrame.setVisible(true);
    }

}
