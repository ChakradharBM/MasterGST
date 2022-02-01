<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Upload GSTR4 | File GSTR4</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://cdn.datatables.net/fixedheader/3.1.5/js/dataTables.fixedHeader.min.js"></script>
<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="https://cdn.datatables.net/fixedheader/3.1.5/css/fixedHeader.dataTables.min.css" media="all" />
<style>
#gstintable thead{font-size:12px;}
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
<c:set var="userid" value="${id}"/>

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
		$('#b2bor_records').val('${b2borAmts.totalTransactions}' ? '${b2borAmts.totalTransactions}' :'0');
		$('#b2bor_taxableValue').val('${b2borAmts.totalTaxableAmount}' ? '${b2borAmts.totalTaxableAmount}' : '0.00');
		$('#b2bor_cgst').val('${b2borAmts.totalCGSTAmount}' ? '${b2borAmts.totalCGSTAmount}' : '0.00');
		$('#b2bor_sgst').val('${b2borAmts.totalSGSTAmount}' ? '${b2borAmts.totalSGSTAmount}' : '0.00');
		$('#b2bor_igst').val('${b2borAmts.totalIGSTAmount}' ? '${b2borAmts.totalIGSTAmount}' : '0.00');
		$('#b2bor_cess').val('${b2borAmts.totalCESSAmount}' ? '${b2borAmts.totalCESSAmount}' : '0.00');
			 
		$('#b2br_records').val('${b2brAmts.totalTransactions}' ? '${b2brAmts.totalTransactions}' : '0');
		$('#b2br_taxableValue').val('${b2brAmts.totalTaxableAmount}' ? '${b2brAmts.totalTaxableAmount}' : '0.00');
		$('#b2br_cgst').val('${b2brAmts.totalCGSTAmount}' ? '${b2brAmts.totalCGSTAmount}' : '0.00');
		$('#b2br_sgst').val('${b2brAmts.totalSGSTAmount}' ? '${b2brAmts.totalSGSTAmount}' : '0.00');
		$('#b2br_igst').val('${b2brAmts.totalIGSTAmount}' ? '${b2brAmts.totalIGSTAmount}' : '0.00');
		$('#b2br_cess').val('${b2brAmts.totalCESSAmount}' ? '${b2brAmts.totalCESSAmount}' : '0.00');
		
		$('#b2c_records').val('${b2cAmts.totalTransactions}' ? '${b2cAmts.totalTransactions}' : '0');
		$('#b2c_taxableValue').val('${b2cAmts.totalTaxableAmount}' ? '${b2cAmts.totalTaxableAmount}' : '0.00');
		$('#b2c_cgst').val('${b2cAmts.totalCGSTAmount}' ? '${b2cAmts.totalCGSTAmount}' : '0.00');
		$('#b2c_sgst').val('${b2cAmts.totalSGSTAmount}' ? '${b2cAmts.totalSGSTAmount}' : '0.00');
		$('#b2c_igst').val('${b2cAmts.totalIGSTAmount}' ? '${b2cAmts.totalIGSTAmount}' : '0.00');
		$('#b2c_cess').val('${b2cAmts.totalCESSAmount}' ? '${b2cAmts.totalCESSAmount}' : '0.00');
		
		$('#imps_records').val('${impsAmts.totalTransactions}' ? '${impsAmts.totalTransactions}' : '0');
		$('#imps_taxableValue').val('${impsAmts.totalTaxableAmount}' ? '${impsAmts.totalTaxableAmount}' : '0.00');
		$('#imps_cgst').val('${impsAmts.totalCGSTAmount}' ? '${impsAmts.totalCGSTAmount}' : '0.00');
		$('#imps_sgst').val('${impsAmts.totalSGSTAmount}' ? '${impsAmts.totalSGSTAmount}' : '0.00');
		$('#imps_igst').val('${impsAmts.totalIGSTAmount}' ? '${impsAmts.totalIGSTAmount}' : '0.00');
		$('#imps_cess').val('${impsAmts.totalCESSAmount}' ? '${impsAmts.totalCESSAmount}' : '0.00');
		
		if('<c:out value="${gstr4annualcmp}"/>' != "" && '<c:out value="${gstr4annualcmp}"/>' != null){
			
			if('<c:out value="${gstr4annualcmp.cmpsmry}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry}"/>' != null){
				if('<c:out value="${gstr4annualcmp.cmpsmry.out_sup}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.out_sup}"/>' != null){
					if('<c:out value="${gstr4annualcmp.cmpsmry.out_sup.tax_val}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.out_sup.tax_val}"/>' != null){
						$('#tab5_outwardTxval').val('<c:out value="${gstr4annualcmp.cmpsmry.out_sup.tax_val}"/>');
					}else{$('#tab5_outwardTxval').val('0.0');}
					
					$('#tab5_outwardIamt').val('0.0');
					if('<c:out value="${gstr4annualcmp.cmpsmry.out_sup.camt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.out_sup.camt}"/>' != null){
						$('#tab5_outwardCamt').val('<c:out value="${gstr4annualcmp.cmpsmry.out_sup.camt}"/>');					
					}else{$('#tab5_outwardCamt').val('0.0');}
					if('<c:out value="${gstr4annualcmp.cmpsmry.out_sup.samt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.out_sup.samt}"/>' != null){
						$('#tab5_outwardSamt').val('<c:out value="${gstr4annualcmp.cmpsmry.out_sup.samt}"/>');
					}else{$('#tab5_outwardSamt').val('0.0');}
					
					$('#tab5_outwardCsamt').val('0.0');
				}
				if('<c:out value="${gstr4annualcmp.cmpsmry.in_sup}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.in_sup}"/>' != null){
					if('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.tax_val}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.in_sup.tax_val}"/>' != null){
						$('#tab5_inwardTaxval').val('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.tax_val}"/>');
					}else{$('#tab5_inwardTaxval').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.iamt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.in_sup.iamt}"/>' != null){
						$('#tab5_inwardIamt').val('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.iamt}"/>');					
					}else{$('#tab5_inwardIamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.camt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.in_sup.camt}"/>' != null){
						$('#tab5_inwardCamt').val('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.camt}"/>');					
					}else{$('#tab5_inwardCamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.samt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.in_sup.samt}"/>' != null){
						$('#tab5_inwardSamt').val('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.samt}"/>');
					}else{$('#tab5_inwardSamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.csamt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.in_sup.csamt}"/>' != null){
						$('#tab5_inwardCsamt').val('<c:out value="${gstr4annualcmp.cmpsmry.in_sup.csamt}"/>');					
					}else{$('#tab5_inwardCsamt').val('0.0');}
				}
				if('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.tax_pay}"/>' != null){
					if('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.tax_val}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.tax_val}"/>' != null){
						$('#tab5_taxpaidTaxval').val('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.tax_val}"/>');
					}else{$('#tab5_taxpaidTaxval').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.iamt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.iamt}"/>' != null){
						$('#tab5_taxpaidIamt').val('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.iamt}"/>');					
					}else{$('#tab5_taxpaidIamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.camt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.camt}"/>' != null){
						$('#tab5_taxpaidCamt').val('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.camt}"/>');					
					}else{$('#tab5_taxpaidCamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.samt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.samt}"/>' != null){
						$('#tab5_taxpaidSamt').val('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.samt}"/>');
					}else{$('#tab5_taxpaidSamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.csamt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.csamt}"/>' != null){
						$('#tab5_taxpaidCsamt').val('<c:out value="${gstr4annualcmp.cmpsmry.tax_pay.csamt}"/>');					
					}else{$('#tab5_taxpaidCsamt').val('0.0');}
				}
				if('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.intr_pay}"/>' != null){
					$('#tab5_interestTaxval').val('0.0');
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.iamt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.iamt}"/>' != null){
						$('#tab5_interestIamt').val('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.iamt}"/>');					
					}else{$('#tab5_interestIamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.camt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.camt}"/>' != null){
						$('#tab5_interestCamt').val('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.camt}"/>');					
					}else{$('#tab5_interestCamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.samt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.samt}"/>' != null){
						$('#tab5_interestSamt').val('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.samt}"/>');
					}else{$('#tab5_interestSamt').val('0.0');}
					
					if('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.csamt}"/>' != "" && '<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.csamt}"/>' != null){
						$('#tab5_interestCsamt').val('<c:out value="${gstr4annualcmp.cmpsmry.intr_pay.csamt}"/>');					
					}else{$('#tab5_interestCsamt').val('0.0');}
				}
			}
			if('<c:out value="${gstr4annualcmp.tdstcs}"/>' != "" && '<c:out value="${gstr4annualcmp.tdstcs}"/>' != null){
				<c:forEach items="${gstr4annualcmp.tdstcs}" var="tdstcs" varStatus="loop">
					$('#tab7_tdstcs${loop.index}Ctin').val('${tdstcs.ctin}');
					$('#tab7_tdstcs${loop.index}Gross').val('${tdstcs.gross_val}');
					$('#tab7_tdstcs${loop.index}Camt').val('${tdstcs.camt}');
					$('#tab7_tdstcs${loop.index}Samt').val('${tdstcs.samt}');
				</c:forEach>					
			}
		}
		
		function forceNumeric(){
			var $input = $(this);
			$input.val($input.val().replace(/[^\d.,]+/g,''));
		}
		
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
function supplierWiseAmts(type){
	var yearCode = $('#gstr4FinancialYear').val();
	$('#gstwiseData').html('');
	var tableContent='';
	$.ajax({
		url : contextPath+'/populateSupplierData'+urlSuffixs+'/'+yearCode+'?type='+type,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			var tableContent = "";
			var index = 1;
			if(response !="" && response !=null){
				if(type == 'b2bor'){
					if(response.b2bor !="" && response.b2bor !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2bor.length; i++){
							
							for(var j = 0; j< response.b2bor[i].itms.length; j++){
								txval += response.b2bor[i].itms[j].txval;
								iamt += response.b2bor[i].itms[j].iamt;
								camt += response.b2bor[i].itms[j].camt;
								samt += response.b2bor[i].itms[j].samt;
								csamt += response.b2bor[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2bor[i].ctin+'</td><td></td><td>'+response.b2bor[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'b2br'){
					if(response.b2br !="" && response.b2br !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2br.length; i++){
							
							for(var j = 0; j< response.b2br[i].itms.length; j++){
								txval += response.b2br[i].itms[j].txval;
								iamt += response.b2br[i].itms[j].iamt;
								camt += response.b2br[i].itms[j].camt;
								samt += response.b2br[i].itms[j].samt;
								csamt += response.b2br[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2br[i].ctin+'</td><td></td><td>'+response.b2br[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'b2c'){
					if(response.b2bur !="" && response.b2bur !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2bur.length; i++){
							
							for(var j = 0; j< response.b2bur[i].itms.length; j++){
								txval += response.b2bur[i].itms[j].txval;
								iamt += response.b2bur[i].itms[j].iamt;
								camt += response.b2bur[i].itms[j].camt;
								samt += response.b2bur[i].itms[j].samt;
								csamt += response.b2bur[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2bur[i].ctin+'</td><td></td><td>'+response.b2bur[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'imps'){
					if(response.imps !="" && response.imps !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.imps.length; i++){
							
							for(var j = 0; j< response.imps[i].itms.length; j++){
								txval += response.imps[i].itms[j].txval;
								iamt += response.imps[i].itms[j].iamt;
								//camt += response.imps[i].itms[j].camt;
								//samt += response.imps[i].itms[j].samt;
								csamt += response.imps[i].itms[j].csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td></td><td></td><td>'+response.imps[i].pos+'</td><td>'+txval+'</td><td>'+iamt+'</td><td>'+camt+'</td><td>'+samt+'</td><td>'+csamt+'</td></tr>';
							index++;
						}
					}
				}
			}
			$('#gstwiseData').append(tableContent);
		},error:function(err){console.log(err);}
	});
}
	function formatNumber(nStr) {
		var negativenumber = false;
		if(nStr && nStr.includes("-")){
			negativenumber = true;
			nStr = nStr.replace("-","");
		}
		nStr=nStr.toString();var afterPoint = '';
		if(nStr.indexOf('.') > 0)
		   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
		nStr = Math.floor(nStr);
		nStr=nStr.toString();
		var lastThree = nStr.substring(nStr.length-3);
		var otherNumbers = nStr.substring(0,nStr.length-3);
		if(otherNumbers != '')
		    lastThree = ',' + lastThree;
		var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
		if(negativenumber){
			res = "-"+res;
		}
		return res;
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
					<select class="pull-right mt-1" id="gstr4FinancialYear"><option value="2021">2021 - 2022</option><option value="2020">2020 - 2021</option><option value="2019">2019 - 2020</option><option value="2018">2018 - 2019</option><option value="2017">2017 - 2018</option></select>
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
						<div class="pull-right helpguide" data-toggle="modal" data-target="#helpGuideModal"> Help To File GSTR4</div>
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link active" data-toggle="tab" href="#gtab1" role="tab">GSTR 4</a>
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
				<a href="#" id="populategstr9" onclick = "autocalculategstr4()" class="btn btn-greendark pull-right ml-2">Auto Generate</a>
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
								    <label for="turnover" class="col-sm-3 col-form-label ml-3"> Aggregate Turnover in the preceding Financial year</label>
								    <div class="col-sm-6">
								      	<input type="text" class="form-control" id="turnover" value="${turnover}" style="border: 1px solid lightgray;width:50%;">
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
						       4. Inward supplies including supplies on which tax is to be paid on reverse charge
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerOne" class="collapse show" aria-labelledby="headinginnerOne" data-parent="#accordion">
						      <div class="card-body p-2">
						        <div class="customtable db-ca-gst tabtable1 mt-2">
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
										<td colspan="7">4A. Inward supplies received from a registered supplier (other than supplies attracting reverse charge)</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input" id="b2bor_records" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2bor_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2bor_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2bor_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2bor_igst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2bor_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr4-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('b2bor')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></span></td>
									</tr>
									<tr>
										<td colspan="7">4B. Inward supplies received from a registered supplier (attracting reverse charge)</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input" id="b2br_records" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2br_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2br_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2br_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2br_igst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2br_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr4-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('b2br')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></span></td>
									</tr>
									<tr>
										<td colspan="7">4C. Inward supplies received from an unregistered supplier</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control tpthree-input" id="b2c_records" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2c_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2c_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2c_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2c_igst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control tpthree-input" id="b2c_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr4-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('b2c')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></span></td>
									</tr>
									<tr>
										<td colspan="7">4D. Import of service</td>
									</tr>
									<tr>
										<td class="text-left"><input type="text" class="form-control" id="imps_records" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control" id="imps_taxableValue" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control" id="imps_cgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control" id="imps_sgst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control" id="imps_igst" name="" readonly/></td>
										<td class="text-right form-group gstr4-error"><input type="text" class="form-control" id="imps_cess" name=""  readonly/></td>
										<td class="text-center form-group gstr4-error"><span class="action_icons" ><span class="add add-btn" id="addrow" onclick="supplierWiseAmts('imps')" data-toggle="modal" data-target="#addModal">+</span><a class="btn-edt ml-1" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a></span></span></td>
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
						          5. Summary of self-assessed liability as per FORM GST CMP-08 (Net of advances, credit and debit notes and any other adjustment due to amendments etc.)
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerTwo" class="collapse show" aria-labelledby="headinginnerTwo" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
									<th class="text-left" rowspan="2">SR.No</th>
										<th class="text-left" width="30%" rowspan="2">Description</th>
										<th class="text-left" rowspan="2">Value</th>
										<th class="text-center" colspan="4">Amount Of Tax</th>
									</tr>
									<tr>
									<th class="text-left" rowspan="4">Integrated tax(<i class="fa fa-rupee"></i>)</th>
									<th class="text-left" rowspan="4">Central tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left" rowspan="4">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">1</td>
										<td class="text-left form-group gst6-error">Outward supplies (including exempt supplies)</td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_outwardTxval" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_outwardIamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_outwardCamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_outwardSamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_outwardCsamt" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">2</td>
										<td class="text-left form-group gst6-error">Inward supplies attracting reverse charge including import of services</td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_inwardTaxval" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_inwardIamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_inwardCamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_inwardSamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_inwardCsamt" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">3</td>
										<td class="text-left form-group gst6-error">Tax paid (1+2) </td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_taxpaidTaxval" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_taxpaidIamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_taxpaidCamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_taxpaidSamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_taxpaidCsamt" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">4</td>
										<td class="text-left form-group gst6-error">Interest paid, if any </td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_interestTaxval" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_interestIamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_interestCamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_interestSamt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab5_interestCsamt" class="form-control tpthree-input" placeholder="0.00"/></td>
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
						          6. Tax rate wise details of outward supplies / inward supplies attracting reverse charge during the year 
						          <p class="mb-0">(Net of advances, credit and debit notes and any other adjustment due to amendments etc.)</p>
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerThree" class="collapse show" aria-labelledby="headinginnerTwo" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
									<th class="text-left" rowspan="2">SR.No</th>
										<th class="text-left" rowspan="2">Type of supply (Outward/Inward)</th>
										<th class="text-left" rowspan="2">Rate Of Tax (%)</th>
										<th class="text-left" rowspan="2">Value</th>
										<th class="text-center" colspan="4">Amount Of Tax</th>
									</tr>
									<tr>
									<th class="text-left" rowspan="4">Integrated tax(<i class="fa fa-rupee"></i>)</th>
									<th class="text-left" rowspan="4">Central tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left" rowspan="4">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">1</td>
										<td class="text-left form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">2</td>
										<td class="text-left form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-left">3</td>
										<td class="text-left form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
								</tbody>
								<tfoot>
									<tr>
										<th></th>
										<th></th>
										<th style="color:black;">Total</th>
										<th></th>
										<th class="pl-1"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></th>
										<th class="pl-1"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></th>
										<th class="pl-1"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></th>
										<th class="pl-1"><input type="text" class="form-control tpthree-input" placeholder="0.00"/></th>
									</tr>
								</tfoot>
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
						          7. TDS/TCS Credit received
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFour" class="collapse show" aria-labelledby="headinginnerFour" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2" style="overflow-y:auto;">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
											<th class="text-left" rowspan="2">GSTIN of Deductor / ecommerce operator</th>
											<th class="text-left" rowspan="2">Gross Value</th>
											<th class="text-center" colspan="2">Amount</th>
									</tr>
									<tr>
										<th class="text-left" rowspan="2">Central tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left" rowspan="2">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
									</tr>
									
								</thead>
								<tbody>
									<tr>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs0Ctin" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs0Gross" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs0Camt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs0Samt" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									<tr>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs1Ctin" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs1Gross"class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs1Camt" class="form-control tpthree-input" placeholder="0.00"/></td>
										<td class="text-right form-group gst6-error"><input type="text" id="tab7_tdstcs1Samt" class="form-control tpthree-input" placeholder="0.00"/></td>
									</tr>
									
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 4 End -->
						   <!-- Table 5 start -->
						   <div class="card">
						    <div class="card-header" id="headinginnerFive">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFive" aria-expanded="true" aria-controls="collapseinnerFive">
						          8. Tax, interest, late fee payable and paid
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFive" class="collapse show" aria-labelledby="headinginnerFive" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Sr.No.</th>
										<th class="text-left">Type of tax</th>
										<th class="text-left">Tax amount payable (As per table 6)</th>
										<th class="text-left">Tax Amount already paid (Through FORM GST CMP-08 )</th>
										<th class="text-left">Balance amount of tax payable,if any(3-4)</th>
										<th class="text-left">Interest Payable</th>
										<th class="text-left">Interest Paid</th>
										<th class="text-left">Late Fee Payable</th>
										<th class="text-left">Late Fee Paid</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">1</td>
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
									</tr>
									<tr>
										<td class="text-left">2</td>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
									</tr>
									<tr>
										<td class="text-left">3</td>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
									</tr>
									<tr>
										<td class="text-left">4</td>
										<td class="text-left">Cess</td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control tpthree-input"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
										<td class="text-right form-group gst6-error"><input type="text" class="form-control"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 5 End -->
						   <!-- Table 6 start -->
						   <div class="card">
						    <div class="card-header" id="headinginnerSeven">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerSeven" aria-expanded="true" aria-controls="collapseinnerSeven">
						          9. Refund claimed from Electronic cash ledger
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerSeven" class="collapse show" aria-labelledby="headinginnerSeven" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Description</th>
										<th class="text-center">Tax</th>
										<th class="text-center">Interest</th>
										<th class="text-center">Penalty</th>
										<th class="text-center">Other</th>
										<th class="text-center">Debit Entry No's</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>(a) Integrated Tax</td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
									</tr>
									<tr>
										<td>(b) Central Tax</td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
									</tr>
									<tr>
										<td>(c) State/UT Tax</td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
									</tr>
									<tr>
										<td>(d) Cess</td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
										<td><input type="text" class="form-control"/></td>
									</tr>
									<tr>
									<td colspan="7">Bank Account Details</td>
									</tr>
								</tbody>
							</table>
						</div>
						      </div>
						    </div>
						  </div>
						  <!-- Table 6 End -->
						   
						</div>
				</div>
				<div class="col-sm-12 mt-4 text-center">
								<%-- <c:if test="${not empty invoice && not empty invoice.id}">
									<input type="hidden" name="id" value="<c:out value="${invoice.id}"/>">
								</c:if> --%>
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
<!-- modal 3.1--->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
	        <div class="invoice-hdr bluehdr">
	          <h3>Add/Edit Details</h3>
	        </div>
	        <div class="row gstr-info-tabs pl-2 pr-2 pt-1">
	        		 <ul class="nav nav-tabs col-md-12 mt-3 pl-3" role="tablist" id="tabsactive">
						<li class="nav-item"><a class="nav-link active" id="gstinwise" data-toggle="tab" href="#gstintab" role="tab"><span class="serial-num">1</span>GSTIN Wise</a></li>
						<li class="nav-item"><a class="nav-link " id="amountswise" data-toggle="tab" href="#amountstab" role="tab"><span class="serial-num">2</span>Amounts Wise</a></li>
				   </ul>
					<div class="tab-content col-md-12 mb-3 mt-1">
								<div class="tab-pane active col-md-12" id="gstintab" role="tabpane1">
									<div class="customtable">
											<table id="gstintable" class="display row-border dataTable meterialform">
											<thead>
											<tr><th rowspan="2">SR.No</th><th rowspan="2">Supplier GSTIN</th><th rowspan="2">Trade Name</th><th rowspan="2">POS</th><th rowspan="2">Taxable Value</th><th colspan="4" class="text-center">Amount Of Tax</th></tr>
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
//$('#filing_option').html('Yearly');
$('.retutprdntxt').text('Financial Year:');
$('#datetimepicker').val('03-2019');

$("#gstr4FinancialYear").val('${year}');
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
		var pUrl = "${contextPath}/ihubsavestatus/${id}/${usertype}/${client.id}/${returntype}?month=" + month + "&year=" + year + "&invoices=" + invArray;
		$.ajax({
			type: "GET",
			url: pUrl,
			async: false,
			cache: false,
			dataType:"json",
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
	$('#gstr4FinancialYear').change(function() {
		var gstr4FinancialYear = $(this).val(); 
		window.location.href = '${contextPath}/addGSTR4invoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/03/'+gstr4FinancialYear;
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
	function autocalculategstr4(){
		$('#gstr4FinancialYear').val();
		otpExpiryCheck();
		window.location.href = '${contextPath}/getpopulategstr4annualdata'+commonSuffix+'/'+clientId+'/03/'+year;
		if(otpExpirycheck == 'OTP_VERIFIED'){
			window.location.href = '${contextPath}/getpopulategstr4annualdata'+commonSuffix+'/'+clientId+'/GSTR4Annual/03/'+year;
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