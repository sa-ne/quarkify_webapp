<html>
  <head><title>JSP Test First</title></head> 
  <body>
    <%for(int i=0; i<10; i++)
      {    
    %>
    <h2>#<%out.print(i); %> <%out.print("This is great!!"); %></h2>
    <%}
    
    out.println("Session ID: " + session.getId());
    %><br /> 
    Current Time: <%= java.util.Calendar.getInstance().getTime()  %> 
  </body> 
</html> 