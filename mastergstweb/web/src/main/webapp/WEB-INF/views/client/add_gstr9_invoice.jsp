<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Upload GSTR9A | File GSTR9A</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://cdn.datatables.net/fixedheader/3.1.5/js/dataTables.fixedHeader.min.js"></script>
<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>

<link rel="stylesheet" href="https://cdn.datatables.net/fixedheader/3.1.5/css/fixedHeader.dataTables.min.css" media="all" />
<style>
#sel1{background-color: #eceeef;}
.group.upload-btn{font-size: 14px;}
.gstr-info-tabs .nav-link{min-width: 70px;text-align: center;}
input.form-control.tpseven-input.tpseven-input-edit ,input.form-control.tpseven-input{border: 1px solid #ccc;margin: 0;border-radius: 4px;padding: 3px 1px;height: 23px;}
.panel-heading.active{background-color: #8ee3fe;padding: 5px 10px;}
.panel-heading: first-child{border-radius: calc(.25rem - 1px) calc(.25rem - 1px) 0 0;}
.panel-heading{background-color: #f7f7f9;padding: 5px 10px; border:1px solid rgba(0,0,0,.125)}
a.panel-title{ text-decoration:none ;font-weight: bold;}
a.panel-title .fa.pull-right {margin-left: 1.3em;margin-top: 0.3rem;}
.panel-heading .helpguide { float :right}
#dbTable3 tbody td , #dbTable4 tbody td , #dbTable5 tbody td{ padding-right: 18px;}
.gst-3b-error .help-block .list-unstyled li{text-align: left;line-height: 0.91;margin-top: -45px;margin-left: 0px;background-color: rgba(255, 255, 255, 0.77);border-radius: 4px;box-shadow: 0px 0px 8px #da8e8e;padding: 2px;}
.suplies-body{padding-bottom: 4rem!important;}
.db-ca-gst .help-block.with-errors{z-index: 2!important;}
.nav-bread{height:35px!important}
.helpguide{margin-bottom: 5px;margin-top: 3px;margin-left: 6px;height: 19px;display:none}
.acco-btn{font-weight:bold}
a.btn.btn-blue-dark.btn-sm{padding: 0px 7px!important;}
.db-ca-wrap{padding-top: 99px!important;}
.auto-row{background-color:#ffb6c15e!important} 
.inneracco{background-color: #f7f7f9;border-radius: 0;}
.inneracco .card{border: 1px solid rgba(0,0,0,.125);border-radius: 0;}
.inneracco .card .card-header{    padding: 2px 10px;border-radius: 0;}
.panel-heading > a[aria-expanded="false"]:after { font-family: FontAwesome;content: "\f067 "; float:right ;margin-left: 1.3em;margin-top: 0.3rem; font-weight: 500;}
.panel-heading > a[aria-expanded="true"]:after { font-family: FontAwesome;content: "\f068 ";  float:right;margin-left: 1.3em;margin-top: 0.3rem; font-weight: 500;}
.no-expa > a[aria-expanded="true"]:after , .no-expa > a[aria-expanded="false"]:after{display:none}
.acco-btn[aria-expanded="false"]:after { font-family: FontAwesome;content: "\f067 "; float:right ;margin-left: 1.3em;margin-top: 0.3rem; font-weight: 500;}
.acco-btn[aria-expanded="true"]:after { font-family: FontAwesome;content: "\f068 ";  float:right;margin-left: 1.3em;margin-top: 0.3rem; font-weight: 500;}
.db-ca-gst-wrap .accordion .card-header:after{content:none}
.acco-btn{width: 100%;text-align: left;box-shadow: none;text-decoration:none;padding: 0px!important; margin: 0px!important;background-color: #f7f7f9;color: #0279d8;}
.acco-btn:focus , .acco-btn:hover {box-shadow: none;    text-decoration: none;}
.db-ca-wrap table.dataTable.row-border tbody tr td:first-child{color: black;}
</style>
<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
<script>
    $(document).ready(function(){
		$('#sel1').val('03${year}');
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
			$('.fy-drop').val('${invoice.fp}');
		$('#nav-client').addClass('active');
		function forceNumeric(){
			var $input = $(this);
			$input.val($input.val().replace(/[^\d.,]+/g,''));
		}
		//$('body').on('propertychange input', 'input.form-control', forceNumeric);
		
		

		
		var headertext = [],
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
		}
		//$('#populategstr9').attr('href','${contextPath}/populate9/${id}/${fullname}/${usertype}/${client.id}/'+03+'/${year}');
	});
	
	function deleteItem(supType, dType, index) {
		$('input[type="hidden"]').each(function() {
			var name = $(this).attr('name');
			if(name.indexOf(supType+"."+dType+"["+index+"]") >= 0) {
				$(this).remove();
			}
		});
		//tableObj[dType].row('#'+dType+index).remove().draw(false);
		$('#'+dType+index).remove();
	}
	
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
	function showSubmitPopup() {
		$('#submitInvModal').modal('show');
		$('#btnSubmitInv').attr('onclick', "submitReturns()");
	}
	

	
	function invokeOTP(btn) {
		var state = "${client.statename}";
		var gstname = "${client.gstname}";
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
			error : function(e, status, error) {
				if(e.responseText) {
					$('#idClientError').html(e.responseText);
				}
			}
		});
	}
	function validateDownloadOtp() {
		var otp1 = $('#dotp1').val();
		var otp2 = $('#dotp2').val();
		var otp3 = $('#dotp3').val();
		var otp4 = $('#dotp4').val();
		var otp5 = $('#dotp5').val();
		var otp6 = $('#dotp6').val();
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
				$('#downloadOtpModalClose').click();
				closeNotifications();
			},
			error : function(e, status, error) {
				$('#downloadOtpModalClose').click();
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function evcFilingOTP() {
		$.ajax({
			url : "${contextPath}/fotpevc/${id}/${client.id}/${returntype}/${month}/${year}",
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(data) {
				$('#evcOtpModal').modal('show');
			}
		});
	}
function fileEVC() {
	var otp = $('#evcotp1').val();
	$('#evcOtpModal').modal('hide');
	$.ajax({
		url : '${contextPath}/fretevcfile/${id}/${client.id}/${returntype}/'+otp+'/${month}/${year}',
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(retResponse) {
			if(retResponse.error && retResponse.error.message) {
				errorNotification(retResponse.error.message);
			} else if(retResponse.status_cd == '0') {
				errorNotification(retResponse.status_desc);
			} else if(retResponse.status_cd == '1') {
				successNotification('Return filing successful!');
			} else {
				errorNotification('Unable to file returns!');
			}
		}
	});
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
					<!-- <span class="datetimetxt"> 
						<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
					</span> -->
					<select class="pull-right mt-1" id="gstr9FinancialYear"><option value="2022">2021 - 2022</option><option value="2021">2020 - 2021</option><option value="2020">2019 - 2020</option><option value="2019">2018 - 2019</option><option value="2018">2017 - 2018</option></select>
					<h6 class="f-14-b pull-right mt-2 mr-2 font-weight-bold" style="display: inline-block;">Financial Year : </h6> 
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
						<div class="pull-right helpguide" data-toggle="modal" data-target="#helpGuideModal"> Help To File GSTR9</div>
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link active" data-toggle="tab" href="#gtab1" role="tab">GSTR 9</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab2" role="tab" onclick='fetchRetSummary("true","${client.id}","${year}")'>FILE GSTR 9</a>
							</li>
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
				<span class="text-right bs-fancy-checks meterialform" style="float:left;display: inline-flex;margin-top: 10px;font-size:18px; font-weight:bold"> 
					 <div class="form-radio" style="font-size:16px; margin-left:20px!important">
                        <div class="radio">
                            <label>
                               <input name="type" id="type" type="radio" value="NillRated"  />
                               <i class="helper"></i>Nil Return</label>
                         </div>
                     </div>
                     <div class="form-radio" style="font-size:16px; margin-left:20px!important">
                        <div class="radio">
                            <label>
                               <input name="type" id="type" type="radio" value="NonNillRated" checked />
                               <i class="helper"></i>Not Nil Return</label>
                         </div>
                     </div>
				</span>
				<a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark permissionUpload_Invoice pull-right ml-2" onclick="uploadInvoice(this)">Upload to GSTIN</a>
				<a href="#" id="populategstr9" onclick = "autocalculategstr9()" class="btn btn-greendark pull-right ml-2">Auto Generate</a>
				</div>
				<div class="group upload-btn"></div>
				<div class="col-md-12 col-sm-12">
				<div class="gstr-info-tabs">
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link active" data-toggle="tab" href="#main_tab1" role="tab"><span class="serial-num">1</span>Part II</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#main_tab2" role="tab"><span class="serial-num">2</span>Part III</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#main_tab3" role="tab"><span class="serial-num">3</span>Part IV</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#main_tab4" role="tab"><span class="serial-num">4</span>Part V</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#main_tab5" role="tab"><span class="serial-num">5</span>Part VI</a>
							</li>
						</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<!-- Tab panes 1-->
					<div class="tab-pane active" id="main_tab1" role="tabpane1">
					<div class="panel panel-default m-b-0">
					<!--- 3.1 --->
					
					 	<div class="group upload-btn" >
							<div class="mb-2">Details of Outward and inward supplies declared during the financial year<span class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_1"> Help Guide</span><span class="pull-right"> <a href="#" class="btn btn-sm btn-blue-dark tpone-edit " onClick="clickEdit('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Edit</a>  <a href="#" class="btn btn-sm btn-blue-dark tpone-save" style="display:none" onClick="clickSave('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Save</a> <a href="#" class="btn btn-sm btn-blue-dark tpone-cancel" style="display:none" onClick="clickCancel('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');" >Cancel</a></span></div>
						</div>
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerOne">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerOne" aria-expanded="true" aria-controls="collapseinnerOne">
						       (4)Details of advances, inward and outward supplies on which tax is payable as declared in returns filed during the financial year
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerOne" class="collapse show" aria-labelledby="headinginnerOne" data-parent="#accordion">
						      <div class="card-body p-2">
						        <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable1st" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Nature of Supplies</th>
										<th class="text-left">Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)Supplies made to un-registered persons (B2C)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2c_txval" name="table4.b2c.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2c.txval}" />' data-variavel="tab4Afield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2c_iamt" name="table4.b2c.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2c.camt}" />' data-variavel="tab4Afield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2c_samt" name="table4.b2c.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2c.samt}" />' data-variavel="tab4Afield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2c_iamt" name="table4.b2c.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2c.iamt}" />' data-variavel="tab4Afield4" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2c_csamt" name="table4.b2c.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2c.csamt}" />' data-variavel="tab4Afield5" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(B)Supplies made to registered persons (B2B)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2b_txval" name="table4.b2b.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2b.txval}" />' data-variavel="tab4Bfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2b_camt" name="table4.b2b.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2b.camt}" />' data-variavel="tab4Bfield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2b_samt" name="table4.b2b.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2b.samt}" />' data-variavel="tab4Bfield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2b_iamt" name="table4.b2b.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2b.iamt}" />' data-variavel="tab4Bfield4" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"><input type="text" readonly="true" class="form-control tpone-input" id="table4_b2b_csamt" name="table4.b2b.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.b2b.csamt}" />' data-variavel="tab4Bfield5" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(C)Zero rated supply (Export) on payment of tax (except supplies to SEZs) </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_exp_txval" name="table4.exp.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.exp.txval}" />' data-variavel="tab4Cfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_exp_iamt" name="table4.exp.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.exp.iamt}" />' data-variavel="tab4Cfield4" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_exp_csamt" name="table4.exp.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.exp.csamt}" />' data-variavel="tab4Cfield5" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(D)Supply to SEZs on payment of tax </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_sez_txval" name="table4.sez.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.sez.txval}" />' data-variavel="tab4Dfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"><input type="text" readonly="true" class="form-control tpone-input" id="table4_sez_iamt" name="table4.sez.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.sez.iamt}" />' data-variavel="tab4Dfield4" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"><input type="text" readonly="true" class="form-control tpone-input" id="table4_sez_csamt" name="table4.sez.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.sez.csamt}" />' data-variavel="tab4Dfield5" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(E)Deemed Exports </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_deemed_txval" name="table4.deemed.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.deemed.txval}" />' data-variavel="tab4Efield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_deemed_camt" name="table4.deemed.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.deemed.camt}" />' data-variavel="tab4Efield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_deemed_samt" name="table4.deemed.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.deemed.samt}" />' data-variavel="tab4Efield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_deemed_iamt" name="table4.deemed.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.deemed.iamt}" />' data-variavel="tab4Efield4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_deemed_csamt" name="table4.deemed.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.deemed.csamt}" />' data-variavel="tab4Efield5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)Advances on which tax has been paid but invoice has not been issued (not covered under (A) to (E) above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_at_txval" name="table4.at.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.at.txval}" />' data-variavel="tab4Ffield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_at_camt" name="table4.at.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.at.camt}" />' data-variavel="tab4Ffield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_at_samt" name="table4.at.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.at.samt}" />' data-variavel="tab4Ffield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_at_iamt" name="table4.at.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.at.iamt}" />' data-variavel="tab4Ffield4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_at_csamt" name="table4.at.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.at.csamt}" />' data-variavel="tab4Ffield5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(G)Inward supplies on which tax is to be paid on reverse charge basis</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_rchrg_txval" name="table4.rchrg.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.rchrg.txval}" />' data-variavel="tab4Gfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_rchrg_camt" name="table4.rchrg.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.rchrg.camt}" />' data-variavel="tab4Gfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_rchrg_samt" name="table4.rchrg.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.rchrg.samt}" />' data-variavel="tab4Gfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_rchrg_iamt" name="table4.rchrg.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.rchrg.iamt}" />' data-variavel="tab4Gfield4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_rchrg_csamt" name="table4.rchrg.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.rchrg.csamt}" />' data-variavel="tab4Gfield5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(H) Sub-total (A to G above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4AtoG_txval" name = "table4AtoG.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4AtoG.txval}" />' data-formula="#tab4Afield1# + #tab4Bfield1# + #tab4Cfield1# + #tab4Dfield1# + #tab4Efield1# + #tab4Ffield1# + #tab4Gfield1#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4AtoG_camt" name ="table4AtoG.camt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4AtoG.camt}" />'  data-formula="#tab4Afield2# + #tab4Bfield2# + #tab4Efield2# + #tab4Ffield2# + #tab4Gfield2#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4AtoG_samt" name ="table4AtoG.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4AtoG.samt}" />' data-formula="#tab4Afield3# + #tab4Bfield3# + #tab4Efield3# + #tab4Ffield3# + #tab4Gfield3#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4AtoG_iamt" name ="table4AtoG.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4AtoG.iamt}" />' data-formula="#tab4Afield4# + #tab4Bfield4# + #tab4Cfield4# + #tab4Dfield4# + #tab4Efield4# + #tab4Ffield4# + #tab4Gfield4#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4AtoG_csamt" name ="table4AtoG.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4AtoG.csamt}" />' data-formula="#tab4Afield5# + #tab4Bfield5# + #tab4Cfield5# + #tab4Dfield5# + #tab4Efield5# + #tab4Ffield5# + #tab4Gfield5#" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(I)Credit Notes issued in respect of transactions specified in (B) to (E) above (-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_crNt_txval" name="table4.crNt.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.crNt.txval}" />' data-variavel="tab4Ifield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_crNt_camt" name="table4.crNt.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.crNt.camt}" />' data-variavel="tab4Ifield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_crNt_samt" name="table4.crNt.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.crNt.samt}" />' data-variavel="tab4Ifield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_crNt_iamt" name="table4.crNt.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.crNt.iamt}" />' data-variavel="tab4Ifield4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_crNt_csamt" name="table4.crNt.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.crNt.csamt}" />' data-variavel="tab4Ifield5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(J)Debit Notes issued in respect of transactions specified in (B) to (E) above (+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_drNt_txval" name="table4.drNt.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.drNt.txval}" />' data-variavel="tab4Jfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_drNt_camt" name="table4.drNt.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.drNt.camt}" />' data-variavel="tab4Jfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_drNt_samt" name="table4.drNt.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.drNt.samt}" />' data-variavel="tab4Jfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_drNt_iamt" name="table4.drNt.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.drNt.iamt}" />' data-variavel="tab4Jfield4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_drNt_csamt" name="table4.drNt.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.drNt.csamt}" />' data-variavel="tab4Jfield5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(K)Supplies / tax declared through Amendments (+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdPos_txval" name="table4.amdPos.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdPos.txval}" />' data-variavel="tab4Kfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdPos_camt" name="table4.amdPos.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdPos.camt}" />' data-variavel="tab4Kfield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdPos_samt" name="table4.amdPos.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdPos.samt}" />' data-variavel="tab4Kfield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdPos_iamt" name="table4.amdPos.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdPos.iamt}" />' data-variavel="tab4Kfield4" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdPos_csamt" name="table4.amdPos.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdPos.csamt}" />' data-variavel="tab4Kfield5" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(L)Supplies / tax reduced through Amendments (-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdNeg_txval" name="table4.amdNeg.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdNeg.txval}" />' data-variavel="tab4Lfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdNeg_camt" name="table4.amdNeg.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdNeg.camt}" />' data-variavel="tab4Lfield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdNeg_samt" name="table4.amdNeg.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdNeg.samt}" />' data-variavel="tab4Lfield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdNeg_iamt" name="table4.amdNeg.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdNeg.iamt}" />' data-variavel="tab4Lfield4" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table4_amdNeg_csamt" name="table4.amdNeg.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4.amdNeg.csamt}" />' data-variavel="tab4Lfield5" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(M)Sub-total (I to L above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4ItoL_txval" name = "table4ItoL.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4ItoL.txval}" />'  data-formula="#tab4Jfield1# + #tab4Kfield1# - #tab4Ifield1# - #tab4Lfield1#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4ItoL_camt" name ="table4ItoL.camt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4ItoL.camt}" />' data-formula="#tab4Jfield2# + #tab4Kfield2# - #tab4Ifield2# - #tab4Lfield2#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4ItoL_samt" name ="table4ItoL.samt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4ItoL.samt}" />' data-formula="#tab4Jfield3# + #tab4Kfield3# - #tab4Ifield3# - #tab4Lfield3#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4ItoL_iamt" name ="table4ItoL.iamt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4ItoL.iamt}" />' data-formula="#tab4Jfield4# + #tab4Kfield4# - #tab4Ifield4# - #tab4Lfield4#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table4ItoL_csamt" name ="table4ItoL.csamt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4ItoL.csamt}"/>' data-formula="#tab4Jfield5# + #tab4Kfield5# - #tab4Ifield5# - #tab4Lfield5#"  /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(N)Supplies and advances on which tax is to be paid (H + M) above</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  id="table4HtoM_txval" name = "table4HtoM.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4HtoM.txval}" />' data-variavel="tab4Nfield1" data-formula="#tab4Afield1# + #tab4Bfield1# + #tab4Cfield1# + #tab4Dfield1# + #tab4Efield1# + #tab4Ffield1# + #tab4Gfield1# + #tab4Jfield1# + #tab4Kfield1# - #tab4Ifield1# - #tab4Lfield1#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  id="table4HtoM_camt" name = "table4HtoM.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4HtoM.camt}" />' data-formula="#tab4Afield2# + #tab4Bfield2# + #tab4Efield2# + #tab4Ffield2# + #tab4Gfield2# + #tab4Jfield2# + #tab4Kfield2# - #tab4Ifield2# - #tab4Lfield2#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  id="table4HtoM_samt" name = "table4HtoM.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4HtoM.samt}" />' data-formula="#tab4Afield3# + #tab4Bfield3# + #tab4Efield3# + #tab4Ffield3# + #tab4Gfield3# + #tab4Jfield3# + #tab4Kfield3# - #tab4Ifield3# - #tab4Lfield3#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  id="table4HtoM_iamt" name = "table4HtoM.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4HtoM.iamt}" />' data-formula="#tab4Afield4# + #tab4Bfield4# + #tab4Efield4# + #tab4Ffield4# + #tab4Gfield4# + #tab4Jfield4# + #tab4Kfield4# - #tab4Ifield4# - #tab4Lfield4#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  id="table4HtoM_csamt" name = "table4HtoM.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table4HtoM.csamt}" />' data-formula="#tab4Afield5# + #tab4Bfield5# + #tab4Efield5# + #tab4Ffield5# + #tab4Gfield5# + #tab4Jfield5# + #tab4Kfield5# - #tab4Ifield5# - #tab4Lfield5#" /><div class="help-block with-errors"></div></td>
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
						          (5)Details of Outward supplies on which tax is not payable as declared in returns filed during the financial year
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerTwo" class="collapse show" aria-labelledby="headinginnerTwo" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Nature of Supplies</th>
										<th class="text-left">Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)Zero rated supply (Export) without payment of tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_zeroRtd_txval" name="table5.zeroRtd.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.zeroRtd.txval}" />' data-variavel="tab5Afield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(B)Supply to SEZs without payment of tax </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_sez_txval" name="table5.sez.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.sez.txval}" />' data-variavel="tab5Bfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(C)Supplies on which tax is to be paid by the recipient on reverse charge basis</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_rchrg_txval" name="table5.rchrg.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.rchrg.txval}" />' data-variavel="tab5Cfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(D)Exempted </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_exmt_txval" name="table5.exmt.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.exmt.txval}" />' data-variavel="tab5Dfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(E)Nil Rated </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_nil_txval" name="table5.nil.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.nil.txval}" />' data-variavel="tab5Efield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(F)Non-GST supply</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_nonGst_txval" name="table5.nonGst.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.nonGst.txval}" />' data-variavel="tab5Ffield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(G)Sub-total (A to F above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" id="table5AtoF_txval" name="table5AtoF.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5AtoF.txval}" />' data-formula="#tab5Afield1# + #tab5Bfield1# + #tab5Cfield1# + #tab5Dfield1# + #tab5Efield1# + #tab5Ffield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(H)Credit Notes issued in respect of transactions specified in A to F above (-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_crNt_txval" name="table5.crNt.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.crNt.txval}" />' data-variavel="tab5Hfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(I)Debit Notes issued in respect of transactions specified in A to F above (+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_drNt_txval" name="table5.drNt.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.drNt.txval}" />' data-variavel="tab5Ifield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(J)Supplies declared through Amendments (+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_amdPos_txval" name="table5.amdPos.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.amdPos.txval}" />' data-variavel="tab5Jfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(K)Supplies reduced through Amendments (-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="table5_amdNeg_txval" name="table5.amdNeg.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5.amdNeg.txval}" />' data-variavel="tab5Kfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(L)Sub-Total (H to K above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="table5HtoK_txval" name="table5HtoK.txval" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table5HtoK.txval}" />' data-formula="#tab5Ifield1# + #tab5Jfield1# - #tab5Hfield1# - #tab5Kfield1#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(M)Turnover on which tax is not to be paid (G + L above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="turnoverOnTaxNotPaid_txval" name="turnoverOnTaxNotPaid.txval" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.turnoverOnTaxNotPaid.txval}" />' data-formula="#tab5Afield1# + #tab5Bfield1# + #tab5Cfield1# + #tab5Dfield1# + #tab5Efield1# + #tab5Ffield1# + #tab5Ifield1# + #tab5Jfield1# - #tab5Hfield1# - #tab5Kfield1#" data-variavel="tab5Kfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(N)Total Turnover (including advances)(4N + 5M - 4G above)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="totalTurnOver_txval" name="totalTurnOver.txval" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.totalTurnOver.txval}" />' data-variavel="tab5Nfield1" data-formula="#tab4Afield1# + #tab4Bfield1# + #tab4Cfield1# + #tab4Dfield1# + #tab4Efield1# + #tab4Ffield1# + #tab4Gfield1# + #tab4Jfield1# + #tab4Kfield1# - #tab4Ifield1# - #tab4Lfield1# + #tab5Afield1# + #tab5Bfield1# + #tab5Cfield1# + #tab5Dfield1# + #tab5Efield1# + #tab5Ffield1# + #tab5Ifield1# + #tab5Jfield1# - #tab5Hfield1# - #tab5Kfield1# - #tab4Gfield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="totalTurnOver_camt" name="totalTurnOver.camt" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.totalTurnOver.camt}" />' data-variavel="tab5Nfield2" data-formula="#tab4Afield2# + #tab4Bfield2# + #tab4Cfield2# + #tab4Dfield2# + #tab4Efield2# + #tab4Ffield2# + #tab4Gfield2# + #tab4Jfield2# + #tab4Kfield2# - #tab4Ifield2# - #tab4Lfield2# - #tab4Gfield2# - #tab4Gfield2# - #tab4Gfield2# - #tab4Gfield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="totalTurnOver_samt" name="totalTurnOver.samt" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.totalTurnOver.samt}" />' data-variavel="tab5Nfield3" data-formula="#tab4Afield3# + #tab4Bfield3# + #tab4Cfield3# + #tab4Dfield3# + #tab4Efield3# + #tab4Ffield3# + #tab4Gfield3# + #tab4Jfield3# + #tab4Kfield3# - #tab4Ifield3# - #tab4Lfield3# - #tab4Gfield3# - #tab4Gfield3# - #tab4Gfield3# - #tab4Gfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="totalTurnOver_iamt" name="totalTurnOver.iamt" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.totalTurnOver.iamt}" />' data-variavel="tab5Nfield4" data-formula="#tab4Afield4# + #tab4Bfield4# + #tab4Cfield4# + #tab4Dfield4# + #tab4Efield4# + #tab4Ffield4# + #tab4Gfield4# + #tab4Jfield4# + #tab4Kfield4# - #tab4Ifield4# - #tab4Lfield4# - #tab4Gfield4# - #tab4Gfield4# - #tab4Gfield4# - #tab4Gfield4#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input auto-val-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" id="totalTurnOver_csamt" name="totalTurnOver.csamt" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.totalTurnOver.csamt}" />' data-variavel="tab5Nfield5" data-formula="#tab4Afield5# + #tab4Bfield5# + #tab4Cfield5# + #tab4Dfield5# + #tab4Efield5# + #tab4Ffield5# + #tab4Gfield5# + #tab4Jfield5# + #tab4Kfield5# - #tab4Ifield5# - #tab4Lfield5# - #tab4Gfield5# - #tab4Gfield5# - #tab4Gfield5# - #tab4Gfield5#"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  
						</div>
					</div>
				</div>
						
						<div class="tab-pane" id="main_tab2" role="tabpanel">
						<div class="group upload-btn">
							<div class="mb-2">Details of ITC as declared in returns filed during the financial year<div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_2"> Help Guide</div><span class="pull-right"> <a href="#" class="btn btn-sm btn-blue-dark tptwo-edit"  onClick="clickEdit('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a> <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap',2);" >Cancel</a></span></div>
						</div>
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerThree">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerThree" aria-expanded="true" aria-controls="collapseinnerThree">
						       (6)Details of ITC availed as declared in returns filed during the financial year
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerThree" class="collapse show" aria-labelledby="headinginnerThree" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable2 mt-2">       
    						<table id="dbTable3" class="dbTable2 display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Type</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr class="">
										<td class="text-left" colspan="2">(A)Total amount of input tax credit availed through FORM GSTR-3B (sum total of Table 4A of FORM GSTR-3B)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc3b_camt" name = "itc3b.camt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc3b.camt}" />' data-variavel="tab6Afield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc3b_samt" name = "itc3b.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc3b.samt}" />' data-variavel="tab6Afield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc3b_iamt" name = "itc3b.iamt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc3b.iamt}" />' data-variavel="tab6Afield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc3b_csamt" name = "itc3b.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc3b.csamt}" />' data-variavel="tab6Afield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" rowspan="3">(B)Inward supplies (other than imports and inward supplies liable to reverse charge but includes services received from SEZs)</td>
										<td class="text-right form-group gst-3b-error" style="border-left: 1px solid #ddd;">Inputs<input type="hidden" name="table6.suppNonRchrg[0].itcTyp" value="ip"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg0_camt" name="table6.suppNonRchrg[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[0].camt}" />' data-variavel="tab6BIfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[0]_samt" name="table6.suppNonRchrg[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[0].samt}" />' data-variavel="tab6BIfield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[0]_iamt" name="table6.suppNonRchrg[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[0].iamt}" />' data-variavel="tab6BIfield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[0]_csamt" name="table6.suppNonRchrg[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[0].csamt}" />' data-variavel="tab6BIfield4" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Capital Goods<input type="hidden" name="table6.suppNonRchrg[1].itcTyp" value="cg"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg1_camt" name="table6.suppNonRchrg[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[1].camt}" />' data-variavel="tab6BCfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[1]_samt" name="table6.suppNonRchrg[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[1].samt}" />' data-variavel="tab6BCfield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[1]_iamt" name="table6.suppNonRchrg[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[1].iamt}" />' data-variavel="tab6BCfield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[1]_csamt" name="table6.suppNonRchrg[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[1].csamt}" />' data-variavel="tab6BCfield4" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Input Services<input type="hidden" name="table6.suppNonRchrg[2].itcTyp" value="is"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[2]_camt" name="table6.suppNonRchrg[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[2].camt}" />' data-variavel="tab6BISfield1" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[2]_samt" name="table6.suppNonRchrg[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[2].samt}" />' data-variavel="tab6BISfield2" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[2]_iamt" name="table6.suppNonRchrg[2].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[2].iamt}" />' data-variavel="tab6BISfield3" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppNonRchrg[2]_csamt" name="table6.suppNonRchrg[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppNonRchrg[2].csamt}" />' data-variavel="tab6BISfield4" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" rowspan="3">(C)Inward supplies received from unregistered persons liable to reverse charge (other than B above) on which tax is paid & ITC availed</td>
										<td class="text-right form-group gst-3b-error" style="border-left: 1px solid #ddd;">Inputs<input type="hidden" name="table6.suppRchrgUnreg[0].itcTyp" value="ip"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[0]_camt" name="table6.suppRchrgUnreg[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[0].camt}" />' data-variavel = "tab6CIfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[0]_samt" name="table6.suppRchrgUnreg[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[0].samt}" />' data-variavel = "tab6CIfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[0]_iamt" name="table6.suppRchrgUnreg[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[0].iamt}" />' data-variavel = "tab6CIfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[0]_csamt" name="table6.suppRchrgUnreg[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[0].csamt}" />' data-variavel = "tab6CIfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Capital Goods<input type="hidden" name="table6.suppRchrgUnreg[1].itcTyp" value="cg"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[1]_camt" name="table6.suppRchrgUnreg[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[1].camt}" />' data-variavel = "tab6CCfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[1]_samt" name="table6.suppRchrgUnreg[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[1].samt}" />' data-variavel = "tab6CCfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[1]_iamt" name="table6.suppRchrgUnreg[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[1].iamt}" />' data-variavel = "tab6CCfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[1]_csamt" name="table6.suppRchrgUnreg[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[1].csamt}" />' data-variavel = "tab6CCfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Input Services<input type="hidden" name="table6.suppRchrgUnreg[2].itcTyp" value="is"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[2]_camt" name="table6.suppRchrgUnreg[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[2].camt}" />' data-variavel = "tab6CISfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[2]_samt" name="table6.suppRchrgUnreg[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[2].samt}" />' data-variavel = "tab6CISfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[2]_iamt" name="table6.suppRchrgUnreg[2].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[2].iamt}" />' data-variavel = "tab6CISfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgUnreg[2]_csamt" name="table6.suppRchrgUnreg[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgUnreg[2].csamt}" />' data-variavel = "tab6CISfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" rowspan="3">(D)Inward supplies received from registered persons liable to reverse charge (other than B above) on which tax is paid and ITC availed</td>
										<td class="text-right form-group gst-3b-error" style="border-left: 1px solid #ddd;">Inputs<input type="hidden" name="table6.suppRchrgReg[0].itcTyp" value="ip"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[0]_camt" name="table6.suppRchrgReg[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[0].camt}" />' data-variavel = "tab6DIfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[0]_samt" name="table6.suppRchrgReg[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[0].samt}" />' data-variavel = "tab6DIfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[0]_iamt" name="table6.suppRchrgReg[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[0].iamt}" />' data-variavel = "tab6DIfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[0]_csamt" name="table6.suppRchrgReg[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[0].csamt}" />' data-variavel = "tab6DIfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Capital Goods<input type="hidden" name="table6.suppRchrgReg[1].itcTyp" value="cg"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[1]_camt" name="table6.suppRchrgReg[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[1].camt}" />' data-variavel = "tab6DCfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[1]_samt" name="table6.suppRchrgReg[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[1].samt}" />' data-variavel = "tab6DCfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[1]_iamt" name="table6.suppRchrgReg[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[1].iamt}" />' data-variavel = "tab6DCfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[1]_csamt" name="table6.suppRchrgReg[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[1].csamt}" />' data-variavel = "tab6DCfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Input Services<input type="hidden" name="table6.suppRchrgReg[2].itcTyp" value="is"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[2]_camt" name="table6.suppRchrgReg[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[2].camt}" />' data-variavel = "tab6DISfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[2]_samt" name="table6.suppRchrgReg[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[2].samt}" />' data-variavel = "tab6DISfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[2]_iamt" name="table6.suppRchrgReg[2].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[2].iamt}" />' data-variavel = "tab6DISfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_suppRchrgReg[2]_csamt" name="table6.suppRchrgReg[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.suppRchrgReg[2].csamt}" />' data-variavel = "tab6DISfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" rowspan="2">(E)Import of goods (including supplies from SEZs)</td>
										<td class="text-right form-group gst-3b-error" style="border-left: 1px solid #ddd;">Inputs<input type="hidden" name="table6.iog[0].itcTyp" value="ip"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_iog[0]_iamt" name="table6.iog[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.iog[0].iamt}" />' data-variavel = "tab6EIfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_iog[0]_csamt" name="table6.iog[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.iog[0].csamt}" />' data-variavel = "tab6EIfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">Capital Goods<input type="hidden" name="table6.iog[1].itcTyp" value="cg"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_iog[1]_iamt" name="table6.iog[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.iog[1].iamt}" />' data-variavel = "tab6ECfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_iog[1]_csamt" name="table6.iog[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.iog[1].csamt}" />' data-variavel = "tab6ECfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)Import of services (excluding inward supplies from SEZs)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_ios_iamt" name="table6.ios.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.ios.iamt}" />' data-variavel = "tab6Ffield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_ios_csamt" name="table6.ios.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.ios.csamt}" />' data-variavel = "tab6Ffield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(G)Input Tax credit received from ISD</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_isd_camt" name="table6.isd.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.isd.camt}" />' data-variavel = "tab6Gfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_isd_samt" name="table6.isd.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.isd.samt}" />' data-variavel = "tab6Gfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_isd_iamt" name="table6.isd.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.isd.iamt}" />' data-variavel = "tab6Gfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_isd_csamt" name="table6.isd.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.isd.csamt}" />' data-variavel = "tab6Gfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(H)Amount of ITC reclaimed (other than B above) under the provisions of the Act</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_itcClmd_camt" name="table6.itcClmd.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.itcClmd.camt}" />' data-variavel = "tab6Hfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_itcClmd_samt" name="table6.itcClmd.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.itcClmd.samt}" />' data-variavel = "tab6Hfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_itcClmd_iamt" name="table6.itcClmd.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.itcClmd.iamt}" />' data-variavel = "tab6Hfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_itcClmd_csamt" name="table6.itcClmd.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.itcClmd.csamt}" />' data-variavel = "tab6Hfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(I)Sub-total (B to H above)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6BtoH_camt" name="table6BtoH.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6BtoH.camt}" />' data-formula="#tab6BIfield1# + #tab6BCfield1# + #tab6BISfield1# + #tab6CIfield1# + #tab6CCfield1# + #tab6CISfield1# + #tab6DIfield1# + #tab6DCfield1# + #tab6DISfield1# + #tab6Gfield1# + #tab6Hfield1#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6BtoH_samt" name="table6BtoH.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6BtoH.samt}" />' data-formula="#tab6BIfield2# + #tab6BCfield2# + #tab6BISfield2# + #tab6CIfield2# + #tab6CCfield2# + #tab6CISfield2# + #tab6DIfield2# + #tab6DCfield2# + #tab6DISfield2# + #tab6Gfield2# + #tab6Hfield2#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6BtoH_iamt" name="table6BtoH.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6BtoH.iamt}" />' data-formula="#tab6BIfield3# + #tab6BCfield3# + #tab6BISfield3# + #tab6CIfield3# + #tab6CCfield3# + #tab6CISfield3# + #tab6DIfield3# + #tab6DCfield3# + #tab6DISfield3# + #tab6EIfield3# + #tab6ECfield3# + #tab6Ffield3#  + #tab6Gfield3# + #tab6Hfield3#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6BtoH_csamt" name="table6BtoH.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6BtoH.csamt}" />' data-formula="#tab6BIfield4# + #tab6BCfield4# + #tab6BISfield4# + #tab6CIfield4# + #tab6CCfield4# + #tab6CISfield4# + #tab6DIfield4# + #tab6DCfield4# + #tab6DISfield4# + #tab6EIfield4# + #tab6ECfield4# + #tab6Ffield4#  + #tab6Gfield4# + #tab6Hfield4#" /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(J)Difference (I - A above)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6J_camt" name="table6J.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6J.camt}" />' data-formula="#tab6BIfield1# + #tab6BCfield1# + #tab6BISfield1# + #tab6CIfield1# + #tab6CCfield1# + #tab6CISfield1# + #tab6DIfield1# + #tab6DCfield1# + #tab6DISfield1# + #tab6Gfield1# + #tab6Hfield1# - #tab6Afield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6J_samt" name="table6J.samt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6J.camt}" />' data-formula="#tab6BIfield2# + #tab6BCfield2# + #tab6BISfield2# + #tab6CIfield2# + #tab6CCfield2# + #tab6CISfield2# + #tab6DIfield2# + #tab6DCfield2# + #tab6DISfield2# + #tab6Gfield2# + #tab6Hfield2# - #tab6Afield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6J_iamt" name="table6J.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6J.camt}" />' data-formula="#tab6BIfield3# + #tab6BCfield3# + #tab6BISfield3# + #tab6CIfield3# + #tab6CCfield3# + #tab6CISfield3# + #tab6DIfield3# + #tab6DCfield3# + #tab6DISfield3# + #tab6EIfield3# + #tab6ECfield3# + #tab6Ffield3#  + #tab6Gfield3# + #tab6Hfield3# - #tab6Afield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6J_csamt" name="table6J.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6J.camt}" />' data-formula="#tab6BIfield4# + #tab6BCfield4# + #tab6BISfield4# + #tab6CIfield4# + #tab6CCfield4# + #tab6CISfield4# + #tab6DIfield4# + #tab6DCfield4# + #tab6DISfield4# + #tab6EIfield4# + #tab6ECfield4# + #tab6Ffield4#  + #tab6Gfield4# + #tab6Hfield4# - #tab6Afield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(K)Transition Credit through TRAN-I (including revisions if any)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_tran1_camt" name="table6.tran1.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.tran1.camt}" />' data-variavel = "tab6Kfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_tran1_samt" name="table6.tran1.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.tran1.samt}" />' data-variavel = "tab6Kfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(L)Transition Credit through TRAN-II</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_tran2_camt" name="table6.tran2.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.tran2.camt}" />' data-variavel = "tab6Lfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_tran2_samt" name="table6.tran2.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.tran2.samt}" />' data-variavel = "tab6Lfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(M)Any other ITC availed but not specified above</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_other_camt" name="table6.other.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.other.camt}" />' data-variavel = "tab6Mfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_other_samt" name="table6.other.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.other.samt}" />' data-variavel = "tab6Mfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_other_iamt" name="table6.other.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.other.iamt}" />' data-variavel = "tab6Mfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table6_other_csamt" name="table6.other.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6.other.csamt}" />' data-variavel = "tab6Mfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(N)Sub-total (K to M above)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6N_camt" name="table6N.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6N.camt}" />' data-formula="#tab6Kfield1# + #tab6Lfield1# + #tab6Mfield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6N_samt" name="table6N.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6N.samt}" />' data-formula="#tab6Kfield2# + #tab6Lfield2# + #tab6Mfield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6N_iamt" name="table6N.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6N.iamt}" />' data-formula="#tab6Mfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6N_csamt" name="table6N.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6N.csamt}" />' data-formula="#tab6Mfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(O)Total ITC availed (I + N above)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6O_camt" name="table6O.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6O.camt}" />' data-formula="#tab6BIfield1# + #tab6BCfield1# + #tab6BISfield1# + #tab6CIfield1# + #tab6CCfield1# + #tab6CISfield1# + #tab6DIfield1# + #tab6DCfield1# + #tab6DISfield1# + #tab6Gfield1# + #tab6Hfield1# + #tab6Kfield1# + #tab6Lfield1# + #tab6Mfield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6O_samt" name="table6O.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6O.samt}" />' data-formula="#tab6BIfield2# + #tab6BCfield2# + #tab6BISfield2# + #tab6CIfield2# + #tab6CCfield2# + #tab6CISfield2# + #tab6DIfield2# + #tab6DCfield2# + #tab6DISfield2# + #tab6Gfield2# + #tab6Hfield2# + #tab6Kfield2# + #tab6Lfield2# + #tab6Mfield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6O_iamt" name="table6O.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6O.iamt}" />' data-formula="#tab6BIfield3# + #tab6BCfield3# + #tab6BISfield3# + #tab6CIfield3# + #tab6CCfield3# + #tab6CISfield3# + #tab6DIfield3# + #tab6DCfield3# + #tab6DISfield3# + #tab6EIfield3# + #tab6ECfield3# + #tab6Ffield3#  + #tab6Gfield3# + #tab6Hfield3# + #tab6Mfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table6O_csamt" name="table6O.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table6O.csamt}" />' data-formula="#tab6BIfield4# + #tab6BCfield4# + #tab6BISfield4# + #tab6CIfield4# + #tab6CCfield4# + #tab6CISfield4# + #tab6DIfield4# + #tab6DCfield4# + #tab6DISfield4# + #tab6EIfield4# + #tab6ECfield4# + #tab6Ffield4#  + #tab6Gfield4# + #tab6Hfield4# + #tab6Mfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
					   	  </div>
						      </div>
						    </div>
						  </div>
						  <div class="card">
						    <div class="card-header" id="headinginnerFour">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFour" aria-expanded="true" aria-controls="collapseinnerFour">
						       (7)Details of ITC Reversed and Ineligible ITC as declared in returns filed during the financial year
							   </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFour" class="collapse show" aria-labelledby="headinginnerFour" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable2 mt-2">       
    						<table id="dbTable4" class="dbTable2 display add-row-tb  row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left"></th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)As per Rule 37</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule37_camt" name="table7.rule37.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule37.camt}" />' data-variavel = "tab7Afield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule37_samt" name="table7.rule37.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule37.samt}" />' data-variavel = "tab7Afield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule37_iamt" name="table7.rule37.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule37.iamt}" />' data-variavel = "tab7Afield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule37_csamt" name="table7.rule37.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule37.csamt}" />' data-variavel = "tab7Afield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(B)As per Rule 39 </td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule39_camt" name="table7.rule39.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule39.camt}" />' data-variavel = "tab7Bfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule39_samt" name="table7.rule39.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule39.samt}" />' data-variavel = "tab7Bfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule39_iamt" name="table7.rule39.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule39.iamt}" />' data-variavel = "tab7Bfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule39_csamt" name="table7.rule39.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule39.csamt}" />' data-variavel = "tab7Bfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(C)As per Rule 42</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule42_camt" name="table7.rule42.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule42.camt}" />' data-variavel = "tab7Cfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule42_samt" name="table7.rule42.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule42.samt}" />' data-variavel = "tab7Cfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule42_iamt" name="table7.rule42.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule42.iamt}" />' data-variavel = "tab7Cfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule42_csamt" name="table7.rule42.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule42.csamt}" />' data-variavel = "tab7Cfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(D)As per Rule 43 </td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule43_camt" name="table7.rule43.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule43.camt}" />' data-variavel = "tab7Dfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule43_samt" name="table7.rule43.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule43.samt}" />' data-variavel = "tab7Dfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule43_iamt" name="table7.rule43.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule43.iamt}" />' data-variavel = "tab7Dfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_rule43_csamt" name="table7.rule43.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.rule43.csamt}" />' data-variavel = "tab7Dfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(E)As per section 17(5)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_sec17_camt" name="table7.sec17.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.sec17.camt}" />' data-variavel = "tab7Efield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_sec17_samt" name="table7.sec17.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.sec17.samt}" />' data-variavel = "tab7Efield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_sec17_iamt" name="table7.sec17.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.sec17.iamt}" />' data-variavel = "tab7Efield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_sec17_csamt" name="table7.sec17.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.sec17.csamt}" />' data-variavel = "tab7Efield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)Reversal of TRAN-I credit</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_revslTran1_camt" name="table7.revslTran1.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.revslTran1.camt}" />' data-variavel = "tab7Ffield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_revslTran1_samt" name="table7.revslTran1.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.revslTran1.samt}" />' data-variavel = "tab7Ffield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(G)Reversal of TRAN-II credit</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_revslTran2_camt" name="table7.revslTran2.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.revslTran2.camt}" />' data-variavel = "tab7Gfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_revslTran2_samt" name="table7.revslTran2.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.revslTran2.samt}" />' data-variavel = "tab7Gfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr class="">
										<td class="text-left">(H)Other reversals (pl. specify)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_other[0]_desc" name="table7.other[0].desc" placeholder="Description"  maxlength="30"><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_other[0]_camt" name="table7.other[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.other[0].camt}" />' data-variavel = "tab7Hfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_other[0]_samt" name="table7.other[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.other[0].samt}" />' data-variavel = "tab7Hfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_other[0]_iamt" name="table7.other[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.other[0].iamt}" />' data-variavel = "tab7Hfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table7_other[0]_csamt" name="table7.other[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7.other[0].csamt}" />' data-variavel = "tab7Hfield4"/><div class="help-block with-errors"></div></td>
										<td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row mt-1">+</a></td>									
									</tr>
									<tr class="auto-row">
										<td class="text-left">(I)Total ITC Reversed (A to H above)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="totalitcrev1" name="table7I.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7I.camt}" />' data-formula = "#tab7Afield1# + #tab7Bfield1# + #tab7Cfield1# + #tab7Dfield1# + #tab7Efield1# + #tab7Ffield1# + #tab7Gfield1# + #tab7Hfield1# "/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="totalitcrev2" name="table7I.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7I.samt}" />' data-formula = "#tab7Afield2# + #tab7Bfield2# + #tab7Cfield2# + #tab7Dfield2# + #tab7Efield2# + #tab7Ffield2# + #tab7Gfield2# + #tab7Hfield2# "/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="totalitcrev3" name="table7I.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7I.iamt}" />' data-formula = "#tab7Afield3# + #tab7Bfield3# + #tab7Cfield3# + #tab7Dfield3# + #tab7Efield3# + #tab7Hfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="totalitcrev4" name="table7I.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7I.csamt}" />' data-formula = "#tab7Afield4# + #tab7Bfield4# + #tab7Cfield4# + #tab7Dfield4# + #tab7Efield4# + #tab7Hfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(J)Net ITC Available for Utilization (6O - 7I)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="netitcutil1" name="table7J.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7J.camt}" />' data-formula = "#tab6BIfield1# + #tab6BCfield1# + #tab6BISfield1# + #tab6CIfield1# + #tab6CCfield1# + #tab6CISfield1# + #tab6DIfield1# + #tab6DCfield1# + #tab6DISfield1# + #tab6Gfield1# + #tab6Hfield1# + #tab6Kfield1# + #tab6Lfield1# + #tab6Mfield1# - #tab7Afield1# - #tab7Bfield1# - #tab7Cfield1# - #tab7Dfield1# - #tab7Efield1# - #tab7Ffield1# - #tab7Gfield1# - #tab7Hfield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="netitcutil2" name="table7J.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7J.samt}" />' data-formula = "#tab6BIfield2# + #tab6BCfield2# + #tab6BISfield2# + #tab6CIfield2# + #tab6CCfield2# + #tab6CISfield2# + #tab6DIfield2# + #tab6DCfield2# + #tab6DISfield2# + #tab6Gfield2# + #tab6Hfield2# + #tab6Kfield2# + #tab6Lfield2# + #tab6Mfield2# - #tab7Afield2# - #tab7Bfield2# - #tab7Cfield2# - #tab7Dfield2# - #tab7Efield2# - #tab7Ffield2# - #tab7Gfield2# - #tab7Hfield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="netitcutil3" name="table7J.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7J.iamt}" />' data-formula = "#tab6BIfield3# + #tab6BCfield3# + #tab6BISfield3# + #tab6CIfield3# + #tab6CCfield3# + #tab6CISfield3# + #tab6DIfield3# + #tab6DCfield3# + #tab6DISfield3# + #tab6EIfield3# + #tab6ECfield3# + #tab6Ffield3#  + #tab6Gfield3# + #tab6Hfield3# + #tab6Mfield3# - #tab7Afield3# - #tab7Bfield3# - #tab7Cfield3# - #tab7Dfield3# - #tab7Efield3# - #tab7Hfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="netitcutil4" name="table7J.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table7J.csamt}" />' data-formula = "#tab6BIfield4# + #tab6BCfield4# + #tab6BISfield4# + #tab6CIfield4# + #tab6CCfield4# + #tab6CISfield4# + #tab6DIfield4# + #tab6DCfield4# + #tab6DISfield4# + #tab6EIfield4# + #tab6ECfield4# + #tab6Ffield4#  + #tab6Gfield4# + #tab6Hfield4# + #tab6Mfield4# - #tab7Afield4# - #tab7Bfield4# - #tab7Cfield4# - #tab7Dfield4# - #tab7Efield4# - #tab7Hfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									</tbody>
									</table>
								</div>
						      </div>
						    </div>
						  </div>
						  <div class="card">
						    <div class="card-header" id="headinginnerFive">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFive" aria-expanded="true" aria-controls="collapseinnerFive">
						          (8)Other ITC related information
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFive" class="collapse show" aria-labelledby="headinginnerFive" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable2 mt-2">       
    						<table id="dbTable5" class="dbTable2 display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left"></th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr class="auto-row">
										<td class="text-left">(A)ITC as per GSTR-2A (Table 3 & 5 thereof)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc2a_camt" name="itc2a.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc2a.camt}" />' data-variavel = "tab8Afield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc2a_samt" name="itc2a.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc2a.samt}" />' data-variavel = "tab8Afield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc2a_iamt" name="itc2a.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc2a.iamt}" />' data-variavel = "tab8Afield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="itc2a_csamt" name="itc2a.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itc2a.csamt}" />' data-variavel = "tab8Afield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(B)ITC as per sum total of 6(B) and 6(H) above</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8B_camt" name="table8B.camt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8B.camt}"/>' data-variavel = "tab8Bfield1" data-formula = "#tab6BIfield1# + #tab6BCfield1# + #tab6BISfield1# + #tab6Hfield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8B_samt" name="table8B.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8B.samt}" />' data-variavel = "tab8Bfield2" data-formula = "#tab6BIfield2# + #tab6BCfield2# + #tab6BISfield2# + #tab6Hfield2#" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8B_iamt" name="table8B.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8B.iamt}" />' data-variavel = "tab8Bfield3" data-formula = "#tab6BIfield3# + #tab6BCfield3# + #tab6BISfield3# + #tab6Hfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8B_csamt" name="table8B.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8B.csamt}" />' data-variavel = "tab8Bfield4" data-formula = "#tab6BIfield4# + #tab6BCfield4# + #tab6BISfield4# + #tab6Hfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(C)ITC on inward supplies (other than imports and inward supplies liable to reverse charge but includes services received from SEZs) received during 2017-18 but availed during April to September, 2018 </td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcInwdSupp_camt" name="table8.itcInwdSupp.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcInwdSupp.camt}" />' data-variavel = "tab8Cfield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcInwdSupp_samt" name="table8.itcInwdSupp.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcInwdSupp.samt}" />' data-variavel = "tab8Cfield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcInwdSupp_iamt" name="table8.itcInwdSupp.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcInwdSupp.iamt}" />' data-variavel = "tab8Cfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcInwdSupp_csamt" name="table8.itcInwdSupp.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcInwdSupp.csamt}" />' data-variavel = "tab8Cfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(D)Difference [A-(B+C)]</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8D_camt" name="table8D.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8D.camt}" />' data-formula = "#tab8Afield1# - #tab8Bfield1# - #tab8Cfield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8D_samt" name="table8D.samt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8D.samt}" />' data-formula = "#tab8Afield2# - #tab8Bfield2# - #tab8Cfield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8D_iamt" name="table8D.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8D.iamt}" />' data-formula = "#tab8Afield3# - #tab8Bfield3# - #tab8Cfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8D_csamt" name="table8D.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8D.csamt}" />' data-formula = "#tab8Afield4# - #tab8Bfield4# - #tab8Cfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(E)ITC available but not availed (out of D)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtAvaild_camt" name="table8.itcNtAvaild.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtAvaild.camt}" />' data-variavel = "tab8Efield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtAvaild_samt" name="table8.itcNtAvaild.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtAvaild.samt}" />' data-variavel = "tab8Efield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtAvaild_iamt" name="table8.itcNtAvaild.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtAvaild.samt}" />' data-variavel = "tab8Efield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtAvaild_csamt" name="table8.itcNtAvaild.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtAvaild.csamt}" />' data-variavel = "tab8Efield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)ITC available but ineligible (out of D)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtEleg_camt" name="table8.itcNtEleg.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtEleg.camt}" />' data-variavel = "tab8Ffield1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtEleg_samt" name="table8.itcNtEleg.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtEleg.samt}" />' data-variavel = "tab8Ffield2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtEleg_iamt" name="table8.itcNtEleg.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtEleg.iamt}" />' data-variavel = "tab8Ffield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_itcNtEleg_csamt" name="table8.itcNtEleg.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.itcNtEleg.csamt}" />' data-variavel = "tab8Ffield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(G)IGST paid on import of goods (including supplies from SEZ)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_iogTaxpaid_iamt" name="table8.iogTaxpaid.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.iogTaxpaid.iamt}" />' data-variavel = "tab8Gfield3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="table8_iogTaxpaid_csamt" name="table8.iogTaxpaid.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8.iogTaxpaid.csamt}" />' data-variavel = "tab8Gfield4"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(H)IGST credit availed on import of goods (as per 6(E) above)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8H_iamt" name="table8H.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8H.iamt}" />' data-variavel = "tab8Hfield3" data-formula = "#tab6EIfield3# + #tab6ECfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8H_csamt" name="table8H.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8H.csamt}" />' data-variavel = "tab8Hfield4" data-formula = "#tab6EIfield4# + #tab6ECfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(I)Difference (G-H)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8I_iamt" name="table8I.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8I.iamt}" />' data-variavel = "tab8Ifield3" data-formula = "#tab8Gfield3# - #tab8Hfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8I_camt" name="table8I.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8I.csamt}" />' data-variavel = "tab8Ifield4" data-formula = "#tab8Gfield4# - #tab8Hfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(J)ITC available but not availed on import of goods (Equal to I)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8J_iamt" name="table8J.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8J.iamt}" />' data-variavel = "tab8Jfield3" data-formula = "#tab8Ifield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8J_csamt" name="table8J.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8J.csamt}" />' data-variavel = "tab8Jfield4" data-formula = "#tab8Ifield4#"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">(K)Total ITC to be lapsed in current financial year (E + F + J)</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8K_camt" name="table8K.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8K.camt}" />' data-variavel = "tab8Kfield1" data-formula = "#tab8Efield1# + #tab8Ffield1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8K_samt" name="table8K.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8K.samt}" />' data-variavel = "tab8Kfield2" data-formula = "#tab8Efield2# + #tab8Ffield2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8K_iamt" name="table8K.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8K.iamt}" />' data-variavel = "tab8Kfield3" data-formula = "#tab8Efield3# + #tab8Ffield3# + #tab8Jfield3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input auto-val-input" id="table8K_csamt" name="table8K.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table8K.csamt}" />' data-variavel = "tab8Kfield4" data-formula = "#tab8Efield4# + #tab8Ffield4# + #tab8Jfield4#"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						</div>
						</div>
					<div class="tab-pane" id="main_tab3" role="tabpanel">
					<div class="group upload-btn">
							<div class="mb-2"> Details of tax paid as declared in returns filed during the financial year<div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_3"> Help Guide</div><span class="pull-right"> <a href="#" class="btn btn-sm  btn-blue-dark tpthree-edit"  onClick="clickEdit('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpthree-cancel" onClick="clickSave('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpthree-cancel" onClick="clickCancel('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input','',3);" >Cancel</a></span></div>
						</div>
						<div class="customtable db-ca-gst tabtable3 mt-2">
							<table id="dbTable6" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" rowspan="2">Description</th>
										<th class="text-left" rowspan="2">Tax Payable</th>
										<th class="text-left" rowspan="2">Paid through cash</th>
										<th class="text-center" colspan="4">Paid through ITC</th>
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
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input " id="table9_iamt_txpyble" name="table9.iamt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.iamt.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input auto-val-input" id="iamt_txpaidCash" name="iamt.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.iamt.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input auto-val-input" id="iamt_taxPaidItcCamt" name="iamt.taxPaidItcCamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.iamt.taxPaidItcCamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input auto-val-input" id="iamt_taxPaidItcSamt" name="iamt.taxPaidItcSamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.iamt.taxPaidItcSamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input auto-val-input" id="iamt_taxPaidItcIamt" name="iamt.taxPaidItcIamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.iamt.taxPaidItcIamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc " id="table9_camt_txpyble" name="table9.camt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.camt.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="camt_txpaidCash" name="camt.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.camt.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="camt_taxPaidItcCamt" name="camt.taxPaidItcCamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.camt.taxPaidItcCamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="camt_taxPaidItcIamt" name="camt.taxPaidItcIamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.camt.taxPaidItcIamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="table9_samt_txpyble" name="table9.samt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.samt.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="samt_txpaidCash" name="samt.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.samt.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="samt_taxPaidItcSamt" name="samt.taxPaidItcSamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.samt.taxPaidItcSamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="samt_taxPaidItcIamt" name="samt.taxPaidItcIamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.samt.taxPaidItcIamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">Cess</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="table9_csamt_txpyble" name="table9.csamt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.csamt.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="csamt_txpaidCash" name="csamt.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.csamt.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="csamt_taxPaidItcCsamt" name="csamt.taxPaidItcCsamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.csamt.taxPaidItcCsamt}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Interest</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="table9_intr_txpyble" name="table9.intr.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.intr.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="intr_txpaidCash" name="intr.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.intr.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">Late fee</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="table9_fee_txpyble" name="table9.fee.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.fee.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="fee_txpaidCash" name="fee.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.fee.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">Penalty</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="table9_pen_txpyble" name="table9.pen.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.pen.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="pen_txpyble" name="pen.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.pen.txpaidCash}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">Other</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="table9_other_txpyble" name="table9.other.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table9.other.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc auto-val-input" id="other_txpaidCash" name="other.txpaidCash" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.other.txpyble}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="tab-pane" id="main_tab4" role="tabpanel">
					
						<div class="group upload-btn">
							<div class="mb-2 row"> <span class="col-md-10">Particulars of the transactions for the previous FY declared in returns of April to September of current FY or upto date of filing of annual return of previous FY whichever is earlier</span><span class="col-md-2 pull-right"><span class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_4"> Help Guide</span><span class="pull-right"><a href="#" class="btn btn-sm  btn-blue-dark tpfour-edit"  onClick="clickEdit('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpfour-cancel" onClick="clickSave('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpfour-cancel" onClick="clickCancel('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input','',4);" >Cancel</a></span></span></div>
						</div>
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerSix">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerSix" aria-expanded="true" aria-controls="collapseinnerSix">
						       10,11,12&13 Particulars of the transactions for the FY 2017-18 declared in returns between April, 2018 till March, 2019.
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerSix" class="collapse show" aria-labelledby="headinginnerSix" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable9 mt-2">
							<table id="dbTable7" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Nature of Supplies</th>
										<th class="text-left">Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(10)Supplies / tax declared through Amendments (+) (net of debit notes)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_dbnAmd_txval" name="table10.dbnAmd.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.dbnAmd.txval}" />' data-variavel = "tab10field1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_dbnAmd_camt" name="table10.dbnAmd.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.dbnAmd.camt}" />' data-variavel = "tab10field2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_dbnAmd_samt" name="table10.dbnAmd.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.dbnAmd.samt}" />' data-variavel = "tab10field3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_dbnAmd_iamt" name="table10.dbnAmd.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.dbnAmd.iamt}" />' data-variavel = "tab10field4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_dbnAmd_csamt" name="table10.dbnAmd.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.dbnAmd.csamt}" />' data-variavel = "tab10field5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(11)Supplies / tax reduced through Amendments (-) (net of credit notes)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_cdnAmd_txval" name="table10.cdnAmd.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.cdnAmd.txval}" />' data-variavel = "tab11field1"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_cdnAmd_camt" name="table10.cdnAmd.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.cdnAmd.camt}" />' data-variavel = "tab11field2"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_cdnAmd_samt" name="table10.cdnAmd.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.cdnAmd.samt}" />' data-variavel = "tab11field3"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_cdnAmd_iamt" name="table10.cdnAmd.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.cdnAmd.iamt}" />' data-variavel = "tab11field4"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_cdnAmd_csamt" name="table10.cdnAmd.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.cdnAmd.csamt}" />' data-variavel = "tab11field5"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(12)Reversal of ITC availed during previous financial year</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcRvsl_camt" name="table10.itcRvsl.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcRvsl.camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcRvsl_samt" name="table10.itcRvsl.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcRvsl.samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcRvsl_iamt" name="table10.itcRvsl.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcRvsl.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcRvsl_csamt" name="table10.itcRvsl.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcRvsl.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(13)ITC availed for the previous financial year </td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcAvaild_camt" name="table10.itcAvaild.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcAvaild.camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcAvaild_samt" name="table10.itcAvaild.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcAvaild.samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcAvaild_iamt" name="table10.itcAvaild.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcAvaild.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10_itcAvaild_csamt" name="table10.itcAvaild.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10.itcAvaild.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left">Total turnover(5N + 10 - 11)	</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10111213_txval" name="table10111213.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10111213.txval}" />' data-formula = "#tab5Nfield1# + #tab10field1# - #tab11field1#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10111213_camt" name="table10111213.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10111213.camt}" />' data-formula = "#tab5Nfield2# + #tab10field2# - #tab11field2#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10111213_samt" name="table10111213.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10111213.samt}" />' data-formula = "#tab5Nfield3# + #tab10field3# - #tab11field3#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10111213_iamt" name="table10111213.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals"  placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10111213.iamt}" />' data-formula = "#tab5Nfield4# + #tab10field4# - #tab11field4#"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="table10111213_csamt" name="table10111213.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table10111213.csamt}" />' data-formula = "#tab5Nfield5# + #tab10field5# - #tab11field5#"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  <div class="card">
						    <div class="card-header" id="headinginnerSeven">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerSeven" aria-expanded="true" aria-controls="collapseinnerSeven">
						        (14)Differential tax paid on account of declaration in 10 & 11 above
					           </button>
						      </h5>
						    </div>
						    <div id="collapseinnerSeven" class="collapse show" aria-labelledby="headinginnerSeven" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable4 mt-2">
							<table id="dbTable8" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Description</th>
										<th class="text-left">Payable(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Paid(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>Integrated Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_iamt_txpyble" name="table14.iamt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.iamt.txpyble}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_iamt_txpaid" name="table14.iamt.txpaid" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.iamt.txpaid}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr> 
									<tr>
										<td>Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_camt_txpyble" name="table14.camt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.camt.txpyble}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_camt_txpaid" name="table14.camt.txpaid" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.camt.txpaid}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td>State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_samt_txpyble" name="table14.samt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.samt.txpyble}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_samt_txpaid" name="table14.samt.txpaid" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.samt.txpaid}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr> 
									<tr>
										<td>Cess</td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_csamt_txpyble" name="table14.csamt.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.csamt.txpyble}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_csamt_txpaid" name="table14.csamt.txpaid" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.csamt.txpaid}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td>Interest</td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_intr_txpyble" name="table14.intr.txpyble" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.intr.txpyble}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="table14_intr_txpaid" name="table14.intr.txpaid" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table14.intr.txpaid}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
					</div>
					</div>
					<div class="tab-pane" id="main_tab5" role="tabpanel">
				<!--- 6 ---->
						<div class="group upload-btn">
							<div class="mb-2">Other Information<div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_3"> Help Guide</div><span class="pull-right"> <a href="#" class="btn btn-sm  btn-blue-dark tpsix-edit"  onClick="clickEdit('.tpsix-save', '.tpsix-cancel', '.tpsix-edit','.tpsix-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpsix-cancel" onClick="clickSave('.tpsix-save', '.tpsix-cancel', '.tpsix-edit','.tpsix-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpsix-cancel" onClick="clickCancel('.tpsix-save', '.tpsix-cancel', '.tpsix-edit','.tpsix-input','',6);" >Cancel</a></span></div>
						</div>
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerEight">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerEight" aria-expanded="true" aria-controls="collapseinnerEight">
						       (15)Particulars of Demands and Refunds
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerEight" class="collapse show" aria-labelledby="headinginnerEight" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable3 mt-2">
							<table id="dbTable9" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Details</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Interest</th>
										<th class="text-left">Penalty</th>
										<th class="text-left">Late Fee / Others</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)Total Refund claimed</td>
										<td class="text-right form-group gst-3b-error"><input type="text"  readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdClmd_camt" name="table15.rfdClmd.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdClmd.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text"  readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdClmd_samt" name="table15.rfdClmd.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdClmd.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdClmd_iamt" name="table15.rfdClmd.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdClmd.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdClmd_csamt" name="table15.rfdClmd.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdClmd.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(B)Total Refund sanctioned</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdSanc_camt" name="table15.rfdSanc.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdSanc.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdSanc_samt" name="table15.rfdSanc.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdSanc.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdSanc_iamt" name="table15.rfdSanc.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdSanc.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdSanc_csamt" name="table15.rfdSanc.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdSanc.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(c)Total Refund Rejected</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdRejt_camt" name="table15.rfdRejt.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdRejt.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdRejt_samt" name="table15.rfdRejt.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdRejt.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdRejt_iamt" name="table15.rfdRejt.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdRejt.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdRejt_csamt" name="table15.rfdRejt.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdRejt.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(D)Total Refund Pending</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdPend_camt" name="table15.rfdPend.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdPend.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdPend_samt" name="table15.rfdPend.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdPend.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdPend_iamt" name="table15.rfdPend.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdPend.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_rfdPend_csamt" name="table15.rfdPend.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.rfdPend.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(E)Total demand of taxes</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_camt" name="table15.taxDmnd.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_samt" name="table15.taxDmnd.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_iamt" name="table15.taxDmnd.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_csamt" name="table15.taxDmnd.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_intr" name="table15.taxDmnd.intr" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.intr}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_pen" name="table15.taxDmnd.pen" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.pen}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxDmnd_fee" name="table15.taxDmnd.fee" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxDmnd.fee}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)Total taxes paid in respect of E above</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_camt" name="table15.taxPaid.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_samt" name="table15.taxPaid.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_iamt" name="table15.taxPaid.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_csamt" name="table15.taxPaid.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_intr" name="table15.taxPaid.intr" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.intr}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_pen" name="table15.taxPaid.pen" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.pen}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_taxPaid_fee" name="table15.taxPaid.fee" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.taxPaid.fee}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(G)Total demands pending out of E above</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_camt" name="table15.dmndTaxPend.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_samt" name="table15.dmndTaxPend.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_iamt" name="table15.dmndTaxPend.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_csamt" name="table15.dmndTaxPend.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.csamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_intr" name="table15.dmndTaxPend.intr" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.intr}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_pen" name="table15.dmndTaxPend.pen" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.pen}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input elg_itc" id="table15_dmndTaxPend_fee" name="table15.dmndTaxPend.fee" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table15.dmndTaxPend.fee}" />'/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
								</table>
								</div>
						      </div>
						    </div>
						  </div>
						  <div class="card">
						    <div class="card-header" id="headinginnerNine">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerNine" aria-expanded="true" aria-controls="collapseinnerNine">
						        (16)Information on supplies received from composition taxpayers, deemed supply under section 143 and goods sent on approval basis Details <br/>Taxable Value Central
							   </button>
						      </h5>
						    </div>
						    <div id="collapseinnerNine" class="collapse show" aria-labelledby="headinginnerNine" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable8 mt-2">
							<table id="dbTable10" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Details</th>
										<th class="text-left">Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)Supplies received from Composition taxpayers</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_compSupp_txval" name="table16.compSupp.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.compSupp.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
									</tr>
									<tr>
										<td class="text-left">(B)Deemed supply under Section 143 </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_deemedSupp_txval" name="table16.deemedSupp.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.deemedSupp.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_deemedSupp_camt" name="table16.deemedSupp.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.deemedSupp.camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_deemedSupp_samt" name="table16.deemedSupp.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.deemedSupp.samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_deemedSupp_iamt" name="table16.deemedSupp.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.deemedSupp.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_deemedSupp_csamt" name="table16.deemedSupp.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.deemedSupp.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(C)Goods sent on approval basis but not returned</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_notReturned_txval" name="table16.notReturned.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.notReturned.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_notReturned_camt" name="table16.notReturned.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.notReturned.camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_notReturned_samt" name="table16.notReturned.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.notReturned.samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_notReturned_iamt" name="table16.notReturned.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.notReturned.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table16_notReturned_csamt" name="table16.notReturned.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table16.notReturned.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  <div class="card">
						    <div class="card-header" id="headinginnerTen">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTen" aria-expanded="true" aria-controls="collapseinnerTen">
						         (17) HSN Wise Summary of outward supplies
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerTen" class="collapse show" aria-labelledby="headinginnerTen" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable10 mt-2">
							<table id="dbTable_hsn_out1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">HSN Code</th>
										<th class="text-left">UQC</th>
										<th class="text-left">Total Quantity</th>
										<th class="text-left">Taxable Value</th>
										<th class="text-left">Rate of Tax</th>
										<th class="text-left">Central Tax</th>
										<th class="text-left">State Tax</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_hsnSc" name="table17.items[0].hsnSc" value="${invoice.table17.items[0].hsnSc}"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_uqc" name="table17.items[0].uqc" value="${invoice.table17.items[0].uqc}"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_qty" name="table17.items[0].qty" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].qty}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_txval" name="table17.items[0].txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_rt" name="table17.items[0].rt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].rt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_camt" name="table17.items[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_samt" name="table17.items[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_iamt" name="table17.items[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table17_items[0]_csamt" name="table17.items[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table17.items[0].csamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('1')">+</a></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						   <div class="card">
						    <div class="card-header" id="headinginnerEleven">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerEleven" aria-expanded="true" aria-controls="collapseinnerEleven">
						         (18)HSN Wise Summary of Inward supplies
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerEleven" class="collapse show" aria-labelledby="headinginnerEleven" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable11 mt-2">
							<table id="dbTable_hsn_out2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">HSN Code</th>
										<th class="text-left">UQC</th>
										<th class="text-left">Total Quantity</th>
										<th class="text-left">Taxable Value</th>
										<th class="text-left">Rate of Tax</th>
										<th class="text-left">Central Tax</th>
										<th class="text-left">State Tax</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_hsnSc" name="table18.items[0].hsnSc"   maxlength="15" value="${invoice.table18.items[0].hsnSc}"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_uqc" name="table18.items[0].uqc" value="${invoice.table17.items[0].uqc}" /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_qty" name="table18.items[0].qty" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].qty}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_txval" name="table18.items[0].txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_rt" name="table18.items[0].rt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].rt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_camt" name="table18.items[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_samt" name="table18.items[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_iamt" name="table18.items[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input" id="table18_items[0]_csamt" name="table18.items[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.table18.items[0].csamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="addmorewrap1" style="display:none;height: 30px;border-bottom: 1px solid #eaeaea;"><a href="javascript:void(0)" class="btn btn-blue-dark btn-sm pull-right addmore-other-row-1 mt-1" onClick="addmorerow('2')">+</a></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						   <div class="card">
						    <div class="card-header" id="headinginnerTwelve">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTwelve" aria-expanded="true" aria-controls="collapseinnerTwelve">
						         (19)Late fee payable and paid
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerTwelve" class="collapse show" aria-labelledby="headinginnerTwelve" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable12 mt-2">
							<table id="dbTable11" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Payable</th>
										<th class="text-left">Paid</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left form-group gst-3b-error">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left form-group gst-3b-error">State Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpsix-input"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						</div>
					</div>
					</div>
					</div>
				</div>
				<!-- <div class="verify_anual_wrap">
					<h6>Verification</h6>
					<p>I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed there from and in case of any reduction in output tax liability the benefit thereof has been/will be passed on to the recipient of supply. </p>
					<h6>Place:</h6>
					<h6>Signatory:</h6>
					<h6>Date:</h6>
					<h6>Status:</h6>
				</div> -->
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
					<!-- Tab panes 2-->
					<div class="tab-pane" id="gtab2" role="tabpanel">
					<div class="alert alert-success" role="alert">This Data is populated from GSTN Portal, Usually it will take 2 to 20 minutes of time to update on GSTN portal, when you don't see any data Click on Refresh in 10 to 20 minutes.</div>
						<!-- table start -->
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable4" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Taxable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable5" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Taxable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable6" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Itc Type</th>
										<th>Taxable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable7" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Desc</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable8" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable9" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Tax Payable</th>
										<th>Paid Through Cash</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable10" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Taxable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable14" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Taxable Amt</th>
										<th>Taxpaid</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable15" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
										<TH>intr</TH>
										<TH>fee</TH>
										<TH>pen</TH>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable16" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type</th>
										<th>Taxable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable17" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type>
										<th>Hsn/Service</th>
										<th>UQC</th>
										<th>Desc</th>
										<th>Isconcesstional</th>
										<th>Qty</th>
										<th>Rate</th>
										<th>Txable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<div class="customtable db-ca-view tabtable3">
							 <table id="dbFilingTable18" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Type>
										<th>Hsn/Service</th>
										<th>UQC</th>
										<th>Desc</th>
										<th>Isconcesstional</th>
										<th>Qty</th>
										<th>Rate</th>
										<th>Txable Amt</th>
										<th>Integrated Tax</th>
										<th>Central Tax</th>
										<th>State/UT Tax</th>
										<th>CESS</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<!-- table end -->
					</div>

					<!-- Tab panes 3-->
					<div class="tab-pane" id="gtab3"s role="tabpanel">
						<!-- table start -->
						<div class="customtable db-ca-view tabtable3">
							Currently all payments are accepting through GSTN (Govt.) Portal, please <a href="https://services.gst.gov.in/services/login" target="_blank">click here</a> to login, pay Tax and come back.
							 
						</div>
						<!-- table end -->
					</div>

					<!-- Tab panes 4-->
					<div class="tab-pane" id="gtab4" role="tabpanel">
					<form:form method="POST" id="sup4Form" data-toggle="validator" class="meterialform invoiceform" name="salesinvoceform" action="${contextPath}/saveoffliab/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
					<div class="col-md-12 col-sm-12">
						<div class="group upload-btn">
							<span class="pull-right">  <a href="#" class="btn btn-sm btn-blue-dark tpseven-edit" onClick="clickEdit('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');" style="margin-top:1px;padding:6px 10px!important">Edit</a>  <a href="#" class="btn btn-sm btn-blue-dark tpseven-save" style="display:none;margin-right: 3px;margin-top:1px;padding:6px 10px!important" onClick="clickSave('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');">Save</a><a href="#" class="btn btn-sm btn-blue-dark tpseven-cancel" style="display:none;margin-top:1px;padding:6px 10px!important" onClick="clickCancel('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input','',7);">Cancel</a>
							<a href="#" class='btn btn-greendark' onclick="invokeOffsetLiab();">Offset Liability</a></span>
						</div>
						<!-- table start -->
						<div class="customtable">
							<table class="display row-border dataTable meterialform" id="dbTable12" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Description</th>
										<th class="text-left">Tax Payable(<i class="fa fa-rupee"></i>)</th>
										<th colspan="4" class="text-center">Paid through ITC</th>
										<th class="text-left">Tax/Cess Paid in Cash(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Interest Paid in Cash(Total in <i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Late Fee Paid in Cash(<i class="fa fa-rupee"></i>)</th>
									</tr>
									<tr>
										<th class="text-left">&nbsp;</th>
										<th class="text-left">&nbsp;</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">&nbsp;</th>
										<th class="text-left">&nbsp;</th>
										<th class="text-left">&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td colspan="9"><span class="card-title">Other than reverse charge</a></td>
									</tr>
									<tr>
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].igst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdcgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdsgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].ipd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].igstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].cgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cgstPdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cgstPdcgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cgstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cgstLfeepd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].sgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.sgstPdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.sgstPdsgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].spd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].sgstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].sgstLfeepd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">CESS</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].cess.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cessPdcess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cspd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cessIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td colspan="9"><span class="card-title">Reverse Charge</a></td>
									</tr>
									<tr>
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].igst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].ipd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].cgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].cpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text"  readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].sgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].spd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">CESS</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].cess.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].cspd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
								</tbody>
							</table>
						</div>
						<!-- table end -->
					</div>
					<input type="hidden" name="id" value="<c:out value="${invoice.id}"/>">
					<input type="hidden" name="userid" value="<c:out value="${id}"/>">
					<input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">	
					<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
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

