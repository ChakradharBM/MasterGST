<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Journals</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp"%>
	<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/dashboard-cas.css" media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/journal/journal.css" media="all" />
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
	<script src="${contextPath}/static/mastergst/js/common/table-rowgroup.js"></script>
    <script src="${contextPath}/static/mastergst/js/common/dataTables-rowGroups.js"></script>
    <script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
    <script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap.min.js"></script>
    <script src="${contextPath}/static/mastergst/js/common/datetimepicker.js"></script>
    <script src="${contextPath}/static/mastergst/js/client/payment.js" type="text/javascript"></script>
    <script src="${contextPath}/static/mastergst/js/journal/journal.js" type="text/javascript"></script>
    <style type="text/css">
    	 .invStatus_Deleted, .invStatus_Cancelled{text-decoration: line-through;text-decoration-color: red;}
    </style>
<script type="text/javascript">
var paymentslist=new Array();var voucherList=new Array();var contraList=new Array();var invoiceArray=new Object();var jdetails;var jdd=new Array();var journaltable;var debit='';var credit='';var cashbankledgername = new Array();
$(document).ready( function () {
	var date = new Date();
	month = '<c:out value="${month}"/>';
	year = '<c:out value="${year}"/>';
	if(month == null || month == '') {
		month = date.getMonth()+1;
		year = date.getFullYear();
	}
	var day = date.getDate();
	var mnt = date.getMonth()+1;
	var yr = date.getFullYear();
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
	var date = $('.jdpMonths').datepicker({
		autoclose: true,
		viewMode: 1,
		minViewMode: 1,
		format: 'mm-yyyy'
	}).on('changeDate', function(ev) {
		//updateReturnPeriod(ev.date);
		month = ev.date.getMonth()+1;
		year = ev.date.getFullYear();
	});
	$('.jdpMonths').datepicker('update', dateValue);
	var jtype = '<c:out value="${type}"/>';
	document.getElementById('filing_option').innerHTML = jtype;
	if(jtype == 'Monthly'){
		$('.jmonthely-sp').css("display", "inline-block");$('.jyearly-sp').css("display", "none");	
	}else{
		document.getElementById('yearlyoption').innerHTML=year+"-"+(parseInt(year)+1);
		$('.jyearly-sp').css("display", "inline-block");$('.jmonthely-sp').css("display", "none");
	}
	loadJournalInvTable('${id}', '${client.id}','${month}', '${year}',jtype);
	$.ajax({
		url: contextPath+"/allVoucherContra"+paymenturlSuffix+"/Voucher/"+Paymenturlprefix,
		async: true,
		cache: false,
		success : function(response){
		if(response.length > 0){
			$.each(response, function(index, voucherData) {
				var voucher = new Object();
				voucher.id = voucherData.userId;
				voucher.returnType = voucherData.returnType;
				voucher.totcramount = voucherData.totcramount;
				voucher.totdramount = voucherData.totdramount;
				voucher.voucherNumber = voucherData.voucherNumber;
				voucher.voucherDate = voucherData.voucherDate;
				voucher.vouchernotes = voucherData.vouchernotes;
				voucher.voucheritems = voucherData.voucheritems;
				voucherList.push(voucher);
			});
		}
	}
});
$.ajax({
	url: contextPath+"/allVoucherContra"+paymenturlSuffix+"/Contra/"+Paymenturlprefix,
	async: true,
	cache: false,
	success : function(response){
	if(response.length > 0){
		$.each(response, function(index, contraData) {
			var contra = new Object();
			contra.id = contraData.userId;
			contra.returnType = contraData.returnType;
			contra.totcramount = contraData.totcramount;
			contra.totdramount = contraData.totdramount;
			contra.contraNumber = contraData.contraNumber;
			contra.contraDate = contraData.contraDate;
			contra.contranotes = contraData.contranotes;
			contra.contraitems = contraData.contraitems;
			contraList.push(contra);
		});
	}
}
});
	var otherconfigs = '<c:out value="${othrconfigs.enableDrcr}"/>';
	var client_id = '<c:out value="${client_id}"/>';
	var customers=new Array();
	var index = 1;
	var debitTotal = 0.00;
	var creditTotal = 0.00;
	if(otherconfigs == "false"){
		debit = "By";
		credit = "To";
	}else{
		debit = "Dr";
		credit = "Cr";
	}
	var journalid = "";var journalinvoiceNumber = "";var journalvoucherNumber = "";
	var journalreturntype = "";
	var jsptype = 'journal';
	
	  
	  $('.dataTables_filter input').addClass('searchclass');
	  $('#debit').html(debitTotal);
	  $('#credit').html(creditTotal);
	  $('.abcd').parent().css('border-bottom','1px dashed lightgrey');
	  $('.leftindent').parent().parent().css('line-height','0.5');
	  $('.rightindent').parent().parent().css('line-height','0.5');
	  $('.rtType').css('margin-top','10px');
	  $('.srtType').css('margin-top','5px');
	  $(".indformat").each(function(){ 
		   $(this).html($(this).html().replace(/,/g , ''));
	  });
	  $(".indformats").each(function(){ 
		   $(this).html($(this).html().replace(/,/g , ''));
	  });
	  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	  OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
	  
	  
	  $('.searchclass ').on( "keyup", function() {
		  $('.abcd').parent().css('border-bottom','1px dashed lightgrey');
		  $('.leftindent').parent().parent().css('line-height','0.5');
		  $('.rightindent').parent().parent().css('line-height','0.5');
		  $('.rtType').css('margin-top','10px');
		  $('.srtType').css('margin-top','5px');
		  $(".indformat").each(function(){
			   
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		  $(".indformats").each(function(){ 
			   $(this).html($(this).html().replace(/,/g , ''));
		  });
		  OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
		  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	  });
	  $('.sorting_asc,.sorting_desc').on( "click", function() {
		  $('.abcd').parent().css('border-bottom','1px dashed lightgrey');
		  $('.leftindent').parent().parent().css('line-height','0.5');
		  $('.rightindent').parent().parent().css('line-height','0.5');
		  $('.rtType').css('margin-top','10px');
		  $('.srtType').css('margin-top','5px');
		  $(".indformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		  $(".indformats").each(function(){ 
			   $(this).html($(this).html().replace(/,/g , ''));
		  });
		  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
		  OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
	  });
	  $('select[name="dbTable1_length"]').on('change', function(){
		  $('.abcd').parent().css('border-bottom','1px dashed lightgrey');
		  $('.leftindent').parent().parent().css('line-height','0.5');
		  $('.rightindent').parent().parent().css('line-height','0.5');
		  $('.rtType').css('margin-top','10px');
		  $('.srtType').css('margin-top','5px');
		  $(".indformat").each(function(){
			   
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		  $(".indformats").each(function(){ 
			   $(this).html($(this).html().replace(/,/g , ''));
		  });
		  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
		  OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
		});
	} );

$("div.toolbar").html('<h4>Journals</h4>');
</script>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	<!--- breadcrumb start -->
	<div class="breadcrumbwrap">
		<div class="container">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink"	link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" />
						<c:choose>
								<c:when
									test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when>
								<c:otherwise>Business</c:otherwise>
							</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink"
							link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose>
									<c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
									<c:otherwise>${client.businessname}</c:otherwise>
								</c:choose></a></li>
						<li class="breadcrumb-item active">Journals</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class="row" style="float: right;display: block;width: 100%;margin-right: -6px;margin-bottom: 14px;">
				<c:if test="${user.accessJournalExcel}">
					<a class="btn btn-blue-dark mr-2 pull-right" style="padding: 7px 20px;" href="${contextPath}/journalsExcelDownload/${id}/${client.id}/${month}/${year}/${type}">Excel</a>
				</c:if>
				<a href="#" class="btn btn-blue-dark mr-2 pull-right" style="padding:7px 20px" data-toggle="modal" data-target="#voucherModal">Add Voucher</a>
				<a href="#" class="btn btn-blue-dark mr-2 pull-right" style="padding:7px 20px" data-toggle="modal" data-target="#ContraModal">Add Contra</a>
			</div>
			<div>
			<div class="chooseperiod mr-0"><label style="margin-bottom: 3px;margin-right:12px">Report Type:</label>
			<div class="dropdown" style="display: inline-block;"><span class="dropdown-toggle" data-toggle="dropdown" id="fillingoption">
					<div class="typ-ret">
						<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
						<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" style="vertical-align: super;"></i></span>
					</div>
				</Span>
				<div class="dropdown-menu ret-type">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
				</div>
				</div>
				<span class="jmonthely-sp" style="display: inline-flex;" id="jmonthely-sp"><span><label id="ret-period" style="margin-bottom: 3px;margin-right:12px">Return Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right" style="display:inline-flex;">
						<div class="input-group date jdpMonths" id="jdpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div><a href="#" class="btn btn-greendark pull-right" role="button"	style="padding: 4px 10px;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: none" class="jyearly-sp"> 
				<label id="ret-period" style="margin-bottom: 3px;margin-right:12px">Return Period:</label><div class="dropdown" style="display: inline-block;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<div class="typ-ret">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="jfinancialYear1">
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div></div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase" onClick="getdiv()">Generate</a>
				</span>
			</div>
		
		
		<h6 style="display: inline-block;">Journal Summary</h6>
		</div>
		
		
		<div class="invoicesumwrap journalsumwrap mt-3 mb-3" style="box-shadow: 0 0 2px 2px #eff3f6;">  
					<ul class="invoicesum journalsum" style="border-bottom:unset!important">
						<li style="padding:11px 100px;width: 50%;"><h5>Total Debit</h5><p id="debit" class="indformats text-center mt-1" style="font-size:20px">0.00</p></li>
						<li style="padding:11px 100px;width: 50%;"><h5>Total Credit</h5><p id="credit" class="indformats text-center mt-1" style="font-size:20px">0.00</p></li>
					</ul>
				</div>
				<div class="col-md-12 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display dataTable journaltable meterialform" cellspacing="0" width="100%">
						
						</table>
                </div>
		</div>
	</div>
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3 id="delheader">Delete Voucher </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete <span id="delitem">Voucher </span> <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Customer</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>
var vrowCount = 1;
	function showDeletePopup(journalId, returnType) {
		$('#deleteModal').modal('show');
		$('#delheader').html("Delete "+returnType+"");
		$('#delitem').html(""+returnType+"");
		$('#btnDelete').html("Delete "+returnType+"").attr('onclick', "deleteJournal('"+journalId+"')");
	}
	function deleteJournal(journalId) {
		$.ajax({
			url: "${contextPath}/deljournal/"+journalId,
			success : function(response) {
				location.href = '${contextPath}/journaldetails/${id}/${fullname}/${usertype}/${clientid}/${month}/${year}?type=';
			}
		});
	}
	function updateInvoiceDetails(invoice,rettype) {
		var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0,  totalITCIGST1 = 0, totalITCCGST1 = 0, totalITCSGST1 = 0, totalITCCESS1 = 0, totalinvitc = 0, totalExempted = 0;
		invoice.id = invoice.userid;
		invoice.invoicetype = invoice.invtype;
		if(invoice.invoiceno == null){
			invoice.invoiceno = '';
		}
		if(invoice.invoiceEcomGSTIN == null){
			invoice.invoiceEcomGSTIN = '';
		}
		if(invoice.tdstcsenable == null){
			invoice.tdstcsenable = 'false';
		}
		if(invoice.section == null){
			invoice.section = '';
		}
		if(invoice.tcstdspercentage == null){
			invoice.tcstdspercentage = 0.00;
		}
		if(invoice.tcstdsAmount == null){
			invoice.tcstdsAmount = 0.00;
		}
		if(invoice.netAmount == null){
			invoice.netAmount = 0.00;
		}
		if(invoice.revchargetype == null){
			invoice.revchargetype = 'Regular';
		}
		if(invoice.ledgerName == null){
			invoice.ledgerName = '';
		}
		if(invoice.dealerType == null){
			invoice.dealerType = '';
		}
		if(invoice.vendorName == null){
			invoice.vendorName = '';
		}
		invoice.serialnoofinvoice = invoice.invoiceno;
		if(invoice.b2b && invoice.b2b.length > 0) {
			if(invoice.b2b[0].ctin == null){
				invoice.b2b[0].ctin = '';
			}
			invoice.billedtogstin = invoice.b2b[0].ctin;
			if(invoice.b2b[0].inv && invoice.b2b[0].inv.length > 0) {
				invoice.address = invoice.b2b[0].inv[0].address;
				invoice.invTyp = invoice.b2b[0].inv[0].invTyp;
			}
		} else {
			invoice.billedtogstin = '';
			invoice.address = '';
		}
		if(invoice.pi && invoice.pi.length > 0){
			var expDate = invoice.pi[0].expirydate;
			expDate = expDate.split('-'); 
			invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
		}else if(invoice.est && invoice.est.length > 0){
			var expDate = invoice.est[0].expirydate;
			expDate = expDate.split('-');
			invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
		}
		if(invoice.po && invoice.po.length > 0){
			var deliveryDate = invoice.po[0].deliverydate;
			deliveryDate = deliveryDate.split('-');
			invoice.deliverydate = deliveryDate[0] + "/" + deliveryDate[1] + "/" + deliveryDate[2];
		}
		
		if(invoice.dc && invoice.dc.length > 0){
			invoice.challantype = invoice.dc[0].challanType;
		}
		
		if(invoice.diffPercent == null){
			invoice.diffPercent = 'No';
		}
		if(invoice.billDate != null){
			var billDate = new Date(invoice.billDate);
			var day = billDate.getDate() + "";
			var month = (billDate.getMonth() + 1) + "";
			var year = billDate.getFullYear() + "";
			day = checkZero(day);
			month = checkZero(month);
			year = checkZero(year);
			invoice.billDate = day + "/" + month + "/" + year;
		}
		if(invoice.dateofinvoice != null){
			var invDate = new Date(invoice.dateofinvoice);
			var day = invDate.getDate() + "";
			var month = (invDate.getMonth() + 1) + "";
			var year = invDate.getFullYear() + "";
			day = checkZero(day);
			month = checkZero(month);
			year = checkZero(year);
			invoice.dateofinvoice = day + "/" + month + "/" + year;
		}else{invoice.dateofinvoice = "";}
		if(invoice.dueDate != null){
			var invDate = new Date(invoice.dueDate);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			invoice.dueDate = day + "/" + month + "/" + year;
		}else{invoice.dueDate = "";}
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
			if(invoice.bankDetails.accountName == null){
				invoice.accountname = '';
			}else{
				invoice.accountname = invoice.bankDetails.accountName;
			}
			} else {
			invoice.bankdetails = 'false';
		}
		if(invoice.fullname == null) {
			invoice.fullname = '';
		}
		if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
			invoice.invoiceCustomerIdAndBilledToName = '';
		}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
			invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
		}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
			invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
		}
		if(invoice.invoiceCustomerId == null) {
			invoice.invoiceCustomerId = '';
		}
		if(invoice.billedtoname == null) {
			invoice.billedtoname = '';
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
		if(invoice.totaltax) {
		} else {
			invoice.totaltax = 0.00;
		}
		if(invoice.totalamount) {
		} else {
			invoice.totalamount = 0.00;
		}
		if(('<%=MasterGSTConstants.B2C%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) || ('<%=MasterGSTConstants.B2CSA%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0)) {
			invoice.splyTy = invoice.b2cs[0].splyTy;
			invoice.ecommercegstin = invoice.b2cs[0].etin;
		}
		if(('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.at && invoice.at.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.at && invoice.at.length > 0)) {
			invoice.splyTy = invoice.at[0].splyTy;
		}else if(('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.txi && invoice.txi.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.txi && invoice.txi.length > 0)){
			invoice.splyTy = invoice.txi[0].splyTy;
		}
		if(('<%=MasterGSTConstants.ATPAID%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0)) {
			invoice.splyTy = invoice.txpd[0].splyTy;
			invoice.invtype = 'Adv. Adjustments';
		}
		if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
			invoice.voucherNumber = invoice.cdn[0].nt[0].inum;
			invoice.ntty = invoice.cdn[0].nt[0].ntty;
			invoice.rsn = invoice.cdn[0].nt[0].rsn;
			invoice.pGst = invoice.cdn[0].nt[0].pGst;
			if(invoice.cdn[0].nt[0].idt != null){
				invoice.voucherDate = invoice.cdn[0].nt[0].idt;
			}else{
				invoice.voucherDate = '';
			}
			if(invoice.cdn[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdn[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
			
		} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
			invoice.voucherNumber = invoice.cdnr[0].nt[0].inum;
			invoice.ntty = invoice.cdnr[0].nt[0].ntty;
			invoice.rsn = invoice.cdnr[0].nt[0].rsn;
			invoice.pGst = invoice.cdnr[0].nt[0].pGst;
			if(invoice.cdnr[0].nt[0].idt != null){
				invoice.voucherDate = invoice.cdnr[0].nt[0].idt;
			}else{
				invoice.voucherDate = '';
			}
			if(invoice.cdnr[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
		}
		if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
			invoice.voucherNumber = invoice.cdnur[0].inum;
			if(invoice.cdnur[0].idt != null){
				invoice.voucherDate = invoice.cdnur[0].idt;
			}else{
				invoice.voucherDate = '';
			}
			invoice.ntty = invoice.cdnur[0].ntty;
			invoice.rsn = invoice.cdnur[0].rsn;
			invoice.pGst = invoice.cdnur[0].pGst;
			invoice.cdnurtyp = invoice.cdnur[0].typ;
			if(invoice.cdnur[0].ntty == 'C'){
				invoice.invtype = 'Credit Note(UR)';
			}else if(invoice.cdnur[0].ntty == 'D'){
				invoice.invtype = 'Debit Note(UR)';
			}
		}
		if(('<%=MasterGSTConstants.EXPORTS%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0) || ('<%=MasterGSTConstants.EXPA%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0)) {
			invoice.portcode = invoice.exp[0].inv[0].sbpcode;
			invoice.shippingBillNumber = invoice.exp[0].inv[0].sbnum;
			invoice.exportType = invoice.exp[0].expTyp;
			if(invoice.exp[0].inv[0].sbdt != null && invoice.exp[0].inv[0].sbdt != undefined){
				invoice.shippingBillDate = formatDate(invoice.exp[0].inv[0].sbdt);
			}else{
				invoice.shippingBillDate = '';
			}
		}
		if(('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) || ('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0)) {
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
		if(invoice.advOriginalInvoiceNumber == null) {
			invoice.advOriginalInvoiceNumber = 0.00;
		}
		if(invoice.advPCustname == null) {
			invoice.advPCustname = "";
		}
		if(invoice.advPInvamt == null) {
			invoice.advPInvamt = 0.00;
		}
		if(invoice.advPIgstamt == null) {
			invoice.advPIgstamt = 0.00;
		}
		if(invoice.advPCgstamt == null) {
			invoice.advPCgstamt = 0.00;
		}
		if(invoice.advPSgstamt == null) {
			invoice.advPSgstamt = 0.00;
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
						item.hsn=item.hsn.substring(0,item.hsn.indexOf(' :'));
					}
				}
				if(item.igstamount) {
					if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalIGST1 += item.igstamount;
							}else{
								totalIGST1 -=item.igstamount;
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalIGST1 -= item.igstamount;
							}else{
								totalIGST1 +=item.igstamount;
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalIGST1 +=item.igstamount;
								}else{
									totalIGST1 -=item.igstamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalIGST1 -=item.igstamount;
								}else{
									totalIGST1 +=item.igstamount;
								}
							}
						}else{
							totalIGST1 +=item.igstamount;
						}
					}
				} else {
					item.igstamount = 0.00;
				}
				if(item.cgstamount) {
					if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST1 -=item.cgstamount;
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST1 +=item.cgstamount;
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalCGST1 +=item.cgstamount;
								}else{
									totalCGST1 -=item.cgstamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalCGST1 -=item.cgstamount;
								}else{
									totalCGST1 +=item.cgstamount;
								}
							}
						}else{
							totalCGST1 +=item.cgstamount;
						}
					}
				} else {
					item.cgstamount = 0.00;
				}
				if(item.sgstamount) {
					if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST1 -=item.sgstamount;
								}
							}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST1 +=item.sgstamount;
								}
							}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
								if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST1 +=item.sgstamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST1 +=item.sgstamount;
								}
							}
							}else{
								totalSGST1 +=item.sgstamount;
							}
					}
				} else {
					item.sgstamount = 0.00;
				}
				if(item.cessamount) {
					if(invoice.gstStatus != 'CANCELLED'){
							if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS1 -=item.cessamount;
								}
							}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS1 +=item.cessamount;
								}
							}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
								if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
									if(invoice.cdnur[0].ntty == 'D'){
										totalCESS1 +=item.cessamount;
									}else{
										totalCESS1 -=item.cessamount;
									}
								}else{
									if(invoice.cdnur[0].ntty == 'C'){
										totalCESS1 -=item.cessamount;
									}else{
										totalCESS1 +=item.cessamount;
									}
								}
							}else{
								totalCESS1 +=item.cessamount;
							}
					}
				} else {
					item.cessamount = 0.00;
				}
				if(item.exmepted != null && item.quantity != null){
					if(invoice.gstStatus != 'CANCELLED'){
						
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(invoice.cdnur[0].ntty == 'C'){
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}else{
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						}
						
						
					}
				}
				
				if(item.discount == null) {
					item.discount = 0.00;
				}
				if(item.advreceived == null) {
					item.advreceived = 0.00;
				}
				if(item.advReceiptNo == null){
					item.advReceiptNo = "";
				}
				if(item.advReceiptDate == null){
					item.advReceiptDate = "";
				}
				if(item.advStateName == null){
					item.advStateName = "";
				}
				if(item.advReceivedAmount == null){
					item.advReceivedAmount = 0.00;
				}
				if(item.advAdjustableAmount == null){
					item.advAdjustableAmount = 0.00;
				}
				if(item.advadjustedAmount == null){
					item.advadjustedAmount = 0.00;
				}
				if(item.igstavltax) {
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCIGST1 +=item.igstavltax;
					}
				} else {
					item.igstavltax = 0.00;
				}
				if(item.cgstavltax) {
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCCGST1 +=item.cgstavltax;
					}
				} else {
					item.cgstavltax = 0.00;
				}
				if(item.sgstavltax) {
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCSGST1 +=item.sgstavltax;
					}
				} else {
					item.sgstavltax = 0.00;
				}
				if(item.cessavltax) {
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCCESS1 +=item.cessavltax;
					}
				} else {
					item.cessavltax = 0.00;
				}
				if(item.type) {
				} else {
					item.type = '';
				}
			});
		}
		invoice.totalitc = totalinvitc;
		if(invoice.totalitc) {
		} else {
			invoice.totalitc = 0.00;
		}
		invoice.igstamount = totalIGST1;
		invoice.cgstamount = totalCGST1;
		invoice.sgstamount = totalSGST1;
		invoice.cessamount = totalCESS1;
		invoice.igstavltax = totalITCIGST1;
		invoice.cgstavltax = totalITCCGST1;
		invoice.sgstavltax = totalITCSGST1;
		invoice.cessavltax = totalITCCESS1;
		invoice.totalExempted = totalExempted; 
		return invoice;
	}
	
	function getval(sel) {
		document.getElementById('filing_option').innerHTML = sel;
		if (sel == 'Yearly') {
			$('.jmonthely-sp').css("display", "none");$('.jyearly-sp').css("display", "inline");
		} else {
			$('.jmonthely-sp').css("display", "inline");$('.jyearly-sp').css("display", "none");
		}
	}
	function getdiv() {
		var abc = $('#fillingoption span').html();
		var jmonth = '<c:out value="${month}"/>';
		var jyear = '<c:out value="${year}"/>';
		if(abc == 'Yearly'){
			$('.jyearly-sp').css("display", "inline-block");$('.jmonthely-sp').css("display", "none");
			var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
			jyear = yrs1;
			var pUrl = "${contextPath}/getAddtionalJournalInvs/${id}/${client_id}/"+jmonth+"/"+jyear+"?type=Yearly";
			journaltable.ajax.url(pUrl).load();
		}else{
			$('.jmonthely-sp').css("display", "inline-block");$('.jyearly-sp').css("display", "none");	
			var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
			jyear = yr;
			jmonth = mn;
			var pUrl = "${contextPath}/getAddtionalJournalInvs/${id}/${client_id}/"+jmonth+"/"+jyear+"?type=Monthly";
			journaltable.ajax.url(pUrl).load();
		}
	}
	function updateYearlyOption(value){
		document.getElementById('yearlyoption').innerHTML=value;
	}
	$('.dropdown').on("click",function(){
	    $(this).find(".dropdown-menu").toggle();
	    $(this).siblings().find(".dropdown-menu").hide();
	  });
</script>
</body>
</html>