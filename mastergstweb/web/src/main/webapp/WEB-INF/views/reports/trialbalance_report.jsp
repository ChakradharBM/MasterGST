<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | Trail Balance Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/jquery-simple-tree-tables.css" media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery-simple-tree-table.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>

</head>
<style>
#fillingoption_trail::after,#fillingoption1_trail::after{display:none;}
.datepicker-dropdown{top:183px!important;}
.Process_text{position: absolute;top: 80%;width: 100%;font-weight: 700;z-index: 2;color: red;}
</style>
<script>
function getval(sel) {
		document.getElementById('filing_option_trail').innerHTML = sel;/* document.getElementById('filing_option1_trail').innerHTML = sel; document.getElementById('filing_option2_trail').innerHTML = sel; */
		$('#processing').css('top','10%');
		if (sel == 'Custom') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type-trail').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
		} else {
			$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
		}
	};
	function updateYearlyOption(value){
		document.getElementById('yearlyoption_trail').innerHTML=value;
		document.getElementById('yearlyoption1_trail').innerHTML=value;
		document.getElementById('yearlyoption2_trail').innerHTML=value;
	}
	
	/*function exportF(elem) {
		  var reporttype = $('#fillingoption_trail span').html();
			var returnPeriod='';
			if(reporttype == 'Monthly'){
				var month=$('#monthly').val().split("-");
				returnPeriod = month[0]+''+month[1];
			}else if (reporttype == 'Yearly') {
				var year=$('#yearlyoption_trail').html().split("-");
				returnPeriod = year[0]+'-'+year[1];
				
			}
		  var clientName = '${client.businessname}';
		  var table = document.getElementById("collapsed");
		  var html = table.outerHTML;
		  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
		  elem.setAttribute("href", url);
		  elem.setAttribute("download", "TrailBalance_Report_"+clientName+"_"+returnPeriod+".xls"); // Choose the file name
		  return false;
	}*/
	
	function exportF(elem) {
		  $('.tree-opened .treechildhead,.tree-opened .childhead,.tree-empty .treechildhead,.tree-empty .childhead').css("font-size","13px");
		  $('.tree-opened .treechildhead,.tree-opened .childhead,.tree-empty .treechildhead,.tree-empty .childhead').css("font-style","italic");
		  $('.tree-opened .treechildhead,.tree-opened .childhead').css("font-weight","600");
		  $('.treemainhead').css("font-weight","600");
		  $('.treemainhead,.childhead,.tb').css('border', '#000 solid 1px');
		  var reporttype = $('#fillingoption_trail span').html();
			var returnPeriod='';
			if(reporttype == 'Monthly'){
				var month=$('#monthly').val().split("-");
				returnPeriod = month[0]+''+month[1];
			}else if (reporttype == 'Yearly') {
				var year=$('#yearlyoption_trail').html().split("-");
				returnPeriod = year[0]+'-'+year[1];
			}else{
				var fromtime=$('.fromtime').val();
				var totime=$('.totime').val();
				returnPeriod = fromtime+'-'+totime; 
			}
		  var clientName = '${client.businessname}';
		let file = new Blob([$('.trailbalance').html()], {type:"data.application/vnd.ms-excel"});
		let url = URL.createObjectURL(file);
		let a = $("<a />", {
		  href: url,
		  download: "TrailBalance_Report_"+clientName+"_"+returnPeriod+".xls",
		  sheet:  "Sheet1"
	            
		  }).appendTo("body").get(0).click();
		$('.treemainhead,.childhead,.tb').css('border', '');
		  e.preventDefault();
	}
	
</script>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
<div class="breadcrumbwrap" >
	<div class="container bread">
	<div class="row">
        <div class="col-md-12 col-sm-12">
		<ol class="breadcrumb">
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
			<li class="breadcrumb-item active">Trail Balance Report</li>
		</ol>
		<div class="retresp"></div></div></div></div>
	</div>
	
	<div class="db-ca-wrap monthely1">
		<div class="container">
			<div class=" "></div>
			<div id="processing" class="Process_text text-center"></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Trail Balance Report of ${client.businessname}</h4><p></p>
						<div class=" "></div>
			<div class="dropdown chooseteam mr-0" style="z-index:2;"><span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption_trail" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
					<div class="typ-ret-trail" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option_trail" class="filing_option_trail"	style="vertical-align: top;">Monthly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div>
				</Span>
				<div class="dropdown-menu ret-type-trail"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button></div>
				</span> 
				<span style="display: none" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1_trail"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption_trail" class="yearlyoption_trail">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1_trail" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div>
					<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
				</span>
				<span class="datetimetxt custom-sp" style="display: none" id="custom-sp">
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
			<a href="#" id="" class="btn btn-blue mb-3" onclick="exportF(this)" style="position:relative;z-index:2;padding: 6px 15px 5px;font-weight: bold;color: #435a93;float: right;margin-left: 10px;">Download To Excel<i class="fa fa-download" aria-hidden="true"></i></a>
			<div class="trailbalance">
			<div class="customtable db-ca-view">
					<table id="collapsed" class="row-border dataTable meterialform trailTab trailbalanceTable" cellspacing="0" width="100%">
						<thead>
							<tr><th class="text-center tb">Particulars - Main Head</th><th class="text-center tb">Opening</th><th class="text-center tb">Debit</th><th class="text-center tb">Credit</th><th class="text-center tb">Closing Dr/Cr</th></tr>
						</thead>				
						<tbody id="trailbalanceTableTbody">
						</tbody>
						<tfoot class="trailTab_tfoot">
							<tr> <th class="tb" style="color:black">Totals</th><th class="text-right ind_formats tb" id="totopeningamt" style="color:black">0.00</th><th class="text-right ind_formats tb" id="totdebitamt" style="color:black">0.00</th><th class="text-right ind_formats tb" id="totcreditamt" style="color:black">0.00</th><th class="text-right ind_formats tb" id="totclosingamt" style="color:black">0.00</th></tr>
						</tfoot>
					</table>
					<div id="noteinfo"></div>
				</div>
				</div>
			</div>
			</div>
