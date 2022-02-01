<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | ITC Claimed Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
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
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>
.salestable{position:unset}button.dt-button.buttons-excel.buttons-html5{width: auto;}.dt-buttons{left: 59%;}
.noramltable-row{padding:14px; border: 1px solid lightgray;border-radius: 6px;}
.monthly_summary_totals , .yearly_summary_totals , .custom_summary_totals{background-color: white;}
.datepicker-orient-left{top:310px!important}
.monthly_summary_totals , .yearly_summary_totals , .custom_summary_totals{margin-bottom:20px}
.dropdown:hover .dropdown-content.reportclaim{display: block;}.arrow-up {width: 0; height: 0; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportclaim{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 550px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
<div class="breadcrumbwrap" >
	<div class="container bread">
		<div class="row">
	        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
					<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
					<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
					<li class="breadcrumb-item active">ITC Claimed Report</li>
				</ol>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>
<div class="container" style="padding-top:130px;">
	<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right mb-2" role="button" style="padding: 4px 25px;">Back</a>
	<span class="reports-monthly"><h5>Monthly ITC Claimed Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h5><p>Monthly ITC Claimed Report gives you a summary of your monthly purchaes.</p></span>
	<span class="reports-yearly" style="display: none;"><h5>Yearly ITC Claimed Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h5><p>Yearly ITC Claimed Report gives you a summary of your annual Purchases.</p></span>
	<span class="reports-custom" style="display: none;"><h5>Custom ITC  Claimed Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h5><p>Custom ITC  Claimed  Report gives you a summary of your monthly, quarterly and annual Purchases.</p></span>
	<div class="monthly_summary_totals">
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Totals Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices<div class="normaltable-col-txt" id="idTotalTransactions"></div></div>
				<div class="normaltable-col hdr">Total Taxable Value<div class="normaltable-col-txt" id="idTotalTaxableValue"></div></div>
				<div class="normaltable-col hdr">Total Tax Value<div class="normaltable-col-txt" id="idTotalTaxValue"></div></div>
				<div class="normaltable-col hdr">Total Amount <div class="normaltable-col-txt" id="idTotalAmount"></div></div>
				<div class="normaltable-col hdr">Total ITC Claimed<div class="normaltable-col-txt" id="idTotalItcClaimed"></div></div>
			</div>	
		</div>
	</div>
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
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
				<div class="dropdown-content reportclaim"> <span class="arrow-up"></span><span class="pl-2"> All the Claimed Invoices from your PurchaseRegister/Books Monthly, Yearly and Custom Wise</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="customtable db-ca-view salestable reportsdbTable">
				<table id="dbItcInvoiceTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th class="text-center">Type</th><th class="text-center">Invoice No</th><th class="text-center dt-body-right">Suppliers</th><th class="text-center dt-body-right">GSTIN</th><th class="text-center dt-body-right">Date</th><th class="text-center dt-body-right">Taxable Amt</th><th class="text-center dt-body-right">Total Tax</th><th class="text-center dt-body-right">Total Amt</th><th class="text-center dt-body-right">ITC Claimed</th><th class="text-center dt-body-right">ITC Claimed Date</th>
						</tr>
					</thead>
					<tbody id="customhsn"></tbody>
				</table>
			</div>
		</div>
		<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
			<div class="modal-dialog modal-md modal-right" role="document">
			    <div class="modal-content">
			    	<div class="modal-body">
			        	<button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
			        	<div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
			        	<div class=" p-2 steptext-wrap"><span class="pl-2"> All the Claimed Invoices from your PurchaseRegister/Books Monthly, Yearly and Custom Wise</span> </div>
			      	</div>
			      	<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
			      	</div>
			    </div>
			</div>
		</div>
<%@include file="/WEB-INF/views/includes/footer.jsp"%>
<script src="${contextPath}/static/mastergst/js/reports/itc_reports.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function(){
		var date = new Date();
		var month = date.getMonth()+1;
		var	year = date.getFullYear();
		loadItcInvoices('${id}', '${clientid}', '${varRetType}', '${varRetTypeCode}', month, year, "itc_claimed");
	});
	function generateData() {
		var abc = $('#fillingoption span').html();
		if(abc == 'Monthly'){
			$('#${varRetTypeCode}SummaryTable').css("display", "none");
			$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
			var fp = $('#monthly').val();var fpsplit = fp.split('-');var month = parseInt(fpsplit[0]);	var year = parseInt(fpsplit[1]);
			loadItcInvoices('${id}', '${clientid}', '${varRetType}', '${varRetTypeCode}', month, year, "itc_claimed", "monthly");
		}else if (abc == 'Yearly') {
			$('#${varRetTypeCode}SummaryTable').css("display", "block");
			$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
			var year=$('#yearlyoption').html().split("-");
			loadItcInvoices('${id}', '${clientid}', '${varRetType}', '${varRetTypeCode}', 0, year[0], "itc_claimed", "yearly");
		}else{
			$('#${varRetTypeCode}SummaryTable').css("display", "block");
			$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
			var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
			loadItcInvoices('${id}', '${clientid}', '${varRetType}', '${varRetTypeCode}', fromtime, totime, "itc_claimed", "custom");
		}
	}	
</script>
</body>
</html>
