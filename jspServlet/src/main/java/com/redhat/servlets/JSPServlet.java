package com.redhat.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.runtime.configuration.ProfileManager;

public class JSPServlet extends HttpServlet
  {

    private static final long   serialVersionUID = 1L;
    private static final String lineSeparator    = System.getProperty("line.separator");
    private static final String imports          = "import java.io.IOException;\n" + "import java.io.PrintWriter;\n\n" + "import javax.servlet.RequestDispatcher;\nimport javax.servlet.ServletConfig;\n" + "import javax.servlet.ServletContext;\n" + "import javax.servlet.ServletException;\n" + "import javax.servlet.annotation.WebServlet;\n" + "import javax.servlet.http.HttpServlet;\n"
        + "import javax.servlet.http.HttpServletRequest;\n" + "import javax.servlet.http.HttpServletResponse;\n" + "import javax.servlet.http.HttpSession;\n";
    private static final String JSPObjectsInit   = "        ServletContext application = null;\n" + "        ServletConfig config = null;\n" + "        PrintWriter out = null;\n" + "        Object page = null;\n" + "        //PageContext pageContext = null;\n" + "        HttpSession session = request.getSession();\n" + "        String contentType = \"text/html\"; //initial\n"
        + "        String errorPage = null; //initial\n" + "        //isErrorPage\n" + "        \n" + "        application = request.getServletContext();\n" + "        config = getServletConfig();\n" + "        out = response.getWriter();\n" + "        page = this;\n" + "        response.setContentType(contentType);\n" + "        // setCharacterEncoding\n";

    private static final String prefix               = "package com.redhat.servlets.jsp;\n\n" + imports
        + "\n\n@WebServlet(\n    name = \"<JSP_NAME>\",\n    urlPatterns = {\"/<JSP_NAME>\"}\n  )\npublic class <JSP_NAME> extends HttpServlet //initial\n  {\n    private static final long serialVersionUID = 1L;\n//<DECLARATIONS>\n//<SERVLETINFO>\n    @Override\n    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException\n      {\n"
        + JSPObjectsInit + "\n\n        try\n" + "          {\n";
    private static final String suffix               = "          }\n" + "        catch(Exception ex)\n" + "          {\n" + "            if(errorPage != null)\n" + "              {\n" + "                request.setAttribute(\"javax.servlet.error.exception\", ex);\n" + "            \n" + "                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(errorPage);\n"
        + "                dispatcher.forward(request, response);\n" + "              }\n" + "            else\n" + "              throw new ServletException(ex);\n" + "          }\n" + "      }\n  }";
    private static final String servletInfo          = "\n    @Override\n    public String getServletInfo()\n      {\n        return(\"<SERVLETINFO>\");\n      }\n";
    private static final String _QUARKUS_PROFILE_DEV = "dev";
    // private static final String jspBasePath = "classes" + File.separator + "META-INF" + File.separator + "resources" + File.separator;
    private static final String jspBasePath = ".." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "META-INF" + File.separator + "resources" + File.separator;

    private static boolean isQuarkusDevMode = false;
    private static String  rootPath         = null;
    private static String  servletCtxPath   = null;

    @Override
    public void init() throws ServletException
      {
        String profileName = null;

        super.init();
        profileName = ProfileManager.getActiveProfile();
        isQuarkusDevMode = (profileName.equals(_QUARKUS_PROFILE_DEV));
      }

    private static String getConfig(String key)
      {
        String value;

        try
          {
            value = ConfigProvider.getConfig().getValue(key, String.class);
          }
        catch(NoSuchElementException ex)
          {
            value = "";
          }

        return(value);
      }

    private boolean hasContentChanged(String servletPath, String jspPath)
      {
        boolean changed = false;

        try
          {
            Path file = null;
            BasicFileAttributes servletAttr = null;
            BasicFileAttributes jspAttr = null;
            FileTime jspMod, servletMod;

            file = Paths.get(jspPath);
            if(Files.exists(file))
              {
                jspAttr = Files.readAttributes(file, BasicFileAttributes.class);
                file = Paths.get(servletPath);
                if(Files.exists(file))
                  {
                    servletAttr = Files.readAttributes(file, BasicFileAttributes.class);
                    jspMod = jspAttr.lastModifiedTime();
                    servletMod = servletAttr.lastModifiedTime();
                    if(servletMod.compareTo(jspMod) < 0)
                      changed = true;
                  }
                else
                  changed = true;
              }
            else
              changed = true;
          }
        catch(IOException e)
          {
            e.printStackTrace();
          }
        return(changed);
      }

    private String readCode(String sourcePath) throws FileNotFoundException
      {
        String code = null;
        InputStream stream = new FileInputStream(sourcePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        code = reader.lines().collect(Collectors.joining(lineSeparator));

        try
          {
            reader.close();
          }
        catch(IOException ex)
          {
          }

        return(code);
      }

    private String codify(String content, String jspPath, String modPrefix, boolean addSuffix)
      {
        Path file = null;
        String includeContent = null;
        String includePath = null;
        String parsed = "";
        String code = "";
        String declarations = "";
        String directive = "";
        String[] lines = null;
        String line = null;
        Vector<String> directives = null;
        int index1, index2, lastSeparator;
        boolean isCode = false;
        int type = 0;

        directives = new Vector<String>();
        lines = content.split("\n");

        for(int i = 0; i < lines.length; i++)
          {
            line = lines[i] + "\\n";
            while((index1 = line.indexOf("<%")) != -1)
              {
                code += genOutput(line.substring(0, index1), isCode, type);
                isCode = true;
                line = line.substring(index1 + 2);
                if(line.startsWith("="))
                  {
                    type = 1;
                    line = line.substring(1);
                  }
                else if(line.startsWith("!"))
                  {
                    type = 2;
                    line = line.substring(1);
                  }
                else if(line.startsWith("@"))
                  {
                    type = 3;
                    line = line.substring(1).trim();
                  }

                if((index1 = line.indexOf("%>")) != -1)
                  {
                    if(type < 2)
                      code += genOutput(line.substring(0, index1), isCode, type);
                    else if(type == 2)
                      declarations += genOutput(line.substring(0, index1), isCode, type);
                    else if(type == 3)
                      {
                        directive += line.substring(0, index1).trim();
                        if(directive.startsWith("include") && (index2 = directive.indexOf('=')) != -1)
                          {
                            directive = directive.substring(index2 + 1).trim();
                            includePath = jspPath + directive.substring(1, directive.length() - 1);
                            file = Paths.get(includePath);
                            if(Files.exists(file))
                              {
                                try
                                  {
                                    includeContent = readCode(includePath);
                                  }
                                catch(FileNotFoundException ex)
                                  {
                                  }
                                lastSeparator = includePath.lastIndexOf(File.separator.charAt(0));
                                code += codify(includeContent, includePath.substring(0, lastSeparator + 1), modPrefix, false);
                              }
                            System.out.println(includePath);
                          }
                        else
                          directives.add(directive);
                        directive = "";
                      }
                    isCode = false;
                    type = 0;
                    line = line.substring(index1 + 2);
                  }
              }

            if((index1 = line.indexOf("%>")) != -1)
              {
                if(type < 2)
                  code += genOutput(line.substring(0, index1), isCode, type);
                else if(type == 2)
                  declarations += genOutput(line.substring(0, index1), isCode, type);
                else if(type == 3)
                  {
                    directive += line.substring(0, index1).trim();
                    directives.add(directive);
                    directive = "";
                  }

                isCode = false;
                type = 0;
                line = line.substring(index1 + 2);
              }

            if((line.trim().length() > 0) && (!"\\n".equals(line.trim())))
              {
                if(type < 2)
                  code += genOutput(line, isCode, type);
                else if(type == 2)
                  declarations += genOutput(line, isCode, type);
                else if(type == 3)
                  {
                    if(line.endsWith("\\n"))
                      line = line.substring(0, line.length() - 2);
                    directive += line.trim() + " ";
                  }

              }
          }

        if(addSuffix)
        parsed = processJspDirectives(directives, modPrefix).replaceFirst("//<DECLARATIONS>", "\n" + declarations + "\n//<DECLARATIONS>") + code + suffix;
        else
          parsed = code;

        return(parsed);
      }

    private String processJspDirectives(Vector<String> directives, String modPrefix)
      {
        Iterator<String> it = null;
        HashMap<String, String> map = null;

        if((directives != null) && !directives.isEmpty())
          {
            it = directives.iterator();
            while(it.hasNext())
              {
                map = parseJspDirective(it.next());
                if(map.get("directiveType").equals("page"))
                  modPrefix = processJspDirectivePage(map, modPrefix);
              }
          }

        return(modPrefix);
      }

    private String processJspDirectivePage(HashMap<String, String> attr, String modPrefix)
      {
        Iterator<String> it = null;
        String key, value = null;
        String info = "";
        String[] imports = null;

        if(attr != null)
          {
            it = attr.keySet().iterator();
            while(it.hasNext())
              {
                key = it.next();
                if("info".equals(key))
                  {
                    value = attr.get(key);
                    info = servletInfo.replaceFirst("<SERVLETINFO>", value);
                    modPrefix = modPrefix.replaceFirst("//<SERVLETINFO>", info);
                  }
                else if("contentType".equals(key))
                  {
                    value = attr.get(key);
                    modPrefix = modPrefix.replaceFirst("String contentType = \"text/html\"; //initial", "String contentType = \"" + value + "\";");
                  }
                else if("import".equals(key))
                  {
                    value = attr.get(key);
                    if(value.indexOf(',') != -1)
                      {
                        imports = value.split(",");
                        value = "";
                        for(int i = 0; i < imports.length; i++)
                          value += "import " + imports[i].trim() + ";\n";
                      }
                    else
                      value = "import " + value + ";\n";
                    modPrefix = modPrefix.replaceFirst("import ", value + "import ");
                  }
                else if("errorPage".equals(key))
                  {
                    value = attr.get(key);
                    modPrefix = modPrefix.replaceFirst("String errorPage = null; //initial", "String errorPage = \"" + value + "\";");
                  }
                else if("isErrorPage".equals(key))
                  {
                    value = attr.get(key);
                    if("true".equals(value))
                      modPrefix = modPrefix.replaceFirst("//isErrorPage", "Throwable exception = (Throwable)request.getAttribute(\"javax.servlet.error.exception\");");
                  }
                else if("extends".equals(key))
                  {
                    value = attr.get(key);
                    modPrefix = modPrefix.replaceFirst("extends HttpServlet //initial", "extends " + value);
                  }
                else if("session".equals(key))
                  {
                    value = attr.get(key);
                    if("false".equals(value))
                      modPrefix = modPrefix.replaceFirst("        HttpSession session =", "        HttpSession session  = null; //");
                  }
                else if("pageEncoding".equals(key))
                  {
                    value = attr.get(key);
                    modPrefix = modPrefix.replaceFirst("// setCharacterEncoding", "response.setCharacterEncoding(\"" + value + "\");");
                  }
              }
          }

        return(modPrefix);
      }

    private HashMap<String, String> parseJspDirective(String directive)
      {
        int index1, index2, splitLength;
        String attribute = null;
        String value = null;
        String[] attributeInfo = null;
        HashMap<String, String> attributes = new HashMap<String, String>();

        if(directive.startsWith("page"))
          {
            directive = directive.substring("page".length()).trim();
            attributes.put("directiveType", "page");
          }
        else if(directive.startsWith("include"))
          {
            directive = directive.substring("include".length()).trim();
            attributes.put("directiveType", "include");
          }

        while(directive.indexOf('=') != -1)
          {
            if((index1 = directive.indexOf('"')) != -1) // first '"'
              if((index2 = directive.indexOf('"', index1 + 1)) != -1) // second '"'
                {
                  // omitting enclosing double quotes
                  attribute = directive.substring(0, index2 + 1);
                  directive = directive.substring(index2 + 1).trim();
                  attributeInfo = attribute.split("=");
                  if(attributeInfo != null)
                    {
                      // compensate for attribute values that contain '='
                      splitLength = attributeInfo.length;
                      if(splitLength > 2)
                        {
                          value = attributeInfo[1];
                          for(int i = 2; i < splitLength; i++)
                            value += "=" + attributeInfo[i];
                        }
                      else
                        value = attributeInfo[1];

                      value = value.trim();
                      attributes.put(attributeInfo[0].trim(), value.substring(1, value.length() - 1).trim());
                    }
                }
          }

        return(attributes);
      }

    private String genOutput(String content, boolean tag, int type)
      {
        String output = "";

        if(content != null)
          {
            if((content.length() > 0) && (content.isBlank()))
              content = " ";
            if((type == 0) && !tag && (content.contains("\"")))
              content = content.replaceAll("\"", "\\\\\"");
              
//            if((content.trim().length() > 0) && (!"\\n".equals(content.trim())))
//              {
                if((type == 1) || tag)
                  {
                    if(content.endsWith("\\n"))
                      content = content.substring(0, content.length() - 2);

                    content = content.trim();
                    if(content.length() > 0)
                      if(type == 1)
                        output = "            out.print(" + content + ");\n";
                      else if(type == 2)
                        output = "    " + content + "\n";
                      else
                        output = "            " + content + "\n";
                  }
                else
                  output = "            out.print(\"" + content + "\");\n";
//              }
          }

        return(output);
      }

    private String makeClassPackage(String packageName)
      {
        File file = null;
        // String path = "classes" + File.separator;
        String path = ".." + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;

        path += packageName.replace('.', File.separator.charAt(0));

        if(!Files.isDirectory(Paths.get(path)))
          {
            file = new File(path);
            file.mkdirs();
          }
        return(path);
      }

    private Path saveSource(String source, String path, String filename) throws IOException
      {
        Path sourcePath = FileSystems.getDefault().getPath(path, filename);
        Files.write(sourcePath, source.getBytes(StandardCharsets.UTF_8));

        try
          {
            // delay to allow for recompile before servlets get re-registered
            Thread.sleep(2000L);
          }
        catch(InterruptedException ex)
          {
          }

        return sourcePath;
      }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
      {
        PrintWriter out = null;
        Path file = null;
        String path = makeClassPackage("com.redhat.servlets.jsp");

        String fullPath = request.getRequestURI();
        String jspServlet = null;
        String jspFullPath = jspBasePath;
        String jspRelPath = null;
        String pathPrefix = null;

        rootPath = getConfig("quarkus.http.root-path");
        servletCtxPath = getConfig("quarkus.servlet.context-path");
        pathPrefix = rootPath + servletCtxPath + "/";
        jspRelPath = fullPath.substring(pathPrefix.length());
        if(!File.separator.equals("/"))
          jspRelPath = jspRelPath.replace('/', File.separator.charAt(0));

        jspFullPath += jspRelPath;
        jspServlet = jspRelPath.replace("/", "__").replace('.', '_');

        if(!isQuarkusDevMode || !hasContentChanged(path + File.separator + jspServlet + ".java", jspFullPath))
          {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/" + jspServlet);
            dispatcher.forward(request, response);
          }
        else
          {
            file = Paths.get(jspFullPath);
            if(Files.exists(file))
              {
                String content = readCode(jspFullPath);
                int lastSeparator = jspFullPath.lastIndexOf(File.separator.charAt(0));
                String parsed = codify(content, jspFullPath.substring(0, lastSeparator + 1), prefix, true);

                parsed = parsed.replaceAll("<JSP_NAME>", jspServlet);
                saveSource(parsed, path, jspServlet + ".java");

                // changes to JSPs require reloading the servlet container so redirect is best approach
                response.sendRedirect(pathPrefix + jspRelPath);
              }
            else
              {
                // set HTTP response code
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                // Set response content type
                response.setContentType("text/html");

                out = response.getWriter();
                out.println("<head><title>404 Not Found</title><body bgcolor=\"lightgray\"><br><br><br><br><br><center><h1>404</h1><h2>Not Found</h2><br /><hr /><br />Quarkus</center></body>");
              }
          }
      }
  }
