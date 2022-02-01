<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<title>MasterGST | India's Best GST Software | Login</title>
	<%@include file="/WEB-INF/views/includes/base_script.jsp" %>
	<style>body{padding-bottom: 3rem!important;min-height:90%;}	</style>
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
            <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> India's Best GST Software, We Simplify GST Return Filing </h1>
            <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
			<div class="col-md-4 col-sm-12 m-auto"></div>
            <div class="col-md-4 col-sm-12 m-auto">
              <div class="formbox">
               
			   <form class="meterialform" id="loginform" name="loginform"  action="signin" method="post" role="form">
                  <div class="whitebg">
                    <h2> Login</h2>
                    <!-- serverside error begin -->                    
                    <div class="errormsg"><c:out value="${error}"/></div> 
                    <!-- serverside error end -->                   
                    <div class="labletxt astrich">Mail id<span class="errormsg" id="EmailId_Msg"></span></div>
                    <div class="form-group"> 
                      <input type="email" required="required" id="EmailId" name="email" data-error="" placeholder="JaneSmith@mycompany.com" onkeypress="return onEnterfunction(event)"/>
                      <label for="input" class="control-label"></label>
                      <i class="bar"></i> </div>
                    <div class="labletxt astrich">Password <span class="errormsg" id="Password_Msg"></span></div> 
                    <div class="form-group">
                      <input type="password" id="Password" name="password" required="required" placeholder="Password" onkeypress="return onEnterfunction(event)" />
                      <label for="input" class="control-label"></label>
                      <i class="bar"></i> </div>
                  </div>
                  <div class="bluebg">
                    <p><button type="button" id="loginbutton" onClick="login()" class="btn btn-lg btn-blue">Login</button></p>
                    <p><a href="reset" id="pwd-req">Reset Password</a></p>
                   
                  </div>
                </form>
              
			  </div>
            </div>
          <div class="col-md-4 col-sm-12 m-auto"></div>
		  </div>
        <!-- login form end -->
        <script>
		function onEnterfunction(event){
			$('.errormsg').html('');
			if(event.keyCode == 13) {
				document.getElementById("loginbutton").click();
			}
		}
	</script>
        </div>
      </div>
    </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
  </body>
</html>