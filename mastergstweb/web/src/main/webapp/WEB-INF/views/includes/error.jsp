<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<%@include file="/WEB-INF/views/includes/script.jsp" %>
</head>
  <body>
    <!-- header page begin -->
    <nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse">
      <!-- toggle button for device begin -->
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
      <!-- toggle button for device end -->
      <a class="navbar-brand" href="https://www.mastergst.com" target="_blank">
      <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst" />
      </a>
      <div class="collapse navbar-collapse" id="navbarCollapse">
        <div class="mainnav-wrap">
          <ul class="navbar-nav mainnav-right">
	
			
			
            
          </ul>
        </div>
      </div>
    </nav>
    <!-- header page end -->
  
	<div class="bodywrap">
      <div class="container">
        <div class="row">
			
        <div class="notfound_wrap col-12">
	
		<div class="clearfix">
			<center><img src="${contextPath}/static/mastergst/images/master/404-img.png" alt="404"></center>
		</div>
		<h4>Looks like's you've got lost</h4>
			<h5>The page you're looking for doesn't exist or has moved</h5>
				<div class="clearfix"><a href="http://app.mastergst.com/login" class="btn btn-white  mt-4">GO HOME</a></div>
		</div>
		
        </div>
      </div>
    </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
  </body>
</html>