<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="varRetType" value='<%= (String)request.getParameter("returntype") %>'/>
<c:set var="tabName" value='<%= (String)request.getParameter("tabName") %>'/>
<c:set var="urlSuffix" value="${id}/${fullname}/${usertype}/${client.id}/${varRetType}"/>
<c:set var="misurlSuffix" value="${id}/${fullname}/${usertype}/${client.id}"/>
<c:set var="varGSTR1" value="<%=MasterGSTConstants.GSTR1%>"/>
<c:set var="varGSTR1A" value="<%=MasterGSTConstants.GSTR1A%>"/>
<c:set var="varGSTR2" value="<%=MasterGSTConstants.GSTR2%>"/>
<c:set var="varGSTR2A" value="<%=MasterGSTConstants.GSTR2A%>"/>
<c:set var="varGSTR3B" value="<%=MasterGSTConstants.GSTR3B%>"/>
<c:set var="varGSTR4" value="<%=MasterGSTConstants.GSTR4%>"/>
<c:set var="varGSTR6" value="<%=MasterGSTConstants.GSTR6%>"/>
<c:set var="varGSTR5" value="<%=MasterGSTConstants.GSTR5%>"/>
<c:set var="varANX1" value="<%=MasterGSTConstants.ANX1%>"/>
<c:set var="varANX1A" value="<%=MasterGSTConstants.ANX1A%>"/>
<c:set var="varANX2" value="<%=MasterGSTConstants.ANX2%>"/>
<c:set var="varANX2A" value="<%=MasterGSTConstants.ANX2A%>"/>
<c:set var="varEWAYBILL" value="<%=MasterGSTConstants.EWAYBILL%>"/>
<c:set var="varPurchase" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>"/>
<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
<c:set var="varDeliverchallan" value="<%=MasterGSTConstants.DELIVERYCHALLANS%>"/>
<c:set var="varProforma" value="<%=MasterGSTConstants.PROFORMAINVOICES%>"/>
<c:set var="varEstimates" value="<%=MasterGSTConstants.ESTIMATES%>"/>
<c:set var="varPurchaseOrder" value="<%=MasterGSTConstants.PURCHASEORDER%>"/>
<script src="${contextPath}/static/mastergst/js/client/gstr2breconcile.js" type="text/javascript"></script>
<span style="font-size:13px; color:#0dce2f; font-weight:bold;margin-left:3px;float: right;"> 
	<c:if test="${empty client.gstr2bReconcileDate }"><span style="color:red;font-size:13px">You Haven't Reconciled Yet,</span></c:if>
	<c:if test="${not empty client.gstr2bReconcileDate }">Last Reconciled on: ${client.gstr2bReconcileDate}</c:if>
