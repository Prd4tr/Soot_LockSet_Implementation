/*
class C
{
    public int c;
    private final Object obj = new Object();
    public void method(int a)
    {
        synchronized(obj)
        {
            c =a;
        }
    }
}
*/
public class Locks{
    public int c;
    private final Object obj1 = new Object();
    private final Object obj2 = new Object();
    
    public void method2(int a)    //{{17}}           {{}}
    {                             //{}    -> {{17}}     -> {{17},{}}
        synchronized(obj2)        //{}    -> {{17,18}}      -> {{17,18},{18}} 
        {
            c =3;                 //{}   -> {{17,18}}      -> {{17,18},{18}} 
            //method2(b);
        }                         //{}    -> {{17}}     -> {{17},{}}
        method2(b);
    }
    public void method1(int a)
    {
        synchronized(obj1) // entermonitor obj1
        {
            c =2;// {}  -> {{17}}
            c =a;//  {}  -> {{17}}
            Locks lobj2 = new Locks();
            lobj2.method2(a);//  {}  -> {{17}}
            //
        }//{}   -> {{}}
    }
    public static void main(String[] args)
    {
        int a =10; // {}    ->{{}}
        int b =1; 
        b =a;
        Locks lobj1;
        if(*) lobj1 = new Locks(1);
        else lobj1 = new Locks(2);
        lobj1.method1(a); //{}     -> {{}}
        //{{}}
        method2();
    }
}

/*
obj1=o1;
Sync(obj1) //obj1 ={o1} 
{ 
    stmnt//spark lockset {{o1},{o3}}
    Sync(obj2)//obj2 = {o2,o4}
    {
        // Lockset {{o1,o2},{o1,o4}} //spark lockset {{o1,o2},{o3,o2},{o1,o4},{o3,o4}}
    } // obj2 ={o2} // {{o1},{o3},{o1,o4},{o3,o4}}
}
obj1= o3;
spark returned  obj1 ={o1,o3} 

spark lockset= {{o1,o2},{o3,o2}}

For Android to Jimple
entermonitor $r1

exitmonitor $r1

For Java
r2 = $r1
entermonitor $r1

exitmonitor $r2

*/
