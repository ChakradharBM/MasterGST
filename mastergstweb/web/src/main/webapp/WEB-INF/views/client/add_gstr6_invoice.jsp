<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Upload GSTR6 | File GSTR6</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/invoice/gstr6.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://cdn.datatables.net/fixedheader/3.1.5/js/dataTables.fixedHeader.min.js"></script>
<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="https://cdn.datatables.net/fixedheader/3.1.5/css/fixedHeader.dataTables.min.css" media="all" />

<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
<script>
    $(document).ready(function(){
        var i=1;
        // Add minus icon for collapse element which is open by default
    	$(".collapse.in").each(function(){
           $(this).siblings(".main-accordion.panel-heading").find(".fa").addClass("fa-minus").removeClass("fa-plus");
			$(this).parent().find(".main-accordion.panel-heading").addClass("active");
	    });
        $('.main-collapse.panel-collapse').on('show.bs.collapse', function (e) {
        	$(e.target).closest('.panel').siblings().find('.main-collapse.panel-collapse').collapse('hide');
		});
        // Toggle plus minus icon on show hide of collapse element
        $(".main-collapse.collapse").on('show.bs.collapse', function(){
        	$(this).parent().find(".fa").removeClass("fa-plus").addClass("fa-minus");
			$(this).parent().find(".main-accordion.panel-heading").addClass("active");
        }).on('hide.bs.collapse', function(){
            $(this).parent().find(".fa").removeClass("fa-minus").addClass("fa-plus");
			$(this).parent().find(".main-accordion.panel-heading").removeClass("active");
        });
        });
