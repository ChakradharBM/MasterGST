<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | India's Best GST Software | Signup</title>
<%@include file="/WEB-INF/views/includes/base_script.jsp" %>
<script>
    (function(h,o,t,j,a,r){
        h.hj=h.hj||function(){(h.hj.q=h.hj.q||[]).push(arguments)};
        h._hjSettings={hjid:2765016,hjsv:6};
        a=o.getElementsByTagName('head')[0];
        r=o.createElement('script');r.async=1;
        r.src=t+h._hjSettings.hjid+j+h._hjSettings.hjsv;
        a.appendChild(r);
    })(window,document,'https://static.hotjar.com/c/hotjar-','.js?sv=');
</script>
<style>body{padding-bottom: 3rem!important;;min-height:90%;}</style>
</head>
<body>
 <%@include file="/WEB-INF/views/includes/login_header.jsp" %>
<div class="bodywrap">
  <div class="container">
    <div class="formboxwrap">
      <h1 class="f-30-l" style="font-size: 1.5rem;text-align:center;color:#8a8b8b;">India's Best GST Software, Easiest way to File GST Returns,<br>
        Manage & Track your Invoices </h1>
      <div class="row">
      
        <div class="col-md-12 col-sm-12 m-auto">
          <!-- signup for all boxes begin -->
          <div class="signup-box-wrap meterialform">
            <h5>Please select whom you want to signup as</h5>
            
			 <div class="signup-box cas">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="cas" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon2.png" alt="CAs / CMAs"> </div>
              <div class="signup-box-txt">
                <h4>CA/CMA/CS/Tax Professional</h4>
              </div>
            </div>
			<!-- end -->
            <div class="signup-box taxPractitioners">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="taxPractitioners" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon3.png" alt="Tax Professional"> </div>
              <div class="signup-box-txt">
                <h4>Suvidha Centers</h4>
              </div>
            </div>
			<!-- end -->
            <div class="signup-box businesses">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="businesses" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon4.png" alt="Businesses"> </div>
              <div class="signup-box-txt">
                <h4>Small/Medium Businesses</h4>
              </div>
            </div>
            <!-- end -->
          
            <div class="signup-box enterprises">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="enterprises" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon5.png" alt="Enterprises"> </div>
              <div class="signup-box-txt">
                <h4>Enterprises</h4>
              </div>
            </div>
			
            <div class="signup-box partner">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="partner" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-icon2.png" alt="​Partners"></div><div class="signup-box-txt"><h4>&nbsp;Partners</h4>
              </div>
            </div>
            <!-- end -->
            <div class="signup-box asp-developers">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="aspdevelopers" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-icon3.png" alt="ASP / Developers​​"> </div>
              <div class="signup-box-txt">
                <h4>ASP<small>s</small>/Developers</h4>
              </div>
            </div>
            <!-- end -->
          </div>
          <!-- signup for all boxes end -->
          <!-- owl carousel begin -->
          <div class="col-md-8 col-sm-12 m-auto">
            <div class="owl-carousel" id="signupSlider">
              <div class="item">
                <div class="signup-comentswrap">
                  <div class="comentsbox">
                    <p>MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                  </div>
                  <!--<div class="coments-user"> <img src="${contextPath}/static/mastergst/images/gst-partner-enrollment/user.jpg" alt="User" class="rounded-circle" width="42" height="42" /><span>SHRINIVAS MOHARIR, CEO, BVM Consultancy Services</span> </div>-->
                </div>
              </div>
              <!-- end -->
              
              <!-- end -->
            </div>
          </div>
          <!-- owl carousel end -->
        </div>
        <!-- comment icon begin -->
        <div class="comenticon"> <img src="${contextPath}/static/mastergst/images/gst-partner-enrollment/icon3.png" alt="Comment" /> </div>
        <!-- comment icon end -->
      </div>
    </div>
  </div>
</div>

<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<script src="${contextPath}/static/mastergst/js/signups/signups.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/signups/owl-scripts.js" type="text/javascript"></script>
<script type="text/javascript">
	var inviteId = '<c:out value="${inviteId}"/>';
	$(function () {
		$('.customers').on("click", function() {
			  window.location.href = 'signup?type=customer&subscrid=&inviteId='+inviteId;
		});
		$('.partner').on("click", function() {
			  window.location.href = 'signup?type=partner&subscrid=&inviteId='+inviteId;
		});
		$('.asp-developers').on("click", function() {
			  window.location.href = 'signup?type=aspdeveloper&subscrid=&inviteId='+inviteId;
		});
		$('.consumer').on("click", function() {
			  window.location.href = 'signup?type=consumer&subscrid=&inviteId='+inviteId;
		});
		$('.cas').on("click", function() {
			  window.location.href = 'signup?type=cacmas&subscrid=&inviteId='+inviteId;
		});
		$('.taxPractitioners').on("click", function() {
			  window.location.href = 'signup?type=suvidha&subscrid=&inviteId='+inviteId;
		});
		$('.businesses').on("click", function() {
			  window.location.href = 'signup?type=business&subscrid=&inviteId='+inviteId;
		});
		$('.enterprises').on("click", function() {
			  window.location.href = 'signup?type=enterprise&subscrid=&inviteId='+inviteId;
		});
	});
</script>
</body>
</html>
