<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | Partner Payments Report </title>
    <%@include file="/WEB-INF/views/includes/common_script.jsp"%>
	<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" 	media="all" />
	<!-- datepicker start -->
	<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script> 
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
	<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
</head>
<style>
.datetimetxt{display:inline-flex;padding:0px;}
.dpfromtime,.to-picker{border:none;}
.customtable.db-ca-view .dataTables_wrapper .dataTables_filter input {width:140px;}
</style>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
    <div class="bodybreadcrumb">
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="bdcrumb-tabs">
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item">Reports</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">BACK</a>
			<h4>Partner Payments Report</h4>
			<div class=""></div>
			<div class="">
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type"	style="width: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a>
						<!-- <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> -->
						<!-- <a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a> -->
					</div>
					<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						</div>
					</span> 
					<span style="display:none" class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="filing_option1"	style="margin-right: 10px; display: inline-flex;">
							<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
							<div class="typ-ret type_ret_yearly" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
								<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
									<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
								</span>
							</div>
						</span>
						<div class="dropdown-menu ret-type1" id="financialYear1" style="width: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
					</span>
					<span class="datetimetxt custom-sp" style="display:none" id="custom-sp">
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						<div class="datetimetxt datetime-wrap to-picker">
						<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
							<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 8px; height: 28px; margin-right: 10px;">
								<input type="text" class="form-control totime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>
						</div>
						<div class="datetimetxt datetime-wrap dpfromtime">
							<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
							<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
								<input type="text" class="form-control fromtime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>	
						</div>
					</span>
				</div>
			</div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
			    <div class="customtable db-ca-view salestable">
					<table id="partnerpaymentTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th>Parter ID</th><th>Partner Name</th><th class="text-center">Number</th><th class="text-center">Subscribed Amount</th><th class="text-center">Partner %</th><th class="text-center">Partner Amount</th><th class="text-center">Invoice From Partner</th><th class="text-center">Payment Status</th><th class="text-center">Comments / Edit</th>
							</tr>
						</thead>
						<tbody id="partnerpaymentTableBody">
						</tbody>
					</table>
				</div>
			</div>
			</div>
		</div>
				<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
				    <div class="modal-dialog modal-md modal-right" role="document">
				        <div class="modal-content">
				            <div class="modal-body">
				                <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
				                <div class="invoice-hdr bluehdr">
				                    <h3>Help To Read This Report</h3></div>
				                <div class=" p-2 steptext-wrap"><span class="pl-2">All the Sale Invoices from your SaleRegister/Books Monthly, Yearly and Custom Wise</span> </div>
				            </div>
				            <div class="modal-footer">
				                <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
				            </div>
				        </div>
				    </div>
				</div>
			
	
	<div class="modal fade" id="patnerPaymentModal" role="dialog" aria-labelledby="patnerPaymentModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content" style="height:100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Partner Payment</h3>
                    </div>
				</div>
				<div class="modal-body meterialform popupright">
					<form:form method="post" data-toggle="validator" class="meterialform bankform" id="partnerpaymentdetails" name="partnerpaymentdetails">
					<div class="row pl-5 p-5">
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Invoice No</p>
                            <span class="errormsg" id="pinvoiceno_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control pname" id="pinvoiceno" name="pinvoiceno"  placeholder="Enter Invoice No" value=""/>
                            <label for="pinvoiceno" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Invoice Date</p>
                            <span class="errormsg" id="pinvoicedate_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control pname" id="pinvoicedate" onmousedown="partnerdatepick();" name="pinvoicedate"  placeholder="Enter Invoice Date" value=""/>
                            <label for="pinvoicedate" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Partner id</p>
                            <span class="errormsg" id="pid_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control pid" id="pid" name="pid" required="required" data-error="Please Enter Partner ID"  placeholder="Enter Partner ID" value=""/>
                            <label for="pid" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Partner Name</p>
                            <span class="errormsg" id="pname_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control pname" id="pname" name="pname" required="required" data-error="Please Enter Partner Name"  placeholder="Enter Partner Name" value=""/>
                            <label for="pname" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Subscribed Amount</p>
                            <span class="errormsg" id="subamt_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control subamt" id="subamt" name="subamt" required="required" data-error="Please Enter Subscribed Amount"  placeholder="Enter Subscribed Amount" value=""/>
                            <label for="subamt" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt astrich">Partner Share</p>
                            <span class="errormsg" id="pshare_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control pshare" id="pshare" name="pshare" required="required" data-error="Please Enter Partner Share"  placeholder="Enter Partner Share" value=""/>
                            <label for="pshare" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pt-2 text-right lable-txt astrich">Partner Amount</div>
                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <span class="errormsg" id="pamount_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control pamount" onkeyup="findRemainingAmts()" id="pamount" name="pamount" required="required" data-error="Please Enter Partner Amount"  placeholder="Enter Partner Amount" value=""/>
                            <label for="pamount" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pt-2 text-right lable-txt astrich">Paid Amount</div>
                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <span class="errormsg" id="paidamt_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control paidamt" onkeyup="findRemainingAmts()" id="paidamt" name="paidamt" required="required" data-error="Please Enter Paid Amount"  placeholder="Enter Paid Amount" value=""/>
                            <label for="paidamt" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pt-2 text-right lable-txt astrich">TDS Amount</div>
                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <span class="errormsg" id="ptdsamt_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control paidamt ptdsamt" onkeyup="findRemainingAmts()" id="ptdsamt" name="ptdsamt"  placeholder="Enter TDS Amount" value=""/>
                            <label for="ptdsamt" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pt-2 text-right lable-txt astrich">Remaining Amount</div>
                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <span class="errormsg" id="ramount_Msg" style="margin-top:-33px"></span>
                            <input type="text" class="form-control ramount" id="ramount" name="ramount" required="required" data-error="Please Enter Remaining Amount" disabled placeholder="Enter Remaining Amount" value=""/>
                            <label for="ramount" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                    </div>
					</form:form>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
					<a type="button" class="btn  btn-blue-dark" id="savePartnerPayments">Save</a>
					<button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>	
