<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | Ledger Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>

<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/jquery-simple-tree-tables.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/ledger_report.css" media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery-simple-tree-table.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/reports/ledger_reports.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/table-rowgroup.js"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables-rowGroups.js"></script>
<script src="${contextPath}/static/mastergst/js/client/payment.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/journal/journal.js" type="text/javascript"></script>   
<script src="${contextPath}/static/mastergst/js/reports/ledgerReport_journal.js" type="text/javascript"></script>
<script>
	var paymentslist=new Array();
	var voucherList=new Array();var contraList=new Array();
	var invoiceArray=new Object();
</script>
</head>
<body>
<c:set value="${clientid}" var="clientid"/>
<c:set value="${id}" var="userid"/>
<c:set value="${fullname}" var="fullname"/>
<c:set value="${usertype}" var="usertype"/>
<c:set value="${month}" var="month"/>
<c:set value="${year}" var="year"/>

<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
<div class="breadcrumbwrap" >
	<div class="container bread">
		<div class="row">
	        <div class="col-md-12 col-sm-12">
			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
				<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
				<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
				<li class="breadcrumb-item active">Ledger Report</li>
			</ol>
			<div class="retresp"></div></div>
		</div>
	</div>
</div>
	<div class="db-ca-wrap monthely1">
		<div class="container">	
			<div id="processing" class="Process_text text-center"></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Ledger Report of ${client.businessname}</h4><p></p>
			<label>Ledger Name:</label>
			<select id="ledgername" class="ledrop_search" name="state" style="width:180px!important">
				<option value=""> - select ledger - </option>
				<c:forEach items="${ledgers}" var="ledger">
					<option value="<c:out value="${ledger.ledgerName}"/>"><c:out value="${ledger.ledgerName}"/></option>
				</c:forEach>
			</select>
			<span id="ledgername_error" style="color:red;"></span>
			<div class="chooseperiod mr-0 mt-0"><label style="margin-bottom: 3px;margin-right:12px">Report Type:</label>
			<div class="dropdown" style="display: inline-block;"><span class="dropdown-toggle" data-toggle="dropdown" id="fillingoption">
					<div class="typ-ret">
						<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
						<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" style="vertical-align: super;"></i></span>
					</div>
				</span>
				<div class="dropdown-menu ret-type">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item"	href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				</div>
				<span class="cmonthely-sp" style="display: inline-flex;" id="cmonthely-sp"><span><label id="ret-period" style="margin-bottom: 3px;margin-right:12px">Return Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right" style="display:inline-flex;">
						<div class="input-group date cdpMonths" id="cdpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div><a href="#" class="btn btn-greendark pull-right" role="button"	style="padding: 4px 10px;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: none" class="cyearly-sp"> 
				<label id="ret-period" style="margin-bottom: 3px;margin-right:12px">Return Period:</label><div class="dropdown" style="display: inline-block;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<div class="typ-ret">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="cfinancialYear1">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase" onClick="getdiv()">Generate</a></div>
				</span>
				<span class="datetimetxt ccustom-sp" style="display: none;z-index:2" id="ccustom-sp">
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="02-2012">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>	
					</div>
					<div class="datetimetxt datetime-wrap">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="02-2012">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
					</div> 	
				</span>
			</div>
			<div class="customtable db-ca-view mt-3">
					<table id="ledgerReportTable" class="row-border dataTable meterialform ledgerTab ledgerReportTable" cellspacing="0" width="100%" style="border:1px solid lightgray">
						<thead>
							<tr><th class="text-left">Date</th><th class="text-left">Particulars</th><th class="text-left">Voucher Type</th><th class="text-right">Voucher Number</th><th class="text-right">Debit</th><th class="text-right">Credit</th></tr>
						</thead>				
						<tbody id="ledgerReportTableTbody">
						<tr id="opnening"><td class="text-right"></td><td class="text-left" style="color:black!important;font-weight:bold;"><span class="mr-3"></span>Opening Balance</td><td class="text-right"></td><td class="text-right"></td><td class="text-right" style="color:black!important;font-weight:bold;"><span id="openingDebit"></span></td><td class="text-right" style="color:black!important;font-weight:bold;"><span id="openingCredit"></span></td></tr>
						<!-- <tr data-toggle="modal" data-target="#detailedModal"><td class="text-left">01-04-2019</td><td class="text-left"><span class="mr-3">To</span> GST SALE @ 5 %</td><td class="text-left">Gst Sale ( Story)</td><td class="text-right">MMS/19-20/17</td><td class="text-right" >33,327.00</td><td class="text-right">0.00</td></tr> -->
						</tbody>
						<tfoot>
							<tr><th class="text-right"></th><th class="text-left"><span class="mr-3"></span></th><th class="text-right"></th><th class="text-right">Current Total</th><th class="text-right" id="currentDebitTotal"></th><th class="text-right" id="currentCreditTotal"></th></tr>
							<tr><th class="text-right"></th><th class="text-left"><span class="mr-3"></span>Closing Balance</th><th class="text-right"></th><th class="text-right"></th><th class="text-right"><span id="closingDebit"></span></th><th class="text-right"><span id="closingCredit"></span></th></tr>
						</tfoot>
					</table>
				</div>
			</div>
			</div>
			<div class="modal fade" id="detailedModal" role="dialog" aria-labelledby="detailedModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document" style="width: 900px;">
            <div class="modal-content">
            <div class="modal-header p-0">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		         	<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
		         </button>
		         <div class="invoice-hdr bluehdr"><h3>Ledger Detailed View</h3></div>
            </div>
		      <div class="modal-body p-5">
		         <div class="meterialform row customtable db-ca-view">
				     	<table id="ledgerJournalsViewTable" class="row-border dataTable meterialform ledgerTab ledgerdetailedReportTable mb-4" cellspacing="0" width="100%" border="1" style="border:1px solid lightgray"></table>
					</div>
					<div class="modal-footer mt-3" style="position: absolute;bottom: 0px;right:0px"><a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a></div>
				</div>
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
	<div class="modal fade" id="successModal" role="dialog" aria-labelledby="successModal" aria-hidden="true">
	  <div class="modal-dialog col-6 modal-center" role="document">
	    <div class="modal-content">
	      <div class="modal-body">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"  onclick="successJournals()"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
	        <div class="invoice-hdr bluehdr">
	          <h3 id="delheader">Success</h3>
	        </div>
	        <div class=" pl-4 pt-4 pr-4">
	          <h6 class="text-success" id="h6data"></h6>
	          <p class="smalltxt" id="pdata">Number :<strong id="strongdata"></strong></p>
	        </div>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="successJournals()">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
