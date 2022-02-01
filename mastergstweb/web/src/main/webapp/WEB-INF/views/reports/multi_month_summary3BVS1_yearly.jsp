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
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_3bvs1_yearly.css" media="all" />
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
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<script type="text/javascript">
$(window).on('load', function() {
	OSREC.CurrencyFormatter.formatAll({selector : '.ind_formatss'});
});
$(document).ready(function() {
	//$("#month4_reportTable").clone(true).appendTo('.reportTable4').addClass('clone'); 
	$( ".helpicon" ).hover(function() {$('.report3BVS1 ').show();
	}, function() {$('.report3BVS1 ').hide();
	});
} );
function exportF(elem) {
	  var table = document.getElementById("excel_table");
	  var html = table.outerHTML;
	  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
	  elem.setAttribute("href", url);
	  elem.setAttribute("download", "SALES_VS_GSTR3B_VS_GSTR1_yearly.xls"); // Choose the file name
	  return false;
}
</script>
<script type="text/javascript">
	$('#month4_reportTable td:nth-child(2) , #month4_reportTable td:nth-child(3)').css('background-color', '#a480d052');
	$('#month4_reportTable tr:nth-child(1) td:nth-last-child(1) , #month4_reportTable tr:nth-child(1) td:nth-last-child(2)').css('background-color', '#e6b8b74d');
	$('#month4_reportTable tr:nth-child(1) td:nth-child(4) , #month4_reportTable tr:nth-child(1) td:nth-child(5) , #month4_reportTable tr:nth-child(7) td:nth-child(4) , #month4_reportTable tr:nth-child(7) td:nth-child(5) , #month4_reportTable tr:nth-child(11) td:nth-child(4) , #month4_reportTable tr:nth-child(11) td:nth-child(5) , #month4_reportTable tr:nth-child(12) td:nth-child(4) , #month4_reportTable tr:nth-child(12) td:nth-child(5) , #month4_reportTable tr:nth-child(13) td:nth-child(4) , #month4_reportTable tr:nth-child(13) td:nth-child(5) , #month4_reportTable tr:nth-child(14) td:nth-child(4) , #month4_reportTable tr:nth-child(14) td:nth-child(5) , #month4_reportTable tr:nth-child(15) td:nth-child(4) , #month4_reportTable tr:nth-child(16) td:nth-child(4) , #month4_reportTable tr:nth-child(16) td:nth-child(5)').css('background-color', '#d9e5f5');
	$('#month4_reportTable tr:nth-child(1) td:nth-last-child(1) , #month4_reportTable tr:nth-child(1)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(7)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(7)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(11)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(11)  td:nth-last-child(2), #month4_reportTable tr:nth-child(12)  td:nth-last-child(1), #month4_reportTable tr:nth-child(12)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(13)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(13)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(14)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(14)  td:nth-last-child(2) , #month4_reportTable tr:nth-child(15)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(16)  td:nth-last-child(1) , #month4_reportTable tr:nth-child(16)  td:nth-last-child(2)').css('background-color', '#e6b8b74d');
	$('.reportTable tr th.books_th').css('background-color', '#8064a2');
	function getdiv(){
		var customyear=$('#financialyear').val();
		var filingoption=$('#filingoption').val();
		window.location.href = '${contextPath}/multiMonthreports3bvs1yearly/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>/'+filingoption+"/"+customyear;
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
							<c:when	test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when>
								<c:otherwise>Business</c:otherwise>
							</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose>
							<c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
								<c:otherwise>${client.businessname}</c:otherwise>
							</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
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
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}"
				class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>SALES VS GSTR 3B VS GSTR1 Report</h4>
		<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#report1helpGuideModal"> Help To Read This Report
			
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
			<div class="dropdwn mr-0" style="height: 32px;float: right;">
				<a id="downloadLink" class="excel_btn" onclick="exportF(this)" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
				<lable for="monthly" class="mr-1" style="vertical-align: middle;font-weight: bolder;">Select Report Filing Option: </lable>
				<div style="display: inline-flex;">
					<select class="form-control" id="filingoption" style="z-index:2;padding: 0px;height: 28px;margin-right: 8px;">
						<c:choose>
							<c:when test="${filingoption eq 'Quarterly'}">
								<option value="Monthly">Monthly</option>
								<option selected="selected" value="Quarterly">Quarterly</option>
							</c:when>
							<c:otherwise>
								<option value="Monthly">Monthly</option>
								<option value="Quarterly">Quarterly</option>
							</c:otherwise>
						</c:choose>
					</select>
				</div>
				<lable for="monthly" class="mr-1" style="vertical-align: middle;font-weight: bolder;">Select Financial Year: </lable>
				<div class="pull-right" style="display: inline-flex;">
					<select class="form-control" id="financialyear" style="z-index:2;padding: 0px;height: 28px;margin-right: 8px;">
						<c:choose>
							<c:when test="${customyear eq '2021'}">
								<option selected="selected" value="2021">2021 - 2022</option>
								<option value="2020">2020 - 2021</option>
								<option value="2019">2019 - 2020</option>
								<option value="2018">2018 - 2019</option>
								<option value="2017">2017 - 2018</option>
							</c:when>
							<c:when test="${customyear eq '2020'}">
								<option value="2021">2021 - 2022</option>
								<option selected="selected" value="2020">2020 - 2021</option>
								<option value="2019">2019 - 2020</option>
								<option value="2018">2018 - 2019</option>
								<option value="2017">2017 - 2018</option>
							</c:when>
							<c:when test="${customyear eq '2019'}">
								<option value="2021">2021 - 2022</option>
								<option value="2020">2020 - 2021</option>
								<option selected="selected" value="2019">2019 - 2020</option>
								<option value="2018">2018 - 2019</option>
								<option value="2017">2017 - 2018</option>
							</c:when>
							<c:when test="${customyear eq '2018'}">
								<option value="2021">2021 - 2022</option>
								<option value="2020">2020 - 2021</option>
								<option value="2019">2019 - 2020</option>
								<option selected="selected" value="2018">2018 - 2019</option>
								<option value="2017">2017 - 2018</option>
							</c:when>
							<c:when test="${customyear eq '2017'}">
								<option value="2021">2021 - 2022</option>
								<option value="2020">2020 - 2021</option>
								<option value="2019">2019 - 2020</option>
								<option value="2018">2018 - 2019</option>
								<option selected="selected" value="2017">2017 - 2018</option>
							</c:when>
							<c:otherwise>
								<option value="2021">2021 - 2022</option>
								<option value="2020">2020 - 2021</option>
								<option value="2019">2019 - 2020</option>
								<option value="2018">2018 - 2019</option>
								<option value="2017">2017 - 2018</option>
							</c:otherwise>
						</c:choose>
					</select>
					<button class="btn btn-greendark pull-right" style="padding: 4px 10px; z-index: 2;" onClick="getdiv()">Generate</button>
					<br/>
				</div>
			</div>
			</div>
			<div class="container">
			<div id="yearProcess" class="text-center"></div>
				<!-- summary start -->
				<table id="excel_table">
				<tr>
				<td>
				<div class="customtable db-ca-view reportTable reportTable4">
					<table id="month4_reportTable" border="1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th colspan="7" class="text-center maintable_hr" id="monthname">Yearly Summary Report <c:out value="${data.summaryyear}"/></th>
								<th colspan="6" class="text-center maintable_hr">Comparison</th>
							</tr>
							<tr>
								<th rowspan="2" class="text-center tax_de_hr">Tax Details</th>
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
								<td class="ind_formatss" id="b2b_R_CBW_Taxableamount"><c:out value="${data.b2b_R_CBW_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_R_CBW_Taxamount"><c:out value="${data.b2b_R_CBW_Taxamount}"/></td>
								<td rowspan="6" class="ind_formatss" id="GSTR3B_3a_Taxableamt"><c:out value="${data.GSTR3B_3a_Taxableamt}"/></td>
								<td rowspan="6" class="ind_formatss" id="GSTR3B_3a_Taxamt"><c:out value="${data.GSTR3B_3a_Taxamt}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_R_CBW_Taxableamount"><c:out value="${data.gstr1b2b_R_CBW_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_R_CBW_Taxamount"><c:out value="${data.gstr1b2b_R_CBW_Taxamount}"/></td>
								<td rowspan="6" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxableamount_Total1"><c:out value="${data.diffBook_GSTR3B_Taxableamount_Total1}"/></td>
								<td rowspan="6" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxamount_Total1"><c:out value="${data.diffBook_GSTR3B_Taxamount_Total1}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2B_Taxableamount"><c:out value="${data.diffBook_GSTR1_B2B_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2B_Taxamount"><c:out value="${data.diffBook_GSTR1_B2B_Taxamount}"/></td>
								<td rowspan="6" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxableamount_Total1"><c:out value="${data.diffGSTR1_GSTR3B_Taxableamount_Total1}"/></td>
								<td rowspan="6" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxamount_Total1"><c:out value="${data.diffGSTR1_GSTR3B_Taxamount_Total1}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">B2C (+/- Debit/Credit Notes & Adjustments.)</p></td>
								<td class="ind_formatss" id="b2cl_R_CBW_Taxableamount"><c:out value="${data.b2cl_R_CBW_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2cl_R_CBW_Taxamount"><c:out value="${data.b2cl_R_CBW_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2cl_R_CBW_Taxableamount"><c:out value="${data.gstr1b2cl_R_CBW_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2cl_R_CBW_Taxamount"><c:out value="${data.gstr1b2cl_R_CBW_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2C_Taxableamount"><c:out value="${data.diffBook_GSTR1_B2C_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2C_Taxamount"><c:out value="${data.diffBook_GSTR1_B2C_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Deemed exports</p></td>
								<td class="ind_formatss" id="b2b_CL_DE_Taxableamount"><c:out value="${data.b2b_CL_DE_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_CL_DE_Taxamount"><c:out value="${data.b2b_CL_DE_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_DE_Taxableamount"><c:out value="${data.gstr1b2b_CL_DE_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_DE_Taxamount"><c:out value="${data.gstr1b2b_CL_DE_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Deemed_Taxableamount"><c:out value="${data.diffBook_GSTR1_Deemed_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Deemed_Taxamount"><c:out value="${data.diffBook_GSTR1_Deemed_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Taxable under RCM</p></td>
								<td class="ind_formatss" id="rcm_Taxableamount"><c:out value="${data.rcm_Taxableamount}"/></td>
								<td class="ind_formatss" id="rcm_Taxamount"><c:out value="${data.rcm_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1rcm_Taxableamount"><c:out value="${data.gstr1rcm_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1rcm_Taxamount"><c:out value="${data.gstr1rcm_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss"id="diffBook_GSTR1_RCM_Taxableamount"><c:out value="${data.diffBook_GSTR1_RCM_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_RCM_Taxamount"><c:out value="${data.diffBook_GSTR1_RCM_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Advances received</p></td>
								<td class="ind_formatss" id="">0</td>
								<td class="ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="b-gstr1_td ind_formatss" id="">0</td class="b-gstr1_td">
								<td class="b-gstr1_td ind_formatss" id="">0</td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">'-Advances adjusted</p></td>
								<td class="ind_formatss" id="">0</td>
								<td class="ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="b-gstr1_td ind_formatss" id="">0</td>
								<td class="b-gstr1_td ind_formatss" id="">0</td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">SEZ sales-WP</p></td>
								<td class="ind_formatss" id="b2b_CL_SEWP_Taxableamount"><c:out value="${data.b2b_CL_SEWP_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_CL_SEWP_Taxamount"><c:out value="${data.b2b_CL_SEWP_Taxamount}"/></td>
								<td rowspan="4" class="ind_formatss" id="GSTR3B_3b_Taxableamt"><c:out value="${data.GSTR3B_3b_Taxableamt}"/></td>
								<td rowspan="4" class="ind_formatss" id="GSTR3B_3b_Taxamt"><c:out value="${data.GSTR3B_3b_Taxamt}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWP_Taxableamount"><c:out value="${data.gstr1b2b_CL_SEWP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWP_Taxamount"><c:out value="${data.gstr1b2b_CL_SEWP_Taxamount}"/></td>
								<td rowspan="4" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxableamount_Total2"><c:out value="${data.diffBook_GSTR3B_Taxableamount_Total2}"/></td>
								<td rowspan="4" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxamount_Total2"><c:out value="${data.diffBook_GSTR3B_Taxamount_Total2}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWP_Taxableamount"><c:out value="${data.diffBook_GSTR1_SEZWP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWP_Taxamount"><c:out value="${data.diffBook_GSTR1_SEZWP_Taxamount}"/></td>
								<td rowspan="4" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxableamount_Total2"><c:out value="${data.diffGSTR1_GSTR3B_Taxableamount_Total2}"/></td>
								<td rowspan="4" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxamount_Total2"><c:out value="${data.diffGSTR1_GSTR3B_Taxamount_Total2}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">SEZ sales-WOP</p></td>
								<td class="ind_formatss" id="b2b_CL_SEWOP_Taxableamount"><c:out value="${data.b2b_CL_SEWOP_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_CL_SEWOP_Taxamount"><c:out value="${data.b2b_CL_SEWOP_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWOP_Taxableamount"><c:out value="${data.gstr1b2b_CL_SEWOP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWOP_Taxamount"><c:out value="${data.gstr1b2b_CL_SEWOP_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWOP_Taxableamount"><c:out value="${data.diffBook_GSTR1_SEZWOP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWOP_Taxamount"><c:out value="${data.diffBook_GSTR1_SEZWOP_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Exports-WP</p></td>
								<td class="ind_formatss" id="exports_WP_Taxableamount"><c:out value="${data.exports_WP_Taxableamount}"/></td>
								<td class="ind_formatss" id="exports_WP_Taxamount"><c:out value="${data.exports_WP_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WP_Taxableamount"><c:out value="${data.gstr1exports_WP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WP_Taxamount"><c:out value="${data.gstr1exports_WP_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WP_Taxableamount"><c:out value="${data.diffBook_GSTR1_Export_WP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WP_Taxamount"><c:out value="${data.diffBook_GSTR1_Export_WP_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Exports-WOP</p></td>
								<td class="ind_formatss" id="exports_WOP_Taxableamount"><c:out value="${data.exports_WOP_Taxableamount}"/></td>
								<td class="ind_formatss" id="exports_WOP_Taxamount"><c:out value="${data.exports_WOP_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WOP_Taxableamount"><c:out value="${data.gstr1exports_WOP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WOP_Taxamount"><c:out value="${data.gstr1exports_WOP_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WOP_Taxableamount"><c:out value="${data.diffBook_GSTR1_Export_WOP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WOP_Taxamount"><c:out value="${data.diffBook_GSTR1_Export_WOP_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Exempted & Nil rated</p></td>
								<td class="ind_formatss" id="nilrated_Exempted_Taxableamount"><c:out value="${data.nilrated_Exempted_Taxableamount}"/></td>
								<td class="ind_formatss" id="nilrated_Exempted_Taxamount"><c:out value="${data.nilrated_Exempted_Taxamount}"/></td>
								<td class="ind_formatss" id="GSTR3B_3c_Taxableamt"><c:out value="${data.GSTR3B_3c_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_3c_Taxamt"><c:out value="${data.GSTR3B_3c_Taxamt}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1nilrated_Exempted_Taxableamount"><c:out value="${data.gstr1nilrated_Exempted_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1nilrated_Exempted_Taxamount"><c:out value="${data.gstr1nilrated_Exempted_Taxamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Nilrated_Exempted_Taxableamount"><c:out value="${data.diffBook_GSTR3B_Nilrated_Exempted_Taxableamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Nilrated_Exempted_Taxamount"><c:out value="${data.diffBook_GSTR3B_Nilrated_Exempted_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Nil_Taxableamount"><c:out value="${data.diffBook_GSTR1_Nil_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Nil_Taxamount"><c:out value="${data.diffBook_GSTR1_Nil_Taxamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount"><c:out value="${data.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Non-GST</p></td>
								<td class="ind_formatss" id="nongst_Taxableamount"><c:out value="${data.nongst_Taxableamount}"/></td>
								<td class="ind_formatss" id="nongst_Taxamount"><c:out value="${data.nongst_Taxamount}"/></td>
								<td class="ind_formatss" id="GSTR3B_3e_Taxableamt"><c:out value="${data.GSTR3B_3e_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_3e_Taxamt">0.00<%-- <c:out value="${data.GSTR3B_3d_Taxamt}"/> --%></td>
								<td class="gstr1_td ind_formatss" id="gstr1nongst_Taxableamount"><c:out value="${data.gstr1nongst_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1nongst_Taxamount"><c:out value="${data.gstr1nongst_Taxamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_NonGST_Taxableamount"><c:out value="${data.diffBook_GSTR3B_NonGST_Taxableamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_NonGST_Taxamount"><c:out value="${data.diffBook_GSTR3B_NonGST_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_NonGst_Taxableamount"><c:out value="${data.diffBook_GSTR1_NonGst_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_NonGst_Taxamount"><c:out value="${data.diffBook_GSTR1_NonGst_Taxamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_NonGST_Taxableamount"><c:out value="${data.diffGSTR1_GSTR3B_NonGST_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_NonGST_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_NonGST_Taxamount}"/></td>
							</tr>
							<tr>
								<td><h6 align="right" class="3bvs2ainfo">Total Taxable value</h6></td>
								<td class="ind_formatss" id="book_Total_Taxableamt1"><c:out value="${data.book_Total_Taxableamt1}"/></td>
								<td class="ind_formatss" id="book_Total_Taxamt1"><c:out value="${data.book_Total_Taxamt1}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxableamt1"><c:out value="${data.GSTR3B_Total_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxamt1"><c:out value="${data.GSTR3B_Total_Taxamt2}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxableamt1"><c:out value="${data.gstr1_Total_Taxableamt1}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxamt1"><c:out value="${data.gstr1_Total_Taxamt1}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxableamount1"><c:out value="${data.diffBook_GSTR3B_Total_Taxableamount1}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxamount1"><c:out value="${data.diffBook_GSTR3B_Total_Taxamount1}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxableamount1"><c:out value="${data.diffBook_GSTR1_Total_Taxableamount1}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxamount1"><c:out value="${data.diffBook_GSTR1_Total_Taxamount1}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxableamount1"><c:out value="${data.diffGSTR1_GSTR3B_Total_Taxableamount1}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxamount1"><c:out value="${data.diffGSTR1_GSTR3B_Total_Taxamount1}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Inter-state</p></td>
								<td class="ind_formatss" id="book_inter_Taxableamount"><c:out value="${data.book_inter_Taxableamount}"/></td>
								<td class="ind_formatss" id="book_inter_Taxamount"><c:out value="${data.book_inter_Taxamount}"/></td>
								<td rowspan="2" class="ind_formatss" id="GSTR3B_Total_Taxableamt"><c:out value="${data.GSTR3B_Total_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_IGST"><c:out value="${data.GSTR3B_IGST}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_inter_Taxableamount"><c:out value="${data.gstr1_inter_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_inter_Taxamount"><c:out value="${data.gstr1_inter_Taxamount}"/></td>
								<td rowspan="2" class="gst3b_td ind_formatss"id="diffBook_GSTR3B_Inter_Taxableamount"><c:out value="${data.diffBook_GSTR3B_Inter_Taxableamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Inter_Taxamount"><c:out value="${data.diffBook_GSTR3B_Inter_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Inter_Taxableamount"><c:out value="${data.diffBook_GSTR1_Inter_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Inter_Taxamount"><c:out value="${data.diffBook_GSTR1_Inter_Taxamount}"/></td>
								<td rowspan="2" class="ind_formatss" id="diffGSTR1_GSTR3B_Inter_Taxableamount"><c:out value="${data.diffGSTR1_GSTR3B_Inter_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Inter_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_Inter_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Intra-state</p></td>
								<td class="ind_formatss" id="book_intra_Taxableamount"><c:out value="${data.book_intra_Taxableamount}"/></td>
								<td class="ind_formatss" id="book_intra_Taxamount"><c:out value="${data.book_intra_Taxamount}"/></td>
								<td class="ind_formatss" id="GSTR3B_CGST_SGST"><c:out value="${data.GSTR3B_CGST_SGST}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_intra_Taxableamount"><c:out value="${data.gstr1_intra_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_intra_Taxamount"><c:out value="${data.gstr1_intra_Taxamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Intra_Taxamount"><c:out value="${data.diffBook_GSTR3B_Intra_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Intra_Taxableamount"><c:out value="${data.diffBook_GSTR1_Intra_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Intra_Taxamount"><c:out value="${data.diffBook_GSTR1_Intra_Taxamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Intra_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_Intra_Taxamount}"/></td>
							</tr>
							<tr>
								<td><h6 align="right" class="3bvs2ainfo">Total Taxable value</h6></td>
								<td class="ind_formatss" id="book_Total_Taxableamt2"><c:out value="${data.book_Total_Taxableamt2}"/></td>
								<td class="ind_formatss" id="book_Total_Taxamt2"><c:out value="${data.book_Total_Taxamt2}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxableamt2"><c:out value="${data.GSTR3B_Total_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxamt2"><c:out value="${data.GSTR3B_Total_Taxamt2}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxableamt2"><c:out value="${data.gstr1_Total_Taxableamt2}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxamt2"><c:out value="${data.gstr1_Total_Taxamt2}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxableamount2"><c:out value="${data.diffBook_GSTR3B_Total_Taxableamount2}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxamount2"><c:out value="${data.diffBook_GSTR3B_Total_Taxamount2}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxableamount2"><c:out value="${data.diffBook_GSTR1_Total_Taxableamount2}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxamount2"><c:out value="${data.diffBook_GSTR1_Total_Taxamount2}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxableamount2"><c:out value="${data.diffGSTR1_GSTR3B_Inter_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxamount2"><c:out value="${data.diffGSTR1_GSTR3B_Total_Taxamount2}"/></td>
							</tr>
						</tbody>
					</table>
				</div>
				</td>
				</tr>
				<!--  summary end -->
				<c:forEach items="${gstr3bvsgstr1}" var="data">
				<tr></tr>
				<tr>
				<td>
				<div class="customtable db-ca-view reportTable reportTable4">
					<table id="month4_reportTable" border="1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th colspan="7" class="text-center maintable_hr" id="monthname"><c:out value="${data.month}"/></th>
								<th colspan="6" class="text-center" >Comparision</th>
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
								<td class="ind_formatss" id="b2b_R_CBW_Taxableamount"><c:out value="${data.b2b_R_CBW_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_R_CBW_Taxamount"><c:out value="${data.b2b_R_CBW_Taxamount}"/></td>
								<td rowspan="6" class="ind_formatss" id="GSTR3B_3a_Taxableamt"><c:out value="${data.GSTR3B_3a_Taxableamt}"/></td>
								<td rowspan="6" class="ind_formatss" id="GSTR3B_3a_Taxamt"><c:out value="${data.GSTR3B_3a_Taxamt}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_R_CBW_Taxableamount"><c:out value="${data.gstr1b2b_R_CBW_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_R_CBW_Taxamount"><c:out value="${data.gstr1b2b_R_CBW_Taxamount}"/></td>
								<td rowspan="6" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxableamount_Total1"><c:out value="${data.diffBook_GSTR3B_Taxableamount_Total1}"/></td>
								<td rowspan="6" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxamount_Total1"><c:out value="${data.diffBook_GSTR3B_Taxamount_Total1}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2B_Taxableamount"><c:out value="${data.diffBook_GSTR1_B2B_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2B_Taxamount"><c:out value="${data.diffBook_GSTR1_B2B_Taxamount}"/></td>
								<td rowspan="6" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxableamount_Total1"><c:out value="${data.diffGSTR1_GSTR3B_Taxableamount_Total1}"/></td>
								<td rowspan="6" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxamount_Total1"><c:out value="${data.diffGSTR1_GSTR3B_Taxamount_Total1}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">B2C (+/- Debit/Credit Notes & Adjustments.)</p></td>
								<td class="ind_formatss" id="b2cl_R_CBW_Taxableamount"><c:out value="${data.b2cl_R_CBW_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2cl_R_CBW_Taxamount"><c:out value="${data.b2cl_R_CBW_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2cl_R_CBW_Taxableamount"><c:out value="${data.gstr1b2cl_R_CBW_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2cl_R_CBW_Taxamount"><c:out value="${data.gstr1b2cl_R_CBW_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2C_Taxableamount"><c:out value="${data.diffBook_GSTR1_B2C_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_B2C_Taxamount"><c:out value="${data.diffBook_GSTR1_B2C_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Deemed exports</p></td>
								<td class="ind_formatss" id="b2b_CL_DE_Taxableamount"><c:out value="${data.b2b_CL_DE_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_CL_DE_Taxamount"><c:out value="${data.b2b_CL_DE_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_DE_Taxableamount"><c:out value="${data.gstr1b2b_CL_DE_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_DE_Taxamount"><c:out value="${data.gstr1b2b_CL_DE_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Deemed_Taxableamount"><c:out value="${data.diffBook_GSTR1_Deemed_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Deemed_Taxamount"><c:out value="${data.diffBook_GSTR1_Deemed_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Taxable under RCM</p></td>
								<td class="ind_formatss" id="rcm_Taxableamount"><c:out value="${data.rcm_Taxableamount}"/></td>
								<td class="ind_formatss" id="rcm_Taxamount"><c:out value="${data.rcm_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1rcm_Taxableamount"><c:out value="${data.gstr1rcm_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1rcm_Taxamount"><c:out value="${data.gstr1rcm_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss"id="diffBook_GSTR1_RCM_Taxableamount"><c:out value="${data.diffBook_GSTR1_RCM_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_RCM_Taxamount"><c:out value="${data.diffBook_GSTR1_RCM_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Advances received</p></td>
								<td class="ind_formatss" id="">0</td>
								<td class="ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="b-gstr1_td ind_formatss" id="">0</td class="b-gstr1_td">
								<td class="b-gstr1_td ind_formatss" id="">0</td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">'-Advances adjusted</p></td>
								<td class="ind_formatss" id="">0</td>
								<td class="ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="gstr1_td ind_formatss" id="">0</td>
								<td class="b-gstr1_td ind_formatss" id="">0</td>
								<td class="b-gstr1_td ind_formatss" id="">0</td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">SEZ sales-WP</p></td>
								<td class="ind_formatss" id="b2b_CL_SEWP_Taxableamount"><c:out value="${data.b2b_CL_SEWP_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_CL_SEWP_Taxamount"><c:out value="${data.b2b_CL_SEWP_Taxamount}"/></td>
								<td rowspan="4" class="ind_formatss" id="GSTR3B_3b_Taxableamt"><c:out value="${data.GSTR3B_3b_Taxableamt}"/></td>
								<td rowspan="4" class="ind_formatss" id="GSTR3B_3b_Taxamt"><c:out value="${data.GSTR3B_3b_Taxamt}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWP_Taxableamount"><c:out value="${data.gstr1b2b_CL_SEWP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWP_Taxamount"><c:out value="${data.gstr1b2b_CL_SEWP_Taxamount}"/></td>
								<td rowspan="4" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxableamount_Total2"><c:out value="${data.diffBook_GSTR3B_Taxableamount_Total2}"/></td>
								<td rowspan="4" class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Taxamount_Total2"><c:out value="${data.diffBook_GSTR3B_Taxamount_Total2}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWP_Taxableamount"><c:out value="${data.diffBook_GSTR1_SEZWP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWP_Taxamount"><c:out value="${data.diffBook_GSTR1_SEZWP_Taxamount}"/></td>
								<td rowspan="4" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxableamount_Total2"><c:out value="${data.diffGSTR1_GSTR3B_Taxableamount_Total2}"/></td>
								<td rowspan="4" class="ind_formatss" id="diffGSTR1_GSTR3B_Taxamount_Total2"><c:out value="${data.diffGSTR1_GSTR3B_Taxamount_Total2}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">SEZ sales-WOP</p></td>
								<td class="ind_formatss" id="b2b_CL_SEWOP_Taxableamount"><c:out value="${data.b2b_CL_SEWOP_Taxableamount}"/></td>
								<td class="ind_formatss" id="b2b_CL_SEWOP_Taxamount"><c:out value="${data.b2b_CL_SEWOP_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWOP_Taxableamount"><c:out value="${data.gstr1b2b_CL_SEWOP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1b2b_CL_SEWOP_Taxamount"><c:out value="${data.gstr1b2b_CL_SEWOP_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWOP_Taxableamount"><c:out value="${data.diffBook_GSTR1_SEZWOP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_SEZWOP_Taxamount"><c:out value="${data.diffBook_GSTR1_SEZWOP_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Exports-WP</p></td>
								<td class="ind_formatss" id="exports_WP_Taxableamount"><c:out value="${data.exports_WP_Taxableamount}"/></td>
								<td class="ind_formatss" id="exports_WP_Taxamount"><c:out value="${data.exports_WP_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WP_Taxableamount"><c:out value="${data.gstr1exports_WP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WP_Taxamount"><c:out value="${data.gstr1exports_WP_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WP_Taxableamount"><c:out value="${data.diffBook_GSTR1_Export_WP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WP_Taxamount"><c:out value="${data.diffBook_GSTR1_Export_WP_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Exports-WOP</p></td>
								<td class="ind_formatss" id="exports_WOP_Taxableamount"><c:out value="${data.exports_WOP_Taxableamount}"/></td>
								<td class="ind_formatss" id="exports_WOP_Taxamount"><c:out value="${data.exports_WOP_Taxamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WOP_Taxableamount"><c:out value="${data.gstr1exports_WOP_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1exports_WOP_Taxamount"><c:out value="${data.gstr1exports_WOP_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WOP_Taxableamount"><c:out value="${data.diffBook_GSTR1_Export_WOP_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Export_WOP_Taxamount"><c:out value="${data.diffBook_GSTR1_Export_WOP_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Exempted & Nil rated</p></td>
								<td class="ind_formatss" id="nilrated_Exempted_Taxableamount"><c:out value="${data.nilrated_Exempted_Taxableamount}"/></td>
								<td class="ind_formatss" id="nilrated_Exempted_Taxamount"><c:out value="${data.nilrated_Exempted_Taxamount}"/></td>
								<td class="ind_formatss" id="GSTR3B_3c_Taxableamt"><c:out value="${data.GSTR3B_3c_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_3c_Taxamt"><c:out value="${data.GSTR3B_3c_Taxamt}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1nilrated_Exempted_Taxableamount"><c:out value="${data.gstr1nilrated_Exempted_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1nilrated_Exempted_Taxamount"><c:out value="${data.gstr1nilrated_Exempted_Taxamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Nilrated_Exempted_Taxableamount"><c:out value="${data.diffBook_GSTR3B_Nilrated_Exempted_Taxableamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Nilrated_Exempted_Taxamount"><c:out value="${data.diffBook_GSTR3B_Nilrated_Exempted_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Nil_Taxableamount"><c:out value="${data.diffBook_GSTR1_Nil_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Nil_Taxamount"><c:out value="${data.diffBook_GSTR1_Nil_Taxamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount"><c:out value="${data.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Non-GST</p></td>
								<td class="ind_formatss" id="nongst_Taxableamount"><c:out value="${data.nongst_Taxableamount}"/></td>
								<td class="ind_formatss" id="nongst_Taxamount"><c:out value="${data.nongst_Taxamount}"/></td>
								<td class="ind_formatss" id="GSTR3B_3d_Taxableamt"><c:out value="${data.GSTR3B_3e_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_3d_Taxamt">0.00<%-- <c:out value="${data.GSTR3B_3d_Taxamt}"/> --%></td>
								<td class="gstr1_td ind_formatss" id="gstr1nongst_Taxableamount"><c:out value="${data.gstr1nongst_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1nongst_Taxamount"><c:out value="${data.gstr1nongst_Taxamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_NonGST_Taxableamount"><c:out value="${data.diffBook_GSTR3B_NonGST_Taxableamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_NonGST_Taxamount"><c:out value="${data.diffBook_GSTR3B_NonGST_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_NonGst_Taxableamount"><c:out value="${data.diffBook_GSTR1_NonGst_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_NonGst_Taxamount"><c:out value="${data.diffBook_GSTR1_NonGst_Taxamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_NonGST_Taxableamount"><c:out value="${data.diffGSTR1_GSTR3B_NonGST_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_NonGST_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_NonGST_Taxamount}"/></td>
							</tr>
							<tr>
								<td><h6 align="right" class="3bvs2ainfo">Total Taxable value</h6></td>
								<td class="ind_formatss" id="book_Total_Taxableamt1"><c:out value="${data.book_Total_Taxableamt1}"/></td>
								<td class="ind_formatss" id="book_Total_Taxamt1"><c:out value="${data.book_Total_Taxamt1}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxableamt1"><c:out value="${data.GSTR3B_Total_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxamt1"><c:out value="${data.GSTR3B_Total_Taxamt2}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxableamt1"><c:out value="${data.gstr1_Total_Taxableamt1}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxamt1"><c:out value="${data.gstr1_Total_Taxamt1}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxableamount1"><c:out value="${data.diffBook_GSTR3B_Total_Taxableamount1}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxamount1"><c:out value="${data.diffBook_GSTR3B_Total_Taxamount1}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxableamount1"><c:out value="${data.diffBook_GSTR1_Total_Taxableamount1}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxamount1"><c:out value="${data.diffBook_GSTR1_Total_Taxamount1}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxableamount1"><c:out value="${data.diffGSTR1_GSTR3B_Total_Taxableamount1}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxamount1"><c:out value="${data.diffGSTR1_GSTR3B_Total_Taxamount1}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Inter-state</p></td>
								<td class="ind_formatss" id="book_inter_Taxableamount"><c:out value="${data.book_inter_Taxableamount}"/></td>
								<td class="ind_formatss" id="book_inter_Taxamount"><c:out value="${data.book_inter_Taxamount}"/></td>
								<td rowspan="2" class="ind_formatss" id="GSTR3B_Total_Taxableamt"><c:out value="${data.GSTR3B_Total_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_IGST"><c:out value="${data.GSTR3B_IGST}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_inter_Taxableamount"><c:out value="${data.gstr1_inter_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_inter_Taxamount"><c:out value="${data.gstr1_inter_Taxamount}"/></td>
								<td rowspan="2" class="gst3b_td ind_formatss"id="diffBook_GSTR3B_Inter_Taxableamount"><c:out value="${data.diffBook_GSTR3B_Inter_Taxableamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Inter_Taxamount"><c:out value="${data.diffBook_GSTR3B_Inter_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Inter_Taxableamount"><c:out value="${data.diffBook_GSTR1_Inter_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Inter_Taxamount"><c:out value="${data.diffBook_GSTR1_Inter_Taxamount}"/></td>
								<td rowspan="2" class="ind_formatss" id="diffGSTR1_GSTR3B_Inter_Taxableamount"><c:out value="${data.diffGSTR1_GSTR3B_Inter_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Inter_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_Inter_Taxamount}"/></td>
							</tr>
							<tr>
								<td><p align="right" class="3bvs2ainfo">Intra-state</p></td>
								<td class="ind_formatss" id="book_intra_Taxableamount"><c:out value="${data.book_intra_Taxableamount}"/></td>
								<td class="ind_formatss" id="book_intra_Taxamount"><c:out value="${data.book_intra_Taxamount}"/></td>
								<td class="ind_formatss" id="GSTR3B_CGST_SGST"><c:out value="${data.GSTR3B_CGST_SGST}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_intra_Taxableamount"><c:out value="${data.gstr1_intra_Taxableamount}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_intra_Taxamount"><c:out value="${data.gstr1_intra_Taxamount}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Intra_Taxamount"><c:out value="${data.diffBook_GSTR3B_Intra_Taxamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Intra_Taxableamount"><c:out value="${data.diffBook_GSTR1_Intra_Taxableamount}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Intra_Taxamount"><c:out value="${data.diffBook_GSTR1_Intra_Taxamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Intra_Taxamount"><c:out value="${data.diffGSTR1_GSTR3B_Intra_Taxamount}"/></td>
							</tr>
							<tr>
								<td><h6 align="right" class="3bvs2ainfo">Total Taxable value</h6></td>
								<td class="ind_formatss" id="book_Total_Taxableamt2"><c:out value="${data.book_Total_Taxableamt2}"/></td>
								<td class="ind_formatss" id="book_Total_Taxamt2"><c:out value="${data.book_Total_Taxamt2}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxableamt2"><c:out value="${data.GSTR3B_Total_Taxableamt}"/></td>
								<td class="ind_formatss" id="GSTR3B_Total_Taxamt2"><c:out value="${data.GSTR3B_Total_Taxamt2}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxableamt2"><c:out value="${data.gstr1_Total_Taxableamt2}"/></td>
								<td class="gstr1_td ind_formatss" id="gstr1_Total_Taxamt2"><c:out value="${data.gstr1_Total_Taxamt2}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxableamount2"><c:out value="${data.diffBook_GSTR3B_Total_Taxableamount2}"/></td>
								<td class="gst3b_td ind_formatss" id="diffBook_GSTR3B_Total_Taxamount2"><c:out value="${data.diffBook_GSTR3B_Total_Taxamount2}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxableamount2"><c:out value="${data.diffBook_GSTR1_Total_Taxableamount2}"/></td>
								<td class="b-gstr1_td ind_formatss" id="diffBook_GSTR1_Total_Taxamount2"><c:out value="${data.diffBook_GSTR1_Total_Taxamount2}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxableamount2"><c:out value="${data.diffGSTR1_GSTR3B_Inter_Taxableamount}"/></td>
								<td class="ind_formatss" id="diffGSTR1_GSTR3B_Total_Taxamount2"><c:out value="${data.diffGSTR1_GSTR3B_Total_Taxamount2}"/></td>
							</tr>
						</tbody>
					</table>
				</div>
				</td>
				</tr>
			</c:forEach>
			</table>		
		</div>
	</div>
	<div class="modal fade" id="report1helpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
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
</body>
</html>