/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs221final;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Michael Fink
 */
public final class Magician {
    
    private static final String dbURL = "jdbc:derby://localhost:1527/CS221.FinalDB"; ;
    private static Connection conn;
    private PreparedStatement ps;
     private ResultSet r;
     
     
    private int id;
    private ArrayList<String> mageName;
   
  
    
    
    
    public Magician()
    {
        try{
    
             conn = DriverManager.getConnection(dbURL, "java", "java");  
        }
        catch(SQLException e){}
    
   mageName= new ArrayList<>(Arrays.asList(initMages()));
   id=mageName.size()-1;
    
    }
    
  // intitalizes the classes mageName and id  ////id is the id corresponding to the arraylist
    public final String[] initMages() 
     {
        String[] items = {};
        
        try
        {
        ps = conn.prepareStatement("select name from magician while order by id asc");
          r = ps.executeQuery();
          
        ArrayList<String> listItems = new ArrayList<>();
        if(!r.next())
        { id=-1;
            return new String[0];
        }else do
        {
            listItems.add(r.getString("name"));
        
        
        items = listItems.toArray(items);
        
        }while(r.next());
        }
        catch(SQLException e){System.out.println(e.getMessage());}
        
        
        
        return items;
    }
   
    //adds to db and mageName
    public String add(String magician)
    {//op= "and" uunwait== customerName was moved from the waitlist on holiName
       
  
        try
        {
             ps = conn.prepareStatement("insert into magician (name,id) values (?,?)");
            ps.setString(1, magician);
               id++;
            
            ps.setString(2,(id)+"");
                   
            mageName.add(magician);
          
            ps.executeUpdate();
            //System.out.println("Wait list? " + WaitList.check());
            if(WaitList.check())
               return Booking.unWait(id)+"\n";
            
        }
        catch(SQLException e)
        {
            System.out.println("mage add "+  e.getMessage());
        }
        return "";
    }
    
    
    
    
    //returns first available magician, or -1 if there are no free magicians
    public int[] getAllFree(int holiday_id)
    {    ArrayList<Integer> list = new ArrayList<>();
            int[] names;
        try
        {
        ps = conn.prepareStatement("Select id from magician where id not in ( Select magician_id from booked where booked.holiday_id=?)");
        ps.setInt(1, holiday_id);
        r = ps.executeQuery();
    

        while(r.next())
        {
            list.add(r.getInt(1));
        }
        if(list.size()>0)
        {
          names = (new int[list.size()]);
        
        
        
         for (int x=0;x<list.size();x++)
         {
            names[x]=list.get(x);
            
         }
        
        return names;
        
        }}
        catch(SQLException e){System.out.println("Didn't work " + e); 
        }
        return null;
    }
    
  
    public String getStatus(String magician)
    {   ResultSet r2;
        String out = magician + "'s Status:\n";
        int temp;
        try
        {
             ps = conn.prepareStatement("select * from booked where magician_id=?");
             
             ps.setInt(1, getID(magician));
             r =ps.executeQuery();
            
        
        while(r.next())
        {
            out = out + "Booked by "+ r.getString("name");   
            temp=r.getInt("holiday_id");
            
            ps = conn.prepareStatement("select name from holiday where id=?");
                ps.setInt(1, temp);
            r2= ps.executeQuery();
            if(r2.next())
                out= out+" on "+ r2.getString("name") + "\n";
        }
        
        return out;
        }
        catch(SQLException e)
        {    
        return "Error mage get Status: " + e.getMessage();
        }
    }
    
    //trades mage id for name no SQL commands needed because of magename.get(idnum) 
    public String getName(int idNum)
    {
        if(mageName.size()> idNum)
            if(mageName.get(idNum)!=null)
    return mageName.get(idNum);
        return null;
    }
    
    
    //for the intial combos? returns magenum as a string[]
    public String[] getItems() // for the initial combo
    {
        ArrayList<String> mageName2= new ArrayList<>();
        for(String n : mageName)
        {if(!n.equals(""))
            mageName2.add(n);
        }
        return mageName2.toArray(new String[mageName.size()]);
    }
    
    //trades mage name for id no sql commands because of  magename.contains and .index
    public int getID(String magician)
    {
        if(mageName.contains(magician))
          return  mageName.indexOf(magician);
        return -1;
    }
   
    public String drop(String magician)
    {//op custName was rebooked or moved to waitlist
      
       String s="";
      //  System.out.println("Mage drop: " + magician);
        try{
          int tempID = getID(magician);
           
              //System.out.println("Mage drop id"+tempID );
            ps = conn.prepareStatement("delete from magician where id=?");
            ps.setInt(1, tempID);
            ps.executeUpdate();
            
            mageName.set(tempID, ""); // sets an empty string to the temp id in mageName
            // removes from magician db and updates the arraylist
            //next need to remove from booking/waitlist
            
             s=Booking.rebook(tempID, magician)+s;
            
        }
        catch(SQLException e ){System.out.println("Magician not dropped: " + e.getMessage());}
        return s;
    }
}