</span>
<h4 class="hdrtitle">
	<span class="summary_retperiod">
		<a href="#" data-toggle="modal" data-target="#reconcile2bmodal" style="color: #404144;text-decoration: underline;">GSTR2B Reconcile Summary</a>
	</span>
	<label> , Reconciled By</label>
	<select class="invoiceview" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByGstr2bReconcileTrDate('${id}',this.value,'Purchase Register','Purchase_Register','${client.address}')">
		<option value="Invoice Date">Invoice Date</option>
		<option value="Transaction Date">Transaction Date</option>
	</select>
	<c:if test="${reconcileCounts eq null}">
		<a href="#" class="btn btn-blue 2breconbtn reconbtn d-none" onClick="reconcileMonthlyGstr2b('${clientid}','${id}')">Reconcile Now</a>
	</c:if>
	<c:if test="${not empty reconcileCounts}">
		<a href="#" class="btn btn-blue 2breconbtn reconbtn d-none" onClick="reconcileMonthlyGstr2b('${clientid}','${id}')">Reconcile Now</a>
		<span style="font-size: 14px;" class="rccn"><img src="${contextPath}/static/mastergst/images/master/gears.gif" width="40px" style="margin-left:6px;margin-right:6px;"/> <a href="#" onclick="verify2bReconsilation('${client.id}')">click here </a>for status</span>
	</c:if>
	<span style="font-size: 14px;" class="processverification d-none"><c:out value="${reconcileCounts}"/>  <img src="${contextPath}/static/mastergst/images/master/gears.gif" width="40px" style="margin-left:6px;margin-right:6px;"/> <a href="#" onclick="verify2bReconsilation('${client.id}')">click here </a>for status</span>
	<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=yearlyGstr2bRecocileReport" class="btn btn-blue yearreconbtn">Yearly Reconcile</a>
	<div class="dropdown" style="color: #404144;;margin-left:20px;font-size: 17px;float:right">Reconcile Rules<div class="dropdown-content" style="z-index:3"><span class="arrow-right"></span><div>Allowed Tax/Invoice Amount Difference for Reconcile<span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.reconcileDiff}">${clientConfig.reconcileDiff}  </c:when><c:otherwise>0.00</c:otherwise></c:choose> Rupees</span><br/>Allowed days for Invoice Date Difference for Reconcile<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.allowedDays}"><fmt:formatNumber type="number" pattern="###" value="${clientConfig.allowedDays}" />  </c:when><c:otherwise>0</c:otherwise></c:choose> Days</span>
		<br/>Considers as Matched if Invoice Number matches anywhere in the Invoice Number<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableInvoiceMatch}"><c:if test="${clientConfig.enableInvoiceMatch eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableInvoiceMatch eq false}"><span style="color:red">No</span></c:if> </c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
		<br/>Ignore "/"(Backward Slash)<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreSlash}"><c:if test="${clientConfig.enableIgnoreSlash eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreSlash eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
		<br/>Ignore "-"(Hyphen)<span class="colon" style="margin-left: 51px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreHyphen}"><c:if test="${clientConfig.enableIgnoreHyphen eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreHyphen eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
		<br/>Ignore "0"(Zero/O)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreZero}"><c:if test="${clientConfig.enableIgnoreZero eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreZero eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
		<br/>Ignore "I"(Capital i)<span class="colon" style="margin-left: 50px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreI}"><c:if test="${clientConfig.enableIgnoreI eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreI eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
		<br/>Ignore "l"(Small l)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreL}"><c:if test="${clientConfig.enableIgnoreL eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreL eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
		<a href="#" onClick="moreReconcileConfig()" >Click here for more configurations....</a></div></div>
	</div> 
</h4>
<%-- <jsp:include page="/WEB-INF/views/client/gstr2bfilter.jsp" /> --%>

<!-- gstr2b reconcile filters start -->
<div class="normaltable gstr2bnormaltable meterialform">
	<div class="filter gstr2bfilter">
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter</div>
			<div class="noramltable-row-desc">
			<div class="Gstr2bReconsilationfilter">
				<span id="divselGstr2bReconsilationInvArrayFilters"></span>
				<span class="btn-remove-tag" onclick="clearGstr2bReconsilationFilters('All')">Clear All<span data-role="remove"></span></span>
			</div>
			</div>
		</div>
	</div>
	<div class="noramltable-row">
		<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
		<div class="noramltable-row-desc">
			<select id="Gstr2bReconsilationMultiselect1" class="multiselect-ui form-control" multiple="multiple">
				<option value="Mismatched">Mismatched</option>
				<option value="Matched">Matched</option>
				<option value="Round Off Matched">Round Off Matched</option>
				<option value="Manual Matched">Manual Matched</option>
				<option value="Matched In Other Months">MATCHED IN OTHER MONTHS</option>
				<option value="Probable Matched">Probable Matched</option>
				<option value="Invoice No Mismatched">Invoice No Mismatched</option>
				<option value="Invoice Value Mismatched">Invoice Value Mismatched</option>
				<option value="Invoice Date Mismatched">Invoice Date Mismatched</option>
				<option value="GST No Mismatched">GST No Mismatched</option>
				<option value="Tax Mismatched">Tax Mismatched</option>
				<option value="Not In Purchases">Not In Purchases</option>
				<option value="Not In GSTR2B">Not In GSTR2B</option>
			</select>
			<select id="Gstr2bReconsilationMultiselect2" class="multiselect-ui form-control" multiple="multiple">
				<option value="B2B">B2B Invoices</option>
				<option value="Debit Note">Debit Note</option>
				<option value="Credit Note">Credit Note</option>
				<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
			</select>
			<span class="multiselectuserlist">
				<select id="Gstr2bReconsilationMultiselect3" class="multiselect-ui form-control" multiple="multiple">
				</select>
			</span>
			<span class="missmultiselectuserlist">
				<select id="Gstr2bReconsilationMultiselect4" class="multiselect-ui form-control" multiple="multiple">
			</select>
			</span>
			<select id="Gstr2bReconsilationMultiselect5" class="multiselect-ui form-control" multiple="multiple">
				<c:forEach items="${client.branches}" var="branch">
					<option value="${branch.name}">${branch.name}</option>
				</c:forEach>
			</select>
			<select id="Gstr2bReconsilationMultiselect6" class="multiselect-ui form-control" multiple="multiple">
				<c:forEach items="${client.verticals}" var="vertical">
					<option value="${vertical.name}">${vertical.name}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="noramltable-row">
		<div class="noramltable-row-hdr">Filter Summary</div>
		<div class="noramltable-row-desc">
			<div class="normaltable-col hdr">Total Invoices
				<div class="normaltable-col-txt" id="idGstr2bReconsilationCount"></div>
			</div>
			<div class="normaltable-col hdr">Total Amount
				<div class="normaltable-col-txt" id="idGstr2bReconsilationTotAmtVal"></div>
			</div>
			<div class="normaltable-col hdr">Total Taxable Value
				<div class="normaltable-col-txt" id="idGstr2bReconsilationTaxableVal"></div>
			</div>
			<div class="normaltable-col hdr">Total Tax Value
				<div class="normaltable-col-txt" id="idGstr2bReconsilationTaxVal"></div>
			</div>
			<div class="normaltable-col hdr">Total IGST
				<div class="normaltable-col-txt" id="idGstr2bReconsilationIGST"></div>
			</div>
			<div class="normaltable-col hdr">Total CGST
				<div class="normaltable-col-txt" id="idGstr2bReconsilationCGST"></div>
			</div>
			<div class="normaltable-col hdr">Total SGST
				<div class="normaltable-col-txt" id="idGstr2bReconsilationSGST"></div>
			</div>
			<div class="normaltable-col hdr">Total CESS
				<div class="normaltable-col-txt" id="idGstr2bReconsilationCESS"></div>
			</div>
		</div>
	</div>
</div>
<!-- gstr2b reconcile filters end-->

<h4 class="hdrtitle"style="position: relative;margin-top: -15px!important; z-index:2;">
	<a href="#" class="btn btn-blue reconModal g2bMannualMatching disable" data-toggle="modal" data-target="#reconModal" onclick="g2bMannualMatchingInv('${client.id}','monthly')">Manual Match</a>
	<div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Reconcile" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu">
	<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldReconsilexls2b/${id}/${client.id}/<%=MasterGSTConstants.GSTR2%>/${month}/${year}?dwnldtype=invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldReconsilexls2b/${id}/${client.id}/<%=MasterGSTConstants.GSTR2%>/${month}/${year}?dwnldtype=alldetailswise">All DETAILS DOWNLOAD</a></div></div></div>
</h4>
<div class="customtable db-ca-view tabtable2 reconcile2btable">
	<div class="customtable db-ca-view">
		<span class="mt-1 select_msg" style="position:absolute;left:60%;color:#5769bb;"></span>
		<table id="gstr2bReconsileDbTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th class="text-center">
						<div class="checkbox"> 
							<label>
								<input type="checkbox" id="checkMismatch" onClick="updateGstr2bMainMisMatchSelection(this)"/><i class="helper"></i>
							</label>
						</div>
					</th>
					<th></th><th> Type</th><th class="text-center">Suppliers</th><th>Ret.Period</th>
					<th class="text-center">Invoice Number</th>
					<th class="text-center">Invoice Date</th>
					<th class="text-center">GSTN No</th><th class="text-center">Invoice Value</th>
					<th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th>
					<th class="text-center">Status</th>
					<!-- <th class="text-center">Branch</th><th class="text-center">Vertical</th>
					<th class="text-center">Uploaded By</th> -->
					<th class="text-center">Notes</th>
				</tr>
			</thead>
			<tbody id="gstr2bReconsileDbTableBody"></tbody>
		</table>
	</div>
</div>
<div class="modal fade" id="reconcile2bprocessModal" tabindex="-1" role="dialog" aria-labelledby="reconcile2bprocessModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document" style="max-width: unset; width: 50%;">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Reconciliation Process</h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
        	<div class="gstr2breconprogress">
        	<p><span id="gstr2bverificationType"> </span>&nbsp;Reconciliation is in Process, Initiated by :&nbsp; <span id="gstr2breconinitiatedby"></span></p>
        	<h5>Reconciliation Status</h5>
          <p><span id="gstr2breconrtntype"></span> : <span id="gstr2bprocessInfo" style="font-weight:bold"></span> invoices out of <span id="gstr2bverificationInfo"  style="font-weight:bold"></span></p>
          <p class="gstr2bprinfo" style="display:none;">Purchase Register : <span id="gstr2bprocessPrInfo" style="font-weight:bold"></span> invoices out of <span id="gstr2bverificationPrInfo"  style="font-weight:bold"></span></p>
          </div>
          <div class="gstr2breconcompleted">
          	<p><span id="gstr2breconcomplete"></span></p>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
$(function(){
	if(otherconfigdetails.enableTransDate == true){
		$('.invoiceview').val('Transaction Date');
	}else{
		$('.invoiceview').val('Invoice Date');
	}
	var isgstr2bmisMatchTabLoaded = false;
	$('#gstr2bmisMatchTab').on('click',function() {
		if(!isgstr2bmisMatchTabLoaded){
			loadGstr2bReconcileUsersByClient('${id}', '${client.id}', loadGstr2bReconcileUsersInDropDown);
			loadGstr2bReconcileInvoiceSupport('${id}', '${client.id}', '${month}', '${year}', loadGstr2bReconcileCustomersInDropdown);
			loadGstr2bReconcileInvTable('${id}', '${client.id}', '${month}', '${year}');
			initiateCallBacksForMultiSelectGstr2bReconcile();
			initializeRemoveAppliedFiltersGstr2bReconcile();
			loadGstr2bReconcileSummary('${id}', '${client.id}', '${month}', '${year}');
			isgstr2bmisMatchTabLoaded = true;
		}
	});
	var rc2b = "${reconcileCounts}";
	if(rc2b != null && rc2b != ""){
		$('.2breconbtn').css('display','none').addClass('d-none');
		execute2bQuery('${client.id}');
	}else{
		$('.2breconbtn').css('display','inline-block').removeClass('d-none');
	}
});

function initGstr2bReconcileTab(){
	loadGstr2bReconcileUsersByClient('${id}', '${client.id}', loadGstr2bReconcileUsersInDropDown);
	loadGstr2bReconcileInvoiceSupport('${id}', '${client.id}', '${month}', '${year}', loadGstr2bReconcileCustomersInDropdown);
	loadGstr2bReconcileInvTable('${id}', '${client.id}', '${month}', '${year}');
	initiateCallBacksForMultiSelectGstr2bReconcile();
	initializeRemoveAppliedFiltersGstr2bReconcile();
	loadGstr2bReconcileSummary('${id}', '${client.id}', '${month}', '${year}');
	var rc2b = "${reconcileCounts}";
	if(rc2b != null && rc2b != ""){
		execute2bQuery('${client.id}');
	}
}
function reconcileMonthlyGstr2b(){
	$('.2breconbtn').addClass("btn-loader");
	$('.2breconbtn').html("Reconciling...");
	$.ajax({
		url : '${contextPath}/verifySubscription/${id}/${client.id}',
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			$('.reconcile_btn,.2breconbtn').addClass("d-none");
			$('.processverification').removeClass("d-none");
			if(response == 'verified') {
				var urlStr = '${contextPath}/gstr2breconcileinv/${id}/${fullname}/${usertype}/${client.id}/GSTR2A/${month}/${year}?reconcileType=';
				$.ajax({
					url: urlStr,
					async: true,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
					}
				});
				setTimeout(function() { execute2bQuery('${client.id}'); }, 5000);
			}else{
				$('.processverification,.rccn').addClass("d-none");
		    	  $('.2breconbtn').removeClass("btn-loader");
		    	  $('.2breconbtn').html("Reconcile Now");
		    	  $('.2breconbtn').removeClass("d-none");
				errorNotification(response);
			}
		},error : function(error){
		}
	});	
}

</script>