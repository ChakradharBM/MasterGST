<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Group Wise Payments Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
 <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/groupwisesalesreports.css" media="all" /> 
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script type="text/javascript">
$(document).ready(function(){$('.nonAspReports').addClass('active');});
</script>
<style>
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink"
							link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" />
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
						<li class="breadcrumb-item active">GST Group Wise Payments Received Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="group_and_client" class="group_and_client" style="margin-top: 186px;">
		<div class="form-group" style="display:inline;">
			<select id="multeselectgroup_payments" class="multeselectgroup multiselect-ui form-control" multiple="multiple">
			</select>
		</div>
		<div class="form-group" style="display:inline;">
			<select id="multeselectclient_payments" class="multeselectclient multiselect-ui form-control" multiple="multiple">
			</select>
		</div>
	</div>
	<div class="db-ca-wrap monthely1" id="monthely_payments">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/cp_ClientsReports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Group wise Monthly Payments Received Report of&nbsp;<c:out value="${user.fullname}"/><c:choose><c:when test='${fn:length(client.fullname) > 25}'>${fn:substring(client.fullname, 0, 25)}..</c:when><c:otherwise>${client.fullname}</c:otherwise></c:choose> </h4><p>Monthly Payment Report gives you a summary of your monthly Payments.<span id="monthly_clients_errormsg"></span></p>
			<div class="dropdown chooseteam mr-0"><span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div>
				</Span>
				<div class="dropdown-menu ret-type"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div><a href="#" class="btn btn-greendark pull-right" role="button"	style="padding: 4px 10px;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: none" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2020">2021 - 2022</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase" onClick="getdiv()">Generate</a>
				</span>
				<span class="datetimetxt custom-sp" style="display: none" id="custom-sp">
					<a href="#" class="btn btn-greendark pull-right" role="button"	style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</a>
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
			<div class=" "></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
					<div class="normaltable meterialform" id="monthlynormaltable">
		<div class="filter">
			<div class="noramltable-row"><div class="noramltable-row-hdr">Filter</div><div class="noramltable-row-desc"><div class="sfilter"><span id="divFiltersGSTR1"></span>
					<span class="btn-remove-tag" onClick="clearFilters('GSTR1','month')">Clear All<span data-role="remove"></span></span>
				</div></div>
			</div>
		</div>
	<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div><div class="noramltable-row-desc">
				<select id="multiselectGSTR11" class="multiselect-ui form-control" multiple="multiple"><option value="2021">2021-2022</option><option value="2020">2020-2021</option><option value="2019">2019-2020</option><option value="2018">2018-2019</option><option value="2017">2017-2018</option></select>
				<select id="multiselectGSTR15" class="multiselect-ui form-control" multiple="multiple"><option value="01">January</option><option value="02">February</option><option value="03">March</option><option value="04">April</option><option value="05">May</option><option value="06">June</option><option value="07">July</option><option value="08">August</option><option value="09">September</option><option value="10">October</option><option value="11">November</option><option value="12">December</option></select>
				<select id="multiselectGSTR12" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectGSTR13" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectGSTR14" class="multiselect-ui form-control" multiple="multiple"><option value="cash">Cash</option><option value="Bank">Bank</option><option value="TDS-IT">TDS - IT</option><option value="TDS-GST">TDS - GST</option><option value="Discount">Discount</option><option value="Others">Others</option></select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
			<div class="normaltable-col hdr" style="width:235px;">Total No.Of Receipts<div class="normaltable-col-txt" id="idCountGSTR1"></div></div>
			 <!-- <div class="normaltable-col hdr">Total Amount Receivable<div class="normaltable-col-txt" id="idTotalAmtGSTR1"></div></div> -->
				<div class="normaltable-col hdr" style="width:235px;">Total Amount Received<div class="normaltable-col-txt" id="idTotalValGSTR1"></div></div>
				<!-- <div class="normaltable-col hdr">Total Amount To Be Received<div class="normaltable-col-txt" id="idTotalPendingGSTR1"></div></div> -->
				</div>
	</div>
	</div>
			</div>
			<div id="monthProcess" class="text-center" style="display: none;"></div>
			<div class="customtable db-ca-view globalsalestable">
		<table id='globalsalestable' class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead><tr><th class="text-center" width=3%>S.no</th><th class="text-center" width=70px>Date</th><th class="text-center">Receipt Id</th><th class="text-center">Invoice Number</th><th class="text-center">Customer</th><th class="text-center">GSTIN</th><th class="text-center">Total Amount Received</th><th class="text-center">Payment Mode</th><th class="text-center">Reference No</th><th class="text-center" style="display:none;">Financial Year</th><th class="text-center" style="display:none;">Month</th></tr></thead>		
			<tbody id='invBodysalestable1'>
			</tbody>
		</table>
	</div>
		</div>
	</div>
	<div class="db-ca-wrap yearly1" style="display: none" id="yearly_payments">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/cp_ClientsReports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Group wise Yearly Payments Received Report of&nbsp;<c:out value="${user.fullname}"/><c:choose><c:when test='${fn:length(client.fullname) > 25}'>${fn:substring(client.fullname, 0, 25)}..</c:when><c:otherwise>${client.fullname}</c:otherwise></c:choose> </h4><p>Yearly Payment Report gives you a summary of your annual Payments.<span id="yearly_clients_errormsg"></span></p>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<span>Report Type:</span>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 16%; top: 26px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					<a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"	id="monthely-sp">
				<label id="ret-period">Report Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months"	style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" value="02-2012"	readonly=""> 
							<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: inline-block" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
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
				<span class="datetimetxt custom-sp" style="display: none" id="custom-sp">
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="02-2012"	readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>	
					</div>
					<div class="datetimetxt datetime-wrap">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="02-2012" readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
					</div> 	
				</span>
			</div>
			<div class=" "></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<div class="normaltable meterialform" id="yearlynormaltable">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div><div class="noramltable-row-desc"><div class="sfilter"><span id="divFiltersyearlyGSTR1"></span><span class="btn-remove-tag" onClick="clearFilters('GSTR1','year')">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
