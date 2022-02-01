<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<<%@include file="/WEB-INF/views/includes/script.jsp" %>
<style>body{padding-bottom: 3rem!important;;min-height:90%;}</style>
</head>
<body>
 <%@include file="/WEB-INF/views/includes/login_header.jsp" %>
<div class="bodywrap">
  <div class="container">
    <div class="formboxwrap m-auto">
      <h3 class="f-30-l">The Easiest way to Pay Gst Tax ,<br>
        Manage & Track your  Goods and services </h3>
      <div class="row">
      <div class="col-md-1 col-sm-12"><a href="signupall?subscrid=&inviteId="  class="arrowPrev"><img src="${contextPath}/static/mastergst/images/signups/arrow-lg.png" alt="arrow"></a></div>
        <div class="col-md-10 col-sm-12">
          <!-- signup for all boxes begin -->
          <div class="signup-box-wrap meterialform">
          <h4>Your Selected Option: <strong>Customers</strong></h4>
           <h5>Please select whom you want to signup as</h5>
            <div class="signup-box consumer">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <label>
                    <input name="radio" id="consumer" type="radio">
                    <i class="helper"></i></label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon1.png" alt="Consumer"> </div>
              <div class="signup-box-txt">
                <h4>Consumers</h4>
              </div>
            </div>
            <!-- end -->
            <div class="signup-box cas">
              <div class="signup-img">
                <div class="form-radio">
                  <div class="radio">
                    <input name="radio" id="cas" type="radio">
                    <i class="helper"></i></label>
                    <label>
                  </div>
                </div>
                <!-- end -->
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon2.png" alt="cas"> </div>
              <div class="signup-box-txt">
                <h4>CA<small>s</small>/Experts</h4>
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
                <img src="${contextPath}/static/mastergst/images/signups/signup-inst-icon3.png" alt="Tax Practitioners"> </div>
              <div class="signup-box-txt">
                <h4>Tax Practitioners</h4>
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
                <h4>Businesses</h4>
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
            <!-- end -->
          </div>
          <!-- signup for all boxes end -->
          <!-- owl carousel begin -->
          <div class="col-md-9 col-sm-12 m-auto">
            <div class="owl-carousel" id="signupSlider">
              <div class="item">
                <div class="signup-comentswrap">
                  <div class="comentsbox">
                    <p>MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                  </div>
                  <!--<div class="coments-user"> <img src="${contextPath}/static/mastergst/images/gst-partner-enrollment/user.jpg" alt="User" class="rounded-circle" width="42" height="42" /><span>SHRINIVAS MOHARIR, CEO, BVM Consultancy Services</span> </div>-->
                </div>
              </div>
             
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
<<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<script src="${contextPath}/static/mastergst/js/signups/signups.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/signups/owl-scripts.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function () {
		$('.consumer').on("click", function() {
			  window.location.href = 'signup?type=consumer&inviteId=&subscrid=';
		});
		$('.cas').on("click", function() {
			  window.location.href = 'signup?type=cacmas&inviteId=&subscrid=';
		});
		$('.taxPractitioners').on("click", function() {
			  window.location.href = 'signup?type=taxp&inviteId=&subscrid=';
		});
		$('.businesses').on("click", function() {
			  window.location.href = 'signup?type=business&inviteId=&subscrid=';
		});
		$('.enterprises').on("click", function() {
			  window.location.href = 'signup?type=enterprise&inviteId=&subscrid=';
		});
	});
</script>
</body>
</html>
