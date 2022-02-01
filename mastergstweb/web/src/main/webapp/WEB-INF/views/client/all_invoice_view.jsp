<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Billing Software</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/all-invoice-views.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/gstr2b/gstr2b_reconsilation.js" type="text/javascript"></script>
<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
<c:set var="varGSTR1A" value="<%=MasterGSTConstants.GSTR1A%>"/>
<c:set var="varYes" value="Y"/>
<script type="text/javascript">
var accessDwnldewabillinv='${user.accessDwnldewabillinv}';var invoiceStatus='<c:out value="${type}"/>';var otherreturnType='<c:out value="${otherreturnType}"/>';var rType='<c:out value="${returntype}"/>'; var clinettr='${client.turnovergoptions}';var turnoveroptionsArray = new Array();var paymentslist=new Array();var customerClientBankDetailss;var otpExpirycheck;
	var sendMsgCount=0;var invoiceArray=new Object(),mailArray = new Array(),msgArray = new Array(),irnCanArray=new Array(),gstMatchArray=new Array(),PIArray=new Array(),POArray=new Array(),supEmailids=new Array(),supCCEmailids=new Array(),custEmailids=new Array(),custCCEmailids=new Array(),gstnNotSelArray=new Array(),gstnArray=new Array(),sendMsgArray=new Array(),sendAllMsgsArray=new Array(), ewaybillArray = new Array(),vehicleUpdateArray = new Array(),vUpdateArray = new Array(),ITCclaimedArray = new Array(), selArray = new Object(), prchArray=new Array(), prchInvArray=new Array(), selInvArray=new Array(),Gstr2aArray=new Array(), MismatchArray=new Array(), selMatchArray=new Array(), mannualMatchArray = new Array(), mannuallMatchArray = new Array(),mannulaMatching = new Array(),mannuMatch = new Array(),mnmatch = new Array();var invoiceTable=new Object(), dbFilingTable, dbSendMsgTable,dbHSNFilingTable, dbDocIssueFilingTable, offLiabTable, purchaseTable,gstr2aTable,mismatchTable,gstr2bMismatchTable,mannualMatchtable,mannualViewMatchTable,showInvTable,showInvPRTable;var ipAddress='',gstinnomatch = '', uploadResponse, gstSummary=null;
	var Gstr2bReconsilationArray = new Array(), Gstr2bReconsilationSummaryArray = new Array(), selG2bMatchArray = new Array(), expensesArray = new Array, gstr2bReconsileTable;
	$(function () {
		var finndate = new Date();
		var finyear = finndate.getFullYear();
		var finmnth = finndate.getMonth()+1;
		if(finmnth < 4){
			finyear = finyear-1;
			$('#invoiceviewfinancialYear').val(finyear);
		}else{
			$('#invoiceviewfinancialYear').val(finyear);
		}
		
		//$('#invoiceviewfinancialYear').val('${year}');
		<c:forEach items="${client.turnovergoptions}" var="turnover" varStatus="loop">
		turnoveroptionsArray.push('${turnover.year}-${turnover.turnover}');
	</c:forEach>
		$('input.btaginput').tagsinput({  tagClass: 'big',  });
		$(document).on('click','.btn-remove-tag',function(){$('.bootstrap-tagsinput').html('');});
		$('.multiselect-container>li>a>label').on("click", function(e) {
			e.preventDefault();	var t = $(this).text();$('.bootstrap-tagsinput').append('<span class="tag label label-info">'+ t +'<span data-role="remove"></span></span>');	 
		});
		var icount=$('.dbTableUp tbody').children().length;
		$('.addmore').click(function(){
			icount++;var t = $(this).parent().parent().closest('div').children("table").attr('id');	var u = $(this).parent().closest('div').children("table").attr('id');var docNum = t.replace("dbTableUp","");var index = $('#'+t+' tbody').children().length;
			$('#'+t+' tbody').append('<tr class="inter-state-supplies'+icount+'"><td class="text-left"><input type="hidden" name="docDet['+(docNum-1)+'].docNum" value="'+docNum+'"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet['+(docNum-1)+'].docs['+index+'].from" <c:if test="${not empty invoiceSubmissionData}">value="${invoiceSubmissionData.startInvoiceNo}"</c:if>> </td><td class="text-left"><input type="text" class="form-control" pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="docDet['+(docNum-1)+'].docs['+index+'].to" <c:if test="${not empty invoiceSubmissionData}">value="${invoiceSubmissionData.endInvoiceNo}"</c:if>></td><td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="totals-num'+icount+'" name="docDet['+(docNum-1)+'].docs['+index+'].totnum"></td><td class="text-left"><input type="text" class="form-control" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" id="cancel-num'+icount+'" name="docDet['+(docNum-1)+'].docs['+index+'].cancel"></td><td class="text-left"><input type="text" class="form-control" id="net-issue'+icount+'" readonly="true" name="docDet['+(docNum-1)+'].docs['+index+'].netIssue"></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>');
		});
		if('<c:out value="${otherreturn_type}"/>' != 'additionalInv'){
			if((('<c:out value="${client.filingOption}"/>' == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')
					&& ('<c:out value="${returntype}"/>' == '<%=MasterGSTConstants.GSTR1%>')) || (('<c:out value="${client.filingOption}"/>' == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')
					&& ('<c:out value="${returntype}"/>' == '<%=MasterGSTConstants.GSTR4%>'))) {
				if(${month} == 3 || ${month} == 2 || ${month} == 1) {monthNames[${month}-1]='Jan - Mar';
				} else if(${month} == 6 || ${month} == 5 || ${month} == 4) {monthNames[${month}-1]='Apr - Jun';
				} else if(${month} == 9 || ${month} == 8 || ${month} == 7) {monthNames[${month}-1]='Jul - Sep';
				} else if(${month} == 12 || ${month} == 11 || ${month} == 10) {monthNames[${month}-1]='Oct - Dec';}
			}
		}
		
		var monthdisplay = ""+monthNames[${month}-1]+" ${year}";
		
		if('<c:out value="${otherreturn_type}"/>' == 'additionalInv'){
			var invviewoption = '${client.invoiceViewOption}';
			if(invviewoption == "Yearly"){
				var yesr = $('#invoiceviewfinancialYear option:selected').text();
				monthdisplay = yesr;
			}
		}		
		$('.summary_retperiod').each(function() {$(this).append(' Of <span style="color:#ff9900">'+monthdisplay+' </span><c:if test="${returntype eq 'EWAYBILL'}">, Connection Status<span id="authStatus" style="margin-left: 4px;"></span><span id="ebill_msg"></span></c:if>');});
		
	});
	function updateFilingOption(value) {
		$.ajax({
			url : '${contextPath}/saveflngoptn/${client.id}/'+value+'/'+month+'/'+year,
			async: false,
			cache: false,
			success : function(data) {
				successNotification('Filing Option save successful!');
				if(value == 'Quarterly'){
					if(month == 1 || month == 2 || month == 3){month = 3;
					}else if(month == 4 || month == 5 || month == 6){month = 6;
					}else if(month == 7 || month == 8 || month == 9){month = 9;
					}else if(month == 10 || month == 11 || month == 12){month = 12;}
				}
				window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>/'+month+'/'+year+'?type='+invoiceStatus;
			},
			error : function(e) {if(e.responseText) {errorNotification(e.responseText);}}
		});
		document.getElementById('filing_option').innerHTML=value;
	}
	
	function updateInvoiceViewOption(option){
		$.ajax({
			url : '${contextPath}/saveinvoiceviewoptn/${client.id}/'+option,
			async: false,
			cache: false,
			success : function(data) {
				successNotification('Invoice View Option save successful!');
				window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${paymentreturnType}"/>/'+month+'/'+year+'?type='+invoiceStatus;
			},
			error : function(e) {if(e.responseText) {errorNotification(e.responseText);}}
		});
	}
	function updateMismatchDataNew(btn, acceptFlag) {
		$(btn).addClass('btn-loader');
		$.ajax({
			type: "POST",
			url: "${contextPath}/prfmmismatch/"+acceptFlag,
			async: false,
			cache: false,
			data: JSON.stringify(selMatchArray),
			contentType: 'application/json',
			success : function(response) {
				window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>/'+month+'/'+year+'?type=mmtch';
			},
			error : function(e, status, error) {if(e.responseText) {errorNotification(e.responseText);}}
		});
	}
	function downloadGSTRecords(btn) {
		$(btn).addClass('btn-loader');
		window.location.href = '${contextPath}/dwnldinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>/'+month+'/'+year;
	}
	function invokeOTP(btn) {
		var state = "${client.statename}";var gstname = "${client.gstname}";
		$('#dotp_Msg').text('').css("display","none");
		$("#dwnldOtpEntryForm")[0].reset();
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
				$('#downloadOtpModal').modal('show');
			},
			error : function(e, status, error) {}
		});
	}
	function validateDownloadOtp() {
		var otp1 = $('#dotp1').val();var otp2 = $('#dotp2').val();var otp3 = $('#dotp3').val();var otp4 = $('#dotp4').val();var otp5 = $('#dotp5').val();var otp6 = $('#dotp6').val();
		if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){	
			$('#dotp_Msg').text('Please Enter otp').css("display","block");
		}else{
			var otp = otp1+otp2+otp3+otp4+otp5+otp6;
			var pUrl = "${contextPath}/ihubauth/"+otp;
			$("#otpEntryForm")[0].reset();
			$.ajax({
				type: "POST",
				url: pUrl,
				async: false,
				cache: false,
				data: JSON.stringify(uploadResponse),
				dataType:"json",
				contentType: 'application/json',
				success : function(authResponse) {
					if(authResponse.status_cd == '1'){closeNotifications();$('#downloadOtpModalClose').click();
					}else{$('#dotp_Msg').text('Please Enter Valid Otp').css('display','block');}
				},
				error : function(e, status, error) {
					$('#downloadOtpModalClose').click();
					if(e.responseText) {errorNotification(e.responseText);}
				}
			});
		}
	}
	function otpTryAgain(){
		$('#dotp_Msg').text('').css("display","none");$("#dwnldOtpEntryForm")[0].reset();
		var state = "${client.statename}";var gstname = "${client.gstname}";
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
			},
			error : function(e, status, error) {}
		});	
	}
	function uploadInvoiceSet(invArray, index, total,hsn) {
		gstSummary = null;
		var pUrl ="";
		if('${returntype}' == 'ANX2'){
			pUrl="${contextPath}/ihubsaveANX2status/${id}/${usertype}/${client.id}/${returntype}?month=" + month + "&year=" + year + "&invoices=" + invArray;
		}else{
			pUrl="${contextPath}/ihubsavestatus/${id}/${usertype}/${client.id}/${returntype}?month=" + month + "&year=" + year+"&hsn="+hsn;
		}
		$.ajax({
			type: "POST",
			url: pUrl,
			async: false,
			cache: false,
			dataType:"json",
			data:JSON.stringify(invArray),
			contentType: 'application/json',
			success : function(response) {
				if(response.status_cd == '1') {
					selInvArray = new Array();
					$('.select_msg').text('');
					processResponse(response);
					if(total != '') {if(hsn == 'hsn'){if(index == total){successNotification('Uploaded '+(index)+' of total '+total+' records!');}else{successNotification('Uploaded '+(index+1)+' of total '+total+' records!');}}else{successNotification('Uploaded HSN Summary');}}
				} else if(index == 0 && response.error 
					&& response.error.error_cd == 'AUTH4037') {
					$('#uploadOtpModal').modal('show');$('#idClientError').html("We noticed that your GSTIN <strong>( ${client.gstnnumber} )</strong> doesn't have API Access with the GSTN Portal Login/User Name <strong>${client.gstname}</strong> Please enable the API access and update GSTN User Name correctly, Please follow below steps.");
					$('#gstnUserId').val('${client.gstname}');
				} else {
					if(response.error && response.error.message) {errorNotification(response.error.message);
					} else if(response.status_cd == '0') {
						if(response.status_desc == 'OTP verification is not yet completed!' || response.status_desc == 'Invalid Session' || response.status_desc == 'Unauthorized User!' || response.status_desc == 'Missing Mandatory Params' || response.status_desc == 'API Authorization Failed') {
							errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
						} else if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
							var errorMsg = 'Your subscription has expired. Kindly subscribe to proceed further!';
							if(varUserType == 'suvidha' || varUserType == 'enterprise'){errorMsg += ' Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.';
							}else{errorMsg += ' Click <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.';}
							errorNotification(errorMsg);
						} else if(response.status_desc == 'You are not allowed to access Return for selected return period') {
							errorNotification('You are not allowed to access Return for selected return period. Kindly update <a href="#" onclick="updateFilingOption(\'<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>\')" class="btn btn-sm btn-blue-dark">Filing Type</a> to proceed further!');
						} else {errorNotification(response.status_desc);}
					}
				}
			},
			error : function(e, status, error) {if(e.responseText) {errorNotification(e.responseText);}}
		});
	}
	function showOtp(){
		var state = "${client.statename}";
		var gstname = $('#gstnUserId').val();
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
				$('.gstn-otp-wrap').show();
			},
			error : function(e, status, error) {if(e.responseText) {$('#idClientError').html(e.responseText);}}
		});
	}
	function validOtp() {
		var otp1 = $('#otp1').val();var otp2 = $('#otp2').val();var otp3 = $('#otp3').val();var otp4 = $('#otp4').val();var otp5 = $('#otp5').val();var otp6 = $('#otp6').val();
		var otp = otp1+otp2+otp3+otp4+otp5+otp6;var pUrl = "${contextPath}/ihubauth/"+otp;
		$("#otpEntryForm")[0].reset();
		$.ajax({
			type: "POST",
			url: pUrl,
			async: false,
			cache: false,
			data: JSON.stringify(uploadResponse),
			dataType:"json",
			contentType: 'application/json',
			success : function(authResponse) {
				$('.gstn-otp-wrap').hide();
				if(authResponse.status_cd == '1') {
					uploadToGST();$('#idVerifyClient').parent().show();$('#idVerifyClient').html("Verified OTP Number successfully. Your User Name for GSTN Number (<strong>${client.gstnnumber}</strong>) verified.");
					closeNotifications();
				}
			},
			error : function(e, status, error) {if(e.responseText) {$('#idClientError').html(e.responseText);}}
		});
	}
	function uploadToGST() {
		var pUrl = "${contextPath}/ihubsavestatus/${id}/${usertype}/${client.id}/${returntype}?month=" + month + "&year=" + year;
		$.ajax({
			type: "POST",
			url: pUrl,
			async: false,
			cache: false,
			dataType:"json",
			data:JSON.stringify(selArray['${returntype.replaceAll(' ', '_')}']),
			contentType: 'application/json',
			success : function(statusResponse) {
				processResponse(statusResponse);
			},
			error : function(e, status, error) {if(e.responseText) {$('#idClientError').html(e.responseText);}}
		});
	}
