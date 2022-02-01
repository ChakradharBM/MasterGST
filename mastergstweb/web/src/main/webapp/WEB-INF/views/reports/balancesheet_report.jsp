<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | Balance Sheet Report</title>
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
<style>
	#fillingoption_balance::after,#fillingoption1_balance::after{display:none;}
	.datepicker-orient-left {top:180px!important;}
	#processing{color: red;font-size: large;font-weight: 600;top: 50%;position: absolute;z-index: 1;width: 100%;}
</style>
<script>
function getval(sel) {
	document.getElementById('filing_option_balance').innerHTML = sel;
	$('#processing').css('top','10%');
	
	if (sel == 'Custom') {
		$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type-balance').css("left", "16%");
	} else if (sel == 'Yearly') {
		$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-balance').css("left", "19%");
	} else {
		$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-balance').css("left", "19%");
	}
}
function updateYearlyOption(value){
	document.getElementById('yearlyoption_balance').innerHTML=value;
}
function exportF(elem) {
	  $('.tree-opened .treechildhead,.tree-opened .childhead,.tree-empty .treechildhead,.tree-empty .childhead').css("font-size","13px");
	  $('.tree-opened .treechildhead,.tree-opened .childhead,.tree-empty .treechildhead,.tree-empty .childhead').css("font-style","italic");
	  $('.tree-opened .treechildhead,.tree-opened .childhead').css("font-weight","600");
	  $('.treemainhead').css("font-weight","600");
	  $('.treemainhead,.childhead,.bs').css('border', '#000 solid 1px');
	  var reporttype = $('#filing_option_balance span').html();
		var returnPeriod='';
		if(reporttype == 'Monthly'){
			var month=$('#monthly').val().split("-");
			returnPeriod = month[0]+''+month[1];
		}else if (reporttype == 'Yearly') {
			var year=$('#yearlyoption_balance').html().split("-");
			returnPeriod = year[0]+'-'+year[1];
		}else{
			var fromtime=$('.fromtime').val();
			var totime=$('.totime').val();
			returnPeriod = fromtime+'-'+totime; 
		}
	  var clientName = '${client.businessname}';
	let file = new Blob([$('.balancesheet').html()], {type:"application/vnd.ms-excel"});
	let url = URL.createObjectURL(file);
	let a = $("<a />", {
	  href: url,
	  download: "BalanceSheet_Report_"+clientName+"_"+returnPeriod+".xls"}).appendTo("body").get(0).click();
	$('.treemainhead,.childhead,.bs').css('border', '');
	  e.preventDefault();
}
</script>
</head>
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
						<li class="breadcrumb-item active">Balance Sheet Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap monthely1">
		<div class="container">
			<div id="processing" class="Process_text text-center"></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Balance Sheet Report of ${client.businessname}</h4><p></p>
			<div class="dropdown chooseteam mr-0" style="z-index:2;"><span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption_balance" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
				<div class="typ-ret-balance" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
					<span id="filing_option_balance" class="filing_option_balance"	style="vertical-align: top;">Monthly</span>
					<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
				</div>
				</Span>
				<div class="dropdown-menu ret-type-balance"	style="width: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><!-- <a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a> -->
				</div>
				<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
							<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
					</div>
				</span> 
				<span style="display: none" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1_balance"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption_balance" class="yearlyoption_balance">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1_balance" style="WIDTH: 108px !important; min-width: 36px; left: 63%; top: 26px; border-radius: 2px">
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
			<!-- <a href="#" id="" class="btn btn-blue mb-3" onclick="exportF(this)" style="position:relative;z-index:2;padding: 6px 15px 5px;font-weight: bold;color: #435a93;float: right;margin-left: 10px;">Download To Excel<i class="fa fa-download" aria-hidden="true"></i></a> -->
			<div class="balancesheet">
			<div class="customtable db-ca-view" style="width:100%;display:inline-block;">	
				<div class="row pt-1">
					<div class="col-md-6 pr-0">
						<table id="collapsed" class="row-border liabilitiesTable dataTable meterialform balanceTab" cellspacing="0" width="50%" style="margin-top: -7px;">
							<thead>
								<tr><th class="text-center bs">Liabilities</th><th class="text-center bs">Amount(Rs)</th>
							</thead>
							<tbody id="liabilitiesTableBody">
							</tbody>
							<tfoot>
								<tr><th style="color:black;" class="bs">Totals</th><th style="color:black;" id="liabitiesTotal"class="text-right ind_formats bs">0.00</th></tr>
							</tfoot>
						</table>
					</div>
					<div class="col-md-6 pl-0">
						<table id="collapsed1" class="row-border assertTable dataTable meterialform balanceTab" cellspacing="0" width="50%" style="margin-top: -7px;">
							<thead>
								<tr><th class="text-center bs">Assets</th><th class="text-center bs">Amount(Rs)</th></tr>
							</thead>
							<tbody id="assertTableBody">
							</tbody>
							<tfoot>
								<tr><th class="bs" style="color:black;">Totals</th><th style="color:black;" id="assetsTotal" class="text-right ind_formats bs">0.00</th></tr>
							</tfoot>
						</table>
					</div>
				</div>
			</div>
			</div>
		</div>
	</div>
