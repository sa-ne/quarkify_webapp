# JSP Support in Quarkus

Quarkus does not have native support for JSPs.  That being said, Quarkus does provide servlet support.  Since JSP technology relies on servlet architecture, it is relatively straight forward to implement the majority of JSP functionality.  Note that this code does not endeavor to implement all traditional JSP tag support.  For instance artifacts such as taglibs require additional supporting framework componentry.  The objective here is to provide "***just-enough***" JSP capability.

## Specification reference:

The specification used to implement behavior was based on [JSP 2.3](https://download.oracle.com/otn-pub/jcp/jsp-2_3-mrel2-eval-spec/JSP2.3MR.pdf).

## Basic JSP Translation

As we parse the JSP for translation to a servlet we have to address implicit objects that JSP logic will expect to exist as well as interpreting of tag artifacts. 

### Implicit JSP Objects:

| Object Reference | Type |
| ----------- | ----------- |
| **application**| ServletContext |
| **config** | ServletConfig |
| **out** | PrintWriter |
| **page** | Object |
| **session** | HttpSession |
| **exception** | Throwable |

### Logic and Assignment Tags

| Name | Opening Markup Element |
| ----------- | ----------- |
| **Scriptlet tag** | <% |
| **Expression tag** | <%= |
| **Declaration tag** | <%! |

### Directive Tags

	<%@ include file = "relative url" >
	<%@ page attribute = "value" %>

The attribute details for the ***page*** tag are:

| Attribute | Status |
| ----------- | ----------- |
| language | ignored |
| contentType | implemented |
| extends | implemented |
| import | implemented |
| info | implemented |
| session | implemented |
| errorPage | implemented |
| isErrorPage | implemented |

## Quarkus Considerations:

### Request Path Differences:

Running in Quarkus there are going to be some differences.  For instance in a traditional java web application you have a notion of path dictated by the servlet context.  This is usually referred to as the context root.  As the name implies this establishes the relative root for servlet paths.  

	http://[server][port]/[context-root][servlet-path]

Quarkus does not apply a context root path as a convention by default.  Instead applications will start at:

	http://[server][port]/[servlet-path]
    
You can add back the context root using one of two Quarkus config

	quarkus.servlet.context-path=/abc
	quarkus.http.root-path=/xyz

**quarkus.servlet.context-path** allows you to set the root context path for servlets, however note that this applies only to servlets.  The rest of your application resources will not benefit from this change.  You may want to consider instead to use **quarkus.http.root-path**.  This works more indescriminantly across the application.

### Quarkus Active Profile Awareness
The JSP support implementation is aware of the selected runtime profile.  Specifically it will detect if you are running in *dev* mode and leverage Quarkus' ***Live Coding*** feature.  Note that since this will require a reload of the runtime, state information will be reset.  For example if you were looking for something in the HttpServletSession but Quarkus detects that you made a change to the JSP (triggering the reload), those values stored in the session would be null when the JSP loads (right after a Quarkus reload).  

It's not a bug, it's a feature!

