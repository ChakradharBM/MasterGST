<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Multi Month Summary</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>
#downloaddata_process{position: absolute;top: 9%;z-index: 3;left: 40%;font-weight: bolder;font-size: 21px;}
.months_na li{width:85px;text-align:center;border-right:1px solid #f0f2f3}
.months_na li:nth-child(9) , .months_na li:nth-child(10) ,.months_na li:nth-child(11) , .months_na li:nth-child(12){width:86px}
.months_na{background-color:white;width:100%;font-size:13px;list-style:none;display: inline-flex;padding-left: 0px;margin: 0px;padding-top: 6px;margin-left: 1px;}
.dropdown:hover .dropdown-content.reportSummaryGSTR3B{display: block;}.arrow-up {width: 0; height: 14px; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportSummaryGSTR3B{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 340px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
.td_headings{padding-top: 13px;border:none}
#multimonth_gstr3b, #reportTable4 , #reportTable4 th {border:none;overflow:hidden}
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
	var currentMonth= new Date().getMonth()+1;
	if(currentMonth < 4){year--;}
	var nextYear = year+1;
	gstr3b_downloadurls(year);
});
function gstr3b_ajaxfunction(year){
	var months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
	var currentMonth=new Date().getMonth()+1;
	var curryear = new Date().getFullYear();
	$.ajax({
		url: "${contextPath}/gstr3bstatus/${id}/${client.id}/"+year,
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
						if(parseInt(value.currrentmonth) <= currentMonth-1){
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
						if(data.length >=12){
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
var invoiceArray=new Object();
	$('input.btaginput').tagsinput({  tagClass: 'big',  });
	$(document).on('click','.btn-remove-tag',function(){$('.bootstrap-tagsinput').html('');});  
	$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();	$('.bootstrap-tagsinput').append('<span class="tag label label-info">'+ t +'<span data-role="remove"></span></span>'); }); 
	function updateYearlyOption(value){document.getElementById('yearlyoption1').innerHTML=value;}
	function gstr3bdetails(){
		var fp = $('.yearlyoption').text();	var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
		$('.ind_formatss').html(0.00);
		$.ajax({
			url: "${contextPath}/getGSTR3BYearlyinvs/${id}/${client.id}/GSTR3B/"+yrs,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(summary) {
				gstr3bsummary(summary);
			}
		});
		$('.dwnld-noticedinfo').removeClass('d-none');
		gstr3b_downloadurls(yrs);
	}
	function gstr3b_downloadurls(year){
		var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec'];
		var changeyear=year-1;
		var nxtyr = year+1;
		var currentMonth= new Date().getMonth()+1;
		var currentYear = new Date().getFullYear();
		for(var i=1;i<=12;i++){
			 $('.dwnldlink'+i).html('-').removeAttr('onclick');
			 if(i>=10){
				$('#mnth'+i).html('-');
				$('#dwnld'+i).html('-').css({'vertical-align':'inherit','color':'red'});
			}else{
				$('#dwnld0'+i).html('-').css({'vertical-align':'inherit','color':'red'});
				$('#mnth0'+i).html('-');
			}
		}
		var currentMonths=new Date().getMonth()+1;
		if('2017' != year){
			if(currentMonths <= 3 && year == currentYear-1 && year <= currentYear){
				var currentMonth=new Date().getMonth()+1;
				for(var i=1;i<=12;i++){
					$('.dwnldlink'+i).addClass(i+""+nxtyr);
					if(currentMonth == 1 || currentMonth == 2 || currentMonth == 3){
						if(i >= 4){
							$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
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
								$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+nxtyr+'\')');
								$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
								$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
							}
						}
					}else{
						if(i >= 4){
							if(i<=currentMonth){
								$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
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
			}else if(year >= currentYear){
					for(var i=1;i<=12;i++) {
						if(i >= 4){
							if(i<=currentMonths){
								$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
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
					$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+nxtyr+'\')');
				}else{
					$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
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
					$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
				}else{
					if(i <= 3){
						$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
						$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
						$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+nxtyr+'\')');
					}else if(i>=7){
						$('#mnth0'+i).html(mnths[i-1]+"-"+year);
						$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
						$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
					}
				}
			}
		}else{
			for(var i=1;i<=12;i++){
				if(i <= 3){
					$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+nxtyr+'\')');
				}else{
					$('.dwnldlink'+i).html('Download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
				}
				if(i>=10){
					$('#mnth'+i).html(mnths[i-1]+"-"+year);
					$('.dwnldlink'+i).html('download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
					$('#dwnld'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
				}else{
					if(i<=3){
						$('#mnth0'+i).html(mnths[i-1]+"-"+nxtyr);
						$('.dwnldlink'+i).html('download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+nxtyr+'\')');
						$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
					}else{
						$('#mnth0'+i).html(mnths[i-1]+"-"+year);
						$('.dwnldlink'+i).html('download').attr('onclick','excelreportgstr3b(\''+i+'\',\''+year+'\')');
						$('#dwnld0'+i).html('Not Downloaded').css({'vertical-align':'inherit','color':'red'});
					}
				}	
			}
		}
		gstr3b_ajaxfunction(year);
	}
</script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<jsp:include page="/WEB-INF/views/client/otpverification.jsp"/>
  <div class="breadcrumbwrap">
  <div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">MultiMonth GSTR3B Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="downloaddata_process" class="text-center"></div>
  	<div class="db-ca-wrap yearly1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h5>Multi month GSTR3B Report</h5><p>GSTR3B Report gives you a summary of your quarterly and annual purchases.</p>	
			<p style="color:red" class="dwnld-noticedinfo">We have noticed some of the months GSTR3B is not downloaded, please download to see full data set for this financial year.</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reportSummaryGSTR3B"> <span class="arrow-up"></span><span class="pl-2">All Filed Data Downloaded From GSTIN Yearly Wise</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<label>Report Type:</label>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px"> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
				</div> 
				<span style="display: inline-block;margin-bottom: 4px;" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="gstr3bdetails()">Generate</a>
				</span>
				<span class="datetimetxt custom-sp" style="display: none" id="custom-sp">
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="gstr3bdetails()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="02-2012"	readonly="">
							<span class="input-group-addon add-on pull-right">
								<i class="fa fa-sort-desc" id="date-drop"></i> 
							</span>
						</div>					
					</div>
					<div class="datetimetxt datetime-wrap">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="02-2012" readonly="">
							<span class="input-group-addon add-on pull-right">
								<i	class="fa fa-sort-desc" id="date-drop"></i>
							</span>
						</div>
					</div> 
				</span>
			</div>  
	<div style="background-color: #f6f9fb;" class="dash_tabs_container">
	<div>
	<ul class="months_na">
 <li  class="months">Months</li>
  <li class="months">April</li>
  <li class="months">May</li>
  <li class="months">June</li>
  <li class="months">July</li>
  <li class="months">August</li>
  <li class="months">September</li>
  <li class="months">October</li>
  <li class="months">November</li>
  <li class="months">December</li>
  <li class="months">January </li>
  <li class="months">February</li>
  <li class="months">March</li>
</ul>
  <div class="tab-content" style="display:none;">
	<div class="tab-pane active" id="dashtab1" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab2" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab3" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab4" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab5" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab6" role="tabpanel"><p></p></div>
	<div class="tab-pane" id="dashtab7" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab8" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab9" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab10" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab11" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab12" role="tabpanel"><p></p></div>
  </div>
</div>
	</div>		
       <div style="background-color:white;">
			  <!-- GST info start -->
				<div class="gstr3b_info pb-0">
				<ul class="pl-0" style="border-bottom: 1px solid #f0f2f3;">
				<li><div class="gstr3bbox"><span class="status" style="height: 36px;">Status</span><span class="lastdwnld">last downloaded</span></div></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld04">-</span><span><a href="#" class="dwnldlink4" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth04">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld05">-</span><span><a href="#" class="dwnldlink5" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth05">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld06">-</span><span><a href="#" class="dwnldlink6" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth06">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld07">-</span><span><a href="#" class="dwnldlink7" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth07">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld08">-</span><span><a href="#" class="dwnldlink8" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth08">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld09">-</span><span><a href="#" class="dwnldlink9" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth09">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld10">-</span><span><a href="#" class="dwnldlink10" style="font-size:13px;">-</a></span><hr/><span  style="font-size:12px;display:block;margin-top: 10px;" id="mnth10">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld11">-</span><span><a href="#" class="dwnldlink11" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth11">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld12">-</span><span><a href="#" class="dwnldlink12" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth12">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld01">-</span><span><a href="#" class="dwnldlink1" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth01">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld02">-</span><span><a href="#" class="dwnldlink2" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth02">-</span></li>
				<li class="gstr3bbox"><span style="color:red;font-size:12px;display: block;text-align: center;padding: 2px; height: 40px;" id="dwnld03">-</span><span><a href="#" class="dwnldlink3" style="font-size:13px;">-</a></span><hr/><span style="font-size:12px;display:block;margin-top: 10px;" id="mnth03">-</span></li>
				 </ul>
				</div></div>
	<!-- <a href="#" class="btn btn-blue reportbtn">Export Report as Excel</a> -->
			<a id="downloadLink" class="btn btn-blue reportbtn mb-2" onclick="exportF(this)" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
			<div id="yearProcess" class="text-center"></div>
			<c:set var="monthArray" value="${['4','5','6','7','8','9','10','11','12','1','2','3']}" />
			<div class="customtable db-ca-view reportTable reportTable4" style="margin-top:30px;">
			<table id="multimonth_gstr3b" border="1" style="width: 100%;">
			<tr><td class="td_headings" style="background-color:#f6f9fb!important;"><h5 style="text-align:center;">3.1 Tax on outward and reverse charge inward supplies</h5></td></tr>
			<tr><td><div class="containerdiv">
				<table id="reportTable4"  border="1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th class="text-center">Particulars</th>
							  <th class="text-center">April</th>
							  <th class="text-center">May</th>
							  <th class="text-center">June</th>
							  <th class="text-center">July</th>
							  <th class="text-center">August</th>
							  <th class="text-center">September</th>
							  <th class="text-center">October</th>
							  <th class="text-center">November</th>
							  <th class="text-center">December</th>
							  <th class="text-center">January</th>
							  <th class="text-center">February</th>
							  <th class="text-center">March</th>
							  <th class="text-center">Total</th>
						</tr>
					</thead>
					<tbody id="yeartotoalreport">
			  <tr>
					<td colspan="14"  style="background-color: aliceblue!important;"><h6 align="left">a) Outward taxable supplies (other than zero rated, nil rated and exempted)</h6> </td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Total Taxable Value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igst31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigst31a">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgst31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgst31a">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgst31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgst31a">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cess31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcess31a">0.00</td>
              </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">b) Outward taxable supplies (zero rated)</h6> </td>
	          </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Total taxable value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31b${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31b">0.00</td>
              </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igst31b${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigst31b">0.00</td>
              </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cess31b${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcess31b">0.00</td>
              </tr>
              <tr>
					<td colspan="14"style="background-color: aliceblue!important;"><h6 align="left">c) Other outward supplies (Nil rated, exempted)</h6> </td>
	          </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Total Taxable value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31c">0.00</td>
              </tr>
              <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">d)Inward Supplies Liable to Reverse charge</h6> </td>
	          </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Total Taxable Value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31d">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igst31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigst31d">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgst31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgst31d">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgst31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgst31d">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cess31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcess31d">0.00</td>
              </tr>
               <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">e)Non-GST Outward Supplies</h6> </td>
	          </tr>
               <tr>
					<td><h6 class="gstr3binfo" align="right">Total taxable value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31e${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31e">0.00</td>
              </tr>
    				</tbody>
				</table>
				</div>
				</td></tr>
			<!-- </div>
			<div class="customtable db-ca-view reportTable reportTable4"> -->
			<tr><td class="td_headings"  style="background-color:#f6f9fb!important;"><h5 align="center">4.Eligible ITC</h5></td></tr>
			<tr><td><div class="containerdiv">	<table  border="1" id="reportTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
				<thead>
			   <tr>
                 <th align="center">Particulars</th>
				  <th style="text-align: center;">April</th>
				  <th style="text-align: center;">May</th>
				  <th style="text-align: center;">June</th>
				  <th style="text-align: center;">July</th>
				  <th style="text-align: center;">August</th>
				  <th style="text-align: center;">September</th>
				  <th style="text-align: center;">October</th>
				  <th style="text-align: center;">November</th>
				  <th style="text-align: center;">December</th>
				  <th style="text-align: center;">January</th>
				  <th style="text-align: center;">February</th>
				  <th style="text-align: center;">March</th>  
                     <th style="text-align: center;">Total</th> 				  
                </tr>
              </thead>
              <tbody>
              <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">A)ITC Available (Whether in full or part)</h6> </td>
	          </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">1.Import of Goods</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcigstimpgoods${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditcigstimpgoods">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">ii)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcimpgoodscess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditccessimpgoods">0.00</td>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">1.Import of Services</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcigstimpservices${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditcigstimpservices">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcimpservicescess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditccessimpservices">0.00</td>
			  </tr>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">3.Inward supplies liable to reverse charge(other than 1 &2 above)</h6> </td>			
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="left">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitc4c">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="left">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitc4c">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitc4c">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitc4c">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">4.Inward supplies from ISD</h6> </td>
			</tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcisd">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcisd">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcisd">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcisd">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">5.All other ITC</h6> </td>
			  </tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcother">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcother">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcother">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcother">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">B.ITC Reversed</h6> </td>
			  </tr>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">1.As per Rule 42 & 43 of SGST/CGST rules</h6> </td>
			  </tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcrev">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcrev">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcrev">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcrev">0.00</td>
			  </tr>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2.Others</h6> </td>
			  </tr>
			    <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">C) Net ITC Available (A-B)</h6> </td>
			  </tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcnet">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcnet">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcnet">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcnet">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">D) Ineligible ITC</h6> </td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">1) As per section 17(5) of CGST/SGST Act</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2) Others</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcinelgothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcinelgothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcinelgothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcinelgothers">0.00</td>
			  </tr>
              </tbody>
				</table>
				</div>
				</td></tr>
				<!-- </div>
			<div class="customtable db-ca-view reportTable reportTable4"> -->
			<tr><td class="td_headings"  style="background-color:#f6f9fb!important;"><h5 align="center">5. Values of exempt, Nil-rated and non-GST inward supplies</h5></td></tr>
			<tr><td><div class="containerdiv">
				<table  border="1" id="reportTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
				<thead>
			   <tr>
                 <th align="center">Particulars</th>
				  <th style="text-align: center;">April</th>
				  <th style="text-align: center;">May</th>
				  <th style="text-align: center;">June</th>
				  <th style="text-align: center;">July</th>
				  <th style="text-align: center;">August</th>
				  <th style="text-align: center;">September</th>
				  <th style="text-align: center;">October</th>
				  <th style="text-align: center;">November</th>
				  <th style="text-align: center;">December</th>
				  <th style="text-align: center;">January</th>
				  <th style="text-align: center;">February</th>
				  <th style="text-align: center;">March</th>  
                   	<th style="text-align: center;">Total</th>			  
                </tr>
              </thead>
			<tbody>
			<tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">1) From a supplier under composition scheme, Exempt and Nil rated supply</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"class="gstr3binfo" align="right">i)Inter-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interstate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterstate51a">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">i)Intra-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="intrastate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdintrastate51a">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2) Non GST supplies</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">i)Inter-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="nongstinterstate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdnongstinterstate51a">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">ii)Intra-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="nongstintrastate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdnongstintrastate51a">0.00</td>
			  </tr>
			</tbody>
			</table>
			</div>
			</td>
			</tr>
			
			
			
			<!-- Table 6 Offset Liability start-->
				

				<tr><td class="td_headings"  style="background-color:#f6f9fb!important;"><h5 align="center">6. Offset Liability</h5></td></tr>
				<tr><td><div class="containerdiv">	<table  border="1" id="reportTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
				<thead>
			   <tr>
                 <th align="center">Particulars</th>
				  <th style="text-align: center;">April</th>
				  <th style="text-align: center;">May</th>
				  <th style="text-align: center;">June</th>
				  <th style="text-align: center;">July</th>
				  <th style="text-align: center;">August</th>
				  <th style="text-align: center;">September</th>
				  <th style="text-align: center;">October</th>
				  <th style="text-align: center;">November</th>
				  <th style="text-align: center;">December</th>
				  <th style="text-align: center;">January</th>
				  <th style="text-align: center;">February</th>
				  <th style="text-align: center;">March</th>  
                     <th style="text-align: center;">Total</th> 				  
                </tr>
              </thead>
              <tbody>
              <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">1) Other than reverse charge</h6> </td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Tax Payable</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayableigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayableigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayablecgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayablecgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayablesgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayablesgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayablecess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayablecess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Paid through ITC</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST using IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcigstusingigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcigstusingigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST using CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcigstusingcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcigstusingcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST using SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcigstusingsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcigstusingsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST using IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitccgstusingigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitccgstusingigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST using CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitccgstusingcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitccgstusingcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST using IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcsgstusingigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcsgstusingigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST using SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcsgstusingsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcsgstusingsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess using Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitccessusingcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitccessusingcess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Tax/Cess Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashcess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Interest Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashigstcess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Late Fee Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="latefeepaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdlatefeepaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="latefeepaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdlatefeepaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2. Reverse Charge</h6> </td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.2 Tax Payable</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayableigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayableigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayablecgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayablecgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayablesgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayablesgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayablecess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayablecess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.2 Tax/Cess Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashcess">0.00</td>
			  </tr>
              </tbody>
				</table>
				</div>
				</td></tr>
			<!-- Table 6 Offset Liability end-->
			
			<!-- </div>
			-->
			</table>
			</div>
			</div>
			
			</div>
			<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
        <div class=" p-2 steptext-wrap"><span class="pl-2">All Filed Data Downloaded From GSTIN Yearly Wise</span> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
		 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
<script type="text/javascript">
function exportF(elem) {
	  var table = document.getElementById("multimonth_gstr3b");
	  var html = table.outerHTML;
	  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
	  elem.setAttribute("href", url);
	  elem.setAttribute("download", "MGST_MULTI_MONTH_GSTR3B.xls"); // Choose the file name
	  return false;
}
var gstnnumber='<c:out value="${client.gstnnumber}"/>';
$(function() {
	$( ".helpicon" ).hover(function() {$('.reportSummaryGSTR3B').show();
	}, function() {$('.reportSummaryGSTR3B').hide();
	});
	var year = new Date().getFullYear();
	var currentMonth= new Date().getMonth()+1;
	if(currentMonth < 4){year--;}
	$('.ind_formatss').html(0.00);
	$.ajax({
		url: "${contextPath}/getGSTR3BYearlyinvs/${id}/${client.id}/GSTR3B/"+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(summary) {
			gstr3bsummary(summary);
		}
	});
});

function gstr3bsummary(summary){
	var ytdtax = 0.00;var ytdigst31a = 0.00;var ytdsgst31a = 0.00;var ytdcgst31a = 0.00; var ytdcess31a = 0.00;
	var ytdtax31b = 0.00;var ytdigst31b = 0.00;var ytdcess31b = 0.00;
	var ytdtax31c = 0.00;
	var ytdtax31d = 0.00;var ytdigst31d = 0.00;var ytdsgst31d = 0.00;var ytdcgst31d = 0.00; var ytdcess31d = 0.00;
	var ytdtax31e = 0.00;
	var ytditcimpgoodsigst = 0.00;var ytditcimpgoodscess = 0.00;
	var ytditcimpservicesigst = 0.00;var ytditcimpservicescess = 0.00;
	var ytdigstitc4c = 0.00;var ytdsgstitc4c = 0.00;var ytdcgstitc4c = 0.00; var ytdcessitc4c = 0.00;
	var ytdigstitcisd = 0.00;var ytdsgstitcisd = 0.00;var ytdcgstitcisd = 0.00; var ytdcessitcisd = 0.00;
	var ytdigstitcother = 0.00;var ytdsgstitcother = 0.00;var ytdcgstitcother = 0.00; var ytdcessitcother = 0.00;
	var ytdigstitcrev = 0.00;var ytdsgstitcrev = 0.00;var ytdcgstitcrev = 0.00; var ytdcessitcrev = 0.00;
	var ytdigstitcrevothers = 0.00;var ytdsgstitcrevothers = 0.00;var ytdcgstitcrevothers = 0.00; var ytdcessitcrevothers = 0.00;
	var ytdigstitcnet = 0.00;var ytdsgstitcnet = 0.00;var ytdcgstitcnet = 0.00; var ytdcessitcnet = 0.00;
	var ytdigstitcinelg = 0.00;var ytdsgstitcinelg = 0.00;var ytdcgstitcinelg = 0.00; var ytdcessitcinelg = 0.00;
	var ytdigstitcinelgothers = 0.00;var ytdsgstitcinelgothers = 0.00;var ytdcgstitcinelgothers = 0.00; var ytdcessitcinelgothers = 0.00;
	var ytdinterstate51a = 0.00;var ytdintrastate51a = 0.00;var ytdnongstinterstate51a = 0.00;var ytdnongstintrastate51a = 0.00;
	
	var ytdtaxpayableigst = 0.00;var ytdtaxpayablecgst = 0.00;var ytdtaxpayablesgst = 0.00;var ytdtaxpayablecess = 0.00;
	
	var ytdpaidthroughitcigstusingigst = 0.00;var ytdpaidthroughitcigstusingcgst = 0.00;var ytdpaidthroughitcigstusingsgst = 0.00;
	var ytdpaidthroughitccgstusingigst = 0.00;var ytdpaidthroughitccgstusingcgst = 0.00;var ytdpaidthroughitcsgstusingigst = 0.00;
	var ytdpaidthroughitcsgstusingsgst = 0.00;var ytdpaidthroughitccessusingcess = 0.00;
	
	var ytdtaxorcesspaidincashigst = 0.00;var ytdtaxorcesspaidincashcgst = 0.00;var ytdtaxorcesspaidincashsgst = 0.00;var ytdtaxorcesspaidincashcess = 0.00;
	var ytdinterestpaidincashigst = 0.00;var ytdinterestpaidincashcgst = 0.00;var ytdinterestpaidincashsgst = 0.00;var ytdinterestpaidincashcess = 0.00;
	var ytdlatefeepaidincashcgst = 0.00;var ytdlatefeepaidincashsgst = 0.00;
	
	var ytdrctaxpayableigst = 0.00;var ytdrctaxpayablecgst = 0.00;var ytdrctaxpayablesgst = 0.00;var ytdrctaxpayablecess = 0.00;
	var ytdrctaxorcesspaidincashigst = 0.00;var ytdrctaxorcesspaidincashcgst = 0.00;var ytdrctaxorcesspaidincashsgst = 0.00;var ytdrctaxorcesspaidincashcess = 0.00;
	
	$.each(summary, function(key, value){
		var gstr3bdet = JSON.parse(value);
		if(gstr3bdet != null){
			$('#taxable'+key).html(gstr3bdet.supDetails.osupDet.txval);$('#igst31a'+key).html(gstr3bdet.supDetails.osupDet.iamt);$('#cgst31a'+key).html(gstr3bdet.supDetails.osupDet.camt);	$('#sgst31a'+key).html(gstr3bdet.supDetails.osupDet.samt);$('#cess31a'+key).html(gstr3bdet.supDetails.osupDet.csamt);
			$('#taxable31b'+key).html(gstr3bdet.supDetails.osupZero.txval);	$('#igst31b'+key).html(gstr3bdet.supDetails.osupZero.iamt);$('#cess31b'+key).html(gstr3bdet.supDetails.osupZero.csamt);
			$('#taxable31c'+key).html(gstr3bdet.supDetails.osupNilExmp.txval);
			$('#taxable31d'+key).html(gstr3bdet.supDetails.isupRev.txval);$('#igst31d'+key).html(gstr3bdet.supDetails.isupRev.iamt);$('#cgst31d'+key).html(gstr3bdet.supDetails.isupRev.camt);	$('#sgst31d'+key).html(gstr3bdet.supDetails.isupRev.samt);$('#cess31d'+key).html(gstr3bdet.supDetails.isupRev.csamt);
			$('#taxable31e'+key).html(gstr3bdet.supDetails.osupNongst.txval);
			if(gstr3bdet.itcElg.itcAvl.length > 0){
				$('#itcigstimpgoods'+key).html(0.00);$('#itcimpgoodscess'+key).html(0.00);
				$('#itcigstimpservices'+key).html(0.00);$('#itcimpservicescess'+key).html(0.00);
				$('#igstitc4c'+key).html(0.00);$('#cgstitc4c'+key).html(0.00);$('#sgstitc4c'+key).html(0.00);$('#cessitc4c'+key).html(0.00);
				$('#igstitcisd'+key).html(0.00);$('#cgstitcisd'+key).html(0.00);$('#sgstitcisd'+key).html(0.00);$('#cessitcisd'+key).html(0.00);
				$('#igstitcother'+key).html(0.00);$('#cgstitcother'+key).html(0.00);$('#sgstitcother'+key).html(0.00);$('#cessitcother'+key).html(0.00);
				if(gstr3bdet.itcElg.itcAvl[0] != undefined){
					if(gstr3bdet.itcElg.itcAvl[0].ty == "IMPG"){
							$('#itcigstimpgoods'+key).html(gstr3bdet.itcElg.itcAvl[0].iamt);$('#itcimpgoodscess'+key).html(gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt !=null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt !=null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "IMPS"){
							$('#itcigstimpservices'+key).html(gstr3bdet.itcElg.itcAvl[0].iamt);$('#itcimpservicescess'+key).html(gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt !=null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt !=null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "ISRC"){
							$('#igstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[0].iamt);
							$('#cgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[0].camt);
							$('#sgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[0].samt);
							$('#cessitc4c'+key).html(gstr3bdet.itcElg.itcAvl[0].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[0].iamt !=null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].camt !=null && gstr3bdet.itcElg.itcAvl[0].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].samt !=null && gstr3bdet.itcElg.itcAvl[0].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt !=null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "ISD"){
							$('#igstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[0].iamt);$('#cgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[0].camt);$('#sgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[0].samt);$('#cessitcisd'+key).html(gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt != null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].camt != null && gstr3bdet.itcElg.itcAvl[0].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].samt != null && gstr3bdet.itcElg.itcAvl[0].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt != null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "OTH"){
							$('#igstitcother'+key).html(gstr3bdet.itcElg.itcAvl[0].iamt);$('#cgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[0].camt);$('#sgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[0].samt);$('#cessitcother'+key).html(gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt != null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].camt != null && gstr3bdet.itcElg.itcAvl[0].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].samt != null && gstr3bdet.itcElg.itcAvl[0].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt != null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					
				}
				if(gstr3bdet.itcElg.itcAvl[1] != undefined){
					if(gstr3bdet.itcElg.itcAvl[1].ty == "IMPG"){
							$('#itcigstimpgoods'+key).html(gstr3bdet.itcElg.itcAvl[1].iamt);$('#itcimpgoodscess'+key).html(gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt !=null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt !=null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "IMPS"){
							$('#itcigstimpservices'+key).html(gstr3bdet.itcElg.itcAvl[1].iamt);$('#itcimpservicescess'+key).html(gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt !=null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt !=null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "ISRC"){
							$('#igstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[1].iamt);
							$('#cgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[1].camt);
							$('#sgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[1].samt);
							$('#cessitc4c'+key).html(gstr3bdet.itcElg.itcAvl[1].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[1].iamt !=null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].camt !=null && gstr3bdet.itcElg.itcAvl[1].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].samt !=null && gstr3bdet.itcElg.itcAvl[1].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt !=null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "ISD"){
							$('#igstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[1].iamt);$('#cgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[1].camt);$('#sgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[1].samt);$('#cessitcisd'+key).html(gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt != null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].camt != null && gstr3bdet.itcElg.itcAvl[1].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].samt != null && gstr3bdet.itcElg.itcAvl[1].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt != null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "OTH"){
							$('#igstitcother'+key).html(gstr3bdet.itcElg.itcAvl[1].iamt);$('#cgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[1].camt);$('#sgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[1].samt);$('#cessitcother'+key).html(gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt != null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].camt != null && gstr3bdet.itcElg.itcAvl[1].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].samt != null && gstr3bdet.itcElg.itcAvl[1].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt != null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcAvl[2] != undefined){
					if(gstr3bdet.itcElg.itcAvl[2].ty == "IMPG"){
							$('#itcigstimpgoods'+key).html(gstr3bdet.itcElg.itcAvl[2].iamt);$('#itcimpgoodscess'+key).html(gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt !=null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt !=null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "IMPS"){
							$('#itcigstimpservices'+key).html(gstr3bdet.itcElg.itcAvl[2].iamt);$('#itcimpservicescess'+key).html(gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt !=null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt !=null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "ISRC"){
							$('#igstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[2].iamt);
							$('#cgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[2].camt);
							$('#sgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[2].samt);
							$('#cessitc4c'+key).html(gstr3bdet.itcElg.itcAvl[2].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[2].iamt !=null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].camt !=null && gstr3bdet.itcElg.itcAvl[2].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].samt !=null && gstr3bdet.itcElg.itcAvl[2].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt !=null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "ISD"){
							$('#igstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[2].iamt);$('#cgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[2].camt);$('#sgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[2].samt);$('#cessitcisd'+key).html(gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt != null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].camt != null && gstr3bdet.itcElg.itcAvl[2].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].samt != null && gstr3bdet.itcElg.itcAvl[2].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt != null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "OTH"){
							$('#igstitcother'+key).html(gstr3bdet.itcElg.itcAvl[2].iamt);$('#cgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[2].camt);$('#sgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[2].samt);$('#cessitcother'+key).html(gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt != null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].camt != null && gstr3bdet.itcElg.itcAvl[2].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].samt != null && gstr3bdet.itcElg.itcAvl[2].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt != null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcAvl[3] != undefined){
					if(gstr3bdet.itcElg.itcAvl[3].ty == "IMPG"){
							$('#itcigstimpgoods'+key).html(gstr3bdet.itcElg.itcAvl[3].iamt);$('#itcimpgoodscess'+key).html(gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt !=null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt !=null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "IMPS"){
							$('#itcigstimpservices'+key).html(gstr3bdet.itcElg.itcAvl[3].iamt);$('#itcimpservicescess'+key).html(gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt !=null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt !=null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "ISRC"){
							$('#igstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[3].iamt);
							$('#cgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[3].camt);
							$('#sgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[3].samt);
							$('#cessitc4c'+key).html(gstr3bdet.itcElg.itcAvl[3].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[3].iamt !=null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].camt !=null && gstr3bdet.itcElg.itcAvl[3].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].samt !=null && gstr3bdet.itcElg.itcAvl[3].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt !=null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "ISD"){
							$('#igstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[3].iamt);$('#cgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[3].camt);$('#sgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[3].samt);$('#cessitcisd'+key).html(gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt != null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].camt != null && gstr3bdet.itcElg.itcAvl[3].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].samt != null && gstr3bdet.itcElg.itcAvl[3].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt != null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "OTH"){
							$('#igstitcother'+key).html(gstr3bdet.itcElg.itcAvl[3].iamt);$('#cgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[3].camt);$('#sgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[3].samt);$('#cessitcother'+key).html(gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt != null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].camt != null && gstr3bdet.itcElg.itcAvl[3].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].samt != null && gstr3bdet.itcElg.itcAvl[3].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt != null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcAvl[4] != undefined){
					if(gstr3bdet.itcElg.itcAvl[4].ty == "IMPG"){
							$('#itcigstimpgoods'+key).html(gstr3bdet.itcElg.itcAvl[4].iamt);$('#itcimpgoodscess'+key).html(gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt !=null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt !=null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "IMPS"){
							$('#itcigstimpservices'+key).html(gstr3bdet.itcElg.itcAvl[4].iamt);$('#itcimpservicescess'+key).html(gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt !=null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt !=null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "ISRC"){
							$('#igstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[4].iamt);
							$('#cgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[4].camt);
							$('#sgstitc4c'+key).html(gstr3bdet.itcElg.itcAvl[4].samt);
							$('#cessitc4c'+key).html(gstr3bdet.itcElg.itcAvl[4].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[4].iamt !=null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].camt !=null && gstr3bdet.itcElg.itcAvl[4].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].samt !=null && gstr3bdet.itcElg.itcAvl[4].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt !=null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "ISD"){
							$('#igstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[4].iamt);$('#cgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[4].camt);$('#sgstitcisd'+key).html(gstr3bdet.itcElg.itcAvl[4].samt);$('#cessitcisd'+key).html(gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt != null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].camt != null && gstr3bdet.itcElg.itcAvl[4].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].samt != null && gstr3bdet.itcElg.itcAvl[4].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt != null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "OTH"){
							$('#igstitcother'+key).html(gstr3bdet.itcElg.itcAvl[4].iamt);$('#cgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[4].camt);$('#sgstitcother'+key).html(gstr3bdet.itcElg.itcAvl[4].samt);$('#cessitcother'+key).html(gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt != null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].camt != null && gstr3bdet.itcElg.itcAvl[4].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].samt != null && gstr3bdet.itcElg.itcAvl[4].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt != null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
				}
			}else{
				$('#itcigstimpgoods'+key).html(0.00);$('#itcimpgoodscess'+key).html(0.00);
				$('#itcigstimpservices'+key).html(0.00);$('#itcimpservicescess'+key).html(0.00);
				$('#igstitc4c'+key).html(0.00);$('#cgstitc4c'+key).html(0.00);$('#sgstitc4c'+key).html(0.00);$('#cessitc4c'+key).html(0.00);
				$('#igstitcisd'+key).html(0.00);$('#cgstitcisd'+key).html(0.00);$('#sgstitcisd'+key).html(0.00);$('#cessitcisd'+key).html(0.00);
				$('#igstitcother'+key).html(0.00);$('#cgstitcother'+key).html(0.00);$('#sgstitcother'+key).html(0.00);$('#cessitcother'+key).html(0.00);
			}
			if(gstr3bdet.itcElg.itcRev.length > 0){
				$('#igstitcrev'+key).html(0.00);$('#cgstitcrev'+key).html(0.00);$('#sgstitcrev'+key).html(0.00);$('#cessitcrev'+key).html(0.00);
				$('#igstitcrevothers'+key).html(0.00);$('#cgstitcrevothers'+key).html(0.00);$('#sgstitcrevothers'+key).html(0.00);$('#cessitcrevothers'+key).html(0.00);
				if(gstr3bdet.itcElg.itcRev[0] != undefined){
					if(gstr3bdet.itcElg.itcRev[0].ty == "RUL"){
						$('#igstitcrev'+key).html(gstr3bdet.itcElg.itcRev[0].iamt);
						$('#cgstitcrev'+key).html(gstr3bdet.itcElg.itcRev[0].camt);
						$('#sgstitcrev'+key).html(gstr3bdet.itcElg.itcRev[0].samt);
						$('#cessitcrev'+key).html(gstr3bdet.itcElg.itcRev[0].csamt);
						if(gstr3bdet.itcElg.itcRev[0].iamt !=null && gstr3bdet.itcElg.itcRev[0].iamt !=""){
							ytdigstitcrev = ytdigstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].iamt);
						}
						if(gstr3bdet.itcElg.itcRev[0].camt !=null && gstr3bdet.itcElg.itcRev[0].camt !=""){
							ytdcgstitcrev = ytdcgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].camt);
						}
						if(gstr3bdet.itcElg.itcRev[0].samt !=null && gstr3bdet.itcElg.itcRev[0].csamt !=""){
							ytdsgstitcrev = ytdsgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].samt);
						}
						if(gstr3bdet.itcElg.itcRev[0].csamt !=null && gstr3bdet.itcElg.itcRev[0].csamt !=""){
							ytdcessitcrev = ytdcessitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcRev[0].ty == "OTH"){
							$('#igstitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[0].iamt);$('#cgstitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[0].camt);$('#sgstitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[0].samt);$('#cessitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[0].csamt);
							if(gstr3bdet.itcElg.itcRev[0].iamt !=null && gstr3bdet.itcElg.itcRev[0].iamt !=""){
								ytdigstitcrevothers = ytdigstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].iamt);
							}
							if(gstr3bdet.itcElg.itcRev[0].camt !=null && gstr3bdet.itcElg.itcRev[0].camt !=""){
								ytdcgstitcrevothers = ytdcgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].camt);
							}
							if(gstr3bdet.itcElg.itcRev[0].samt !=null && gstr3bdet.itcElg.itcRev[0].samt !=""){
								ytdsgstitcrevothers = ytdsgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].samt);
							}
							if(gstr3bdet.itcElg.itcRev[0].csamtt !=null && gstr3bdet.itcElg.itcRev[0].csamt !=""){
								ytdcessitcrevothers = ytdcessitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcRev[1] != undefined){
					if(gstr3bdet.itcElg.itcRev[1].ty == "RUL"){
						$('#igstitcrev'+key).html(gstr3bdet.itcElg.itcRev[1].iamt);
						$('#cgstitcrev'+key).html(gstr3bdet.itcElg.itcRev[1].camt);
						$('#sgstitcrev'+key).html(gstr3bdet.itcElg.itcRev[1].samt);
						$('#cessitcrev'+key).html(gstr3bdet.itcElg.itcRev[1].csamt);
						if(gstr3bdet.itcElg.itcRev[1].iamt !=null && gstr3bdet.itcElg.itcRev[1].iamt !=""){
							ytdigstitcrev = ytdigstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].iamt);
						}
						if(gstr3bdet.itcElg.itcRev[1].camt !=null && gstr3bdet.itcElg.itcRev[1].camt !=""){
							ytdcgstitcrev = ytdcgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].camt);
						}
						if(gstr3bdet.itcElg.itcRev[1].samt !=null && gstr3bdet.itcElg.itcRev[1].csamt !=""){
							ytdsgstitcrev = ytdsgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].samt);
						}
						if(gstr3bdet.itcElg.itcRev[1].csamt !=null && gstr3bdet.itcElg.itcRev[1].csamt !=""){
							ytdcessitcrev = ytdcessitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcRev[1].ty == "OTH"){
						$('#igstitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[1].iamt);$('#cgstitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[1].camt);$('#sgstitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[1].samt);$('#cessitcrevothers'+key).html(gstr3bdet.itcElg.itcRev[1].csamt);
						if(gstr3bdet.itcElg.itcRev[1].iamt !=null && gstr3bdet.itcElg.itcRev[1].iamt !=""){
							ytdigstitcrevothers = ytdigstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].iamt);
						}
						if(gstr3bdet.itcElg.itcRev[1].camt !=null && gstr3bdet.itcElg.itcRev[1].camt !=""){
							ytdcgstitcrevothers = ytdcgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].camt);
						}
						if(gstr3bdet.itcElg.itcRev[1].samt !=null && gstr3bdet.itcElg.itcRev[1].samt !=""){
							ytdsgstitcrevothers = ytdsgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].samt);
						}
						if(gstr3bdet.itcElg.itcRev[1].csamtt !=null && gstr3bdet.itcElg.itcRev[1].csamt !=""){
							ytdcessitcrevothers = ytdcessitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].csamt);
						}
					}
				}
			}else{
				$('#igstitcrev'+key).html(0.00);$('#cgstitcrev'+key).html(0.00);$('#sgstitcrev'+key).html(0.00);$('#cessitcrev'+key).html(0.00);
				$('#igstitcrevothers'+key).html(0.00);$('#cgstitcrevothers'+key).html(0.00);$('#sgstitcrevothers'+key).html(0.00);$('#cessitcrevothers'+key).html(0.00);
			}
			if(gstr3bdet.itcElg.itcNet != undefined){
				$('#igstitcnet'+key).html(gstr3bdet.itcElg.itcNet.iamt);$('#cgstitcnet'+key).html(gstr3bdet.itcElg.itcNet.camt);$('#sgstitcnet'+key).html(gstr3bdet.itcElg.itcNet.samt);$('#cessitcnet'+key).html(gstr3bdet.itcElg.itcNet.csamt);
				if(gstr3bdet.itcElg.itcNet.iamt !=null && gstr3bdet.itcElg.itcNet.iamt !=""){
					ytdigstitcnet = ytdigstitcnet+parseFloat(gstr3bdet.itcElg.itcNet.iamt);
				}
				if(gstr3bdet.itcElg.itcNet.camt !=null && gstr3bdet.itcElg.itcNet.camt !=""){
					ytdcgstitcnet = ytdcgstitcnet+parseFloat(gstr3bdet.itcElg.itcNet.camt);
				}
				if(gstr3bdet.itcElg.itcNet.samt !=null && gstr3bdet.itcElg.itcNet.samt !=""){
					ytdsgstitcnet = ytdsgstitcnet+parseFloat(gstr3bdet.itcElg.itcNet.samt);
				}
				if(gstr3bdet.itcElg.itcNet.csamt !=null && gstr3bdet.itcElg.itcNet.csamt !=""){
					ytdcessitcnet = ytdcessitcnet+parseFloat(gstr3bdet.itcElg.itcNet.csamt);
				}
			}else{
				$('#igstitcnet'+key).html(0.00);$('#cgstitcnet'+key).html(0.00);$('#sgstitcnet'+key).html(0.00);$('#cessitcnet'+key).html(0.00);
			}
			if(gstr3bdet.itcElg.itcInelg.length > 0){
				$('#igstitcinelg'+key).html(0.00);$('#cgstitcinelg'+key).html(0.00);$('#sgstitcinelg'+key).html(0.00);$('#cessitcinelg'+key).html(0.00);
				$('#igstitcinelgothers'+key).html(0.00);$('#cgstitcinelgothers'+key).html(0.00);$('#sgstitcinelgothers'+key).html(0.00);$('#cessitcinelgothers'+key).html(0.00);
				if(gstr3bdet.itcElg.itcInelg[0] != undefined){
					if(gstr3bdet.itcElg.itcInelg[0].ty == "RUL"){
						$('#igstitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[0].iamt);$('#cgstitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[0].camt);$('#sgstitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[0].samt);$('#cessitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[0].csamt);
						if(gstr3bdet.itcElg.itcInelg[0].iamt !=null && gstr3bdet.itcElg.itcInelg[0].iamt !=""){
							ytdigstitcinelg = ytdigstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].iamt);
						}
						if(gstr3bdet.itcElg.itcInelg[0].camt !=null && gstr3bdet.itcElg.itcInelg[0].camt !=""){
							ytdcgstitcinelg = ytdcgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].camt);
						}
						if(gstr3bdet.itcElg.itcInelg[0].samt !=null && gstr3bdet.itcElg.itcInelg[0].samt !=""){
							ytdsgstitcinelg = ytdsgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].samt);
						}
						if(gstr3bdet.itcElg.itcInelg[0].csamt !=null && gstr3bdet.itcElg.itcInelg[0].csamt !=""){
							ytdcessitcinelg = ytdcessitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcInelg[0].ty == "OTH"){
							$('#igstitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[0].iamt);$('#cgstitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[0].camt);$('#sgstitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[0].samt);$('#cessitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[0].csamt);
							if(gstr3bdet.itcElg.itcInelg[0].iamt !=null && gstr3bdet.itcElg.itcInelg[0].iamt !=""){
								ytdigstitcinelgothers = ytdigstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].iamt);
							}
							if(gstr3bdet.itcElg.itcInelg[0].camt !=null && gstr3bdet.itcElg.itcInelg[0].camt !=""){
								ytdcgstitcinelgothers = ytdcgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].camt);
							}
							if(gstr3bdet.itcElg.itcInelg[0].samt !=null && gstr3bdet.itcElg.itcInelg[0].samt !=""){
								ytdsgstitcinelgothers = ytdsgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].samt);
							}
							if(gstr3bdet.itcElg.itcInelg[0].csamt !=null && gstr3bdet.itcElg.itcInelg[0].csamt !=""){
								ytdcessitcinelgothers = ytdcessitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcInelg[1] != undefined){
					
					if(gstr3bdet.itcElg.itcInelg[1].ty == "RUL"){
						$('#igstitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[1].iamt);$('#cgstitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[1].camt);$('#sgstitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[1].samt);$('#cessitcinelg'+key).html(gstr3bdet.itcElg.itcInelg[1].csamt);
						if(gstr3bdet.itcElg.itcInelg[1].iamt !=null && gstr3bdet.itcElg.itcInelg[1].iamt !=""){
							ytdigstitcinelg = ytdigstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].iamt);
						}
						if(gstr3bdet.itcElg.itcInelg[1].camt !=null && gstr3bdet.itcElg.itcInelg[1].camt !=""){
							ytdcgstitcinelg = ytdcgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].camt);
						}
						if(gstr3bdet.itcElg.itcInelg[1].samt !=null && gstr3bdet.itcElg.itcInelg[1].samt !=""){
							ytdsgstitcinelg = ytdsgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].samt);
						}
						if(gstr3bdet.itcElg.itcInelg[1].csamt !=null && gstr3bdet.itcElg.itcInelg[1].csamt !=""){
							ytdcessitcinelg = ytdcessitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcInelg[1].ty == "OTH"){
							$('#igstitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[1].iamt);$('#cgstitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[1].camt);$('#sgstitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[1].samt);$('#cessitcinelgothers'+key).html(gstr3bdet.itcElg.itcInelg[1].csamt);
							if(gstr3bdet.itcElg.itcInelg[1].iamt !=null && gstr3bdet.itcElg.itcInelg[1].iamt !=""){
								ytdigstitcinelgothers = ytdigstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].iamt);
							}
							if(gstr3bdet.itcElg.itcInelg[1].camt !=null && gstr3bdet.itcElg.itcInelg[1].camt !=""){
								ytdcgstitcinelgothers = ytdcgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].camt);
							}
							if(gstr3bdet.itcElg.itcInelg[1].samt !=null && gstr3bdet.itcElg.itcInelg[1].samt !=""){
								ytdsgstitcinelgothers = ytdsgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].samt);
							}
							if(gstr3bdet.itcElg.itcInelg[1].csamt !=null && gstr3bdet.itcElg.itcInelg[1].csamt !=""){
								ytdcessitcinelgothers = ytdcessitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].csamt);
							}
					}
				}
			}else{
				$('#igstitcinelg'+key).html(0.00);$('#cgstitcinelg'+key).html(0.00);$('#sgstitcinelg'+key).html(0.00);$('#cessitcinelg'+key).html(0.00);
				$('#igstitcinelgothers'+key).html(0.00);$('#cgstitcinelgothers'+key).html(0.00);$('#sgstitcinelgothers'+key).html(0.00);$('#cessitcinelgothers'+key).html(0.00);
			}
			
			if(gstr3bdet.inwardSup.isupDetails.length > 0){
				if(gstr3bdet.inwardSup.isupDetails[0] != undefined){
					$('#interstate51a'+key).html(gstr3bdet.inwardSup.isupDetails[0].inter);
					if(gstr3bdet.inwardSup.isupDetails[0].inter !=null && gstr3bdet.inwardSup.isupDetails[0].inter !=""){
						ytdinterstate51a = ytdinterstate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[0].inter);
					}
					$('#intrastate51a'+key).html(gstr3bdet.inwardSup.isupDetails[0].intra);
					if(gstr3bdet.inwardSup.isupDetails[0].intra !=null && gstr3bdet.inwardSup.isupDetails[0].intra !=""){
						ytdintrastate51a = ytdintrastate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[0].intra);
					}
				}else{
					$('#interstate51a'+key).html(0.00);
					$('#intrastate51a'+key).html(0.00);
				}
				if(gstr3bdet.inwardSup.isupDetails[1] != undefined){
					$('#nongstinterstate51a'+key).html(gstr3bdet.inwardSup.isupDetails[1].inter);
					if(gstr3bdet.inwardSup.isupDetails[1].inter !=null && gstr3bdet.inwardSup.isupDetails[1].inter !=""){
						ytdnongstinterstate51a = ytdnongstinterstate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[1].inter);
					}
					$('#nongstintrastate51a'+key).html(gstr3bdet.inwardSup.isupDetails[1].intra);
					if(gstr3bdet.inwardSup.isupDetails[1].intra !=null && gstr3bdet.inwardSup.isupDetails[1].intra !=""){
						ytdnongstintrastate51a = ytdnongstintrastate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[1].intra);
					}
				}else{
					$('#nongstinterstate51a'+key).html(0.00);
					$('#nongstintrastate51a'+key).html(0.00);
				}
			}else{
				$('#interstate51a'+key).html(0.00);
				$('#intrastate51a'+key).html(0.00);
				$('#nongstinterstate51a'+key).html(0.00);
				$('#nongstintrastate51a'+key).html(0.00);
			}
			if(gstr3bdet.supDetails.osupDet.txval !=null && gstr3bdet.supDetails.osupDet.txval !=""){
				ytdtax = ytdtax+parseFloat(gstr3bdet.supDetails.osupDet.txval);				
			}
			if(gstr3bdet.supDetails.osupDet.iamt !=null && gstr3bdet.supDetails.osupDet.iamt !=""){
				ytdigst31a = ytdigst31a+parseFloat(gstr3bdet.supDetails.osupDet.iamt);
			}
			if(gstr3bdet.supDetails.osupDet.camt !=null && gstr3bdet.supDetails.osupDet.camt !=""){
				ytdcgst31a = ytdcgst31a+parseFloat(gstr3bdet.supDetails.osupDet.camt);
			}
			if(gstr3bdet.supDetails.osupDet.samt !=null && gstr3bdet.supDetails.osupDet.samt !=""){
				ytdsgst31a = ytdsgst31a+parseFloat(gstr3bdet.supDetails.osupDet.samt);
			}
			if(gstr3bdet.supDetails.osupDet.csamt !=null && gstr3bdet.supDetails.osupDet.csamt !=""){
				ytdcess31a = ytdcess31a+parseFloat(gstr3bdet.supDetails.osupDet.csamt);
			}
			if(gstr3bdet.supDetails.osupZero.txval !=null && gstr3bdet.supDetails.osupZero.txval !=""){
				ytdtax31b = ytdtax31b+parseFloat(gstr3bdet.supDetails.osupZero.txval);
			}
			if(gstr3bdet.supDetails.osupZero.iamt !=null && gstr3bdet.supDetails.osupZero.iamt !=""){
				ytdigst31b = ytdigst31b+parseFloat(gstr3bdet.supDetails.osupZero.iamt);
			}
			if(gstr3bdet.supDetails.osupZero.csamt !=null && gstr3bdet.supDetails.osupZero.csamt !=""){
				ytdcess31b = ytdcess31b+parseFloat(gstr3bdet.supDetails.osupZero.csamt);
			}
			if(gstr3bdet.supDetails.osupNilExmp.txval !=null && gstr3bdet.supDetails.osupNilExmp.txval !=""){
				ytdtax31c = ytdtax31c+parseFloat(gstr3bdet.supDetails.osupNilExmp.txval);
			}
			if(gstr3bdet.supDetails.isupRev.txval !=null && gstr3bdet.supDetails.isupRev.txval !=""){
				ytdtax31d = ytdtax31d+parseFloat(gstr3bdet.supDetails.isupRev.txval);
			}
			if(gstr3bdet.supDetails.isupRev.iamt !=null && gstr3bdet.supDetails.isupRev.iamt !=""){
				ytdigst31d = ytdigst31d+parseFloat(gstr3bdet.supDetails.isupRev.iamt);
			}
			if(gstr3bdet.supDetails.isupRev.camt !=null && gstr3bdet.supDetails.isupRev.camt !=""){
				ytdcgst31d = ytdcgst31d+parseFloat(gstr3bdet.supDetails.isupRev.camt);
			}
			if(gstr3bdet.supDetails.isupRev.samt !=null && gstr3bdet.supDetails.isupRev.samt !=""){
				ytdsgst31d = ytdsgst31d+parseFloat(gstr3bdet.supDetails.isupRev.samt);
			}
			if(gstr3bdet.supDetails.isupRev.csamt !=null && gstr3bdet.supDetails.isupRev.csamt !=""){
				ytdcess31d = ytdcess31d+parseFloat(gstr3bdet.supDetails.isupRev.csamt);
			}
			if(gstr3bdet.supDetails.osupNongst.txval !=null && gstr3bdet.supDetails.osupNongst.txval != ""){
				ytdtax31e = ytdtax31e+parseFloat(gstr3bdet.supDetails.osupNongst.txval);
			}
			if(gstr3bdet.offLiab !=null){
				if(gstr3bdet.offLiab.taxPayable !=null){
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].igst != null && gstr3bdet.offLiab.taxPayable[0].igst.tx !=null){
						$('#taxpayableigst'+key).html(gstr3bdet.offLiab.taxPayable[0].igst.tx);
						ytdtaxpayableigst += parseFloat(gstr3bdet.offLiab.taxPayable[0].igst.tx);						
					}
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].cgst != null && gstr3bdet.offLiab.taxPayable[0].cgst.tx !=null){
						$('#taxpayablecgst'+key).html(gstr3bdet.offLiab.taxPayable[0].cgst.tx);
						ytdtaxpayablecgst += parseFloat(gstr3bdet.offLiab.taxPayable[0].cgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].sgst != null && gstr3bdet.offLiab.taxPayable[0].sgst.tx !=null){
						$('#taxpayablesgst'+key).html(gstr3bdet.offLiab.taxPayable[0].sgst.tx);
						ytdtaxpayablesgst += parseFloat(gstr3bdet.offLiab.taxPayable[0].sgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].cess != null && gstr3bdet.offLiab.taxPayable[0].cess.tx !=null){
						$('#taxpayablecess'+key).html(gstr3bdet.offLiab.taxPayable[0].cess.tx);
						ytdtaxpayablecess += parseFloat(gstr3bdet.offLiab.taxPayable[0].cess.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].igst != null && gstr3bdet.offLiab.taxPayable[1].igst.tx !=null){
						$('#rctaxpayableigst'+key).html(gstr3bdet.offLiab.taxPayable[1].igst.tx);
						ytdrctaxpayableigst += parseFloat(gstr3bdet.offLiab.taxPayable[1].igst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].cgst != null && gstr3bdet.offLiab.taxPayable[1].cgst.tx !=null){
						$('#rctaxpayablecgst'+key).html(gstr3bdet.offLiab.taxPayable[1].cgst.tx);
						ytdrctaxpayablecgst += parseFloat(gstr3bdet.offLiab.taxPayable[1].cgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].sgst != null && gstr3bdet.offLiab.taxPayable[1].sgst.tx !=null){
						$('#rctaxpayablesgst'+key).html(gstr3bdet.offLiab.taxPayable[1].sgst.tx);
						ytdrctaxpayablesgst += parseFloat(gstr3bdet.offLiab.taxPayable[1].sgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].cess != null && gstr3bdet.offLiab.taxPayable[1].cess.tx !=null){
						$('#rctaxpayablecess'+key).html(gstr3bdet.offLiab.taxPayable[1].cess.tx);
						ytdrctaxpayablecess += parseFloat(gstr3bdet.offLiab.taxPayable[1].cess.tx);
					}
				}
				if(gstr3bdet.offLiab.pditc !=null){
					if(gstr3bdet.offLiab.pditc.igstPdigst != null){
						$('#paidthroughitcigstusingigst'+key).html(gstr3bdet.offLiab.pditc.igstPdigst);
						ytdpaidthroughitcigstusingigst += parseFloat(gstr3bdet.offLiab.pditc.igstPdigst);
					}
					if(gstr3bdet.offLiab.pditc.igstPdcgst != null){
						$('#paidthroughitcigstusingcgst'+key).html(gstr3bdet.offLiab.pditc.igstPdcgst);
						ytdpaidthroughitcigstusingcgst += parseFloat(gstr3bdet.offLiab.pditc.igstPdcgst);
					}
					if(gstr3bdet.offLiab.pditc.igstPdsgst != null){
						$('#paidthroughitcigstusingsgst'+key).html(gstr3bdet.offLiab.pditc.igstPdsgst);
						ytdpaidthroughitcigstusingsgst += parseFloat(gstr3bdet.offLiab.pditc.igstPdsgst);
					}
					if(gstr3bdet.offLiab.pditc.cgstPdigst != null){
						$('#paidthroughitccgstusingigst'+key).html(gstr3bdet.offLiab.pditc.cgstPdigst);
						ytdpaidthroughitccgstusingigst += parseFloat(gstr3bdet.offLiab.pditc.cgstPdigst);
					}
					if(gstr3bdet.offLiab.pditc.cgstPdcgst != null){
						$('#paidthroughitccgstusingcgst'+key).html(gstr3bdet.offLiab.pditc.cgstPdcgst);
						ytdpaidthroughitccgstusingcgst += parseFloat(gstr3bdet.offLiab.pditc.cgstPdcgst);
					}
					if(gstr3bdet.offLiab.pditc.sgstPdigst != null){
						$('#paidthroughitcsgstusingigst'+key).html(gstr3bdet.offLiab.pditc.sgstPdigst);
						ytdpaidthroughitcsgstusingigst += parseFloat(gstr3bdet.offLiab.pditc.sgstPdigst);
					}
					if(gstr3bdet.offLiab.pditc.sgstPdsgst != null){
						$('#paidthroughitcsgstusingsgst'+key).html(gstr3bdet.offLiab.pditc.sgstPdsgst);
						ytdpaidthroughitcsgstusingsgst += parseFloat(gstr3bdet.offLiab.pditc.sgstPdsgst);
					}
					if(gstr3bdet.offLiab.pditc.cessPdcess != null){
						$('#paidthroughitccessusingcess'+key).html(gstr3bdet.offLiab.pditc.cessPdcess);
						ytdpaidthroughitccessusingcess += parseFloat(gstr3bdet.offLiab.pditc.cessPdcess);
					}
				}
				//gstr3bdet.offLiab.pdcash[0].ipd
				if(gstr3bdet.offLiab.pdcash !=null){
					if(gstr3bdet.offLiab.pdcash[0] != null && gstr3bdet.offLiab.pdcash[0].ipd !=null){
						$('#taxorcesspaidincashigst'+key).html(gstr3bdet.offLiab.pdcash[0].ipd);
						ytdtaxorcesspaidincashigst += parseFloat(gstr3bdet.offLiab.pdcash[0].ipd);
					}
					if(gstr3bdet.offLiab.pdcash[1] != null && gstr3bdet.offLiab.pdcash[1].ipd !=null){
						$('#rctaxorcesspaidincashigst'+key).html(gstr3bdet.offLiab.pdcash[1].ipd);
						ytdrctaxorcesspaidincashigst += parseFloat(gstr3bdet.offLiab.pdcash[1].ipd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cpd !=null){
						$('#taxorcesspaidincashcgst'+key).html(gstr3bdet.offLiab.pdcash[0].cpd);
						ytdtaxorcesspaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[0].cpd);
					}
					if(gstr3bdet.offLiab.pdcash[1] != null  && gstr3bdet.offLiab.pdcash[1].cpd !=null){
						$('#rctaxorcesspaidincashcgst'+key).html(gstr3bdet.offLiab.pdcash[1].cpd);
						ytdrctaxorcesspaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[1].cpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].spd !=null){
						$('#taxorcesspaidincashsgst'+key).html(gstr3bdet.offLiab.pdcash[0].spd);
						ytdtaxorcesspaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[0].spd);
					}
					if(gstr3bdet.offLiab.pdcash[1] != null  && gstr3bdet.offLiab.pdcash[1].spd !=null){
						$('#rctaxorcesspaidincashsgst'+key).html(gstr3bdet.offLiab.pdcash[1].spd);
						ytdrctaxorcesspaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[1].spd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cspd !=null){
						$('#taxorcesspaidincashcess'+key).html(gstr3bdet.offLiab.pdcash[0].cspd);
						ytdtaxorcesspaidincashcess += parseFloat(gstr3bdet.offLiab.pdcash[0].cspd);	
					}
					if(gstr3bdet.offLiab.pdcash[1] != null  && gstr3bdet.offLiab.pdcash[1].cspd !=null){
						$('#rctaxorcesspaidincashcess'+key).html(gstr3bdet.offLiab.pdcash[1].cspd);
						ytdrctaxorcesspaidincashcess += parseFloat(gstr3bdet.offLiab.pdcash[1].cspd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].igstIntrpd !=null){
						$('#interestpaidincashigst'+key).html(gstr3bdet.offLiab.pdcash[0].igstIntrpd);
						ytdinterestpaidincashigst += parseFloat(gstr3bdet.offLiab.pdcash[0].igstIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cgstIntrpd !=null){
						$('#interestpaidincashcgst'+key).html(gstr3bdet.offLiab.pdcash[0].cgstIntrpd);
						ytdinterestpaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[0].cgstIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].sgstIntrpd !=null){
						$('#interestpaidincashsgst'+key).html(gstr3bdet.offLiab.pdcash[0].sgstIntrpd);
						ytdinterestpaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[0].sgstIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cessIntrpd !=null){
						$('#interestpaidincashcess'+key).html(gstr3bdet.offLiab.pdcash[0].cessIntrpd);
						ytdinterestpaidincashcess += parseFloat(gstr3bdet.offLiab.pdcash[0].cessIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cgstLfeepd !=null){
						$('#latefeepaidincashcgst'+key).html(gstr3bdet.offLiab.pdcash[0].cgstLfeepd);
						ytdlatefeepaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[0].cgstLfeepd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cgstLfeepd !=null){
						$('#latefeepaidincashsgst'+key).html(gstr3bdet.offLiab.pdcash[0].sgstLfeepd);
						ytdlatefeepaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[0].sgstLfeepd);
					}
				}	
			}
		}else{
			$('#taxable'+key).html(0.00);$('#igst31a'+key).html(0.00);$('#cgst31a'+key).html(0.00);	$('#sgst31a'+key).html(0.00);$('#cess31a'+key).html(0.00);
			$('#taxable31b'+key).html(0.00);$('#igst31b'+key).html(0.00);$('#cess31b'+key).html(0.00);
			$('#taxable31c'+key).html(0.00);
			$('#taxable31d'+key).html(0.00);$('#igst31d'+key).html(0.00);$('#cgst31d'+key).html(0.00);	$('#sgst31d'+key).html(0.00);$('#cess31d'+key).html(0.00);
			$('#taxable31e'+key).html(0.00);
			$('#itcigstimpgoods'+key).html(0.00);$('#itcimpgoodscess'+key).html(0.00);
			$('#itcigstimpservices'+key).html(0.00);$('#itcimpservicescess'+key).html(0.00);
			$('#igstitc4c'+key).html(0.00);$('#cgstitc4c'+key).html(0.00);$('#sgstitc4c'+key).html(0.00);$('#cessitc4c'+key).html(0.00);
			$('#igstitcisd'+key).html(0.00);$('#cgstitcisd'+key).html(0.00);$('#sgstitcisd'+key).html(0.00);$('#cessitcisd'+key).html(0.00);
			$('#igstitcother'+key).html(0.00);$('#cgstitcother'+key).html(0.00);$('#sgstitcother'+key).html(0.00);$('#cessitcother'+key).html(0.00);
			$('#igstitcrev'+key).html(0.00);$('#cgstitcrev'+key).html(0.00);$('#sgstitcrev'+key).html(0.00);$('#cessitcrev'+key).html(0.00);
			$('#igstitcrevothers'+key).html(0.00);$('#cgstitcrevothers'+key).html(0.00);$('#sgstitcrevothers'+key).html(0.00);$('#cessitcrevothers'+key).html(0.00);
			$('#igstitcnet'+key).html(0.00);$('#cgstitcnet'+key).html(0.00);$('#sgstitcnet'+key).html(0.00);$('#cessitcnet'+key).html(0.00);
			$('#igstitcinelg'+key).html(0.00);$('#cgstitcinelg'+key).html(0.00);$('#sgstitcinelg'+key).html(0.00);$('#cessitcinelg'+key).html(0.00);
			$('#igstitcinelgothers'+key).html(0.00);$('#cgstitcinelgothers'+key).html(0.00);$('#sgstitcinelgothers'+key).html(0.00);$('#cessitcinelgothers'+key).html(0.00);
			$('#interstate51a'+key).html(0.00);	$('#intrastate51a'+key).html(0.00);
			$('#nongstinterstate51a'+key).html(0.00);$('#nongstintrastate51a'+key).html(0.00);
		}
	});
	$('#ytdtaxable').html(ytdtax);$('#ytdigst31a').html(ytdigst31a);$('#ytdcgst31a').html(ytdcgst31a);$('#ytdsgst31a').html(ytdsgst31a);$('#ytdcess31a').html(ytdcess31a);
	$('#ytdtaxable31b').html(ytdtax31b);$('#ytdigst31b').html(ytdigst31b);$('#ytdcess31b').html(ytdcess31b);
	$('#ytdtaxable31c').html(ytdtax31c);
	$('#ytdtaxable31d').html(ytdtax31d);$('#ytdigst31d').html(ytdigst31d);$('#ytdcgst31d').html(ytdcgst31d);$('#ytdsgst31d').html(ytdsgst31d);$('#ytdcess31d').html(ytdcess31d);
	$('#ytdtaxable31e').html(ytdtax31e);
	$('#ytditcigstimpgoods').html(ytditcimpgoodsigst);$('#ytditccessimpgoods').html(ytditcimpgoodscess);
	$('#ytditcigstimpservices').html(ytditcimpservicesigst);$('#ytditccessimpservices').html(ytditcimpservicescess);
	$('#ytdigstitc4c').html(ytdigstitc4c);$('#ytdcgstitc4c').html(ytdcgstitc4c);$('#ytdsgstitc4c').html(ytdsgstitc4c);$('#ytdcessitc4c').html(ytdcessitc4c);
	$('#ytdigstitcisd').html(ytdigstitcisd);$('#ytdcgstitcisd').html(ytdcgstitcisd);$('#ytdsgstitcisd').html(ytdsgstitcisd);$('#ytdcessitcisd').html(ytdcessitcisd);
	$('#ytdigstitcother').html(ytdigstitcother);$('#ytdcgstitcother').html(ytdcgstitcother);$('#ytdsgstitcother').html(ytdsgstitcother);$('#ytdcessitcother').html(ytdcessitcother);
	$('#ytdigstitcrev').html(ytdigstitcrev);$('#ytdcgstitcrev').html(ytdcgstitcrev);$('#ytdsgstitcrev').html(ytdsgstitcrev);$('#ytdcessitcrev').html(ytdcessitcrev);
	$('#ytdigstitcrevothers').html(ytdigstitcrevothers);$('#ytdcgstitcrevothers').html(ytdcgstitcrevothers);$('#ytdsgstitcrevothers').html(ytdsgstitcrevothers);$('#ytdcessitcrevothers').html(ytdcessitcrevothers);
	$('#ytdigstitcnet').html(ytdigstitcnet);$('#ytdcgstitcnet').html(ytdcgstitcnet);$('#ytdsgstitcnet').html(ytdsgstitcnet);$('#ytdcessitcnet').html(ytdcessitcnet);
	$('#ytdigstitcinelg').html(ytdigstitcinelg);$('#ytdcgstitcinelg').html(ytdcgstitcinelg);$('#ytdsgstitcinelg').html(ytdsgstitcinelg);$('#ytdcessitcinelg').html(ytdcessitcinelg);
	$('#ytdigstitcinelgothers').html(ytdigstitcinelgothers);$('#ytdcgstitcinelgothers').html(ytdcgstitcinelgothers);$('#ytdsgstitcinelgothers').html(ytdsgstitcinelgothers);$('#ytdcessitcinelgothers').html(ytdcessitcinelgothers);
	$('#ytdinterstate51a').html(ytdinterstate51a);$('#ytdintrastate51a').html(ytdintrastate51a);
	$('#ytdnongstinterstate51a').html(ytdnongstinterstate51a);$('#ytdnongstintrastate51a').html(ytdnongstintrastate51a);
	
	$('#ytdtaxpayableigst').html(ytdtaxpayableigst);$('#ytdtaxpayablecgst').html(ytdtaxpayablecgst);$('#ytdtaxpayablesgst').html(ytdtaxpayablesgst);$('#ytdtaxpayablecess').html(ytdtaxpayablecess);
	
	$('#ytdpaidthroughitcigstusingigst').html(ytdpaidthroughitcigstusingigst);$('#ytdpaidthroughitcigstusingcgst').html(ytdpaidthroughitcigstusingcgst);$('#ytdpaidthroughitcigstusingsgst').html(ytdpaidthroughitcigstusingsgst);
	$('#ytdpaidthroughitccgstusingigst').html(ytdpaidthroughitccgstusingigst);$('#ytdpaidthroughitccgstusingcgst').html(ytdpaidthroughitccgstusingcgst);$('#ytdpaidthroughitcsgstusingigst').html(ytdpaidthroughitcsgstusingigst);
	$('#ytdpaidthroughitcsgstusingsgst').html(ytdpaidthroughitcsgstusingsgst);$('#ytdpaidthroughitccessusingcess').html(ytdpaidthroughitccessusingcess);
	
	$('#ytdtaxorcesspaidincashigst').html(ytdtaxorcesspaidincashigst);$('#ytdtaxorcesspaidincashcgst').html(ytdtaxorcesspaidincashcgst);$('#ytdtaxorcesspaidincashsgst').html(ytdtaxorcesspaidincashsgst);$('#ytdtaxorcesspaidincashcess').html(ytdtaxorcesspaidincashcess);
	$('#ytdinterestpaidincashigst').html(ytdinterestpaidincashigst);$('#ytdinterestpaidincashcgst').html(ytdinterestpaidincashcgst);$('#ytdinterestpaidincashsgst').html(ytdinterestpaidincashsgst);$('#ytdinterestpaidincashcess').html(ytdinterestpaidincashcess);
	$('#ytdlatefeepaidincashcgst').html(ytdlatefeepaidincashcgst);$('#ytdlatefeepaidincashsgst').html(ytdlatefeepaidincashsgst);
	
	$('#ytdrctaxpayableigst').html(ytdrctaxpayableigst);$('#ytdrctaxpayablecgst').html(ytdrctaxpayablecgst);$('#ytdrctaxpayablesgst').html(ytdrctaxpayablesgst);$('#ytdrctaxpayablecess').html(ytdrctaxpayablecess);
	$('#ytdrctaxorcesspaidincashigst').html(ytdrctaxorcesspaidincashigst);$('#ytdrctaxorcesspaidincashcgst').html(ytdrctaxorcesspaidincashcgst);$('#ytdrctaxorcesspaidincashsgst').html(ytdrctaxorcesspaidincashsgst);$('#ytdrctaxorcesspaidincashcess').html(ytdrctaxorcesspaidincashcess);
	
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formatss'
	});
}
   var table = $('table.display').DataTable({
  	"dom": '<"toolbar">frtip',
  	"buttons": [
  		{
	  		extend: 'excelHtml5',
	  		title: "test",
	  		filename: "testFile"
  		}
  		],
  	"pageLength": 8,
	 "responsive":false,
	 "ordering": false,
	 "searching": true,
	 "paging":true,
	 "bInfo" : true,
     "language": {
  	    "search": "_INPUT_",
        "searchPlaceholder": "Search...",
        "paginate": {
           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
			"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
       }
     }
   }); 
