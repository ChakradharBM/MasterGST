<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Multi Month Summary</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_3bvs1.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script type="text/javascript">
	var invoiceArray = new Object();
	function getdiv() {
		var date=$('#monthly').val().split("-");
		ajaxFunction(date[0],date[1]);
	}
	function exportF(elem) {
		  var table = document.getElementById("month4_reportTable");
		  var html = table.outerHTML;
		  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
		  elem.setAttribute("href", url);
		  elem.setAttribute("download", "SALES_VS_GSTR3B_VS_GSTR1.xls"); // Choose the file name
		  return false;
	}
</script>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	<div class="breadcrumbwrap">
		<div class="container">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" />
						<c:choose>
								<c:when
									test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when>
								<c:otherwise>Business</c:otherwise>
							</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose>
									<c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
									<c:otherwise>${client.businessname}</c:otherwise>
								</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink"
							link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">SALES VS GSTR3B VS GSTR1 Report</li>
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
			<h4>SALES VS GSTR 3B VS GSTR1 Report</h4>
			
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal"> Help To Read This Report
			
			<div class="dropdown-content report3BVS1 mt-1"> 
			<span class="arrow-up"></span>
			<ul class="pl-2">
                <li> <span class="steptext-desc"><b>1.  Sales </b><span class="colon" style="margin-left: 17px;">:</span><span class="pl-2"> All the Sale Invoices from your SaleRegister/Books</span></span></li>
				<li><span class="steptext-desc"><b>2. GSTR1 </b><span class="colon" style="margin-left: 10px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>
				<li> <span class="steptext-desc"><b>3. GSTR3B </b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2">Filed Data downloaded from GSTIN</span></span></li>                
		 		
		 </ul> 	
		 <b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between Sales and GSTR1 (Sales Data - (minus) GSTR1 Data)</span><span class="pl-2" style="display: block;">Differences between Sales and GSTR3B (Sales Data - (minus) GSTR3B Data)</span><span class="pl-2" style="display: block;">Differences between GSTR3B and GSTR1 (GSTR3B - (minus) GSTR1 Data)</span>
			
			</div>
			</div><span class="helpbtn" style="position: relative;top:2px;"><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
				<lable for="monthly" class="mr-1">
				<a id="downloadLink" class="excel_btn" onclick="exportF(this)" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
				<b>Select Month: </b></lable>
				<div class="datetimetxt datetime-wrap pull-right" style="display:inline-flex">
					<div class="input-group date dpMonths3b" id="dpMonths3b" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
						<input type="text" class="form-control monthly" id="monthly"> <span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
					</div>
					<a href="#" class="btn btn-greendark pull-right" role="button" style="padding: 4px 10px; z-index: 2;" onClick="getdiv()">Generate</a>
				</div>
			</div>

			<div id="yearProcess" class="text-center"></div>
			<div class="customtable db-ca-view reportTable reportTable4">
				<table id="month4_reportTable" border="1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th colspan="7" class="text-center maintable_hr" id="monthname"></th>
							<th colspan="6" class="text-center maintable_hr">Comparision</th>
						</tr>
						<tr>
							<th rowspan="2" class="text-center">Tax Details</th>
							<th colspan="2" class="text-center books_header">Sales</th>
							<th colspan="2" class="text-center gstr3b-th">GSTR3B</th>
							<th colspan="2" class="text-center gstr1-th">GSTR1</th>
							<th colspan="2" class="text-center books_header">Sales - GSTR3B</th>
							<th colspan="2" class="text-center gstr3b-th">Sales - GSTR1</th>
							<th colspan="2" class="text-center gstr1-th">GSTR3B - GSTR1</th>
						</tr>
						<tr>
							<th class="text-center books_th" style="color:white!important">Taxable value</th>
							<th class="text-center books_th">Tax</th>
							<th class="text-center gstr3b-th">Taxable value</th>
							<th class="text-center gstr3b-th">Tax</th>
							<th class="text-center gstr1-th">Taxable value</th>
							<th class="text-center gstr1-th">Tax</th>
							<th class="text-center books_header">Taxable value</th>
							<th class="text-center books_header">Tax</th>
							<th class="text-center gstr3b-th">Taxable value</th>
							<th class="text-center gstr3b-th">Tax</th>
							<th class="text-center gstr1-th">Taxable value</th>
							<th class="text-center gstr1-th">Tax</th>
						</tr>
					</thead>
					<tbody id="yeartotoalreport">
						<tr>
							<td><p align="right" class="3bvs2ainfo">B2B (+/- Debit/Credit Notes & Adjustments.)</p></td>
							<td id="b2b_R_CBW_Taxableamount">0</td>
							<td id="b2b_R_CBW_Taxamount">0</td>
							<td rowspan="6" id="GSTR3B_3a_Taxableamt"></td>
							<td rowspan="6" id="GSTR3B_3a_Taxamt"></td>
							<td class="gstr1_td" id="gstr1b2b_R_CBW_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1b2b_R_CBW_Taxamount">0</td>
							<td rowspan="6" class="gst3b_td" id="diffBook_GSTR3B_Taxableamount_Total1">0</td>
							<td rowspan="6" class="gst3b_td" id="diffBook_GSTR3B_Taxamount_Total1">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_B2B_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_B2B_Taxamount">0</td>
							<td rowspan="6" id="diffGSTR1_GSTR3B_Taxableamount_Total1">0</td>
							<td rowspan="6" id="diffGSTR1_GSTR3B_Taxamount_Total1">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">B2C (+/- Debit/Credit Notes & Adjustments.)</p></td>
							<td id="b2cl_R_CBW_Taxableamount">0</td>
							<td id="b2cl_R_CBW_Taxamount">0</td>
							<td class="gstr1_td" id="gstr1b2cl_R_CBW_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1b2cl_R_CBW_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_B2C_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_B2C_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Deemed exports</p></td>
							<td id="b2b_CL_DE_Taxableamount">0</td>
							<td id="b2b_CL_DE_Taxamount">0</td>
							<td class="gstr1_td" id="gstr1b2b_CL_DE_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1b2b_CL_DE_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Deemed_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Deemed_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Taxable under RCM</p></td>
							<td id="rcm_Taxableamount">0</td>
							<td id="rcm_Taxamount">0</td>
							<td class="gstr1_td" id="gstr1rcm_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1rcm_Taxamount">0</td>
							<td class="b-gstr1_td"id="diffBook_GSTR1_RCM_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_RCM_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Advances received</p></td>
							<td id="">0</td>
							<td id="">0</td>
							<td class="gstr1_td" id="">0</td>
							<td class="gstr1_td" id="">0</td>
							<td class="b-gstr1_td" id="">0</td class="b-gstr1_td">
							<td class="b-gstr1_td" id="">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">'-Advances adjusted</p></td>
							<td id="">0</td>
							<td id="">0</td>
							<td class="gstr1_td" id="">0</td>
							<td class="gstr1_td" id="">0</td>
							<td class="b-gstr1_td" id="">0</td>
							<td class="b-gstr1_td" id="">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">SEZ sales-WP</p></td>
							<td id="b2b_CL_SEWP_Taxableamount">0</td>
							<td id="b2b_CL_SEWP_Taxamount">0</td>
							<td rowspan="4" id="GSTR3B_3b_Taxableamt"></td>
							<td rowspan="4" id="GSTR3B_3b_Taxamt"></td>
							<td class="gstr1_td" id="gstr1b2b_CL_SEWP_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1b2b_CL_SEWP_Taxamount">0</td>
							<td rowspan="4" class="gst3b_td" id="diffBook_GSTR3B_Taxableamount_Total2">0</td>
							<td rowspan="4" class="gst3b_td" id="diffBook_GSTR3B_Taxamount_Total2">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_SEZWP_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_SEZWP_Taxamount">0</td>
							<td rowspan="4" id="diffGSTR1_GSTR3B_Taxableamount_Total2">0</td>
							<td rowspan="4" id="diffGSTR1_GSTR3B_Taxamount_Total2">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">SEZ sales-WOP</p></td>
							<td id="b2b_CL_SEWOP_Taxableamount">0</td>
							<td id="b2b_CL_SEWOP_Taxamount">0</td>
							<td class="gstr1_td" id="gstr1b2b_CL_SEWOP_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1b2b_CL_SEWOP_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_SEZWOP_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_SEZWOP_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Exports-WP</p></td>
							<td id="exports_WP_Taxableamount">0</td>
							<td id="exports_WP_Taxamount">0</td>
							<td class="gstr1_td" id="gstr1exports_WP_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1exports_WP_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Export_WP_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Export_WP_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Exports-WOP</p></td>
							<td id="exports_WOP_Taxableamount">0</td>
							<td id="exports_WOP_Taxamount">0</td>
							<td class="gstr1_td" id="gstr1exports_WOP_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1exports_WOP_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Export_WOP_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Export_WOP_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Exempted & Nil rated</p></td>
							<td id="nilrated_Exempted_Taxableamount">0</td>
							<td id="nilrated_Exempted_Taxamount">0</td>
							<td id="GSTR3B_3c_Taxableamt">0</td>
							<td id="GSTR3B_3c_Taxamt">0</td>
							<td class="gstr1_td" id="gstr1nilrated_Exempted_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1nilrated_Exempted_Taxamount">0</td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Nilrated_Exempted_Taxableamount">0</td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Nilrated_Exempted_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Nil_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Nil_Taxamount">0</td>
							<td id="diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount">0</td>
							<td id="diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Non-GST</p></td>
							<td id="nongst_Taxableamount">0</td>
							<td id="nongst_Taxamount">0</td>
							<td id="GSTR3B_3e_Taxableamt">0</td>
							<td id="GSTR3B_3e_Taxamt">0.00</td>
							<td class="gstr1_td" id="gstr1nongst_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1nongst_Taxamount">0</td>
							<td class="gst3b_td" id="diffBook_GSTR3B_NonGST_Taxableamount">0</td>
							<td class="gst3b_td" id="diffBook_GSTR3B_NonGST_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_NonGst_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_NonGst_Taxamount">0</td>
							<td id="diffGSTR1_GSTR3B_NonGST_Taxableamount">0</td>
							<td id="diffGSTR1_GSTR3B_NonGST_Taxamount">0</td>
						</tr>
						<tr>
							<td><h6 align="right" class="3bvs2ainfo">Total Taxable value</h6></td>
							<td id="book_Total_Taxableamt1"></td>
							<td id="book_Total_Taxamt1"></td>
							<td id="GSTR3B_Total_Taxableamt1"></td>
							<td id="GSTR3B_Total_Taxamt1"></td>
							<td class="gstr1_td" id="gstr1_Total_Taxableamt1"></td>
							<td class="gstr1_td" id="gstr1_Total_Taxamt1"></td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Total_Taxableamount1"></td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Total_Taxamount1"></td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Total_Taxableamount1"></td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Total_Taxamount1"></td>
							<td id="diffGSTR1_GSTR3B_Total_Taxableamount1"></td>
							<td id="diffGSTR1_GSTR3B_Total_Taxamount1"></td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Inter-state</p></td>
							<td id="book_inter_Taxableamount">0</td>
							<td id="book_inter_Taxamount">0</td>
							<td rowspan="2" id="GSTR3B_Total_Taxableamt"></td>
							<td id="GSTR3B_IGST">0</td>
							<td class="gstr1_td" id="gstr1_inter_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1_inter_Taxamount">0</td>
							<td rowspan="2" class="gst3b_td"id="diffBook_GSTR3B_Inter_Taxableamount">0</td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Inter_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Inter_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Inter_Taxamount">0</td>
							<td rowspan="2" id="diffGSTR1_GSTR3B_Inter_Taxableamount">0</td>
							<td id="diffGSTR1_GSTR3B_Inter_Taxamount">0</td>
						</tr>
						<tr>
							<td><p align="right" class="3bvs2ainfo">Intra-state</p></td>
							<td id="book_intra_Taxableamount">0</td>
							<td id="book_intra_Taxamount">0</td>
							<td id="GSTR3B_CGST_SGST">0</td>
							<td class="gstr1_td" id="gstr1_intra_Taxableamount">0</td>
							<td class="gstr1_td" id="gstr1_intra_Taxamount">0</td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Intra_Taxamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Intra_Taxableamount">0</td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Intra_Taxamount">0</td>
							<td id="diffGSTR1_GSTR3B_Intra_Taxamount">0</td>
						</tr>
						<tr>
							<td><h6 align="right" class="3bvs2ainfo">Total Taxable value</h6></td>
							<td id="book_Total_Taxableamt2"></td>
							<td id="book_Total_Taxamt2"></td>
							<td id="GSTR3B_Total_Taxableamt2"></td>
							<td id="GSTR3B_Total_Taxamt2"></td>
							<td class="gstr1_td" id="gstr1_Total_Taxableamt2"></td>
							<td class="gstr1_td" id="gstr1_Total_Taxamt2"></td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Total_Taxableamount2"></td>
							<td class="gst3b_td" id="diffBook_GSTR3B_Total_Taxamount2"></td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Total_Taxableamount2"></td>
							<td class="b-gstr1_td" id="diffBook_GSTR1_Total_Taxamount2"></td>
							<td id="diffGSTR1_GSTR3B_Total_Taxableamount2"></td>
							<td id="diffGSTR1_GSTR3B_Total_Taxamount2"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap custom1" style="display: none">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}"
				class="btn btn-blue-dark pull-right" role="button"
				style="padding: 4px 25px;">Back</a>
			<h2>GSTR2A Report</h2>
			<p>GSTR2A Report gives you a summary of your quarterly and annual
				purchases.</p>
			<div class="dropdown chooseteam  mr-0">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption"
					style="margin-right: 10px; display: inline-flex;"> <span
					style="height: 32px">Report Type:</span>
					<div class="typ-ret"
						style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option2" class="filing_option"
							style="vertical-align: top">Custom</span> <span
							class="input-group-addon add-on pull-right"
							style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i>
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type"
					style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px">
					<a class="dropdown-item" href="#" value="Yearly"
						onClick="getval('Yearly')">Yearly</a> <a class="dropdown-item"
						href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span style="display: none; margin-bottom: 4px" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption1"
					style="margin-right: 10px; display: inline-flex;"> <label
						id="ret-period" style="margin-bottom: 3px;">Return Period:</label>
						<div class="typ-ret"
							style="border: 1px solid; border-radius: 2px; background-color: white; height: 27px; align-items: top; padding-right: 14px; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top;" id="yearlyoption2"class="yearlyoption">2021 - 2022</span>
							 <span	class="input-group-addon add-on pull-right"	style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc" style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
				</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div> <a href="#" class="btn btn-greendark  pull-right" role="button"
					style="padding: 4px 10px;; text-transform: uppercase;"
					onClick="getdiv()">Generate</a>
				</span> <span class="datetimetxt custom-sp" style="display: block"
					id="custom-sp"> <a href="#"
					class="btn btn-greendark  pull-right" role="button"
					style="padding: 4px 10px; text-transform: uppercase;"
					onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
						<label
							style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"
							data-date="10-11-2012" data-date-format="dd-mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="11-02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"> <i
								class="fa fa-sort-desc" id="date-drop"></i>
							</span>
						</div>
					</div>
					<div class="datetimetxt datetime-wrap">
						<label
							style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom"
							data-date="10-2-2012" data-date-format="dd-mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime"
								value="11-02-2014" readonly=""> <span
								class="input-group-addon add-on pull-right"> <i
								class="fa fa-sort-desc" id="date-drop"></i>
							</span>
						</div>
					</div>
				</span>
			</div>
		</div>
	</div>
	<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Help To Read This Report </h3>
        </div>
        <div class=" p-2 steptext-wrap">
		<ul>
                <li> <span class="steptext-desc"><b>1. Sales </b><span class="colon" style="margin-left: 20px;">:</span><span class="pl-2"> All the Sale Invoices from your SaleRegister/Books</span></span></li>
				<li><span class="steptext-desc"><b>2 . GSTR1 </b><span class="colon" style="margin-left: 11px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>
				<li> <span class="steptext-desc"><b>3 . GSTR3B </b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2">Filed Data downloaded from GSTIN</span></span></li>                
		 </ul> 	
 			<b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between Sales and GSTR1 (Sales Data - (minus) GSTR1 Data)</span><span class="pl-2" style="display: block;">Differences between Sales and GSTR3B (Sales Data - (minus) GSTR3B Data)</span><span class="pl-2" style="display: block;">Differences between GSTR3B and GSTR1 (GSTR3B - (minus) GSTR1 Data)</span>
         </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->
	<script type="text/javascript">
		/* var date = $('.dpMonths3b').datepicker({
			autoclose : true,
			viewMode : 1,
			minViewMode : 1,
			format : 'mm-yyyy'
		}) */
		var gstnnumber = '<c:out value="${client.gstnnumber}"/>';
		var headertext = [], headers = document
				.querySelectorAll("table.display th"), tablerows = document
				.querySelectorAll("table.display th"), tablebody = document
				.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {
			var current = headers[i];
			headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
		}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {
			for (var j = 0, col; col = row.cells[j]; j++) {
				col.setAttribute("data-th", headertext[j]);
			}
		}
		OSREC.CurrencyFormatter.formatAll({selector : '.ind_formatss'});
		function checkZero(data) {
			if (data.length == 1) {data = "0" + data;}
			return data;
		}
		function formatDate(date) {
			if (date == null || typeof (date) === 'string'
					|| date instanceof String) {
				return date;
			} else {
				var d = new Date(date), month = '' + (d.getMonth() + 1), day = ''+ d.getDate(), year = d.getFullYear();
				if (month.length < 2)
					month = '0' + month;
				if (day.length < 2)
					day = '0' + day;
				return [ day, month, year ].join('-');
			}
		}
		$('#month4_reportTable td:nth-child(2) , #month4_reportTable td:nth-child(3)').addClass('sample');
		$('#month4_reportTable tr:nth-child(1) td:nth-last-child(1) , #month4_reportTable tr:nth-child(1) td:nth-last-child(2)').addClass('sample1');
		$('#month4_reportTable tr:nth-child(1) td:nth-child(4) , #month4_reportTable tr:nth-child(1) td:nth-child(5) , #month4_reportTable tr:nth-child(7) td:nth-child(4) , #month4_reportTable tr:nth-child(7) td:nth-child(5) , #month4_reportTable tr:nth-child(11) td:nth-child(4) , #month4_reportTable tr:nth-child(11) td:nth-child(5) , #month4_reportTable tr:nth-child(12) td:nth-child(4) , #month4_reportTable tr:nth-child(12) td:nth-child(5) , #month4_reportTable tr:nth-child(13) td:nth-child(4) , #month4_reportTable tr:nth-child(13) td:nth-child(5) , #month4_reportTable tr:nth-child(14) td:nth-child(4) , #month4_reportTable tr:nth-child(14) td:nth-child(5) , #month4_reportTable tr:nth-child(15) td:nth-child(4) , #month4_reportTable tr:nth-child(16) td:nth-child(4) , #month4_reportTable tr:nth-child(16) td:nth-child(5)')
				.addClass('sample2');
		$('#month4_reportTable tr:nth-child(1) td:nth-last-child(1) , #month4_reportTable tr:nth-child(1)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(7)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(7)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(11)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(11)  td:nth-last-child(2), #month4_reportTable tr:nth-child(12)  td:nth-last-child(1), #month4_reportTable tr:nth-child(12)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(13)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(13)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(14)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(14)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(15)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(16)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(16)  td:nth-last-child(2)')
				.addClass('sample3');
	</script>
