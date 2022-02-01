<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<%@page import="com.mastergst.usermanagement.runtime.support.InvoiceListSupport"%> 
<c:set var="varRetType" value='<%= (String)request.getParameter("returntype") %>'/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<style>
.nav-bread{height:35px!important}
.db-ca-wrap{padding-top: 99px!important;} 
</style>
<!DOCTYPE html>
<html>
<head>
<title>E-Invoice</title>
</head>

<h4 class="hdrtitlefiling" style="color: #404144; font-size: 18px;  margin-bottom: 5px; line-height: 2; display: table; width: 100%;">
<span class="esummary_retperiod" style="float: left;">E-Invoices</span>
<span class="text-right" style="float: right;margin-top: -19px;font-size:18px;">Total Amount : <span class="total_amount" style="font-size:27px; margin-left:0px!important"><span style="font-size:26px;" id="idTabTotalEINVOICE"></span></span></span>
<span class="total_amount" style="font-size:27px; margin-left:0px!important"><span class="ind_format" id=""></span></span>
</h4>
<div class="normaltable meterialform" id="updtEinvfilter_summary" style="display:none;">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divFiltersEINVOICE"></span>
					<span class="btn-remove-tag" onclick="clearEInvFilters('EINVOICE')">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div>
			<div class="noramltable-row-desc">
					<select id="emultiselectEINVOICE1" class="multiselect-ui form-control" multiple="multiple">
					<option value="Generated">Generated</option><option value="Not Generated">Not Generated</option><option value="Cancelled">Cancelled</option><option value="Failed">Failed</option>
					
					</select>
				<span class="invoiceTypelist">
				<select id="emultiselectEINVOICE2" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${InvoiceListSupport.getInvTypeList(varRetType)}" var="invTypeLi">
						<option value="${invTypeLi.key}">${invTypeLi.value}</option>
					</c:forEach>
				</select>
				</span>
			
				<select id="emultiselectEINVOICE3" class="multiselect-ui form-control" multiple="multiple">
				<!--  -->
				</select>
			<select id="emultiselectEINVOICE4" class="multiselect-ui form-control" multiple="multiple">
					<option value="N">Regular</option>
					<option value="Y">Reverse</option>
			</select>
			<select id="emultiselectEINVOICE5" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.branches}" var="branch">
						<option value="${branch.name}">${branch.name}</option>
					</c:forEach>
				</select>
				<select id="emultiselectEINVOICE6" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.verticals}" var="vertical">
						<option value="${vertical.name}">${vertical.name}</option>
					</c:forEach>
				</select> 
			<select id="emultiselectEINVOICE7" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="emultiselectEINVOICE8" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="emultiselectEINVOICE9" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="emultiselectEINVOICE10" class="multiselect-ui form-control" multiple="multiple"></select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idCountEINVOICE"></div>
				</div>
				<div class="normaltable-col hdr">Total Amount 
					<div class="normaltable-col-txt" id="idTotAmtValEINVOICE"></div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idTaxableValEINVOICE"></div>
				</div>
				
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idTaxValEINVOICE"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total IGST
					<div class="normaltable-col-txt" id="idIGSTEINVOICE"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CGST
					<div class="normaltable-col-txt" id="idCGSTEINVOICE"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total SGST
					<div class="normaltable-col-txt" id="idSGSTEINVOICE"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CESS
					<div class="normaltable-col-txt" id="idCESSEINVOICE"></div>
				</div>
				
			</div>
		</div>
	</div>

<h4 class="hdrtitle" style="margin:0px">
<a  href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-esm permissionEinvoice_Actions-Generate_IRN disabled" id="genIRN" onclick="allInvGenerateIRN()">GENERATE IRN</a>
<a  href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-esm permissionEinvoice_Actions-Cancel_IRN disabled" id="cancelIRN" onclick="showEinvoiceCancelPopup('EINVOICE')">CANCEL IRN</a>
<a type="button" class="btn btn-greendark pull-right btn-all-iview-sales btn-esm disabled" data-toggle="tooltip" title="We can get last 3 days irn generated data" id="synceinv" style="color:white; box-shadow:none; font-size:14px" onclick="updateEinvInvoiceStatus()" link="${contextPath}/syncirnstatus/${id}/${fullname}/${usertype}/${client.id}/EINVOICE/${month}/${year}">Sync<i class="fa fa-refresh" style="font-size: 14px; color: #fff; margin-left:5px"></i></a>
<!-- <a  href="#" class="btn btn-blue" id="createEinvoice" data-toggle="modal" data-target="#einvoiceModal">Create E-Invoice</a> -->
<div class="dropdown pull-right" style="margin-left: 10px;"><div class="split-button-menu-dropdown invoicemenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="menudropdown" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue invoicemenu" id="idmenudropdown" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Create E-Invoice</button><div class="dropdown-menu invoicedrop" id="invoicedrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="#" onClick="showEinvoicePopup('<%=MasterGSTConstants.B2B%>','<c:out value="${varRetType}"/>',true)">ADD B2B</a><a class="dropdown-item" href="#" onClick="showEinvoicePopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<c:out value="${varRetType}"/>',true)">CREDIT/DEBIT NOTES</a><a class="dropdown-item" href="#" onClick="showEinvoicePopup('<%=MasterGSTConstants.EXPORTS%>','<c:out value="${varRetType}"/>',true)">EXPORT INVOICE</a><a class="dropdown-item" href="#" onClick="showEinvoicePopup('<%=MasterGSTConstants.B2C%>','<c:out value="${varRetType}"/>',true)">B2C INVOICE</a></div></div></div>


