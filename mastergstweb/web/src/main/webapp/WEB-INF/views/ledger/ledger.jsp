<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Ledger </title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<script src="${contextPath}/static/mastergst/js/common/datetimepicker.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>

<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>

<style>
.dataTables_scrollBody{border-bottom:none!important}
.dt-buttons {float: right; position: relative; top: -23px; color:white}
button.dt-button.buttons-excel.buttons-html5, button.dt-button.buttons-print,
	button.dt-button.buttons-pdf.buttons-html5 {
	background-color: #8ee3fe;
	color: #435a9e !important;
	font-size: 14px;
	padding: 5px 10px !important;
	border-radius: 4px;
	border: 1px;
	width: auto;
	cursor:pointer;
}
.dt-buttons :hover{background-color:#364365!important; color:white}
h4.f-18-b.pull-left {
	margin-bottom: 0px !important
}

</style>
</head>

<body class="body-cls">
   <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
		<!--- beadcrumb start -->
 		
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">				
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active"><c:choose><c:when test="${type eq 'cash'}">Cash Ledger</c:when><c:when test="${type eq 'credit'}">Credit Ledger</c:when><c:otherwise>Liability Ledger</c:otherwise></c:choose></li>
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

        <!--- breadcrumb end -->
        <div class="db-ca-wrap">
            <div class="container">

                <!-- Dashboard body start -->
                <div class="row">
                    <!-- dashboard left block begin -->
                    <div class="col-md-12 col-sm-12">
                        <h2 class="hdrtitle-18" style="display: inline-block;"><c:choose><c:when test="${type eq 'cash'}">Cash Ledger</c:when><c:when test="${type eq 'credit'}">Credit Ledger</c:when><c:otherwise>Liability Ledger</c:otherwise></c:choose></h2>
                        <a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
						<div class=" "></div>
                        <div class="invoice-wrap">
                            <div class="invoice-wrap-in">
                                <form class="meterialform invoiceform">
                                    <!-- invoice header start -->
									<span class="errormsg" id="fromdate_Msg" style="float:left"></span>
                                    <div class="invoice-hdr" style="margin-top:18px">
										<c:if test="${type ne 'tax'}">
										<div class="row">
											<div class="col-md-2 col-sm-12" style="padding-right:1px!important">
												<div class="lable-txt astrich">From</div>
												<div class="form-group">
													<span class="errormsg" id="fromDate_Msg"></span>
													<input type="text" id="fromdate" name="fromdate" class="date_field" aria-describedby="fromdate" pattern="^\d{1,2}-\d{1,2}-\d{4}$" />
													<label for="fromdate" class="control-label"></label>
													<div class="help-block with-errors"></div>
													<i class="bar"></i> </div>
											</div>
											<div class="col-md-2 col-sm-12">
												<div class="lable-txt astrich">To</div>
												<div class="form-group">
													<span class="errormsg" id="todate_Msg" ></span>
													<input type="text" id="todate" class="date_field" name="todate" aria-describedby="todate" pattern="^\d{1,2}-\d{1,2}-\d{4}$" />
													<label for="todate" class="control-label"></label>
													<div class="help-block with-errors"></div>
													<i class="bar"></i> </div>
											</div>
											<div class="col-md-2 col-sm-12"><a type="button" class="btn btn-blue-dark btn-sm" onClick="fetchDetails(this)" style="margin-top:20px;">Generate Report</a>
											</div>
										</div>
										</c:if>
										<c:if test="${type eq 'tax'}">
										<div class="row">
										<div class="col-md-3 col-sm-12">
												
												<div class="form-group">
												<div class="row">
												<div class="col-md-8 col-sm-12">
												<div class="lable-txt" id="ledge-month">Choose Month </div>
												</div>
												<div class="col-md-4 col-sm-12">
												<span class="datetimetxt" id="rtperiod">
													<input type="text" class="rtperiod" id="datetimepicker1" /><i class="fa fa-sort-desc"></i>
													<label for="datetimepicker1" class="control-label"></label>
													<div class="help-block with-errors"></div>
													<i class="bar"></i> 
													</span>
													</div>
													</div>
													</div>
											</div>
											<div class="col-md-2 col-sm-12"><a type="button" class="btn btn-blue-dark btn-sm" id="liabilityLedger" onClick="fetchLiabilityLedgerDetails(this)">Generate Report</a>
											</div>
									</div>

										</c:if>
                                    </div>
                                    <!-- end -->

                                    <div class="mb-3 mt-2"></div>
                                    <!-- item details start -->
                                    <!-- itemdetailsblock-wrap start -->
                                    <div class="itemdetailsblock-wrap">
                                         
                                         
                                        <!-- table start -->

					<div class=" ">
						<h4 class="f-18-b pull-left"><c:choose><c:when test="${type eq 'cash'}">Cash Ledger</c:when><c:when test="${type eq 'credit'}">Credit Ledger</c:when><c:otherwise>Liability Ledger</c:otherwise></c:choose> Details</h4>
					</div>
					<div class=" "></div>
					<div class="customtable invoicetable">
						<div id="invoicetable_wrapper" class="dataTables_wrapper">
							<table id="invoicetable" class="display row-border dataTable meterialform" role="grid" aria-describedby="invoicetable_info" style="width: 100%;" cellspacing="0" width="100%">
								<thead>
									<tr role="row">
										<th rowspan="2" class="sorting_disabled" colspan="1"> Date of Deposit/Debit</th>
										<th rowspan="2" class="sorting_disabled" colspan="1"> Time</th>
										<th rowspan="2" class="sorting_disabled" colspan="1">Reporting Date</th>
										<th rowspan="2" class="sorting_disabled" colspan="1">Reference No</th>
										<th rowspan="2" class="sorting_disabled" colspan="1">Tax Period</th>
										<th rowspan="2" class="sorting_disabled" colspan="1">Description</th>
										<th rowspan="2" class="sorting_disabled" colspan="1">Transaction Type</th>
										<th colspan="5" class="text-center" rowspan="1">Amount</th>
										<th colspan="5" class="text-center" rowspan="1">Balance</th>
									</tr>
									<tr role="row">
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 36px;">IGST</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">CGST</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">SGST</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">CESS</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">Total</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 36px;">IGST</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">CGST</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">SGST</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">CESS</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">Total</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								</table>
											</div>
                                        </div>

                                    </div>
                                    <!-- itemdetailsblock-wrap end -->
                                </form>
                            </div>
                        </div>
                        <!-- inovice wrap end -->
                    </div>

                    <!-- dashboard left block end -->


                </div>

                <!-- Dashboard body end -->
            </div>
        </div>
        <!-- db-ca-wrap end -->

       <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->

<div class="modal fade" id="detailModal" tabindex="-1" role="dialog" aria-labelledby="detailModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="  p-2 steptext-wrap">
			<table width="100%" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover">
            <thead>
              <tr>
                <th>Interest</th>
                <th>Tax</th>
                <th>Penalty</th>
                <th>Fee</th>
				<th>Other</th>
              </tr>
            </thead>
            <tbody id="detailBody">
            </tbody>
          </table>
         </div>
      </div>
    </div>
  </div>
</div>
</body>
<script type="text/javascript">
	var ledgerType='<c:out value="${type}"/>';
	var gstnnumber='<c:out value="${client.gstnnumber}"/>';
	var ledgerTable;
	var ledgerFileName;
	$(function () {
		var date = new Date();
		if(month == null || month == '') {
			month = date.getMonth()+1;
			year = date.getFullYear();
		}
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		$('#nav-client').addClass('active');
		$('[data-toggle="tooltip"]').tooltip();
		$('#fromdate,#todate').datetimepicker({
			timepicker: false,
			format: 'd-m-Y',
			maxDate: 0,
			scrollMonth: true
		});
		$('#datetimepicker1').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy',
			beforeShowMonth: function() {
			return 'datepicker-orient-left';
		}
		});
		$('#datetimepicker1').datepicker('update', dateValue);
		if(ledgerType == 'cash'){
			ledgerFileName = 'MGST_CashLedger_'+gstnnumber+'_'+month+year;
		}else if(ledgerType == 'credit'){
			ledgerFileName = 'MGST_CreditLedger_'+gstnnumber+'_'+month+year;
		}else if(ledgerType == 'tax'){
			ledgerFileName = 'MGST_LiabilityLedger_'+gstnnumber+'_'+month+year;
		}
		ledgerTable = $('#invoicetable').DataTable({
            dom: 'Bfrtip',
            "paging": true,
            "searching": false,
            "responsive": true,
			scrollX:        true,
			scrollCollapse: true,
			buttons : [ {
				extend : 'excel',
				filename : ledgerFileName,
				title : '',
				text  : 'Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
			}],
			"columnDefs": [
			  { className: "dt-right", "targets": [7,8,9,10,11,12,13,14,15,16] }
			]
        });
		if(ledgerType == 'tax') {
			$('#liabilityLedger').click();
			//fetchLiabilityLedgerDetails();
		}
	});
	function fetchLiabilityLedgerDetails(btn) {
		$(btn).addClass('btn-loader');
		var dt = $('#datetimepicker1').val();
		var rxDatePattern = /^(\d{1,2})(\/|-)(\d{4})$/;
		var dtFromArray = dt.match(rxDatePattern);
		var mn = dtFromArray[1];
		var yr = dtFromArray[3];
		mn = mn.replace(/^0+/, '');
		month = mn;
		year = yr;
		var pUrl="${contextPath}/ihubldgrtax/${id}/${client.id}/"+month+"/"+year;
		$.ajax({
			url: pUrl,
			success : function(ledgerResponse) {
				$(btn).removeClass('btn-loader');
				if(ledgerResponse.error){
					errorNotification(ledgerResponse.error.message);
				}else if(ledgerResponse.message){
					successNotification(ledgerResponse.message);
				}
				updateCashDashboard(ledgerResponse);
			},
			error : function(e) {
				$(btn).removeClass('btn-loader');
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	
	function fetchDetails(btn) {
		var pUrl=null;
		var fromVal = $('#fromdate').val();
		var toVal = $('#todate').val();
		if(fromVal == ''){
			$('#fromDate_Msg').html('Please Enter From Date');
		}
		if(toVal == ''){
			$('#todate_Msg').html('Please Enter To Date');
		}
		var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
		var dtFromArray = fromVal.match(rxDatePattern);
		dtFromDay = dtFromArray[1];
		dtFromMonth= dtFromArray[3];	
		dtFromYear = dtFromArray[5];
		var fromVal1 = dtFromMonth+"/"+dtFromDay+"/"+dtFromYear;
		var dtToArray = toVal.match(rxDatePattern);
		dtToDay = dtToArray[1];
		dtToMonth= dtToArray[3];	
		dtToYear = dtToArray[5];
		var toVal1 = dtToMonth+"/"+dtToDay+"/"+dtToYear;
		if(new Date(fromVal1) <= new Date(toVal1)){
			$(btn).addClass('btn-loader');
			$('#fromdate_Msg').html('');
			$('#fromDate_Msg').html('');
			$('#todate_Msg').html('');
			if(ledgerType == 'cash') {
			pUrl="${contextPath}/ihubldgrcsh/${id}/${client.id}/"+fromVal+"/"+toVal;
		} else if(ledgerType == 'credit') {
			pUrl="${contextPath}/ihubldgritc/${id}/${client.id}/"+fromVal+"/"+toVal;
		} else if(ledgerType == 'tax') {
			pUrl="${contextPath}/ihubldgrtax/${id}/${client.id}/"+month+"/"+year;
		}
		$.ajax({
			url: pUrl,
			success : function(ledgerResponse) {
				$(btn).removeClass('btn-loader');
				if(ledgerResponse.error){
					errorNotification(ledgerResponse.error.message);
				}else if(ledgerResponse.message){
					successNotification(ledgerResponse.message);
				}
				if(ledgerType == 'cash') {
					updateCashDashboard(ledgerResponse);
				} else if(ledgerType == 'credit') {
					updateITCDashboard(ledgerResponse);
				} else if(ledgerType == 'tax') {
					updateCashDashboard(ledgerResponse);
				}
			},
			error : function(e) {
				$(btn).removeClass('btn-loader');
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
		
		}else{
			$('#fromdate_Msg').html('From Date must be less than To date');
			$('#fromDate_Msg').html('');
			$('#todate_Msg').html('');
		}
	}
	function updateCashDashboard(ledgerResponse) {
		ledgerTable.clear();
		if(ledgerResponse.data) {
			if(ledgerResponse.data.op_bal) {
				ledgerTable.row.add([ '-', '-', '-', '-', '-', ledgerResponse.data.op_bal.desc, '-', '-', '-', '-', '-', '-', detailsContent(ledgerResponse.data.op_bal.igstbal), detailsContent(ledgerResponse.data.op_bal.cgstbal), detailsContent(ledgerResponse.data.op_bal.sgstbal), detailsContent(ledgerResponse.data.op_bal.cessbal), formatNumber(parseFloat(ledgerResponse.data.op_bal.tot_rng_bal).toFixed(2)) ] );
			}
			if(ledgerResponse.data.cl_bal) {
				ledgerTable.row.add([ '-', '-', '-', '-', '-', ledgerResponse.data.cl_bal.desc, '-', '-', '-', '-', '-', '-', detailsContent(ledgerResponse.data.cl_bal.igstbal), detailsContent(ledgerResponse.data.cl_bal.cgstbal), detailsContent(ledgerResponse.data.cl_bal.sgstbal), detailsContent(ledgerResponse.data.cl_bal.cessbal), formatNumber(parseFloat(ledgerResponse.data.cl_bal.tot_rng_bal).toFixed(2)) ] );
			}
			if(ledgerResponse.data.tr) {
				ledgerResponse.data.tr.forEach(function(row) {
					if(ledgerType == 'tax') {
						row.dpt_dt = row.dt;
						row.dpt_time = '-';
						row.rpt_dt = '-';
						if(row.ref_no) {
							row.refNo = row.ref_no;
						}
						if(ledgerResponse.data.ret_period) {
							row.ret_period = ledgerResponse.data.ret_period;
						}
					}
					if(row.tr_typ == 'Cr') {
						row.tr_typ = 'Credit';
					} else if(row.tr_typ == 'Dr') {
						row.tr_typ = 'Debit';
					}
					if(row.dpt_time == null || row.dpt_time == undefined) {
						row.dpt_time = '-';
					}
					if(row.rpt_dt == null || row.rpt_dt == undefined) {
						row.rpt_dt = '-';
					}
					if(row.dpt_dt == null || row.dpt_dt == undefined) {
						row.dpt_dt = '-';
					}
					if(row.refNo == null || row.refNo == undefined) {
						row.refNo = '-';
					}
					if(row.ret_period == null || row.ret_period == undefined) {
						row.ret_period = '-';
					}
					ledgerTable.row.add([ row.dpt_dt, row.dpt_time, row.rpt_dt, row.refNo, row.ret_period, row.desc, row.tr_typ, detailsContent(row.igst), detailsContent(row.cgst), detailsContent(row.sgst), detailsContent(row.cess), formatNumber(parseFloat(row.tot_tr_amt).toFixed(2)), detailsContent(row.igstbal), detailsContent(row.cgstbal), detailsContent(row.sgstbal), detailsContent(row.cessbal), formatNumber(parseFloat(row.tot_rng_bal).toFixed(2)) ] );
				});
			}
		}
		ledgerTable.draw();
	}
	function updateITCDashboard(ledgerResponse) {
		ledgerTable.clear();
		if(ledgerResponse.data && ledgerResponse.data.itcLdgDtls) {
			if(ledgerResponse.data.itcLdgDtls.op_bal) {
				ledgerTable.row.add([ '-', '-', '-', '-', '-', ledgerResponse.data.itcLdgDtls.op_bal.desc, '-', '-', '-', '-', '-', '-', formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.op_bal.igstTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.op_bal.cgstTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.op_bal.sgstTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.op_bal.cessTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.op_bal.tot_rng_bal).toFixed(2)) ] );
			}
			if(ledgerResponse.data.itcLdgDtls.cl_bal) {
				ledgerTable.row.add([ '-', '-', '-', '-', '-', ledgerResponse.data.itcLdgDtls.cl_bal.desc, '-', '-', '-', '-', '-', '-', formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.cl_bal.igstTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.cl_bal.cgstTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.cl_bal.sgstTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.cl_bal.cessTaxBal).toFixed(2)), formatNumber(parseFloat(ledgerResponse.data.itcLdgDtls.cl_bal.tot_rng_bal).toFixed(2)) ] );
			}
			if(ledgerResponse.data.itcLdgDtls.tr) {
				ledgerResponse.data.itcLdgDtls.tr.forEach(function(row) {
					ledgerTable.row.add([ row.dt, '-', '-', row.ref_no, row.ret_period, row.desc, row.tr_typ, formatNumber(parseFloat(row.igstTaxAmt).toFixed(2)), formatNumber(parseFloat(row.cgstTaxAmt).toFixed(2)), formatNumber(parseFloat(row.sgstTaxAmt).toFixed(2)), formatNumber(parseFloat(row.cessTaxAmt).toFixed(2)), formatNumber(parseFloat(row.tot_tr_amt).toFixed(2)), formatNumber(parseFloat(row.igstTaxBal).toFixed(2)), formatNumber(parseFloat(row.cgstTaxBal).toFixed(2)), formatNumber(parseFloat(row.sgstTaxBal).toFixed(2)), formatNumber(parseFloat(row.cessTaxBal).toFixed(2)), formatNumber(parseFloat(row.tot_rng_bal).toFixed(2)) ] );
				});
			}
		}
		ledgerTable.draw();
	}
	function detailsContent(rowObj) {
		return '<a href="#" onclick="showDetails('+rowObj.tx+','+rowObj.intr+','+rowObj.pen+','+rowObj.fee+','+rowObj.oth+')">'+formatNumber(parseFloat(rowObj.tot).toFixed(2))+'</a>';
	}
	function showDetails(tx, intr, pen, fee, oth) {
		$('#detailBody').html('<tr><td>'+formatNumber(parseFloat(tx).toFixed(2))+'</td><td>'+formatNumber(parseFloat(intr).toFixed(2))+'</td><td>'+formatNumber(parseFloat(pen).toFixed(2))+'</td><td>'+formatNumber(parseFloat(fee).toFixed(2))+'</td><td>'+formatNumber(parseFloat(oth).toFixed(2))+'</td></tr>');
		$('#detailModal').modal('show');
	}
</script>

</html> 