<div class="modal fade" id="helpGuideModal" tabindex="-1" role="dialog" aria-labelledby="helpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Help To File GSTR9</h3>
        </div>
        <div class="group upload-btn p-2 steptext-wrap">
<ul>
                <li><span class="steptext">Step 1:</span> <span class="steptext-desc">Enter <c:choose><c:when test="${returntype eq varPurchase}">GSTR2</c:when><c:when test="${returntype eq varGSTR3B}">GSTR3B</c:when><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR1}">GSTR1</c:when><c:when test="${returntype eq varGSTR4}">GSTR4</c:when><c:otherwise>${returntype}</c:otherwise></c:choose> Summary & Upload to GSTIN</span></li><li> 
<span class="steptext"> Step 2:</span> <span class="steptext-desc">Currently all payments are accepting through GSTN (Govt.) Portal</span>
</li>
<li><span class="steptext"> Step 3:</span> <span class="steptext-desc">Enter & Save Offset Liability Details. Click on Offset Liability</span></li>
<li><span class="steptext">Step 4:</span> <span class="steptext-desc">Click on "File <c:choose><c:when test="${returntype eq varPurchase}">GSTR2</c:when><c:when test="${returntype eq varGSTR3B}">GSTR3B</c:when><c:when test="${returntype eq varGSTR2}">GSTR2</c:when><c:when test="${returntype eq varGSTR1}">GSTR1</c:when><c:when test="${returntype eq varGSTR4}">GSTR4</c:when><c:otherwise>${returntype}</c:otherwise></c:choose> with Digital Signature (DSC)", Please login to your Digital Signature to file.</span>
</li>                
 </ul> 	
 <p style="text-align:center">For more details please click <a href="https://www.mastergst.com/user-guide/how-to-add-gstr3b-invoice.html" target="_blank">here</a></p>			
         </div>
      </div>
      <div class="modal-footer">
   
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<!-- modal 3.1--->
<div class="modal fade" id="helpguideModal_1" tabindex="-1" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3> 3.1. 3B Monthly Summary <span class="smalltxt">Help Guide</span></h3>
        </div>
        <div class="group upload-btn p-4 helpguide-wrap">
        
