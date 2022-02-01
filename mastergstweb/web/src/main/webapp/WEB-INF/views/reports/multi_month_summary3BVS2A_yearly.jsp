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
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_3bvs2a_yearly.css" media="all" />
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
	$(window).on('load', function() {
		OSREC.CurrencyFormatter.formatAll({selector : '.ind_formatss'});
	});
	function getdiv() {
		var customyear=$('#financialyear').val();
		window.location.href = '${contextPath}/multiMonthreports3BVS2Ayearly/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>/'+customyear;
	}
	function exportF(elem) {
		  var table = document.getElementById("excel_table");
		  var html = table.outerHTML;
		  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
		  elem.setAttribute("href", url);
		  elem.setAttribute("download", "PURCHASES_VS_GSTR3B_VS_GSTR2A_yearly.xls"); // Choose the file name
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
			<div class="dropdown chooseteam mr-0" style="height: 32px;margin-bottom: 23px;">
				<p></p>
			<div class="dropdwn mr-0" style="height: 32px;float: right;">
				<a id="downloadLink" class="excel_btn" onclick="exportF(this)" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
				<lable for="monthly" class="mr-1" style="vertical-align: middle;font-weight: bolder;">Select Financial Year: </lable>
				<div class="pull-right" style="display: inline-flex;">
					<select class="form-control" id="financialyear" style="z-index:2;padding: 0px;height: 28px;margin-right: 8px;">
						<c:choose>
							<c:when test="${customyear eq '2020'}">
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
			<div id="yearProcess" class="text-center"></div>
			<table id="excel_table">
				<tr>
				<td>
					<div class="customtable db-ca-view reportTable reportTable4">
						<table id="month4_reportTable" border="1" class="display row-border dataTable meterialform" cellspacing="0" width="1">
							<thead>
								<tr>
									<th colspan="7" class="text-center month_header" id="month_name">Yearly Summary Report <c:out value="${data.summaryyear}"/></th>						  
								</tr>
								<tr>
									<th class="text-center tax_hr">Tax Details</th>
									<th class="text-center books_header">Purchases</th>
									<th class="text-center">GSTR3B</th>
									<th class="text-center">GSTR2A</th>
									<th class="text-center">Purchases-GSTR3B</th>
									<th class="text-center">Purchases-GSTR2A</th>
									<th class="text-center">GSTR3B-GSTR2A</th>
								</tr>
							</thead>
							<tbody id="yeartotoalreport">
								<tr>
									<td><h6 align="right" class="3bvs2ainfo">IGST - Import of goods</h6> </td>
									<td class="ind_formatss"><c:out value="${data.importGoods_IGST_GSTR2}"/></td>
									<td class="ind_formatss"><c:out value="${data.importGoods_IGST_GSTR3B}"/></td>
									<td class="ind_formatss"><c:out value="${data.importGoods_IGST_GSTR2A}"/></td>
									<td class="ind_formatss"><c:out value="${data.diffImportGoods_IGST_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss"><c:out value="${data.diffImportGoods_IGST_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss"><c:out value="${data.diffImportGoods_IGST_GSTR3B_GSTR2A}"/></td>
					        	</tr>
								<tr>
									<td><h6 align="right" class="3bvs2ainfo">IGST - Import of Services</h6> </td>
									<td class="ind_formatss"><c:out value="${data.importServices_IGST_GSTR2}"/></td>
									<td class="ind_formatss"><c:out value="${data.importServices_IGST_GSTR3B}"/></td>
									<td style="text-align:center;">NA</td>
									<td class="ind_formatss"><c:out value="${data.diffImportServices_IGST_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss"><c:out value="${data.importServices_IGST_GSTR2}"/></td>
									<td class="ind_formatss"><c:out value="${data.importServices_IGST_GSTR3B}"/></td>
								</tr>
								<tr class="rcminfo">
									<td class="dropdown rcmhead">
									<h6 align="right" class="3bvs2ainfo">RCM (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6> 
									<div class="dropdown-content rcmdata">
										<span class="arrow-top"></span>
											<table class="inner_table" id="inner_table">
												<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">RCM Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss"><c:out value="${data.rcm_IGST_GSTR2}"/></td><td class="ind_formatss" id="rcm_IGST_GSTR3B"><c:out value="${data.rcm_IGST_GSTR3B}"/></td><td class="ind_formatss" id="rcm_IGST_GSTR2A"><c:out value="${data.rcm_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_IGST_GSTR2_GSTR3B"><c:out value="${data.diffRCM_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffRCM_IGST_GSTR2_GSTR2A"><c:out value="${data.diffRCM_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_IGST_GSTR3B_GSTR2A"><c:out value="${data.diffRCM_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss"><c:out value="${data.rcm_CGST_GSTR2}"/></td><td class="ind_formatss" id="rcm_CGST_GSTR3B"><c:out value="${data.rcm_CGST_GSTR3B}"/></td><td class="ind_formatss" id="rcm_CGST_GSTR2A"><c:out value="${data.rcm_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_CGST_GSTR2_GSTR3B"><c:out value="${data.diffRCM_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffRCM_CGST_GSTR2_GSTR2A"><c:out value="${data.diffRCM_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_CGST_GSTR3B_GSTR2A"><c:out value="${data.diffRCM_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss"><c:out value="${data.rcm_SGST_GSTR2}"/></td><td class="ind_formatss" id="rcm_SGST_GSTR3B"><c:out value="${data.rcm_SGST_GSTR3B}"/></td><td class="ind_formatss" id="rcm_SGST_GSTR2A"><c:out value="${data.rcm_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_SGST_GSTR2_GSTR3B"><c:out value="${data.diffRCM_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffRCM_SGST_GSTR2_GSTR2A"><c:out value="${data.diffRCM_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_SGST_GSTR3B_GSTR2A"><c:out value="${data.diffRCM_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="rcm_GSTR2"><c:out value="${data.rcm_GSTR2}"/></td>
									<td class="ind_formatss"><c:out value="${data.rcm_GSTR3B}"/></td>
									<td class="ind_formatss"><c:out value="${data.rcm_GSTR2A}"/></td>
									<td class="ind_formatss"><c:out value="${data.diffRCM_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss"><c:out value="${data.diffRCM_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss"><c:out value="${data.diffRCM_GSTR3B_GSTR2A}"/></td>
								</tr>
								<tr class="isdinfo">
									<td class="dropdown isdhead">
									<h6 align="right" class="3bvs2ainfo">ISD (+) <br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6>
									<div class="dropdown-content isddata">
											<span class="arrow-top"></span>
											<table class="inner_table">
												<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">ISD Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss"><c:out value="${data.isd_IGST_GSTR2}"/></td><td class="ind_formatss" id="isd_IGST_GSTR3B"><c:out value="${data.isd_IGST_GSTR3B}"/></td><td class="ind_formatss" id="isd_IGST_GSTR2A"><c:out value="${data.isd_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_IGST_GSTR2_GSTR3B"><c:out value="${data.diffISD_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffISD_IGST_GSTR2_GSTR2A"><c:out value="${data.diffISD_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_IGST_GSTR3B_GSTR2A"><c:out value="${data.diffISD_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss"><c:out value="${data.isd_CGST_GSTR2}"/></td><td class="ind_formatss" id="isd_CGST_GSTR3B"><c:out value="${data.isd_CGST_GSTR3B}"/></td><td class="ind_formatss" id="isd_CGST_GSTR2A"><c:out value="${data.isd_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_CGST_GSTR2_GSTR3B"><c:out value="${data.diffISD_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffISD_CGST_GSTR2_GSTR2A"><c:out value="${data.diffISD_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_CGST_GSTR3B_GSTR2A"><c:out value="${data.diffISD_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss"><c:out value="${data.isd_SGST_GSTR2}"/></td><td class="ind_formatss" id="isd_SGST_GSTR3B"><c:out value="${data.isd_SGST_GSTR3B}"/></td><td class="ind_formatss" id="isd_SGST_GSTR2A"><c:out value="${data.isd_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_SGST_GSTR2_GSTR3B"><c:out value="${data.diffISD_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffISD_SGST_GSTR2_GSTR2A"><c:out value="${data.diffISD_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_SGST_GSTR3B_GSTR2A"><c:out value="${data.diffISD_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="isd_GSTR2"><c:out value="${data.isd_GSTR2}"/></td>
									<td class="ind_formatss" id="isd_GSTR3B"><c:out value="${data.isd_GSTR3B}"/></td>
									<td class="ind_formatss" id="isd_GSTR2A"><c:out value="${data.isd_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffISD_GSTR2_GSTR3B"><c:out value="${data.diffISD_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffISD_GSTR2_GSTR2A"><c:out value="${data.diffISD_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffISD_GSTR3B_GSTR2A"><c:out value="${data.diffISD_GSTR3B_GSTR2A}"/></td>
					            </tr>
								<tr class="eligibleinfo">
									<td class="dropdown eligiblehead">
									<h6 align="right" class="3bvs2ainfo">Eligible (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6> 
									<div class="dropdown-content eligibledata">
											<span class="arrow-bottom"></span>
											<table class="inner_table">
												<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">Eligible Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="eligible_IGST_GSTR2"><c:out value="${data.eligible_IGST_GSTR2}"/></td><td class="ind_formatss" id="eligible_IGST_GSTR3B"><c:out value="${data.eligible_IGST_GSTR3B}"/></td><td class="ind_formatss" id="eligible_IGST_GSTR2A"><c:out value="${data.eligible_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_IGST_GSTR2_GSTR3B"><c:out value="${data.diffEligible_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffEligible_IGST_GSTR2_GSTR2A"><c:out value="${data.diffEligible_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_IGST_GSTR3B_GSTR2A"><c:out value="${data.diffEligible_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="eligible_CGST_GSTR2"><c:out value="${data.eligible_CGST_GSTR2}"/></td><td class="ind_formatss" id="eligible_CGST_GSTR3B"><c:out value="${data.eligible_CGST_GSTR3B}"/></td><td class="ind_formatss" id="eligible_CGST_GSTR2A"><c:out value="${data.eligible_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_CGST_GSTR2_GSTR3B"><c:out value="${data.diffEligible_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffEligible_CGST_GSTR2_GSTR2A"><c:out value="${data.diffEligible_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_CGST_GSTR3B_GSTR2A"><c:out value="${data.diffEligible_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="eligible_SGST_GSTR2"><c:out value="${data.eligible_SGST_GSTR2}"/></td><td class="ind_formatss" id="eligible_SGST_GSTR3B"><c:out value="${data.eligible_SGST_GSTR3B}"/></td><td class="ind_formatss" id="eligible_SGST_GSTR2A"><c:out value="${data.eligible_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_SGST_GSTR2_GSTR3B"><c:out value="${data.diffEligible_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffEligible_SGST_GSTR2_GSTR2A"><c:out value="${data.diffEligible_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_SGST_GSTR3B_GSTR2A"><c:out value="${data.diffEligible_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="eligible_GSTR2"><c:out value="${data.eligible_GSTR2}"/></td>
									<td class="ind_formatss" id="eligible_GSTR3B"><c:out value="${data.eligible_GSTR3B}"/></td>
									<td class="ind_formatss" id="eligible_GSTR2A"><c:out value="${data.eligible_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffEligible_GSTR2_GSTR3B"><c:out value="${data.diffEligible_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffEligible_GSTR2_GSTR2A"><c:out value="${data.diffEligible_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffEligible_GSTR3B_GSTR2A"><c:out value="${data.diffEligible_GSTR3B_GSTR2A}"/></td>
					            </tr>
					            <tr class="ineligibleinfo">
									<td class="dropdown ineligiblehead">
										<h6 align="right" class="3bvs2ainfo">InEligible (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6>
										<div class="dropdown-content ineligibledata">
											<span class="arrow-bottom"></span>
											<table class="inner_table">
												<thead><tr><th style="width:13%;background-color: #7d3938ba!important;color:#fff!important">InEligible Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="ineligible_IGST_GSTR2"><c:out value="${data.ineligible_IGST_GSTR2}"/></td><td class="ind_formatss" id="ineligible_IGST_GSTR3B"><c:out value="${data.ineligible_IGST_GSTR3B}"/></td><td class="ind_formatss" id="ineligible_IGST_GSTR2A"><c:out value="${data.ineligible_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_IGST_GSTR2_GSTR3B"><c:out value="${data.diffIneligible_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffIneligible_IGST_GSTR2_GSTR2A"><c:out value="${data.diffIneligible_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_IGST_GSTR3B_GSTR2A"><c:out value="${data.diffIneligible_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="ineligible_CGST_GSTR2"><c:out value="${data.ineligible_CGST_GSTR2}"/></td><td class="ind_formatss" id="ineligible_CGST_GSTR3B"><c:out value="${data.ineligible_CGST_GSTR3B}"/></td><td class="ind_formatss" id="ineligible_CGST_GSTR2A"><c:out value="${data.ineligible_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_CGST_GSTR2_GSTR3B"><c:out value="${data.diffIneligible_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffIneligible_CGST_GSTR2_GSTR2A"><c:out value="${data.diffIneligible_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_CGST_GSTR3B_GSTR2A"><c:out value="${data.diffIneligible_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="ineligible_SGST_GSTR2"><c:out value="${data.ineligible_SGST_GSTR2}"/></td><td class="ind_formatss" id="ineligible_SGST_GSTR3B"><c:out value="${data.ineligible_SGST_GSTR3B}"/></td><td class="ind_formatss" id="ineligible_SGST_GSTR2A"><c:out value="${data.ineligible_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_SGST_GSTR2_GSTR3B"><c:out value="${data.diffIneligible_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffIneligible_SGST_GSTR2_GSTR2A"><c:out value="${data.diffIneligible_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_SGST_GSTR3B_GSTR2A"><c:out value="${data.diffIneligible_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									 </td>
									<td class="ind_formatss" id="ineligible_GSTR2"><c:out value="${data.ineligible_GSTR2}"/></td>
									<td class="ind_formatss" id="ineligible_GSTR3B"><c:out value="${data.ineligible_GSTR3B}"/></td>
									<td class="ind_formatss" id="ineligible_GSTR2A"><c:out value="${data.ineligible_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffIneligible_GSTR2_GSTR3B"><c:out value="${data.diffIneligible_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffIneligible_GSTR2_GSTR2A"><c:out value="${data.diffIneligible_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffIneligible_GSTR3B_GSTR2A"><c:out value="${data.diffIneligible_GSTR3B_GSTR2A}"/></td>
					            </tr>
					            <tr class="isdinfo">
									<td  class="dropdown credithead">
									<h6 align="right" class="3bvs2ainfo">Credit reversed (+)<br/><span style="font-size: 13px;color:chocolate">please mouseover to view full details</span></h6>
									<div class="dropdown-content creditdata">
											<span class="arrow-bottom"></span>
											<table class="inner_table">
												<thead><tr><th style="width:15%;background-color: #7d3938ba!important;color:#fff!important">Credit Reversed Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="credit_Reversed_IGST_GSTR2"><c:out value="${data.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="credit_Reversed_IGST_GSTR3B"><c:out value="${data.credit_Reversed_IGST_GSTR3B}"/></td><td style="text-align:center;">NA</td><td class="ind_formatss" id="diffCredit_Reversed_IGST_GSTR2_GSTR3B"><c:out value="${data.diffCredit_Reversed_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss"><c:out value="${data.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="diffCredit_Reversed_IGST_GSTR3B_GSTR2A"><c:out value="${data.credit_Reversed_IGST_GSTR3B}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="credit_Reversed_CGST_GSTR2"><c:out value="${data.credit_Reversed_CGST_GSTR2}"/></td><td class="ind_formatss" id="credit_Reversed_CGST_GSTR3B"><c:out value="${data.credit_Reversed_CGST_GSTR3B}"/></td><td style="text-align:center;">NA</td><td class="ind_formatss" id="diffCredit_Reversed_CGST_GSTR2_GSTR3B"><c:out value="${data.diffCredit_Reversed_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss"><c:out value="${data.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="diffCredit_Reversed_CGST_GSTR3B_GSTR2A"><c:out value="${data.credit_Reversed_IGST_GSTR3B}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="credit_Reversed_SGST_GSTR2"><c:out value="${data.credit_Reversed_SGST_GSTR2}"/></td><td class="ind_formatss" id="credit_Reversed_SGST_GSTR3B"><c:out value="${data.credit_Reversed_SGST_GSTR3B}"/></td><td style="text-align:center;">NA</td><td class="ind_formatss" id="diffCredit_Reversed_SGST_GSTR2_GSTR3B"><c:out value="${data.diffCredit_Reversed_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss"><c:out value="${data.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="diffCredit_Reversed_SGST_GSTR3B_GSTR2A"><c:out value="${data.credit_Reversed_IGST_GSTR3B}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="credit_Reversed_GSTR2"><c:out value="${data.credit_Reversed_GSTR2}"/></td>
									<td class="ind_formatss" id="credit_Reversed_GSTR3B"><c:out value="${data.credit_Reversed_GSTR3B}"/></td>
									<td style="text-align:center;">NA</td>
									<td class="ind_formatss" id="diffCredit_Reversed_GSTR2_GSTR3B"><c:out value="${data.diffCredit_Reversed_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffCredit_Reversed_GSTR2_GSTR2A"><c:out value="${data.credit_Reversed_GSTR2}"/></td>
									<td class="ind_formatss" id="diffCredit_Reversed_GSTR3B_GSTR2A"><c:out value="${data.credit_Reversed_GSTR3B}"/></td>
					             </tr>
		 					</tbody>
						</table>
					</div>
			</td>
			</tr>
			
			<div id="yearProcess" class="text-center"></div>
				<c:forEach items="${gstr3bvsgstr2a}" var="gstr">
				<tr></tr>
				<tr>
				<td>
					<div class="customtable db-ca-view reportTable reportTable4">
						<table id="month4_reportTable" border="1" class="display row-border dataTable meterialform" cellspacing="0" width="1"/>
							<thead>
								<tr>
									<th colspan="7" class="text-center month_header" id="month_name"><c:out value="${gstr.month}"/></th>						  
								</tr>
								<tr>
									<th class="text-center tax_hr">Tax Details</th>
									<th class="text-center Purchases_header">Purchases</th>
									<th class="text-center">GSTR3B</th>
									<th class="text-center">GSTR2A</th>
									<th class="text-center">Purchases-GSTR3B</th>
									<th class="text-center">Purchases-GSTR2A</th>
									<th class="text-center">GSTR3B-GSTR2A</th>
								</tr>
							</thead>
							<tbody id="yeartotoalreport">
								<tr>
									<td><h6 align="right" class="3bvs2ainfo">IGST - Import of goods</h6> </td>
									<td class="ind_formatss" id="importGoods_IGST_GSTR2"><c:out value="${gstr.importGoods_IGST_GSTR2}"/></td>
									<td class="ind_formatss" id="importGoods_IGST_GSTR3B"><c:out value="${gstr.importGoods_IGST_GSTR3B}"/></td>
									<td class="ind_formatss" id="importGoods_IGST_GSTR2A"><c:out value="${gstr.importGoods_IGST_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffImportGoods_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffImportGoods_IGST_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffImportGoods_IGST_GSTR2_GSTR2A"><c:out value="${gstr.diffImportGoods_IGST_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffImportGoods_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffImportGoods_IGST_GSTR3B_GSTR2A}"/></td>
					        	</tr>
								<tr>
									<td><h6 align="right" class="3bvs2ainfo">IGST - Import of Services</h6> </td>
									<td class="ind_formatss" id="importServices_IGST_GSTR2"><c:out value="${gstr.importServices_IGST_GSTR2}"/></td>
									<td class="ind_formatss" id="importServices_IGST_GSTR3B"><c:out value="${gstr.importServices_IGST_GSTR3B}"/></td>
									<td style="text-align:center;">NA</td>
									<td class="ind_formatss" id="diffImportServices_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffImportServices_IGST_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffImportServices_IGST_GSTR2_GSTR2A"><c:out value="${gstr.importServices_IGST_GSTR2}"/></td>
									<td class="ind_formatss" id="diffImportServices_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.importServices_IGST_GSTR3B}"/></td>
								</tr>
								<tr class="rcminfo">
									<td class="dropdown rcmhead">
									<h6 align="right" class="3bvs2ainfo">RCM (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6> 
									<div class="dropdown-content rcmdata">
											<span class="arrow-top"></span>
											<table class="inner_table" id="inner_table">
												<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">RCM Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="rcm_IGST_GSTR2"><c:out value="${gstr.rcm_IGST_GSTR2}"/></td><td class="ind_formatss" id="rcm_IGST_GSTR3B"><c:out value="${gstr.rcm_IGST_GSTR3B}"/></td><td class="ind_formatss" id="rcm_IGST_GSTR2A"><c:out value="${gstr.rcm_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffRCM_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffRCM_IGST_GSTR2_GSTR2A"><c:out value="${gstr.diffRCM_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffRCM_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="rcm_CGST_GSTR2"><c:out value="${gstr.rcm_CGST_GSTR2}"/></td><td class="ind_formatss" id="rcm_CGST_GSTR3B"><c:out value="${gstr.rcm_CGST_GSTR3B}"/></td><td class="ind_formatss" id="rcm_CGST_GSTR2A"><c:out value="${gstr.rcm_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_CGST_GSTR2_GSTR3B"><c:out value="${gstr.diffRCM_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffRCM_CGST_GSTR2_GSTR2A"><c:out value="${gstr.diffRCM_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_CGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffRCM_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="rcm_SGST_GSTR2"><c:out value="${gstr.rcm_SGST_GSTR2}"/></td><td class="ind_formatss" id="rcm_SGST_GSTR3B"><c:out value="${gstr.rcm_SGST_GSTR3B}"/></td><td class="ind_formatss" id="rcm_SGST_GSTR2A"><c:out value="${gstr.rcm_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_SGST_GSTR2_GSTR3B"><c:out value="${gstr.diffRCM_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffRCM_SGST_GSTR2_GSTR2A"><c:out value="${gstr.diffRCM_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffRCM_SGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffRCM_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="rcm_GSTR2"><c:out value="${gstr.rcm_GSTR2}"/></td>
									<td class="ind_formatss" id="rcm_GSTR3B"><c:out value="${gstr.rcm_GSTR3B}"/></td>
									<td class="ind_formatss" id="rcm_GSTR2A"><c:out value="${gstr.rcm_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffRCM_GSTR2_GSTR3B"><c:out value="${gstr.diffRCM_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffRCM_GSTR2_GSTR2A"><c:out value="${gstr.diffRCM_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffRCM_GSTR3B_GSTR2A"><c:out value="${gstr.diffRCM_GSTR3B_GSTR2A}"/></td>
								</tr>
								<tr class="isdinfo">
									<td class="dropdown isdhead">
									<h6 align="right" class="3bvs2ainfo">ISD (+) <br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6>
									<div class="dropdown-content isddata">
											<span class="arrow-top"></span>
											<table class="inner_table">
												<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">ISD Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="isd_IGST_GSTR2"><c:out value="${gstr.isd_IGST_GSTR2}"/></td><td class="ind_formatss" id="isd_IGST_GSTR3B"><c:out value="${gstr.isd_IGST_GSTR3B}"/></td><td class="ind_formatss" id="isd_IGST_GSTR2A"><c:out value="${gstr.isd_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffISD_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffISD_IGST_GSTR2_GSTR2A"><c:out value="${gstr.diffISD_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffISD_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="isd_CGST_GSTR2"><c:out value="${gstr.isd_CGST_GSTR2}"/></td><td class="ind_formatss" id="isd_CGST_GSTR3B"><c:out value="${gstr.isd_CGST_GSTR3B}"/></td><td class="ind_formatss" id="isd_CGST_GSTR2A"><c:out value="${gstr.isd_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_CGST_GSTR2_GSTR3B"><c:out value="${gstr.diffISD_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffISD_CGST_GSTR2_GSTR2A"><c:out value="${gstr.diffISD_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_CGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffISD_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="isd_SGST_GSTR2"><c:out value="${gstr.isd_SGST_GSTR2}"/></td><td class="ind_formatss" id="isd_SGST_GSTR3B"><c:out value="${gstr.isd_SGST_GSTR3B}"/></td><td class="ind_formatss" id="isd_SGST_GSTR2A"><c:out value="${gstr.isd_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_SGST_GSTR2_GSTR3B"><c:out value="${gstr.diffISD_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffISD_SGST_GSTR2_GSTR2A"><c:out value="${gstr.diffISD_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffISD_SGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffISD_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="isd_GSTR2"><c:out value="${gstr.isd_GSTR2}"/></td>
									<td class="ind_formatss" id="isd_GSTR3B"><c:out value="${gstr.isd_GSTR3B}"/></td>
									<td class="ind_formatss" id="isd_GSTR2A"><c:out value="${gstr.isd_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffISD_GSTR2_GSTR3B"><c:out value="${gstr.diffISD_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffISD_GSTR2_GSTR2A"><c:out value="${gstr.diffISD_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffISD_GSTR3B_GSTR2A"><c:out value="${gstr.diffISD_GSTR3B_GSTR2A}"/></td>
					            </tr>
								<tr class="eligibleinfo">
									<td class="dropdown eligiblehead">
									<h6 align="right" class="3bvs2ainfo">Eligible (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6> 
									<div class="dropdown-content eligibledata">
											<span class="arrow-bottom"></span>
											<table class="inner_table">
												<thead><tr><th style="background-color: #7d3938ba!important;color:#fff!important">Eligible Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="eligible_IGST_GSTR2"><c:out value="${gstr.eligible_IGST_GSTR2}"/></td><td class="ind_formatss" id="eligible_IGST_GSTR3B"><c:out value="${gstr.eligible_IGST_GSTR3B}"/></td><td class="ind_formatss" id="eligible_IGST_GSTR2A"><c:out value="${gstr.eligible_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffEligible_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffEligible_IGST_GSTR2_GSTR2A"><c:out value="${gstr.diffEligible_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffEligible_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="eligible_CGST_GSTR2"><c:out value="${gstr.eligible_CGST_GSTR2}"/></td><td class="ind_formatss" id="eligible_CGST_GSTR3B"><c:out value="${gstr.eligible_CGST_GSTR3B}"/></td><td class="ind_formatss" id="eligible_CGST_GSTR2A"><c:out value="${gstr.eligible_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_CGST_GSTR2_GSTR3B"><c:out value="${gstr.diffEligible_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffEligible_CGST_GSTR2_GSTR2A"><c:out value="${gstr.diffEligible_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_CGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffEligible_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="eligible_SGST_GSTR2"><c:out value="${gstr.eligible_SGST_GSTR2}"/></td><td class="ind_formatss" id="eligible_SGST_GSTR3B"><c:out value="${gstr.eligible_SGST_GSTR3B}"/></td><td class="ind_formatss" id="eligible_SGST_GSTR2A"><c:out value="${gstr.eligible_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_SGST_GSTR2_GSTR3B"><c:out value="${gstr.diffEligible_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffEligible_SGST_GSTR2_GSTR2A"><c:out value="${gstr.diffEligible_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffEligible_SGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffEligible_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="eligible_GSTR2"><c:out value="${gstr.eligible_GSTR2}"/></td>
									<td class="ind_formatss" id="eligible_GSTR3B"><c:out value="${gstr.eligible_GSTR3B}"/></td>
									<td class="ind_formatss" id="eligible_GSTR2A"><c:out value="${gstr.eligible_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffEligible_GSTR2_GSTR3B"><c:out value="${gstr.diffEligible_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffEligible_GSTR2_GSTR2A"><c:out value="${gstr.diffEligible_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffEligible_GSTR3B_GSTR2A"><c:out value="${gstr.diffEligible_GSTR3B_GSTR2A}"/></td>
					            </tr>
					            <tr class="ineligibleinfo">
									<td class="dropdown ineligiblehead">
										<h6 align="right" class="3bvs2ainfo">InEligible (+)<br/><span style="font-size: 13px;color:chocolate">Please mouseover to view full details</span></h6>
										<div class="dropdown-content ineligibledata">
											<span class="arrow-bottom"></span>
											<table class="inner_table">
												<thead><tr><th style="width:13%;background-color: #7d3938ba!important;color:#fff!important">InEligible Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="ineligible_IGST_GSTR2"><c:out value="${gstr.ineligible_IGST_GSTR2}"/></td><td class="ind_formatss" id="ineligible_IGST_GSTR3B"><c:out value="${gstr.ineligible_IGST_GSTR3B}"/></td><td class="ind_formatss" id="ineligible_IGST_GSTR2A"><c:out value="${gstr.ineligible_IGST_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffIneligible_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffIneligible_IGST_GSTR2_GSTR2A"><c:out value="${gstr.diffIneligible_IGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffIneligible_IGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="ineligible_CGST_GSTR2"><c:out value="${gstr.ineligible_CGST_GSTR2}"/></td><td class="ind_formatss" id="ineligible_CGST_GSTR3B"><c:out value="${gstr.ineligible_CGST_GSTR3B}"/></td><td class="ind_formatss" id="ineligible_CGST_GSTR2A"><c:out value="${gstr.ineligible_CGST_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_CGST_GSTR2_GSTR3B"><c:out value="${gstr.diffIneligible_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffIneligible_CGST_GSTR2_GSTR2A"><c:out value="${gstr.diffIneligible_CGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_CGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffIneligible_CGST_GSTR3B_GSTR2A}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="ineligible_SGST_GSTR2"><c:out value="${gstr.ineligible_SGST_GSTR2}"/></td><td class="ind_formatss" id="ineligible_SGST_GSTR3B"><c:out value="${gstr.ineligible_SGST_GSTR3B}"/></td><td class="ind_formatss" id="ineligible_SGST_GSTR2A"><c:out value="${gstr.ineligible_SGST_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_SGST_GSTR2_GSTR3B"><c:out value="${gstr.diffIneligible_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss" id="diffIneligible_SGST_GSTR2_GSTR2A"><c:out value="${gstr.diffIneligible_SGST_GSTR2_GSTR2A}"/></td><td class="ind_formatss" id="diffIneligible_SGST_GSTR3B_GSTR2A"><c:out value="${gstr.diffIneligible_SGST_GSTR3B_GSTR2A}"/></td></tr>
												</tbody>
											</table>
										</div>
									 </td>
									<td class="ind_formatss" id="ineligible_GSTR2"><c:out value="${gstr.ineligible_GSTR2}"/></td>
									<td class="ind_formatss" id="ineligible_GSTR3B"><c:out value="${gstr.ineligible_GSTR3B}"/></td>
									<td class="ind_formatss" id="ineligible_GSTR2A"><c:out value="${gstr.ineligible_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffIneligible_GSTR2_GSTR3B"><c:out value="${gstr.diffIneligible_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffIneligible_GSTR2_GSTR2A"><c:out value="${gstr.diffIneligible_GSTR2_GSTR2A}"/></td>
									<td class="ind_formatss" id="diffIneligible_GSTR3B_GSTR2A"><c:out value="${gstr.diffIneligible_GSTR3B_GSTR2A}"/></td>
					            </tr>
					            <tr class="isdinfo">
									<td  class="dropdown credithead">
									<h6 align="right" class="3bvs2ainfo">Credit reversed (+)<br/><span style="font-size: 13px;color:chocolate">please mouseover to view full details</span></h6>
									<div class="dropdown-content creditdata">
											<span class="arrow-bottom"></span>
											<table class="inner_table">
												<thead><tr><th style="width:15%;background-color: #7d3938ba!important;color:#fff!important">Credit Reversed Type</th><th>Purchases</th><th>GSTR3B</th><th>GSTR2A</th><th>Purchases-GSTR3B</th><th>Purchases-GSTR2A</th><th>GSTR3B-GSTR2A</th></tr></thead>
												<tbody>
													<tr><td>IGST</td><td class="ind_formatss" id="credit_Reversed_IGST_GSTR2"><c:out value="${gstr.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="credit_Reversed_IGST_GSTR3B"><c:out value="${gstr.credit_Reversed_IGST_GSTR3B}"/></td><td style="text-align:center;">NA</td><td class="ind_formatss" id="diffCredit_Reversed_IGST_GSTR2_GSTR3B"><c:out value="${gstr.diffCredit_Reversed_IGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss"><c:out value="${gstr.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="diffCredit_Reversed_IGST_GSTR3B_GSTR2A"><c:out value="${gstr.credit_Reversed_IGST_GSTR3B}"/></td></tr>
													<tr><td>CGST</td><td class="ind_formatss" id="credit_Reversed_CGST_GSTR2"><c:out value="${gstr.credit_Reversed_CGST_GSTR2}"/></td><td class="ind_formatss" id="credit_Reversed_CGST_GSTR3B"><c:out value="${gstr.credit_Reversed_CGST_GSTR3B}"/></td><td style="text-align:center;">NA</td><td class="ind_formatss" id="diffCredit_Reversed_CGST_GSTR2_GSTR3B"><c:out value="${gstr.diffCredit_Reversed_CGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss"><c:out value="${gstr.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="diffCredit_Reversed_CGST_GSTR3B_GSTR2A"><c:out value="${gstr.credit_Reversed_IGST_GSTR3B}"/></td></tr>
													<tr><td>SGST</td><td class="ind_formatss" id="credit_Reversed_SGST_GSTR2"><c:out value="${gstr.credit_Reversed_SGST_GSTR2}"/></td><td class="ind_formatss" id="credit_Reversed_SGST_GSTR3B"><c:out value="${gstr.credit_Reversed_SGST_GSTR3B}"/></td><td style="text-align:center;">NA</td><td class="ind_formatss" id="diffCredit_Reversed_SGST_GSTR2_GSTR3B"><c:out value="${gstr.diffCredit_Reversed_SGST_GSTR2_GSTR3B}"/></td><td class="ind_formatss"><c:out value="${gstr.credit_Reversed_IGST_GSTR2}"/></td><td class="ind_formatss" id="diffCredit_Reversed_SGST_GSTR3B_GSTR2A"><c:out value="${gstr.credit_Reversed_IGST_GSTR3B}"/></td></tr>
												</tbody>
											</table>
										</div>
									</td>
									<td class="ind_formatss" id="credit_Reversed_GSTR2"><c:out value="${gstr.credit_Reversed_GSTR2}"/></td>
									<td class="ind_formatss" id="credit_Reversed_GSTR3B"><c:out value="${gstr.credit_Reversed_GSTR3B}"/></td>
									<td style="text-align:center;">NA</td>
									<td class="ind_formatss" id="diffCredit_Reversed_GSTR2_GSTR3B"><c:out value="${gstr.diffCredit_Reversed_GSTR2_GSTR3B}"/></td>
									<td class="ind_formatss" id="diffCredit_Reversed_GSTR2_GSTR2A"><c:out value="${gstr.credit_Reversed_GSTR2}"/></td>
									<td class="ind_formatss" id="diffCredit_Reversed_GSTR3B_GSTR2A"><c:out value="${gstr.credit_Reversed_GSTR3B}"/></td>
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
		 <b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between Purchases and GSTR3B (Purchases Data - (minus) GSTR3B Data)</span><span class="pl-2" style="display: block;">Differences between Purchases and GSTR2A (Purchase Data - (minus) GSTR2A Data)</span><span class="pl-2" style="display: block;">Differences between GSTR3B and GSTR2A (GSTR3B - (minus) GSTR2A Data)</span>
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
$( ".helpicon" ).hover(function() {$('.report3BVS2A ').show();
}, function() {$('.report3BVS2A ').hide();
});
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
</script>
</body>
</html>