<div class="noramltable-row">
		<div class="noramltable-row-hdr">Search Filter</div>
		<div class="noramltable-row-desc">
					<select id="multiselectyearGSTR11" class="multiselect-ui form-control" multiple="multiple"><option value="2021">2021-2022</option><option value="2020">2020-2021</option><option value="2019">2019-2020</option><option value="2018">2018-2019</option><option value="2017">2017-2018</option></select>
				<select id="multiselectyearGSTR15" class="multiselect-ui form-control" multiple="multiple"><option value="01">January</option><option value="02">February</option><option value="03">March</option><option value="04">April</option><option value="05">May</option><option value="06">June</option><option value="07">July</option><option value="08">August</option><option value="09">September</option><option value="10">October</option><option value="11">November</option><option value="12">December</option></select>
				<select id="multiselectyearGSTR12" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectyearGSTR13" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectyearGSTR14" class="multiselect-ui form-control" multiple="multiple"><option value="cash">Cash</option><option value="Bank">Bank</option><option value="TDS-IT">TDS - IT</option><option value="TDS-GST">TDS - GST</option><option value="Discount">Discount</option><option value="Others">Others</option></select>
			</div>
		</div>
		<div class="noramltable-row">
		<div class="noramltable-row-hdr">Filter Summary</div>
		<div class="noramltable-row-desc">
				<div class="normaltable-col hdr" style="width:235px;">Total Payments<div class="normaltable-col-txt" id="idCountyearGSTR1"></div></div>
				<!-- <div class="normaltable-col hdr">Total Amount Receivable<div class="normaltable-col-txt" id="idTotalamtyearGSTR1"></div></div> -->
				<div class="normaltable-col hdr" style="width:235px;">Total Amount Received<div class="normaltable-col-txt" id="idTotalValyearGSTR1"></div></div>
				<!-- <div class="normaltable-col hdr">Total Amount To Be Received<div class="normaltable-col-txt" id="idPendAmtyearGSTR1"></div></div> -->
				</div>
				</div>
	</div>
			</div>
			<div id="yearProcess" class="text-center" style="display:none;"></div>
			<div class="customtable db-ca-view globalpaymentsalestable2" >
				<table id="globalpaymentsalestable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
					<thead><tr><th class="text-center" width=5%>S.no</th><th class="text-center" width=12%>Date</th><th class="text-center">Receipt Id</th><th class="text-center">Invoice Number</th><th class="text-center">Customer</th><th class="text-center">GSTIN</th><th class="text-center">Total Amount Received</th><th class="text-center">Payment Mode</th><th class="text-center">Reference No</th><th class="text-center" style="display:none;">Financial Year</th><th class="text-center" style="display:none;">Month</th></tr></thead>
					
				</table>
			</div>
				
		</div>
	</div>
	<div class="db-ca-wrap custom1" id="custom_payments"  style="display: none">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/cp_ClientsReports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Group wise Custom Payments Received Report of&nbsp;<c:out value="${user.fullname}"/><c:choose><c:when test='${fn:length(client.fullname) > 25}'>${fn:substring(client.fullname, 0, 25)}..</c:when><c:otherwise>${client.fullname}</c:otherwise></c:choose> </h4><p>Custom Payment Report gives you a summary of your monthly, quarterly and annual Payments.<span id="custom_clients_errormsg"></span></p>
			<div class="dropdown chooseteam  mr-0">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<span style="height: 32px">Report Type:</span>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option2" class="filing_option"	style="vertical-align: top">Custom</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i>
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> 
					<a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"	id="monthely-sp">
					<label id="ret-period">Report Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" value="02-2012" readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: none margin-bottom:4px" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; height: 27px; align-items: top; padding-right:14px; min-width: 104px;max-width: 104px;">
							<span style="vertical-align: top;" id="yearlyoption2" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2019">2019 - 2020</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div> 
					<a href="#" class="btn btn-greendark  pull-right" role="button" style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
				</span> 
				<span class="datetimetxt custom-sp" style="display: block" id="custom-sp"> 
				<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase;"	onClick="getdiv()">Generate</a>
				<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="10-11-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="11-02-2012" readonly=""> 
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>	
					</div>
					<div class="datetimetxt datetime-wrap">
					<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="10-2-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="11-02-2014" readonly="">
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
					</div> 
				</span>
			</div>
			<div class=" "></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<div class="normaltable meterialform" id="customnormaltable">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div><div class="noramltable-row-desc"><div class="sfilter"><span id="divFilterscustomGSTR1"></span><span class="btn-remove-tag" onClick="clearFilters('GSTR1','custom')">Clear All<span data-role="remove"></span></span></div></div>
			</div>
		</div>
<div class="noramltable-row"><div class="noramltable-row-hdr">Search Filter</div>
			<div class="noramltable-row-desc">	<select id="multiselectcustomGSTR11" class="multiselect-ui form-control" multiple="multiple"><option value="2017">2017-2018</option><option value="2018">2018-2019</option><option value="2019">2019-2020</option></select>
				<select id="multiselectcustomGSTR15" class="multiselect-ui form-control" multiple="multiple"><option value="01">January</option><option value="02">February</option><option value="03">March</option><option value="04">April</option><option value="05">May</option><option value="06">June</option><option value="07">July</option><option value="08">August</option><option value="09">September</option><option value="10">October</option><option value="11">November</option><option value="12">December</option></select>
				<select id="multiselectcustomGSTR12" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectcustomGSTR13" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectcustomGSTR14" class="multiselect-ui form-control" multiple="multiple"><option value="cash">Cash</option><option value="Bank">Bank</option><option value="TDS-IT">TDS - IT</option><option value="TDS-GST">TDS - GST</option><option value="Discount">Discount</option><option value="Others">Others</option></select></div>
		</div>
		<div class="noramltable-row">
		<div class="noramltable-row-hdr">Filter Summary</div>
		<div class="noramltable-row-desc">
				<div class="normaltable-col hdr" style="width:235px;">Total No.Of Payments<div class="normaltable-col-txt" id="idCountcustomGSTR1"></div></div>
		 		<!-- <div class="normaltable-col hdr">Total Amount Receivable<div class="normaltable-col-txt" id="idTotalAmtcustomGSTR1"></div></div> -->
				<div class="normaltable-col hdr" style="width:235px;">Total Amount Received<div class="normaltable-col-txt" id="idTotalValcustomGSTR1"></div></div>
				<!-- <div class="normaltable-col hdr">Total Amount To Be Received<div class="normaltable-col-txt" id="idPendAmtcustomGSTR1"></div></div> -->
				</div>
				</div>
	</div>
			</div>
			<div id="customProcess" class="text-center" style="display:none;"></div>
			<div class="customtable db-ca-view reportTable reportTable5">
				
			</div>
			<div class="customtable db-ca-view globalpaymentsalestable3">
		<table id='globalpaymentsalestable3' class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead><tr><th class="text-center" width=5%>S.no</th><th class="text-center" width=12%>Date</th><th class="text-center">Receipt Id</th><th class="text-center">Invoice Number</th><th class="text-center">Customer</th><th class="text-center">GSTIN</th><th class="text-center">Total Amount Received</th><th class="text-center">Payment Mode</th><th class="text-center">Reference No</th><th class="text-center" style="display:none;">Financial Year</th><th class="text-center" style="display:none;">Month</th></tr></thead>
			<tbody id='invBodysalestable3'>
			</tbody>
		</table>
	</div>
		</div>
	</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