<script type="text/javascript">

$(document).ready(function(){
	var month=new Date().getMonth()+1
	var year=new Date().getFullYear();
	$('#currentYearAmounts').html('Amounts ('+month+'-'+year+')');
	var pUrl= "${contextPath}/getmonthlybalancesheet/${client.id}/"+month+"/"+year;
	//var pUrl= "${contextPath}/getyearlybalancesheet/${client.id}/"+year;
	var selecttype='month';
	ajaxFuntion(pUrl,selecttype);
	
	$('.liabilitiesTable').height($('.assertTable').height());
});
function generateData(){
	var reporttype = $('#filing_option_balance').html();
	var pUrl='';
	if(reporttype == 'Monthly'){
		var month=$('#monthly').val().split("-");
		$('#currentYearAmounts').html('Amounts ('+month[0]+'-'+month[1]+')');
		pUrl="${contextPath}/getmonthlybalancesheet/${client.id}/"+month[0]+"/"+month[1];
		var selecttype='monthly';
		ajaxFuntion(pUrl,selecttype);
	}else if (reporttype == 'Yearly') {
		var year=$('#yearlyoption_balance').html().split("-");
		pUrl="${contextPath}/getyearlybalancesheet/${client.id}/"+year[0];
		$('#currentYearAmounts').html('Amounts ('+year[0]+'-'+year[1]+')');
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
			assetsResponsedata(data,selecttype);
			liabilitiesResponsedata(data,selecttype);
		},error:function(err){
			$('.assertTable #assertTableBody tr').detach();
			$('.assertTable #assertTableBody').append('<tr><td colspan="2" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');
			
			$('.liabilitiesTable #liabilitiesTableBody tr').detach();
			$('.liabilitiesTable #liabilitiesTableBody').append('<tr><td colspan="2" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');
			$('#processing').text('');
		}
	});
}