$(".reportTable2  div.toolbar").html('<h4>Monthly Summary of BVM</h4>');  
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
  OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formatss'
});		
	function formatDate(date) {
		if(date == null || typeof(date) === 'string' || date instanceof String) {
			return date;
		} else {
			var d = new Date(date),
				month = '' + (d.getMonth() + 1),
				day = '' + d.getDate(),
				year = d.getFullYear();

			if (month.length < 2) month = '0' + month;
			if (day.length < 2) day = '0' + day;
			return [day, month, year].join('-');
		}
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
				}else if(response == "expired"){errorNotification('Your subscription has expired. Kindly <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
				}else{errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
				}
			},error : function(response) {}
		});
	}
	function excelreportgstr3b(mnth,year){
		otpandsubscriptionverification="";
		subscriptionandotpcheck();
		var months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
		if(otpandsubscriptionverification == 'OTP_VERIFIED'){
			$('.gstr3b_info').append('<div class="spin-loader" style="z-index:99;width:100%;position: absolute;top: 3%;text-align: center;"><img class="loader" src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" style="width: 13%;"></div>');
			$('.gstr3b_info ul').css('opacity','0.2');	
			$.ajax({
				//url: "${contextPath}/dwnldgstr3bsummary/${id}/${client.id}/"+mnth+"/"+year,
				url: "${contextPath}/addsupinvoice_reports/${id}/${fullname}/${usertype}/${client.id}/"+mnth+"/"+year,
				contentType: 'application/json',
				success : function(data) {
					//$('#downloaddata_process').text('');$('.yearly1').css('opacity','1');
					if(data == "DATA_FOUND"){
						var d = new Date();
						var currentdate = d.getDate()+"-"+ months[d.getMonth()]+"-"+ d.getFullYear();
						if(mnth < 10){
							$('#dwnld0'+mnth).text('Downloaded');
				        	$('#dwnld0'+mnth).css('vertical-align','super');
				        	$('#dwnld0'+mnth).css('color','green');
							$('#mnth0'+mnth).html(formatInvoiceDate(d));
						}else{
							$('#dwnld'+mnth).text('Downloaded');
				        	$('#dwnld'+mnth).css('vertical-align','super');
				        	$('#dwnld'+mnth).css('color','green');
							$('#mnth'+mnth).html(formatInvoiceDate(d));
						}
						successNotification('GSTR3B Summary Downloaded from GSTN Successfully for '+(mnths[mnth-1]+"-"+year));
					}else if(data.indexOf('Primary') > 0){
						errorNotification(data);
					}else if(data.indexOf('Your Admin') > 0){
						errorNotification(data);
					}else if(data == "subscription_expired"){
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further!');
					}else{
						var d = new Date();
						var currentdate = d.getDate()+"-"+ months[d.getMonth()]+"-"+ d.getFullYear();
						if(mnth < 10){
							$('#dwnld0'+mnth).text('Downloaded');
				        	$('#dwnld0'+mnth).css('vertical-align','super');
				        	$('#dwnld0'+mnth).css('color','green');
							$('#mnth0'+mnth).html(formatInvoiceDate(d));
						}else{
							$('#dwnld'+mnth).text('Downloaded');
				        	$('#dwnld'+mnth).css('vertical-align','super');
				        	$('#dwnld'+mnth).css('color','green');
							$('#mnth'+mnth).html(formatInvoiceDate(d));
						}
						successNotification('No GSTR3B data found from GSTN for '+(mnths[mnth-1]+"-"+year));
					}
					$('.gstr3b_info div.spin-loader').remove();
					$('.gstr3b_info ul').css('opacity','1');
			    },error: function(error) {
					$('.gstr3b_info div.spin-loader').remove();
					$('.gstr3b_info ul').css('opacity','1');
				}
			});	
		}
	}
</script>
</body>
</html>