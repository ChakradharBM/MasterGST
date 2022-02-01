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
			<div class="col-md-4 col-sm-12 m-auto"></div>
              <div class="col-md-4 col-sm-12 m-auto">
                <div class="formbox">
                  <form class="meterialform" id="loginform" name="resetpwd"  action="resetpwd" method="post" role="form">
                    <div class="whitebg">
                      <h2> Reset Password</h2>
                      <!-- serverside error begin -->                    
                      <div class="errormsg"> <c:out value="${error}"/></div>
                      <!-- serverside error end --> 
                      <p class="f-12-gy">Please enter your email address or phone number to login your account.</p>
                      <div class="labletxt astrich">Mail id</div>
                      <div class="form-group"> 
                        <span class="errormsg" id="email_Msg"></span>
                        <input type="email" required="required" id="email" name="email"  placeholder="JaneSmith@mycompnay.com" />
                        <label for="input" class="control-label"></label> <i class="bar"></i> 
                      </div>
                    </div>
                    <div class="bluebg">
						<p><button type="button" onClick="passwordRequest()" class="btn btn-lg btn-blue">Submit</button></p>
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