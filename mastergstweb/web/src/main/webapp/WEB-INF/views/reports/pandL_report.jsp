<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | P&L Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/jquery-simple-tree-tables.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
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
.db-ca-wrap table.pandlTable tbody tr td{border-left: 1px solid lightgray!important;}
table.dataTable{border-left: 1px solid lightgray!important;border-right: 1px solid lightgray!important;}
.yearly-sp .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.yearly-sp .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.yearly-sp .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
#fillingoption_pandl::after,#fillingoption1_pandl::after{display:none;}
.datepicker-dropdown{top:200px!important;}
#processing{color: red;position: absolute;width: 100%;top:15%;z-index: 2;font-weight: bold;font-size: 20px;}
.padding_left{padding-left:3.5rem!important}
</style>
<script>
function getval(sel) {
		document.getElementById('filing_option_pandl').innerHTML = sel;
		if (sel == 'Custom') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type-pandl').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-pandl').css("left", "19%");
			$('#pandldwnldxls').css("margin-left","18%");
		} else {
			$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-pandl').css("left", "19%");
			$('#pandldwnldxls').css("margin-left","34%");
		}
	};
	function updateYearlyOption(value){
		document.getElementById('yearlyoption_pandl').innerHTML=value;
		document.getElementById('yearlyoption1_pandl').innerHTML=value;
		document.getElementById('yearlyoption2_pandl').innerHTML=value;
	}	
	function exportF(elem) {
		  $('.tree-opened .treechildhead,.tree-opened .childhead,.tree-empty .treechildhead,.tree-empty .childhead').css("font-size","13px");
		  $('.tree-opened .treechildhead,.tree-opened .childhead,.tree-empty .treechildhead,.tree-empty .childhead').css("font-style","italic");
		  $('.tree-opened .treechildhead,.tree-opened .childhead').css("font-weight","600");
		  $('.treemainhead').css("font-weight","600");
		  $('.treemainhead,.childhead,.pl').css('border', '#000 solid 1px');
		  var reporttype = $('#fillingoption_pandl span').html();
			var returnPeriod='';
			if(reporttype == 'Monthly'){
				var month=$('#monthly').val().split("-");
				returnPeriod = month[0]+''+month[1];
			}else if (reporttype == 'Yearly') {
				var year=$('#yearlyoption_pandl').html().split("-");
				returnPeriod = year[0]+'-'+year[1];
				
			}
		  var clientName = '${client.businessname}';
		let file = new Blob([$('.pandlexcel').html()], {type:"application/vnd.ms-excel"});
		let url = URL.createObjectURL(file);
		let a = $("<a />", {
		  href: url,
		  download: "PANDL_Report_"+clientName+"_"+returnPeriod+".xls"}).appendTo("body").get(0).click();
		$('.treemainhead,.childhead,.pl').css('border', '');
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
			<li class="breadcrumb-item active">P&L Report</li>
		</ol>
		<div class="retresp"></div></div></div></div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div id="processing" class="Process_text text-center"></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h5>P&L Report of ${client.businessname}</h5>
			<!-- <h6>Profit And Loss Statement For The Year Ended 31.03.2020</h6> -->
			<div class=""></div>
			<div class="dropdown chooseteam mr-0" style="z-index:2;">
			<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption_pandl" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
					<div class="typ-ret-pandl" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option_pandl" class="filing_option_pandl"	style="vertical-align: top;">Monthly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div>
				</span>
				
				<div class="dropdown-menu ret-type-pandl"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div><a href="#" class="btn btn-greendark pull-right" role="button" style="padding: 4px 10px;" onClick="generateData()">Generate</a>
					</div>
				</span>
				<span style="display: none" class="yearly-sp">
		
				<div class="meterialform mt-2" style="float:left;position: absolute;left: -192px;">
                      <div class="checkbox pull-right" id="" style="margin-top:-4px;">
                      		<label style="font-size: 16px;margin-top: -4px;">
                       		<input class="previousYearlyAmounts" id="previousYearlyAmounts" type="checkbox">
                        	<i class="helper" style="margin-top: 4px;"></i>Include Previous Year</label>
                      </div>
                    </div>
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1_pandl"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption_pandl" class="yearlyoption_pandl">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1_pandl" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase" onClick="generateData()">Generate</a>
				</span>
				
			</div>
			<a href="#" id="pandldwnldxls" class="btn btn-blue mb-3" onclick="exportF(this)" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;margin-left: 34%;">Download To Excel<i class="fa fa-download" aria-hidden="true"></i></a>
			<div class="pandlexcel">
			<div class="customtable db-ca-view">
				
				<table id="collapsed" class="row-border dataTable meterialform pandlTab pandlTable" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th class="text-center pl">Main Heading/Group</th>
							<th class="text-center pl" id="currentYearAmounts" style="width:20%">Amounts</th>
							<!-- <th class="text-center prev-td" id="previousYearAmounts" style="width:20%">Amounts</th> -->	
						</tr>
					</thead>
					<tbody id="pandlTableTbody">
					</tbody>
					<tfoot>
						<tr>
							<th style="color:black;" class="pl">Totals</th>
							<th class="text-right ind_formats pl" id="allTotals" style="color:black;padding-right: 8px;">0.00</th>
							<!-- <th class="text-right ind_formats prev-td" id="allPrevTotals" style="color:black;padding-right: 8px;">0.00</th> -->
						</tr>
					</tfoot>
				</table>
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
	month = ev.date.getMonth()+1;
	year = ev.date.getFullYear();
});
$('.dpCustom').datepicker({
	format : "dd-mm-yyyy",
	viewMode : "days",
	minViewMode : "days"
}).on('changeDate', function(ev) {
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
	day = ev.date.getDate();
	mnt = ev.date.getMonth()+1;
	yr = ev.date.getFullYear();
	$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
});
$('.dpMonths').datepicker('update', dateValue);
$('#collapsed').simpleTreeTable({
	margin: '20',	
	});
