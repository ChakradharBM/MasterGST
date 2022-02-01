<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value="<%=MasterGSTConstants.GSTR1%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | Sales Report</title>
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
    <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
    <%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
</head>
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
						<li class="breadcrumb-item active">Sales Report</li>
	                </ol>
	                <div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>
				<span class="reports-monthly">Monthly Sales Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
				<span class="reports-yearly" style="display: none;">Yearly Sales Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
				<span class="reports-custom" style="display: none;">Custom Sales Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
			</h2>
			<p>
				<span class="reports-monthly">Monthly Sales Report gives you a summary of your monthly sales.</span>
				<span class="reports-yearly" style="display: none;">Monthly Sales Report gives you a summary of your monthly sales.</span>
				<span class="reports-custom" style="display: none;">Monthly Sales Report gives you a summary of your monthly sales.</span>
			</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;">
				Help To Read This Report<div class="dropdown-content reportSales"> <span class="arrow-up"></span><span class="pl-2"> All the Sales Invoices from your <c:if test="${varRetType eq 'GSTR1'}">Sales Register</c:if>/Books Monthly, Yearly and Custom Wise</span></div>
			</div>
			<span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class=""></div>
			<div class="">
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
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
			    <div class="normaltable meterialform" id="updatefilter_summary_reports" style="display:none;">
			        <div class="filter">
			            <div class="noramltable-row">
			                <div class="noramltable-row-hdr">Filter</div>
			                <div class="noramltable-row-desc">
			                    <div class="sfilter"><span id="divFiltersGSTR1"></span>
			                        <span class="btn-remove-tag" onClick="clearInvFiltersReports('GSTR1','')">Clear All<span data-role="remove"></span></span>
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
			                    <option value="B2C">B2CS (Small) Invoices</option>
			                    <option value="B2CL">B2CL (Large)</option>
			                    <option value="<%=MasterGSTConstants.B2BA%>">B2BA Invoices</option>
			                    <option value="<%=MasterGSTConstants.B2CSA%>">B2CSA (Small)</option>
			                    <option value="<%=MasterGSTConstants.B2CLA%>">B2CLA (Large)</option>
				                <option value="Debit Note">Debit Note</option>
								<option value="Credit Note">Credit Note</option>
								<option value="Debit Note(UR)">Debit Note(UR)</option>
								<option value="Credit Note(UR)">Credit Note(UR)</option>
			                    <!-- <option value="Credit/Debit Notes">Credit/Debit Notes</option>
			                    <option value="Credit/Debit Note for Unregistered Taxpayers">Credit/Debit Note for Unregistered Taxpayers</option> -->
			                    <option value="<%=MasterGSTConstants.EXPORTS%>"><%=MasterGSTConstants.EXPORTS%></option>
			                    <option value="<%=MasterGSTConstants.NIL%>">Nil Rated / Exempted / Non-GST</option>
			                </select>
			               	<select id="multiselect${varRetTypeCode}2" class="multiselect-ui form-control" multiple="multiple"></select>
			                <select id="multiselect${varRetTypeCode}3" class="multiselect-ui form-control" multiple="multiple"></select>
			                <select id="multiselect${varRetTypeCode}4" class="multiselect-ui form-control" multiple="multiple">
								<c:forEach items="${client.branches}" var="branch">
									<option value="${branch.name}">${branch.name}</option>
								</c:forEach>
							</select>
							<select id="multiselect${varRetTypeCode}5" class="multiselect-ui form-control" multiple="multiple">
								<c:forEach items="${client.verticals}" var="vertical">
									<option value="${vertical.name}">${vertical.name}</option>
								</c:forEach>
							</select>
							<select id="multiselect${varRetTypeCode}6" class="multiselect-ui form-control" multiple="multiple"></select>
							<select id="multiselect${varRetTypeCode}7" class="multiselect-ui form-control" multiple="multiple"></select>
							<select id="multiselect${varRetTypeCode}8" class="multiselect-ui form-control" multiple="multiple"></select>
							<select id="multiselect${varRetTypeCode}9" class="multiselect-ui form-control" multiple="multiple"></select>
			            </div>
			        </div>
			        <div class="noramltable-row">
			            <div class="noramltable-row-hdr">Filter Summary</div>
			            <div class="noramltable-row-desc">
			                <div class="normaltable-col hdr" style="width:8%">Total Invoices<div class="normaltable-col-txt" id="idCountGSTR1"></div></div>
			                <div class="normaltable-col hdr">Total Taxable Value<div class="normaltable-col-txt" id="idTaxableValGSTR1"></div></div>
			                <div class="normaltable-col hdr">Total Exempted<div class="normaltable-col-txt" id="idExemptedValGSTR1"></div></div>
			                <div class="normaltable-col hdr">Total Tax Value<div class="normaltable-col-txt" id="idTaxValGSTR1"></div></div>
			                <div class="normaltable-col hdr">Total Amount<div class="normaltable-col-txt" id="idTotAmtValGSTR1"></div></div>
			                <div class="normaltable-col hdr filsummary">Total IGST<div class="normaltable-col-txt" id="idIGSTGSTR1"></div></div>
			                <div class="normaltable-col hdr filsummary">Total CGST<div class="normaltable-col-txt" id="idCGSTGSTR1"></div></div>
			                <div class="normaltable-col hdr filsummary">Total SGST<div class="normaltable-col-txt" id="idSGSTGSTR1"></div></div>
			                <div class="normaltable-col hdr filsummary">Total CESS<div class="normaltable-col-txt" id="idCESSGSTR1"></div></div>
			                <div class="normaltable-col hdr filsummary">Total TCS<div class="normaltable-col-txt" id="idTCSTDSGSTR1"></div></div>
			            </div>
			        </div>
			    </div>
				<div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="${varRetTypeCode}SummaryTable" style="display:none;">
				    <div class ="row">
					    <div class="col-sm-9 pr-0"><h4><span class="reports-yearly" style="display: none;">Yearly Summary of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span><span class="reports-custom" style="display: none;">Custom Summary of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span></h4></div>
					    <div class="col-sm-3">
					    	<a href="${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetTypeCode}?reporttype=invoice_report&year=${year}&fromdate=null&todate=null" id="dwnldxls" class="btn btn-blue mb-3 pull-right excel_btn" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;" data-toggle="tooltip" data-placement="top" title="Download Financial Summary To Excel">Download To Excel<i class="fa fa-download ml-1" aria-hidden="true"></i></a>				    	
					    </div>
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
						  	<tr><td align="center"><h6>Taxable Value</h6> </td><td class="text-right ind_formatss" id="totalTaxableValue4">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue5">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue6">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue7">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue8">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue9">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue10">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue11">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue12">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue1">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue2">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue3">0.0</td><td class="text-right ind_formatss" id="ytotal_Taxablevalue">0.0</td></tr>
						  	<tr><td align="center"><h6>Tax Value</h6> </td><td class="text-right ind_formatss" id="taxAmt4">0.0</td><td class="text-right ind_formatss" id="taxAmt5">0.0</td><td class="text-right ind_formatss" id="taxAmt6">0.0</td><td class="text-right ind_formatss" id="taxAmt7">0.0</td><td class="text-right ind_formatss" id="taxAmt8">0.0</td><td class="text-right ind_formatss" id="taxAmt9">0.0</td><td class="text-right ind_formatss" id="taxAmt10">0.0</td><td class="text-right ind_formatss" id="taxAmt11">0.0</td><td class="text-right ind_formatss" id="taxAmt12">0.0</td><td class="text-right ind_formatss" id="taxAmt1">0.0</td><td class="text-right ind_formatss" id="taxAmt2">0.0</td><td class="text-right ind_formatss" id="taxAmt3">0.0</td><td class="text-right ind_formatss" id="ytotal_Taxvalue">0.0</td></tr>
						  	<tr><td align="center"><h6>Exempted Value</h6> </td><td class="text-right ind_formatss" id="exemAmt4">0.0</td><td class="text-right ind_formatss" id="exemAmt5">0.0</td><td class="text-right ind_formatss" id="exemAmt6">0.0</td><td class="text-right ind_formatss" id="exemAmt7">0.0</td><td class="text-right ind_formatss" id="exemAmt8">0.0</td><td class="text-right ind_formatss" id="exemAmt9">0.0</td><td class="text-right ind_formatss" id="exemAmt10">0.0</td><td class="text-right ind_formatss" id="exemAmt11">0.0</td><td class="text-right ind_formatss" id="exemAmt12">0.0</td><td class="text-right ind_formatss" id="exemAmt1">0.0</td><td class="text-right ind_formatss" id="exemAmt2">0.0</td><td class="text-right ind_formatss" id="exemAmt3">0.0</td><td class="text-right ind_formatss" id="ytotal_Exemvalue">0.0</td></tr>
						  	<tr><td align="center"><h6>Total Amount</h6> </td><td class="text-right ind_formatss" id="sales4">0.0</td><td class="text-right ind_formatss" id="sales5">0.0</td><td class="text-right ind_formatss" id="sales6">0.0</td><td class="text-right ind_formatss" id="sales7">0.0</td><td class="text-right ind_formatss" id="sales8">0.0</td><td class="text-right ind_formatss" id="sales9">0.0</td><td class="text-right ind_formatss" id="sales10">0.0</td><td class="text-right ind_formatss" id="sales11">0.0</td><td class="text-right ind_formatss" id="sales12">0.0</td><td class="text-right ind_formatss" id="sales1">0.0</td><td class="text-right ind_formatss" id="sales2">0.0</td><td class="text-right ind_formatss" id="sales3">0.0</td><td class="text-right ind_formatss" id="ytotal_TotalAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>IGST Amount</h6> </td><td class="text-right ind_formatss" id="igstAmount4">0.0</td><td class="text-right ind_formatss" id="igstAmount5">0.0</td><td class="text-right ind_formatss" id="igstAmount6">0.0</td><td class="text-right ind_formatss" id="igstAmount7">0.0</td><td class="text-right ind_formatss" id="igstAmount8">0.0</td><td class="text-right ind_formatss" id="igstAmount9">0.0</td><td class="text-right ind_formatss" id="igstAmount10">0.0</td><td class="text-right ind_formatss" id="igstAmount11">0.0</td><td class="text-right ind_formatss" id="igstAmount12">0.0</td><td class="text-right ind_formatss" id="igstAmount1">0.0</td><td class="text-right ind_formatss" id="igstAmount2">0.0</td><td class="text-right ind_formatss" id="igstAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_IGSTAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>CGST Amount</h6> </td><td class="text-right ind_formatss" id="cgstAmount4">0.0</td><td class="text-right ind_formatss" id="cgstAmount5">0.0</td><td class="text-right ind_formatss" id="cgstAmount6">0.0</td><td class="text-right ind_formatss" id="cgstAmount7">0.0</td><td class="text-right ind_formatss" id="cgstAmount8">0.0</td><td class="text-right ind_formatss" id="cgstAmount9">0.0</td><td class="text-right ind_formatss" id="cgstAmount10">0.0</td><td class="text-right ind_formatss" id="cgstAmount11">0.0</td><td class="text-right ind_formatss" id="cgstAmount12">0.0</td><td class="text-right ind_formatss" id="cgstAmount1">0.0</td><td class="text-right ind_formatss" id="cgstAmount2">0.0</td><td class="text-right ind_formatss" id="cgstAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_CGSTAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>SGST Amount</h6> </td><td class="text-right ind_formatss" id="sgstAmount4">0.0</td><td class="text-right ind_formatss" id="sgstAmount5">0.0</td><td class="text-right ind_formatss" id="sgstAmount6">0.0</td><td class="text-right ind_formatss" id="sgstAmount7">0.0</td><td class="text-right ind_formatss" id="sgstAmount8">0.0</td><td class="text-right ind_formatss" id="sgstAmount9">0.0</td><td class="text-right ind_formatss" id="sgstAmount10">0.0</td><td class="text-right ind_formatss" id="sgstAmount11">0.0</td><td class="text-right ind_formatss" id="sgstAmount12">0.0</td><td class="text-right ind_formatss" id="sgstAmount1">0.0</td><td class="text-right ind_formatss" id="sgstAmount2">0.0</td><td class="text-right ind_formatss" id="sgstAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_SGSTAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>CESS Amount</h6> </td><td class="text-right ind_formatss" id="cessAmount4">0.0</td><td class="text-right ind_formatss" id="cessAmount5">0.0</td><td class="text-right ind_formatss" id="cessAmount6">0.0</td><td class="text-right ind_formatss" id="cessAmount7">0.0</td><td class="text-right ind_formatss" id="cessAmount8">0.0</td><td class="text-right ind_formatss" id="cessAmount9">0.0</td><td class="text-right ind_formatss" id="cessAmount10">0.0</td><td class="text-right ind_formatss" id="cessAmount11">0.0</td><td class="text-right ind_formatss" id="cessAmount12">0.0</td><td class="text-right ind_formatss" id="cessAmount1">0.0</td><td class="text-right ind_formatss" id="cessAmount2">0.0</td><td class="text-right ind_formatss" id="cessAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_CessAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>TCS Payable</h6> </td><td class="text-right ind_formatss" id="tcsAmt4">0.0</td><td class="text-right ind_formatss" id="tcsAmt5">0.0</td><td class="text-right ind_formatss" id="tcsAmt6">0.0</td><td class="text-right ind_formatss" id="tcsAmt7">0.0</td><td class="text-right ind_formatss" id="tcsAmt8">0.0</td><td class="text-right ind_formatss" id="tcsAmt9">0.0</td><td class="text-right ind_formatss" id="tcsAmt10">0.0</td><td class="text-right ind_formatss" id="tcsAmt11">0.0</td><td class="text-right ind_formatss" id="tcsAmt12">0.0</td><td class="text-right ind_formatss" id="tcsAmt1">0.0</td><td class="text-right ind_formatss" id="tcsAmt2">0.0</td><td class="text-right ind_formatss" id="tcsAmt3">0.0</td><td class="text-right ind_formatss" id="ytotal_Tcsvalue">0.0</td></tr>
				        </tbody>
				    </table>
				</div>
				<div class="customtable db-ca-view salestable reportsdbTableGSTR1">
					<table id='reports_dataTableGSTR1' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th>Type</th><th class="text-center">Invoice No</th><th class="text-center">Customers</th><th class="text-center">GSTIN</th><th class="text-center">Date</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th>
							</tr>
						</thead>
						<tbody id='invBodysalestable1'></tbody>
					</table>
				</div>
				<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
				    <div class="modal-dialog modal-md modal-right" role="document">
				        <div class="modal-content">
				            <div class="modal-body">
				                <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
				                <div class="invoice-hdr bluehdr">
				                    <h3>Help To Read This Report</h3></div>
				                <div class=" p-2 steptext-wrap"><span class="pl-2">All the Sale Invoices from your SaleRegister/Books Monthly, Yearly and Custom Wise</span> </div>
				            </div>
				            <div class="modal-footer">
				                <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
				            </div>
				        </div>
				    </div>
				</div>
			</div>
		</div>
	</div>
    <%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp"/>
	<script src="${contextPath}/static/mastergst/js/reports/sales_reports.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function(){
			var booksOrReturns = '';
			var type='';
			$('#${varRetTypeCode}SummaryTable').css("display", "none");
			loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
			loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}','${month}', '${year}', type, loadReportsCustomersInDropdown);
			loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}', booksOrReturns);
			initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		});
		function generateData() {
			var abc = $('#fillingoption span').html();
			var booksOrReturns = '';
			var type='';
			clearInvFiltersReportss('GSTR1');
			if(abc == 'Monthly'){
				$('#${varRetTypeCode}SummaryTable').css("display", "none");
				$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
				var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
				var type='';
				loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
				loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', mn, yr, type, loadReportsCustomersInDropdown);
				loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
				initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			}else if (abc == 'Yearly') {
				$('#${varRetTypeCode}SummaryTable').css("display", "block");
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
				var year=$('#yearlyoption').html().split("-");
				var type='';
				loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
				loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', 0, year[1], type, loadReportsCustomersInDropdown);
				loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				loadReportsSummary('${id}','${varRetTypeCode}','${client.id}', 0, year[1], type);
				initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				//$('#dwnldxls').attr('href','${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetTypeCode}/'+year[0].trim());
				//loadSummaryForExcelDwnld('${id}', '${client.id}', '${varRetType}',year[0].trim(),null,null,'Yearly');
				//$('#dwnldxls').attr('href','${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetType}?reporttype=invoice_report&year='+year[0].trim()+'&fromdate=null&todate=null');
			}else{
				$('#${varRetTypeCode}SummaryTable').css("display", "block");
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
				var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
				var type ='custom';
				loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
				loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', fromtime, totime, type, loadReportsCustomersInDropdown);
				loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, type);
				initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
				loadReportsSummary('${id}','${varRetType}','${client.id}', fromtime, totime,'custom');
				invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, type);
				//loadSummaryForExcelDwnld('${id}', '${client.id}', '${varRetType}',"0",fromtime,totime,'custom');
				//$('#dwnldxls').attr('href','${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetType}?reporttype=invoice_report&year=0&fromdate='+fromtime+'&todate='+totime);
			}	
		}
	</script>
</body>
</html>