<h4><strong>3.1(a)</strong> Outward supplies other than zero rated, nil rated and exempted</h4>

<p>Include the taxable value of all inter-State and intra-State B2B as well as B2C supplies made during the tax period. Reporting should be net off debit/credit notes and amendments of amounts pertaining to earlier tax periods, if any.</p>

<p><strong>Value of Taxable Supplies =</strong> (Value of invoices) + (Value of Debit Notes) - (Value of Credit Notes) + (Value of advances received for which invoices have not been issued in the same Month) - (Value of advances adjusted against invoices).</p>

<p><strong>Integrated Tax, Central Tax, State/UT Tax and Cess:</strong> Only Tax amount should be entered against respective head. Please ensure you declare a tax amount IGST and/or CGST and SGST along with Cess applicable, if any.</p>
<h4><strong>3.1(b)</strong> Outward taxable supplies (zero rated)</h4>

<p>Mention Export Supplies made including supplies to SEZ/SEZ developers. Total taxable value should include supplies on which tax has been charged as well as supplies made against bond or letter of undertaking.</p>

<p>Integrated Tax and Cess should include amount of tax, if paid, on the supplies made.</p>

<h4><strong>3.1(c)</strong> Other outward supplies (Nil rated, exempted)</h4>

<p>Here include all outward supplies which are not liable to tax either because they are nil rated or exempt through notification. It should not include export supplies or supplies made to SEZ developers or units declared in 3.1(b) above.</p>