</script>
</head>
<body class="body-cls">
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
  <div class="breadcrumbwrap nav-bread">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<c:choose><c:when test="${otherreturnType eq 'SalesRegister' || otherreturnType eq 'PurchaseRegister'}"><li class="breadcrumb-item active">${otherreturnType}</li></c:when><c:when test="${returntype eq 'EWAYBILL'}"><li class="breadcrumb-item active">EWAY BILLS</li></c:when><c:otherwise><li class="breadcrumb-item active">${returntype}</li></c:otherwise></c:choose>
					</ol>
					<c:choose>
						<c:when test="${paymentreturnType eq MasterGSTConstants.PROFORMAINVOICES || paymentreturnType eq MasterGSTConstants.DELIVERYCHALLANS || paymentreturnType eq MasterGSTConstants.ESTIMATES || paymentreturnType eq 'SalesRegister' || paymentreturnType eq MasterGSTConstants.PURCHASEORDER || paymentreturnType == 'Expenses' || paymentreturnType eq 'PurchaseRegister'}">
							<c:if test='${not empty client}'>
								<c:if test="${not empty client.invoiceViewOption}">
									<c:if test="${client.invoiceViewOption eq 'Yearly'}">
										<select class="pull-right m-1 ml-0" name="invoiceview_financialYear" id="invoiceviewfinancialYear">
											<option value="2021">2021 - 2022</option>
											<option value="2020">2020 - 2021</option>
											<option value="2019">2019 - 2020</option>
											<option value="2018">2018 - 2019</option>
											<option value="2017">2017 - 2018</option>
										</select>
										<span class="f-14-b pull-right mt-1 font-weight-bold">Financial Year :</span>									
									</c:if>
									<c:if test="${client.invoiceViewOption eq 'Monthly' || empty client.invoiceViewOption}">
										<span class="datetimetxt"><input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i></span>
										<span class="f-14-b pull-right mt-1 font-weight-bold"><c:if test="${returntype != 'EWAYBILL'}">Return Period:</c:if><c:if test="${returntype == 'EWAYBILL'}">Month:</c:if></span>	
									</c:if>
								</c:if>
								<c:if test="${empty client.invoiceViewOption}">
									<span class="datetimetxt"><input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i></span>
									<span class="f-14-b pull-right mt-1 font-weight-bold"><c:if test="${returntype != 'EWAYBILL'}">Return Period:</c:if><c:if test="${returntype == 'EWAYBILL'}">Month:</c:if></span>	
								</c:if>
							</c:if>
						</c:when>
						<c:otherwise>
							<span class="datetimetxt"><input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i></span>
							<span class="f-14-b pull-right mt-1 font-weight-bold"><c:if test="${returntype != 'EWAYBILL'}">Return Period:</c:if><c:if test="${returntype == 'EWAYBILL'}">Month:</c:if></span>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${paymentreturnType eq MasterGSTConstants.PROFORMAINVOICES || paymentreturnType eq MasterGSTConstants.DELIVERYCHALLANS || paymentreturnType eq MasterGSTConstants.ESTIMATES || paymentreturnType eq 'SalesRegister' || paymentreturnType eq MasterGSTConstants.PURCHASEORDER || paymentreturnType == 'Expenses' || paymentreturnType eq 'PurchaseRegister'}">
							<span class="dropdown chooseteam">
								<span class="dropdown-toggle" data-toggle="dropdown" id="invoiceviewoption">
									<b><span class="inv_view_txt">Invoice</span> View By :</b> 
									<span id="invoiceview_option">
										<c:if test='${not empty client}'>
											<c:if test="${not empty client.invoiceViewOption}">${client.invoiceViewOption}</c:if>
											<c:if test="${empty client.invoiceViewOption}">Monthly</c:if>
										</c:if>
									</span>
								</span>
								<div class="dropdown-menu" style="width: 108px!important;min-width: 36px;left: 52%;">
									<a class="dropdown-item" href="#" onClick="updateInvoiceViewOption('<%=MasterGSTConstants.INVOICEVIEW_OPTION_MONTHLY%>')"><%=MasterGSTConstants.INVOICEVIEW_OPTION_MONTHLY%></a>
									<a class="dropdown-item" href="#" onClick="updateInvoiceViewOption('<%=MasterGSTConstants.INVOICEVIEW_OPTION_YEARLY%>')"><%=MasterGSTConstants.INVOICEVIEW_OPTION_YEARLY%></a>
								</div>
							</span>
						</c:when>
						<c:otherwise>
							<c:if test="${returntype != 'EWAYBILL'}"><span class="dropdown chooseteam">
							  <span  <c:if test = "${returntype eq varGSTR1 || returntype eq varGSTR5 || returntype eq varGSTR6}">class="dropdown-toggle" data-toggle="dropdown"</c:if> id="fillingoption"><b>Filing Option:</b> <span id="filing_option"><c:if test='${not empty client}'><c:if test='${not empty client.filingOption}'> <c:choose><c:when test="${returntype eq varPurchase || returntype eq varGSTR2 || returntype eq 'GSTR2A'}">Monthly</c:when><c:otherwise>${client.filingOption}</c:otherwise></c:choose></c:if> <c:if test='${empty client.filingOption}'>None</c:if></c:if></span></span>
							  <div class="dropdown-menu" style="width: 108px!important;min-width: 36px;left: 52%;">
								<a class="dropdown-item" href="#" onClick="updateFilingOption('<%=MasterGSTConstants.FILING_OPTION_MONTHLY%>')"><%=MasterGSTConstants.FILING_OPTION_MONTHLY%></a>
								<a class="dropdown-item" href="#" onClick="updateFilingOption('<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')"><%=MasterGSTConstants.FILING_OPTION_QUARTERLY%></a>
							  </div>
							</span></c:if>
						</c:otherwise>
					</c:choose>
					
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
        <div class="db-ca-wrap db-ca-gst-wrap">
            <div class="container">
                 <div class="row">
                    <div class="col-md-12 col-sm-12">
						<div class="gstr-info-tabs">					