function assetsResponsedata(response,selecttype){
	var i=1; var padding_left='pl-1';
	var content='';
	var total=0;
	var totopeningamt=0, totdebitamt=0, totcreditamt=0, totclosingamt=0;
	$('.assertTable #assertTableBody tr').detach();
	var dataflag=false;
	$.each(response, function(key, group){
		var path=group.path.split("/");
		if("ASSETS" == path[0].toUpperCase()){
			var flag=false;
			if(group.amount.closingamt !=0){flag=true;dataflag=true;}
			if(flag){
				var j=1;
				content+='<tr data-node-id="'+i+'">';
				content+='<td class="treemainhead">'+group.groupname+'</td>';
				content+='<td align="right" class="treemainhead"><span class="ind_formats">'+group.amount.closingamt+'</span></td></tr>';
				total+=group.amount.closingamt;
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt !=0){
						var datanodeid=i+'.'+j;
						var datanodepid=i;
						var abc = 20;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
						content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span></td></tr>';
						if(subgroup.children){
							content+=assetsChildnodes(datanodeid, datanodepid, content, subgroup.children,padding_left);
						}
						j++;
					}
				});
				i++;
			}
		}
	});
	if(dataflag==false){
		content+='<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">No Records Found</td></tr>';
	}
	$('#assetsTotal').html(total)
	$('.assertTable #assertTableBody').append(content);	
	$('.balanceTab').simpleTreeTable({margin: '20'});
}
function assetsChildnodes(datanodeid, datanodepid, content, subgrpchilds,padding_left){
	var i=1;
	var childdatanodeid=datanodeid+'.'+i;
	var content="";
	$.each(subgrpchilds, function(key, subgroup){
		if(subgroup.amount.closingamt !=0){
			var abc = 25;
			content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
			content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
			content+='<td align="right" class="childhead"><span class="ind_formats">'+subgroup.amount.closingamt+'</span></td></tr>';
			
			if(subgroup.children){
				content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,padding_left,1);
			}
			i++;
			childdatanodeid=datanodeid+'.'+i;			
		}
	});
	return content;	
}

