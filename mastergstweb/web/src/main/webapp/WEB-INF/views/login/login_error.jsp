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
			<li class="nav-item">
              <a class="nav-link" href="${contextPath}/logout"> Logout</a>
			</li>
			
            <li class="nav-item circle">
              <a class="nav-link" href="tel:+91 7901022478" data-toggle="tooltip" data-placement="left" title="+91 7901022478"> <img src="${contextPath}/static/mastergst/images/master/phone-icon.png" alt="Phone" /> </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <!-- header page end -->
	
    <div class="bodywrap">
      <div class="container">
        <div class="row">
          <!-- login form begin -->
          <div class="formboxwrap">
           <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> India's Best GST Software, We Simplify GST Return Filing </h1>
            <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
			<div class="col-md-2 col-sm-12 m-auto"></div>
            <div class="col-md-8 col-sm-12 m-auto">
              <div class="formbox">
                <form class="meterialform">
                     <div class="whitebg">
                        <h2>Welcome Message</h2>
                           <!-- serverside error begin -->                    
                    <!-- serverside error end --> 
                        <div class="col-xs-12 p-4">
                           <p class="alert sucess-txt" style="text-align:justify;">Thank you  for your interest in MasterGST GST Software, Our sales team will contact you for enabling the access to Suvidha Centers along with initial training and onboarding process.<br/><br/>You can reach us at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992. </p>
                        </div>
                     </div>
                  </form>
              </div>
            </div>
            <div class="col-md-4 col-sm-12 m-auto"></div>
		  </div>
          <!-- login form end -->
        </div>
      </div>
    </div>
     <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
  </body>
</html> 
 