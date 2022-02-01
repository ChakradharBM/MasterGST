<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value="<%=MasterGSTConstants.GSTR2%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Group Wise Purchase Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/reports/globalSales_reports.js" type="text/javascript"></script>
<style type="text/css">
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
button.multiselect.dropdown-toggle.btn-block.text-left.btn.btn-default{text-align: center!important;}
ul.multiselect-container.dropdown-menu{width:max-content;max-width: 300px;overflow-x: auto;}
ul.multiselect-container.dropdown-menu li{white-space: pre;}
ul.multiselect-container.dropdown-menu::-webkit-scrollbar-track{-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);border-radius: 10px;background-color: #ffffff;}
ul.multiselect-container.dropdown-menu::-webkit-scrollbar{width: 12px;background-color: #F5F5F5;}
ul.multiselect-container.dropdown-menu::-webkit-scrollbar-thumb{border-radius: 10px;-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);	background-color: #e5e5e5;}
div#globalsalestable1_length, div#globalsalestable2_length, div#globalsalestable3_length {position: relative;}
.dropdown-menu.ret-type{left:16.4%!important;top: 28px;width: 108px;min-width: unset;}
.custom1 .container .chooseteam .dropdown-menu.ret-type{left:16.7%!important}
 #group_and_client{position: absolute;right: 48%;margin-top: 199px;z-index:2;}
 #filing_option1::after,#filingoption1::after{display:none;}
</style>
<script type="text/javascript">
$(document).ready(function(){$('.nonAspReports').addClass('active');});
</script>
</head>
<body class="body-cls">
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Global Reports</a></li>
						<li class="breadcrumb-item active">GST Group Wise Purchases Reports</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="group_and_client" class="group_and_client">
		<div class="form-group" style="display:inline;">
			<select id="multeselectgroup" class="multeselectgroup multiselect-ui form-control" multiple="multiple">
			</select>
		</div>
		<div class="form-group" style="display:inline;">
			<select id="multeselectclient" class="multeselectclient multiselect-ui form-control" multiple="multiple">
			</select>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/cp_ClientsReports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>
				<span class="reports-monthly">Group wise Monthly Purchase Report</span>
				<span class="reports-yearly" style="display: none;">Group wise Yearly Purchase Report</span>
				<span class="reports-custom" style="display: none;">Group wise Custom Purchase Report</span>
			</h2>
			<p>
				<span class="reports-monthly">Group wise Monthly Purchase Report gives you a summary of your monthly purchases.<span id="monthly_clients_errormsg"></span></span>
				<span class="reports-yearly" style="display: none;">Group wise Yearly Purchase Report gives you a summary of your monthly purchases.<span id="yearly_clients_errormsg"></span></span>
				<span class="reports-custom" style="display: none;">Group wise Custom Purchase Report gives you a summary of your monthly purchases.<span id="custom_clients_errormsg"></span></span>
			</p>
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filingoption" class="filingoption"	style="vertical-align: top;">Monthly</span>
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
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
						</div>
					</span> 
					<span style="display:none" class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="filingoption1"	style="margin-right: 10px; display: inline-flex;">
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
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
					</span>
					<span class="datetimetxt custom-sp" style="display:none" id="custom-sp">
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
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
						<div class="noramltable-row">
							<div class="noramltable-row-hdr">Filter</div>
							<div class="noramltable-row-desc">
								<div class="sfilter">
									<span id="divFilters${varRetTypeCode}"></span><span class="btn-remove-tag" onClick="clearFilters('GSTR2','')">Clear All<span data-role="remove"></span></span>
								</div>
							</div>
						</div>
					</div>
						<div class="noramltable-row">
							<div class="noramltable-row-hdr">Search Filter</div>
							<div class="noramltable-row-desc">
								<select id="multiselect${varRetTypeCode}1" class="multiselect-ui form-control" multiple="multiple">
									<option value="<%=MasterGSTConstants.ADVANCES%>">Advance Tax</option>
									<option value="<%=MasterGSTConstants.ATPAID%>">Advance Adjusted</option>
									<option value="B2B">B2B Invoices</option>
									<option value="<%=MasterGSTConstants.B2BUR%>"><%=MasterGSTConstants.B2BUR%></option>
									<option value="Debit Note">Debit Note</option>
									<option value="Credit Note">Credit Note</option>
									<option value="Debit Note(UR)">Debit Note(UR)</option>
									<option value="Credit Note(UR)">Credit Note(UR)</option>
									<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
									<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
									<option value="<%=MasterGSTConstants.ITC_REVERSAL%>"><%=MasterGSTConstants.ITC_REVERSAL%></option>
									<option value="<%=MasterGSTConstants.NIL%>">Nil Rated / Exempted / Non-GST</option>
									<option value="<%=MasterGSTConstants.ISD%>"><%=MasterGSTConstants.ISD%></option>
								</select>
								<!-- <span class="multiselectuserlist"></span> -->
									<select id="multiselect${varRetTypeCode}2" class="multiselect-ui form-control" multiple="multiple"></select>
								
								<select id="multiselect${varRetTypeCode}3" class="multiselect-ui form-control" multiple="multiple"></select>
								<select id="multiselect${varRetTypeCode}4" class="multiselect-ui form-control" multiple="multiple"></select>
								<select id="multiselect${varRetTypeCode}5" class="multiselect-ui form-control" multiple="multiple"></select>
								<!-- <select id="multiselect${varRetTypeCode}7" class="multiselect-ui form-control" multiple="multiple"></select> -->
							</div>
						</div>
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter Summary</div>
				<div class="noramltable-row-desc">
					<div class="normaltable-col hdr">Total Invoices<div class="normaltable-col-txt" id="idCountGSTR2"></div></div>
					<div class="normaltable-col hdr">Total Taxable Value<div class="normaltable-col-txt" id="idTaxableValGSTR2"></div></div>
					<!-- <div class="normaltable-col hdr">Total Exempted<div class="normaltable-col-txt" id="idExemptedValGSTR2"></div></div> -->
					<div class="normaltable-col hdr">Total Tax Value<div class="normaltable-col-txt" id="idTaxValGSTR2"></div></div>
					<div class="normaltable-col hdr">Total Amount <div class="normaltable-col-txt" id="idTotAmtValGSTR2"></div></div>
					<div class="normaltable-col hdr filsummary">Total IGST<div class="normaltable-col-txt" id="idIGSTGSTR2"></div></div>
					<div class="normaltable-col hdr filsummary">Total CGST<div class="normaltable-col-txt" id="idCGSTGSTR2"></div></div>
					<div class="normaltable-col hdr filsummary">Total SGST<div class="normaltable-col-txt" id="idSGSTGSTR2"></div></div>
					<div class="normaltable-col hdr filsummary">Total CESS<div class="normaltable-col-txt" id="idCESSGSTR2"></div></div>
					<div class="normaltable-col hdr filsummary">Total ITC<div class="normaltable-col-txt" id="idITCValGSTR2"></div></div>
					<div class="normaltable-col hdr filsummary">Total TDS<div class="normaltable-col-txt" id="idTCSTDSGSTR2"></div></div>
				</div>
		</div>
	</div>
	<div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="${varRetTypeCode}SummaryTable" style="display:none">
				<div class ="row">
				    <div class="col-sm-9 pr-0"></div>
				    <div class="col-sm-3"><a href="#" id="groupwise_dwnldxls" class="btn btn-blue mb-3 pull-right excel_btn" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;" data-toggle="tooltip" data-placement="top" title="Download Finacial Summary To Excel">Download To Excel<i class="fa fa-download ml-1" aria-hidden="true"></i></a></div>
				    </div>
				    <table id="reportTable4" class="display row-border dataTable fixed-col meterialform" cellspacing="0" width="100%">
					<thead><tr><th class="text-center">Tax Details</th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th> <th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th><th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th><th class="text-center">Total</th></tr></thead>
					<tbody id="yeartotoalreport">
			  <tr><td align="center"><h6>Transactions</h6> </td><td class="text-right" id="totalinvoices4">0</td><td class="text-right" id="totalinvoices5">0</td><td class="text-right" id="totalinvoices6">0</td><td class="text-right" id="totalinvoices7">0</td><td class="text-right" id="totalinvoices8">0</td><td class="text-right" id="totalinvoices9">0</td><td class="text-right" id="totalinvoices10">0</td><td class="text-right" id="totalinvoices11">0</td><td class="text-right" id="totalinvoices12">0</td><td class="text-right" id="totalinvoices1">0</td><td class="text-right" id="totalinvoices2">0</td><td class="text-right" id="totalinvoices3">0</td><td class="text-right ind_formatss" id="ytotal_Transactions">0</td></tr><tr><td align="center"><h6>Taxable Value</h6> </td><td class="text-right ind_formatss" id="totalTaxableValue4">0</td><td class="text-right ind_formatss" id="totalTaxableValue5">0</td><td class="text-right ind_formatss" id="totalTaxableValue6">0</td><td class="text-right ind_formatss" id="totalTaxableValue7">0</td><td class="text-right ind_formatss" id="totalTaxableValue8">0</td><td class="text-right ind_formatss" id="totalTaxableValue9">0</td><td class="text-right ind_formatss" id="totalTaxableValue10">0</td><td class="text-right ind_formatss" id="totalTaxableValue11">0</td><td class="text-right ind_formatss" id="totalTaxableValue12">0</td><td class="text-right ind_formatss" id="totalTaxableValue1">0</td><td class="text-right ind_formatss" id="totalTaxableValue2">0</td><td class="text-right ind_formatss" id="totalTaxableValue3">0</td><td class="text-right ind_formatss" id="ytotal_Taxablevalue">0.0</td> </tr>
			  <!-- <tr><td align="center"><h6>Exempted Value</h6> </td><td class="text-right ind_formatss" id="exemAmt4">0</td><td class="text-right ind_formatss" id="exemAmt5">0</td><td class="text-right ind_formatss" id="exemAmt6">0</td><td class="text-right ind_formatss" id="exemAmt7">0</td><td class="text-right ind_formatss" id="exemAmt8">0</td><td class="text-right ind_formatss" id="exemAmt9">0</td><td class="text-right ind_formatss" id="exemAmt10">0</td><td class="text-right ind_formatss" id="exemAmt11">0</td><td class="text-right ind_formatss" id="exemAmt12">0</td><td class="text-right ind_formatss" id="exemAmt1">0</td><td class="text-right ind_formatss" id="exemAmt2">0</td><td class="text-right ind_formatss" id="exemAmt3">0</td><td class="text-right ind_formatss" id="ytotal_Exemvalue">0.0</td></tr> -->
			  <!-- <tr><td align="center"><h6>TCS Value</h6> </td><td class="text-right ind_formatss" id="tcsAmt4">0</td><td class="text-right ind_formatss" id="tcsAmt5">0</td><td class="text-right ind_formatss" id="tcsAmt6">0</td><td class="text-right ind_formatss" id="tcsAmt7">0</td><td class="text-right ind_formatss" id="tcsAmt8">0</td><td class="text-right ind_formatss" id="tcsAmt9">0</td><td class="text-right ind_formatss" id="tcsAmt10">0</td><td class="text-right ind_formatss" id="tcsAmt11">0</td><td class="text-right ind_formatss" id="tcsAmt12">0</td><td class="text-right ind_formatss" id="tcsAmt1">0</td><td class="text-right ind_formatss" id="tcsAmt2">0</td><td class="text-right ind_formatss" id="tcsAmt3">0</td><td class="text-right ind_formatss" id="ytotal_Tcsvalue">0.0</td></tr> -->
			  <tr><td align="center"><h6>Tax Value</h6> </td><td class="text-right ind_formatss" id="taxAmt4">0</td><td class="text-right ind_formatss" id="taxAmt5">0</td><td class="text-right ind_formatss" id="taxAmt6">0</td><td class="text-right ind_formatss" id="taxAmt7">0</td><td class="text-right ind_formatss" id="taxAmt8">0</td><td class="text-right ind_formatss" id="taxAmt9">0</td><td class="text-right ind_formatss" id="taxAmt10">0</td><td class="text-right ind_formatss" id="taxAmt11">0</td><td class="text-right ind_formatss" id="taxAmt12">0</td><td class="text-right ind_formatss" id="taxAmt1">0</td><td class="text-right ind_formatss" id="taxAmt2">0</td><td class="text-right ind_formatss" id="taxAmt3">0</td> <td class="text-right ind_formatss" id="ytotal_Taxvalue">0.0</td></tr><tr><td align="center"><h6>Total Amount</h6> </td><td class="text-right ind_formatss" id="sales4">0</td><td class="text-right ind_formatss" id="sales5">0</td><td class="text-right ind_formatss" id="sales6">0</td><td class="text-right ind_formatss" id="sales7">0</td><td class="text-right ind_formatss" id="sales8">0</td><td class="text-right ind_formatss" id="sales9">0</td><td class="text-right ind_formatss" id="sales10">0</td><td class="text-right ind_formatss" id="sales11">0</td><td class="text-right ind_formatss" id="sales12">0</td><td class="text-right ind_formatss" id="sales1">0</td><td class="text-right ind_formatss" id="sales2">0</td><td class="text-right ind_formatss" id="sales3">0</td><td class="text-right ind_formatss" id="ytotal_TotalAmount">0.0</td></tr>
			  <tr><td align="center"><h6>IGST Amount</h6> </td><td class="text-right ind_formatss" id="salesigstAmount4">0</td><td class="text-right ind_formatss" id="salesigstAmount5">0</td><td class="text-right ind_formatss" id="salesigstAmount6">0</td><td class="text-right ind_formatss" id="salesigstAmount7">0</td><td class="text-right ind_formatss" id="salesigstAmount8">0</td><td class="text-right ind_formatss" id="salesigstAmount9">0</td><td class="text-right ind_formatss" id="salesigstAmount10">0</td><td class="text-right ind_formatss" id="salesigstAmount11">0</td><td class="text-right ind_formatss" id="salesigstAmount12">0</td><td class="text-right ind_formatss" id="salesigstAmount1">0</td><td class="text-right ind_formatss" id="salesigstAmount2">0</td><td class="text-right ind_formatss" id="salesigstAmount3">0</td><td class="text-right ind_formatss" id="ytotal_IGSTAmount">0.0</td></tr><tr><td align="center"><h6>CGST Amount</h6> </td><td class="text-right ind_formatss" id="salescgstAmount4">0</td><td class="text-right ind_formatss" id="salescgstAmount5">0</td><td class="text-right ind_formatss" id="salescgstAmount6">0</td><td class="text-right ind_formatss" id="salescgstAmount7">0</td><td class="text-right ind_formatss" id="salescgstAmount8">0</td><td class="text-right ind_formatss" id="salescgstAmount9">0</td><td class="text-right ind_formatss" id="salescgstAmount10">0</td><td class="text-right ind_formatss" id="salescgstAmount11">0</td><td class="text-right ind_formatss" id="salescgstAmount12">0</td><td class="text-right ind_formatss" id="salescgstAmount1">0</td><td class="text-right ind_formatss" id="salescgstAmount2">0</td><td class="text-right ind_formatss" id="salescgstAmount3">0</td><td class="text-right ind_formatss" id="ytotal_CGSTAmount">0.0</td></tr>
			  <tr><td align="center"><h6>SGST Amount</h6> </td><td class="text-right ind_formatss" id="salessgstAmount4">0</td><td class="text-right ind_formatss" id="salessgstAmount5">0</td><td class="text-right ind_formatss" id="salessgstAmount6">0</td><td class="text-right ind_formatss" id="salessgstAmount7">0</td><td class="text-right ind_formatss" id="salessgstAmount8">0</td><td class="text-right ind_formatss" id="salessgstAmount9">0</td><td class="text-right ind_formatss" id="salessgstAmount10">0</td><td class="text-right ind_formatss" id="salessgstAmount11">0</td><td class="text-right ind_formatss" id="salessgstAmount12">0</td><td class="text-right ind_formatss" id="salessgstAmount1">0</td><td class="text-right ind_formatss" id="salessgstAmount2">0</td><td class="text-right ind_formatss" id="salessgstAmount3">0</td><td class="text-right ind_formatss" id="ytotal_SGSTAmount">0.0</td></tr><tr><td align="center"><h6>CESS Amount</h6> </td><td class="text-right ind_formatss" id="salescessAmount4">0</td><td class="text-right ind_formatss" id="salescessAmount5">0</td><td class="text-right ind_formatss" id="salescessAmount6">0</td><td class="text-right ind_formatss" id="salescessAmount7">0</td><td class="text-right ind_formatss" id="salescessAmount8">0</td><td class="text-right ind_formatss" id="salescessAmount9">0</td><td class="text-right ind_formatss" id="salescessAmount10">0</td><td class="text-right ind_formatss" id="salescessAmount11">0</td><td class="text-right ind_formatss" id="salescessAmount12">0</td><td class="text-right ind_formatss" id="salescessAmount1">0</td><td class="text-right ind_formatss" id="salescessAmount2">0</td><td class="text-right ind_formatss" id="salescessAmount3">0</td><td class="text-right ind_formatss" id="ytotal_CessAmount">0.0</td></tr>
			<tr><td align="center"><h6>Itc Amount</h6> </td><td class="text-right ind_formatss" id="salesItcAmount4">0</td><td class="text-right ind_formatss" id="salesItcAmount5">0</td><td class="text-right ind_formatss" id="salesItcAmount6">0</td><td class="text-right ind_formatss" id="salesItcAmount7">0</td><td class="text-right ind_formatss" id="salesItcAmount8">0</td><td class="text-right ind_formatss" id="salesItcAmount9">0</td><td class="text-right ind_formatss" id="salesItcAmount10">0</td><td class="text-right ind_formatss" id="salesItcAmount11">0</td><td class="text-right ind_formatss" id="salesItcAmount12">0</td><td class="text-right ind_formatss" id="salesItcAmount1">0</td><td class="text-right ind_formatss" id="salesItcAmount2">0</td><td class="text-right ind_formatss" id="salesItcAmount3">0</td><td class="text-right ind_formatss" id="ytotal_ITCvalue">0.0</td></tr>
			<tr><td align="center"><h6>TCS Receivable</h6> </td><td class="text-right ind_formatss" id="tcsAmt4">0</td><td class="text-right ind_formatss" id="tcsAmt5">0</td><td class="text-right ind_formatss" id="tcsAmt6">0</td><td class="text-right ind_formatss" id="tcsAmt7">0</td><td class="text-right ind_formatss" id="tcsAmt8">0</td><td class="text-right ind_formatss" id="tcsAmt9">0</td><td class="text-right ind_formatss" id="tcsAmt10">0</td><td class="text-right ind_formatss" id="tcsAmt11">0</td><td class="text-right ind_formatss" id="tcsAmt12">0</td><td class="text-right ind_formatss" id="tcsAmt1">0</td><td class="text-right ind_formatss" id="tcsAmt2">0</td><td class="text-right ind_formatss" id="tcsAmt3">0</td><td class="text-right ind_formatss" id="ytotal_Tcsvalue">0.0</td></tr>
			   <tr><td align="center"><h6>TDS Payable</h6> </td><td class="text-right ind_formatss" id="tdsAmt4">0</td><td class="text-right ind_formatss" id="tdsAmt5">0</td><td class="text-right ind_formatss" id="tdsAmt6">0</td><td class="text-right ind_formatss" id="tdsAmt7">0</td><td class="text-right ind_formatss" id="tdsAmt8">0</td><td class="text-right ind_formatss" id="tdsAmt9">0</td><td class="text-right ind_formatss" id="tdsAmt10">0</td><td class="text-right ind_formatss" id="tdsAmt11">0</td><td class="text-right ind_formatss" id="tdsAmt12">0</td><td class="text-right ind_formatss" id="tdsAmt1">0</td><td class="text-right ind_formatss" id="tdsAmt2">0</td><td class="text-right ind_formatss" id="tdsAmt3">0</td><td class="text-right ind_formatss" id="ytotal_Tdsvalue">0.0</td></tr>
			</tbody>
				</table>
				</div>
	<div class="customtable db-ca-view salestable reportsdbTableGSTR2">
					<table id='gloablReports_dataTableGSTR2' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th>Type</th><th class="text-center">Invoice No</th><th class="text-center">Suppliers</th><th class="text-center">GSTIN</th><th class="text-center">Date</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th><th class="text-center">Total ITC</th>
							</tr>
						</thead>
						<tbody id='invBodysalestable1'></tbody>
					</table>
				</div>
	
</div>
		</div>
	</div>

	
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->
</body>
<script type="text/javascript">
var clientidsArray=new Array();
var groupArr =new Array();
var selectGroupArrayClientids= new Array();
var salesFileName;
var booksOrReturns = "books";
var gstnnumber='<c:out value="${client.gstnnumber}"/>';
var rtype = '${varRetTypeCode}';
	$(function() {
		var clientname = '<c:out value="${client.businessname}"/>';
		var date = new Date();
		month = '<c:out value="${month}"/>';
		year = '<c:out value="${year}"/>';
	
	
	$('#divFiltersyearlyGSTR1').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		$('#multiselectyearGSTR12').multiselect('deselect', [val]);
		$('#multiselectyearGSTR13').multiselect('deselect', [val]);
		$('#multiselectyearGSTR14').multiselect('deselect', [val]);
		$('#multiselectyearGSTR15').multiselect('deselect', [val]);
		$('#multiselectyearGSTR16').multiselect('deselect', [val]);
		applyFilters('GSTR1','year');
	});
	$('#divFilterscustomGSTR1').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		$('#multiselectcustomGSTR12').multiselect('deselect', [val]);
		$('#multiselectcustomGSTR13').multiselect('deselect', [val]);
		$('#multiselectcustomGSTR14').multiselect('deselect', [val]);
		$('#multiselectcustomGSTR15').multiselect('deselect', [val]);
		$('#multiselectcustomGSTR16').multiselect('deselect', [val]);
		applyFilters('GSTR1','custom');
	});
	<c:forEach items="${branches}" var="branch">
		$("#multiselect"+rtype+"4").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
	</c:forEach>
	multiselrefresh_reports('#multiselect'+rtype+'4', '- Branches -' , '${varRetType}');
	<c:forEach items="${verticals}" var="vertical">
		$("#multiselect"+rtype+"5").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
	</c:forEach>
	multiselrefresh_reports('#multiselect'+rtype+'5', '- Verticals -' , '${varRetType}');
	<c:forEach items="${listOfClients}" var="clients">
		$(".multeselectclient").append($("<option></option>").attr("value","${clients.id}").text("${clients.businessname}-${clients.gstnnumber}"));
	</c:forEach>
	var counts=0;
	var groupArray=[], selectGroupArray=[];
	<c:forEach items="${listOfClients}" var="group">
		<c:if test="${not empty group.groupName}">
			if(counts == 0){
					groupArray.push('<c:out value="${group.groupName}"/>');
					$(".multeselectgroup").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));
			}
			if(jQuery.inArray('<c:out value="${group.groupName}"/>', groupArray ) == -1){
				groupArray.push('<c:out value="${group.groupName}"/>');
				$(".multeselectgroup").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));
			}
			counts++;
		</c:if>
	</c:forEach>
	
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
    				$('.multeselectclient').empty().multiselect('rebuild');
	    			 for(var i =0;i <=data.length-1;i++){
	    			  $(".multeselectclient").append($("<option></option>").attr("value",data[i].firstname).text(data[i].businessname+"-"+data[i].gstnnumber));
	    			} 
	    			$('.multeselectclient').multiselect('rebuild');
	    		},error : function(data) {
    			}
    		});
    	}else{
    		$.ajax({
    			url: '${contextPath}/getclientdata/'+groupsnamelist+'/<c:out value="${id}"/>',
    			type:'GET',
    			contentType: 'application/json',
    			success : function(data) {
    				selectGroupArrayClientids= new Array();
    				$('.multeselectclient').empty().multiselect('rebuild');
	    			for(var i =0;i <=data.length-1;i++){
	    				selectGroupArrayClientids.push(data[i].firstname);
	    			  	$(".multeselectclient").append($("<option></option>").attr("value",data[i].firstname).text(data[i].businessname+"-"+data[i].gstnnumber));
	    			} 
	    			$('.multeselectclient').multiselect('rebuild');
    			},error : function(data) {}
    		});
    	}
     }
    function applyGroup() {
		var groupArr =new Array();
	    var groupOptions = $('.multeselectgroup option:selected');
    	
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
    $('.multeselectgroup').multiselect({
		nonSelectedText: '- Group Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGroup();
		},
		onSelectAll: function() {
			applyGroup();
		},
		onDeselectAll: function() {
			applyGroup();
		}
	});
	function applyClient() {
		var clientArr =new Array();
	    var clientOptions = $('.multeselectclient option:selected');
    	
	    if(clientOptions.length > 0) {
	    	for(var i=0;i<clientOptions.length;i++) {
	    		clientArr.push(clientOptions[i].value);
	    		getlistOfClientids(clientArr);
			}
		}else{
			clientArr.push('CLIENTS_NOTFOUND');
			getlistOfClientids(clientArr);
		}
    }
	function getlistOfClientids(listofclients){
		clientidsArray=new Array();
		clientidsArray.push(listofclients);
	}
	$('.multeselectclient').multiselect({
		nonSelectedText: '- client/business Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyClient();
		},
		onSelectAll: function() {
			applyClient();
		},
		onDeselectAll: function() {
			applyClient();
		}
	});
	
});

