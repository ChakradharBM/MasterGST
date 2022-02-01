<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value="<%=MasterGSTConstants.GSTR2B%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | GSTR2B Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<style>
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
.months_na li{width:85px;text-align:center;border-right:1px solid #f0f2f3}
.months_na li:nth-child(9), .months_na li:nth-child(10), .months_na li:nth-child(11), .months_na li:nth-child(12){width:86px}
.months_na{background-color:white;width:100%;font-size:13px;list-style:none;display: inline-flex;padding-left: 0px;margin: 0px;padding-top: 6px;margin-left: 1px;}
</style>
<script type="text/javascript">
var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
function formatInvoiceDate(date){
	var invDate = new Date(date);
	var day = invDate.getDate() + "";
	var month = (invDate.getMonth() + 1) + "";
	var year = invDate.getFullYear() + "";
	day = checkZero(day);
	month = checkZero(month);
	year = checkZero(year);
	return day + "-" + mnths[month-1] + "-" + year;
}
function checkZero(data){
	if(data.length == 1){
		data = "0" + data;
  	}
	return data;
}
	$(document).ready(function(){
		var year = new Date().getFullYear();
		var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
		var currentMonth=new Date().getMonth()+1;
		if(currentMonth < 4){year--;}
		gstr2b_downloadurls(year);
	});
	function gstr2b_ajaxfunction(year){
		var months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
		var currentMonth=new Date().getMonth()+1;
		var curryear = new Date().getFullYear();
		$.ajax({
			url: "${contextPath}/returndownloadstatus/${id}/${client.id}/GSTR2B/"+year,
			contentType: 'application/json',
			success : function(data) {
				var fp = $('.yearlyoption').text();	
				var fpsplit = fp.split(' - ');
				var yrs = parseInt(fpsplit[0]);
				jQuery.each(data, function(index, value){
					if(curryear == year){
						if(parseInt(value.currrentmonth) >= 4){
							if(data.length >= (currentMonth-3)){
								$('.dwnld-noticedinfo').addClass('d-none');
							}
							if(parseInt(value.currrentmonth) <= currentMonth){
								$('#dwnld'+value.currrentmonth).html('Downloaded');
					        	$('#dwnld'+value.currrentmonth).css('vertical-align','super');
					        	$('#dwnld'+value.currrentmonth).css('color','green');
					        	$('#mnth'+value.currrentmonth).html(formatInvoiceDate(value.updatedDate));
							}
						}
					}else{
						if(yrs == 2017){
							if(data.length >= 9){
								$('.dwnld-noticedinfo').addClass('d-none');
							}
						}else{
							if(data.length >= 12){
								$('.dwnld-noticedinfo').addClass('d-none');
							}						
						}
						$('#dwnld'+value.currrentmonth).html('Downloaded');
			        	$('#dwnld'+value.currrentmonth).css('vertical-align','super');
			        	$('#dwnld'+value.currrentmonth).css('color','green');
			        	$('#mnth'+value.currrentmonth).html(formatInvoiceDate(value.updatedDate));
					}
		        });
			}
		});
	}
	function gstr2b_downloadurls(year){
		var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
		var changeyear=year-1;
		var nxtyr = year+1;
		var currentyear = new Date().getFullYear();
		for(var i=1;i<=12;i++){
			$('.dwnldlink'+i).html('-').removeAttr('onclick');
			if(i>=10){
				$('#mnth'+i).html('-');
				$('#dwnld'+i).html('-');
				
			}else{
				$('#mnth0'+i).html('-');
				$('#dwnld0'+i).html('-');
			} 
		}
		var currentMonths=new Date().getMonth()+1;
		if('2017' != year){
			if(currentMonths <= 3 && year == currentyear-1 && year <= currentyear){
				var currentMonth=new Date().getMonth()+1;
				for(var i=1;i<=12;i++){
					$('.dwnldlink'+i).addClass(i+""+nxtyr);
					if(currentMonth == 1 || currentMonth == 2 || currentMonth == 3){
						if(i >= 4){
							$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
							if(i>=10){
								$('#mnth'+i).html(mnths[i-1]+"-"+year);
								$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
							}else{
								if(i >= 4){
									$('#mnth0'+i).html(mnths[i-1]+"-"+year);
									$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
								}
							}
						}else{
							if(i<=currentMonth){
								$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+nxtyr+'\')');
								$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
								$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
							}
						}
					}else{
						if(i >= 4){
							if(i<=currentMonth){
								$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
								if(i>=10){
									$('#mnth'+i).html(mnths[i-1]+"-"+year);
									$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
								}else{
									if(i >= 4){
										$('#mnth0'+i).html(mnths[i-1]+"-"+year);
										$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
									}
								} 
							}
						}
					}
				}
			}else if(year >= currentyear){
					for(var i=1;i<=12;i++) {
						if(i >= 4){
							if(i<=currentMonths){
								$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
								if(i>=10){
									$('#mnth'+i).html(mnths[i-1]+"-"+year);
									$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
								}else{
									if(i >= 4){
										$('#mnth0'+i).html(mnths[i-1]+"-"+year);
										$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
									}
								} 
							}
						}
					}
				}else{
					for(var i=1;i<=12;i++){
						if(i <= 3){
							$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+nxtyr+'\')');
						}else{
							$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
						}
						if(i>=10){
							$('#mnth'+i).html(mnths[i-1]+"-"+year);
							$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
						}else{
							if(i <= 3){
								$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
							}else{
								$('#mnth0'+i).html(mnths[i-1]+"-"+year);
							}
							$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
						}
					}
				}
			}else if('2017' == year){
				for(var i=1;i<=12;i++){
					$('.dwnldlink'+i).addClass(i+""+year);
					if(i>=10){
						$('#mnth'+i).html(mnths[i-1]+"-"+year);
						$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
						$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
					}else{
						if(i <= 3){
							$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
							$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
							$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+nxtyr+'\')');
						}else if(i>=7){
							$('#mnth0'+i).html(mnths[i-1]+"-"+year);
							$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
							$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
						}
					}
				}
			}else{
				for(var i=1;i<=12;i++){
					if(i <= 3){
						$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+nxtyr+'\')');
					}else{
						$('.dwnldlink'+i).html('Download').attr('onclick','downloadgstr2bdata(\''+i+'\',\''+year+'\')');
					}
					if(i>=10){
						$('#mnth'+i).html(mnths[i-1]+"-"+year);
						$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
					}else{
						if(i <= 3){
							$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
						}else{
							$('#mnth0'+i).html(mnths[i-1]+"-"+year);
						}
						$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
					}
				}
			}
			gstr2b_ajaxfunction(year);
		}
	var otpandsubscriptionverification="";
	function subscriptionandotpcheck(){
		otpandsubscriptionverification="";
		$.ajax({
			url : "${contextPath}/subscriptiondata/${id}/${client.id}/${month}/${year}",
			async: false,
			type: 'GET',
			success : function(response) {
				otpandsubscriptionverification = response;
				if(response == "OTP_VERIFIED"){
				}else if(response == "expired"){
					errorNotification('Your subscription has expired. Kindly <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
				}else{
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
				}
			},error : function(response) {}
		});
	}
	
	function downloadgstr2bdata(mnth,year){
		otpandsubscriptionverification="";
		var months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
		subscriptionandotpcheck();
		
		if(otpandsubscriptionverification == 'OTP_VERIFIED'){
			$('.gstr3b_info').append('<div class="spin-loader" style="z-index:99;width:100%;position: absolute;top: 15%;text-align: center;"><img class="loader" src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" style="width: 13%;"></div>');
			$('.gstr3b_info ul').css('opacity','0.2');	
			$.ajax({
				url: "${contextPath}/download2binvs/${id}/${fullname}/${usertype}/${client.id}/GSTR2B/"+mnth+"/"+year,
				contentType: 'application/json',
				success : function(data) {
					if(data == "" || data == null){
						if(mnth < 10){
							$('#dwnld0'+mnth).text('Downloaded');
				        	$('#dwnld0'+mnth).css('vertical-align','super');
				        	$('#dwnld0'+mnth).css('color','green');
							$('#mnth0'+mnth).html(formatInvoiceDate(new Date()));
						}else{
							var date=new Date(data.updatedDate);
							$('#dwnld'+mnth).text('Downloaded');
				        	$('#dwnld'+mnth).css('vertical-align','super');
				        	$('#dwnld'+mnth).css('color','green');
							$('#mnth'+mnth).html(formatInvoiceDate(new Date()));
						}
						successNotification('No GSTR2B invoices found from GSTN for '+(mnths[mnth-1]+"-"+year));
						$('.gstr3b_info div.spin-loader').remove();
						$('.gstr3b_info ul').css('opacity','1');
					}else{
						if(data.errormsg != "" && data.errormsg != null){
							errorNotification(data.errormsg);
						}else{
							var date=new Date(data.updatedDate);
							$('#dwnld'+data.currrentmonth).text('Downloaded');
				        	$('#dwnld'+data.currrentmonth).css('vertical-align','super');
				        	$('#dwnld'+data.currrentmonth).css('color','green');
							$('#mnth'+data.currrentmonth).html(formatInvoiceDate(data.updatedDate));
							successNotification('GSTR2B Invoices Downloaded from GSTN Successfully for '+(mnths[mnth-1]+"-"+year));
							$('.gstr3b_info div.spin-loader').remove();
							$('.gstr3b_info ul').css('opacity','1');							
						}
					}
			    },error: function(error) {
					$('.gstr3b_info div.spin-loader').remove();
					$('.gstr3b_info ul').css('opacity','1');
				}
			});
		}
	}
	var invoiceArray=new Object();
	$('input.btaginput').tagsinput({tagClass : 'big',});
	$(document).on('click', '.btn-remove-tag', function() {$('.bootstrap-tagsinput').html('');});
	$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
		$('.bootstrap-tagsinput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
	});
