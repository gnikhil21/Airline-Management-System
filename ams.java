import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

class User {
    String name, emailId, username, contactNumber;
    private String password;
    int userId;

    Flights flight=new Flights("", "", "", "", "", 0,"");
    Bookings book=new Bookings("", "", "", 0,"","","","","");	

    User(String name,String emailId,String username, String password,String contactNumber,int userId) throws Exception {

        this.username = username;
        this.password = password;
        this.name=name;
        this.emailId=emailId;
        this.contactNumber=contactNumber;
        this.userId=userId;
        
    }

    boolean login(String password) {
    	if(this.password.equals(password))
    		return true;
    	else
    	  	return false;
    }

    void register() throws Exception {
    	
    	Class.forName("com.mysql.jdbc.Driver");  
		Connection con=DriverManager.getConnection(  
		"jdbc:mysql://localhost:3306/sys","root","1234");  		
		
		PreparedStatement stmt=con.prepareStatement("insert into user1 values (?,?,?,?,?,?)");  
		
		stmt.setInt(1, this.userId);
		stmt.setString(2, this.name);
		stmt.setString(3, this.emailId);
		stmt.setString(4, this.username);
		stmt.setString(5, this.password);
		stmt.setString(6, this.contactNumber);
		
		stmt.executeUpdate();
		stmt.close();
		con.close();
    }

    void viewFlights() throws Exception {
    	Scanner in3=new Scanner(System.in);
        if(flight.displayFlights()==0)
        	return;
        System.out.println("Do you want to book a flight: (Y/n)");
        char ch=in3.next().charAt(0);
        if(ch!='n')
        {
        	this.bookFlight();
        }
    }

    void bookFlight() throws Exception {
    	Scanner in4=new Scanner(System.in);
    	System.out.println("Enter flight number: ");
    	String no=in4.nextLine();
    	
    	Connection con=Project.connection();
    	Statement stmt=con.createStatement();
		ResultSet rs=stmt.executeQuery("select * from flights_1 where flightNo='"+no+"'");
		rs.next();
    	
    	book=new Bookings(this.name, this.emailId, this.contactNumber, this.userId, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
    	
    	PreparedStatement st=con.prepareStatement("insert into bookings values (?,?,?,?,?,?,?,?,?)");  
		
		st.setString(1, book.name);
		st.setString(2, book.email);
		st.setString(3, book.contactNumber);
		st.setInt(4, book.userId);
		st.setString(5, book.source);
		st.setString(6, book.destination);
		st.setString(7, book.flightNo);
		st.setString(8, book.startTime);
		st.setString(9, book.endTime);

		st.executeUpdate();
		con.close();
		stmt.close();
		st.close();
		
		System.out.println("Your have booked the flight");
    	
    }

    void bookedFlights() throws Exception {
        book.displayBookings(this.userId);
    }

}

class Admin {
    String name, email, username, password, contactNumber;

    Flights flight=new Flights("", "", "", "", "", 0,"");
    Bookings book=new Bookings("", "", "", 0,"","","","","");	
	
    Admin(String name, String email,String contactNumber) {
        this.name = name;
        this.email = email;
        this.contactNumber=contactNumber;
    }

    boolean login(String username,String password) {
    	if(username.equals("admin")&&password.equals("admin"))
    		return true;
    	return false;
    }

    void viewFlights() throws Exception {

        flight.displayFlights();
    }

