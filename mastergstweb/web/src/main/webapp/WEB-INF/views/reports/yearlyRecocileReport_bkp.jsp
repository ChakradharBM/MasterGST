<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Purchase Register & GTSR2A yearly Reconciliation Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/all-invoice-view.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<style>
.dropdown:hover .dropdown-content.reportSummary{display: block;}.arrow-up {width: 0; height: 14px; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportSummary{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 400px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
.months_na li{width:85px;text-align:center;border-right:1px solid #f0f2f3}
.months_na li:nth-child(9) , .months_na li:nth-child(10) ,.months_na li:nth-child(11) , .months_na li:nth-child(12){width:86px}
.months_na{background-color:white;width:100%;font-size:13px;list-style:none;display: inline-flex;padding-left: 0px;margin: 0px;padding-top: 6px;margin-left: 1px;}
#yearProcess{top:370px!important;}
#yearreconciletitle{text-transform: none;}
.reconciletablee #dbTable3_length{margin-left:0px!important}
</style>
<script type="text/javascript">
var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];var mannualMatchArray = new Array(),mannuallMatchArray = new Array(),supEmailids=new Array(),supCCEmailids=new Array();
function formatInvoiceDate(date){
	var invDate = new Date(date);
	var day = invDate.getDate() + "";
	var month = (invDate.getMonth() + 1) + "";
	var year = invDate.getFullYear() + "";
	day = checkZero(day);
	month = checkZero(month);
	year = checkZero(year);
	return day + "-" + mnths[month-1] + "-" + year;
}
function checkZero(data){
	if(data.length == 1){
		data = "0" + data;
  	}
	return data;
}
$(document).ready(function(){
	var year = new Date().getFullYear();
	var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
	var nextYear = year+1;
	var currentMonth=new Date().getMonth()+1;
	
});
	$('input.btaginput').tagsinput({  tagClass: 'big',  });
	$(document).on('click','.btn-remove-tag',function(){$('.bootstrap-tagsinput').html('');});  
	$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();	$('.bootstrap-tagsinput').append('<span class="tag label label-info">'+ t +'<span data-role="remove"></span></span>'); }); 
	function updateYearlyOption(value){document.getElementById('yearlyoption1').innerHTML=value;}
	function getval(sel){document.getElementById('filing_option1').innerHTML=sel;document.getElementById('filing_option2').innerHTML=sel;
	if(sel == 'Custom'){
	$('.monthely-sp').css("display","none");$('.yearly-sp').css("display","none");$('.custom-sp').css("display","inline-block");$('.dropdown-menu.ret-type').css("left","16%");
	}else if(sel == 'Yearly'){
	$('.monthely-sp').css("display","none");$('.yearly-sp').css("display","inline-block");$('.custom-sp').css("display","none");$('.dropdown-menu.ret-type').css("left","19%");
	}else {
	$('.monthely-sp').css("display","inline-block");$('.yearly-sp').css("display","none");$('.custom-sp').css("display","none");$('.dropdown-menu.ret-type').css("left","19%");
	}
};