$( document ).ready(function() {
	$('#multeselectclient').parent().css('width','172.81px');
});
var invoiceArray=new Object();
function getdiv() {
	var clientname = '<c:out value="${client.businessname}"/>';var abc = $('#fillingoption span').html();
	$('#multiselectGSTR14,#multiselectcustomGSTR14,#multiselectyearGSTR14').empty();
	$('#divFiltersGSTR1').html('');
if(abc == 'Monthly'){
	$('#GSTR1SummaryTable').css("display", "none");
	$('span#filingoption').css("vertical-align", "middle");
	var fp = $('#monthly').val();
	var fpsplit = fp.split('-');
	var mn = parseInt(fpsplit[0]);	
	var yr = parseInt(fpsplit[1]);
	salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+mn+yr;
	if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
		if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
			$('#monthly_clients_errormsg').html('Please select at least one client/business name').css('color','red');
		}else{
			loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
			loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',mn, yr,abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
			loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		}
	}else{
		if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
			$('#monthly_clients_errormsg').html('');
			loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
			loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',mn, yr, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
			loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		}else{
			$('#monthly_clients_errormsg').html('');
			loadGlobalReportsUsersByClient('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
			loadGlobalReportsInvoiceSupport(selectGroupArrayClientids, '${varRetType}', '${varRetTypeCode}',mn, yr, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
			loadGlobalReportsInvTable('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', selectGroupArrayClientids, 'Purchase Register','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		}
	}
}else if (abc == 'Yearly') {
	$('#GSTR2SummaryTable').css("display", "block");$('span#filingoption').css("vertical-align", "bottom");		
		var fp = $('.yearlyoption').text();
		var fpsplit = fp.split(' - ');
		var yrs = parseInt(fpsplit[0]);
		var yrs1 = parseInt(fpsplit[0])+1;
		var year=$('#yearlyoption').html().split("-");
		salesFileName = 'MGST_Sales_Yearly_'+gstnnumber+'_'+yrs+'-'+yrs1;
		$(".reportTable4  div.toolbar").html('<h4>Financial Summary</h4>');
		if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
			if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
				$('#yearly_clients_errormsg').html('Please select at least one client/business name').css('color','red');
			}else{
				$('#yearly_clients_errormsg').html('');
				loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
				loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',0, year[1], abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
				loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, abc);
				loadGlobalReportsSummary('${id}','${varRetTypeCode}',clientidsArray, 0, year[1],booksOrReturns,abc);
				initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				//$('#groupwise_dwnldxls').attr("href","${contextPath}/dwnldGlobalReportsFinancialSummaryxls/${id}/${varRetTypeCode}?clientids="+clientidsArray+"&year="+year[0].trim()+"&fromdate=null&todate=null&booksorReturns="+booksOrReturns);
			}
		}else{
			if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
				$('#yearly_clients_errormsg').html('');
				loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
				loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',0, year[1], abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
				loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, abc);
				loadGlobalReportsSummary('${id}','${varRetTypeCode}',clientidsArray, 0, year[1],booksOrReturns, abc);
				initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				//$('#groupwise_dwnldxls').attr("href","${contextPath}/dwnldGlobalReportsFinancialSummaryxls/${id}/${varRetTypeCode}?clientids="+clientidsArray+"&year="+year[0].trim()+"&fromdate=null&todate=null&booksorReturns="+booksOrReturns);
			}else{
				$('#yearly_clients_errormsg').html('');
				loadGlobalReportsUsersByClient('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
				loadGlobalReportsInvoiceSupport(selectGroupArrayClientids, '${varRetType}', '${varRetTypeCode}',0, year[1], abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
				loadGlobalReportsInvTable('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, abc);
				loadGlobalReportsSummary('${id}','${varRetTypeCode}',selectGroupArrayClientids, 0, year[1], booksOrReturns,abc);
				initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', selectGroupArrayClientids, 'Purchase Register','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				//$('#groupwise_dwnldxls').attr("href","${contextPath}/dwnldGlobalReportsFinancialSummaryxls/${id}/${varRetTypeCode}?clientids="+selectGroupArrayClientids+"&year="+year[0].trim()+"&fromdate=null&todate=null&booksorReturns="+booksOrReturns);
			}		
		}
	}else{
		$('#GSTR2SummaryTable').css("display", "block");
		$('span#filingoption').css("vertical-align","bottom");
		var fromtime = $('.fromtime').val();
		var totime = $('.totime').val();
		$('.fromtime').val(fromtime);
		$('.totime').val(totime);
		salesFileName = 'MGST_Sales_Custom_'+gstnnumber+'_'+fromtime+'_'+totime;
		$(".reportTable5  div.toolbar").html('<h4>Custom Summary</h4>');
	if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
	if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
		$('#custom_clients_errormsg').html('Please select at least one client/business name').css('color','red');
	}else{
		$('#custom_clients_errormsg').html('');
		loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
		loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',fromtime, totime, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
		loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, abc);
		loadGlobalReportsSummary('${id}','${varRetTypeCode}',clientidsArray, fromtime, totime, booksOrReturns,abc);
		initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
		initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
		invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, abc);
		//$('#groupwise_dwnldxls').attr("href","${contextPath}/dwnldGlobalReportsFinancialSummaryxls/${id}/${varRetTypeCode}?clientids="+clientidsArray+"&year=0&fromdate="+fromtime+"&todate="+totime+"&booksorReturns="+booksOrReturns);
	}
}else{
	if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
		$('#custom_clients_errormsg').html('');
		loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
		loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',fromtime, totime, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
		loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, abc);
		loadGlobalReportsSummary('${id}','${varRetTypeCode}',clientidsArray, fromtime, totime,booksOrReturns, abc);
		initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
		initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
		invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, abc);
		//$('#groupwise_dwnldxls').attr("href","${contextPath}/dwnldGlobalReportsFinancialSummaryxls/${id}/${varRetTypeCode}?clientids="+clientidsArray+"&year=0&fromdate="+fromtime+"&todate="+totime+"&booksorReturns="+booksOrReturns);
	}else{
		$('#custom_clients_errormsg').html('');
		loadGlobalReportsUsersByClient('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
		loadGlobalReportsInvoiceSupport(selectGroupArrayClientids, '${varRetType}', '${varRetTypeCode}',fromtime, totime, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
		loadGlobalReportsInvTable('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, abc);
		loadGlobalReportsSummary('${id}','${varRetTypeCode}',selectGroupArrayClientids, fromtime, totime, booksOrReturns,abc);
		initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
		initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
		invsDownloads('${id}', selectGroupArrayClientids, 'Purchase Register','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, abc);
		//$('#groupwise_dwnldxls').attr("href","${contextPath}/dwnldGlobalReportsFinancialSummaryxls/${id}/${varRetTypeCode}?clientids="+selectGroupArrayClientids+"&year=0&fromdate="+fromtime+"&todate="+totime+"&booksorReturns="+booksOrReturns);
	}		
}
	} 
}
</script>
<script type="text/javascript">
$(function(){
	$('#${varRetTypeCode}SummaryTable').css("display", "none");
	loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
	loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}','${month}', '${year}', 'Monthly', 'books', loadGlobalReportsCustomersInDropdown);
	//loadGlobalReportsInvTable('${id}',clientidsArray, '${varRetType}','${varRetTypeCode}','Monthly',url,url_itemwise,url_invoicewise,url_fulldetails,booksOrReturns);
	loadDefaultDataTable('${varRetType}','${varRetTypeCode}');
	initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
	initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}'); 
	invsDownloads('${id}', clientidsArray, 'Purchase Register','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}',  'books','Monthly');
});

</script>
<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp" />
</body>
</html>