    void addFlights() throws Exception {
    	Scanner in2 = new Scanner(System.in);
    	String source, destination, flightNo,date, startTime="", endTime="";
        double duration=0.0;
        
    	System.out.print("Source: ");
        source = in2.nextLine();
        System.out.print("Destination: ");
        destination= in2.nextLine();
        do {
        	System.out.print("Flight No: ");
            flightNo = in2.nextLine();
        }while(Project.check("flightNo", flightNo,"flights_1")!=1);
        System.out.print("Start Time: ");
        int fs=1;
        while(fs==1)
        {
        	try {
            	startTime = in2.nextLine();
            	if (!startTime.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d"))
            		throw new Exception("Enter valid start time: ");
            	fs=0;
            }
            catch (Exception e) {
				System.out.print(e.getMessage());
			}
        }
        System.out.print("End Time: ");
        int fe=1;
        while(fe==1)
        {
        	try {
            	endTime = in2.nextLine();
            	if (!endTime.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d"))
            		throw new Exception("Enter valid end time: ");
            	fe=0;
            }
            catch (Exception e) {
				System.out.print(e.getMessage());
			}
        }
        System.out.print("Duration: ");
        int fd=1;
        while(fd==1)
        {
        	try {
        		String s=in2.nextLine();
            	duration = Double.parseDouble(s);
            	fd=0;
            }
            catch (Exception e)
            {
            	System.out.println("Invalid duration");
            	System.out.println("Enter valid duration: ");
            }
        }
        System.out.print("Date: ");
        date = in2.nextLine();
        
        
        Flights flight = new Flights(source, destination, flightNo, startTime, endTime, duration,date);
        
        Connection con=Project.connection();		
		PreparedStatement stmt=con.prepareStatement("insert into flights_1 values (?,?,?,?,?,?,?)");  
		
		stmt.setString(1, flight.source);
		stmt.setString(2, flight.destination);
		stmt.setString(3, flight.flightNo);
		stmt.setString(4, flight.startTime);
		stmt.setString(5, flight.endTime);
		stmt.setDouble(6, flight.duration);
		stmt.setString(7, flight.date);
		
		stmt.executeUpdate();
		stmt.close();
		con.close();
    }

    void bookedFlights() throws Exception {
        book.displayBookings();
    }
}

class Flights {
    String source, destination, flightNo, startTime, endTime,date;
    double duration;

    Flights(String source,String destination,String flightNo,String startTime,String endTime,double duration,String date)
    {
    	this.source=source;
    	this.destination=destination;
    	this.flightNo=flightNo;
    	this.startTime=startTime;
    	this.endTime=endTime;
    	this.duration=duration;
    	this.date=date;
    }
    
    int displayFlights() throws Exception{
    	Connection con=Project.connection(); 
		Statement stmt=con.createStatement();
		
		ResultSet r=stmt.executeQuery("select count(*) from flights_1");
		r.next();
		int count=r.getInt(1);
		if(count==0)
		{
			System.out.println("No flights available");
			return 0;
		}
		
		ResultSet rs=stmt.executeQuery("select * from flights_1 ORDER BY date ASC");
		System.out.println("Source\t\tDestination\tFlight No\tStart Time\tEnd Time\tDuration(Hrs)\tDate");
		while(rs.next())
		{
			System.out.println(rs.getString(1)+"\t\t"+rs.getString(2)+"\t\t"+rs.getString(3)+"\t\t"+rs.getString(4)+"\t\t"+rs.getString(5)+"\t\t"+rs.getDouble(6)+
					"\t\t"+rs.getString(7));
		}
		stmt.close();
		con.close();
		return 1;
    }
}

class Bookings {
    String name, email, contactNumber;
    int userId;
    String source, destination, flightNo, startTime, endTime;

    Bookings(String name,String email,String contactNumber,int userId,String source,String destination,String flightNo,String startTime,String endTime)
    {
    	this.name=name;
    	this.email=email;
    	this.contactNumber=contactNumber;
    	this.userId=userId;
    	this.source=source;
    	this.destination=destination;
    	this.flightNo=flightNo;
    	this.startTime=startTime;
    	this.endTime=endTime;
    }
    
    void displayBookings(int userId) throws Exception{
    	Connection con=Project.connection(); 
		
		Statement stmt=con.createStatement();
		
		ResultSet r=stmt.executeQuery("select count(*) from bookings where userId="+userId);
		r.next();
		int count=r.getInt(1);
		if(count==0)
		{
			System.out.println("No flights booked");
			return;
		}
		ResultSet rs=stmt.executeQuery("select * from bookings where userId="+userId);
		//rs.next();
		System.out.println("Name\t\tEmail Id\t\tContact No\tUser Id\t\tSource\t\tDestination\tFlight No\tStart Time\tEnd Time");
		while(rs.next())
		{
			System.out.println(rs.getString(1)+"\t\t"+rs.getString(2)+"\t\t"+rs.getString(3)+"\t"+rs.getInt(4)+"\t\t"+rs.getString(5)+"\t\t"+rs.getString(6)
			+"\t\t"+rs.getString(7)+"\t\t"+rs.getString(8)+"\t\t"+rs.getString(9));
		}
		
		stmt.close();
		con.close();
    }
    
    void displayBookings() throws Exception {
    	Connection con=Project.connection(); 
		
		Statement stmt=con.createStatement();
		
		ResultSet r=stmt.executeQuery("select count(*) from bookings");
		r.next();
		int count=r.getInt(1);
		if(count==0)
		{
			System.out.println("No flights booked");
			return;
		}
		
		ResultSet rs=stmt.executeQuery("select * from bookings");
		System.out.println("Name\t\tEmail Id\t\tContact No\tUser Id\t\tSource\t\tDestination\tFlight No\tStart Time\tEnd Time");
		while(rs.next())
		{
			System.out.println(rs.getString(1)+"\t\t"+rs.getString(2)+"\t\t"+rs.getString(3)+"\t"+rs.getInt(4)+"\t\t"+rs.getString(5)+"\t\t"+rs.getString(6)
			+"\t\t"+rs.getString(7)+"\t\t"+rs.getString(8)+"\t\t"+rs.getString(9));
		}
		stmt.close();
		con.close();
    }
}

class Project {
	
	public static Connection connection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");  
		Connection con=DriverManager.getConnection(  
		"jdbc:mysql://localhost:3306/sys","root","1234"); 
		
		return con;
	}
	
	public static int check(String type,String value,String table) throws Exception
	{
		Connection con=connection();
		Statement stmt=con.createStatement();
		ResultSet r=stmt.executeQuery("select "+type+" from "+table);
		while(r.next())
		{
			if(r.getString(1).equals(value))
			{
				System.out.println(type+" already exists.");
				return 0;
			}
		}
		
		return 1;
	}
	
	public static int choice(String str)
	{
		Scanner in=new Scanner(System.in);
		int f1=0,choice=0;
        while(f1==0)
        {
        	try {
            	System.out.println(str);
            	String s=in.nextLine();
            	choice = Integer.parseInt(s);
            	f1=1;
            }
            catch (Exception e) {
            	System.out.println("Enter valid option.");
    		}
        }
        return choice;
	}

	public static int isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + // part before @
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return 0;
        if(pat.matcher(email).matches())
        	return 1;
        else
        	return 0;
    }
	
