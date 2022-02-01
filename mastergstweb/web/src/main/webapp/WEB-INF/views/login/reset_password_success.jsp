<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
  <head>
	<%@include file="/WEB-INF/views/includes/script.jsp" %>
  </head>
  <body>
     <!-- header page begin -->
    <%@include file="/WEB-INF/views/includes/login_header.jsp" %>
    <!-- header page end -->
	
    <div class="bodywrap">
      <div class="container">
        <div class="row">
          <!-- login form begin -->
          <div class="formboxwrap">
           <h3> Filing GST Made Simple, & Pay your Tax easily </h3>
            <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
			<div class="col-md-3 col-sm-12 m-auto"></div>
             <div class="col-md-6 col-sm-12 m-auto">
				   <div class="formbox">
                  <form class="meterialform">
                     <div class="whitebg">
                        <h2> Reset Password</h2>
                           <!-- serverside error begin -->                    
                    <div class="errormsg"> </div> 
                    <!-- serverside error end --> 
                        <div class="col-xs-12 p-4">
                           <p class="alert sucess-txt">Your Password has been reset
                              successfully.
                           </p>
                        </div>
                     </div>
                     <div class="bluebg text-center pb-2">
                        <a href="login" class="btn btn-lg btn-blue">Continue to Login</a>
                     </div>
                  </form>
               </div>         
            </div>
			 <div class="col-md-3 col-sm-12 m-auto"></div>
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
 