<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Subscription</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/subscriptions/subscriptions.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<style>
.db-ca-wrap{padding-top:75px!important}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>

<div class="db-ca-wrap mt-3">
  <div class="container">
    <!-- Dashboard body start -->
    <div class="">
      <!-- dashboard left block begin -->
      <div class="subscriptwrap">
        <div class="stepwizard-wrap">
          <div class="stepwizard">
            <div class="stepwizard-row setup-panel online2">
              <div class="stepwizard-step"> <a href="#step-1" id="idReview" class="btn btn-default btn-circle disabled" disabled="disabled"><span class="snum">1 Review</span></a>
              </div>
              <div class="stepwizard-step"> <a href="#step-2" id="idMessage" class="btn btn-default btn-circle disabled" disabled="disabled"><span class="snum">2 Thank You Message</span></a>
              </div>
            </div>
          </div>
          <!-- stepy form -->
          <form role="form" data-toggle="validator" action="" class="dbform bs-radio">
            <div class="stepwizard-iner">
            <!-- stepy form 1 -->
            <!-- stepy form 2 -->
            <div class="setup-content" id="step-1">
              <div class="payment_view">
                <div class="row">
                  <div class="col-md-5 col-sm-12">
                    <h4 class="f-14-b text-left">Billing Address</h4>
                  </div>
                  <div class="col-md-7 col-sm-12">
					<div class="td-row form-group"> <span class="td-col-l">Company Name</span> <span class="td-col-r"><input type="text" id="idCompanyName" class="form-control" name="compname" /></span> </div>
                    <div class="td-row form-group"> <span class="td-col-l">GSTN Number</span> <span class="td-col-r"><input type="text" id="idGSTNNumber" name="gstno" pattern="^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" class="form-control" /><div class="help-block with-errors"></div></span> </div>
					<div class="td-row form-group" style="margin-bottom:-6px!important"> <span class="td-col-l">Billing Address</span> <span class="td-col-r"><textarea type="text" id="address" name="address" class="form-control mapicon"> </textarea></span><div class="">&nbsp;</div> </div>
					<div class="td-row form-group"> <span class="td-col-l">State</span> <span class="td-col-r"><input type="text" id="statename" name="statename" class="form-control" required="required" /><div class="help-block with-errors"></div>
					<div id="statenameempty" style="display:none">
						<div class="ddbox">
						  <p>Search didn't return any results.</p>
						</div>
					</div></span> </div>
				  </div>
                </div>
				<div class="btmbdr"></div>
                <div class="row">
                  <div class="col-md-5 col-sm-12">
                    <h4 class="f-14-b text-left">Subscription Details</h4>
                  </div>
                  <div class="col-md-7 col-sm-12">
					<jsp:useBean id="now" class="java.util.Date" />
                    <div class="td-row"> <span class="td-col-l">Subscription Start Date</span> <span class="td-col-r"><fmt:formatDate value="${now}" pattern="dd-MM-yyyy" /></span> </div>
                    <div class="td-row"> <span class="td-col-l">Expiry/Renewal Date</span> <span class="td-col-r"><fmt:formatDate value="${expiryDate}" pattern="dd-MM-yyyy" /></span> </div>
					<div class="td-row"> <span class="td-col-l">Subscription Amount</span> <span class="td-col-r"><i class="fa fa-rupee"></i> <c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*100/118}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount}" /></c:otherwise></c:choose></span> </div>
					<div class="">&nbsp;</div>
                  </div>
                </div>
				<div class="btmbdr"></div>
				<div class="row">
                  <div class="col-md-2 col-sm-12">
                    <h4 class="f-14-b text-left">Coupon Details</h4>
                  </div>
				  <div class="col-md-4 col-sm-12">
                    <div class="td-row form-group"> <span class="td-col-l">Enter Coupon Code</span> <span class="td-col-r" style="width:31%!important"><input type="text" id="idCoupon" class="form-control" name="couponcode" /> </span> <a class="btn btn-blue-dark btn-sm ml-2" type="button" onclick="performCoupon()" style="position:absolute">Apply</a>
					<span class="errormsg" id="couponError_Msg"></span></div>
                  </div>
                  <div class="col-md-6 col-sm-12">
					<div class="td-row disAmount" style="display:none"> <span class="td-col-l ml-2 text-center">Discount Amount</span> <span class="td-col-r rupi" id="discountAmount"><i class="fa fa-rupee"></i> - 0.00</span> </div>
					<!--<div class="td-row disCode" style="display:none"> <span class="td-col-l ml-2 text-center">Applied Coupon Code</span> <span class="td-col-r rupi" id="couponApplied" style="padding-right:16px!important"> </span> </div>-->
					<div class="td-row disCode" style="display:none"> <span class="td-col-l ml-2 text-center couponApplied"></span></div>
				  </div>
                </div>
				<div class="btmbdr"></div>
                <div class="row">
                  <div class="col-md-5 col-sm-12">
                    <h4 class="f-14-b text-left">Payment Details</h4>
                  </div>
                  <div class="col-md-7 col-sm-12">
                    <!-- <div class="td-row"> <span class="td-col-l boldtxt">Coupon Code</span> <span class="td-col-r rupi">(-) <i class="fa fa-rupee"></i> 0</span> </div> -->
                    <div class="td-row"> <span class="td-col-l">Sub Total</span> <span class="td-col-r rupi" id="subTotal"><i class="fa fa-rupee"></i> <c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*100/118}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount}" /></c:otherwise></c:choose></span> </div>
					<div class="td-row inter" style="display:none"> <span class="td-col-l">IGST</span> <span class="td-col-c rupi" id="totalIGST"><i class="fa fa-rupee"></i><span class="ind_format" id="igstAmount"> <c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.18*100/118}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.18}" /></c:otherwise></c:choose></span></span> </div>
					<div class="td-row intra" style="display:none"> <span class="td-col-l">CGST</span> <span class="td-col-c rupi" id="totalCGST"><i class="fa fa-rupee"></i> <span class="ind_format" id="cgstAmount"><c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.09*100/118}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.09}" /></c:otherwise></c:choose></span></span> </div>
					<div class="td-row intra" style="display:none"> <span class="td-col-l">SGST</span> <span class="td-col-c rupi" id="totalSGST"><i class="fa fa-rupee"></i> <span class="ind_format" id="sgstAmount"><c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.09*100/118}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.09}" /></c:otherwise></c:choose></span></span> </div>
					<div class="td-row"> <span class="td-col-l totalGST" style="font-weight:bold">Total GST</span> <span class="td-col-r rupi" id="totalGST"><i class="fa fa-rupee"></i><c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.18*100/118}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*0.18}" /></c:otherwise></c:choose></span> </div>
                  </div>
                </div>
				<div class="btmbdr"></div>
                <div class="row">
                  <div class="col-md-5 col-sm-12"> &nbsp; </div>
                  <div class="col-md-7 col-sm-12">
                    <div class="td-row totalamount"> <span class="td-col-l">Grand Total</span> <span class="td-col-r rupi" id="totalamount"><i class="fa fa-rupee"></i><c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"> <fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*1.18}" /></c:otherwise></c:choose></span> </div>
					 <div class="btmbdr"></div>
				 </div>
                 
                </div>
              </div>
              <div class="row"><div class="col-12" style="display:block">
                <div class="form-group footer-btns text-right"> <a class="btn btn-blue-dark disable" onclick="performPayment()" href='#'>Pay Now</a> <a class="btn btn-blue ml-2" href='${contextPath}/dbllng/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}' type="button">Cancel</a> </div>
              </div></div>
            </div>
            <!-- stepy form 3-->
            <div class="setup-content" id="step-2">
              <div class="col-12 pay-suces text-center" style="display:block">
                <h3>SUCCESS MESSAGE</h3>
                <p>&nbsp;</p>
                <p>Your payment of Rs.<c:choose><c:when test="${paymentlink.rateofinclusivetax eq 'true'}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount}" /></c:when><c:otherwise><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentlink.paidAmount*1.18}" /></c:otherwise></c:choose> is successfully completed. Kindly save the Order Id <b>${order_id}</b> for your reference. </p>
				<p><a class="btn btn-greendark" href='${contextPath}/dbllng/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}'>Continue</a></p>
              </div>
            </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- footer begin here -->

 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->

