package web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import msg.Msg;


@WebServlet({"/WebClientServlet","/webclient"})
public class WebClientServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
    
   Client client;
   
    public WebClientServlet() {
       
        try {
           
//           client = new Client("70.12.113.191",8888);
//           String ServerIp="70.12.113.191";
           
           
           
              client = new Client("70.12.231.236", 8888);
                String ServerIp="70.12.231.236";
          

//             client = new Client("70.12.224.85", 8888);
//             String ServerIp="70.12.224.85";
             Msg msg= new Msg(ServerIp,null);
             
          } catch (IOException e) {
             e.printStackTrace();
          }
    
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String tid = request.getParameter("ip");
      String txt = request.getParameter("txt");
      
      
      System.out.println(tid +"--------servletCheck-------"+txt);
      Msg msg= new Msg("fromservlet",txt,tid);
      client.startClient(msg);
      
      
    }
    }