function getdiv() {
	selMatchArray =new Array();supEmailids=new Array();supCCEmailids=new Array();
	gstMatchArray=new Array();gstnNotSelArray=new Array();sendMsgCount=0;
	var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
	$('#retperiod').text(fp);
	yearlyreconcile(yrs);
}
</script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<jsp:include page="/WEB-INF/views/client/otpverification.jsp"/>
  <div class="breadcrumbwrap">
  <div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">Purchase Register & GTSR2A yearly Reconciliation Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
  <div class="db-ca-wrap yearly1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>Purchase Register & GTSR2A yearly Reconciliation Report</h2><p>Financial year period invoices of Purchase Register.<br>Financial year starting to till now records of GSTR2A invoices.</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reportSummary" style="right:unset"> <span class="arrow-up"></span><span class="pl-2">Financial year period invoices of Purchase Register.</span><br><span class="pl-2">Financial year starting to till now records of GSTR2A invoices.</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			
			<div class="dropdown chooseteam mr-0" style="height: 32px">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<label>Report Type:</label>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px"> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					<!-- <a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a> -->
				</div> 
				<span style="display: inline-block;margin-bottom: 4px;" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Financial Year:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
				</span>
			</div>
			<div class=""></div>
			
			<div id="yearProcess" class="text-center"></div>
			<div style="margin-top:30px">
			<h4 class="hdrtitle" id="yearreconciletitle"><span class="summary_retperiod"><a href="#" data-toggle="modal" data-target="#reconcilemodal" style="color: #404144;text-decoration: underline;">Reconcile Summary</a></span> of FY <span id="retperiod"></span><span style="font-size:13px; color:#0dce2f; font-weight:bold;margin-left:3px"> <c:if test="${empty client.reconcileDate }"><span style="color:red;font-size:13px">You Haven't Reconciled Yet,</span></c:if><c:if test="${not empty client.reconcileDate }">Last Reconciled on: ${client.reconcileDate}</c:if> </span> <a href="#" onClick="reconcileYeralyGstr2a()" class="btn btn-blue" style="color: #404144;;margin-left:10px;font-size: 12px;float: right; position: absolute; padding: 2px 6px 0px;">Reconcile Now</a>
			<div class="dropdown" style="color: #404144;;margin-left:20px;font-size: 17px;float:right">Reconcile Rules<div class="dropdown-content" style="z-index:3;line-height: 2;"><span class="arrow-right"></span><div>Allowed Tax/Invoice Amount Difference for Reconcile<span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.reconcileDiff}">${clientConfig.reconcileDiff}  </c:when><c:otherwise>0.00</c:otherwise></c:choose> Rupees</span></br>Allowed days for Invoice Date Difference for Reconcile<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.allowedDays}"><fmt:formatNumber type="number" pattern="###" value="${clientConfig.allowedDays}" />  </c:when><c:otherwise>0</c:otherwise></c:choose> Days</span>
			</br>Considers as Matched if Invoice Number matches anywhere in the Invoice Number<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableInvoiceMatch}"><c:if test="${clientConfig.enableInvoiceMatch eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableInvoiceMatch eq false}"><span style="color:red">No</span></c:if> </c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "/"(Backward Slash)<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreSlash}"><c:if test="${clientConfig.enableIgnoreSlash eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreSlash eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "-"(Hyphen)<span class="colon" style="margin-left: 51px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreHyphen}"><c:if test="${clientConfig.enableIgnoreHyphen eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreHyphen eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "0"(Zero/O)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreZero}"><c:if test="${clientConfig.enableIgnoreZero eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreZero eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "I"(Capital i)<span class="colon" style="margin-left: 50px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreI}"><c:if test="${clientConfig.enableIgnoreI eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreI eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "l"(Small l)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreL}"><c:if test="${clientConfig.enableIgnoreL eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreL eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			<a href="#" onClick="moreReconcileConfig()" >Click here for more configurations....</a></div></div></div> 
			</h4>			
	<jsp:include page="/WEB-INF/views/client/mismatchfilter.jsp" />
	<h4 class="hdrtitle"style="position: relative;margin-top: -15px!important; z-index:2;"><!-- <a href="#" id="btnMisMatchReject" class="btn btn-red disable" onClick="updateMismatchDataNew(this,false)">Reject GSTR2A invoice(s)</a> --><a href="#" id="btnMisMatchAccept" class="btn btn-acceptgreen disable" onClick="updateMismatchDataNew(this,true)">Accept GSTR2A invoice(s)</a>
	<!-- <a href="${contextPath}/dwnldmismatchxls/${id}/${client.id}/<%=MasterGSTConstants.GSTR2%>/${month}/${year}" class="btn btn-blue permissionExcel_Download_In_Books_And_Returns-Reconcile">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>-->
	
	<!--<a id="downloadExcelLink" class="btn btn-blue pull-right permissionExcel_Download_In_Books_And_Returns-Reconcile" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>-->
	<div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Reconcile ml-2" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="downloadExcelLink">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="downloadExcelLinkAlldetails">All DETAILS DOWNLOAD</a></div></div></div>
	
	
	
	<a href="#" class="btn btn-blue sendmessage disable" data-toggle="modal" data-target="#sendMessageModal">Send Message</a> <a href="#" class="btn btn-blue reconModal mannualMatching disable" data-toggle="modal" data-target="#reconModal" onclick="mannualMatchingInv('${client.id}','yearly')">Manual Match</a></h4>
    <div class="customtable db-ca-view tabtable2 reconciletablee">
					<div class="customtable db-ca-view" style="margin-top: 65px;">
						<table id="dbTable3" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead><tr><th class="text-center"><div class="checkbox"> <label><input type="checkbox" id="checkMismatch" onClick="updateMainMisMatchSelection(this)"/><i class="helper"></i></label></div> </th><th> </th><th> Type</th><th class="text-center">Suppliers</th><th>Ret.Period</th><th class="text-center">Uploaded By</th><th class="text-center">Vertical</th><th class="text-center">customer id</th><th class="text-center">Invoice Number</th><th class="text-center">Invoice Date</th><th class="text-center">GSTN No</th><th class="text-center">Invoice Value</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Branch</th><th class="text-center">Status</th><th class="text-center">Notes</th></tr></thead>
							<tbody id="idMisMatchBody"></tbody>
						</table>
					</div>
					</div>
				</div>
		</div>
		</div>
		<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
        <div class=" p-2 steptext-wrap"><span class="pl-2">Financial year period invoices of Purchase Register.</span><br><span class="pl-2">Financial year starting to till now records of GSTR2A invoices.</span> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/views/client/alliview_modal.jsp" />
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
    <script src="${contextPath}/static/mastergst/js/client/alliviews.js" type="text/javascript"></script>
<script type="text/javascript">
var invoiceStatus='<c:out value="${type}"/>';var gstnnumber='<c:out value="${client.gstnnumber}"/>';var invoiceArray=new Object(),mismatchTable,dbSendMsgTable,mannualMatchtable,mannualViewMatchTable,selMatchArray=new Array();
var sendMsgCount=0;var gstMatchArray=new Array(),gstnNotSelArray=new Array(),gstnArray=new Array(),sendMsgArray=new Array(),sendAllMsgsArray=new Array();
var reconcileyearly='<c:out value="${yearlyreconcile}"/>';var rType='<c:out value="${returntype}"/>'; 
		$(function() {
			$( ".helpicon" ).hover(function() {$('.reportSummary').show();
			}, function() {$('.reportSummary').hide();
			});
			var clientname = '<c:out value="${client.businessname}"/>';
			var date = new Date();
		month = '<c:out value="${month}"/>';
		year = '<c:out value="${year}"/>';
		if(${month} < 4){year = year-1;}
		yearlyreconcile(year);
		if(reconcileyearly == 'yearly'){
			$('#reconcilemodal').modal('show');
		}
		var year2 = parseInt(year)+1;
		gstr2afilename = 'MGST_GSTR2A_Yearly_'+gstnnumber+'_'+year+'-'+year2;
		if(month == null || month == '') {
			month = date.getMonth()+1;
			year = date.getFullYear();
		}
		var day = date.getDate();
		var mnt = date.getMonth()+1;
		var yr = date.getFullYear();
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
		var date = $('.dpMonths').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy'
		}).on('changeDate', function(ev) {
			updateReturnPeriod(ev.date);
			month = ev.date.getMonth()+1;
			year = ev.date.getFullYear();
		});
		$('.dpCustom').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			updateReturnPeriod(ev.date);
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpCustom1').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			updateReturnPeriod(ev.date);
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpMonths').datepicker('update', dateValue);
		$('.dpCustom').datepicker('update', customValue);
		$('.dpCustom1').datepicker('update', customValue);
		var yearval = "";
		if(${month} < 4){var nxtyear = ${year};var yearval = ${year}-1+"-"+nxtyear;}else{var nxtyear = ${year}+1;var yearval = ${year}+"-"+nxtyear;}
		document.getElementById('yearlyoption1').innerHTML=yearval;
		$('#retperiod').text(yearval);
		$.ajax({
			url: "${contextPath}/cp_users/${id}/${client.id}",
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if (response.length > 0) {
					$("#MMmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
					response.forEach(function(cp_user) {
						$("#MMmultiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
					});
				}else{
					$("#MMmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				}
				$('#MMmultiselect3').multiselect({
					nonSelectedText: '- User -',
					includeSelectAllOption: true,
					onChange: function(element, checked) {
						applyMMFilters();
					},
					onSelectAll: function() {
						applyMMFilters();
					},
					onDeselectAll: function() {
						applyMMFilters();
					}
				});
				
				$("#MMmultiselect3").multiselect('rebuild');
			}
		});
		$('#multiselectyearGSTR14').multiselect({
			nonSelectedText: '- Suppliers -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR1','year');
			},
			onSelectAll: function() {
				applyFilters('GSTR1','year');
			},
			onDeselectAll: function() {
				applyFilters('GSTR1','year');
			}
		});
		
		$('#divFiltersyearlyGSTR1').on('click', '.deltag', function(e) {
			var val = $(this).data('val');
			$('#multiselectyearGSTR12').multiselect('deselect', [val]);
			$('#multiselectyearGSTR13').multiselect('deselect', [val]);
			$('#multiselectyearGSTR14').multiselect('deselect', [val]);
			$('#multiselectyearGSTR15').multiselect('deselect', [val]);
			$('#multiselectyearGSTR16').multiselect('deselect', [val]);
			applyFilters('GSTR1','year');
		});
		
		<c:forEach items="${client.branches}" var="branch">
			$("#multiselectyearGSTR15,#multiselectcustomGSTR15").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
		</c:forEach>
		<c:forEach items="${client.verticals}" var="vertical">
			$("#multiselectyearGSTR16,#multiselectcustomGSTR16").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
		</c:forEach>
		$('#multiselectyearGSTR12').multiselect({
			nonSelectedText: '- Invoice Type -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR1','year');
			},
			onSelectAll: function() {
				applyFilters('GSTR1','year');
			},
			onDeselectAll: function() {
				applyFilters('GSTR1','year');
			}
		});
		$('#multiselectcustomGSTR12').multiselect({
			nonSelectedText: '- Invoice Type -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR1','custom');
			},
			onSelectAll: function() {
				applyFilters('GSTR1','custom');
			},
			onDeselectAll: function() {
				applyFilters('GSTR1','custom');
			}
		});
		$('#multiselectyearGSTR15').multiselect({
			nonSelectedText: '- Branches -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR1','year');
			},
			onSelectAll: function() {
				applyFilters('GSTR1','year');
			},
			onDeselectAll: function() {
				applyFilters('GSTR1','year');
			}
		});
		
		$('#multiselectyearGSTR16').multiselect({
			nonSelectedText: '- Verticals -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR1','year');
			},
			onSelectAll: function() {
				applyFilters('GSTR1','year');
			},
			onDeselectAll: function() {
				applyFilters('GSTR1','year');
			}
		});
});
		
		$(".reportTable2  div.toolbar").html('<h4>Monthly Summary of BVM</h4>');  
		   var headertext = [],
		     headers = document.querySelectorAll("table.display th"),
		     tablerows = document.querySelectorAll("table.display th"),
		     tablebody = document.querySelector("table.display tbody");
		   for (var i = 0; i < headers.length; i++) {
		     var current = headers[i];
		     headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
		   }
		   for (var i = 0, row; row = tablebody.rows[i]; i++) {
		     for (var j = 0, col; col = row.cells[j]; j++) {
		        col.setAttribute("data-th", headertext[j]);
		     }
		   }
		  OSREC.CurrencyFormatter.formatAll({
				selector: '.ind_formatss'
		});
	function formatDate(date) {
		if(date == null || typeof(date) === 'string' || date instanceof String) {
			return date;
		} else {
			var d = new Date(date),
				month = '' + (d.getMonth() + 1),
				day = '' + d.getDate(),
				year = d.getFullYear();

			if (month.length < 2) month = '0' + month;
			if (day.length < 2) day = '0' + day;

			return [day, month, year].join('-');
		}
	}
