<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | GSTR2B</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/gstr2b/gstr2b.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.min.js"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/invoice/gstr2binvoice.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/fixedHeader.dataTables.min.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css"	media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<%-- <script src="${contextPath}/static/mastergst/js/client/gstr2b.js" type="text/javascript"></script> --%>

<script type="text/javascript">
	var dbFilingTable4, dbFilingTable5, dbFilingTable6, dbFilingTable7, dbFilingTable8, dbFilingTable9, dbFilingTable10, dbFilingTable11, dbFilingTable12, dbFilingTable13, dbFilingTable14,  dbFilingTable15, dbFilingTable16,  dbFilingTable17,  dbFilingTable18, gstSummary=null, indexObj = new Object(), tableObj = new Object();
	var ipAddress = '', uploadResponse;var otpExpirycheck;
	$(function () {
		//$('.fy-drop').val('${invoice.fp}');
		$('#nav-client').addClass('active');
		function forceNumeric(){
			var $input = $(this);
			$input.val($input.val().replace(/[^\d.,]+/g,''));
		}
		/* var headertext = [],
		headers = document.querySelectorAll("table.display th"),
		tablerows = document.querySelectorAll("table.display th"),
		tablebody = document.querySelector("table.display tbody");

		for (var i = 0; i < headers.length; i++) {
			var current = headers[i];
			headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
		}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {
			for (var j = 0, col; col = row.cells[j]; j++) {
				col.setAttribute("data-th", headertext[j]);
			}
		} */
	});
</script>
</head>
<body class="body-cls suplies-body">
   <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>    
		<!--- breadcrumb start -->
 
