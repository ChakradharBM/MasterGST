<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Software | Upload GSTR9C | File GSTR9C</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/invoice/gstr9cinvoice.css" media="all" />
<script src="${contextPath}/static/mastergst/js/gstr9/gstr9c.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>

<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
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
	var ipAddress = '', uploadResponse;
	$(function () {
		$(".tpone-input, .tptwo-input, .tpthree-input, .tpfour-input, .tpfive-input, .tpcheck-input").attr('readonly', true);
		$('.tpone-save, .tpone-cancel,.tptwo-save, .tptwo-cancel,.tpthree-save, .tpthree-cancel,.tpfour-save, .tpfour-cancel,.tpfive-save, .tpfive-cancel, .addmorewrap').hide();

		$(".otp_form_input .invoice_otp").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next().next('.form-control').focus();
			}
		});
		$('#nav-client').addClass('active');
		$('.elg_itc,.elg_itc1').on( "change", function() {
			var id = $(this).attr('id');
			updateElgItcLogic(id);
		});
		function forceNumeric(){
			var $input = $(this);
			$input.val($input.val().replace(/[^\d.,]+/g,''));
		}
		$('body').on('propertychange input', 'input.form-control', forceNumeric);
		
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
			$('#interSupTblBody').append('<tr id="'+(selectedOption+index)+'"><td class="text-left"><input type="text" class="tptwo-input" readonly="true" value="'+$("#interSup_type option:selected").text()+'" /></td><td class="text-left"><input type="text" class="tptwo-input" readonly="true" value="'+$("#interSup_pos").val()+'" /></td><td class="text-left"><input type="text" class="tptwo-input" readonly="true" value="'+$("#interSup_txval").val()+'" /></td><td class="text-left"><input type="text" class="tptwo-input" readonly="true" value="'+$("#interSup_iamt").val()+'" /></td><td class="text-left"><img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteItem(\'interSup\',\''+selectedOption+'\','+index+')" class="delrow"></td></tr>');
			$("form[name='salesinvoceform']").append('<input type="hidden" name="interSup.'+selectedOption+'['+index+'].pos" value="'+$("#interSup_pos").val()+'">');
			$("form[name='salesinvoceform']").append('<input type="hidden" name="interSup.'+selectedOption+'['+index+'].txval" value="'+$("#interSup_txval").val()+'">');
			$("form[name='salesinvoceform']").append('<input type="hidden" name="interSup.'+selectedOption+'['+index+'].iamt" value="'+$("#interSup_iamt").val()+'">');
		}
		$("#interSup_type").val('');
		$("#interSup_pos").val('');
		$("#interSup_txval").val('');
		$("#interSup_iamt").val('');
		indexObj[selectedOption]++;
	}
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
	function chkLiability() {
		$('.tpcheck-input').prop("readonly", !document.getElementById('chkBoxLiab').checked);
	}
	function clickEdit(a,b,c,d,e){
		$(a).show();
		$(b).show();
		$(c).hide();
		$(d).attr('readonly', false);
		$('.auto-row td .form-control').attr('readonly', true);
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
		var formObj = document.getElementById('supForm');
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
	}

	function submitReturns() {
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
	}
	function fetchRetSummary(acceptFlag) {
		if(gstSummary == null) {
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
							|| response.status_desc == 'Unauthorized User!' || response.status_desc == 'Missing Mandatory Params'  || response.status_desc == 'API Authorization Failed') {
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
				} else {
					if(response.error && response.error.message) {
						errorNotification(response.error.message);
					} else if(response.status_cd == '0') {
						if(response.status_desc == 'OTP verification is not yet completed!' 
							|| response.status_desc == 'Invalid Session'
							|| response.status_desc == 'Unauthorized User!' || response.status_desc == 'Missing Mandatory Params' || response.status_desc == 'API Authorization Failed') {
							errorNotification(response.status_desc+'. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
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
		var state = "${client.statename}";
		var gstname = "${client.gstname}";
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname+"&ipAddress="+ipAddress,
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
	var table6Count =3,table8Count =3,table10Count =3,table13Count =3,table15Count =3;
	var tablemnths =['','','','D','E','F','G','H','I','J'];
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
								<a class="nav-link active" data-toggle="tab" href="#gtab1" role="tab">GSTR9C INVOICES</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab3" role="tab">Tax Payment</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab4" role="tab">Offset Liability</a>
							</li>
							<li class="nav-item">
								<a class="nav-link" data-toggle="tab" href="#gtab2" role="tab" onclick="fetchRetSummary(true)">FILING GSTR3B</a>
							</li>
						</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<!-- Tab panes 1-->
					<div class="tab-pane active" id="gtab1" role="tabpane1">
				<form:form method="POST" id="supForm" data-toggle="validator" class="meterialform invoiceform gstr9cinvoceform" name="gstr9cinvoceform" action="${contextPath}/saveAnnual9Cinvoice/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
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
				<a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark permissionUpload_Invoice pull-right ml-2 <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>" onclick="uploadInvoice(this)">Upload to GSTIN</a>
				<a href="${contextPath}/populate3b/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-greendark pull-right ml-2 <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Auto Generate From Sales & Purchases</a>
				</div>
				<div class="group upload-btn mb-2"></div>
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
						</ul>
						<div class="tab-content">
					    <div id="main_tab1" class="tab-pane fade in active">
					    <div class="group upload-btn" >
							<div class="mb-2">Reconciliation of turnover declared in audited Annual Financial Statement with turnover declared in Annual Return (GSTR9)
							<span class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_1"> Help Guide</span><span class="pull-right"> <a href="#" class="btn btn-sm btn-blue-dark tpone-edit " onClick="clickEdit('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Edit</a>  <a href="#" class="btn btn-sm btn-blue-dark tpone-save" style="display:none" onClick="clickSave('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');">Save</a> <a href="#" class="btn btn-sm btn-blue-dark tpone-cancel" style="display:none" onClick="clickCancel('.tpone-save', '.tpone-cancel', '.tpone-edit','.tpone-input');" >Cancel</a></span></div>
						</div>
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerOne">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerOne" aria-expanded="true" aria-controls="collapseinnerOne">
						       		(5)Reconciliation of Gross Turnover
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerOne" class="collapse show" aria-labelledby="headinginnerOne" data-parent="#accordion">
						      <div class="card-body p-2">
						       <div class="customtable db-ca-gst tabtable1 mt-2">
					      		<table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" colspan="3">Reconciliation of Gross Turnover</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left" colspan="2">(A)Turnover (including exports) as per audited financial statements for the State / UT (For multi-GSTIN units under same PAN the turnover shall be derived from the audited Annual Financial Statement)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_turnovr" name="auditedData.table5.turnovr" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.turnovr}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(B)Unbilled revenue at the beginning of Financial Year </td>
										<td class="text-center">(+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_unbilRevBeg" name="auditedData.table5.unbilRevBeg" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.unbilRevBeg}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(C)Unadjusted advances at the end of the Financial Year</td>
										<td class="text-center">(+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_unadjAdvEnd" name="auditedData.table5.unadjAdvEnd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.unadjAdvEnd}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(D)Deemed Supply under Schedule I </td>
										<td class="text-center">(+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_dmdSup" name="auditedData.table5.dmdSup" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.dmdSup}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(E)Credit Notes issued after the end of the financial year but reflected in the annual return </td>
										<td class="text-center">(+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_crdNtsIssued" name="auditedData.table5.crdNtsIssued" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.crdNtsIssued}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)Trade Discounts accounted for in the audited Annual Financial Statement but are not permissible under GST</td>
										<td class="text-center">(+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_trdDis" name="auditedData.table5.trdDis" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.trdDis}" />' /><div class="help-block with-errors"></div></td>
									</tr><tr>
										<td class="text-left">(G)Turnover from April 2017 to June 2017 </td>
										<td class="text-center">(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_turnovrAprJun" name="auditedData.table5.turnovrAprJun" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.turnovrAprJun}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(H)Unbilled revenue at the end of Financial Year </td>
										<td class="text-center">(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_unbilRevEnd" name="auditedData.table5.unbilRevEnd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.unbilRevEnd}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(I)Unadjusted Advances at the beginning of the Financial Year </td>
										<td class="text-center">(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_unadjAdvBeg" name="auditedData.table5.unadjAdvBeg" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.unadjAdvBeg}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(J)Credit notes accounted for in the audited Annual Financial Statement but are not permissible under GST</td>
										<td class="text-center">(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_crdNoteAcc" name="auditedData.table5.crdNoteAcc" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.crdNoteAcc}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(K)Adjustments on account of supply of goods by SEZ units to DTA Units </td>
										<td class="text-center">(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_adjDta" name="auditedData.table5.adjDta" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.adjDta}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(L)Turnover for the period under composition scheme</td>
										<td class="text-center">(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_turnovrComp" name="auditedData.table5.turnovrComp" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.turnovrComp}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(M)Adjustments in turnover under section 15 and rules thereunder </td>
										<td class="text-center">(+/-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_adjTurnSec" name="auditedData.table5.adjTurnSec" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.adjTurnSec}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(N)Adjustments in turnover due to foreign exchange fluctuations</td>
										<td class="text-center">(+/-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_adjTurnFef" name="auditedData.table5.adjTurnFef" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.adjTurnFef}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(O)Adjustments in turnover due to reasons not listed above  </td>
										<td class="text-center">(+/-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_adjTurnOthrsn" name="auditedData.table5.adjTurnOthrsn" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.adjTurnOthrsn}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left" colspan="2">(P)Annual turnover after adjustments as above</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_annulTurnAdj" name="auditedData.table5.annulTurnAdj" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.annulTurnAdj}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" colspan="2">(Q)Turnover as declared in Annual Return (GSTR9)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_annulTurnDecl" name="auditedData.table5.annulTurnDecl" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.annulTurnDecl}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left" colspan="2">(R)Un-Reconciled turnover (Q - P)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpone-input" id="auditedData_table5_unrecTurnovr" name="auditedData.table5.unrecTurnovr" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table5.unrecTurnovr}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									</tbody>
									</table></div>
									</div></div></div>
									 <div class="card">
								    <div class="card-header" id="headinginnerTwo">
								      <h5 class="mb-0">
								        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTwo" aria-expanded="true" aria-controls="collapseinnerTwo">
								         (6) Reasons for Un - Reconciled difference in Annual Gross Turnover
								        </button>
								      </h5>
								    </div>
								    <div id="collapseinnerTwo" class="collapse show" aria-labelledby="headinginnerTwo" data-parent="#accordion">
								      <div class="card-body p-2">
								       <div class="customtable db-ca-gst tabtable1 mt-2">
									<table class="display row-border dataTable meterialform" id="ReconTurnOver">
									<thead>
									<tr>
										<th class="text-left" colspan="4">Reasons for Un - Reconciled difference in Annual Gross Turnover</th>
									</tr>
									</thead>
									<tbody id="table6Body">
									<tr>
										<td class="text-left">(A)Reason 1 <input type="hidden" name="auditedData.table6.rsn[0].number" value="RSN1" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" style="width: 100%;" readonly="true" class="form-control tpone-input" id="auditedData_table6_rsn[0]_desc" name="auditedData.table6.rsn[0].desc" placeholder="Reason Description"  value="${invoice.auditedData.table6.rsn[0].desc}" /><div class="help-block with-errors"></div></td>
									<td></td>
									</tr>
									<tr>
										<td class="text-left" >(B)Reason 2 <input type="hidden" name="auditedData.table6.rsn[1].number" value="RSN2" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" style="width: 100%;" readonly="true" class="form-control tpone-input" id="auditedData_table6_rsn[1]_desc" name="auditedData.table6.rsn[1].desc" placeholder="Reason Description" value="${invoice.auditedData.table6.rsn[1].desc}" /><div class="help-block with-errors"></div></td>
									<td></td>
									</tr>
									<tr>
										<td class="text-left" >(C)Reason 3 <input type="hidden" name="auditedData.table6.rsn[2].number" value="RSN3" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" style="width: 100%;" readonly="true" class="form-control tpone-input" id="auditedData_table6_rsn[2]_desc" name="auditedData.table6.rsn[2].desc" placeholder="Reason description" value="${invoice.auditedData.table6.rsn[2].desc}"/><div class="help-block with-errors"></div></td>
										<td><input type="button" class="btn btn-blue-dark addmore" onclick="addReconTurnover('table6','table6Body')" value="Add"/></td>
									</tr>
									</tbody>
									</table></div></div></div></div>
									 <div class="card">
								    <div class="card-header" id="headinginnerThree">
								      <h5 class="mb-0">
								        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerThree" aria-expanded="true" aria-controls="collapseinnerThree">
								       (7)Reconciliation of Taxable Turnover
								        </button>
								      </h5>
								    </div>
								    <div id="collapseinnerThree" class="collapse show" aria-labelledby="headinginnerThree" data-parent="#accordion">
								      <div class="card-body p-2">
								       <div class="customtable db-ca-gst tabtable2 mt-2">   
									<table class="display row-border dataTable meterialform">
									<thead>
									<tr>
										<th class="text-left" colspan="3">Reconciliation of Taxable Turnover</th>
									</tr>
									</thead>
									<tbody>
									<tr class="auto-row">
										<td class="text-left"  colspan="2">(A)Annual turnover after adjustments (from 5P above)</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_annulTurnAdj" name="auditedData.table7.annulTurnAdj" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table7.annulTurnAdj}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left"  colspan="2">(B)Value of Exempted, Nil Rated, Non-GST supplies, No-Supply turnover </td>
										<td class="text-right form-group gst-3b-error"><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_othrTurnovr" name="auditedData.table7.othrTurnovr" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table7.othrTurnovr}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left"  colspan="2">(C)Zero rated supplies without payment of tax </td>
										<td class="text-right form-group gst-3b-error"><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_zeroSup" name="auditedData.table7.zeroSup" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoce.auditedData.table7.zeroSup}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left"  colspan="2">(D)Supplies on which tax is to be paid by the recipient on reverse charge basis</td>
										<td class="text-right form-group gst-3b-error"><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_revSup" name="auditedData.table7.revSup" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table7.revSup}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left"  colspan="2">(E)Taxable turnover as per adjustments above (A-B-C-D) </td>
										<td class="text-right form-group gst-3b-error"><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_taxTurnAdj" name="auditedData.table7.taxTurnAdj" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table7.taxTurnAdj}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left"  colspan="2">(F)Taxable turnover as per liability declared in Annual Return (GSTR9)</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_taxTurnAnnul" name="auditedData.table7.taxTurnAnnul" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table7.taxTurnAnnul}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left"  colspan="2">(G)Unreconciled taxable turnover (F-E)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" colspan="2" style="width: 79%;" readonly="true" class="form-control tpone-input" id="auditedData_table7_unrecTaxTurn" name="auditedData.table7.unrecTaxTurn" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table7.unrecTaxTurn}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									</tbody>
									</table></div></div></div></div>
									<div class="card">
								    <div class="card-header" id="headinginnerFour">
								      <h5 class="mb-0">
								        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFour" aria-expanded="true" aria-controls="collapseinnerFour">
								      (8) Reasons for Un - Reconciled difference in taxable turnover
									   </button>
								      </h5>
								    </div>
								    <div id="collapseinnerFour" class="collapse show" aria-labelledby="headinginnerFour" data-parent="#accordion">
								      <div class="card-body p-2">
								      <div class="customtable db-ca-gst tabtable2 mt-2">    
									<table class="display row-border dataTable meterialform">
									<thead>
									<tr>
										<th class="text-left" colspan="4">Reasons for Un - Reconciled difference in taxable turnover</th>
									</tr>
									</thead>
									<tbody id="table8Body">
									<tr>
										<td class="text-left" >(A)Reason 1 <input type="hidden" name="auditedData.table8.rsn[0].number" value="RSN1" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" style="width: 100%;" readonly="true" class="form-control tpone-input" id="auditedData_table8_rsn[0]_desc" name="auditedData.table8.rsn[0].desc" placeholder="Reason Description" value="${invoice.auditedData.table8.rsn[0].desc}"/><div class="help-block with-errors"></div></td>
										<td></td>
									</tr>
									<tr>
										<td class="text-left" >(B)Reason 2 <input type="hidden" name="auditedData.table8.rsn[1].number" value="RSN2" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" style="width: 100%;" readonly="true" class="form-control tpone-input" id="auditedData_table8_rsn[1]_desc" name="auditedData.table8.rsn[1].desc" placeholder="Reason Description" value="${invoice.auditedData.table8.rsn[1].desc}"/><div class="help-block with-errors"></div></td>
										<td></td>
									</tr>
									<tr>
										<td class="text-left" >(C)Reason 3 <input type="hidden" name="auditedData.table8.rsn[2].number" value="RSN3" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" readonly="true" style="width: 100%;" class="form-control tpone-input" id="auditedData_table8_rsn[2]_desc" name="auditedData.table8.rsn[2].desc" placeholder="Reason Description" value="${invoice.auditedData.table8.rsn[2].desc}" /><div class="help-block with-errors"></div></td>
									<td><input type="button" class="btn btn-blue-dark addmore" onclick="addReconTurnover('table8','table8Body')" value="Add"/></td>
									</tr>
								</tbody>
							</table>
							</div></div></div></div>
						</div>
						</div>
						
					    <div id="main_tab2" class="tab-pane fade">
					      <div class="group upload-btn">
							<div class="mb-2">Reconciliation of tax paid<div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_2"> Help Guide</div><span class="pull-right"> <a href="#" class="btn btn-sm btn-blue-dark tptwo-edit"  onClick="clickEdit('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');">Edit</a> <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickSave('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tptwo-cancel" onClick="clickCancel('.tptwo-save', '.tptwo-cancel', '.tptwo-edit','.tptwo-input', '.addmorewrap',2);" >Cancel</a></span></div>
						</div>
						<div id="accordion" class="inneracco">
						<div class="card">
						    <div class="card-header" id="headinginnerFive">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerFive" aria-expanded="true" aria-controls="collapseinnerFive">
						          (9)Reconciliation of rate wise liability and amount payable thereon
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerFive" class="collapse show" aria-labelledby="headinginnerFive" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable2 mt-2">     
						<div class="customtable db-ca-gst tabtable2 mt-2">       
    						<table id="dbTable2" class="dbTable2 display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Taxable Value</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">(A)5% <input type="hidden" name="auditedData.table9.rate[0].desc" value="5" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[0]_taxVal" name="auditedData.table9.rate[0].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[0].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[0]_cgst" name="auditedData.table9.rate[0].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[0].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[0]_sgst" name="auditedData.table9.rate[0].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[0].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[0]_igst" name="auditedData.table9.rate[0].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[0].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[0]_cess" name="auditedData.table9.rate[0].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[0].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(B)5% (RC)<input type="hidden" name="auditedData.table9.rate[1].desc" value="5RC" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[1]_taxVal" name="auditedData.table9.rate[1].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[1].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[1]_cgst" name="auditedData.table9.rate[1].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[1].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[1]_sgst" name="auditedData.table9.rate[1].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[1].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[1]_igst" name="auditedData.table9.rate[1].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[1].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[1]_cess" name="auditedData.table9.rate[1].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[1].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<td class="text-left">(C)12%<input type="hidden" name="auditedData.table9.rate[2].desc" value="12" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[2]_taxVal" name="auditedData.table9.rate[2].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[2].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[2]_cgst" name="auditedData.table9.rate[2].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[2].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[2]_sgst" name="auditedData.table9.rate[2].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[2].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[2]_igst" name="auditedData.table9.rate[2].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[2].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[2]_cess" name="auditedData.table9.rate[2].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[2].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
										<tr>
										<td class="text-left">(D)12% (RC)<input type="hidden" name="auditedData.table9.rate[3].desc" value="12RC" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[3]_taxVal" name="auditedData.table9.rate[3].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[3].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[3]_cgst" name="auditedData.table9.rate[3].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[3].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[3]_sgst" name="auditedData.table9.rate[3].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[3].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[3]_igst" name="auditedData.table9.rate[3].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[3].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[3]_cess" name="auditedData.table9.rate[3].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[3].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(E)18%<input type="hidden" name="auditedData.table9.rate[4].desc" value="18" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[4]_taxVal" name="auditedData.table9.rate[4].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[4].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[4]_cgst" name="auditedData.table9.rate[4].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[4].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[4]_sgst" name="auditedData.table9.rate[4].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[4].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[4]_igst" name="auditedData.table9.rate[4].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[4].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[4]_cess" name="auditedData.table9.rate[4].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[4].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<td class="text-left">(F)18% (RC)<input type="hidden" name="auditedData.table9.rate[5].desc" value="18RC" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[5]_taxVal" name="auditedData.table9.rate[5].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[5].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[5]_cgst" name="auditedData.table9.rate[5].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[5].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[5]_sgst" name="auditedData.table9.rate[5].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[5].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[5]_igst" name="auditedData.table9.rate[5].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[5].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[5]_cess" name="auditedData.table9.rate[5].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[5].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(G)28%<input type="hidden" name="auditedData.table9.rate[6].desc" value="28" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[6]_taxVal" name="auditedData.table9.rate[6].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[6].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[6]_cgst" name="auditedData.table9.rate[6].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[6].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[6]_sgst" name="auditedData.table9.rate[6].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[6].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[6]_igst" name="auditedData.table9.rate[6].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[6].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[6]_cess" name="auditedData.table9.rate[6].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[6].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(H)28% (RC)<input type="hidden" name="auditedData.table9.rate[7].desc" value="28RC" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[7]_taxVal" name="auditedData.table9.rate[7].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[7].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[7]_cgst" name="auditedData.table9.rate[7].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[7].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[7]_sgst" name="auditedData.table9.rate[7].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[7].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[7]_igst" name="auditedData.table9.rate[7].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[7].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[7]_cess" name="auditedData.table9.rate[7].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[7].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<td class="text-left">(I)3%<input type="hidden" name="auditedData.table9.rate[1].desc" value="3" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[8]_taxVal" name="auditedData.table9.rate[8].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[8].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[8]_cgst" name="auditedData.table9.rate[8].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[8].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[8]_sgst" name="auditedData.table9.rate[8].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[8].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[8]_igst" name="auditedData.table9.rate[8].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[8].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[8]_cess" name="auditedData.table9.rate[8].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[8].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
										<tr>
										<td class="text-left">(J)0.25%<input type="hidden" name="auditedData.table9.rate[1].desc" value="0.25" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[9]_taxVal" name="auditedData.table9.rate[9].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[9].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[9]_cgst" name="auditedData.table9.rate[9].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[9].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[9]_sgst" name="auditedData.table9.rate[9].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[9].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[9]_igst" name="auditedData.table9.rate[9].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[9].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[9]_cess" name="auditedData.table9.rate[9].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[9].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(K)0.10%<input type="hidden" name="auditedData.table9.rate[1].desc" value="0.10" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[10]_taxVal" name="auditedData.table9.rate[10].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[10].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[10]_cgst" name="auditedData.table9.rate[10].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[10].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[10]_sgst" name="auditedData.table9.rate[10].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[10].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[10]_igst" name="auditedData.table9.rate[10].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[10].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_rate[10]_cess" name="auditedData.table9.rate[10].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.rate[10].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<td class="text-left">(L)Interest</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_inter_cgst" name="auditedData.table9.inter.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.inter.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_inter_sgst" name="auditedData.table9.inter.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.inter.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_inter_igst" name="auditedData.table9.inter.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.inter.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_inter_cess" name="auditedData.table9.inter.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.inter.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(M)Late Fee</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_lateFee_cgst" name="auditedData.table9.lateFee.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.lateFee.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_lateFee_sgst" name="auditedData.table9.lateFee.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.lateFee.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_lateFee_igst" name="auditedData.table9.lateFee.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.lateFee.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_lateFee_cess" name="auditedData.table9.lateFee.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.lateFee.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(N)Penalty</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_pen_cgst" name="auditedData.table9.pen.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.pen.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_pen_sgst" name="auditedData.table9.pen.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.pen.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_pen_igst" name="auditedData.table9.pen.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.pen.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_pen_cess" name="auditedData.table9.pen.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.pen.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(O)Others</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_oth_cgst" name="auditedData.table9.oth.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.oth.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_oth_sgst" name="auditedData.table9.oth.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.oth.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_oth_igst" name="auditedData.table9.oth.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.oth.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_oth_cess" name="auditedData.table9.oth.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.oth.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left" colspan="2">(P)Total amount to be paid as per tables above</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPayable_cgst" name="auditedData.table9.totAmtPayable.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPayable.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPayable_sgst" name="auditedData.table9.totAmtPayable.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPayable.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPayable_igst" name="auditedData.table9.totAmtPayable.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPayable.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPayable_cess" name="auditedData.table9.totAmtPayable.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPayable.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" colspan="2">(Q)Total amount paid as declared in Annual Return (GSTR 9)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPaid_cgst" name="auditedData.table9.totAmtPaid.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPaid.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPaid_sgst" name="auditedData.table9.totAmtPaid.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPaid.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPaid_igst" name="auditedData.table9.totAmtPaid.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPaid.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_totAmtPaid_cess" name="auditedData.table9.totAmtPaid.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.totAmtPaid.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" colspan="2">(R)Un-reconciled payment of amount</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_unrecAmt_cgst" name="auditedData.table9.unrecAmt.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.unrecAmt.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_unrecAmt_sgst" name="auditedData.table9.unrecAmt.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.unrecAmt.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_unrecAmt_igst" name="auditedData.table9.unrecAmt.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.unrecAmt.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table9_unrecAmt_cess" name="auditedData.table9.unrecAmt.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table9.unrecAmt.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									</tbody></table></div></div></div></div>
									<div class="card">
							    <div class="card-header" id="headinginnerSix">
							      <h5 class="mb-0">
							        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerSix" aria-expanded="true" aria-controls="collapseinnerSix">
							       (10)Reasons for un-reconciled payment of amount
							        </button>
							      </h5>
							    </div>
							    <div id="collapseinnerSix" class="collapse show" aria-labelledby="headinginnerSix" data-parent="#accordion">
							      <div class="card-body p-2">
							       <div class="customtable db-ca-gst tabtable9 mt-2">
								<table id="dbTable7" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
									<thead>
									<tr>
										<th colspan="4">Reasons for un-reconciled payment of amount</th>
									</tr>
									</thead>
									<tbody id="table10Body">
									<tr>
										<td class="text-left" >(A)Reason 1 <input type="hidden" name="auditedData.table10.rsn[0].number" value="RSN1" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table10_rsn[0]_desc" name="auditedData.table10.rsn[0].desc" placeholder="Reason Description"  value="${invoice.auditedData.table10.rsn[0].desc}"/><div class="help-block with-errors"></div></td>
									<td></td>
									</tr>
									<tr>
										<td class="text-left" >(B)Reason 2 <input type="hidden" name="auditedData.table10.rsn[1].number" value="RSN2" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table10_rsn[1]_desc" name="auditedData.table10.rsn[1].desc" placeholder="Reason Description" value="${invoice.auditedData.table10.rsn[1].desc}"/><div class="help-block with-errors"></div></td>
									<td></td>
									</tr>
									<tr>
										<td class="text-left">(C)Reason 3 <input type="hidden" name="auditedData.table10.rsn[2].number" value="RSN3" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table10_rsn[2]_desc" name="auditedData.table10.rsn[2].desc" placeholder="Reason Description" value="${invoice.auditedData.table10.rsn[2].desc}"/><div class="help-block with-errors"></div></td>
										<td><input type="button" class="btn btn-blue-dark addmore" onclick="addReconTurnover('table10','table10Body')" value="Add"/></td>
									</tr>
								</tbody>
								</table></div></div></div></div>
								<div class="card">
						    <div class="card-header" id="headinginnerSeven">
						      <h5 class="mb-0">
						        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerSeven" aria-expanded="true" aria-controls="collapseinnerSeven">
						        (11) Additional amount payable but not paid (due to reasons specified under Tables 6,8 and 10 above)
					           </button>
						      </h5>
						    </div>
						    <div id="collapseinnerSeven" class="collapse show" aria-labelledby="headinginnerSeven" data-parent="#accordion">
						      <div class="card-body p-2">
						      <div class="customtable db-ca-gst tabtable4 mt-2">
							<table id="dbTable7" class="dbTable7 display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Taxable Value</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">5%<input type="hidden" name="auditedData.table11.rate[0].desc" value="5" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[0]_taxVal" name="auditedData.table11.rate[0].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[0].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[0]_cgst" name="auditedData.table11.rate[0].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[0].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[0]_sgst" name="auditedData.table11.rate[0].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[0].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[0]_igst" name="auditedData.table11.rate[0].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[0].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[0]_cess" name="auditedData.table11.rate[0].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[0].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<td class="text-left">12%<input type="hidden" name="auditedData.table11.rate[1].desc" value="12" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[1]_taxVal" name="auditedData.table11.rate[1].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[1].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[1]_cgst" name="auditedData.table11.rate[1].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[1].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[1]_sgst" name="auditedData.table11.rate[1].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[1].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[1]_igst" name="auditedData.table11.rate[1].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[1].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[1]_cess" name="auditedData.table11.rate[1].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[1].cess}" />' /><div class="help-block with-errors"></div></td>
									<tr>
										<td class="text-left">18%<input type="hidden" name="auditedData.table11.rate[2].desc" value="18" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[2]_taxVal" name="auditedData.table11.rate[2].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[2].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[2]_cgst" name="auditedData.table11.rate[2].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[2].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[2]_sgst" name="auditedData.table11.rate[2].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[2].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[2]_igst" name="auditedData.table11.rate[2].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[2].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[2]_cess" name="auditedData.table11.rate[2].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[2].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<tr>
										<td class="text-left">28%<input type="hidden" name="auditedData.table11.rate[3].desc" value="28" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[3]_taxVal" name="auditedData.table11.rate[3].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[3].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[3]_cgst" name="auditedData.table11.rate[3].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[3].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[3]_sgst" name="auditedData.table11.rate[3].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[3].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[3]_igst" name="auditedData.table11.rate[3].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[3].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[3]_cess" name="auditedData.table11.rate[3].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[3].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
									<td class="text-left">3%<input type="hidden" name="auditedData.table11.rate[4].desc" value="3" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[4]_taxVal" name="auditedData.table11.rate[4].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[4].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[4]_cgst" name="auditedData.table11.rate[4].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[4].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[4]_sgst" name="auditedData.table11.rate[4].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[4].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[4]_igst" name="auditedData.table11.rate[4].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[4].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[4]_cess" name="auditedData.table11.rate[4].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[4].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
										<tr>
										<td class="text-left">0.25%<input type="hidden" name="auditedData.table11.rate[5].desc" value="0.25" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[5]_taxVal" name="auditedData.table11.rate[5].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[5].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[5]_cgst" name="auditedData.table11.rate[5].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[5].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[5]_sgst" name="auditedData.table11.rate[5].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[5].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[5]_igst" name="auditedData.table11.rate[5].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[5].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[5]_cess" name="auditedData.table11.rate[5].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[5].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">0.10%<input type="hidden" name="auditedData.table11.rate[6].desc" value="0.1" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[6]_taxVal" name="auditedData.table11.rate[6].taxVal" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[6].taxVal}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[6]_cgst" name="auditedData.table11.rate[6].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[6].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[6]_sgst" name="auditedData.table11.rate[6].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[6].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[6]_igst" name="auditedData.table11.rate[6].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[6].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_rate[6]_cess" name="auditedData.table11.rate[6].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.rate[6].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Interest</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_inter_cgst" name="auditedData.table11.inter.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.inter.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_inter_sgst" name="auditedData.table11.inter.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.inter.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_inter_igst" name="auditedData.table11.inter.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.inter.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_inter_cess" name="auditedData.table11.inter.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.inter.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Late Fee</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_lateFee_cgst" name="auditedData.table11.lateFee.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.lateFee.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_lateFee_sgst" name="auditedData.table11.lateFee.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.lateFee.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_lateFee_igst" name="auditedData.table11.lateFee.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.lateFee.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_lateFee_cess" name="auditedData.table11.lateFee.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.lateFee.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Late Fee</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_pen_cgst" name="auditedData.table11.pen.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.pen.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_pen_sgst" name="auditedData.table11.pen.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.pen.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_pen_igst" name="auditedData.table11.pen.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.pen.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_pen_cess" name="auditedData.table11.pen.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.pen.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Others</td>
										<td class="text-right form-group gst-3b-error"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_oth_cgst" name="auditedData.table11.oth.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.oth.cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_oth_sgst" name="auditedData.table11.oth.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.oth.sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_oth_igst" name="auditedData.table11.oth.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.oth.igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table11_oth_cess" name="auditedData.table11.oth.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table11.oth.cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
							</div></div></div></div>
						</div>
					    </div></div>
					    <div id="main_tab3" class="tab-pane fade">
					     <div class="group upload-btn">
							<div class="mb-2"> 9C.4 Reconciliation of Input Tax Credit (ITC)<div class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_3"> Help Guide</div><span class="pull-right"> <a href="#" class="btn btn-sm  btn-blue-dark tpthree-edit"  onClick="clickEdit('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpthree-cancel" onClick="clickSave('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpthree-cancel" onClick="clickCancel('.tpthree-save', '.tpthree-cancel', '.tpthree-edit','.tpthree-input','',3);" >Cancel</a></span></div>
						</div>
						<div id="accordion" class="inneracco">
						  <div class="card">
						    <div class="card-header" id="headinginnerEight">
						      <h5 class="mb-0">
						        <button class="btn acco-btn" data-toggle="collapse" data-target="#collapseinnerEight" aria-expanded="true" aria-controls="collapseinnerEight">
						      (12) Reconciliation of Net Input Tax Credit (ITC)
						        </button>
						      </h5>
						    </div>
						    <div id="collapseinnerEight" class="collapse show" aria-labelledby="headinginnerEight" data-parent="#accordion">
						      <div class="card-body p-2">
							<div class="customtable db-ca-gst tabtable3 mt-2">
							<table id="dbTable3" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" colspan="3">Reconciliation of Net Input Tax Credit (ITC)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-right form-group gst-3b-error" colspan="2">(A)ITC availed as per audited Annual Financial Statement for the State/ UT (For multi-GSTIN units under same PAN this should be derived from books of accounts)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table12_itcAvail" name="auditedData.table12.itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table12.itcAvail}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">(B)ITC booked in earlier Financial Years claimed in current Financial Year</td>
										<td>(+)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table12_itcBookEarl" name="auditedData.table12.itcBookEarl" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table12.itcBookEarl}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error">(C)ITC booked in current Financial Year to be claimed in subsequent Financial Years</td>
										<td>(-)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table12_itcBookCurr" name="auditedData.table12.itcBookCurr" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table12.itcBookCurr}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-right form-group gst-3b-error" colspan="2">(D)ITC availed as per audited financial statements or books of account </td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table12_itcAvailAudited" name="auditedData.table12.itcAvailAudited" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table12.itcAvailAudited}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error" colspan="2">(E)ITC claimed in Annual Return (GSTR9)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table12_itcClaim" name="auditedData.table12.itcClaim" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table12.itcClaim}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-right form-group gst-3b-error" colspan="2">(F)Un-reconciled ITC </td>
										<td class="text-right form-group gst-3b-error"><input type="text"  class="form-control tpthree-input elg_itc" id="auditedData_table12_unrecItc" name="auditedData.table12.unrecItc" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table12.unrecItc}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									</tbody>
									</table></div></div></div></div>
									<div class="card">
								    <div class="card-header" id="headinginnerNine">
								      <h5 class="mb-0">
								        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerNine" aria-expanded="true" aria-controls="collapseinnerNine">
								        (13)Reasons for un-reconciled difference in ITC
									   </button>
								      </h5>
								    </div>
								    <div id="collapseinnerNine" class="collapse show" aria-labelledby="headinginnerNine" data-parent="#accordion">
								      <div class="card-body p-2">
								      <div class="customtable db-ca-gst tabtable8 mt-2">
									<table id="dbTable9" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
									<thead>
									<tr><th colspan="4">Reasons for un-reconciled difference in ITC</th></tr>
									</thead>
									<tbody id="table13Body">
									<tr>
										<td class="text-left">(A) Reason 1 <input type="hidden" name="auditedData.table13.rsn[0].number" value="RSN1" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table13_rsn[0]_desc" name="auditedData.table13.rsn[0].desc" placeholder="Reason desription" value="${invoice.auditedData.table13.rsn[0].desc}"/><div class="help-block with-errors"></div></td>
										<td></td>
									</tr>
									<tr>
										<td class="text-left">(B) Reason 2 <input type="hidden" name="auditedData.table13.rsn[1].number" value="RSN2" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table13_rsn[1]_desc" name="auditedData.table13.rsn[1].desc" placeholder="Reason desription" value="${invoice.auditedData.table13.rsn[1].desc}"/><div class="help-block with-errors"></div></td>
										<td></td>
									</tr>
									<tr>
										<td class="text-left">(C) Reason 3 <input type="hidden" name="auditedData.table13.rsn[2].number" value="RSN3" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table13_rsn[2]_desc" name="auditedData.table13.rsn[2].desc" placeholder="Reason desription" value="${invoice.auditedData.table13.rsn[2].desc}"/><div class="help-block with-errors"></div></td>
										<td><input type="button" class="btn btn-blue-dark addmore" onclick="addReconTurnover('table13','table13Body')" value="Add"/></td>
									</tr>
									</tbody>
									</table></div></div></div></div>
									<div class="card">
									    <div class="card-header" id="headinginnerTen">
									      <h5 class="mb-0">
									        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTen" aria-expanded="true" aria-controls="collapseinnerTen">
									         (14)Reconciliation of ITC declared in Annual Return (GSTR9) with ITC availed on expenses as per audited Annual Financial Statement or books of account
									        </button>
									      </h5>
									    </div>
									    <div id="collapseinnerTen" class="collapse show" aria-labelledby="headinginnerTen" data-parent="#accordion">
									      <div class="card-body p-2">
									      <div class="customtable db-ca-gst tabtable10 mt-2">
									<table id="dbTable8" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
									<thead>
										<tr>
											<th>Description</th>
											<th>Value</th>
											<th>Amount of Total ITC </th>
											<th>Amount of eligible ITC availed</th>
										</tr>
									</thead>
									<tbody>
									<tr>
										<td class="text-left">(A)Purchases<input type="hidden" name="auditedData.table14.items[0].desc" value="PURCHS" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[0]_val" name="auditedData.table14.items[0].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[0].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[0]_itcAmt" name="auditedData.table14.items[0].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[0].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[0]_itcAvail" name="auditedData.table14.items[0].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[0].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(B)Freight / Carriage <input type="hidden" name="auditedData.table14.items[1].desc" value="FRTCAR" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[1]_val" name="auditedData.table14.items[1].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[1].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[1]_itcAmt" name="auditedData.table14.items[1].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[1].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[1]_itcAvail" name="auditedData.table14.items[1].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[1].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(C)Power and Fuel<input type="hidden" name="auditedData.table14.items[2].desc" value="POFUEL" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[2]_val" name="auditedData.table14.items[2].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[2].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[2]_itcAmt" name="auditedData.table14.items[2].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[2].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[2]_itcAvail" name="auditedData.table14.items[2].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[2].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(D)Imported goods (Including received from SEZs)<input type="hidden" name="auditedData.table14.items[3].desc" value="IMPGDS" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[3]_val" name="auditedData.table14.items[3].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[3].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[3]_itcAmt" name="auditedData.table14.items[3].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[3].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[3]_itcAvail" name="auditedData.table14.items[3].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[3].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(E)Rent and Insurance<input type="hidden" name="auditedData.table14.items[4].desc" value="RNTEXP" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[4]_val" name="auditedData.table14.items[4].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[4].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[4]_itcAmt" name="auditedData.table14.items[4].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[4].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[4]_itcAvail" name="auditedData.table14.items[4].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[4].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(F)Goods lost, stolen, destroyed, written off or disposed of by way of gift or free samples<input type="hidden" name="auditedData.table14.items[5].desc" value="GDSLST" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[5]_val" name="auditedData.table14.items[5].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[5].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[5]_itcAmt" name="auditedData.table14.items[5].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[5].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[5]_itcAvail" name="auditedData.table14.items[5].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[5].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(G)Royalties<input type="hidden" name="auditedData.table14.items[6].desc" value="RYLTES" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[6]_val" name="auditedData.table14.items[6].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[6].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[6]_itcAmt" name="auditedData.table14.items[6].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[6].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[6]_itcAvail" name="auditedData.table14.items[6].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[6].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(H)Employees' Cost (Salaries, wages,Bonus etc.)<input type="hidden" name="auditedData.table14.items[7].desc" value="EMPCST" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[7]_val" name="auditedData.table14.items[7].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[7].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[7]_itcAmt" name="auditedData.table14.items[7].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[7].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[7]_itcAvail" name="auditedData.table14.items[7].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[7].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(I)Conveyance charges<input type="hidden" name="auditedData.table14.items[8].desc" value="CONCHR" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[8]_val" name="auditedData.table14.items[8].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[8].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[8]_itcAmt" name="auditedData.table14.items[8].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[8].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[8]_itcAvail" name="auditedData.table14.items[8].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[8].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(J)Bank Charges <input type="hidden" name="auditedData.table14.items[9].desc" value="BNKCHR" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[9]_val" name="auditedData.table14.items[9].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[9].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[9]_itcAmt" name="auditedData.table14.items[9].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[9].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[9]_itcAvail" name="auditedData.table14.items[9].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[9].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(K)Entertainment charges<input type="hidden" name="auditedData.table14.items[10].desc" value="ENTCHR" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[10]_val" name="auditedData.table14.items[10].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[10].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[10]_itcAmt" name="auditedData.table14.items[10].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[10].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[10]_itcAvail" name="auditedData.table14.items[10].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[10].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(L)Stationery Expenses (including postage etc.)<input type="hidden" name="auditedData.table14.items[11].desc" value="STNEXP" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[11]_val" name="auditedData.table14.items[11].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[11].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[11]_itcAmt" name="auditedData.table14.items[11].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[11].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[11]_itcAvail" name="auditedData.table14.items[11].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[11].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(M)Repair and Maintenance<input type="hidden" name="auditedData.table14.items[12].desc" value="RPRMNT" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[12]_val" name="auditedData.table14.items[12].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[12].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[12]_itcAmt" name="auditedData.table14.items[12].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[12].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[12]_itcAvail" name="auditedData.table14.items[12].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[12].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(N)Other Miscellaneous expenses<input type="hidden" name="auditedData.table14.items[13].desc" value="OTRMIS" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[13]_val" name="auditedData.table14.items[13].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[13].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[13]_itcAmt" name="auditedData.table14.items[13].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[13].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[13]_itcAvail" name="auditedData.table14.items[13].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[13].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(O)Capital goods<input type="hidden" name="auditedData.table14.items[14].desc" value="CAPGDS" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[14]_val" name="auditedData.table14.items[14].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[14].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[14]_itcAmt" name="auditedData.table14.items[14].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[14].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[14]_itcAvail" name="auditedData.table14.items[14].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[14].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(P)Any other expense 1<input type="hidden" name="auditedData.table14.items[15].desc" value="ANEXP1" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[15]_val" name="auditedData.table14.items[15].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[15].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[15]_itcAmt" name="auditedData.table14.items[15].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[15].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[15]_itcAvail" name="auditedData.table14.items[15].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[15].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(Q)Any other expense 2<input type="hidden" name="auditedData.table14.items[16].desc" value="ANEXP2" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[16]_val" name="auditedData.table14.items[16].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[16].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[16]_itcAmt" name="auditedData.table14.items[16].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[16].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[16]_itcAvail" name="auditedData.table14.items[16].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[16].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(R)Any other expense 3<input type="hidden" name="auditedData.table14.items[17].desc" value="ANEXP3" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[17]_val" name="auditedData.table14.items[17].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[17].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[17]_itcAmt" name="auditedData.table14.items[17].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[17].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[17]_itcAvail" name="auditedData.table14.items[17].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[17].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(S)Any other expense 4<input type="hidden" name="auditedData.table14.items[18].desc" value="ANEXP4" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[18]_val" name="auditedData.table14.items[18].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[18].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[18]_itcAmt" name="auditedData.table14.items[18].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[18].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[18]_itcAvail" name="auditedData.table14.items[18].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[18].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">(T)Any other expense 5<input type="hidden" name="auditedData.table14.items[19].desc" value="ANEXP5" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[19]_val" name="auditedData.table14.items[19].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[19].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[19]_itcAmt" name="auditedData.table14.items[19].itcAmt" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[19].itcAmt}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_items[19]_itcAvail" name="auditedData.table14.items[19].itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.items[19].itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr class="auto-row">
										<td class="text-left" colspan="3">(U)Total amount of eligible ITC availed</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_totEligItc_itcAvail" name="auditedData.table14.totEligItc.itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.totEligItc.itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" colspan="3">(V)ITC claimed in Annual Return (GSTR9)</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_itcClaim_itcAvail" name="auditedData.table14.itcClaim.itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.itcClaim.itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left" colspan="3">(W)Un-reconciled ITC</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tptwo-input" id="auditedData_table14_unrecItc_itcAvail" name="auditedData.table14.unrecItc.itcAvail" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table14.unrecItc.itcAvail}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									</tbody>
									</table></div></div></div></div>
									<div class="card">
									    <div class="card-header" id="headinginnerEleven">
									      <h5 class="mb-0">
									        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerEleven" aria-expanded="true" aria-controls="collapseinnerEleven">
									         (15)Reasons for un-reconciled difference in ITC
									        </button>
									      </h5>
									    </div>
									    <div id="collapseinnerEleven" class="collapse show" aria-labelledby="headinginnerEleven" data-parent="#accordion">
									      <div class="card-body p-2">
									      <div class="customtable db-ca-gst tabtable11 mt-2">
									<table id="dbTable8" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
									<thead>
									<tr><th colspan="4">Reasons for un-reconciled difference in ITC</th></tr>
									</thead>
									<tbody id="table15Body">
									<tr>
										<td class="text-left">(A) Reason 1 <input type="hidden" name="auditedData.table15.rsn[0].number" value="RSN1" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table15_rsn[0]_desc" name="auditedData.table15.rsn[0].desc" placeholder="Reason Description"  value="${invoice.auditedData.table15.rsn[0].desc}"/><div class="help-block with-errors"></div></td>
										<td></td>
									</tr>
									<tr>
										<td class="text-left">(B) Reason 2 <input type="hidden" name="auditedData.table15.rsn[1].number" value="RSN2" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table15_rsn[1]_desc" name="auditedData.table15.rsn[1].desc" placeholder="Reason Description"  value="${invoice.auditedData.table15.rsn[1].desc}"/><div class="help-block with-errors"></div></td>
										<td></td>
									</tr>
									<tr>
										<td class="text-left">(C) Reason 3 <input type="hidden" name="auditedData.table15.rsn[2].number" value="RSN3" /></td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table15_rsn[2]_desc" name="auditedData.table15.rsn[2].desc" placeholder="Reason Description"  value="${invoice.auditedData.table15.rsn[2].desc}"/><div class="help-block with-errors"></div></td>
										<td><input type="button" class="btn btn-blue-dark addmore" onclick="addReconTurnover('table15','table15Body')" value="Add"/></td>
									</tr>
									</tbody>
									</table></div></div></div></div>
									<div class="card">
								    <div class="card-header" id="headinginnerTwelve">
								      <h5 class="mb-0">
								        <button class="btn acco-btn collapsed" data-toggle="collapse" data-target="#collapseinnerTwelve" aria-expanded="true" aria-controls="collapseinnerTwelve">
								         (16)Tax payable on un-reconciled difference in ITC (due to reasons specified in 13 and 15 above)
								        </button>
								      </h5>
								    </div>
								    <div id="collapseinnerTwelve" class="collapse show" aria-labelledby="headinginnerTwelve" data-parent="#accordion">
								      <div class="card-body p-2">
								      <div class="customtable db-ca-gst tabtable12 mt-2">
									<table id="dbTable10" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
									<thead>
									<tr>
									<th>Description</th>
									<th>Amount Payable</th>
									</tr>
									</thead>
									<tbody>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error" colspan="2"><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table16_cgst" name="auditedData.table16.cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table16.cgst}" />'/><div class="help-block with-errors"></div></td>
										
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table16_sgst" name="auditedData.table16.sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table16.sgst}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table16_igst" name="auditedData.table16.igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table16.igst}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Cess</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table16_cess" name="auditedData.table16.cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table16.cess}" />'/><div class="help-block with-errors"></div></td>
										
									</tr>
									<tr>
										<td class="text-left">Interest</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table16_inter" name="auditedData.table16.inter" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table16.inter}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Penalty</td>
										<td class="text-right form-group gst-3b-error" ><input type="text" class="form-control tpthree-input elg_itc" id="auditedData_table16_pen" name="auditedData.table16.pen" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.table16.pen}" />'/><div class="help-block with-errors"></div></td>
									</tr>
									</tbody>
									</table>
								</div></div></div></div></div>
						</div>
					    <div id="main_tab4" class="tab-pane fade">
					      <div class="group upload-btn">
							<div class="mb-2 row"> <span class="col-md-10">9C.5 Auditor's recommendation on additional Liability due to non-reconciliation</span><span class="col-md-2 pull-right"><span class="helpguide pull-right" data-toggle="modal" data-target="#helpguideModal_4"> Help Guide</span><span class="pull-right"><a href="#" class="btn btn-sm  btn-blue-dark tpfour-edit"  onClick="clickEdit('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');">Edit</a>  <a href="#" class="btn btn-sm  btn-blue-dark tpfour-cancel" onClick="clickSave('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input');" >Save</a> <a href="#" class="btn btn-sm  btn-blue-dark tpfour-cancel" onClick="clickCancel('.tpfour-save', '.tpfour-cancel', '.tpfour-edit','.tpfour-input','',4);" >Cancel</a></span></span></div>
						</div>
						<div class="customtable db-ca-gst tabtable9 mt-2">
							<table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th class="text-left" width="30%">Description</th>
										<th class="text-left">Value(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Central Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">State/UT Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">Integrated Tax(<i class="fa fa-rupee"></i>)</th>
										<th class="text-left">CESS(<i class="fa fa-rupee"></i>)</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="text-left">5%<input type="hidden" name="auditedData.addLiab.taxPay[0].desc" value="5" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[0]_val" name="auditedData.addLiab.taxPay[0].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[0].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[0]_cgst" name="auditedData.addLiab.taxPay[0].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[0].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[0]_sgst" name="auditedData.addLiab.taxPay[0].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[0].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[0]_igst" name="auditedData.addLiab.taxPay[0].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[0].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[0]_cess" name="auditedData.addLiab.taxPay[0].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[0].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">12%<input type="hidden" name="auditedData.addLiab.taxPay[1].desc" value="12" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[1]_val" name="auditedData.addLiab.taxPay[1].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[1].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[1]_cgst" name="auditedData.addLiab.taxPay[1].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[1].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[1]_sgst" name="auditedData.addLiab.taxPay[1].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[1].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[1]_igst" name="auditedData.addLiab.taxPay[1].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[1].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[1]_cess" name="auditedData.addLiab.taxPay[1].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[1].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">18%<input type="hidden" name="auditedData.addLiab.taxPay[2].desc" value="18" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[2]_val" name="auditedData.addLiab.taxPay[2].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[2].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[2]_cgst" name="auditedData.addLiab.taxPay[2].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[2].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[2]_sgst" name="auditedData.addLiab.taxPay[2].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[2].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[2]_igst" name="auditedData.addLiab.taxPay[2].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[2].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[2]_cess" name="auditedData.addLiab.taxPay[2].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[2].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">28%<input type="hidden" name="auditedData.addLiab.taxPay[3].desc" value="28" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[3]_val" name="auditedData.addLiab.taxPay[3].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[3].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[3]_cgst" name="auditedData.addLiab.taxPay[3].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[3].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[3]_sgst" name="auditedData.addLiab.taxPay[3].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[3].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[3]_igst" name="auditedData.addLiab.taxPay[3].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[3].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[3]_cess" name="auditedData.addLiab.taxPay[3].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[3].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">3%<input type="hidden" name="auditedData.addLiab.taxPay[4].desc" value="3" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[4]_val" name="auditedData.addLiab.taxPay[4].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[4].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[4]_cgst" name="auditedData.addLiab.taxPay[4].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[4].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[4]_sgst" name="auditedData.addLiab.taxPay[4].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[4].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[4]_igst" name="auditedData.addLiab.taxPay[4].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[4].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[4]_cess" name="auditedData.addLiab.taxPay[4].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[4].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">0.25%<input type="hidden" name="auditedData.addLiab.taxPay[5].desc" value="0.25" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[5]_val" name="auditedData.addLiab.taxPay[5].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[5].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[5]_cgst" name="auditedData.addLiab.taxPay[5].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[5].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[5]_sgst" name="auditedData.addLiab.taxPay[5].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[5].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[5]_igst" name="auditedData.addLiab.taxPay[5].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[5].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[5]_cess" name="auditedData.addLiab.taxPay[5].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[5].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">0.10%<input type="hidden" name="auditedData.addLiab.taxPay[6].desc" value="0.1" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[6]_val" name="auditedData.addLiab.taxPay[6].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[6].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[6]_cgst" name="auditedData.addLiab.taxPay[6].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[6].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[6]_sgst" name="auditedData.addLiab.taxPay[6].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[6].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[6]_igst" name="auditedData.addLiab.taxPay[6].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[6].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[6]_cess" name="auditedData.addLiab.taxPay[6].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[6].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Input Tax Credit<input type="hidden" name="auditedData.addLiab.taxPay[7].desc" value="INP" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[7]_val" name="auditedData.addLiab.taxPay[7].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[7].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[7]_cgst" name="auditedData.addLiab.taxPay[7].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[7].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[7]_sgst" name="auditedData.addLiab.taxPay[7].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[7].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[7]_igst" name="auditedData.addLiab.taxPay[7].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[7].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[7]_cess" name="auditedData.addLiab.taxPay[7].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[7].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Interest<input type="hidden" name="auditedData.addLiab.taxPay[8].desc" value="INT" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[8]_val" name="auditedData.addLiab.taxPay[8].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[8].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[8]_cgst" name="auditedData.addLiab.taxPay[8].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[8].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[8]_sgst" name="auditedData.addLiab.taxPay[8].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[8].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[8]_igst" name="auditedData.addLiab.taxPay[8].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[8].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[8]_cess" name="auditedData.addLiab.taxPay[8].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[8].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Late Fee<input type="hidden" name="auditedData.addLiab.taxPay[9].desc" value="LAT" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[9]_val" name="auditedData.addLiab.taxPay[9].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[9].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[9]_cgst" name="auditedData.addLiab.taxPay[9].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[9].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[9]_sgst" name="auditedData.addLiab.taxPay[9].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[9].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[9]_igst" name="auditedData.addLiab.taxPay[9].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[9].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[9]_cess" name="auditedData.addLiab.taxPay[9].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[9].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Penalty<input type="hidden" name="auditedData.addLiab.taxPay[10].desc" value="PEN" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[10]_val" name="auditedData.addLiab.taxPay[10].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[10].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[10]_cgst" name="auditedData.addLiab.taxPay[10].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[10].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[10]_sgst" name="auditedData.addLiab.taxPay[10].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[10].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[10]_igst" name="auditedData.addLiab.taxPay[10].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[10].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[10]_cess" name="auditedData.addLiab.taxPay[10].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[10].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Any other amount paid for supplies not included in Annual Return (GSTR 9)<input type="hidden" name="auditedData.addLiab.taxPay[11].desc" value="ANO" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[11]_val" name="auditedData.addLiab.taxPay[11].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[11].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[11]_cgst" name="auditedData.addLiab.taxPay[11].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[11].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[11]_sgst" name="auditedData.addLiab.taxPay[11].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[11].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[11]_igst" name="auditedData.addLiab.taxPay[11].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[11].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[11]_cess" name="auditedData.addLiab.taxPay[11].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[11].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Erroneous refund to be paid back<input type="hidden" name="auditedData.addLiab.taxPay[12].desc" value="ERP" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[12]_val" name="auditedData.addLiab.taxPay[12].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[12].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[12]_cgst" name="auditedData.addLiab.taxPay[12].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[12].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[12]_sgst" name="auditedData.addLiab.taxPay[12].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[12].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[12]_igst" name="auditedData.addLiab.taxPay[12].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[12].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[12]_cess" name="auditedData.addLiab.taxPay[12].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[12].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Outstanding demands to be settled<input type="hidden" name="auditedData.addLiab.taxPay[13].desc" value="OUD" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[13]_val" name="auditedData.addLiab.taxPay[13].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[13].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[13]_cgst" name="auditedData.addLiab.taxPay[13].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[13].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[13]_sgst" name="auditedData.addLiab.taxPay[13].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[13].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[13]_igst" name="auditedData.addLiab.taxPay[13].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[13].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[13]_cess" name="auditedData.addLiab.taxPay[13].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[13].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">Other (Pl. specify)<input type="hidden" name="auditedData.addLiab.taxPay[14].desc" value="OTH" /></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[14]_val" name="auditedData.addLiab.taxPay[14].val" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[14].val}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[14]_cgst" name="auditedData.addLiab.taxPay[14].cgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[14].cgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[14]_sgst" name="auditedData.addLiab.taxPay[14].sgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[14].sgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[14]_igst" name="auditedData.addLiab.taxPay[14].igst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[14].igst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="true" class="form-control tpfour-input" id="auditedData_addLiab_taxPay[14]_cess" name="auditedData.addLiab.taxPay[14].cess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.auditedData.addLiab.taxPay[14].cess}" />' /><div class="help-block with-errors"></div></td>
									</tr>
								</tbody>
							</table>
						</div>
					    </div>
					  </div>
						</div>
						</div>
				<div class="col-md-12 col-sm-12">
			<div id="accordion" class="accordion panel-group card" role="tablist" aria-multiselectable="true">
					<!--- 4 ---->
					<!--- 5 --->      
					
				</div>
				<div class="verify_anual_wrap">
					<h6>Verification</h6>
					<p>I hereby solemnly affirm and declare that the information given herein above is true and correct to the best of my knowledge and belief and nothing has been concealed there from and in case of any reduction in output tax liability the benefit thereof has been/will be passed on to the recipient of supply. </p>
					<h6>Place:</h6>
					<h6>Signatory:</h6>
					<h6>Date:</h6>
					<h6>Status:</h6>
				</div>
				</div>
							<div class="col-sm-12 mt-4 text-center">
								<c:if test='${not empty invoice.id}'>
								<input type="hidden" name="id" value="<c:out value="${invoice.id}"/>">
								<input type="hidden" name="auditedData.gstin" value="<c:out value="${invoice.auditedData.gstin}"/>">
								<input type="hidden" name="auditedData.fp" value="<c:out value="${invoice.auditedData.fp}"/>">
								</c:if>
								<c:if test='${empty invoice.id}'>
								<input type="hidden" name="auditedData.gstin" value="<c:out value="${client.gstnnumber}"/>">
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
					<form:form method="POST" id="sup4Form" data-toggle="validator" class="meterialform invoiceform" name="salesinvoceform" action="${contextPath}/saveoffliab/${returntype}/${usertype}/${month}/${year}" modelAttribute="invoice">
					<div class="col-md-12 col-sm-12">
						<div class="group upload-btn">
							<span class="pull-right">  <a href="#" class="btn btn-sm btn-blue-dark tpseven-edit <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>"" onClick="clickEdit('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');" style="margin-top:1px;padding:6px 10px!important">Edit</a>  <a href="#" class="btn btn-sm btn-blue-dark tpseven-save" style="display:none;margin-right: 3px;margin-top:1px;padding:6px 10px!important" onClick="clickSave('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input');">Save</a><a href="#" class="btn btn-sm btn-blue-dark tpseven-cancel" style="display:none;margin-top:1px;padding:6px 10px!important" onClick="clickCancel('.tpseven-save', '.tpseven-cancel', '.tpseven-edit','.tpseven-input','',7);">Cancel</a>
							<a href="#" class='btn btn-greendark <c:if test="${client.status eq statusFiled || empty invoice.id}">disable</c:if>' onclick="invokeOffsetLiab();">Offset Liability</a></span>
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
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].igst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].igst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.igstPdigst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdcgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.igstPdcgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.igstPdsgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.igstPdsgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].ipd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.offLiab.pdcash[0].ipd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].igstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2"  value="${invoice.offLiab.pdcash[0].igstIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].cgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].cgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cgstPdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.cgstPdigst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cgstPdcgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.cgstPdcgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cgstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cgstIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cgstLfeepd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cgstLfeepd}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].sgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].sgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.sgstPdigst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.sgstPdigst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.sgstPdsgst" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.sgstPdsgst}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].spd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].spd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].sgstIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].sgstIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].sgstLfeepd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].sgstLfeepd}" />' /><div class="help-block with-errors"></div></td>
									</tr>
									<tr>
										<td class="text-left">CESS</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[0].cess.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[0].cess.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pditc.cessPdcess" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pditc.cessPdcess}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cspd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cspd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[0].cessIntrpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[0].cessIntrpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td colspan="9"><span class="card-title">Reverse Charge</a></td>
									</tr>
									<tr>
										<td class="text-left">Integrated Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].igst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].igst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].ipd" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].ipd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">Central Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].cgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].cgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].cpd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].cpd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">State/UT Tax</td>
										<td class="text-right form-group gst-3b-error"><input type="text"  readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].sgst.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].sgst.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].spd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].spd}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
									</tr>
									<tr>
										<td class="text-left">CESS</td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.taxPayable[1].cess.tx" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.taxPayable[1].cess.tx}" />' /><div class="help-block with-errors"></div></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right"></td>
										<td class="text-right form-group gst-3b-error"><input type="text" readonly="readonly" class="form-control tpseven-input" name="offLiab.pdcash[1].cspd" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" data-error="Please enter number with max 2 decimals" placeholder="0.00"  maxlength="15" value='<fmt:formatNumber type="number" pattern="#############.##" minFractionDigits="2" maxFractionDigits="2" value="${invoice.offLiab.pdcash[1].cspd}" />' /><div class="help-block with-errors"></div></td>
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
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;
		var year = eDate.getFullYear();
		window.location.href = '${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year;
	}
 $('.card-header').click(function(){
  $(this).addClass('active').siblings().removeClass('active');
 });
</script>
</body>

 </html> 