</body>
<script type="text/javascript">
function getval(sel) {
	document.getElementById('filing_option').innerHTML = sel;
	if (sel == 'Custom') {
		$('.cmonthely-sp').css("display", "none");$('.cyearly-sp').css("display", "none");$('.ccustom-sp').css("display", "inline");
	} else if (sel == 'Yearly') {
		$('.cmonthely-sp').css("display", "none");$('.cyearly-sp').css("display", "inline");$('.ccustom-sp').css("display", "none");
	} else {
		$('.cmonthely-sp').css("display", "inline");$('.cyearly-sp').css("display", "none");$('.ccustom-sp').css("display", "none");
	}
}
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
}
var ctype = document.getElementById('filing_option').innerHTML;
if(ctype == 'Monthly'){
	$('.cmonthely-sp').css("display", "inline-block");$('.cyearly-sp').css("display", "none");	
}else{
	document.getElementById('yearlyoption').innerHTML=year+"-"+(parseInt(year)+1);
	$('.cyearly-sp').css("display", "inline-block");$('.cmonthely-sp').css("display", "none");
}
function getdiv(){
	var ledgername=$('#ledgername').val();
	if(ledgername == ""){
		$('#ledgername_error').html("please select ledger");
	}else{
		$('#ledgername_error').html("");
		var reporttype = $('#filing_option').html();
		var fUrl="";
		if(reporttype == 'Monthly'){
			var month=$('#monthly').val().split("-");
			fUrl="${contextPath}/getmonthlyledgersdata/${client.id}/"+month[0]+"/"+month[1]+"?ledgername="+ledgername;
			ajaxFunction(fUrl,ledgername);
		}else if (reporttype == 'Yearly') {
			var year=$('#yearlyoption').html().split("-");
			fUrl="${contextPath}/getyearlyledgersdata/${client.id}/"+year[0]+"/"+ledgername;
			ajaxFunction(fUrl,ledgername);
		}else{
			var fromtime=$('.fromtime').val();
			var totime=$('.totime').val();
			fUrl="${contextPath}/getcustomledgersdata/${client.id}/"+totime+"/"+fromtime+"/"+ledgername;
			ajaxFunction(fUrl,ledgername);
		}			
	}
}

function ajaxFunction(fUrl,ledgername){
	$.ajax({
		url: fUrl,
		async: true,
		cache: false,
		dataType:"json",
		beforeSend: function () {$('#processing').text('Processing...');},
		contentType: 'application/json',
		success : function(data) {
			ledgertabledata(data.journals,ledgername);
			ledgerClosingAndOpeningBalance(data.closingandOpeningamounts);
		},
		error:function(err){}
	});
}

function populateElement(id){
	$.ajax({
		url: "${contextPath}/getjournalsdata/"+id,
		async: true,
		cache: false,
		dataType:"json",
		beforeSend: function () {$('#processing').text('Processing...');},
		contentType: 'application/json',
		success : function(data) {
			var client_id = '<c:out value="${clientid}"/>';
			journalsView(data, client_id, 'ledgerreport');
		},
		error:function(err){}
	});
}

function successJournals(){
	window.location.href = '${contextPath}/ledgerreports/${userid}/${fullname}/${usertype}/${clientid}/${month}/${year}';
}
$('#detailedModal').on('hide.bs.modal', function () {
	if ($.fn.DataTable.isDataTable('#ledgerJournalsViewTable') ) {$('#ledgerJournalsViewTable').DataTable().destroy();}
});
</script>
</html>
