<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Multi Month Summary</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_3bvs2a.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
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
	function getdiv() {
		var date=$('#monthly').val().split("-");
		ajaxFunction(date[0],date[1]);
	}
	$(document).ready(function(){
		var month=new Date().getMonth()+1;
		var year=new Date().getFullYear();
		if(month<10){
			$('#monthly').val("0"+month+"-"+year);		
		}else{
			$('#monthly').val(month+"-"+year);
		}
		$('#monthly').datepicker({
			autoclose : true,
			viewMode : 1,
			minViewMode : 1,
			format : 'mm-yyyy'
		});
	});
	function exportF(elem) {
		  var table = document.getElementById("month4_reportTable");
		  var html = table.outerHTML;
		  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
		  elem.setAttribute("href", url);
		  elem.setAttribute("download", "PURCHASES_VS_GSTR3B_VS_GSTR2A.xls"); // Choose the file name
		  return false;
	}
</script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
  <div class="breadcrumbwrap">
  <div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">PURCHASES VS GSTR3B VS GSTR2A Report</li>
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
			<h4>PURCHASES VS GSTR 3B VS GSTR2A Report</h4>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#report3bvs2ahelpGuideModal"> Help To Read This Report
			
			<div class="dropdown-content report3BVS2A mt-1"> 
			<span class="arrow-up"></span>
			<ul class="pl-2">
                <li> <span class="steptext-desc"><b>1.  Purchases </b><span class="colon" style="margin-left: 11px;">:</span><span class="pl-2"> All the Purchase Invoices from your PurchaseRegister/Books</span></span></li>
				<li><span class="steptext-desc"><b>2. GSTR3B </b><span class="colon" style="margin-left: 24px;">:</span><span class="pl-2">Filed Data downloaded from GSTIN</span></span></li>
				<li> <span class="steptext-desc"><b>3. GSTR2A </b><span class="colon" style="margin-left: 24px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>                
		 		
		 </ul> 	
		 <b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between Purchases and GSTR3B (Purchases Data - (minus) GSTR3B Data)</span><span class="pl-2" style="display: block;">Differences between Purchases and GSTR2A (Purchases Data - (minus) GSTR2A Data)</span><span class="pl-2" style="display: block;">Differences between GSTR3B and GSTR2A (GSTR3B - (minus) GSTR2A Data)</span>
			
			</div>
			</div><span class="helpbtn" style="position: relative;top:2px;"><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
				<lable for="monthly" class="mr-1">
				<a id="downloadLink" class="excel_btn" onclick="exportF(this)" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
				<b>Select Month: </b></lable>
				<div class="datetimetxt datetime-wrap pull-right">
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
							<th colspan="7" class="text-center month_header" id="month_name"></th>						  
						</tr>
						<tr>
							<th class="text-center tax_hr">Tax Details</th>
							<th class="text-center books_header">Purchases</th>
							<th class="text-center">GSTR3B</th>
							<th class="text-center">GSTR2A</th>
							<th class="text-center" data-toggle="tooltip" title="Difference Between Purchases and GSTR3B (Purchases-GSTR3B)">Purchases-GSTR3B</th>
							<th class="text-center" data-toggle="tooltip" title="Difference Between Purchases and GSTR2A (Purchases-GSTR2A)">Purchases-GSTR2A</th>
							<th class="text-center" data-toggle="tooltip" title="Difference Between GSTR3B and GSTR2A (GSTR3B-GSTR2A)">GSTR3B-GSTR2A</th>
						</tr>
					</thead>
					<tbody id="yeartotoalreport">
						<tr>
							<td><h6 align="right" class="3bvs2ainfo">IGST - Import of goods</h6> </td>
							<td id="importGoods_IGST_GSTR2">0</td><td id="importGoods_IGST_GSTR3B">0</td><td id="importGoods_IGST_GSTR2A">0</td><td id="diffImportGoods_IGST_GSTR2_GSTR3B">0</td><td id="diffImportGoods_IGST_GSTR2_GSTR2A">0</td><td id="diffImportGoods_IGST_GSTR3B_GSTR2A">0</td>
			        	</tr>
						<tr>
							<td><h6 align="right" class="3bvs2ainfo">IGST - Import of Services</h6> </td>
							<td id="importServices_IGST_GSTR2">0</td><td id="importServices_IGST_GSTR3B">0</td><td style="text-align:center;">NA</td><td id="diffImportServices_IGST_GSTR2_GSTR3B">0</td><td id="diffImportServices_IGST_GSTR2_GSTR2A">0</td><td id="diffImportServices_IGST_GSTR3B_GSTR2A">0</td>
						</tr>
						<tr class="rcminfo">
							<td class="dropdown rcmhead">
							<h6 align="right" class="3bvs2ainfo">RCM (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6> 
							<div class="dropdown-content rcmdata" style="display:none">
									<span class="arrow-top"></span>
									<table class="inner_table" id="inner_table">
										<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">RCM Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
										<tbody>
											<tr><td>IGST</td><td id="rcm_IGST_GSTR2">0</td><td id="rcm_IGST_GSTR3B">0</td><td id="rcm_IGST_GSTR2A">0</td><td id="diffRCM_IGST_GSTR2_GSTR3B">0</td><td id="diffRCM_IGST_GSTR2_GSTR2A">0</td><td id="diffRCM_IGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>CGST</td><td id="rcm_CGST_GSTR2">0</td><td id="rcm_CGST_GSTR3B">0</td><td id="rcm_CGST_GSTR2A">0</td><td id="diffRCM_CGST_GSTR2_GSTR3B">0</td><td id="diffRCM_CGST_GSTR2_GSTR2A">0</td><td id="diffRCM_CGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>SGST</td><td id="rcm_SGST_GSTR2">0</td><td id="rcm_SGST_GSTR3B">0</td><td id="rcm_SGST_GSTR2A">0</td><td id="diffRCM_SGST_GSTR2_GSTR3B">0</td><td id="diffRCM_SGST_GSTR2_GSTR2A">0</td><td id="diffRCM_SGST_GSTR3B_GSTR2A">0</td></tr>
										</tbody>
									</table>
								</div>
							</td>
							<td id="rcm_GSTR2">0</td>
							<td id="rcm_GSTR3B">0</td>
							<td id="rcm_GSTR2A">0</td>
							<td id="diffRCM_GSTR2_GSTR3B">0</td>
							<td id="diffRCM_GSTR2_GSTR2A">0</td>
							<td id="diffRCM_GSTR3B_GSTR2A">0</td>
						</tr>
						<tr class="isdinfo">
							<td class="dropdown isdhead">
							<h6 align="right" class="3bvs2ainfo">ISD (+) <br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6>
							<div class="dropdown-content isddata" style="display:none">
									<span class="arrow-top"></span>
									<table class="inner_table">
										<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">ISD Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
										<tbody>
											<tr><td>IGST</td><td id="isd_IGST_GSTR2">0</td><td id="isd_IGST_GSTR3B">0</td><td id="isd_IGST_GSTR2A">0</td><td id="diffISD_IGST_GSTR2_GSTR3B">0</td><td id="diffISD_IGST_GSTR2_GSTR2A">0</td><td id="diffISD_IGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>CGST</td><td id="isd_CGST_GSTR2">0</td><td id="isd_CGST_GSTR3B">0</td><td id="isd_CGST_GSTR2A">0</td><td id="diffISD_CGST_GSTR2_GSTR3B">0</td><td id="diffISD_CGST_GSTR2_GSTR2A">0</td><td id="diffISD_CGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>SGST</td><td id="isd_SGST_GSTR2">0</td><td id="isd_SGST_GSTR3B">0</td><td id="isd_SGST_GSTR2A">0</td><td id="diffISD_SGST_GSTR2_GSTR3B">0</td><td id="diffISD_SGST_GSTR2_GSTR2A">0</td><td id="diffISD_SGST_GSTR3B_GSTR2A">0</td></tr>
										</tbody>
									</table>
								</div>
							</td>
							<td  id="isd_GSTR2">0</td>
							<td  id="isd_GSTR3B">0</td>
							<td  id="isd_GSTR2A">0</td>
							<td  id="diffISD_GSTR2_GSTR3B">0</td>
							<td  id="diffISD_GSTR2_GSTR2A">0</td>
							<td  id="diffISD_GSTR3B_GSTR2A">0</td>
			            </tr>
						<tr class="eligibleinfo">
							<td class="dropdown eligiblehead">
							<h6 align="right" class="3bvs2ainfo">Eligible (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6> 
							<div class="dropdown-content eligibledata" style="display:none">
									<span class="arrow-bottom"></span>
									<table class="inner_table">
										<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">Eligible Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
										<tbody>
											<tr><td>IGST</td><td id="eligible_IGST_GSTR2">0</td><td id="eligible_IGST_GSTR3B">0</td><td id="eligible_IGST_GSTR2A">0</td><td id="diffEligible_IGST_GSTR2_GSTR3B">0</td><td id="diffEligible_IGST_GSTR2_GSTR2A">0</td><td id="diffEligible_IGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>CGST</td><td id="eligible_CGST_GSTR2">0</td><td id="eligible_CGST_GSTR3B">0</td><td id="eligible_CGST_GSTR2A">0</td><td id="diffEligible_SGST_GSTR2_GSTR3B">0</td><td id="diffEligible_CGST_GSTR2_GSTR2A">0</td><td id="diffEligible_CGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>SGST</td><td id="eligible_SGST_GSTR2">0</td><td id="eligible_SGST_GSTR3B">0</td><td id="eligible_SGST_GSTR2A">0</td><td id="diffEligible_CGST_GSTR2_GSTR3B">0</td><td id="diffEligible_SGST_GSTR2_GSTR2A">0</td><td id="diffEligible_SGST_GSTR3B_GSTR2A">0</td></tr>
										</tbody>
									</table>
								</div>
							</td>
							<td id="eligible_GSTR2">0</td>
							<td  id="eligible_GSTR3B">0</td>
							<td  id="eligible_GSTR2A">0</td>
							<td  id="diffEligible_GSTR2_GSTR3B">0</td>
							<td  id="diffEligible_GSTR2_GSTR2A">0</td>
							<td  id="diffEligible_GSTR3B_GSTR2A">0</td>
			            </tr>
			            <tr class="ineligibleinfo">
							<td class="dropdown ineligiblehead">
							<h6 align="right" class="3bvs2ainfo">InEligible (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6>
							<div class="dropdown-content ineligibledata" style="display:none">
									<span class="arrow-bottom"></span>
									<table class="inner_table">
										<thead><tr><th style="width:13%;background-color: #7d3938ba!important;color:#fff!important">InEligible Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
										<tbody>
											<tr><td>IGST</td><td id="ineligible_IGST_GSTR2">0</td><td id="ineligible_IGST_GSTR3B">0</td><td id="ineligible_IGST_GSTR2A">0</td><td id="diffIneligible_IGST_GSTR2_GSTR3B">0</td><td id="diffIneligible_IGST_GSTR2_GSTR2A">0</td><td id="diffIneligible_IGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>CGST</td><td id="ineligible_CGST_GSTR2">0</td><td id="ineligible_CGST_GSTR3B">0</td><td id="ineligible_CGST_GSTR2A">0</td><td id="diffIneligible_CGST_GSTR2_GSTR3B">0</td><td id="diffIneligible_CGST_GSTR2_GSTR2A">0</td><td id="diffIneligible_CGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>SGST</td><td id="ineligible_SGST_GSTR2">0</td><td id="ineligible_SGST_GSTR3B">0</td><td id="ineligible_SGST_GSTR2A">0</td><td id="diffIneligible_SGST_GSTR2_GSTR3B">0</td><td id="diffIneligible_SGST_GSTR2_GSTR2A">0</td><td id="diffIneligible_SGST_GSTR3B_GSTR2A">0</td></tr>
										</tbody>
									</table>
								</div>
							 </td>
							<td  id="ineligible_GSTR2">0</td>
							<td  id="ineligible_GSTR3B">0</td>
							<td  id="ineligible_GSTR2A">0</td>
							<td  id="diffIneligible_GSTR2_GSTR3B">0</td>
							<td  id="diffIneligible_GSTR2_GSTR2A">0</td>
							<td  id="diffIneligible_GSTR3B_GSTR2A">0</td>
			            </tr>
			             <tr class="isdinfo">
							<td  class="dropdown credithead">
							<h6 align="right" class="3bvs2ainfo">Credit reversed (+)<br/><span style="font-size: 13px;color:chocolate">please mouseover to view full details</span></h6>
							<div class="dropdown-content creditdata">
									<span class="arrow-bottom"></span>
									<table class="inner_table">
										<thead><tr><th style="width:15%;background-color: #7d3938ba!important;color:#fff!important">Credit Reversed Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
										<tbody>
											<tr><td>IGST</td><td id="credit_Reversed_IGST_GSTR2">0</td><td id="credit_Reversed_IGST_GSTR3B">0</td><td style="text-align:center;">NA</td><td id="diffCredit_Reversed_IGST_GSTR2_GSTR3B">0</td><td id="diffCredit_Reversed_IGST_GSTR2_GSTR2A">0</td><td id="diffCredit_Reversed_IGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>CGST</td><td id="credit_Reversed_CGST_GSTR2">0</td><td id="credit_Reversed_CGST_GSTR3B">0</td><td style="text-align:center;">NA</td><td id="diffCredit_Reversed_CGST_GSTR2_GSTR3B">0</td><td id="diffCredit_Reversed_CGST_GSTR2_GSTR2A">0</td><td id="diffCredit_Reversed_CGST_GSTR3B_GSTR2A">0</td></tr>
											<tr><td>SGST</td><td id="credit_Reversed_SGST_GSTR2">0</td><td id="credit_Reversed_SGST_GSTR3B">0</td><td style="text-align:center;">NA</td><td id="diffCredit_Reversed_SGST_GSTR2_GSTR3B">0</td><td id="diffCredit_Reversed_SGST_GSTR2_GSTR2A">0</td><td id="diffCredit_Reversed_SGST_GSTR3B_GSTR2A">0</td></tr>
										</tbody>
									</table>
								</div>
							</td>
							<td id="credit_Reversed_GSTR2">0</td>
							<td id="credit_Reversed_GSTR3B">0</td>
							<td style="text-align:center;">NA</td>
							<td id="diffCredit_Reversed_GSTR2_GSTR3B">0</td>
							<td id="diffCredit_Reversed_GSTR2_GSTR2A">0</td>
							<td id="diffCredit_Reversed_GSTR3B_GSTR2A">0</td>
			             </tr>
					</tbody>
				</table>
			</div>
		</div>
		</div>
		<div class="modal fade" id="report3bvs2ahelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Help To Read This Report </h3>
        </div>
        <div class=" p-2 steptext-wrap">
		<ul class="pl-2">
                <li> <span class="steptext-desc"><b>1.  Purchases </b><span class="colon" style="margin-left: 6px;">:</span><span class="pl-2"> All the Purchase Invoices from your PurchaseRegister/Books</span></span></li>
				<li><span class="steptext-desc"><b>2. GSTR3B </b><span class="colon" style="margin-left: 19px;">:</span><span class="pl-2">Filed Data downloaded from GSTIN</span></span></li>
				<li> <span class="steptext-desc"><b>3. GSTR2A </b><span class="colon" style="margin-left: 19px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>                
		 		
		 </ul> 	
		 <b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between Purchases and GSTR3B (Purchases Data - (minus) GSTR3B Data)</span><span class="pl-2" style="display: block;">Differences between Purchases and GSTR2A (Purchases Data - (minus) GSTR2A Data)</span><span class="pl-2" style="display: block;">Differences between GSTR3B and GSTR2A (GSTR3B - (minus) GSTR2A Data)</span>
         </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
$(document).ready(function(){
	var month=new Date().getMonth()+1;
	var year=new Date().getFullYear();
	ajaxFunction(month,year);
	$( ".helpicon" ).hover(function() {$('.report3BVS2A ').show();
	}, function() {$('.report3BVS2A ').hide();
	});
	$( ".rcmhead" ).hover(function() {$('.rcmdata').show();
	}, function() {$('.rcmdata').hide();
	});
	$( ".isdhead" ).hover(function() {$('.isddata').show();
	}, function() {$('.isddata').hide();
	});
	$( ".eligiblehead" ).hover(function() {$('.eligibledata').show();
	}, function() {$('.eligibledata').hide();
	});
	$( ".ineligiblehead" ).hover(function() {$('.ineligibledata').show();
	}, function() {$('.ineligibledata').hide();
	});
	$( ".credithead" ).hover(function() {$('.creditdata').show();
	}, function() {$('.creditdata').hide();
	});
	
});
function ajaxFunction(month,year){
	$.ajax({
		url: "${contextPath}/compare_gstr3b_vs_gstr2amonthly/${client.id}/"+month+"/"+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
 		beforeSend: function () {
	        //$('#yearProcess').text('Processing...');
	    },
		success : function(response) {
			responedata(response);
		},error: function(err){
		}
	});
}
function responedata(res){
	
	
	$('#month_name').html(res.month);
	$('#importGoods_IGST_GSTR3B').html(formatNumber(res.importGoods_IGST_GSTR3B.toFixed(2)));
	$('#importServices_IGST_GSTR3B').html(formatNumber(res.importServices_IGST_GSTR3B.toFixed(2)));
	$('#rcm_GSTR3B').html(formatNumber(res.rcm_GSTR3B.toFixed(2)));
	$('#rcm_CGST_GSTR3B').html(formatNumber(res.rcm_CGST_GSTR3B.toFixed(2)));
	$('#rcm_SGST_GSTR3B').html(formatNumber(res.rcm_SGST_GSTR3B.toFixed(2)));
	$('#rcm_IGST_GSTR3B').html(formatNumber(res.rcm_IGST_GSTR3B.toFixed(2)));
	$('#isd_GSTR3B').html(formatNumber(res.isd_GSTR3B.toFixed(2)));
	$('#isd_CGST_GSTR3B').html(formatNumber(res.isd_CGST_GSTR3B.toFixed(2)));
	$('#isd_SGST_GSTR3B').html(formatNumber(res.isd_SGST_GSTR3B.toFixed(2)));
	$('#isd_IGST_GSTR3B').html(formatNumber(res.isd_IGST_GSTR3B.toFixed(2)));
	$('#eligible_CGST_GSTR3B').html(formatNumber(res.eligible_CGST_GSTR3B.toFixed(2)));
	$('#eligible_SGST_GSTR3B').html(formatNumber(res.eligible_SGST_GSTR3B.toFixed(2)));
	$('#eligible_IGST_GSTR3B').html(formatNumber(res.eligible_IGST_GSTR3B.toFixed(2)));
	$('#ineligible_CGST_GSTR3B').html(formatNumber(res.ineligible_CGST_GSTR3B.toFixed(2)));
	$('#ineligible_SGST_GSTR3B').html(formatNumber(res.ineligible_SGST_GSTR3B.toFixed(2)));
	$('#ineligible_IGST_GSTR3B').html(formatNumber(res.ineligible_IGST_GSTR3B.toFixed(2)));
	$('#credit_Reversed_GSTR3B').html(formatNumber(res.credit_Reversed_GSTR3B.toFixed(2)));
	$('#credit_Reversed_CGST_GSTR3B').html(formatNumber(res.credit_Reversed_CGST_GSTR3B.toFixed(2)));
	$('#credit_Reversed_SGST_GSTR3B').html(formatNumber(res.credit_Reversed_SGST_GSTR3B.toFixed(2)));
	$('#credit_Reversed_IGST_GSTR3B').html(formatNumber(res.credit_Reversed_IGST_GSTR3B.toFixed(2)));
	$('#ineligible_GSTR3B').html(formatNumber(res.ineligible_GSTR3B.toFixed(2)));
	$('#eligible_GSTR3B').html(formatNumber(res.eligible_GSTR3B.toFixed(2)));
	$('#rcm_GSTR2').html(formatNumber(res.rcm_GSTR2.toFixed(2)));
	$('#isd_GSTR2').html(formatNumber(res.isd_GSTR2.toFixed(2)));
	$('#rcm_IGST_GSTR2').html(formatNumber(res.rcm_IGST_GSTR2.toFixed(2)));
	$('#rcm_CGST_GSTR2').html(formatNumber(res.rcm_CGST_GSTR2.toFixed(2)));
	$('#rcm_SGST_GSTR2').html(formatNumber(res.rcm_SGST_GSTR2.toFixed(2)));
	$('#isd_IGST_GSTR2').html(formatNumber(res.isd_IGST_GSTR2.toFixed(2)));
	$('#isd_CGST_GSTR2').html(formatNumber(res.isd_CGST_GSTR2.toFixed(2)));
	$('#isd_SGST_GSTR2').html(formatNumber(res.isd_SGST_GSTR2.toFixed(2)));
	$('#importGoods_IGST_GSTR2').html(formatNumber(res.importGoods_IGST_GSTR2.toFixed(2)));
	$('#importServices_IGST_GSTR2').html(formatNumber(res.importServices_IGST_GSTR2.toFixed(2)));
	$('#eligible_GSTR2').html(formatNumber(res.eligible_GSTR2.toFixed(2)));
	$('#eligible_CGST_GSTR2').html(formatNumber(res.eligible_CGST_GSTR2.toFixed(2)));
	$('#eligible_SGST_GSTR2').html(formatNumber(res.eligible_SGST_GSTR2.toFixed(2)));
	$('#eligible_IGST_GSTR2').html(formatNumber(res.eligible_IGST_GSTR2.toFixed(2)));
	$('#ineligible_GSTR2').html(formatNumber(res.ineligible_GSTR2.toFixed(2)));
	$('#ineligible_CGST_GSTR2').html(formatNumber(res.ineligible_CGST_GSTR2.toFixed(2)));
	$('#ineligible_SGST_GSTR2').html(formatNumber(res.ineligible_SGST_GSTR2.toFixed(2)));
	$('#ineligible_IGST_GSTR2').html(formatNumber(res.ineligible_IGST_GSTR2.toFixed(2)));
	$('#credit_Reversed_GSTR2').html(formatNumber(res.credit_Reversed_GSTR2.toFixed(2)));
	$('#credit_Reversed_CGST_GSTR2').html(formatNumber(res.credit_Reversed_CGST_GSTR2.toFixed(2)));
	$('#credit_Reversed_SGST_GSTR2').html(formatNumber(res.credit_Reversed_SGST_GSTR2.toFixed(2)));
	$('#credit_Reversed_IGST_GSTR2').html(formatNumber(res.credit_Reversed_IGST_GSTR2.toFixed(2)));
	$('#diffImportGoods_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffImportGoods_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffImportServices_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffImportServices_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffRCM_GSTR2_GSTR3B').html(formatNumber(res.diffRCM_GSTR2_GSTR3B.toFixed(2)));
	$('#diffRCM_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffRCM_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffRCM_CGST_GSTR2_GSTR3B').html(formatNumber(res.diffRCM_CGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffRCM_SGST_GSTR2_GSTR3B').html(formatNumber(res.diffRCM_SGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffISD_GSTR2_GSTR3B').html(formatNumber(res.diffISD_GSTR2_GSTR3B.toFixed(2)));
	$('#diffISD_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffISD_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffISD_CGST_GSTR2_GSTR3B').html(formatNumber(res.diffISD_CGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffISD_SGST_GSTR2_GSTR3B').html(formatNumber(res.diffISD_SGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_GSTR2_GSTR3B').html(formatNumber(res.diffCredit_Reversed_GSTR2_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffCredit_Reversed_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_CGST_GSTR2_GSTR3B').html(formatNumber(res.diffCredit_Reversed_CGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_SGST_GSTR2_GSTR3B').html(formatNumber(res.diffCredit_Reversed_SGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_SGST_GSTR2_GSTR2A').html(formatNumber(res.credit_Reversed_SGST_GSTR2.toFixed(2)));
	$('#diffCredit_Reversed_SGST_GSTR3B_GSTR2A').html(formatNumber(res.credit_Reversed_SGST_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_IGST_GSTR2_GSTR2A').html(formatNumber(res.credit_Reversed_IGST_GSTR2.toFixed(2)));
	$('#diffCredit_Reversed_IGST_GSTR3B_GSTR2A').html(formatNumber(res.credit_Reversed_IGST_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_CGST_GSTR2_GSTR2A').html(formatNumber(res.credit_Reversed_CGST_GSTR2.toFixed(2)));
	$('#diffCredit_Reversed_CGST_GSTR3B_GSTR2A').html(formatNumber(res.credit_Reversed_CGST_GSTR3B.toFixed(2)));
	$('#diffImportGoods_IGST_GSTR2_GSTR2A').html(formatNumber(res.diffImportGoods_IGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffImportGoods_IGST_GSTR3B_GSTR2A').html(formatNumber(res.diffImportGoods_IGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffImportServices_IGST_GSTR2_GSTR2A').html(formatNumber(res.importServices_IGST_GSTR2.toFixed(2)));
	$('#diffImportServices_IGST_GSTR3B_GSTR2A').html(formatNumber(res.importServices_IGST_GSTR3B.toFixed(2)));
	$('#diffCredit_Reversed_GSTR2_GSTR2A').html(formatNumber(res.credit_Reversed_GSTR2.toFixed(2)));
	$('#diffCredit_Reversed_GSTR3B_GSTR2A').html(formatNumber(res.credit_Reversed_GSTR3B.toFixed(2)));
	
	$('#diffEligible_GSTR2_GSTR3B').html(formatNumber(res.diffEligible_GSTR2_GSTR3B.toFixed(2)));
	$('#diffEligible_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffEligible_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffEligible_CGST_GSTR2_GSTR3B').html(formatNumber(res.diffEligible_CGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffEligible_SGST_GSTR2_GSTR3B').html(formatNumber(res.diffEligible_SGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffIneligible_GSTR2_GSTR3B').html(formatNumber(res.diffIneligible_GSTR2_GSTR3B.toFixed(2)));
	$('#diffIneligible_IGST_GSTR2_GSTR3B').html(formatNumber(res.diffIneligible_IGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffIneligible_CGST_GSTR2_GSTR3B').html(formatNumber(res.diffIneligible_CGST_GSTR2_GSTR3B.toFixed(2)));
	$('#diffIneligible_SGST_GSTR2_GSTR3B').html(formatNumber(res.diffIneligible_SGST_GSTR2_GSTR3B.toFixed(2)));
	$('#rcm_GSTR2A').html(formatNumber(res.rcm_GSTR2A.toFixed(2)));
	$('#isd_GSTR2A').html(formatNumber(res.isd_GSTR2A.toFixed(2)));
	$('#rcm_IGST_GSTR2A').html(formatNumber(res.rcm_IGST_GSTR2A.toFixed(2)));
	$('#rcm_CGST_GSTR2A').html(formatNumber(res.rcm_CGST_GSTR2A.toFixed(2)));
	$('#rcm_SGST_GSTR2A').html(formatNumber(res.rcm_SGST_GSTR2A.toFixed(2)));
	$('#isd_IGST_GSTR2A').html(formatNumber(res.isd_IGST_GSTR2A.toFixed(2)));
	$('#isd_CGST_GSTR2A').html(formatNumber(res.isd_CGST_GSTR2A.toFixed(2)));
	$('#isd_SGST_GSTR2A').html(formatNumber(res.isd_SGST_GSTR2A.toFixed(2)));
	$('#importGoods_IGST_GSTR2A').html(formatNumber(res.importGoods_IGST_GSTR2A.toFixed(2)));
	$('#importServices_IGST_GSTR2A').html(formatNumber(res.importServices_IGST_GSTR2A.toFixed(2)));
	$('#eligible_GSTR2A').html(formatNumber(res.eligible_GSTR2A.toFixed(2)));
	$('#eligible_CGST_GSTR2A').html(formatNumber(res.eligible_CGST_GSTR2A.toFixed(2)));
	$('#eligible_SGST_GSTR2A').html(formatNumber(res.eligible_SGST_GSTR2A.toFixed(2)));
	$('#eligible_IGST_GSTR2A').html(formatNumber(res.eligible_IGST_GSTR2A.toFixed(2)));
	$('#ineligible_GSTR2A').html(formatNumber(res.ineligible_GSTR2A.toFixed(2)));
	$('#ineligible_CGST_GSTR2A').html(formatNumber(res.ineligible_CGST_GSTR2A.toFixed(2)));
	$('#ineligible_SGST_GSTR2A').html(formatNumber(res.ineligible_SGST_GSTR2A.toFixed(2)));
	$('#ineligible_IGST_GSTR2A').html(formatNumber(res.ineligible_IGST_GSTR2A.toFixed(2)));

	$('#diffEligible_GSTR2_GSTR2A').html(formatNumber(res.diffEligible_GSTR2_GSTR2A.toFixed(2)));
	$('#diffEligible_IGST_GSTR2_GSTR2A').html(formatNumber(res.diffEligible_IGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffEligible_CGST_GSTR2_GSTR2A').html(formatNumber(res.diffEligible_CGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffEligible_SGST_GSTR2_GSTR2A').html(formatNumber(res.diffEligible_SGST_GSTR2_GSTR2A.toFixed(2)));
	
	$('#diffIneligible_GSTR2_GSTR2A').html(formatNumber(res.diffIneligible_GSTR2_GSTR2A.toFixed(2)));
	$('#diffIneligible_IGST_GSTR2_GSTR2A').html(formatNumber(res.diffIneligible_IGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffIneligible_CGST_GSTR2_GSTR2A').html(formatNumber(res.diffIneligible_CGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffIneligible_SGST_GSTR2_GSTR2A').html(formatNumber(res.diffIneligible_SGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffRCM_GSTR2_GSTR2A').html(formatNumber(res.diffRCM_GSTR2_GSTR2A.toFixed(2)));
	$('#diffRCM_IGST_GSTR2_GSTR2A').html(formatNumber(res.diffRCM_IGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffRCM_CGST_GSTR2_GSTR2A').html(formatNumber(res.diffRCM_CGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffRCM_SGST_GSTR2_GSTR2A').html(formatNumber(res.diffRCM_SGST_GSTR2_GSTR2A.toFixed(2)));
	
	$('#diffISD_GSTR2_GSTR2A').html(formatNumber(res.diffISD_GSTR2_GSTR2A.toFixed(2)));
	$('#diffISD_IGST_GSTR2_GSTR2A').html(formatNumber(res.diffISD_IGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffISD_CGST_GSTR2_GSTR2A').html(formatNumber(res.diffISD_CGST_GSTR2_GSTR2A.toFixed(2)));
	$('#diffISD_SGST_GSTR2_GSTR2A').html(formatNumber(res.diffISD_SGST_GSTR2_GSTR2A.toFixed(2)));
	
	$('#diffISD_GSTR3B_GSTR2A').html(formatNumber(res.diffISD_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffISD_IGST_GSTR3B_GSTR2A').html(formatNumber(res.diffISD_IGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffISD_CGST_GSTR3B_GSTR2A').html(formatNumber(res.diffISD_CGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffISD_SGST_GSTR3B_GSTR2A').html(formatNumber(res.diffISD_SGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffRCM_GSTR3B_GSTR2A').html(formatNumber(res.diffRCM_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffRCM_IGST_GSTR3B_GSTR2A').html(formatNumber(res.diffRCM_IGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffRCM_CGST_GSTR3B_GSTR2A').html(formatNumber(res.diffRCM_CGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffRCM_SGST_GSTR3B_GSTR2A').html(formatNumber(res.diffRCM_SGST_GSTR3B_GSTR2A.toFixed(2)));
	
	$('#diffEligible_GSTR3B_GSTR2A').html(formatNumber(res.diffEligible_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffEligible_IGST_GSTR3B_GSTR2A').html(formatNumber(res.diffEligible_IGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffEligible_CGST_GSTR3B_GSTR2A').html(formatNumber(res.diffEligible_CGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffEligible_SGST_GSTR3B_GSTR2A').html(formatNumber(res.diffEligible_SGST_GSTR3B_GSTR2A.toFixed(2)));
	
	$('#diffIneligible_GSTR3B_GSTR2A').html(formatNumber(res.diffIneligible_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffIneligible_IGST_GSTR3B_GSTR2A').html(formatNumber(res.diffIneligible_IGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffIneligible_CGST_GSTR3B_GSTR2A').html(formatNumber(res.diffIneligible_CGST_GSTR3B_GSTR2A.toFixed(2)));
	$('#diffIneligible_SGST_GSTR3B_GSTR2A').html(formatNumber(res.diffIneligible_SGST_GSTR3B_GSTR2A.toFixed(2)));
	
}
$(function(){
  $('[data-toggle="tooltip"]').tooltip({
   container: 'body'
  });
});
</script>
</body>
</html>