<h4><strong>3.1(d)</strong> Inward supplies (liable to reverse charge)</h4>

<p>Include inward supplies which are subject to reverse charge mechanism. This also includes supplies received from unregistered persons on which tax is liable to be paid by recipient.</p>

<h4><strong>3.1(e)</strong> Non-GST Outward Supplies</h4>

<p>Amount in Total taxable value should include aggregate of value of all the supplies which are not chargeable under GST Act e.g. petroleum products.        </p>
        		
         </div>
      </div>
      <div class="modal-footer mb-2">
   
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<!-- modal 3.2--->
<div class="modal fade" id="helpguideModal_2" tabindex="-1" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3> 3.2. Inter-state supplies<span class="smalltxt">Help Guide</span> </h3>
        </div>
        <div class="group upload-btn p-4 helpguide-wrap">
        
<p> Out of supplies shown in earlier Table (3.1), declare the details of inter-State supplies made to unregistered persons, composition taxable persons and UIN holders in the respective sub-sections along with the place of supply.</p>

<p>The details mentioned in this Table will not be considered in computation of output liability.</p>

<p>Please ensure the details of inter-State sales declared here is part of the declaration in Table 3.1 above and it doesn't exceed the amount declared over there.</p>
         </div>
      </div>
      <div class="modal-footer mb-2">
   
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<!-- modal 4--->
<div class="modal fade" id="helpguideModal_3" tabindex="-1" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>3.3. Eligible ITC<span class="smalltxt">Help Guide</span> </h3>
        </div>
        <div class="group upload-btn p-4 helpguide-wrap">
    <h4>(A) ITC Available (whether in full or part)</h4>

