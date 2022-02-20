package model;


/**
 * Trida pro ulozeni tahu
 * @author petrij2
 */
public class Tah
    {
        /** Z ktereho pole hrac tahne */
        XY odkud;
        /** Na ktere pole hrac tahne */
        XY kam;
        /** Priznaky tahu (napr. zda se skace)*/
        int priznaky;
        
        /** Priznaky skakani */
        public static final int SKAKANI=1;
        /** Priznaky skakani damou */
        public static final int SKAKANI_DAMOU=2;
                
        /**
         * Konstruktor tahu
         * @param odkud
         * @param kam
         */
        public Tah(XY odkud,XY kam)
        {
            this.odkud=odkud;
            this.kam=kam;
            this.priznaky=0;
        }  
        
        /**
         * Konstruktor tahu
         * @param odkud
         * @param kam
         * @param priznaky
         */
        public Tah(XY odkud,XY kam,int priznaky)
        {
            this.odkud=odkud;
            this.kam=kam;
            this.priznaky=priznaky;
        }
        
    @Override
        public String toString()
        {
            return odkud.toString()+" --> "+kam.toString();
        }
    }
