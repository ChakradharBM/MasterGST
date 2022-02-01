function formatDate(data){
	var createdDt = new Date(data) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}
function journalsView(journaldata, client_id, reportactiontype){
	var debit='Dr', credit='Cr';
	var createdDate = formatUpdatedDate(journaldata.dateofinvoice);
	var invStatus = journaldata.status != undefined ? journaldata.status : "";
	var journalid = journaldata.invoiceId;
	var journalreturntype = journaldata.returnType;
	var jsptype = 'ledgerreport';
	var index = 1;
	var invokeData = new Array();
	var createdDate = formatUpdatedDate(journaldata.dateofinvoice);
	var invStatus = journaldata.status != undefined ? journaldata.status : "";
	var journalid = journaldata.invoiceId;
	var journalreturntype = journaldata.returnType;
	if(journalreturntype == 'Voucher' || journalreturntype == 'Contra'){
		journalid = journaldata.userId;
	}
	if(journaldata.drEntrie != undefined && journaldata.crEntrie != undefined){
		journaldata.drEntrie.forEach(function(drEntrie){
			var journalss = new Array();
			journalss.push(index);
			journalss.push('<span class="rightindent invStatus_'+invStatus+'">'+debit+' &nbsp; '+drEntrie.name+' A/c</span>');
			journalss.push('<span class="rightindent indformat invStatus_'+invStatus+'">'+drEntrie.value+'</span>');
			journalss.push('<span class=""></span>');
			journalss.push(createdDate);
			if(journalreturntype == 'GSTR1'){
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+journaldata.invoiceType+' - Sales)</span></span></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
			}else if(journalreturntype == 'Voucher'){
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Voucher)</span></span></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateVoucherDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'Contra'){
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Contra)</span></span></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateContraDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'Payment Receipt'){
				var journalvoucherNumber = journaldata.invoiceNumber;
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Receipt)</span></span></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR1\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR1\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'Payment'){
				var journalvoucherNumber = journaldata.invoiceNumber;
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Payment)</span></span></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR2\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR2\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'GSTR2'){
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+journaldata.invoiceType+' - Purchase)</span></span></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
			}else if(journalreturntype == 'EXPENSES'){
				journalss.push('<a class="hrefStatus_'+invStatus+'" href="#">EXPENSES</a><div class="srtType"></div>');
				//journalss.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\''+journalreturntype+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\''+journalreturntype+'\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}
			invokeData.push(journalss);
		});
		var lastCol = 0;
		journaldata.crEntrie.forEach(function(crEntrie){
			var pindex = new Array();
			pindex.push(index);
			var cssAdd = ++lastCol == journaldata.crEntrie.length ? "abcd" : "";
			pindex.push('<span class="'+cssAdd+' leftindent invStatus_'+invStatus+'">'+credit+' &nbsp; '+crEntrie.name+' A/c</span>');
			pindex.push('<span class="'+cssAdd+'"></span>');
			pindex.push('<span class="'+cssAdd+' indformat invStatus_'+invStatus+' rightindent">'+crEntrie.value+'</span>');
			pindex.push(createdDate);
			if(journalreturntype == 'GSTR1'){
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+journaldata.invoiceType+' - Sales)</span></span></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
			}else if(journalreturntype == 'Voucher'){
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Voucher)</span></span></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateVoucherDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'Contra'){
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Contra)</span></span></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updateContraDetails(\''+journalid+'\',\'journaldetails\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showDeletePopup(\''+journalid+'\',\''+journalreturntype+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'Payment Receipt'){
				var journalvoucherNumber = journaldata.invoiceNumber;
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Receipt)</span></span></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR1\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR1\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'Payment'){
				var journalvoucherNumber = journaldata.invoiceNumber;
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">(Payment)</span></span></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\'GSTR2\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\'GSTR2\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}else if(journalreturntype == 'GSTR2'){
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">'+journaldata.invoiceNumber+'</a><div class="srtType"><span class="rightindents"><span class="rightindents">('+journaldata.invoiceType+' - Purchase)</span></span></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="editInvPopup(null,\''+journalreturntype+'\',\''+journalid+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
			}else if(journalreturntype == 'EXPENSES'){
				pindex.push('<a class="hrefStatus_'+invStatus+'" href="#">EXPENSES</a><div class="srtType"></div>');
				//pindex.push('<a class="hrefStatus_'+invStatus+'" href="#" onClick="updatePaymentDetails(\''+journalid+'\',\''+journalreturntype+'\',\''+jsptype+'\')"><i class="fa fa-edit editStatus_'+invStatus+'" style="font-size: 17px;vertical-align: middle;"></i></a><a class="hrefStatus_'+invStatus+'" href="#" onClick="showJournalPaymentDeletePopup(\''+journalid+'\',\''+journalreturntype+'\',\''+client_id+'\',\''+journalvoucherNumber+'\',\'abcd\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-left: 2px;margin-top: -10px;"></a>');
			}
			invokeData.push(pindex);
		});
	}
	var table = $('#ledgerJournalsViewTable').DataTable({
	    columns: [
	        {
	        	name: 'second',
	            title: 'S.No',
	            width:'1%' 
	        },
	        {
	            title: 'Journal Details / Particulars',
	            width:'50%',
	           // sortable: false
	        },
	        {
	            title: 'Debit',
	          //  sortable: false
	        },
	        {
	            title: 'Credit',
	            //sortable: false
	        },
	        {
	        	name: 'second1',
	            title: 'Created Date',
	            width:'8%'
	        },
			{	
	        	name: 'second2',
	            title: 'Invoice No',
	            width:'10%',
	            //sortable: false
	        }
			/*,{	
	        	name: 'second3',
	            title: 'Action',
	            width:'5%',
	        }*/
	    ],
	    data: invokeData,
	    rowsGroup: [// Always the array (!) of the column-selectors in specified order to which rows groupping is applied
	                // (column-selector could be any of specified in https://datatables.net/reference/type/column-selector)
	        'second:name',
	        'second1:name',
	        'second2:name'
	    ],
	    'dom':'lrt',
	    "paging": false,
	    "ordering": false,
	    "bInfo" : false,
	    "bsearchable": false, 
        "info":     false,
	    //"lengthChange": true,
	    "lengthMenu": [[10, 25, 50,100, -1], [10, 25, 50,100, "All"]],
	    pageLength: '10',
	    drawCallback: function(){
	          $('.paginate_button', this.api().table().container())          
	             .on('click', function(){
	            	 $('.abcd').parent().css('border-bottom','1px dashed lightgrey');
	       		  $('.leftindent').parent().parent().css('line-height','0.5');
	       		  $('.rightindent').parent().parent().css('line-height','0.5');
	       		  $('.rtType').css('margin-top','10px');
	       		  $('.srtType').css('margin-top','5px');
	       		  $(".indformat,.indformats").each(function(){
	       			    $(this).html($(this).html().replace(/,/g , ''));
	       			});
	       		  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	       		  OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
	             });       
	       }
	    });
		$(".indformat,.indformats").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
	  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	  OSREC.CurrencyFormatter.formatAll({selector : '.indformats'});
	  $('#processing').text('');
}

function showDeletePopup(journalId, returnType, journalnumber) {
	$('#deleteModal').modal('show');
	$('#delheader').html("Delete "+returnType+"");
	$('#delitem').html(""+returnType+"");
	$('#btnDelete').html("Delete "+returnType+"").attr('onclick', "deleteJournal('"+journalId+"','"+returnType+"','"+journalnumber+"')");
}
function deleteJournal(journalId,returnType,journalnumber) {
	$.ajax({
		url: contextPath+"/deljournal/"+journalId,
		success : function(response) {
			$('#successModal').modal('show');
			$('#h6data').html(returnType +"&nbsp; Deleted Successfully...!");
			$('#pdata').html(returnType+" Number :<strong>"+journalnumber+"</strong>");
		}
	});
}