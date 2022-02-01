<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | Stock Aging Report</title>
    <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
    <%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/jquery-simple-tree-tables.css" media="all" />
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
    <script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
    <script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
    <script src="${contextPath}/static/mastergst/js/reports/aging_report.js" type="text/javascript"></script>
</head>
<style>
.sret-period{margin-bottom: 0;padding: 0px;vertical-align: baseline;font-size: 1rem;text-transform: capitalize;margin-right: 10px;}
.age::after,#age_filing_option1::after{display:none;}
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
						<li class="breadcrumb-item active">Aging Report</li>
	                </ol>
	                <div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
		<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>
				<span><c:choose><c:when test="${type eq 'receipt'}">Receipt</c:when><c:otherwise>Payment</c:otherwise></c:choose> Aging Report of ${client.businessname}</span>
			</h4>
			<div class="">
				<div class="dropdown chooseteam mr-0" style="z-index:2;position: absolute;right:15px;height: 32px;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
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
			</div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<div class="normaltable meterialform" id="updatefilter_summary_reports">
				<div class="noramltable-row"></div>
				<div class="noramltable-row"></div>
			<div class="noramltable-row mt-5">
			            <div class="noramltable-row-hdr">Summary</div>
			            <div class="noramltable-row-desc">
			                <div class="normaltable-col hdr">Less than 30Days<div class="normaltable-col-txt" id="idless30days"></div></div>
			                <div class="normaltable-col hdr">30 - 60 Days<div class="normaltable-col-txt" id="id30to60days"></div></div>
			                <div class="normaltable-col hdr filsummary">60 - 90 Days<div class="normaltable-col-txt" id="id60to90days"></div></div>
			                <div class="normaltable-col hdr filsummary">90 - 120 Days<div class="normaltable-col-txt" id="id90to180days"></div></div>
			                <div class="normaltable-col hdr filsummary">Greater than 120 Days<div class="normaltable-col-txt" id="idgreater180days"></div></div>
			            </div>
			        </div>
			        </div>
			        </div>
			
		   <div class="customtable db-ca-view salestable reportsdbTableage mt-5">
					<table id='accounting_aging_${type}' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th class="text-center">Date</th>
								<th class="text-center">Invoice Ref. No</th>
								<th class="text-center">Party Name</th>
								<th class="text-center">Pending Amount</th>
								<th class="text-center">Payment due date</th>
								<th class="text-center interval0"> < 30 days</th>
								<th class="text-center interval30">30 to 60 days</th>
								<th class="text-center interval60">60 to 90 days</th>
								<th class="text-center interval90">90 to 120 days</th>
								<th class="text-center interval120">> 120 Days</th>
							</tr>
						</thead>
						<tbody id='age_accounting_body'></tbody>
					</table>
			 </div> 
		
		</div>
		</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script type="text/javascript">
	$(document).ready(function(){
		//var yr=$('#yearlyoption1').html().split("-");
		//var yearcode = yr[0].trim()+"-"+yr[1].trim();
		var pUrl = '${contextPath}/getstockagingDatat/${client.id}/${type}/${month}/${year}';
		loadReportsInvTable('${type}', '<c:out value="${client.id}"/>', pUrl);
		OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
	});
	function updateYearlyOption(value){
		$('.yearlyoption').text(value);
	}
	
	function getdiv(){
		var yr=$('#yearlyoption1').html().split("-");
		var yearcode = yr[0].trim()+"-"+yr[1].trim();
		var pUrl = '${contextPath}/getstockagingDatat/${client.id}/${type}/'+yearcode;
		loadReportsInvTable('${type}', '<c:out value="${client.id}"/>', pUrl);
		OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
	}
	
	function generateData() {
		var abc = $('#fillingoption span').html();
		var type='';
		if(abc == 'Monthly'){
			$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
			var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
			var pUrl = '${contextPath}/getstockagingDatat/${client.id}/${type}/'+mn+'/'+yr;
			loadReportsInvTable('${type}', '<c:out value="${client.id}"/>', pUrl);
		}else if (abc == 'Yearly') {
			$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
			var year=$('#yearlyoption').html().split("-");
			var pUrl = '${contextPath}/getstockagingDatat/${client.id}/${type}/0/'+year[0];
			loadReportsInvTable('${type}', '<c:out value="${client.id}"/>', pUrl);
		}else{
			$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
			var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
			var pUrl = '${contextPath}/getstockagingDatatCustom/${client.id}/${type}/'+fromtime+'/'+totime;
			loadReportsInvTable('${type}', '<c:out value="${client.id}"/>', pUrl);
		}	
		OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
	}
	
	
	
	</script>
</body>
</html>