<script src="${contextPath}/static/mastergst/js/common/stepy-wizards.js" type="text/javascript"></script></head>
<script type="text/javascript">
$(function () {
	$('#nav-billing').addClass('active');
	var status='<c:out value="${type}"/>';
	var username = '<c:out value="${fullname}"/>';
	$('#idCompanyName').val(username);
	if(status == 'Success') {
		$('#idMessage').removeClass('disabled').click();
	} else if(status == 'Review') {
		$('#idReview').removeClass('disabled').click();
	}
	$('.selectplan').on('click', function(){
		$('.pricing-item.active .selectplan').text('Select Plan');
		$(this).closest('.pricing-item').addClass('active').siblings().removeClass('active');
		$('.pricing-item.active .selectplan').text('Selected');
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
	
	$('#statename').on('input',function(e){
		var statename = $('#statename').val();
		statename = statename.toUpperCase();
		if(statename == 'TELANGANA') {
			$('.intra').show();
			$('.inter').hide();
		} else {
			$('.inter').show();
			$('.intra').hide();
		}
		$('.disable').removeClass('disable');
	});
	
	var stateoptions = {
		url: function(phrase) {
			phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");
			return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
		},
		getValue: "name",
		list: {
			onChooseEvent: function() {
				var stateData = $("#statename").getSelectedItemData();
				if(stateData.tin == 36) {
					$('.intra').show();
					$('.inter').hide();
				} else {
					$('.inter').show();
					$('.intra').hide();
				}
				$('.disable').removeClass('disable');
			},
			onLoadEvent: function() {
				if($("#eac-container-statename ul").children().length == 0) {
					//$("#statename").val('');
					$("#statenameempty").show();
				} else {
					$("#statenameempty").hide();
				}
			}
		}
	};
	$("#statename").easyAutocomplete(stateoptions);
	google.maps.event.addDomListener(window, 'load', initialize);
});
function initialize() {
	var address = document.getElementById('address');
	var autocomplete = new google.maps.places.Autocomplete(address);
}
function performPayment() {
	var compname = $("#idCompanyName").val();
	var gstno = $("#idGSTNNumber").val();
	var statename = $("#statename").val();
	var address = $("#address").val();
	var coupon = $("#idCoupon").val();
	if(statename != '') {
		var pymt = new Object();
		pymt.userid = '<c:out value="${id}"/>';
		pymt.name = '<c:out value="${fullname}"/>';
		pymt.usertype = '<c:out value="${usertype}"/>';
		//pymt.planid = '<c:out value="${plan.id}"/>';
		pymt.month = '<c:out value="${month}"/>';
		pymt.year = '<c:out value="${year}"/>';
		coupon = coupon.toUpperCase();
		var coupons = [];
		$.ajax({
			url:"${contextPath}/coupons",
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				for(var i=0;i<response.length;i++) {
					coupons.push(response[i].code);
				}
			}
		});
		var rti = ${paymentlink.rateofinclusivetax};
		var pa =  ${paymentlink.paidAmount};
		if(rti == 'true' || rti == true){
			pa = pa*100/118;
		}
		pa = pa*1.18;
		if(coupon != '') {
			$.ajax({
				url: "${contextPath}/coupons/"+coupon,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					for(var i=0;i<coupons.length;i++){
						if(response.data.code == coupons[i]){
							if(response.data.value){
								var val = response.data.value/100;
								var codeval = 1-val;
								pymt.amount = pa*codeval*1.18;
							}else{
								var val = response.data.disCountValue;
								pymt.amount = (pa-val)*1.18;
							}
						}
					}
				},
					error : function(e, status, error) {
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
			}else{
				pymt.amount = pa;
			}
		}
		pymt.customerName = compname;
		pymt.customerGSTN = gstno;
		pymt.customerPOS = statename;
		pymt.customerAddress = address;
		pymt.couponCode = coupon;
		pymt.discountAmount = $('#disAmount').text().replace(/,/g, "");
		pymt.actualAmount = ${paymentlink.paidAmount};
		statename = statename.toUpperCase();
		if(statename == 'TELANGANA' || statename == '36-TELANGANA') {
			pymt.sgstAmount = $('#sgstAmount').text().replace(/,/g, "");
			pymt.cgstAmount = $('#cgstAmount').text().replace(/,/g, "");
		}else{
			pymt.igstAmount = $('#igstAmount').text().replace(/,/g, "");
		}
		$.ajax({
			type: "POST",
			url: "${contextPath}/prfmpayment",
			async: false,
			cache: false,
			data: JSON.stringify(pymt),
			contentType: 'application/json',
			success : function(reqId) {
				location.href="${contextPath}/ccavreqp/"+reqId+"/"+pymt.amount;
			},
			error : function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}


function performCoupon() {
	var coupon = $("#idCoupon").val();
	coupon = coupon.toUpperCase();
	var coupons = [];
	$.ajax({
		url:"${contextPath}/coupons",
		async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				for(var i=0;i<response.length;i++) {
					coupons.push(response[i].code);
				}
			}
	});
	if(coupon != '') {
		$.ajax({
			url: "${contextPath}/coupons/"+coupon,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response.data == null){
					$('.disAmount,.disCode').css("display","none");
					$('#couponError_Msg').html('Invalid Coupon code');
				}else{
					$('.couponApplied').html("Coupon Code is Applied("+coupon+")");
					$('.disAmount,.disCode').css("display","block");
					var rti = ${paymentlink.rateofinclusivetax};
					var pa =  ${paymentlink.paidAmount};
					if(rti == 'true' || rti == true){
						pa = pa*100/118;
					}
					for(var i=0;i<coupons.length;i++){
						if(response.data.code == coupons[i]){
							if(response.data.value){
								var val = response.data.value/100;
								var codeval = 1-val;			
								$('#discountAmount').html('<i class="fa fa-rupee"></i>-  <span class="ind_format" id="disAmount">'+pa*val+'</span>');
								$('#subTotal').html('<i class="fa fa-rupee"></i> <span class="ind_format">'+pa*codeval+'</span>');
								$('#totalIGST,#totalGST').html('<i class="fa fa-rupee"></i> <span class="ind_format" id="igstAmount">'+pa*codeval*0.18+'</span>');
								$('#totalCGST').html('<i class="fa fa-rupee"></i> <span class="ind_format" id="cgstAmount">'+pa*codeval*0.09+'</span>');
								$('#totalSGST').html('<i class="fa fa-rupee"></i> <span class="ind_format" id="sgstAmount">'+pa*codeval*0.09+'</span>');
								$('#totalamount').html('<i class="fa fa-rupee"></i><span class="ind_format" id="totalAmount">'+pa*codeval*1.18+'</span>');
								$('#couponError_Msg').html('');	
							}else{
								var val = response.data.disCountValue;
								$('#discountAmount').html('<i class="fa fa-rupee"></i>-  <span class="ind_format" id="disAmount">'+val+'</span>');
								$('#subTotal').html('<i class="fa fa-rupee"></i> <span class="ind_format">'+(pa-val)+'</span>');
								$('#totalIGST,#totalGST').html('<i class="fa fa-rupee"></i> <span class="ind_format" id="igstAmount">'+((pa-val)*0.18)+'</span>');
								$('#totalCGST').html('<i class="fa fa-rupee"></i> <span class="ind_format" id="cgstAmount">'+((pa-val)*0.09)+'</span>');
								$('#totalSGST').html('<i class="fa fa-rupee"></i> <span class="ind_format" id="sgstAmount">'+((pa-val)*0.09)+'</span>');
								$('#totalamount').html('<i class="fa fa-rupee"></i><span class="ind_format" id="totalAmount">'+((pa-val)*1.18)+'</span>');
								$('#couponError_Msg').html('');
							}
						}
					}
				}
			},
			error : function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}else{
		$('.disAmount').css("display","none");
		$('#couponError_Msg').html('Please Enter Coupon Code');
	}
	OSREC.CurrencyFormatter.formatAll({
			selector: '.ind_format'
		});
}
</script>
</body>
</html>