<!-- <a  href="#" class="btn btn-blue btn-esm disabled" id="importEinvoice">Import</a> -->
<a href="" data-toggle="modal" data-target="#einvimportModal" class="btn btn-blue pull-right btn-all-iview-sales permissionGeneral-Import_Sales">Import</a>
<div class="dropdown pull-right" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showEinvDeletePopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs')" class="pull-right dropdown-item disabled" id="deleteEInvoices">DELETE</a>
				<a href="#" onclick="showEinvDeletePopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','deleteAll')" class="pull-right dropdown-item">DELETE ALL</a>
				</div>
			</div>
		</div>
<div class="dropdown pull-right" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/EINVOICE/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/EINVOICE/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/EINVOICE/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div>
</h4>

	<div class="modal fade" id="einvimportModal" role="dialog" aria-labelledby="einvimportModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content" id="einvidImportBody">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="invoice-hdr bluehdr"><h3>Import E-Invoices</h3></div>
					<div class="row alert alert-danger mr-1 ml-2" id="eimporterr" role="alert" style="display:none;"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" /><span id="eimportSummaryError"></span></div>
					<div class="row p-3">
						<div class="errormsg col-md-12" id="etemplateType_Msg" style="position:relative;left:49%;"></div>
							<div class="pl-5 pr-5">
								<form:form method="POST" class="meterialform" id="einvimportModalForm" enctype="multipart/form-data" action="${contextPath}/uploadeInvoice/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}/mastergsteinv">
										<div class="row meterialform">
											<div class="col-md-4 p-0 mb-2 text-right"><h6><span class="astrich imp"></span>Select Template Type : </h6></div>
											<div class="col-md-6">
												<select id="etemplateType" name="templatetype" style="width:90%" onchange="updateEInvoiceTypeUrl(this.value)">
													<option value="">-Select Template Type-</option>
													<option value="mastergst">MasterGST E-invoice Template</option>
													<option value="sage">Sage Template</option>
													<c:if test = "${user.accessEntertainmentEinvoiceTemplate eq true}">
														<option value="entertainment">Entertainment E-Invoice</option>
													</c:if>
													<c:forEach items="${mappers}" var="mapper">
														<c:if test='${mapper.mapperType == "einvoice"}'>
															<option value="${mapper.id}">${mapper.mapperName}</option>
														</c:if>
													</c:forEach>
												</select>
											</div>
											<div class="col-md-4 p-0 mb-2 text-right"><h6 class="vert_branch">Branch : </h6></div><div class="col-md-6"><select  name="branch" style="width:90%"><option value="">- Branch -</option><c:forEach items="${client.branches}" var="branch"><option value="${branch.name}">${branch.name}</option></c:forEach></select></div>
											<div class="col-md-4 p-0 mb-2 text-right"><h6 class="vert_branch vert_text">Vertical : </h6></div><div class="col-md-6"><select name="vertical" style="width:90%"><option value="">- Vertical -</option><c:forEach items="${client.verticals}" var="vertical"><option value="${vertical.name}">${vertical.name}</option></c:forEach></select></div>
									</div>
									<div class="row">
											<fieldset style="width:  100%;">
					                              <div class="filedragwrap" onclick="chooseefileSheets()">
											              <div id="efiledrag1" style="display: block;">
												                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
												                <div class="filedraginput"> <span id="chosefile-cnt">Choose File</span><input type="file" name="file" id="efileselect1" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" onchange="updateESheets()">  </div>
												                <div class="ortxt"> --( OR )--</div> <div id="efiledrag1" >Drop files here</div>
											              </div> 
					             				 </div>
					           			 </fieldset>	
					           			 <div class="col-md-12 col-sm-12 alert alert-danger mr-1 ml-2" id="eimporterr" role="alert" style="display:none;"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" /><span id="eimportSummaryError"></span></div>
												<div class="form-group col-md-12 col-sm-12" id="eidSheet" style="display:none;">
													<p class="lable-txt">File Name : <span id="emessages"></span> </p><div class="">&nbsp;</div>
													<div class="form-check form-check-inline" style="position:inherit;"><input class="form-check-input" type="checkbox" id="einvoiceSheet" value="invoiceList" name='list' checked="checked" /><label for="invoiceSheet"><span class="ui"></span></label> <span class="labletxt">Invoices</span> </div><div class=""></div>
													<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="ecreditSheet" value="creditList" name='list' checked="checked" /><label for="creditSheet"><span class="ui"></span></label> <span class="labletxt">Credit/Debit Notes</span></div><div class=""></div>
													<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="crediturSheet" value="cdnurList" name='list' checked="checked" /><label for="crediturSheet"><span class="ui"></span></label> <span class="labletxt">Credit/Debit Note for Unregistered</span></div><div class=""></div>
													<div class="form-check form-check-inline sales_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="eexportSheet" value="exportList" name='list' checked="checked" /><label for="exportSheet"><span class="ui"></span></label> <span class="labletxt">Exports</span></div>
												</div>
												<div class="form-group col-4"><input type="button" class="btn btn-blue-dark" onClick="performEinvImport(this)" value="Import Invoices"/></div>
												<div class="form-group col-12" id="emsgImportProgress" style="display:none;">We are importing your invoices. It may take few seconds based on your network bandwidth. Please wait.. </div>
									</div>
						</form:form>
						</div>
						<div class="mt-4"><div class="form-group col-md-12 col-sm-12"><div class="rightside-wrap"><h5>Download Templates</h5><ul>
						<li class="col-12 einv_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_einvoices_Template.xls">MasterGST E-invoice Template</a></li>
						<li class="col-12 einv_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_einvoices_Template.xls">Sage E-Invoice Template</a></li>
						<li class="col-12 einv_template entertaiment_einvoice_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Entertainment_Mastergst_Einvoice_Template.xls">Entertainment E-Invoice Template</a></li>
						</ul></div></div></div>
            </div>
        </div>
    </div>
	<div class="modal-content" id="eidImportSummary" style="display:none;">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Import Invoice Summary </h3></div> <div class="">&nbsp;</div>
		<div class="col-11 m-auto">
        <p>We have processed all your invoices and find the below Import invoices Summary, for Failed invoices you can download from the error log and correct the invoices and re-import again.</p>
          <table width="100%" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover"><thead> 
          	<tr> 
          		<th></th>
            	<th colspan="2">Totals</th>
            	<th colspan="2">Imported</th>
            	<th colspan="2">Failed</th>
            </tr>
              <tr>
                <th>Invoice Name</th>
                <th>Line Items</th>
                <th>Invoices</th>
                <th>Line Items</th>
                <th>Invoices</th>
                <th>Line Items</th>
                <th>Invoices</th>
              </tr></thead> <tbody id="eimportSummaryBody"></tbody> </table>
          <table width="100%" border="0" cellspacing="0" id="einverrorXls" cellpadding="5" class="table table-inverse" style="display:none"><tr><td>Download Error Log file <a class="ml-3" href="${contextPath}/geterrorxls"><i class="fa fa-file-excel-o"></i> Error Sheet</a></td><td class="redtxt"></td></tr></table>
        </div>
      </div>
      <div class="modal-footer">
      <a href="${contextPath}/einvoice/${id}/${fullname}/${usertype}/${returntype}/${client.id}/${month}/${year}" class="btn btn-blue-dark urllink" >Continue..</a>
      <a href="${contextPath}/einvoice/${id}/${fullname}/${usertype}/${returntype}/${client.id}/${month}/${year}" class="btn btn-secondary urllink">Close</a>
      </div>
    </div>
	</div>
