<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | TDS Report</title>
    <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
    <%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/jquery-simple-tree-tables.css" media="all" />
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
    <script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
    <script src="${contextPath}/static/mastergst/js/reports/tds_reports.js" type="text/javascript"></script>
</head>
<style>
.sret-period{margin-bottom: 0;padding: 0px;vertical-align: baseline;font-size: 1rem;text-transform: capitalize;margin-right: 10px;}
#fillingoption_tds::after,#fillingoption_tds1::after{display:none;}
.datepicker-dropdown{top:183px!important;}
.datetime-wrap{display: inline-flex};
.dataTables_length select {background-color: white;}
</style>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
    <div class="breadcrumbwrap">
	    <div class="container bread">
	     	<div class="row">
	        	<div class="col-md-12 col-sm-12">
	            	<ol class="breadcrumb">
	                	<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">TDS Report</li>
	                </ol>
	                <div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
		<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right mb-3" role="button" style="padding: 4px 25px;">Back</a>
			<h4>
				<span>TDS Report of ${client.businessname}</span>
			</h4>
			<div class=""></div>
				<div class="dropdown chooseteam mr-0" style="z-index:2;position: absolute;right:15px;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption_tds" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type"	style="width: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
					</div>
					<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						</div>
					</span> 
					<span style="display:none" class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="filing_option1"	style="margin-right: 10px; display: inline-flex;">
							<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
							<div class="typ-ret type_ret_yearly" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
								<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
									<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
								</span>
							</div>
						</span>
						<div class="dropdown-menu ret-type1" id="financialYear1" style="width: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
					</span>
					<span class="datetimetxt custom-sp" style="display:none" id="custom-sp">
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						<div class="datetimetxt datetime-wrap to-picker">
						<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
							<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 8px; height: 28px; margin-right: 10px;">
								<input type="text" class="form-control totime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>
						</div>
						<div class="datetimetxt datetime-wrap dpfromtime">
							<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
							<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
								<input type="text" class="form-control fromtime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>	
						</div>
					</span>
				</div>
				<div class="tab-pane" id="gtab1" role="tabpanel">
				<div class="normaltable meterialform" id="updatefilter_summary_reports" style="display:none;">
				<div class="noramltable-row"></div>
				<div class="noramltable-row"></div>
			<div class="noramltable-row mt-5">
			            <div class="noramltable-row-hdr">Summary</div>
			            <div class="noramltable-row-desc">
			                <div class="normaltable-col hdr" style="width:8%">Total Transactions<div class="normaltable-col-txt" id="idCount"></div></div>
			                <div class="normaltable-col hdr">Total Amount<div class="normaltable-col-txt" id="idTotAmtVal"></div></div>
			                <div class="normaltable-col hdr filsummary">Total TDS<div class="normaltable-col-txt" id="idTDS"></div></div>
			            </div>
			        </div>
			        </div>
			        </div>
			        <div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="tdsSummaryTable" style="display:none;">
				    <div class ="row">
					    <div class="col-sm-9 pr-0"><h4><span class="reports-yearly" style="display: none;">Yearly Summary of ${client.businessname}</span></h4></div>
				    </div>

				    <table id="reportTable4" class="display row-border dataTable fixed-col meterialform" cellspacing="0" width="100%">
				        <thead>
				            <tr>
								<th class="text-center">Tax Details</th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th>
								<th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th>
								<th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th><th class="text-center">Total</th>
							</tr>
				        </thead>
				        <tbody id="yeartotoalreport">
					        <tr><td align="center"><h6>Transactions</h6> </td><td class="text-right" id="totalinvoices4">0</td><td class="text-right" id="totalinvoices5">0</td><td class="text-right" id="totalinvoices6">0</td><td class="text-right" id="totalinvoices7">0</td><td class="text-right" id="totalinvoices8">0</td><td class="text-right" id="totalinvoices9">0</td><td class="text-right" id="totalinvoices10">0</td><td class="text-right" id="totalinvoices11">0</td><td class="text-right" id="totalinvoices12">0</td><td class="text-right" id="totalinvoices1">0</td><td class="text-right" id="totalinvoices2">0</td><td class="text-right" id="totalinvoices3">0</td><td class="text-right ind_formatss" id="ytotal_Transactions">0</td></tr>
						  	<tr><td align="center"><h6>Total Amount</h6> </td><td class="text-right ind_formatss" id="totalAmountValue4">0.0</td><td class="text-right ind_formatss" id="totalAmountValue5">0.0</td><td class="text-right ind_formatss" id="totalAmountValue6">0.0</td><td class="text-right ind_formatss" id="totalAmountValue7">0.0</td><td class="text-right ind_formatss" id="totalAmountValue8">0.0</td><td class="text-right ind_formatss" id="totalAmountValue9">0.0</td><td class="text-right ind_formatss" id="totalAmountValue10">0.0</td><td class="text-right ind_formatss" id="totalAmountValue11">0.0</td><td class="text-right ind_formatss" id="totalAmountValue12">0.0</td><td class="text-right ind_formatss" id="totalAmountValue1">0.0</td><td class="text-right ind_formatss" id="totalAmountValue2">0.0</td><td class="text-right ind_formatss" id="totalAmountValue3">0.0</td><td class="text-right ind_formatss" id="ytotal_Amountvalue">0.0</td></tr>
						  	<tr><td align="center"><h6>TDS Value</h6> </td><td class="text-right ind_formatss" id="tdsAmt4">0.0</td><td class="text-right ind_formatss" id="tdsAmt5">0.0</td><td class="text-right ind_formatss" id="tdsAmt6">0.0</td><td class="text-right ind_formatss" id="tdsAmt7">0.0</td><td class="text-right ind_formatss" id="tdsAmt8">0.0</td><td class="text-right ind_formatss" id="tdsAmt9">0.0</td><td class="text-right ind_formatss" id="tdsAmt10">0.0</td><td class="text-right ind_formatss" id="tdsAmt11">0.0</td><td class="text-right ind_formatss" id="tdsAmt12">0.0</td><td class="text-right ind_formatss" id="tdsAmt1">0.0</td><td class="text-right ind_formatss" id="tdsAmt2">0.0</td><td class="text-right ind_formatss" id="tdsAmt3">0.0</td><td class="text-right ind_formatss" id="ytotal_TDSAmount">0.0</td></tr>
				        </tbody>
				    </table>
				</div>
		   <div class="customtable db-ca-view salestable reportsdbTableage mt-5" style="overflow-x: auto;">
					<table id='tds_accounting_datatable' class="row-border dataTable meterialform" cellspacing="0" width="100%;">
						<thead>
							<tr>
								<!-- <th class="text-center">SL No.</th> -->	
								<th class="text-center">Date of deduction</th>
								<th class="text-center">Invoice Ref No</th>
								<th class="text-center">Party Name</th>
								<th class="text-center">PAN of deductee</th>
								<th class="text-center">Category based on PAN</th>
								<th class="text-center">Invoice amount</th>	
								<th class="text-center">Section code</th>	
								<th class="text-center">Standard rate</th>	
								<th class="text-center">TDS Deducted</th>
							</tr>
						</thead>
						<tbody id='tds_accounting_body'></tbody>
					</table>
			 </div>
		</div>
		</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script type="text/javascript">
	function updateYearlyOption(value){
		document.getElementById('yearlyoption').innerHTML=value;
	}
	$(function(){
		var date = new Date();
		var month = date.getMonth()+1;
		var	year = date.getFullYear();
		var day = date.getDate();
		var mnt = date.getMonth()+1;
		var yr = date.getFullYear();
		salesFileName = 'MGST_Monthly_'+gstnnumber+'_'+month+year;
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
		var date = $('.dpMonths').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy'
		}).on('changeDate', function(ev) {
			month = ev.date.getMonth()+1;
			year = ev.date.getFullYear();
		});
		$('.dpCustom').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
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
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpMonths').datepicker('update', dateValue);
		$('.dpCustom').datepicker('update', customValue);
		$('.dpCustom1').datepicker('update', customValue);
	});
	$('#tds_accounting').DataTable( {
		"dom": '<"toolbar"f>lrtip<"clear">',
	    responsive: true
	} );
	function generateData() {
		var abc = $('#fillingoption_tds span').html();
		var pUrl = '';
		if(abc == 'Monthly'){
			$('#tdsSummaryTable').css("display", "none");
			$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
			var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
			var month=$('#monthly').val().split("-");
			pUrl = "${contextPath}/gettdsdetails/${client.id}/"+month[0]+"/"+month[1]+"?report=tdsreport";
		}else if (abc == 'Yearly') {
			$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
			$('#tdsSummaryTable').css("display", "block");
			var year=$('#yearlyoption').html().split("-");
			pUrl = '${contextPath}/gettdsdetails/${client.id}/0/'+year[0]+"?report=tdsreport";
			var psummaryUrl = '${contextPath}/getsummarytdsdetails/${client.id}/0/'+year[0]+"?report=tdsreport";
			loadReportsSummary(psummaryUrl,'tds');
		}else{
			$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
			$('#tdsSummaryTable').css("display", "block");
			var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
			pUrl = '${contextPath}/gettdscustomdetails/${client.id}/'+fromtime+'/'+totime+"?report=tdsreport";
			var psummaryUrl = '${contextPath}/getsummarycustomtdsdetails/${client.id}/'+fromtime+'/'+totime+"?report=tdsreport";
			loadReportsSummary(psummaryUrl,'tds');
		}
		loadReportsInvTable(abc, '<c:out value="${client.id}"/>','tds', pUrl);	
	}
	function getval(sel) {
		document.getElementById('filing_option').innerHTML = sel;
		$('#processing').css('top','10%');
		if (sel == 'Custom') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type-trail').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
		} else {
			$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
		}
	}
	
	$(document).ready(function(){
		var month=new Date().getMonth()+1
		var year=new Date().getFullYear();
		var pUrl = '${contextPath}/gettdsdetails/${client.id}/'+month+"/"+year+"?report=tdsreport";
		loadReportsInvTable('Monthly', '<c:out value="${client.id}"/>','tds', pUrl);
	});
	</script>
</body>
</html>