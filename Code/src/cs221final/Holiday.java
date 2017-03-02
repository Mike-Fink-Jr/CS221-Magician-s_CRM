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
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Michael Fink
 */
public class Holiday {
    
    private final String dbURL = "jdbc:derby://localhost:1527/CS221.FinalDB"; ;
    private ResultSet r;
    private Connection conn;
    private PreparedStatement ps;
    
    private int id;
    private ArrayList<String> holiName;
    
    public Holiday()
    {
        try{
            conn = DriverManager.getConnection(dbURL, "java", "java");
        }
        catch(SQLException e)
        {System.out.println("holiday start: "+e);
        }
        
         holiName= new ArrayList<>(Arrays.asList(init()));
         if(holiName.size()>0)
         id=holiName.size()-1;
        else id=-1;
        
    
    }
    
  // intitalizes the classes mageName and id
    public final String[] init() 
     {
        String[] items = {};
        
        try
        {
          ps = conn.prepareStatement("select name from holiday while order by id asc");
          r = ps.executeQuery();
          
        ArrayList<String> listItems = new ArrayList<>();
        if(!r.next())
        { 
            return new String[0];
        }
        else do
        {
            listItems.add(r.getString("name"));
        }while(r.next());
        
        items = listItems.toArray(items);
  
        
         
       
         
         
        }
        catch(SQLException e){System.out.println(e.getMessage());}
        
        
        
        return items;
    }
    
    
    public boolean isBooked(int x)//holidayid
    {try{
         ps = conn.prepareStatement("select holiday_id from wait_list");
       
            r= ps.executeQuery();
            
            
               while( r.next())
                   if(r.getInt("holiday_ID")==x)
                       return true;
               return false;
                   
                   
            
    }catch(SQLException e){ System.out.println("holi isbooked :" + e.getMessage());}
    return false;
    }
    
    
    
    public void add(String holiday)
    {
        try
        {
            if(holiName.size()<=0)
                id=0;
            holiName.add(holiday);
        
            ps = conn.prepareStatement("insert into holiday (name , id) values (? , ?)");
            ps.setString(1, holiday);
            ps.setString(2,id+"" );
           
            id++;
            ps.executeUpdate();
        }
        catch(SQLException e)
        {
            
        }
        
    }
    
    public String[] getItems()
    {
        return holiName.toArray(new String[holiName.size()]);
    }
    
  
    
    
    
    public String getStatus(String holiday)
    {ResultSet r2;
        String out = holiday + " Status:\n";
        int temp;
        try
        {
             ps = conn.prepareStatement("select * from booked where holiday_id= (select id from holiday where name=?)");
             ps.setString(1,holiday);
           
             r =ps.executeQuery();
            
        
        while(r.next())
        {
            out = out +  r.getString("name")+ " booked with ";   
            temp=r.getInt("magician_id");
            
            ps = conn.prepareStatement("select name from magician where id=?");
                ps.setInt(1, temp);
            r2= ps.executeQuery();
            if(r2.next())
                out= out+ r2.getString("name") + "\n";
        }
        
        return out;
        }
        catch(SQLException e)
        {    
        return "Error holi get Status: " + e.getMessage();
        }
    }
  

    public String getName(int id)
    {
        return holiName.get(id);
    }
    public String getMageName(int id)
    {
        try{
            ps = conn.prepareStatement("select name from Magician where id=?");
            ps.setInt(1,id);
            r = ps.executeQuery();
            r.next();
            return r.getString(1);
        }
        catch(SQLException e){System.out.println("get mage name: "+e.getMessage());}
        return null;
    }
        public int getID(String name)
    {
     if(holiName.contains(name))
         return holiName.indexOf(name);
     return -1;
    }
}
