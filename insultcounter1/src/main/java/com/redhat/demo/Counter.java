package com.redhat.demo;

import java.io.IOException;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CounterServlet", urlPatterns = { "/index.jsp" })

public class Counter extends HttpServlet
  {
    private static final long serialVersionUID = 1L;
    private static int accessCount = 0;
    private static final Random rn = new Random();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
      {
        String next = "/counter.jsp";
        HttpSession session = null;
        String userCount = null;
        
        accessCount++;
        session = request.getSession();
        if(session.getAttribute("userCount") != null)
          {
            userCount = (String)session.getAttribute("userCount");
            userCount = (Integer.parseInt(userCount) + 1) + "";
          }
        else
          userCount = "1";
        
        session.setAttribute("userCount", userCount);
        session.setAttribute("accessCount", accessCount + "");
        session.setAttribute("insult", getInsult());
        
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(next);
        dispatcher.forward(request, response);
      }
    
    private String getInsult()
    {
      String[] verb = {"stink", "run", "laugh", "walk", "stand", "talk", "cry", "waddle", "sound", "fumble", "crawl", "screech", "yelp", "snivel", "whimper", "whine", "sob", "howl", "blubber"};
      String[] noun = {"codfish", "chimpanzee", "duck", "circus clown", "seal", "ringworm", "toad", "hyena", "oaf", "lout", "sphincter", "turd", "goblin", "hobgoblin", "pustule"};
      String[] adjective = {" sweaty", " slimy", " wet", " greasy", " wounded", " confused", " dazed", " well-oiled", " dripping", " gelatinous", "n excited", " frightened", " dejected", " flustered"};
      int verbIndex, nounIndex, adjectiveIndex;
      
      verbIndex = rn.nextInt(verb.length);
      adjectiveIndex = rn.nextInt(adjective.length);
      nounIndex = rn.nextInt(noun.length);
      
      // You {verb} like a {adjective} {noun}!
      return("You " + verb[verbIndex] + " like a" + adjective[adjectiveIndex] + " " + noun[nounIndex] + "!");
    }

  }
