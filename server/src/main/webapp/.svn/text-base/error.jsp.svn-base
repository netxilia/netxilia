<%@ page session="false" language="java" isErrorPage="true" %>

<html>
 <head>
  <title>Error</title>
 </head>
<body >
Origin : <code>${requestScope["javax.servlet.error.request_uri"]}</code>
Exception : <code>${requestScope["javax.servlet.error.exception"]}</code>
Exception type :<code>${requestScope["javax.servlet.error.exception_type"]}</code>
Stack: <code>
<%
	Object obj = request.getAttribute("javax.servlet.error.exception");
    if (obj!=null && obj instanceof Exception){
	Exception ex = (Exception)obj;
	ex.printStackTrace(new java.io.PrintWriter(out));
    }else{
    out.println("Exception object is null");
    }
    String message = (String)request.getAttribute("javax.servlet.error.message");
    if (message == null) message = "";
  
    if (message.contains("Could not locate an ActionBean")) //remove the Stripes message with ALL actionBeans
    	message = "Could not locate the given URL";
%>
</code>


 </body>
</html>
