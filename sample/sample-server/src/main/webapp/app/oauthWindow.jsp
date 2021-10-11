<%@ page import="java.util.Arrays" %>
<%@ page import="org.apache.http.client.utils.URIBuilder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="false" %>
<!doctype html>
<html>
<head>
    <script type="text/javascript">
      <%
      URIBuilder uriBuilder = new URIBuilder();
      request.getParameterMap().forEach((key, values) -> Arrays.stream(values)
              .forEach(value -> uriBuilder.addParameter(key, value)));
      %>
      if (window.opener) {
        window.opener.postMessage('<%=uriBuilder.toString()%>', location.origin);
      } else {
        document.body.innerText =
            "Your browser seems to be stopping this window from communicating with the main window.";
      }
    </script>
</head>
<body></body>
</html>