function liabilitiesResponsedata(response,selecttype){
	var i=2; var padding_left='pl-1';
	var content='';
	var total=0, pandlTotal=0;
	var totopeningamt=0, totdebitamt=0, totcreditamt=0, totclosingamt=0;
	$('.liabilitiesTable #liabilitiesTableBody tr').detach();
	var dataflag=false;
	$.each(response, function(key, group){
		var path=group.path.split("/");
		if("INCOME" == path[0].toUpperCase()){
			pandlTotal+=group.amount.closingamt;
			if(pandlTotal !=0){
				dataflag=true;				
			}
		}
		if("EXPENSES" == path[0].toUpperCase()){
			pandlTotal+=group.amount.closingamt;
			if(pandlTotal !=0){
				dataflag=true;				
			}
		}
		if("LIABILITIES" == path[0].toUpperCase()){
			var flag=false;
			if(group.amount.closingamt !=0){flag=true;dataflag=true;}
			if(flag){
				var j=1;
				content+='<tr data-node-id="'+i+'">';
				content+='<td class="treemainhead">'+group.groupname+'</td>';
				if(group.amount.closingamt<=0){
					total+=Math.abs(group.amount.closingamt);
					content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(group.amount.closingamt);+'</span></td></tr>';
				}else{
					total+=-group.amount.closingamt;
					content+='<td align="right" class="treemainhead">-<span class="ind_formats">'+group.amount.closingamt+'</span></td></tr>';
				}
				
				$.each(group.children, function(key, subgroup){
					if(subgroup.amount.closingamt !=0){
						var datanodeid=i+'.'+j;
						var datanodepid=i;
						var abc=20;
						content+='<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
						content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
						if(subgroup.amount.closingamt<=0){
							content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt);+'</span></td></tr>';
						}else{
							content+='<td align="right" class="childhead">-<span class="ind_formats">'+subgroup.amount.closingamt+'</span></td></tr>';
						}
						if(subgroup.children){
							content+=liabilitiesChildnodes(datanodeid, datanodepid, content, subgroup.children,padding_left);
						}
						j++;	
					}
				});
				i++;
			}
		}
	});
	if(dataflag==true && pandlTotal !=0){
		var abc = 20;
		content+='<tr data-node-id="1">';
		content+='<td class="treemainhead">Reserves and surplus</td>';
		if(pandlTotal<=0){
			content+='<td align="right" class="treemainhead"><span class="ind_formats">'+Math.abs(pandlTotal)+'</span></td></tr>';
		}else{
			content+='<td align="right" class="treemainhead">-<span class="ind_formats">'+pandlTotal+'</span></td></tr>';			
		}
		content+='<tr data-node-id="1.1" data-node-pid="1">';
		content+='<td class="childhead" style="padding-left:'+abc+'px;">Profit and loss account</td>';
		if(pandlTotal<=0){
			content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(pandlTotal)+'</span></td></tr>';
		}else{
			content+='<td align="right" class="childhead">-<span class="ind_formats">'+pandlTotal+'</span></td></tr>';			
		}
	}
	if(dataflag==false){
		content+='<tr><td colspan="5" style="text-align: center;height: 50px;vertical-align: inherit;border-right: 1px solid lightgray !important;">No Records Found</td></tr>';
	}
	if(pandlTotal<=0){
		total+=Math.abs(pandlTotal);
	}else{
		total+=-pandlTotal;
	}
	
	$('#liabitiesTotal').html(total)
	$('.liabilitiesTable #liabilitiesTableBody').append(content);	
	$('.balanceTab').simpleTreeTable({margin: '20'});
	OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
	var lheight = $('#liabilitiesTableBody').innerHeight();
	var aheight = $('#assertTableBody').innerHeight();
	if(lheight > aheight){
		var diffheight = lheight-aheight;
		var content="<tr style='height:"+diffheight+"px;color:white'><td></td><td></td></tr>";
		$('.assertTable #assertTableBody').append(content);	
	}else if(lheight < aheight){
		var diffheight = aheight-lheight;
		var content="<tr style='height:"+diffheight+"px;color:white'><td></td><td></td></tr>";
		$('.liabilitiesTable #liabilitiesTableBody').append(content);	
	}
	$('#processing').text('');
}
function liabilitiesChildnodes(datanodeid, datanodepid, content, subgrpchilds,padding_left){
	var i=1;
	var childdatanodeid=datanodeid+'.'+i;
	var content="";
	$.each(subgrpchilds, function(key, subgroup){
		if(subgroup.amount.closingamt !=0){
			var abc=25;
			content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
			content+='<td class="childhead" style="padding-left:'+abc+'px;">'+subgroup.groupname+'</td>';
			if(subgroup.amount.closingamt<=0){
				content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt);+'</span></td></tr>';
			}else{
				content+='<td align="right" class="childhead">-<span class="ind_formats">'+subgroup.amount.closingamt+'</span></td></tr>';
			}
			if(subgroup.children){
				content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,padding_left,1);
			}
			i++;
			childdatanodeid=datanodeid+'.'+i;
		}
	});
	return content;	
}

function childnodes(datanodeid, datanodepid, content, subgrpchilds,padding_left,z){
	var i=1;
	var childdatanodeid=datanodeid+'.'+i;
	var content="";
	$.each(subgrpchilds, function(key, subgroup){
		if(subgroup.amount.closingamt != 0 || subgroup.amount.previousyearclosingamt !=0){	
			var k=z;
			var abc = "";
			if(k == 1){
				abc = z*35;
			}else{
				abc = 35+z*5;
			}
			content+='<tr data-node-id="'+childdatanodeid+'" data-node-pid="'+datanodeid+'">';
			content+='<td class="childhead" style="padding-left:'+abc+'px">'+subgroup.groupname+'</td>';
			
			if(subgroup.amount.closingamt<=0){
				content+='<td align="right" class="childhead"><span class="ind_formats">'+Math.abs(subgroup.amount.closingamt)+'</span></td>';
			}else{
				content+='<td align="right" class="childhead">-<span class="ind_formats">'+subgroup.amount.closingamt+'</span></td>';
			}
			k++;
			if(subgroup.children){
				content+=childnodes(childdatanodeid, datanodepid, content, subgroup.children,padding_left,k);
			}
			i++;
			childdatanodeid=datanodeid+'.'+i;	
		}
	});
	return content;
}

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
</script>
</body>
</html>