<script>
var day =new Date().getDate();
var mnt =new Date().getMonth()+1;
var yr =new Date().getFullYear();
var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
$('.fromtime').val(customValue);
$('.totime').val(customValue);
var date = $('.dpMonths').datepicker({
	autoclose: true,
	viewMode: 1,
	minViewMode: 1,
	format: 'mm-yyyy'
}).on('changeDate', function(ev) {
	//updateReturnPeriod(ev.date);
	month = ev.date.getMonth()+1;
	year = ev.date.getFullYear();
});
$('.dpCustom').datepicker({
	format : "dd-mm-yyyy",
	viewMode : "days",
	minViewMode : "days"
}).on('changeDate', function(ev) {
	//updateReturnPeriod(ev.date);
	day = ev.date.getDate();
	mnt = ev.date.getMonth()+1;
	yr = ev.date.getFullYear();
	$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
});
$('.dpCustom1').datepicker({
	format : "dd-mm-yyyy",
	viewMode : "days",
	minViewMode : "days"
}).on('changeDate', function(ev) {
	//updateReturnPeriod(ev.date);
	day = ev.date.getDate();
	mnt = ev.date.getMonth()+1;
	yr = ev.date.getFullYear();
	$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
});
$('.dpMonths').datepicker('update', dateValue);

$(document).ready(function(){
	var month=new Date().getMonth()+1
	var year=new Date().getFullYear();
	$.ajax({
		url: "${contextPath}/getmonthlytrailbalance/${client.id}/"+month+"/"+year,
		async: true,
		cache: false,
		dataType:"json",
		beforeSend: function () {$('#processing').text('Processing...');},
		contentType: 'application/json',
		success : function(data) {
			responsedata(data);
		},error:function(err){$('.trailbalanceTable #trailbalanceTableTbody tr').detach();
		$('.trailbalanceTable #trailbalanceTableTbody').append('<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');}
	});
});

