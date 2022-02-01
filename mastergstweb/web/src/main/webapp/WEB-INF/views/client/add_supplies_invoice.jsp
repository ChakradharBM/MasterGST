<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Upload GSTR3B | File GSTR3B</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/gstr3b.css" media="all" />
<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
<style>
#otpboxwrap .formbox { padding:50px; }
#otpboxwrap .formbox .btn-blue-dark { margin-left:10px }
.otp_form_input { margin:30px auto 40px; }
.otp_form_input input[type='text'] { border-radius:0; border: none; border-bottom: 3px solid #4664be; width: 20%; display: inline-block; margin-left: 7px; text-align: center; font-size:50px ; margin-bottom:15px	}
.cashledger_row{border: 1px solid lightgray;background-color:white;min-height:100px;box-shadow: 0 0 2px 2px #eff3f6;margin-left: 2px;margin-right: 2px;border-radius: 10px;margin-bottom: 36px!important;}
.cashledger_summary_totals , .creditledger_summary_totals {background-color: white;}
.cashledger_summary_totals .normaltable-col.hdr , .creditledger_summary_totals .normaltable-col.hdr {width: 3%;}
</style>
<script>
    $(document).ready(function(){
        // Add minus icon for collapse element which is open by default
        $(".collapse.in").each(function(){
        	$(this).siblings(".panel-heading").find(".fa").addClass("fa-minus").removeClass("fa-plus");
			$(this).parent().find(".panel-heading").addClass("active");
	    });
        $('.panel-collapse').on('show.bs.collapse', function (e) {
			$(e.target).closest('.panel').siblings().find('.panel-collapse').collapse('hide');
		});
        // Toggle plus minus icon on show hide of collapse element
        $(".collapse").on('show.bs.collapse', function(){
        	$(this).parent().find(".fa").removeClass("fa-plus").addClass("fa-minus");
			$(this).parent().find(".panel-heading").addClass("active");
        }).on('hide.bs.collapse', function(){
        	$(this).parent().find(".fa").removeClass("fa-minus").addClass("fa-plus");
			$(this).parent().find(".panel-heading").removeClass("active");
        });
    });
</script>
<script type="text/javascript">
	var dbFilingTable, gstSummary=null, indexObj = new Object(), tableObj = new Object();
	var ipAddress = '', uploadResponse;var otpExpirycheck;var impgigst;
	$(function () {
		$(".tpone-input, .tptwo-input, .tpthree-input, .tpfour-input, .tpfive-input, .tpcheck-input").attr('readonly', true);
		$('.tpone-save, .tpone-cancel,.tptwo-save, .tptwo-cancel,.tpthree-save, .tpthree-cancel,.tpfour-save, .tpfour-cancel,.tpfive-save, .tpfive-cancel, .addmorewrap').hide();
	
		indexObj['unregDetails'] = parseInt("<c:choose><c:when test='${not empty invoice && not empty invoice.interSup.unregDetails}'>${fn:length(invoice.interSup.unregDetails)}</c:when><c:otherwise>1</c:otherwise></c:choose>");
		indexObj['compDetails'] = parseInt("<c:choose><c:when test='${not empty invoice && not empty invoice.interSup.compDetails}'>${fn:length(invoice.interSup.compDetails)}</c:when><c:otherwise>1</c:otherwise></c:choose>");
		indexObj['uinDetails'] = parseInt("<c:choose><c:when test='${not empty invoice && not empty invoice.interSup.uinDetails}'>${fn:length(invoice.interSup.uinDetails)}</c:when><c:otherwise>1</c:otherwise></c:choose>");

		$(".otp_form_input .invoice_otp").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next().next('.form-control').focus();
			}
		});
		var submitStatus='<c:out value="${invoice.submitStatus}"/>';
		if(submitStatus == 'true') {
			$('#idTrueCopyBtn').removeClass('disable');
		}

		$('#nav-client').addClass('active');

		$('.elg_itc,.elg_itc1').on( "change", function() {
			var id = $(this).attr('id');
			updateElgItcLogic(id);
		});
		function forceNumeric(){
			var $input = $(this);
			$input.val($input.val().replace(/[^\d.,]+/g,''));
		}
		$('#supForm,#sup4Form').on('propertychange input', 'input.form-control', forceNumeric);
		
		var unregDetailsState = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-unregDetails_pos ul").children().length == 0) {
						$("#unregDetails_posempty").show();
					} else {
						$("#unregDetails_posempty").hide();
					}
				}
			}
		};
		$("#unregDetails_pos").easyAutocomplete(unregDetailsState);
		var compDetailsState = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-compDetails_pos ul").children().length == 0) {
						$("#compDetails_posempty").show();
					} else {
						$("#compDetails_posempty").hide();
					}
				}
			}
		};
		$("#compDetails_pos").easyAutocomplete(compDetailsState);
		var uinDetailsState = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-uinDetails_pos ul").children().length == 0) {
						$("#uinDetails_posempty").show();
					} else {
						$("#uinDetails_posempty").hide();
					}
				}
			}
		};
		$("#uinDetails_pos").easyAutocomplete(uinDetailsState);
		var interSupState = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-interSup_pos ul").children().length == 0) {
						$("#interSup_posempty").show();
					} else {
						$("#interSup_posempty").hide();
					}
				}
			}
		};
		$("#interSup_pos").easyAutocomplete(interSupState);

		var date = $('#dateofinvoice').datepicker({
			format: 'dd/mm/yyyy'
		}).on('changeDate', function(ev) {
			$('#dateofinvoice').datepicker('setValue', ev.date);
			//$('.datepicker').hide();
			$('#dateofinvoice').validator('update');
		});

		tableObj['unregDetails'] = $('#unregDetailsTable').DataTable({
			dom: 'Bfrtip',
			"searching": false,
			"pageLength": 5,
			"language": {
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		dbFilingTable = $('#dbFilingTable').DataTable({
            "dom": '<"toolbar">frtip',
            "paging": true,
			"pageLength": 25,
            "searching": false,
            "responsive": true,
			"columnDefs": [
			  { className: "dt-right", "targets": [4,5,6,7,8] }
			]
        });
		$(".tabtable3  div.toolbar").html('<h4>GSTR3B Filing Summary Of '+monthNames[${month}-1]+' ${year}</h4> <c:if test="${client.status ne statusFiled}"><a href="#" class="btn btn-greendark permissionFile_Invoice"  data-toggle="modal" data-target="#fileReturnModal" id="idTrueCopyBtn">File GSTR3B with DSC</a>  <a href="#" class="btn btn-greendark permissionFile_Invoice" onclick="evcFilingOTP()" id="idEVCBtn">File GSTR3B with EVC</a></c:if><button class="btn btn-primary" style="color:white; box-shadow:none;" onclick="fetchRetSummary(false)">Refresh<i class="fa fa-refresh" id="refreshSummary" style="font-size: 15px; color: #fff; margin-left:5px"></i></button>');

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
	function addInterSupplyRecord() {
		var selectedOption = $("#interSup_type").val();
		var index = indexObj[selectedOption];
		if($("#interSup_pos").val() != '') {
			var suptype = $("#interSup_type option:selected").text();
			var supplytype = "";
			if(suptype == 'Supplies made to unregistered persons'){
				supplytype = "A."+suptype;
			}else if(suptype == 'Supplies made to composition taxable persons'){
				supplytype = "B."+suptype;
			}else if(suptype == 'Supplies made to UIN holders'){
				supplytype = "C."+suptype;
			}
			$('#interSupTblBody').append('<tr id="'+(selectedOption+index)+'"><td class="text-left">'+supplytype+'</td><td class="text-left"><input type="text" class="tptwo-input form-control" readonly="true" value="'+$("#interSup_pos").val()+'" /></td><td class="text-left"><input type="text" class="tptwo-input form-control" readonly="true" value="'+$("#interSup_txval").val()+'" /></td><td class="text-left"><input type="text" class="tptwo-input form-control" readonly="true" value="'+$("#interSup_iamt").val()+'" /></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteItem(\'interSup\',\''+selectedOption+'\','+index+')" class="delrow"></td></tr>');
			$("form[name='salesinvoceform']").append('<input type="hidden" name="interSup.'+selectedOption+'['+index+'].pos" value="'+$("#interSup_pos").val()+'">');
			$("form[name='salesinvoceform']").append('<input type="hidden" name="interSup.'+selectedOption+'['+index+'].txval" value="'+$("#interSup_txval").val()+'">');
			$("form[name='salesinvoceform']").append('<input type="hidden" name="interSup.'+selectedOption+'['+index+'].iamt" value="'+$("#interSup_iamt").val()+'">');
		}
		$("#interSup_type").val('');
		$("#interSup_pos").val('');
		$("#interSup_txval").val('');
		$("#interSup_iamt").val('');
		indexObj[selectedOption]++;
		$('form[name="salesinvoceform"]').validator('update');
	}
	function deleteItem(supType, dType, index) {
		$('.gstr3bform input[type="hidden"]').each(function() {
			var name = $(this).attr('name');
			if(name){
				if(name.indexOf(supType+"."+dType+"["+index+"]") >= 0) {
					$(this).remove();
				}
			}
		});
		//tableObj[dType].row('#'+dType+index).remove().draw(false);
		$('#'+dType+index).remove();
	}
	function chkLiability() {
		$('.tpcheck-input').prop("readonly", !document.getElementById('chkBoxLiab').checked);
	}
	function clickEdit(a,b,c,d,e){
		$(a).show();
		$(b).show();
		$(c).hide();
		$(d).attr('readonly', false);
		$(d).addClass('tpone-input-edit');
		$(e).show();
	}
	function clickSave(a,b,c,d,e){
		$(a).hide();
		$(b).hide();
		$(c).show();
		$(d).attr('readonly', true);
		$(d).addClass('tpone-input-edit');
		$(e).hide();
		var formObj;
		if(a == '.tpseven-save'){
			formObj = document.getElementById('sup4Form');
		}else{
			formObj = document.getElementById('supForm');
		}
		var formURL = formObj.action;
		var formData = new FormData(formObj);
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
	function invokeOffsetLiab() {
		otpExpiryCheck();
		if(otpExpirycheck == "OTP_VERIFIED"){
			$.ajax({
				url: "${contextPath}/ihubsaveoffliab/${id}/${client.id}/${returntype}/${invoice.id}?month=" + month + "&year=" + year,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response.error.message) {
						errorNotification(response.error.message);
					}
					if(response.message) {
						successNotification(response.message);
					}
				},error: function(e, status, error) {
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}else{
			errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}

	function submitReturns() {
		otpExpiryCheck();
		if(otpExpirycheck == "OTP_VERIFIED"){
			var month = '<c:out value="${month}"/>';
			var year = '<c:out value="${year}"/>';
			$.ajax({
				url: "${contextPath}/retsubmit/${id}/${client.id}/${returntype}/${month}/${year}",
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response.status_cd == '1') {
						$('#idTrueCopyBtn').removeClass('disable');
						successNotification('Submit returns successful!');
					} else if(response.error && response.error.message) {
						errorNotification(response.error.message);
						if(response.error.message == 'You already have Submitted/Filed For Current Return Period') {
							$('#idTrueCopyBtn').removeClass('disable');
						}
					} else if(response.status_cd == '0') {
						if(response.status_desc == 'OTP verification is not yet completed!' 
							|| response.status_desc == 'Invalid Session'
							|| response.status_desc == 'Unauthorized User!'  || response.status_desc == 'Missing Mandatory Params' || response.status_desc == 'API Authorization Failed') {
							errorNotification(response.status_desc+'. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
						} else if(response.status_desc == 'You already have Submitted/Filed For Current Return Period') {
							$('#idTrueCopyBtn').removeClass('disable');
							errorNotification(response.status_desc);
						} else {
							errorNotification(response.status_desc);
						}
					}
				},
				error: function(e, status, error) {
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}else{
			errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}
	function fetchRetSummary(acceptFlag) {
		otpExpiryCheck();
		if(otpExpirycheck == 'OTP_VERIFIED'){
			if(acceptFlag == false){
				$('#refreshSummary').addClass('fa-spin');
			}
			successNotification('Loading Summary information. Please wait..!');
			var month = '<c:out value="${month}"/>';
			var year = '<c:out value="${year}"/>';
			$.ajax({
				url: "${contextPath}/ihubretsum/${id}/${client.id}/${returntype}?month="+month+"&year="+year,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					closeNotifications();
					if(acceptFlag == false){
						$('#refreshSummary').removeClass('fa-spin');
					}
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
					} else if(response.data) {
						//$('#idFilingBtn').removeClass('disable');
						//$('#idFilingKYCBtn').removeClass('disable');
						gstSummary = response;
						dbFilingTable.clear().draw();
						if(gstSummary.data.sup_details) {
							if(gstSummary.data.sup_details.osup_det) {
								if(gstSummary.data.sup_details.osup_det.txval == undefined || gstSummary.data.sup_details.osup_det.txval == null) {
									gstSummary.data.sup_details.osup_det.txval = '';
								}
								if(gstSummary.data.sup_details.osup_det.iamt == undefined || gstSummary.data.sup_details.osup_det.iamt == null) {
									gstSummary.data.sup_details.osup_det.iamt = '';
								}
								if(gstSummary.data.sup_details.osup_det.camt == undefined || gstSummary.data.sup_details.osup_det.camt == null) {
									gstSummary.data.sup_details.osup_det.camt = '';
								}
								if(gstSummary.data.sup_details.osup_det.samt == undefined || gstSummary.data.sup_details.osup_det.samt == null) {
									gstSummary.data.sup_details.osup_det.samt = '';
								}
								if(gstSummary.data.sup_details.osup_det.csamt == undefined || gstSummary.data.sup_details.osup_det.csamt == null) {
									gstSummary.data.sup_details.osup_det.csamt = '';
								}
								dbFilingTable.row.add(['Tax on outward and revese charge inward supplies','Outward taxable supplies','','',
									'<span class="ind_formats">'+gstSummary.data.sup_details.osup_det.txval+'</span>',
									'<span class="ind_formats">'+gstSummary.data.sup_details.osup_det.iamt+'</span>',
									'<span class="ind_formats">'+gstSummary.data.sup_details.osup_det.camt+'</span>',
									'<span class="ind_formats">'+gstSummary.data.sup_details.osup_det.samt+'</span>',
									'<span class="ind_formats">'+gstSummary.data.sup_details.osup_det.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formats'
									});
							}
							if(gstSummary.data.sup_details.osup_zero) {
								if(gstSummary.data.sup_details.osup_zero.txval == undefined || gstSummary.data.sup_details.osup_zero.txval == null) {
									gstSummary.data.sup_details.osup_zero.txval = '';
								}
								if(gstSummary.data.sup_details.osup_zero.iamt == undefined || gstSummary.data.sup_details.osup_zero.iamt == null) {
									gstSummary.data.sup_details.osup_zero.iamt = '';
								}
								if(gstSummary.data.sup_details.osup_zero.camt == undefined || gstSummary.data.sup_details.osup_zero.camt == null) {
									gstSummary.data.sup_details.osup_zero.camt = '';
								}
								if(gstSummary.data.sup_details.osup_zero.samt == undefined || gstSummary.data.sup_details.osup_zero.samt == null) {
									gstSummary.data.sup_details.osup_zero.samt = '';
								}
								if(gstSummary.data.sup_details.osup_zero.csamt == undefined || gstSummary.data.sup_details.osup_zero.csamt == null) {
									gstSummary.data.sup_details.osup_zero.csamt = '';
								}
								dbFilingTable.row.add(['Tax on outward and revese charge inward supplies','Outward taxable supplies (zero rated)','','',
									'<span class="ind_formatzr">'+gstSummary.data.sup_details.osup_zero.txval+'</span>',
									'<span class="ind_formatzr">'+gstSummary.data.sup_details.osup_zero.iamt+'</span>',
									'<span class="ind_formatzr">'+gstSummary.data.sup_details.osup_zero.camt+'</span>',
									'<span class="ind_formatzr">'+gstSummary.data.sup_details.osup_zero.samt+'</span>',
									'<span class="ind_formatzr">'+gstSummary.data.sup_details.osup_zero.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatzr'
									});
							}
							if(gstSummary.data.sup_details.osup_nil_exmp) {
								if(gstSummary.data.sup_details.osup_nil_exmp.txval == undefined || gstSummary.data.sup_details.osup_nil_exmp.txval == null) {
									gstSummary.data.sup_details.osup_nil_exmp.txval = '';
								}
								if(gstSummary.data.sup_details.osup_nil_exmp.iamt == undefined || gstSummary.data.sup_details.osup_nil_exmp.iamt == null) {
									gstSummary.data.sup_details.osup_nil_exmp.iamt = '';
								}
								if(gstSummary.data.sup_details.osup_nil_exmp.camt == undefined || gstSummary.data.sup_details.osup_nil_exmp.camt == null) {
									gstSummary.data.sup_details.osup_nil_exmp.camt = '';
								}
								if(gstSummary.data.sup_details.osup_nil_exmp.samt == undefined || gstSummary.data.sup_details.osup_nil_exmp.samt == null) {
									gstSummary.data.sup_details.osup_nil_exmp.samt = '';
								}
								if(gstSummary.data.sup_details.osup_nil_exmp.csamt == undefined || gstSummary.data.sup_details.osup_nil_exmp.csamt == null) {
									gstSummary.data.sup_details.osup_nil_exmp.csamt = '';
								}
								dbFilingTable.row.add(['Tax on outward and revese charge inward supplies','Other outward supplies (Nil rated, exempted)','','',
									'<span class="ind_formatnr">'+gstSummary.data.sup_details.osup_nil_exmp.txval+'</span>',
									'<span class="ind_formatnr">'+gstSummary.data.sup_details.osup_nil_exmp.iamt+'</span>',
									'<span class="ind_formatnr">'+gstSummary.data.sup_details.osup_nil_exmp.camt+'</span>',
									'<span class="ind_formatnr">'+gstSummary.data.sup_details.osup_nil_exmp.samt+'</span>',
									'<span class="ind_formatnr">'+gstSummary.data.sup_details.osup_nil_exmp.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatnr'
									});
							}
							if(gstSummary.data.sup_details.isup_rev) {
								if(gstSummary.data.sup_details.isup_rev.txval == undefined || gstSummary.data.sup_details.isup_rev.txval == null) {
									gstSummary.data.sup_details.isup_rev.txval = '';
								}
								if(gstSummary.data.sup_details.isup_rev.iamt == undefined || gstSummary.data.sup_details.isup_rev.iamt == null) {
									gstSummary.data.sup_details.isup_rev.iamt = '';
								}
								if(gstSummary.data.sup_details.isup_rev.camt == undefined || gstSummary.data.sup_details.isup_rev.camt == null) {
									gstSummary.data.sup_details.isup_rev.camt = '';
								}
								if(gstSummary.data.sup_details.isup_rev.samt == undefined || gstSummary.data.sup_details.isup_rev.samt == null) {
									gstSummary.data.sup_details.isup_rev.samt = '';
								}
								if(gstSummary.data.sup_details.isup_rev.csamt == undefined || gstSummary.data.sup_details.isup_rev.csamt == null) {
									gstSummary.data.sup_details.isup_rev.csamt = '';
								}
								dbFilingTable.row.add(['Tax on outward and revese charge inward supplies','Inward supplies','','',
									'<span class="ind_formatis">'+gstSummary.data.sup_details.isup_rev.txval+'</span>',
									'<span class="ind_formatis">'+gstSummary.data.sup_details.isup_rev.iamt+'</span>',
									'<span class="ind_formatis">'+gstSummary.data.sup_details.isup_rev.camt+'</span>',
									'<span class="ind_formatis">'+gstSummary.data.sup_details.isup_rev.samt+'</span>',
									'<span class="ind_formatis">'+gstSummary.data.sup_details.isup_rev.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatis'
									});
							}
							if(gstSummary.data.sup_details.osup_nongst) {
								if(gstSummary.data.sup_details.osup_nongst.txval == undefined || gstSummary.data.sup_details.osup_nongst.txval == null) {
									gstSummary.data.sup_details.osup_nongst.txval = '';
								}
								if(gstSummary.data.sup_details.osup_nongst.iamt == undefined || gstSummary.data.sup_details.osup_nongst.iamt == null) {
									gstSummary.data.sup_details.osup_nongst.iamt = '';
								}
								if(gstSummary.data.sup_details.osup_nongst.camt == undefined || gstSummary.data.sup_details.osup_nongst.camt == null) {
									gstSummary.data.sup_details.osup_nongst.camt = '';
								}
								if(gstSummary.data.sup_details.osup_nongst.samt == undefined || gstSummary.data.sup_details.osup_nongst.samt == null) {
									gstSummary.data.sup_details.osup_nongst.samt = '';
								}
								if(gstSummary.data.sup_details.osup_nongst.csamt == undefined || gstSummary.data.sup_details.osup_nongst.csamt == null) {
									gstSummary.data.sup_details.osup_nongst.csamt = '';
								}
								dbFilingTable.row.add(['Tax on outward and revese charge inward supplies','Non-GST outward supplies','','',
									'<span class="ind_formatnos">'+gstSummary.data.sup_details.osup_nongst.txval+'</span>',
									'<span class="ind_formatnos">'+gstSummary.data.sup_details.osup_nongst.iamt+'</span>',
									'<span class="ind_formatnos">'+gstSummary.data.sup_details.osup_nongst.camt+'</span>',
									'<span class="ind_formatnos">'+gstSummary.data.sup_details.osup_nongst.samt+'</span>',
									'<span class="ind_formatnos">'+gstSummary.data.sup_details.osup_nongst.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatnos'
									});
							}
						}
						if(gstSummary.data.inter_sup) {
							if(gstSummary.data.inter_sup.unreg_details) {
								gstSummary.data.inter_sup.unreg_details.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.iamt == undefined || record.iamt == null) {
										record.iamt = '';
									}
									if(record.camt == undefined || record.camt == null) {
										record.camt = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Inter-state supplies','Supplies made to unregistered persons',record.pos,'',
									'<span class="ind_formatsup">'+record.txval+'</span>',
									'<span class="ind_formatsup">'+record.iamt+'</span>',
									'<span class="ind_formatsup">'+record.camt+'</span>',
									'<span class="ind_formatsup">'+record.samt+'</span>',
									'<span class="ind_formatsup">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatsup'
									});
								});
							}
							if(gstSummary.data.inter_sup.comp_details) {
								gstSummary.data.inter_sup.comp_details.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.iamt == undefined || record.iamt == null) {
										record.iamt = '';
									}
									if(record.camt == undefined || record.camt == null) {
										record.camt = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Inter-state supplies','Supplies made to composition taxable persons',record.pos,'',
									'<span class="ind_formatsctp">'+record.txval+'</span>',
									'<span class="ind_formatsctp">'+record.iamt+'</span>',
									'<span class="ind_formatsctp">'+record.camt+'</span>',
									'<span class="ind_formatsctp">'+record.samt+'</span>',
									'<span class="ind_formatsctp">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatsctp'
									});
								});
							}
							if(gstSummary.data.inter_sup.uin_details) {
								gstSummary.data.inter_sup.uin_details.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.iamt == undefined || record.iamt == null) {
										record.iamt = '';
									}
									if(record.camt == undefined || record.camt == null) {
										record.camt = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Inter-state supplies','Supplies made to UIN holders',record.pos,'',
									'<span class="ind_formatscuh">'+record.txval+'</span>',
									'<span class="ind_formatscuh">'+record.iamt+'</span>',
									'<span class="ind_formatscuh">'+record.camt+'</span>',
									'<span class="ind_formatscuh">'+record.samt+'</span>',
									'<span class="ind_formatscuh">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatscuh'
									});
								});
							}
						}
						if(gstSummary.data.itc_elg) {
							if(gstSummary.data.itc_elg.itc_avl) {
								gstSummary.data.itc_elg.itc_avl.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.ty == undefined || record.ty == null) {
										record.ty = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.iamt == undefined || record.iamt == null) {
										record.iamt = '';
									}
									if(record.camt == undefined || record.camt == null) {
										record.camt = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Eligible ITC','ITC available',record.pos,record.ty,
									'<span class="ind_formateitc">'+record.txval+'</span>',
									'<span class="ind_formateitc">'+record.iamt+'</span>',
									'<span class="ind_formateitc">'+record.camt+'</span>',
									'<span class="ind_formateitc">'+record.samt+'</span>',
									'<span class="ind_formateitc">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formateitc'
									});
								});
							}
							if(gstSummary.data.itc_elg.itc_rev) {
								gstSummary.data.itc_elg.itc_rev.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.ty == undefined || record.ty == null) {
										record.ty = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.iamt == undefined || record.iamt == null) {
										record.iamt = '';
									}
									if(record.camt == undefined || record.camt == null) {
										record.camt = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Eligible ITC','ITC Reversed',record.pos,record.ty,
									'<span class="ind_formatitcr">'+record.txval+'</span>',
									'<span class="ind_formatitcr">'+record.iamt+'</span>',
									'<span class="ind_formatitcr">'+record.camt+'</span>',
									'<span class="ind_formatitcr">'+record.samt+'</span>',
									'<span class="ind_formatitcr">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatitcr'
									});
								});
							}
							if(gstSummary.data.itc_elg.itc_net) {
								if(gstSummary.data.itc_elg.itc_net.pos == undefined || gstSummary.data.itc_elg.itc_net.pos == null) {
									gstSummary.data.itc_elg.itc_net.pos = '';
								}
								if(gstSummary.data.itc_elg.itc_net.ty == undefined || gstSummary.data.itc_elg.itc_net.ty == null) {
									gstSummary.data.itc_elg.itc_net.ty = '';
								}
								if(gstSummary.data.itc_elg.itc_net.txval == undefined || gstSummary.data.itc_elg.itc_net.txval == null) {
									gstSummary.data.itc_elg.itc_net.txval = '';
								}
								if(gstSummary.data.itc_elg.itc_net.iamt == undefined || gstSummary.data.itc_elg.itc_net.iamt == null) {
									gstSummary.data.itc_elg.itc_net.iamt = '';
								}
								if(gstSummary.data.itc_elg.itc_net.camt == undefined || gstSummary.data.itc_elg.itc_net.camt == null) {
									gstSummary.data.itc_elg.itc_net.camt = '';
								}
								if(gstSummary.data.itc_elg.itc_net.samt == undefined || gstSummary.data.itc_elg.itc_net.samt == null) {
									gstSummary.data.itc_elg.itc_net.samt = '';
								}
								if(gstSummary.data.itc_elg.itc_net.csamt == undefined || gstSummary.data.itc_elg.itc_net.csamt == null) {
									gstSummary.data.itc_elg.itc_net.csamt = '';
								}
								dbFilingTable.row.add(['Eligible ITC','Net ITC available',gstSummary.data.itc_elg.itc_net.pos,
									gstSummary.data.itc_elg.itc_net.ty,
									'<span class="ind_formatitcr">'+gstSummary.data.itc_elg.itc_net.txval+'</span>',
									'<span class="ind_formatitcr">'+gstSummary.data.itc_elg.itc_net.iamt+'</span>',
									'<span class="ind_formatitcr">'+gstSummary.data.itc_elg.itc_net.camt+'</span>',
									'<span class="ind_formatitcr">'+gstSummary.data.itc_elg.itc_net.samt+'</span>',
									'<span class="ind_formatitcr">'+gstSummary.data.itc_elg.itc_net.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatitcr'
									});
							}
							if(gstSummary.data.itc_elg.itc_inelg) {
								gstSummary.data.itc_elg.itc_inelg.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.ty == undefined || record.ty == null) {
										record.ty = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.iamt == undefined || record.iamt == null) {
										record.iamt = '';
									}
									if(record.camt == undefined || record.camt == null) {
										record.camt = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Eligible ITC','Ineligible ITC',record.pos,record.ty,
									'<span class="ind_formatitcie">'+record.txval+'</span>',
									'<span class="ind_formatitcie">'+record.iamt+'</span>',
									'<span class="ind_formatitcie">'+record.camt+'</span>',
									'<span class="ind_formatitcie">'+record.samt+'</span>',
									'<span class="ind_formatitcie">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatitcie'
									});
								});
							}
						}
						if(gstSummary.data.inward_sup) {
							if(gstSummary.data.inward_sup.isup_details) {
								gstSummary.data.inward_sup.isup_details.forEach(function(record) {
									if(record.pos == undefined || record.pos == null) {
										record.pos = '';
									}
									if(record.ty == undefined || record.ty == null) {
										record.ty = '';
									}
									if(record.txval == undefined || record.txval == null) {
										record.txval = '';
									}
									if(record.intra == undefined || record.intra == null) {
										record.intra = '';
									}
									if(record.inter == undefined || record.inter == null) {
										record.inter = '';
									}
									if(record.samt == undefined || record.samt == null) {
										record.samt = '';
									}
									if(record.csamt == undefined || record.csamt == null) {
										record.csamt = '';
									}
									dbFilingTable.row.add(['Exempt, nil and Non GST inward suplies','',record.pos,record.ty,
									'<span class="ind_formatnis">'+record.txval+'</span>',
									'<span class="ind_formatnis">'+record.intra+'</span>',
									'<span class="ind_formatnis">'+record.inter+'</span>',
									'<span class="ind_formatnis">'+record.samt+'</span>',
									'<span class="ind_formatnis">'+record.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatnis'
									});
								});
							}
						}
						if(gstSummary.data.intr_ltfee) {
							if(gstSummary.data.intr_ltfee.intr_details) {
								if(gstSummary.data.intr_ltfee.intr_details.pos == undefined || gstSummary.data.intr_ltfee.intr_details.pos == null) {
									gstSummary.data.intr_ltfee.intr_details.pos = '';
								}
								if(gstSummary.data.intr_ltfee.intr_details.ty == undefined || gstSummary.data.intr_ltfee.intr_details.ty == null) {
									gstSummary.data.intr_ltfee.intr_details.ty = '';
								}
								if(gstSummary.data.intr_ltfee.intr_details.txval == undefined || gstSummary.data.intr_ltfee.intr_details.txval == null) {
									gstSummary.data.intr_ltfee.intr_details.txval = '';
								}
								if(gstSummary.data.intr_ltfee.intr_details.iamt == undefined || gstSummary.data.intr_ltfee.intr_details.iamt == null) {
									gstSummary.data.intr_ltfee.intr_details.iamt = '';
								}
								if(gstSummary.data.intr_ltfee.intr_details.camt == undefined || gstSummary.data.intr_ltfee.intr_details.camt == null) {
									gstSummary.data.intr_ltfee.intr_details.camt = '';
								}
								if(gstSummary.data.intr_ltfee.intr_details.samt == undefined || gstSummary.data.intr_ltfee.intr_details.samt == null) {
									gstSummary.data.intr_ltfee.intr_details.samt = '';
								}
								if(gstSummary.data.intr_ltfee.intr_details.csamt == undefined || gstSummary.data.intr_ltfee.intr_details.csamt == null) {
									gstSummary.data.intr_ltfee.intr_details.csamt = '';
								}
								dbFilingTable.row.add(['Interest and Late fee','Interest',gstSummary.data.intr_ltfee.intr_details.pos,
									gstSummary.data.intr_ltfee.intr_details.ty,
									'<span class="ind_formatlfee">'+gstSummary.data.intr_ltfee.intr_details.txval+'</span>',
									'<span class="ind_formatlfee">'+gstSummary.data.intr_ltfee.intr_details.iamt+'</span>',
									'<span class="ind_formatlfee">'+gstSummary.data.intr_ltfee.intr_details.camt+'</span>',
									'<span class="ind_formatlfee">'+gstSummary.data.intr_ltfee.intr_details.samt+'</span>',
									'<span class="ind_formatlfee">'+gstSummary.data.intr_ltfee.intr_details.csamt+'</span>']).draw(false);
									OSREC.CurrencyFormatter.formatAll({
										selector: '.ind_formatlfee'
									});
							}
						}
					}
				},
				error : function(e, status, error) {
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}else{
			errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}
	function trueCopyFiling() {
		$.ajax({
			url : '${contextPath}/truecopy/${id}/${client.id}/${returntype}/${month}/${year}',
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(data) {
				$("form[name='certlistForm']").append('<input type="hidden" name="authstr" value="'+data.authstr+'">');
				$("form[name='certlistForm']").append('<input type="hidden" name="cs" value="'+data.cs+'">');
				$("form[name='certlistForm']").append('<input type="hidden" name="dscpin" value="">');
				
				$('#certlistForm').ajaxSubmit( {
					url: 'https://localhost:8099/certlist',
					dataType: 'json',
					type: 'POST',
					cache: false,
					success: function(certResponse) {
						if(certResponse && certResponse.certlist && certResponse.certlist.length > 0) {
							successNotification('Certificate verification successful!');
							$("form[name='certsignForm']").append('<input type="hidden" name="authstr" value="'+data.authstr+'">');
							$("form[name='certsignForm']").append('<input type="hidden" name="cs" value="'+data.cs+'">');
							$("form[name='certsignForm']").append('<input type="hidden" name="certname" value="'+certResponse.certlist[certResponse.certlist.length-1]+'">');
							$("form[name='certsignForm']").append('<input type="hidden" name="dscpin" value="">');
							$("form[name='certsignForm']").append('<input type="hidden" name="hash" value="'+data.hash256+'">');

							$('#certsignForm').ajaxSubmit({
								dataType : 'json',
								url : 'https://localhost:8099/sign',
								cache : false,
								success: function(signResponse) {
									signResponse.content = data.content;
									successNotification('Signing successful!');
									$.ajax({
										type : "POST",
										url: "${contextPath}/retfile/${id}/${client.id}/${returntype}/${month}/${year}",
										data : JSON.stringify(signResponse),
										async: false,
										cache: false,
										dataType:"json",
										contentType: 'application/json',
										success : function(retResponse) {
											if(retResponse.error && retResponse.error.message) {
												errorNotification(retResponse.error.message);
											} else if(retResponse.status_cd == '0') {
												errorNotification(retResponse.status_desc);
											} else {
												successNotification('Return filing successful!');
											}
										},
										error: function(e) {
											if(e.responseText) {
												errorNotification(e.responseText);
											}
										}
									});
								}
							});
						} else {
							errorNotification("Please check the status of your DSC software and ePass key");
						}
					},
					error: function(e) {
						errorNotification("Please check the status of your DSC software and ePass key");
					}
				});
			},
			error : function(e) {
				if(e.responseText) {
					errorNotification(e.responseText);
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
						gstSummary = null;
						successNotification('Upload GSTR3B completed successfully!');
						//$('#idReturnSubmitBtn').removeClass('disable');
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
			errorNotification('Your OTP Session Expired, Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}
	function updateElgItcLogic(id) {
		var suffix = id.substring(id.length - 5);
		var aVal = 0, bVal = 0;
		$('.elg_itc').each(function() {
			if($(this).attr('id').indexOf(suffix) > 0) {
				if($(this).val()) {
					aVal+=parseFloat($(this).val());
				}
			}
		});
		$('.elg_itc1').each(function() {
			if($(this).attr('id').indexOf(suffix) > 0) {
				if($(this).val()) {
					bVal+=parseFloat($(this).val());
				}
			}
		});
		var cId = 'itcElg_itcNet';
		if(suffix.indexOf('_') >= 0) {
			cId += suffix;
		} else {
			cId += '_'+suffix;
		}
		$('#'+cId).val(aVal-bVal);
	}
	function updateField(value, fieldId) {
		if($('#'+fieldId).val() == 0 && value > 0) {
			$('#'+fieldId).val(value);
			updateElgItcLogic(fieldId);
		}
	}
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
			error : function(e, status, error) {
				if(e.responseText) {
					$('#idClientError').html(e.responseText);
				}
			}
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
					if(authResponse.status_cd == '1'){
						closeNotifications();
						$('#downloadOtpModalClose').click();
					}else{
						$('#dotp_Msg').text('Please Enter Valid Otp').css('display','block');
					}
				},
				error : function(e, status, error) {
					$('#downloadOtpModalClose').click();
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}
	}
	function otpTryAgain(){
		$('#dotp_Msg').text('').css("display","none");
		$("#dwnldOtpEntryForm")[0].reset();
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
			error : function(e, status, error) {
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
					<span class="datetimetxt"> 
						<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
					</span>
					<span class="retutprdntxt">
						Return Period: 
					</span>
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
						<div class="pull-right helpguide" data-toggle="modal" data-target="#helpGuideModal"> Help To File GSTR3B</div>
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">
								<a class="nav-link permissionGSTR3B-GSTR3B_Invoices active" data-toggle="tab" href="#gtab1" role="tab">GSTR3B INVOICES</a>
							</li>
							<li class="nav-item">
								<a class="nav-link permissionGSTR3B-TAX_Payment" data-toggle="tab" href="#gtab3" role="tab">Tax Payment</a>
							</li>
							<li class="nav-item">
								<a class="nav-link permissionGSTR3B-Offset_Liability" data-toggle="tab" href="#gtab4" <c:if test="${empty client.status || client.status eq 'Pending'}">onclick="populateoffsetliability()" </c:if> role="tab">Offset Liability</a>
							</li>
							<li class="nav-item">
								<a class="nav-link permissionGSTR3B-Filing_GSTR3B" data-toggle="tab" href="#gtab2" role="tab" onclick="fetchRetSummary(true)">FILING GSTR3B</a>
							</li>
						</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<!-- Tab panes 1-->
					<div class="tab-pane active" id="gtab1" role="tabpane1">

				<form:form method="POST" id="supForm" data-toggle="validator" class="meterialform invoiceform gstr3bform" name="salesinvoceform" action="${contextPath}/savesupinvoice/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
				<div class="col-md-12 col-sm-12">
				<!-- <input type="submit" class="btn btn-blue-dark btn-sm pull-right ml-2" value="<c:choose><c:when test='${not empty invoice && not empty invoice.id}'>Update</c:when><c:otherwise>Add</c:otherwise></c:choose> Invoice"/>
				<a href="#" class="btn btn-greendark permissionSubmit_Invoice pull-right ml-2" onclick="showSubmitPopup()" id="idReturnSubmitBtn">Submit GSTR3B</a> -->
				<span class="text-right" style="float: left;margin-top: 10px;font-size:18px; font-weight:bold">Filing Status : 
					<span style="font-size:16px; margin-left:0px!important">
						<c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">
						<span class="color-green status-style">${client.status}</span>
						</c:if>
						<c:if test="${client.status eq statusPending || empty client.status}">
						<span class="color-yellow pen-style">Pending</span>
						</c:if>
					</span>
				</span>
				<a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark permissionGSTR3B-GSTR3B_Invoices permissionUpload_Invoice pull-right ml-2 <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>" onclick="uploadInvoice(this)">Upload to GSTIN</a>
				<a href="${contextPath}/populate3b/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" data-toggle="tooltip" title='Table 3.1 and D. Inwards Supplies values are auto generated based on "<c:choose><c:when test="${configdetails.enableTransDate eq 'true'}">Transaction</c:when><c:otherwise>Invoice</c:otherwise></c:choose> date" from Purchase Register, Please change the view by "<c:choose><c:when test="${configdetails.enableTransDate eq 'true'}">Invoice</c:when><c:otherwise>Transaction</c:otherwise></c:choose> Date " to consider inwards values based on <c:choose><c:when test="${configdetails.enableTransDate eq 'true'}">Invoice</c:when><c:otherwise>Transaction</c:otherwise></c:choose> Date.' class="btn btn-greendark permissionGSTR3B-GSTR3B_Invoices pull-right ml-2 <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Auto Generate From Sales & Purchases</a>
				<a href="#" onclick="populateGSTR1Liabilities()" class="btn btn-greendark permissionGSTR3B-GSTR3B_Invoices pull-right ml-2 <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Auto Calculate From GSTR1 Liability</a>
				</div>
				<div class="group upload-btn mb-2"></div>
				<p style="font-size: 12px; margin-left: 16px;">Note: Table 3.1 and D. Inwards Supplies values are auto generated based on "<c:choose><c:when test="${configdetails.enableTransDate eq 'true'}">Transaction</c:when><c:otherwise>Invoice</c:otherwise></c:choose> date" from Purchase Register, Please change the view by "<c:choose><c:when test="${configdetails.enableTransDate eq 'true'}">Invoice</c:when><c:otherwise>Transaction</c:otherwise></c:choose> Date " to consider inwards values based on <c:choose><c:when test="${configdetails.enableTransDate eq 'true'}">Invoice</c:when><c:otherwise>Transaction</c:otherwise></c:choose> Date.</p>
				<div class="col-md-12 col-sm-12">
			<div id="accordion" class="accordion panel-group card" role="tablist" aria-multiselectable="true">
				<div class="panel panel-default m-b-0">
					<!--- 3.1 --->
					<div class="panel-heading active"  role="tab"  >
						<a class="panel-title" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="false">3.1 Tax on outward and reverse charge inward supplies<i class="fa fa-plus pull-right"></i></a> <span class="helpguide" data-toggle="modal" data-target="#helpguideModal_1"> Help Guide</span>
					</div>
					<div id="collapseOne" class="card-block panel-collapse collapse in show" role="tabpanel"  aria-labelledby="collapseOne">         
						<div class="group upload-btn" >
							<span class="pull-right"> <a href="#" class="permissionGSTR3B-GSTR3B_Invoices btn btn-sm btn-blue-dark tpone-edit <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>" onClick="clickEdit('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Edit</a>  <a href="#" class="btn btn-sm btn-blue-dark tpone-save" style="display:none" onClick="clickSave('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Save</a> <a href="#" class="btn btn-sm btn-blue-dark tpone-cancel" style="display:none" onClick="clickCancel('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');" >Cancel</a></span>
						</div>
						<div class="customtable db-ca-gst tabtable1 mt-2">
							<table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Nature of Supplies</th>
										<th class="text-left">Total Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">A.Outward taxable supplies (other than zero rated, nil rated and exempted)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupDet.txval" name="supDetails.osupDet.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupDet.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input " id="supDetails.osupDet.iamt" name="supDetails.osupDet.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupDet.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input " id="supDetails_osupDet_camt" name="supDetails.osupDet.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'supDetails_osupDet_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupDet.camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails_osupDet_samt" name="supDetails.osupDet.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'supDetails_osupDet_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupDet.samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupDet.csamt" name="supDetails.osupDet.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupDet.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">B.Outward taxable supplies (zero rated) </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupZero.txval" name="supDetails.osupZero.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupZero.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupZero.iamt" name="supDetails.osupZero.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupZero.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupZero.csamt" name="supDetails.osupZero.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupZero.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">C.Other outward supplies (Nil rated, exempted) </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupNilExmp.txval" name="supDetails.osupNilExmp.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupNilExmp.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
									</tr>
									<tr>
										<td class="text-left">D.Inward supplies (Liable to reverse change) </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.isupRev.txval" name="supDetails.isupRev.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.isupRev.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.isupRev.iamt" name="supDetails.isupRev.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.isupRev.iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails_isupRev_camt" name="supDetails.isupRev.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'supDetails_isupRev_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.isupRev.camt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails_isupRev_samt" name="supDetails.isupRev.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'supDetails_isupRev_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.isupRev.samt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.isupRev.csamt" name="supDetails.isupRev.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.isupRev.csamt}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">E.Non-GST outward supplies </td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="supDetails.osupNongst.txval" name="supDetails.osupNongst.txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.supDetails.osupNongst.txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
										<td class="text-right form-group"></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					</div>
					<!--- 3.2 --->
					<div class="panel panel-default m-b-0">
					<!--- 3.2 --->
					<div class="panel-heading"  role="tab"  >
						<a class="panel-title" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false">3.2 Inter-state supplies <span class="fa fa-plus pull-right"></span></a><div class="helpguide" data-toggle="modal" data-target="#helpguideModal_2"> Help Guide</div>
					</div>
					<div id="collapseTwo" class="card-block panel-collapse collapse" role="tabpanel">                 
						<div class="group upload-btn">
							<span class="pull-right"> <a href="#" class="permissionGSTR3B-GSTR3B_Invoices btn btn-sm btn-blue-dark tptwo-edit <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>""  onClick="clickEdit('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a> <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap',2);" >Cancel</a></span>
						</div>
						<div class="customtable db-ca-gst tabtable2 mt-2">       
    						<table id="dbTable2" class="dbTable2 display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Title</th>
										<th class="text-left">Place of Supply (State/UT)</th>
										<th class="text-left">Total Taxable Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Amount of Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Action</th>
									</tr>
								</thead>
								<tbody id="interSupTblBody">
									<c:if test='${not empty invoice.interSup.unregDetails && not empty invoice.interSup.unregDetails[0].txval}'>
									<c:forEach items="${invoice.interSup.unregDetails}" var="item" varStatus="loop">
									<tr id="unregDetails${loop.index}">
										<td class="text-left">A.Supplies made to unregistered persons</td>
										<td class="text-left">${item.pos}</td>
										<td class="text-left">${item.txval}</td>
										<td class="text-left">${item.iamt}</td>
										<td class="text-left"><c:if test='${loop.index > 0}'><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteItem('interSup','unregDetails',${loop.index})" class="delrow"></c:if></td>
									</tr>
									</c:forEach>
									</c:if>
									<c:if test='${empty invoice.interSup.unregDetails || empty invoice.interSup.unregDetails[0].txval}'>
									<tr class="inter-state-supplies1">
										<td class="text-left">A.Supplies made to unregistered persons </td>
										<td class="text-left">
											<input id="unregDetails_pos" class="form-control tptwo-input" name="interSup.unregDetails[0].pos" placeholder="Place of Supply" value="${invoice.interSup.unregDetails[0].pos}" />
											<div id="unregDetails_posempty" style="display:none">
												<div class="ddbox">
												  <p>Search didn't return any results.</p>
												</div>
											</div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="unregDetails_txval" name="interSup.unregDetails[0].txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.interSup.unregDetails[0].txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="unregDetails_iamt" name="interSup.unregDetails[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.interSup.unregDetails[0].iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-left"> </td>
									</tr>
									</c:if>
									<c:if test='${not empty invoice.interSup.compDetails && not empty invoice.interSup.compDetails[0].txval}'>
									<c:forEach items="${invoice.interSup.compDetails}" var="item" varStatus="loop">
									<tr id="compDetails{loop.index}">
										<td class="text-left">B.Supplies made to composition taxable persons</td>
										<td class="text-left">${item.pos}</td>
										<td class="text-left">${item.txval}</td>
										<td class="text-left">${item.iamt}</td>
										<td class="text-left"><c:if test='${loop.index > 0}'><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteItem('interSup','compDetails',${loop.index})" class="delrow"></c:if></td>
									</tr>
									</c:forEach>
									</c:if>
									<c:if test='${empty invoice.interSup.compDetails || empty invoice.interSup.compDetails[0].txval}'>
									<tr class="inter-state-supplies2">
										<td class="text-left">B.Supplies made to composition taxable persons </td>
										<td class="text-left">
											<input id="compDetails_pos" class="form-control tptwo-input" name="interSup.compDetails[0].pos" placeholder="Place of Supply" value="${invoice.interSup.compDetails[0].pos}" />
											<div id="compDetails_posempty" style="display:none">
												<div class="ddbox">
												  <p>Search didn't return any results.</p>
												</div>
											</div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="compDetails_txval" name="interSup.compDetails[0].txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.interSup.compDetails[0].txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="compDetails_iamt" name="interSup.compDetails[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.interSup.compDetails[0].iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-left"> </td>
									</tr>
									</c:if>
									<c:if test='${not empty invoice.interSup.uinDetails && not empty invoice.interSup.uinDetails[0].txval}'>
									<c:forEach items="${invoice.interSup.uinDetails}" var="item" varStatus="loop">
									<tr id="uinDetails{loop.index}">
										<td class="text-left">C.Supplies made to UIN holders</td>
										<td class="text-left">${item.pos}</td>
										<td class="text-left">${item.txval}</td>
										<td class="text-left">${item.iamt}</td>
										<td class="text-left"><c:if test='${loop.index > 0}'><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteItem('interSup','uinDetails',${loop.index})" class="delrow"></c:if></td>
									</tr>
									</c:forEach>
									</c:if>
									<c:if test='${empty invoice.interSup.compDetails || empty invoice.interSup.uinDetails[0].txval}'>
									<tr class="inter-state-supplies3">
										<td class="text-left">C.Supplies made to UIN holders </td>
										<td class="text-left">
											<input id="uinDetails_pos" class="form-control tptwo-input" name="interSup.uinDetails[0].pos" placeholder="Place of Supply" value="${invoice.interSup.uinDetails[0].pos}" />
											<div id="uinDetails_posempty" style="display:none">
												<div class="ddbox">
												  <p>Search didn't return any results.</p>
												</div>
											</div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="uinDetails_txval" name="interSup.uinDetails[0].txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.interSup.uinDetails[0].txval}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="uinDetails_iamt" name="interSup.uinDetails[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.interSup.uinDetails[0].iamt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-left"> </td>
									</tr>
									</c:if>
								</tbody>
							</table>
							<div class="group upload-btn addmorewrap"> 
								<table class="display row-border dataTable meterialform" id="dbTableAddMore2" cellspacing="0" width="100%">
									<tr class="interstatesuplies">
										<td class="text-left">
											<select id="interSup_type" class="ddselect text-left form-control pull-left" placeholder="Choose Type"><option value="unregDetails">Supplies made to unregistered persons</option><option value="compDetails">Supplies made to composition taxable persons</option><option value="uinDetails">Supplies made to UIN holders</option></select>
										</td>
										<td class="text-left  form-group gst-3b-error">
											<input id="interSup_pos" class="form-control tptwo-input" placeholder="Place of Supply" />
											<div id="interSup_posempty" style="display:none">
												<div class="ddbox">
												  <p>Search didn't return any results.</p>
												</div>
											</div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="interSup_txval" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" /><div class="help-block with-errors"></div></td>
										<td class="text-left  form-group gst-3b-error"><input type="text" class="form-control tptwo-input" id="interSup_iamt"  pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" /><div class="help-block with-errors"></div></td>
										<td class="text-left">
											<a href="#" class="btn btn-blue-dark btn-sm pull-right addmore mt-1" onclick="addInterSupplyRecord()">Add</a>
										</td>
									</tr>
								</table>
							</div>  
						</div>
					</div>
					</div>
					<!--- 4 ---->
					<div class="panel panel-default m-b-0">
					<!--- 4--->
					<div class="panel-heading"  role="tab"  >
						<a class="panel-title" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false">4 Eligible ITC <span class="fa fa-plus pull-right"></span></a><div class="helpguide" data-toggle="modal" data-target="#helpguideModal_3"> Help Guide</div>
					</div>
					<div id="collapseThree" class="card-block panel-collapse collapse" role="tabpanel"> 
						<div class="group upload-btn">
							<span class="pull-right"> <a href="#" class="permissionGSTR3B-GSTR3B_Invoices btn btn-sm  btn-blue-dark tpthree-edit <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>""  onClick="clickEdit('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpthree-cancel" onClick="clickSave('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpthree-cancel" onClick="clickCancel('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input','',3);" >Cancel</a></span>
						</div>
						<div class="customtable db-ca-gst tabtable3 mt-2">
							<table id="dbTable3" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Details</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td colspan="5" class="boldtxt">(A) ITC available (whether in full or part)</td>
									</tr>
									<tr>
										<td class="text-left">(1).Import of goods<input type="hidden" name="itcElg.itcAvl[0].ty" value="IMPG" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'IMPG'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl1_iamt" name="itcElg.itcAvl[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl1_iamt" name="itcElg.itcAvl[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'IMPG'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl1_csamt" name="itcElg.itcAvl[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl1_csamt" name="itcElg.itcAvl[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(2).Import of services<input type="hidden" name="itcElg.itcAvl[1].ty" value="IMPS" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'IMPS'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl2_iamt" name="itcElg.itcAvl[1].iamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'IMPS'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl2_iamt" name="itcElg.itcAvl[1].iamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl2_iamt" name="itcElg.itcAvl[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'IMPS'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl2_csamt" name="itcElg.itcAvl[1].csamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'IMPS'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl2_csamt" name="itcElg.itcAvl[1].csamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl2_csamt" name="itcElg.itcAvl[1].csamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(3).Inward supplies liable to reverse charge (other than 1&amp; 2 above )<input type="hidden" name="itcElg.itcAvl[2].ty" value="ISRC" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_iamt" name="itcElg.itcAvl[2].iamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_iamt" name="itcElg.itcAvl[2].iamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_iamt" name="itcElg.itcAvl[2].iamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_iamt" name="itcElg.itcAvl[2].iamt" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_camt" name="itcElg.itcAvl[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_camt" name="itcElg.itcAvl[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_camt" name="itcElg.itcAvl[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].camt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_camt" name="itcElg.itcAvl[2].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_samt" name="itcElg.itcAvl[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_samt" name="itcElg.itcAvl[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_samt" name="itcElg.itcAvl[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_samt" name="itcElg.itcAvl[2].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl3_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_csamt" name="itcElg.itcAvl[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_csamt" name="itcElg.itcAvl[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISRC'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_csamt" name="itcElg.itcAvl[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl3_csamt" name="itcElg.itcAvl[2].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(4).Inward supplies form ISD<input type="hidden" name="itcElg.itcAvl[3].ty" value="ISD" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_iamt" name="itcElg.itcAvl[3].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_iamt" name="itcElg.itcAvl[3].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_iamt" name="itcElg.itcAvl[3].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_iamt" name="itcElg.itcAvl[3].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_iamt" name="itcElg.itcAvl[3].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_camt" name="itcElg.itcAvl[3].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_camt" name="itcElg.itcAvl[3].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_camt" name="itcElg.itcAvl[3].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_camt" name="itcElg.itcAvl[3].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].camt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_camt" name="itcElg.itcAvl[3].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_samt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_samt" name="itcElg.itcAvl[3].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_samt" name="itcElg.itcAvl[3].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_samt" name="itcElg.itcAvl[3].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_samt" name="itcElg.itcAvl[3].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_samt" name="itcElg.itcAvl[3].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" onchange="updateField(this.value, 'itcElg_itcAvl4_camt')" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_csamt" name="itcElg.itcAvl[3].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_csamt" name="itcElg.itcAvl[3].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_csamt" name="itcElg.itcAvl[3].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'ISD'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_csamt" name="itcElg.itcAvl[3].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl4_csamt" name="itcElg.itcAvl[3].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(5).All other ITC<input type="hidden" name="itcElg.itcAvl[4].ty" value="OTH" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_iamt" name="itcElg.itcAvl[4].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_iamt" name="itcElg.itcAvl[4].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_iamt" name="itcElg.itcAvl[4].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_iamt" name="itcElg.itcAvl[4].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[4].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_iamt" name="itcElg.itcAvl[4].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[4].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_iamt" name="itcElg.itcAvl[4].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_camt" name="itcElg.itcAvl[4].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_camt" name="itcElg.itcAvl[4].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_camt" name="itcElg.itcAvl[4].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_camt" name="itcElg.itcAvl[4].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[4].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_camt" name="itcElg.itcAvl[4].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[4].camt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_camt" name="itcElg.itcAvl[4].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_samt" name="itcElg.itcAvl[4].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[0].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_samt" name="itcElg.itcAvl[4].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[1].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_samt" name="itcElg.itcAvl[4].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[2].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_samt" name="itcElg.itcAvl[4].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[3].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[4].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_samt" name="itcElg.itcAvl[4].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcAvl[4].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_samt" name="itcElg.itcAvl[4].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcAvl5_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcAvl[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_csamt" name="itcElg.itcAvl[4].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcAvl[0].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_csamt" name="itcElg.itcAvl[4].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcAvl[1].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[2].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_csamt" name="itcElg.itcAvl[4].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcAvl[2].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[3].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_csamt" name="itcElg.itcAvl[4].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcAvl[3].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcAvl[4].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_csamt" name="itcElg.itcAvl[4].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcAvl[4].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc" id="itcElg_itcAvl5_csamt" name="itcElg.itcAvl[4].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td colspan="5" class="boldtxt">(B) ITC Reversed</td>
									</tr>
									<tr>
										<td class="text-left">(1).As per Rule 42 &amp; 43 of CGST/SGST rules<input type="hidden" name="itcElg.itcRev[0].ty" value="RUL" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_iamt" name="itcElg.itcRev[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_iamt" name="itcElg.itcRev[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_camt" name="itcElg.itcRev[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev1_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].camt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_camt" name="itcElg.itcRev[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev1_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_samt" name="itcElg.itcRev[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev1_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))"  value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_samt" name="itcElg.itcRev[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev1_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))"  value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_csamt" name="itcElg.itcRev[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev1_csamt" name="itcElg.itcRev[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(2).Others<input type="hidden" name="itcElg.itcRev[1].ty" value="OTH" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_iamt" name="itcElg.itcRev[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcRev[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_iamt" name="itcElg.itcRev[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[1].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_iamt" name="itcElg.itcRev[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_camt" name="itcElg.itcRev[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev2_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcRev[0].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcRev[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_camt" name="itcElg.itcRev[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev2_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcRev[1].camt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_camt" name="itcElg.itcRev[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev2_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_samt" name="itcElg.itcRev[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev2_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcRev[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_samt" name="itcElg.itcRev[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev2_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[1].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_samt" name="itcElg.itcRev[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcRev2_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcRev[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_csamt" name="itcElg.itcRev[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[0].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcRev[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_csamt" name="itcElg.itcRev[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcRev[1].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input elg_itc1" id="itcElg_itcRev2_csamt" name="itcElg.itcRev[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left boldtxt">(C) Net ITC available (A)-(B)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input" id="itcElg_itcNet_iamt" name="itcElg.itcNet.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))"  value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcNet.iamt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input" id="itcElg_itcNet_camt" name="itcElg.itcNet.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcNet_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcNet.camt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input" id="itcElg_itcNet_samt" name="itcElg.itcNet.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcNet_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcNet.samt}" />'/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input" id="itcElg_itcNet_csamt" name="itcElg.itcNet.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcNet.csamt}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td colspan="5" class="text-left boldtxt">(D) Ineligible ITC</td>
									</tr>
									<tr>
										<td class="text-left">(1).As Per Section 17(5)<input type="hidden" name="itcElg.itcInelg[0].ty" value="RUL" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg1.iamt" name="itcElg.itcInelg[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg1.iamt" name="itcElg.itcInelg[0].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg1_camt" name="itcElg.itcInelg[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg1_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))"  value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].camt}" />' /></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg1_camt" name="itcElg.itcInelg[0].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg1_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))"  value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />' /></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg1_samt" name="itcElg.itcInelg[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg1_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg1_samt" name="itcElg.itcInelg[0].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg1_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'RUL'}"><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg1.csamt" name="itcElg.itcInelg[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg1.csamt" name="itcElg.itcInelg[0].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(2).Others<input type="hidden" name="itcElg.itcInelg[1].ty" value="OTH" /></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg2.iamt" name="itcElg.itcInelg[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].iamt}" />'/></c:when><c:when test="${invoice.itcElg.itcInelg[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg2.iamt" name="itcElg.itcInelg[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[1].iamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg2.iamt" name="itcElg.itcInelg[1].iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg2_camt" name="itcElg.itcInelg[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg2_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].camt}" />'/></c:when><c:when test="${invoice.itcElg.itcInelg[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg2_camt" name="itcElg.itcInelg[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg2_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[1].camt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg2_camt" name="itcElg.itcInelg[1].camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg2_samt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg2_samt" name="itcElg.itcInelg[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg2_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[0].samt}" />'/></c:when><c:when test="${invoice.itcElg.itcInelg[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg2_samt" name="itcElg.itcInelg[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg2_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[1].samt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg_itcInelg2_samt" name="itcElg.itcInelg[1].samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" onchange="updateField(this.value, 'itcElg_itcInelg2_camt')" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><c:choose><c:when test="${invoice.itcElg.itcInelg[0].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg2.csamt" name="itcElg.itcInelg[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.itcElg.itcInelg[0].csamt}" />'/></c:when><c:when test="${invoice.itcElg.itcInelg[1].ty eq 'OTH'}"><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg2.csamt" name="itcElg.itcInelg[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.itcElg.itcInelg[1].csamt}" />'/></c:when><c:otherwise><input type="text" class="form-control tpthree-input" id="itcElg.itcInelg2.csamt" name="itcElg.itcInelg[1].csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="" />'/></c:otherwise></c:choose><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					</div>
					<!--- 5 --->      
					<div class="panel panel-default m-b-0">
					<!--- 5 --->
					<div class="panel-heading"  role="tab"  >
						<a class="panel-title" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="false">5 Exempt, nil and Non GST inward suplies <i class="fa fa-plus pull-right"></i></a><div class="helpguide" data-toggle="modal" data-target="#helpguideModal_4"> Help Guide</div>
					</div>
					<div id="collapseFour" class="card-block panel-collapse collapse" role="tabpanel">
						<div class="group upload-btn">
							<span class="pull-right"> <a href="#" class="permissionGSTR3B-GSTR3B_Invoices btn btn-sm  btn-blue-dark tpfour-edit <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>""  onClick="clickEdit('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpfour-cancel" onClick="clickSave('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpfour-cancel" onClick="clickCancel('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input','',4);" >Cancel</a></span>
						</div>
						<div class="customtable db-ca-gst tabtable4 mt-2">
							<table id="dbTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Nature of supplies</th>
										<th class="text-left">Inter - State supples(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Intra - State supples(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>A.From a supplier under composition scheme, Exempt and Nil rated supply<input type="hidden" name="inwardSup.isupDetails[0].ty" value="GST" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="inwardSup.isupDetails1.inter" name="inwardSup.isupDetails[0].inter" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.inwardSup.isupDetails[0].inter}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="inwardSup.isupDetails1.intra" name="inwardSup.isupDetails[0].intra" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.inwardSup.isupDetails[0].intra}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr> 
									<tr>
										<td>B.Non GST supply<input type="hidden" name="inwardSup.isupDetails[1].ty" value="NONGST" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="inwardSup.isupDetails2.inter" name="inwardSup.isupDetails[1].inter" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.inwardSup.isupDetails[1].inter}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="inwardSup.isupDetails2.intra" name="inwardSup.isupDetails[1].intra" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.inwardSup.isupDetails[1].intra}" />' class="form-control tpfour-input"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					</div>
					<!--- 5.1 --->
					<div class="panel panel-default m-b-0">
					<!--- 5.1 --->
					<div class="panel-heading"  role="tab"  >
						<a class="panel-title" role="tab" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="false"">5.1 Interest and Late fee <i class="fa fa-plus pull-right"></i></a> <div class="helpguide" data-toggle="modal" data-target="#helpguideModal_5"> Help Guide</div>
					</div>
					<div id="collapseFive" class="card-block panel-collapse collapse" role="tabpanel">       
						<!-- <div class="group upload-btn">
							<span class="pull-right"> <a href="#" class="btn btn-sm  btn-blue-dark tpfive-edit"  onClick="clickEdit('.tpfive-save', '.tpfive-cancel', '.tpfive-edit','.tpfive-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpfive-cancel" onClick="clickSave('.tpfive-save', '.tpfive-cancel', '.tpfive-edit','.tpfive-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpfive-cancel" onClick="clickCancel('.tpfive-save', '.tpfive-cancel', '.tpfive-edit','.tpfive-input');" >Cancel</a></span>
						</div> -->
						<div class="customtable db-ca-gst tabtable5 mt-2">
							<h6>Info messages : </h6>
							<div class="alert alert-info">Declare interest payable on tax liabilities on supplies attracting reverse charge as well as other than reverse charge</div>
							<div class="alert alert-danger">Late fee for the month includes previous months late fee charged due to delay in filing of return. The calculation shall be Rs.50(Rs.20 for Nil Returns)*No.of days of Delay as  per Act (CGST/SGST).</div>
							<div class="meterialform mt-2 mb-2 group upload-btn">
								<span class="checkbox pull-left"><label><input type="checkbox" id="chkBoxLiab" <c:if test="${client.status eq statusPending  || empty client.status}">onclick="chkLiability()" </c:if> <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disabled </c:if>>	<i class="helper <c:if test="${client.status eq statusSubmitted  || client.status eq statusFiled}">disable</c:if>"></i>Please select the check box if you wish to declare any Interest liabilities. Please note Interest amounts declared here under respective heads need to be paid in cash in addition to tax liabilities for the month. GSTR 3B can be filed only after complete payment of all liabilities. </label></span>
							</div>
							<table id="dbTable5" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left">Description</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>Interest & Late fee</td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="intrLtfee.intrDetails.iamt" name="intrLtfee.intrDetails.iamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.intrLtfee.intrDetails.iamt}" />' class="form-control tpcheck-input"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="intrLtfee_intrDetails_camt" name="intrLtfee.intrDetails.camt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.intrLtfee.intrDetails.camt}" />' class="form-control tpcheck-input" onchange="updateField(this.value, 'intrLtfee_intrDetails_samt')"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="intrLtfee_intrDetails_samt" name="intrLtfee.intrDetails.samt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.intrLtfee.intrDetails.samt}" />' class="form-control tpcheck-input"  onchange="updateField(this.value, 'intrLtfee_intrDetails_camt')"/><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" id="intrLtfee.intrDetails.csamt" name="intrLtfee.intrDetails.csamt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.intrLtfee.intrDetails.csamt}" />' class="form-control tpcheck-input"/><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table> 
						</div>
					</div>
				</div>
				</div>
				</div>
							<div class="col-sm-12 mt-4 text-center">
								<c:if test='${not empty invoice.id}'>
								<input type="hidden" name="id" value="<c:out value="${invoice.id}"/>">
								<input type="hidden" name="gstin" value="<c:out value="${invoice.gstin}"/>">
								<input type="hidden" name="retPeriod" value="<c:out value="${invoice.retPeriod}"/>">
								<c:if test='${not empty invoice.interSup.unregDetails}'>
								<c:forEach items="${invoice.interSup.unregDetails}" var="item" varStatus="loop">
								<input type="hidden" name="interSup.unregDetails[${loop.index}].pos" value="${item.pos}">
								<input type="hidden" name="interSup.unregDetails[${loop.index}].txval" value="${item.txval}">
								<input type="hidden" name="interSup.unregDetails[${loop.index}].iamt" value="${item.iamt}">
								</c:forEach>
								</c:if>
								<c:if test='${not empty invoice.interSup.compDetails}'>
								<c:forEach items="${invoice.interSup.compDetails}" var="item" varStatus="loop">
								<input type="hidden" name="interSup.compDetails[${loop.index}].pos" value="${item.pos}">
								<input type="hidden" name="interSup.compDetails[${loop.index}].txval" value="${item.txval}">
								<input type="hidden" name="interSup.compDetails[${loop.index}].iamt" value="${item.iamt}">
								</c:forEach>
								</c:if>
								<c:if test='${not empty invoice.interSup.uinDetails}'>
								<c:forEach items="${invoice.interSup.uinDetails}" var="item" varStatus="loop">
								<input type="hidden" name="interSup.uinDetails[${loop.index}].pos" value="${item.pos}">
								<input type="hidden" name="interSup.uinDetails[${loop.index}].txval" value="${item.txval}">
								<input type="hidden" name="interSup.uinDetails[${loop.index}].iamt" value="${item.iamt}">
								</c:forEach>
								</c:if>
								</c:if>
								<c:if test='${empty invoice.id}'>
								<input type="hidden" name="gstin" value="<c:out value="${client.gstnnumber}"/>">
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
							 <table id="dbFilingTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th>Category</th>
										<th>Description</th>
										<th>Point of Sale</th>
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
						<span style="font-weight: bold;color: #374583;padding-left:15px;padding-right:15px;">The Cash available as on date and ITC available(considering ITC of current tax period) are shown below </span>
						<div class="row mb-2 mt-2 returns_dropdown" style="padding-left:15px;padding-right:15px;">
   						<div class="col-md-6 pr-2">
					      <div class="cashledger_row">
					         <h6 class="mb-2 mt-2 ml-4" style="display: inline-block;color:#374583">CASH LEDGER</h6>
					         <a class="pl-1" style="font-size: 12px; color: black;" href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=cash">( Full Report )</a>
					         <p style="display: inline-block;margin-left:5px;margin-bottom: 2px;float: right;margin-top: 5px;margin-right: 27px;padding: 2px;font-size:13px;">Closing Balance As on : <span class="ml-2 ledgerDate"></span></p>
					         <div class="cashledger_summary_totals">
					            <div class="noramltable-row">
					               <div class="noramltable-row-desc">
					                  <div class="normaltable-col hdr">IGST <div class="normaltable-col-txt clind_formats" id="cashledgerigst">0.00</div></div>
					                  <div class="normaltable-col hdr">CGST<div class="normaltable-col-txt clind_formats" id="cashledgercgst">0.00</div></div>
					                  <div class="normaltable-col hdr">SGST<div class="normaltable-col-txt clind_formats" id="cashledgersgst">0.00</div></div>
					                  <div class="normaltable-col hdr">CESS<div class="normaltable-col-txt clind_formats" id="cashledgercess">0.00</div></div>
					               </div>
					            </div>
					         </div>
					      </div>
					   </div>
					   <div class="col-md-6 pl-2">
					      <div class="cashledger_row">
					         <h6 class="mb-2 mt-2 ml-4" style="display: inline-block;color:#374583">CREDIT LEDGER</h6>
					         <a class="pl-1" style="font-size: 12px; color: black;" href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=credit">( Full Report )</a>
					         <p style="display: inline-block;margin-left:5px;margin-bottom: 2px;float: right;margin-top: 5px;margin-right: 27px;padding: 2px;font-size:13px;">Closing Balance As on : <span class="ml-2 ledgerDate" style="padding: 1px;"></span></p>
					         <div class="creditledger_summary_totals">
					            <div class="noramltable-row">
					               <div class="noramltable-row-desc">
					                  <div class="normaltable-col hdr">IGST<div class="normaltable-col-txt clind_formats" id="creditledgerigst">0.00</div></div>
					                  <div class="normaltable-col hdr">CGST<div class="normaltable-col-txt clind_formats" id="creditledgercgst">0.00</div></div>
					                  <div class="normaltable-col hdr">SGST<div class="normaltable-col-txt clind_formats" id="creditledgersgst">0.00</div></div>
					                  <div class="normaltable-col hdr">CESS<div class="normaltable-col-txt clind_formats" id="creditledgercess">0.00</div></div>
					               </div>
					            </div>
					         </div>
					      </div>
					   </div>
					</div>
					<form:form method="POST" id="sup4Form" data-toggle="validator" class="meterialform invoiceform" name="salesinvoceform" action="${contextPath}/saveoffliab/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
					<div class="col-md-12 col-sm-12">
						<div class="group upload-btn">
							<span class="pull-right"><a href="#" class="permissionGSTR3B-GSTR3B_Invoices btn btn-sm btn-blue-dark tpseven-edit <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>"" onClick="clickEdit('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');" style="margin-top:1px;padding:6px 10px!important">Edit</a>  <a href="#" class="btn btn-sm btn-blue-dark tpseven-save" style="display:none;margin-right: 3px;margin-top:1px;padding:6px 10px!important" onClick="clickSave('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');">Save</a><a href="#" class="btn btn-sm btn-blue-dark tpseven-cancel" style="display:none;margin-top:1px;padding:6px 10px!important" onClick="clickCancel('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input','',7);">Cancel</a>
							<a href="#" class='btn btn-greendark <c:if test="${client.status eq statusFiled || empty invoice.id}">disable</c:if>' onclick="invokeOffsetLiab();">Offset Liability</a> </span>
						</div>
						<!-- table start -->
						<div class="customtable">
							<table class="display row-border dataTable meterialform" id="dbTable7" cellspacing="0" width="100%">
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
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].igst.tx" id="offLiabTaxPayableoigsttx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].igst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdigst" id="offLiabPditcigstpdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.igstPdigst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdcgst" id="offLiabPditcigstpdcgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.igstPdcgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdsgst" id="offLiabPditcigstpdsgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.igstPdsgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].ipd" id="offLiabpdcashoipd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.offLiab.pdcash[0].ipd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].igstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.offLiab.pdcash[0].igstIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].cgst.tx" id="offLiabTaxPayableocgsttx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].cgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cgstPdigst" id="offLiabPditccgstpdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.cgstPdigst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cgstPdcgst" id="offLiabPditccgstpdcgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.cgstPdcgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cpd" id="offLiabpdcashocpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cgstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cgstIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cgstLfeepd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cgstLfeepd}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].sgst.tx" id="offLiabTaxPayableosgsttx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].sgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.sgstPdigst" id="offLiabPditcsgstpdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.sgstPdigst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.sgstPdsgst" id="offLiabPditcsgstpdsgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.sgstPdsgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].spd" id="offLiabpdcashospd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].spd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].sgstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].sgstIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].sgstLfeepd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].sgstLfeepd}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">CESS</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].cess.tx" id="offLiabTaxPayableocesstx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].cess.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cessPdcess" id="offLiabPditccesspdcess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.cessPdcess}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cspd" id="offLiabpdcashocspd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cspd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cessIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cessIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td colspan="9"><span class="card-title">Reverse Charge</a></td>
									</tr>
									<tr>
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offLiabTaxPayableiigsttx" name="offLiab.taxPayable[1].igst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].igst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offpdcashiipd" name="offLiab.pdcash[1].ipd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].ipd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offLiabTaxPayableicgsttx" name="offLiab.taxPayable[1].cgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].cgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offpdcashicpd" name="offLiab.pdcash[1].cpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].cpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text"  readonly="readonly" class="form-control tpseven-input" id="offLiabTaxPayableisgsttx" name="offLiab.taxPayable[1].sgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].sgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offpdcashispd" name="offLiab.pdcash[1].spd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].spd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">CESS</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offLiabTaxPayableicesstx" name="offLiab.taxPayable[1].cess.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].cess.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" id="offpdcashicspd" name="offLiab.pdcash[1].cspd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].cspd}" />' /><div class="help-block with-errors"></div></td>
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
          <h3>Help To File GSTR3B</h3>
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
											<span class="errormsg" id="otp_Msg"></span>
											<div class="col-sm-12 otp_form_input" style="display:block;margin-top:30px">
												<div class="group upload-btn">
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
												<h6>Didn't receive OTP? <a href="#" onClick="otpTryAgain()">try again</a></h6>
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
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;
		var year = eDate.getFullYear();
		window.location.href = '${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year;
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
 $('.card-header').click(function(){
  $(this).addClass('active').siblings().removeClass('active');
 });
 var fullDate = new Date();
 var twoDigitMonth = (fullDate.getMonth()+1)+"";
 if(twoDigitMonth.length==1) {
	 twoDigitMonth="0" +twoDigitMonth;	 
 }
 var twoDigitDate = fullDate.getDate()+"";
 if(twoDigitDate.length==1){
	 twoDigitDate="0" +twoDigitDate;	 
 }
 var ledgerDate = twoDigitDate + "/" + twoDigitMonth + "/" + fullDate.getFullYear();
 $('.ledgerDate').text(ledgerDate);
var itcNetIamt=0.0,itcNetSamt=0.0,itcNetCamt=0.0,itcNetCsamt=0.0;
var cashLedgerIamt=0.0,cashLedgerSamt=0.0,cashLedgerCamt=0.0,cashLedgerCsamt=0.0;
if('${invoice.itcElg.itcNet.iamt}' !="" && '${invoice.itcElg.itcNet.iamt}' !=null){
	itcNetIamt='${invoice.itcElg.itcNet.iamt}';
}
if('${invoice.itcElg.itcNet.camt}' !="" && '${invoice.itcElg.itcNet.camt}' !=null){
	itcNetCamt='${invoice.itcElg.itcNet.camt}';
}
if('${invoice.itcElg.itcNet.samt}' !="" && '${invoice.itcElg.itcNet.samt}' !=null){
	itcNetSamt='${invoice.itcElg.itcNet.samt}';
}
if('${invoice.itcElg.itcNet.csamt}' !="" && '${invoice.itcElg.itcNet.csamt}' !=null){
	itcNetCsamt='${invoice.itcElg.itcNet.csamt}';
}
function populateoffsetliability(){
	$.ajax({
				url: "${contextPath}/populateOffsetLiablitys/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}",
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					$('#offLiabTaxPayableoigsttx').val((response.data.tx_pmt.tx_py[0].igst.tx).toFixed(2));
					$('#offLiabTaxPayableocgsttx').val((response.data.tx_pmt.tx_py[0].cgst.tx).toFixed(2));
					$('#offLiabTaxPayableosgsttx').val((response.data.tx_pmt.tx_py[0].sgst.tx).toFixed(2));
					$('#offLiabTaxPayableocesstx').val((response.data.tx_pmt.tx_py[0].cess.tx).toFixed(2));
					$('#offLiabPditcigstpdigst').val((response.data.tx_pmt.pditc.i_pdi).toFixed(2));
					$('#offLiabPditcigstpdcgst').val((response.data.tx_pmt.pditc.i_pdc).toFixed(2));
					$('#offLiabPditcigstpdsgst').val((response.data.tx_pmt.pditc.i_pds).toFixed(2));
					$('#offLiabPditccgstpdigst').val((response.data.tx_pmt.pditc.c_pdi).toFixed(2));
					$('#offLiabPditccgstpdcgst').val((response.data.tx_pmt.pditc.c_pdc).toFixed(2));
					$('#offLiabPditcsgstpdigst').val((response.data.tx_pmt.pditc.s_pdi).toFixed(2));
					$('#offLiabPditcsgstpdsgst').val((response.data.tx_pmt.pditc.s_pds).toFixed(2));
					$('#offLiabPditccesspdcess').val((response.data.tx_pmt.pditc.cs_pdcs).toFixed(2));
					$('#offLiabpdcashoipd').val((response.data.tx_pmt.pdcash[0].ipd).toFixed(2));
					$('#offLiabpdcashocpd').val((response.data.tx_pmt.pdcash[0].cpd).toFixed(2));
					$('#offLiabpdcashospd').val((response.data.tx_pmt.pdcash[0].spd).toFixed(2));
					$('#offLiabpdcashocspd').val((response.data.tx_pmt.pdcash[0].cspd).toFixed(2));
					
					$('#offLiabTaxPayableiigsttx').val((response.data.tx_pmt.tx_py[1].igst.tx).toFixed(2));
					$('#offLiabTaxPayableicgsttx').val((response.data.tx_pmt.tx_py[1].cgst.tx).toFixed(2));
					$('#offLiabTaxPayableisgsttx').val((response.data.tx_pmt.tx_py[1].sgst.tx).toFixed(2));
					$('#offLiabTaxPayableicesstx').val((response.data.tx_pmt.tx_py[1].cess.tx).toFixed(2));
					$('#offpdcashiipd').val((response.data.tx_pmt.pdcash[1].ipd).toFixed(2));
					$('#offpdcashicpd').val((response.data.tx_pmt.pdcash[1].cpd).toFixed(2));
					$('#offpdcashispd').val((response.data.tx_pmt.pdcash[1].spd).toFixed(2));
					$('#offpdcashicspd').val((response.data.tx_pmt.pdcash[1].cspd).toFixed(2));
					
				}
	});

}

$(function(){
	otpExpiryCheck();
	if(otpExpirycheck == "OTP_VERIFIED"){
		$.ajax({
			url: "${contextPath}/cashAndCreditLedgerDetails/${id}/${client.id}",
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(data) {
				if(data.cashledger){
					if(data.cashledger.igstbal.tot !="" && data.cashledger.igstbal.tot !=null){cashLedgerIamt=data.cashledger.igstbal.tot}
					if(data.cashledger.sgstbal.tot !="" && data.cashledger.sgstbal.tot !=null){cashLedgerSamt=data.cashledger.sgstbal.tot}
					if(data.cashledger.cgstbal.tot !="" && data.cashledger.cgstbal.tot !=null){cashLedgerCamt=data.cashledger.cgstbal.tot}
					if(data.cashledger.cessbal.tot !="" && data.cashledger.cessbal.tot !=null){cashLedgerCsamt=data.cashledger.cesstbal.tot}
				}
				if(data.creditledger){
					if(data.creditledger.igstTaxBal !="" && data.creditledger.igstTaxBal !=null){itcNetIamt=data.creditledger.igstTaxBal;}
					if(data.creditledger.sgstTaxBal !="" && data.creditledger.sgstTaxBal !=null){itcNetSamt=data.creditledger.sgstTaxBal}
					if(data.creditledger.cgstTaxBal !="" && data.creditledger.cgstTaxBal !=null){itcNetCamt=data.creditledger.cgstTaxBal}
					if(data.creditledger.cessTaxBal !="" && data.creditledger.cessTaxBal !=null){itcNetCsamt=data.creditledger.cessTaxBal}
				}
			},error:function(error){
				
			}
		});
		$('#cashledgerigst').html(cashLedgerIamt);
		$('#cashledgersgst').html(cashLedgerSamt);
		$('#cashledgercgst').html(cashLedgerCamt);
		$('#cashledgercess').html(cashLedgerCsamt);
		
		$('#creditledgerigst').html(itcNetIamt);
		$('#creditledgersgst').html(itcNetSamt);
		$('#creditledgercgst').html(itcNetCamt);
		$('#cashledgercess').html(itcNetCsamt);
	}else{
		$('#cashledgerigst').html(cashLedgerIamt);
		$('#cashledgersgst').html(cashLedgerSamt);
		$('#cashledgercgst').html(cashLedgerCamt);
		$('#cashledgercess').html(cashLedgerCsamt);
		
		$('#creditledgerigst').html(itcNetIamt);
		$('#creditledgersgst').html(itcNetSamt);
		$('#creditledgercgst').html(itcNetCamt);
		$('#cashledgercess').html(itcNetCsamt);
	}
	OSREC.CurrencyFormatter.formatAll({selector: '.clind_formats'});
});
function populateGSTR1Liabilities(){
	otpExpiryCheck();
	//window.location.href = "${contextPath}/gstr1liabilityAutoCal/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}";
	 if(otpExpirycheck == 'OTP_VERIFIED'){
		window.location.href = "${contextPath}/gstr1liabilityAutoCal/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}";
	}else{
		errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
	}
}
</script>
</body>

 </html> 