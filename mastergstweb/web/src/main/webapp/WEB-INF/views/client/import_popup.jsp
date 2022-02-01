<div class="modal fade" id="importModal1" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content" id="idImportBody">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3 id="importsTitle">Import <c:choose><c:when test="${returntype ne varPurchase && returntype ne varGSTR2 && returntype ne varGSTR6}">Sales Invoices</c:when><c:otherwise>Purchase Invoices</c:otherwise></c:choose></h3>
                    </div>
					<div class="row p-2">
						<span class="errormsg" id="templateType_Msg" style="position:relative;left:51%"></span>
						<div class="meterialform col-sm-12" style="margin-bottom: 10px;">
						 <h6 style="display:inline-block;margin-right:6%" id="sel_inv_Type"><span class="astrich"></span>Select Template Type : </h6>
							<c:choose><c:when test="${returntype ne varPurchase && returntype ne varGSTR2 && returntype ne varGSTR6}">
							<select id="templateType" name="templatetype" onchange="updateInvoiceTypeUrl(this.value)" style="width: 265px;">
								<option value="">- Select Template Type -</option>
								<option value="mastergst">MasterGST Template</option>
								<option value="tally">Tally Template</option>
								<option value="tallyv17">Tally Template-V1.7</option>
								<option value="tallyprime">Tally Prime Template</option>
								<option value="sage">Sage Template</option>
								<option value="offlineutility">GSTR1 Offline Utility</option>
								<c:forEach items="${mappers}" var="mapper">
							<c:if test='${mapper.mapperType == "Sales"}'>
							<option value="${mapper.id}">${mapper.mapperName}</option>
							</c:if>
							</c:forEach>
							</select>
							</c:when>
							<c:otherwise><select id="templateType" name="templatetype" onchange="updateInvoiceTypeUrl(this.value)" style="width: 265px;">
								<option value="">- Select Template Type -</option>
								<option value="mastergst">MasterGST Template</option>
								<option value="tally">Tally Template</option>
								<option value="sage">Sage Template</option>
								<option value="walterpack">Single Sheet Template of Purchase Invoice</option>
								<c:forEach items="${mappers}" var="mapper">
							<c:if test='${mapper.mapperType == "Purchases"}'>
							<option value="${mapper.id}">${mapper.mapperName}</option>
							</c:if>
							</c:forEach>
							</select></c:otherwise></c:choose>
                        </div>
						<div class="pl-5 pr-5">
						<form:form method="POST" class="meterialform" id="uploadInvoiceForm" action="${contextPath}/uploadInvoice/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}/mastergst" enctype="multipart/form-data" onSubmit="return fileSizeValidate();">
						<div class="row">
							<div class="meterialform col-sm-12" style="margin-bottom:  10px;">
								<h6 style="display:inline-block;margin-right: 36px;text-align:  left;margin-left: 81px;text-align:  right;" class="imp_branch">Branch : </h6>
								<select name="branch" style="width:265px;">
									<option value="">- Branch -</option>
									<c:forEach items="${client.branches}" var="branch">
									<option value="${branch.name}">${branch.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="row">
							<div class="meterialform col-sm-12" style="margin-bottom:  10px;">
								<h6 style="display:inline-block; margin-right: 35px;margin-left: 80px;text-align:right" class="vert_branch">Vertical : </h6>
								<select name="vertical" style="width:265px;">
									<option value="">- Vertical -</option>
									<c:forEach items="${client.verticals}" var="vertical">
									<option value="${vertical.name}">${vertical.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="row" style="margin-left:  25px;width:  519px;" id="dropContainer" >
							<fieldset style="width:  100%;">
                              <div class="filedragwrap" onclick="choosefileSheets()">
              <div id="filedrag" style="display: block;">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
                <div class="filedraginput"> <span class="choosefile importchoosefile">Choose File</span>
                  <input type="file" name="file" id="fileselect" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.csv" onchange="updateSheets()">
                </div>
                <div class="clearfix ortxt"> --( OR )--</div>
                <div id="filedrag">Drop file here</div>
              </div>
            </div>
            </fieldset>
            <div id="hsnsumtallytemplate" style="display:none">Hsn Summary will be update</div>
							<div class="form-group col-md-12 col-sm-12" id="idSheet" style="display:none;">
								<p class="lable-txt">File Name  :  <span id="messages"></span></p>
								<div class="clearfix">&nbsp;</div>
								<div id="mastergstSheet" class="sheets">
									<div class="form-check form-check-inline" style="position:inherit;">
										<input class="form-check-input" type="checkbox" id="invoiceSheet" value="invoiceList" name='list' data="Invoices" checked="checked" />
										<label for="invoiceSheet"><span class="ui"></span>
										</label> <span class="labletxt">Invoices</span> 
									</div><div class="clearfix"></div>
								</div>
								<div id="tallySheet" class="sheets">
									<div class="form-check formcheck-inline" style="position:initial;">
										<input class="form-check-input" type="checkbox" id="b2bSheet" value="b2bList" name='list' checked="checked" />
										<label for="b2bSheet"><span class="ui"></span>
										</label> <span class="labletxt">B2B</span> 
									</div><div class="clearfix"></div>
									<div class="form-check form-check-inline" style="position:initial;">
										<input class="form-check-input" type="checkbox" id="b2cSheet" value="b2cList" name='list' checked="checked" />
										<label for="b2cSheet"><span class="ui"></span>
										</label> <span class="labletxt">B2C</span> 
									</div><div class="clearfix"></div>
									<div class="form-check form-check-inline" style="position:initial;">
										<input class="form-check-input" type="checkbox" id="b2clSheet" value="b2clList" name='list' checked="checked" />
										<label for="b2clSheet"><span class="ui"></span>
										</label> <span class="labletxt">B2CL</span> 
									</div><div class="clearfix"></div>
								</div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="creditSheet" value="creditList" name='list' data="Credit_Debit_Notes" checked="checked" />
									<label for="creditSheet"><span class="ui"></span>
									</label> <span class="labletxt">Credit/Debit Notes</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="advanceSheet" value="advReceiptList" name='list' data="AdvanceReceipt" checked="checked" />
									<label for="advanceSheet"><span class="ui"></span>
									</label> <span class="labletxt">Advances</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="advanceSheet" value="advAdjustedList" name='list' data="AdvanceAdjusted" checked="checked" />
									<label for="advanceSheet"><span class="ui"></span>
									</label> <span class="labletxt">Advance Adjusted Detail</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="advanceSheet" value="nilList" name='list' data="Nil_Exempted_Non-GST" checked="checked" />
									<label for="advanceSheet"><span class="ui"></span>
									</label> <span class="labletxt">Nil Supplies</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="advanceSheet" value="cdnurList" name='list' data="Credit_Debit_Note_Unregistered" checked="checked" />
									<label for="advanceSheet"><span class="ui"></span>
									</label> <span class="labletxt">Credit/Debit Note for Unregistered</span> 
								</div><div class="clearfix"></div>
								<c:choose>
								<c:when test="${returntype eq varPurchase || returntype eq varGSTR2 || returntype eq varGSTR6}">
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="b2buSheet" value="b2buList" name='list' data="B2B_Unregistered" checked="checked" />
									<label for="b2buSheet"><span class="ui"></span>
									</label> <span class="labletxt">B2B Unregistered</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="impGoodsSheet" value="impgList" name='list' data="Import_Goods" checked="checked" />
									<label for="impGoodsSheet"><span class="ui"></span>
									</label> <span class="labletxt">Import Goods</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="impServicesSheet" value="impsList" name='list' data="Import_Services" checked="checked" />
									<label for="impServicesSheet"><span class="ui"></span>
									</label> <span class="labletxt">Import Services</span> 
								</div><div class="clearfix"></div>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="itcRvslSheet" value="itrvslList" name='list' data="ITC_Reversal" checked="checked" />
									<label for="itcRvslSheet"><span class="ui"></span>
									</label> <span class="labletxt">ITC Reversal</span> 
								</div><div class="clearfix"></div>
								</c:when>
								<c:otherwise>
								<div class="form-check form-check-inline" style="position:initial;">
									<input class="form-check-input" type="checkbox" id="exportSheet" value="exportList" name='list' data="Exports" checked="checked" />
									<label for="exportSheet"><span class="ui"></span>
									</label> <span class="labletxt">Exports</span> 
								</div>
								</c:otherwise>
								</c:choose>
							</div>
							<div class="form-group col-4">
								<input type="button" class="btn btn-blue" onclick="performImport(this)" value="Import Invoices"/>
							</div>
							<div class="row alert alert-danger" role="alert" style="display:none;"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" />
					  			<span id="importSummaryError"></span>
							</div>
							<div class="form-group col-12" id="msgImportProgress" style="display:none;">
								We are importing your invoices. It may take few seconds based on your network bandwidth. Please wait..
							</div>
						</div>
						</form:form>
						</div>
						<div class="mt-2">
						<div class="form-group col-md-12 col-sm-12">
                            <div class="rightside-wrap">
								<h5>Download Templates</h5>
								<ul>
									<c:choose>
									<c:when test="${returntype ne varPurchase && returntype ne varGSTR2 && returntype ne varGSTR6}">
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template.xls">MasterGST Sales Template</a></li>
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template.xls">Tally Sales Template</a> </li>
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template_v17.xls">Tally Sales Template_V1.7</a> </li>
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Prime_Template.xls">Tally Sales Prime Template</a> </li>
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/GSTR1_Offline_Utility.xlsx">GSTR1 Offline Template</a> </li>
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Sales_Invoices_Template.xlsx" >Sage Sales Template</a></li>
									<li class="col-12 sales_template fhpl_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_Additional_Fields.xls">MasterGST Sales Template with Additional Fields</a></li>
									<li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_All_Fields.xls">MasterGST Sales Template with All Fields</a></li>
									</c:when>
									<c:otherwise>
									<li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Purchase_Invoices_Template.xls">MasterGST Purchase Template</a></li>
									<li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Purchase_Invoices_Template.xlsx" >Tally Purchase Template</a></li>
									<li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Purchase_Invoices_Template.xlsx" >Sage Purchase Template</a></li>
									<li class="col-12 purchase_template walterpack_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Walter_Purchase_Invoices_Template.xls" >Walter Purchase Template</a></li>
									</c:otherwise>
									</c:choose>
								</ul>
							</div>
                        </div>
                </div>
            </div>
        </div>
    </div>
	<div class="modal-content" id="idImportSummary" style="display:none;">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Import Invoice Summary </h3>
        </div>
        <div class="clearfix">&nbsp;</div>
		<div class="col-11 m-auto">
        <p>We have processed all your invoices and find the below Import invoices Summary, for Failed invoices you can download from the error log and correct the invoices and re-import again.</p>
          <table width="100%" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover">
            <thead>
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
              </tr>
            </thead>
            <tbody id="importSummaryBody">
            </tbody>
          </table>
          <table width="100%" border="0" cellspacing="0" id="errorXls" cellpadding="5" class="table table-inverse" style="display:none">
            <tr>
              <td>Download Error Log file <a class="ml-3" href="${contextPath}/geterrorxls"><i class="fa fa-file-excel-o"></i> Error Sheet</a></td>
              <td class="redtxt"></td>
            </tr>
          </table>
        </div>
      </div>
      <div class="modal-footer">
        <a href="#" class="btn btn-blue-dark urllink" link="${contextPath}/alliview/${id}/${fullname}/${usertype}/${client.id}/${returntype}?type=prchse">Continue..</a>
        <a href="#" class="btn btn-secondary urllink" link="${contextPath}/alliview/${id}/${fullname}/${usertype}/${client.id}/${returntype}?type=prchse">Close</a>
      </div>
    </div>
</div>
</div>
<script type="text/javascript">
var avlTypes = new Array();
function updateInvoiceTypeUrl(invType) {
	avlTypes = new Array();
	if(invType == 'mastergst' || invType == 'mgstFHPL' || invType == 'mastergst_all_fileds') {
		$("#uploadInvoiceForm").attr("action","${contextPath}/uploadInvoice/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}/"+invType+"");
	} else if(invType == 'tally'){
		$("#uploadInvoiceForm").attr("action","${contextPath}/uploadTallyInvoice/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}");
	}else if(invType == 'tallyv17' || invType == 'tallyprime'){
		$("#uploadInvoiceForm").attr("action","${contextPath}/uploadTallyInvoiceNewVersion/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}/"+invType+"");
	}else if(invType == 'sage'){
		$("#importModalForm").attr("action","${contextPath}/importSageInvoice/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}");
	}else if(invType == 'walterpack'){
		$("#importModalForm").attr("action","${contextPath}/importWalterpackInvoice/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}");
	}else if(invType == 'offlineutility'){
		$("#importModalForm").attr("action","${contextPath}/uploadGstr1OfflineUtility/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}");
	}else{
		$("#uploadInvoiceForm").attr("action","${contextPath}/uploadmapImportInvoice/"+invType+"/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${month}/${year}");
		<c:forEach items="${mappers}" var="mapper">
		if('<c:out value="${mapper.mapperName}"/>' == invType) {
			<c:forEach items="${mapper.invTypes}" var="inType">
				avlTypes.push('<c:out value="${inType}"/>');
			</c:forEach>
		}
		</c:forEach>
	}
}
function choosefileSheets(){
	$('input:file')[0].click();
}
function updateSheets() {
	$('#idSheet').show();
	$('.sheets').hide();
	var invType = $('#templateType').val();
	if(invType != 'tally'){
		invType = 'mastergst';
	}
	$('#'+invType+'Sheet').show();
	
	$('input:checkbox').each(function() {
		if(avlTypes.length == 0 || $.inArray($(this).attr('data'), avlTypes) != -1) {
			$(this).prop('checked', true);
		} else {
			$(this).prop('checked', false);
		}
	});
}
function performImport(btn) {
	var templateType = $('#templateType').val();
	if(templateType != ''){
	$('#templateType_Msg').html('');
	$(btn).addClass('btn-loader');
	$('#uploadInvoiceForm').ajaxSubmit( {
		url: $("#uploadInvoiceForm").attr("action"),
		dataType: 'json',
		type: 'POST',
		cache: false,
		success: function(response) {
			$(btn).removeClass('btn-loader');
			if(response && response.summaryList && response.summaryList.length > 0) {
				response.summaryList.forEach(function(inv) {
					$('#importSummaryBody').append("<tr><td>"+inv.name+"</td><td class='blocktxt'>"+inv.total+"</td><td class='blocktxt'>"+inv.totalinvs+"</td><td class='greentxt'>"+inv.success+"</td><td class='greentxt'>"+inv.invsuccess+"</td><td class='redtxt'>"+inv.failed+"</td><td class='redtxt'>"+inv.invfailed+"</td></tr>");
				});
				$('#idImportSummary').show();
				$('#idImportBody').hide();
				if(response.month) {
					month = response.month;
					year = response.year;
				}
				if(response.showLink) {
					$('#errorXls').show();
				}
			} else if(response && response.error) {
				$('#importSummaryError').parent().show().html(response.error);
			}
		},
		error: function(e) {
			$(btn).removeClass('btn-loader');
			$('#msgImportProgress').hide();
			$('#importSummaryError').parent().show().html("Something went wrong, Please try again");
		}
	});
	}else{
		$('#templateType_Msg').html('Please Select Template Type');
	}
}
$('input[type="file"]').change(function(e){
		var fileName = e.target.files[0].name;
		$('#messages').html(fileName);	
});
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map2.js" type="text/javascript"></script>