<html> 
  <head><%@ include file="title.jsp" %></head>  
  <body>
  <%@ page language="java" contentType="text/html; charset=ISO-8859-1" session="false" info="Quarkus" extends="javax.servlet.http.HttpServlet"
    import ="java.util.Date, java.util.Iterator" pageEncoding= "ISO-8859-1" errorPage="/error.jsp" %> 
  <%@page import="java.util.HashMap" session="true"%>  
  <%! private String hi = "Hello World!!"; %> 
  <%!  
    private String sayHi() 
      {  
        // test II
        return(hi);   
      }     
  %> 
    <%for(int i=0; i<10; i++)
      {   
    %>
    <h2><%out.print(i); %> <%out.print("Second test.jsp!!"); %></h2>
    <%}

    String id = (session == null)? "" : session.getId();
    out.println("Session ID: " + id);
    %><br /> <hr>
    Current Time: <%= java.util.Calendar.getInstance().getTime()  %><br />
    Salutations: <%= sayHi() %>
  </body> 
</html> 