<script type="text/javascript">
$(document).ready(function(){
	var month=new Date().getMonth()+1;
	var year=new Date().getFullYear();
	$( ".helpicon" ).hover(function() {$('.report3BVS1 ').show();
	}, function() {$('.report3BVS1 ').hide();
	});
	if(month<10){
		$('#monthly').val("0"+month+"-"+year);		
	}else{
		$('#monthly').val(month+"-"+year);
	}
	$('.dpMonths3b').datepicker({
		autoclose : true,
		viewMode : 1,
		minViewMode : 1,
		format : 'mm-yyyy'
	})
	 ajaxFunction(month,year);
});
function ajaxFunction(month,year){
	$.ajax({
		url: "${contextPath}/compare_gstr3b_vs_gstr1/${client.id}/${id}/"+month+"/"+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
 		beforeSend: function () {
	        //$('#yearProcess').text('Processing...');
	    },
		success : function(data) {
			$('#monthname').html(data.month);
			$("#GSTR3B_3a_Taxableamt").html(formatNumber(data.gstr3B_3a_Taxableamt.toFixed(2)));
			$("#GSTR3B_3a_Taxamt").html(formatNumber(data.gstr3B_3a_Taxamt.toFixed(2)));
			$("#GSTR3B_3b_Taxableamt").html(formatNumber(data.gstr3B_3b_Taxableamt.toFixed(2)));
			$("#GSTR3B_3b_Taxamt").html(formatNumber(data.gstr3B_3b_Taxamt.toFixed(2)));
			
			$("#GSTR3B_3c_Taxableamt").html(formatNumber(data.gstr3B_3c_Taxableamt.toFixed(2)));
			$("#GSTR3B_3c_Taxamt").html(formatNumber(data.gstr3B_3c_Taxamt.toFixed(2)));
			$("#GSTR3B_3e_Taxableamt").html(formatNumber(data.gstr3B_3e_Taxableamt.toFixed(2)));
			//$("#GSTR3B_3d_Taxamt").html(formatNumber(data.gstr3B_3d_Taxamt.toFixed(2)));
		
			$('#GSTR3B_Total_Taxableamt').html(formatNumber(data.gstr3B_Total_Taxableamt.toFixed(2)));
			
			$('#GSTR3B_IGST').html(formatNumber(data.gstr3B_IGST.toFixed(2)));
			$('#GSTR3B_CGST_SGST').html(formatNumber(data.gstr3B_CGST_SGST.toFixed(2)));
			
			$('#b2b_R_CBW_Taxableamount').html(formatNumber(data.b2b_R_CBW_Taxableamount.toFixed(2)));
			$('#b2b_R_CBW_Taxamount').html(formatNumber(data.b2b_R_CBW_Taxamount.toFixed(2)));
			$('#b2cl_R_CBW_Taxableamount').html(formatNumber(data.b2cl_R_CBW_Taxableamount.toFixed(2)));
			$('#b2cl_R_CBW_Taxamount').html(formatNumber(data.b2cl_R_CBW_Taxamount.toFixed(2)));
			
			$('#b2b_CL_DE_Taxableamount').html(formatNumber(data.b2b_CL_DE_Taxableamount.toFixed(2)));
			$('#b2b_CL_DE_Taxamount').html(formatNumber(data.b2b_CL_DE_Taxamount.toFixed(2)));
			
			$('#rcm_Taxableamount').html(formatNumber(data.rcm_Taxableamount.toFixed(2)));
			$('#rcm_Taxamount').html(formatNumber(data.rcm_Taxamount.toFixed(2)));
			
			$('#b2b_CL_SEWP_Taxableamount').html(formatNumber(data.b2b_CL_SEWP_Taxableamount.toFixed(2)));
			$('#b2b_CL_SEWP_Taxamount').html(formatNumber(data.b2b_CL_SEWP_Taxamount.toFixed(2)));
			$('#b2b_CL_SEWOP_Taxableamount').html(formatNumber(data.b2b_CL_SEWOP_Taxableamount.toFixed(2)));
			$('#b2b_CL_SEWOP_Taxamount').html(formatNumber(data.b2b_CL_SEWOP_Taxamount.toFixed(2)));
			$('#exports_WP_Taxableamount').html(formatNumber(data.exports_WP_Taxableamount.toFixed(2)));
			$('#exports_WP_Taxamount').html(formatNumber(data.exports_WP_Taxamount.toFixed(2)));
			$('#exports_WOP_Taxableamount').html(formatNumber(data.exports_WOP_Taxableamount.toFixed(2)));
			$('#exports_WOP_Taxamount').html(formatNumber(data.exports_WOP_Taxamount.toFixed(2)));
			
			$('#nilrated_Exempted_Taxableamount').html(formatNumber(data.nilrated_Exempted_Taxableamount.toFixed(2)));
			$('#nilrated_Exempted_Taxamount').html(formatNumber(data.nilrated_Exempted_Taxamount.toFixed(2)));
			$('#nongst_Taxableamount').html(formatNumber(data.nongst_Taxableamount.toFixed(2)));
			$('#nongst_Taxamount').html(formatNumber(data.nongst_Taxamount.toFixed(2)));
			
			$('#book_inter_Taxableamount').html(formatNumber(data.book_inter_Taxableamount.toFixed(2)));
			$('#book_inter_Taxamount').html(formatNumber(data.book_inter_Taxamount.toFixed(2)));
			$('#book_intra_Taxableamount').html(formatNumber(data.book_intra_Taxableamount.toFixed(2)));
			$('#book_intra_Taxamount').html(formatNumber(data.book_intra_Taxamount.toFixed(2)));
			
			$('#book_Total_Taxableamt1').html(formatNumber(data.book_Total_Taxableamt1.toFixed(2)));
			$('#book_Total_Taxamt1').html(formatNumber(data.book_Total_Taxamt1.toFixed(2)));
			$('#book_Total_Taxableamt2').html(formatNumber(data.book_Total_Taxableamt2.toFixed(2)));
			$('#book_Total_Taxamt2').html(formatNumber(data.book_Total_Taxamt2.toFixed(2)));
			
			$('#diffBook_GSTR3B_Taxableamount_Total1').html(formatNumber(data.diffBook_GSTR3B_Taxableamount_Total1.toFixed(2)));
			$('#diffBook_GSTR3B_Taxamount_Total1').html(formatNumber(data.diffBook_GSTR3B_Taxamount_Total1.toFixed(2)));
			
			$('#diffBook_GSTR3B_Taxableamount_Total2').html(formatNumber(data.diffBook_GSTR3B_Taxableamount_Total2.toFixed(2)));
			$('#diffBook_GSTR3B_Taxamount_Total2').html(formatNumber(data.diffBook_GSTR3B_Taxamount_Total2.toFixed(2)));
			
			$('#diffBook_GSTR3B_Nilrated_Exempted_Taxableamount').html(formatNumber(data.diffBook_GSTR3B_Nilrated_Exempted_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR3B_Nilrated_Exempted_Taxamount').html(formatNumber(data.diffBook_GSTR3B_Nilrated_Exempted_Taxamount.toFixed(2)));
		
			$('#diffBook_GSTR3B_NonGST_Taxableamount').html(formatNumber(data.diffBook_GSTR3B_NonGST_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR3B_NonGST_Taxamount').html(formatNumber(data.diffBook_GSTR3B_NonGST_Taxamount.toFixed(2)));
			
			$('#GSTR3B_Total_Taxableamt1').html(formatNumber(data.gstr3B_Total_Taxableamt.toFixed(2)));
			$('#GSTR3B_Total_Taxableamt2').html(formatNumber(data.gstr3B_Total_Taxableamt.toFixed(2)));
			$('#GSTR3B_Total_Taxamt1').html(formatNumber(data.gstr3B_Total_Taxamt2.toFixed(2)));
			$('#GSTR3B_Total_Taxamt2').html(formatNumber(data.gstr3B_Total_Taxamt2.toFixed(2)));
			
			$('#diffBook_GSTR3B_Total_Taxableamount1').html(formatNumber(data.diffBook_GSTR3B_Total_Taxableamount1.toFixed(2)));
			$('#diffBook_GSTR3B_Total_Taxamount1').html(formatNumber(data.diffBook_GSTR3B_Total_Taxamount1.toFixed(2)));
		
			$('#diffBook_GSTR3B_Inter_Taxableamount').html(formatNumber(data.diffBook_GSTR3B_Inter_Taxableamount.toFixed(2)));
			
			$('#diffBook_GSTR3B_Inter_Taxamount').html(formatNumber(data.diffBook_GSTR3B_Inter_Taxamount.toFixed(2)));
			$('#diffBook_GSTR3B_Intra_Taxamount').html(formatNumber(data.diffBook_GSTR3B_Intra_Taxamount.toFixed(2)));
			$('#diffBook_GSTR3B_Total_Taxableamount2').html(formatNumber(data.diffBook_GSTR3B_Total_Taxableamount2.toFixed(2)));
			$('#diffBook_GSTR3B_Total_Taxamount2').html(formatNumber(data.diffBook_GSTR3B_Total_Taxamount2.toFixed(2)));
			
			$('#gstr1b2b_R_CBW_Taxableamount').html(formatNumber(data.gstr1b2b_R_CBW_Taxableamount.toFixed(2)));
			$('#gstr1b2b_R_CBW_Taxamount').html(formatNumber(data.gstr1b2b_R_CBW_Taxamount.toFixed(2)));
			$('#gstr1b2cl_R_CBW_Taxableamount').html(formatNumber(data.gstr1b2cl_R_CBW_Taxableamount.toFixed(2)));
			$('#gstr1b2cl_R_CBW_Taxamount').html(formatNumber(data.gstr1b2cl_R_CBW_Taxamount.toFixed(2)));
			
			$('#gstr1b2b_CL_DE_Taxableamount').html(formatNumber(data.gstr1b2b_CL_DE_Taxableamount.toFixed(2)));
			$('#gstr1b2b_CL_DE_Taxamount').html(formatNumber(data.gstr1b2b_CL_DE_Taxamount.toFixed(2)));
			
			$('#gstr1rcm_Taxableamount').html(formatNumber(data.gstr1rcm_Taxableamount.toFixed(2)));
			$('#gstr1rcm_Taxamount').html(formatNumber(data.gstr1rcm_Taxamount.toFixed(2)));
			
			$('#gstr1b2b_CL_SEWP_Taxableamount').html(formatNumber(data.gstr1b2b_CL_SEWP_Taxableamount.toFixed(2)));
			$('#gstr1b2b_CL_SEWP_Taxamount').html(formatNumber(data.gstr1b2b_CL_SEWP_Taxamount.toFixed(2)));
			$('#gstr1b2b_CL_SEWOP_Taxableamount').html(formatNumber(data.gstr1b2b_CL_SEWOP_Taxableamount.toFixed(2)));
			$('#gstr1b2b_CL_SEWOP_Taxamount').html(formatNumber(data.gstr1b2b_CL_SEWOP_Taxamount.toFixed(2)));
			$('#gstr1exports_WP_Taxableamount').html(formatNumber(data.gstr1exports_WP_Taxableamount.toFixed(2)));
			$('#gstr1exports_WP_Taxamount').html(formatNumber(data.gstr1exports_WP_Taxamount.toFixed(2)));
			$('#gstr1exports_WOP_Taxableamount').html(formatNumber(data.gstr1exports_WOP_Taxableamount.toFixed(2)));
			$('#gstr1exports_WOP_Taxamount').html(formatNumber(data.gstr1exports_WOP_Taxamount.toFixed(2)));
			
			$('#gstr1nilrated_Exempted_Taxableamount').html(formatNumber(data.gstr1nilrated_Exempted_Taxableamount.toFixed(2)));
			$('#gstr1nilrated_Exempted_Taxamount').html(formatNumber(data.gstr1nilrated_Exempted_Taxamount.toFixed(2)));
			$('#gstr1nongst_Taxableamount').html(formatNumber(data.gstr1nongst_Taxableamount.toFixed(2)));
			$('#gstr1nongst_Taxamount').html(formatNumber(data.gstr1nongst_Taxamount.toFixed(2)));

			$('#gstr1_inter_Taxableamount').html(formatNumber(data.gstr1_inter_Taxableamount.toFixed(2)));
			$('#gstr1_inter_Taxamount').html(formatNumber(data.gstr1_inter_Taxamount.toFixed(2)));
			$('#gstr1_intra_Taxableamount').html(formatNumber(data.gstr1_intra_Taxableamount.toFixed(2)));
			$('#gstr1_intra_Taxamount').html(formatNumber(data.gstr1_intra_Taxamount.toFixed(2)));
			
			$('#gstr1_Total_Taxableamt1').html(formatNumber(data.gstr1_Total_Taxableamt1.toFixed(2)));
			$('#gstr1_Total_Taxamt1').html(formatNumber(data.gstr1_Total_Taxamt1.toFixed(2)));
			$('#gstr1_Total_Taxableamt2').html(formatNumber(data.gstr1_Total_Taxableamt2.toFixed(2)));
			$('#gstr1_Total_Taxamt2').html(formatNumber(data.gstr1_Total_Taxamt2.toFixed(2)));
			
			$('#diffGSTR1_GSTR3B_Taxableamount_Total1').html(formatNumber(data.diffGSTR1_GSTR3B_Taxableamount_Total1.toFixed(2)));
			$("#diffGSTR1_GSTR3B_Taxamount_Total1").html(formatNumber(data.diffGSTR1_GSTR3B_Taxamount_Total1.toFixed(2)));

			$('#diffGSTR1_GSTR3B_Taxableamount_Total2').html(formatNumber(data.diffGSTR1_GSTR3B_Taxableamount_Total2.toFixed(2)));
			$("#diffGSTR1_GSTR3B_Taxamount_Total2").html(formatNumber(data.diffGSTR1_GSTR3B_Taxamount_Total2.toFixed(2)));
			
			$('#diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount').html(formatNumber(data.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount.toFixed(2)));
			$('#diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount').html(formatNumber(data.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount.toFixed(2)));			
			$('#diffGSTR1_GSTR3B_NonGST_Taxableamount').html(formatNumber(data.diffGSTR1_GSTR3B_NonGST_Taxableamount.toFixed(2)));
			$('#diffGSTR1_GSTR3B_NonGST_Taxamount').html(formatNumber(data.diffGSTR1_GSTR3B_NonGST_Taxamount.toFixed(2)));
			
			$('#diffGSTR1_GSTR3B_Inter_Taxableamount').html(formatNumber(data.diffGSTR1_GSTR3B_Inter_Taxableamount.toFixed(2)));
			$('#diffGSTR1_GSTR3B_Inter_Taxamount').html(formatNumber(data.diffGSTR1_GSTR3B_Inter_Taxamount.toFixed(2)));
			$('#diffGSTR1_GSTR3B_Intra_Taxamount').html(formatNumber(data.diffGSTR1_GSTR3B_Intra_Taxamount.toFixed(2)));
			
			$('#diffGSTR1_GSTR3B_Total_Taxableamount1').html(formatNumber(data.diffGSTR1_GSTR3B_Total_Taxableamount1.toFixed(2)));
			$('#diffGSTR1_GSTR3B_Total_Taxamount1').html(formatNumber(data.diffGSTR1_GSTR3B_Total_Taxamount1.toFixed(2)));
			$('#diffGSTR1_GSTR3B_Total_Taxableamount2').html(formatNumber(data.diffGSTR1_GSTR3B_Inter_Taxableamount.toFixed(2)));
			$('#diffGSTR1_GSTR3B_Total_Taxamount2').html(formatNumber(data.diffGSTR1_GSTR3B_Total_Taxamount2.toFixed(2)));
			
			$('#diffBook_GSTR1_B2B_Taxableamount').html(formatNumber(data.diffBook_GSTR1_B2B_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_B2B_Taxamount').html(formatNumber(data.diffBook_GSTR1_B2B_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_B2C_Taxableamount').html(formatNumber(data.diffBook_GSTR1_B2C_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_B2C_Taxamount').html(formatNumber(data.diffBook_GSTR1_B2C_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_Deemed_Taxableamount').html(formatNumber(data.diffBook_GSTR1_Deemed_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_Deemed_Taxamount').html(formatNumber(data.diffBook_GSTR1_Deemed_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_RCM_Taxableamount').html(formatNumber(data.diffBook_GSTR1_RCM_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_RCM_Taxamount').html(formatNumber(data.diffBook_GSTR1_RCM_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_SEZWP_Taxableamount').html(formatNumber(data.diffBook_GSTR1_SEZWP_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_SEZWP_Taxamount').html(formatNumber(data.diffBook_GSTR1_SEZWP_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_SEZWOP_Taxableamount').html(formatNumber(data.diffBook_GSTR1_SEZWOP_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_SEZWOP_Taxamount').html(formatNumber(data.diffBook_GSTR1_SEZWOP_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_Export_WP_Taxableamount').html(formatNumber(data.diffBook_GSTR1_Export_WP_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_Export_WP_Taxamount').html(formatNumber(data.diffBook_GSTR1_Export_WP_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_Export_WOP_Taxableamount').html(formatNumber(data.diffBook_GSTR1_Export_WOP_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_Export_WOP_Taxamount').html(formatNumber(data.diffBook_GSTR1_Export_WOP_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_Nil_Taxableamount').html(formatNumber(data.diffBook_GSTR1_Nil_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_Nil_Taxamount').html(formatNumber(data.diffBook_GSTR1_Nil_Taxamount.toFixed(2)));
			
			$('#diffBook_GSTR1_NonGst_Taxableamount').html(formatNumber(data.diffBook_GSTR1_NonGst_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_NonGst_Taxamount').html(formatNumber(data.diffBook_GSTR1_NonGst_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_Inter_Taxableamount').html(formatNumber(data.diffBook_GSTR1_Inter_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_Inter_Taxamount').html(formatNumber(data.diffBook_GSTR1_Inter_Taxamount.toFixed(2)));
			$('#diffBook_GSTR1_Intra_Taxableamount').html(formatNumber(data.diffBook_GSTR1_Intra_Taxableamount.toFixed(2)));
			$('#diffBook_GSTR1_Intra_Taxamount').html(formatNumber(data.diffBook_GSTR1_Intra_Taxamount.toFixed(2)));
			
			$('#diffBook_GSTR1_Total_Taxableamount1').html(formatNumber(data.diffBook_GSTR1_Total_Taxableamount1.toFixed(2)));
			$('#diffBook_GSTR1_Total_Taxamount1').html(formatNumber(data.diffBook_GSTR1_Total_Taxamount1.toFixed(2)));
			$('#diffBook_GSTR1_Total_Taxableamount2').html(formatNumber(data.diffBook_GSTR1_Total_Taxableamount2.toFixed(2)));
			$('#diffBook_GSTR1_Total_Taxamount2').html(formatNumber(data.diffBook_GSTR1_Total_Taxamount2.toFixed(2)));
		}, 
		error : function(error){
		}
	});	
}
</script>
</body>
</html>