</script>
<script type="text/javascript">
$(document).ready(function(){
	var month=new Date().getMonth()+1
	var year=new Date().getFullYear();
	$('#currentYearAmounts').html('Amounts ('+month+'-'+year+')');
	var pUrl= "${contextPath}/getmonthlypandl/${client.id}/"+month+"/"+year;
	//var pUrl= "${contextPath}/getyearlypandl/${client.id}/"+year;
	var selecttype='month';
	ajaxFuntion(pUrl,selecttype);
});
function generateData(){
	var reporttype = $('#fillingoption_pandl span').html();
	var pUrl='';
	if(reporttype == 'Monthly'){
		var month=$('#monthly').val().split("-");
		$('#currentYearAmounts').html('Amounts ('+month[0]+'-'+month[1]+')');
		pUrl="${contextPath}/getmonthlypandl/${client.id}/"+month[0]+"/"+month[1];
		var selecttype='monthly';
		ajaxFuntion(pUrl,selecttype);
	}else if (reporttype == 'Yearly') {
		var year=$('#yearlyoption_pandl').html().split("-");
		pUrl="${contextPath}/getyearlypandl/${client.id}/"+year[0];
		$('#currentYearAmounts').html('Amounts ('+year[0]+'-'+year[1]+')');
		$('#previousYearAmounts').html('Amounts ('+(year[0]-1)+'-'+year[0]+')');
		var selecttype='yearly';
		ajaxFuntion(pUrl,selecttype);
	}
}