<!-- Modal End -->
<!-- commentsModal Start -->			
	<div class="modal fade" id="ppaymentCommentsModal" role="dialog" aria-labelledby="ppaymentCommentsModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content" style="height:100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Comments/Narration</h3>
                    </div>
				</div>
				<div class="modal-body meterialform popupright">
					<div class="row pl-4 pr-2 pt-4">
					<span id="nocomments_leads" class="pl-2"></span>
					<div class="form-group col-md-12">
						<div class="partnercommentsInfo" style="max-height:300px;min-height:300px;overflow-y:auto;"></div>
					</div>
						<div class="form-group col-md-12 mb-0">
						<label for="Comments">Add Comments/Narration :</label>
						<textarea class="form-control" id="partnercomments" style="width:97%;height:110px;border:1px solid lightgray;"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
					<a type="button" class="btn  btn-blue-dark" id="addCommentBtn">Save</a>
				 	<button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" id="mthCd"/>
	<input type="hidden" id="yrCd"/>
<!-- commentsModal End -->	
    <%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script src="${contextPath}/static/mastergst/js/admin/patnerpayment_reports.js" type="text/javascript"></script>
	<script type="text/javascript">
		var partnerPaymentTable;
		var date = new Date();
		var mth = date.getMonth()+1;
		var	yer = date.getFullYear();
		var loginUser = '<c:out value="${fullname}"/>';
		var loginUserId = '<c:out value="${id}"/>';
		$(function(){
			$('#reports_lnk').addClass('active');
			loadPartnersTable(mth, yer, null, null, 'monthly');
		});
		function generateData() {
			var abc = $('#fillingoption span').html();
			var type='';
			if(abc == 'Monthly'){
				$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
				var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
				loadPartnersTable(mn, yr, null, null, 'monthly')
			}else if (abc == 'Yearly') {
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
				var year=$('.yearlyoption').html().split("-");
				var yr = parseInt(year[1]);
				loadPartnersTable(0, yr, null, null, 'yearly')
			}else{
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
				var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
				loadPartnersTable(0, 0, fromtime, totime, 'custom')
			}	
		}
	</script>
</body>
</html>