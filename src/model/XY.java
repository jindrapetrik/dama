package model;

/** Tato trida obaluje souradnice X a Y
 *
 * @author JPEXS
 */
public class XY
    {
        public int x;
        public int y;
        
        /** 
         * Konstruktor vytvari tridu (prekvapive)
         * @param x xova souradnice
         * @param y yova souradnice
         */
        public XY(int x,int y)
        {
            this.x=x;
            this.y=y;
        }
        
        /** 
         * kontroluje zda jsou shodne
         * @param xy souradnice
         * @return true pokud jsou shodne
         */
        public boolean equals(XY xy)
        {
            return (xy.x==x)&&(xy.y==y);
        }
        
        /** 
         * kontroluje zda jsou shodne
         * @param x xova souradnice
         * @param y yova souradnice
         * @return true pokud jsou shodne
         */
        public boolean equals(int x,int y)
        {
            return (this.x==x)&&(this.y==y);
        }
         
        
        /**
         * zjistuje, zda jsou soradnice obyvatelne (tmave pole)
         * @return true pokud je pole tmave
         */
        public boolean jeObyvatelne()
        {
            return ModelHry.jeObyvatelneXY(x,y);
        }
    }