</body>
<script type="text/javascript">
var clientidsArray=new Array();
var groupArr =new Array();
var selectGroupArrayClientids= new Array();
var groupPaymentsArray = new Object();
var hiddenCols = new Array();
var groupPaymentsTable;
var salesFileName;var gstnnumber='<c:out value="${client.gstnnumber}"/>';

	<c:forEach items="${listOfClients}" var="clients">
	$("#multeselectclient_payments").append($("<option></option>").attr("value","${clients.id}").html("${clients.businessname}- <span> ${clients.gstnnumber}</span>"));
</c:forEach>
var counts=0;var groupArray=[], selectGroupArray=[];
<c:forEach items="${listOfClients}" var="group">
	<c:if test="${not empty group.groupName}">
		if(counts == 0){groupArray.push('<c:out value="${group.groupName}"/>');	$("#multeselectgroup_payments").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));}
		if(jQuery.inArray('<c:out value="${group.groupName}"/>', groupArray ) == -1){groupArray.push('<c:out value="${group.groupName}"/>');$("#multeselectgroup_payments").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));}
		counts++;
	</c:if>
</c:forEach>
	
	$(function() {
		var clientname = '<c:out value="${client.businessname}"/>';	
		var date = new Date();
		month = '<c:out value="${month}"/>';
		year = '<c:out value="${year}"/>';
		if(month == null || month == '') {month = date.getMonth()+1;year = date.getFullYear();}
			var day = date.getDate();var mnt = date.getMonth()+1;var yr = date.getFullYear();
			salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+month+year;var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
			var date = $('.dpMonths').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy'
		}).on('changeDate', function(ev) {
		//updateReturnPeriod(ev.date);
		month = ev.date.getMonth()+1;year = ev.date.getFullYear();
	});
	$('.dpCustom').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		//updateReturnPeriod(ev.date);
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
	});
	$('.dpCustom1').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		//updateReturnPeriod(ev.date);
		day = ev.date.getDate();mnt = ev.date.getMonth()+1;yr = ev.date.getFullYear();
		$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
	});
	$('.dpMonths').datepicker('update', dateValue);$('.dpCustom').datepicker('update', customValue);$('.dpCustom1').datepicker('update', customValue);
	$('#multeselectclient_payments').multiselect({
		nonSelectedText: '- client/business Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyClient();},
		onSelectAll: function() {applyClient();},
		onDeselectAll: function() {applyClient();}
	});
	$('#multeselectgroup_payments').multiselect({
		nonSelectedText: '- Group Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyGroup();},
		onSelectAll: function() {applyGroup();},
		onDeselectAll: function() {applyGroup();}
	});
	$('#multiselectGSTR11').multiselect({
		nonSelectedText: '- Financial Year -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','month');},
		onSelectAll: function() {applyFilters('GSTR1','month');},
		onDeselectAll: function() {applyFilters('GSTR1','month');}
	});
	$('#multiselectyearGSTR11').multiselect({
		nonSelectedText: '- Financial Year -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','year');},
		onSelectAll: function() {applyFilters('GSTR1','year');},
		onDeselectAll: function() {applyFilters('GSTR1','year');}
	});
	$('#multiselectcustomGSTR11').multiselect({
		nonSelectedText: '- Financial Year -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','custom');},
		onSelectAll: function() {applyFilters('GSTR1','custom');},
		onDeselectAll: function() {applyFilters('GSTR1','custom');}
	});
	$('#multiselectGSTR15').multiselect({
		nonSelectedText: '- Month -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','month');},
		onSelectAll: function() {applyFilters('GSTR1','month');},
		onDeselectAll: function() {applyFilters('GSTR1','month');}
	});
	$('#multiselectyearGSTR15').multiselect({
		nonSelectedText: '- Month -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','year');},
		onSelectAll: function() {applyFilters('GSTR1','year');},
		onDeselectAll: function() {applyFilters('GSTR1','year');}
	});
	$('#multiselectcustomGSTR15').multiselect({
		nonSelectedText: '- Month -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','custom');},
		onSelectAll: function() {applyFilters('GSTR1','custom');},
		onDeselectAll: function() {applyFilters('GSTR1','custom');}
	});
	$('#multiselectGSTR12').multiselect({
		nonSelectedText: '- Customers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','month');},
		onSelectAll: function() {applyFilters('GSTR1','month');},
		onDeselectAll: function() {applyFilters('GSTR1','month');}
	});
	$('#multiselectyearGSTR12').multiselect({
		nonSelectedText: '- Customers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','year');},
		onSelectAll: function() {applyFilters('GSTR1','year');},
		onDeselectAll: function() {applyFilters('GSTR1','year');}
	});
	$('#multiselectcustomGSTR12').multiselect({
		nonSelectedText: '- Customers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','custom');},
		onSelectAll: function() {applyFilters('GSTR1','custom');},
		onDeselectAll: function() {applyFilters('GSTR1','custom');}
	});
	$('#multiselectGSTR13').multiselect({
		nonSelectedText: '- GSTIN -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','month');},
		onSelectAll: function() {applyFilters('GSTR1','month');},
		onDeselectAll: function() {applyFilters('GSTR1','month');}
	});
	$('#multiselectyearGSTR13').multiselect({
		nonSelectedText: '- GSTIN -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','year');},
		onSelectAll: function() {applyFilters('GSTR1','year');},
		onDeselectAll: function() {applyFilters('GSTR1','year');}
	});
	$('#multiselectcustomGSTR13').multiselect({
		nonSelectedText: '- GSTIN -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','custom');},
		onSelectAll: function() {applyFilters('GSTR1','custom');},
		onDeselectAll: function() {applyFilters('GSTR1','custom');}
	});
	$('#multiselectGSTR14').multiselect({
		nonSelectedText: '- Payment Mode -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','month');},
		onSelectAll: function() {applyFilters('GSTR1','month');},
		onDeselectAll: function() {applyFilters('GSTR1','month');}
	});
	$('#multiselectyearGSTR14').multiselect({
		nonSelectedText: '- Payment Mode -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','year');},
		onSelectAll: function() {applyFilters('GSTR1','year');},
		onDeselectAll: function() {applyFilters('GSTR1','year');}
	});
	$('#multiselectcustomGSTR14').multiselect({
		nonSelectedText: '- Payment Mode -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','custom');},
		onSelectAll: function() {applyFilters('GSTR1','custom');},
		onDeselectAll: function() {applyFilters('GSTR1','custom');}
	});
	
	$('#multiselectGSTR12').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyFilters('GSTR1','month');},
		onSelectAll: function() {applyFilters('GSTR1','month');},
		onDeselectAll: function() {applyFilters('GSTR1','month');}
	});
	 $('#multeselectgroup_payments').multiselect({
			nonSelectedText: '- Group Name -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {applyGroup();},
			onSelectAll: function() {applyGroup();},
			onDeselectAll: function() {applyGroup();}
		});
	function getGroupNameData(groupsnamelist){
		if(groupsnamelist == "GROUPS_NOTFOUND"){
			selectGroupArrayClientids= new Array();
			clientidsArray=new Array();
    		clientidsArray.push('CLIENTS_NOTFOUND');
			selectGroupArrayClientids.push('GROUPS_NOTFOUND');
    		var userid='<c:out value="${id}"/>';
    		$.ajax({
    			url: '${contextPath}/getclientsdata/'+userid,
    			type:'GET',
    			contentType: 'application/json',
    			success : function(data) {
    				$('#multeselectclient_payments').empty().multiselect('rebuild');
	    			for(var i =0;i <=data.length-1;i++){
	    				$("#multeselectclient_payments").append($("<option></option>").attr("value",data[i].firstname).text(data[i].businessname+"-"+data[i].gstnnumber));
	    			} 
	    			$('#multeselectclient_payments').multiselect('rebuild');
	    		},error : function(data) {}
    		});
    	}else{
    		$.ajax({
    			url: '${contextPath}/getclientdata/'+groupsnamelist+'/<c:out value="${id}"/>',
    			type:'GET',
    			contentType: 'application/json',
    			success : function(data) {
    				selectGroupArrayClientids= new Array();
    				$('#multeselectclient_payments').empty().multiselect('rebuild');
	    			for(var i =0;i <=data.length-1;i++){
	    				selectGroupArrayClientids.push(data[i].firstname);
	    			  	$("#multeselectclient_payments").append($("<option></option>").attr("value",data[i].firstname).text(data[i].businessname+"-"+data[i].gstnnumber));
	    			} 
	    			$('#multeselectclient_payments').multiselect('rebuild');
    			},error : function(data) {}
    		});
    	}
     }
    function applyGroup() {
		var groupArr =new Array(); 
		var groupOptions = $('#multeselectgroup_payments option:selected');
	    if(groupOptions.length > 0) {
	    	for(var i=0;i<groupOptions.length;i++) {
	    		groupArr.push(groupOptions[i].value);
	    		getGroupNameData(groupArr);
	    	}
		}else{
			groupArr.push('GROUPS_NOTFOUND');
			getGroupNameData(groupArr);
		}
    }
   
	function applyClient() {
		var clientArr =new Array();
	    var clientOptions = $('#multeselectclient_payments option:selected');
    	if(clientOptions.length > 0) {
	    	for(var i=0;i<clientOptions.length;i++) {
	    		clientArr.push(clientOptions[i].value);	getlistOfClientids(clientArr);
			}
		}else{
			clientArr.push('CLIENTS_NOTFOUND');getlistOfClientids(clientArr);
		}
    }
	function getlistOfClientids(listofclients){
		clientidsArray=new Array();clientidsArray.push(listofclients);
	}
	$('#multeselectclient_payments').multiselect({
		nonSelectedText: '- client/business Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyClient();},
		onSelectAll: function() {applyClient();},
		onDeselectAll: function() {applyClient();}
	});
	
	$('#divFiltersGSTR1').on('click', '.deltag', function(e) {
		var val = $(this).data('val');$('#multiselectGSTR11').multiselect('deselect', [val]);$('#multiselectGSTR12').multiselect('deselect', [val]);$('#multiselectGSTR13').multiselect('deselect', [val]);$('#multiselectGSTR14').multiselect('deselect', [val]);$('#multiselectGSTR15').multiselect('deselect', [val]);applyFilters('GSTR1','month');
	});
	$('#divFiltersyearlyGSTR1').on('click', '.deltag', function(e) {
		var val = $(this).data('val');$('#multiselectyearGSTR11').multiselect('deselect', [val]);$('#multiselectyearGSTR12').multiselect('deselect', [val]);$('#multiselectyearGSTR13').multiselect('deselect', [val]);$('#multiselectyearGSTR14').multiselect('deselect', [val]);$('#multiselectyearGSTR15').multiselect('deselect', [val]);applyFilters('GSTR1','year');
	});
	$('#divFilterscustomGSTR1').on('click', '.deltag', function(e) {
		var val = $(this).data('val');$('#multiselectcustomGSTR11').multiselect('deselect', [val]);$('#multiselectcustomGSTR12').multiselect('deselect', [val]);$('#multiselectcustomGSTR13').multiselect('deselect', [val]);$('#multiselectcustomGSTR14').multiselect('deselect', [val]);$('#multiselectcustomGSTR15').multiselect('deselect', [val]);applyFilters('GSTR1','custom');
	});
	function applyFilters(retType,val) {
		groupPaymentsTable.clear();
		var financialOptions = new Array();
		var paymentOptions = new Array();
		var customerOptions = new Array();
		var gstinOptions = new Array();
		var monthOptions = new Array();
		if(val == 'month'){
			financialOptions = $('#multiselectGSTR11 option:selected');
			paymentOptions = $('#multiselectGSTR14 option:selected');
			customerOptions = $('#multiselectGSTR12 option:selected');
			gstinOptions = $('#multiselectGSTR13 option:selected');
			monthOptions = $('#multiselectGSTR15 option:selected');
		}else if(val == 'year'){
			financialOptions = $('#multiselectyearGSTR11 option:selected');
			paymentOptions = $('#multiselectyearGSTR14 option:selected');
			customerOptions = $('#multiselectyearGSTR12 option:selected');
			gstinOptions = $('#multiselectyearGSTR13 option:selected');
			monthOptions = $('#multiselectyearGSTR15 option:selected');
		}else{
			financialOptions = $('#multiselectcustomGSTR11 option:selected');
			paymentOptions = $('#multiselectcustomGSTR14 option:selected');
			customerOptions = $('#multiselectcustomGSTR12 option:selected');
			gstinOptions = $('#multiselectcustomGSTR13 option:selected');
			monthOptions = $('#multiselectcustomGSTR15 option:selected');
			}
		if(financialOptions.length > 0 || paymentOptions.length > 0 || customerOptions.length > 0 || gstinOptions.length > 0 || monthOptions.length > 0){
			if(val=="month"){
				$('#monthlynormaltable .filter').css("display","block");
				$('#yearlynormaltable .filter').css("display","none");
				$('#customnormaltable .filter').css("display","none");
			}else if(val == 'year'){
				$('#monthlynormaltable .filter').css("display","none");
				$('#yearlynormaltable .filter').css("display","block");
				$('#customnormaltable .filter').css("display","none");
			}else{
				$('#monthlynormaltable .filter').css("display","none");
				$('#yearlynormaltable .filter').css("display","none");
				$('#customnormaltable .filter').css("display","block");
				}
		}else{
			$('#monthlynormaltable .filter').css("display","none");
			$('#yearlynormaltable .filter').css("display","none");
			$('#customnormaltable .filter').css("display","none");
			}
		var financialArr=new Array();var monthArr=new Array();var customerArr=new Array();var gstinArr=new Array();var paymentArr=new Array();var filterContent='';
		if(financialOptions.length > 0) {
			for(var i=0;i<financialOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+financialOptions[i].text+'<span data-val="'+financialOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				financialArr.push(financialOptions[i].value);
			}
		} else {
			financialArr.push('All');
		}
		if(monthOptions.length > 0) {
			for(var i=0;i<monthOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+monthOptions[i].text+'<span data-val="'+monthOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				monthArr.push(monthOptions[i].value);
			}
		} else {
			monthArr.push('All');
		}
		if(customerOptions.length > 0) {
			for(var i=0;i<customerOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customerOptions[i].text+'<span data-val="'+customerOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				customerArr.push(customerOptions[i].value);
			}
		} else {
			customerArr.push('All');
		}
		if(gstinOptions.length > 0) {
			for(var i=0;i<gstinOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+gstinOptions[i].value+'<span data-val="'+gstinOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				gstinArr.push(gstinOptions[i].value);
			}
		} else {
			gstinArr.push('All');
		}
		if(paymentOptions.length > 0) {
			for(var i=0;i<paymentOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+paymentOptions[i].value+'<span data-val="'+paymentOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				paymentArr.push(paymentOptions[i].value);
			}
		} else {
			paymentArr.push('All');
		}
		if(val=='month'){$('#divFiltersGSTR1').html(filterContent);commonInvoiceFilter(retType, financialArr,monthArr, customerArr, gstinArr,paymentArr,'month');
		}else if(val == 'year'){$('#divFiltersyearlyGSTR1').html(filterContent);commonInvoiceFilter(retType, financialArr, monthArr,customerArr, gstinArr,paymentArr,'year');
		}else{$('#divFilterscustomGSTR1').html(filterContent);commonInvoiceFilter(retType, financialArr, monthArr,customerArr, gstinArr,paymentArr,'custom');}
		groupPaymentsTable.draw();
	}
});
	function commonInvoiceFilter(retType, arrFinancial,arrMonth, arrCustomer, arrGstin, arrPayment,vals) {
		if(groupPaymentsArray['GSTR1'].length > 0) {
			var rows = new Array();
			var taxArray = new Array();
			var rowNode;
			var i=1;
			groupPaymentsArray['GSTR1'].forEach(function(itemData) {
				var date = itemData.paymentDate;
				var dt = date.split("-");
				itemData.financialYear = dt[2];
				itemData.month = dt[1];
				var rowData  = new Array();
				if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
					$.each(itemData.paymentitems, function(index, payemntItemData) {
						rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,payemntItemData.amount,payemntItemData.modeOfPayment,payemntItemData.referenceNumber,itemData.financialYear,itemData.month];
						rows.push(rowData);
						taxArray.push([payemntItemData.amount,payemntItemData.previousPendingBalance,payemntItemData.pendingBalance]);
						i++;
					});
				}else{
					rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,itemData.amount,itemData.modeOfPayment,itemData.referenceNumber,itemData.financialYear,itemData.month];
					rows.push(rowData);
					taxArray.push([itemData.amount,itemData.previousPendingBalance,itemData.pendingBalance]);
					i++;
				}
			});
			var index = 0, tpayments=0, tamt=0, pptamnt=0, pbal=0;
			rows.forEach(function(row) {
			if((arrFinancial.length == 0 || $.inArray('All', arrFinancial) >= 0 || $.inArray(row[9], arrFinancial) >= 0)
					&& (arrMonth.length == 0 || $.inArray('All', arrMonth) >= 0 || $.inArray(row[10], arrMonth) >= 0)
					&& (arrCustomer.length == 0 || $.inArray('All', arrCustomer) >= 0 || $.inArray(row[4], arrCustomer) >= 0)
					&& (arrGstin.length == 0 || $.inArray('All', arrGstin) >= 0 || $.inArray(row[5], arrGstin) >= 0)
					&& (arrPayment.length == 0 || $.inArray('All', arrPayment) >= 0 || $.inArray(row[7], arrPayment) >= 0)) {
				
					row[6] = "<span class='ind_formats'>"+row[6]+"</span>";
					rowNode = groupPaymentsTable.row.add(row);
					tpayments++;
					tamt+=parseFloat(taxArray[index][0]);
					pptamnt+=parseFloat(taxArray[index][1]);
					pbal+=parseFloat(taxArray[index][2]);
			  }
			  index++;
			});
			
			if(vals=="month"){
				$('#idCountGSTR1').html(tpayments);$('#idTotalValGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2)));$('#idTotalAmtGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt+pbal).toFixed(2))); $('#idTotalPendingGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pbal).toFixed(2))); 
				}else if(vals=='year'){$('#idCountyearGSTR1').html(tpayments);$('#idTotalValyearGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2)));$('#idTotalamtyearGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt+pbal).toFixed(2))); $('#idPendAmtyearGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pbal).toFixed(2))); 
				}else{$('#idCountcustomGSTR1').html(tpayments);$('#idTotalValcustomGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2)));$('#idTotalAmtcustomGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt+pbal).toFixed(2))); $('#idPendAmtcustomGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pbal).toFixed(2))); }
		}
	}
	function clearFilters(retType,val) {
		$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		$('#divFilters').html('');
		$('.normaltable .filter').css("display","none");
		groupPaymentsTable.clear();
		commonInvoiceFilter(retType, new Array(),new Array(), new Array(),new Array(),new Array(),val);
		groupPaymentsTable.draw();
	}
$( document ).ready(function() {
	$('#multeselectclient').parent().css('width','172.81px');
groupPaymentsTable = $("#globalsalestable, #globalpaymentsalestable2, #globalpaymentsalestable3").DataTable({
	dom: '<"toolbar"f>lrtip<"clear">', 		
	"paging": true,
	"searching": true,
	"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
	"responsive": true,
	"ordering": true,
	"language": {
		"search": "_INPUT_",
		"searchPlaceholder": "Search...",
		"paginate": {
		   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
			"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
	   }
	 }
});
});

hiddenCols.push(9);
hiddenCols.push(10);
$('input.btaginput').tagsinput({tagClass : 'big',});
$(document).on('click', '.btn-remove-tag', function() {$('.bootstrap-tagsinput').html('');});
$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
	$('.bootstrap-tagsinput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
});
function getval(sel) {
	document.getElementById('filing_option').innerHTML = sel;document.getElementById('filing_option1').innerHTML = sel;document.getElementById('filing_option2').innerHTML = sel;
	if (sel == 'Custom') {$('#group_and_client').css("right","53%");$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type').css("left", "16%");
	} else if (sel == 'Yearly') {$('#group_and_client').css("right","47%");$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type').css("left", "19%");
	} else {$('#group_and_client').css("right","47%");$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type').css("left", "19%"); }
};
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;document.getElementById('yearlyoption1').innerHTML=value;document.getElementById('yearlyoption2').innerHTML=value;
}
function getdiv() {
	var clientname = '<c:out value="${client.businessname}"/>';
	var abc = $('#fillingoption span').html();
if(abc == 'Monthly'){
	$('#monthely_payments').css("display", "block");$('yearly_payments').css("display", "none");$('#custom_payments').css("display", "none");$('span#fillingoption').css("vertical-align", "middle");
	var fp = $('#monthly').val();
	var fpsplit = fp.split('-');
	var mn = parseInt(fpsplit[0]);	
	var yr = parseInt(fpsplit[1]);
	
	if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
		if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
			$('#monthly_clients_errormsg').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please select at least one client/business name').css('color','red');
		}else{
			$('#monthly_clients_errormsg').html('');
			ajaxFunctionMonthly(clientidsArray,mn,yr);
		}
	}else{
		if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
			$('#monthly_clients_errormsg').html('');
			ajaxFunctionMonthly(clientidsArray,mn,yr);
		}else{
			$('#monthly_clients_errormsg').html('');
			ajaxFunctionMonthly(selectGroupArrayClientids,mn,yr);
		}		
	}
	
}else if(abc == 'Yearly'){
	$('#monthely_payments').css("display", "none");
	$('#yearly_payments').css("display", "block");
	$('#custom_payments').css("display", "none");
	//$('span#fillingoption').css("vertical-align", "bottom");		
	var fp = $('.yearlyoption').text();
	var fpsplit = fp.split(' - ');
	var yrs = parseInt(fpsplit[0]);
	var yrs1 = parseInt(fpsplit[0])+1;
	
	if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
		if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
			$('#yearly_clients_errormsg').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please select at least one client/business name').css('color','red');
		}else{
			$('#yearly_clients_errormsg').html('');
			ajaxFunctionYearly(clientidsArray,yrs);
		}
	}else{
		if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
			$('#yearly_clients_errormsg').html('');
			ajaxFunctionYearly(clientidsArray,yrs);
		}else{
			$('#yearly_clients_errormsg').html('');
			ajaxFunctionYearly(selectGroupArrayClientids,yrs);
		}		
	}
	
}else{
	$('#monthely_payments').css("display", "none");
	$('#yearly_payments').css("display", "none");
	$('#custom_payments').css("display", "block");
	var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
	if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
		if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
			$('#custom_clients_errormsg').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please select at least one client/business name').css('color','red');
		}else{
			$('#custom_clients_errormsg').html('');
			ajaxFunctionCustom(clientidsArray,fromtime,totime);
		}
	}else{
		if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
			$('#custom_clients_errormsg').html('');
			ajaxFunctionCustom(clientidsArray,fromtime,totime);
		}else{
			$('#custom_clients_errormsg').html('');
			ajaxFunctionCustom(selectGroupArrayClientids,fromtime,totime);
		}		
	}

}
}
</script>
<script type="text/javascript">