</script>
<script type="text/javascript">
	var dbFilingTable4, dbFilingTable5, dbFilingTable6, dbFilingTable7, dbFilingTable8, dbFilingTable9, dbFilingTable10, dbFilingTable11, dbFilingTable12, dbFilingTable13, dbFilingTable14,  dbFilingTable15, dbFilingTable16,  dbFilingTable17,  dbFilingTable18, gstSummary=null, indexObj = new Object(), tableObj = new Object();
	var ipAddress = '', uploadResponse;var otpExpirycheck;
	$(function () {
		$(".tpone-input, .tptwo-input, .tpthree-input, .tpfour-input, .tpfive-input, .tpsix-input, .tpcheck-input").attr('readonly', true);
		$('.tpone-save, .tpone-cancel,.tptwo-save, .tptwo-cancel,.tpsix-save, .tpsix-cancel,.tpthree-save, .tpthree-cancel,.tpfour-save, .tpfour-cancel,.tpfive-save, .tpfive-cancel, .addmorewrap').hide();
			$(".otp_form_input .invoice_otp").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next().next('.form-control').focus();
			}
		});
			$('#gstr6_gstin').val('${client.gstnnumber}');
			$('#legal_name').val("${client.businessname}");
			$('.fy-drop').val('${invoice.fp}');
		$('#nav-client').addClass('active');
	});
	function clickEdit(a,b,c,d,e){
		$(a).show();
		$(b).show();
		$(c).hide();
		$('.addmorewrap1').css('display','block');
		$(d).attr('readonly', false);
		$('.auto-val-input').attr('readonly', true);
		$('.fy-drop').prop('disabled', false);
		$('.fy-drop').css('background-color', 'white');
		$(d).addClass('tpone-input-edit');
		$(e).show();
	}
	
	function clickSave(a,b,c,d,e){
		$(a).hide();
		$(b).hide();
		$(c).show();
		$(d).attr('readonly', true);
		$(d).addClass('tpone-input-edit');
		$('.fy-drop').prop('disabled', true);
		$('.fy-drop').css('background-color', '#eceeef');
		$(e).hide();
		var formObj = document.getElementById('supForm');
		var formURL = formObj.action;
		var formData = new FormData(formObj);
		formData.append("fp",$('#sel1').val());
		$.ajax({
			url: formURL,
			type: 'POST',
			data:  formData,
			mimeType:"multipart/form-data",
			contentType: false,
			cache: false,
			processData:false,
			success: function(data) {
				successNotification('Save data successful!');
			},
			error: function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function clickCancel(a,b,c,d,e,f){
		$(a).hide();
		$(b).hide();
		$(c).show();
		$(d).attr('readonly', true);
		$(d).addClass('tpone-input-edit');
		$('.fy-drop').prop('disabled', true);
		$('.fy-drop').css('background-color', '#eceeef');
		$(e).hide();
		if(f == 1){
			$('#dbTable').find(':input').each(function() {
            switch(this.type) {
                case 'text':
                   $(this).val('');
                   break;
                }
            });
		}else if(f == 2){
				$('#dbTable'+f+',#dbTableAddMore'+f).find(':input').each(function() {
            switch(this.type) {
                case 'text':
                   $(this).val('');
                   break;
                }
            });
		}else{
			$('#dbTable'+f).find(':input').each(function() {
            switch(this.type) {
                case 'text':
                   $(this).val('');
                   break;
                }
            });
		}
	}
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
						<li class="breadcrumb-item">${returntype}</li>
					</ol>
					<span class="datetimetxt"> 
					<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
				</span>
				<span class="f-14-b pull-right mt-1 font-weight-bold">
					Return Period: 
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
						<div class="pull-right helpguide" data-toggle="modal" data-target="#helpGuideModal"> Help To File GSTR6</div>
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link active" data-toggle="tab" href="#gtab1" role="tab">GSTR 6</a>
							</li>
							<%-- <li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab2" role="tab" onclick='fetchRetSummary("true","${client.id}","${year}")'>FILE GSTR 6</a>
							</li> --%>
						</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<!-- Tab panes 1-->
					<div class="tab-pane active" id="gtab1" role="tabpane1">
				<form:form method="POST" id="supForm" data-toggle="validator" class="meterialform invoiceform" name="salesinvoceform" action="${contextPath}/saveAnnualinvoice/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
				<div class="col-md-12 col-sm-12">
				<span class="text-right" style="float: left;margin-top: 10px;font-size:18px; font-weight:bold">Filing Status : 
					<span style="font-size:16px; margin-left:0px!important">
						<span class="color-yellow pen-style">Pending</span>
					</span>
				</span>
				<a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark permissionUpload_Invoice pull-right ml-2" onclick="uploadInvoice(this)">Upload to GSTIN</a>
				<a href="#" id="populategstr6" onclick = "autocalculategstr6()" class="btn btn-greendark pull-right ml-2">Auto Generate</a>
				</div>
				<div class="group upload-btn"></div>
				<div class="group upload-btn" >
							<div class="mb-2"><span class="pull-right">
							 <a href="#" class="btn btn-sm btn-blue-dark tpone-edit " onClick="clickEdit('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpthree-input');">Edit</a> 
							  <a href="#" class="btn btn-sm btn-blue-dark tpone-save" style="display:none" onClick="clickSave('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpthree-input');">Save</a>
							   <a href="#" class="btn btn-sm btn-blue-dark tpone-cancel" style="display:none" onClick="clickCancel('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpthree-input');" >Cancel</a>
							   </span></div>
						</div>
						<div class="group upload-btn" >
							<div class="form-group row">
								    <label for="legalname" class="col-sm-3 col-form-label ml-3">1) GSTIN</label>
								    <div class="col-sm-6">
								      	<input type="text" class="form-control tpthree-input" id="gstr6_gstin" style="border: 1px solid lightgray;width:50%;" readonly>
								    </div>
							  </div>
							  <div class="form-group row">
								    <label for="legalname" class="col-sm-3 col-form-label ml-3">2) a. Legal Name Of the Registered Person</label>
								    <div class="col-sm-6">
								      	<input type="text" class="form-control tpthree-input" id="legal_name" style="border: 1px solid lightgray;width:50%;" readonly>
								    </div>
							  </div>
							  <div class="form-group row">
								    <label for="tradename" class="col-sm-3 col-form-label ml-3 pl-4"> b. Trade Name, if any</label>
								    <div class="col-sm-6">
								      	<input type="text" class="form-control tpthree-input" id="trade_name" style="border: 1px solid lightgray;width:50%;" readonly>
								    </div>
							  </div>
							 </div>
					<div class="panel panel-default m-b-0">
					<!--- 3.1 --->
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerOne">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerOne" aria-expanded="true" aria-controls="collapseinnerOne">
						       (3)Input Tax credit Received For Distribution
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerOne" class="collapse show" aria-labelledby="headinginnerOne" data-parent="#accordion">
						      <div class="card-body p-2">
						        <div class="customtable db-ca-gst tabtable1 mt-2" style="overflow:auto;">
							<table id="dbTable1st" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" rowspan="2">No.Of Records</th>
										<th class="text-left" rowspan="2">Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center" colspan="4">Amount Of Tax</th>
										<th class="text-center" rowspan="2">Actions</th>
									</tr>
									<tr>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="b2b_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2b_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2b_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2b_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2b_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2b_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('b2b')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
									</tbody>
								</table>
						    </div>
						      </div>
						    </div>
						  </div>
						  <div class="card">
						    <div class="card-header" id="headinginnerTwo">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTwo" aria-expanded="true" aria-controls="collapseinnerTwo">
						          (4) Total ITC/Eligible ITC/Ineligible ITC to be distributed for Tax Period(From Table No.3)
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerTwo" class="collapse show" aria-labelledby="headinginnerTwo" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="itc_dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)Total ITC Available for Distribution</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="totalITC_cgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="totalITC_sgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="totalITC_igst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="totalITC_cess" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">(B)Amount Of Eligible ITC </td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="elgtotalITC_cgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="elgtotalITC_sgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="elgtotalITC_igst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="elgtotalITC_cess" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">(C)Amount Of Ineligible ITC </td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgtotalITC_cgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgtotalITC_sgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgtotalITC_igst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgtotalITC_cess" placeholder="0.00"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  
						  <!-- Table 3 start -->
						   <div class="card">
						    <div class="card-header" id="headinginnerThree">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerThree" aria-expanded="true" aria-controls="collapseinnerThree">
						          (5) Distribution Of Input Tax credit reported in Table.4
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerThree" class="collapse show" aria-labelledby="headinginnerTwo" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" rowspan="2">No.Of Records</th>
										<th class="text-center" colspan="4">Amount Of Tax</th>
										<th class="text-center" rowspan="2">Actions</th>
									</tr>
									<tr>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="7">5A. Distribution Of the Amount Of Eligible ITC</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="elg5A_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5A_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5A_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5A_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5A_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('ISDElg')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
									 <tr>
										<td class="text-left" colspan="7">5B. Distribution Of the Amount Of InEligible ITC</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="elg5B_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5B_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5B_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5B_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elg5B_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('ISDInElg')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
								</tbody>
							</table>
							
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 3 End -->
						  
						   <!-- Table 4 start -->
						   <div class="card">
						    <div class="card-header" id="headinginnerFour">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFour" aria-expanded="true" aria-controls="collapseinnerFour">
						          (6) Amendments In Information Furnished in Earlier Returns in Table No.3
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFour" class="collapse show" aria-labelledby="headinginnerFour" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2" style="overflow-y:auto;">
							<table id="Amd_dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" rowspan="2">No.Of Records</th>
										<th class="text-left" rowspan="2">Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-center" colspan="4">Amount Of Tax</th>
										<th class="text-center" rowspan="2">Actions</th>
									</tr>
									<tr>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="13">6A. Information Furnished in Table 3 in an earlier period was incorrect</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="b2ba_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2ba_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2ba_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2ba_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2ba_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="b2ba_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('b2ba')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
									<tr>
										<td class="text-left" colspan="13">6B. Debit Notes/Credit Notes Received[Original]</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="cdn_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdn_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdn_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdn_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdn_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdn_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('cdn')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
									<tr>
										<td class="text-left" colspan="13">6C. Debit Notes/Credit Notes[Amendments]</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="cdna_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdna_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdna_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdna_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdna_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="cdna_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('cdna')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
								</tbody>
							</table>
							
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 4 End -->
						   <!-- Table 5 start -->
						  <!--  <div class="card">
						    <div class="card-header" id="headinginnerFive">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFive" aria-expanded="true" aria-controls="collapseinnerFive">
						          (7) Input Tax credit mis-matches and reclaims to be distributed in the tax period
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFive" class="collapse show" aria-labelledby="headinginnerFive" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">7(A) Input Tax credit Mismatch</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">7(B Input Tax credit reclaimed on rectification of mismatch</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div> -->
						  <!-- Table 5 End -->
						   <!-- Table 6 start -->
						   <!-- <div class="card">
						    <div class="card-header" id="headinginnerSeven">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerSeven" aria-expanded="true" aria-controls="collapseinnerSeven">
						          (8) Distribution of input Tax credit reported in Table No.6 and Table No.7(Plus/Minus)
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerSeven" class="collapse show" aria-labelledby="headinginnerSeven" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" rowspan="2" width="30%">GSTIN of Supplier</th>
										<th class="text-center" colspan="2">ISD credit No</th>
										<th class="text-center" colspan="2">ISD Invoice</th>
										<th class="text-center" colspan="4">Input Tax Distribution By ISD</th>
									</tr>
									<tr>
										<th class="text-center" rowspan="2">No</th>
										<th class="text-center" rowspan="2">Date</th>
										<th class="text-center" rowspan="2">No</th>
										<th class="text-center" rowspan="2">Date</th>
										<th class="text-left" rowspan="2">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left" rowspan="2">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left" rowspan="2">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left" rowspan="2">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="9">8(A) Distribution Of the Amount Of Eligible ITC</td>
									</tr>
									<tr>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
									</tr>
									<tr>
										<td class="text-left" colspan="9">8(B) Distribution Of the Amount Of InEligible ITC</td>
									</tr>
									<tr>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
										<td><input type="text" class="form-control tpthree-input"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div> -->
						  <!-- Table 6 End -->
						   <!-- Table 7 start -->
						   <div class="card">
						    <div class="card-header" id="headinginnerEight">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerEight" aria-expanded="true" aria-controls="collapseinnerEight">
						          (7) Redistribution of ITC distributed to a wrong recipient(plus/minus)
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerEight" class="collapse show" aria-labelledby="headinginnerEight" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2" style="overflow:auto;">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" rowspan="2">No.Of Records</th>
										<th class="text-center" colspan="4">Amount Of Tax</th>
										<th class="text-center" rowspan="2">Actions</th>
									</tr>
									<tr>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="12">7(A) Distribution Of the Amount Of Eligible ITC</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="elgisda_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elgisda_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elgisda_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elgisda_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="elgisda_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('ISDAElg')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
									 <tr>
										<td class="text-left" colspan="12">7(B) Distribution Of the Amount Of InEligible ITC</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input text-right" id="inelgisda_records" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgisda_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgisda_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgisda_igst" name="" readonly/></td>
										<td class="text-right form-group gstr6-error"><input type="text" class="form-control tpthree-input text-right" id="inelgisda_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr6-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('ISDAInElg')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></td>
									</tr>
								</tbody>
							</table>
							
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 7 End -->
						   <!-- Table 8 start -->
						   <div class="card">
						    <div class="card-header" id="headinginnerNine">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerNine" aria-expanded="true" aria-controls="collapseinnerNine">
						          (8) Late Fee
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerNine" class="collapse show" aria-labelledby="headinginnerNine" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">On Account Of</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Debit Entry No</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left form-group gst6-error">Late Fee</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="latefee_cgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="latefee_sgst" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input text-right" id="latefee_debitno"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 8 End -->
						   <!-- Table 9 start -->
						  <!--  <div class="card">
						    <div class="card-header" id="headinginnerTen">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTen" aria-expanded="true" aria-controls="collapseinnerTen">
						          (11) Refund Claimed From Electronic Cash Ledger
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerTen" class="collapse show" aria-labelledby="headinginnerTen" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Description</th>
										<th class="text-left">Fee</th>
										<th class="text-left">Other</th>
										<th class="text-left">Debit Entry No's</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left form-group gst6-error">(a) Central tax</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
									</tr>
									<tr>
										<td class="text-left form-group gst6-error">(b) State/UT tax</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
									</tr>
									<tr>
										<td class="text-left form-group gst6-error" colspan="4">Bank Account Details</td>
										
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div> -->
						  <!-- Table 9 End -->
						</div>
				</div>
				<div class="col-sm-12 mt-4 text-center">
								<c:if test="${not empty invoice && not empty invoice.id}">
								<input type="hidden" name="id" value="<c:out value="${invoice.id}"/>">
								</c:if>
								<input type="hidden" name="userid" value="<c:out value="${id}"/>">
								<input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">	
								<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
							</div>
						
                        </form:form>
					
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
       <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModal" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
	        <div class="invoice-hdr bluehdr">
	          <h3>Invoice Wise Details</h3>
	        </div>
	        <div class="row gstr-info-tabs pl-2 pr-2 pt-1">
	        		 <ul class="nav nav-tabs col-md-12 mt-3 pl-4" role="tablist" id="tabsactive">
						<li class="nav-item"><a class="nav-link active" id="gstinwise" data-toggle="tab" href="#gstintab" role="tab"><span class="serial-num">1</span>GSTIN Wise</a></li>
						<!-- <li class="nav-item"><a class="nav-link " id="amountswise" data-toggle="tab" href="#amountstab" role="tab"><span class="serial-num">2</span>Amounts Wise</a></li> -->
				   </ul>
					<div class="tab-content col-md-12 mb-3 mt-3">
								<div class="tab-pane active col-md-12" id="gstintab" role="tabpane1">
									<div class="customtable">
											<table id="gstintable" class="display row-border dataTable meterialform">
											<thead>
											<tr><th rowspan="2">SR.No</th><th rowspan="2">Supplier GSTIN</th>
											<th rowspan="2">Invoice No</th><th rowspan="2">Invoice Date</th><th rowspan="2">Taxable Value</th>
											<th colspan="4" class="text-center">Amount Of Tax</th></tr>
											<tr><th>IGST</th><th>CGST</th><th>SGST</th><th>CESS</th></tr>
											</thead>
											<tbody id="gstwiseData"></tbody>
											</table>
									</div>
			        		  </div>
			        		  <div class="tab-pane col-md-12" id="amountstab" role="tabpane2">
									HELLO
			        		  </div>
			        </div>
	         </div>
      </div>
      <div class="modal-footer mb-2">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