function generateData(){
	var reporttype = $('#fillingoption_trail span').html();
	$('.Process_text').css('top','5%');
	if(reporttype == 'Monthly'){
		var month=$('#monthly').val().split("-");
		$.ajax({
			url: "${contextPath}/getmonthlytrailbalance/${client.id}/"+month[0]+"/"+month[1],
			async: true,
			cache: false,
			dataType:"json",
			beforeSend: function () {$('#processing').text('Processing...');},
			contentType: 'application/json',
			success : function(data) {
				responsedata(data);
			},error:function(err){$('.trailbalanceTable #trailbalanceTableTbody tr').detach();
			$('.trailbalanceTable #trailbalanceTableTbody').append('<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');}
		});
	}else if (reporttype == 'Yearly') {
		var year=$('#yearlyoption_trail').html().split("-");
		$.ajax({
			url: "${contextPath}/getyearlytrailbalance/${client.id}/"+year[0],
			async: true,
			cache: false,
			dataType:"json",
			beforeSend: function () {$('#processing').text('Processing...');},
			contentType: 'application/json',
			success : function(data) {
				responsedata(data);
			},error:function(err){$('.trailbalanceTable #trailbalanceTableTbody tr').detach();
			$('.trailbalanceTable #trailbalanceTableTbody').append('<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');}
		});
	}else{
		var fromtime=$('.fromtime').val();
		var totime=$('.totime').val();
		$.ajax({
			url: "${contextPath}/getcustomtrailbalance/${client.id}/"+fromtime+"/"+totime,
			async: true,
			cache: false,
			dataType:"json",
			beforeSend: function () {$('#processing').text('Processing...');},
			contentType: 'application/json',
			success : function(data) {
				responsedata(data);
			},error:function(err){$('.trailbalanceTable #trailbalanceTableTbody tr').detach();
			$('.trailbalanceTable #trailbalanceTableTbody').append('<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');}
		});
	}
}
	function responsedata(response){
		var i=1;
		var content='';var padding_left='padding_left';
		var totopeningamt=0,totdebitamt=0,totcreditamt=0,totclosingamt=0;
		$('.trailbalanceTable #trailbalanceTableTbody tr').detach();
		var dataflag=false;
		$.each(response, function(key, group){
			var flag=false;
			//if(group.amount.openingamt !=0){flag=true;dataflag=true;}
			//if(group.amount.debitamt !=0){flag=true;dataflag=true;}
			//if(group.amount.creditamt !=0){flag=true;dataflag=true;}
			//if(group.amount.closingamt !=0){flag=true;dataflag=true;}
			
			if(group.amount.closingamt ==0 && group.amount.openingamt ==0 && group.amount.debitamt ==0 && group.amount.creditamt ==0){
				flag=false;
			}else{
				flag=true;dataflag=true;
			}
			if(flag){
				var j=1,k=1;
				content+='<tr data-node-id="'+i+'">';
				content+='<td class="treemainhead">'+group.groupname+'</td>';
				
				if(group.amount.openingamt<0){
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.openingamt)+'</span> Cr</td>';
				}else{
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+group.amount.openingamt+'</span> Dr</td>';
				}
				
				content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.debitamt)+'</span></td>';
				content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.creditamt)+'</span></td>';
				
				if(group.amount.closingamt<0){
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.closingamt)+'</span> Cr</td></tr>';
				}else{
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+group.amount.closingamt+'</span> Dr</td></tr>';
				}
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt ==0 && subgroup.amount.openingamt ==0 && subgroup.amount.debitamt ==0 && subgroup.amount.creditamt ==0){
						
					}else{
						var datanodeid=i+'.'+j;
						var datanodepid=i;
						var abc = 40;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td class="childhead" style="padding-left:'+abc+'px">'+subgroup.groupname+'</td>';
						if(subgroup.amount.openingamt<0){
							content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.openingamt)+'</span> Cr</td>';
						}else{
							content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.openingamt+'</span> Dr</td>';
						}
						content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.debitamt)+'</span></td>';
						content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.creditamt)+'</span></td>';
						
						if(subgroup.amount.closingamt<0){
							content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span> Cr</td></tr>';
						}else{
							content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span> Dr</td></tr>';
						}
						if(subgroup.children){
							content+=childnodes(datanodeid, datanodepid, content, subgroup.children,1);
						}
						j++;
					}
				});
				i++;
			}
			totopeningamt+=group.amount.openingamt;
			totdebitamt+=group.amount.debitamt;
			totcreditamt+=group.amount.creditamt;
			totclosingamt+=group.amount.closingamt;
		});
		if(dataflag==false){
			content+='<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">No Records Found</td></tr>';
		}else{
			if(totopeningamt != 0){
				$('#totopeningamt').css('color','red');					
			}
			if(totclosingamt != 0){
				$('#totclosingamt').css('color','red');	
			}
			if(totopeningamt == 0 && totclosingamt == 0){
				$('#totopeningamt').css('color','black');					
				$('#totclosingamt').css('color','black');	
				$('#noteinfo').html('');
			}			
			//$('#noteinfo').html('<b>Note: </b><span style="color:red;"><b>Opening balance and Closing balance doesn\'t match</b></span>');
		}
		$('.trailbalanceTable #trailbalanceTableTbody').append(content);
		$('#collapsed').simpleTreeTable({margin: '20'});
		$('#processing').text('');
		$("#totopeningamt").html(totopeningamt);
		$("#totdebitamt").html(totdebitamt);
		$("#totcreditamt").html(totcreditamt);
		$("#totclosingamt").html(totclosingamt);
		$(".indformat").each(function(){
			$(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
	}

	function childnodes(datanodeid, datanodepid, content, subgrpchilds,z){
		var i=1;
		var childdatanodeid=datanodeid+'.'+i;
		var content="";
		$.each(subgrpchilds, function(key, subgroup){
			if(subgroup.amount.closingamt ==0 && subgroup.amount.openingamt ==0 && subgroup.amount.debitamt ==0 && subgroup.amount.creditamt ==0){
				
			}else{
				var k=z;
				var abc = "";
				if(k == 1){
					abc = z*60;
				}else{
					abc = 60+z*5;
				}
				content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
				content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
				if(subgroup.amount.openingamt<0){
					content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.openingamt)+'</span> Cr</td>';
				}else{
					content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.openingamt+'</span> Dr</td>';
				}
				content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.debitamt)+'</span></td>';
				content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.creditamt)+'</span></td>';
				
				if(subgroup.amount.closingamt<0){
					content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span> Cr</td></tr>';
				}else{
					content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span> Dr</td></tr>';
				}
				k++;
				if(subgroup.children){
					content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,k);
				}
				i++;
				childdatanodeid=datanodeid+'.'+i;
				
			}
		});
		return content;	
	}
$('#collapsed').simpleTreeTable({margin: '20'});
</script>
</body>
</html>