function updateInvoiceDetails(invoice) {
	var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0;
		invoice.id = invoice.userid;
		invoice.invoicetype = invoice.invtype;
		if(invoice.invoiceno == null){
			invoice.invoiceno = '';
		}
		invoice.serialnoofinvoice = invoice.invoiceno;
		if(invoice.b2b && invoice.b2b.length > 0) {
			if(invoice.b2b[0].ctin == null){
				invoice.b2b[0].ctin = '';
			}
			invoice.billedtogstin = invoice.b2b[0].ctin;
			if(invoice.b2b[0].inv && invoice.b2b[0].inv.length > 0) {
				invoice.address = invoice.b2b[0].inv[0].address;
			}
		} else {
			invoice.billedtogstin = '';
			invoice.address = '';
		}
		var invDate = new Date(invoice.dateofinvoice);
		var day = invDate.getDate() + "";
		var inv_month = (invDate.getMonth() + 1) + "";
		var year = invDate.getFullYear() + "";
		day = checkZero(day);
		inv_month = checkZero(inv_month);
		year = checkZero(year);
		invoice.dateofinvoice = day + "/" + inv_month + "/" + year;
		var fp=invoice.fp;
		var month=fp.substring(0,2);
		if(invoice.bankDetails) {
			if(invoice.bankDetails.bankname == '' && invoice.bankDetails.accountnumber == '' && invoice.bankDetails.branchname == '' && invoice.bankDetails.ifsccode == ''){
				invoice.bankdetails = 'false';
			}else{
				invoice.bankdetails = 'true';
			}	
			invoice.bankname = invoice.bankDetails.bankname;
			invoice.accountnumber = invoice.bankDetails.accountnumber;
			invoice.branchname = invoice.bankDetails.branchname;
			invoice.ifsccode = invoice.bankDetails.ifsccode;
		} else {
			invoice.bankdetails = 'false';
		}
		if(invoice.billedtoname == null) {
			invoice.billedtoname = '';
		}
		if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
			invoice.invoiceCustomerIdAndBilledToName = '';
		}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
			invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
		}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
			invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
		}
		if(invoice.fullname == null) {
			invoice.fullname = '';
		}
		if(invoice.branch == null) {
			invoice.branch = '';
		}
		if(invoice.vertical == null) {
			invoice.vertical = '';
		}
		if(invoice.totaltaxableamount) {
		} else {
			invoice.totaltaxableamount = 0.00;
		}
		if(invoice.totalitc) {
		} else {
			invoice.totalitc = 0.00;
		}
		if(invoice.totaltax) {
		} else {
			invoice.totaltax = 0.00;
		}
		if(invoice.totalamount) {
		} else {
			invoice.totalamount = 0.00;
		}
		if('<%=MasterGSTConstants.B2C%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) {
			invoice.splyTy = invoice.b2cs[0].splyTy;
			invoice.ecommercegstin = invoice.b2cs[0].etin;
		}
		if('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.at && invoice.at.length > 0) {
			invoice.splyTy = invoice.at[0].splyTy;
		}
		if('<%=MasterGSTConstants.ATPAID%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) {
			invoice.splyTy = invoice.txpd[0].splyTy;
		}
		if('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) {
			invoice.voucherNumber = invoice.cdn[0].nt[0].ntNum;
			invoice.ntty = invoice.cdn[0].nt[0].ntty;
			invoice.rsn = invoice.cdn[0].nt[0].rsn;
			invoice.pGst = invoice.cdn[0].nt[0].pGst;
			if(invoice.cdn[0].nt[0].ntDt != null){
				invoice.voucherDate = formatDate(invoice.cdn[0].nt[0].ntDt);
			}else{
				invoice.voucherDate = '';
			}
		} else if('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) {
			invoice.voucherNumber = invoice.cdnr[0].nt[0].ntNum;
			invoice.ntty = invoice.cdnr[0].nt[0].ntty;
			invoice.rsn = invoice.cdnr[0].nt[0].rsn;
			invoice.pGst = invoice.cdnr[0].nt[0].pGst;
			if(invoice.cdnr[0].nt[0].ntDt != null){
				invoice.voucherDate = formatDate(invoice.cdnr[0].nt[0].ntDt);
			}else{
				invoice.voucherDate = '';
			}
		}
		if('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) {
			invoice.voucherNumber = invoice.cdnur[0].ntNum;
			if(invoice.cdnur[0].ntDt != null){
				invoice.voucherDate = formatDate(invoice.cdnur[0].ntDt);
			}else{
				invoice.voucherDate = '';
			}
			invoice.ntty = invoice.cdnur[0].ntty;
			invoice.rsn = invoice.cdnur[0].rsn;
			invoice.pGst = invoice.cdnur[0].pGst;
			invoice.cdnurtyp = invoice.cdnur[0].typ;
		}
		if('<%=MasterGSTConstants.EXPORTS%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0) {
			invoice.portcode = invoice.exp[0].inv[0].sbpcode;
			invoice.shippingBillNumber = invoice.exp[0].inv[0].sbnum;
			invoice.exportType = invoice.exp[0].expTyp;
			if(invoice.exp[0].inv[0].sbdt != null && invoice.exp[0].inv[0].sbdt != undefined){
				invoice.shippingBillDate = formatDate(invoice.exp[0].inv[0].sbdt);
			}else{
				invoice.shippingBillDate = '';
			}
		}
		if('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) {
			invoice.isdDocty = invoice.isd[0].doclist[0].isdDocty;
			invoice.documentNumber = invoice.isd[0].doclist[0].docnum;
			if(invoice.isd[0].doclist[0].docdt != null && invoice.isd[0].doclist[0].docdt != undefined){
				invoice.documentDate = formatDate(invoice.isd[0].doclist[0].docdt);
			}else{
				invoice.documentDate = '';
			}
		}
		if('<%=MasterGSTConstants.IMP_GOODS%>' == invoice.invtype && invoice.impGoods && invoice.impGoods.length > 0) {
			invoice.isSez = invoice.impGoods[0].isSez;
			invoice.stin = invoice.impGoods[0].stin;
			invoice.impBillNumber = invoice.impGoods[0].boeNum;
			if(invoice.impGoods[0].boeDt != null){
				invoice.impBillDate = formatDate(invoice.impGoods[0].boeDt);
			}else{
				invoice.impBillDate = '';
			}
			invoice.boeVal = invoice.impGoods[0].boeVal;
			invoice.impPortcode = invoice.impGoods[0].portCode;
		}
		if(invoice.items) {
			invoice.items.forEach(function(item) {
				if(item.rate == null) {
					if(item.igstrate) {
						item.rate = item.igstrate;
					} else if(item.cgstrate) {
						item.rate = 2*item.cgstrate;
					}
				}
				if(item.hsn) {
					item.code = item.hsn;
					if(item.hsn.indexOf(':') > 0) {
						item.hsn=item.hsn.substring(0,item.hsn.indexOf(':'));
					}
				}
				if(item.igstamount) {
					totalIGST1 +=item.igstamount;
				} else {
					item.igstamount = 0.00;
				}
				if(item.cgstamount) {
					totalCGST1 +=item.cgstamount;
				} else {
					item.cgstamount = 0.00;
				}
				if(item.sgstamount) {
					totalSGST1 +=item.sgstamount;
				} else {
					item.sgstamount = 0.00;
				}
				if(item.cessamount) {
					totalCESS1 +=item.cessamount;
				} else {
					item.cessamount = 0.00;
				}
				if(item.discount == null) {
					item.discount = 0.00;
				}
				if(item.advreceived == null) {
					item.advreceived = 0.00;
				}
				if(item.type) {
					
				} else {
					item.type = '';
				}
			});
		}
		invoice.igstamount = totalIGST1;
		invoice.cgstamount = totalCGST1;
		invoice.sgstamount = totalSGST1;
		invoice.cessamount = totalCESS1;
		return invoice;
	}