	public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        int choice=choice("1 to login,2 to register");
        String username,password,name,emailId,contactNumber="";

        switch (choice) {
            case 1:
            	System.out.println("Please login");
                System.out.print("username: ");
                username = in.nextLine();
                System.out.print("password: ");
                password = in.nextLine();
                Admin admin=new Admin("Admin","admin@gmail.com","xxxxxxxxxx");
                if(admin.login(username, password))
                {
                	
                	System.out.println("Welcome Admin!!");
                	String ch = null;
                	do
                	{                    	
                    	int choice2=choice("1 to view flights,2 to add flights,3 to all bookings");
                    	
                    	switch(choice2)
                    	{
                    		case 1:admin.viewFlights();
                    				break;
                    		case 2:admin.addFlights();
                    				break;
                    		case 3:admin.bookedFlights();
                    				break;
                				
                			default:return;
                    	}
                    	System.out.println("Do you want to continue(Y/n)");

                    	try {
                    		ch=in.nextLine();
                    	}
                    	catch (Exception e)
                    	{
                    		System.out.println(e);
                    	}

                	}while(ch.charAt(0)!='n');
         
                }
                else
                {
                	Connection con=connection();
                	Statement stmt=con.createStatement();
                	
                	while(true)
                	{
                		ResultSet r=stmt.executeQuery("select username from user1");
                		int find=0;
                		while(r.next())
                		{
                			if(username.equals(r.getString(1)))
                			{
                				find=1;
                				break;
                			}
                		}
                		if(find==1)
                			break;
                		else
                		{
                			System.out.println("Enter valid username: ");
                			username=in.nextLine();
                		}
                	}
                	
            		ResultSet rs=stmt.executeQuery("select * from user1 where username='"+username+"'");
            		rs.next();
            		User user=new User(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(1));
            		while(!user.login(password))
            		{
            			System.out.println("Wrong password");
            			System.out.println("Enter password again: ");
            			password=in.nextLine();
            		}
            			
            		System.out.println("Welcome "+user.name+"!!");
                	String ch = null;
                	do
                	{                    	
                    	int choice2=choice("1 to view and book flights,2 to view history of bookings");
                    	
                    	switch(choice2)
                    	{
                    		case 1:user.viewFlights();
                    				break;
                    		case 2:user.bookedFlights();
                    				break;
                    		default:return;
                    	}
                    	System.out.println("Do you want to continue(Y/n)");
                    	
                    	try {
                    		ch=in.nextLine();
                    	}
                    	catch (Exception e)
                    	{
                    		System.out.println(e);
                    	}

                	}while(ch.charAt(0)!='n');
                }
                break;

            case 2:
            	System.out.println("Register");
                System.out.print("Name: ");
                name = in.nextLine();
                do
                {
                	System.out.print("Email Id: ");
                    emailId= in.nextLine();                   
                    
                }while(isValid(emailId)!=1||check("emailId",emailId,"user1")!=1);
                
                do
                {
                	System.out.print("Username: ");
                	username = in.nextLine();
                }while(check("username",username,"user1")!=1);
                
                do
                {
                	System.out.print("password: ");
                	password = in.nextLine();
                }while(check("password",password,"user1")!=1);
                
                System.out.print("ContactNumber: ");
                int f=1;
                while(f==1)
                {
                	try {
                    	contactNumber = in.nextLine();
                    	if(contactNumber.length()!=10)
                    		throw new Exception("Enter valid contact number: ");
                    	for(int i=0;i<contactNumber.length();i++)
                    	{
                    		if(Character.isAlphabetic(contactNumber.charAt(i)))
                    			throw new Exception("Enter valid contact number: ");
                    	}
                    	f=0;
                    }
                    catch (Exception e) {
    					System.out.print(e.getMessage());
    				}
                }
				try {
					Connection con=Project.connection(); 
					Statement stmt=con.createStatement();
					ResultSet r=stmt.executeQuery("select count(*) from user1");
					r.next();
					int count=r.getInt(1);
					User user_ob = new User(name,emailId,username, password,contactNumber,count+1);
					user_ob.register();
						
	            	String ch = null;
	            	do
	            	{	                	
	                	int choice2=choice("1 to view and book flights,2 to view history of bookings");
	                	
	                	switch(choice2)
	                	{
	                		case 1:user_ob.viewFlights();
	                				break;
	                		case 2:user_ob.bookedFlights();
	                				break;
	                		default:return;
	                	}
	                	System.out.println("Do you want to continue(Y/n)");

	                	try {
	                		ch=in.nextLine();
	                	}
	                	catch (Exception e)
	                	{
	                		System.out.println(e);
	                	}

	            	}while(ch.charAt(0)!='n');
				} catch (Exception e) {
					System.out.println(e);
				}
                break;
            	default:in.close();
            			return;
        }

        in.close();
    }
}