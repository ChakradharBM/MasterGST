<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Leads</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/admin/leads.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style type="text/css">
.DTFC_LeftBodyLiner,.DTFC_RightBodyLiner{padding-right:0px!important}
#leadsTable{word-break: break-all;}
</style>
</head>
<body>
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
	<div class="bodybreadcrumb">
            <div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="bdcrumb-tabs">
							<ul class="nav nav-tabs" role="tablist">
								<li class="nav-item"><a class="nav-link active" id="all_leads"  href="#" onclick="leadsTableData('allLeadsTab')">All Leads</a></li>
								<li class="nav-item"><a class="nav-link " id="salesteam"  href="#" onclick="leadsTableData('salesTab')">Sales Team</a></li>
								<li class="nav-item"><a class="nav-link" id="partners" href="#" onclick="leadsTableData('partnersTab')">Partners</a></li>
							</ul>
						</div>
					</div>
			</div>
            </div>
        </div>
	<div class="db-ca-wrap">
		<div class="container">
					<div class="">
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type"	style="width: 108px !important; min-width: 36px; left: 14%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					</div>
					
					<div class="typeDiv" style="display: inline-flex;">
					<span class="dropdown-toggle" data-toggle="dropdown" id="typeoption" style="margin-right: 10px; display: inline-flex;"><label>Type:</label>
						<div class="typ-day" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="type_option" class="type_option"	style="vertical-align: top;">Day Wise</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu day-type"	style="width: 108px !important; min-width: 36px; left: 38%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="DayWise" onClick="getDowVal('Day Wise')">Day Wise </a> <a class="dropdown-item" href="#" value="WeekWise" onClick="getDowVal('Week Wise')">Week Wise</a>
					</div>
					</div>
					
					<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap_leads pull-right">
							<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>
							
							<button class="btn btn-greendark pull-right generate" style="padding: 4px 10px;font-size:14px" onClick="generateData('allLeadsTab')">Generate</button>
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
						<button class="btn btn-greendark pull-right generate" style="padding: 4px 10px;font-size:14px" onClick="generateData('allLeadsTab')">Generate</button>
					</span>
				</div>
			</div>
		<div class="tab-pane" id="gtab1" role="tabpanel">
			    <div class="normaltable meterialform" id="updatefilter_summary_reports">
			         <div class="filter">
			            <div class="noramltable-row">
			                <div class="noramltable-row-hdr">Filter</div>
			                <div class="noramltable-row-desc">
			                    <div class="sfilter"><span id="divFiltersPartner"></span>
			                        <span class="btn-remove-tag" onClick="clearPartnerFiltersReports()">Clear All<span data-role="remove"></span></span>
			                    </div>
			                </div>
			            </div>
			        </div>
			        <div class="noramltable-row">
			            <div class="noramltable-row-hdr">Search Filter</div>
			            <div class="noramltable-row-desc">
			              	<select id="multiselectPartnerType" class="multiselect-ui form-control" multiple="multiple">
			              		<option value="Sales Team">Sales Team</option>
			              		<option value="Silver Partner">Silver Partner</option>
			              		<option value="Gold Partner">Gold Partner</option>
			              		<option value="Platinum Partner">Platinum Partner</option>
			              	</select>
							<select id="multiselectPartnerName" class="multiselect-ui form-control" multiple="multiple">
			              	</select>
			            </div>
			        </div>
			        <div class="noramltable-row">
			            <div class="noramltable-row-hdr">Summary</div>
			            <div class="noramltable-row-desc">
			                <div class="normaltable-col hdr">Total Leads<div class="normaltable-col-txt" id="idCountLeads">0.00</div></div>
			                <div class="normaltable-col hdr">Total New<div class="normaltable-col-txt" id="idCountNew">0.00</div></div>
			                <div class="normaltable-col hdr">Total Demo<div class="normaltable-col-txt" id="idCountDemo">0.00</div></div>
			                <div class="normaltable-col hdr">Total Pending<div class="normaltable-col-txt" id="idCountPending">0.00</div></div>
			                <div class="normaltable-col hdr">Total Joined<div class="normaltable-col-txt" id="idCountJoined">0.00</div></div>
			                <div class="normaltable-col hdr">Estimated Revenue<div class="normaltable-col-txt ind_formatss" id="idCountRevenue">0.00</div></div>
			                <div class="normaltable-col hdr">Subscription Revenue<div class="normaltable-col-txt ind_formatss" id="idCountSubRevenue">0.00</div></div>
			            </div>
			        </div>
			    </div>
			</div>
			<h6>Summary Table</h6>
		<div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="daySummaryTable" style="">
				    <table id="dayTable" class="display row-border dataTable fixed-col meterialform" cellspacing="0" width="100%">
				        <thead>
				            <tr>
								<th class="text-center">Details</th><th class="text-center">01</th><th class="text-center">02</th><th class="text-center">03</th><th class="text-center">04</th><th class="text-center">05</th><th class="text-center">06</th><th class="text-center">07</th><th class="text-center">08</th><th class="text-center">09</th><th class="text-center">10</th>
								<th class="text-center">11</th><th class="text-center">12</th><th class="text-center">13</th><th class="text-center">14</th><th class="text-center">15</th><th class="text-center">16</th><th class="text-center">17</th><th class="text-center">18</th><th class="text-center">19</th><th class="text-center">20</th>
								<th class="text-center">21</th><th class="text-center">22</th><th class="text-center">23</th><th class="text-center">24</th><th class="text-center">25</th><th class="text-center">26</th><th class="text-center">27</th><th class="text-center">28</th><th class="text-center">29</th><th class="text-center">30</th><th class="text-center">31</th><th class="text-center">Total</th>
							</tr>
				        </thead>
				        <tbody id="yeartotoalreport">
					      <tr>
					        <td align="center"><h6>Total Leads</h6> </td>
					        <td class="text-right" id="totalleads1">0</td>
					        <td class="text-right" id="totalleads2">0</td>
					        <td class="text-right" id="totalleads3">0</td>
					        <td class="text-right" id="totalleads4">0</td>
					        <td class="text-right" id="totalleads5">0</td>
					        <td class="text-right" id="totalleads6">0</td>
					        <td class="text-right" id="totalleads7">0</td>
					        <td class="text-right" id="totalleads8">0</td>
					        <td class="text-right" id="totalleads9">0</td>
					        <td class="text-right" id="totalleads10">0</td>
					        
					        <td class="text-right" id="totalleads11">0</td>
					        <td class="text-right" id="totalleads12">0</td>
					        <td class="text-right" id="totalleads13">0</td>
					        <td class="text-right" id="totalleads14">0</td>
					        <td class="text-right" id="totalleads15">0</td>
					        <td class="text-right" id="totalleads16">0</td>
					        <td class="text-right" id="totalleads17">0</td>
					        <td class="text-right" id="totalleads18">0</td>
					        <td class="text-right" id="totalleads19">0</td>
					        <td class="text-right" id="totalleads20">0</td>
					        
					        <td class="text-right" id="totalleads21">0</td>
					        <td class="text-right" id="totalleads22">0</td>
					        <td class="text-right" id="totalleads23">0</td>
					        <td class="text-right" id="totalleads24">0</td>
					        <td class="text-right" id="totalleads25">0</td>
					        <td class="text-right" id="totalleads26">0</td>
					        <td class="text-right" id="totalleads27">0</td>
					        <td class="text-right" id="totalleads28">0</td>
					        <td class="text-right" id="totalleads29">0</td>
					        <td class="text-right" id="totalleads30">0</td>
					        <td class="text-right" id="totalleads31">0</td>
					        <td class="text-right totLeads" id="totalleadstotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Total New</h6> </td>
					        <td class="text-right" id="totalNew1">0</td>
					        <td class="text-right" id="totalNew2">0</td>
					        <td class="text-right" id="totalNew3">0</td>
					        <td class="text-right" id="totalNew4">0</td>
					        <td class="text-right" id="totalNew5">0</td>
					        <td class="text-right" id="totalNew6">0</td>
					        <td class="text-right" id="totalNew7">0</td>
					        <td class="text-right" id="totalNew8">0</td>
					        <td class="text-right" id="totalNew9">0</td>
					        <td class="text-right" id="totalNew10">0</td>
					        
					        <td class="text-right" id="totalNew11">0</td>
					        <td class="text-right" id="totalNew12">0</td>
					        <td class="text-right" id="totalNew13">0</td>
					        <td class="text-right" id="totalNew14">0</td>
					        <td class="text-right" id="totalNew15">0</td>
					        <td class="text-right" id="totalNew16">0</td>
					        <td class="text-right" id="totalNew17">0</td>
					        <td class="text-right" id="totalNew18">0</td>
					        <td class="text-right" id="totalNew19">0</td>
					        <td class="text-right" id="totalNew20">0</td>
					        
					        <td class="text-right" id="totalNew21">0</td>
					        <td class="text-right" id="totalNew22">0</td>
					        <td class="text-right" id="totalNew23">0</td>
					        <td class="text-right" id="totalNew24">0</td>
					        <td class="text-right" id="totalNew25">0</td>
					        <td class="text-right" id="totalNew26">0</td>
					        <td class="text-right" id="totalNew27">0</td>
					        <td class="text-right" id="totalNew28">0</td>
					        <td class="text-right" id="totalNew29">0</td>
					        <td class="text-right" id="totalNew30">0</td>
					        <td class="text-right" id="totalNew31">0</td>
					        <td class="text-right totNew" id="totalNewtotals">0</td>
					        </tr>
					        
					        <tr><td align="center"><h6>Total Demo</h6> </td>
					        <td class="text-right" id="totalDemo1">0</td>
					        <td class="text-right" id="totalDemo2">0</td>
					        <td class="text-right" id="totalDemo3">0</td>
					        <td class="text-right" id="totalDemo4">0</td>
					        <td class="text-right" id="totalDemo5">0</td>
					        <td class="text-right" id="totalDemo6">0</td>
					        <td class="text-right" id="totalDemo7">0</td>
					        <td class="text-right" id="totalDemo8">0</td>
					        <td class="text-right" id="totalDemo9">0</td>
					        <td class="text-right" id="totalDemo10">0</td>
					        
					        <td class="text-right" id="totalDemo11">0</td>
					        <td class="text-right" id="totalDemo12">0</td>
					        <td class="text-right" id="totalDemo13">0</td>
					        <td class="text-right" id="totalDemo14">0</td>
					        <td class="text-right" id="totalDemo15">0</td>
					        <td class="text-right" id="totalDemo16">0</td>
					        <td class="text-right" id="totalDemo17">0</td>
					        <td class="text-right" id="totalDemo18">0</td>
					        <td class="text-right" id="totalDemo19">0</td>
					        <td class="text-right" id="totalDemo20">0</td>
					        
					        <td class="text-right" id="totalDemo21">0</td>
					        <td class="text-right" id="totalDemo22">0</td>
					        <td class="text-right" id="totalDemo23">0</td>
					        <td class="text-right" id="totalDemo24">0</td>
					        <td class="text-right" id="totalDemo25">0</td>
					        <td class="text-right" id="totalDemo26">0</td>
					        <td class="text-right" id="totalDemo27">0</td>
					        <td class="text-right" id="totalDemo28">0</td>
					        <td class="text-right" id="totalDemo29">0</td>
					        <td class="text-right" id="totalDemo30">0</td>
					        <td class="text-right" id="totalDemo31">0</td>
					        <td class="text-right totDemo" id="totalDemototals">0</td>
					        </tr>
					        
						  	 <tr><td align="center"><h6>Total Pending</h6> </td>
					        <td class="text-right" id="totalPending1">0</td>
					        <td class="text-right" id="totalPending2">0</td>
					        <td class="text-right" id="totalPending3">0</td>
					        <td class="text-right" id="totalPending4">0</td>
					        <td class="text-right" id="totalPending5">0</td>
					        <td class="text-right" id="totalPending6">0</td>
					        <td class="text-right" id="totalPending7">0</td>
					        <td class="text-right" id="totalPending8">0</td>
					        <td class="text-right" id="totalPending9">0</td>
					        <td class="text-right" id="totalPending10">0</td>
					        
					        <td class="text-right" id="totalPending11">0</td>
					        <td class="text-right" id="totalPending12">0</td>
					        <td class="text-right" id="totalPending13">0</td>
					        <td class="text-right" id="totalPending14">0</td>
					        <td class="text-right" id="totalPending15">0</td>
					        <td class="text-right" id="totalPending16">0</td>
					        <td class="text-right" id="totalPending17">0</td>
					        <td class="text-right" id="totalPending18">0</td>
					        <td class="text-right" id="totalPending19">0</td>
					        <td class="text-right" id="totalPending20">0</td>
					        
					        <td class="text-right" id="totalPending21">0</td>
					        <td class="text-right" id="totalPending22">0</td>
					        <td class="text-right" id="totalPending23">0</td>
					        <td class="text-right" id="totalPending24">0</td>
					        <td class="text-right" id="totalPending25">0</td>
					        <td class="text-right" id="totalPending26">0</td>
					        <td class="text-right" id="totalPending27">0</td>
					        <td class="text-right" id="totalPending28">0</td>
					        <td class="text-right" id="totalPending29">0</td>
					        <td class="text-right" id="totalPending30">0</td>
					        <td class="text-right" id="totalPending31">0</td>
					        <td class="text-right totPending" id="totalPendingtotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Total Joined</h6> </td>
					        <td class="text-right" id="totalJoined1">0</td>
					        <td class="text-right" id="totalJoined2">0</td>
					        <td class="text-right" id="totalJoined3">0</td>
					        <td class="text-right" id="totalJoined4">0</td>
					        <td class="text-right" id="totalJoined5">0</td>
					        <td class="text-right" id="totalJoined6">0</td>
					        <td class="text-right" id="totalJoined7">0</td>
					        <td class="text-right" id="totalJoined8">0</td>
					        <td class="text-right" id="totalJoined9">0</td>
					        <td class="text-right" id="totalJoined10">0</td>
					        
					        <td class="text-right" id="totalJoined11">0</td>
					        <td class="text-right" id="totalJoined12">0</td>
					        <td class="text-right" id="totalJoined13">0</td>
					        <td class="text-right" id="totalJoined14">0</td>
					        <td class="text-right" id="totalJoined15">0</td>
					        <td class="text-right" id="totalJoined16">0</td>
					        <td class="text-right" id="totalJoined17">0</td>
					        <td class="text-right" id="totalJoined18">0</td>
					        <td class="text-right" id="totalJoined19">0</td>
					        <td class="text-right" id="totalJoined20">0</td>
					        
					        <td class="text-right" id="totalJoined21">0</td>
					        <td class="text-right" id="totalJoined22">0</td>
					        <td class="text-right" id="totalJoined23">0</td>
					        <td class="text-right" id="totalJoined24">0</td>
					        <td class="text-right" id="totalJoined25">0</td>
					        <td class="text-right" id="totalJoined26">0</td>
					        <td class="text-right" id="totalJoined27">0</td>
					        <td class="text-right" id="totalJoined28">0</td>
					        <td class="text-right" id="totalJoined29">0</td>
					        <td class="text-right" id="totalJoined30">0</td>
					        <td class="text-right" id="totalJoined31">0</td>
					        <td class="text-right totJoined" id="totalJoinedtotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Estimated revenue</h6> </td>
					        <td class="text-right ind_formatss" id="totalestrev1">0</td>
					        <td class="text-right ind_formatss" id="totalestrev2">0</td>
					        <td class="text-right ind_formatss" id="totalestrev3">0</td>
					        <td class="text-right ind_formatss" id="totalestrev4">0</td>
					        <td class="text-right ind_formatss" id="totalestrev5">0</td>
					        <td class="text-right ind_formatss" id="totalestrev6">0</td>
					        <td class="text-right ind_formatss" id="totalestrev7">0</td>
					        <td class="text-right ind_formatss" id="totalestrev8">0</td>
					        <td class="text-right ind_formatss" id="totalestrev9">0</td>
					        <td class="text-right ind_formatss" id="totalestrev10">0</td>
					        
					        <td class="text-right ind_formatss" id="totalestrev11">0</td>
					        <td class="text-right ind_formatss" id="totalestrev12">0</td>
					        <td class="text-right ind_formatss" id="totalestrev13">0</td>
					        <td class="text-right ind_formatss" id="totalestrev14">0</td>
					        <td class="text-right ind_formatss" id="totalestrev15">0</td>
					        <td class="text-right ind_formatss" id="totalestrev16">0</td>
					        <td class="text-right ind_formatss" id="totalestrev17">0</td>
					        <td class="text-right ind_formatss" id="totalestrev18">0</td>
					        <td class="text-right ind_formatss" id="totalestrev19">0</td>
					        <td class="text-right ind_formatss" id="totalestrev20">0</td>
					        
					        <td class="text-right ind_formatss" id="totalestrev21">0</td>
					        <td class="text-right ind_formatss" id="totalestrev22">0</td>
					        <td class="text-right ind_formatss" id="totalestrev23">0</td>
					        <td class="text-right ind_formatss" id="totalestrev24">0</td>
					        <td class="text-right ind_formatss" id="totalestrev25">0</td>
					        <td class="text-right ind_formatss" id="totalestrev26">0</td>
					        <td class="text-right ind_formatss" id="totalestrev27">0</td>
					        <td class="text-right ind_formatss" id="totalestrev28">0</td>
					        <td class="text-right ind_formatss" id="totalestrev29">0</td>
					        <td class="text-right ind_formatss" id="totalestrev30">0</td>
					        <td class="text-right ind_formatss" id="totalestrev31">0</td>
					        <td class="text-right ind_formatss totEstimatedAmt" id="totalestrevtotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Subscription Revenue</h6> </td>
					        <td class="text-right ind_formatss" id="totalsubscrrev1">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev2">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev3">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev4">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev5">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev6">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev7">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev8">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev9">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev10">0</td>
					        
					        <td class="text-right ind_formatss" id="totalsubscrrev11">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev12">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev13">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev14">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev15">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev16">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev17">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev18">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev19">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev20">0</td>
					        
					        <td class="text-right ind_formatss" id="totalsubscrrev21">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev22">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev23">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev24">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev25">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev26">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev27">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev28">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev29">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev30">0</td>
					        <td class="text-right ind_formatss" id="totalsubscrrev31">0</td>
					        <td class="text-right ind_formatss totSubscriptionAmt" id="totalsubscrrevtotals">0</td>
					        </tr>
						  	
				        </tbody>
				    </table>
			</div>
		
		<div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="weekSummaryTable" style="display:none">
				    <table id="weekTable" class="display row-border dataTable fixed-col meterialform" cellspacing="0" width="100%">
				        <thead>
				            <tr>
								<th class="text-center">Details</th><th class="text-center">Week 1</th><th class="text-center">Week 2</th><th class="text-center">Week 3</th><th class="text-center">Week 4</th><th class="text-center">Week 5</th>
								<th class="text-center">Total</th>
							</tr>
				        </thead>
				        <tbody id="">
					         <tr><td align="center"><h6>Total Leads</h6> </td>
					        <td class="text-right" id="wtotalleads1">0</td>
					        <td class="text-right" id="wtotalleads2">0</td>
					        <td class="text-right" id="wtotalleads3">0</td>
					        <td class="text-right" id="wtotalleads4">0</td>
					        <td class="text-right" id="wtotalleads5">0</td>
					        
					        <td class="text-right totLeads" id="wtotalleadstotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Total New</h6> </td>
					        <td class="text-right" id="wtotalNew1">0</td>
					        <td class="text-right" id="wtotalNew2">0</td>
					        <td class="text-right" id="wtotalNew3">0</td>
					        <td class="text-right" id="wtotalNew4">0</td>
					        <td class="text-right" id="wtotalNew5">0</td>
					        
					        <td class="text-right totNew" id="wtotalNewtotals">0</td>
					        </tr>
					         <tr><td align="center"><h6>Total Demo</h6> </td>
					        <td class="text-right" id="wtotalDemo1">0</td>
					        <td class="text-right" id="wtotalDemo2">0</td>
					        <td class="text-right" id="wtotalDemo3">0</td>
					        <td class="text-right" id="wtotalDemo4">0</td>
					        <td class="text-right" id="wtotalDemo5">0</td>
					       
					        <td class="text-right totDemo" id="wtotalDemototals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Total Pending</h6> </td>
					        <td class="text-right" id="wtotalPending1">0</td>
					        <td class="text-right" id="wtotalPending2">0</td>
					        <td class="text-right" id="wtotalPending3">0</td>
					        <td class="text-right" id="wtotalPending4">0</td>
					        <td class="text-right" id="wtotalPending5">0</td>
					        
					        <td class="text-right totPending" id="wtotalPendingtotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Total Joined</h6> </td>
					        <td class="text-right" id="wtotalJoined1">0</td>
					        <td class="text-right" id="wtotalJoined2">0</td>
					        <td class="text-right" id="wtotalJoined3">0</td>
					        <td class="text-right" id="wtotalJoined4">0</td>
					        <td class="text-right" id="wtotalJoined5">0</td>
					       
					        <td class="text-right totJoined" id="wtotalJoinedtotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Estimated revenue</h6> </td>
					        <td class="text-right ind_formatss" id="wtotalestrev1">0</td>
					        <td class="text-right ind_formatss" id="wtotalestrev2">0</td>
					        <td class="text-right ind_formatss" id="wtotalestrev3">0</td>
					        <td class="text-right ind_formatss" id="wtotalestrev4">0</td>
					        <td class="text-right ind_formatss" id="wtotalestrev5">0</td>
					        
					        <td class="text-right ind_formatss totEstimatedAmt" id="wtotalestrevtotals">0</td>
					        </tr>
						  	 <tr><td align="center"><h6>Subscription Revenue</h6> </td>
					        <td class="text-right ind_formatss" id="wtotalsubscrrev1">0</td>
					        <td class="text-right ind_formatss" id="wtotalsubscrrev2">0</td>
					        <td class="text-right ind_formatss" id="wtotalsubscrrev3">0</td>
					        <td class="text-right ind_formatss" id="wtotalsubscrrev4">0</td>
					        <td class="text-right ind_formatss" id="wtotalsubscrrev5">0</td>
					       
					        <td class="text-right ind_formatss totSubscriptionAmt" id="wtotalsubscrrevtotals">0</td>
					        </tr>
						  	
				        </tbody>
				    </table>
			</div>
		
		<div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="YaerlySummaryTable" style="display:none;">
				    <table id="yearlyTable" class="display row-border dataTable fixed-col meterialform" cellspacing="0" width="100%">
				        <thead>
				            <tr>
								<th class="text-center">Details</th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th>
								<th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th>
								<th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th><th class="text-center">Total</th>
							</tr>
				        </thead>
				        <tbody id="yeartotoalreport">
					        <tr><td align="center"><h6>Total Leads</h6> </td><td class="text-right" id="ytotalleads4">0</td><td class="text-right" id="ytotalleads5">0</td><td class="text-right" id="ytotalleads6">0</td><td class="text-right" id="ytotalleads7">0</td><td class="text-right" id="ytotalleads8">0</td><td class="text-right" id="ytotalleads9">0</td><td class="text-right" id="ytotalleads10">0</td><td class="text-right" id="ytotalleads11">0</td><td class="text-right" id="ytotalleads12">0</td><td class="text-right" id="ytotalleads1">0</td><td class="text-right" id="ytotalleads2">0</td><td class="text-right" id="ytotalleads3">0</td><td class="text-right totLeads" id="ytotalleadstotals">0</td></tr>
						  	<tr><td align="center"><h6>Total New</h6> </td><td class="text-right" id="ytotalNew4">0.0</td><td class="text-right " id="ytotalNew5">0.0</td><td class="text-right" id="ytotalNew6">0.0</td><td class="text-right" id="ytotalNew7">0.0</td><td class="text-right" id="ytotalNew8">0.0</td><td class="text-right" id="ytotalNew9">0.0</td><td class="text-right" id="ytotalNew10">0.0</td><td class="text-right" id="ytotalNew11">0.0</td><td class="text-right" id="ytotalNew12">0.0</td><td class="text-right" id="ytotalNew1">0.0</td><td class="text-right" id="ytotalNew2">0.0</td><td class="text-right" id="ytotalNew3">0.0</td><td class="text-right totNew" id="ytotalNewtotals">0.0</td></tr>
						  	<tr><td align="center"><h6>Total Demo</h6> </td><td class="text-right" id="ytotalDemo4">0.0</td><td class="text-right " id="ytotalDemo5">0.0</td><td class="text-right" id="ytotalDemo6">0.0</td><td class="text-right" id="ytotalDemo7">0.0</td><td class="text-right" id="ytotalDemo8">0.0</td><td class="text-right" id="ytotalDemo9">0.0</td><td class="text-right" id="ytotalDemo10">0.0</td><td class="text-right" id="ytotalDemo11">0.0</td><td class="text-right" id="ytotalDemo12">0.0</td><td class="text-right" id="ytotalDemo1">0.0</td><td class="text-right" id="ytotalDemo2">0.0</td><td class="text-right" id="ytotalDemo3">0.0</td><td class="text-right totDemo" id="ytotalDemototals">0.0</td></tr>
						  	<tr><td align="center"><h6>Total Pending</h6> </td><td class="text-right" id="ytotalPending4">0.0</td><td class="text-right" id="ytotalPending5">0.0</td><td class="text-right" id="ytotalPending6">0.0</td><td class="text-right" id="ytotalPending7">0.0</td><td class="text-right" id="ytotalPending8">0.0</td><td class="text-right" id="ytotalPending9">0.0</td><td class="text-right" id="ytotalPending10">0.0</td><td class="text-right" id="ytotalPending11">0.0</td><td class="text-right" id="ytotalPending12">0.0</td><td class="text-right" id="ytotalPending1">0.0</td><td class="text-right" id="ytotalPending2">0.0</td><td class="text-right" id="ytotalPending3">0.0</td><td class="text-right totPending" id="ytotalPendingtotals">0.0</td></tr>
						  	<tr><td align="center"><h6>Total Joined</h6> </td><td class="text-right" id="ytotalJoined4">0.0</td><td class="text-right" id="ytotalJoined5">0.0</td><td class="text-right" id="ytotalJoined6">0.0</td><td class="text-right" id="ytotalJoined7">0.0</td><td class="text-right" id="ytotalJoined8">0.0</td><td class="text-right" id="ytotalJoined9">0.0</td><td class="text-right" id="ytotalJoined10">0.0</td><td class="text-right" id="ytotalJoined11">0.0</td><td class="text-right" id="ytotalJoined12">0.0</td><td class="text-right" id="ytotalJoined1">0.0</td><td class="text-right" id="ytotalJoined2">0.0</td><td class="text-right" id="ytotalJoined3">0.0</td><td class="text-right totJoined" id="ytotalJoinedtotals">0.0</td></tr>
						  	<tr><td align="center"><h6>Estimated Revenue</h6> </td><td class="text-right ind_formatss" id="ytotalestrev4">0.0</td><td class="text-right ind_formatss" id="ytotalestrev5">0.0</td><td class="text-right ind_formatss" id="ytotalestrev6">0.0</td><td class="text-right ind_formatss" id="ytotalestrev7">0.0</td><td class="text-right ind_formatss" id="ytotalestrev8">0.0</td><td class="text-right ind_formatss" id="ytotalestrev9">0.0</td><td class="text-right ind_formatss" id="ytotalestrev10">0.0</td><td class="text-right ind_formatss" id="ytotalestrev11">0.0</td><td class="text-right ind_formatss" id="ytotalestrev12">0.0</td><td class="text-right ind_formatss" id="ytotalestrev1">0.0</td><td class="text-right ind_formatss" id="ytotalestrev2">0.0</td><td class="text-right ind_formatss" id="ytotalestrev3">0.0</td><td class="text-right ind_formatss totEstimatedAmt" id="ytotalestrevtotals">0.0</td></tr>
						  	<tr><td align="center"><h6>Subscription Revenue</h6> </td><td class="text-right ind_formatss" id="ytotalsubscrrev4">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev5">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev6">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev7">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev8">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev9">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev10">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev11">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev12">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev1">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev2">0.0</td><td class="text-right ind_formatss" id="ytotalsubscrrev3">0.0</td><td class="text-right ind_formatss totSubscriptionAmt" id="ytotalsubscrrevtotals">0.0</td></tr>
						  	
				        </tbody>
				    </table>
				</div>
		
		
		    	<div class="partner-data">
					<div class="customtable db-ca-view leadsTable">
						<table id="leadsTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
			              <thead>
			                <tr>
				                  <th class="text-center">Partner Name</th>
				                  <th class="text-center">Partner Phone No</th>
				                  <th class="text-center">Partner Email</th>
								  <th class="text-center">User Name</th>
				                  <th class="text-center">User Email</th>
				                  <th class="text-center">User Phone No</th>
				                  <th class="text-center">User Type</th>
				                  <th class="text-center">Est.Revenue</th>
								  <th class="text-center">Sub.Amt</th>
				                  <th class="text-center">Created Date</th>
								  <th class="text-center">Join Date</th>
				                  <th class="text-center">Status</th>
				                    <th class="text-center">Notes</th>
			                </tr>
			              </thead>
			              <tbody>         
			              </tbody>
		            	</table>
		          	</div>		
				</div>
	    	</div>
	</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->
	 <!-- inviteBusinessModal Start -->
