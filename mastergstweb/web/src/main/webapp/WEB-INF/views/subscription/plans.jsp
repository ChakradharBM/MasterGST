<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Subscription</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/subscriptions/subscriptions.css" media="all" />
<style> 
.db-ca-wrap{padding-top:50px!important}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>

<div class="db-ca-wrap mt-3">
  <div class="container">
    <!-- Dashboard body start -->
    <div class="row">
      <!-- dashboard left block begin -->
      <div class="col-md-12 col-sm-12 subscriptwrap">
         <div class="">
                <!-- Nav tabs
                <ul class="nav nav-tabs nav-justified" role="tablist">
                  <li class="nav-item"> <a class="nav-link active" data-toggle="tab" href="#pricenav1" role="tab">Monthly</a> </li>
                  <li class="nav-item"> <a class="nav-link" data-toggle="tab" href="#pricenav2" role="tab">6 Months</a> </li>
                  <li class="nav-item"> <a class="nav-link" data-toggle="tab" href="#pricenav3" role="tab">12 Months</a> </li>
                </ul> -->
                <!-- Tab panes -->
                <div class="tab-content">
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
                           <p class="alert sucess-txt" style="text-align:justify;">Thank you  for your interest in MasterGST GST Software,<br/> For Subscription Please contact our Sales team.<br/>You can reach us at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992. </p>
                        </div>
                     </div>
                  </form>
              </div>
            </div>
            <div class="col-md-4 col-sm-12 m-auto"></div>
		  </div>
          <!-- login form end -->
        </div>
                  <!--<div class="tab-pane active" id="pricenav1" role="pricenav1">
                    <div class="pricing pricing-box">
                      <c:forEach items="${plans}" var="plan">
					  	<div class="pricing-item">
					    <h3 class="pricing-title">${plan.name}</h3>
                        <div class="pricing-price"><span class="pricing-currency"><i class="fa fa-rupee"></i></span><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${plan.price}" /></div>
						<c:if test="${pType ne 'AddOn'}">
                        <p class="pricing-sentence">Perfect for ${plan.name} Organizations</p>
                        <ul class="pricing-feature-list">
                          <li><span  class="pricing-feature"> Unlimited Users</span></li>
                          <li><span  class="pricing-feature">${plan.invoice} Invoices</span></li>
						  <c:if test="${plan.client eq 0}">
                          <li><span  class="pricing-feature"> Unlimited Business/GSTN</span></li>
						  </c:if>
						  <c:if test="${plan.client ne 0}">
                          <li><span  class="pricing-feature"> ${plan.client} Business/GSTN</span></li>
						  </c:if>
						  <li><span  class="pricing-feature"> Unlimited Notifications</span></li>
                        </ul>
						</c:if>
                        <c:if test="${pType eq 'AddOn'}">
                        <p class="pricing-sentence"></p>
                        <ul class="pricing-feature-list">
                          <li><span  class="pricing-feature">${plan.invoice} Invoices</span></li>
						  <li><span  class="pricing-feature"> Valid till Subscription End Date</span></li>
                        </ul>
						</c:if>
                        <div class="pricing-footer">
                          <button link='${contextPath}/sbscrrvw/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${plan.id}/${pType}/${month}/${year}' class="pricing-action selectplan">Choose plan</button>
                        </div>
                      </div>
					  </c:forEach>
                    </div>
                  </div>-->
                  <!-- <div class="tab-pane" id="pricenav2" role="pricenav2">
                    <div class="pricing pricing-box">
                      <c:forEach items="${plans}" var="plan">
					  <c:if test="${plan.duration eq 6}">
						<div class="pricing-item">
					    <h3 class="pricing-title">${plan.name}</h3>
                        <div class="pricing-price"><span class="pricing-currency"><i class="fa fa-rupee"></i></span>${plan.price}<span class="sep">/</span><span class="small">month</span></div>
                        <p class="pricing-sentence">Perfect for ${plan.name}</p>
                        <ul class="pricing-feature-list">
                          <li><span  class="pricing-feature"> Unlimited Users</span></li>
                          <li><span  class="pricing-feature">${plan.invoice} Invoices Per Month</span></li>
                          <li><span  class="pricing-feature"> One Business/GSTN</span></li>
                          <li><span  class="pricing-feature"> Unlimited Notifications</span></li>
                        </ul>
                        <div class="pricing-footer">
                          <button link='${contextPath}/sbscrrvw/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${plan.id}/${month}/${year}' class="pricing-action selectplan">Choose plan</button>
                        </div>
                      </div>
					  </c:if>
					  </c:forEach>
                      <div class="pricing-item">
                        <h3 class="pricing-title">Enterprises</h3>
                        <div class="pricing-price f-24 mb-4">Contact  sales<span class="sep">/</span><span class="small">month</span></div>
                        <p class="pricing-sentence">Comfortable for Enterprises</p>
                        <ul class="pricing-feature-list">
                          <li><span  class="pricing-feature"> Unlimited Users</span></li>
                          <li><span  class="pricing-feature">60001 and Above Invoices</span></li>
                          <li><span  class="pricing-feature"> One Business/GSTN</span></li>
                          <li><span  class="pricing-feature"> Unlimited Notifications</span></li>
                        </ul>
                        <div class="pricing-footer">
                          <button class="pricing-action selectplan">Choose plan</button>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="tab-pane" id="pricenav3" role="pricenav3">
                    <div class="pricing pricing-box">
                      <c:forEach items="${plans}" var="plan">
					  <c:if test="${plan.duration eq 12}">
						<div class="pricing-item">
					    <h3 class="pricing-title">${plan.name}</h3>
                        <div class="pricing-price"><span class="pricing-currency"><i class="fa fa-rupee"></i></span>${plan.price}<span class="sep">/</span><span class="small">month</span></div>
                        <p class="pricing-sentence">Perfect for ${plan.name}</p>
                        <ul class="pricing-feature-list">
                          <li><span  class="pricing-feature"> Unlimited Users</span></li>
                          <li><span  class="pricing-feature">${plan.invoice} Invoices Per Month</span></li>
                          <li><span  class="pricing-feature"> One Business/GSTN</span></li>
                          <li><span  class="pricing-feature"> Unlimited Notifications</span></li>
                        </ul>
                        <div class="pricing-footer">
                          <button link='${contextPath}/sbscrrvw/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${plan.id}/${month}/${year}' class="pricing-action selectplan">Choose plan</button>
                        </div>
                      </div>
					  </c:if>
					  </c:forEach>
                      <div class="pricing-item">
                        <h3 class="pricing-title">Enterprises</h3>
                        <div class="pricing-price f-24 mb-4">Contact  sales<span class="sep">/</span><span class="small">month</span></div>
                        <p class="pricing-sentence">Comfortable for Enterprises</p>
                        <ul class="pricing-feature-list">
                          <li><span  class="pricing-feature"> Unlimited Users</span></li>
                          <li><span  class="pricing-feature">60001 and Above Invoices</span></li>
                          <li><span  class="pricing-feature"> One Business/GSTN</span></li>
                          <li><span  class="pricing-feature"> Unlimited Notifications</span></li>
                        </ul>
                        <div class="pricing-footer">
                          <button class="pricing-action selectplan">Choose plan</button>
                        </div>
                      </div>
                    </div>
                  </div> -->
                </div>
              </div>
      </div>
    </div>
  </div>
</div>
</div>

<!-- footer begin here -->

 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->

<script type="text/javascript">
	$('#nav-billing').addClass('active');
	$('.selectplan').on('click', function(){
		$('.pricing-item.active .selectplan').text('Select Plan');
		$(this).closest('.pricing-item').addClass('active').siblings().removeClass('active');
		$('.pricing-item.active .selectplan').text('Selected');
		window.location.href = $(this).attr('link');
	});
	$('.pricing-box .pricing-item:first-child').addClass('active');
	$('.pricing-box .pricing-item').hover(function(){
		$(this).addClass('active').siblings().removeClass('active');
	});
	$('.btn.btn-primary').removeClass('prevstep');
	$('.stepwizard-step .btn.btn-circle').on('click', function(){
		$(this).parent().prevAll().addClass('prevstep');
		$(this).parent().nextAll().removeClass('prevstep');
		$(this).parent().removeClass('prevstep');
	});
</script>
</body>
</html>