<c:if test="${otherreturn_type != 'additionalInv'}"><c:choose><c:when test="${returntype eq varPurchase}"></c:when><c:when test="${returntype eq varGSTR2}"></c:when><c:otherwise><div class="pull-right helpguide" data-toggle="modal" data-target="#helpGuideModal"> Help To File <c:choose><c:when test="${returntype eq varPurchase}">GSTR2</c:when><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR1}">GSTR1</c:when><c:otherwise>${returntype}</c:otherwise></c:choose></div></c:otherwise></c:choose></c:if>
<ul class="nav nav-tabs" role="tablist" style="max-width:85%">
  <c:if test="${otherreturn_type != 'additionalInv' && returntype != 'EWAYBILL'}">
  <li class="nav-item"><a class="nav-link permission${returntype}-${returntype}_Summary active" id="summarytab" data-toggle="tab" href="#gtab4" role="tab"><c:if test="${otherreturn_type ne 'additionalInv'}"><span class="serial-num">1</span></c:if> <c:choose><c:when test="${returntype eq varPurchase || returntype eq varGSTR2 || returntype eq 'GSTR2A'}">PURCHASE</c:when><c:when test="${returntype eq varGSTR1}">GSTR1(SALES)</c:when><c:otherwise>${returntype}</c:otherwise></c:choose> SUMMARY</a></li>
  </c:if>
  <c:if test="${returntype eq 'ANX2'}">
  <li class='nav-item'><a class="nav-link permissionGSTR2-Purchases" data-toggle="tab" id="purchaseTab" href="#gtab5" role="tab"><span class="serial-num">2</span>PURCHASE INVOICES</a></li>
  </c:if>
  <c:if test="${otherreturn_type == 'additionalInv'}">
  <li class="nav-item"><a class="nav-link permission${returntype}-${returntype}_Summary active" id="summarytab" data-toggle="tab" href="#gtab4" role="tab"><c:if test="${otherreturn_type ne 'additionalInv'}"><span class="serial-num">1</span></c:if> <c:choose><c:when test="${returntype eq varPurchase || returntype eq varGSTR2 || returntype eq 'GSTR2A'}">PURCHASE</c:when><c:when test="${returntype eq varGSTR1}">SALES</c:when><c:otherwise>${returntype}</c:otherwise></c:choose> SUMMARY</a></li>
  </c:if>
  <c:if test="${returntype eq varGSTR2 || returntype eq 'GSTR2A'}">
  <li class='nav-item'><a class="nav-link permissionGSTR2-Purchases" data-toggle="tab" id="purchaseTab" href="#gtab5" role="tab"><c:if test="${otherreturn_type ne 'additionalInv'}"><span class="serial-num">2</span> PURCHASES</c:if><c:if test="${otherreturn_type eq 'additionalInv'}">PURCHASE INVOICES</c:if> </a></li>
  <c:if test="${otherreturn_type != 'additionalInv'}">
  <li class="nav-item"><a class="nav-link permissionGSTR2-GSTR2A" data-toggle="tab" id="downloadTab" href="#gtab6" role="tab"><span class="serial-num">3</span> GSTR2A</a></li>
  <li class="nav-item"><a class="nav-link" data-toggle="tab" id="gstr2bTab" onclick="" href="#gtab2b" role="tab"><span class="serial-num">4</span> GSTR2B</a></li>
 <li class="nav-item"><a class="nav-link permissionGSTR2-MisMatched" data-toggle="tab" id="reconcileTab" href="#gtab2" role="tab"><span class="serial-num">5</span> GSTR2A RECONCILE</a></li>
 <li class="nav-item"><a class="nav-link permissionGSTR2-MisMatched" data-toggle="tab" id="gstr2bmisMatchTab" href="#gtab2bReconcile" role="tab"><span class="serial-num">6</span> GSTR2B RECONCILE</a></li>
  
  </c:if>
  </c:if>
  <c:if test="${otherreturn_type != 'additionalInv'}">
  <c:if test="${returntype eq varGSTR2 || returntype eq 'GSTR2A'}">
  <li class="nav-item">
    <a class="nav-link permissionGSTR2-Unclaimed_ITC" data-toggle="tab" id="unClaimedItcTab" href="#gtab8" role="tab"><span class="serial-num">7</span> Unclaimed ITC</a>
  </li>
  </c:if>
  </c:if>
   <li class='nav-item'>
	  	<c:choose>
	  		<c:when test="${returntype eq varPurchase}"><a class="nav-link permissionInvoices-${returntype}-View" data-toggle="tab" id="invoiceTab" href="#gtab1" role="tab">PURCHASE</a></c:when>
	  		<c:when test="${returntype eq varGSTR1}"><a class="nav-link permissionInvoices-Sales-View" data-toggle="tab" id="invoiceTab" href="#gtab1" role="tab"><c:if test="${otherreturn_type ne 'additionalInv'}"><span class="serial-num">2</span>SALES</c:if><c:if test="${otherreturn_type eq 'additionalInv'}">SALE INVOICES</c:if> </a></c:when>
	  		<c:when test="${returntype eq varGSTR2}"></c:when>
	  		<c:when test="${returntype eq 'EWAYBILL'}"><a class="nav-link active" data-toggle="tab" id="invoiceTab" href="#EWAYBILLTab" role="tab"><span class="serial-num">1</span> EWAY BILLS</a></c:when>
			<c:when test="${returntype eq 'ANX2'}"><a class="nav-link" data-toggle="tab" id="invoiceTab" href="#gtab1" role="tab"><span class="serial-num">3</span> ANX2</a></c:when>
	  		<c:otherwise><a class="nav-link permission${returntype}-${returntype}" data-toggle="tab" id="invoiceTab" href="#gtab1" role="tab"><span class="serial-num">2</span> ${returntype}</a></c:otherwise>
	  	</c:choose>
  </li>
  <c:if test="${otherreturn_type eq 'additionalInv'}">
	<c:if test="${returntype eq varGSTR1}">
  	<li class="nav-item"><a class="nav-link permissionGSTR1-Delivery_Challans" data-toggle="tab" id="deliveryTab" href="#DELIVERYCHALLANS" role="tab">DELIVERY CHALLANS</a></li><li class="nav-item"><a class="nav-link permissionGSTR1-Proforma_Invoices" data-toggle="tab" id="proformaTab" href="#PROFORMAINVOICES" role="tab">PROFORMA INVOICES</a></li><li class="nav-item"><a class="nav-link permissionGSTR1-Estimates" data-toggle="tab" id="estimateTab" href="#ESTIMATES" role="tab">ESTIMATES</a></li>
	</c:if>
	<c:if test="${returntype eq varGSTR2 || returntype eq 'GSTR2A'}"><li class="nav-item"><a class="nav-link permission${returntype}-Purchase_Order" data-toggle="tab" id="purchaseOrderTab" href="#PurchaseOrder" role="tab">Purchase Order</a></li><li class="nav-item"><a class="nav-link permission${returntype}-Expenses" data-toggle="tab" id="expensesTab" href="#Expenses" role="tab">Expenses</a></li></c:if>
  </c:if>
  <c:if test="${otherreturn_type != 'additionalInv'}"><c:if test="${returntype eq varGSTR1 || returntype eq varANX1 || returntype eq varGSTR6}"><li class="nav-item"><a class="nav-link permission${returntype}-Amendments" data-toggle="tab" id="gstr1aTab" href="#gtab9" role="tab"><span class="serial-num">3</span> AMENDMENTS</a></li> </c:if></c:if>
  <c:if test="${otherreturn_type != 'additionalInv'}"> <c:if test="${returntype eq varGSTR1 || returntype eq varANX1}"><li class="nav-item"><a class="nav-link permissionGSTR1-Upload_Documents" data-toggle="tab" id="docIssueTab" href="#gtab7" role="tab"><span class="serial-num">4</span> UPLOAD DOCUMENTS</a></li></c:if></c:if>
  <c:if test="${otherreturn_type != 'additionalInv'}">
  <c:if test="${returntype != varPurchase}">
  <li class="nav-item">
    <a class="nav-link permission${returntype}-Filing_${returntype}" data-toggle="tab" id="filingTab" href="#gtab3" role="tab" onClick="fetchRetSummary(true,'${returntype}')">
	<c:choose><c:when test="${returntype eq varGSTR1 || returntype eq varANX1}"><span class="serial-num">5</span> FILING ${returntype}</c:when>
	<c:when test="${returntype eq varGSTR4 || returntype eq varGSTR1A || returntype eq varGSTR5}"><span class="serial-num">3</span> FILING ${returntype}</c:when>
	<c:when test="${returntype eq varGSTR6}"><span class="serial-num">4</span> FILING ${returntype}</c:when>
	</c:choose>
	 </a>
  </li>
  </c:if>
  </c:if>
  <c:if test="${returntype eq 'ANX1'}">
  <li class="nav-item">
    <a class="nav-link" data-toggle="tab" id="filingTab" href="#gtab3" role="tab" onClick="fetchRetSummary(true,'${returntype}')">
	<span class="serial-num">3</span> FILING ${returntype}
	 </a>
  </li>
  </c:if>
   <c:if test="${returntype eq 'ANX2'}">
  <li class="nav-item"><a class="nav-link permissionGSTR2-MisMatched" data-toggle="tab" id="misMatchTab" href="#gtab2" onclick="anx2tab()"role="tab"><span class="serial-num">4</span> RECONCILE</a></li>
  </c:if>