</script>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	<jsp:include page="/WEB-INF/views/client/otpverification.jsp"/>
<div class="breadcrumbwrap" >
	<div class="container bread">
	<div class="row">
        <div class="col-md-12 col-sm-12">
		<ol class="breadcrumb">
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
			<li class="breadcrumb-item active">GSTR2B Report</li>
		</ol>
		<div class="retresp"></div></div></div></div>
	</div>
	<div class="db-ca-wrap yearly1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>Yearly GSTR2B Report of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h2><p>Yearly GSTR2B Report gives you a summary of your annual sales.</p>
			<p style="color:red" class="dwnld-noticedinfo">We have noticed some of the months GSTR2B is not downloaded, please download to see full data set for this financial year.</p>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<span>Report Type:</span>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 18.4%; top: 26px">
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"	id="monthely-sp">
				<label id="ret-period">Return Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months"	style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" value="02-2012"	readonly=""> 
							<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getReturnData()">Generate</a>
					</div>
				</span> 
				<span style="display: inline-block" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div>
					</span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getReturnData()">Generate</a>
				</span>
			</div>
			<div class=" "></div>
			<div style="background-color: #f6f9fb;" class="dash_tabs_container">
				<div>
					<ul class="months_na">
						<li class="months">Months</li><li class="months">April</li><li class="months">May</li><li class="months">June</li>
						<li class="months">July</li><li class="months">August</li><li class="months">September</li><li class="months">October</li>
						<li class="months">November</li><li class="months">December</li><li class="months">January </li><li class="months">February</li><li class="months">March</li>
					</ul>
				</div>
			</div>		
       		<div style="background-color:white;">
				<div class="gstr3b_info pb-0">
				<ul class="pl-0" style="border-bottom: 1px solid #f0f2f3;">
						<li><div class="gstr3bbox"><span class="status" style="height: 36px;">Status</span><span class="lastdwnld">last downloaded</span></div></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld04">-</span><span><a href="#" class="dwnldlink4" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth04">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld05">-</span><span><a href="#" class="dwnldlink5" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth05">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld06">-</span><span><a href="#" class="dwnldlink6" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth06">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld07">-</span><span><a href="#" class="dwnldlink7" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth07">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld08">-</span><span><a href="#" class="dwnldlink8" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth08">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld09">-</span><span><a href="#" class="dwnldlink9" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth09">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld10">-</span><span><a href="#" class="dwnldlink10" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth10">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld11">-</span><span><a href="#" class="dwnldlink11" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth11">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld12">-</span><span><a href="#" class="dwnldlink12" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth12">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld01">-</span><span><a href="#" class="dwnldlink1" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth01">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld02">-</span><span><a href="#" class="dwnldlink2" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth02">-</span></li>
						<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px" id="dwnld03">-</span><span><a href="#" class="dwnldlink3" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth03">-</span></li>
				</ul>
				</div>
			</div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<div class="normaltable meterialform" id="updatefilter_summary_reports" style="display:none;">
					<div class="filter">
						<div class="noramltable-row">
							<div class="noramltable-row-hdr">Filter</div>
							<div class="noramltable-row-desc">
								<div class="sfilter">
									<span id="divFiltersGSTR2B"></span>
									<span class="btn-remove-tag" onClick="clearGstr2bInvFiltersReportss('GSTR2B')">Clear All<span data-role="remove"></span></span>
								</div>
							</div>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Search Filter</div>
						<div class="noramltable-row-desc">
							<select id="multiselect${varRetTypeCode}1" class="multiselect-ui form-control" multiple="multiple">
								<option value="B2B">B2B Invoices</option>
								<option value="B2BA">B2BA Invoices</option>
								<option value="Debit Note">Debit Note</option>
								<option value="Credit Note">Credit Note</option>
								<option value="IMPG">IMPG</option>
								<option value="IMPGSEZ">IMPGSEZ</option>
								<option value="ISD">ISD</option>
								<option value="ISDA">ISDA</option>
							</select>
							<select id="multiselect${varRetTypeCode}2" class="multiselect-ui form-control" multiple="multiple"></select>
							<select id="multiselect${varRetTypeCode}3" class="multiselect-ui form-control" multiple="multiple"></select>
							<select id="multiselect${varRetTypeCode}4" class="multiselect-ui form-control" multiple="multiple">
								<c:forEach items="${client.branches}" var="branch">
									<option value="${branch.name}">${branch.name}</option>
								</c:forEach>
							</select>
							<select id="multiselect${varRetTypeCode}5" class="multiselect-ui form-control" multiple="multiple">
								<c:forEach items="${client.verticals}" var="vertical">
									<option value="${vertical.name}">${vertical.name}</option>
								</c:forEach>
							</select>
							</div>
						</div>
						<div class="noramltable-row">
							<div class="noramltable-row-hdr">Filter Summary</div>
								<div class="noramltable-row-desc">
								<div class="normaltable-col hdr">Total Invoices<div class="normaltable-col-txt" id="idCount${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr">Total Taxable Value<div class="normaltable-col-txt" id="idTaxableVal${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr">Total Tax Value<div class="normaltable-col-txt" id="idTaxVal${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr">Total Amount <div class="normaltable-col-txt" id="idTotAmtVal${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr filsummary">Total IGST<div class="normaltable-col-txt" id="idIGST${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr filsummary">Total CGST<div class="normaltable-col-txt" id="idCGST${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr filsummary">Total SGST<div class="normaltable-col-txt" id="idSGST${varRetTypeCode}"></div></div>
								<div class="normaltable-col hdr filsummary">Total CESS<div class="normaltable-col-txt" id="idCESS${varRetTypeCode}"></div></div>
							</div>
						</div>
					</div>
				</div>
				<div class="customtable db-ca-view reportTable reportTable4 fixed-col-div" id="${varRetTypeCode}SummaryTable">
				    <div class ="row">
						<div class="col-sm-9 pr-0">
						    <h4><span class="reports-yearly" style="display: none;">Yearly Summary of <c:out value="${client.businessname}"/></span><span class="reports-custom" style="display: none;">Custom Summary of <c:out value="${client.businessname}"/></span></h4>
						</div>
						<div class="col-sm-3">
							<a href="${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetTypeCode}?reporttype=Multimonth-Reports&year=${year}&fromdate=null&todate=null" id="dwnldxls" class="btn btn-blue mb-3 pull-right excel_btn" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;" data-toggle="tooltip" data-placement="top" title="Download Financial Summary To Excel">Download To Excel<i class="fa fa-download ml-1" aria-hidden="true"></i></a>				    	
						</div>
					</div>
				    <table id="reportTable4" class="display row-border dataTable fixed-col meterialform" cellspacing="0" width="100%">
				        <thead>
				            <tr>
								<th class="text-center">Tax Details</th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th>
								<th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th>
								<th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th><th class="text-center">Total</th>
							</tr>
				        </thead>
				        <tbody id="yeartotoalreport">
					        <tr><td align="center"><h6>Transactions</h6> </td><td class="text-right" id="totalinvoices4">0</td><td class="text-right" id="totalinvoices5">0</td><td class="text-right" id="totalinvoices6">0</td><td class="text-right" id="totalinvoices7">0</td><td class="text-right" id="totalinvoices8">0</td><td class="text-right" id="totalinvoices9">0</td><td class="text-right" id="totalinvoices10">0</td><td class="text-right" id="totalinvoices11">0</td><td class="text-right" id="totalinvoices12">0</td><td class="text-right" id="totalinvoices1">0</td><td class="text-right" id="totalinvoices2">0</td><td class="text-right" id="totalinvoices3">0</td><td class="text-right ind_formatss" id="ytotal_Transactions">0</td></tr>
						  	<tr><td align="center"><h6>Taxable Value</h6> </td><td class="text-right ind_formatss" id="totalTaxableValue4">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue5">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue6">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue7">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue8">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue9">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue10">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue11">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue12">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue1">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue2">0.0</td><td class="text-right ind_formatss" id="totalTaxableValue3">0.0</td><td class="text-right ind_formatss" id="ytotal_Taxablevalue">0.0</td></tr>
						  	<tr><td align="center"><h6>Tax Value</h6> </td><td class="text-right ind_formatss" id="taxAmt4">0.0</td><td class="text-right ind_formatss" id="taxAmt5">0.0</td><td class="text-right ind_formatss" id="taxAmt6">0.0</td><td class="text-right ind_formatss" id="taxAmt7">0.0</td><td class="text-right ind_formatss" id="taxAmt8">0.0</td><td class="text-right ind_formatss" id="taxAmt9">0.0</td><td class="text-right ind_formatss" id="taxAmt10">0.0</td><td class="text-right ind_formatss" id="taxAmt11">0.0</td><td class="text-right ind_formatss" id="taxAmt12">0.0</td><td class="text-right ind_formatss" id="taxAmt1">0.0</td><td class="text-right ind_formatss" id="taxAmt2">0.0</td><td class="text-right ind_formatss" id="taxAmt3">0.0</td><td class="text-right ind_formatss" id="ytotal_Taxvalue">0.0</td></tr>
						  	<tr><td align="center"><h6>Total Amount</h6> </td><td class="text-right ind_formatss" id="sales4">0.0</td><td class="text-right ind_formatss" id="sales5">0.0</td><td class="text-right ind_formatss" id="sales6">0.0</td><td class="text-right ind_formatss" id="sales7">0.0</td><td class="text-right ind_formatss" id="sales8">0.0</td><td class="text-right ind_formatss" id="sales9">0.0</td><td class="text-right ind_formatss" id="sales10">0.0</td><td class="text-right ind_formatss" id="sales11">0.0</td><td class="text-right ind_formatss" id="sales12">0.0</td><td class="text-right ind_formatss" id="sales1">0.0</td><td class="text-right ind_formatss" id="sales2">0.0</td><td class="text-right ind_formatss" id="sales3">0.0</td><td class="text-right ind_formatss" id="ytotal_TotalAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>IGST Amount</h6> </td><td class="text-right ind_formatss" id="igstAmount4">0.0</td><td class="text-right ind_formatss" id="igstAmount5">0.0</td><td class="text-right ind_formatss" id="igstAmount6">0.0</td><td class="text-right ind_formatss" id="igstAmount7">0.0</td><td class="text-right ind_formatss" id="igstAmount8">0.0</td><td class="text-right ind_formatss" id="igstAmount9">0.0</td><td class="text-right ind_formatss" id="igstAmount10">0.0</td><td class="text-right ind_formatss" id="igstAmount11">0.0</td><td class="text-right ind_formatss" id="igstAmount12">0.0</td><td class="text-right ind_formatss" id="igstAmount1">0.0</td><td class="text-right ind_formatss" id="igstAmount2">0.0</td><td class="text-right ind_formatss" id="igstAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_IGSTAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>CGST Amount</h6> </td><td class="text-right ind_formatss" id="cgstAmount4">0.0</td><td class="text-right ind_formatss" id="cgstAmount5">0.0</td><td class="text-right ind_formatss" id="cgstAmount6">0.0</td><td class="text-right ind_formatss" id="cgstAmount7">0.0</td><td class="text-right ind_formatss" id="cgstAmount8">0.0</td><td class="text-right ind_formatss" id="cgstAmount9">0.0</td><td class="text-right ind_formatss" id="cgstAmount10">0.0</td><td class="text-right ind_formatss" id="cgstAmount11">0.0</td><td class="text-right ind_formatss" id="cgstAmount12">0.0</td><td class="text-right ind_formatss" id="cgstAmount1">0.0</td><td class="text-right ind_formatss" id="cgstAmount2">0.0</td><td class="text-right ind_formatss" id="cgstAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_CGSTAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>SGST Amount</h6> </td><td class="text-right ind_formatss" id="sgstAmount4">0.0</td><td class="text-right ind_formatss" id="sgstAmount5">0.0</td><td class="text-right ind_formatss" id="sgstAmount6">0.0</td><td class="text-right ind_formatss" id="sgstAmount7">0.0</td><td class="text-right ind_formatss" id="sgstAmount8">0.0</td><td class="text-right ind_formatss" id="sgstAmount9">0.0</td><td class="text-right ind_formatss" id="sgstAmount10">0.0</td><td class="text-right ind_formatss" id="sgstAmount11">0.0</td><td class="text-right ind_formatss" id="sgstAmount12">0.0</td><td class="text-right ind_formatss" id="sgstAmount1">0.0</td><td class="text-right ind_formatss" id="sgstAmount2">0.0</td><td class="text-right ind_formatss" id="sgstAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_SGSTAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>CESS Amount</h6> </td><td class="text-right ind_formatss" id="cessAmount4">0.0</td><td class="text-right ind_formatss" id="cessAmount5">0.0</td><td class="text-right ind_formatss" id="cessAmount6">0.0</td><td class="text-right ind_formatss" id="cessAmount7">0.0</td><td class="text-right ind_formatss" id="cessAmount8">0.0</td><td class="text-right ind_formatss" id="cessAmount9">0.0</td><td class="text-right ind_formatss" id="cessAmount10">0.0</td><td class="text-right ind_formatss" id="cessAmount11">0.0</td><td class="text-right ind_formatss" id="cessAmount12">0.0</td><td class="text-right ind_formatss" id="cessAmount1">0.0</td><td class="text-right ind_formatss" id="cessAmount2">0.0</td><td class="text-right ind_formatss" id="cessAmount3">0.0</td><td class="text-right ind_formatss" id="ytotal_CessAmount">0.0</td></tr>
						  	<tr><td align="center"><h6>TCS Value</h6> </td><td class="text-right ind_formatss" id="tcsAmt4">0.0</td><td class="text-right ind_formatss" id="tcsAmt5">0.0</td><td class="text-right ind_formatss" id="tcsAmt6">0.0</td><td class="text-right ind_formatss" id="tcsAmt7">0.0</td><td class="text-right ind_formatss" id="tcsAmt8">0.0</td><td class="text-right ind_formatss" id="tcsAmt9">0.0</td><td class="text-right ind_formatss" id="tcsAmt10">0.0</td><td class="text-right ind_formatss" id="tcsAmt11">0.0</td><td class="text-right ind_formatss" id="tcsAmt12">0.0</td><td class="text-right ind_formatss" id="tcsAmt1">0.0</td><td class="text-right ind_formatss" id="tcsAmt2">0.0</td><td class="text-right ind_formatss" id="tcsAmt3">0.0</td><td class="text-right ind_formatss" id="ytotal_Tcsvalue">0.0</td></tr>
				        </tbody>
				    </table>
				</div>
				<div class="customtable db-ca-view salestable reportsdbTable_${varRetTypeCode}">
					<table id='reports_dataTableGSTR2B' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>					
								<th>Type</th><th class="text-center">Invoice No</th><th class="text-center">Customers</th><th class="text-center">GSTIN</th><th class="text-center">Date</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th>
							</tr>
						</thead>
						<tbody id='invBodysalestable2'>
						</tbody>
					</table>
			   </div>			
			</div>
		</div>s
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script src="${contextPath}/static/mastergst/js/reports/multimonth_gstr2b.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			var year = new Date().getFullYear();
			var yr=$('#yearlyoption1').html().split("-");
			var month = new Date().getMonth()+1;
			loadGstr2bReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadGstr2bReportsUsersInDropDown);
			loadGstr2bReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', 0, yr[0], loadGstr2bReportsUsersInDropDown);
			loadGstr2bReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, yr[1], yr[0]);
			loadGstr2bReportsSummary('${id}', '${client.id}', '${varRetType}', 0, yr[0]);
			invsGstr2bDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, yr[1], yr[0]);
			initiateCallBacksForGstr2bMultiSelectReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGstr2bReports('${varRetType}','${varRetTypeCode}');
		});
		function getReturnData(){
			defaultClearGstr2bInvFiltersReports('GSTR2B','year')
			var year =$('#yearlyoption1').text().split("-");
			gstr2b_downloadurls(parseInt(year[0]));
			loadGstr2bReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadGstr2bReportsUsersInDropDown);
			loadGstr2bReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', 0, year[0], loadGstr2bReportsUsersInDropDown);
			loadGstr2bReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, year[1], year[0]);
			loadGstr2bReportsSummary('${id}', '${client.id}', '${varRetType}', 0, year[0]);
			invsGstr2bDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, year[1], year[0]);
			initiateCallBacksForGstr2bMultiSelectReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGstr2bReports('${varRetType}','${varRetTypeCode}');
		}
	</script>
</body>
</html>