<div class="breadcrumbwrap nav-bread">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item">GSTR2B</li>
					</ol>
					<span class="datetimetxt"> 
						<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
					</span>
					<span class="retutprdntxt">
						Return Period: 
					</span> 
					<!-- <select class="pull-right mt-1" id="gstr2bFinancialYear"><option value="2018">2017 - 2018</option><option value="2019">2018 - 2019</option><option value="2020">2019 - 2020</option><option value="2021">2020 - 2021</option></select>
					<h6 class="f-14-b pull-right mt-2 mr-2 font-weight-bold" style="display: inline-block;">Financial Year : </h6>  -->
					<span class="dropdown chooseteam">
					  <span id="fillingoption"><b>Filing Option:</b> <span id="filing_option">Monthly</span></span>
					 </span>
					 
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>

	<!--- breadcrumb end -->
	<div class="db-ca-wrap db-ca-gst-wrap">
		<div class="container" style="min-height: 400px">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<div class="gstr-info-tabs">
						<div class="pull-right helpguide" data-toggle="modal" data-target="#helpGuideModal"> Help To File GSTR2B</div>
						<h4 class="mb-3">GSTR2B</h4>
						<a href="${contextPath}/sync2binvs/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="<%=MasterGSTConstants.GSTR2B%>"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=gstr2b"  id="dwnldFromGstinBtn" onclick = "syncWithGstr2b('GSTN')" class="btn btn-greendark pull-right btn-sm btn-all-iview-sales ml-0">Download From GSTIN</a>
						<a href="${contextPath}/dwnldITCAvailableSummaryxls/${id}/${client.id}/<%=MasterGSTConstants.GSTR2B%>/${month}/${year}?type=gstr2b" id="excelDownloadGstr2b" onclick = "syncWithGstr2b('Excel')" class="btn btn-blue-dark btn-sm pull-right mr-2">Download To Excel</a>
						<%-- <a href="${contextPath}/dwnldITCAvailableToPDF/${id}/${client.id}/<%=MasterGSTConstants.GSTR2B%>/${month}/${year}" id="" onclick = "" class="btn btn-blue-dark btn-sm pull-right mr-2">Download To PDF</a> --%>
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link active" data-toggle="tab" href="#main_tab1" role="tab">ITC Available Summary</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#main_tab2" role="tab">ITC Not Available Summary</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab2" role="tab">All tables</a>
							</li>
						</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<!-- Tab panes 1-->
					<div class="tab-pane active" id="main_tab1" role="tabpane1">
						      <h5>FORM SUMMARY - ITC Available </h5>
						        <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable1st" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-center" width="30%">Heading</th>
										<th class="text-center">GSTR-3B table</th>
										<th class="text-center">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="6">Credit which may be availed under FORM GSTR-3B</td>
									</tr>
									<tr>
										<td class="text-left" colspan="6">( Part A )  ITC Available - Credit may be claimed in relevant headings in GSTR-3B</td>
									</tr>
									<tr data-toggle="tooltip" data-placement="top" title="If this is positive, credit may be availed under Table 4(A)(5) of FORM GSTR-3B."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="1">(I) All other ITC - Supplies from registered persons other than reverse charge </a></td><td class="text-right" style="width: 65px;">4(A)(5)</td><td class="text-right" id="itcorCGST"></td><td class="text-right" id="itcorSGST"></td><td class="text-right" id="itcorIGST"></td><td class="text-right" id="itcorCESS"></td>
									</tr>
									<tr class="cat1" style="display:none">
										<td class="table-conta text-left" colspan="6">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr><td style="width:30%">B2B - Invoices</td><td></td><td class="text-right" id="itcorB2BCGST"></td><td class="text-right" id="itcorB2BSGST"></td><td class="text-right" id="itcorB2BIGST"></td><td class="text-right" id="itcorB2BCESS"></td></tr>
														<tr><td style="width:30%">B2B - Debit notes</td><td></td><td class="text-right" id="itcorCDNCGST"></td><td class="text-right" id="itcorCDNSGST"></td><td class="text-right" id="itcorCDNIGST"></td><td class="text-right" id="itcorCDNCESS"></td></tr>
														<tr>
															<td style="width:30%">B2B - Invoices (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcorB2BACGST"></td>
															<td class="text-right" id="itcorB2BASGST"></td>
															<td class="text-right" id="itcorB2BAIGST"></td>
															<td class="text-right" id="itcorB2BACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcorCDNACGST"></td>
															<td class="text-right" id="itcorCDNASGST"></td>
															<td class="text-right" id="itcorCDNAIGST"></td>
															<td class="text-right" id="itcorCDNACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<tr data-toggle="tooltip" data-placement="top" title="If this is positive, credit may be availed under Table 4(A)(4) of FORM GSTR-3B."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="2">(II) Inward Supplies from ISD </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">4(A)(4)</td>
										<td class="text-right" id="itcCGST"></td>
										<td class="text-right" id="itcSGST"></td>
										<td class="text-right" id="itcIGST"></td>
										<td class="text-right" id="itcCESS"></td>
									</tr>
									<tr class="cat2" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">ISD - Invoices</td>
															<td></td>
															<td class="text-right" id="itcisdCGST"></td>
															<td class="text-right" id="itcisdSGST"></td>
															<td class="text-right" id="itcisdIGST"></td>
															<td class="text-right" id="itcisdCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">ISD - Invoices (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcisdaCGST"></td>
															<td class="text-right" id="itcisdaSGST"></td>
															<td class="text-right" id="itcisdaIGST"></td>
															<td class="text-right" id="itcisdaCESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<!-- 3rd -->
									<tr data-toggle="tooltip" data-placement="top" title="These supplies shall be declared in Table 3.1(d) of FORM GSTR-3B for payment of tax."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="3">(III) Inward Supplies liable for reverse charge </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">3.1(d) , 4(A)(3)</td>
										<td class="text-right" id="itcrCGST"></td>
										<td class="text-right" id="itcrSGST"></td>
										<td class="text-right" id="itcrIGST"></td>
										<td class="text-right" id="itcrCESS"></td>
									</tr>
									<tr class="cat3" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">B2B - Invoices</td>
															<td></td>
															<td class="text-right" id="itcrB2BCGST"></td>
															<td class="text-right" id="itcrB2BSGST"></td>
															<td class="text-right" id="itcrB2BIGST"></td>
															<td class="text-right" id="itcrB2BCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes</td>
															<td></td>
															<td class="text-right" id="itcrCDNCGST"></td>
															<td class="text-right" id="itcrCDNSGST"></td>
															<td class="text-right" id="itcrCDNIGST"></td>
															<td class="text-right" id="itcrCDNCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Invoices (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcrB2BACGST"></td>
															<td class="text-right" id="itcrB2BASGST"></td>
															<td class="text-right" id="itcrB2BAIGST"></td>
															<td class="text-right" id="itcrB2BACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes (Amendment)</td>
															<td style="width:63px;"></td>
															<td class="text-right" id="itcrCDNACGST"></td>
															<td class="text-right" id="itcrCDNASGST"></td>
															<td class="text-right" id="itcrCDNAIGST"></td>
															<td class="text-right" id="itcrCDNACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<!-- 4th -->
									<tr data-toggle="tooltip" data-placement="top" title="If this is positive, credit may be availed under Table 4(A)(1) of FORM GSTR-3B."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="4">(IV) Import of Goods </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">4(A)(1)</td>
										<td class="text-right" id="itcIMPCGST"></td>
										<td class="text-right" id="itcIMPSGST"></td>
										<td class="text-right" id="itcIMPIGST"></td>
										<td class="text-right" id="itcIMPCESS"></td>
									</tr>
									<tr class="cat4" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">IMPG - Import of goods from overseas</td>
															<td></td>
															<td class="text-right" id="itcIMPGCEST"></td>
															<td class="text-right" id="itcIMPGSGST"></td>
															<td class="text-right" id="itcIMPGIGST"></td>
															<td class="text-right" id="itcIMPGCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">IMPG (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcIMPGACGST"></td>
															<td class="text-right" id="itcIMPGASGST"></td>
															<td class="text-right" id="itcIMPGAIGST"></td>
															<td class="text-right" id="itcIMPGACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">IMPGSEZ - Import of goods from SEZ</td>
															<td></td>
															<td class="text-right" id="itcIMPGSEZCGST"></td>
															<td class="text-right" id="itcIMPGSEZSGST"></td>
															<td class="text-right" id="itcIMPGSEZIGST"></td>
															<td class="text-right" id="itcIMPGSEZCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">IMPGSEZ (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcIMPGSEZACGST"></td>
															<td class="text-right" id="itcIMPGSEZASGST"></td>
															<td class="text-right" id="itcIMPGSEZAIGST"></td>
															<td class="text-right" id="itcIMPGSEZACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									
									<!-- completed -->
									
									<tr>
										<td class="text-left" colspan="7">( Part B )  ITC Reversal - Credit should be reversed in relevant headings in GSTR-3B </td>
									</tr>
									<tr data-toggle="tooltip" data-placement="top" title="If this is negative, then credit may be reclaimed subject to reversal of the same on an earlier instance."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="5">(I) Others </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">4(B)(2)</td>
										<td class="text-right" id="itcOthCGST"></td>
										<td class="text-right" id="itcOthSGST"></td>
										<td class="text-right" id="itcOthIGST"></td>
										<td class="text-right" id="itcOthCESS"></td>
									</tr>
									<tr class="cat5" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">B2B - Credit notes</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcOthB2BCGST"></td>
															<td class="text-right" id="itcOthB2BSGST"></td>
															<td class="text-right" id="itcOthB2BIGST"></td>
															<td class="text-right" id="itcOthB2BCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Credit notes (Amendment)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcOthCDNACGST"></td>
															<td class="text-right" id="itcOthCDNASGST"></td>
															<td class="text-right" id="itcOthCDNAIGST"></td>
															<td class="text-right" id="itcOthCDNACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Credit notes (Reverse charge)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcOthCDNRCGST"></td>
															<td class="text-right" id="itcOthCDNRSGST"></td>
															<td class="text-right" id="itcOthCDNRIGST"></td>
															<td class="text-right" id="itcOthCDNRCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Credit notes (Reverse charge)(Amendment)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcOthCDNRACGST"></td>
															<td class="text-right" id="itcOthCDNRASGST"></td>
															<td class="text-right" id="itcOthCDNRAIGST"></td>
															<td class="text-right" id="itcOthCDNRACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">ISD - Credit notes</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcOthISDCDNCGST"></td>
															<td class="text-right" id="itcOthISDCDNSGST"></td>
															<td class="text-right" id="itcOthISDCDNIGST"></td>
															<td class="text-right" id="itcOthISDCDNCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">ISD - Credit notes (Amendment)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcOthISDCDNACGST"></td>
															<td class="text-right" id="itcOthISDCDNASGST"></td>
															<td class="text-right" id="itcOthISDCDNAIGST"></td>
															<td class="text-right" id="itcOthISDCDNACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<!-- ITC Available End -->
									</tbody>
								</table>
						    </div>
						</div>
					<div class="tab-pane" id="main_tab2" role="tabpanel">
						      <!-- <h5>FORM SUMMARY - ITC Not Available	</h5><a href="#" id="" onclick = "" class="btn btn-blue-dark pull-right ml-2">Download To Excel</a> -->
						      <h5>FORM SUMMARY - ITC Not Available	</h5>
						       <div class="customtable db-ca-gst tabtable2 mt-2">       
    						<table id="dbTable3" class="dbTable2 display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-center" width="30%">Heading</th>
										<th class="text-center">GSTR-3B table</th>
										<th class="text-center">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="7">Credit which may not be availed under FORM GSTR-3B</td>
									</tr>
									<tr>
										<td class="text-left" colspan="7">( Part A )  ITC Not Available</td>
									</tr>
									<tr data-toggle="tooltip" data-placement="top" title="Such credit shall not be taken in FORM GSTR-3B."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="6">(I) All other ITC - Supplies from registered persons other than reverse charge </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">NA</td>
										<td class="text-right" id="itcunorCGST"></td>
										<td class="text-right" id="itcunorSGST"></td>
										<td class="text-right" id="itcunorIGST"></td>
										<td class="text-right" id="itcunorCESS"></td>
									</tr>
									<tr class="cat6" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">B2B - Invoices</td>
															<td></td>
															<td class="text-right" id="itcunorB2BCGST"></td>
															<td class="text-right" id="itcunorB2BSGST"></td>
															<td class="text-right" id="itcunorB2BIGST"></td>
															<td class="text-right" id="itcunorB2BCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes</td>
															<td></td>
															<td class="text-right" id="itcunorCDNCGST"></td>
															<td class="text-right" id="itcunorCDNSGST"></td>
															<td class="text-right" id="itcunorCDNIGST"></td>
															<td class="text-right" id="itcunorCDNCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Invoices (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcunorB2BACGST"></td>
															<td class="text-right" id="itcunorB2BASGST"></td>
															<td class="text-right" id="itcunorB2BAIGST"></td>
															<td class="text-right" id="itcunorB2BACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcunorCDNACGST"></td>
															<td class="text-right" id="itcunorCDNASGST"></td>
															<td class="text-right" id="itcunorCDNAIGST"></td>
															<td class="text-right" id="itcunorCDNACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<tr data-toggle="tooltip" data-placement="top" title="Such credit shall not be taken in FORM GSTR-3B."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="7">(II) Inward Supplies from ISD </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">NA</td>
										<td class="text-right" id="itcunISDTotCGST"></td>
										<td class="text-right" id="itcunISDTotSGST"></td>
										<td class="text-right" id="itcunISDTotIGST"></td>
										<td class="text-right" id="itcunISDTotCESS"></td>
									</tr>
									<tr class="cat7" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">ISD - Invoices</td>
															<td></td>
															<td class="text-right" id="itcunISDCGST"></td>
															<td class="text-right" id="itcunISDSGST"></td>
															<td class="text-right" id="itcunISDIGST"></td>
															<td class="text-right" id="itcunISDCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">ISD - Invoices (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcunSDACGST"></td>
															<td class="text-right" id="itcunISDASGST"></td>
															<td class="text-right" id="itcunISDAIGST"></td>
															<td class="text-right" id="itcunISDACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<!-- 3rd -->
									<tr data-toggle="tooltip" data-placement="top" title="These supplies shall be declared in Table 3.1(d) of FORM GSTR-3B for payment of tax."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="8">(III) Inward Supplies liable for reverse charge </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">3.1(d)</td>
										<td class="text-right" id="itcunrTotCGST"></td>
										<td class="text-right" id="itcunrTotSGST"></td>
										<td class="text-right" id="itcunrTotIGST"></td>
										<td class="text-right" id="itcunrTotCESS"></td>
									</tr>
									<tr class="cat8" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">B2B - Invoices</td>
															<td></td>
															<td class="text-right" id="itcunrB2BCGST"></td>
															<td class="text-right" id="itcunrB2BSGST"></td>
															<td class="text-right" id="itcunrB2BIGST"></td>
															<td class="text-right" id="itcunrB2BCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes</td>
															<td></td>
															<td class="text-right" id="itcunrCDNCGST"></td>
															<td class="text-right" id="itcunrCDNSGST"></td>
															<td class="text-right" id="itcunrCDNIGST"></td>
															<td class="text-right" id="itcunrCDNCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Invoices (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcunrB2BACGST"></td>
															<td class="text-right" id="itcunrB2BASGST"></td>
															<td class="text-right" id="itcunrB2BAIGST"></td>
															<td class="text-right" id="itcunrB2BACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Debit notes (Amendment)</td>
															<td></td>
															<td class="text-right" id="itcunrCDNACGST"></td>
															<td class="text-right" id="itcunrCDNASGST"></td>
															<td class="text-right" id="itcunrCDNAIGST"></td>
															<td class="text-right" id="itcunrCDNACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<tr>
										<td class="text-left" colspan="7">( Part B )  ITC Reversal  </td>
									</tr>
									<tr data-toggle="tooltip" data-placement="top" title="Credit shall be reversed under Table 4(B)(2) of FORM GSTR-3B."/>
										<td class="text-left"><a href="#" class="toggler" data-prod-cat="9">(I) Others </a></td>
										<td class="text-right form-group gst-3b-error" style="width: 65px;">4(B)(2)</td>
										<td class="text-right" id="itcunOthTotCGST"></td>
										<td class="text-right" id="itcunOthTotSGST"></td>
										<td class="text-right" id="itcunOthTotIGST"></td>
										<td class="text-right" id="itcunOthTotCESS"></td>
									</tr>
									<tr class="cat9" style="display:none">
										<td class="table-conta text-left" colspan="7">
											<div>
												<table style="width:100%;">
													<tbody>
														<tr>
															<td style="width:30%">B2B - Credit notes</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcunOthB2BCGST"></td>
															<td class="text-right" id="itcunOthB2BSGST"></td>
															<td class="text-right" id="itcunOthB2BIGST"></td>
															<td class="text-right" id="itcunOthB2BCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Credit notes (Amendment)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcunOthCDNACGST"></td>
															<td class="text-right" id="itcunOthCDNASGST"></td>
															<td class="text-right" id="itcunOthCDNAIGST"></td>
															<td class="text-right" id="itcunOthCDNACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Credit notes (Reverse charge)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcunOthCDNCGST"></td>
															<td class="text-right" id="itcunOthCDNSGST"></td>
															<td class="text-right" id="itcunOthCDNIGST"></td>
															<td class="text-right" id="itcunOthCDNCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">B2B - Credit notes (Reverse charge)(Amendment)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcunOthCDNRACGST"></td>
															<td class="text-right" id="itcunOthCDNRASGST"></td>
															<td class="text-right" id="itcunOthCDNRAIGST"></td>
															<td class="text-right" id="itcunOthCDNRACESS"></td>
														</tr>
														<tr>
															<td style="width:30%">ISD - Credit notes</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcunOthISDCGST"></td>
															<td class="text-right" id="itcunOthISDSGST"></td>
															<td class="text-right" id="itcunOthISDIGST"></td>
															<td class="text-right" id="itcunOthISDCESS"></td>
														</tr>
														<tr>
															<td style="width:30%">ISD - Credit notes (Amendment)</td>
															<td style="width:63px"></td>
															<td class="text-right" id="itcunOthISDACGST"></td>
															<td class="text-right" id="itcunOthISDASGST"></td>
															<td class="text-right" id="itcunOthISDAIGST"></td>
															<td class="text-right" id="itcunOthISDACESS"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</td>
									</tr>
									<!-- ITC Available End -->
								</tbody>
							</table>
					   	  </div>
						      </div>
					
					<!-- Tab panes 2-->
					<div class="tab-pane" id="gtab2" role="tabpanel"><!-- 
					<a href="#" id="download_all" onclick = "" class="btn btn-blue-dark down-all pull-right ml-2">Download All To Excel</a> -->
						<!-- table start -->
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link active" data-toggle="tab" href="#gtab2alltables" role="tab" >GSTR2B All Details</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab2b2b" role="tab">B2B</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab2b2ba" role="tab">B2BA</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab23" role="tab">B2B-CDNR</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab24" role="tab">B2B-CDNRA</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab25" role="tab">ISD</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab26" role="tab">ISDA</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab27" role="tab">IMPG</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab28" role="tab">IMPGSEZ</a>
							</li>
						</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<!-- Tab panes 1-->
				<div class="tab-pane active" id="gtab2alltables" role="tabpane1">
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divFiltersGstr2b"></span>
					<span class="btn-remove-tag" onclick="clearGstr2bFilters()">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div>
			<div class="noramltable-row-desc gstr2bfilter">
				<select id="multiselectInvType" class="multiselect-ui form-control" multiple="multiple">
					<option value="B2B">B2B Invoices</option>
					<option value="B2BA">B2BA</option>
					<option value="Credit Note">Credit Note</option>
					<option value="Debit Note">Debit Note</option>
					<option value="Credit Note(CDNA)">Credit Note(CDNA)</option>
					<option value="Debit Note(CDNA)">Debit Note(CDNA)</option>
					<option value="ISD">ISD</option>
					<option value="ISDA">ISDA</option>
					<option value="IMPG">IMPG</option>
					<option value="IMPGA">IMPGA</option>
				</select>
				<select id="multiselectUsers" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectSuppliers" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectReverseCharge" class="multiselect-ui form-control" multiple="multiple">
					<option value="Y">YES</option>
					<option value="N">NO</option>
				</select>
				<select id="multiselectITCAvailablity" class="multiselect-ui form-control" multiple="multiple">
					<option value="Y">ITC Available</option>
					<option value="N">ITC Not Available</option>
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idCountGSTR2B">0</div>
				</div>
				<div class="normaltable-col hdr">Total Amount 
					<div class="normaltable-col-txt" id="idTotAmtValGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idTaxableValGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				
				<div class="normaltable-col hdr">Total Exempted
					<div class="normaltable-col-txt" id="idExemptedValGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idTaxValGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total IGST
					<div class="normaltable-col-txt" id="idIGSTGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CGST
					<div class="normaltable-col-txt" id="idCGSTGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total SGST
					<div class="normaltable-col-txt" id="idSGSTGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CESS
					<div class="normaltable-col-txt" id="idCESSGSTR2B"><i class="fa fa-rupee"></i>0.00</div>
				</div>
				
			</div>
		</div>
	</div>
	<h4 class="hdrtitle" style="margin:0px">
	<div id="invview_Process" class=""  style="color:red;font-size:20px;position:absolute;z-index:99;margin-top:134px;top:37%;left: 46%;"></div>
	<div class="customtable db-ca-view tabtable1 invtabtable1 dbTablegstr2b">
		<table id='dbTablegstr2b' class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th>Type</th>
					<th class="text-center">Invoice No</th>
					<th class="text-center" style="max-width:230px!important;width:auto!important;">Suppliers</th>
					<th class="text-center">GSTIN</th><th class="text-center">Invoice Date</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th>
				</tr>
			</thead>
			<tbody id="dbTablegstr2bBody">
			</tbody>
		</table>
	</div>
				</div>
				<div class="tab-pane" id="gtab2b2b" role="tabpanel">
					<h5>Taxable inward supplies received from registered persons</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							 <table id="datatableb2b" class="all_table row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Invoice type</th>
										<th>Invoice number</th>
										<th>GSTIN of supplier</th>
										<th>Trade/Legal name</th>										
										<th>Invoice Date</th>
										<th>Place of supply</th>
										<th>Supply Attract Reverse Charge</th>
										<th>Invoice Value</th>
										<th>Taxable Value</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>GSTR-1/5 Period</th>
										<th>GSTR-1/5 Filing Date</th>
										<th>ITC Availability</th>
										<th>Reason</th>
										<th>Applicable % of Tax Rate</th>
									</tr>
								</thead>
								<tbody id="datatableb2bBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab2b2ba" role="tabpanel">
					<h5>Amendments to previously filed invoices by supplier</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							 <table id="datatableb2ba" class="all_table row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Invoice type</th>
										<th>Invoice number</th>
										<th>GSTIN of supplier</th>
										<th>Trade/Legal name</th>										
										<th>Invoice Date</th>
										<th>Place of supply</th>
										<th>Supply Attract Reverse Charge</th>
										<th>Invoice Value</th>
										<th>Taxable Value</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>GSTR-1/5 Period</th>
										<th>GSTR-1/5 Filing Date</th>
										<th>ITC Availability</th>
										<th>Reason</th>
										<th>Applicable % of Tax Rate</th>
									</tr>
								</thead>
								<tbody id ="datatableb2baBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab23" role="tabpanel">
					<h5>Debit/Credit notes (Original)</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							<table id="datatablecdnr" class="all_table row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Note number</th>
										<th>GSTIN of supplier</th>
										<th>Trade/Legal name</th>
										<th>Note date</th>
										<th>Place of supply</th>
										<th>Supply Attract Reverse Charge</th>
										<th>Note Value</th>
										<th>Taxable Value</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>GSTR-1/5 Period</th>
										<th>GSTR-1/5 Filing Date</th>
										<th>ITC Availability</th>
										<th>Reason</th>
										<th>Applicable % of Tax Rate</th>
									</tr>
								</thead>
								<tbody id="datatablecdnrBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab24" role="tabpanel">
					<h5>Amendments to previously filed Credit/Debit notes by supplier</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							 <table id="datatablecdnra" class="all_table row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Note number</th>
										<th>GSTIN of supplier</th>
										<th>Trade/Legal name</th>
										<th>Note date</th>
										<th>Place of supply</th>
										<th>Supply Attract Reverse Charge</th>
										<th>Note Value</th>
										<th>Taxable Value</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>GSTR-1/5 Period</th>
										<th>GSTR-1/5 Filing Date</th>
										<th>ITC Availability</th>
										<th>Reason</th>
										<th>Applicable % of Tax Rate</th>
									</tr>
								</thead>
								<tbody id="datatablecdnraBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab25" role="tabpanel">
					<h5>ISD Credits</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							<table id="datatableisd" class="all_table row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>ISD Document type</th>
										<th>ISD Document number</th>
										<th>GSTIN of ISD</th>
										<th>Trade/Legal name</th>
										<th>ISD Document date</th>
										<th>Original Invoice Number</th>
										<th>Original invoice date</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>ISD GSTR-6 Period</th>
										<th>ISD GSTR-6 Filing Date</th>
										<th>Eligibility of ITC</th>
									</tr>
								</thead>
								<tbody id="datatableisdBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab26" role="tabpanel">
					<h5>Amendments ISD Credits received</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							<table id="datatableisda" class="all_table row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>ISD Document type</th>
										<th>ISD Document number</th>
										<th>GSTIN of ISD</th>
										<th>Trade/Legal name</th>
										<th>ISD Document date</th>
										<th>Original Invoice Number</th>
										<th>Original invoice date</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>ISD GSTR-6 Period</th>
										<th>ISD GSTR-6 Filing Date</th>
										<th>Eligibility of ITC</th>
									</tr>
								</thead>
								<tbody id="datatableisdaBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab27" role="tabpanel">
					<h5>Import of goods from overseas on bill of entry	</h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							<table id="datatableimpg" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Icegate Reference Date</th>
										<th>Port Code</th>
										<th>Number</th>
										<th>Date</th>
										<th>Taxable Value</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>Amended (Yes)</th>
									</tr>
								</thead>
								<tbody id="datatableimpgBody">
								</tbody>
							</table>
						</div>
				</div>
				<div class="tab-pane" id="gtab28" role="tabpanel">
					<h5>Import of goods from SEZ units/developers on bill of entry<span></h5>
					<div class="customtable db-ca-view tabtable3 all_table_wrap">
							 <table id="datatableimpgsez" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>GSTIN of supplier</th>
										<th>Trade/Legal name</th>
										<th>Icegate Reference Date</th>
										<th>Port Code</th>
										<th>Number</th>
										<th>Date</th>
										<th>Taxable Value</th>
										<th>IGST</th>
										<th>CGST</th>
										<th>SGST</th>
										<th>CESS</th>
										<th>Amended (Yes)</th>
									</tr>
								</thead>
								<tbody id="datatableimpgsezBody">
								</tbody>
							</table>
						</div>
				</div>
				
				</div>
						<!-- table end -->
					</div>
                    </div>

                    <!-- dashboard left block end -->


                </div>

                <!-- Dashboard body end -->
            </div>
        </div>
        <!-- db-ca-wrap end -->
</div>
</div>
<!-- downloadOtpModal Start -->
	<div class="modal fade" id="downloadOtpModal" role="dialog" aria-labelledby="downloadOtpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="downloadOtpModalClose" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Verify OTP</h3>
					</div>
					<div class="group upload-btn p-4" style="min-height:600px;">
						<div class="formboxwrap">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3>
							<h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto">
								<div class="formbox otpbox">
									<form class="meterialform" id="dwnldOtpEntryForm" data-toggle="validator">
										<div class="whitebg">
											<h2> Verify Mobile Number</h2>
											<h6>OTP has been sent to your GSTIN registered mobile number & e-mail, Please enter the same below
											</h6>
											<!-- serverside error begin -->                    
											<div class="errormsg"> </div>
											<!-- serverside error end --> 
											<div class="col-sm-12 otp_form_input">
												<div class="group upload-btn">
													<div class="errormsg" id="otp_Msg"></div>
													<div class="group upload-btn"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="1" placeholder="0" />
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="2" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="3" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="4" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="5" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp otp_seq" id="dotp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="6" placeholder="0"/>
													<div class="help-block with-errors"></div>
												</div>
												<h6>Didn't receive OTP? <a href="">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center">
											<p><a href="#" onClick="validateDownloadOtp()" class="btn btn-lg btn-blue btn-verify">Verify OTP</a></p>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
function updateReturnPeriod(eDate) {
	var month = eDate.getMonth()+1;
	var year = eDate.getFullYear();
	window.location.href = '${contextPath}/addgstr2binvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/GSTR2B/'+month+'/'+year;
}
	$(document).ready(function(){
		loadTables('${id}', '${client.id}','${varRetType}','${month}', '${year}');
		/* $('#dbTablegstr2b,#datatableb2b,#datatableb2ba,#datatable23,#datatable24,#datatable25,#datatable26,#datatable27,#datatable28').DataTable({
			"dom": '<"toolbar"f>lrtip<"clear">',
			ordering: true,
			info: false,
			filter: true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"paging": true,
			"searching": true
		});*/
	    $(".toggler").click(function(e){
	    	e.preventDefault();
	        $('.cat'+$(this).attr('data-prod-cat')).toggle();
	    }); 
	});
	function invokeOTP(btn) {
		var state = "${client.statename}";var gstname = "${client.gstname}";
		$('#dotp_Msg').text('').css("display","none");
		$("#dwnldOtpEntryForm")[0].reset();
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
				$('#downloadOtpModal').modal('show');
			},
			error : function(e, status, error) {}
		});
	}
	function validateDownloadOtp() {
		var otp1 = $('#dotp1').val();var otp2 = $('#dotp2').val();var otp3 = $('#dotp3').val();var otp4 = $('#dotp4').val();var otp5 = $('#dotp5').val();var otp6 = $('#dotp6').val();
		if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){	
			$('#dotp_Msg').text('Please Enter otp').css("display","block");
		}else{
			var otp = otp1+otp2+otp3+otp4+otp5+otp6;
			var pUrl = "${contextPath}/ihubauth/"+otp;
			$("#dwnldOtpEntryForm")[0].reset();
			$.ajax({
				type: "POST",
				url: pUrl,
				async: false,
				cache: false,
				data: JSON.stringify(uploadResponse),
				dataType:"json",
				contentType: 'application/json',
				success : function(authResponse) {
					if(authResponse.status_cd == '1'){closeNotifications();$('#downloadOtpModalClose').click();
					}else{$('#dotp_Msg').text('Please Enter Valid Otp').css('display','block');}
				},
				error : function(e, status, error) {
					$('#downloadOtpModalClose').click();
					if(e.responseText) {errorNotification(e.responseText);}
				}
			});
		}
	}
	function otpTryAgain(){
		$('#dotp_Msg').text('').css("display","none");$("#dwnldOtpEntryForm")[0].reset();
		var state = "${client.statename}";var gstname = "${client.gstname}";
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
			},
			error : function(e, status, error) {}
		});	
	}
</script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.formula.js" type="text/javascript"></script>
</body>

 </html> 