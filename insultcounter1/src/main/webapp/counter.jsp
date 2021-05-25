<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Insult Counter</title>
  <link rel="stylesheet" type="text/css" href="css/style.css" media="screen" />
  <link rel="shortcut icon" href="images/favicon.ico" />
</head>
<body>
<%
  String userCount = (String)session.getAttribute("userCount");
  String accessCount = (String)session.getAttribute("accessCount");
  String insult = (String)session.getAttribute("insult");
%>
<br /><br />
<center>
<table cellspacing="0" cellpadding="0" class="mainpage">
  <tr>
    <td align="left">
	  <div class="header">
            <table width="100%">
             <tr>
               <td align="left"><h1><img src="images/redhat-logo.png" alt="Red Hat" /></h1></td>
               <td align="right"><h1>Insult Counter</h1></td>
             </tr>
             </table>
	  </div>
	  <div class="breadcrumbs">
	    &nbsp;<br />
        <hr color="#E1E1E1" />
	  </div>
	  <br /><br />
	  <center><h1 style="color:red;"><%=insult %></h1></center>
      <table cellspacing="0" cellpadding="0" border="0">
        <tr valign="top">
          <td>
	    <div class="nav">
	    <br /><br /><br /><br /><br /><br /><br />
	    <img src="images/blush.png" height="35%" width="35%" />
            <br /><br />
	  </div>
          </td>
          <td>
	  <div class="middle">
            <!-- Maintain minimum layout size using spacer.png -->
            <img src="images/spacer.png" width="500px" height="1px" alt="" /><br />
            
	        <br /><br /><br /><br /><br /><br />
            <h3>This instance has been accessed <%=accessCount %> times in total.</h3>
            <h3>This instance has been accessed <%=userCount %> times by you.</h3>
	        <br /><br />
          </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</center>
</body>
</html>
