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
    public void method2(int a)
    {
        synchronized(obj2)
        {
            c =3;
            Locks lobj2 = new Locks();

        }
    }
    public void method1(int a)
    {
        synchronized(obj1)
        {
            c =2;
            c =a;
            Locks lobj2 = new Locks();
            lobj2.method2(a);
        }
    }
    public static void main(String[] args)
    {
        int a =10;
        int b =1;
        b =a;
        Locks lobj1 = new Locks();
        lobj1.method1(a);
    }
}