</ul>
<div class="tab-content">
			<div class="tab-pane active" id="gtab4" role="tabpane4">
			<c:if test="${returntype eq varGSTR2 || returntype eq 'ANX2'}">
				<jsp:include page="/WEB-INF/views/client/invoiceSummery.jsp">
				<jsp:param name="returntype" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>" />
			</jsp:include>
			</c:if>
			<c:if test="${returntype eq varGSTR1}">
				<jsp:include page="/WEB-INF/views/client/invoiceSummery.jsp">
				<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
			</jsp:include>
			</c:if>
			<c:if test="${returntype eq 'ANX1'}">
				<jsp:include page="/WEB-INF/views/client/invoiceSummery.jsp">
				<jsp:param name="returntype" value="<%=MasterGSTConstants.ANX1%>" />
			</jsp:include>
			</c:if>
			<c:if test="${returntype eq 'EWAYBILL'}">
			<jsp:include page="/WEB-INF/views/client/invoiceSummery.jsp">
				<jsp:param name="returntype" value="<%=MasterGSTConstants.EWAYBILL%>" />
			</jsp:include>
			<div class="tab-pane" id="EWAYBILLTab" role="tabpanel" >
				<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
					<jsp:param name="id" value="${id}" />
					<jsp:param name="fullname" value="${fullname}" />
					<jsp:param name="usertype" value="${usertype}" />
					<jsp:param name="returntype" value="EWAYBILL" />
					<jsp:param name="client" value="${client}" />
					<jsp:param name="contextPath" value="${contextPath}" />
					<jsp:param name="month" value="${month}" />
					<jsp:param name="year" value="${year}" />
					<jsp:param name="tabName" value="invoiceTab" />
				</jsp:include>
			</div>
		</c:if>
			</div>
	<c:if test="${returntype eq varGSTR2 || returntype eq 'ANX2'}">
	  <div class="tab-pane" id="gtab5" role="tabpane5">
		<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
			<jsp:param name="id" value="${id}" />
			<jsp:param name="fullname" value="${fullname}" />
			<jsp:param name="usertype" value="${usertype}" />
			<jsp:param name="returntype" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>" />
			<jsp:param name="client" value="${client}" />
			<jsp:param name="contextPath" value="${contextPath}" />
			<jsp:param name="month" value="${month}" />
			<jsp:param name="year" value="${year}" />
			<jsp:param name="tabName" value="purchaseTab" />
		</jsp:include>
		</div>
		<c:if test="${otherreturnType ne 'PurchaseRegister'}">
		<div class="tab-pane" id="gtab6" role="tabpane6">
			<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
				<jsp:param name="id" value="${id}" />
				<jsp:param name="fullname" value="${fullname}" />
				<jsp:param name="usertype" value="${usertype}" />
				<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR2A%>" />
				<jsp:param name="client" value="${client}" />
				<jsp:param name="contextPath" value="${contextPath}" />
				<jsp:param name="month" value="${month}" />
				<jsp:param name="year" value="${year}" />
				<jsp:param name="tabName" value="downloadTab" />
			</jsp:include>
		</div>
		<div class="tab-pane" id="gtab8" role="tabpanel">
			<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
				<jsp:param name="id" value="${id}" />
				<jsp:param name="fullname" value="${fullname}" />
				<jsp:param name="usertype" value="${usertype}" />
				<jsp:param name="returntype" value="Unclaimed" />
				<jsp:param name="client" value="${client}" />
				<jsp:param name="contextPath" value="${contextPath}" />
				<jsp:param name="month" value="${month}" />
				<jsp:param name="year" value="${year}" />
				<jsp:param name="tabName" value="unClaimedItcTab" />
			</jsp:include>
		</div>
		</c:if>
	</c:if>
	
	<div class="tab-pane" id="gtab2b" role="tabpanel" >
			<jsp:include page="/WEB-INF/views/client/allDetails2b.jsp">
				<jsp:param name="id" value="${id}" />
				<jsp:param name="fullname" value="${fullname}" />
				<jsp:param name="usertype" value="${usertype}" />
				<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR2B%>" />
				<jsp:param name="client" value="${client}" />
				<jsp:param name="contextPath" value="${contextPath}" />
				<jsp:param name="month" value="${month}" />
				<jsp:param name="year" value="${year}" />
				<jsp:param name="tabName" value="gstr2bTab" />
			</jsp:include>
	</div>
	
	<c:choose>
	<c:when test="${otherreturntype eq 'SalesRegister' || otherreturntype eq 'PurchaseRegister' || returntype eq 'EWAYBILL'}">
	</c:when>
	<c:otherwise>
	<div class="tab-pane" id="gtab1" role="tabpanel" >
	<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="${returntype}" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="invoiceTab" />
	</jsp:include>
	</div>
	</c:otherwise>
	</c:choose>
	<c:if test="${otherreturnType eq 'SalesRegister' || otherreturnType eq 'PROFORMAINVOICES' || otherreturnType eq 'DELIVERYCHALLANS' || otherreturnType eq 'ESTIMATES'}">
	<div class="tab-pane" id="PROFORMAINVOICES" role="tabpanel" >
	<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="PROFORMAINVOICES" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="proformaTab" />
	</jsp:include>
	</div>
	<div class="tab-pane" id="DELIVERYCHALLANS" role="tabpanel" >
	<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="DELIVERYCHALLANS" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="deliveryTab" />
	</jsp:include>
	</div>
	<div class="tab-pane" id="ESTIMATES" role="tabpanel" >
	<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="ESTIMATES" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="estimateTab" />
	</jsp:include>
	</div>
	</c:if>
	<c:if test="${otherreturnType eq 'PurchaseRegister' || otherreturnType eq 'PurchaseOrder'}">
	<div class="tab-pane" id="PurchaseOrder" role="tabpanel" >
	<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="PurchaseOrder" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="purchaseOrderTab" />
	</jsp:include>
	</div>
	</c:if>
	<c:if test="${otherreturnType eq 'PurchaseRegister' || otherreturnType eq 'EXPENSES'}">
	<div class="tab-pane" id="Expenses" role="tabpanel" >
	<jsp:include page="/WEB-INF/views/client/alliview_tab_upd.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="EXPENSES" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="expensesTab" />
	</jsp:include>
	</div>
	</c:if>
	<c:if test="${returntype ne varGSTR3B && returntype ne varGSTR2B && returntype ne varGSTR1 && otherreturnType ne 'PurchaseRegister'}">
        <div class="tab-pane" id="gtab2" role="tabpanel">
        	<jsp:include page="/WEB-INF/views/client/reconcile_tab.jsp">
				<jsp:param name="id" value="${id}" />
				<jsp:param name="fullname" value="${fullname}" />
				<jsp:param name="usertype" value="${usertype}" />
				<jsp:param name="returntype" value="${returntype}" />
				<jsp:param name="client" value="${client}" />
				<jsp:param name="contextPath" value="${contextPath}" />
				<jsp:param name="month" value="${month}" />
				<jsp:param name="year" value="${year}" />
				<jsp:param name="tabName" value="reconcileTab" />
			</jsp:include> 
			</div>
		
				
		<div class="tab-pane" id="gtab2bReconcile" role="tabpanel">
			<jsp:include page="/WEB-INF/views/client/gstr2breconcile_tab.jsp">
				<jsp:param name="id" value="${id}" />
				<jsp:param name="fullname" value="${fullname}" />
				<jsp:param name="usertype" value="${usertype}" />
				<jsp:param name="returntype" value="${returntype}" />
				<jsp:param name="client" value="${client}" />
				<jsp:param name="contextPath" value="${contextPath}" />
				<jsp:param name="month" value="${month}" />
				<jsp:param name="year" value="${year}" />
				<jsp:param name="tabName" value="gstr2breconcileTab" />
			</jsp:include> 
		</div>
	</c:if>
				<div class="tab-pane" id="gtab3" role="tabpanel">
				<div class="alert alert-success" role="alert">This Data is populated from GSTN Portal, Usually it will take 2 to 20 minutes of time to update on GSTN portal, when you don't see any data Click on Refresh in 10 to 20 minutes.</div>
					<div class="customtable db-ca-view tabtable3">
						 <table id="dbFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead><tr><th> Type</th><th>Ordering</th><th>Uploaded Invoices</th><th>Taxable Amt</th><th>Total Amt</th><th>Total Tax</th></tr></thead>
							<tbody></tbody><tfoot><tr><th>Totals</th><th></th><th id="totalInvoices"></th><th id="totalTaxableAmt"></th><th id="totalAmt"></th><th id="totalTaxAmt"></th></tr></tfoot>
						</table>
					</div>
					<c:if test="${returntype eq varGSTR4}">
					<div class="customtable db-ca-view tabtable8">
						 <table id="offLiabTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead><tr><th>Category</th><th>Liability Id</th><th>Transaction Date</th><th>Type</th><th class="text-center dt-body-right">Tax</th><th class="text-center dt-body-right">Interest</th><th class="text-center dt-body-right">Penalty</th><th class="text-center dt-body-right">Fee</th><th class="text-center dt-body-right">Other</th><th class="text-center dt-body-right">Total</th></tr></thead>
							<tbody></tbody>
						</table>
					</div>
					</c:if>
					<c:if test="${returntype ne varGSTR3B && returntype ne varGSTR1A && returntype ne 'ANX1'}">
					<div class="customtable db-ca-view tabtable5">
						 <table id="dbHSNFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead><tr><th>S.NO</th><th>HSN/SAC</th><th>DESCRIPTION</th><th class="text-center dt-body-right">QUANTITY</th><th class="text-center dt-body-right"><c:choose><c:when test="${month > 4 && year > 2020}">TAX RATE</c:when><c:when test="${month < 4 && year > 2021}">TAX RATE</c:when><c:otherwise>VALUE</c:otherwise></c:choose></th><th class="text-center dt-body-right">TAXABLE</th><th class="text-center dt-body-right">IGST</th><th class="text-center dt-body-right">CGST</th><th class="text-center dt-body-right">SGST</th><th class="text-center dt-body-right">CESS</th></tr></thead>
							<tbody>
							</tbody>
							<tfoot><tr><th></th><th></th><th></th><th>Totals</th><th></th><th id="hsnTotalTaxableAmount"></th><th id="hsnTotalIgstAmount"></th><th id="hsnTotalCgstAmount"></th><th id="hsnTotalSgstAmount"></th><th id="hsnTotalCessAmount"></th></tr></tfoot>
						</table>
					</div>
					<c:if test="${returntype eq varGSTR1}">
					<div class="customtable db-ca-view tabtable7">
						 <table id="dbDocIssueFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead><tr><th>Document No</th><th>From serial no</th><th>To serial no</th><th class="text-center dt-body-right">Total Number</th><th class="text-center dt-body-right">Cancelled</th><th class="text-center dt-body-right">Net Issued</th></tr></thead>
							<tbody>
							</tbody>
						</table>
					</div>
					</c:if>
					</c:if>
				</div>