<div class="modal fade" id="busineesModal" role="dialog" aria-labelledby="busineesModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content" style="height:100vh;">
       <div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3 id="leadorBusinessTxt">View Lead</h3>
                    </div>
				</div>
      	<div class="modal-body meterialform popupright bs-fancy-checks">
			 <div class="row pl-4 pr-4 pt-4 pb-0">
                        <form:form method="POST" data-toggle="validator" id="businessinvite" class="meterialform row pr-4 pl-4" name="userform" action="" modelAttribute="client"> 
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Customer Name</p>
                            <span class="errormsg" id="customerName_Msg"></span>
                            <input type="text" id="customerName" name="name" required="required" placeholder="Rajesh" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Customer Email </p>
                            <span class="errormsg" id="customerEmail_Msg"></span>
                            <input type="text" id="customerEmail" name="email" required="required" placeholder="rajesh@gmail.com" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Phone</p>
                            <span class="errormsg" id="phone_Msg"></span>
                            <p class="indiamobilecode">+91</p><input type="text" id="mobileId" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" required="required" placeholder="9848012345" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
							<div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt">User Type</p>
	                            <select class="form-control" name="clienttype" id="clienttype"><option value="">--Select--</option><option value="cacmas">CA/CMA/CS/TAX PROFESSIONAL</option><option value="suvidha">SUVIDHA CENTERS</option><option value="business">SMALL/MEDIUM BUSINESS</option><option value="enterprise">ENTERPRISE</option><option value="partner">PARTNER</option><option value="aspdeveloper">ASP/DEVELOPER</option></select>
	                            <label for="input" class="control-label"></label>
	                            <i class="bar"></i> 
                          	</div>
							 <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">Estimated Cost </p>
		                            <input type="text" id="estimatedCost" name="estimatedCost"  placeholder="123456" value="" />
		                            <label for="input" class="control-label"></label>
		                            <i class="bar"></i>
	                          </div>
	                          <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">State </p>
                            <input type="text" id="state" name="state" value="" />
                            <label for="state" class="control-label"></label>
								<div class="help-block with-errors"></div>
								<div id="stateempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
								</div>
								<i class="bar"></i></div>
								<div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">City </p>
		                            <input type="text" id="city" name="city"  value="" />
		                            <label for="input" class="control-label"></label>
		                            <i class="bar"></i>
		                       </div>	
		                       <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">Industry/Vertical </p>
		                            <input type="text" id="industryType" name="industryType"  value="" />
		                            <label for="input" class="control-label"></label>
		                            <div class="help-block with-errors"></div>
								<div id="industryTypeempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
		                       </div><i class="bar"></i></div>		
		                        <div class="form-group col-md-6 col-sm-12">
									 <p class="lable-txt">Product Type</p>
									  <select id="productType" class="mt-1" name="productType" multiple value="" >
									  <c:forEach items="${productTypes}" var="type">
									  <option value="${type}">${type}</option>
									  </c:forEach> 
									  </select>
									  <label for="input" class="control-label"></label><i class="bar"></i>
							 </div>
							  <div class="form-group col-md-6 col-sm-12">
							  		<div class="form-group col-md-12 col-sm-12 ">
			                            <p class="lable-txt col-md-7 pl-0 pr-0" style="display:inline-block;">Demo Status </p>
			                            <div class="form-check form-check-inline ml-3">
		                                    <input class="form-check-input" type="checkbox" id="demostatus" name="demostatus" value=""/>
		                                    <label for="demostatus"><span class="ui"></span></label>
	                                	</div>
	                                </div>
	                                <div class="form-group col-md-12 col-sm-12">
		                            <p class="lable-txt col-md-7 pl-0 pr-0" style="display:inline-block;">Need To FollowUp </p>
		                            <div class="form-check form-check-inline ml-3">
	                                    <input class="form-check-input" type="checkbox" id="followup" name="needFollowup" value=""/>
	                                    <label for="followup"><span class="ui"></span>
	                                    </label> <!-- <span class="labletxt" style="margin-top:0px!important">Active</span> -->
                                	</div>
		                       </div>	
		                       </div>		
		                       <div class="form-group col-md-6 col-sm-12" id="followupdateDiv" style="display:none;">
		                            <p class="lable-txt">FollowUp Date</p>
		                            <input type="text" id="needFollowupdate" name="needFollowupdate" placeholder="dd/MM/YYYY" value="" />
		                            <label for="input" class="control-label"></label>
		                            <i class="bar"></i>
                             </div>
                             	
                            	
						<div class="form-group col-md-12 col-sm-12 mb-2">
                            <p class="lable-txt astrich">Description</p>
                            <span class="errormsg" id="paidAmount_Msg"></span>
                            <textarea  id="description" name="content" required="required" placeholder="I have noticed MasterGST is so simple and easy to use and file GST returns, I highly recommend this GST Software. Please click on the link to signup." ></textarea>
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div> 
                            
					  </div>
			</form:form>
      </div>
      	<div class="modal-footer text-center mt-0 pt-0" style="display:block">
				<a type="button" class="btn btn-blue-dark" data-toggle="modal" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">Close</span></a>
		</div>
       
    </div>
  </div>