function ajaxFuntion(pUrl,selecttype){
	$.ajax({
		url: pUrl,
		async: true,
		cache: false,
		dataType:"json",
		beforeSend: function () {$('#processing').text('Processing...')},
		contentType: 'application/json',
		success : function(data) {
			responsedata(data,selecttype);
		},error:function(err){
			$('.pandlTable #pandlTableTbody tr').detach();
			$('.pandlTable #pandlTableTbody').append('<tr><td colspan="2" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');
			$('#processing').text('');
		}
	});
}
function responsedata(response,selecttype){
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			var year=$('#yearlyoption_pandl').html().split("-");
			$("#collapsed>thead>tr").append('<th class="text-center prev-td pl" id="previousYearAmounts" style="width: 20%; display: none;">Amounts</th>');
			$('#previousYearAmounts').html('Amounts ('+(year[0]-1)+'-'+year[0]+')');
	  		$("#collapsed>tfoot>tr").append('<th class="text-right ind_formats prev-td pl" id="allPrevTotals" style="color: black; padding-right: 8px; display: none;">0.00</th>');
		}else{
			$('#collapsed').find('[id*="previousYearAmounts"]').remove();
			$('#collapsed').find('[id*="allPrevTotals"]').remove();	
		}
	}else{
		$('#collapsed').find('[id*="previousYearAmounts"]').remove();
		$('#collapsed').find('[id*="allPrevTotals"]').remove();
	}
	var i=1; var padding_left='padding_left',padding_left5='pl-5';
	var content='';
	var total=0, totRevenue=0, totDirectExpenses=0, totIndirectExpenses=0, directIncomeTotal=0, indirectIncomeTotal=0;
	var prevTotal=0, prevTotRevenue=0, prevTotDirectExpenses=0, prevTotIndirectExpenses=0;
	var prevdirectIncomeTotal=0,previndirectIncomeTotal=0;
	$('.pandlTable #pandlTableTbody tr').detach();
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="pl"><strong>I. Direct Income</strong></td>';
	content+='<td class="pl" align="right"><strong><span id="directIncomeTotal" class="ind_formats"></span></strong></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td align="right" class="prev-td"><strong><span id="prevdirectIncomeTotal" class="ind_formats"></span></strong></td></tr>';
		}else{
			content+='</tr>';
		}
	}else{
		content+='</tr>';
	}
	$.each(response, function(key, group){
		var path=group.path.split("/");
		if("INCOME" == path[0].toUpperCase() && "DIRECT INCOME" == path[1].toUpperCase()){
			if(group.amount.closingamt != 0 || group.amount.previousyearclosingamt != 0){
				var j=1,k=1;
				var qwerty = j*50;
				content+='<tr data-node-id="'+i+'">';
				content+='<td class="treemainhead" style="padding-left:'+qwerty+'px">'+group.groupname+'</td>';
				if(group.amount.closingamt<=0){
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.closingamt)+'</span></td>';
				}else{
					content+='<td align="right" class="treemainhead">-<span class="ind_formats">'+group.amount.closingamt+'</span></td>';
				}
				if("yearly" == selecttype){
					if ($('#previousYearlyAmounts').is(':checked')) {
						if(group.amount.previousyearclosingamt<=0){
							content+='<td class="prev-td treemainhead" align="right"><span class="ind_formats">'+Math.abs(group.amount.previousyearclosingamt)+'</span></td></tr>';				
						}else{
							content+='<td class="prev-td treemainhead" align="right">-<span class="ind_formats">'+group.amount.previousyearclosingamt+'</span></td></tr>';				
						}
					}else{
						content+='</tr>';
					}
				}else{
					content+='</tr>';
				}
				if(group.amount.closingamt > 0 || group.amount.previousyearclosingamt > 0){
					total+=-group.amount.closingamt;
					totRevenue+=-group.amount.closingamt;
					prevTotal+=-group.amount.previousyearclosingamt;
					prevTotRevenue+=-group.amount.previousyearclosingamt;
					directIncomeTotal+=-group.amount.closingamt;
					prevdirectIncomeTotal+=-group.amount.previousyearclosingamt;
				}else{
					total+=Math.abs(group.amount.closingamt);
					totRevenue+=Math.abs(group.amount.closingamt);
					prevTotal+=Math.abs(group.amount.previousyearclosingamt);
					prevTotRevenue+=Math.abs(group.amount.previousyearclosingamt);
					directIncomeTotal+=Math.abs(group.amount.closingamt);
					prevdirectIncomeTotal+=Math.abs(group.amount.previousyearclosingamt);
				}
				
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt != 0 || subgroup.amount.previousyearclosingamt != 0){
						var datanodeid=i+'.'+j;
						//var abc = (k+1)*30;
						var abc = 60;
						var datanodepid=i;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td class="childhead" style="padding-left:'+abc+'px">'+subgroup.groupname+'</td>';
						if(subgroup.amount.closingamt<=0){
							content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span></td>';
						}else{
							content+='<td align="right" class="childhead">-<span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
						}
						if("yearly" == selecttype){
							if ($('#previousYearlyAmounts').is(':checked')) {
								if(subgroup.amount.previousyearclosingamt<=0){
									content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+Math.abs(subgroup.amount.previousyearclosingamt)+'</span></td></tr>';
								}else{
									content+='<td class="prev-td childhead" align="right">-<span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td></tr>';
								}
							}else{
								content+='</tr>';		
							}
						}else{
							content+='</tr>';
						}
						if(subgroup.children){
							content+=incomechildnodes(datanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
						}
						j++;
					}
				});
				k++;
				i++;
			}
		}
	});
	$('.pandlTable #pandlTableTbody').append(content);
	content='';
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="pl"><strong>II. Indirect Income</strong></td>';
	content+='<td class="pl" align="right"><strong><span id="indirectIncomeTotal" class="ind_formats"></span></strong></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td align="right" class="prev-td pl"><strong><span id="previndirectIncomeTotal" class="ind_formats"></span></strong></td></tr>';
		}else{
			content+='</tr>';
		}
	}else{
		content+='</tr>';
	}
	$.each(response, function(key, group){
		var path=group.path.split("/");
		if("INCOME" == path[0].toUpperCase() && "DIRECT INCOME" != path[1].toUpperCase()){
			if(group.amount.closingamt != 0 || group.amount.previousyearclosingamt != 0){
				var j=1,k=1;
				var qwerty = j*50;
				content+='<tr data-node-id="'+i+'">';
				content+='<td class="treemainhead" style="padding-left:'+qwerty+'px">'+group.groupname+'</td>';
				if(group.amount.closingamt<=0){
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.closingamt)+'</span></td>';				
				}else{
					content+='<td align="right" class="treemainhead">-<span class="ind_formats">'+group.amount.closingamt+'</span></td>';	
				}
				if("yearly" == selecttype){
					if ($('#previousYearlyAmounts').is(':checked')) {
						if(group.amount.previousyearclosingamt<=0){
							content+='<td class="prev-td treemainhead" align="right"><span class="ind_formats">'+Math.abs(group.amount.previousyearclosingamt)+'</span></td></tr>';				
						}else{
							content+='<td class="prev-td treemainhead" align="right">-<span class="ind_formats">'+group.amount.previousyearclosingamt+'</span></td></tr>';
						}
					}else{
						content+='</tr>';
					}
				}else{
					content+='</tr>';
				}
				if(group.amount.closingamt > 0 || group.amount.previousyearclosingamt > 0){
					total+=-group.amount.closingamt;
					totRevenue+=-group.amount.closingamt;
					prevTotal+=-group.amount.previousyearclosingamt;
					prevTotRevenue+=-group.amount.previousyearclosingamt;
					indirectIncomeTotal+=-group.amount.closingamt;
					previndirectIncomeTotal+=-group.amount.previousyearclosingamt;
				}else{
					total+=Math.abs(group.amount.closingamt);
					totRevenue+=Math.abs(group.amount.closingamt);
					prevTotal+=Math.abs(group.amount.previousyearclosingamt);
					prevTotRevenue+=Math.abs(group.amount.previousyearclosingamt);
					indirectIncomeTotal+=Math.abs(group.amount.closingamt);
					previndirectIncomeTotal+=Math.abs(group.amount.previousyearclosingamt);
				}
				
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt != 0 || subgroup.amount.previousyearclosingamt != 0){					
						var datanodeid=i+'.'+j;
						//var abc = (k+1)*30;
						var abc = 60;
						var datanodepid=i;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td class="childhead"  style="padding-left:'+abc+'px">'+subgroup.groupname+'</td>';
						
						if(subgroup.amount.closingamt<=0){
							content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span></td>';
						}else{
							content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
						}
						if("yearly" == selecttype){
							if ($('#previousYearlyAmounts').is(':checked')) {
								if(subgroup.amount.previousyearclosingamt<=0){
									content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+Math.abs(subgroup.amount.previousyearclosingamt)+'</span></td></tr>';					
								}else{
									content+='<td class="prev-td childhead" align="right">-<span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td></tr>';
								}
							}else{
								content+='</tr>';
							}
						}else{
							content+='</tr>';
						}
						if(subgroup.children){
							content+=incomechildnodes(datanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
						}
						j++;
					}
				});
				k++;
				i++;
			}
		}
	});
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="treemainhead"><strong>III. Total Revenue (I+II)</strong></td>';
	content+='<td align="right" class="treemainhead"><strong><span class="ind_formats" id="totRevenue"></span></strong></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td class="prev-td treemainhead" align="right"><strong><span class="ind_formats" id="prevTotRevenue"></span></strong></td></tr>';
		}else{
			content+='</tr>';
		}
	}else{
		content+='</tr>';
	}
	$('.pandlTable #pandlTableTbody').append(content);
	content='';
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="pl"><strong>IV. Expenses</strong></td>';
	content+='<td class="pl"></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td class="prev-td pl"></td></tr>';
		}else{
			content+= '</tr>';	
		}
	}else{
		content+= '</tr>';
	}
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="pl" style="padding-left:25px"><strong>Direct Expenses</strong></td>';
	content+='<td class="pl" align="right"><strong><span id="totDirectExpenses" class="ind_formats"></span></strong></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td align="right" class="prev-td pl"><strong><span id="prevTotDirectExpenses" class="ind_formats"></span></strong></td></tr>';
		}else{
			content+='</tr>';
		}
	}else{
		content+='</tr>';
	}
	padding_left='pl-8';
	$.each(response, function(key, group){
		
		var path=group.path.split("/");
		if("EXPENSES" == path[0].toUpperCase() && "DIRECT EXPENSES" == path[1].toUpperCase()){
			if(group.amount.closingamt != 0 || group.amount.previousyearclosingamt != 0){			
				var j=1,k=1;
				var qwerty = j*50;
				content+='<tr data-node-id="'+i+'">';
				content+='<td style="padding-left:'+qwerty+'px" class="treemainhead">'+group.groupname+'</td>';
				
				content+='<td align="right" class="treemainhead"><span class="ind_formats">'+group.amount.closingamt+'</span></td>';
				if("yearly" == selecttype){
					if ($('#previousYearlyAmounts').is(':checked')) {
						content+='<td class="prev-td treemainhead" align="right"><span class="ind_formats">'+group.amount.previousyearclosingamt+'</span></td></tr>';
					}else{
						content+='</tr>';
					}
				}else{
					content+='</tr>';
				}
				total+=group.amount.closingamt;
				totDirectExpenses+=group.amount.closingamt;
				prevTotal+=group.amount.previousyearclosingamt;
				prevTotDirectExpenses+=group.amount.previousyearclosingamt;
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt !=0){
						var datanodeid=i+'.'+j;
						var datanodepid=i;
						//var abc = (k+1)*30;
						var abc = 60;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td style="padding-left:'+abc+'px" class="childhead">'+subgroup.groupname+'</td>';
						content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
						if("yearly" == selecttype){
							if ($('#previousYearlyAmounts').is(':checked')) {
								content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td></tr>';
							}else{
								content+='</tr>';
							}
						}else{
							content+='</tr>';
						}
						if(subgroup.children){
							content+=expensesChildnodes(datanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
						}
						j++;						
					}
				});
				k++;
				i++;
			}
		}
	});
	
	$('.pandlTable #pandlTableTbody').append(content);
	content='';
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="pl" style="padding-left:25px"><strong>Indirect Expenses</strong></td>';
	content+='<td class="pl" align="right"><strong><span id="totIndirectExpenses" class="ind_formats"></span></strong></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td align="right" class="prev-td pl"><strong><span id="prevTotIndirectExpenses" class="ind_formats"></span></strong></td></tr>';
		}else{
			content+='</tr>';
		}
	}else{
		content+='</tr>';
	}
	$.each(response, function(key, group){
		
		var path=group.path.split("/");
		if("EXPENSES" == path[0].toUpperCase() && "DIRECT EXPENSES" != path[1].toUpperCase()){
			if(group.amount.closingamt != 0 || group.amount.previousyearclosingamt != 0){
				var j=1,k=1;
				var qwerty = j*50;
				content+='<tr data-node-id="'+i+'">';
				content+='<td  style="padding-left:'+qwerty+'px" class="treemainhead">'+group.groupname+'</td>';
				content+='<td align="right" class="treemainhead"><span class="ind_formats">'+group.amount.closingamt+'</span></td>';
				if("yearly" == selecttype){
					if ($('#previousYearlyAmounts').is(':checked')) {
						content+='<td class="prev-td treemainhead" align="right"><span class="ind_formats">'+group.amount.previousyearclosingamt+'</span></td></tr>';
					}else{
						content+='</tr>';
					}
				}else{
					content+='</tr>';
				}
				total+=group.amount.closingamt;
				totIndirectExpenses+=group.amount.closingamt;
				prevTotal+=group.amount.previousyearclosingamt;
				prevTotIndirectExpenses+=group.amount.previousyearclosingamt;
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt !=0){
						var datanodeid=i+'.'+j;
						var datanodepid=i;
						//var abc = (k+1)*30;
						var abc = 60;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td  style="padding-left:'+abc+'px" class="childhead">'+subgroup.groupname+'</td>';
						content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
						if("yearly" == selecttype){
							if ($('#previousYearlyAmounts').is(':checked')) {
								content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td></tr>';
							}else{
								content+='</tr>';
							}
						}else{
							content+='</tr>';
						}
						if(subgroup.children){
							content+=expensesChildnodes(datanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
						}
						j++;
					}
				});
				k++;
				i++;
			}
		}
	});
	content+='<tr data-node-id="'+0+'">';
	content+='<td class="pl"><strong>Profit for the year (III-IV)</strong></td>';
	content+='<td class="pl" align="right"><strong><span class="ind_formats" id="totProfit">0</span></strong></td>';
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			content+='<td class="prev-td pl" align="right"><strong><span class="ind_formats" id="prevTotProfit">0</span></strong></td></tr>';
		}else{
			content+='</tr>';
		}
	}else{
		content+='</tr>';
	}
	$('.pandlTable #pandlTableTbody').append(content);
	$('#collapsed').simpleTreeTable({margin: '20'});
	$('#processing').text('');
	$("#totRevenue").html(totRevenue);
	$("#totDirectExpenses").html(totDirectExpenses);
	$("#totIndirectExpenses").html(totIndirectExpenses);
	
	$("#allTotals").html(totRevenue-(totDirectExpenses+totIndirectExpenses));
	$("#totProfit").html(totRevenue-(totDirectExpenses+totIndirectExpenses));
	
	$("#prevTotRevenue").html(prevTotRevenue);
	$("#prevTotDirectExpenses").html(prevTotDirectExpenses);
	$("#prevTotIndirectExpenses").html(prevTotIndirectExpenses);
	$("#prevTotProfit").html(prevTotRevenue-(prevTotDirectExpenses+prevTotIndirectExpenses));
	
	$("#directIncomeTotal").html(directIncomeTotal);
	$("#prevdirectIncomeTotal").html(prevdirectIncomeTotal);
	$("#indirectIncomeTotal").html(indirectIncomeTotal);
	$("#previndirectIncomeTotal").html(previndirectIncomeTotal);
	$("#allPrevTotals").html(prevTotRevenue-(prevTotDirectExpenses+prevTotIndirectExpenses));
	$('#collapsed tr:gt(0)').each(function() {
		 if (this.innerText === '') {
			 $(this).remove();
		 }
	});
	if("yearly" == selecttype){
		if ($('#previousYearlyAmounts').is(':checked')) {
			$('.prev-td').css('display','table-cell');
		}else{
			$('.prev-td').css('display','none');
		}
	}else{
		$('.prev-td').css('display','none');
	}
	OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
}
function childnodes(datanodeid, datanodepid, content, subgrpchilds,padding_left,selecttype){
	var i=1,k=1;
	var childdatanodeid=datanodeid+'.'+i;
	var content="";
	$.each(subgrpchilds, function(key, subgroup){
		if(subgroup.amount.closingamt != 0 || subgroup.amount.previousyearclosingamt !=0){			
			
			content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
			content+='<td class="childhead" style="padding-left:100px">'+subgroup.groupname+'</td>';
			
			if(subgroup.amount.closingamt<=0){
				content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span></td>';
			}else{
				content+='<td align="right" class="childhead">-<span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
			}
			if("yearly" == selecttype){
				if ($('#previousYearlyAmounts').is(':checked')) {
					if(subgroup.amount.previousyearclosingamt<=0){
						content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+Math.abs(subgroup.amount.previousyearclosingamt)+'</span></td><tr/>';			
					}else{
						content+='<td class="prev-td childhead" align="right">-<span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td><tr/>';
					}
				}else{
					content+='</tr>';
				}
			}else{
				content+='</tr>';
			}
			if(subgroup.children){
				content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
			}
			i++;
			childdatanodeid=datanodeid+'.'+i;	
		}
	});
	return content;
}
function incomechildnodes(datanodeid, datanodepid, content, subgrpchilds,padding_left,selecttype){
	var i=1,k=1;
	var childdatanodeid=datanodeid+'.'+i;
	var abc = 80;
	var content="";
	$.each(subgrpchilds, function(key, subgroup){
		if(subgroup.amount.closingamt != 0 || subgroup.amount.previousyearclosingamt !=0){			
			
			content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
			content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
			
			if(subgroup.amount.closingamt<=0){
				content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span></td>';
			}else{
				content+='<td align="right" class="childhead">-<span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
			}
			if("yearly" == selecttype){
				if ($('#previousYearlyAmounts').is(':checked')) {
					if(subgroup.amount.previousyearclosingamt<=0){
						content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+Math.abs(subgroup.amount.previousyearclosingamt)+'</span></td><tr/>';			
					}else{
						content+='<td class="prev-td childhead" align="right">-<span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td><tr/>';
					}
				}else{
					content+='</tr>';
				}
			}else{
				content+='</tr>';
			}
			if(subgroup.children){
				content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
			}
			i++;
			childdatanodeid=datanodeid+'.'+i;	
		}
	});
	return content;
}
function expensesChildnodes(datanodeid, datanodepid, content, subgrpchilds,padding_left,selecttype){
	var i=1,k=1;
	//var abc = (k+1)*40;
	var abc = 80;
	var childdatanodeid=datanodeid+'.'+i;
	var content="";
	$.each(subgrpchilds, function(key, subgroup){
		if(subgroup.amount.closingamt != 0 || subgroup.amount.previousyearclosingamt != 0){		
			content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
			content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
			content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
			if("yearly" == selecttype){
				if ($('#previousYearlyAmounts').is(':checked')) {
					content+='<td class="prev-td childhead" align="right"><span class="ind_formats">'+subgroup.amount.previousyearclosingamt+'</span></td><tr/>';
				}else{
					content+='</tr>';
				}
			}else{
				content+='</tr>';
			}
			if(subgroup.children){
				content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,padding_left,selecttype);
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