var invRowCount =1,b2bainvRowCount =1,itcRowCount=1;
var b2bAmount = '${b2bAmts}';
 $('#b2b_records').val('${b2bAmts.totalTransactions}' ? '${b2bAmts.totalTransactions}' : '0');
$('#b2b_taxableValue').val('${b2bAmts.totalTaxableAmount}' ? '${b2bAmts.totalTaxableAmount}' : '0.00');
$('#b2b_cgst').val('${b2bAmts.totalCGSTAmount}' ? '${b2bAmts.totalCGSTAmount}' : '0.00');
$('#b2b_sgst').val('${b2bAmts.totalSGSTAmount}' ? '${b2bAmts.totalSGSTAmount}' : '0.00');
$('#b2b_igst').val('${b2bAmts.totalIGSTAmount}' ? '${b2bAmts.totalIGSTAmount}' : '0.00'); 
$('#b2b_cess').val('${b2bAmts.totalCESSAmount}' ? '${b2bAmts.totalCESSAmount}' : '0.00'); 
if('${b2baAmts}' != '' && '${b2baAmts}' != null){
	$('#b2ba_records').val('${b2baAmts.totalTransactions}' ? '${b2baAmts.totalTransactions}' : '0');
	$('#b2ba_taxableValue').val('${b2baAmts.totalTaxableAmount}' ? '${b2baAmts.totalTaxableAmount}' : '0.00');
	$('#b2ba_cgst').val('${b2baAmts.totalCGSTAmount}' ? '${b2baAmts.totalCGSTAmount}' : '0.00');
	$('#b2ba_sgst').val('${b2baAmts.totalSGSTAmount}' ? '${b2baAmts.totalSGSTAmount}' : '0.00');
	$('#b2ba_igst').val('${b2baAmts.totalIGSTAmount}' ? '${b2baAmts.totalIGSTAmount}' : '0.00'); 
	$('#b2ba_cess').val('${b2baAmts.totalCESSAmount}' ? '${b2baAmts.totalCESSAmount}' : '0.00'); 
}
if('${cdnAmts}' != '' && '${cdnAmts}' != null){
	$('#cdn_records').val('${cdnAmts.totalTransactions}' ? '${cdnAmts.totalTransactions}' : '0');
	$('#cdn_taxableValue').val('${cdnAmts.totalTaxableAmount}' ? '${cdnAmts.totalTaxableAmount}' : '0.00');
	$('#cdn_cgst').val('${cdnAmts.totalCGSTAmount}' ? '${cdnAmts.totalCGSTAmount}' : '0.00');
	$('#cdn_sgst').val('${cdnAmts.totalSGSTAmount}' ? '${cdnAmts.totalSGSTAmount}' : '0.00');
	$('#cdn_igst').val('${cdnAmts.totalIGSTAmount}' ? '${cdnAmts.totalIGSTAmount}' : '0.00'); 
	$('#cdn_cess').val('${cdnAmts.totalCESSAmount}' ? '${cdnAmts.totalCESSAmount}' : '0.00'); 
}
if('${cdnaAmts}' != '' && '${cdnaAmts}' != null){
	$('#cdna_records').val('${cdnaAmts.totalTransactions}' ? '${cdnaAmts.totalTransactions}' : '0');
	$('#cdna_taxableValue').val('${cdnaAmts.totalTaxableAmount}' ? '${cdnaAmts.totalTaxableAmount}' : '0.00');
	$('#cdna_cgst').val('${cdnaAmts.totalCGSTAmount}' ? '${cdnaAmts.totalCGSTAmount}' : '0.00');
	$('#cdna_sgst').val('${cdnaAmts.totalSGSTAmount}' ? '${cdnaAmts.totalSGSTAmount}' : '0.00');
	$('#cdna_igst').val('${cdnaAmts.totalIGSTAmount}' ? '${cdnaAmts.totalIGSTAmount}' : '0.00'); 
	$('#cdna_cess').val('${cdnaAmts.totalCESSAmount}' ? '${cdnaAmts.totalCESSAmount}' : '0.00'); 
}
if('${isdAmts}' != '' && '${isdAmts}' != null){
	$('#elg5A_records').val('${isdAmts.totalTransactions}' ? '${isdAmts.totalTransactions}' : '0');
	$('#elg5A_cgst').val('${isdAmts.totalCGSTAmount}' ? '${isdAmts.totalCGSTAmount}' : '0.00');
	$('#elg5A_sgst').val('${isdAmts.totalSGSTAmount}' ? '${isdAmts.totalSGSTAmount}' : '0.00');
	$('#elg5A_igst').val('${isdAmts.totalIGSTAmount}' ? '${isdAmts.totalIGSTAmount}' : '0.00'); 
	$('#elg5A_cess').val('${isdAmts.totalCESSAmount}' ? '${isdAmts.totalCESSAmount}' : '0.00'); 
}
if('${isdInElgAmts}' != '' && '${isdInElgAmts}' != null){
	$('#elg5B_records').val('${isdInElgAmts.totalTransactions}' ? '${isdInElgAmts.totalTransactions}' : '0');
	$('#elg5B_cgst').val('${isdInElgAmts.totalCGSTAmount}' ? '${isdInElgAmts.totalCGSTAmount}' : '0.00');
	$('#elg5B_sgst').val('${isdInElgAmts.totalSGSTAmount}' ? '${isdInElgAmts.totalSGSTAmount}' : '0.00');
	$('#elg5B_igst').val('${isdInElgAmts.totalIGSTAmount}' ? '${isdInElgAmts.totalIGSTAmount}' : '0.00'); 
	$('#elg5B_cess').val('${isdInElgAmts.totalCESSAmount}' ? '${isdInElgAmts.totalCESSAmount}' : '0.00'); 
}
if('${isdaAmts}' != '' && '${isdaAmts}' != null){
	$('#elgisda_records').val('${isdaAmts.totalTransactions}' ? '${isdaAmts.totalTransactions}' : '0');
	$('#elgisda_cgst').val('${isdaAmts.totalCGSTAmount}' ? '${isdaAmts.totalCGSTAmount}' : '0.00');
	$('#elgisda_sgst').val('${isdaAmts.totalSGSTAmount}' ? '${isdaAmts.totalSGSTAmount}' : '0.00');
	$('#elgisda_igst').val('${isdaAmts.totalIGSTAmount}' ? '${isdaAmts.totalIGSTAmount}' : '0.00'); 
	$('#elgisda_cess').val('${isdaAmts.totalCESSAmount}' ? '${isdaAmts.totalCESSAmount}' : '0.00'); 
}
if('${isdaInElgAmts}' != '' && '${isdaInElgAmts}' != null){
	$('#inelgisda_records').val('${isdaInElgAmts.totalTransactions}' ? '${isdaInElgAmts.totalTransactions}' : '0');
	$('#inelgisda_cgst').val('${isdaInElgAmts.totalCGSTAmount}' ? '${isdaInElgAmts.totalCGSTAmount}' : '0.00');
	$('#inelgisda_sgst').val('${isdaInElgAmts.totalSGSTAmount}' ? '${isdaInElgAmts.totalSGSTAmount}' : '0.00');
	$('#inelgisda_igst').val('${isdaInElgAmts.totalIGSTAmount}' ? '${isdaInElgAmts.totalIGSTAmount}' : '0.00'); 
	$('#inelgisda_cess').val('${isdaInElgAmts.totalCESSAmount}' ? '${isdaInElgAmts.totalCESSAmount}' : '0.00'); 
}
if('${syncData.totalItc}' != '' && '${syncData.totalItc}' != null){
	$('#totalITC_igst').val('${syncData.totalItc.iamt}' ? '${syncData.totalItc.iamt}' : '0.00');
	$('#totalITC_cgst').val('${syncData.totalItc.camt}' ? '${syncData.totalItc.camt}' : '0.00');
	$('#totalITC_sgst').val('${syncData.totalItc.samt}' ? '${syncData.totalItc.samt}' : '0.00');
	$('#totalITC_cess').val('${syncData.totalItc.csamt}' ? '${syncData.totalItc.csamt}' : '0.00');
}
if('${syncData.elgItc}' != '' && '${syncData.elgItc}' != null){
	$('#elgtotalITC_igst').val('${syncData.elgItc.iamt}' ? '${syncData.elgItc.iamt}' : '0.00');
	$('#elgtotalITC_cgst').val('${syncData.elgItc.camt}' ? '${syncData.elgItc.camt}' : '0.00');
	$('#elgtotalITC_sgst').val('${syncData.elgItc.samt}' ? '${syncData.elgItc.samt}' : '0.00');
	$('#elgtotalITC_cess').val('${syncData.elgItc.csamt}' ? '${syncData.elgItc.csamt}' : '0.00');
}
if('${syncData.inelgItc}' != '' && '${syncData.inelgItc}' != null){
	$('#inelgtotalITC_igst').val('${syncData.inelgItc.iamt}' ? '${syncData.inelgItc.iamt}' : '0.00');
	$('#inelgtotalITC_cgst').val('${syncData.inelgItc.camt}' ? '${syncData.inelgItc.camt}' : '0.00');
	$('#inelgtotalITC_sgst').val('${syncData.inelgItc.samt}' ? '${syncData.inelgItc.samt}' : '0.00');
	$('#inelgtotalITC_cess').val('${syncData.inelgItc.csamt}' ? '${syncData.inelgItc.csamt}' : '0.00');
}
if('${syncData.latefee}' != '' && '${syncData.latefee}' != null){
	$('#latefee_cgst').val('${syncData.latefee.cLamt}' ? '${syncData.latefee.cLamt}' : '0.00');
	$('#latefee_sgst').val('${syncData.latefee.sLamt}' ? '${syncData.latefee.sLamt}' : '0.00');
	$('#latefee_debitno').val('${syncData.latefee.debitId}' ? '${syncData.latefee.debitId}' : '');
}
$(document).ready(function(){
//loadTables(b2bAmount);
});
function otpExpiryCheck(){
	otpExpirycheck = "";
	$.ajax({
		url : _getContextPath()+'/otpexpiry/'+clientId,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			otpExpirycheck = response;
		}
	});
}
function autocalculategstr6(){
	otpExpiryCheck();
	window.location.href = _getContextPath()+'/getpopulategstr6data'+urlSuffix+'/'+varUserType+'/'+month+'/'+year;
	/* if(otpExpirycheck == 'OTP_VERIFIED'){
		window.location.href = _getContextPath()+'/getpopulategstr6data'+urlSuffix+'/'+varUserType+'/'+month+'/'+year;
	}else{
		errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
	} */
}
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;
		var year = eDate.getFullYear();
		window.location.href = '${contextPath}/addGSTR6invoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year;
	}

</script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.formula.js" type="text/javascript"></script>
<script  src="${contextPath}/static/mastergst/js/gstr6/gstr6_updt.js" type="text/javascript"></script>
</body>

 </html> 