<p>Declare here the details of credit being claimed on inward supplies made during the tax period, further categorized into:</p>

<ol>
<li>Import of goods</li>
<li>Import of Services (Should have been declared in 3.1 for tax liability under reverse charge mechanism)</li>
<li>Inward supplies on which tax is payable on reverse charge basis (Should have been declared in 3.1 for tax liability on supplies attracting reverse charge)
Credit received from ISD (Input Service Distributor)</li>
<li>Any other credit. (This will cover all inward supplies from registered taxpayers on which tax has been charged. Transition relation credits should not be mentioned here. Transition credit should be claimed through GST TRAN-1)</li>

</ol><h4>B) ITC Reversed</h4>
<p>Any reversal of ITC claimed as per applicable rules will be declared in this section and the same will be reduced from the credit as per (A) above.</p>

<h4>C) Net ITC Available (A-B)</h4>

<p>This section will be auto calculated by the system considering the values provided in A&B (ITC available & ITC reversed)</p>

<h4>D) Ineligible ITC</h4>
<p>ITC which is not eligible needs to be declared here. Please ensure it is not availed or reversed in A & B above.</p>
         </div>
      </div>
      <div class="modal-footer mb-2">
   
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


<!-- modal 5--->
<div class="modal fade" id="helpguideModal_4" tabindex="-1" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3> 3.4. Exempt, nil and Non GST inward suplies<span class="smalltxt">Help Guide</span></h3>
        </div>
        <div class="group upload-btn p-4 helpguide-wrap">
        
<h4>Declare the values of inward supplies with respect to the following, in this section:</h4>

<ol>
<li>From suppliers under composition scheme, Supplies exempt from tax and Nil rated supplies.</li>
<li>Supplies which are not covered under GST Act.</li>
</ol>
The above values have to be declared separately for Intra-State and Inter-State supplies.
         </div>
      </div>
      <div class="modal-footer mb-2">
   
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


<!-- modal 6--->
<div class="modal fade" id="helpguideModal_5" tabindex="-1" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>3.5. Interest and Late fee <span class="smalltxt">Help Guide</span> </h3>
        </div>
        <div class="group upload-btn p-4 helpguide-wrap">
 
<p> Interest is payable on the delayed payment of taxes after the last date as well as for invoices/ debit notes declared in current tax period belonging to earlier tax period.</p>

<p>The self-calculated interest liability needs to be declared by the taxpayer in this field.</p>

<p>Interest for both reverse charge as well as for forward charge related liabilities needs to be declared here. </p>

<p>Late fee is auto calculated by the system based on the date of filing and the due date for the return. There is no late fees payable for IGST and CESS and hence the same has been disabled.</p>

 

  
  </div>
      </div>
      <div class="modal-footer mb-2">
   
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
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
	<!-- downloadOtpModal End -->

<!-- Submit Invoice Modal -->
<div class="modal fade" id="submitInvModal" tabindex="-1" role="dialog" aria-labelledby="submitInvModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Submit</h3>
        </div>
        <div class="group upload-btn pl-4 pt-4 pr-4">
          <h6>Once you click CONFIRM & SUBMIT, your GSTR-3B will be submitted and respective liabilities/input credits will be reflected in the respective ledgers. You will NOT be able to make any further modifications.</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once invoices are submitted, it cannot be modified.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnSubmitInv" data-dismiss="modal">Submit</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="fileReturnModal" tabindex="-1" role="dialog" aria-labelledby="fileReturnModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Pre-requisites for DSC Filing</h3>
        </div>
        <div class="group upload-btn p-2 steptext-wrap">
<ul>
                <li><span class="steptext-desc">1) Install Digital Signature Software, <a href="https://files.truecopy.in/downloads/lh/latest/DSCSignerLH.msi">Click Here</a> to download & install</span></li>
				<li><span class="steptext-desc">2) Make sure Digital Signature software is running in your system</span></li>
				<li><span class="steptext-desc">3) Make Sure ePass Application is Running in your System</span></li>
				<li><span class="steptext-desc">3) Login to ePass Application</span></li>
 </ul>
         <ul><li>==================================================</li></ul>
		<ul>
		<li><span class="steptext">Step 1:</span> <span class="steptext-desc"> Certificate verification</span></li><li> 
<span class="steptext"> Step 2:</span> <span class="steptext-desc"> Sign the invoices</span></li>
<li><span class="steptext">Step 3:</span> <span class="steptext-desc"> Filing of invoices</span>
</li>
 </ul>
         </div>

      </div>
      <div class="modal-footer">
		<button type="button" class="btn btn-secondary" onClick="trueCopyFiling()" data-dismiss="modal">File Now</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

	<form id="certlistForm" name="certlistForm" method="POST" enctype="multipart/form-data">
	</form>
	<form id="certsignForm" name="certsignForm" method="POST" enctype="multipart/form-data">
	</form>

	<!-- evcOtpModal Start -->
	<div class="modal fade" id="evcOtpModal" role="dialog" aria-labelledby="evcOtpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="evcOtpModalClose" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Submit EVC OTP</h3>
					</div>
					<div class="group upload-btn p-4" style="min-height:600px;">
						<div class="formboxwrap">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3>
							<h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto">
								<div class="formbox otpbox">
									<form class="meterialform" id="evcOtpEntryForm" data-toggle="validator">
										<div class="whitebg">
											<h2> Verify EVC OTP</h2>
											<h6>OTP has been sent to your GSTN registered mobile number & e-mail, Please enter the same below
											</h6>
											<!-- serverside error begin -->                    
											<div class="errormsg"> </div>
											<!-- serverside error end --> 
											<div class="col-sm-12">
												<div class="group upload-btn">
													<div class="errormsg"></div>
													<div class="group upload-btn"></div>
													<input type="text" class="evcotp" id="evcotp1" required="required"  data-minlength="4" maxlength="6" pattern="[a-zA-Z0-9]+" data-error="Please enter valid otp number" tabindex="1" placeholder="0" />
													<div class="help-block with-errors"></div>
												</div>
												<h6>Didn't receive OTP? <a href="">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center">
											<p><a href="#" onClick="fileEVC()" class="btn btn-lg btn-blue btn-verify">Submit</a></p>
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
	<!-- evcOtpModal End -->
	
<script type="text/javascript">
$('#filing_option').html('Yearly');
$('.retutprdntxt').text('Financial Year:');
$('#datetimepicker').val('03-2019');