</div>
<!-- inviteBusinessModal End -->
	<!-- commentsModal Start -->			
	<div class="modal fade" id="leadCommentsModal" role="dialog" aria-labelledby="leadCommentsModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content" style="height:100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Show Comments/Narration</h3>
                    </div>
				</div>
				<div class="modal-body meterialform popupright">
					<div class="row pl-4 pr-2 pt-4">
					<span id="nocomment_leads" class="pl-2"></span>
					<div class="form-group col-md-12">
						<div class="leadscommentTab" style="max-height:300px;min-height:300px;overflow-y:auto;">
						</div>
					</div>
						
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
				 <button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>	
<!-- commentsModal End -->		
</body>
<script>
$('#leads_lnk').addClass('active');
var user = '<c:out value="${user}"/>';
var date = new Date();
var mth = date.getMonth()+1;
var	yer = date.getFullYear();
$('#productType').select2();
	$('#needFollowupdate').datepicker({
		format: 'dd/mm/yyyy',
		
	});
var leadsTable;
loadLeadsTable('allLeadsTab',mth,yer,'Monthly');
loadSummaryTable('dayTable',mth,yer,'Monthly','allLeadsTab');
initiateCallBacksForMultiSelectReports();
loadPartnerSupport('dayTable',mth,yer,'Monthly','allLeadsTab',loadPartnersInDropdown);
initializeRemoveAppliedFilters();
function generateData(tabType) {
	clearPartnerFiltersReports();
	var abc = $('#fillingoption span').html();
	var dow = $('#type_option').text();
	var type='';
	if(abc == 'Monthly'){
		$('#YaerlySummaryTable').css("display","none");
		var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
		loadLeadsTable(tabType,mn,yr,abc);
		loadPartnerSupport(tabType,mn,yr,abc,tabType,loadPartnersInDropdown);
		if(dow == "Day Wise"){
			$('#daySummaryTable').css("display","block");$('#weekSummaryTable,#YaerlySummaryTable').css("display","none");
			loadSummaryTable('dayTable',mn,yr,abc,tabType);
		}else{
			$('#weekSummaryTable').css("display","block");$('#daySummaryTable,#YaerlySummaryTable').css("display","none");
			loadSummaryTable('weekTable',mn,yr,abc,tabType);
		}
	}else if(abc == 'Yearly'){
		var year=$('#yearlyoption').html().split("-");
		var yr = parseInt(year[1]);
		$('#YaerlySummaryTable').css("display","block");$('#daySummaryTable,#weekSummaryTable').css("display","none");
		loadLeadsTable(tabType,0,yr,abc);
		loadSummaryTable('yearlyTable',0,yr,abc,tabType);
		loadPartnerSupport(tabType,0,yr,abc,tabType,loadPartnersInDropdown);
	}else{
		$('#YaerlySummaryTable').css("display","block");$('#daySummaryTable,#weekSummaryTable').css("display","none");
		loadYearlySummaryTable();
	}
	initiateCallBacksForMultiSelectReports();
	initializeRemoveAppliedFilters();
}
$('#followup').change(function() {
	if($('#followup').is(':checked')){
		$('#followup').val("true");
		$('#followupdateDiv').css("display","block");
	}else{
		$('#followup').val("false");
		$('#followupdateDiv').css("display","none");
	}
	$('#needFollowupdate').val("");
});
</script>
</html>