function ajaxFunctionCustom(clientidsArray,fromtime,totime){
	if ( $.fn.DataTable.isDataTable('#globalpaymentsalestable3') ) {$('#globalpaymentsalestable3').DataTable().destroy();}
	$('#globalpaymentsalestable3 tbody').empty();
	$.ajax({
		url: "${contextPath}/getselectedclientpaymentscustom/GSTR1/"+clientidsArray+"/"+fromtime+"/"+totime,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {$('#customProcess').show().text('Processing...');},
		success : function(response){
			if(response.length > 0){
				var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;var invnos={};var pendamt=0;var totFullAmt=0;
				$('#globalpaymentsalestable3 tbody').html('');
					$.each(response, function(index, itemData) {
						var date = itemData.paymentDate;
						var dt = date.split("-");
						itemData.financialYear = dt[2];
						itemData.month = dt[1];
						if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
							$.each(itemData.paymentitems, function(index, payemntItemData) {
								$('#globalpaymentsalestable3 tbody').append('<tr><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(payemntItemData.amount).toFixed(2))+'</td><td class="text-left">'+payemntItemData.modeOfPayment+'</td><td class="text-left">'+payemntItemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td></tr>');
								totalamt += parseFloat(payemntItemData.amount);
								totFullAmt += parseFloat(payemntItemData.previousPendingBalance);
								pendamt += parseFloat(payemntItemData.pendingBalance);
								counts++;
								i++;
								transcounts++;
							});
						}else{
							$('#globalpaymentsalestable3 tbody').append('<tr><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(itemData.amount).toFixed(2))+'</td><td class="text-left">'+itemData.modeOfPayment+'</td><td class="text-left">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td></tr>');
							totalamt += parseFloat(itemData.amount);
							totFullAmt += parseFloat(itemData.previousPendingBalance);
							pendamt += parseFloat(itemData.pendingBalance);
							counts++;
							i++;
							transcounts++;
						}
						//$('#globalpaymentsalestable3 tbody').append('<tr><td class="text-center">'+i+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center" class="text-center">'+itemData.invoiceNumber+'</td><td class="text-center">'+itemData.customerName+'</td><td class="text-center">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(itemData.amount).toFixed(2))+'</td><td class="text-center">'+itemData.modeOfPayment+'</td><td class="text-center">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td></tr>');
						if(itemData.customerName) {
							if(counts == 0){custnames.push(itemData.customerName);$("#multiselectcustomGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));}
							if(jQuery.inArray(itemData.customerName, custnames) == -1){custnames.push(itemData.customerName);$("#multiselectcustomGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));}
						}
						if(itemData.gstNumber) {
							if(counts == 0){gstins.push(itemData.gstNumber);$("#multiselectcustomGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));}
							if(jQuery.inArray(itemData.gstNumber, gstins) == -1){gstins.push(itemData.gstNumber);$("#multiselectcustomGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));}
						}
					});
					totFullAmt = totalamt + pendamt;
					$('#idCountcustomGSTR1').html(transcounts);
					$('#idTotalValcustomGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalamt).toFixed(2)));
					$('#idPendAmtcustomGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pendamt).toFixed(2)));
					$('#idTotalAmtcustomGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totFullAmt).toFixed(2)));
					$("#multiselectcustomGSTR12").multiselect('rebuild');
					$("#multiselectcustomGSTR13").multiselect('rebuild');
					groupPaymentsTable = $("#globalpaymentsalestable3").DataTable({
						"dom": '<"toolbar"f>lrtip<"clear">', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
						"responsive": true,
						"ordering": true,
						"columnDefs": [
							{
								"targets": hiddenCols,
								"visible": false,
								"searchable": true
							}
						] ,
						"language": {
							"search": "_INPUT_",
							"searchPlaceholder": "Search...",
							"paginate": {
							   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
								"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
						   }
						 }
					});
					$(".globalpaymentsalestable3 div.toolbar").append('<a href="${contextPath}/dwnldxlscustomGroupPaymentsReceived/GSTR1/'+clientidsArray+'/'+fromtime+'/'+totime+'" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					groupPaymentsArray['GSTR1'] = response;
					$('#customProcess').hide().text('');
				}else{if ( $.fn.DataTable.isDataTable('#globalpaymentsalestable3') ) {$('#globalpaymentsalestable3').DataTable().destroy();}
			$('#globalpaymentsalestable3 tbody').empty();
			groupPaymentsTable = $("#globalpaymentsalestable3").DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">', 		
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
					"responsive": true,
					"ordering": true,
					"columnDefs": [
						{
							"targets": hiddenCols,
							"visible": false,
							"searchable": true
						}
					] ,
					"language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search...",
						"paginate": {
						   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
							"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
					   }
					 }
				});
			$(".globalpaymentsalestable3 div.toolbar").append('<a href="${contextPath}/dwnldxlscustomGroupPaymentsReceived/GSTR1/'+clientidsArray+'/'+fromtime+'/'+totime+'" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
			$('#idCountcustomGSTR1').html(0);
			$('#idTotalValcustomGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			$('#idTotalAmtcustomGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			$('#idPendAmtcustomGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
				$("#multiselectcustomGSTR12").multiselect('rebuild');
				$("#multiselectcustomGSTR13").multiselect('rebuild');$('#customProcess').hide().text('');
			}
				},error:function(err){
				}
	});
}

function ajaxFunctionMonthly(clientidsArray,month,year){
	if ( $.fn.DataTable.isDataTable('#globalsalestable') ) {$('#globalsalestable').DataTable().destroy();}
	$('#globalsalestable tbody').empty();
	$.ajax({
		url: "${contextPath}/getselectedclientpaymentsmonthly/GSTR1/"+clientidsArray+"/"+month+"/"+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {$('#monthProcess').show().text('Processing...');},
		success : function(response){
			if(response.length > 0){
				var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;var invnos={};var pendamt=0;var totFullAmt=0;
				$('#globalsalestable tbody').html('');
					$.each(response, function(index, itemData) {
						var date = itemData.paymentDate;
						var dt = date.split("-");
						itemData.financialYear = dt[2];
						itemData.month = dt[1];
						if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
							$.each(itemData.paymentitems, function(index, payemntItemData) {
								$('#globalsalestable tbody').append('<tr><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(payemntItemData.amount).toFixed(2))+'</td><td class="text-left">'+payemntItemData.modeOfPayment+'</td><td class="text-left">'+payemntItemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td></tr>');
								totalamt += parseFloat(payemntItemData.amount);
								totFullAmt += parseFloat(payemntItemData.previousPendingBalance);
								pendamt += parseFloat(payemntItemData.pendingBalance);
								counts++;
								i++;
								transcounts++;
							});
						}else{
							$('#globalsalestable tbody').append('<tr><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(itemData.amount).toFixed(2))+'</td><td class="text-left">'+itemData.modeOfPayment+'</td><td class="text-left">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td></tr>');
							totalamt += parseFloat(itemData.amount);
							totFullAmt += parseFloat(itemData.previousPendingBalance);
							pendamt += parseFloat(itemData.pendingBalance);
							counts++;
							i++;
							transcounts++;
						}
						if(itemData.customerName) {
							if(counts == 0){
								custnames.push(itemData.customerName);
								$("#multiselectGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));
								}
							if(jQuery.inArray(itemData.customerName, custnames) == -1){
								custnames.push(itemData.customerName);
								$("#multiselectGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));}
						}
						if(itemData.gstNumber) {
							if(counts == 0){gstins.push(itemData.gstNumber);$("#multiselectGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));}
							if(jQuery.inArray(itemData.gstNumber, gstins) == -1){gstins.push(itemData.gstNumber);$("#multiselectGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));}
						}
					});
					totFullAmt = totalamt + pendamt;
					$('#idCountGSTR1').html(transcounts);
					$('#idTotalValGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalamt).toFixed(2)));
					$('#idTotalPendingGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pendamt).toFixed(2)));
					$('#idTotalAmtGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totFullAmt).toFixed(2)));
					$("#multiselectGSTR12").multiselect('rebuild');
					$("#multiselectGSTR13").multiselect('rebuild');
					
					groupPaymentsTable = $("#globalsalestable").DataTable({
						"dom": '<"toolbar"f>lrtip<"clear">', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
						"responsive": true,
						"ordering": true,
						"columnDefs": [
							{
								"targets": hiddenCols,
								"visible": false,
								"searchable": true
							}
						] ,
						"language": {
							"search": "_INPUT_",
							"searchPlaceholder": "Search...",
							"paginate": {
							   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
								"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
						   }
						 }
					});
					$(".globalsalestable div.toolbar").append('<a href="${contextPath}/dwnldxlsmonthlyGroupPaymentsReceived/GSTR1/'+clientidsArray+'/${month}/${year}" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					groupPaymentsArray['GSTR1'] = response;
					$('#monthProcess').hide().text('');
			}else{if ( $.fn.DataTable.isDataTable('#globalsalestable') ) {$('#globalsalestable').DataTable().destroy();}
			
			$('#globalsalestable tbody').empty();
			groupPaymentsTable = $("#globalsalestable").DataTable({
				"dom": '<"toolbar"f>lrtip<"clear">', 		
				"paging": true,
				"searching": true,
				"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
				"responsive": true,
				"ordering": true,
				"columnDefs": [
					{
						"targets": hiddenCols,
						"visible": false,
						"searchable": true
					}
				] ,
				"language": {
					"search": "_INPUT_",
					"searchPlaceholder": "Search...",
					"paginate": {
					   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
						"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
				   }
				 }
			});
			$(".globalsalestable div.toolbar").append('<a href="${contextPath}/dwnldxlsmonthlyGroupPaymentsReceived/GSTR1/'+clientidsArray+'/${month}/${year}" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>'); 
			$('#idCountGSTR1').html(0);
			$('#idTotalValGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			$('#idTotalPendingGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			$('#idTotalAmtGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
				$("#multiselectGSTR12").multiselect('rebuild');
				$("#multiselectGSTR13").multiselect('rebuild');$('#monthProcess').hide().text('');
			}
				},error:function(err){
				}
	});
}
function ajaxFunctionYearly(clientidsArray,years){
	if ( $.fn.DataTable.isDataTable('#globalpaymentsalestable2') ) {$('#globalpaymentsalestable2').DataTable().destroy();}
	$('#globalpaymentsalestable2 tbody').empty();
	$.ajax({
		url: "${contextPath}/getselectedclientpaymentsyearly/GSTR1/"+clientidsArray+"/"+years,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {$('#yearProcess').show().text('Processing...');},
		success : function(response){
			if(response.length > 0){
				var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;var invnos={};var pendamt=0;var totFullAmt=0;
				$('#globalpaymentsalestable2 tbody').html('');
					$.each(response, function(index, itemData) {
						var date = itemData.paymentDate;
						var dt = date.split("-");
						itemData.financialYear = dt[2];
						itemData.month = dt[1];
						if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
							$.each(itemData.paymentitems, function(index, payemntItemData) {
								$('#globalpaymentsalestable2 tbody').append('<tr><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(payemntItemData.amount).toFixed(2))+'</td><td class="text-left">'+payemntItemData.modeOfPayment+'</td><td class="text-left">'+payemntItemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td></tr>');
								totalamt += parseFloat(payemntItemData.amount);
								totFullAmt += parseFloat(payemntItemData.previousPendingBalance);
								pendamt += parseFloat(payemntItemData.pendingBalance);
								counts++;
								i++;
								transcounts++;
							});
						}else{
							$('#globalpaymentsalestable2 tbody').append('<tr><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(itemData.amount).toFixed(2))+'</td><td class="text-left">'+itemData.modeOfPayment+'</td><td class="text-left">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td></tr>');
							totalamt += parseFloat(itemData.amount);
							totFullAmt += parseFloat(itemData.previousPendingBalance);
							pendamt += parseFloat(itemData.pendingBalance);
							counts++;
							i++;
							transcounts++;
						}
						
						//$('#globalpaymentsalestable2 tbody').append('<tr><td class="text-center">'+i+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center" class="text-center">'+itemData.invoiceNumber+'</td><td class="text-center">'+itemData.customerName+'</td><td class="text-center">'+itemData.gstNumber+'</td><td class="text-right">'+formatNumber(parseFloat(itemData.amount).toFixed(2))+'</td><td class="text-center">'+itemData.modeOfPayment+'</td><td class="text-center">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td></tr>');
						if(itemData.customerName) {
							if(counts == 0){custnames.push(itemData.customerName);$("#multiselectyearGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));}
							if(jQuery.inArray(itemData.customerName, custnames) == -1){custnames.push(itemData.customerName);$("#multiselectyearGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));}
						}
						if(itemData.gstNumber) {
							if(counts == 0){gstins.push(itemData.gstNumber);$("#multiselectyearGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));}
							if(jQuery.inArray(itemData.gstNumber, gstins) == -1){gstins.push(itemData.gstNumber);$("#multiselectyearGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));}
						}
					});
					totFullAmt = totalamt + pendamt;
					$('#idCountyearGSTR1').html(transcounts);
					$('#idTotalValyearGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalamt).toFixed(2)));
					$('#idPendAmtyearGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pendamt).toFixed(2)));
					$('#idTotalamtyearGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totFullAmt).toFixed(2)));
					$("#multiselectyearGSTR12").multiselect('rebuild');
					$("#multiselectyearGSTR13").multiselect('rebuild');
					groupPaymentsTable = $("#globalpaymentsalestable2").DataTable({
						"dom": '<"toolbar"f>lrtip<"clear">', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
						"responsive": true,
						"ordering": true,
						"columnDefs": [
							{
								"targets": hiddenCols,
								"visible": false,
								"searchable": true
							}
						] ,
						"language": {
							"search": "_INPUT_",
							"searchPlaceholder": "Search...",
							"paginate": {
							   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
								"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
						   }
						 }
					});
					groupPaymentsArray['GSTR1'] = response;
					$(".globalpaymentsalestable2 div.toolbar").append('<a href="${contextPath}/dwnldxlsyearlyGroupPaymentsReceived/GSTR1/'+clientidsArray+'/'+years+'" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>'); 
					$('#yearProcess').hide().text('');
			}else{if ( $.fn.DataTable.isDataTable('#globalpaymentsalestable2') ) {$('#globalpaymentsalestable2').DataTable().destroy();}
			$('#globalpaymentsalestable2 tbody').empty();
			groupPaymentsTable = $("#globalpaymentsalestable2").DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">', 		
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
					"responsive": true,
					"ordering": true,
					"columnDefs": [
						{
							"targets": hiddenCols,
							"visible": false,
							"searchable": true
						}
					] ,
					"language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search...",
						"paginate": {
						   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
							"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
					   }
					 }
				});
			$(".globalpaymentsalestable2 div.toolbar").append('<a href="${contextPath}/dwnldxlsyearlyGroupPaymentsReceived/GSTR2/'+clientidsArray+'/${year}" id="monthlydwnldxls" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
			$('#idCountyearGSTR1').html(0);
			$('#idTotalValyearGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			$('#idTotalamtyearGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			$('#idPendAmtyearGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
				$("#multiselectGSTR12").multiselect('rebuild');
				$("#multiselectGSTR13").multiselect('rebuild');$('#yearProcess').hide().text('');
			}
				},error:function(err){
				}
	});
}

</script>

</html>