function yearlyreconcile(year){
	$('#yearProcess').text('Processing...');
	invoiceArray['prFY'] = new Array();
	var fyUrl = "${contextPath}/getPurchaseRegisterinvs/${id}/${client.id}/Purchase Register/${month}/"+year;
		$.ajax({
			url: fyUrl,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			beforeSend: function () {$('#yearProcess').text('Processing...');},
			success : function(fyData) {	
				var fyList = null;
				if(fyData.content) {
					fyList = fyData.content;
				} else {
					fyList = fyData;
				}
				if(fyList instanceof Array) {
					if(fyList.length > 0) {
						fyList.forEach(function(fyInv){
							JSON.stringify(fyInv);
							var fyInvoice = updateInvoiceDetails(fyInv);
							invoiceArray['prFY'].push(fyInvoice);
						});
					}
				}
							
				invoiceArray['g2FYMatched'] = new Array();
				var fyUrl = "${contextPath}/getgstr2Matchedfyinvss/${id}/${client.id}/GSTR2/${month}/"+year;
				$.ajax({
					url: fyUrl,
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(fyData) {
						var fyList = null;
						if(fyData.content) {
							fyList = fyData.content;
						} else {
							fyList = fyData;
						}
						if(fyList instanceof Array) {
							if(fyList.length > 0) {
								fyList.forEach(function(fyInv){
									var fyInvoice = updateInvoiceDetails(fyInv);
									invoiceArray['g2FYMatched'].push(fyInvoice);
								});
							}
						}
					},
					error : function(data) {
						updateMismatchDataabcd();
					}
				});
				invoiceArray['g2MannualFYMatched'] = new Array();
				var fyUrl = "${contextPath}/getgstr2MannualMatchedfyinvss/${id}/${client.id}/GSTR2/${month}/"+year;
				$.ajax({
					url: fyUrl,
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(fyData) {
						var fyList = null;
						if(fyData.content) {
							fyList = fyData.content;
						} else {
							fyList = fyData;
						}
						if(fyList instanceof Array) {
							if(fyList.length > 0) {
								fyList.forEach(function(fyInv){
									var fyInvoice = updateInvoiceDetails(fyInv);
									invoiceArray['g2MannualFYMatched'].push(fyInvoice);
								});
							}
						}
					},
					error : function(data) {
						updateMismatchDataabcd();
					}
				});
				invoiceArray['gPMannualFYMatched'] = new Array();
				var fyUrl = "${contextPath}/getMannualMatchedfyinvss/${id}/${client.id}/GSTR2/${month}/"+year;
				$.ajax({
					url: fyUrl,
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(fyData) {
						var fyList = null;
						if(fyData.content) {
							fyList = fyData.content;
						} else {
							fyList = fyData;
						}
						if(fyList instanceof Array) {
							if(fyList.length > 0) {
								fyList.forEach(function(fyInv){
									var fyInvoice = updateInvoiceDetails(fyInv);
									invoiceArray['gPMannualFYMatched'].push(fyInvoice);
								});
							}
						}
					},
					error : function(data) {
						updateMismatchDataabcd();
					}
				});
				updateMismatchDataabcd();
			},
			error : function(data) {
				updateMismatchDataabcd();
			}
			});
		var dwnldExcel = "${contextPath}/downloadReconsileInvsExcel/${id}/${client.id}/${month}/"+year+"?dwnldtype=invoicewise";
		var alldetailsdwnldExcel = "${contextPath}/downloadReconsileInvsExcel/${id}/${client.id}/${month}/"+year+"?dwnldtype=alldetailswise";
		$('#downloadExcelLink').attr('href',dwnldExcel);
		$('#downloadExcelLinkAlldetails').attr('href',alldetailsdwnldExcel);
}
	function reconcileYeralyGstr2a(){
		var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
		$('.reconcile_btn').addClass("btn-loader");
		$('.reconcile_btn').html("Reconciling...");
		$.ajax({
			url : contextPath+'/subscriptionCheck/${client.id}/${id}',
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response.status_cd == '0') {
					$('.reconcile_btn').removeClass("btn-loader");
					 $('.reconcile_btn').html("Reconcile");
					if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
						if(varUserType == 'suvidha' || varUserType == 'enterprise'){
							errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
						}else{
							errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
						}
					}else {
						errorNotification(response.status_desc);
					}
				}else{
						window.location.href = '${contextPath}/reconcileyearlyinv/${id}/${fullname}/${usertype}/${client.id}/GSTR2A/4/'+yrs;
				}
			}
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
				window.location.href = '${contextPath}/reports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year+'?type=yearlyRecocileReport'
			},
			error : function(e, status, error) {if(e.responseText) {errorNotification(e.responseText);}}
		});
	}
	$(document).on('mouseover','#tot_tax', function (event) {
		$(this).find('#tax_tot_drop1').css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
	}).on('mouseleave','#tot_tax',  function(){
	    	$(this).next().css("display","none");
	    	$(this).find("#tax_tot_drop1").stop(true, true).delay(100).fadeOut(300);
	   }); 
</script>
</body>
</html>