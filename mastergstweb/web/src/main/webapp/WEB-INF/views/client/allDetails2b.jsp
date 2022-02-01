<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | GSTR2B</title>
<script src="${contextPath}/static/mastergst/js/gstr2b/gstr2b.js" type="text/javascript"></script>
<style>
.db-ca-wrap table.dataTable.row-border tbody tr td:first-child{color: #354052;}
#dbTablegstr2b_filter,#dbTablegstr2b_length{font-weight:100;font-size:16px;}
  #dbTablegstr2b_filter label input { padding:4px 5px; font-size:14px; height:31px; background-image:none; border-radius:3px; width:250px; max-width:440px; border-color:#eaeaea }
  #gtab2alltables .normaltable{margin-bottom:10px}

</style>
</head>
<body>
	<div class="db-ca-wrap" style="padding-top: 0px !important;margin-top:0px!important;">
		<div class="container">
		<div class="" style="display:flow-root;margin-bottom: 10px;">
		<a href="${contextPath}/sync2binvs/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="<%=MasterGSTConstants.GSTR2B%>"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=returns"  id="dwnldFromGstinBtn" onclick = "" class="btn btn-greendark pull-right btn-sm btn-all-iview-sales ml-0">Download From GSTIN</a>
		<a href="${contextPath}/dwnldITCAvailableSummaryxls/${id}/${client.id}/<%=MasterGSTConstants.GSTR2B%>/${month}/${year}?type=returns" id="" onclick = "" class="btn btn-blue-dark btn-sm pull-right mr-2">Download To Excel</a>
			</div>
			<ul class="nav nav-tabs" role="tablist">
				<li class="nav-item"><a class="nav-link gstr2b_lnk active" data-toggle="tab" href="#gtab2alltables" role="tab">GSTR2B All Details</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab2b2b" role="tab">B2B</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab2b2ba" role="tab">B2BA</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab23" role="tab">B2B-CDNR</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab24" role="tab">B2B-CDNRA</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab25" role="tab">ISD</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab26" role="tab">ISDA</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab" href="#gtab27" role="tab">IMPG</a></li>
				<li class="nav-item"><a class="nav-link gstr2b_lnk" data-toggle="tab"href="#gtab28" role="tab">IMPGSEZ</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active" id="gtab2alltables" role="tabpane1">
					<div class="normaltable meterialform">
						<div class="filter">
							<div class="noramltable-row">
								<div class="noramltable-row-hdr">Filter</div>
								<div class="noramltable-row-desc">
									<div class="sfilter">
										<span id="divFiltersGstr2b"></span> <span class="btn-remove-tag" onclick="clearGstr2bFilters()">Clear All<span data-role="remove"></span>
										</span>
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
									<div class="normaltable-col-txt" id="idTotAmtValGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>
								<div class="normaltable-col hdr">Total Taxable Value
									<div class="normaltable-col-txt" id="idTaxableValGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>

								<div class="normaltable-col hdr">Total Exempted
									<div class="normaltable-col-txt" id="idExemptedValGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>

								<div class="normaltable-col hdr">Total Tax Value
									<div class="normaltable-col-txt" id="idTaxValGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>
								<div class="normaltable-col hdr filsummary">Total IGST
									<div class="normaltable-col-txt" id="idIGSTGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>
								<div class="normaltable-col hdr filsummary">Total CGST
									<div class="normaltable-col-txt" id="idCGSTGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>
								<div class="normaltable-col hdr filsummary">Total SGST
									<div class="normaltable-col-txt" id="idSGSTGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>
								<div class="normaltable-col hdr filsummary">Total CESS
									<div class="normaltable-col-txt" id="idCESSGSTR2B">
										<i class="fa fa-rupee"></i>0.00
									</div>
								</div>

							</div>
						</div>
					</div>
					<h4 class="hdrtitle" style="margin: 0px">
						<div id="invview_Process" class="" style="color: red; font-size: 20px; position: absolute; z-index: 99; margin-top: 134px; top: 37%; left: 46%;"></div>
						<div
							class="customtable db-ca-view tabtable1 invtabtable1 dbTablegstr2b">
							<table id='dbTablegstr2b' class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th class="text-center">Invoice No</th>
										<th class="text-center" style="max-width: 230px !important; width: auto !important;">Suppliers</th>
										<th class="text-center">GSTIN</th>
										<th class="text-center">Invoice Date</th>
										<th class="text-center">Taxable Amt</th>
										<th class="text-center">Total Tax</th>
										<th class="text-center">Total Amt</th>
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
		</div>
	</div>
</body>
<script>
$(document).ready(function(){
	loadTables('${id}', '${client.id}','${varRetType}','${month}', '${year}');
});
</script>
</html>