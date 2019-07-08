<%-- 
    Document   : serves as the View in our Project 4 Task 2
    Author     : Zirui Zheng
    Andrew ID  : ziruizhe
--%>

<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype")%>

<html>
    <head>
        <title>App Data</title>
    </head>
    <body>
<!--        set up a table to display all analytics data-->
        <table>           
            <tr>
                <th><h1>App Data Analytics</b></h1></th>
            </tr>
<!--            get a arraylist that stores all the data
            arraylist[0] are all the log information
            arraylist[1] is "total visits", arraylist[2] is "top device", ... respectively-->
            <%
                String[] names = {"Total Visits", "Top Device", "Top Device Visits", "Average Process Time", "Shortest Process Time", "Top Search", "Top Search Amount"};
                ArrayList<String> list =  (ArrayList<String>)request.getAttribute("list"); 
                for (int i = 0; i < names.length; i++) {
            %>
            <tr>
                <td>
                    <%= names[i]%>
                </td>
                <td>
                    <%= list.get(i+1)%>
                </td>
                <%
                    }
                %>                   
            </tr>
        </table>
<!--            display all logging information from all the requests-->
        <h1>All Log Information</h1>    
        <p>
            <% String[] logs = list.get(0).split("\n");
                for (String log : logs){
            %>
            <br/><%= log%>
            <% } %>
        </p>
    </body>
</html>