</div>


    <!-- Delete Modal Start-->
<div class="modal fade" id="deleteEinvModal" tabindex="-1" role="dialog" aria-labelledby="deleteEinvModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Invoice </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete <span id="deleinvDetails"></span></h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> 1)Deletes only Not Generated & Failed Invoices.<br><span style="margin-left:30px">2)Once invoices are deleted, it cannot be reversed.</span></p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete_einv" data-dismiss="modal">Delete Invoice</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
  <!-- Delete Modal End-->
  <script src="${contextPath}/static/mastergst/js/common/einvfiledrag-map.js" type="text/javascript"></script>
<script>
var einvAuthSttauss = '<c:out value="${einvAuthSttaus}"/>';
$(function() {
	var einvAuthSttaus = '<c:out value="${einvAuthSttaus}"/>';
	einvsubscriptioncheck();
	einitiateCallBacksForMultiSelect('${varRetType}','${varRetTypeCode}');
	eloadInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}');
	eloadInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}','${month}', '${year}', loadeCustomersInDropdown);	
	eloadEinvoiceAuthStatus('${varRetType}','${varRetTypeCode}',einvAuthSttaus);
	//TODO need to apply the filter manipulation		
	einitializeRemoveAppliedFilters('${varRetType}','${varRetTypeCode}');
			
});

function updateEInvoiceTypeUrl(invType) {
	if(invType == 'mastergst') {
		$("#einvimportModalForm").attr("action","${contextPath}/uploadeInvoice/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}/mastergsteinv");
	}else if(invType == 'sage'){
		$("#einvimportModalForm").attr("action","${contextPath}/uploadeInvoice/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}/sage");
	}else if(invType == 'entertainment'){
		$("#einvimportModalForm").attr("action","${contextPath}/uploadeInvoice/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}/allu");
	}else{
		$("#einvimportModalForm").attr("action","${contextPath}/uploadEinvmapImportInvoice/"+invType+"/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}");
	}
}
</script>
</html>