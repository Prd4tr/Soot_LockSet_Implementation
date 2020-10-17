public class Lockset {
    public int c;
    pblic Locks(int a){c = a};
    public static void main(String[] args)
    {
        int a =10; //{}     -> {{}}
        int b =1;  //{}     -> {{}}
        b =a; //
        Integer obj1,obj2,obj3;
        obj1 = new Integer(); obj1 =1;
        obj3 = new Integer(); obj3 =3; //points_to_set(obj3) = {11}
        obj2 = new Integer(); obj2 =2;
        obj1 = obj3; //points_to_set(obj1) = {10, 11}
        obj2 = obj3; //points_to_set(obj2) = {11, 12}
        synchronized(obj1)      //{}    -> {{10},{11}}
        {
            synchronized(obj2)  //{}    -> {{10,11},{11,11},{10,12},{11,12}}
            {
                obj2 = 4;       //{}    -> {{10,11},{11,11},{10,12},{11,12}}
            
            }                   //{}    -> {{10},{11},{12}}
        
        }                       //{}    -> {{},{12}}
        //{{}} ideally
    }
}
synchronized(obj1)      //{}    -> {{10},{11}}
{

}
synchronized()          //{}    ->  {{10,11}}