$("#gstr9FinancialYear").val('${year}');
var today = new Date();
var startDate = new Date(today.getFullYear(), 2);
var endDate = new Date(today.getFullYear(), 2);

$("#datetimepicker").datepicker({
	viewMode: 1,
	minViewMode: 1,
	yearRange:'2018:2020',
	format: 'mm-yyyy',
   startDate: startDate,
   endDate: endDate
   
});

var k=1; var l=1
function addmorerow(a){
	var m=k-1;
	var tab = a;
	if(a == 1){
		tab = 17;
	}else if(a == 2){
		tab = 18;
	}
	if(k == 1){
		$('#dbTable_hsn_out'+a).append('<tr id="addrow_'+a+''+k+'"><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].hsnSc" name="table'+tab+'.items['+k+'].hsnSc"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].uqc" name="table'+tab+'.items['+k+'].uqc"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].qty" name="table'+tab+'.items['+k+'].qty"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].txval" name="table'+tab+'.items['+k+'].txval"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].rt" name="table'+tab+'.items['+k+'].rt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].camt" name="table'+tab+'.items['+k+'].camt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].samt" name="table'+tab+'.items['+k+'].samt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].iamt" name="table'+tab+'.items['+k+'].iamt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].csamt" name="table'+tab+'.items['+k+'].csamt"></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>');
	k++;
	}else{
		$('#dbTable_hsn_out'+a).append('<tr id="addrow_'+a+''+k+'"><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].hsnSc" name="table'+tab+'.items['+k+'].hsnSc"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].uqc" name="table'+tab+'.items['+k+'].uqc"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].qty" name="table'+tab+'.items['+k+'].qty"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].txval" name="table'+tab+'.items['+k+'].txval"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].rt" name="table'+tab+'.items['+k+'].rt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].camt" name="table'+tab+'.items['+k+'].camt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].samt" name="table'+tab+'.items['+k+'].samt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].iamt" name="table'+tab+'.items['+k+'].iamt"></td><td><input type="text" class="form-control" id="table'+tab+'.items['+k+'].csamt" name="table'+tab+'.items['+k+'].csamt"></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>');
		k++;
	}
	}
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;
		var year = eDate.getFullYear();
		window.location.href = '${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year;
	}
	var i=1;
	$('.addmore-other-row').click(function(){
		var j=i-1;
		if(i == 1){
		$('<tr id="addrow_'+i+'"><td>(H'+i+')Other reversals (pl. specify)</td><td><input type="text" class="form-control" id="table7.other['+i+'].desc" name= "table7.other['+i+'].desc"></td><td><input type="text" class="form-control" id="table7.other['+i+'].camt" name= "table7.other['+i+'].camt" data-variavel="tab7H'+i+'field1"></td><td><input type="text" class="form-control" id="table7.other['+i+'].samt" name= "table7.other['+i+'].samt" data-variavel="tab7H'+i+'field2"></td><td><input type="text" class="form-control" id="table7.other['+i+'].iamt" name= "table7.other['+i+'].iamt" data-variavel="tab7H'+i+'field3"></td><td><input type="text" class="form-control" id="table7.other['+i+'].csamt" name= "table7.other['+i+'].csamt" data-variavel="tab7H'+i+'field4"></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>').insertAfter($(this).closest('tr'));
		var totalitcrev1 =  $('#totalitcrev1').attr('data-formula');
		var netitcutil1 =  $('#netitcutil1').attr('data-formula');
		totalitcrev1 = totalitcrev1 + '+#tab7H'+i+'field1#';
		$('#totalitcrev1').attr('data-formula',totalitcrev1);
		netitcutil1 = netitcutil1 + '- #tab7H'+i+'field1#';
		$('#netitcutil1').attr('data-formula',netitcutil1);
		
		var totalitcrev2 =  $('#totalitcrev2').attr('data-formula');
		var netitcutil2 =  $('#netitcutil2').attr('data-formula');
		totalitcrev2 = totalitcrev2 + '+#tab7H'+i+'field2#';
		$('#totalitcrev2').attr('data-formula',totalitcrev2);
		netitcutil2 = netitcutil2 + '- #tab7H'+i+'field2#';
		$('#netitcutil2').attr('data-formula',netitcutil2);
		
		var totalitcrev3 =  $('#totalitcrev3').attr('data-formula');
		var netitcutil3 =  $('#netitcutil3').attr('data-formula');
		totalitcrev3 = totalitcrev3 + '+#tab7H'+i+'field3#';
		$('#totalitcrev3').attr('data-formula',totalitcrev3);
		netitcutil3 = netitcutil3 + '- #tab7H'+i+'field3#';
		$('#netitcutil3').attr('data-formula',netitcutil3);
		
		var totalitcrev4 =  $('#totalitcrev4').attr('data-formula');
		var netitcutil4 =  $('#netitcutil4').attr('data-formula');
		totalitcrev4 = totalitcrev4 + '+#tab7H'+i+'field4#';
		$('#totalitcrev4').attr('data-formula',totalitcrev4);
		netitcutil4 = netitcutil4 + '- #tab7H'+i+'field4#';
		$('#netitcutil4').attr('data-formula',netitcutil4);
		i++;
		}else{
			$('<tr id="addrow_'+i+'"><td>(H'+i+')Other reversals (pl. specify)</td><td><input type="text" class="form-control" id="table7.other['+i+'].desc" name= "table7.other['+i+'].desc"></td><td><input type="text" class="form-control" id="table7.other['+i+'].camt" name= "table7.other['+i+'].camt" data-variavel="tab7H'+i+'field1"></td><td><input type="text" class="form-control" id="table7.other['+i+'].samt" name= "table7.other['+i+'].samt" data-variavel="tab7H'+i+'field2"></td><td><input type="text" class="form-control" id="table7.other['+i+'].iamt" name= "table7.other['+i+'].iamt" data-variavel="tab7H'+i+'field3"></td><td><input type="text" class="form-control" id="table7.other['+i+'].csamt" name= "table7.other['+i+'].csamt" data-variavel="tab7H'+i+'field4"></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>').insertAfter($('#addrow_'+j).closest('tr'));
			var totalitcrev1 =  $('#totalitcrev1').attr('data-formula');
			var netitcutil1 =  $('#netitcutil1').attr('data-formula');
			totalitcrev1 = totalitcrev1 + '+#tab7H'+i+'field1#';
			$('#totalitcrev1').attr('data-formula',totalitcrev1);
			netitcutil1 = netitcutil1 + '- #tab7H'+i+'field1#';
			$('#netitcutil1').attr('data-formula',netitcutil1);
			
			var totalitcrev2 =  $('#totalitcrev2').attr('data-formula');
			var netitcutil2 =  $('#netitcutil2').attr('data-formula');
			totalitcrev2 = totalitcrev2 + '+#tab7H'+i+'field2#';
			$('#totalitcrev2').attr('data-formula',totalitcrev2);
			netitcutil2 = netitcutil2 + '- #tab7H'+i+'field2#';
			$('#netitcutil2').attr('data-formula',netitcutil2);
			
			var totalitcrev3 =  $('#totalitcrev3').attr('data-formula');
			var netitcutil3 =  $('#netitcutil3').attr('data-formula');
			totalitcrev3 = totalitcrev3 + '+#tab7H'+i+'field3#';
			$('#totalitcrev3').attr('data-formula',totalitcrev3);
			netitcutil3 = netitcutil3 + '- #tab7H'+i+'field3#';
			$('#netitcutil3').attr('data-formula',netitcutil3);
			
			var totalitcrev4 =  $('#totalitcrev4').attr('data-formula');
			var netitcutil4 =  $('#netitcutil4').attr('data-formula');
			totalitcrev4 = totalitcrev4 + '+#tab7H'+i+'field4#';
			$('#totalitcrev4').attr('data-formula',totalitcrev4);
			netitcutil4 = netitcutil4 + '- #tab7H'+i+'field4#';
			$('#netitcutil4').attr('data-formula',netitcutil4);
			i++;
		}
		dataFormula();
		});
	function deleteDocItem(item) {
		i--;
		k--;
		var delid = $(item).parent().parent().attr('id');
		var did = delid.split('_');
		var totalitcrev1 =  $('#totalitcrev1').attr('data-formula');
		var netitcutil1 =  $('#netitcutil1').attr('data-formula');
		totalitcrev1 = totalitcrev1.replace('+#tab7H'+did[1]+'field1#','');
		$('#totalitcrev1').attr('data-formula',totalitcrev1);
		netitcutil1 = netitcutil1.replace('- #tab7H'+did[1]+'field1#','');
		$('#netitcutil1').attr('data-formula',netitcutil1);
		
		var totalitcrev2 =  $('#totalitcrev2').attr('data-formula');
		var netitcutil2 =  $('#netitcutil2').attr('data-formula');
		totalitcrev2 = totalitcrev2.replace('+#tab7H'+did[1]+'field2#','');
		$('#totalitcrev2').attr('data-formula',totalitcrev2);
		netitcutil2 = netitcutil2.replace('- #tab7H'+did[1]+'field2#','');
		$('#netitcutil2').attr('data-formula',netitcutil2);
		
		var totalitcrev3 =  $('#totalitcrev3').attr('data-formula');
		var netitcutil3 =  $('#netitcutil3').attr('data-formula');
		totalitcrev3 = totalitcrev3.replace('+#tab7H'+did[1]+'field3#','');
		$('#totalitcrev3').attr('data-formula',totalitcrev3);
		netitcutil3 = netitcutil3.replace('- #tab7H'+did[1]+'field3#','');
		$('#netitcutil3').attr('data-formula',netitcutil3);
		
		var totalitcrev4 =  $('#totalitcrev4').attr('data-formula');
		var netitcutil4 =  $('#netitcutil4').attr('data-formula');
		totalitcrev4 = totalitcrev4.replace('+#tab7H'+did[1]+'field4#','');
		$('#totalitcrev4').attr('data-formula',totalitcrev4);
		netitcutil4 = netitcutil4.replace('- #tab7H'+did[1]+'field4#','');
		$('#netitcutil4').attr('data-formula',netitcutil4);
		
		
		$(item).parent().parent().remove();
		dataFormula();
	}
	function updateInvoiceStatus(){
		$.ajax({
			url : _getContextPath()+'/otpexpiry/'+clientId,
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				if(response == "OTP_VERIFIED"){
					window.location.href = _getContextPath()+'/updateInvStatus'+commonSuffix+'/'+clientId+'/GSTR1/'+month+'/'+year;
				}else{
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
				}
			}
		});
	}
	function uploadInvoice(btn) {
		otpExpiryCheck();
		if(otpExpirycheck == "OTP_VERIFIED"){
		$(btn).addClass('btn-loader');
		var invArray = new Array();
		var pUrl = "${contextPath}/ihubsavestatus/${id}/${usertype}/${client.id}/${returntype}?month=" + month + "&year=" + year+ "&hsn=hsn";
		$.ajax({
			type: "POST",
			url: pUrl,
			async: false,
			cache: false,
			dataType:"json",
			data:JSON.stringify(invArray),
			contentType: 'application/json',
			success : function(response) {
				$(btn).removeClass('btn-loader');
				if(response.data && response.data.error_report && response.data.error_report.error_msg) {
					errorNotification(response.data.error_report.error_msg);
				} else if(response.status_cd == '1') {
					successNotification('Upload GSTR9 completed successfully!');
				} else {
					if(response.error && response.error.message) {
						errorNotification(response.error.message);
					} else if(response.status_cd == '0') {
						if(response.status_desc == 'OTP verification is not yet completed!' 
							|| response.status_desc == 'Invalid Session'
							|| response.status_desc == 'Unauthorized User!' || response.status_desc == 'Missing Mandatory Params' || response.status_desc == 'API Authorization Failed') {
							errorNotification('Your OTP Session Expired, Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
						} else  if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
							errorNotification('Your subscription has expired. Kindly <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
						} else {
							errorNotification(response.status_desc);
						}
					}
				}
			},
			error : function(e, status, error) {
				$(btn).removeClass('btn-loader');
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
		}else{
			errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}
	$('#gstr9FinancialYear').change(function() {
		var gstr9FinancialYear = $(this).val(); 
		window.location.href = '${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/03/'+gstr9FinancialYear;
	});
	function fetchRetSummary(type,clientid,year){
		otpExpiryCheck();
		if(otpExpirycheck == "OTP_VERIFIED"){
		successNotification('Loading Summary information. Please wait..!');
		$.ajax({
			url: contextPath+"/getgstr9data/"+clientid+"/"+year,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				closeNotifications();
				if(response.error && response.error.message) {
					errorNotification(response.error.message);
				} else if(response.status_cd == '0') {
					if(response.status_desc == 'OTP verification is not yet completed!' 
						|| response.status_desc == 'Invalid Session'
						|| response.status_desc == 'Unauthorized User!' || response.status_desc == 'Missing Mandatory Params' || response.status_desc == 'API Authorization Failed') {
						errorNotification(response.status_desc+'. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
					} else {
						errorNotification(response.status_desc);
					}
				} else if(response.data){
				if(response.data.table4){
					dbFilingTable4.clear().draw();
					if(response.data.table4.b2c){
						dbFilingTable4.row.add(['(A)Supplies made to un-registered persons (B2C)', '<span class="ind_formats">'+response.data.table4.b2c.txval+'</span>', '<span class="ind_formats">'+response.data.table4.b2c.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.b2c.camt+'</span>','<span class="ind_formats">'+response.data.table4.b2c.samt+'</span>','<span class="ind_formats">'+response.data.table4.b2c.csamt+'</span>']);
					}
					if(response.data.table4.b2b){
						dbFilingTable4.row.add(['(B)Supplies made to registered persons (B2B)', '<span class="ind_formats">'+response.data.table4.b2b.txval+'</span>', '<span class="ind_formats">'+response.data.table4.b2b.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.b2b.camt+'</span>','<span class="ind_formats">'+response.data.table4.b2b.samt+'</span>','<span class="ind_formats">'+response.data.table4.b2b.csamt+'</span>']);
					}
					if(response.data.table4.sez){
						dbFilingTable4.row.add(['(D)Supply to SEZs on payment of tax', '<span class="ind_formats">'+response.data.table4.sez.txval+'</span>', '<span class="ind_formats">'+response.data.table4.sez.iamt+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="ind_formats">'+response.data.table4.sez.csamt+'</span>']);
					}
					if(response.data.table4.exp){
						dbFilingTable4.row.add(['(C)Zero rated supply (Export) on payment of tax (except supplies to SEZs)', '<span class="ind_formats">'+response.data.table4.exp.txval+'</span>', '<span class="ind_formats">'+response.data.table4.exp.iamt+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="ind_formats">'+response.data.table4.exp.csamt+'</span>']);
					}
					if(response.data.table4.deemed ){
						dbFilingTable4.row.add(['(E)Deemed Exports', '<span class="ind_formats">'+response.data.table4.deemed.txval+'</span>', '<span class="ind_formats">'+response.data.table4.deemed.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.deemed.camt+'</span>','<span class="ind_formats">'+response.data.table4.deemed.samt+'</span>','<span class="ind_formats">'+response.data.table4.deemed.csamt+'</span>']);
					}
					if(response.data.table4.at){
						dbFilingTable4.row.add(['(F)Advances on which tax has been paid but invoice has not been issued (not covered under (A) to (E) above)', '<span class="ind_formats">'+response.data.table4.at.txval+'</span>', '<span class="ind_formats">'+response.data.table4.at.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.at.camt+'</span>','<span class="ind_formats">'+response.data.table4.at.samt+'</span>','<span class="ind_formats">'+response.data.table4.at.csamt+'</span>']);
					}
					if(response.data.table4.rchrg){
						dbFilingTable4.row.add(['(G)Inward supplies on which tax is to be paid on reverse charge basis', '<span class="ind_formats">'+response.data.table4.rchrg.txval+'</span>', '<span class="ind_formats">'+response.data.table4.rchrg.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.rchrg.camt+'</span>','<span class="ind_formats">'+response.data.table4.rchrg.samt+'</span>','<span class="ind_formats">'+response.data.table4.rchrg.csamt+'</span>']);
					}
					if(response.data.table4.amd_pos){
						dbFilingTable4.row.add(['(K)Supplies / tax declared through Amendments (+)', '<span class="ind_formats">'+response.data.table4.amd_pos.txval+'</span>', '<span class="ind_formats">'+response.data.table4.amd_pos.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.amd_pos.camt+'</span>','<span class="ind_formats">'+response.data.table4.amd_pos.samt+'</span>','<span class="ind_formats">'+response.data.table4.amd_pos.csamt+'</span>']);
					}
					if(response.data.table4.amd_neg){
						dbFilingTable4.row.add(['(L)Supplies / tax reduced through Amendments (-)', '<span class="ind_formats">'+response.data.table4.amd_neg.txval+'</span>', '<span class="ind_formats">'+response.data.table4.amd_neg.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.amd_neg.camt+'</span>','<span class="ind_formats">'+response.data.table4.amd_neg.samt+'</span>','<span class="ind_formats">'+response.data.table4.amd_neg.csamt+'</span>']);
					}
					if(response.data.table4.cr_nt){
						dbFilingTable4.row.add(['(I)Credit Notes issued in respect of transactions specified in (B) to (E) above (-)', '<span class="ind_formats">'+response.data.table4.cr_nt.txval+'</span>','<span class="ind_formats">'+response.data.table4.cr_nt.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.cr_nt.camt+'</span>','<span class="ind_formats">'+response.data.table4.cr_nt.samt+'</span>','<span class="ind_formats">'+response.data.table4.cr_nt.csamt+'</span>']);
					}
					if(response.data.table4.dr_nt){
						dbFilingTable4.row.add(['(J)Debit Notes issued in respect of transactions specified in (B) to (E) above (+)', '<span class="ind_formats">'+response.data.table4.dr_nt.txval+'</span>', '<span class="ind_formats">'+response.data.table4.dr_nt.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.dr_nt.camt+'</span>','<span class="ind_formats">'+response.data.table4.dr_nt.samt+'</span>','<span class="ind_formats">'+response.data.table4.dr_nt.csamt+'</span>']);
					}
					if(response.data.table4.sub_totalAG){
						dbFilingTable4.row.add(['(H) Sub-total (A to G above)', '<span class="ind_formats">'+response.data.table4.sub_totalAG.txval+'</span>', '<span class="ind_formats">'+response.data.table4.sub_totalAG.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.sub_totalAG.camt+'</span>','<span class="ind_formats">'+response.data.table4.sub_totalAG.samt+'</span>','<span class="ind_formats">'+response.data.table4.sub_totalAG.csamt+'</span>']);
					}
					if(response.data.table4.sup_adv){
						dbFilingTable4.row.add(['(N)Supplies and advances on which tax is to be paid (H + M) above', '<span class="ind_formats">'+response.data.table4.sup_adv.txval+'</span>', '<span class="ind_formats">'+response.data.table4.sup_adv.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.sup_adv.camt+'</span>','<span class="ind_formats">'+response.data.table4.sup_adv.samt+'</span>','<span class="ind_formats">'+response.data.table4.sup_adv.csamt+'</span>']);
					}
					if(response.data.table4.sub_totalIL){
						dbFilingTable4.row.add(['(M)Sub-total (I to L above)', '<span class="ind_formats">'+response.data.table4.sub_totalIL.txval+'</span>', '<span class="ind_formats">'+response.data.table4.sub_totalIL.iamt+'</span>', '<span class="ind_formats">'+response.data.table4.sub_totalIL.camt+'</span>','<span class="ind_formats">'+response.data.table4.sub_totalIL.samt+'</span>','<span class="ind_formats">'+response.data.table4.sub_totalIL.csamt+'</span>']);
					}
					dbFilingTable4.draw();
					$(".ind_formats").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats'});
				}
				if(response.data.table5){
					dbFilingTable5.clear().draw();
					if(response.data.table5.zero_rtd){
						dbFilingTable5.row.add(['(A)Zero rated supply (Export) without payment of tax', '<span class="ind_formats5">'+response.data.table5.zero_rtd.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.sez){
						dbFilingTable5.row.add(['(B)Supply to SEZs without payment of tax', '<span class="ind_formats5">'+response.data.table5.sez.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.rchrg){
						dbFilingTable5.row.add(['(C)Supplies on which tax is to be paid by the recipient on reverse charge basis', '<span class="ind_formats5">'+response.data.table5.rchrg.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.exmt){
						dbFilingTable5.row.add(['(D)Exempted', '<span class="ind_formats5">'+response.data.table5.exmt.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.nil){
						dbFilingTable5.row.add(['(E)Nil Rated', '<span class="ind_formats5">'+response.data.table5.nil.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.non_gst){
						dbFilingTable5.row.add(['(F)Non-GST supply', '<span class="ind_formats5">'+response.data.table5.non_gst.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.cr_nt){
						dbFilingTable5.row.add(['(H)Credit Notes issued in respect of transactions specified in A to F above (-)', '<span class="ind_formats5">'+response.data.table5.cr_nt.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.dr_nt){
						dbFilingTable5.row.add(['(I)Debit Notes issued in respect of transactions specified in A to F above (+)', '<span class="ind_formats5">'+response.data.table5.dr_nt.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.amd_pos){
						dbFilingTable5.row.add(['(J)Supplies declared through Amendments (+)', '<span class="ind_formats5">'+response.data.table5.amd_pos.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.amd_neg){
						dbFilingTable5.row.add(['(K)Supplies reduced through Amendments (-)', '<span class="ind_formats5">'+response.data.table5.amd_neg.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.sub_totalAF){
						dbFilingTable5.row.add(['G)Sub-total (A to F above)', '<span class="ind_formats5">'+response.data.table5.sub_totalAF.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.sub_totalHK){
						dbFilingTable5.row.add(['L)Sub-Total (H to K above)', '<span class="ind_formats5">'+response.data.table5.sub_totalHK.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.tover_tax_np){
						dbFilingTable5.row.add(['(M)Turnover on which tax is not to be paid (G + L above)', '<span class="ind_formats5">'+response.data.table5.tover_tax_np.txval+'</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table5.total_tover){
						dbFilingTable5.row.add(['(N)Total Turnover (including advances)(4N + 5M - 4G above)', '<span class="ind_formats5">'+response.data.table5.total_tover.txval+'</span>', '<span class="ind_formats5">'+response.data.table5.total_tover.iamt+'</span>', '<span class="ind_formats5">'+response.data.table5.total_tover.camt+'</span>','<span class="ind_formats5">'+response.data.table5.total_tover.samt+'</span>','<span class="ind_formats5">'+response.data.table5.total_tover.csamt+'</span>']);
					}
					dbFilingTable5.draw();
					$(".ind_formatss").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats5'});
				}
				if(response.data.table6){
					dbFilingTable6.clear().draw();
					if(response.data.table6.supp_non_rchrg){
						$.each(response.data.table6.supp_non_rchrg, function(key) {
							dbFilingTable6.row.add(['(B)Inward supplies (other than imports and inward supplies liable to reverse charge but includes services received from SEZs)', '<span class="">'+response.data.table6.supp_non_rchrg[key].itc_typ+'</span>', '<span class="">-</span>',  '<span class="ind_formats6">'+response.data.table6.supp_non_rchrg[key].iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.supp_non_rchrg[key].camt+'</span>','<span class="ind_formats6">'+response.data.table6.supp_non_rchrg[key].samt+'</span>','<span class="ind_formats6">'+response.data.table6.supp_non_rchrg[key].csamt+'</span>']);						 
						});
					}
					if(response.data.table6.supp_rchrg_reg){
						$.each(response.data.table6.supp_rchrg_reg, function(key) {
							dbFilingTable6.row.add(['(D)Inward supplies received from registered persons liable to reverse charge (other than B above) on which tax is paid and ITC availed', '<span class="">'+response.data.table6.supp_rchrg_reg[key].itc_typ+'</span>', '<span class="">-</span>',  '<span class="ind_formats6">'+response.data.table6.supp_rchrg_reg[key].iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.supp_rchrg_reg[key].camt+'</span>','<span class="ind_formats6">'+response.data.table6.supp_rchrg_reg[key].samt+'</span>','<span class="ind_formats6">'+response.data.table6.supp_rchrg_reg[key].csamt+'</span>']);						 
						});
					}
					if(response.data.table6.ios){
						dbFilingTable6.row.add(['(F)Import of services (excluding inward supplies from SEZs)','<span class="">-</span>', '<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.ios.iamt+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="ind_formats6">'+response.data.table6.ios.csamt+'</span>']);						 
					}
					if(response.data.table6.isd){
						dbFilingTable6.row.add(['(G)Input Tax credit received from ISD','<span class="">-</span>', '<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.isd.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.isd.camt+'</span>','<span class="ind_formats6">'+response.data.table6.isd.samt+'</span>','<span class="ind_formats6">'+response.data.table6.isd.csamt+'</span>']);						 
					}
					if(response.data.table6.itc_clmd){
						dbFilingTable6.row.add(['(H)Amount of ITC reclaimed (other than B above) under the provisions of the Act','<span class="">-</span>', '<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.itc_clmd.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.itc_clmd.camt+'</span>','<span class="ind_formats6">'+response.data.table6.itc_clmd.samt+'</span>','<span class="ind_formats6">'+response.data.table6.itc_clmd.csamt+'</span>']);						 
					}
					if(response.data.table6.other){
						dbFilingTable6.row.add(['(M)Any other ITC availed but not specified above','<span class="">-</span>', '<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.other.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.other.camt+'</span>','<span class="ind_formats6">'+response.data.table6.other.samt+'</span>','<span class="ind_formats6">'+response.data.table6.other.csamt+'</span>']);						 
					}
					if(response.data.table6.tran1){
						dbFilingTable6.row.add(['(K)Transition Credit through TRAN-I (including revisions if any)','<span class="">-</span>', '<span class="">-</span>', '<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.tran1.camt+'</span>','<span class="ind_formats6">'+response.data.table6.tran1.samt+'</span>','<span class="">-</span>']);						 
					}
					if(response.data.table6.tran2){
						dbFilingTable6.row.add(['(L)Transition Credit through TRAN-II','<span class="">-</span>', '<span class="">-</span>', '<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.tran2.camt+'</span>','<span class="ind_formats6">'+response.data.table6.tran2.samt+'</span>','<span class="">-</span>']);						 
					}
					if(response.data.table6.iog){
						$.each( response.data.table6.iog, function(key) {
							dbFilingTable6.row.add(['(E)Import of goods (including supplies from SEZs)', '<span class="">'+response.data.table6.iog[key].itc_typ+'</span>', '<span class="">-</span>',  '<span class="ind_formats6">'+response.data.table6.iog[key].iamt+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="ind_formats6">'+response.data.table6.iog[key].csamt+'</span>']);						 
						});
					}
					if(response.data.table6.supp_rchrg_unreg){
						$.each(response.data.table6.supp_non_rchrg, function(key) {
							dbFilingTable6.row.add(['(C)Inward supplies received from unregistered persons liable to reverse charge (other than B above) on which tax is paid & ITC availed', '<span class="">'+response.data.table6.supp_rchrg_unreg[key].itc_typ+'</span>', '<span class="">-</span>',  '<span class="ind_formats6">'+response.data.table6.supp_rchrg_unreg[key].iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.supp_rchrg_unreg[key].camt+'</span>','<span class="ind_formats6">'+response.data.table6.supp_rchrg_unreg[key].samt+'</span>','<span class="ind_formats6">'+response.data.table6.supp_rchrg_unreg[key].csamt+'</span>']);						 
						});
					}
					if(response.data.table6.difference){
						dbFilingTable6.row.add(['(J)Difference (I - A above)','<span class="">-</span>','<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.difference.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.difference.camt+'</span>','<span class="ind_formats6">'+response.data.table6.difference.samt+'</span>','<span class="ind_formats6">'+response.data.table6.difference.csamt+'</span>']);
					}
					if(response.data.table6.sub_totalKM){
						dbFilingTable6.row.add(['(N)Sub-total (K to M above)','<span class="">-</span>','<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.sub_totalKM.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.sub_totalKM.camt+'</span>','<span class="ind_formats6">'+response.data.table6.sub_totalKM.samt+'</span>','<span class="ind_formats6">'+response.data.table6.sub_totalKM.csamt+'</span>']);
					}
					if(response.data.table6.sub_totalBH){
						dbFilingTable6.row.add(['(I)Sub-total (B to H above)','<span class="">-</span>','<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.sub_totalBH.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.sub_totalBH.camt+'</span>','<span class="ind_formats6">'+response.data.table6.sub_totalBH.samt+'</span>','<span class="ind_formats6">'+response.data.table6.sub_totalBH.csamt+'</span>']);
					}
					if(response.data.table6.total_itc_availed){
						dbFilingTable6.row.add(['(O)Total ITC availed (I + N above)','<span class="">-</span>','<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.total_itc_availed.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.total_itc_availed.camt+'</span>','<span class="ind_formats6">'+response.data.table6.total_itc_availed.samt+'</span>','<span class="ind_formats6">'+response.data.table6.total_itc_availed.csamt+'</span>']);
					}
					if(response.data.table6.itc_3b){
						dbFilingTable6.row.add(['(A)Total amount of input tax credit availed through FORM GSTR-3B (sum total of Table 4A of FORM GSTR-3B)','<span class="">-</span>','<span class="">-</span>', '<span class="ind_formats6">'+response.data.table6.itc_3b.iamt+'</span>', '<span class="ind_formats6">'+response.data.table6.itc_3b.camt+'</span>','<span class="ind_formats6">'+response.data.table6.itc_3b.samt+'</span>','<span class="ind_formats6">'+response.data.table6.itc_3b.csamt+'</span>']);
					}
					dbFilingTable6.draw();
					$(".ind_formats6").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats6'});
				}
				if(response.data.table7){
					dbFilingTable7.clear().draw();
					if(response.data.table7.rule37){
						dbFilingTable7.row.add(['(A)As per Rule 37', '<span class="">-</span>',' <span class="">-</span>', '<span class="ind_formats7">'+response.data.table7.rule37.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.rule37.camt+'</span>','<span class="ind_formats7">'+response.data.table7.rule37.samt+'</span>','<span class="ind_formats7">'+response.data.table7.rule37.csamt+'</span>']);
					}
					if(response.data.table7.rule39){
						dbFilingTable7.row.add(['(B)As per Rule 39', '<span class="">-</span>',' <span class="">-</span>', '<span class="ind_formats7">'+response.data.table7.rule39.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.rule39.camt+'</span>','<span class="ind_formats7">'+response.data.table7.rule39.samt+'</span>','<span class="ind_formats7">'+response.data.table7.rule39.csamt+'</span>']);
					}
					if(response.data.table7.rule42){
						dbFilingTable7.row.add(['(C)As per Rule 42', '<span class="">-</span>', ' <span class="">-</span>','<span class="ind_formats7">'+response.data.table7.rule42.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.rule42.camt+'</span>','<span class="ind_formats7">'+response.data.table7.rule42.samt+'</span>','<span class="ind_formats7">'+response.data.table7.rule42.csamt+'</span>']);
					}
					if(response.data.table7.rule43){
						dbFilingTable7.row.add(['(D)As per Rule 43', '<span class="">-</span>',' <span class="">-</span>', '<span class="ind_formats7">'+response.data.table7.rule43.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.rule43.camt+'</span>','<span class="ind_formats7">'+response.data.table7.rule43.samt+'</span>','<span class="ind_formats7">'+response.data.table7.rule43.csamt+'</span>']);
					}
					if(response.data.table7.sec17){
						dbFilingTable7.row.add(['(E)As per section 17(5)',' <span class="">-</span>','<span class="ind_formats7">'+response.data.table7.sec17.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.sec17.camt+'</span>','<span class="ind_formats7">'+response.data.table7.sec17.samt+'</span>','<span class="ind_formats7">'+response.data.table7.sec17.csamt+'</span>']);
					}
					if(response.data.table7.revsl_tran1){
						dbFilingTable7.row.add(['(F)Reversal of TRAN-I credit', ' <span class="">-</span>','<span class="">-</span>', '<span class="ind_formats7">'+response.data.table7.revsl_tran1.camt+'</span>','<span class="ind_formats7">'+response.data.table7.revsl_tran1.samt+'</span>','<span class="">-</span>']);
					}
					if(response.data.table7.revsl_tran2){
						dbFilingTable7.row.add(['(G)Reversal of TRAN-II credit', ' <span class="">-</span>','<span class="">-</span>', '<span class="ind_formats7">'+response.data.table7.revsl_tran2.camt+'</span>','<span class="ind_formats7">'+response.data.table7.revsl_tran2.samt+'</span>','<span class="">-</span>']);
					}
					if(response.data.table7.other){
						$.each(response.data.table7.other, function(key) {
							dbFilingTable7.row.add(['(H)Other reversals (pl. specify)', '<span class="">'+response.data.table7.other[key].desc+'</span>','<span class="ind_formats7">'+response.data.table7.other[key].iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.other[key].camt+'</span>','<span class="ind_formats7">'+response.data.table7.other[key].samt+'</span>','<span class="ind_formats7">'+response.data.table7.other[key].csamt+'</span>']);
						});
					}
					if(response.data.table7.net_itc_aval){
						dbFilingTable7.row.add(['(J)Net ITC Available for Utilization (6O - 7I)', '<span class="">-</span>','<span class="ind_formats7">'+response.data.table7.net_itc_aval.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.net_itc_aval.camt+'</span>','<span class="ind_formats7">'+response.data.table7.net_itc_aval.samt+'</span>','<span class="ind_formats7">'+response.data.table7.net_itc_aval.csamt+'</span>']);
					}
					if(response.data.table7.tot_itc_revd){
						dbFilingTable7.row.add(['(I)Total ITC Reversed (A to H above)', '<span class="">-</span>','<span class="ind_formats7">'+response.data.table7.tot_itc_revd.iamt+'</span>', '<span class="ind_formats7">'+response.data.table7.tot_itc_revd.camt+'</span>','<span class="ind_formats7">'+response.data.table7.tot_itc_revd.samt+'</span>','<span class="ind_formats7">'+response.data.table7.tot_itc_revd.csamt+'</span>']);
					}
					dbFilingTable7.draw();
					$(".ind_formats7").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats7'});
				}
				if(response.data.table8){
					dbFilingTable8.clear().draw();
					if(response.data.table8.itc_inwd_supp){
						dbFilingTable8.row.add(['(C)ITC on inward supplies (other than imports and inward supplies liable to reverse charge but includes services received from SEZs) received during 2017-18 but availed during April to September, 2018','<span class="ind_formats8">'+response.data.table8.itc_inwd_supp.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.itc_inwd_supp.camt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_inwd_supp.samt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_inwd_supp.csamt+'</span>']);
					}
					if(response.data.table8.itc_nt_avalid){
						dbFilingTable8.row.add(['(E)ITC available but not availed (out of D)','<span class="ind_formats8">'+response.data.table8.itc_nt_avalid.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.itc_nt_avalid.camt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_nt_avalid.samt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_nt_avalid.csamt+'</span>']);
					}
					if(response.data.table8.itc_nt_eleg){
						dbFilingTable8.row.add(['(F)ITC available but ineligible (out of D)','<span class="ind_formats8">'+response.data.table8.itc_nt_eleg.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.itc_nt_eleg.camt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_nt_eleg.samt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_nt_eleg.csamt+'</span>']);
					}
					if(response.data.table8.iog_taxpaid){
						dbFilingTable8.row.add(['(G)IGST paid on import of goods (including supplies from SEZ)','<span class="ind_formats8">'+response.data.table8.iog_taxpaid.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.iog_taxpaid.camt+'</span>','<span class="ind_formats8">'+response.data.table8.iog_taxpaid.samt+'</span>','<span class="ind_formats8">'+response.data.table8.iog_taxpaid.csamt+'</span>']);
					}
					if(response.data.table8.itc_2a){
						dbFilingTable8.row.add(['(A)ITC as per GSTR-2A (Table 3 & 5 thereof)','<span class="ind_formats8">'+response.data.table8.itc_2a.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.itc_2a.camt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_2a.samt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_2a.csamt+'</span>']);
					}
					if(response.data.table8.itc_tot){
						dbFilingTable8.row.add(['(B)ITC as per sum total of 6(B) and 6(H) above','<span class="ind_formats8">'+response.data.table8.itc_tot.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.itc_tot.camt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_tot.samt+'</span>','<span class="ind_formats8">'+response.data.table8.itc_tot.csamt+'</span>']);
					}
					if(response.data.table8.iog_itc_availd){
						dbFilingTable8.row.add(['(H)IGST credit availed on import of goods (as per 6(E) above)','<span class="ind_formats8">'+response.data.table8.iog_itc_availd.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.iog_itc_availd.camt+'</span>','<span class="ind_formats8">'+response.data.table8.iog_itc_availd.samt+'</span>','<span class="ind_formats8">'+response.data.table8.iog_itc_availd.csamt+'</span>']);
					}
					if(response.data.table8.iog_itc_ntavaild){
						dbFilingTable8.row.add(['(J)ITC available but not availed on import of goods (Equal to I)','<span class="ind_formats8">'+response.data.table8.iog_itc_ntavaild.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.iog_itc_ntavaild.camt+'</span>','<span class="ind_formats8">'+response.data.table8.iog_itc_ntavaild.samt+'</span>','<span class="ind_formats8">'+response.data.table8.iog_itc_ntavaild.csamt+'</span>']);
					}
					if(response.data.table8.differenceABC){
						dbFilingTable8.row.add(['(D)Difference [A-(B+C)]','<span class="ind_formats8">'+response.data.table8.differenceABC.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.differenceABC.camt+'</span>','<span class="ind_formats8">'+response.data.table8.differenceABC.samt+'</span>','<span class="ind_formats8">'+response.data.table8.differenceABC.csamt+'</span>']);
					}
					if(response.data.table8.differenceGH){
						dbFilingTable8.row.add(['(I)Difference (G-H)','<span class="ind_formats8">'+response.data.table8.differenceGH.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.differenceGH.camt+'</span>','<span class="ind_formats8">'+response.data.table8.differenceGH.samt+'</span>','<span class="ind_formats8">'+response.data.table8.differenceGH.csamt+'</span>']);
					}
					if(response.data.table8.tot_itc_lapsed){
						dbFilingTable8.row.add(['(K)Total ITC to be lapsed in current financial year (E + F + J)','<span class="ind_formats8">'+response.data.table8.tot_itc_lapsed.iamt+'</span>', '<span class="ind_formats8">'+response.data.table8.tot_itc_lapsed.camt+'</span>','<span class="ind_formats8">'+response.data.table8.tot_itc_lapsed.samt+'</span>','<span class="ind_formats8">'+response.data.table8.tot_itc_lapsed.csamt+'</span>']);
					}
					dbFilingTable8.draw();
					$(".ind_formats8").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats8'});
				}
				if(response.data.table9){	
					dbFilingTable9.clear().draw();
					if(response.data.table9.iamt){
						dbFilingTable9.row.add(['Integrated Tax', '<span class="ind_formats9">'+response.data.table9.iamt.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.iamt.txpaid_cash+'</span>', '<span class="ind_formats9">'+response.data.table9.iamt.tax_paid_itc_iamt+'</span>','<span class="ind_formats9">'+response.data.table9.iamt.tax_paid_itc_camt+'</span>','<span class="ind_formats9">'+response.data.table9.iamt.tax_paid_itc_samt+'</span>','<span class="">-</span>']);
					}
					if(response.data.table9.camt){
						dbFilingTable9.row.add(['Central Tax', '<span class="ind_formats9">'+response.data.table9.camt.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.camt.txpaid_cash+'</span>', '<span class="ind_formats9">'+response.data.table9.camt.tax_paid_itc_iamt+'</span>','<span class="ind_formats9">'+response.data.table9.camt.tax_paid_itc_camt+'</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table9.samt){
						dbFilingTable9.row.add(['State/UT Tax', '<span class="ind_formats9">'+response.data.table9.samt.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.samt.txpaid_cash+'</span>', '<span class="ind_formats9">'+response.data.table9.samt.tax_paid_itc_iamt+'</span>','<span class="">-</span>','<span class="ind_formats9">'+response.data.table9.samt.tax_paid_itc_samt+'</span>','<span class="">-</span>']);
					}
					if(response.data.table9.csamt){
						dbFilingTable9.row.add(['Cess', '<span class="ind_formats9">'+response.data.table9.csamt.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.csamt.txpaid_cash+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="ind_formats9">'+response.data.table9.csamt.tax_paid_itc_csamt+'</span>']);
					}
					if(response.data.table9.intr){
						dbFilingTable9.row.add(['Interest', '<span class="ind_formats9">'+response.data.table9.intr.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.intr.txpaid_cash+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table9.tee){
						dbFilingTable9.row.add(['Late fee', '<span class="ind_formats9">'+response.data.table9.tee.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.tee.txpaid_cash+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table9.pen){
						dbFilingTable9.row.add(['Penalty', '<span class="ind_formats9">'+response.data.table9.pen.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.pen.txpaid_cash+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table9.other){
						dbFilingTable9.row.add(['other', '<span class="ind_formats9">'+response.data.table9.other.txpyble+'</span>', '<span class="ind_formats9">'+response.data.table9.other.txpaid_cash+'</span>', '<span class="">-</span>','<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					dbFilingTable9.draw();
					$(".ind_formats9").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats9'});
				}
				if(response.data.table10){
					dbFilingTable10.clear().draw();
					if(response.data.table10.dbn_amd){
						dbFilingTable10.row.add(['(10)Supplies / tax declared through Amendments (+) (net of debit notes)', '<span class="ind_formats10">'+response.data.table10.dbn_amd.txval+'</span>', '<span class="ind_formats10">'+response.data.table10.dbn_amd.iamt+'</span>', '<span class="ind_formats10">'+response.data.table10.dbn_amd.camt+'</span>','<span class="ind_formats10">'+response.data.table10.dbn_amd.samt+'</span>','<span class="ind_formats10">'+response.data.table10.dbn_amd.csamt+'</span>']);
					}
					if(response.data.table10.cdn_amd){
						dbFilingTable10.row.add(['(11)Supplies / tax reduced through Amendments (-) (net of credit notes)', '<span class="ind_formats10">'+response.data.table10.cdn_amd.txval+'</span>', '<span class="ind_formats10">'+response.data.table10.cdn_amd.iamt+'</span>', '<span class="ind_formats10">'+response.data.table10.cdn_amd.camt+'</span>','<span class="ind_formats10">'+response.data.table10.cdn_amd.samt+'</span>','<span class="ind_formats10">'+response.data.table10.cdn_amd.csamt+'</span>']);
					}
					if(response.data.table10.itc_rvsl){
						dbFilingTable10.row.add(['(12)Reversal of ITC availed during previous financial year', '<span class="ind_formats10">'+response.data.table10.itc_rvsl.txval+'</span>', '<span class="ind_formats10">'+response.data.table10.itc_rvsl.iamt+'</span>', '<span class="ind_formats10">'+response.data.table10.itc_rvsl.camt+'</span>','<span class="ind_formats10">'+response.data.table10.itc_rvsl.samt+'</span>','<span class="ind_formats10">'+response.data.table10.itc_rvsl.csamt+'</span>']);
					}
					if(response.data.table10.itc_availd){
						dbFilingTable10.row.add(['(13)ITC availed for the previous financial year','<span class="ind_formats10">'+response.data.table10.itc_availd.txval+'</span>', '<span class="ind_formats10">'+response.data.table10.itc_availd.iamt+'</span>', '<span class="ind_formats10">'+response.data.table10.itc_availd.camt+'</span>','<span class="ind_formats10">'+response.data.table10.itc_availd.samt+'</span>','<span class="ind_formats10">'+response.data.table10.itc_availd.csamt+'</span>']);
					}
					if(response.data.table10.total_turnover){
						dbFilingTable10.row.add(['Total turnover(5N + 10 - 11)', '<span class="ind_formats10">'+response.data.table10.total_turnover.txval+'</span>', '<span class="ind_formats10">'+response.data.table10.total_turnover.iamt+'</span>', '<span class="ind_formats10">'+response.data.table10.total_turnover.camt+'</span>','<span class="ind_formats10">'+response.data.table10.total_turnover.samt+'</span>','<span class="ind_formats10">'+response.data.table10.total_turnover.csamt+'</span>']);
					}
					dbFilingTable10.draw();
					$(".ind_formats10").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats10'});
				}
				if(response.data.table14){
					dbFilingTable14.clear().draw();
					if(response.data.table14.iamt){
						dbFilingTable14.row.add(['Integrated Tax','<span class="ind_formats14">'+response.data.table14.iamt.txpyble+'</span>', '<span class="ind_formats14">'+response.data.table14.iamt.txpaid+'</span>']);
					}
					if(response.data.table14.samt){
						dbFilingTable14.row.add(['State/UT Tax','<span class="ind_formats14">'+response.data.table14.samt.txpyble+'</span>', '<span class="ind_formats14">'+response.data.table14.samt.txpaid+'</span>']);
					}
					if(response.data.table14.camt){
						dbFilingTable14.row.add(['Central Tax','<span class="ind_formats14">'+response.data.table14.camt.txpyble+'</span>', '<span class="ind_formats14">'+response.data.table14.camt.txpaid+'</span>']);
					}
					if(response.data.table14.csamt){
						dbFilingTable14.row.add(['Cess','<span class="ind_formats14">'+response.data.table14.csamt.txpyble+'</span>', '<span class="ind_formats14">'+response.data.table14.csamt.txpaid+'</span>']);
					}
					if(response.data.table14.intr){
						dbFilingTable14.row.add(['Interest','<span class="ind_formats14">'+response.data.table14.intr.txpyble+'</span>', '<span class="ind_formats14">'+response.data.table14.intr.txpaid+'</span>']);
					}
					dbFilingTable14.draw();
					$(".ind_formats14").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats14'});
				}
				if(response.data.table15){
					dbFilingTable15.clear().draw();
					if(response.data.table15.rfd_clmd){
						dbFilingTable15.row.add(['(A)Total Refund claimed', '<span class="ind_formats15">'+response.data.table15.rfd_clmd.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.rfd_clmd.camt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_clmd.samt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_clmd.csamt+'</span>', '<span class="">-</span>', '<span class="">-</span>', '<span class="">-</span>']);
					}
					if(response.data.table15.rfd_sanc){
						dbFilingTable15.row.add(['(B)Total Refund sanctioned', '<span class="ind_formats15">'+response.data.table15.rfd_sanc.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.rfd_sanc.camt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_sanc.samt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_sanc.csamt+'</span>', '<span class="">-</span>', '<span class="">-</span>', '<span class="">-</span>']);
					}
					if(response.data.table15.rfd_rejt){
						dbFilingTable15.row.add(['(c)Total Refund Rejected', '<span class="ind_formats15">'+response.data.table15.rfd_rejt.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.rfd_rejt.camt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_rejt.samt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_rejt.csamt+'</span>', '<span class="">-</span>', '<span class="">-</span>', '<span class="">-</span>']);
					}
					if(response.data.table15.rfd_pend){
						dbFilingTable15.row.add(['(D)Total Refund Pending', '<span class="ind_formats15">'+response.data.table15.rfd_pend.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.rfd_pend.camt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_pend.samt+'</span>','<span class="ind_formats15">'+response.data.table15.rfd_pend.csamt+'</span>', '<span class="">-</span>', '<span class="">-</span>', '<span class="">-</span>']);
					}
					if(response.data.table15.tax_dmnd){
						dbFilingTable15.row.add(['(E)Total demand of taxes', '<span class="ind_formats15">'+response.data.table15.tax_dmnd.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.tax_dmnd.camt+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.samt+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.csamt+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.intr+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.fee+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.pen+'</span>']);
					}
					if(response.data.table15.tax_paid){
						dbFilingTable15.row.add(['(F)Total taxes paid in respect of E above', '<span class="ind_formats15">'+response.data.table15.tax_dmnd.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.tax_dmnd.camt+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.samt+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.csamt+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.intr+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.fee+'</span>','<span class="ind_formats15">'+response.data.table15.tax_dmnd.pen+'</span>']);
					}
					if(response.data.table15.dmnd_pend){
						dbFilingTable15.row.add(['(G)Total demands pending out of E above', '<span class="ind_formats15">'+response.data.table15.dmnd_pend.iamt+'</span>', '<span class="ind_formats15">'+response.data.table15.dmnd_pend.camt+'</span>','<span class="ind_formats15">'+response.data.table15.dmnd_pend.samt+'</span>','<span class="ind_formats15">'+response.data.table15.dmnd_pend.csamt+'</span>','<span class="ind_formats15">'+response.data.table15.dmnd_pend.intr+'</span>','<span class="ind_formats15">'+response.data.table15.dmnd_pend.fee+'</span>','<span class="ind_formats15">'+response.data.table15.dmnd_pend.pen+'</span>']);
					}
					dbFilingTable15.draw();
					$(".ind_formats15").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats15'});
				}	
				if(response.data.table16){
					dbFilingTable16.clear().draw();
					if(response.data.table16.comp_supp){
						dbFilingTable16.row.add(['(A)Supplies received from Composition taxpayers', '<span class="ind_formats16">'+response.data.table16.comp_supp.txval+'</span>', '<span class="">-</span>', '<span class="">-</span>','<span class="">-</span>','<span class="">-</span>']);
					}
					if(response.data.table16.deemed_supp){
						dbFilingTable16.row.add(['(B)Deemed supply under Section 143', '<span class="ind_formats16">'+response.data.table16.deemed_supp.txval+'</span>', '<span class="ind_formats16">'+response.data.table16.deemed_supp.iamt+'</span>', '<span class="ind_formats16">'+response.data.table16.deemed_supp.camt+'</span>', '<span class="ind_formats16">'+response.data.table16.deemed_supp.samt+'</span>', '<span class="ind_formats16">'+response.data.table16.deemed_supp.csamt+'</span>']);
					}
					if(response.data.table16.not_returned){
						dbFilingTable16.row.add(['(C)Goods sent on approval basis but not returned', '<span class="ind_formats16">'+response.data.table16.not_returned.txval+'</span>', '<span class="ind_formats16">'+response.data.table16.not_returned.iamt+'</span>', '<span class="ind_formats16">'+response.data.table16.not_returned.camt+'</span>', '<span class="ind_formats16">'+response.data.table16.not_returned.samt+'</span>', '<span class="ind_formats16">'+response.data.table16.not_returned.csamt+'</span>']);
					}
					dbFilingTable16.draw();
					$(".ind_formats16").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats16'});
				}
				if(response.data.table17){
					dbFilingTable17.clear().draw();
					if(response.data.table17.items){
						$.each(response.data.table17.items, function(key) {
							dbFilingTable17.row.add(['Items', '<span class="">'+response.data.table17.items[key].hsn_sc+'</span>','<span class="">'+response.data.table17.items[key].uqc+'</span>','<span class="">'+response.data.table17.items[key].desc+'</span>','<span class="">'+response.data.table17.items[key].isconcesstional+'</span>', '<span class="ind_formats17">'+response.data.table17.items[key].qty+'</span>','<span class="ind_formats17">'+response.data.table17.items[key].rt+'</span>', '<span class="ind_formats17">'+response.data.table17.items[key].txval+'</span>', '<span class="ind_formats17">'+response.data.table17.items[key].iamt+'</span>','<span class="ind_formats17">'+response.data.table17.items[key].camt+'</span>','<span class="ind_formats17">'+response.data.table17.items[key].samt+'</span>','<span class="ind_formats17">'+response.data.table17.items[key].csamt+'</span>']);
						});
					}
					dbFilingTable17.draw();
					$(".ind_formats17").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats17'});
				}	
				if(response.data.table18){
					dbFilingTable18.clear().draw();
					if(response.data.table18.items){
						$.each(response.data.table18.items, function(key) {
							dbFilingTable18 .row.add(['Items', '<span class="">'+response.data.table18.items[key].hsn_sc+'</span>','<span class="">'+response.data.table18.items[key].uqc+'</span>','<span class="">'+response.data.table18.items[key].desc+'</span>','<span class="">'+response.data.table18.items[key].isconcesstional+'</span>', '<span class="ind_formats17">'+response.data.table18.items[key].qty+'</span>','<span class="ind_formats17">'+response.data.table18.items[key].rt+'</span>', '<span class="ind_formats17">'+response.data.table18.items[key].txval+'</span>', '<span class="ind_formats17">'+response.data.table18.items[key].iamt+'</span>','<span class="ind_formats17">'+response.data.table18.items[key].camt+'</span>','<span class="ind_formats17">'+response.data.table18.items[key].samt+'</span>','<span class="ind_formats17">'+response.data.table18.items[key].csamt+'</span>']);
						});
					}
					dbFilingTable18.draw();
					$(".ind_formats18").each(function(){
					    $(this).html($(this).html().replace(/,/g , ''));
					});
					OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats18'});
				}
				}
			},
			error : function(e, status, error) {
			}
		});
		}else{
			errorNotification('Your OTP Session Expired, Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}
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
	function autocalculategstr9(){
		otpExpiryCheck();
		if(otpExpirycheck == 'OTP_VERIFIED'){
			window.location.href = _getContextPath()+'/populate9'+commonSuffix+'/'+clientId+'/03/'+year;
		}else{
			errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}
</script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.formula.js" type="text/javascript"></script>
<script  src="${contextPath}/static/mastergst/js/gstr9/gstr9custom_datatables.js" type="text/javascript"></script>
<script  src="${contextPath}/static/mastergst/js/gstr9/gstr9.js" type="text/javascript"></script>
</body>

 </html> 