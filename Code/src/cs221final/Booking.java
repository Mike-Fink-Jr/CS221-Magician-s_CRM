/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs221final;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Michael Fink
 */
public class Booking 
{
    private static final String dbURL = "jdbc:derby://localhost:1527/CS221.FinalDB";
    private static Magician mage;
    private static Holiday holi;
    
    private static Connection conn;
    private static PreparedStatement ps;
     private static ResultSet r;
    
    
    
    public static void importInstances(Magician m, Holiday h)
    {           mage=m;
                holi=h;
                try{
                conn= DriverManager.getConnection(dbURL, "java", "java");
                }
                catch(SQLException e)
                {
                    System.out.println("importinstances :" + e.getMessage());
                }
    }

    /**
     *
     * @param holiday_id
     * @param name
     * @param t
     * @return String out that says weather the guy goes to WL or is booked
     */
    public static String book(int holiday_id,String name, Timestamp t)
    {
        int mageID;
        int magesID[];
        String out;
        try
        {
            if(mage.getAllFree(holiday_id)!=null)
            { magesID = mage.getAllFree(holiday_id);
               
               mageID=magesID[0];
               }else mageID=-1;
   
      //  System.out.println("magician id " + mageID);
        
        if(mageID != -1 )
        {    
              
                ps = conn.prepareStatement("insert into booked (holiday_id, magician_id, timestamp, name) values (?,?, ?, ?)");
                ps.setInt(1, holiday_id);
                ps.setInt(2, mageID);
                
                // for rebooking from waitlist if null normal book if t then  rebooker
                if(t==null)
                ps.setTimestamp(3, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                else ps.setTimestamp(3,t);
                ps.setString(4, name);
                ps.executeUpdate();

                ps = conn.prepareStatement("select name from magician where id=?");
                ps.setInt(1, mageID);
                r = ps.executeQuery();
                r.next();
                out = name+" was booked with " +r.getString("name")+" on " + holi.getName(holiday_id);
            
        }
        else //none free call waitlist.add
            {
                out= WaitList.add(holiday_id, name);
               // out = name + " added to waiting list for " +holi.getName(holiday_id);
            }
        
        
        }catch(SQLException e)
        {
            out = "Error: " + e.getMessage();
        }
        return out;
    }
    /**
     *
     * @param holiday_id
     * @param name
     * @return String out that says weather the guy goes to WL or is booked
     */
    public static String book(int holiday_id,String name)
    {
        int mageID;
        int magesID[];
        String out;
        try
        {
            if(mage.getAllFree(holiday_id)!=null)
            { magesID = mage.getAllFree(holiday_id);
               
               mageID=magesID[0];
               }else mageID=-1;
   
      //  System.out.println("magician id " + mageID);
        
        if(mageID != -1 )
        {    
              
                ps = conn.prepareStatement("insert into booked (holiday_id, magician_id, timestamp, name) values (?,?, ?, ?)");
                ps.setInt(1, holiday_id);
                ps.setInt(2, mageID);
                
                // for rebooking from waitlist if null normal book if t then  rebooker

                ps.setTimestamp(3, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
               
                ps.setString(4, name);
                ps.executeUpdate();

                ps = conn.prepareStatement("select name from magician where id=?");
                ps.setInt(1, mageID);
                r = ps.executeQuery();
                r.next();
                out = name+" was booked with " +r.getString("name")+" on " + holi.getName(holiday_id);
            
        }
        else //none free call waitlist.add
            {
                WaitList.add(holiday_id, name);
                out = name + " added to waiting list for " +holi.getName(holiday_id);
            }
        
        
        }catch(SQLException e)
        {
            out = "Error: " + e.getMessage();
        }
        return out;
    }
    
    public static String unWait(int mageID)
    {
        //grab all of the holidays
        // books waitlist for the new mageID
     ArrayList<Integer> holidays=new ArrayList<>();
             
        for (int x=0; x < holi.getItems().length;x++)
        {                                   
            if(holi.isBooked(x))         //true if holiday-id (x) is on the waitlist holiday id
            {
                holidays.add(x);
            } 
        }       
                       
        String s=""; String c; 
     //   System.out.println("string: "+s+" size: "+holidays.size());
         if(!holidays.isEmpty()&& mage.getName(mageID)!=null)
            {
        try
        {   for(Integer hID : holidays)
                {
       
                    ps = conn.prepareStatement("select * from wait_list where holiday_id=? order by timestamp asc");
                    ps.setInt(1, hID);
                    r = ps.executeQuery();
                    // System.out.println("string: "+s+" size: "+hID);
                             
                    if(r.next())
                    {s=s+"\n";
                      ps = conn.prepareStatement("insert into booked (timestamp, holiday_id , magician_id , name) values (?, ?, ?, ?)");
                      ps.setTimestamp(1, r.getTimestamp("timestamp"));
                    
                      ps.setInt(2, hID);
                      ps.setInt(3, mageID);
                     
                      c= r.getString("name");
                      ps.setString(4, c);
                   
                      ps.executeUpdate();
                           WaitList.delete(r.getTimestamp("timestamp"));
                      s= s+"Customer "+c+" was moved from the wait list on "+holi.getName(hID)+" and booked with "+mage.getName(mageID);
                    // System.out.println("string: "+s+" size: "+hID);
                    }
               
                }
            
        }
        catch(SQLException e){}
        return s;
        }else return "there are no holidays available";
    }
    
    public static String fromWaitList(String holiday)
    {
      
        String s="";
        try
        {
            ps = conn.prepareStatement("select * from wait_list where holiday_id=(select id from holiday where name = ?) and order by timestamp asc");
            ps.setString(1, holiday);
            r = ps.executeQuery();
            if(r.next())
            { //System.out.println("booking from wl inside if");
               
           //   System.out.println(book(r.getInt("holiday_id"), r.getString("name"),r.getTimestamp("timestamp"))+"\n"+ r.getString("name")+" was moved from the waitlist"+s);
                WaitList.delete(r.getTimestamp("timestamp"));
            }
        }
        catch(SQLException e){}
        return s;
    }
    
   
    public static String rebook(int magician_id, String magician)
    {
        String s="";
        
        try
        {
            ps = conn.prepareStatement("select * from booked where magician_id=?");
            ps.setInt(1, magician_id);
            r=ps.executeQuery();
       // System.out.println("rebook1");
            while(r.next())
            {//System.out.println("rebook2");
                if(holi.isBooked(r.getInt("holiday_ID")))
                {  
     //  System.out.println("rebook3");
            s= WaitList.add(r.getInt("holiday_ID"),r.getString("name"),r.getTimestamp("timestamp"))+"\n"+s;
                   //   s=  book(r.getInt("holiday_id"), r.getString("name"),r.getTimestamp("timestamp"))+s;
              //   System.out.println("rebook4");
                   delete(r.getString("name"), r.getInt("holiday_id"));
       //  System.out.println("rebook5");
                 }else
                { s= WaitList.add(r.getInt("holiday_id"), r.getString("name"),r.getTimestamp("timestamp"))+s;
                     WaitList.delete(r.getTimestamp("Timestamp"));
         //            System.out.println("rebook6");}
                } }
            
            
        }
        catch(SQLException e){System.out.println("Rebook failed: " + e.getMessage());}
        return s;
    }
      public static void delete(String name,int holiday_id)
    {
        try
        {
            ps= conn.prepareStatement("delete from booked where (name=?) AND (holiday_id=?)");
            ps.setString(1,name);
            ps.setInt(2,holiday_id);
            ps.executeUpdate();
        //    System.out.println("booking delete end:- name: "+name+"holiday="+holiday_id);
        }
        catch(SQLException e){}
    }
      
    public static void delete(String name,String holiday)
    {
        try
        {
            ps= conn.prepareStatement("delete from booked where (name=?) AND (holiday_id=(select id from holiday where name=?))");
            ps.setString(1,name);
            ps.setString(2,holiday);
            ps.executeUpdate();
            //System.out.println("booking delete end:- name: "+name+"holiday="+holiday);
        }
        catch(SQLException e){}
    }
      
    public static String cancelHoliday(String holiday, String name)
    {
       String s = name+" canceled their appointment on "+holiday;
        try{
               ps = conn.prepareStatement("select timestamp from wait_list where name=? and holiday_id=(select id from holiday where name=?) ");
            ps.setString(1, name );
                  ps.setString(2, holiday );
            r=ps.executeQuery();
            
            if(r.next())
            {
                WaitList.delete(r.getTimestamp("timestamp"));
                return  name+" was removed from the waitlist on "+holiday ;
                
            }else{    
           delete(name, holiday);
           
           ps = conn.prepareStatement("select * from wait_list where holiday_id=(select id from holiday where name=?) order by timestamp asc");
            ps.setString(1, holiday );
            r=ps.executeQuery();
            
            if(r.next())
            {          WaitList.delete(r.getTimestamp("timestamp"));
       s=  book(r.getInt("holiday_id"), r.getString("name"))+"\n"+s;
          
            }}
          }
        catch(SQLException e){}
        return s;
    }
    /*public static String cancel(String holiday, String WLname)
    {String s="";
    try{
       ps = conn.prepareStatement("select name from wait_list where holiday_id=(select id from holiday where name=?)");
            ps.setString(1, holiday );
            r=ps.executeQuery();
            
            
            
            
    }catch(SQLException e){}
    return s;
    }*/
    
    
    


}