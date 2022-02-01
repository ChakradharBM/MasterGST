<%@page import="com.mastergst.core.common.MasterGSTConstants"%>
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="returntype" value='<%= (String)request.getParameter("returntype") %>'/>
<c:set var="varRetTypeCode" value='${returntype.replaceAll(" ", "_")}'/>
<c:set var="varclientStatus" value='${client.status}'/>
<c:if test="${otherreturn_type ne 'additionalInv' && returntype ne 'EWAYBILL'}">
<div class="invoicesumwrap" >  <ul class="invoicesum">
	<li><h5>Total No Invoices Created</h5>						
		<p>
			<span id="totalInvoices${varRetTypeCode}"></span> 
			<c:choose>
				<c:when test="${returntype eq varGSTR2}"><a class="urllink addInvoice permissionInvoices-Purchase-Add" href="#" onClick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)" link1="${contextPath}/addpinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>?stype="></c:when>
				<c:otherwise><a class="urllink addInvoice permissionInvoices-Sales-Add" href="#" onClick="showInvPopup('<%=MasterGSTConstants.B2B%>','<c:out value="${returntype}"/>',true)" link1="${contextPath}/addsinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>?stype="></c:otherwise>
				</c:choose>Add Invoice</a> | 
				<a href="#" class="addInvoice permissionImport_Invoice <c:if test='${returntype eq varGSTR2 || returntype eq varGSTR4 || returntype eq varGSTR6}'>permissionGeneral-Import_Purchases</c:if><c:if test='${returntype eq varGSTR1 || returntype eq varGSTR5}'>permissionGeneral-Import_Sales</c:if>" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('${returntype}')">Import</a></p>
</li>
 <li> <h5>Pending Uploads to Govt/Gstn</h5>
 <p><span id="totalPending${varRetTypeCode}"></span> <a href="#" class="uploadinvoice permissionGSTN_Actions-Upload_Sales" onClick="invokeUpload()">Upload to Govt</a></p>
</li>
 <li>  <h5>Uploaded to Govt / GSTN</h5><p><p><span id="totalUploaded${varRetTypeCode}"></span> <a href="#" class="submitinvoices permissionGSTN_Actions-Submit_GST_Returns" onClick="invokeSubmit('${id}')">Submit Now</a></p></li>
<li>  <h5>Failed to upload</h5><p><span id="totalFailed${varRetTypeCode}"></span> <c:choose><c:when test="${returntype eq varGSTR1}"><a class="permissionInvoices-Sales-View" href="#" onClick="viewFailed('${returntype}')">View Now</a></c:when><c:when test="${returntype eq varGSTR2}"><a class="permissionInvoices-Purchase-View"href="#" onClick="viewFailed('${returntype}')">View Now</a></c:when><c:otherwise><a class="permission${returntype}-${returntype}" href="#" onClick="viewFailed('${returntype}')">View Now</a></c:otherwise></c:choose></p></li></ul></div>
</c:if>
<c:if test="${returntype ne 'EWAYBILL'}">
<div class="customtable db-ca-view tabtable4">
	<table id="summery_${varRetTypeCode}" class="row-border dataTable meterialform" cellspacing="0" width="100%">
		<thead>
			<tr>
				<th>TYPE OF INVOICE</th><th class="text-right">ORDERING</th><th class="text-right"> NO. INVOICES </th><th class="text-right">TAXABLE AMT</th><th class="text-right">TAX AMT </th><th class="text-right">TOTAL AMT INCL. TAX</th><th class="text-right"></th>
			</tr>
		</thead>
		<tbody id="invoiceSummary"></tbody>
	</table>
</div>
</c:if>
<c:if test="${otherreturn_type ne 'additionalInv' && returntype ne 'EWAYBILL' && returntype eq 'GSTR1'}">
<div class="customtable db-ca-view hsnTable">
	<table id="hsnSummary_${varRetTypeCode}" class="row-border dataTable meterialform" cellspacing="0" width="100%">
		<thead><tr><th class="text-center">HSN/SAC</th><th class="text-center">DESCRIPTION</th><th class="text-center dt-body-right">QUANTITY</th><th class="text-center dt-body-right"><c:choose><c:when test="${month > 4 && year > 2020}">TAX RATE</c:when><c:when test="${month < 4 && year > 2021}">TAX RATE</c:when><c:otherwise>VALUE</c:otherwise></c:choose></th><th class="text-center dt-body-right">TAXABLE</th><th class="text-center dt-body-right">IGST</th><th class="text-center dt-body-right">CGST</th><th class="text-center dt-body-right">SGST</th><th class="text-center dt-body-right">CESS</th></tr></thead>
		<tbody id="hsnSummary"></tbody>
	</table>
</div>
</c:if>
<script type="text/javascript">
	$(function() {
		var retType = '${returntype}';
		if(retType == 'GSTR2'){
			retType = 'Purchase Register';
		}
		$('#summarytab').on('click', function(){	
			loadInvSummeryTable('${client.id}', retType,'${varRetTypeCode}', '${month}', '${year}','${client.status}','${otherreturnType}','${otperror}');
			if('${otherreturnType}' == "" && '${varRetTypeCode}' == "GSTR1"){
				loadHSNSummaryTable('${client.id}', retType,'${varRetTypeCode}', '${month}', '${year}');
			}
		});
	});
	</script>	
				