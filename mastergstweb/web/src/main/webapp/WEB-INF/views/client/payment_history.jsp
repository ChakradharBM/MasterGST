<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Sales Record Payment History</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp"%>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<%-- <script src="${contextPath}/static/mastergst/js/client/paymentss.js" type="text/javascript"></script> --%>
<script src="${contextPath}/static/mastergst/js/client/newpayment.js" type="text/javascript"></script>
<style>
#paymentstable thead tr th,#paymentstable1 thead tr th{font-weight:500;}
.payment-info-tabs .nav-tabs .nav-item.show .nav-link, .payment-info-tabs .nav-tabs .nav-link.active, .payment-info-tabs .nav-tabs .nav-link:hover, .payment-info-tabs .nav-tabs .nav-link.active:hover { background:transparent; border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; color:#374583; font-weight:bold;font-size:13px }
.payment-info-tabs .nav-tabs .nav-item.show .nav-link, .payment-info-tabs .nav-tabs .nav-link.active, .payment-info-tabs .nav-tabs .nav-link:hover, .gstr-info-tabs .nav-tabs .nav-link.active:hover { background:transparent; border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; color:#374583; font-weight:bold;font-size:13px }
.payment-info-tabs .nav-tabs .nav-link { border-bottom:3px solid transparent; border-left:0; border-right:0; border-top:0; color:#707172; font-size:13px; text-transform:uppercase }
.gpayment-info-tabs .nav-tabs .nav-link:hover { border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; font-size:13px}
.payment-info-tabs .nav-tabs { border:0 }
.payment-info-tabs .nav-tabs .nav-item { position:relative; }
.payment-info-tabs .nav-tabs .nav-link.active::after, .payment-info-tabs .nav-tabs .nav-link:hover::after {content:'';border-left: 10px solid transparent;border-right: 10px solid transparent;border-top: 10px solid #8ee3fe;position:absolute;left:40%;bottom:-10px;}
.payment-info-tabs .nav-tabs .nav-link.active::before, .payment-info-tabs .nav-tabs .nav-link:hover::before {content: '';border-left: 10px solid transparent;border-right: 10px solid transparent;border-top: 10px solid #f6f9fb;position: absolute;left: 40%;bottom: -6px;z-index: 1;}
.nav-bread{height:35px!important}
.db-ca-wrap{padding-top: 99px!important;} 
</style>
</head>
<script>var paymentslist=new Array(),cashbankledgername = new Array();
var contextPath='<c:out value="${contextPath}"/>';
var paymentsArray;
var type = '<c:out value="${type}"/>';
invoicesTotalAmount= '<c:out value="${invoicesTotalAmount}"/>';
purchseinvoicesTotalAmount= '<c:out value="${purchseinvoicesTotalAmount}"/>';
var tabType = '<c:out value="${type}"/>';
function showDeletePopup(paymentid,returntype,clientid,vouchernumber,invoicenumber) {
	$('#deleteModal').modal('show');
	//$('#delPopupDetails').html(name);
	if(/^[/]*$/.test(vouchernumber) == false) {
		vouchernumber = vouchernumber.replaceAll("/","invNumCheck");
	}
	if(/^[/]*$/.test(invoicenumber) == false) {
		invoicenumber = invoicenumber.replaceAll("/","invNumCheck");
	}
	$('#btnDelete').attr('onclick', "deletePayment('"+paymentid+"','"+returntype+"','"+clientid+"','"+vouchernumber+"','"+invoicenumber+"')");
}
function deletePayment(paymentid,returntype,clientid,vouchernumber,invoicenumber) {
	//var dpaymenturlSuffix='/${id}/${fullname}/${usertype}/${clientid}';var dPaymenturlprefix='${month}/${year}';
	$.ajax({
		url: "${contextPath}/delpayment/"+paymentid+"/"+returntype+"/"+clientid+"/"+vouchernumber+"/"+invoicenumber,
		success : function(response) {
			if(returntype == 'GSTR1'){
				window.location.href = '${contextPath}/payments_history'+paymenturlSuffix+'/GSTR1/'+Paymenturlprefix+'?type=receive'; 
				//$('#paymentstable tr.row'+paymentid).remove().draw();
				//paymentstable.row( $('.row'+paymentid) ).remove().draw();
			}else{
				window.location.href = '${contextPath}/payments_history'+paymenturlSuffix+'/GSTR2/'+Paymenturlprefix+'?type=made';
				//paymentstable1.row( $('.row'+paymentid) ).remove().draw();	
			}
		}
	});
}
</script>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	<div class="breadcrumbwrap">
		<div class="container">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item">
							<a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>
								<c:choose>
									<c:when
										test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when>
									<c:otherwise>Business</c:otherwise>
								</c:choose>
							</a>
						</li>
						<li class="breadcrumb-item">
							<a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change">
								<c:choose>
									<c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
									<c:otherwise>${client.businessname}</c:otherwise>
								</c:choose>
							</a>
						</li>
						<li class="breadcrumb-item active">Record Payments History</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class="payment-info-tabs">
				<ul class="nav nav-tabs" role="tablist" style="max-width:85%">
					<li class="nav-item"><a class="nav-link active" data-toggle="tab" id="salesTab" href="#receiptstab" role="tab1"><span class="serial-num">1</span> Receipts</a></li>
			        <li class="nav-item"><a class="nav-link" data-toggle="tab" id="purchasesTab" href="#paymentstab" role="tab2"><span class="serial-num">2</span> Payments</a></li>
				</ul>
			</div>
			<div class="tab-content">
				<div class="tab-pane active" id="receiptstab" role="tabpane1">
					<jsp:include page="/WEB-INF/views/client/payment_history_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="client" value="${client}"/>
						<jsp:param name="paymentsTabName" value="salesTab"/>
					</jsp:include>
				</div >
				<div class="tab-pane" id="paymentstab" role="tabpane2">
					<jsp:include page="/WEB-INF/views/client/payment_history_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR2%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="client" value="${client}"/>
						<jsp:param name="paymentsTabName" value="purchasesTab"/>
					</jsp:include>
		  		</div>
			</div>
			<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
	  			<div class="modal-dialog col-6 modal-center" role="document">
	    			<div class="modal-content">
	      				<div class="modal-body">
	        				<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
	        				<div class="invoice-hdr bluehdr">
	          					<h3>Delete Payment </h3>
	        				</div>
	        				<div class=" pl-4 pt-4 pr-4">
	          					<h6>Are you sure you want to delete Payment <span id="delPopupDetails"></span> ?</h6>
	          					<p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
	        				</div>
	      				</div>
	      				<div class="modal-footer">
					        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Payment</button>
					        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
	      				</div>
	    			</div>
	  			</div>
			</div>
		</div>
	</div>
</body>

</html>