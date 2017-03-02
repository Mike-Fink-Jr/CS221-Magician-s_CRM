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
import java.util.Calendar;

/**
 *
 * @author Michael Fink
 */
public class WaitList {
    
    private static final String dbURL = "jdbc:derby://localhost:1527/CS221.FinalDB";
    private static Holiday holi;
    
    
    
    private static Connection conn;
    private static PreparedStatement ps;
     private static ResultSet r;
    
    
    
    public static void importInstances( Holiday h)
    {           
                holi=h;
                try{
                conn= DriverManager.getConnection(dbURL, "java", "java");
                }
                catch(SQLException e)
                {
                    System.out.println("importinstances :" + e.getMessage());
                }
    }
    public static String add(int holiday_id, String name, Timestamp t)
    {
        try
        {
            ps = conn.prepareStatement("insert into wait_list (holiday_id, timestamp, name) values (?,?,?)");
            ps.setInt(1, holiday_id);
            if(t==null)
            ps.setTimestamp(2, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
            else ps.setTimestamp(2, t);
            ps.setString(3, name);
            ps.executeUpdate();
            
            return name+" was added to the waiting list for the holiday "+holi.getName(holiday_id);
            
        }
        catch(SQLException e)
        {}
        return "";
    }
    public static String add(int holiday_id, String name)
    {
        try
        {
            ps = conn.prepareStatement("insert into wait_list (holiday_id, timestamp, name) values (?,?,?)");
            ps.setInt(1, holiday_id);
         
            ps.setTimestamp(2, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
            ps.setString(3, name);
            ps.executeUpdate();
            
            return name+" was added to the waiting list for the holiday "+holi.getName(holiday_id);
            
        }
        catch(SQLException e)
        {}
        return "";
    }
    
    public static String read(String holiday)
    {
        String out = "Customers on the Waitlist for holiday "+holiday+":\n";
        try
        {
        ps = conn.prepareStatement("select name from wait_list where holiday_id=? order by timestamp asc");
        ps.setInt(1, holi.getID(holiday));
        r = ps.executeQuery();
        if(r.next())
        {
            do
            {
            out += r.getString("name")+ "\n";
            }while(r.next());
        
            return out;
        }else return "There is no one on the "+holiday+" Waitlist.";
        
        }
        catch(SQLException e)
        {
            return "Error: " + e.getMessage();
        }
    }
    
    //returns true if there is someone on the waiting list
    public static boolean check()
    {
        try
        {

            ps = conn.prepareStatement("select * from wait_list");
            r = ps.executeQuery();
            if(r.next())//result has something
                return true;
            else
                return false;
        }
        catch(SQLException e){return false;}
    }
    
    public static void delete(Timestamp time)
    {
        try
        {System.out.println("wl delete");
            ps = conn.prepareStatement("delete from wait_list where timestamp=?");
            ps.setTimestamp(1,time);
            ps.executeUpdate();
        }
        catch(SQLException e)
        {}
    }
}