<c:if test="${returntype eq varGSTR1}">
<div class="tab-pane" id="gtab7" role="tabpanel">
	<h4 class="hdrtitle" style="margin-bottom:-40px"><span class="summary_retperiod">Documents</span></h4>
	<%@include file="/WEB-INF/views/client/doc_issue.jsp" %>		
</div>
</c:if>
 <c:if test="${otherreturn_type != 'additionalInv'}">
<c:if test="${returntype eq varGSTR1 || returntype eq varGSTR6}">
<div class="tab-pane" id="gtab9" role="tabpanel">
	<jsp:include page="/WEB-INF/views/client/alliview_tab.jsp">
		<jsp:param name="id" value="${id}" />
		<jsp:param name="fullname" value="${fullname}" />
		<jsp:param name="usertype" value="${usertype}" />
		<jsp:param name="returntype" value="${returntype}Amnd" />
		<jsp:param name="client" value="${client}" />
		<jsp:param name="contextPath" value="${contextPath}" />
		<jsp:param name="month" value="${month}" />
		<jsp:param name="year" value="${year}" />
		<jsp:param name="tabName" value="gstr1aTab" />
	</jsp:include>
</div>
</c:if>
</c:if>
</div>
						</div>
					</div>
                </div>
            </div>
        </div>
    </div>
	<jsp:include page="/WEB-INF/views/client/EwaybillModals.jsp" />
	<jsp:include page="/WEB-INF/views/client/alliview_modal.jsp" />
	<form id="certlistForm" name="certlistForm" method="POST" enctype="multipart/form-data"></form>
	<form id="certsignForm" name="certsignForm" method="POST" enctype="multipart/form-data"></form>
    <jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
 <script src="${contextPath}/static/mastergst/js/common/ebillfiledrag-map.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/alliviews.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function () {
        $(".tabtable3  div.toolbar").html('<h4><c:choose><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR6}">GSTR6(PURCHASE)</c:when><c:otherwise>${returntype}(SALES)</c:otherwise></c:choose> Filing Summary Of '+monthNames[${month}-1]+' ${year}</h4><a href="#" class="btn btn-greendark permissionGSTN_Actions-File_GST_Returns <c:if test="${otperror eq varYes || empty client.status || client.status eq statusFiled}">disable</c:if>" onclick="evcFilingOTP()" id="idEVCBtn">File <c:choose><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR4}">GSTR4</c:when><c:when test="${returntype eq varGSTR6}">GSTR6</c:when><c:when test="${returntype eq varGSTR5}">GSTR5</c:when><c:otherwise>GSTR1</c:otherwise></c:choose> with EVC</a><a href="#" class="btn btn-greendark permissionGSTN_Actions-File_GST_Returns <c:if test="${otperror eq varYes || empty client.status || client.status eq statusFiled}">disable</c:if>" data-toggle="modal" data-target="#fileReturnModal" id="idTrueCopyBtn">File <c:choose><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR4}">GSTR4</c:when><c:when test="${returntype eq varGSTR6}">GSTR6</c:when><c:when test="${returntype eq varGSTR5}">GSTR5</c:when><c:otherwise>GSTR1</c:otherwise></c:choose> with DSC</a> <c:if test="${returntype eq varGSTR4 || returntype eq varGSTR6}"><a href="#" class="btn btn-greendark permissionOffset_Liability disable" onclick="showOffsetPopup()" id="idOffLiabBtn">Offset</a></c:if> <c:choose><c:when test="${returntype eq varGSTR4 || returntype eq varGSTR7 || returntype eq varGSTR8}"><a href="#" class="btn btn-greendark" onclick="proceedToFile()">Proceed To File</a></c:when><c:otherwise><a href="#" class="btn btn-greendark permissionGSTN_Actions-Submit_GST_Returns <c:if test="${otperror eq varYes || client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>" onclick="showSubmitPopup(\'${id}\')" id="idReturnSubmitBtn">Submit <c:choose><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR5}">GSTR5</c:when><c:when test="${returntype eq varGSTR6}">GSTR6</c:when><c:otherwise>GSTR1</c:otherwise></c:choose></a></c:otherwise></c:choose><button class="btn btn-primary" style="color:white; box-shadow:none;" onclick="fetchRetSummary(false,\'${returntype}\')">Refresh<i class="fa fa-refresh" id="refreshSummary" style="font-size: 15px; color: #fff; margin-left:5px"></i></button>');
		$(".tabtable5  div.toolbar").html('<h4 style="margin-top: 5px;margin-right: 10px;">HSN Filing Summary Of '+monthNames[${month}-1]+' ${year}</h4><div class="dataTables_length" id="dbHSNFilingTable_length"><label>Show <select name="dbHSNFilingTable_length" aria-controls="dbHSNFilingTable" class=""><option value="10">10</option><option value="25">25</option><option value="50">50</option><option value="100">100</option></select> entries</label></div>');$(".tabtable7  div.toolbar").html('<h4 style="margin-top: 5px;margin-right: 10px;">Document Issue Summary Of '+monthNames[${month}-1]+' ${year}</h4><div class="dataTables_length" id="dbHSNFilingTable_length"><label>Show <select name="dbHSNFilingTable_length" aria-controls="dbHSNFilingTable" class=""><option value="10">10</option><option value="25">25</option><option value="50">50</option><option value="100">100</option></select> entries</label></div>');
        $(".tabtable4  div.toolbar").html('<h4><c:choose><c:when test="${returntype eq varPurchase}">Purchase</c:when><c:when test="${returntype eq varGSTR2}">Purchase</c:when><c:when test="${returntype eq varGSTR6}">Purchase</c:when><c:otherwise>Sales</c:otherwise></c:choose> Summary Of '+monthNames[${month}-1]+' ${year}</h4>');
		var headertext = [],headers = document.querySelectorAll("table.display th"),tablerows = document.querySelectorAll("table.display th"),tablebody = document.querySelector("table.display tbody");
	   
	});
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;	var year = eDate.getFullYear();
		if(otherreturnType == 'DELIVERYCHALLANS' || otherreturnType == 'PROFORMAINVOICES' || otherreturnType == 'ESTIMATES' || otherreturnType == 'PurchaseOrder' || otherreturnType == 'SalesRegister' || otherreturnType == 'PurchaseRegister' || otherreturnType == 'Expenses'){window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${otherreturnType}"/>/'+month+'/'+year+'?type='+invoiceStatus;
		}else{window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>/'+month+'/'+year+'?type='+invoiceStatus;}
	}
</script>
<script type="text/javascript">
$('#sendMessageModal').on('hidden.bs.modal', function (e) {
	  $(this).find("input,textarea,select").val('').end();
	});
</script>
</html>