import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SetCardGame {
	static int SIZE=12; //9 or 12
	final static boolean plus3=false; //if true, SIZE must be 9
	final static float MISTAKE = 0.00f; //percentage of deals where player misses 1 set
	
	static List deck;

	static int card1;
	static int card2;
	static int card3;
	static int numCards = 0;
    static int winnings=0;
	static Random r = new Random(System.currentTimeMillis());
	static List theDeal;

	static void initDeck()
	{
		deck = new ArrayList();
	    int i=0;
	    for (i=80;i>=0;i--)
	    {
	        deck.add(i);
	    }
	}

	static int dealCard()
	{
		
		int i = r.nextInt(deck.size());
	    int card = (int) deck.remove(i);
	   
	    return card;
	}

	static boolean isSetNum(int a, int b, int c)
	{
	    if ((a==b) && (b==c))
	    {
	        return true;
	    }
	    
	    if ((a!=b) && (b!=c) && (a!=c))
	    {
	        return true;
	    }
	    
	    return false;
	    
	}

	static int findThirdNum(int a, int b)
	{
	    if (a==b) return a;
	    
	    if ((0!=a) && (0!=b)) return 0;
	    
	    if ((1!=a) && (1!=b)) return 1;
	    
	    return 2;
	    
	}
	
	static int findThirdCard(int c1, int c2)
	{
	    List l1 = convertToAttributes(c1);
	    List l2 = convertToAttributes(c2);
	    
	    int x1 = findThirdNum((int)l1.get(0),(int)l2.get(0));
	    int x2 = findThirdNum((int)l1.get(1),(int)l2.get(1));
	    int x3 = findThirdNum((int)l1.get(2),(int)l2.get(2));
	    int x4 = findThirdNum((int)l1.get(3),(int)l2.get(3));
	    
	    return x1 + x2*3 + x3*9 + x4*27;
	}

	static boolean isSet(int c1, int c2, int c3)
	{
	    List l1 = convertToAttributes(c1);
	    List l2 = convertToAttributes(c2);
	    List l3 = convertToAttributes(c3);
	    
	    if (!isSetNum((int)l1.get(0),(int)l2.get(0),(int)l3.get(0)))
	    {
	        return false;
	    }
	    
	    if (!isSetNum((int)l1.get(1),(int)l2.get(1),(int)l3.get(1)))
	    {
	        return false;
	    }
	    
	    if (!isSetNum((int)l1.get(2),(int)l2.get(2),(int)l3.get(2)))
	    {
	        return false;
	    }
	    
	    if (!isSetNum((int)l1.get(3),(int)l2.get(3),(int)l3.get(3)))
	    {
	        return false;
	    }
	    
	    return true;
	}

	static List convertToAttributes(int card)
	{
	    int a = card%3;
	    card = card/3;
	    int b = card%3;
	    card = card/3;
	    int c = card%3;
	    card = card/3;
	    int d = card%3;
	    
	    List l = new ArrayList();
	    l.add(a);l.add(b);l.add(c);l.add(d);
	    return l;
	}

	static int calcNumSets()
	{
	    int num=0;
	    int i=0;int j=0; int k=0;
	    for (i=0; i<12; i++)
	    {
	        for (j=i+1; j<12;j++)
	        {
	            for (k=j+1;k<12;k++)
	            {
	                int c1 = (int)theDeal.get(i);
	                int c2 = (int)theDeal.get(j);
	                int c3 = (int)theDeal.get(k);
	                if (isSet(c1,c2,c3)) 
	                {
	                    num++;
	     
	                }
	            }
	        }
	    }
	    return num;
	}

	static int  calcSets()
	{
	    List l = new ArrayList();
	    List l2= new ArrayList();
	    List l3 = new ArrayList();
	    int i=0;int j=0; int k=0;
	    for (i=0; i<SIZE; i++)
	    {
	        for (j=i+1; j<SIZE;j++)
	        {
	            for (k=j+1;k<SIZE;k++)
	            {
	            	int c1 = (int)theDeal.get(i);
	                int c2 = (int)theDeal.get(j);
	                int c3 = (int)theDeal.get(k);
	                if (isSet(c1,c2,c3)) 
	                {
	                    l.add(c1); l.add(c2); l.add(c3);
	                    //llSay(0,(string)convertToAttributes(c1)+" "+(string)convertToAttributes(c2)+" "+(string)convertToAttributes(c3));
	                }
	            }
	        }
	    }
	 
	 
	    for (i=0;i < l.size(); i++)
	    {
	        int num=0;
	        int card = (int)l.get(i);
	        for (j=0;j<l.size();j++)
	        {
	            if ((int)l.get(j)==card)
	            {
	                num++;
	            }
	        }
	        l2.add(num);
	    }
	    
	    for (i=3; i<=9; i++)
	    {
	        for (j=0; j<l.size(); j=j+3)
	        {
	            int score =(int) l2.get(j)+ (int)l2.get(j+1) +(int)l2.get(j+2);
	            //llSay(0,"i= "+(string)i+"  score: "+ (string)score);
	            if (score==i)
	            {
	                //copy set to list.  Zero out cards
	                l3.add(l.get(j)); l3.add(l.get(j+1)); l3.add(l.get(j+2));
	                l2 = remove(l,l2,(int)l.get(j),(int)l.get(j+1),(int)l.get(j+2));
	            } 
	        }
	    }
	    

	  
	    return l3.size()/3;
	}

	static List remove (List l1, List l2, int c1, int c2, int c3)
	{
	    int i=0;
	    for (i=0; i<l1.size(); i++)
	    {
	        if ( ((int) l1.get(i)==c1) || ((int) l1.get(i)==c2) || ((int) l1.get(i)==c3))
	        {
	            l2.set(i, 100);
	         }
	    }
	    return l2;
	}

	
    public static void main(String s[])
    {
    	int mistakes=0;
        int n1=0;
        int n0=0;
        int n2=0;
        int n3=0;
        int n4=0;
        int ii=0;
        for (ii=0; ii<100000; ii++)
        {
        	if (plus3) SIZE=9;
        	
            initDeck();
            int i=1; 
            theDeal = new ArrayList();
            for (i=0; i<SIZE; i++)
            {
                int c = dealCard();
                //llMessageLinked(i,c,"","");
                theDeal.add(c);
            }
            //llMessageLinked(2,0,"","");
            //llMessageLinked(3,1,"","");
            //llMessageLinked(4,2,"","");
            numCards = 0;
            //llSay(0,"There are "+(string)calcNumSets()+" sets.");
            int n = calcSets();
            if ((n==0) && (plus3) && (SIZE==9))
            {
            	for (i=0; i<3; i++)
                {
                    int c = dealCard();
                    //llMessageLinked(i,c,"","");
                    theDeal.add(c);
                    SIZE=12;
                }
            	n = calcSets();
            }
            
            if (n==0) n0++;
            if(n==1) n1++;
            if(n==2) n2++;
            if(n==3) n3++;
            if(n==4) n4++;
            
            float x = r.nextFloat();
            if ((x<MISTAKE) && (n>0))
        	{
            	mistakes++;
        	   n--;
        	}
            
            if (SIZE==9)
            {
                if (n==0) winnings++;
                if (n==1) winnings+=0;
                if (n==2) winnings-=2;
                if (n==3) winnings-=25;
            } else
            {
            	if (n==0) winnings+=2;
                if (n==1) winnings+=1;
                if (n==2) winnings+=0;
                if (n==3) winnings-=4;
                if (n==4) winnings-=100;
            }
        }
        
        System.out.println("number of 0 sets: "+n0);
        System.out.println("number of 1 sets: "+n1);
        System.out.println("number of 2 sets: "+n2);
        System.out.println("number of 3 sets: "+n3);
        System.out.println("number of 4 sets: "+n4);
        
        System.out.println("House wins "+winnings);
        System.out.println("mistakes: "+mistakes);
        
    }
}
    
