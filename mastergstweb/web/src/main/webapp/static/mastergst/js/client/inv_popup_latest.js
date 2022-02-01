	$(document).ready(function(){
		intialIntialization();
		$('#salesinvoceform').submit(function(e) {
	var invType =$('#idInvType').val();var retType = $('#retType').val();var err = 0;var invmsg = $('#invno_Msg').html();var gstmsg = $('.clientgstno').text();
	$('#bankAcctNo').parent().removeClass('has-error has-danger');$('#bankAcctNo').removeAttr("data-error");
	$('#bankIFSC').parent().removeClass('has-error has-danger');$('#bankIFSC').removeAttr("data-error");
	if(invType == 'Advances' || 'Advance Adjusted Detail'){
		 $('#uqc_text1').removeAttr("required");
        $('#qty_text1').removeAttr("required");
		$('#qty_text1,#uqc_text1').parent().removeClass('has-error has-danger');
	}
	$('#salesinvoceform input[required="required"]').each(function(){
		 var abc = $(this).val();
		var ele=$('.form-group').is('.has-error');
	    if (ele) { 
	    	err = 1;
	      $('.btn_popup_save').removeClass('btn-loader-blue');
	    	
	    }else{
	    	if(abc == ''){
	    		err=1;
	    		$('.btn_popup_save').removeClass('btn-loader-blue');
	    	}else{
	    	err=0;
	    	$('.btn_popup_save').addClass('btn-loader-blue');
	    	}
	    }
	 });

	$('#salesinvoceform').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  err = 1;
		 		   $(this).parent().addClass('has-error has-danger');
		 	   }else{
		 		   $(this).parent().removeClass('has-error has-danger');
		 	   }
	    }
	});
	$('#salesinvoceform').find('select').each(function(){
	    if(!$(this).prop('required')){
	    }else{
				if (this.value == '-Select-' || this.value == '') {
					err = 1;
			       $(this).parent().addClass('has-error has-danger');
			    }else{
			    	$(this).parent().removeClass('has-error has-danger');
			    }
	    }
	});
	if(invmsg != '' && (retType == 'GSTR1' || retType == 'DELIVERYCHALLANS' || retType == 'PROFORMAINVOICES' || retType == 'ESTIMATES' || retType == 'PurchaseOrder' || retType == 'PurchaseRegister' || retType == 'Purchase Register' || retType == 'GSTR2')){err =1;}
	
	if(gstmsg != ''){err=1;}
	$.each($("#allinvoice tr td input.disval"),function() {
		if ($(this).val()) {
			var dStr = ($(this).val()).toString();
			if(dStr.indexOf("%") > -1){
				dStr = dStr.replace("%","");
			}
			$(this).val(dStr);
		}
	});
	$.each($("#allinvoice tr td input.cessval"),function() {
		if ($(this).val()) {
			var dStr = ($(this).val()).toString();
			if(dStr.indexOf("%") > -1){
				dStr = dStr.replace("%","");
			}
			$(this).val(dStr);
		}
	});
	if(invType != 'Nil Supplies' && retType != 'EWAYBILL'){
	    $.each($("#allinvoice tr td.invoiceTaxrate"),function() {
			if ($(this).html() == null || $(this).html() == '') {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details";err =1;}
		})
	}else{
		$.each($("#allinvoice tr td.addITCRuleFlag"),function() {
			if ($(this).html() == null || $(this).html() == '') {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details";err =1;}
		})	
	}
	if(retType == 'GSTR1' || retType == 'SalesRegister'){
	
	if(otherconfigdetails.enableSalesFields == false && otherconfigdetails.enableSalesFields == '' && otherconfigdetails.enableSalesFields == null){
	if(invType != 'B2C' || (invType != 'ISD' && retType == 'GSTR6')){
		if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD'){
		$.each($("#allinvoice tr td input.hsnval"),function() {
			if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";err =1;}
		})
		}
	}
	}
	}else if(retType == 'GSTR2' || retType == 'Purchase Register' || retType == 'PurchaseRegister'){
		if(otherconfigdetails.enablePurFields == false && otherconfigdetails.enablePurFields == '' && otherconfigdetails.enablePurFields == null){
			if(invType != 'B2C' || (invType != 'ISD' && retType == 'GSTR6')){
				if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD'){
				$.each($("#allinvoice tr td input.hsnval"),function() {
					if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";err =1;}
				})
				}
			}
			}
	}else{
		if(invType != 'B2C' || (invType != 'ISD' && retType == 'GSTR6')){
			if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD'){
			$.each($("#allinvoice tr td input.hsnval"),function() {
				if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";err =1;}
			})
			}
		}
	}
	if(invType == 'ISD' && retType == 'GSTR6'){
		$.each($("#allinvoice tr td select.taxrate_textDisable"),function() { 
		if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";err =1;}
	})
  }
  if(invType != 'ISD' && retType != 'GSTR6' && invType != 'Advances' ){
	  if(retType == 'GSTR1' || retType == 'SalesRegister'){
	  if(otherconfigdetails.enableSalesFields == false && otherconfigdetails.enableSalesFields == '' && otherconfigdetails.enableSalesFields == null){
	  if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD' && retType != 'EWAYBILL'){
   $.each($("#allinvoice tr td input.qtyval"),function() { 
    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
  })
	  }
  if(invType != 'Advances' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && retType != 'EWAYBILL'){
   $.each($("#allinvoice tr td input.uqcval"),function() {
	   if(invType != 'Nil Supplies' && invType != 'ITC Reversal' && invType != 'ISD'){
    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;
    }else{
    	$.ajax({
			url: contextPath+"/validuqcconfig?query="+ $(this).val() + "&format=json",
			async:false,
			contentType: 'application/json',
			success : function(response) {
				if(response.length == 0){
					$('.invoiceUqc').addClass('has-error has-danger');
					document.getElementById("invitemdetails").innerHTML = "Please Enter Valid UQC";
					err =1;
				}
			}
		});
    }
    }else{
    	if($(this).val()){
    		$.ajax({
    			url: contextPath+"/validuqcconfig?query="+ $(this).val() + "&format=json",
    			async:false,
    			contentType: 'application/json',
    			success : function(response) {
    				if(response.length == 0){document.getElementById("invitemdetails").innerHTML = "Please Enter Valid UQC";err =1;$('.invoiceUqc').addClass('has-error has-danger'); }
    			}
    		});	
    	}
    }
  })
}
  $.each($("#allinvoice tr td input.hsnval"),function() {
	   if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD'){
    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;
    }}
  })
	  }
	  }else if(retType == 'GSTR2' || retType == 'PurchaseRegister'  || retType == 'Purchase Register'){
		  if(otherconfigdetails.enablePurFields == false && otherconfigdetails.enablePurFields == '' && otherconfigdetails.enablePurFields == null){
			  if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD'){
		   $.each($("#allinvoice tr td input.qtyval"),function() { 
		    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
		  })
			  }
		  if(invType != 'Advances' && invType != 'Advance Adjusted Detail' && invType != 'TXPA'){
		   $.each($("#allinvoice tr td input.uqcval"),function() {
			   if(invType != 'Nil Supplies' && invType != 'ITC Reversal' && invType != 'ISD'){
		    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;
		    }else{
		    	$.ajax({
					url: contextPath+"/validuqcconfig?query="+ $(this).val() + "&format=json",
					async:false,
					contentType: 'application/json',
					success : function(response) {
						if(response.length == 0){
							$('.invoiceUqc').addClass('has-error has-danger');
							document.getElementById("invitemdetails").innerHTML = "Please Enter Valid UQC";
							err =1;
						}
					}
				});
		    }
		    }else{
		    	if($(this).val()){
		    		$.ajax({
		    			url: contextPath+"/validuqcconfig?query="+ $(this).val() + "&format=json",
		    			async:false,
		    			contentType: 'application/json',
		    			success : function(response) {
		    				if(response.length == 0){document.getElementById("invitemdetails").innerHTML = "Please Enter Valid UQC";err =1;$('.invoiceUqc').addClass('has-error has-danger'); }
		    			}
		    		});	
		    	}
		    }
		  })
		}
		  $.each($("#allinvoice tr td input.hsnval"),function() {
			   if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'ITC Reversal' && invType != 'ISD'){
		    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;
		    }}
		  })
			  }
	  }else{
		  if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD' && retType != 'EWAYBILL'){
			   $.each($("#allinvoice tr td input.qtyval"),function() { 
			    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
			  })
				  }
			  if(invType != 'Advances' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && retType != 'EWAYBILL'){
			   $.each($("#allinvoice tr td input.uqcval"),function() {
				   if(invType != 'Nil Supplies' && invType != 'ITC Reversal' && invType != 'ISD'){
			    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;
			    }else{
			    	$.ajax({
						url: contextPath+"/validuqcconfig?query="+ $(this).val() + "&format=json",
						async:false,
						contentType: 'application/json',
						success : function(response) {
							if(response.length == 0){
								$('.invoiceUqc').addClass('has-error has-danger');
								document.getElementById("invitemdetails").innerHTML = "Please Enter Valid UQC";
								err =1;
							}
						}
					});
			    }
			    }else{
			    	if($(this).val()){
			    		$.ajax({
			    			url: contextPath+"/validuqcconfig?query="+ $(this).val() + "&format=json",
			    			async:false,
			    			contentType: 'application/json',
			    			success : function(response) {
			    				if(response.length == 0){document.getElementById("invitemdetails").innerHTML = "Please Enter Valid UQC";err =1;$('.invoiceUqc').addClass('has-error has-danger'); }
			    			}
			    		});	
			    	}
			    }
			  })
			}
			  $.each($("#allinvoice tr td input.hsnval"),function() {
				   if(invType != 'Nil Supplies' && invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'ITC Reversal' && invType != 'ISD'){
			    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;
			    }}
			  })
	  }
	  
  if(invType == 'Exports'){
		if($("#exportType option:selected").val()=='WPAY'){
		   $.each($("#allinvoice tr td select.taxrate_textDisable"),function() { 
			if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
		  })
		}
  }else{
	  if(invType != 'Nil Supplies' && invType != 'ITC Reversal' && retType != 'EWAYBILL'){
	$.each($("#allinvoice tr td select.taxrate_textDisable"),function() { 
    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
  })  
  }
  }
	  
	  
	  
   /*if(invType != 'Nil Supplies' && invType != 'Exports' && invType != 'Credit/Debit Notes' && invType != 'Advances' && invType != 'ITC Reversal' && invType != 'B2B' && invType != 'B2C' && invType != 'B2CL' && type != 'Advance Adjusted Detail' && invType != 'TXPA' && invType !='Credit/Debit Note for Unregistered Taxpayers' && invType !='Advance Adjusted Detail' && invType != 'TXPA' && invType !='PROFORMAINVOICES' && invType !='DELIVERYCHALLANS' && invType !='ESTIMATES' && invType !='PurchaseOrder' && invType != 'B2BA' && invType != 'B2CSA' && invType != 'B2CLA' && invType != 'ATA' && invType != 'EXPA' && invType != 'TXPA' && invType != 'CDNURA' && invType != 'CDNA'){
	  $.each($("#allinvoice tr td select.itcval"),function() { 
    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";err =1;}
	})
   }*/
   if(invType == 'Advances' ){
  $.each($("#allinvoice tr td input.advrval"),function() { 
    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";err =1;}
  })
   }
  if(invType != 'Nil Supplies' && invType != 'ITC Reversal'  && invType !='PROFORMAINVOICES' && invType !='DELIVERYCHALLANS' && invType !='ESTIMATES' && invType !='PurchaseOrder' && retType != 'EWAYBILL'){
	  if(enableSalesCess == true || enableSalesCess == "true"){
		  $.each($("#allinvoice tr td input.cessval"),function() { 
		    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
		  })
	  }
  }
  }
  if(invType == 'Nil Supplies'){
	  $.each($("#allinvoice tr td select.itcruleval"),function() {
		    if (!$(this).val()) {document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields"; err =1;}
		  })  
  }
	if(retType == 'GSTR2' || retType == 'PurchaseRegister' || retType == 'Purchase Register'){
		var invdate = $('#dateofinvoice').val();var billdate = $('.billDate').val();
		var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
		var dtFromArray = invdate.match(rxDatePattern);
		dtFromDay = dtFromArray[1];
		dtFromMonth= dtFromArray[3];	
		dtFromYear = dtFromArray[5];
		var invdate1 = dtFromMonth+"/"+dtFromDay+"/"+dtFromYear;
		var dtToArray = billdate.match(rxDatePattern);
		dtToDay = dtToArray[1];
		dtToMonth= dtToArray[3];	
		dtToYear = dtToArray[5];
		var billdate1 = dtToMonth+"/"+dtToDay+"/"+dtToYear;
		if(new Date(billdate1) < new Date(invdate1)){
			err=1;
			$('#transactiondate').css("display","block");
		}else{
			$('#transactiondate').css("display","none");
			//err=0;
		}
}	
	
	if(invType == 'ITC Reversal'){
		var abcd=0;var firstrow=0;
		$.each($("#allinvoice1 tr.item_edit1"),function() {
			if($('#isdigsttax_text1').val() == '' || $('#isdcgsttax_text1').val() == '' || $('#isdsgsttax_text1').val() == ''){
				abcd=0;
				if($('#isdigsttax_text1').val() != '' || $('#isdcgsttax_text1').val() != '' || $('#isdsgsttax_text1').val() != ''){firstrow=1;
				}else{firstrow=0;}
			}else{abcd=1;firstrow=1;}
			})
		
		var secondrow=0;
		$.each($("#allinvoice1 tr.item_edit2"),function() {
			if($('#isdigsttax_text2').val() == '' || $('#isdcgsttax_text2').val() == '' || $('#isdsgsttax_text2').val() == ''){
				if(abcd == 1){
					if($('#isdigsttax_text2').val() != '' || $('#isdcgsttax_text2').val() != '' || $('#isdsgsttax_text2').val() != ''){abcd=0;}
				}
			
				if($('#isdigsttax_text2').val() != '' || $('#isdcgsttax_text2').val() != '' || $('#isdsgsttax_text2').val() != ''){secondrow=1;
				}else{secondrow=0;}
			}else{abcd=1;secondrow=1;}
			})
			var thirdrow=0;
			$.each($("#allinvoice1 tr.item_edit3"),function() {
			if($('#isdigsttax_text3').val() == '' || $('#isdcgsttax_text3').val() == '' || $('#isdsgsttax_text3').val() == ''){
				if(abcd == 1){
					if($('#isdigsttax_text3').val() != '' || $('#isdcgsttax_text3').val() != '' || $('#isdsgsttax_text3').val() != ''){abcd=0;}
				}
				if($('#isdigsttax_text3').val() != '' || $('#isdcgsttax_text3').val() != '' || $('#isdsgsttax_text3').val() != ''){
					thirdrow=1;
				}else{thirdrow=0;}
				
			}else{abcd=1;thirdrow=1;}
			})
			var fourthrow=0;
			$.each($("#allinvoice1 tr.item_edit4"),function() {
			if($('#isdigsttax_text4').val() == '' || $('#isdcgsttax_text4').val() == '' || $('#isdsgsttax_text4').val() == ''){
			
				if(abcd == 1){
					if($('#isdigsttax_text4').val() != '' || $('#isdcgsttax_text4').val() != '' || $('#isdsgsttax_text4').val() != ''){
						abcd=0;
					}
				}
				if($('#isdigsttax_text4').val() != '' || $('#isdcgsttax_text4').val() != '' || $('#isdsgsttax_text4').val() != ''){
					fourthrow=1;
				}else{	fourthrow=0;}
				
			}else{abcd=1;fourthrow=1;}
			})
			var fifthrow=0;
			$.each($("#allinvoice1 tr.item_edit5"),function() {
			if($('#isdigsttax_text5').val() == '' || $('#isdcgsttax_text5').val() == '' || $('#isdsgsttax_text5').val() == ''){
			
				if(abcd == 1){
					if($('#isdigsttax_text5').val() != '' || $('#isdcgsttax_text5').val() != '' || $('#isdsgsttax_text5').val() != ''){
						abcd=0;
					}
				}
				if($('#isdigsttax_text5').val() != '' || $('#isdcgsttax_text5').val() != '' || $('#isdsgsttax_text5').val() != ''){
					fifthrow=1;
				}else{	fifthrow=0;}
				
			}else{
				abcd=1;fifthrow=1;
			}
			})
			var sixthrow=0;
			$.each($("#allinvoice1 tr.item_edit6"),function() {
			if($('#isdigsttax_text6').val() == '' || $('#isdcgsttax_text6').val() == '' || $('#isdsgsttax_text6').val() == ''){
			
				if(abcd == 1){
					if($('#isdigsttax_text6').val() != '' || $('#isdcgsttax_text6').val() != '' || $('#isdsgsttax_text6').val() != ''){
						abcd=0;
					}
				}
				if($('#isdigsttax_text6').val() != '' || $('#isdcgsttax_text6').val() != '' || $('#isdsgsttax_text6').val() != ''){
					sixthrow=1;
				}else{	sixthrow=0;}
				
			}else{
				abcd=1;sixthrow=1;
			}
			})
			var seventhrow=0;
			$.each($("#allinvoice1 tr.item_edit7"),function() {
			if($('#isdigsttax_text7').val() == '' || $('#isdcgsttax_text7').val() == '' || $('#isdsgsttax_text7').val() == ''){
			
				if(abcd == 1){
					if($('#isdigsttax_text7').val() != '' || $('#isdcgsttax_text7').val() != '' || $('#isdsgsttax_text7').val() != ''){
						abcd=0;
					}
				}
				if($('#isdigsttax_text7').val() != '' || $('#isdcgsttax_text7').val() != '' || $('#isdsgsttax_text7').val() != ''){
					seventhrow=1;
				}else{	seventhrow=0;}
				
			}else{
				abcd=1;seventhrow=1;
			}
			})
		if(abcd == 1){
			err=0;
			document.getElementById("invitemdetails").innerHTML = "";
		}else{
			err=1;
			if(firstrow == '0' && secondrow == '0' && thirdrow == '0' && fourthrow == '0' && fifthrow == '0' && sixthrow == '0' && seventhrow == '0'){
				document.getElementById("invitemdetails").innerHTML = "Please fill atleast one Item";
			}else{
				document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";
			}
			
		}
	
	}
	if(invType == 'ISD'){
		var abcd=0;
		var firstrow=0;
		$.each($("#allinvoice1 tr.item_edit1"),function() {
			if($('#isdigsttax_text1').val() == '' || $('#isdcgsttax_text1').val() == '' || $('#isdsgsttax_text1').val() == ''){
				abcd=0;
				if($('#isdigsttax_text1').val() != '' || $('#isdcgsttax_text1').val() != '' || $('#isdsgsttax_text1').val() != ''){
					firstrow=1;
				}else{
				firstrow=0;
				}
			}else{
				abcd=1;
				firstrow=1;
			}
			})
		
		var secondrow=0;
		$.each($("#allinvoice1 tr.item_edit2"),function() {
			if($('#isdigsttax_text2').val() == '' || $('#isdcgsttax_text2').val() == '' || $('#isdsgsttax_text2').val() == ''){
			
				if(abcd == 1){
					if($('#isdigsttax_text2').val() != '' || $('#isdcgsttax_text2').val() != '' || $('#isdsgsttax_text2').val() != ''){
						abcd=0;
					}
				}
			
				if($('#isdigsttax_text2').val() != '' || $('#isdcgsttax_text2').val() != '' || $('#isdsgsttax_text2').val() != ''){
					secondrow=1;
				}else{
					secondrow=0;
				}
			}else{
				abcd=1;secondrow=1;
			}
			})
			var thirdrow=0;
			$.each($("#allinvoice1 tr.item_edit3"),function() {
			if($('#isdigsttax_text3').val() == '' || $('#isdcgsttax_text3').val() == '' || $('#isdsgsttax_text3').val() == ''){
				
				if(abcd == 1){
					if($('#isdigsttax_text3').val() != '' || $('#isdcgsttax_text3').val() != '' || $('#isdsgsttax_text3').val() != ''){
						abcd=0;
					}
				}
				if($('#isdigsttax_text3').val() != '' || $('#isdcgsttax_text3').val() != '' || $('#isdsgsttax_text3').val() != ''){
					thirdrow=1;
				}else{thirdrow=0;}
				
			}else{
				abcd=1;thirdrow=1;
			}
			})
			var fourthrow=0;
			$.each($("#allinvoice1 tr.item_edit4"),function() {
			if($('#isdigsttax_text4').val() == '' || $('#isdcgsttax_text4').val() == '' || $('#isdsgsttax_text4').val() == ''){
			
				if(abcd == 1){
					if($('#isdigsttax_text4').val() != '' || $('#isdcgsttax_text4').val() != '' || $('#isdsgsttax_text4').val() != ''){
						abcd=0;
					}
				}
				if($('#isdigsttax_text4').val() != '' || $('#isdcgsttax_text4').val() != '' || $('#isdsgsttax_text4').val() != ''){
					fourthrow=1;
				}else{	fourthrow=0;}
				
			}else{
				abcd=1;fourthrow=1;
			}
			})
		if(abcd == 1){
			err=0;
			document.getElementById("invitemdetails").innerHTML = "";
		}else{
			err=1;
			if(firstrow == '0' && secondrow == '0' && thirdrow == '0' && fourthrow == '0'){
				document.getElementById("invitemdetails").innerHTML = "Please fill atleast one Item";
			}else{
				document.getElementById("invitemdetails").innerHTML = "Please fill Item Details/You are missing mandatory fields, please enter the red-colour high lighted fields";
			}
			
		}
	
	}
	
	if(invType == 'Credit/Debit Notes' || invType == 'Credit/Debit Note for Unregistered Taxpayers'){
		var originalvtottaxamount =parseFloat($('#originalinvamt').val());
		var cdn_taxableamout=parseFloat($('#cdn_taxableamount').val());
		if(originalvtottaxamount < cdn_taxableamout){
			document.getElementById("invitemdetails").innerHTML = "Credit/Debit Note total taxable value should not be greater than Original invoice total taxable value";
			err=1;
		}else{
			//err=0;
			if( $('#serialnoofinvoice').val() != $('#voucherNumber').val()){
				   //err=0;
				   $('.creditno').text("");
			   }else{
				  err=1; 
				  $('.creditno').text("OriginalInvNo and credit Note No should not equal");
			   }
         document.getElementById("invitemdetails").innerHTML = '';
		}
	}
	if (invType == 'ADVANCE ADJUSTED DETAIL' || invType == 'Advance Adjusted Detail' || invType == 'TXPA') {
		var originalvtottaxamount =parseFloat($('#advPInvamt').val());
		if(originalvtottaxamount != "" && originalvtottaxamount > 0){
			var totalTaxableadj=document.getElementById("totTaxable").innerHTML;	
			if(/[,\-]/.test(totalTaxableadj)){
			totalTaxableadj = totalTaxableadj.replace(/,/g , '');
			}
			if(totalTaxableadj != '' && totalTaxableadj>0){
				totalTaxableadj = parseFloat(totalTaxableadj);
			}
			if(originalvtottaxamount < totalTaxableadj){
				document.getElementById("invitemdetails").innerHTML = "Advance to be Adjusted value should not be greater than Original invoice total taxable value";
				err=1;
			}
		}
		$('#uqc_text1').attr("required", false);
		$('#qty_text1').attr("required", false);
		$('#hsn_text1').attr("required", false);
		$('#qty_text1').parent().removeClass("has-error has-danger");
	}
  if (err != 0) {
	  $('.btn_popup_save').removeClass('btn-loader-blue');
    return false;
  }else{
	  return true;
  }
});
		var clientSignUrl = _getContextPath()+'/getClientDetails/'+clientId;
		$.ajax({
			url: clientSignUrl,
			async: false,
			cache: false,
			success : function(response) {clientSignatureDetails = response;},
			error : function(e) {}
		});
		$('#itemasOndate').datetimepicker({
			timepicker: false,
			format: 'd/m/Y',
	        scrollMonth: true
		});
		$('.asOndatewrap').click(function(){
			$('#itemasOndate').focus();
		});
		
		$('input[type=radio][name=itemType]').change(function() {
			updateItemProduct(this.value);
		});
		function updateItemProduct(value){
			if(value == "Product"){
				$('.itemCode_txt').text("Item Code");
				$('.itemName_txt').text("Item Name");
				$('.item_grpCode_txt').text("Item Group Code");
				$('.item_grpName_txt').text("Item Group Name");
				$('.item_description_txt').text("Item Description");
				$('.hsnSacTxt').text("HSN Code");
				$('.itemDescription').attr("placeholder","Item Description");
				$('.itemSalesPriceTxt').text('Item Sale Price');
				$('.item_description_txt').css("margin-left","2px");
				$('.descInput').css({"padding-left":"52px"});
				$('.descInput').addClass("pr-0");
				$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').removeClass("d-none");
				var uqcoptions = {
						url: function(phrase) {
							phrase = phrase.replace('(',"\\(");
							phrase = phrase.replace(')',"\\)");
							return contextPath+"/uqcconfig?query="+ phrase + "&format=json";
						},
						getValue: "name",
						list: {
							onLoadEvent: function() {
								if($("#eac-container-unit ul").children().length == 0) {
									$("#unitempty").show();
								} else {
									$("#unitempty").hide();
								}
							},
							maxNumberOfElements: 43
						}
					};
					$("#unit").easyAutocomplete(uqcoptions);
			}else{
				$('.itemCode_txt').text("Service Code");
				$('.itemName_txt').text("Service Name");
				$('.item_grpCode_txt').text("Service Group Code");
				$('.item_grpName_txt').text("Service Group Name");
				$('.item_description_txt').text("Service Description");
				$('.itemDescription').attr("placeholder","Service Description");
				$('.hsnSacTxt').text("SAC Code");
				$('#unit').val("NA");
				$('.itemSalesPriceTxt').text('Service Sale Price');
				$('.item_description_txt').css("margin-left","2px");
				$('.descInput').css({"padding-left":"34px"});
				$('.descInput').removeClass("pr-0");
				$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').addClass("d-none");
				var uqcoptions = {
						data: ["NA"]
					};
					$("#unit").easyAutocomplete(uqcoptions);
			}
		}
		
		var invtyperule = new Array();
		$("#sortable_table").tableDnD();
		$("#sortable_table1").tableDnD();
		$("#sortable_table2").tableDnD();
		$("#sortable_table3").tableDnD();
		$("#sortable_table4").tableDnD();
		$("#sortable_table5").tableDnD();
		$('#dateofinvoice').datetimepicker({
			timepicker: false,
			format: 'd/m/Y',
			maxDate: 0,
			scrollMonth: true,
			horizontal: 'left',
			onChangeDateTime:function(dp,$input){
				//$('#dateofinvoice').validator('update');
			}
		});
		$('#validUpto,#enteredDate1,#transDocDate1,#entered_Date1,#trans_DocDate1').datetimepicker({
			timepicker: false,
			format: 'd/m/Y',
			scrollMonth: true
		});
		$('.date_field').datetimepicker({
			timepicker: false,
			format: 'd-m-Y',
			maxDate: 0,
			scrollMonth: true
		});
		$('.advrcdtval').datetimepicker({
			timepicker: false,
			format: 'd-m-Y',
			maxDate: 0,
			scrollMonth: true
		});
		$('.billDate').datetimepicker({
			timepicker: false,
			format: 'd/m/Y',
			maxDate: 0,
			scrollMonth: true
		});
		$('#expirydateofinvoice').datetimepicker({
			timepicker: false,
			format: 'd/m/Y',
			scrollMonth: true
		});
		$('#deliverydate').datetimepicker({
			timepicker: false,
			format: 'd/m/Y',
			maxDate: 0,
			scrollMonth: true
		});
		$('#duedate_div').datetimepicker({
	        timepicker: false,
	        format: 'd/m/Y',
	        maxDate: false,
			scrollMonth: true
	    });
		$("select#typeselect").change(function(){
			$(this).find("option:selected").each(function(){
				var optionValue = $(this).attr("value");
				if(optionValue){
					$(".typebox").not("." + optionValue).hide();
					$("." + optionValue).show();
				} else{
					$(".typebox").hide();
				}
			});
		}).change();
		//$(".bank-details-box").hide();
		$('.addBankDetails').change(function(){
			if($(this).is(":checked")){
				var rtype = $('#retType').val();
				$("#bank_details").show();
				if(rtype != 'GSTR2'){
					if(clientBankDetails && clientBankDetails.length == 1) {
						$('#selectBank').val(clientBankDetails[0].accountnumber);
						selectBankName(clientBankDetails[0].accountnumber);
					}else if(clientBankDetails && clientBankDetails.length > 1){
						$('#selectBank').removeAttr("disabled");
					}
				}
			}else{
				$("#bank_details").hide();
				$('#selectBank').val("");
				$('#bankName').val("");
				$('#bankAcctNo').val("");
				$('#bankAccountName').val("");
				$('#bankBranch').val("");
				$('#bankIFSC').val("");
				$('#selectBank').attr("disabled","disabled");
			}
		});	
		$('.addSnilBankDetails').change(function(){
			if($(this).is(":checked")){
				$("#snil-bank-details").show();
				if(clientBankDetails && clientBankDetails.length == 1) {
					$('#selectBank1').val(clientBankDetails[0].accountnumber);
					selectBankName(clientBankDetails[0].accountnumber);
				}else if(clientBankDetails && clientBankDetails.length > 1){
					$('#selectBank1').removeAttr("disabled");
				}
			}
			else{
				$("#snil-bank-details").hide();
				$('#selectBank1').val("");
				$('#selectBank1').attr("disabled","disabled");
			}
		});
		$('#samebilladdress').change(function(){
			if($(this).is(":checked")){
				var txt = $('#billingAddress').val();
				$('#shippingAddress').val(txt);
			}else{
				$('#shippingAddress').val('');
			}
		});
		$('#samebilladdress1').change(function(){
			if($(this).is(":checked")){
				var txt = $('#billingAddress1').val();
				$('#shippingAddress1').val(txt);
			}else{
				$('#shippingAddress1').val('');
			}
		});
		$('#samebilladdress2').change(function(){
			if($(this).is(":checked")){
				var txt = $('#billingAddress2').val();
				$('#shippingAddress2').val(txt);
			}else{
				$('#shippingAddress2').val('');
			}
		});
		$('#samebilladdress3').change(function(){
			if($(this).is(":checked")){
				var txt = $('#billingAddress3').val();
				$('#shippingAddress3').val(txt);
			}else{
				$('#shippingAddress3').val('');
			}
		});
		$('#samebilladdress4').change(function(){
			if($(this).is(":checked")){
				var txt = $('#billingAddress4').val();
				$('#shippingAddress4').val(txt);
			}else{
				$('#shippingAddress4').val('');
			}
		});
		$("#includetax").change(function(){
			$("#allinvoice tr").each(function(index){
				var rtype = $("#revchargetype>option:selected").val();
				var itype = $("#idInvType").val();
				if(rtype != 'Reverse'){
					if(itype != "Advance Adjusted Detail"){
						findTaxAmount(index+1);
						findTaxableValue(index+1);
						findCessAmount(index+1);
					}else{
						findadvadjTaxAmount(index+1);
					}
					
				$('.rateinctax').text("");
				}else{
					$("#includetax").prop("checked",false);
					$('.rateinctax').text("If we select RCM rate inclusive tax not applicable");
				}		
				index++;
			});
		});
		$('.fa-calendar').click(function(){
			$(this).nextAll('input').first().focus();
		});
	});
	
	var codeoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/hsnsacconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			categories: [{
				listLocation: "hsnConfig",
				header: "--- HSN ---"
			}, {
				listLocation: "sacConfig",
				header: "--- SAC ---"
			}],
			list: {
				match: {
					enabled: false
				},
				onLoadEvent: function() {
					if($("#eac-container-ItemHSNCode ul").children().length == 0) {
						$("#ItemHSNCodeempty").show();
					} else {
						$("#ItemHSNCodeempty").hide();
					}
				}
			}
		};
		$("#ItemHSNCode").easyAutocomplete(codeoptions);
		var uqcoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/uqcconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-unit ul").children().length == 0) {
						$("#unitempty").show();
					} else {
						$("#unitempty").hide();
					}
				},
				maxNumberOfElements: 43
			}
		};
		$("#unit").easyAutocomplete(uqcoptions);
	function validateCustomer(){
		var c = 0;
		var customerorsuppliertype=$('#customerorsuppliertype').val();
		if(customerorsuppliertype == 'GSTR1'){
			
			var customerid=$('#customerid').val();
			var clientid=$('#clientid').val();
			$.ajax({
				type : "GET",
				async: false,
				contentType : "application/json",
				url: contextPath+"/customeridexits/"+clientid+"/"+customerid,
				success : function(response) {
					if(response == 'success'){
						c++;
						$('#customerid_Msg').text('customerid already exists');
						$('#customer_type').addClass('has-error has-danger');
					}else{
						$('#customerid_Msg').text('');
						$('#customer_type').removeClass('has-error has-danger');
					}
				}
			});
		}else{
			var supplierid=$('#suppliercustomerid').val();
			var clientid=$('#clientid').val();
			$.ajax({
				type : "GET",
				async: false,
				contentType : "application/json",
				url: contextPath+"/suppliercustomeridexits/"+clientid+"/"+supplierid,
				success : function(response) {
					if(response == 'success'){
						c++;
						$('#suppliercustomerid_Msg').text('customer id already exists');
						$('#supplier_type').addClass('has-error has-danger');
					}else{
						$('#suppliercustomerid_Msg').text('');
						$('#supplier_type').removeClass('has-error has-danger');
					}
				}
			});
		}
	var custname = $('#custname').val();
	var custgstin= $('#gstnnumber').val();
	var contactperson = $('#contactperson').val();
	custname = custname.replace(/ +/g, "");
	contactperson = contactperson.replace(/ +/g, "");
	var checks = $('.business').attr('checked');
	var state = $('#state').val();
	
	if($('.business').is(":checked")){
		if(custgstin==""){
			$('#gstnnumber_Msg').text("Please enter Valid GSTIN"); 
			c++;
		}else{  
			$('#gstnnumber_Msg').text(""); 
		}
	}else{
		$('#gstnnumber_Msg').text("");
	}
	if(custname==""){
		$('#businessName_Msg').text("Please enter Name"); 
		c++;
	}else{  
		$('#businessName_Msg').text(""); 
	}
	if(state==""){
		$('#state_Msg').text("Please enter state");
		c++;
	}else{
		$('#state_Msg').text("");
	}	
	return c==0; 
}
	
	function saveCustomer() {
		$('#invaddcst').addClass('btn-loader-blue');
		if(validateCustomer()){
			$('.errormsg').css('display','none');
			$('.with-errors').html('');
			$('.form-group').removeClass('has-error has-danger');
			
			$.ajax({
				type: "POST",
				url: $("#customerForm").attr('action'),
				data: $("#customerForm").serialize(),
				success: function(data) {
					$('#billedtoname').val($('#custname').val());
					$('#billedtogstin').val($('#gstnnumber').val());
					$('#billingAddress').val($('#custaddress').val());
					$('#bankTerms').val($('#customertermsinv').val());
					if($('#retType').val() != 'GSTR2'){
						$('#billedtostatecode').val($('#state').val());
						$('#vendorName').val($('#ccustomerLedgerName').val());
						$('.addBankDetails').attr("checked","true");
						if($('.addBankDetails').is(":checked")){
							$("#selectBankDiv").show();
							$("#selectBank").removeAttr("disabled");
							$(".bank-details-box").show();
						}else{
							$(".bank-details-box").hide();
							$("#selectBankDiv").hide();
						}
						$('#selectBank').val($('#customerBankAcctNos').val());	
						$('#bankName').val($('#customerBankNames').val());
						$('#bankAcctNo').val($('#customerBankAcctNos').val());
						$('#bankBranch').val($('#customerBankBranchs').val());
						$('#bankIFSC').val($('#customerBankIFSCs').val());
						$('#bankAccountName').val($('#customerBankAccountNames').val());
					}else{
						$('#bankName').val($('#sbankName').val());
						$('#bankAcctNo').val($('#saccountNumber').val());
						$('#bankBranch').val($('#sbranchAddress').val());
						$('#bankIFSC').val($('#sifscCode').val());
						$('#bankAccountName').val($('#sbeneficiaryName').val());
					}
					updatePan($('#gstnnumber').val());
					$('#ssupplierLedgerName').val($('#ssupplierLedgerName').val());
					$("#addbilledtoname").hide();
					$('#invaddcst').removeClass('btn-loader-blue');
					$('#invoiceModal').css("z-index","");
					$('#addCustomerModal').modal('hide');
					$('.modal-backdrop.show').css("display","none");
					$("#customerForm")[0].reset();
				},
				error: function(data) {
					$('#invaddcst').removeClass('btn-loader-blue');
					$("#addbilledtoname").hide();
					$("#customerForm")[0].reset();
				}
			});
			$('.business').attr('checked','checked');
		}else{
			$('#invaddcst').removeClass('btn-loader-blue');
		}	
	}
	
function saveLedger(type) {
	$('.errormsg').css('display','none');
	$('.with-errors').html('');
	$('.form-group').removeClass('has-error has-danger');
	var invldgrName = $('#addledgername').val()
	var rowno = $('#ledger_rowno').val();
	if($('#addledgerOpeningBalance').val() == ""){
		$('#addledgerOpeningBalance').val(0);
	}
	if(addledgerValidation(type)){
		$.ajax({
			type: "POST",
			url: $("#ledgerForm").attr('action'),
			data: $("#ledgerForm").serialize(),
			success: function(data) {
				$("#addLedgerModal").hide();
				$('#invoiceModal').css("z-index","");
				$('#addLedgerModal').modal('hide');
				$('#ledgerName').val($('#addledgername').val());
				//$('.modal-backdrop.show').css("display","none");
				$("#ledgerForm")[0].reset();
				$('#ledgerName').val(invldgrName);
				$('#voucherledger_text'+rowno).val(invldgrName);
				$('#contraledger_text'+rowno).val(invldgrName);
			},
			error: function(data) {
				$("#addGroupNameempty").hide();
				$("#ledgerForm")[0].reset();
			}
		});
	}
}

function validateItem(){  
	var priceValidation = /^[0-9,]+(\.[0-9]{1,2})?$/;
	var itemNumber = $('#itemNumber').val();
	var itemDescription = $('#itemDescription').val();
	var unit = $('#unit').val();
	var recommendedSellingPriceForB2B = $('#recommendedSellingPriceForB2B').val();
	var recommendedSellingPriceForB2C = $('#recommendedSellingPriceForB2C').val();
	var ItemHSNCode = $('#ItemHSNCode').val();
	var saftlyStockLevel = $('#saftlyStockLevel').val();
	itemDescription = itemDescription.replace(/ +/g, "");
	ItemHSNCode = ItemHSNCode.replace(/ +/g, "");
	var err = 0;
	var c = 0;  
	if(itemNumber==""){
		$('#itemNumber_Msg').text("Please enter Item Number/Code"); 
		c++;
	}else{  
		$('#itemNumber_Msg').text(""); 
	}
		if(itemDescription==""){
			$('#productDescription_Msg').text("Please enter Item Description");
			c++;
		}else{
			$('#productDescription_Msg').text("");
		}
		
		if(unit==""){
			$('#unitofMeasurement_Msg').text("Please enter the Unit of Measurement");
			c++;
		}else{
			$.ajax({
				url: contextPath+"/validuqcconfig?query="+ unit + "&format=json",
				async: false,
				contentType: 'application/json',
				success : function(response) {
					if(response.length == 0){
						err=1;
					}
				},error : function(err){
				}
			});
		}
		
		if(err != 0){
			$('#unitofMeasurement_Msg').text("Please enter valid UQC");
			c++;
		}
	
/*	if(recommendedSellingPriceForB2B==""){
		$('#recommendedSellingPriceForB2B_Msg').text("Please enter numeric value");
		c++;
	}else if(!priceValidation.test(recommendedSellingPriceForB2B)){
			$('#recommendedSellingPriceForB2B_Msg').text("Please enter numeric value");
			c++;
		}else{
		$('#recommendedSellingPriceForB2B_Msg').text("");
	}
	if(recommendedSellingPriceForB2C==""){
		$('#recommendedSellingPriceForB2C_Msg').text("Please enter numeric value");
		c++;
	}else if(!priceValidation.test(recommendedSellingPriceForB2C)){
			$('#recommendedSellingPriceForB2C_Msg').text("Please enter numeric value");
			c++;
		}else{
		$('#recommendedSellingPriceForB2C_Msg').text("");
	}*/
	if(ItemHSNCode==""){
		$('#HSNSACCode_Msg').text("Please enter HSN/SAC Code");
		c++;
	}else{
		$('#HSNSACCode_Msg').text("");
	}
	/*if(saftlyStockLevel==""){
		$('#saftlyStockLevel_Msg').text("Please enter the value for Safety Stock Level");
		c++;
	}else{
		$('#saftlyStockLevel_Msg').text("");
	}*/
	return c==0; 
}

	function saveItem() {
		$('#invadditem').addClass('btn-loader-blue');
		if(validateItem()){
			$('.form-group').removeClass('has-error has-danger');
			var inyType5 = $('#idInvType').val();
			var rowno = $('.rowno').val();
			var sb2b = $('#recommendedSellingPriceForB2B').val();
			var sb2c = $('#recommendedSellingPriceForB2C').val();
			/*if(sb2b != ''){
				sb2b = sb2b.replace(/,/g , '');
				$('#recommendedSellingPriceForB2B').val(sb2b);
			}*/
			$.ajax({
				type: "POST",
				url: $("#itemForm").attr('action'),
				data: $("#itemForm").serialize(),
				success: function(data) {
					$("#additemno").hide();
					$("#addItemModal").hide();
					$('#product_text'+rowno).val($('#itemDescription').val());
					$('#hsn_text'+rowno).val($('#ItemHSNCode').val());
					$('#uqc_text'+rowno).val($('#unit').val());
					$('#rate_text'+rowno).val($('#itemsalePrice').val());
					$('#taxrate_text'+rowno).val($('#taxrate_text').val());
					$('#discount_text'+rowno).val($('#Discount').val());
					$('#exempted_text'+rowno).val($('#itemexmeptedvalue').val());
					$('#qty_text'+rowno).val(1).focus();
					$('#taxableamount_text'+rowno).val('');
					$('#igsttax_text'+rowno).val('');
					$('#sgsttax_text'+rowno).val('');
					$('#cgsttax_text'+rowno).val('');
					$('#total_text'+rowno).val('');
					$('#cessamount_text'+rowno).val('');
					$('#cessrate_text'+rowno).val(0);
					$('#opening_stock'+rowno).val($('#itemopeningStockLevel').val());
					$('#saftey_stock'+rowno).val($('#itemsaftlyStockLevel').val());
					$('#itemCustomField_text1'+rowno).val($('#item_CustomField1aItem').val());
	    			$('#itemCustomField_text2'+rowno).val($('#item_CustomField2aItem').val());
	    			$('#itemCustomField_text3'+rowno).val($('#item_CustomField3aItem').val());
	    			$('#itemCustomField_text4'+rowno).val($('#item_CustomField4aItem').val());
	    			$('#itemId_text'+rowno).val(data);
	    			$('#itemnotes_text'+rowno).val($('#itemCommentss').val());
					if(inyType5 == 'Nil Supllies'){
						findNillTaxAmount(rowno);
					}else{
						findTaxableValue(rowno);
					}
					if(inyType5 == "Advances"){
						findHsnOrSac(rowno);
					}
					var hsnsacdata = $("#ItemHSNCode").val();
							if(itcStateFlag){
								$.ajax({
									url: contextPath+"/hsnOrSacData?query="+ hsnsacdata + "&format=json",
									async: false,
									contentType: 'application/json',
									success : function(response) {
										if(response){
											$('#itctype_text'+rowno).val('ip');
										}else{
											$('#itctype_text'+rowno).val('is');
										}
										document.getElementById('itcpercent_text'+rowno).value=100;
										findITCValue(rowno);
									}
								});
							}
						$('#invadditem').removeClass('btn-loader-blue');
						$('#invoiceModal').css("z-index","");
						$('.modal-backdrop.show').css("display","none");
					$("#itemForm")[0].reset();
				},
				error: function(data) {
					$('#invadditem').removeClass('btn-loader-blue');
					$('#invoiceModal').css("z-index","");
					$('.modal-backdrop.show').css("display","none");
					$("#itemForm")[0].reset();
					$("#addItemModal").hide();
				}
			});
		}else{
			$('#invadditem').removeClass('btn-loader-blue');
		}
	}
	function initializePopupAddr() {
		var billingAddress = document.getElementById('billingAddress');
		var billing = new google.maps.places.Autocomplete(billingAddress);
		var shippingAddress = document.getElementById('shippingAddress');
		var shipping = new google.maps.places.Autocomplete(shippingAddress);
		var billingAddress1 = document.getElementById('billingAddress1');
		var billing1 = new google.maps.places.Autocomplete(billingAddress1);
		var shippingAddress1 = document.getElementById('shippingAddress1');
		var shipping1 = new google.maps.places.Autocomplete(shippingAddress1);
		var billingAddress2 = document.getElementById('billingAddress2');
		var billing2 = new google.maps.places.Autocomplete(billingAddress2);
		var shippingAddress2 = document.getElementById('shippingAddress2');
		var shipping2 = new google.maps.places.Autocomplete(shippingAddress2);
		var billingAddress3 = document.getElementById('billingAddress3');
		var billing3 = new google.maps.places.Autocomplete(billingAddress3);
		var shippingAddress3 = document.getElementById('shippingAddress3');
		var shipping3 = new google.maps.places.Autocomplete(shippingAddress3);
		var billingAddress4 = document.getElementById('billingAddress4');
		var billing4 = new google.maps.places.Autocomplete(billingAddress4);
		var shippingAddress4 = document.getElementById('shippingAddress4');
		var shipping4 = new google.maps.places.Autocomplete(shippingAddress4);
		var isdbillingAddress = document.getElementById('isdbillingAddress');
		var isdbilling = new google.maps.places.Autocomplete(isdbillingAddress);
	}
	
function updateAdvRateType(clientState,advState,returntype) {
		if(returntype != 'GSTR2'){
			if(advState && clientState) {
				var stateCode = advState;
				if(stateCode) {
					if(stateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+stateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									stateCode = states[0].code;
								}
							}
						});
					}
					if(clientState.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+clientState,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									clientState = states[0].code;
								}
							}
						});
					}
					if(stateCode == clientState) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}
		}else{
			var custgstin = document.getElementById('billedtogstin');
			var placeOfSupply = document.getElementById('billedtostatecode');
			var gstin = custgstin.value;
			if(gstin && placeOfSupply) {
				var statecode = gstin.substring(0, 2);
				var billedStateCode = placeOfSupply.value;
				if(billedStateCode) {
					if(billedStateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+billedStateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									billedStateCode = states[0].tin;
								}
							}
						});
					}
					if(statecode == billedStateCode) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}else{
				interStateFlag=true;
			}
			ratetype(placeOfSupply,clientState)	
		}
		var i=1;
		if(!itcStateFlag){
			$('.itcval').val('no');
		}
		$.each($("#allinvoice tr"),function() { 
    findTaxAmount(i);
	i++;
  })
	}
	
	function pupdateRateType(clientState,returntype,billedState) {
		
		if(returntype == 'Purchase Register'){
			returntype = 'GSTR2';
		}
    	var invType = $('#idInvType').val();
		//var billedState = document.getElementById('billedtostatecode');
		if(returntype != 'GSTR2'){
			if(billedState != "" && clientState) {
				var stateCode = billedState;
				if(stateCode) {
					if(stateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+stateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									stateCode = states[0].code;
								}
							}
						});
					}
					if(clientState.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+clientState,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									clientState = states[0].code;
								}
							}
						});
					}
					if(stateCode == clientState) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}
		}else{
			var custgstin = document.getElementById('billedtogstin');
			var placeOfSupply = billedState;
			var gstin = custgstin.value;
			if(gstin && billedState != "") {
				var statecode = gstin.substring(0, 2);
				var billedStateCode = billedState;
				if(billedStateCode) {
					if(billedStateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+billedStateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									billedStateCode = states[0].tin;
								}
							}
						});
					}
					if(statecode == billedStateCode) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}else{
				if(invType == 'B2B Unregistered' || invType == 'B2B'){
					var suptype = $('#printerintra').val();
					if(suptype == 'Inter'){
						interStateFlag=false;
					}else{
						interStateFlag=true;
					}
				}else{
					interStateFlag=true;
				}
			}
			if(invType == 'Import Services' || invType == 'Import Goods'){
				interStateFlag=false;
			}
			pratetype(billedState,clientState);	
		}
		var i=1;
		if(returntype != 'GSTR2'){
			if(invType != 'Nil Supplies'){
				$.each($("#allinvoice tr"),function() {
					findTaxAmount(i);
					findCessAmount(i);
					i++;
				});
			}
		}else{
			if(invType != 'Nil Supplies'){
				$.each($("#allinvoice tr"),function() {
					findTaxAmount(i);
					if(invType != 'Advance Adjusted Detail' && invType != 'TXPA'){
						findCessAmount(i);
					}
					i++;
				});
			}
		}
	}
	
	function pratetype(billedState,clientState){
		var invtype = $('#idInvType').val();
		if(billedState != "" && clientState) {
				var stateCode = billedState;
				if(stateCode) {
					if(stateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+stateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									stateCode = states[0].code;
								}
							}
						});
					}
					if(clientState.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+clientState,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									clientState = states[0].code;
								}
							}
						});
					}
					if(invtype != 'ITC Reversal'){
						if(stateCode == clientState) {
							itcStateFlag=true;
						} else {
							itcStateFlag=false;
						}
					}
				}
			}
	}
	
	function updateRateType(clientState,returntype) {
		if(returntype == 'Purchase Register'){
			returntype = 'GSTR2';
		}
    	var invType = $('#idInvType').val();
		var billedState = document.getElementById('billedtostatecode');
		if(returntype != 'GSTR2'){
			if(billedState && clientState) {
				var stateCode = billedState.value;
				if(stateCode) {
					if(stateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+stateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									stateCode = states[0].code;
								}
							}
						});
					}
					if(clientState.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+clientState,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									clientState = states[0].code;
								}
							}
						});
					}
					if(stateCode == clientState) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}
		}else{
			var custgstin = document.getElementById('billedtogstin');
			var placeOfSupply = document.getElementById('billedtostatecode');
			var gstin = custgstin.value;
			if(gstin && billedState) {
				var statecode = gstin.substring(0, 2);
				var billedStateCode = billedState.value;
				if(billedStateCode) {
					if(billedStateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+billedStateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									billedStateCode = states[0].tin;
								}
							}
						});
					}
					if(statecode == billedStateCode) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}else{
				if(invType == 'B2B Unregistered' || invType == 'B2B'){
					var suptype = $('#printerintra').val();
					if(suptype == 'Inter'){
						interStateFlag=false;
					}else{
						interStateFlag=true;
					}
				}else{
					interStateFlag=true;
				}
			}
			if(invType == 'Import Services' || invType == 'Import Goods'){
				interStateFlag=false;
			}
			ratetype(billedState,clientState);	
		}
		var i=1;
		if(returntype != 'GSTR2'){
			if(invType != 'Nil Supplies'){
				$.each($("#allinvoice tr"),function() {
					findTaxAmount(i);
					findCessAmount(i);
					i++;
				});
			}
		}else{
			if(invType != 'Nil Supplies'){
				$.each($("#allinvoice tr"),function() {
					findTaxAmount(i);
					if(invType != 'Advance Adjusted Detail' && invType != 'TXPA'){
						findCessAmount(i);
					}
					i++;
				});
			}
		}
	}
	function ratetype(billedState,clientState){
		var invtype = $('#idInvType').val();
		if(billedState && clientState) {
				var stateCode = billedState.value;
				if(stateCode) {
					if(stateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+stateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									stateCode = states[0].code;
								}
							}
						});
					}
					if(clientState.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+clientState,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									clientState = states[0].code;
								}
							}
						});
					}
					if(invtype != 'ITC Reversal'){
						if(stateCode == clientState) {
							itcStateFlag=true;
						} else {
							itcStateFlag=false;
						}
					}
				}
			}
	}
	function updateInvType(){
		var retType = $('#retType').val();
		var invType = $('#invTyp').val();
		var exportType = $("#exportType").val();
		var cdnurtyp = $("cdnurtyp").val();
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'CBW' ||  exportType == 'WPAY') {
			interStateFlag=false;
		}else if(invType == 'R'){
			
			if(retType == 'Purchase Register'){
				retType = 'GSTR2';
			}
	    	var invType = $('#idInvType').val();
			var billedState = document.getElementById('billedtostatecode');
			if(retType != 'GSTR2'){
				if(billedState && clntStatename) {
					var stateCode = billedState.value;
					var clientState = clntStatename;
					if(stateCode) {
						if(stateCode.length > 2) {
							$.ajax({
								url: contextPath+"/stateconfig?query="+stateCode,
								async: false,
								cache: false,
								dataType:"json",
								contentType: 'application/json',
								success : function(states) {
									if(states.length == 1) {
										stateCode = states[0].code;
									}
								}
							});
						}
						if(clientState.length > 2) {
							$.ajax({
								url: contextPath+"/stateconfig?query="+clientState,
								async: false,
								cache: false,
								dataType:"json",
								contentType: 'application/json',
								success : function(states) {
									if(states.length == 1) {
										clientState = states[0].code;
									}
								}
							});
						}
						if(stateCode == clientState) {
							interStateFlag=true;
						} else {
							interStateFlag=false;
						}
					}
				}
			}else{
				var custgstin = document.getElementById('billedtogstin');
				var placeOfSupply = document.getElementById('billedtostatecode');
				var gstin = custgstin.value;
				if(gstin && billedState) {
					var statecode = gstin.substring(0, 2);
					var billedStateCode = billedState.value;
					if(billedStateCode) {
						if(billedStateCode.length > 2) {
							$.ajax({
								url: contextPath+"/stateconfig?query="+billedStateCode,
								async: false,
								cache: false,
								dataType:"json",
								contentType: 'application/json',
								success : function(states) {
									if(states.length == 1) {
										billedStateCode = states[0].tin;
									}
								}
							});
						}
						if(statecode == billedStateCode) {
							interStateFlag=true;
						} else {
							interStateFlag=false;
						}
					}
				}else{
					if(invType == 'B2B Unregistered' || invType == 'B2B'){
						var suptype = $('#printerintra').val();
						if(suptype == 'Inter'){
							interStateFlag=false;
						}else{
							interStateFlag=true;
						}
					}else{
						interStateFlag=true;
					}
				}
				if(invType == 'Import Services' || invType == 'Import Goods'){
					interStateFlag=false;
				}
				ratetype(billedState,clientState);	
			}
		}
		if(invType == 'SEWOP' || exportType == 'WOPAY'){
			if(clntlutNumber != ''){
				$('#lutDiv').css("display","block");
				$('#lutNo').val(clntlutNumber);
			}else{
				$('#lutDiv').css("display","none");
				$('#lutNo').val('');
			}
			$("#includetax").prop("disabled",true);
			$(".taxrate_textDisable,.cessval").prop('disabled',true);
		}else{
			$('#lutDiv').css("display","none");
			$('#lutNo').val('');
			$("#includetax").prop("disabled",false);
			$(".taxrate_textDisable,.cessval").prop('disabled',false);
		}
		if(invType == 'R' || invType == 'DE'){
			$(".taxrate_textDisable,.cessval").prop('disabled',false);
			$(".taxrate_textDisable,.cessval").prop('readonly',false);
		}
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'SEWOP'){
		$('#gstin_lab').addClass('astrich');
		$('#billedtogstin').attr("required","true");
		}else{
			$('#gstin_lab').removeClass('astrich');
			$('#billedtogstin').removeAttr("required");
		}
		var i=1;
		$.each($("#allinvoice tr"),function() { 
		    findTaxAmount(i);
		    findCessAmount(i);
			i++;
		});
	}
	
	function updateDocType(){
		var docType = $('#ntty').val();
		var retType = $('#retType').val();
		if(docType == 'C'){
			$('.invoiceNumberText').text("Credit Note.No");
			$('.invoiceDateText').text("Credit Note Date(DD/MM/YYYY)");
			if(retType == 'GSTR1' || retType == 'SalesRegister'){
				$('.ledgerType').text('(Debit)');
				$('.ledgerTypep').text('Debited');
			}else{
				$('.ledgerType').text('(Credit)');
				$('.ledgerTypep').text('Credited');
			}
		}else if(docType == 'D'){
			$('.invoiceNumberText').text("Debit Note.No");
			$('.invoiceDateText').text("Debit Note Date(DD/MM/YYYY)");
			if(retType == 'GSTR1' || retType == 'SalesRegister'){
				$('.ledgerType').text('(Credit)');
				$('.ledgerTypep').text('Credited');
			}else{
				$('.ledgerType').text('(Debit)');
				$('.ledgerTypep').text('Debited');
			}
		}else if(docType == 'R'){
			$('.invoiceNumberText').text("Refund Voucher No");
			$('.invoiceDateText').text("Ref.Voucher Date(DD/MM/YYYY)");
			if(retType == 'GSTR1' || retType == 'SalesRegister'){
				$('.ledgerType').text('(Credit)');
				$('.ledgerTypep').text('Credited');
			}else{
				$('.ledgerType').text('(Debit)');
				$('.ledgerTypep').text('Debited');
			}
		}else{
			$('.invoiceNumberText').text("Invoice Number");
			$('.invoiceDateText').text("Invoice Date(DD/MM/YYYY)");
			if(retType == 'GSTR1' || retType == 'SalesRegister'){
				$('.ledgerType').text('(Credit)');
				$('.ledgerTypep').text('Credited');
			}else{
				$('.ledgerType').text('(Debit)');
				$('.ledgerTypep').text('Debited');
			}
		}
		$('#serialnoofinvoice').val('');
		updateDefaults(docType,retType);
	}
	
	function purchaseTDSOptions(){
		$('#tdssection').append($("<option></option>").attr("value","192A(10)").text("192A"));
		$('#tdssection').append($("<option></option>").attr("value","193(10)").text("193"));
		$('#tdssection').append($("<option></option>").attr("value","194A(10)").text("194A"));
		$('#tdssection').append($("<option></option>").attr("value","194B(30)").text("194B"));
		$('#tdssection').append($("<option></option>").attr("value","194BB(30)").text("194BB"));
		$('#tdssection').append($("<option></option>").attr("value","194C(1)").text("194C"));
		$('#tdssection').append($("<option></option>").attr("value","194D(5)").text("194D"));
		$('#tdssection').append($("<option></option>").attr("value","194DA(2)").text("194DA"));
		$('#tdssection').append($("<option></option>").attr("value","194E(20)").text("194E"));
		$('#tdssection').append($("<option></option>").attr("value","194EE(10)").text("194EE"));
		$('#tdssection').append($("<option></option>").attr("value","194G(5)").text("194G"));
		$('#tdssection').append($("<option></option>").attr("value","194H(5)").text("194H"));
		$('#tdssection').append($("<option></option>").attr("value","194-I(2)").text("194-I"));
		$('#tdssection').append($("<option></option>").attr("value","194-IA(1)").text("194-IA"));
		$('#tdssection').append($("<option></option>").attr("value","194-IB(5)").text("194-IB"));
		$('#tdssection').append($("<option></option>").attr("value","194-IC(10)").text("194-IC"));
		$('#tdssection').append($("<option></option>").attr("value","194J(10)").text("194J"));
		$('#tdssection').append($("<option></option>").attr("value","194LA(10)").text("194LA"));
		$('#tdssection').append($("<option></option>").attr("value","194LBB(10)").text("194LBB"));
		$('#tdssection').append($("<option></option>").attr("value","194LBC(25)").text("194LBC"));
		$('#tdssection').append($("<option></option>").attr("value","194M(5)").text("194M"));
		$('#tdssection').append($("<option></option>").attr("value","194N(2)").text("194N"));
	}
	
	/*function tcstdsoptions(returntype,tcsorTds){
		$('#tdstcssection').children('option').remove();
		$('#tdstcssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
		if(returntype == 'GSTR2' || returntype == 'Purchase Register' || returntype == 'PurchaseRegister'){
			if(tcsorTds == "tcs"){
				$('#tdstcssection').append($("<option></option>").attr("value","206C(1)").text("206C"));
			}
			purchaseTDSOptions();
		}else{
			$('#tdstcssection').append($("<option></option>").attr("value","206C(1)").text("206C"));
		}
	}*/
	
	function updateDefaults(type,returnType) {
		if(returnType == "GSTR1" || returnType == 'ESTIMATES' || returnType == 'DELIVERYCHALLANS' || returnType == 'PurchaseOrder' || returnType == 'PROFORMAINVOICES'){
			
			$('#invNoMissing').css('display','block');
			if(type == 'Credit/Debit Notes'){
				type = 'Credit Note';
			}else if(type == 'Credit/Debit Note for Unregistered Taxpayers'){
				type = 'Credit-Debit Note for Unregistered Taxpayers'
			}else if(type == "D"){
				type = 'Debit Note';	
			}else if(type == "C"){
				type = 'Credit Note';	
			}
		$.ajax({
			url: contextPath+'/invdef'+urlSuffixs+'/'+returnType+'/'+type+'/'+month+'/'+year,
			async: true,
			cache: false,
			success : function(invNo) {
				if(Object.keys(invNo).length != 0){
				if(invNo["InvoiceNumber"] != "") {
					$('#invNoMissing').css('display','none');
					if(type == 'Import Services'){
						$('#serialnoofinvoice1').val(invNo["InvoiceNumber"]);
					}else{
						$('#serialnoofinvoice').val(invNo["InvoiceNumber"]);
						if(invNo["InvoiceNumber"] != "") {
							if(invNo["PreviousInvoiceNumber"] != "") {
								$('.pInvno').css("display","block");
								$('.previousInvoiceNo').html(invNo["PreviousInvoiceNumber"]);
							}else{
								$('.pInvno').css("display","none");
								$('.previousInvoiceNo').html('');
							}
						}else{
							$('.pInvno').css("display","none");
							$('.previousInvoiceNo').html('');
						}
					}
				} 
				}else {
					$('#invNoMissing').css('display','block');
					$('#invNoMissing').html('<a href="'+invConfigUrl+'">Click here </a>to configure your Invoice details.');
					$('#invNoMissing1').html('<a href="'+invConfigUrl+'">Click here </a>to configure your Invoice details.');
					$('.pInvno').css("display","none");
					$('.previousInvoiceNo').html('');
				}
				if(type == "R"){
					$('#serialnoofinvoice').val("");
					$('#invNoMissing').css('display','none');
				}	
			}
		});
		}else{
			$('#invNoMissing').css('display','none');
			$('.pInvno').css("display","none");
			$('.previousInvoiceNo').html('');
		}
		$.ajax({
			url: contextPath+'/bnkdtls'+urlSuffixs,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(bankDetails) {
				clientBankDetails = new Array();
				$('#selectBank').html('').hide();
				$("#selectBank").append($("<option></option>").attr("value","").text("-- Select Bank --")); 
				for (var i=0; i<bankDetails.length; i++) {
					$("#selectBank").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
					clientBankDetails.push(bankDetails[i]);
				}
				if(clientBankDetails && clientBankDetails.length == 1) {
					if(returnType != 'Purchase Register' && returnType != 'PurchaseOrder' && returnType != 'GSTR2' && type != 'Advance Adjusted Detail' && type != 'TXPA' && returnType != 'EWAYBILL' && type != 'Credit Note'){
						$("#bank_details").show();
						$(".addBankDetails").attr("checked","checked");
						$('#selectBank').val(clientBankDetails[0].accountnumber);
						selectBankName(clientBankDetails[0].accountnumber);
					}
					
				}else if(clientBankDetails && clientBankDetails.length > 1){
					if(returnType != 'Purchase Register' && returnType != 'PurchaseOrder' && returnType != 'GSTR2' && type != 'Advance Adjusted Detail' && type != 'TXPA' && returnType != 'EWAYBILL' && type != 'Credit Note'){
						$("#bank_details").hide();
					}
					$('#selectBank').attr("disabled","disabled");
				}
				$("#selectBank").show();
			}
		});
	}
	function updateBankDetails(type,bacctno,bname) {
		$.ajax({
			url: contextPath+'/bnkdtls'+urlSuffixs,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(bankDetails) {
				$('#selectBank').children('option').remove();
				$("#selectBank").append($("<option></option>").attr("value","").text("-- Select Bank --")); 
				clientBankDetails = new Array();
				for (var i=0; i<bankDetails.length; i++) {
					$("#selectBank").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname)); 
					clientBankDetails.push(bankDetails[i]);
				}
				$("#selectBank").show();
				$('.selectBank').val(bacctno);
				if(clientBankDetails && clientBankDetails.length == 1){
					$('#selectBank').attr("disabled","disabled");
				}
			}
		});
	}
	
	function selectBankName(bnknumber) {
		//var bankaccountNumber = $('#selectBank').val();
		$('#bankName,#bankName1').val(' ');
		$('#bankAcctNo,#bankAcctNo1').val(' ');
		$('#bankBranch,#bankBranch1').val(' ');
		$('#bankIFSC,#bankIFSC1').val(' ');
		$('#bankAccountName,#bankAccountName1').val(' ');
		if(bnknumber) {
			clientBankDetails.forEach(function(bankdetail) {
				if(bankdetail.accountnumber == bnknumber) {
					$('#bankName,#bankName1').val(bankdetail.bankname);
					$('#bankAcctNo,#bankAcctNo1').val(bankdetail.accountnumber);
					$('#bankBranch,#bankBranch1').val(bankdetail.branchname);
					$('#bankIFSC,#bankIFSC1').val(bankdetail.ifsccode);
					$('#bankAccountName,#bankAccountName1').val(bankdetail.accountName);
				}
			});
		}else if($('#selectBank').val() !=''){
			var bankaccountNumber = $('#selectBank').val();
			var bankaccountNumber1 = $('#selectBank1').val();
			if(bankaccountNumber){
			clientBankDetails.forEach(function(bankdetail) {
				if(bankdetail.accountnumber == bankaccountNumber) {
					$('#bankName').val(bankdetail.bankname);
					$('#bankAcctNo').val(bankdetail.accountnumber);
					$('#bankBranch').val(bankdetail.branchname);
					$('#bankIFSC').val(bankdetail.ifsccode);
					$('#bankAccountName').val(bankdetail.accountName);
				}
			});
		}
		if(bankaccountNumber1){
			clientBankDetails.forEach(function(bankdetail) {
				if(bankdetail.accountnumber == bankaccountNumber1) {
					$('#bankName1').val(bankdetail.bankname);
					$('#bankAcctNo1').val(bankdetail.accountnumber);
					$('#bankBranch1').val(bankdetail.branchname);
					$('#bankIFSC1').val(bankdetail.ifsccode);
					$('#bankAccountName1').val(bankdetail.accountName);
				}
			});
		}
		}
	}
	
	
	
	 function add_row(clientid, returnType,usrType) {
		 var invoiceLevel_Cess = $('#invLevelCess').val();
		var retType = $('#retType').val();
		var gstno = $('#billedtogstin').val();
		if(retType == 'SalesRegister'){
			retType = 'GSTR1';
		}
		if(retType == 'PurchaseRegister' || retType == 'Purchase Register'){
			retType = 'GSTR2';
		}
		if(returnType == 'Purchase Register'){
			returnType = 'GSTR2';
		}
		var invType1 = $('#invTyp').val();
		var inyType = $('#idInvType3').val();
		var inyType1 = $('#idInvType1').val();
		var inyType2 = $('#idInvType2').val();
		var inyType3 = $('#idInvType4').val();
		var inyType4 = $('#idInvType5').val();
		var inyType5 = $('#idInvType').val();
		
		if(inyType5 == 'Nil Supplies'){
			$.ajax({
				url: contextPath+'/invtypes/'+inyType5+'/'+returnType,
				async: false,
				cache: false,
				success : function(invTypes) {
					invtyperule = invTypes;}
			});
		}
		if(inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA'){
			addAdvFlag = true;
			if(returnType == 'GSTR2'){
				addITCFlag = false;
			}
		}else{
			addAdvFlag = false;
			if(returnType == 'GSTR2'){
				if(inyType5 != 'Advances'){
					addITCFlag = true;
				}else{
					addITCFlag = false;
				}
			}
		}
		if(inyType5 == 'Nil Supplies'){
			addITCRuleFlag = true;
			addITCFlag = false;
			if(inyType5 == 'Nil Supplies'){
				SnilFlag = false;
			}
		}else{
			if((returnType == 'GSTR2') && (inyType5 == 'B2B' || inyType5 == 'B2B UnRegistered'  || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'B2B Unregistered' || inyType5 == 'Import Goods' || inyType5 == 'Import Services')){
				addITCFlag = true;
			}else if((returnType == 'GSTR1') && (inyType5 == 'B2B' || inyType5 == 'B2C' || inyType5 == 'B2CL' || inyType5 == 'B2B UnRegistered'  || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'B2B Unregistered' || inyType5 == 'Import Goods' || inyType5 == 'Import Services')){
				addITCFlag = false;
			}else if(returnType == 'GSTR2A'){
				addITCFlag = false;
			}
			SnilFlag = true;
			addITCRuleFlag = false;
		}
		var invType = $('#idInvType').val();
		if(invType == 'Exports'){
			type = 'exp';
		}else if(retType == 'GSTR1' && (invType == 'Advance Adjusted Detail' || invType == 'TXPA'|| invType == 'Advances')){
			type = 'adv';
		}else if((retType == 'GSTR2' || 'Purchase Register') && (invType == 'Advance Adjusted Detail' || invType == 'TXPA' || invType == 'Advances')){
			type = 'padv';
		}else{
			type = 'remain';
		}
		rowCount++;
		var table = null;
		var rowPrefix = null;
		var row = null;
		var table_len=rowCount;
	  if(table_len == '1'){
			$('.item_delete').hide();
		}else{
			$('.item_delete').show();	
		}
		if(invType1 == 'DE'){
			$("#taxrate_text"+table_len).removeAttr("disabled");
		}
		if(inyType5 == 'Nil Supplies'){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0' data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' value=''><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findNillTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity'/></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' onKeyUp='findNillTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td>";
		}
		if(inyType5 == 'Nil Supplies' && (retType == 'GSTR1')){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"'  placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' value=''><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findNillTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity'/></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' onKeyUp='findNillTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' class='exemp_td form-control exemptFlag text-right' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findNillTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td>";
		}
		if(inyType5 == 'Advances'){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate'/></td><td align='right' id='taxableamount_row"+table_len+"' class='form-group tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")'><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td>";
		}
		if((returnType != 'GSTR2A') && (inyType5 == 'CDNA' || inyType5 == 'CDNURA' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers')){
			table = document.getElementById("sortable_table");
			if(inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers'){
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='uqc_text"+table_len+"'  placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' value=''><div id='uqc_textempty' class='"+type+""+retType+"ddbox4"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' required onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval lksdjlks' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=-1>Nil Rated</option><option value=-2>Exempted</option><option value=-3>Non-GST</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td>";
			}else{
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='uqc_text"+table_len+"'  placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' value=''><div id='uqc_textempty' class='"+type+""+retType+"ddbox4"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' required onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td>";
			}
		}
		if((returnType != 'GSTR2A') && (returnType == 'GSTR1') && (inyType5 == 'CDNA' || inyType5 == 'CDNURA' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers')){
			table = document.getElementById("sortable_table");
			if(inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers'){
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='uqc_text"+table_len+"'  placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' value=''><div id='uqc_textempty' class='"+type+""+retType+"ddbox4"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' required onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=-1>Nil Rated</option><option value=-2>Exempted</option><option value=-3>Non-GST</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right cessFlag' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td>";
			}else{
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service' value=''></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' required id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))'><div id='itemcodeempty' style='display:none' class='"+type+""+retType+"ddbox3"+table_len+"'><div class='"+type+""+retType+"ddbox3'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='uqc_text"+table_len+"'  placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' value=''><div id='uqc_textempty' class='"+type+""+retType+"ddbox4"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' required onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'  value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem' value='' onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue' value='' readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places' value='' /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' value='' readonly></td>";
			}
		}
		if((returnType == 'GSTR2A') && (inyType5 == 'CDNA' || inyType5 == 'CDNURA' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers')){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc'></td><td align='right' id='qty_row"+table_len+"' class='form-group'></td><td align='right' id='rate_row"+table_len+"' class='form-group'></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'></td><td align='right' id='igsttax_row"+table_len+"' class='tablegreybg'></td><td align='right' id='cgsttax_row"+table_len+"' class='tablegreybg'></td><td align='right' id='sgsttax_row"+table_len+"' class='tablegreybg'></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'>0</td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'></td>";
		}
		
		if((returnType != 'EWAYBILL') && (returnType != 'GSTR2A') && (retType != 'GSTR1') &&  (inyType5 == 'B2BA' || inyType5 == 'B2CSA' || inyType5 == 'B2CLA' || inyType5 == 'ATA' || inyType5 == 'TXPA' || inyType5 == 'EXPA' || inyType5 == 'B2B' || inyType5 == 'B2C' || inyType5 == 'B2CL'  || inyType5 == 'Exports' || inyType5 == 'Advance Adjusted Detail' || (returnType == 'GSTR2' && inyType5 == 'B2B Unregistered') || (returnType == 'GSTR2' && inyType5 == 'Import Goods') || (returnType == 'GSTR5' && inyType5 == 'Import Goods') || (returnType == 'GSTR4' && inyType5 == 'Import Services') || (returnType == 'GSTR2' && inyType5 == 'Import Services') || (returnType == 'GSTR4' && inyType5 == 'B2B Unregistered'))){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' placeholder='Item/Product/Service' name='items["+(table_len-1)+"].itemno'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn'  onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg text-right'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";			
		}
		if(returnType == 'EWAYBILL'){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn'  onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg text-right'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")'><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";			
		}
		if((returnType != 'GSTR2A') && (retType == 'GSTR1') &&  (inyType5 == 'B2BA' || inyType5 == 'B2CSA' || inyType5 == 'B2CLA' || inyType5 == 'ATA' || inyType5 == 'TXPA' || inyType5 == 'EXPA' || inyType5 == 'B2B' || inyType5 == 'B2C' || inyType5 == 'B2CL' || inyType5 == 'Exports' || (returnType == 'GSTR2' && inyType5 == 'B2B Unregistered') || (returnType == 'GSTR2' && inyType5 == 'Import Goods') || (returnType == 'GSTR5' && inyType5 == 'Import Goods') || (returnType == 'GSTR4' && inyType5 == 'Import Services') || (returnType == 'GSTR2' && inyType5 == 'Import Services') || (returnType == 'GSTR4' && inyType5 == 'B2B Unregistered'))){
			table = document.getElementById("sortable_table");
			if(inyType5 == 'B2B' && gstno == ''){
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn'  onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)'  class='form-control qtyval negativevalues text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)' class='form-control input_rate_txt rateval negativevalues text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";			
			}else if(inyType5 == 'B2C'){
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn'  onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)'  class='form-control qtyval negativevalues text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)' class='form-control input_rate_txt rateval negativevalues text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";			
			}else{
				rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' placeholder='HSN/SAC' onchange='findHsnOrSac("+table_len+")' name='items["+(table_len-1)+"].hsn'  onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval negativevalues text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval negativevalues text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  value='' placeholder='disper'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";			
			}
		}
		if((returnType == 'GSTR2A') && (inyType5 == 'B2B' || inyType5 == 'B2BA' || inyType5 == 'B2C'  || inyType5 == 'Exports' || inyType5 == 'Advance Adjusted Detail' || inyType5 == 'B2B Unregistered' || inyType5 == 'Import Goods' || inyType5 == 'Import Services')){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control' id='product_text"+table_len+"' placeholder='Item/Product/Service'/></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control' id='hsn_text"+table_len+"' placeholder='HSN/SAC'/></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc'><input type='text' class='form-control' id='uqc_text"+table_len+"' placeholder='UQC'/></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='text' class='form-control' id='qty_text"+table_len+"'/></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' class='form-control' id='rate_text"+table_len+"'/></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"'/><input type='text' class='form-control' id='discount_text"+table_len+"'/></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control' id='taxableamount_text"+table_len+"' readonly/></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select class='form-control' id='taxrate_text"+table_len+"'></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown' id='abb"+table_len+"' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' class='form-control' id='cessrate_text"+table_len+"'/ ></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text' class='form-control cessamtval' id='cessamount_text"+table_len+"' readonly/></td>";
		}
		if(addITCFlag) {
			if(inyType5 == 'B2B' || inyType5 == 'B2C' || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'Advance Adjusted Detail' || (returnType == 'GSTR2' && inyType5 == 'B2B Unregistered') || inyType5 == 'Import Goods' || inyType5 == 'Import Services' || (returnType == '' && inyType5 == 'B2B Unregistered')){
				rowPrefix += '<td id="itctype_row'+table_len+'" class="form-group"><select id="itctype_text'+table_len+'" class="form-control itcval" onchange="updateEligibity(this.value, '+table_len+')" name="items['+(table_len-1)+'].elg"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Service</option><option value="no">Ineligible</option><option value="pending">Pending</option></select></td><td id="itcpercent_row'+table_len+'" class="form-group"><input type="text" class="form-control itc_percent text-right" id="itcpercent_text'+table_len+'" name="items['+(table_len-1)+'].elgpercent" value="" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" onKeyUp="findITCValue('+table_len+')" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter numeric value with a max precision of 2 decimal places"/></td><td id="igstitc_row'+table_len+'" class="tablegreybg" align="right"><input type="text" class="form-control dropdown text-right" id="itc_tax_tot'+table_len+'" name="items['+(table_len-1)+'].totitval"  readonly><div id="itctax_rate_drop pb-0" style="display:none"><span id="icon-drop1"></span><h6 class="text-center mb-2">ITC Amount</h6><div class="row pl-3"><p class="mr-3 mb-0" style="height:25px;">IGST :</p><span style="height:25px;"><input type="text" class="form-control dropdown" id="igstitc_text'+table_len+'" name="items['+(table_len-1)+'].igstavltax" style="border:none;width: 70px;"></span></div><div class="row pl-3"><p class="mr-3 mb-0" style="height:25px;">CGST :</p><span style="height:25px;"><input type="text" class="form-control"  id="cgstitc_text'+table_len+'" name="items['+(table_len-1)+'].cgstavltax" style="border:none;width:65px;"></span></div><div class="row pl-3"><p class="mr-3 mb-0">SGST :</p><span style="height:25px;"><input type="text" class="form-control" id="sgstitc_text'+table_len+'" name="items['+(table_len-1)+'].sgstavltax" style="border:none;width: 67px;"></span></div><div class="row pl-3"><p class="mr-3 mb-2" style="height: 25px;">CESS :</p><span style="height: 25px;"><input type="text" class="form-control" id="cessitc_text'+table_len+'" name="items['+(table_len-1)+'].cessavltax" style="border:none;width: 60px;"></span></div></div></td>';
			}
		}if(inyType5 == 'DELIVERYCHALLANS' || inyType5 == 'PROFORMAINVOICES' || inyType5 == 'ESTIMATES'){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' name='items["+(table_len-1)+"].hsn' placeholder='HSN/SAC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='exempted_row"+table_len+"'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control text-right' id='exempted_text"+table_len+"' name='items["+(table_len-1)+"].exmepted'  onKeyUp='findTaxableValue("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,10})?$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='Exempted' placeholder='Exempted' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";
		}
		if(inyType5 == 'PurchaseOrder'){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span><span id='sno_row1'>"+table_len+"</span></td><td align='left' id='product_row"+table_len+"' class='form-group product_notes'><div class='col-md-12 p-0'><input type='text' class='form-control input_itemDetails_txt itemDetails itemname"+table_len+"' id='product_text"+table_len+"' name='items["+(table_len-1)+"].itemno' placeholder='Item/Product/Service'></div><div id='"+type+"product_textempty"+table_len+"' style='display:none' class='additem_box'><div class='dbbox permissionSettings-Items-Add "+type+"ddbox"+table_len+"'><p>Please add new item</p><input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames("+table_len+")'></div></div><div class='dropdown dropdown-search1 col-md-1 p-0'data-toggle='tooltip' title='Click on Icon C to enter additional details of your Item/Product/Service'><span class='itemnote_info_icon' id='dropdownMenuitemdec' onclick='showAdditionalItemFieldsPopup("+table_len+")'><i><b>C</b></i></span><div class='modal-d modal-arrow"+table_len+"' style='display:none;'></div></div></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td align='left' data ='' id='hsn_row"+table_len+"' class='form-group invoiceHSN'><input type='text' class='form-control hsnval' id='hsn_text"+table_len+"' name='items["+(table_len-1)+"].hsn' placeholder='HSN/SAC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class='"+type+""+retType+"ddbox3"+table_len+"' style='display:none'><div class='"+type+""+retType+"ddbox3' id='"+type+""+retType+"ddbox3"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td align='left' id='uqc_row"+table_len+"' class='form-group invoiceUqc uqc_row"+table_len+"'><input type='text' class='form-control uqcDetails uqcname1 uqcval' required id='uqc_text"+table_len+"' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items["+(table_len-1)+"].uqc' ><div id='uqc_textempty' style='display:none' class='"+type+""+retType+"ddbox4"+table_len+"'><div class='"+type+""+retType+"ddbox4'><p>Search didn't return any results.</p></div></div></td><td align='right' id='qty_row"+table_len+"' class='form-group'><input type='hidden' id='opening_stock"+table_len+"'/><input type='hidden' id='saftey_stock"+table_len+"'/><input type='text' onchange='changeStock("+table_len+")' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)'  class='form-control qtyval text-right' required id='qty_text"+table_len+"' name='items["+(table_len-1)+"].quantity'   onKeyUp='findTaxableValue("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='quantity' placeholder='Quantity' /></td><td align='right' id='rate_row"+table_len+"' class='form-group'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' class='form-control input_rate_txt rateval text-right' id='rate_text"+table_len+"' name='items["+(table_len-1)+"].rateperitem'  onKeyUp='findTaxableValue("+table_len+")' pattern='^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='rate' placeholder='Rate' /></td><td align='right' id='discount_row"+table_len+"'><input type='hidden' class='form-control' id='disper"+table_len+"' name='items["+(table_len-1)+"].disper'  placeholder='disper'  value=''><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' class='form-control disval text-right' id='discount_text"+table_len+"' name='items["+(table_len-1)+"].discount'  onKeyUp='findTaxableValue("+table_len+")' pattern='[0-9]+(\\.[0-9]+)?[%]?' data-error='Please enter numeric value with a max precision of 2 decimal places' aria-describedby='discount' placeholder='25 or 25%' /></td><td align='right' id='taxableamount_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control indformat text-right' id='taxableamount_text"+table_len+"' name='items["+(table_len-1)+"].taxablevalue'  readonly></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST<span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";
		}
		if((returnType != 'GSTR2A') && ( inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA')){
			table = document.getElementById("sortable_table");
			rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow added_row' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='sno_row1'>"+table_len+"</span></td><td class='form-group item_ledger' align='left' id='ledger_row"+table_len+"'> <input type='text' class='form-control ledgerval' style='padding-right:0px!important;width:95%' id='ledger"+table_len+"' name='items["+(table_len-1)+"].ledgerName' placeholder='ledger' value=''><div id='addledgername"+table_len+"' style='display:none'><div class='ledgerddbox"+table_len+"'><p id='newledger'>Please add new Ledger</p><input type='button' class='btn btn-sm btn-blue-dark' id='newledgerval' value='Add New Ledger' data-toggle='modal' onclick='addItemLedger("+table_len+")'></div></div></td><td id='advrecienptno_row"+table_len+"' class='addAdvFlag form-group' align='right' ><input type='text' class='form-control advrcnoval' required id='advrcno_text"+table_len+"' name='items["+(table_len-1)+"].advReceiptNo' maxlength='16' pattern='^[0-9a-zA-Z/ -]+$' data-error='Please enter valid invoice number' style='padding-right:0px!important' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))'><div id='advrcno_textempty"+table_len+"' style='display:none' class='"+type+"paymentddbox3"+table_len+"'><div class='"+type+"paymentddbox"+table_len+"'><p>Search didn't return any results.</p></div></div></td><td id='advrecienptdt_row"+table_len+"' class='addAdvFlag form-group' align='right' ><input type='text' class='form-control advrcdtval' required id='advrcdt_text"+table_len+"' name='items["+(table_len-1)+"].advReceiptDate' readonly></td><td id='advrecienptpos_row"+table_len+"' class='addAdvFlag form-group' align='right' ><input type='text' class='form-control advrcposval' required id='advrcposs_text"+table_len+"' name='items["+(table_len-1)+"].advStateName' readonly></td><td id='advrecienptamt_row"+table_len+"' class='addAdvFlag form-group' align='right' ><input type='text' class='form-control advrcamtval text-right' required id='advrcamt_text"+table_len+"' name='items["+(table_len-1)+"].advReceivedAmount' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' readonly></td><td id='advrecienptavail_row"+table_len+"' class='addAdvFlag form-group' align='right' ><input type='text' class='form-control advrcavailval text-right' required id='advrcavail_text"+table_len+"' name='items["+(table_len-1)+"].advAdjustableAmount' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' readonly></td><td id='advrecienptavailadj_row"+table_len+"' class='addAdvFlag form-group' align='right' ><input type='text' class='form-control advrcavialadjval text-right' id='advrcavailadj_text"+table_len+"' name='items["+(table_len-1)+"].advadjustedAmount' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' onkeyup='checkadvpayemnt("+table_len+")' onblur='checkadvpayemnt("+table_len+")' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$'><div id='adjamt"+table_len+"' class='advadjamnt' style='display:none; color:red'>This Amount is Greater than available Amount</div></td><td align='right' id='taxrate_row"+table_len+"' class='form-group invoiceTaxrate'><select id='taxrate_text"+table_len+"' class='form-control taxrate_textDisable taxrateval' name='items["+(table_len-1)+"].rate' onchange='findTaxAmount("+table_len+")' required><option value=''>-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td><td align='right' id='ttax_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control dropdown text-right' id='abb"+table_len+"' name='items["+(table_len-1)+"].totaltaxamount' readonly><div id='tax_rate_drop pb-0' style='display:none'><div id='icon-drop'></div><h6 style='text-align:center' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3'><p class='mr-3'>IGST <span style='margin-left:8px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text"+table_len+"' name='items["+(table_len-1)+"].igstamount' style='border:none;width: 70px;padding-top: 2px;'></span></div><div class='row pl-3'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text"+table_len+"' name='items["+(table_len-1)+"].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text"+table_len+"' name='items["+(table_len-1)+"].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td align='right' id='cessrate_row"+table_len+"' class='form-group cessFlag'><input type='text' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)' id='cessrate_text"+table_len+"' required class='form-control cessval text-right' name='items["+(table_len-1)+"].cessrate' onKeyUp='findCessAmount("+table_len+")' pattern='^[0-9]+(\.[0-9]{1,2})?[%]?$' data-error='Please enter numeric value with a max precision of 2 decimal places'  /></td><td align='right' id='cessamount_row"+table_len+"'  class='tablegreybg cessFlag'><input type='text'  class='form-control indformat text-right cessamtval' id='cessamount_text"+table_len+"' name='items["+(table_len-1)+"].cessamount' readonly></td>";
		}
		if(addITCRuleFlag) {
			var itcrlz = '<td id="itcrule_row'+table_len+'" align="right" class="form-group">';
			
			itcrlz += '<select class="form-control itcruleval" id="itcrule_text'+table_len+'" name="items['+(table_len-1)+'].type" required><option value="">--Select Type -- </option>';
			for(var i=0;i<invtyperule.length;i++){
				itcrlz += '<option value="'+invtyperule[i]+'">'+invtyperule[i]+'</option>';
			}
			itcrlz += '</select></td>';
			rowPrefix += itcrlz;
		}
		if((returnType != 'GSTR2A') && ( inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA')){
			rowPrefix += "<td align='right' id='totaladvremaining_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='advremaingamount_text"+table_len+"' name='items["+(table_len-1)+"].advRemaingAmount' value='' readonly></td>";
		}
		
		if((returnType != 'GSTR2A') && (inyType5 == 'B2BA' || inyType5 == 'B2CSA' || inyType5 == 'B2CLA' || inyType5 == 'CDNA' || inyType5 == 'CDNURA' || inyType5 == 'ATA' || inyType5 == 'TXPA' || inyType5 == 'B2B' || inyType5 == 'B2CL' || inyType5 == 'B2C' || inyType5 == 'Nil Supplies' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA' || inyType5 == 'Advances' || inyType5 == 'DELIVERYCHALLANS' || inyType5 == 'PROFORMAINVOICES' || inyType5 == 'ESTIMATES' || inyType5 == 'PurchaseOrder' || (returnType == 'GSTR2' && inyType5 == 'B2B Unregistered') || (returnType == 'GSTR2' && inyType5 == 'Import Goods') || (returnType == 'GSTR5' && inyType5 == 'Import Goods') || (returnType == 'GSTR4' && inyType5 == 'Import Services') || (returnType == 'GSTR2' && inyType5 == 'Import Services') || (returnType == 'GSTR2' && inyType5 == 'Nil Supplies') || (returnType == 'GSTR4' && inyType5 == 'B2B Unregistered'))){
			row = table.insertRow(table_len+1).outerHTML=rowPrefix+"<td align='right' id='total_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='total_text"+table_len+"' name='items["+(table_len-1)+"].total' value='' readonly></td><td align='center' width='2%'><a href='javascript:void(0)' class='item_delete' onclick='delete_row("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td><td class='d-none'><input type='hidden' id='itemCustomField_text1"+table_len+"' name='items["+(table_len-1)+"].itemCustomField1'/><input type='hidden' id='itemCustomField_text2"+table_len+"' name='items["+(table_len-1)+"].itemCustomField2'/><input type='hidden' id='itemCustomField_text3"+table_len+"' name='items["+(table_len-1)+"].itemCustomField3'/><input type='hidden' id='itemCustomField_text4"+table_len+"' name='items["+(table_len-1)+"].itemCustomField4'/><input type='hidden' id='itemnotes_text"+table_len+"' name='items["+(table_len-1)+"].itemNotescomments'/><input type='hidden' id='itemId_text"+table_len+"' name='items["+(table_len-1)+"].itemId'/></td></tr>";
		}if((returnType == 'GSTR2A') && (inyType5 == 'B2B' || inyType5 == 'B2BA' || inyType5 == 'B2C' || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA'  || inyType5 == 'B2B Unregistered' || inyType5 == 'Import Goods' || inyType5 == 'Import Services' || inyType5 == 'Nil Supplies')){
			row = table.insertRow(table_len+1).outerHTML=rowPrefix+"<td align='right' id='total_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='total_text"+table_len+"' readonly/></td><td align='center' width='2%'><a href='javascript:void()'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td><td class='d-none'><input type='hidden' id='itemCustomField_text1"+table_len+"' name='items["+(table_len-1)+"].itemCustomField1'/><input type='hidden' id='itemCustomField_text2"+table_len+"' name='items["+(table_len-1)+"].itemCustomField2'/><input type='hidden' id='itemCustomField_text3"+table_len+"' name='items["+(table_len-1)+"].itemCustomField3'/><input type='hidden' id='itemCustomField_text4"+table_len+"' name='items["+(table_len-1)+"].itemCustomField4'/><input type='hidden' id='itemnotes_text"+table_len+"' name='items["+(table_len-1)+"].itemNotescomments'/><input type='hidden' id='itemId_text"+table_len+"' name='items["+(table_len-1)+"].itemId'/></td></tr>";
    }
		if((returnType != 'GSTR2A') && (inyType5 == 'Exports' || inyType5 == 'EXPA' || inyType5 == 'EXPORTS')){
			row = table.insertRow(table_len+1).outerHTML=rowPrefix+"<td align='right' id='total_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='total_text"+table_len+"' name='items["+(table_len-1)+"].total' value='' readonly></td><td align='right' id='currency_row"+table_len+"' class='tablegreybg'><input type='text' class='form-control text-right' id='curtotal_text"+table_len+"' name='items["+(table_len-1)+"].currencytotalAmount' value='' readonly></td><td align='center' width='2%'><a href='javascript:void(0)' class='item_delete' onclick='delete_row("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td><td class='d-none'><input type='hidden' id='itemCustomField_text1"+table_len+"' name='items["+(table_len-1)+"].itemCustomField1'/><input type='hidden' id='itemCustomField_text2"+table_len+"' name='items["+(table_len-1)+"].itemCustomField2'/><input type='hidden' id='itemCustomField_text3"+table_len+"' name='items["+(table_len-1)+"].itemCustomField3'/><input type='hidden' id='itemCustomField_text4"+table_len+"' name='items["+(table_len-1)+"].itemCustomField4'/><input type='hidden' id='itemnotes_text"+table_len+"' name='items["+(table_len-1)+"].itemNotescomments'/><input type='hidden' id='itemId_text"+table_len+"' name='items["+(table_len-1)+"].itemId'/></td></tr>";
		}
		if(inyType5 == 'B2BA' || inyType5 == 'B2CSA' || inyType5 == 'Nil Supplies' || inyType5 == 'B2CLA' || inyType5 == 'CDNA' || inyType5 == 'CDNURA' || inyType5 == 'ATA' || inyType5 == 'TXPA' || inyType5 == 'EXPA' || inyType5 == 'B2B' || inyType5 == 'B2C' || inyType5 == 'B2CL' || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA' || inyType5 == 'Advances' || inyType5 == 'DELIVERYCHALLANS' || inyType5 == 'PROFORMAINVOICES' || inyType5 == 'ESTIMATES' || inyType5 == 'PurchaseOrder' || (returnType == 'GSTR2' && inyType5 == 'B2B Unregistered') || inyType5 == 'Import Goods' || (returnType == 'GSTR2' && inyType5 == 'Import Services') || (returnType == 'GSTR4' && inyType5 == 'Import Services') || (returnType == 'GSTR2' && inyType5 == 'Nil Supplies') || (returnType == 'GSTR4' && inyType5 == 'B2B Unregistered')){
			$('#sortable_table').tableDnDUpdate();
		}
		if(addITCFlag) {
			
			if(!itcStateFlag){
				$('#itctype_text'+rowCount).val('no');
				//$('#itctype_text'+table_len).attr("readonly","readonly");
				$('#itcpercent_text'+rowCount).val('0');
				$('#itcpercent_text'+rowCount).attr("readonly","readonly");
			}
		}
		if($("#cessrate_text"+rowCount).val() == ""){
			var cesstype = $('input[name="cessType"]:checked').val();
			if(cesstype == "Taxable Value"){
				document.getElementById("cessrate_text"+rowCount).value = "0%"; 
			}else{
				document.getElementById("cessrate_text"+rowCount).value = "0"; 
			}
			document.getElementById("cessamount_text"+rowCount).value = "0.0";
		}
		 var ddd = $('#exportType').val();
		//if(ddd == 'WOPAY'){$("#taxrate_text"+i).attr("readonly",true);}
		if(returnType == 'GSTR1'){addITCRuleFlag = false;$('.addITCRuleFlag').hide();if(inyType5 == 'Nil Supplies'){
			$('#itc_rule').show();
			$('.itcrule_text1').show();
		}}
		$('#taxrate_text'+rowCount).val($('#taxrate_text'+rowCount).val()).change();
		$('input').blur(function(){
			if(!$(this).prop('required')){
		    }else{
		    	var bca = $(this).val();
		    	   if( bca == ''){
		    		   $(this).parent().addClass('has-error has-danger');
			 	   }else{
			 		   $(this).parent().removeClass('has-error has-danger');
			 	   }
		    }
		});
		$('#abb'+rowCount).hover(
				  function() {
					  $(this).next().css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
				  }, function() {
					  $(this).next().css("display","none");
				  }
		);
		$('#itc_tax_tot'+rowCount).hover(
				  function() {
					  $(this).next().css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"9%"});
				  }, function() {
					  $(this).next().css("display","none");
				  }
		);
		$('#allinvoice tr').click(function() {
			$(this).addClass("addrowshadow");
			$(this).siblings().removeClass("addrowshadow" );
			//$('#tax_rate_drop1').hide();
			});
		$('select').blur(function(){
			if(!$(this).prop('required')){
			}else{
				if (this.value == '-Select-' || this.value == '') {
			       $(this).parent().addClass('has-error has-danger');
			    }else{
			    	$(this).parent().removeClass('has-error has-danger');
			    }
			}
		});
		
		if(returnType == 'GSTR1' || returnType == 'SalesRegister' || returnType == 'DELIVERYCHALLANS' || returnType == 'PROFORMAINVOICES' || returnType == 'ESTIMATES'){
			if(enableDiscount == true || enableDiscount == "true"){
		        if(inyType5 != "Credit/Debit Notes" && inyType5 != "(Receipt voucher)" && inyType5 != "ADVANCE ADJUSTED DETAIL" && inyType5 != 'TXPA' && inyType5 != "(Nil-Rated/ Exempted / NON-GST)"){
		        	$('#discount_row'+table_len).show();
		        }
		     }else{
		    	 if(inyType5 != "(Nil-Rated/ Exempted / NON-GST)" && inyType5 != "Nil Supplies"){
		    		 $('#discount_row'+table_len).hide();
		    	 }
		     }
			if(enableExempted == true || enableExempted == "true"){
				$('#exempted_row'+table_len).show();	
        	}else{
        		$('#exempted_row'+table_len).hide();	
        	}
			if(enableLedger == true || enableLedger == "true"){
				$('.item_ledger').show();
			}else{
				$('.item_ledger').hide();
			}
			 if(enableSalesCess == true || enableSalesCess == "true"){
				 if(inyType5 != "(Nil-Rated/ Exempted / NON-GST)" && inyType5 != "Nil Supplies"){
					 if($('#invoiceLevelCess').is(":checked")) {
			    		   $('.cessFlag').show();
			    	   }else{
			    		   if(invoiceLevel_Cess == 'No'){
				    		   $('.cessFlag').hide();
				    		   $('#cessrate_text'+table_len).removeAttr("required");
			    		   }
			    	   }
				 }
		       }else{
		    	   if($('#invoiceLevelCess').is(":checked")) {
		    		   $('.cessFlag').show();
		    	   }else{
		    		   if(invoiceLevel_Cess == 'No'){
			    		   $('.cessFlag').hide();
			    		   $('#cessrate_text'+table_len).removeAttr("required");
		    		   }
		    	   }
		       }
        }else if(returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister' || returnType == 'PurchaseOrder'){
        	if(enablePurDiscount == true || enablePurDiscount == "true"){
        		if(inyType5 != "Advance Payments" && inyType5 != "ADVANCE ADJUSTED DETAIL" && inyType5 != 'TXPA' && inyType5 != "ISD INVOICE" && inyType5 != "ITC REVERSAL INVOICE"){
        			$('#discount_row'+table_len).show();
        		}
        	}else{
				if(inyType5 != "(Nil-Rated/ Exempted / NON-GST)" && inyType5 != "Nil Supplies"){
		    		 $('#discount_row'+table_len).hide();
		    	 }
        	}	
        	if(enablePurLedger == true || enablePurLedger == "true"){
    			$('.item_ledger').show();
    		}else{
    			$('.item_ledger').hide();
    		}
        	if(enablePurCess == true || enablePurCess == "true"){
        		if(inyType5 != "Nil Supplies"){
        			$('.cessFlag').show();
        		}
		    }else{
		    	 if(invoiceLevel_Cess == "" || invoiceLevel_Cess == 'No' || invoiceLevel_Cess == null || invoiceLevel_Cess == undefined){
		    		 $('.cessFlag').hide();
		    	 }
		    }
        }
			intialIntialization();
			itmoptions(rowCount,type,retType);
			if(returnType == 'GSTR1'|| returnType == 'SalesRegister'){
				if(inyType5 == 'Exports'){
					 $('.ledgerddbox'+table_len).addClass('exp_ledger');
				}else if(inyType5 == 'Credit/Debit Notes') {
					$('.ledgerddbox'+table_len).addClass('crddr_ledger');
				}else if(inyType5 == 'Nil Supplies'){
					$('.ledgerddbox'+table_len).addClass('nil_ledger');
				}else if(inyType5 == "Advances"){
					$('.ledgerddbox'+table_len).addClass('adv_ledger');
				}else if(inyType5 == "ADVANCE ADJUSTED DETAIL" || inyType5 == "Advance Adjusted Detail" || inyType5 == 'TXPA'){
					$('.ledgerddbox'+table_len).addClass('advadj_ledger');
				}
				if(otherconfigdetails.enableSalesFields == true){
					$('#hsn_text'+table_len).removeAttr("required");
					$('#uqc_text'+table_len).removeAttr("required");
					$('#qty_text'+table_len).removeAttr("required");
					$('#ad_tax1').removeClass("astrich");
					$('#ad_tax2').removeClass("astrich");
					$('#ad_tax3').removeClass("astrich");
				}else{
					$('.hsn_text').attr("required",true);
					$('.uqcDetails ').attr("required",true);
					$('.qtyval').attr("required",true);
					$('#ad_tax1').addClass("astrich");
					$('#ad_tax2').addClass("astrich");
					$('#ad_tax3').addClass("astrich");
				}
			}
			if(returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister'){
				if(inyType5 == "B2B"){
					$('.ledgerddbox'+table_len).addClass('b2b_ledger');
				}else if(inyType5 == 'Credit/Debit Notes') {
					$('.ledgerddbox'+table_len).addClass('crdr_ledger');
				}else if(inyType5 == 'Import Goods') {
					$('.ledgerddbox'+table_len).addClass('impg_ledger');
				}else if(inyType5 == 'Import Services') {
					$('.ledgerddbox'+table_len).addClass('imps_ledger');
				}else if(inyType5 == 'Nil Supplies') {
					$('.ledgerddbox'+table_len).addClass('nil1_ledger');
				}
				if(otherconfigdetails.enablePurFields == true){
					$('#hsn_text'+table_len).removeAttr("required");
					$('#uqc_text'+table_len).removeAttr("required");
					$('#qty_text'+table_len).removeAttr("required");
					$('#ad_tax1').removeClass("astrich");
					$('#ad_tax2').removeClass("astrich");
					$('#ad_tax3').removeClass("astrich");
				}else{$('.hsn_text').attr("required",true);$('.uqcDetails ').attr("required",true);$('.qtyval').attr("required",true);$('#ad_tax1').addClass("astrich");$('#ad_tax2').addClass("astrich");$('#ad_tax3').addClass("astrich");}}
			if(returnType == 'GSTR1' || returnType == 'SalesRegister'){
						if((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true")  && (enableSalesCess == true || enableSalesCess == "true")){
							$("table#sortable_table td:nth-child(2) input[type=text]").css("min-width","91%");
						}else{
							$("table#sortable_table td:nth-child(2) input[type=text]").css("min-width","93%");
						}
	        }else if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
	        		$("table#sortable_table td:nth-child(2) input[type=text]").css("min-width","91%");
	        	
	        }	
			$('form[name="salesinvoceform"]').validator('update');
	}
	function updateCategory(value, no) {
		category = value;
		$("#hsn_text"+no).val('');
	}
	function findTaxableValue(no) {
		var retType = $('#retType').val();
		var q = document.getElementById('qty_text'+no);
		var r = document.getElementById('rate_text'+no);
		var d = document.getElementById('discount_text'+no);
		var dp = document.getElementById('disper'+no);
		var ex = document.getElementById('exempted_text'+no);
		var tx = document.getElementById('taxableamount_text'+no);
		var inyType5 = $('#idInvType').val();
		var gstno = $('#billedtogstin').val();
		if(q && r){
			if(q.value && r.value && tx) {
				var val = parseFloat(q.value)*parseFloat(r.value);
				if(inyType5 != 'Credit/Debit Notes' && inyType5 != 'Nil Supplies'){
					if(d != null){
							if(d.value) {
								var dStr = (d.value).toString();
								if (dStr.indexOf("%") > -1) {
									dStr = dStr.replace("%","");
									if(dStr > 100){
										//var gd = document.getElementById('discount_text'+no).value=parseFloat(100)+"%";
										$('#discount_text'+no).val(parseFloat(100)+"%");
										var gd = $('#discount_text'+no).val();
										dStr = gd.replace("%","");
									}
									dp.value = "percentage";
									val -= val * (dStr/100);
								}else{
									dp.value = "";
									//val -= d.value;
									if(d.value > val){
										d.value = val;
									}
									val -= d.value;
								}
							}
					}
				}
				if(retType == 'GSTR2' || retType == 'Purchase Register' || retType == 'PurchaseRegister'){
					
				}else{
					if(ex != null){
						if(ex.value <= val){
							if(ex.value) {
								var exqty = parseFloat(ex.value)*parseFloat(q.value);
								val -= exqty;
							}
						}else{
							if(ex.value) {
								var exqty = parseFloat(ex.value)*parseFloat(q.value);
								val -= exqty;
							}
							tx.value = Number(0).toFixed(2);
						}
					}
				}
				if(val <= 0){
					if((retType == 'SalesRegister' || retType == 'GSTR1' || retType == 'Sales Register') && ((inyType5 == 'B2B' && gstno == '') || inyType5 == 'B2C')){
						tx.value = val.toFixed(2);
					}else{
						tx.value = Number(0).toFixed(2);
					}
				}else{
					tx.value = val.toFixed(2);
				}
			}
		}
		if(r && q){
			if(r.value == '' || q.value == ''){
				tx.value = Number(0).toFixed(2);
			}
		}
		findTaxAmount(no);
		findCessAmount(no);
		findITCValue(no);
	}
	
	function findNillTaxableValue(no) {
		var q = document.getElementById('qty_text'+no);
		var r = document.getElementById('rate_text'+no);
		var ex = document.getElementById('exempted_text'+no);
		var tx = document.getElementById('taxableamount_text'+no);
		if(q.value && r.value && tx) {
			var val = parseFloat(q.value)*parseFloat(r.value);
			if(retType == 'GSTR2' || retType == 'Purchase Register' || retType == 'PurchaseRegister'){
				ex.value = Number(0).toFixed(2);
			}else{
			if(ex != null){
				if(ex.value <= val){
					if(ex.value) {
						var exqty = parseFloat(ex.value)*parseFloat(q.value);
						val -= exqty;
					}
				}else{
					if(ex.value) {
						var exqty = parseFloat(ex.value)*parseFloat(q.value);
						val -= exqty;
						tx.value = Number(0).toFixed(2);
					}
				}
			}
			}
			if(val < 0){
				tx.value = val.toFixed(2);
			}else{
				tx.value = val.toFixed(2);
			}
		}
		if(r.value == '' || q.value == ''){
			tx.value = Number(0).toFixed(2);
		}
		findNillTaxAmount(no);
		
	}
	
	function updateEligibity(value, no) {
		if(otherconfigdetails != ''){
			if(value == 'ip'){
				$('#itcpercent_text'+no).val(otherconfigdetails.itcinput);
			}else if(value == 'is'){
				$('#itcpercent_text'+no).val(otherconfigdetails.itcinputService);
			}else if(value == 'cp'){
				$('#itcpercent_text'+no).val(otherconfigdetails.itcCapgood);
			}else{
				$('#itcpercent_text'+no).val("0");
				$('#itcpercent_text'+no).prop("readonly",true);
			}
			findITCValue(no);
		}else{
		if(value == 'no'){
			$('#itcpercent_text'+no).prop("readonly",true);
		}else if(value == 'pending'){
			$('#itcpercent_text'+no).prop("readonly",false);
		}
		if(value == 'ip' || value == 'cp' || value == 'is') {
			$('#itcpercent_text'+no).prop("readonly",false);
			if(document.getElementById('itcpercent_text'+no).value == 0) {
				document.getElementById('itcpercent_text'+no).value=100;
			}
			findITCValue(no);
		} else {
			document.getElementById('itcpercent_text'+no).value=0;
			document.getElementById('igstitc_text'+no).value=Number(0).toFixed(2);
			document.getElementById('cgstitc_text'+no).value=Number(0).toFixed(2);
			document.getElementById('sgstitc_text'+no).value=Number(0).toFixed(2);
			document.getElementById('cessitc_text'+no).value=Number(0).toFixed(2);
      	document.getElementById('itc_tax_tot'+no).value=Number(0).toFixed(2);
		}
		}
		findTotal(no);
	}
	function findITCValue(no) {
		var p = document.getElementById('itcpercent_text'+no);
		if(p != null && p.value>100){
			//$('#itcpercent_text'+no).attr("required","true");
			$('#itcpercent_text'+no).val('100').removeAttr("readonly");
		}
		if(p != null && p.value) {
			var iamt = document.getElementById('igsttax_text'+no);
			var camt = document.getElementById('cgsttax_text'+no);
			var csamt = document.getElementById('cessamount_text'+no);
			if(iamt != null && iamt.value) {
				var val = ((parseFloat(p.value)*parseFloat(iamt.value))/100);
				if(val > 0.00) {
					document.getElementById('igstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					igst_itc = document.getElementById('igstitc_text'+no).value;
					var csamt = document.getElementById('cessamount_text'+no);
					if(csamt != null && csamt.value) {
						var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
						if(val > 0.00) {
							document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
						} else {
							document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
						}
					}
					cess_itc = document.getElementById('cessitc_text'+no).value;
					itc_tot = parseFloat(igst_itc) + parseFloat(cess_itc);
					document.getElementById('itc_tax_tot'+no).value = itc_tot.toFixed(2);
				} else {
					document.getElementById('igstitc_text'+no).value = Number(0).toFixed(2);
					document.getElementById('itc_tax_tot'+no).value=Number(0).toFixed(2);
				}
			}
			var camt = document.getElementById('cgsttax_text'+no);
			if(camt != null && camt.value) {
				var val = ((parseFloat(p.value)*parseFloat(camt.value))/100);
				if(val > 0.00) {
					document.getElementById('cgstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					document.getElementById('sgstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					cgst_itc = document.getElementById('cgstitc_text'+no).value;
					sgst_itc = document.getElementById('sgstitc_text'+no).value;
					var csamt = document.getElementById('cessamount_text'+no);
					if(csamt != null && csamt.value) {
						var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
						if(val > 0.00) {
							document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
						} else {
							document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
						}
					}
					cess_itc = document.getElementById('cessitc_text'+no).value;
					itc_tot = parseFloat(cgst_itc) + parseFloat(sgst_itc) + parseFloat(cess_itc);
					document.getElementById('itc_tax_tot'+no).value = itc_tot.toFixed(2);
				} else {
					document.getElementById('cgstitc_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgstitc_text'+no).value = Number(0).toFixed(2);
				}
			}
			var csamt = document.getElementById('cessamount_text'+no);
			if(csamt != null && csamt.value) {
				var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
				if(val > 0.00) {
					document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
				} else {
					document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
				}
			}
		}else{
			var igstitc = document.getElementById('igstitc_text'+no);
			var cgstitc = document.getElementById('cgstitc_text'+no);
			var sgstitc = document.getElementById('sgstitc_text'+no);
			var cessitc = document.getElementById('cessitc_text'+no);
			if(igstitc != null){
				document.getElementById('igstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(cgstitc != null){
				document.getElementById('cgstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(sgstitc != null){
				document.getElementById('sgstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(cessitc != null){
				document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
			}
		}
		updateTotals(no);
	}
	function findNillTaxAmount(no){
		var t = document.getElementById('taxableamount_text'+no);
		var totalValue = 0;
		if(t && t.value) {
			totalValue = parseFloat(t.value);
		}
		document.getElementById('total_text'+no).value = totalValue.toFixed(2);
		updateNillTotals(no);
	}
	
	function checkadvpayemnt(no){
		findTaxAmount(no);
		var adavail = $('#advrcavail_text'+no).val();
		var advavailadj = $('#total_text'+no).val();
		var advrecavialable = $('#advrcavailadj_text'+no).val();
		var advremaingamt=parseFloat(adavail)-parseFloat(advavailadj);
		var intadavail = 0.00;var intadvavailadj = 0.00;
		var totalTaxableadj=document.getElementById("totTaxable").innerHTML;	
		if(/[,\-]/.test(totalTaxableadj)){
		totalTaxableadj = totalTaxableadj.replace(/,/g , '');
		}
		if(totalTaxableadj != '' && totalTaxableadj>0){
			totalTaxableadj = parseFloat(totalTaxableadj);
		}
		var originalinvamt = $('#advPInvamt').val();
		$('#advremaingamount_text'+no).val(parseFloat(advremaingamt).toFixed(2));
		if(adavail != ''){
			intadavail = parseInt(adavail);
		}
		if(advavailadj != ''){
			intadvavailadj = parseInt(advavailadj);
		}
		if(intadvavailadj > intadavail){
			$('#advrcavailadj_text'+no).parent().addClass('has-error has-danger');
			$('#adjamt'+no).css("display","block");
			//var adavail = $('#advrcavailadj_text'+no).val(0.00);
			findTaxAmount(no);
		}else{
			if(originalinvamt !="" && originalinvamt > 0){
				if(totalTaxableadj > originalinvamt){
					$('#advrcavailadj_text'+no).addClass('has-error has-danger');
					$('#advrcavailadj_text'+no).parent().addClass('has-error has-danger');
					$('#adjamt'+no).css("display","block");
				}else{
					$('#adjamt'+no).css("display","none");
				}
			}else{
				$('#adjamt'+no).css("display","none");
			}
			findTaxAmount(no);
		}
		
	}
	$("#billedtostatecode").change(function(){
		var retType = $('#retType').val();
		if(retType == 'GSTR2' || retType == 'Purchase Register' || retType == 'PurchaseRegister'){
		//var itctype = $('.form-control.itcval').val();
		var billstate = $('#billedtostatecode').val();
		
		$("#allinvoice tr").each(function(index){
			var inds = index+1;
			if(clntStatename != billstate){
				$('.form-control.itcval').val('no');
				$('.form-control.itc_percent').val('0');
			}else{
				 var itctype = $('#itctype_text'+inds).val();
				 var itctype1 = $('#itctype_text1').val();
				 $('#itcpercent_text'+inds).attr("readonly",false);
				 $('#itcpercent_text'+inds).removeClass("disabled");
				 $('#itctype_text'+inds).attr('readonly', false);
				 $('#itctype_text'+inds).removeClass("disabled");
				 $('#itcpercent_text1').attr("readonly",false);
					$('#itcpercent_text1').removeClass("disabled");
					$('#itctype_text1').attr('readonly', false);
					$('#itctype_text1').removeClass("disabled");
				if(itctype1 != "no"){
					$('#itcpercent_text1').attr("readonly",false);
					$('#itcpercent_text1').removeClass("disabled");
					$('#itctype_text1').attr('readonly', false);
					$('#itctype_text1').removeClass("disabled");
				}else{
					$('#itcpercent_text1').attr("readonly",true);
					$('#itcpercent_text1').addClass("disabled");
					$('#itctype_text1').attr('readonly', false);
					$('#itctype_text1').removeClass("disabled");
				}
				
				if(itctype != "no"){
					$('#itcpercent_text'+inds).attr("readonly",false);
					$('#itcpercent_text'+inds).removeClass("disabled");
					$('#itctype_text'+inds).attr('readonly', false);
					$('#itctype_text'+inds).removeClass("disabled");
				}else{
					$('#itcpercent_text'+inds).attr("readonly",true);
					$('#itcpercent_text'+inds).addClass("disabled");
					$('#itctype_text'+inds).attr('readonly', false);
					$('#itctype_text'+inds).removeClass("disabled");
				}
			}
		index++;
		});
	}
		});
	function findTaxAmount(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var rtype=$('#retType').val();
		var billname = $('#billedtoname').val();
		var gstno = $('#billedtogstin').val();
		var itype = $("#idInvType").val();
		var invType = $('#invTyp').val();
		var exportType = $("#exportType option:selected").val();
		var cdnurtype = $('#cdnurtyp').val();
		var t = document.getElementById('taxableamount_text'+no);
		if(itype == 'Advances'){
			var rt = document.getElementById('rate_text'+no);
			if(rt){
				$('#taxableamount_text'+no).val(rt.value);
			}
		}
		/*if(dealertype == 'Composition' && revtype == 'Regular'){
			$('#taxrate_text'+no).val("0");
			$('.taxrateval').attr("readonly",true);
			$('#tax_rate').removeClass('astrich');
		}else{*/
			$('#taxrate_text'+no).removeAttr("readonly");
			$('#tax_rate').addClass('astrich');
			if(invType == 'SEWOP' || exportType == 'WOPAY'){
				//$('#taxrate_text'+no).val(0);
				//$('#taxrate_text'+no).prop("disabled",true);
				$('#taxrate_text'+no).prop("disabled",false);
			}else{
				$('#taxrate_text'+no).prop("disabled",false);
			}
		//}
		//if(exportType == 'WOPAY'){
			//$('#taxrate_text'+no).val("0");	}else 
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'CBW' || exportType == 'WPAY' || exportType == 'WOPAY'){
			interStateFlag = false;
		}else if(rtype == 'GSTR1' && (itype == 'Credit/Debit Note for Unregistered Taxpayers' || (itype == 'Credit/Debit Notes' && gstno == ""))){
			if(cdnurtype != 'B2CS'){
				interStateFlag=false;
				if(cdnurtype == 'EXPWOP'){
					//$('.taxrateval').attr("readonly",true);
					//$('.taxrateval').addClass("disabled");
					//$('.taxrateval').val("0");
					$("#includetax").prop("checked",false);
					$("#includetax").prop("disabled",true);
				}else{
					$('.taxrateval').attr("readonly",false);
					$('.taxrateval').removeClass("disabled");
					$("#includetax").prop("disabled",false);
					
				}
			}
			
		}else if((rtype == 'GSTR2' || rtype == 'Purchase Register')){
			if(itype != 'Advance Adjusted Detail' && itype != 'Import Goods' && itype != 'Import Services' && itype != 'ISD' && itype != 'ITC Reversal'){
			if(gstno == "" && revtype == 'Regular'){
				$('#taxrate_text'+no).val("0");
				$('#taxrate_text'+no).addClass("disabled");
				$('.taxrateval').attr("readonly",true);
				$('#itctype_text'+no).val("no").attr("readonly",true).addClass("disabled");
				$('#itcpercent_text'+no).val("0").attr("readonly",true).addClass("disabled");
				//$('#itcpercent_text'+no).removeAttr("readonly");
				$('#tax_rate').removeClass('astrich');
				document.getElementById("billgstin_name").innerHTML = "GSTIN field is empty, Tax amount will not be applicable";
				$("#includetax").prop("checked",false);
				$("#includetax").prop("disabled",true);
			}else{
				$('#taxrate_text'+no).removeClass("disabled");
				/*if(dealertype == 'Composition' && revtype == 'Regular'){
					$('#taxrate_text'+no).val("0");
					$('.taxrateval').attr("readonly",true);
					$('#itctype_text'+no).val("no").attr("readonly",true).addClass("disabled");
					$('#itcpercent_text'+no).val("0").attr("readonly",true).addClass("disabled");
					$('#tax_rate').removeClass('astrich');
				}else{*/
					$('.taxrateval').removeAttr("readonly");
					$('#itctype_text'+no).removeClass("disabled").removeAttr("readonly");
					$('#itcpercent_text'+no).removeClass("disabled").removeAttr("readonly");
					$('#tax_rate').addClass('astrich');
					document.getElementById("billgstin_name").innerHTML = "";
					$("#includetax").prop("disabled",false);
				//}
			}
		}
	} 
		if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
			var rt = document.getElementById('advrcavailadj_text'+no);
			var taxableValue = 0;
			if(rt){
				taxableValue = parseFloat(rt.value);
			}
			var r = document.getElementById('taxrate_text'+no);
			if(rt && r){
			var val = 0;
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				if(rt && rt.value) {
					taxableValue = parseFloat(rt.value);
				}
				var cs = document.getElementById('cessrate_text'+no);
				if(cs && cs.value) {
					cs.value = 0;
				}
				//val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(cs.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				if(r.value != ""){
					val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
				}
			} else {
				$('#includetax').val('No');
					val = ((taxableValue*parseFloat(r.value))/100);
				}
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					var txval = val;
					var cs = document.getElementById('cessamount_text'+no);
					if(cs.value) {
						txval += parseFloat(cs.value);
					}
					var taxbleval = taxableValue;
					var totaltax = document.getElementById('abb'+no);
					if(totaltax.value) {
						taxbleval += parseFloat(totaltax.value);
					}
					document.getElementById('advrcavailadj_text'+no).value = ((taxbleval-txval).toFixed(2))/1;
				}
				if(val > 0) {
					if(interStateFlag) {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
							
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					} else {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21 = parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
					document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
					var igst21 = document.getElementById('igsttax_text'+no).value;
					var cgst21 = document.getElementById('cgsttax_text'+no).value;
					var sgst21 = document.getElementById('sgsttax_text'+no).value;
					var tot21;
					tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
					document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
				}
			} else {
				document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
				var igst21 = document.getElementById('igsttax_text'+no).value;
				var cgst21 = document.getElementById('cgsttax_text'+no).value;
				var sgst21 = document.getElementById('sgsttax_text'+no).value;
				var tot21;
				tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
				document.getElementById('abb'+no).value = tot21.toFixed(2);
			}
			}
			var adavail = 0.00;
			if($('#advrcavail_text'+no).val() != ""){
				adavail = $('#advrcavail_text'+no).val();
			}
			var advavailadj = 0.00;
			if($('#total_text'+no).val() != ""){
				advavailadj = $('#total_text'+no).val();
			}
			var advrecavialable =0.00;
			if($('#advrcavailadj_text'+no).val() != ""){
				advrecavialable = $('#advrcavailadj_text'+no).val();	
			}
			var advtax = 0.00;
			if($('#abb'+no).val() != ""){
				advtax = $('#abb'+no).val();
			}
			var advremaingamt=parseFloat(adavail)-(parseFloat(advrecavialable)+parseFloat(advtax));
			var intadavail = 0.00;var intadvavailadj = 0.00;
			$('#advremaingamount_text'+no).val(parseFloat(advremaingamt).toFixed(2));
			if(adavail != ''){
				intadavail = parseInt(adavail);
			}
			if(advrecavialable && advtax){
				intadvavailadj = parseInt(parseFloat(advrecavialable)+parseFloat(advtax));
			}
			if(intadvavailadj > intadavail){
				$('#advrcavailadj_text'+no).parent().addClass('has-error has-danger');
				$('#adjamt'+no).css("display","block");
			}else{
				$('#adjamt'+no).css("display","none");
			}
			findTotal(no);
		}else{
		var r = document.getElementById('taxrate_text'+no);
		if(t && t.value && r && r.value) {
			var taxableValue = parseFloat(t.value);
			var val = 0;
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				var q = document.getElementById('qty_text'+no);
				var rt = document.getElementById('rate_text'+no);
				var d = document.getElementById('discount_text'+no);
				var ex = document.getElementById('exempted_text'+no);
				if(q && rt){
				if(q.value && rt.value) {
					taxableValue = parseFloat(q.value)*parseFloat(rt.value);
					if(itype !='Credit/Debit Note for Unregistered Taxpayers' && itype !='Credit/Debit Notes'){
						
						if(d != null){
							if(d.value) {
								var dStr = (d.value).toString();
								if (dStr.indexOf("%") > -1) {
									dStr = dStr.replace("%","");
									if(dStr > 100){
										//var gd = document.getElementById('discount_text'+no).value=parseFloat(100)+"%";
										$('#discount_text'+no).val(parseFloat(100)+"%");
										var gd = $('#discount_text'+no).val();
										dStr = gd.replace("%","");
									}
								
									taxableValue -= taxableValue * (dStr/100);
								}else{
									
									//val -= d.value;
									if(d.value > taxableValue){
										d.value = taxableValue;
									}
									taxableValue -= d.value;
								}
							}
						}
					}
					if(ex && ex.value){
						if(ex.value == ''){
							ex.value = 0;
						}
						if(ex.value <= taxableValue) {
							taxableValue -= ex.value;
						}
					}
				}
				}
				var cs = document.getElementById('cessrate_text'+no);
				if(cs.value == '') {
					cs.value = 0;
				}
				//val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(cs.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
				
			} else {
				$('#includetax').val('No');
				var q = document.getElementById('qty_text'+no);
				var rt = document.getElementById('rate_text'+no);
				var d = document.getElementById('discount_text'+no);
				var ex = document.getElementById('exempted_text'+no);
				if(q && rt){
				if(q.value && rt.value) {
					taxableValue = parseFloat(q.value)*parseFloat(rt.value);
					if(itype !='Credit/Debit Note for Unregistered Taxpayers' && itype !='Credit/Debit Notes'){
						if(d != null){
							if(d.value) {
								var dStr = (d.value).toString();
								if (dStr.indexOf("%") > -1) {
									dStr = dStr.replace("%","");
									if(dStr > 100){
										//var gd = document.getElementById('discount_text'+no).value=parseFloat(100)+"%";
										$('#discount_text'+no).val(parseFloat(100)+"%");
										var gd = $('#discount_text'+no).val();
										dStr = gd.replace("%","");
									}
									taxableValue -= taxableValue * (dStr/100);
								}else{
								
									//val -= d.value;
									if(d.value > taxableValue){
										d.value = taxableValue;
									}
									taxableValue -= d.value;
								}
							}
						}
					}
					if(ex && ex.value){
						if(ex.value == ''){
							ex.value = 0;
						}
						if(ex.value <= taxableValue) {
							taxableValue -= ex.value;
						}
					}
					
				}
				}
				var cs = document.getElementById('cessrate_text'+no);
				if(cs.value == '') {
					cs.value = 0;
				}
				//val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(cs.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				val = ((taxableValue*parseFloat(r.value))/100);
			}
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				var txval = val;
				var cs = document.getElementById('cessamount_text'+no);
				if(cs.value) {
					txval += parseFloat(cs.value);
				}
				document.getElementById('taxableamount_text'+no).value = ((taxableValue-txval).toFixed(2))/1;
			}
			if((rtype == 'GSTR2' || 'Purchase Register')){
				if(itype != 'Advance Adjusted Detail' && itype != 'TXPA' && itype != 'Import Goods' && itype != 'Import Services'){
					if(gstno == ""){
						document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
						var igst21 = document.getElementById('igsttax_text'+no).value;
						var cgst21 = document.getElementById('cgsttax_text'+no).value;
						var sgst21 = document.getElementById('sgsttax_text'+no).value;
						var tot21;
						tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
						document.getElementById('abb'+no).value = tot21.toFixed(2);
					}
				}
			}
			if(invType == 'SEWOP' || exportType == 'WOPAY'){
				document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
				var igst21 = document.getElementById('igsttax_text'+no).value;
				var cgst21 = document.getElementById('cgsttax_text'+no).value;
				var sgst21 = document.getElementById('sgsttax_text'+no).value;
				var tot21;
				tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
				document.getElementById('abb'+no).value = tot21.toFixed(2);
			} else if(val > 0) {
				if(interStateFlag) {
					if($('#diffPercent').is(":checked")) {
						if(exportType != 'WOPAY'){
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					}else{
						if(exportType != 'WOPAY'){
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					}
				} else {
					if($('#diffPercent').is(":checked")) {
						if(exportType != 'WOPAY'){
							document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
						}else{
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
								document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}
				}
			} else {
				if((itype == 'B2B' && gstno == "") || itype == 'B2C' || itype == 'Advances'){
					if(interStateFlag) {
						if($('#diffPercent').is(":checked")) {
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
								document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}else{
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
								document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}
					} else {
						if($('#diffPercent').is(":checked")) {
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
								document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}else{
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
								document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}
					}
					}else{
						document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
						var igst21 = document.getElementById('igsttax_text'+no).value;
						var cgst21 = document.getElementById('cgsttax_text'+no).value;
						var sgst21 = document.getElementById('sgsttax_text'+no).value;
						var tot21;
						tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
						document.getElementById('abb'+no).value = tot21.toFixed(2);
				}
			}
			var rettype = $('#retType').val();
			if(itype == "Advances"){
				findHsnOrSac(no);
			}
			if(rettype != 'GSTR1'){
				findITCValue(no);
			}
			findTotal(no);
		}
	}
	}
	
	$('#diffPercent').change(function(){
		if($('#diffPercent').is(":checked")){
			$('.diffPercent').val("Yes");
		}else{
			$('.diffPercent').val("No");
		}
		var itype = $("#idInvType").val();
		$("#allinvoice tr").each(function(index){
			if(itype != "Advance Adjusted Detail"){
				findTaxAmount(index+1);
				findTaxableValue(index+1);
				findCessAmount(index+1);
				findTotal(index+1);
			}else{
				findadvadjdiffTaxAmount(index+1);
			}
			
			index++;
		});
	});
	function findCessAmount(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var quantity = document.getElementById('qty_text'+no);
		var cesstype = $('input[name="cessType"]:checked').val();
		var t = document.getElementById('taxableamount_text'+no);
		var r = document.getElementById('cessrate_text'+no);
		var invType = $('#idInvType').val();
		var itype = $("#invTyp").val();
		var rtype=$('#retType').val();
		var exportType = $("#exportType option:selected").val();
		var gstno = $('#billedtogstin').val();
		/*if(dealertype == 'Composition' && revtype == 'Regular'){
			$('#cessrate_text'+no).val("0");
			$('#cessrate_text'+no).attr("readonly",true);
			$('#rate_as').removeClass('astrich');
		}else{*/
			$('#cessrate_text'+no).removeAttr("readonly");
			$('#rate_as').addClass('astrich');
			if(itype == 'SEWOP' || itype == 'WOPAY'){
				$('#cessrate_text'+no).val(0);
				$('#cessrate_text'+no).prop("disabled",true);	
			}else{
				$('#cessrate_text'+no).prop("disabled",false);
			}
		//}
		if(exportType == 'WOPAY'){
			$('#cessrate_text'+no).val("0").prop("disabled",true).attr("readonly",true);
		}else if((rtype == 'GSTR2' || rtype == 'Purchase Register')){
			if(invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'Import Goods' && invType != 'Import Services' && invType != 'ISD' && invType != 'ITC Reversal'){
				if(gstno == "" && revtype == 'Regular'){
					$('#cessrate_text'+no).val("0");
					$('#cessrate_text'+no).addClass("disabled");
					$('#cessrate_text'+no).attr("readonly",true);
					$('#rate_as').removeClass('astrich');
				}else{
					$('#cessrate_text'+no).removeClass("disabled");
					/*if(dealertype == 'Composition' && revtype == 'Regular'){
						$('#cessrate_text'+no).val("0");
						$('#cessrate_text'+no).attr("readonly",true);
						$('#rate_as').removeClass('astrich');
					}else{*/
						$('#cessrate_text'+no).removeAttr("readonly");
						$('#rate_as').addClass('astrich');
					//}
				}
			}
		}
		if(invType =='Advance Adjusted Detail' || invType == 'TXPA'){
				var rt = document.getElementById('advrcavailadj_text'+no);
				if(rt && rt.value){
					if(quantity && quantity.value){
						var qty = parseFloat(quantity.value);
					}
				var taxableValue = parseFloat(rt.value);
				var val = 0;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
						taxableValue = parseFloat(rt.value);
					var tx = document.getElementById('taxrate_text'+no);
					if(tx.value == '') {
						tx.value = 0;
					}
					val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(tx.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				} else {
					$('#includetax').val('No');
					if(cesstype == "Taxable Value"){
						val = ((taxableValue*parseFloat(r.value))/100);
					}else{
						if(qty){
							val = (qty*parseFloat(r.value));
						}
					}
				}
				if(val > 0) {
					val = ((val.toFixed(2))/1).toFixed(2);
				} else {
					val = Number(0).toFixed(2);
				}
				document.getElementById('cessamount_text'+no).value = val;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					findTaxAmount(no);
				} else {
					$('#includetax').val('No');
					findTotal(no);
				}
			}
		}else{
		if(t && t.value && r.value) {
			var taxableValue = parseFloat(t.value);
			if(quantity && quantity.value){
				var qty = parseFloat(quantity.value);
			}
			var val = 0;
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				var q = document.getElementById('qty_text'+no);
				var rt = document.getElementById('rate_text'+no);
				var d = document.getElementById('discount_text'+no);
				var ex = document.getElementById('exempted_text'+no);
				if(q && rt){
				if(q.value && rt.value) {
					taxableValue = parseFloat(q.value)*parseFloat(rt.value);
					if(invType !='Credit/Debit Note for Unregistered Taxpayers' && invType != 'Credit/Debit Notes'){
						if(d.value == ''){
							d.value = 0;
						}
						if(d.value <= taxableValue) {
							taxableValue -= d.value;
						}
					}
					if(ex && ex.value){
						if(ex.value == ''){
							ex.value = 0;
						}
						if(ex.value <= taxableValue) {
							taxableValue -= ex.value;
						}
					}
				}
				}
				var tx = document.getElementById('taxrate_text'+no);
				if(tx.value == '') {
					tx.value = 0;
				}
				val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(tx.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
			} else {
				$('#includetax').val('No');
				if(cesstype == "Taxable Value"){
					val = ((taxableValue*parseFloat(r.value))/100);
				}else{
					if(qty){
						val = (qty*parseFloat(r.value));
					}
				}
			}
			if(val > 0) {
				val = ((val.toFixed(2))/1).toFixed(2);
			} else {
				val = Number(0).toFixed(2);
			}
			document.getElementById('cessamount_text'+no).value = val;
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				findTaxAmount(no);
			} else {
				$('#includetax').val('No');
				findTotal(no);
			}
		}
	}
		findITCValue(no);
		updateTotals(no);
	}
	function changeCessType() {
		var cesstype = $('input[name="cessType"]:checked').val();
		$("#allinvoice tr").each(function(index){
			var abc = index+1;
			var dStr = ($('#cessrate_text'+abc).val()).toString();
			if(dStr.indexOf("%") > -1){
				dStr = dStr.replace("%","");
			}
			if(cesstype == "Taxable Value"){
				$('#cessrate_text'+abc).val(dStr+"%");
			}else{
				$('#cessrate_text'+abc).val(dStr);
			}
			findCessAmount(index+1);
			index++;
		});
	}
	function updaterevchargeNo(itype,month,year){
		if(itype == "B2B" || itype == "Advances" || itype == "Advance Adjusted Detail" || itype == 'TXPA' || itype == "Import Services"){
			$('#roundoffdiv').removeClass("col-md-2 col-sm-12");
			$('#revChargeNoDiv').css("display","inline-block");
			var invDefUrl = contextPath+'/invdef/'+urlSuffixs+'/Purchase Register/PurchaseReverseChargeNo/'+month+'/'+year;
				$.ajax({
					url: invDefUrl,
					async: true,
					cache: false,
					success : function(invNo) {
						if(Object.keys(invNo).length != 0){
								if(invNo["InvoiceNumber"] != "") {
										$('#revchargeNo').val(invNo.InvoiceNumber);	
										if(invNo["InvoiceNumber"] != "") {
											if(invNo["PreviousInvoiceNumber"] != "") {
												$('.revInvno').css("display","block");
												$('.previousRevChargeNo').html(invNo["PreviousInvoiceNumber"]);
											}else{
												$('.revInvno').css("display","none");
												$('.previousRevChargeNo').html('');
											}
										}else{
											$('.revInvno').css("display","none");
											$('.previousRevChargeNo').html('');
										}
								}
						}else {
							$('#revNoMissing').css('display','block');
							$('#revNoMissing').html('<a href="'+invConfigUrl+'">Click here </a>to configure your Invoice details.');
							$('.revInvno').css("display","none");
							$('.previousRevChargeNo').html('');
						}
					}
				
				});
		}
	}
	
	$("#revchargetype").change(function(){
		var rtype = $(this).children("option:selected").val();
		var rettype=$('#retType').val();
		var itype = $("#idInvType").val();
		if(rtype == 'Reverse'){
			if(rettype == "GSTR2"){
				updaterevchargeNo(itype,month,year);
			}else{
				$('#roundoffdiv').addClass("col-md-2 col-sm-12");
				$('#revChargeNoDiv').css("display","none");
			}
			$("#includetax").prop("checked",false);
			$("#allinvoice tr").each(function(index){
				findTaxAmount(index+1);
				findTaxableValue(index+1);
				findCessAmount(index+1);
				index++;
			});
		}else{
			 $('#roundoffdiv').addClass("col-md-2 col-sm-12");
			 $('#revchargeNo').val('');
			 $('.previousRevChargeNo').html('');
			$('#revChargeNoDiv').css("display","none");
			$('.rateinctax').text("");
			$("#allinvoice tr").each(function(index){
				findTaxAmount(index+1);
				findTaxableValue(index+1);
				findCessAmount(index+1);
				index++;
			});
		}
	});
	
	function findIsdTaxAmount(no){
		var revtype = $("#revchargetype>option:selected").val();
		var itype = $("#idInvType").val();
		var t = document.getElementById('isdtaxableamount_text'+no);
		var totalValue = 0;
		if(t && t.value) {
			totalValue = parseFloat(t.value);
		}
		var cessamt = document.getElementById('isdisdcess_text'+no);
		if(cessamt && cessamt.value){
		if(cessamt.value) {
			totalValue += parseFloat(cessamt.value);
		}
		}
		var samt = document.getElementById('isdsgsttax_text'+no);
		if(samt.value) {
			totalValue += parseFloat(samt.value);
		}
		var iamt = document.getElementById('isdigsttax_text'+no);
		if(iamt.value) {
			totalValue += parseFloat(iamt.value);
		}
		var camt = document.getElementById('isdcgsttax_text'+no);
		if(camt.value) {
			totalValue += parseFloat(camt.value);
		}
		updateTotals(no);
		if(revtype == 'Reverse'){
			if(t && t.value) {
			  document.getElementById('isdtotal_text'+no).value = t.value;
		}
			updateTotals(no);
		}else{
		document.getElementById('isdtotal_text'+no).value = totalValue.toFixed(2);
		updateTotals(no);
		}
		updateTotals(no);
		if(iamt.value != '' || samt.value != '' || camt.value != ''){
			$("#isdigsttax_text"+no).prop('required','required');$("#isdsgsttax_text"+no).prop('required','required');$("#isdcgsttax_text"+no).prop('required','required');
		}else{
			
			$("#isdigsttax_text"+no).removeAttr('required');$("#isdsgsttax_text"+no).removeAttr('required');$("#isdcgsttax_text"+no).removeAttr('required');$("#isdisdcess_text"+no).removeAttr('required');
		}
	
	}
	function findTotal(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var itype = $("#idInvType").val();
		var t;
		if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
			t = document.getElementById('advrcavailadj_text'+no);
		}else{
			t = document.getElementById('taxableamount_text'+no);
		}
		var totalValue = 0;
		if(t && t.value) {
			totalValue = parseFloat(t.value);
		}
		var cessamt = document.getElementById('cessamount_text'+no);
		if(cessamt && cessamt.value) {
			totalValue += parseFloat(cessamt.value);
		}
		var samt = document.getElementById('sgsttax_text'+no);
		if(samt && samt.value) {
			totalValue += parseFloat(samt.value);
		}
		var iamt = document.getElementById('igsttax_text'+no);
		if(iamt && iamt.value) {
			totalValue += parseFloat(iamt.value);
		}
		var camt = document.getElementById('cgsttax_text'+no);
		if(camt && camt.value) {
			totalValue += parseFloat(camt.value);
		}
		var exempted = document.getElementById('exempted_text'+no);
		var qty = document.getElementById('qty_text'+no);
		if(exempted && exempted.value && qty && qty.value) {
			totalValue += (parseFloat(exempted.value))*(parseFloat(qty.value));
		}
		if(itype != 'Advance Adjusted Detail' && $('#includetax').is(":checked")){
			var advt = document.getElementById('rate_text'+no);
			var tt = document.getElementById('total_text'+no);
			if(itype == 'Advances'){
				if(tt){
					if(advt && advt.value) {
						 document.getElementById('total_text'+no).value = advt.value;
					}
				}
			}else{
				var taxableValue = 0;
				if(advt && advt.value && qty && qty.value) {
					taxableValue = parseFloat(qty.value)*parseFloat(advt.value);
				}
				if(tt){
					document.getElementById('total_text'+no).value = taxableValue;
				}
			}
			updateTotals(no);
		}else if(revtype == 'Reverse'){
			if(t && t.value) {
			  document.getElementById('total_text'+no).value = t.value;
			}
			updateTotals(no);
		}else{
			if(totalValue) {
				var tt = document.getElementById('total_text'+no);
				if(tt){
					tt.value = totalValue.toFixed(2);
				}
				if(itype == 'Exports'){
					var excrate = document.getElementById('exchange_Rate');
					if(excrate){
						var extotalvalue = 0;
						if(excrate.value && excrate.value > 0){
							extotalvalue = parseFloat(totalValue)/parseFloat(excrate.value);
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
						}else{
							extotalvalue = 0;
							if(document.getElementById('curtotal_text'+no)){
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
							}
						}
					}
				}
			}else{
				totalValue = 0.00;
				var tt = document.getElementById('total_text'+no);
				if(tt){
					tt.value = totalValue.toFixed(2);
				}
				if(itype == 'Exports'){
					var excrate = document.getElementById('exchange_Rate');
					if(excrate){
						var extotalvalue = 0;
						if(excrate.value && excrate.value > 0){
							extotalvalue = parseFloat(totalValue)/parseFloat(excrate.value);
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
						}else{
							extotalvalue = 0;
							if(document.getElementById('curtotal_text'+no)){
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
							}
						}
					}
				}
			}
		updateTotals(no);
		
		}
	}
	
	function updateNillTotals(no) {
		var retType = $("#retType").val();
		var table=document.getElementById("sortable_table");
		var iRows=rowCount+1;
		var totTaxable = 0,  totTotal = 0;
		for(var i=1;i<iRows;i++) {
			if(i == no) {
				var totalTxbl = document.getElementById('taxableamount_text'+i);
					totTaxable+=parseFloat(totalTxbl.value);
				var total = document.getElementById('total_text'+i);
				if(total.value) {
					totTotal+=parseFloat(total.value);
				}
			} else {
				var totalTxbl = document.getElementById('taxableamount_text'+i);
				if(totalTxbl.value) {
					totTaxable+=parseFloat(totalTxbl.value);
				}
				var total = document.getElementById('total_text'+i);
				if(total.value) {
					totTotal+=parseFloat(total.value);
				}
			}
		}
		//var roundOffTotal_check=document.getElementById("roundOffTotal").checked = true;
		if($('#roundOffTotalChckbox').is(':checked')) {
			$('#idTotal').html(Math.round(parseFloat(totTotal)).toFixed(2));
			$('#roundOffTotalValue').val((Math.round(totTotal)-totTotal).toFixed(2));
		}else if(!$('#roundOffTotalChckbox').is(':checked')){
			$('#idTotal').html(parseFloat(totTotal));
			$('#roundOffTotalValue').val('');
		}
		
		$('#totTotal').html(parseFloat(totTotal));
		$('#totTaxable').html(parseFloat(totTaxable));
		$('#idTotal').html(parseFloat(totTotal));
		$('#hiddenroundOffTotalValue').val(totTotal);
		$('#cdn_taxableamount').val(parseFloat(totTaxable));
		//$('#hiddenroundOffTotalValue').val(totTotal);
		$(".indformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
		tdstcscal();
		if(retType == 'GSTR2' || retType == 'Purchase Register'){
			tdscal();
		}
	}
	
	function updateTotals(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var table=document.getElementById("sortable_table");
		var itype = $("#idInvType").val();
		var retType = $("#retType").val();
		var iRows=rowCount+1;
		if (itype == 'ISD'){
			iRows = 5;
		}else if(itype == 'ITC Reversal'){
			iRows = 8;
		}else{
			iRows=rowCount+1;
		}
		var totTaxable = 0, totIGST = 0, totCGST = 0, totSGST = 0, totCESS = 0, totTotal = 0, totITCCGST = 0, totITCSGST = 0, totITCIGST = 0, totITCCESS = 0, totISDCESS = 0, totAdvRemAmt = 0,totCurTotal = 0;;
		for(var i=1;i<iRows;i++) {
			if(i == no) {
				var totalTxbl , total , totalIGST , totalCGST , totalSGST, totalAdvRemAmount,totalCurencyAmount;
				totalCurencyAmount= document.getElementById('curtotal_text'+i);
				if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
					totalTxbl = document.getElementById('advrcavailadj_text'+i);
					totalAdvRemAmount = document.getElementById('advremaingamount_text'+i);
				}else if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalTxbl = document.getElementById('isdtaxableamount_text'+i);
				}else{
					totalTxbl = document.getElementById('taxableamount_text'+i);
				}
				if(totalTxbl && totalTxbl.value) {
					totTaxable+=parseFloat(totalTxbl.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
				totalIGST = document.getElementById('isdigsttax_text'+i);
				if(totalIGST && totalIGST.value) {
					totIGST+=parseFloat(totalIGST.value);
				}
				totalCGST = document.getElementById('isdcgsttax_text'+i);
				if(totalCGST && totalCGST.value) {
					totCGST+=parseFloat(totalCGST.value);
				}
				totalSGST = document.getElementById('isdsgsttax_text'+i);
				if(totalSGST && totalSGST.value) {
					totSGST+=parseFloat(totalSGST.value);
				}
				}else{
					var totalIGST = document.getElementById('igsttax_text'+i);
					var totalCGST = document.getElementById('cgsttax_text'+i);
					var totalSGST = document.getElementById('sgsttax_text'+i);
					if(totalIGST && totalIGST.value) {
						totIGST+=parseFloat(totalIGST.value);
						totIGST+=parseFloat(totalCGST.value);
						totIGST+=parseFloat(totalSGST.value);
					}
				}
				var totalCESS = document.getElementById('cessamount_text'+i);
				
				
				if(totalAdvRemAmount && totalAdvRemAmount.value) {
					totAdvRemAmt+=parseFloat(totalAdvRemAmount.value);
				}

				
				if(totalCESS && totalCESS.value){
				if(totalCESS.value) {
					totCESS+=parseFloat(totalCESS.value);
				}
				}
				var totalISDCESS = document.getElementById('isdisdcess_text'+i);
				if(totalISDCESS && totalISDCESS.value){
				if(totalISDCESS.value) {
					totISDCESS+=parseFloat(totalISDCESS.value);
				}
				}
				var totalITCCGST = document.getElementById('cgstitc_text'+i);
				var totalITCSGST = document.getElementById('sgstitc_text'+i);
				var totalITCIGST = document.getElementById('igstitc_text'+i);
				var totalITCCESS = document.getElementById('cessitc_text'+i);
				if(totalITCIGST && totalITCIGST.value) {
					totITCIGST+=parseFloat(totalITCCGST.value);
					totITCIGST+=parseFloat(totalITCSGST.value);
					totITCIGST+=parseFloat(totalITCIGST.value);
					totITCIGST+=parseFloat(totalITCCESS.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
				total = document.getElementById('isdtotal_text'+i);
				}else{
				total = document.getElementById('total_text'+i);
				}
				if(total && total.value) {
					totTotal+=parseFloat(total.value);
				}
				if(totalCurencyAmount && totalCurencyAmount.value) {
					totCurTotal+=parseFloat(totalCurencyAmount.value);
				}
			} else {
				var totalTxbl , total , totalIGST , totalCGST , totalSGST, totalAdvRemAmount,totalCurencyAmount;
				totalCurencyAmount= document.getElementById('curtotal_text'+i);
		      	if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
					totalTxbl = document.getElementById('advrcavailadj_text'+i);
					totalAdvRemAmount = document.getElementById('advremaingamount_text'+i);
				}else if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalTxbl = document.getElementById('isdtaxableamount_text'+i);
				}else{
					totalTxbl = document.getElementById('taxableamount_text'+i);
				}
		      	if(totalAdvRemAmount && totalAdvRemAmount.value) {
					totAdvRemAmt+=parseFloat(totalAdvRemAmount.value);
				}
				if(totalTxbl && totalTxbl.value) {
					totTaxable+=parseFloat(totalTxbl.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
				totalIGST = document.getElementById('isdigsttax_text'+i);
				}else{
				totalIGST = document.getElementById('igsttax_text'+i);
				}
				if(totalIGST && totalIGST.value) {
					totIGST+=parseFloat(totalIGST.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
				totalCGST = document.getElementById('isdcgsttax_text'+i);
				if(totalCGST && totalCGST.value) {
					totCGST+=parseFloat(totalCGST.value);
				}
				}else{
				totalCGST = document.getElementById('cgsttax_text'+i);
				if(totalCGST && totalCGST.value) {
					totIGST+=parseFloat(totalCGST.value);
				}
				}
				
				if(itype == 'ISD' || itype == 'ITC Reversal'){
				totalSGST = document.getElementById('isdsgsttax_text'+i);
				if(totalSGST && totalSGST.value) {
					totSGST+=parseFloat(totalSGST.value);
				}
				}else{
				totalSGST = document.getElementById('sgsttax_text'+i);
				if(totalSGST && totalSGST.value) {
					totIGST+=parseFloat(totalSGST.value);
				}
				}
				
				var totalCESS = document.getElementById('cessamount_text'+i);
				if(totalCESS && totalCESS.value){
				if(totalCESS.value) {
					totCESS+=parseFloat(totalCESS.value);
				}
				}
				var totalISDCESS = document.getElementById('isdisdcess_text'+i);
				if(totalISDCESS && totalISDCESS.value){
				if(totalISDCESS.value) {
					totISDCESS+=parseFloat(totalISDCESS.value);
				}}
				var totalITCIGST = document.getElementById('itc_tax_tot'+i);
				if(totalITCIGST != null && totalITCIGST.value) {
					totITCIGST+=parseFloat(totalITCIGST.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
				total = document.getElementById('isdtotal_text'+i);
				}else{
				total = document.getElementById('total_text'+i);
				}
				if(total && total.value) {
					totTotal+=parseFloat(total.value);
				}
				if(totalCurencyAmount && totalCurencyAmount.value) {
					totCurTotal+=parseFloat(totalCurencyAmount.value);
				}
			}
		}
		$('#totTaxable ,#isdtotTaxable').html(parseFloat(totTaxable));
		$('#totIGST ,#isdtotIGST').html(parseFloat(totIGST));
		$('#totCGST ,#isdtotCGST').html(parseFloat(totCGST));
		$('#totSGST ,#isdtotSGST').html(parseFloat(totSGST));
		$('#totCESS').html(parseFloat(totCESS));
		$('#isdtotisdcess').html(parseFloat(totISDCESS));
		$('#totITCCGST').html(parseFloat(totITCCGST));
		$('#totITCSGST').html(parseFloat(totITCSGST));
		$('#totITCIGST').html(parseFloat(totITCIGST));
		$('#totITCCESS').html(parseFloat(totITCCESS));
		$('#totCurAmt').html(parseFloat(totCurTotal));
		$('#totAdvRemaining').html(parseFloat(totAdvRemAmt));
		
		
		//$('#totTotal, #idTotal').html(parseFloat(totTotal));
		if(revtype != 'Reverse'){
		$('#totTotal ,#isdtotTotal').html(parseFloat(totTotal));
		}else{
			$('#totTotal ,#isdtotTotal').html(parseFloat(totTaxable));
		}
		$('#cdn_taxableamount').val(parseFloat(totTaxable));
		if($('#roundOffTotalChckbox').is(':checked')) {
			$('#idTotal').html(Math.round(parseFloat(totTotal)).toFixed(2));
			$('#roundOffTotalValue').val((Math.round(totTotal)-totTotal).toFixed(2));
		}else if(!$('#roundOffTotalChckbox').is(':checked')){
			if(retType == "EWAYBILL"){
				var othervalue = document.getElementById('otherValue');
				if(othervalue && othervalue.value){
					if(othervalue.value) {
							totTotal+=parseFloat((othervalue.value).replace(/,/g , ''));
					}
				}
			}
			$('#idTotal').html(parseFloat(totTotal));
			$('#roundOffTotalValue').val('');
		}
		$('#hiddenroundOffTotalValue').val(parseFloat(totTotal));
		$('#cdn_taxableamount').val(parseFloat(totTaxable));
		$(".indformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
		tdstcscal();
		if(retType == 'GSTR2' || retType == 'Purchase Register'){
			tdscal();
		}
	}
	function findOtherValue(){
		var totTotal = parseFloat($('#totTotal').html().replace(/,/g , ''));
		var othervalue = document.getElementById('otherValue');
				if(othervalue && othervalue.value){
					if(othervalue.value) {
							totTotal+=parseFloat((othervalue.value).replace(/,/g , ''));
					}
				}
				$('#idTotal').html(parseFloat(totTotal));
				$('#hiddenroundOffTotalValue').val(parseFloat(totTotal));
				$(".indformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
	}
	function updateDetailsAfterDelete_row(no){
		var invtype = $('#idInvType').val();
		var retType = $("#retType").val();
		var totalTaxable=document.getElementById("totTaxable").innerHTML;
		var totalRemAmt = document.getElementById("totAdvRemaining").innerHTML;
		var taxableamount = '';
		var taxableamount1 = '';
		var advRemAmt = '';
		var advRemAmt1 = '';
		if(invtype == 'Advance Adjusted Detail' || invtype == 'TXPA'){
			taxableamount=document.getElementById("advrcavailadj_text"+no).value;
			taxableamount1=$("#advrcavailadj_text"+no).val();
			advRemAmt=document.getElementById("advremaingamount_text"+no).value;
			advRemAmt1 = $("#advremaingamount_text"+no).val();
		}else{
			taxableamount=document.getElementById("taxableamount_text"+no).value;
			taxableamount1=$("#taxableamount_text"+no).val();
		}
		if(taxableamount != '' && taxableamount>0){
			totalTaxable = totalTaxable.replace(/,/g , '');
			totalTaxable-=parseFloat(taxableamount).toFixed(2);
		}else if(taxableamount1>0){
			taxableamount1 = taxableamount1.replace(/,/g , '');
			totalTaxable = totalTaxable.replace(/,/g , '');
			totalTaxable-=parseFloat(taxableamount1);
		}
		 if(advRemAmt1>0){
			 advRemAmt1 = advRemAmt1.replace(/,/g , '');
			 totalRemAmt = totalRemAmt.replace(/,/g , '');
			 totalRemAmt-=parseFloat(advRemAmt1);
			}
		if(/[,\-]/.test(totalTaxable)){
		totalTaxable = totalTaxable.replace(/,/g , '');
		}
		if(/[,\-]/.test(totalRemAmt)){
			totalRemAmt = totalRemAmt.replace(/,/g , '');
			}
		$('#totTaxable').html(parseFloat(totalTaxable));
		$('#totAdvRemaining').html(parseFloat(totalRemAmt));
		
		var totalIGST=document.getElementById("totIGST").innerHTML;
		var igstTax=document.getElementById("igsttax_text"+no).value;
		var igstTax1=$("#igsttax_text"+no).val();
		
		if(igstTax != '' && igstTax>0){
			totalIGST = totalIGST.replace(/,/g , '');
			igstTax = igstTax.replace(/,/g , '');
			totalIGST-=parseFloat(igstTax);
		}else if(igstTax1>0){
			totalIGST = totalIGST.replace(/,/g , '');
			igstTax1 = igstTax1.replace(/,/g , '');
			totalIGST-=parseFloat(igstTax1);
		}
		if(/[,\-]/.test(totalIGST)){
			totalIGST = totalIGST.replace(/,/g , '');
		}
		$('#totIGST').html(parseFloat(totalIGST));
		
		var totalCGST=document.getElementById("totIGST").innerHTML;
		var cgstTax=document.getElementById("cgsttax_text"+no).value;
		var cgstTax1=$("#cgsttax_text"+no).val();
		
		if(cgstTax != '' && cgstTax>0){
			totalCGST = totalCGST.replace(/,/g , '');
			cgstTax = cgstTax.replace(/,/g , '');
			totalCGST-=parseFloat(cgstTax);
		}else if(cgstTax1>0){
			totalCGST = totalCGST.replace(/,/g , '');
			cgstTax1 = cgstTax1.replace(/,/g , '');
			totalCGST-=parseFloat(cgstTax1);
		}
		if(/[,\-]/.test(totalCGST)){
			totIGST = totalCGST.replace(/,/g , '');
		}
		$('#totIGST').html(parseFloat(totalCGST));
		
		var totalSGST=document.getElementById("totIGST").innerHTML;
		var sgstTax=document.getElementById("sgsttax_text"+no).innerText;
		var sgstTax1=$("#sgsttax_text"+no).val();
		
		if(sgstTax != '' && sgstTax>0){
			totalSGST = totalSGST.replace(/,/g , '');
			sgstTax = sgstTax.replace(/,/g , '');
			totalSGST-=parseFloat(sgstTax);
		}else if(sgstTax1>0){
			totalSGST = totalSGST.replace(/,/g , '');
			sgstTax1 = sgstTax1.replace(/,/g , '');
			totalSGST-=parseFloat(sgstTax1);
		}
		if(/[,\-]/.test(totalSGST)){
			totIGST = totalSGST.replace(/,/g , '');
		}
		$('#totIGST').html(parseFloat(totalSGST));
		
		var totalCESS=document.getElementById("totCESS").innerHTML;
		var cessamount=document.getElementById("cessamount_text"+no).value;
		var cessamount1=$("#cessamount_text"+no).val();
		
		if(cessamount != '' && cessamount>0){
			totalCESS = totalCESS.replace(/,/g , '');
			cessamount = cessamount.replace(/,/g , '');
			totalCESS-=parseFloat(cessamount);
		}else if(cessamount1>0){
			totalCESS = totalCESS.replace(/,/g , '');
			cessamount1 = cessamount1.replace(/,/g , '');
			totalCESS-=parseFloat(cessamount1);
		}
		if(/[,\-]/.test(totalCESS)){
			totalCESS = totalCESS.replace(/,/g , '');
		}
		$('#totCESS').html(parseFloat(totalCESS));
		
		var totalITCCGST = document.getElementById("totITCIGST").innerHTML;
		// var itcCgst = document.getElementById("cgstitc_row"+no);
		var itcCgst1 = document.getElementById("cgstitc_text"+no);
		if(itcCgst1 != null){
			var itccgst1=itcCgst1.value;
		
		if(itccgst1>0){
			totalITCCGST = totalITCCGST.replace(/,/g , '');
			itccgst1 = itccgst1.replace(/,/g , '');
			totalITCCGST-=parseFloat(itccgst1);
		}
		}
		if(/[,\-]/.test(totalITCCGST)){
			totalITCCGST = totalITCCGST.replace(/,/g , '');
		}
		$('#totITCIGST').html(parseFloat(totalITCCGST));
		
		var totalITCSGST=document.getElementById("totITCIGST").innerHTML;
		// var itcSgst=document.getElementById("sgstitc_row"+no);
		var itcSgst1=document.getElementById("sgstitc_text"+no);
		
		if(itcSgst1 != null){
			var itcsgst1=itcSgst1.value;	
		if(itcsgst1>0){
			totalITCSGST = totalITCSGST.replace(/,/g , '');
			itcsgst1 = itcsgst1.replace(/,/g , '');
			totalITCSGST-=parseFloat(itcsgst1);
		}
		}
		if(/[,\-]/.test(totalITCSGST)){
			totalITCSGST = totalITCSGST.replace(/,/g , '');
		}
		$('#totITCIGST').html(parseFloat(totalITCSGST));
		
		var totalITCIGST=document.getElementById("totITCIGST").innerHTML;
		//var itcIgst=document.getElementById("igstitc_row"+no);
		var itcIgst1=document.getElementById("igstitc_text"+no);
		
		if(itcIgst1 != null){
		//var itcigst=itcIgst.innerText;
		var itcigst1=itcIgst1.value;
		if(itcigst1>0){
			totalITCIGST = totalITCIGST.replace(/,/g , '');
			itcigst1 = itcigst1.replace(/,/g , '');
			totalITCIGST-=parseFloat(itcigst1);
		}
		}
		if(/[,\-]/.test(totalITCIGST)){
			totalITCIGST = totalITCIGST.replace(/,/g , '');
		}
		$('#totITCIGST').html(parseFloat(totalITCIGST));
		
		var totalITCCESS=document.getElementById("totITCIGST").innerHTML;
		// var itcCess=document.getElementById("cessitc_row"+no);
		var itcCess1=document.getElementById("cessitc_text"+no);
		
		if(itcCess1 != null){
			//var itccess=itcCess.innerText;
			var itccess1=itcCess1.value;
		
		if(itccess1>0){
			totalITCCESS = totalITCCESS.replace(/,/g , '');
			itccess1 = itccess1.replace(/,/g , '');
			totalITCCESS-=parseFloat(itccess1);
		}
		}
		if(/[,\-]/.test(totalITCCESS)){
			totalITCCESS = totalITCCESS.replace(/,/g , '');
		}
		$('#totITCIGST').html(parseFloat(totalITCCESS));
		
		var totTotal=document.getElementById("totTotal").innerHTML;
		var total=document.getElementById("total_text"+no).value;
		var total1=$("#total_text"+no).val();
		
		if(total != '' && total>0){
			totTotal = totTotal.replace(/,/g , '');
			total = total.replace(/,/g , '');
			totTotal-=parseFloat(total);
		}else if(total1>0){
			totTotal = totTotal.replace(/,/g , '');
			total1 = total1.replace(/,/g , '');
			totTotal-=parseFloat(total1);
		}
		if(/[,\-]/.test(totTotal)){
		totTotal = totTotal.replace(/,/g , '');
		}
		//$('#totTotal, #idTotal').html(parseFloat(totTotal));
		$('#totTotal').html(parseFloat(totTotal));
		var curtotal = '';
		var curtotTotal=document.getElementById("totCurAmt").innerHTML;
		if(document.getElementById("curtotal_text"+no)){
			curtotal=document.getElementById("curtotal_text"+no).value;
		}
		var curtotal1=$("#curtotal_text"+no).val();
		
		if(curtotal != '' && curtotal>0){
			curtotTotal = curtotTotal.replace(/,/g , '');
			curtotal = curtotal.replace(/,/g , '');
			curtotTotal-=parseFloat(curtotal);
		}else if(curtotal1>0){
			curtotTotal = curtotTotal.replace(/,/g , '');
			curtotal1 = curtotal1.replace(/,/g , '');
			curtotTotal-=parseFloat(curtotal1);
		}
		if(/[,\-]/.test(curtotTotal)){
		curtotTotal = curtotTotal.replace(/,/g , '');
		}
		//$('#totTotal, #idTotal').html(parseFloat(totTotal));
		$('#totCurAmt').html(parseFloat(curtotTotal));
		
		if($('#roundOffTotalChckbox').is(':checked')) {
			$('#idTotal').html(Math.round(parseFloat(totTotal)).toFixed(2));
			$('#roundOffTotalValue').val((Math.round(totTotal)-totTotal).toFixed(2));
		}else if(!$('#roundOffTotalChckbox').is(':checked')){
			$('#idTotal').html(parseFloat(totTotal));
			$('#roundOffTotalValue').val('');
		}
		$('#hiddenroundOffTotalValue').val(totTotal);
		$('#cdn_taxableamount').val(parseFloat(totTaxable));
		$(".indformat").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
		tdstcscal();
		if(retType == 'GSTR2' || retType == 'Purchase Register'){
			tdscal();
		}
	}
	
	function delete_row(no) {
		if(SnilFlag){
			updateDetailsAfterDelete_row(no);
			}
			var table=document.getElementById("sortable_table");
			if(no > rowCount){
				no=no-1;
			}
			table.deleteRow(no+1);
			//$('#sortable_table').tableDnDUpdate();
			rowCount--;
			var retType = $('#retType').val();
			var invType = $('#idInvType').val();
			if(invType == 'Exports'){
				type = 'exp';
			}else if(retType == 'GSTR1' && (invType == 'Advance Adjusted Detail' || invtype == 'TXPA' || invType == 'Advances')){
				type = 'adv';
			}else if((retType == 'GSTR2' || 'Purchase Register') && (invType == 'Advance Adjusted Detail' || invType == 'Advances')){
				type = 'padv';
			}else{
				type = 'remain';
			}
			currRowIndex=null;
			var tablerows = $('#allinvoice tr').length;
				$("#allinvoice tr").each(function(index) {
				 $(this).attr('id',index+1);
				 $(this).find("#sno_row1").html(index+1);
				
				 var rowno = (index+1).toString();
				 var rownoo = (index).toString();
				 $(this).find('td').each (function() {
		   	    	 	var name = $(this).attr('id');
			   	    	if(name != undefined){
			   	    		if(rowno<10){
			   	    			name = name.slice(0, -1);
				   	    		}else{
				   	    			name = name.slice(0, -2);
				   	    		}
							name = name+rowno;
							$(this).attr('id',name);
							$(this).find('.input_itemDetails_txt').each (function() {
								var itemClass = $('.input_itemDetails_txt').attr('class');
								if(itemClass != undefined){
									if(rowno<10){
										itemClass = itemClass.slice(0, -1);
						   	    		}else{
						   	    			itemClass = itemClass.slice(0, -2);
						   	    		}
									itemClass = itemClass+rowno;
									$(this).attr('class',itemClass);
								}
							});
							$(this).find('.additem_box').each (function() {
								var additemid = $('.additem_box').attr('id');
								if(additemid != undefined){
									if(rowno<10){
										additemid = additemid.slice(0, -1);
						   	    		}else{
						   	    			additemid = additemid.slice(0, -2);
						   	    		}
									additemid = additemid+rowno;
									$(this).attr('id',additemid);
								}
							});
							$(this).find('.additem_box div').each (function() {
								var additemclass = $('.additem_box div').attr('class');
								if(additemclass != undefined){
									if(rowno<10){
										additemclass = additemclass.slice(0, -1);
						   	    		}else{
						   	    			additemclass = additemclass.slice(0, -2);
						   	    		}
									additemclass = additemclass+rowno;
									$(this).attr('class',additemclass);
								}
							});
							$(this).find('.additem_box input').each (function() {
								var additemclick = $('.additem_box input').attr('onclick');
								if(additemclick != 'item_delete'){
									var abcd = $(this).attr('onclick');
						   	    	abcd = replaceAt(abcd,12,rowno);
						   	    	$(this).attr('onclick',abcd);
								}else{
									var abcd = $(this).attr('onclick');
							   	    abcd = replaceAt(abcd,14,rowno);
							   	    $(this).attr('onclick',abcd);
								}
							});
							$(this).find('input').each (function() {
								var inputid = $(this).attr('id');
								var inputname = $(this).attr('name');
								if(inputname != undefined){
									if(inputname.indexOf("items[") >= 0) {
										if(rownoo == '9'){
											inputname = inputname.replace('10',' ');
										}
										inputname = replaceAt(inputname,6,rownoo);
										$(this).attr('name',inputname);
										
									}
								}
								if(inputid != undefined){
									if(rowno<10){
										inputid = inputid.slice(0, -1);
						   	    		}else{
						   	    			inputid = inputid.slice(0, -2);
						   	    		}
									inputid = inputid+rowno;
									$(this).attr('id',inputid);
								}
								
								var qty = $(this).attr('class');
								var rate = $(this).attr('class');
								if(invType == 'Nil Supplies'){
									
									if(qty != undefined && qty.indexOf("qtyval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
							   	    	
							   	    	var change = $(this).attr('onchange');
							   	    	change = replaceAt(change,12,rowno);
							   	    	$(this).attr('onchange',change);
									}
									if(rate != undefined && rate.indexOf("rateval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,21,rowno);
							   	    	$(this).attr('onkeyup',abcd);
									}
								}else{
									if(qty != undefined && qty.indexOf("qtyval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,17,rowno);
							   	    	$(this).attr('onkeyup',abcd);
							   	    	
							   	    	var change = $(this).attr('onchange');
							   	    	change = replaceAt(change,12,rowno);
							   	    	$(this).attr('onchange',change);
									}
									if(rate != undefined && rate.indexOf("rateval") >= 0) {
										var abcd = $(this).attr('onkeyup');
							   	    	abcd = replaceAt(abcd,17,rowno);
							   	    	$(this).attr('onkeyup',abcd);
									}
								}
								
								
								var discount = $(this).attr('class');
								if(discount != undefined && discount.indexOf("disval") >= 0) {
									var abcd = $(this).attr('onkeyup');
						   	    	abcd = replaceAt(abcd,17,rowno);
						   	    	$(this).attr('onkeyup',abcd);
								}
								var cessval = $(this).attr('class');
								if(cessval != undefined && cessval.indexOf("cessval") >= 0) {
									var abcd = $(this).attr('onkeyup');
						   	    	abcd = replaceAt(abcd,15,rowno);
						   	    	$(this).attr('onkeyup',abcd);
								}
								var advrcavialadjval = $(this).attr('class');
								if(advrcavialadjval != undefined && advrcavialadjval.indexOf("advrcavialadjval") >= 0) {
									var abcd = $(this).attr('onkeyup');
						   	    	abcd = replaceAt(abcd,16,rowno);
						   	    	$(this).attr('onkeyup',abcd);
						   	    	var abcde = $(this).attr('onblur');
						   	    	abcde = replaceAt(abcde,16,rowno);
						   	    	$(this).attr('onblur',abcde);
								}
								
								
							});
							$(this).find('select').each (function() {
								var inputid = $(this).attr('id');
								var selectname = $(this).attr('name');
								if(selectname != undefined){
									if(selectname.indexOf("items[") >= 0) {
										if(rownoo == '9'){
											selectname = selectname.replace('10',' ');
										}
										selectname = replaceAt(selectname,6,rownoo);
										$(this).attr('name',selectname);
									}
								}
								if(inputid != undefined){
									if(rowno<10){
										inputid = inputid.slice(0, -1);
						   	    		}else{
						   	    			inputid = inputid.slice(0, -2);
						   	    		}
									inputid = inputid+rowno;
									$(this).attr('id',inputid);
								}
								
								var taxrateval = $(this).attr('class');
								if(taxrateval != undefined && taxrateval.indexOf("taxrateval") >= 0) {
									var abcd = $(this).attr('onchange');
						   	    	abcd = replaceAt(abcd,14,rowno);
						   	    	$(this).attr('onchange',abcd);
								}
								
								var itcval = $(this).attr('class');
								if(itcval != undefined && itcval.indexOf("itcval") >= 0) {
									var abcd = $(this).attr('onchange');
						   	    	abcd = replaceAt(abcd,28,rowno);
						   	    	$(this).attr('onchange',abcd);
								}
								itcval
							});
							$(this).find('div.easy-autocomplete-container').each (function() {
								var inputid = $(this).attr('id');
								if(inputid != undefined){
									if(rowno<10){
										inputid = inputid.slice(0, -1);
						   	    		}else{
						   	    			inputid = inputid.slice(0, -2);
						   	    		}
									inputid = inputid+rowno;
									$(this).attr('id',inputid);
								}
							});
						}else{
							$(this).find('input').each (function() {
								var inputname = $(this).attr('id');
								var inputname1 = $(this).attr('class');
								if(inputname != undefined){
									if(rowno<10){
										inputname = inputname.slice(0, -1);
						   	    		}else{
						   	    			inputname = inputname.slice(0, -2);
						   	    		}
									inputname = inputname+rowno;
									$(this).attr('id',inputname);
								}
							});
							$(this).find('a').each (function() {
								var aname = $(this).attr('id');
								if(aname != undefined){
									if(rowno<10){
										aname = aname.slice(0, -1);
						   	    		}else{
						   	    			aname = aname.slice(0, -2);
						   	    		}
								aname = aname+rowno;
								$(this).attr('id',aname);
								}
								var det = $(this).attr('class');
								if(det != 'item_delete'){
									var abcd = $(this).attr('onclick');
						   	    	abcd = replaceAt(abcd,9,rowno);
						   	    	$(this).attr('onclick',abcd);
								}else{
									var abcd = $(this).attr('onclick');
							   	    abcd = replaceAt(abcd,11,rowno);
							   	    $(this).attr('onclick',abcd);
								}
							});
						}
			   	      });
				 if(index+1 < tablerows){
					 itmoptions(index+1,type,retType);
				 }
			});
				$('form[name="salesinvoceform"]').validator('update');
				var tableng = $('#allinvoice tr').length;
				
				if(tableng == '2'){
				$('.item_delete').hide();
				}else{
				$('.item_delete').show();	
				}
	}
	
	function replaceAt(a,index,replacement){
		return a.substr(0, index) + replacement+ a.substr(index + replacement.length);
	}
	
	function updateNames(no) {
		populateItemNames();
		getItemCustomFields(customFieldsData,"aItem",no);
		$('#invoiceModal').css("z-index","1033");
		$('.modal-backdrop.show').css("display","block");
		$('#itemDescription').val($('.itemname'+no).val());
		$('.errormsg').css('display','block').html('');
		$(".form-group").removeClass('has-error has-danger');
		$('.help-block.with-errors').html('');
		$('#unitempty').css('display','none');
		$('.rowno').val(no);
		$('#addItemModal').css('display','block');
		$('#addItemModal').addClass('show');
		$('#addItemModal').modal('show');
	}
	function populateItemNames(){
		var items = {
				url: function(phrase) {
					return contextPath+"/srchItemNames?query="+ phrase + "&format=json";
				},
				getValue: "description",
				list: {
					onChooseEvent: function() {
					}
				}
		};
		$("#itemDescription").easyAutocomplete(items);
	}
	function closeAddCustomer(modalId){
		var inyType5 = $('#idInvType').val();
		if(inyType5 == 'B2B' || inyType5 == 'B2C'  || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA' || inyType5 == 'Advances' || inyType5 == 'B2B Unregistered' || inyType5 == 'Nil Supplies' || inyType5 == 'Import Goods' || inyType5 == 'Import Services' || inyType5 == 'ITC Reversal' || inyType5 == 'ISD' || inyType5 == '' || modalId == 'addEcommerceGSTIN'){
			$('#invoiceModal').css("z-index","");
		}
		$('.itmcustsave').removeClass('btn-loader-blue');
		if(modalId == "addItemModal"){
			closeitempopup();
		}
		$('#'+modalId).modal('hide');
		$('.modal-d ').css('display','none');
	}
	function closeitempopup(){
		$('.itemCode_txt').text("Item Code");
		$('.itemName_txt').text("Item Name");
		$('.item_grpCode_txt').text("Item Group Code");
		$('.item_grpName_txt').text("Item Group Name");
		$('.item_description_txt').text("Item Description");
		$('.hsnSacTxt').text("HSN Code");
		$('.itemDescription').attr("placeholder","Item Description");
		$('.itemSalesPriceTxt').text('Item Sale Price');
		$('.item_description_txt').css("margin-left","2px");
		$('.descInput').css({"padding-left":"52px"});
		$('.descInput').addClass("pr-0");
		$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').removeClass("d-none");
		$('.imagesDiv').hide();
		$('.additemNumber').val("");
		$('.itemName').val("");
		$('.itemGroupNumber').val("");
		$('.itemGroupDescription').val("");
		$('.itemDescription').val("");
		$('.asOndate').val("");
		$('#itemsalePrice').val("");
		$('#saleGstType').val("");
		$('#itempurchasePrice').val("");
		$('#purchaseGstType').val("");
		$('#itemwholeSalePrice').val("");
		$('#itemmrpPrice').val("");
		
		
		$('.itemCategory').val("");
		$('.unitofMeasurement').val("");
		$('.recommendedSellingPriceForB2B').val("");
		/* $('.recommendedSellingPriceForB2C').val(item.sellingpriceb2c); */
		$('.Discount').val("");
		$('.HSNCode').val("");
		$('.saftlyStockLevel').val("");
		$('.taxrate').val("");
			$(".discount_val").hide();
			$('.discountvalue').val("");
			$('.addDiscount').prop("checked",false);
			$(".exempted_val").hide();
			$('.addExempted').prop("checked",false);
			$('.exmeptedvalue').val("");
		$('#recommendedRetailPriceForB2B').val("");
		$('#wholesalePriceForB2B').val("");
		$('#mrpPriceForB2B').val("");
		$('#itemopeningStockLevel').val("");
		$('#itemsaftlyStockLevel').val("");
		$('#itemComments').val("");
			$('#numType').prop("checked",true);
			$('#itemreorderNo').prop("checked",true);
			$('#itemproduct_type').prop("checked",true);
		
		var exp_val = $('.exmeptedvalue').val();
		if(exp_val == ''){
			$('.addExempted').prop('checked',false);
			$(".exempted_val").hide();
		}else{
			$('.addExempted').prop('checked',true);
			$(".exempted_val").show();
		}
		var dis_val = $('.discountvalue').val();
		if(dis_val == ''){
			$('.addDiscount').prop('checked',false);
			$(".discount_val").hide();
		}else{
			$('.addDiscount').prop('checked',true);
			$(".discount_val").show();
		}
		//$('input[type="hidden"][id="itemid"]','input[type="hidden"][id="itemcurrentstock"]','input[type="hidden"][id="itemtotalqtyusage"]').remove();
		//$('input[type="hidden"][id="itemid"],input[type="hidden"][id="itemcurrentstock"],input[type="hidden"][id="itemtotalqtyusage"]').remove();
		//$('form[name="userform"]').validator('update');
	}
	function closePaymentModal(){
		var inyType5 = $('#idInvType').val();
		if(inyType5 == 'B2B' || inyType5 == 'B2C'  || inyType5 == 'Exports' || inyType5 == 'Credit/Debit Notes' || inyType5 == 'Credit/Debit Note for Unregistered Taxpayers' || inyType5 == 'Advance Adjusted Detail' || inyType5 == 'TXPA' || inyType5 == 'Advances' || inyType5 == 'B2B Unregistered' || inyType5 == 'Nil Supplies' || inyType5 == 'Import Goods' || inyType5 == 'Import Services' || inyType5 == 'ITC Reversal' || inyType5 == 'ISD' || inyType5 == ''){
			$('#invoiceModal').css("z-index","");
		}
		$('.payment_table tbody').find("tr:gt(0)").remove();
		$('#paymentsModal').modal('hide');
	}
	function samebilladdresscheck() {
		if($('#samebilladdress').is(":checked")){
			var txt = $('#billingAddress').val();
			$('#shippingAddress').val(txt);
				$('#billingAddress').keyup(function(){
				var txt1 = $('#billingAddress').val();
				$('#shippingAddress').val(txt1);
			});
				 $('#billingAddress').blur(function() {
					var txt1 = $('#billingAddress').val();
					if($('#samebilladdress').is(":checked")){
						$('#shippingAddress').val(txt1);
					}
			  }); 
			}else{
				$('#shippingAddress').val('');
				$('#billingAddress').keyup(function(){
					$('#shippingAddress').val('');
				});
			}
	} 
	function tdschange(){
		var tdspercent = $('#tdssection').val();
		var abcd = tdspercent.substring(tdspercent.lastIndexOf("(")+1,tdspercent.lastIndexOf(")"));
		$('#tds_percent').val(abcd);
		tdscal();
	}
	function tdscal(){
		var tdspercent = $('#tds_percent').val();
		var tdscalval = 0;
		tdscalval = $('#totTaxable').text().replace(/,/g , '')*tdspercent/100;
		 /*if (otherconfigdetails.enableTCS == true) {
			 tdscalval = $('#totTaxable').text().replace(/,/g , '')*tdspercent/100;
	     }else if (otherconfigdetails.enableTCS == false || otherconfigdetails.enableTCS == undefined){
	    	 tdscalval = $('#totTotal').text().replace(/,/g , '')*tdspercent/100;
	     }*/
		$('#tdsfield').text(parseFloat(tdscalval).toFixed(2));
		var totinvval = $('#totTotal').text().replace(/,/g , '');
		$('#tds_amount').val(parseFloat(tdscalval).toFixed(2));
		$('#tdsamt').val(parseFloat(tdscalval).toFixed(2));
		
		var tcscalval = 0;
		if($('#tcsval').is(":checked")){
			tcscalval = $('#tcsamt').val();
		}
		$('#invvalwith_tds').html(parseFloat(totinvval)+parseFloat(tcscalval)-parseFloat(tdscalval));
		$('#invvalwithtds').html(parseFloat(totinvval)+parseFloat(tcscalval));
		
		OSREC.CurrencyFormatter.formatAll({
			selector: '.tcsindformat'
		});
	}
	function changeTdsAmount(){
		var tdsTot = 0;
		var tcsTot = 0;
		var tdsVal = 0;
		if($('#tds_amount').val() != ""){
			tdsVal = $('#tds_amount').val().replace(/,/g , '');
		}
		var tot = $('#totTotal').html();
		if(tot != ""){
			tot = tot.replace(/,/g , '');
		}
		if($('#tcsval').is(":checked")){
			if($('#tcsamt').val() != "" && $('#tcsamt').val() != undefined){
				tcsTot = $('#tcsamt').val().replace(/,/g , '');
			}else{
				tcsTot = $('#tcsfield').html().replace(/,/g , '');
			}
		}
		$('#tdsamt').val(parseFloat(tdsVal).toFixed(2));
		tdsTot = formatNumber((parseFloat(tot)+parseFloat(tcsTot)-parseFloat(tdsVal)).toFixed(2));
		$('#invvalwith_tds').html(tdsTot);
	}
	function tdstcschange(){
		var tcspercent = $('#tdstcssection').val();
		var abcd = tcspercent.substring(tcspercent.lastIndexOf("(")+1,tcspercent.lastIndexOf(")"));
		$('#tcs_percent').val(abcd);
		tdstcscal();
	}
	function tdstcscal(){
		var rettype = $('#retType').val();
		var tcspercent = $('#tcs_percent').val();
		
		var tcscalval = 0;
		 if (otherconfigdetails.enableTCS == true) {
	            tcscalval = $('#totTaxable').text().replace(/,/g , '')*tcspercent/100;
	     }else if (otherconfigdetails.enableTCS == false || otherconfigdetails.enableTCS == undefined){
				tcscalval = $('#totTotal').text().replace(/,/g , '')*tcspercent/100;
	     }
		//var kjsad = $('#totTotal').text().replace(/,/g , '')*tcspercent/100;
		var totinvval = $('#totTotal').text().replace(/,/g , '');
		if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
			$('#tdsamount').val(formatNumber(parseFloat(tcscalval).toFixed(2)));
			$('#tcsamt').val(parseFloat(tcscalval).toFixed(2));
			$('#tcsfield').html(parseFloat(tcscalval).toFixed(2));
			$('#invvalwithtcs').html(parseFloat(totinvval)+parseFloat(tcscalval));
		}else{
			//if($('#tcs_val').is(":checked")){
				$('#tdsamount').val(formatNumber(parseFloat(tcscalval).toFixed(2)));
				$('#tcsamt').val(parseFloat(tcscalval).toFixed(2));
				$('#tcsfield').html(parseFloat(tcscalval).toFixed(2));
				var tdscalval = 0;
				if($('#tdsval').is(":checked")){
					if($('#tdsamt').val() != ""){
						tdscalval = $('#tdsamt').val();
					}
					$('#invvalwith_tds').html(parseFloat(totinvval)+parseFloat(tcscalval)-parseFloat(tdscalval));
				}else{
					$('#invvalwithtds').html(parseFloat(totinvval)+parseFloat(tcscalval));
				}
				
			/*}else{
				$('#tdsamount').val(formatNumber(parseFloat(tcscalval).toFixed(2)));
				$('#invvalwithtds').html(parseFloat(totinvval)-parseFloat(tcscalval));
			}*/
		}
		OSREC.CurrencyFormatter.formatAll({
			selector: '.tcsindformat'
		});
	}
	function changeTcsAmount(){
		var tcsTot = 0;
		var tcsVal = 0;
		var rettype = $('#retType').val();
		if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
			if($('#tdsamount').val() != ""){
				tcsVal = $('#tdsamount').val().replace(/,/g , '');
			}
			$('#tcsamt').val(parseFloat(tcsVal).toFixed(2));
			var tot = $('#totTotal').text().replace(/,/g , '');
			tcsTot = formatNumber((parseFloat(tcsVal)+parseFloat(tot)).toFixed(2));
			$('#invvalwithtcs').html(tcsTot);
		}else{
			if($('#tdsamount').val() != ""){
				tcsVal = $('#tdsamount').val().replace(/,/g , '');
			}
			
			$('#tdsamt').val(parseFloat(tcsVal).toFixed(2));
			var tdsVal = 0;
			var tot = $('#totTotal').text().replace(/,/g , '');
			if($('#tdsval').is(":checked")){
				if($('#tds_amt').val() != "" && $('#tds_amt').val() != undefined){
					tdsVal = $('#td_samt').val().replace(/,/g , '');
				}else{
					tdsVal = $('#tdsfield').html().replace(/,/g , '');
				}
				tcsTot = formatNumber((parseFloat(tcsVal)+parseFloat(tot)-parseFloat(tdsVal)).toFixed(2));
				$('#invvalwith_tds').html(tcsTot);
			}else{
				tcsTot = formatNumber((parseFloat(tcsVal)+parseFloat(tot)).toFixed(2));
				$('#invvalwithtds').html(tcsTot);
			}
			
		}
		
	}
	function tcscheckval(){
		//$('#tdstcssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
		$('#tdstcssection').append($("<option></option>").attr("value","206C(1)").text("206C"));
		if($('#tcsval').is(":checked")){
			$("#tds_val").attr("disabled",false);
			$("#tcs_val").attr("disabled",false);
			$(".tcsorTdsType").removeClass('disclass');
			$('#tcsval').val('true');
			$('#tdstcssection ,#tcs_percent').removeAttr("readonly");
			$('#tdstcssection').removeAttr("disabled");
			var rettype = $('#retType').val();
			var invtype = $('#idInvType').val();
			if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
				var tds = "TCS";
				$('#allinvoicettfoot').append('<tr class="apply_tcsOtds"><th colspan="9" class="tfootwitebg text-right tot_net_type"> TCS Amount </th><th class="tfootbg text-right tcsindformat" id="tcsfield">0.00</th><th class="tfootwitebg"><input type="hidden" name="tcstdsAmount" id="tcsamt" value="0.00"/><a href="#" style="margin-right: 2px;" onclick="edittcstds(\''+tds+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a></th></tr><tr class="apply_tcsOtds"><th colspan="9" class="tfootwitebg text-right tot_net_type">Net Receivable(Total Invoice Value + TCS Amount)</th><th class="tfootbg tcsindformat" id="invvalwithtcs">0.00</th><th class="tfootwitebg"></th></tr>');
				//supportTCS(invtype,enableDiscount,enableExempted,enableSalesCess,enableLedger);
				tdstcschange();
				if($('#invoiceLevelCess').is(":checked")) {
					supportTCS(invtype,enableDiscount,enableExempted,true,enableLedger);
				}else{
					supportTCS(invtype,enableDiscount,enableExempted,false,enableLedger);
				}
			}else{
				var tds = "TCS";
				if($('#tdsval').is(":checked")){
					var row = '<tr class="apply_tcsOtds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="tdsAmt_text"> TCS Amount </th><th class="tfootwitebg text-right tcsindformat" id="tcsfield">0.00</th><th class="tfootwitebg no-i-foot"><input type="hidden" name="tcstdsAmount" id="tcsamt" value="0.00"/><a href="#" style="margin-right: 2px;" onclick="edittcstds(\''+tds+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a></th></tr>';
					//$('#allinvoicettfoot').append('<tr class="apply_tcsOtds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="tdsAmt_text"> TCS Amount </th><th class="tfootwitebg"><input type="text" name="tcstdsAmount" onkeyup="changeTcsAmount()" class="form-control tcsindformat text-right" id="tdsamount" value="0.00" style="font-size:14px;font-weight:600;"/></th><th class="tfootwitebg no-i-foot"></th></tr>');
					$("#allinvoicettfoot tr:first").after(row);
					$('#netTds_text').html('Net Payable(Total Invoice Value + TCS Amount - TDS Amount)');
				}else{
					if(invtype == 'ISD' || invtype == 'ITC Reversal'){
						$('#allinvoicettfoot1').append('<tr class="apply_tcsOtds"><th colspan="13" class="tfootwitebg text-right tot_net_type"> TCS Amount </th><th class="tfootbg indformat">0.00</th><th class="tfootwitebg no-i-foot"></th></tr><tr class="apply_tcsOtds"><th colspan="13" class="tfootwitebg text-right tot_net_type">Net Receivable(Total Invoice Value + TCS Amount)</th><th class="tfootbg indformat">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
					}else{
						$('#allinvoicettfoot').append('<tr class="apply_tcsOtds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="tdsAmt_text"> TCS Amount </th><th class="tfootbg text-right tcsindformat" id="tcsfield">0.00</th><th class="tfootwitebg no-i-foot"><input type="hidden" name="tcstdsAmount" id="tcsamt" value="0.00"/><a href="#" style="margin-right: 2px;" onclick="edittcstds(\''+tds+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a></th></tr><tr class="apply_tcsOtds" id="totaltcs"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="netTds_text">Net Payable(Total Invoice Value + TCS Amount)</th><th class="tfootbg tcsindformat" id="invvalwithtds">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
					}
				}
				tdstcschange();
				if($('#invoiceLevelCess').is(":checked")) {
					supportTDS(invtype,enablePurDiscount,true,enablePurLedger);
				}else{
					supportTDS(invtype,enablePurDiscount,false,enablePurLedger);
				}
				
			}
			//if(invtype == 'Advance Adjusted Detail'){$('.tot_net_type').attr('colspan','11');}
		}else{
			$('#tcsval').val('false');
			$("#tds_val").attr("disabled",true);
			$("#tcs_val").attr("disabled",true);
			$('#tdstcssection').val("");
			$('#tcs_percent').val("");
			$('.tcsorTdsType').addClass('disclass');
			$('#tdstcssection ,#tcs_percent').attr("readonly","true");$('#tdstcssection').attr("disabled",true);
			$('.apply_tcsOtds').remove();
			//$("select#tdstcssection option[value='']").remove();
			$("select#tdstcssection option[value='206C(1)']").remove();
			if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
				
			}else{
				tdstcschange();
				if(!$('#tcsval').is(":checked")){
					$('#netTds_text').html('Net Payable(Total Invoice Value - TDS Amount)');
					//$('#allinvoicettfoot').append('<tr class="apply_tcsOtds" id="totaltcs"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="netTds_text">Net Payable(Total Invoice Value + TCS Amount)</th><th class="tfootbg tcsindformat" id="invvalwithtds">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
				}
			}
			
		}
		//$('#sortable_table').tableDnDUpdate();
	} 
  function tdscheckval(){
		//$('#tdssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
	  var tds = "TDS";
		if($('#tdsval').is(":checked")){
			$('#tdsval').val('true');
			$('#tdssection ,#tds_percent').removeAttr("readonly");
			$('#tdssection').removeAttr("disabled");
			purchaseTDSOptions();
			var rettype = $('#retType').val();
			var invtype = $('#idInvType').val();
			if($('#tcsval').is(":checked")){
				$('#totaltcs').remove();
				$('#allinvoicettfoot').append('<tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id=""> TDS Amount </th><th class="tfootbg text-right tcsindformat" id="tdsfield">0.00</th><th class="tfootwitebg no-i-foot"><input type="hidden" name="tdsAmount" id="tdsamt" value="0.00"/><a href="#" style="margin-right: 2px;" onclick="edittcstds(\''+tds+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a></th></tr><tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="netTds_text">Net Payable(Total Invoice Value + TCS Amount - TDS Amount)</th><th class="tfootbg tcsindformat" id="invvalwith_tds">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
			}else{
				$('#allinvoicettfoot').append('<tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id=""> TDS Amount </th><th class="tfootbg text-right tcsindformat" id="tdsfield">0.00</th><th class="tfootwitebg no-i-foot"><input type="hidden" name="tdsAmount" id="tdsamt" value="0.00"/><a href="#" style="margin-right: 2px;" onclick="edittcstds(\''+tds+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a></th></tr><tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="netTds_text">Net Payable(Total Invoice Value - TDS Amount)</th><th class="tfootbg tcsindformat" id="invvalwith_tds">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
			}
			
			/*if(invtype == 'ISD' || invtype == 'ITC Reversal'){
				$('#allinvoicettfoot1').append('<tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type"> TDS Amount </th><th class="tfootbg indformat">1,00,000</th><th class="tfootwitebg no-i-foot"></th></tr><tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type">Net Receivable(Total Invoice Value - TDS Amount)</th><th class="tfootbg indformat">1,00,000</th><th class="tfootwitebg no-i-foot"></th></tr>');
			}else{
				$('#allinvoicettfoot').append('<tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id=""> TDS Amount </th><th class="tfootwitebg"><input type="text" name="tdsAmount" onkeyup="changeTdsAmount()" class="form-control tcsindformat text-right" id="tds_amount" value="0.00" style="font-size:14px;font-weight:600;"/></th><th class="tfootwitebg no-i-foot"></th></tr><tr class="apply_tds"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="netTds_text">Net Payable(Total Invoice Value - TDS Amount)</th><th class="tfootbg tcsindformat" id="invvalwith_tds">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
			}*/
			if($('#invoiceLevelCess').is(":checked")) {
				supportTDS(invtype,enablePurDiscount,true,enablePurLedger);
			}else{
				supportTDS(invtype,enablePurDiscount,false,enablePurLedger);
			}
			tdschange();
		}else{
			if($('#tcsval').is(":checked")){
				$('#allinvoicettfoot').append('<tr class="apply_tcsOtds" id="totaltcs"><th colspan="13" class="tfootwitebg text-right tot_net_type" id="netTds_text">Net Payable(Total Invoice Value + TCS Amount)</th><th class="tfootbg tcsindformat" id="invvalwithtds">0.00</th><th class="tfootwitebg no-i-foot"></th></tr>');
			}
			$('#tdsval').val('false');
			$('#tdssection').val("");
			$('#tds_percent').val("");
			$('#tdssection ,#tds_percent').attr("readonly","true");
			$('#tdssection').attr("disabled",true);
			$('.apply_tds').remove();
			tdschange();
			if($('#invoiceLevelCess').is(":checked")) {
				supportTDS(invtype,enablePurDiscount,true,enablePurLedger);
			}else{
				supportTDS(invtype,enablePurDiscount,false,enablePurLedger);
			}
		}
	}
	function supportTDS(invtype,enablePurDiscount,enablePurCess,enablePurLedger){
				if(invtype == 'B2B' || invtype == 'B2B Unregistered' || invtype == 'Credit/Debit Notes' || invtype == 'Import Goods' || invtype == 'Import Services'){
					$('.tot_net_type').attr('colspan','12');
				}else if(invtype == 'Nil Supplies'){
					$('.tot_net_type').attr('colspan','8');
				}else if(invtype == 'Advances'){
					$('.tot_net_type').attr('colspan','7');
				}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
					$('.tot_net_type').attr('colspan','12');
				}else if(invtype == 'ISD' || invtype == 'ITC Reversal'){
					$('.tot_net_type').attr('colspan','6');$('.no-i-foot').hide();
				}
				if((enablePurDiscount == true || enablePurDiscount == "true")){
	        		if(invtype != "Nil Supplies" && invtype != "Advance Payments" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"  && invtype != "ISD INVOICE" && invtype != "ITC REVERSAL INVOICE"){
	        			$('.tot_net_type').attr("colspan","13");
	        		}else{
	        			if(invtype == "Advance Payments" || invtype == "Advances"){
		        			$('.tot_net_type').attr("colspan","7");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","12");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","10");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","9");
						}else{
		        			$('.tot_net_type').attr("colspan","10");
		        		}
	        		}
	        	}
				if((enablePurCess == true || enablePurCess == "true")){
	        		if(invtype != "Nil Supplies" && invtype != "Advance Payments" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"  && invtype != "ISD INVOICE" && invtype != "ITC REVERSAL INVOICE"){
	        			$('.tot_net_type').attr("colspan","14");
	        		}else{
	        			if(invtype == "Advance Payments" || invtype == "Advances"){
		        			$('.tot_net_type').attr("colspan","9");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","14");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","12");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","9");
						}else{
		        			$('.tot_net_type').attr("colspan","10");
		        		}
	        		}
	        	}
	        	if((enablePurLedger == true || enablePurLedger == "true")){
	        		if(invtype != "Nil Supplies" && invtype != "Advance Payments" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"  && invtype != "ISD INVOICE" && invtype != "ITC REVERSAL INVOICE"){
	        			$('.tot_net_type').attr("colspan","13");
	        		}else{
	        			if(invtype == "Advance Payments" || invtype == 'Advances'){
		        			$('.tot_net_type').attr("colspan","8");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","13");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","11");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","10");
						}else{
		        			$('.tot_net_type').attr("colspan","10");
		        		}
	        		}
	        	}
	        	
	        	if(((enablePurDiscount == true || enablePurDiscount == "true") && (enablePurLedger == true || enablePurLedger == "true"))){
	        		if(invtype != "Nil Supplies" && invtype != "Advance Payments" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"  && invtype != "ISD INVOICE" && invtype != "ITC REVERSAL INVOICE"){
	        			$('.tot_net_type').attr("colspan","14");
	        		}else{
	        			if(invtype == "Advance Payments" || invtype == "Advances"){
		        			$('.tot_net_type').attr("colspan","8");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","13");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","11");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","10");
						}else{
		        			$('.tot_net_type').attr("colspan","10");
		        		}
	        		}
	        	}
				
				if(((enablePurCess == true || enablePurCess == "true") && (enablePurLedger == true || enablePurLedger == "true"))){
	        		if(invtype != "Nil Supplies" && invtype != "Advance Payments" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"  && invtype != "ISD INVOICE" && invtype != "ITC REVERSAL INVOICE"){
	        			$('.tot_net_type').attr("colspan","15");
	        		}else{
	        			if(invtype == "Advance Payments" || invtype == "Advances"){
		        			$('.tot_net_type').attr("colspan","10");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","15");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","13");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","10");
						}else{
		        			$('.tot_net_type').attr("colspan","11");
		        		}
	        		}
	        	}
				
	        	if(((enablePurDiscount == true || enablePurDiscount == "true") && (enablePurCess == true || enablePurCess == "true"))){
	        			if(invtype == "Advances"){
		        			$('.tot_net_type').attr("colspan","9");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","14");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","12");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","9");
						}else if(invtype == "B2B" || invtype == "Import Goods" || invtype == "Import Services"){
							$('.tot_net_type').attr("colspan","15");
						}else{
		        			$('.tot_net_type').attr("colspan","10");
		        		}
	        	}
	        	if(((enablePurDiscount == true || enablePurDiscount == "true") && (enablePurLedger == true || enablePurLedger == "true") && (enablePurCess == true || enablePurCess == "true"))){
	        		if(invtype != "Nil Supplies" && invtype != "Advance Payments" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"  && invtype != "ISD INVOICE" && invtype != "ITC REVERSAL INVOICE"){
	        			$('.tot_net_type').attr("colspan","16");
	        		}else{
	        			if(invtype == "Advance Payments" || invtype == "Advances"){
		        			$('.tot_net_type').attr("colspan","10");
		        		}else if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.tot_net_type').attr("colspan","15");
		        		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
							$('.tot_net_type').attr("colspan","13");
						}else if(invtype == "Nil Supplies"){
							$('.tot_net_type').attr("colspan","10");
						}else{
		        			$('.tot_net_type').attr("colspan","10");
		        		}
	        		}
	        	}else if(((enablePurDiscount == false || enablePurDiscount == "false") && (enablePurLedger == false || enablePurLedger == "false") && (enablePurCess == false || enablePurCess == "false"))){
					if(invtype == "Advance Adjusted Detail"){
						$('.tot_net_type').attr("colspan","10");
					}else if(invtype == "Nil Supplies"){
						$('.tot_net_type').attr("colspan","9");
					}
				}
			}
			
	function eBillinvoices(){
		var retType = $('#retType').val();
		if(retType == 'EWAYBILL'){
		var invoiceNumberoptions = {
				url: function(phrase) {
					var clientid = $('#clientid').val();var date = $('#dateofinvoice').val();var mnyr = date.split("/");var rettype = $('#retType').val();var invtype = $('#idInvType').val(); invtype = invtype.replace('/', '_');var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/invoiceNumbers/"+mn+"/"+yr+"/EWAYBILL/B2B/?query="+ phrase + "&clientid="+clientid+"&format=json";
				},
				getValue: function(element) {
					return element.invoiceno;
				},
				list: {
					onChooseEvent: function() {var rettype = $('#retType').val();
						var invoice = $("#serialnoofinvoice").getSelectedItemData();
						var invDate = new Date(invoice.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "/" + month + "/" + year;
						$('#billedtoname').val(invoice.billedtoname);$('#billedtostatecode').val(invoice.statename).trigger("change");$('#billedtogstin').val(invoice.b2b[0].ctin);
						$('#fromPincode').val(invoice.sellerDtls.pin);
						$('#toPincode').val(invoice.buyerDtls.pin);
						$('#dateofinvoice').val(invoiceDate);$('#serialnoofinvoice').val(invoice.invoiceno).trigger("change");
						eWayBill_row(invoice);
					},
					onLoadEvent: function() {
						if($("#eac-container-serialnoofinvoice ul").children().length == 0) {$("#serialnoofinvoiceempty").show();} else {$("#serialnoofinvoiceempty").hide();}
					},
					maxNumberOfElements: 43
				}
			};
			$(".serialnoofinvoice").easyAutocomplete(invoiceNumberoptions);
		}
	}
	function eWayBill_row(invoice){
		var clientid = $('#clientid').val();var rettype = $('#retType').val();
		if(rowCount < invoice.items.length) {
			for(var i=1;i<invoice.items.length;i++) {
				if(invoice.items[i]){add_row(clientid,rettype,usertype);}
			}
		}	
		
		$.each(invoice,function(key,value) {
			if(key == 'totalamount'){
				$("#idTotal").html(formatNumber(parseFloat(value).toFixed(2)));
				document.getElementById("totTotal").innerHTML=formatNumber(parseFloat(value).toFixed(2));
			}
			if(key == 'totaltaxableamount'){
				document.getElementById("totTaxable").innerHTML=formatNumber(parseFloat(value).toFixed(2));
			}
			if(key == 'totaltax'){
				document.getElementById("totIGST").innerHTML=formatNumber(parseFloat(value).toFixed(2));
			}
			if(key == 'totalCessAmount'){
				document.getElementById("totCESS").innerHTML = formatNumber(parseFloat(invoice.totalCessAmount).toFixed(2));
			}
		});
		
		for(var i=1;i<=invoice.items.length;i++) {
			$("#itemnotes_text"+i).val(invoice.items[i-1].itemNotescomments);
			document.getElementById("itemId_text" + i).value = invoice.items[i - 1].itemId? invoice.items[i - 1].itemId : '';
			document.getElementById("product_text" + i).value = invoice.items[i - 1].itemno ? invoice.items[i - 1].itemno : '';
			document.getElementById("hsn_text" + i).value = invoice.items[i - 1].hsn ? invoice.items[i - 1].hsn : '';
			document.getElementById("uqc_text" + i).value = invoice.items[i - 1].uqc ? invoice.items[i - 1].uqc : '';
			document.getElementById("qty_text" + i).value = invoice.items[i - 1].quantity ? invoice.items[i - 1].quantity : '0.00';
			document.getElementById("discount_text"+i).value=invoice.items[i-1].discount ? invoice.items[i - 1].discount : '0.00';
			document.getElementById("rate_text" + i).value = invoice.items[i - 1].rateperitem ? invoice.items[i - 1].rateperitem : '0.00';
			document.getElementById("igsttax_text" + i).value = invoice.items[i - 1].igstamount ? invoice.items[i - 1].igstamount.toFixed(2) : '0.00';
	        document.getElementById("cgsttax_text" + i).value = invoice.items[i - 1].cgstamount ? invoice.items[i - 1].cgstamount.toFixed(2) : '0.00';
	        document.getElementById("sgsttax_text" + i).value = invoice.items[i - 1].sgstamount ? invoice.items[i - 1].sgstamount.toFixed(2) : '0.00';
	        document.getElementById("cessamount_text" + i).value = invoice.items[i - 1].cessamount ? invoice.items[i - 1].cessamount.toFixed(2) : '0.00';
	        document.getElementById("taxrate_text" + i).value = invoice.items[i - 1].rate;
	        document.getElementById("cessrate_text" + i).value = invoice.items[i - 1].cessrate ? invoice.items[i - 1].cessrate : '0';
	        document.getElementById("taxableamount_text" + i).value = invoice.items[i - 1].taxablevalue ? invoice.items[i - 1].taxablevalue.toFixed(2) : '0.00';
	        document.getElementById("total_text" + i).value = invoice.items[i - 1].total ? invoice.items[i - 1].total.toFixed(2) : '0.00';
	        if(invoice.items[i - 1].igstamount && invoice.items[i - 1].igstamount > 0){
				document.getElementById("abb"+i).value = invoice.items[i - 1].igstamount.toFixed(2);
	        }else if(invoice.items[i - 1].sgstamount && invoice.items[i - 1].sgstamount > 0 && invoice.items[i - 1].cgstamount && invoice.items[i - 1].cgstamount > 0){
	           document.getElementById("abb"+i).value = (parseFloat(invoice.items[i - 1].cgstamount.toFixed(2)) + parseFloat(invoice.items[i - 1].sgstamount.toFixed(2))).toFixed(2);
	        }
		}
		
	}
	function showEwayBillCancelPopup(invId, retType, ewaybillNo) {
		var inyType = $('#idInvType').val();
		$('#invoiceModal').css("z-index","1040");
		$('#cancelEwayBillModal').modal('show');
		$('#btn_Cancel').attr('onclick', "cancelEwayBillInvoice('"+ewaybillNo+"','"+invId+"','"+retType+"')");
	}
	function cancelEwayBillInvoice(ebillno,invId, retType) {
		var clientid = $('#clientid').val();
		var cancelcode = $('#cancelcode').val();
		var cancelremark = $('#cancelremark').val();
		if(accessDwnldewabillinv == "false"){
			$('#errorNotificationModal').modal("show");
			$('#invoiceModal').css("z-index","1033");
			$('.modal-backdrop.show').css("display","block");
		}else{
    	$('#btn_Cancel').addClass("btn-loader");
		var cancelewaybilldata=new Object();
		cancelewaybilldata.ewbNo = ebillno;
		cancelewaybilldata.cancelRsnCode=cancelcode;
		cancelewaybilldata.cancelRmrk=cancelremark;
		if(cancelcode != '' && cancelremark != ''){
			$.ajax({
				url: contextPath+"/canceeBilllinv/"+urlSuffixs+"/"+invId+"/"+retType+"/"+Paymenturlprefix,
				type: "POST",
				data: JSON.stringify(cancelewaybilldata),
				contentType: 'application/json',
				success : function(response) {
					var s1 = response;
					var s2 = s1.substr(1);
				if(response.length != 0){
					$('#cancel_error').html(s2);
					$('#btn_Cancel').removeClass("btn-loader");
				}else{
					$('#cancel_error').html("");
					location.reload(true);
					$('#btn_Cancel').removeClass("btn-loader");
				}
				}
			});
		}else{$('#btn_Cancel').removeClass("btn-loader");}
	}
}

	function vehicleUpdate(returntype){
		if(accessDwnldewabillinv == "false"){
			errorNotification('Your Access to the Eway Bill Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		}else{
			$('#vehicleUpdateModal').modal("show");
		 $.ajax({
			  url: contextPath+"/populate_vehicleDetails/"+vehicleUpdateArray+"/"+returntype,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response){
					rowCount = 1;
					if(rowCount <= response.length) {
						for(var i=1;i<response.length;i++) {
							if(response[i]){
								addEbillrow('vehicleupdate_table');
							}
						}
						for(var i=1;i<=response.length;i++) {
							$('#vehicle_No'+i).val(response[i-1].vehicleNo);
							$('#from_Place'+i).val(response[i-1].fromPlace);
							$('#from_State'+i).val(response[i-1].fromState);
							$('#tripsht_No'+i).val(response[i-1].tripshtNo);
							$('#userGSTIN_Transin'+i).val(response[i-1].userGSTINTransin);
							$('#entered_Date'+i).val(response[i-1].enteredDate);
							$('#trans_Mode'+i).val(response[i-1].transMode);
							$('#trans_DocNo'+i).val(response[i-1].transDocNo);
							$('#trans_DocDate'+i).val(response[i-1].transDocDate);
							$('#group_No'+i).val(response[i-1].groupNo);
							$('#reason_Code'+i).val(response[i-1].reasonCode);
							$('#reason_Rem'+i).val(response[i-1].reasonRem);
							$('#vehicle_Type'+i).val(response[i-1].vehicleType);
						}
					}	
				
					
			},error:function(err){
			}
			
		}); 
	}	
}
function vehicleupdate(rettype){
		
		var vehicleno,reasonCode,reasonRem,transDocNo,transDocDate,transMode,vehicleType;
		var vuprowCount = $('#vehicleDetailsBody tr').length;var invId=$('#invoiceid').val();
		var clientid = $('#clientid').val();
		if(vehiclerowCount <= vuprowCount) {
			for(var i=1;i<=vuprowCount;i++) {
				vehicleno = $('#vehicleNo'+i).val();
				reasonCode = $('#reasonCode').val();
				reasonRem = $('#reasonRem'+i).val();
				transDocNo = $('#transDocNo'+i).val();
				transDocDate = $('#transDocDate'+i).val();
				transMode = $('#transMode'+i).val();
				vehicleType = $('#vehicleType'+i).val();
				if(reasonCode != "" && reasonRem != "" || reasonCode != undefined && reasonRem != undefined){
				var updatevehicledata=new Object();
				updatevehicledata.vehicleNo=vehicleno;
				updatevehicledata.reasonCode=reasonCode;
				updatevehicledata.reasonRem=reasonRem;
				updatevehicledata.transDocNo=transDocNo;
				updatevehicledata.transDocDate=transDocDate;
				updatevehicledata.transMode=transMode;
				updatevehicledata.vehicleType=vehicleType;
				vUpdateArray.push(updatevehicledata);
			}
			}
			
		}
		if(reasonCode != ""){
			$('#vehicleupDt').addClass("btn-loader");
			$('#vehicleupDt').html("Updating...");
		$.ajax({
			url : contextPath+'/vehicleupdt/'+invId+'/'+urlSuffixs+'/'+Paymenturlprefix,
			type: "POST",
			data: JSON.stringify(vUpdateArray),
			contentType: 'application/json',
			success : function(response) {
				var s1 = response;
				var s2 = s1.substr(1);
			if(response.length != 0){
				$('#ewayBillError').html(s2);
				$('#vehicleupDt').removeClass("btn-loader");
			}else{
				$('#ewayBillError').html("");
				location.reload(true);
				$('#vehicleupDt').removeClass("btn-loader");
			}
			
			}, 	error:function(data){
				$('#vehicleupDt').removeClass("btn-loader");
				 $('#vehicleupDt').html("Update");
	    	}	
		});
			
	}
		
	}
	function Update_Vehicle(){
		if(accessDwnldewabillinv == "false"){
			errorNotification('Your Access to the Eway Bill Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		}else{
			$('#btnVehicleUpdate').addClass("btn-loader");
			$('#btnVehicleUpdate').html("Updating...");
			var vehicleno,fromPlace,fromState,reasonCode,reasonRem,transDocNo,transDocDate,enteredDate,transMode,groupNo,tripshtNo,userGSTINTransin,vehicleType;
			var vuprowCount = $('#vehicleUpdateDetailsBody tr').length;
			var clientid = $('#clientid').val();
			if(rowCount <= vuprowCount) {
				for(var i=1;i<=vuprowCount;i++) {
					vehicleno = $('#vehicle_No'+i).val();
					fromPlace = $('#from_Place'+i).val();
					fromState = $('#from_State'+i).val();
					reasonCode = $('#reason_Code'+i).val();
					reasonRem = $('#reason_Rem'+i).val();
					transDocNo = $('#trans_DocNo'+i).val();
					transDocDate = $('#trans_DocDate'+i).val();
					transMode = $('#trans_Mode'+i).val();
					groupNo = $('#group_No'+i).val();
					enteredDate = $('#entered_Date'+i).val();
					tripshtNo =  $('#tripsht_No'+i).val();
					userGSTINTransin = $('#userGSTIN_Transin'+i).val();
					vehicleType = $('#vehicle_Type'+i).val();
					if(reasonCode != "" && reasonRem != "" || reasonCode != undefined && reasonRem != undefined){
					var updatevehicledata=new Object();
					updatevehicledata.vehicleNo=vehicleno;
					updatevehicledata.fromPlace=fromPlace;
					updatevehicledata.fromState=fromState;
					updatevehicledata.reasonCode=reasonCode;
					updatevehicledata.reasonRem=reasonRem;
					updatevehicledata.transDocNo=transDocNo;
					updatevehicledata.transDocDate=transDocDate;
					updatevehicledata.transMode=transMode;
					updatevehicledata.groupNo=groupNo;
					updatevehicledata.enteredDate=enteredDate;
					updatevehicledata.tripshtNo=tripshtNo;
					updatevehicledata.userGSTINTransin=userGSTINTransin;
					updatevehicledata.vehicleType=vehicleType;
					vUpdateArray.push(updatevehicledata);
				}
				}
				
			}
			
			$.ajax({
				url : contextPath+'/vehicleupdt/'+vehicleUpdateArray+'/'+urlSuffixs+'/'+Paymenturlprefix,
				type: "POST",
				data: JSON.stringify(vUpdateArray),
				contentType: 'application/json',
				success : function(response) {
				var s1 = response;
				var s2 = s1.substr(1);
			if(response.length != 0){
				$('#vehupdt_error').html(s2);
				$('#btnVehicleUpdate').removeClass("btn-loader");
			}else{
				$('#vehupdt_error').html("");
				location.reload(true);
				$('#btnVehicleUpdate').removeClass("btn-loader");
			}
				}, 	error:function(data){
					$('#btnVehicleUpdate').removeClass("btn-loader");
					 $('#btnVehicleUpdate').html("Update");
		    	}	
			});
	}
}
	function cancelCodeSelection(no){
		var cancelremark = $('#cancelremark').val();
		if(cancelremark == "Duplicate"){
			$('#cancelcode').val("1");
		}else if(cancelremark == "Order Cancelled"){
			$('#cancelcode').val("2");
		}else if(cancelremark == "Data Entry mistake"){
			$('#cancelcode').val("3");
		}else if(cancelremark == "Others"){
			$('#cancelcode').val("4");
		}
	}
	function reasonCodeSelect(no){
		var vupdaterem = $('#reason_Rem'+no).val();
		if(vupdaterem == "Due to Break Down"){
			$('#reason_Code'+no).val("1");
		}else if(vupdaterem == "Due to Transhipment"){
			$('#reason_Code'+no).val("2");
		}else if(vupdaterem == "Others (Pls. Specify)"){
			$('#reason_Code'+no).val("3");
		}else if(vupdaterem == "First Time"){
			$('#reason_Code'+no).val("4");
		}
	}
	function reasonCodeSelection(no){
		var vupdaterem = $('#reasonRem'+no).val();
		if(vupdaterem == "Due to Break Down"){
			$('#reasonCode').val("1");
		}else if(vupdaterem == "Due to Transhipment"){
			$('#reasonCode').val("2");
		}else if(vupdaterem == "Others (Pls. Specify)"){
			$('#reasonCode').val("3");
		}else if(vupdaterem == "First Time"){
			$('#reasonCode').val("4");
		}
	}
	function downloadEwayBIllInv(retType){
		var clientid = $('#clientid').val();
		if(accessDwnldewabillinv == "false"){
			errorNotification('Your Access to the Eway Bill Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		}else{
			var today = new Date();
			var firstDay = new Date(year, month, 1);
			var diff = Math.floor(today.getTime() - firstDay.getTime());
			var day = 1000 * 60 * 60 * 24;
		    var days = Math.floor(diff/day);
		    var months = Math.floor(days/31);
		    var years = Math.floor(months/12);
		    var monthNo = months + 1;
			if(monthNo < 6){
				$('.downloadbtn').addClass("btn-loader");
				$('.downloadbtn').html("Downloading...");
				$.ajax({
					url : contextPath+'/dwnldinvByEwaybillNo/'+commonSuffix+"/"+clientid+"/"+retType+"/"+month+"/"+year,
					type: "GET",
					contentType: 'application/json',
					success : function(response) {
						$('.downloadbtn').removeClass("btn-loader");
						 $('.downloadbtn').html("Downloaded");
						location.reload(true);
					}, 	error:function(data){
						$('.downloadbtn').removeClass("btn-loader");
						$('.downloadbtn').html("Download From EwayBill");
			    	}	
				});
			}else{
				errorNotification('GSTIN server give only Past 6 months generated waybills, So you can not download before 6 months data');
			}
		}
	}
	function saveEwayBIllInv(retType){
		$('#ewayBillSave_btn').addClass("btn-loader");
		var err = 0;
$('#einvoice_number').removeAttr("required");
	$('#salesinvoceform').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  err = 1;
		 		   $(this).parent().addClass('has-error has-danger');
		 	   }else{
		 		   $(this).parent().removeClass('has-error has-danger');
		 	   }
	    }
	});
	$('#salesinvoceform').find('select').each(function(){
	    if(!$(this).prop('required')){
	    }else{
				if (this.value == '-Select-' || this.value == '') {
					err = 1;
			       $(this).parent().addClass('has-error has-danger');
			    }else{
			    	$(this).parent().removeClass('has-error has-danger');
			    }
	    }
	});
	var ele=$('.form-group').is('.has-error');
	if(accessDwnldewabillinv == "false"){
		//errorNotification('Your Access to the Eway Bill Module is disabled, Please contact MasterGST support team at sales@mastergst.com or call us @+91-7901022478 | 040-48531992.');
		$('#errorNotificationModal').modal("show");
		$('#ewayBillSave_btn').removeClass("btn-loader");
		$('#invoiceModal').css("z-index","1033");
		$('.modal-backdrop.show').css("display","block");
	}else{
	if(!ele){
		$('#invitemdetails').html("");
		var clientid = $('#clientid').val();
		$.each($("#allinvoice tr td input.disval"),function() {
			if ($(this).val()) {
				var dStr = ($(this).val()).toString();
				if(dStr.indexOf("%") > -1){
					dStr = dStr.replace("%","");
				}
				$(this).val(dStr);
			}
		});
		$.each($("#allinvoice tr td input.cessval"),function() {
			if ($(this).val()) {
				var dStr = ($(this).val()).toString();
				if(dStr.indexOf("%") > -1){
					dStr = dStr.replace("%","");
				}
				$(this).val(dStr);
			}
		});
		if($('#ewybuyerDtls_pin').val() == ""){
			$('#ewybuyerDtls_pin').val(parseInt("0"));
		}
			$.ajax({
				url : contextPath+'/saveEwayBillinvoice/'+retType+"/"+varUserType+"/"+month+"/"+year,
				type: "POST",
				data: $("#salesinvoceform").serialize(),
				success : function(response) {
					var s1 = response;
					var s2 = s1.substr(1);
				if(response.length != 0){
					$('#ewayBillError').html(s2);
				}else{
					$('#ewayBillError').html("");
					window.location.href = contextPath+'/ewaybill'+commonSuffix+'/EWAYBILL/'+clientid+'/'+month+'/'+year;
				}
				$('#ewayBillSave_btn').removeClass("btn-loader");
				}, 	error:function(data){
					$('#ewayBillSave_btn').removeClass("btn-loader");	
		    	}
				
			});
	}else{
		$('#invitemdetails').html("Please fill All Details/You are missing mandatory fields, please enter the red-colour high lighted fields");
	}
}
	if (err != 0) {
	  $('#ewayBillSave_btn').removeClass("btn-loader");	
	  return false;
  }
}
	function saveEwayBIllInvDraft(retType){
		$('#draft_btn').addClass("btn-loader-blue");
		var err = 0;
		$('#einvoice_number').removeAttr("required");
		$('#salesinvoceform').find('input').each(function(){
		    if(!$(this).prop('required')){
		    }else{
		    	var bca = $(this).val();
			 	   if( bca == ''){
			 		  err = 1;
			 		   $(this).parent().addClass('has-error has-danger');
			 	   }else{
			 		   $(this).parent().removeClass('has-error has-danger');
			 	   }
		    }
		});
		$('#salesinvoceform').find('select').each(function(){
		    if(!$(this).prop('required')){
		    }else{
					if (this.value == '-Select-' || this.value == '') {
						err = 1;
				       $(this).parent().addClass('has-error has-danger');
				    }else{
				    	$(this).parent().removeClass('has-error has-danger');
				    }
		    }
		});
		$('#einvoice_number').removeAttr("required").removeClass('has-error has-danger');
		var ele=$('.form-group').is('.has-error');
		if(!ele){
			$('#invitemdetails').html("");
			var clientid = $('#clientid').val();
			if($('#ewybuyerDtls_pin').val() == ""){
				$('#ewybuyerDtls_pin').val(parseInt("0"));
			}
		$.each($("#allinvoice tr td input.disval"),function() {
			if ($(this).val()) {
				var dStr = ($(this).val()).toString();
				if(dStr.indexOf("%") > -1){
					dStr = dStr.replace("%","");
				}
				$(this).val(dStr);
			}
		});
		$.each($("#allinvoice tr td input.cessval"),function() {
			if ($(this).val()) {
				var dStr = ($(this).val()).toString();
				if(dStr.indexOf("%") > -1){
					dStr = dStr.replace("%","");
				}
				$(this).val(dStr);
			}
		});
		$.ajax({
			url : contextPath+'/saveewaybillinvoiceasdraft/'+retType+"/"+varUserType+"/"+month+"/"+year,
			type: "POST",
			data: $("#salesinvoceform").serialize(),
			success : function(response) {
				window.location.href = contextPath+'/ewaybill'+commonSuffix+'/EWAYBILL/'+clientid+'/'+month+'/'+year;
				$('#draft_btn').removeClass("btn-loader-blue");
			}, 	error:function(data){
				$('#draft_btn').removeClass("btn-loader-blue");
	    	}
			
		});
	}else{
		$('#invitemdetails').html("Please fill All Details/You are missing mandatory fields, please enter the red-colour high lighted fields");
	}
	
	if (err != 0) {
	  $('#draft_btn').removeClass("btn-loader-blue");	
	  return false;
  }
}
	function addtoGSTR1(btn,addflag){
		 if(ewaybillArray.length > 0) {
					for(var i=0;i<ewaybillArray.length;i++) {
						addtogstr1(ewaybillArray[i]);
					}
		} else {
					addtogstr1(new Array());
				} 
		}
		 function addtogstr1(ewaybillArray){
			$.ajax({
				type: "GET",
				url: contextPath+"/addtogstr1fromebill/"+ewaybillArray+"/"+clientId+"/"+Paymenturlprefix,
				success : function(response) {
					if(response == true || response == "true"){
						$('#addtogstr1Modal').modal('show');
						$('#maininv_head').html("Invoice is already in SaleRegister, you want to Replace it?");
						$('#view_GSTR1').html("Replace");
						$('#view_GSTR1').attr("onclick","viewGSTR1Inv('true')");
					}else{
						$('#addtogstr1Modal').modal('show');
						$('#view_GSTR1').attr("onclick","viewGSTR1Inv('false')");
					}
				},
				error : function(e, status, error) {
					if(e.responseText) {errorNotification(e.responseText);}
				}
			});
		} 
	function viewGSTR1Inv(res){
		if(res == true || res =="true"){
			$.ajax({
				type: "GET",
				url: contextPath+'/addfromebilltoGSTR1/'+ewaybillArray+"/"+clientId+"/"+month+"/"+year+'?type=res',
				success : function(response) {
					window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/SalesRegister/'+month+'/'+year+'?type=inv';
				},
				error : function(e, status, error) {
					if(e.responseText) {errorNotification(e.responseText);}
				}
			});
		}else{
			window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/SalesRegister/'+month+'/'+year+'?type=inv';
		}
	}
	function configauthentication(){
		$('#authStatus').html('<span style="color:red;">Expired</span><a href="#" style="color:green;"> <span id="inactivebtn">Authenticating...</span></a>');
		$.ajax({
			type: "GET",
			url: contextPath+'/authconfigdetails/'+commonturnOverSuffix+"/"+varRetType+"/"+Paymenturlprefix,
			async: true,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				if(response.connStaus == "Active"){
					$('#authStatus').html('Active').css("color","green");
				}else{
					$('#ebill_msg').html("<span style='color:red;'>Error</span> : Please Check Your <a href='"+contextPath+"/cp_upload"+commonturnOverSuffix+"/"+Paymenturlprefix+"?type=ewaybillconfig'>Configuration</a>");
					$('#authStatus').html('');
				}	
			},
			error : function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function intialIntialization(){
		var eccomgstins = {
				url: function(phrase) {
					var clientid = $('#clientid').val();
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/eccommercegstins?query="+ phrase + "&clientid="+clientid+"&format=json";
				},
				getValue: "gstnnumber",
				list: {
					onChooseEvent: function() {
						var ecommercegstin = $("#ecommercegstin").getSelectedItemData();
						$('#ecomoperatorname').text(ecommercegstin.name);
						$('#invoiceEcomOperator').val(ecommercegstin.name);
					},
					onLoadEvent: function() {
						if($("#eac-container-ecommercegstin ul").children().length == 0) {$("#addeccommercegstin").show();} else {$("#addeccommercegstin").hide();}
					},
					maxNumberOfElements: 30
				}
			};
			$("#ecommercegstin").easyAutocomplete(eccomgstins);	
		var uqcoptions1 = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/uqcconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onLoadEvent: function() {
						if($("#eac-container-uqc_text1 ul").children().length == 0) {
							$(".remainGSTR1ddbox41").show();
							$(".remainGSTR1ddbox41").css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'})
							$(".remainGSTR1ddbox41 p").css({'color':'#CC0000','margin':'0'});
							$('.uqc_row1').addClass('has-error has-danger');
						} else {
							$(".remainGSTR1ddbox41").hide();
							$('.uqc_row1').removeClass('has-error has-danger');
						}
					},
					maxNumberOfElements: 43
				}
			};
			$("#uqc_text1").easyAutocomplete(uqcoptions1);
			$("#uqc_text1").parent().parent().mouseleave(function() {
				$(".remainGSTR1ddbox41").hide();
				
			});
			var codeoptions1 = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/hsnsacconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					categories: [{
						listLocation: "hsnConfig",
						header: "--- HSN ---"
					}, {
						listLocation: "sacConfig",
						header: "--- SAC ---"
					}],
					list: {
						match: {
							enabled: false
						},
						onChooseEvent: function() {
							var hsnsacdata = $("#hsn_text1").getSelectedItemData();
							if(itcStateFlag){
								$.ajax({
									url: contextPath+"/hsnOrSacData?query="+ hsnsacdata.name + "&format=json",
									async: false,
									contentType: 'application/json',
									success : function(response) {
										if(response){
											$('#itctype_text1').val('ip');
											if(otherconfigdetails != ''){
												$('#itcpercent_text1').val(otherconfigdetails.itcinput);
											}else{
												document.getElementById('itcpercent_text1').value=100;
											}
										}else{
											$('#itctype_text1').val('is');
											if(otherconfigdetails != ''){
													$('#itcpercent_text1').val(otherconfigdetails.itcinputService);
											}else{
												document.getElementById('itcpercent_text1').value=100;
											}
										}
										$('#itcpercent_text1').attr("readonly",false);
										findITCValue(1);
									}
								});
							}
						},
						onLoadEvent: function() {
							if($("#eac-container-hsn_text1 ul").css('width','300px').children().length == 0) {
								$("#itemcodeempty").show();
								$(".remainGSTR1ddbox31").css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'})
								$(".remainGSTR1ddbox31 .remainhsnddbox1 p").css({'font-size':'12px','color':'#cc0000','margin':'0'});
							} else {
								$("#itemcodeempty").hide();
							}
						}
					}
				};
			$("#hsn_text1").easyAutocomplete(codeoptions1);
			$("#hsn_text1").parent().parent().mouseleave(function() {
				$("#itemcodeempty").hide();
			});
			
			var atinvoiceNumberoptions1 = {
					url: function(phrase) {
						var clientid = $('#clientid').val();
						var date = $('#dateofinvoice').val();var mnyr = date.split("/");var rettype = $('#retType').val();var invtype = $('#idInvType').val(); invtype = invtype.replace('/', '_');var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/advReceiptInvoiceNumbers/"+mn+"/"+yr+"/"+rettype+"/"+invtype+"/?query="+ phrase + "&clientid="+clientid+"&format=json";
					},
					getValue: function(element) {
						var invnoAndDate = element.invoiceno;
						return invnoAndDate;
					},
					list: {
						onChooseEvent: function() {
							$("#advrcdt_text1").val('');
							$("#advrcposs_text1").val('');
							$("#advrcamt_text1").val(0.00);
							$("#advrcavail_text1").val(0.00);
							var rettype = $('#retType').val();
							var invoice = $("#advrcno_text1").getSelectedItemData();
							var invDate = new Date(invoice.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "-" + month + "-" + year;
							$("#advrcdt_text1").val(invoiceDate);
							$("#advrcposs_text1").val(invoice.statename);
							$("#advrcamt_text1").val(invoice.totalamount);
							var remainingAmount= 0.00;
							if(invoice.advRemainingAmount){
								remainingAmount = invoice.advRemainingAmount
							}else{
								remainingAmount = invoice.totalamount;
							}
							$("#advrcavail_text1").val(remainingAmount);
							$("#advrcavailadj_text1").attr('max',remainingAmount);
							updateAdvRateType(clntStatename,invoice.statename, rettype);
						},
						onLoadEvent: function() {
							if($("#eac-container-advrcno_text1"+" ul").css({'width':'300px','padding-left':'5px'}).children().length == 0) {
								$("#advrcno_textempty").show();
								$(".advpaymentddbox1").css({'left':'50px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','z-index':'1'});
								$(".advpaymentddbox1 p").css({'font-size':'12px','color':'#cc0000','margin':'0'});
								$('#advrcno_text1').addClass('has-error has-danger');
								$("#advrcdt_text1").val('');
								$("#advrcposs_text1").val('');
								$("#advrcamt_text1").val(0.00);
								$("#advrcavail_text1").val(0.00);
								$('#advrcdt_text1').removeAttr('readonly');$("#advrcposs_text1").removeAttr('readonly');
								$("#advrcamt_text1").removeAttr('readonly');$("#advrcavail_text1").removeAttr('readonly');
							} else {
								$("#advrcno_textempty").hide();
								$('#advrcno_textempty1').removeClass('has-error has-danger');
								$('#advrcdt_text1').attr('readonly','readonly');$("#advrcposs_text1").attr('readonly','readonly');
								$("#advrcamt_text1").attr('readonly','readonly');$("#advrcavail_text1").attr('readonly','readonly');
							}
						},
						maxNumberOfElements: 43
					}
				};
			$("#advrcno_text1").easyAutocomplete(atinvoiceNumberoptions1);
			$("#advrcno_text1").parent().parent().mouseleave(function() {
				$("#advrcno_textempty").hide();
				
			});
			var atbstateoptions1 = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/stateconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onClickEvent: function() {
							
							var name = $("#advrcposs_text1").getSelectedItemData().name;
							var rettype = $('#retType').val();updateAdvRateType(clntStatename,name, rettype);
						},
						maxNumberOfElements: 37
					}
				};
			$('#advrcposs_text1').easyAutocomplete(atbstateoptions1);
			var itemoptions1 = {
					url: function(phrase) {
						var clientid = $('#clientid').val();
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/srchcommon?query="+ phrase + "&clientid="+clientid+"&format=json";
					},
					categories: [{
						listLocation: "items"
					}],
					getValue: "itemnoAndDescription",
					list: {
						onChooseEvent: function() {
							var itemData = $("#product_text1").getSelectedItemData();
							var invtype = $('#idInvType').val();
							var rettype = $('#retType').val();
							$('#opening_stock1').val(itemData.currentStock);
							$('#saftey_stock1').val(itemData.stocklevel);
							$("#product_text1").val('');
							$("#product_text1").val(itemData.description);
							if(itemData.code) {
								$("#hsn_text1").val(itemData.code);
							} else if(itemData.hsn) {
								$("#hsn_text1").val(itemData.hsn);
							} else if(itemData.sac) {
								$("#hsn_text1").val(itemData.sac);
							}
							$("#uqc_text1").val(itemData.unit);
							if(itemData.taxrate){
								$("#taxrate_text1").val(itemData.taxrate);
							}
							if(itemData.discount){
								if(invtype != 'Credit/Debit Notes'){
								$("#discount_text1").val(itemData.discount);
								}else{
								$("#discount_text1").val('');
								}
							}
							if(itemData.exmepted){
								if(rettype == 'GSTR2' || rettype == 'Purchase Register' || rettype == 'PurchaseRegister'){
								$("#exempted_text1").val('');
								}else{
								$("#exempted_text1").val(itemData.exmepted);
							}
							}
							if(itemData.salePrice){
								$("#rate_text1").val(itemData.salePrice);
							}else if(itemData.cost) {
								$("#rate_text1").val(itemData.cost);
							} else{
								$("#rate_text1").val(itemData.sellingpriceb2b);
							}
							$("#qty_text1").val(1);
							$("#qty_text1").focus();
							$('#itemCustomField_text11').val(itemData.itemCustomField1);
							$('#itemCustomField_text21').val(itemData.itemCustomField2);
							$('#itemCustomField_text31').val(itemData.itemCustomField3);
							$('#itemCustomField_text41').val(itemData.itemCustomField4);
							$('#itemId_text1').val(itemData.userid);
							//findTaxableValue();
							var invtype = $('#idInvType').val();
							
							var hsnsacdata = $("#hsn_text1").val();
							if(itcStateFlag){
								$.ajax({
									url: contextPath+"/hsnOrSacData?query="+ hsnsacdata + "&format=json",
									async: false,
									contentType: 'application/json',
									success : function(response) {
										if(response){
											$('#itctype_text1').val('ip');
											if(otherconfigdetails != ''){
												$('#itcpercent_text1').val(otherconfigdetails.itcinput);
											}else{
												document.getElementById('itcpercent_text1').value=100;
											}
										}else{
											$('#itctype_text1').val('is');
											if(otherconfigdetails != ''){
												$('#itcpercent_text1').val(otherconfigdetails.itcinputService);
											}else{
												document.getElementById('itcpercent_text1').value=100;
											}
										}
										$('#itcpercent_text1').attr("readonly",false);
										findITCValue(1);
									}
								});
							}
							if(invtype == "Nil Supplies"){
								findNillTaxableValue(1);
							}else{
								findTaxableValue(1);
							}
							if(invtype == "Advances"){
								findHsnOrSac(1);
							}
						},
						onLoadEvent: function() {
							if($("#eac-container-product_text1 ul").children().length == 0) {
								$("#remainproduct_textempty1").show();
								$("#remainproduct_textempty1 p").css({'color':'#CC0000','margin':'0'});
							} else {
								$("#remainproduct_textempty1").hide();
							}
						}
					}
				};
				$("#product_text1").easyAutocomplete(itemoptions1);
				$("#product_text1").parent().parent().mouseleave(function() {
					 setTimeout(function () {$("#remainproduct_textempty1").hide();}, 1000);
				});
				var ledgeroptions1 = {
						url: function(phrase) {
							var clientid = $('#clientid').val();
							phrase = phrase.replace('(',"\\(");
							phrase = phrase.replace(')',"\\)");
							return contextPath+"/ledgerlist/"+clientid+"?query="+ phrase + "&format=json";
						},
						getValue: "ledgerName",
						list: {
							match: {enabled: true},
						onChooseEvent: function() {
							var groupdetails = $("#ledger1").getSelectedItemData();
						}, 
							onLoadEvent: function() {
								if($("#eac-container-ledger1 ul").children().length == 0) {
									$("#addledgername1").show();
									$(".ledgerddbox1").css({'left':'347px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','width':'10.4%','z-index':'2'});
									$("#addledgername1 p").css({'color':'#CC0000','margin':'0'});
								} else {$("#addledgername1").hide();}
							},
							maxNumberOfElements: 10
						},
					};
				$('#ledger1').easyAutocomplete(ledgeroptions1);
				$("#ledger1").parent().parent().mouseleave(function() {$("#addledgername1").hide();});
		}
	
	function itmoptions(rowCoun,type,retType){
		var rc = rowCoun;
		var codeoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/hsnsacconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				categories: [{
					listLocation: "hsnConfig",
					header: "--- HSN ---"
				}, {
					listLocation: "sacConfig",
					header: "--- SAC ---"
				}],
				list: {
					match: {
						enabled: false
					},
					onChooseEvent: function() {
						var hsnsacdata = $("#hsn_text"+rc).getSelectedItemData();
						if(itcStateFlag){
							$.ajax({
								url: contextPath+"/hsnOrSacData?query="+ hsnsacdata.name + "&format=json",
								async: false,
								contentType: 'application/json',
								success : function(response) {
									if(response){
										$('#itctype_text'+rc).val('ip');
										if(otherconfigdetails != ''){
											$('#itcpercent_text'+rc).val(otherconfigdetails.itcinput);
										}else{
											document.getElementById('itcpercent_text'+rc).value=100;
										}
									}else{
										$('#itctype_text'+rc).val('is');
										if(otherconfigdetails != ''){
											$('#itcpercent_text'+rc).val(otherconfigdetails.itcinputService);
										}else{
											document.getElementById('itcpercent_text'+rc).value=100;
										}
									}
									$('#itcpercent_text'+rc).attr("readonly",false);
									findITCValue(rc);
								}
							});
						}
					},
					onLoadEvent: function() {
						if($("#eac-container-hsn_text"+rc+" ul").css('width','300px').children().length == 0) {
							$('.'+type+''+retType+'ddbox3'+rc).show();
							$('.'+type+''+retType+'ddbox3'+rc).css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'})
							$('#hsn_row'+rc).addClass('has-error has-danger');
						} else {
							$('.'+type+''+retType+'ddbox3'+rc).hide();
							$('#hsn_row'+rc).removeClass('has-error has-danger');
							
						}
					}
				}
			};
		$("#hsn_text"+rc).easyAutocomplete(codeoptions);
		$("#hsn_text"+rc).parent().parent().mouseleave(function() {
			$('.'+type+''+retType+'ddbox3'+rc).hide();
		});
		var uqcoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/uqcconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onLoadEvent: function() {
						if($("#eac-container-uqc_text"+rc+" ul").children().length == 0) {
							$('.'+type+''+retType+'ddbox4'+rc).show();
							$('.'+type+''+retType+'ddbox4'+rc).css({'background-color':'#fff','border':'1px solid #f5f5f5','padding':'3px','position':'absolute','width':'8%','z-index':'1','box-shadow':'0px 0px 5px 0px #e5e5e5'});
							$('#uqc_row'+rc).addClass('has-error has-danger');
						} else {
							$('.'+type+''+retType+'ddbox4'+rc).hide();
							$('#uqc_row'+rc).removeClass('has-error has-danger');
						}
					},
					maxNumberOfElements: 43
				}
			};
			$("#uqc_text"+rc).easyAutocomplete(uqcoptions);
			$("#uqc_text"+rc).parent().parent().mouseleave(function() {
				$('.'+type+''+retType+'ddbox4'+rc).hide();
				
			});
				var atbstateoptions = {
						url: function(phrase) {
							phrase = phrase.replace('(',"\\(");
							phrase = phrase.replace(')',"\\)");
							return contextPath+"/stateconfig?query="+ phrase + "&format=json";
						},
						getValue: "name",
						list: {
							onClickEvent: function() {
								var names = $("#advrcposs_text"+rc).getSelectedItemData();
								var nms = names.name;
								var rettype = $('#retType').val();updateAdvRateType(clntStatename,nms, rettype);
							},
							maxNumberOfElements: 37
						}
					};
				$('#advrcposs_text'+rc).easyAutocomplete(atbstateoptions);
				$('#advrcdt_text'+rc).datetimepicker({
					timepicker: false,
					format: 'd-m-Y',
					maxDate: 0,
					scrollMonth: true
				});
				var atinvoiceNumberoptions = {
						url: function(phrase) {
							var date = $('#dateofinvoice').val();var mnyr = date.split("/");var rettype = $('#retType').val();var invtype = $('#idInvType').val(); invtype = invtype.replace('/', '_');var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
							var clientid = $('#clientid').val();
							phrase = phrase.replace('(',"\\(");
							phrase = phrase.replace(')',"\\)");
							return contextPath+"/advReceiptInvoiceNumbers/"+mn+"/"+yr+"/"+rettype+"/"+invtype+"/?query="+ phrase + "&clientid="+clientid+"&format=json";
						},
						getValue: function(element) {
							var invnoAndDate = element.invoiceno;
							return invnoAndDate;
						},
						list: {
							onChooseEvent: function() {
								$("#advrcdt_text"+rc).val('');
								$("#advrcposs_text"+rc).val('');
								$("#advrcamt_text"+rc).val(0.00);
								$("#advrcavail_text"+rc).val(0.00);
								var rettype = $('#retType').val();
								var invoice = $("#advrcno_text"+rc).getSelectedItemData();
								var invDate = new Date(invoice.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "-" + month + "-" + year;
								$("#advrcdt_text"+rc).val(invoiceDate);
								$("#advrcposs_text"+rc).val(invoice.statename);
								$("#advrcamt_text"+rc).val(invoice.totalamount);
								var remainingAmount= 0.00;
								if(invoice.advRemainingAmount){
									remainingAmount = invoice.advRemainingAmount
								}else{
									remainingAmount = invoice.totalamount;
								}
								$("#advrcavail_text"+rc).val(remainingAmount);
								$("#advrcavailadj_text"+rc).attr('max',remainingAmount);
								updateAdvRateType(clntStatename,invoice.statename, rettype);
							},
							onLoadEvent: function() {
							
								if($("#eac-container-advrcno_text"+rc+" ul").css({'width':'300px','padding-left':'5px'}).children().length == 0) {
									$("."+type+"paymentddbox3"+rc).show();
									$("."+type+"paymentddbox3"+rc).css({'left':'50px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','z-index':'1'});
									$("."+type+"paymentddbox3"+rc+" p").css({'font-size':'12px','color':'#cc0000','margin':'0'});
									$('#advrcno_text'+rc).addClass('has-error has-danger');
									$("#advrcdt_text"+rc).val('');
									$("#advrcposs_text"+rc).val('');
									$("#advrcamt_text"+rc).val(0.00);
									$("#advrcavail_text"+rc).val(0.00);
									$('#advrcdt_text'+rc).removeAttr('readonly');$("#advrcposs_text"+rc).removeAttr('readonly');
									$("#advrcamt_text"+rc).removeAttr('readonly');$("#advrcavail_text"+rc).removeAttr('readonly');
								} else {
									$("."+type+"paymentddbox3"+rc).hide();
									$('#advrcno_textempty'+rc).removeClass('has-error has-danger');
									$('#advrcdt_text'+rc).attr('readonly','readonly');$("#advrcposs_text"+rc).attr('readonly','readonly');
									$("#advrcamt_text"+rc).attr('readonly','readonly');$("#advrcavail_text"+rc).attr('readonly','readonly');
								}
							},
							maxNumberOfElements: 43
						}
					};
				$("#advrcno_text"+rc).easyAutocomplete(atinvoiceNumberoptions);
				$("#advrcno_text"+rc).parent().parent().mouseleave(function() {
					$("."+type+"paymentddbox3"+rc).hide();
					
				});
		
		var itemoptions = {
				url: function(phrase) {
					var clientid = $('#clientid').val();
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/srchcommon?query="+ phrase + "&clientid="+clientid+"&format=json";
				},
				categories: [{
					listLocation: "items"
				}],
				getValue: "itemnoAndDescription",
				list: {
					onChooseEvent: function() {
						var itemData = $("#product_text"+rc).getSelectedItemData();
						var rettype = $('#retType').val();
						$('#opening_stock'+rc).val(itemData.currentStock);
						$('#saftey_stock'+rc).val(itemData.stocklevel);
						$("#product_text"+rc).val('');
						$("#product_text"+rc).val(itemData.description);
						if(itemData.code) {
							$("#hsn_text"+rc).val(itemData.code);
						} else if(itemData.hsn) {
							$("#hsn_text"+rc).val(itemData.hsn);
						} else if(itemData.sac) {
							$("#hsn_text"+rc).val(itemData.sac);
						}
						$("#uqc_text"+rc).val(itemData.unit);
						if(itemData.taxrate){
							$("#taxrate_text"+rc).val(itemData.taxrate);
						}
						if(itemData.discount){
							$("#discount_text"+rc).val(itemData.discount);
						}
						if(itemData.exmepted){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register' || rettype == 'PurchaseRegister'){
							$("#exempted_text"+rc).val('');
							}else{
							$("#exempted_text"+rc).val(itemData.exmepted);
						}
						}
						if(itemData.salePrice){
							$("#rate_text"+rc).val(itemData.salePrice);
						}else if(itemData.cost) {
							$("#rate_text"+rc).val(itemData.cost);
						} else{
							$("#rate_text"+rc).val(itemData.sellingpriceb2b);
						}
						$("#qty_text"+rc).val(1);
						$("#qty_text"+rc).focus();
						$('#itemCustomField_text1'+rc).val(itemData.itemCustomField1);
						$('#itemCustomField_text2'+rc).val(itemData.itemCustomField2);
						$('#itemCustomField_text3'+rc).val(itemData.itemCustomField3);
						$('#itemCustomField_text4'+rc).val(itemData.itemCustomField4);
						$('#itemId_text'+rc).val(itemData.userid);
						//findTaxableValue();
						var hsnsacdata = $("#hsn_text"+rc).val();
						if(itcStateFlag){
							$.ajax({
								url: contextPath+"/hsnOrSacData?query="+ hsnsacdata + "&format=json",
								async: false,
								contentType: 'application/json',
								success : function(response) {
									if(response){
										$('#itctype_text'+rc).val('ip');
										if(otherconfigdetails != ''){
												$('#itcpercent_text'+rc).val(otherconfigdetails.itcinput);
										}else{
											document.getElementById('itcpercent_text'+rc).value=100;
										}
									}else{
										$('#itctype_text'+rc).val('is');
										if(otherconfigdetails != ''){
												$('#itcpercent_text'+rc).val(otherconfigdetails.itcinputService);
										}else{
											document.getElementById('itcpercent_text'+rc).value=100;
										}
									}
									findITCValue(rc);
								}
							});
						}
						var invtype = $('#idInvType').val();
						if(invtype == "Nil Supplies"){
							findNillTaxableValue(rc);
						}else{
							findTaxableValue(rc);
						}
						if(invtype == 'Advances'){
							findHsnOrSac(rc);
						}
					},
					onLoadEvent: function() {
						if($("#eac-container-product_text"+rc+" ul").children().length == 0) {
							$("#"+type+"product_textempty"+rc).show();
							$("."+type+"ddbox"+rc).css({'left':'53px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','width':'25.4%','z-index':'2'});
							$("#"+type+"product_textempty"+rc+" p").css({'color':'#CC0000','margin':'0'});
						} else {
							$("#"+type+"product_textempty"+rc).hide();
						}
					}
				}
			};
			$("#product_text"+rc).easyAutocomplete(itemoptions);
			$("#product_text"+rc).parent().parent().mouseleave(function() {
				setTimeout(function () {$("#"+type+"product_textempty"+rc).hide();}, 1000);
			});
			
			var ledgeroptions = {
					url: function(phrase) {
						var clientid = $('#clientid').val();
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/ledgerlist/"+clientid+"?query="+ phrase + "&format=json";
					},
					getValue: "ledgerName",
					list: {
						match: {enabled: true},
					onChooseEvent: function() {
						var groupdetails = $("#ledger"+rc).getSelectedItemData();
					}, 
						onLoadEvent: function() {
							if($("#eac-container-ledger"+rc+" ul").children().length == 0) {
								$("#addledgername"+rc).show();
								$(".ledgerddbox"+rc).css({'left':'347px','position':'absolute','background-color': '#fff','border': '1px solid #f5f5f5','padding': '10px',' z-index': '99','box-shadow': '0px 0px 5px 0px #e5e5e5','width':'10.4%','z-index':'2'});
								$("#addledgername"+rc+" p").css({'color':'#CC0000','margin':'0'});
							
							} else {$("#addledgername"+rc).hide();}
						},
						maxNumberOfElements: 10
					},
				};
			$('#ledger'+rc).easyAutocomplete(ledgeroptions);
			$("#ledger"+rc).parent().parent().mouseleave(function() {$("#addledgername"+rc).hide();});
			
			
	}
	
	
	$('#roundOffTotal').click(function(){
		$('input[name="roundOffTotal"]').click(function(){
            if($(this).prop("checked") == true){
            	var notroundOffTotalValue=$('#hiddenroundOffTotalValue').val();
				if(notroundOffTotalValue != ''){
				$('#roundOffTotalValue').val((Math.round(parseFloat(notroundOffTotalValue))-parseFloat(notroundOffTotalValue)).toFixed(2));$('#idTotal').html(Math.round(notroundOffTotalValue).toFixed(2));
				}
            	OSREC.CurrencyFormatter.formatAll({selector: '.indformat_roundoff'});
            }else if($(this).prop("checked") == false){
            	$('#roundOffTotalValue').val('');var notroundOffTotalValue=$('#hiddenroundOffTotalValue').val();$('#idTotal').html(notroundOffTotalValue);
            	OSREC.CurrencyFormatter.formatAll({selector: '.indformat_roundoff'});	
            }
        });
    });
	
	function checkInvoiceNumber(edit){
		var invNo;var editinv = "";
		var gstnumber = $('#billedtogstin').val();
		if(edit == 'edit'){editinv = "edit";
		}else{editinv = "";}
		var invno = $('#serialnoofinvoice').val();invNo = invno;invno = invno.replace(/\//g,'invNumCheck');var invdate = $('#dateofinvoice').val().split("/"); var retType = $('#retType').val();var clientid = $('#clientid').val();
		if(invoiceNumber == 'undefined' || invoiceNumber != invNo){
		$.ajax({
			url: contextPath+"/invoiceNumbercheck/"+invno+"/"+retType+"/"+clientid+"/"+invdate[1]+"/"+invdate[2]+"?editinv="+editinv+"&gstno="+gstnumber,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(status) {
				if(status) {$('#invno_Msg').html('Invoice Number is already exists');$('#invoicenumber').addClass("has-error has-danger");
				} else {$('#invno_Msg').html('');}
			},
			error : function(status) {
			}
		});
		}else{
			$('#invno_Msg').html('');
			$('#invoicenumber').removeClass("has-error has-danger");
		}
		var val = $('#termDays').val();
		var invdt = $('#dateofinvoice').val();
		var mnyr = invdt.split("/");var dt = parseInt(mnyr[0]);var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
		var d = new Date(yr, mn-parseInt(1), dt);
		if(val == ''){
			$('#duedate_div').val("");
		}else{
			d.setDate(d.getDate() + parseInt(val));
			var day = d.getDate();
			var month = d.getMonth()+parseInt(1);
			var year=d.getFullYear();
			var mndate = ('0' + day).slice(-2) + '/' + ('0' + (month)).slice(-2) + '/' + year;
			$('#duedate_div').datetimepicker({
				value: mndate,
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(yr, mn-parseInt(1), dt)
			});
		}
	}
	function invokeTransGstnPublicAPI(btn){
		var gstnno = $("#transporterId").val();
		$('#transIdnumber_Msg').text('');
		var tranurl = "";
		var statecode_arr = ['01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','29','30','31','32','33','34','35','36','37','38'];
		var userid = $('#userid').val();
		var gstnortransporterid = "GSTIN";
			if(gstnno != '') {
				var statecode = gstnno.substring(0, 2);
				$(btn).addClass('btn-loader');
				var gstnumber = gstnno.toUpperCase();
				if(statecode_arr.indexOf(statecode) == -1){
					gstnortransporterid = "TRANSPORTER";
					tranurl = contextPath+"/transGSTINsearch/"+userid+"/"+clientId+"?transin="+gstnumber;
				}else{
					gstnortransporterid = "GSTIN";
					tranurl = contextPath+"/publicsearch?gstin="+gstnumber+"&userid="+userid;
				}
				$.ajax({
					url: tranurl,
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
						if(gstnortransporterid == "TRANSPORTER"){
							$('#transporterName').val("");
							if(response.status_cd == '0') {
								$('#transIdnumber_Msg').text(response.status_desc);
							}else if(response.error && response.error.message) {
								if(response.error.message == 'SWEB_9035'){$('#transIdnumber_Msg').text("No Records Found");	
								} else{$('#transIdnumber_Msg').text(response.error.message);}
							}
							if(response.status_cd == '1') {
								if(response.data) {
									var address = "";
									Object.keys(response.data).forEach(function(key) {
										if(response.data['tradeName'] == '' || response.data['tradeName'] == null){
											$('#transporterName').val(response.data['legalName']);
										}else{
											$('#transporterName').val(response.data['tradeName']);
										}
									});
								}
							}
						}else{						
							if(response.error && response.error.message) {
								if(response.error.message == 'SWEB_9035'){$('#transIdnumber_Msg').text("No Records Found");	
								} else{$('#transIdnumber_Msg').text(response.error.message);}
							} else if(response.status_cd == '0') {
								$('#transIdnumber_Msg').text(response.status_desc);
							}
							if(response.status_cd == '1') {
								if(response.data) {
									var address = "";
									Object.keys(response.data).forEach(function(key) {
										if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
											$('#transporterName').val(response.data['lgnm']);
										}else{
											$('#transporterName').val(response.data['tradeNam']);
										}
									});
								}
							}
						}
						$(btn).removeClass('btn-loader');
					},
					error : function(e, status, error) {
						$(btn).removeClass('btn-loader');
					}
				});
			}
	}
	function invokegstnPublicAPI(btn) {
		dealertype = "";
		var invType = $('#invTyp').val();var gstnno = $("#billedtogstin").val();$('#igstnnumber_Msg').text('');updatePan(gstnno);
		var userid = $('#userid').val();
	if(invType == 'SEWP' || invType == 'SEWPC'){$('#billedtostatecode').val('97-Other Territory');}
			if(gstnno != '') {
				$(btn).addClass('btn-loader');
				var gstnumber = gstnno.toUpperCase();
				$.ajax({
					url: contextPath+"/publicsearch?gstin="+gstnumber+"&userid="+userid,
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
						if(response.error && response.error.message) {
							if(response.error.message == 'SWEB_9035'){$('#igstnnumber_Msg').text("No Records Found");	
						  	} else{$('#igstnnumber_Msg').text(response.error.message);}
						} else if(response.status_cd == '0') {
							$('#igstnnumber_Msg').text(response.status_desc);
						}
						if(response.status_cd == '1') {
							if(response.data) {
								var address = "";
								Object.keys(response.data).forEach(function(key) {
									if(key == 'dty'){
										dealertype = response.data['dty'];
										$('#dealerType').val(response.data['dty']);
									}
									if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
										$('#billedtoname').val(response.data['lgnm']);
									}else{
										$('#billedtoname').val(response.data['tradeNam']);
									}
								if(key == 'pradr'){
								Object.keys(response.data['pradr']['addr']).forEach(function(key){
									if(response.data['pradr']['addr'][key] != ''){address = address.concat(response.data['pradr']['addr'][key]+",");}
								});
							}
							});
							$('#billingAddress').val(address.slice(0,-1));
							}
						}
						$(btn).removeClass('btn-loader');
					},
					error : function(e, status, error) {
						$(btn).removeClass('btn-loader');
					}
				});
			}
		}
	function updateB2BUR(){
		var suptype = $('#printerintra').val();
		if(suptype == 'Inter'){
			interStateFlag=false;
		}else{
			interStateFlag=true;
		}
		var i=1;
		$.each($("#allinvoice tr"),function() { 
			findTaxAmount(i);
			findCessAmount(i);
			findITCValue(i);
			i++;
		});
	}
	function updateCDNUR(){
		var cdnurtype = $('#cdnurtyp').val();var rtype = $('#retType').val();
		if(rtype == 'GSTR1'){
			if(cdnurtype != 'B2CS'){
				interStateFlag=false;
				if(cdnurtype == 'EXPWOP'){
					$('.taxrateval,.cessval').attr("readonly",true);
					$('.taxrateval,.cessval').addClass("disabled");
					$('.taxrateval,.cessval').val("0");
					$("#includetax").prop("checked",false);
					$("#includetax").prop("disabled",true);
				}else{
					$('.taxrateval,.cessval').attr("readonly",false);
					$('.taxrateval,.cessval').removeClass("disabled");
					$("#includetax").prop("disabled",false);	
				}

				var i=1;
				$.each($("#allinvoice tr"),function() { 
					findTaxAmount(i);
					findCessAmount(i);
					i++;
				});
			}else{
				$('.taxrateval,.cessval').attr("readonly",false);
				$('.taxrateval,.cessval').removeClass("disabled");
				$("#includetax").prop("disabled",false);
				updateRateType(clntStatename, rtype);
			}
			
		}
	}
	
	function updatePan(value) {
		$('#serialnoofinvoice').trigger('change');
		dealertype = "";
		var rtype = $('#retType').val();var type = $('#idInvType').val();var gstno = $('#billedtogstin').val();
		if(value == ''){
			if((type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR2') || (type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR5') || (type == 'CDNURA' && rtype == 'GSTR2') || (type == 'CDNURA' && rtype == 'GSTR5')){
				$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
			}else if((type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR1') || (type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR5') || (type == 'CDNURA' && rtype == 'GSTR1') || (type == 'CDNURA' && rtype == 'GSTR5')){
				$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
			}else if(type == 'Credit/Debit Notes' && rtype == 'GSTR1'){
				$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
			}else if(type == 'Credit/Debit Notes' && rtype == 'GSTR2'){
				$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
			}
			if(type == 'B2B' || type == 'B2C' || type == 'Advances' || type == 'Nil Supplies'){
				$('.negativevalues').attr("onkeypress","return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)");
				$('.negativevalues').attr("pattern","^([-,1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$");
			}
			if(rtype == 'GSTR2'){
				if(type == "B2B" || type == "B2B Unregistered" || type == "B2BUR" || type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers'){
					$('.printerintra').css("display","block");
				}
			}
		}else{
			$('.printerintra').css("display","none");$('.cdnurtype').removeClass("astrich");$('#cdnurtyp,#cdnurinvtyp').attr("readonly","readonly").addClass("disabled").val("");$('#cdnurtyp').removeAttr("required");$('#cdnurinvtyp').removeAttr("required");
			if(type == 'Advances' || type == 'Nil Supplies'){
				$('.negativevalues').attr("onkeypress","return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)");
				$('.negativevalues').attr("pattern","^([-,1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$");
			}else{
				$('.negativevalues').attr("onkeypress","return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)");
				$('.negativevalues').attr("pattern","^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$");
			}
		}
		if(rtype != 'GSTR2'){
			if(value.length == 15) {
				$.ajax({
					url: contextPath+"/srchstatecd?code="+value.substring(0,2),
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
						if(response) {
							if(response.name != ''){$('.placeofsupply').removeClass("has-error has-danger");$('.placeofsupply .with-errors').html('');$("#billedtostatecodeempty").hide(); }
							$('#billedtostatecode').val(response.name);
						}
					}
				});
			}
		}
		updateRateType(clntStatename, rtype);
	}
	/*$("#allinvoice tr").click(function() {
		$(this).addClass("addrowshadow");
		$(this).siblings().removeClass("addrowshadow");
	});*/
	$("#allinvoice tr").click(function() {
		$(this).addClass("addrowshadow");
		$(this).siblings().removeClass("addrowshadow");
	});

	function editAsAmendment(invId, retType) {
		$('#strAmendment').val("true");$('.form-control').removeClass('disable');$('#addrow,#impSeraddrow,#addrow1').removeAttr('disabled').css("background-color","");$('.btn_popup_amnd').hide();$('.btn_popup_save').show();$('.gstr2adeletefield').css("display","block");var i=1;
	}
	
	function addEccomGSTIN(modalId) {
		$('.errormsg').css('display','block').html('');	$('#invoiceModal').css("z-index","1033");$('#'+modalId).modal('show');
		$('#aegstnnumber').val($('#ecommercegstin').val());
		$.ajax({
			url: contextPath+"/countrieslist",
			contentType: 'application/json',
			success : function(response) {
				$('#aecountriesList').append('<option value="India">India</option>');
				for(var i = 0; i < response.length; i++) {$('#aecountriesList').append("<option value="+response[i].name+">"+response[i].name+"</option>");}
			}
		});
	}
	
	function saveEcommerceGSTIN() {
		if(validateEcommerce()){
			$('.errormsg').css('display','none');
			$('.with-errors').html('');
			$('.form-group').removeClass('has-error has-danger');
			$.ajax({
				type: "POST",
				url: $("#aecommerce_form").attr('action'),
				data: $("#aecommerce_form").serialize(),
				success: function(data) {
					$('#ecommercegstin').val($('#aegstnnumber').val());
					$("#addeccommercegstin").hide();
					$('#invoiceModal').css("z-index","");
					$('#addEcommerceGSTIN').modal('hide');
					$('.modal-backdrop.show').css("display","none");
					$("#aecommerce_form")[0].reset();
				},
				error: function(data) {
					$("#addeccommercegstin").hide();
					$("#aecommerce_form")[0].reset();
				}
			});
		}	
	}
	
	function validateEcommerce(){
		var c = 0;
	var custname = $('#aebname').val();
	var contactperson = $('#aoperator').val();
	custname = custname.replace(/ +/g, "");
	contactperson = contactperson.replace(/ +/g, "");
	var state = $('#aestate').val();
	var ecomgstin = $('#aegstnnumber').val();
	if(custname==""){
		$('#abusinessName_Msg').text("Please enter Name"); 
		c++;
	}else{  
		$('#abusinessName_Msg').text(""); 
	}
	if(contactperson==""){
		$('#aoperator_Msg').text("Please enter Ecommerce Operator Name");
		c++;
	}else{
		$('#aoperator_Msg').text("");
	}	
	if(state==""){
		$('#aestate_Msg').text("Please enter State Name");
		c++;
	}else{
		$('#aestate_Msg').text("");
	}	
	if(ecomgstin==""){
		$('#aegstnnumber_Msg').text("Please enter Ecommerce GSTIN");
		c++;
	}else{
		$('#aegstnnumber_Msg').text("");
	}	
	return c==0; 
}
	

	function invokePublicecomAPI(btn) {
		var gstnno = $("#aegstnnumber").val();
		updateecomPan(gstnno);
		var userid = $('#userid').val();
		if(gstnno != '') {
			var gstnumber = gstnno.toUpperCase();
			$(btn).addClass('btn-loader');
			$.ajax({
				url: contextPath+"/publicsearch?gstin="+gstnumber+"&ipAddress="+ipAddress+"&userid="+userid,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response.error && response.error.message) {	
						if(response.error.message == 'SWEB_9035'){
							$('#aegstnnumber_Msg').text("No Records Found");	
					  	} else{
							$('#aegstnnumber_Msg').text(response.error.message);
					  	}
					}
					if(response.status_cd == '1') {
						if(response.data) {
							var address = "";
							if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
								$('#aebname').val(response.data['lgnm']);
								$('#ecomoperatorname').text(response.data['lgnm']);
								$('#invoiceEcomOperator').val(response.data['lgnm']);
								if(response.data['lgnm'] != ''){
									$('#aeomsg ul.list-unstyled li').html('');
									$('#aeomsg').removeClass('has-error has-danger');
								}
							}else{
								$('#aebname').val(response.data['tradeNam']);
								$('#ecomoperatorname').text(response.data['tradeNam']);
								$('#invoiceEcomOperator').val(response.data['tradeNam']);
								if(response.data['tradeNam'] != ''){
									$('#aeomsg ul.list-unstyled li').html('');
									$('#aeomsg').removeClass('has-error has-danger');
								}
							}
						Object.keys(response.data).forEach(function(key) {
							if(key == 'pradr'){
							Object.keys(response.data['pradr']['addr']).forEach(function(key){
								if(response.data['pradr']['addr'][key] != ''){
									if(key != 'pncd' && key != 'stcd'){
										address = address.concat(response.data['pradr']['addr'][key]+",");
									}
									if(key == 'pncd'){
										$('#aeoperatorpincode').val(response.data['pradr']['addr'][key]);
									}
									if(key == 'city'){
										$('#aeopearatorcity').val(response.data['pradr']['addr'][key]);
									}
								}
							});
						}
						});
						$('#aeaddresss').val(address.slice(0,-1));
						}
					}
					$(btn).removeClass('btn-loader');
					//$('#operatorsave').removeClass('disabled');
				},
				error : function(e, status, error) {
					$(btn).removeClass('btn-loader');
				}
			});
		}
	}

	function updateecomPan(value) {
		if(value.length == 15) {
			$('#aoperatorPanNumber').val(value.substring(2,12));
			$('.apan_number .with-errors').html('');
			$('.apan_number').removeClass('has-error has-danger');
			$.ajax({
				url: contextPath+"/srchstatecd?code="+value.substring(0,2),
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response) {
						$('#aestate').val(response.name);
					}
				}
			});
		}
	}
	function closemodal(modalid){
		$(".popup_error").css("display","none");
		$('#cess_qty').removeAttr("checked");
		$('#cess_taxable').prop("checked",true);
		$('.adv_taxable_type').val("false").prop("checked",false);
		$('.invoiceLevelCess').val("Yes").prop("checked",false);
		$('#invLevelCess').val("Yes");
		$('#tcstdstext').html("Enable TCS or TDS : ");
		$('.advancetax').html('Rate Inclusive tax');
		$('.backdetails_wrap,.rateinclusive,.invoiceLevelCess').css("margin-top","0px");
		$('#ad_tax4').html("Rate");
    $('.rateinclusive').parent().removeClass('col-md-6');
		$('#itcClaimedDateP').css('display','none');$('.vehicle_updtrsn,.ewaybilladdress').css('display','none'); $('#revChargeNoDiv').css("display","none");$('.printerintra').css("display","none");
		$('.notewaybilladdress').css('display','block');
		$('.negativevalues').attr("onkeypress","return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)");
		$('.negativevalues').attr("pattern","^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$");
		var terms_cond = $("#invtermss").text();$('#bankTerms').val(terms_cond);$('#invterms').html(terms_cond);
		$('#paymentStatusinv,#InvPendingAmount,#InvReceivedAmount,#cdn_taxableamount,#convertedtoinv,#termDays,#duedate_div,#irnnumber,#printerintra,#srctype').val("");
		//$("#bankTerms").val("");$('#invterms').html('');$("#bankTerms").html("");$('#invterms').val('');
		$('.diffPercent').val("No").removeAttr('checked');
		$('#tdstcssection ,#tcs_percent,#tdssection,#tds_percent').attr("readonly","true");$('#tdstcssection,#tdssection').attr("disabled",true);$('.taxrateval,.cessval').removeAttr("disabled").removeAttr("readonly");
		$("select#tdstcssection option[value='']").remove();
		$("select#tdstcssection option[value='206C(1)']").remove();
		$('.taxrateval,.cessval').removeClass("disabled");
		$('#tax_rate,#rate_as').addClass("astrich")
		$('.apply_tcsOtds').remove();$('.apply_tds').remove();$('#lutDiv').css("display","none");$('.Terms,.duedate').addClass("d-none");
		$('#sortable_table tbody').find("tr:gt(0)").remove();$('#sortable_table tfoot tr').not(':last').remove();
		var invtype = $('#idInvType').val();
		if(invtype !='ITC Reversal' && invtype !='ISD'){$('#product_text1').attr('name','items[0].itemno');$('#hsn_text1').attr('name','items[0].hsn');$('#uqc_text1').attr('name','items[0].uqc');$('#qty_text1').attr('name','items[0].quantity');$('#rate_text1').attr('name','items[0].rateperitem');$('#discount_text1').attr('name','items[0].discount');$('#taxableamount_text1').attr('name','items[0].taxablevalue');$('#taxrate_text1').attr('name','items[0].rate');$('#abb1').attr('name','items[0].totaltaxamount');$('#cessrate_text1').attr('name','items[0].cessrate');$('#cessamount_text1').attr('name','items[0].cessamount');$('#itctype_text1').attr('name','items[0].elg');$('#itcpercent_text1').attr('name','items[0].elgpercent');$('#total_text1').attr('name','items[0].total');}
		if(invtype =='ITC Reversal'){$('#sortable_table1 tbody tr:nth-last-child(1) , #sortable_table1 tbody tr:nth-last-child(2) , #sortable_table1 tbody tr:nth-last-child(3)').remove();}
		$('#allinvoice').append('<tr style="display:none" class="rowshadow item_edit"><td align="center"></td><td align="left"><input type="text" id="new_product" class="btn"></td><td align="left"><input type="text" id="new_hsn" class="btn"></td><td align="right"><input type="text" id="new_uqc" class="btn AdvancesFlag"></td><td align="right"><input type="text" id="new_qty" class="btn AdvancesFlag"></td><td align="right"><input type="text" id="new_rate" class="btn AdvancesFlag"></td><td align="right"><input type="text" id="new_discount" class="btn SnilFlag AdvancesFlag disFlag"></td><td align="right"><input type="text" id="new_taxableamount" class="btn" disabled></td><td align="right"><input type="text" id="new_taxrate" class="btn SnilFlag"></td><td align="center"><input type="text" id="new_igsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_cgsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_sgsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_cessrate" class="btn SnilFlag"></td><td align="center"><input type="text" id="new_cessamount" class="btn SnilFlag" disabled></td><td align="center"><input type="text" id="new_itcrule" class="btn addITCRuleFlag"></td><td align="center"><input type="text" id="new_itctype" class="btn addITCFlag"></td><td align="center"><input type="text" id="new_itcpercent" class="btn"></td><td align="center"><input type="text" id="new_igstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_cgstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_sgstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_cessitc" class="btn addITCFlag"></td><td align="center"><input type="text" id="new_advrcvd" class="btn addAdvFlag" disabled></td><td align="center"><input type="text" id="new_total" class="btn" disabled></td><td width="3%"></td></tr>')
		$('#invitemdetails').text('');addAdvFlag = false;rowCount = 1;
		$('.form-control').removeClass('disable');$('#'+modalid).modal('toggle');$(".body-cls").removeClass('no-scroll');$('#billingAddress_id').text('');
		$('#termselect').text('- Select -')
		$('.taxrateval').removeAttr("readonly");
		dealertype="";
		$('#ewybuyerDtls_gstin,#ewybuyerDtls_lglNm,#ewybuyerDtls_addr1,#ewybuyerDtls_addr2,#ewybuyerDtls_loc,#ewybuyerDtls_pin,#ewybuyerDtls_state,#ewybuyerDtls_pos,#ewydispatcherDtls_nm,#ewydispatcherDtls_addr1,#ewydispatcherDtls_addr2,#ewydispatcherDtls_loc,#ewydispatcherDtls_pin,#ewydispatcherDtls_stcd,#ewyshipmentDtls_trdNm,#ewyshipmentDtls_lglNm,#ewyshipmentDtls_addr1,#ewyshipmentDtls_addr2,#ewyshipmentDtls_loc,#ewyshipmentDtls_pin,#ewyshipmentDtls_stcd').val("");
		$('.tcsval,#tdsval').removeAttr('checked').val("false");
		$('#tdsval,.tcsval').prop('checked',false);
		$('#tcs_val').removeAttr('checked');
		$('#tds_val').prop("checked",true);
		$('#ewayBillSave_btn,#draft_btn,#cancelEwayBillInvoice,#sirnNumber_txt').hide();
		$('#addrow').removeAttr('disabled').css("background-color", "");
		$('.gstr2adeletefield').css("display", "block");
		$('#addrow').removeAttr("onclick");
		$('form[name="salesinvoceform"]').validator('update');
		$('#ewaybuyerAddr,#ewaydispatchAddr,#ewayshipmentAddr,#sirnNumber').html('');
		$('.form-control,.che_form-control').removeClass('disabled');
		  $('.btn_popup_save').removeClass('btn-loader-blue');
		  $('#revchargetype').removeAttr("readonly").removeAttr("disabled");
		  $('#save_btn').removeClass("disabled");
		  $('#salesinvoceform').find('p#newledger').each(function(){
			  $(this).parent().removeClass("advadj_ledger");$(this).parent().removeClass("adv_ledger");$(this).parent().removeClass("nil_ledger");
			  $(this).parent().removeClass("b2b_ledger");$(this).parent().removeClass("exp_ledger");$(this).parent().removeClass("imps_ledger");
			  $(this).parent().removeClass("impg_ledger");$(this).parent().removeClass("b2b_ledger");$(this).parent().removeClass("crdr_ledger");$(this).parent().removeClass("crddr_ledger");
		});
	}
	$(function(){
		$('.itemname').keypress(function(key) {
			if((key.charCode < 97 || key.charCode > 122) && (key.charCode < 65 || key.charCode > 90) && (key.charCode < 48 || key.charCode > 57) && (key.charCode != 45)) return false;
		});
	});
	$("#shippingAddress").keyup(function() {
		var billadd=$("#billingAddress").val();var shipadd=$("#shippingAddress").val();
		if(billadd == shipadd){$("#samebilladdress").prop("checked", true);
		}else{$("#samebilladdress").prop("checked", false);}
	});
	$('#new_product').keypress(function(key) {
		if((key.charCode < 97 || key.charCode > 122) && (key.charCode < 65 || key.charCode > 90) && (key.charCode < 48 || key.charCode > 57) && (key.charCode != 45)) return false;
	});
	
	
	/* function addVehicle_row() {
		 var rowCount = $('#vehicleDetailsBody tr').length;
			var tablen = rowCount;
			rowCount = rowCount+1;
			var table=document.getElementById("sortable_table4");
			$('#vehicleDetailsBody').append('<tr><td class="form-group"><input id="vehicleNo'+rowCount+'" class="form-control" name="VehiclListDetails['+(rowCount-1)+'].vehicleNo"> </td><td class="form-group"><input type="text" class="form-control" id="fromPlace'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].fromPlace" style="width:130px;"/></td><td class="form-group"><input type="text" class="form-control" id="fromState'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].fromState"/></td><td class="form-group"><input type="text" class="form-control" id="tripshtNo'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].tripshtNo"/></td><td class="form-group"><input type="text" class="form-control" id="userGSTINTransin'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].userGSTINTransin"/></td><td class="form-group"><input type="text" class="form-control" id="enteredDate'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].enteredDate"/></td><td class="form-group"><input type="text" class="form-control" id="transMode'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].transMode"/></td><td class="form-group"><input type="text" class="form-control" id="transDocNo'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].transDocNo"/></td><td class="form-group"><input type="text" class="form-control" id="transDocDate'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].transDocDate"/></td><td class="form-group"><input type="text" class="form-control" id="groupNo'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].groupNo"/></td></tr>');
			//$('#vehicleDetailsBody').append('<tr><td id="sno_row2" align="center">'+rowCount+'</td><td class="form-group" style="border: none;margin-bottom: 0px;"><input id="vehicleNo'+rowCount+'" class="form-control" name="VehiclListDetails['+(rowCount-1)+'].vehicleNo"> </td><td class="form-group"><input type="text" class="form-control" id="fromPlace'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].fromPlace" /></td><td class="form-group"><input type="text" class="form-control" id="fromState'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].fromState"/></td><td class="form-group"><input type="text" class="form-control" id="tripshtNo'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].tripshtNo"/></td><td class="form-group"><input type="text" class="form-control" id="userGSTINTransin'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].userGSTINTransin"/></td><td class="form-group"><input type="text" class="form-control" id="enteredDate'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].enteredDate"/></td><td class="form-group"><input type="text" class="form-control" id="transMode'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].transMode"/></td><td class="form-group"><input type="text" class="form-control" id="transDocNo'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].transDocNo"/></td><td class="form-group"><input type="text" class="form-control" id="transDocDate'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].transDocDate"/></td><td class="form-group"><input type="text" class="form-control" id="groupNo'+rowCount+'" name="VehiclListDetails['+(rowCount-1)+'].groupNo"/></td></tr>');
	 }*/
	function addEbillrow(tableid){
		if(tableid == 'vehicle_table'){
			var vrowCount = $('#vehicleDetailsBody tr').length;
			var tablen = vrowCount;
			vehiclerowCount = vrowCount+1;
			var table3=document.getElementById("vehicle_table");
			$('#vehicleDetailsBody ').append('<tr><td id="sNo_row2" align="center">'+vehiclerowCount+'</td><td class="form-group"><input id="vehicleNo'+vehiclerowCount+'" class="form-control" name="vehiclListDetails['+(vehiclerowCount-1)+'].vehicleNo" required></td><td class="form-group"><select class="form-control" id="transMode'+vehiclerowCount+'" name="vehiclListDetails['+(vehiclerowCount-1)+'].transMode" required><option value="">-Select-</option><option value="1">Road</option><option value="2">Rail</option><option value="3">Air</option><option value="4">Ship</option></select></td><td class="form-group"><select class="form-control vehicleType'+vehiclerowCount+'" id="vehicleType'+vehiclerowCount+'" name="vehiclListDetails['+(vehiclerowCount-1)+'].vehicleType" required><option value="">-Select Type-</option><option value="R">Regular</option><option value="O">Over Dimensional Cargo</option></select></td><td class="form-group"><select class="form-control" id="reasonRem'+vehiclerowCount+'" name="vehiclListDetails['+(vehiclerowCount-1)+'].reasonRem"><option value="">-Select-</option><option value="Due to Break Down">Due to Break Down</option><option value="Due to Transhipment">Due to Transhipment</option><option value="Others (Pls. Specify)">Others (Pls. Specify)</option><option value="First Time">First Time</option></select></td><td class="form-group"><input type="text" class="form-control" id="transDocNo'+vehiclerowCount+'" name="vehiclListDetails['+(vehiclerowCount-1)+'].transDocNo"/></td><td class="form-group"><input type="text" class="form-control transDocDate'+vehiclerowCount+'" id="transDocDate'+vehiclerowCount+'" name="vehiclListDetails['+(vehiclerowCount-1)+'].transDocDate"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_btn'+vehiclerowCount+'" class="vehicle_delete disabled" onclick="delete_vehiclerow('+vehiclerowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
		$('.enteredDate'+vehiclerowCount).datetimepicker({
			  	timepicker: false,
			  	format: 'd/m/Y',
			  	scrollMonth: true
			});
			$('.transDocDate'+vehiclerowCount).datetimepicker({
			  	timepicker: false,
			  	format: 'd/m/Y',
			  	scrollMonth: true
			});
		}else if(tableid == 'vehicleupdate_table'){
			var vrowCount = $('#vehicleUpdateDetailsBody tr').length;
			var tablen = vrowCount;
			vehiclerowCount = vrowCount+1;
			var table3=document.getElementById("vehicleupdate_table");
			$('#vehicleUpdateDetailsBody ').append('<tr><td id="sNo_row2" align="center">'+vehiclerowCount+'</td><td class="form-group"><input id="vehicle_No'+vehiclerowCount+'" class="form-control"></td><td class="form-group"><select class="form-control" id="trans_Mode'+vehiclerowCount+'"><option value="">-Select-</option><option value="1">Road</option><option value="2">Rail</option><option value="3">Air</option><option value="4">Ship</option></select></td><td class="form-group"><select id="reason_Rem'+rowCount+'" class="form-control reasonrem"  onchange="reasonCodeSelect('+vehiclerowCount+')"><option value="">-Select-</option><option value="Due to Break Down">Due to Break Down</option><option value="Due to Transhipment">Due to Transhipment</option><option value="Others (Pls. Specify)">Others (Pls. Specify)</option><option value="First Time">First Time</option></select></td><td><select class="form-control vehicle_Type'+vehiclerowCount+'" id="vehicle_Type'+vehiclerowCount+'"><option value="">-Select type-</option><option value="R">Regular</option><option value="O">Over Dimensional Cargo</option></select></td><td class="form-group"><input type="text" class="form-control" id="trans_DocNo'+vehiclerowCount+'"/></td><td class="form-group"><input type="text" class="form-control trans_DocDate'+vehiclerowCount+'" id="trans_DocDate'+vehiclerowCount+'"/></td></tr>');
		$('.entered_Date'+vehiclerowCount).datetimepicker({
			  	timepicker: false,
			  	format: 'd/m/Y',
			  	scrollMonth: true
			});
			$('.trans_DocDate'+vehiclerowCount).datetimepicker({
			  	timepicker: false,
			  	format: 'd/m/Y',
			  	scrollMonth: true
			});
		}
		$('form[name="salesinvoceform"]').validator('update');
	}
	function delete_vehiclerow(no){
		var vtab=document.getElementById("vehicle_table");
		
		if(no >= rowCount){
			no=no-1;
		}
		vtab.deleteRow(no);
		rowCount--;
		$("#vehicleDetailsBody tr").each(function(index) {
			 $(this).attr('id',index+1);
			 $(this).find("#sNo_row2").html(index+1);
			 var rowno = (index+1).toString();
			 var rownoo = (index).toString();
			 $(this).find('input , select').each (function() {
					var inputname1 = $(this).attr('class');
					var inputid1 = $(this).attr('id');
					var inputname = $(this).attr('name');
					var abcd = $(this).attr('onkeyup');
					var change = $(this).attr('onchange');
					/*if(change != undefined){
						change = replaceAt(change,20,rowno);
				   	    $(this).attr('onchange',change);
					}
					if(abcd != undefined){
						abcd = replaceAt(abcd,13,rowno);
				   	    $(this).attr('onkeyup',abcd);
					}
					if(inputname1 != undefined){
						if(rowno<10){
							inputname1 = inputname1.slice(0, -1);
			   	    		}else{
			   	    			inputname1 = inputname1.slice(0, -2);
			   	    		}
						inputname1 = inputname1+rowno;
						$(this).attr('class',inputname1);
					}*/
					if(inputid1 != undefined){
						if(rowno<10){
							inputid1 = inputid1.slice(0, -1);
			   	    		}else{
			   	    			inputid1 = inputid1.slice(0, -2);
			   	    		}
						inputid1 = inputid1+rowno;
						$(this).attr('id',inputid1);
					}
					if(inputname != undefined){
						if(inputname.indexOf("vehiclListDetails[") >= 0) {
							if(rownoo == '9'){
								inputname = inputname.replace('10',' ');
							}
							inputname = replaceAt(inputname,18,rownoo);
							$(this).attr('name',inputname);
							
						}
					}
				});
			 $(this).find('a').each (function() {
					var aname = $(this).attr('id');
					if(aname != undefined){
						if(rowno<10){
							aname = aname.slice(0, -1);
			   	    		}else{
			   	    			aname = aname.slice(0, -2);
			   	    		}
					aname = aname+rowno;
					$(this).attr('id',aname);
					}
					var det = $(this).attr('class');
					if(det != 'vehicle_delete'){
					}else{
						var abcd = $(this).attr('onclick');
				   	    abcd = replaceAt(abcd,18,rowno);
				   	    $(this).attr('onclick',abcd);
					}
				});
		});
	}
	$('#ewayBillDate').datetimepicker({
		timepicker: false,
		format: 'd/m/Y',
		scrollMonth: true
	});
	
	function viewHsnSummary(returnType){
		$('#hsnsummarysortable_table4 tbody').find("tr:gt(0)").remove();
		$('#hsnModal').modal('show');
		$.ajax({
			url: contextPath+'/hsnSummary'+urlSuffixs+'/'+returnType+'/'+month+'/'+year,
			cache: false,
			type : "GET",
			async: true,
			contentType : "application/json",
			success : function(hsndata) {
				for(var i=1;i<hsndata.length;i++){
					addHSNSummaryrow();
				}
				$.each(hsndata, function(index, hsnItemData) {
					index++;
					$('#hsnc_text'+index).val(hsnItemData.hsn_sc);
					$('#hsndesc_text'+index).val(hsnItemData.desc);
					$('#huqc_text'+index).val(hsnItemData.uqc);
					$('#hqty_text'+index).val(hsnItemData.qty);
					if((month > 4 && year > 2020) || (month < 4 && year > 2021)){
						$('#hrt_text'+index).val(hsnItemData.rt);
					}else{
						$('#hval_text'+index).val(hsnItemData.val);
					}
					
					$('#htxval_text'+index).val(hsnItemData.txval);
					$('#higst_text'+index).val(hsnItemData.iamt);
					$('#hcgst_text'+index).val(hsnItemData.camt);
					$('#hsgst_text'+index).val(hsnItemData.samt);
					$('#hcsamt_text'+index).val(hsnItemData.csamt);
				});
			}
		});
		
	}
	function addHSNSummaryrow(){
		hsnrowCount = $('#hsnSummarybody tr').length;	
		
		hsnrowCount = hsnrowCount+1;
		var nameleng = hsnrowCount-1;
		if((month > 4 && year > 2020) || (month < 4 && year > 2021)){
			$('#hsnSummarybody').append('<tr><td id="sno_row2" align="center">'+hsnrowCount+'</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><input type="text" class="form-control hsnc_text'+hsnrowCount+'" id="hsnc_text'+hsnrowCount+'" name="hsnData['+nameleng+'].hsnSc" required="required" placeholder="HSN/SAC" /></td><td class="form-group"><input type="text" class="form-control hsndesc_text'+hsnrowCount+'" id="hsndesc_text'+hsnrowCount+'" placeholder="HSN Desc" name="hsnData['+nameleng+'].desc"/></td><td class="form-group"><input type="text" class="form-control huqc_text'+hsnrowCount+'" id="huqc_text'+hsnrowCount+'" name="hsnData['+nameleng+'].uqc" placeholder="UQC" /></td><td class="form-group"><input type="text" class="form-control hqty_text'+hsnrowCount+'" id="hqty_text'+hsnrowCount+'" name="hsnData['+nameleng+'].qty" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter Quantity" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Quantity" /></td><td class="form-group"><input type="text" class="form-control hrt_text'+hsnrowCount+'" id="hrt_text'+hsnrowCount+'" name="hsnData['+nameleng+'].rt" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please Tax Rate" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Tax Rate" /></td><td class="form-group"><input type="text" class="form-control htxval_text'+hsnrowCount+'" id="htxval_text'+hsnrowCount+'" name="hsnData['+nameleng+'].txval" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Taxable Value" /></td><td class="form-group"><input type="text" class="form-control higst_text'+hsnrowCount+'" id="higst_text'+hsnrowCount+'" name="hsnData['+nameleng+'].iamt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Igst Amount" /></td><td class="form-group"><input type="text" class="form-control hcgst_text'+hsnrowCount+'" id="hcgst_text'+hsnrowCount+'" name="hsnData['+nameleng+'].camt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Cgst Amount" /></td><td class="form-group"><input type="text" class="form-control hsgst_text'+hsnrowCount+'" id="hsgst_text'+hsnrowCount+'" name="hsnData['+nameleng+'].samt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Sgst Amount" /></td><td class="form-group"><input type="text" class="form-control hcsamt_text'+hsnrowCount+'" id="hcsamt_text'+hsnrowCount+'" name="hsnData['+nameleng+'].csamt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Cess Amount" /></td><td align="center" width="2%" style="text-align:center;"><a href="javascript:void(0)" id="delete_button'+hsnrowCount+'" class="item_delete" onclick="delete_HsnSummaryrow('+hsnrowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
		}else{
			$('#hsnSummarybody').append('<tr><td id="sno_row2" align="center">'+hsnrowCount+'</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><input type="text" class="form-control hsnc_text'+hsnrowCount+'" id="hsnc_text'+hsnrowCount+'" name="hsnData['+nameleng+'].hsnSc" required="required" placeholder="HSN/SAC" /></td><td class="form-group"><input type="text" class="form-control hsndesc_text'+hsnrowCount+'" id="hsndesc_text'+hsnrowCount+'" placeholder="HSN Desc" name="hsnData['+nameleng+'].desc"/></td><td class="form-group"><input type="text" class="form-control huqc_text'+hsnrowCount+'" id="huqc_text'+hsnrowCount+'" name="hsnData['+nameleng+'].uqc" placeholder="UQC" /></td><td class="form-group"><input type="text" class="form-control hqty_text'+hsnrowCount+'" id="hqty_text'+hsnrowCount+'" name="hsnData['+nameleng+'].qty" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter Quantity" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Quantity" /></td><td class="form-group"><input type="text" class="form-control hval_text'+hsnrowCount+'" id="hval_text'+hsnrowCount+'" name="hsnData['+nameleng+'].val" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Total Value" /></td><td class="form-group"><input type="text" class="form-control htxval_text'+hsnrowCount+'" id="htxval_text'+hsnrowCount+'" name="hsnData['+nameleng+'].txval" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Taxable Value" /></td><td class="form-group"><input type="text" class="form-control higst_text'+hsnrowCount+'" id="higst_text'+hsnrowCount+'" name="hsnData['+nameleng+'].iamt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Igst Amount" /></td><td class="form-group"><input type="text" class="form-control hcgst_text'+hsnrowCount+'" id="hcgst_text'+hsnrowCount+'" name="hsnData['+nameleng+'].camt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Cgst Amount" /></td><td class="form-group"><input type="text" class="form-control hsgst_text'+hsnrowCount+'" id="hsgst_text'+hsnrowCount+'" name="hsnData['+nameleng+'].samt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Sgst Amount" /></td><td class="form-group"><input type="text" class="form-control hcsamt_text'+hsnrowCount+'" id="hcsamt_text'+hsnrowCount+'" name="hsnData['+nameleng+'].csamt" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Cess Amount" /></td><td align="center" width="2%" style="text-align:center;"><a href="javascript:void(0)" id="delete_button'+hsnrowCount+'" class="item_delete" onclick="delete_HsnSummaryrow('+hsnrowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
		}
		$('form[name="hsnsummaryform"]').validator('update');
	}
	function delete_HsnSummaryrow(no){
		var table4= "";
		var tbodyid = ""
		
		table4 = document.getElementById("hsnsummarysortable_table4"); 
		tbodyid = "hsnSummarybody";
		if(no >= hsnrowCount){
			no=no-1;
		}
		table4.deleteRow(no+1);
		hsnrowCount--;
		$("#"+tbodyid+" tr").each(function(index) {
			 $(this).attr('id',index+1);
			 $(this).find("#sno_row2").html(index+1);
			 var rowno = (index+1).toString();
			 var rownoo = (index).toString();
			 $(this).find('input , select').each (function() {
					var inputname1 = $(this).attr('class');
					var inputname = $(this).attr('name');
					var inputid1 = $(this).attr('id');
					if(inputname1 != undefined){
						if(rowno<10){
							inputname1 = inputname1.slice(0, -1);
			   	    		}else{
			   	    			inputname1 = inputname1.slice(0, -2);
			   	    		}
						inputname1 = inputname1+rowno;
						$(this).attr('class',inputname1);
					}
					if(inputname != undefined){
						if(inputname.indexOf("hsnData[") >= 0) {
							if(rownoo == '9'){
								inputname = inputname.replace('10',' ');
							}
							inputname = replaceAt(inputname,8,rownoo);
							$(this).attr('name',inputname);
							
						}
					}
					if(inputid1 != undefined){
						if(rowno<10){
							inputid1 = inputid1.slice(0, -1);
			   	    		}else{
			   	    			inputid1 = inputid1.slice(0, -2);
			   	    		}
						inputid1 = inputid1+rowno;
						$(this).attr('id',inputid1);
					}
				});
			 $(this).find('a').each (function() {
					var aname = $(this).attr('id');
					if(aname != undefined){
						if(rowno<10){
							aname = aname.slice(0, -1);
			   	    		}else{
			   	    			aname = aname.slice(0, -2);
			   	    		}
					aname = aname+rowno;
					$(this).attr('id',aname);
					}
					var det = $(this).attr('class');
					if(det != 'item_delete'){
					}else{
						var abcd = $(this).attr('onclick');
				   	    abcd = replaceAt(abcd,21,rowno);
				   	    $(this).attr('onclick',abcd);
					}
				});
				
		});
	}
	function addhsnsummary(){
		
			$('#hsnsummaryform').submit();
	}
	function convertFromPI_To_Inv(invId,retType){
		$('#convert_PI_Invoice').addClass('btn-loader-bue');
		$.ajax({
			url: contextPath+"/convertPIToInvoice/"+urlSuffixs+"/"+invId+"/"+retType+"/"+month+"/"+year,
			async: false,
			cache: false,
			success : function(response){
				$('#convert_PI_Invoice').removeClass('btn-loader-bue');
				location.reload(true);
			},error:function(err){
				$('#convert_PI_Invoice').removeClass('btn-loader-bue');
			}
		});
	}
	function cnvrt_PI_To_Invoice(){
		$('#cnvrt_PI_To_Invoice').addClass('btn-loader-bue');
		if(PIArray != ""){
			$.ajax({
				url: contextPath+"/all_convertPIToInvoice/"+urlSuffixs+"/PROFORMAINVOICES/"+month+"/"+year+'?invoiceIds='+PIArray,
				contentType: 'application/json',
				success : function(response){
					$('#cnvrt_PI_To_Invoice').removeClass('btn-loader-bue');
					location.reload(true);
				},error:function(err){
					$('#cnvrt_PI_To_Invoice').removeClass('btn-loader-bue');
				}
			});
		}
	}
	function convertFromPO_To_Inv(invId,retType){
		$('#convert_PO_Invoice').addClass('btn-loader-bue');
		$.ajax({
			url: contextPath+"/convertPOToInvoice/"+urlSuffixs+"/"+invId+"/"+retType+"/"+month+"/"+year,
			success : function(response){
				$('#convert_PO_Invoice').removeClass('btn-loader-bue');
				location.reload(true);
			},error:function(err){
				$('#convert_PO_Invoice').removeClass('btn-loader-bue');
			}
		});
	}
	function cnvrt_PO_To_Invoice(){
		$('#cnvrt_PO_To_Invoice').addClass('btn-loader-bue');
		if(POArray != ""){
			$.ajax({
				url: contextPath+"/all_convertPOToInvoice/"+urlSuffixs+"/PurchaseOrder/"+month+"/"+year+'?invoiceIds='+POArray,
				contentType: 'application/json',
				success : function(response){
					$('#cnvrt_PO_To_Invoice').removeClass('btn-loader-bue');
					location.reload(true);
				},error:function(err){
					$('#cnvrt_PO_To_Invoice').removeClass('btn-loader-bue');
				}
			});
		}
	}
	function updateInvoiceCurrencyCode(){
		var rtype = $('.add_currencyCode').val();
		$('#currency_text').html(rtype);
		$('.usd-lable').html("<span style='padding-left:2px'>1 "+rtype+" = </span>");
	}
	
	function updateInvoiceTypeOfSupply(){
		var invType = $('#exportType').val();
		var eInvType = $('#idInvType').val();
		
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'WPAY' || invType == 'WOPAY') {
			interStateFlag=false;
		}else if(invType == 'R'){
			interStateFlag=true;
		}
		if(eInvType != 'Exports'){
			interStateFlag=false;
		}
		if(invType == 'SEWOP' || invType == 'WOPAY'){
			$("#includetax").prop("disabled",true);
			//$(".taxrate_textDisable").prop('disabled',true);
		}else{
			$("#includetax").prop("disabled",false);
			$(".taxrate_textDisable").prop('disabled',false);
		}
		if(invType == 'DEXP' || invType == 'B2B'){
			$(".taxrate_textDisable").prop('disabled',false);
			$(".taxrate_textDisable").prop('readonly',false);
		}
		var i=1;
		$.each($("#allinvoice tr"),function() { 
			findTaxAmount(i);
	i++;
  })
	}

function getcustomFieldsData(data,returnType,addFlag,invoiceCustomData){
		if(returnType == 'GSTR1'){
			var content ="";
			var indexarry = new Array();
			if(data.sales){
			for(var i=0;i<data.sales.length;i++){
				var index = i + 1;
				var salesData = data.sales[i];
				if(salesData != undefined){
					if(salesData.customFieldType == 'input'){
						var require = salesData.isMandatory ? 'required' : ''; 
						var astrich = salesData.isMandatory ? 'astrich' : '';
						content += '<div class="col-md-6 col-sm-12 form-group pl-4" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+salesData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+salesData.customFieldName+'"/><input class="form-control" name="customField'+index+'" id="customField'+index+'" placeholder="'+salesData.customFieldName+'" '+require+'><span class="control-label"></span><div class="help-block with-errors"></div></div>';
						index++;
					}else if(salesData.customFieldType == 'list'){
						var require = salesData.isMandatory ? 'required' : ''; 
						var astrich = salesData.isMandatory ? 'astrich' : '';
						content += '<div class="col-md-6 col-sm-12 form-group pl-4" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+salesData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+salesData.customFieldName+'"/><select class="form-control" name="customField'+index+'" data-error="Please enter this value" id="customField'+index+'" '+require+'><option value="">-Select-</option>';
						for(var j = 0; j < salesData.typeData.length; j++){
							content += "<option value=\'"+salesData.typeData[j]+"\'>"+salesData.typeData[j]+"</option>";
						}
						content +='</select></div>';
						index++;
					}else if(salesData.customFieldType == 'checkB'){
						content += '<div class="col-md-6 col-sm-12 pl-4" id="inv_CustField1"><h6 id="invcustFieldText1">'+salesData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+salesData.customFieldName+'"/>';
							/*if(invoiceCustomData){
								if(salesData.customFieldName == invoiceCustomData.customField1 || salesData.customFieldName ==  invoiceCustomData.customField2 || salesData.customFieldName == invoiceCustomData.customField3 || salesData.customFieldName ==  invoiceCustomData.customField4){
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+salesData.customFieldName+'" checked>&nbsp;<label>'+salesData.customFieldName+'</label>&nbsp;';
								}else{
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+salesData.customFieldName+'">&nbsp;<label>'+salesData.customFieldName+'</label>&nbsp;';
								}
							}else{
								content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+salesData.customFieldName+'">&nbsp;<label>'+salesData.customFieldName+'</label>&nbsp;';
							}*/	
						for(var j = 0; j < salesData.typeData.length; j++){
							if(invoiceCustomData){
								if(invoiceCustomData.customField1 !="" && invoiceCustomData.customField1 != null && invoiceCustomData.customField1.includes(salesData.typeData[j])){
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'" checked>&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}else if(invoiceCustomData.customField2 !="" && invoiceCustomData.customField2 != null && invoiceCustomData.customField2.includes(salesData.typeData[j])){
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'" checked>&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}else if(invoiceCustomData.customField3 !="" && invoiceCustomData.customField3 != null && invoiceCustomData.customField3.includes(salesData.typeData[j])){
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'" checked>&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}else if(invoiceCustomData.customField4 !="" && invoiceCustomData.customField4 != null && invoiceCustomData.customField4.includes(salesData.typeData[j])){
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'" checked>&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}else {
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'">&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}
							}else{
								content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'">&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
							}
						}
						content += '</div>';
						indexarry.push(index);
						index++;
					}else if(salesData.customFieldType == 'radio'){
						content += '<div class="col-md-6 col-sm-12 pl-4" id="inv_CustField1"><h6 id="invcustFieldText1">'+salesData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+salesData.customFieldName+'"/>';
						for(var j = 0; j < salesData.typeData.length; j++){
							if(invoiceCustomData){
								if(salesData.typeData[j] == invoiceCustomData.customField1 || salesData.typeData[j] ==  invoiceCustomData.customField2 || salesData.typeData[j] == invoiceCustomData.customField3 || salesData.typeData[j] ==  invoiceCustomData.customField4){
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'" checked = "true">&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}else{
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'">&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
								}
							}else{
								content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+salesData.typeData[j]+'">&nbsp;<label>'+salesData.typeData[j]+'</label>&nbsp;';
							}	
						} 
						content += '</div>';
						indexarry.push(index);
						index++;
					}
				}
				$('#customFields_row').html(content);
				
			}
			if(addFlag){
				}else{
					for(var j=1;j<5;j++){
						if(!indexarry.includes(j)){
							if(j==1){
								if(invoiceCustomData.customField1 != "" && invoiceCustomData.customField1 != null){
									$('#customField1').val(invoiceCustomData.customField1);
								}
							}else if(j==2){
								if(invoiceCustomData.customField2 != "" && invoiceCustomData.customField2 != null){
									$('#customField2').val(invoiceCustomData.customField2);
								}
							}else if(j==3){
								if(invoiceCustomData.customField3 != "" && invoiceCustomData.customField3 != null){
									$('#customField3').val(invoiceCustomData.customField3);
								}
							}else if(j==4){
								if(invoiceCustomData.customField4 != "" && invoiceCustomData.customField4 != null){
									$('#customField4').val(invoiceCustomData.customField4);	
								}
							}
						}
					}
					
				}
			}
		}else if(returnType == 'GSTR2' || returnType == 'Purchase Register'){
			var content ="";
			var indexarry = new Array();
			if(data.purchase){
				for(var i=0;i<data.purchase.length;i++){
					var index = i + 1;
					var purchaseData = data.purchase[i];
					if(purchaseData != undefined){
						if(purchaseData.customFieldType == 'input'){
							var require = purchaseData.isMandatory ? 'required' : ''; 
							var astrich = purchaseData.isMandatory ? 'astrich' : '';
							content += '<div class="col-md-6 col-sm-12 form-group pl-4" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+purchaseData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+purchaseData.customFieldName+'"/><input type="text" class="form-control" name="customField'+index+'" id="customField'+index+'" data-error="Please enter this value" placeholder="'+purchaseData.customFieldName+'" '+require+'><span class="control-label"></span><div class="help-block with-errors"></div></div>';
							index++;
						}else if(purchaseData.customFieldType == 'list'){
							var require = purchaseData.isMandatory ? 'required' : ''; 
							var astrich = purchaseData.isMandatory ? 'astrich' : '';
							content += '<div class="col-md-6 col-sm-12 form-group pl-4" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+purchaseData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+purchaseData.customFieldName+'"/><select class="form-control" name="customField'+index+'" data-error="Please enter this value" id="customField'+index+'" '+require+'><option value="">-Select-</option>';
							for(var j = 0; j < purchaseData.typeData.length; j++){
								content += "<option value=\'"+purchaseData.typeData[j]+"\'>"+purchaseData.typeData[j]+"</option>";
							}
							content +='</select></div>';
							index++;
						}else if(purchaseData.customFieldType == 'checkB'){
							content += '<div class="col-md-6 col-sm-12 pl-4" id="inv_CustField1"><h6 id="invcustFieldText1">'+purchaseData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+purchaseData.customFieldName+'"/>';
							/*if(invoiceCustomData){
									if(purchaseData.customFieldName == invoiceCustomData.customField1 || purchaseData.customFieldName ==  invoiceCustomData.customField2 || purchaseData.customFieldName == invoiceCustomData.customField3 || purchaseData.customFieldName ==  invoiceCustomData.customField4){
										content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.customFieldName+'" checked>&nbsp;<label>'+purchaseData.customFieldName+'</label>&nbsp;';
									}else{
										content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.customFieldName+'">&nbsp;<label>'+purchaseData.customFieldName+'</label>&nbsp;';
									}
								}else{
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.customFieldName+'">&nbsp;<label>'+purchaseData.customFieldName+'</label>&nbsp;';
								}*/
							for(var j = 0; j < purchaseData.typeData.length; j++){
								if(invoiceCustomData){
									if(invoiceCustomData.customField1 !="" && invoiceCustomData.customField1 != null && invoiceCustomData.customField1.includes(purchaseData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'" checked>&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}else if(invoiceCustomData.customField2 !="" && invoiceCustomData.customField2 != null && invoiceCustomData.customField2.includes(purchaseData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'" checked>&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}else if(invoiceCustomData.customField3 !="" && invoiceCustomData.customField3 != null && invoiceCustomData.customField3.includes(purchaseData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'" checked>&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}else if(invoiceCustomData.customField4 !="" && invoiceCustomData.customField4 != null && invoiceCustomData.customField4.includes(purchaseData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'" checked>&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}else {
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'">&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}
								}else{
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'">&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
								}
							}
							content += '</div>';
							indexarry.push(index);
							index++;
						}else if(purchaseData.customFieldType == 'radio'){
							content += '<div class="col-md-6 col-sm-12 pl-4" id="inv_CustField1"><h6 id="invcustFieldText1">'+purchaseData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+purchaseData.customFieldName+'"/>';
							for(var j = 0; j < purchaseData.typeData.length; j++){
								if(invoiceCustomData){
									if(purchaseData.typeData[j] == invoiceCustomData.customField1 || purchaseData.typeData[j] ==  invoiceCustomData.customField2 || purchaseData.typeData[j] == invoiceCustomData.customField3 || purchaseData.typeData[j] ==  invoiceCustomData.customField4){
										content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'" checked = "true">&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}else{
										content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'">&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
									}
								}else{
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+purchaseData.typeData[j]+'">&nbsp;<label>'+purchaseData.typeData[j]+'</label>&nbsp;';
								}	
							}
							content += '</div>';
							indexarry.push(index);
							index++;
						}
					}
					$('#customFields_row').html(content);
				}
				if(addFlag){
				}else{
					for(var j=1;j<5;j++){
						if(!indexarry.includes(j)){
							if(j==1){
								if(invoiceCustomData.customField1 != "" && invoiceCustomData.customField1 != null){
									$('#customField1').val(invoiceCustomData.customField1);
								}
							}else if(j==2){
								if(invoiceCustomData.customField2 != "" && invoiceCustomData.customField2 != null){
									$('#customField2').val(invoiceCustomData.customField2);
								}
							}else if(j==3){
								if(invoiceCustomData.customField3 != "" && invoiceCustomData.customField3 != null){
									$('#customField3').val(invoiceCustomData.customField3);
								}
							}else if(j==4){
								if(invoiceCustomData.customField4 != "" && invoiceCustomData.customField4 != null){
									$('#customField4').val(invoiceCustomData.customField4);	
								}
							}
						}
					}
					
				}
			}
		}else if(returnType == 'EWAYBILL'){
			var content ="";
			var indexarry = new Array();
			if(data.ewaybill){
				for(var i=0;i<data.ewaybill.length;i++){
					var index = i + 1;
					var ewaybillData = data.ewaybill[i];
					if(ewaybillData != undefined){
						if(ewaybillData.customFieldType == 'input'){
							var require = ewaybillData.isMandatory ? 'required' : ''; 
							var astrich = ewaybillData.isMandatory ? 'astrich' : '';
							content += '<div class="col-md-6 col-sm-12 form-group pl-4" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+ewaybillData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+ewaybillData.customFieldName+'"/><input class="form-control" name="customField'+index+'" id="customField'+index+'" data-error="Please enter this value" placeholder="'+ewaybillData.customFieldName+'" '+require+'></div>';
						}else if(ewaybillData.customFieldType == 'list'){
							var require = ewaybillData.isMandatory ? 'required' : ''; 
							var astrich = ewaybillData.isMandatory ? 'astrich' : '';
							content += '<div class="col-md-6 col-sm-12 form-group pl-4" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+ewaybillData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+ewaybillData.customFieldName+'"/><select class="form-control" name="customField'+index+'" data-error="Please enter this value" id="customField'+index+'" '+require+'><option value="">-Select-</option>';
							for(var j = 0; j < ewaybillData.typeData.length; j++){
								content += "<option value=\'"+ewaybillData.typeData[j]+"\'>"+ewaybillData.typeData[j]+"</option>";
							}
							content +='</select></div>';
						}else if(ewaybillData.customFieldType == 'checkB'){
							content += '<div class="col-md-6 col-sm-12 pl-4" id="inv_CustField1"><h6 id="invcustFieldText1">'+ewaybillData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+ewaybillData.customFieldName+'"/>';
							/*if(invoiceCustomData){
									if(ewaybillData.customFieldName == invoiceCustomData.customField1 || ewaybillData.customFieldName ==  invoiceCustomData.customField2 || ewaybillData.customFieldName == invoiceCustomData.customField3 || ewaybillData.customFieldName ==  invoiceCustomData.customField4){
										content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.customFieldName+'" checked>&nbsp;<label>'+ewaybillData.customFieldName+'</label>&nbsp;';
									}else{
										content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.customFieldName+'">&nbsp;<label>'+ewaybillData.customFieldName+'</label>&nbsp;';
									}
								}else{
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.customFieldName+'">&nbsp;<label>'+ewaybillData.customFieldName+'</label>&nbsp;';
								}*/
							for(var j = 0; j < ewaybillData.typeData.length; j++){
								if(invoiceCustomData){
									if(invoiceCustomData.customField1 !="" && invoiceCustomData.customField1 != null && invoiceCustomData.customField1.includes(ewaybillData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'" checked>&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}else if(invoiceCustomData.customField2 !="" && invoiceCustomData.customField2 != null && invoiceCustomData.customField2.includes(ewaybillData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'" checked>&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}else if(invoiceCustomData.customField3 !="" && invoiceCustomData.customField3 != null && invoiceCustomData.customField3.includes(ewaybillData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'" checked>&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}else if(invoiceCustomData.customField4 !="" && invoiceCustomData.customField4 != null && invoiceCustomData.customField4.includes(ewaybillData.typeData[j])){
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'" checked>&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}else {
										content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'">&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}
								}else{
									content += '<input type="checkbox" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'">&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
								}
							}
							content += '</div>';
							indexarry.push(index);
							index++;
						}else if(ewaybillData.customFieldType == 'radio'){
							content += '<div class="col-md-6 col-sm-12 pl-4" id="inv_CustField1"><h6 id="invcustFieldText1">'+ewaybillData.customFieldName+'</h6><input type="hidden" name="customFieldText'+index+'" value="'+ewaybillData.customFieldName+'"/>';
							for(var j = 0; j < ewaybillData.typeData.length; j++){
								if(invoiceCustomData){
									if(ewaybillData.typeData[j] == invoiceCustomData.customField1 || ewaybillData.typeData[j] ==  invoiceCustomData.customField2 || ewaybillData.typeData[j] == invoiceCustomData.customField3 || ewaybillData.typeData[j] ==  invoiceCustomData.customField4){
										content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'" checked = "true">&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}else{
										content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'">&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
									}
								}else{
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+ewaybillData.typeData[j]+'">&nbsp;<label>'+ewaybillData.typeData[j]+'</label>&nbsp;';
								}	
							} 
							content += '</div>';
							indexarry.push(index);
							index++;
						}
					}
					$('#customFields_row').html(content);
				}
				if(addFlag){
				}else{
					for(var j=1;j<5;j++){
						if(!indexarry.includes(j)){
							if(j==1){
								if(invoiceCustomData.customField1 != "" && invoiceCustomData.customField1 != null){
									$('#customField1').val(invoiceCustomData.customField1);
								}
							}else if(j==2){
								if(invoiceCustomData.customField2 != "" && invoiceCustomData.customField2 != null){
									$('#customField2').val(invoiceCustomData.customField2);
								}
							}else if(j==3){
								if(invoiceCustomData.customField3 != "" && invoiceCustomData.customField3 != null){
									$('#customField3').val(invoiceCustomData.customField3);
								}
							}else if(j==4){
								if(invoiceCustomData.customField4 != "" && invoiceCustomData.customField4 != null){
									$('#customField4').val(invoiceCustomData.customField4);	
								}
							}
						}
					}
					
				}
			}
		}/*else if(returntype == 'EINVOICE'){
			var content ="";
			var indexarry = new Array();
			if(data.einvoice){
				for(var i=0;i<data.einvoice.length;i++){
					var einvoiceData = data.einvoice[i];
					if(einvoiceData != undefined){
						if(einvoiceData.customFieldType == 'input'){
							var require = einvoiceData.isMandatory ? 'required' : ''; 
							var astrich = einvoiceData.isMandatory ? 'astrich' : '';
							content += '<div class="col-md-2 col-sm-12 form-group" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+einvoiceData.customFieldName+'</h6><input class="form-control" name="customField'+index+'" id="customField'+index+'" data-error="Please enter this value" placeholder="'+einvoiceData.customFieldName+'" '+require+'></div>';
						}else if(einvoiceData.customFieldType == 'list'){
							var require = einvoiceData.isMandatory ? 'required' : ''; 
							var astrich = einvoiceData.isMandatory ? 'astrich' : '';
							content += '<div class="col-md-2 col-sm-12 form-group" id="inv_CustField1"><h6 id="invcustFieldText1" class="'+astrich+'">'+einvoiceData.customFieldName+'</h6><select class="form-control" name="customField'+index+'" data-error="Please enter this value" id="customField'+index+'" '+require+'><option value="">-Select-</option>';
							for(var j = 0; j < einvoiceData.typeData.length; j++){
								content += "<option value=\'"+einvoiceData.typeData[j]+"\'>"+einvoiceData.typeData[j]+"</option>";
							}
							content +='</select></div>';
						}else if(einvoiceData.customFieldType == 'checkB'){
							content += '<div class="col-md-2 col-sm-12" id="inv_CustField1"><h6 id="invcustFieldText1">'+einvoiceData.customFieldName+'</h6>';
							if(invoiceCustomData){
									if(einvoiceData.customFieldName == invoiceCustomData.customField1 || einvoiceData.customFieldName ==  invoiceCustomData.customField2 || einvoiceData.customFieldName == invoiceCustomData.customField3 || einvoiceData.customFieldName ==  invoiceCustomData.customField4){
										content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.customFieldName+'" checked>&nbsp;<label>'+einvoiceData.customFieldName+'</label>&nbsp;';
									}else{
										content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.customFieldName+'">&nbsp;<label>'+einvoiceData.customFieldName+'</label>&nbsp;';
									}
								}else{
									content += '<input type="checkbox" class="" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.customFieldName+'">&nbsp;<label>'+einvoiceData.customFieldName+'</label>&nbsp;';
								}
							content += '</div>';
							indexarry.push(index);
							index++;
						}else if(einvoiceData.customFieldType == 'radio'){
							content += '<div class="col-md-2 col-sm-12" id="inv_CustField1"><h6 id="invcustFieldText1">'+einvoiceData.customFieldName+'</h6>';
							for(var j = 0; j < einvoiceData.typeData.length; j++){
								if(invoiceCustomData){
									if(einvoiceData.typeData[j] == invoiceCustomData.customField1 || einvoiceData.typeData[j] ==  invoiceCustomData.customField2 || einvoiceData.typeData[j] == invoiceCustomData.customField3 || einvoiceData.typeData[j] ==  invoiceCustomData.customField4){
										content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.typeData[j]+'" checked = "true">&nbsp;<label>'+einvoiceData.typeData[j]+'</label>&nbsp;';
									}else{
										content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.typeData[j]+'">&nbsp;<label>'+einvoiceData.typeData[j]+'</label>&nbsp;';
									}
								}else{
									content += '<input type="radio" name="customField'+index+'" id="customField'+index+'" value="'+einvoiceData.typeData[j]+'">&nbsp;<label>'+einvoiceData.typeData[j]+'</label>&nbsp;';
								}	
							} 
							content += '</div>';
							indexarry.push(index);
							index++;
						}
					}
					$('#customFields_row').html(content);
				}
				if(addFlag){
				}else{
					for(var j=1;j<5;j++){
						if(!indexarry.includes(j)){
							if(j==1){
								if(invoiceCustomData.customField1 != "" && invoiceCustomData.customField1 != null){
									$('#customField1').val(invoiceCustomData.customField1);
								}
							}else if(j==2){
								if(invoiceCustomData.customField2 != "" && invoiceCustomData.customField2 != null){
									$('#customField2').val(invoiceCustomData.customField2);
								}
							}else if(j==3){
								if(invoiceCustomData.customField3 != "" && invoiceCustomData.customField3 != null){
									$('#customField3').val(invoiceCustomData.customField3);
								}
							}else if(j==4){
								if(invoiceCustomData.customField4 != "" && invoiceCustomData.customField4 != null){
									$('#customField4').val(invoiceCustomData.customField4);	
								}
							}
						}
					}
					
				}
			}
		}*/
	}
$('.ewybuyerAddrText').on('click', function() {
	$('#ewybuyeraddrModal').modal("show");
	var invCategory = $('#invCategory').val();
	$('#ewybuyerGstno_msg,#ewybuyerTradeName_msg,#ewybuyerAddress1_msg,#ewybuyerloc_msg,#ewybuyerPincode_msg,#ewybuyerStatecode_msg').text("");
	var invtype = $('#idEInvType').val();
	if(invtype == "B2B" || invtype == "Credit/Debit Notes"){
		$('#ewybuyerdetgstin').css("display","block");
		$('#ewybuyerGstno').attr("required",true);
		if(invCategory == "EXPWP" || invCategory == "EXPWOP"){
			$('#ewybuyerGstno').removeAttr("required");
			$('#ewybuyerdetgstinlabel').removeClass("astrich");
		}else{
			$('#ewybuyerdetgstinlabel').addClass("astrich");
		}
	}else{
		$('#ewybuyerGstno').removeAttr("required");
		$('#ewybuyerdetgstinlabel').addClass("astrich");
	}
	$('#ewybuyerTradeName,#ewybuyerAddress1,#ewybuyerloc,#ewybuyerStatecode').attr("required",true);
	   var bstateoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/stateconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onClickEvent: function() {
						var rettype = $('#retType').val();
					},
					onLoadEvent: function() {
						if($("#eac-container-ewybuyerStatecode ul").children().length == 0) {
							$("#ewybuyerStatecodeempty").show();
						}else {
							$("#ewybuyerStatecodeempty").hide();
						}
					},
					maxNumberOfElements: 37
				}
			};
	$("#ewybuyerStatecode").easyAutocomplete(bstateoptions);
	$('#ewybuyerGstno').val($('#ewybuyerDtls_gstin').val());
	$('#ewybuyerTradeName').val($('#ewybuyerDtls_lglNm').val());
	$('#ewybuyerAddress1').val($('#ewybuyerDtls_addr1').val());
	$('#ewybuyerAddress2').val($('#ewybuyerDtls_addr2').val());
	if(invtype != "Exports"){
		$('#ewybuyerloc').val($('#ewybuyerDtls_loc').val());
		if($('#ewybuyerDtls_pin').val() != ""){
			$('#ewybuyerPincode').val(parseInt($('#ewybuyerDtls_pin').val()));	
		}else{
			$('#ewybuyerPincode').val(parseInt("0"));	
		}
		$('#ewybuyerStatecode').val($('#ewybuyerDtls_state').val());
	}else{
		$('#ewybuyerloc').val("Other Teritory");
		$('#ewybuyerPincode').val(parseInt("999999"));	
		$('#ewybuyerStatecode').val("96");
	}
});
function ewayvalidateBuyerDetails(){
	var c = 0;
var buyerGstno = $('#ewaybuyerGstno').val();
var buyerTradeName = $('#ewaybuyerTradeName').val();
var buyerAddress1 = $('#ewaybuyerAddress1').val();
var buyerloc = $('#ewaybuyerloc').val();
var buyerPincode = $('#ewaybuyerPincode').val();
var buyerStatecode = $('#ewaybuyerStatecode').val();


var invtype = $('#idEInvType').val();
var invCategory = $('#invCategory').val();
if(buyerGstno==""){
	$('#ewaybuyerGstno_msg').text("Please enter GSTIN Number"); 
	c++;
}else{  
	$('#ewaybuyerGstno_msg').text(""); 
}

if(buyerPincode==""){
	$('#ewaybuyerPincode_msg').text("Please enter Pincode");
	c++;
}else{
	$('#ewaybuyerPincode_msg').text("");
}
if(buyerStatecode==""){
	$('#ewaybuyerStatecode_msg').text("Please enter state");
	c++;
}else{
	$('#ewaybuyerStatecode_msg').text("");
}	
return c==0; 
}
function ewybuyerAddrDetails(){
	if(ewayvalidateBuyerDetails()){
		$('#ewybuyerDtls_gstin').val($('#ewybuyerGstno').val());
		$('#ewybuyerDtls_lglNm').val($('#ewybuyerTradeName').val());
		$('#ewybuyerDtls_addr1').val($('#ewybuyerAddress1').val());
		$('#ewybuyerDtls_addr2').val($('#ewybuyerAddress2').val());
		$('#ewybuyerDtls_loc').val($('#ewybuyerloc').val());
		$('#ewybuyerDtls_pin').val(parseInt($('#ewybuyerPincode').val()));
		$('#ewybuyerDtls_state').val($('#ewybuyerStatecode').val());
		var pos = $('#ewybuyerStatecode').val().split("-");
		$('#ewybuyerDtls_pos').val(pos[0]);
		if($('#ewysamedispatchaddress').is(":checked")){
			$('#ewyshimentAddrEdit').addClass('d-none');
			   $('#ewyshipmentTradeName').val($('#ewybuyerTradeName').val());
				$('#ewyshipmentAddress1').val($('#ewybuyerAddress1').val());
				$('#ewyshipmentAddress2').val($('#ewybuyerAddress1').val());
				$('#ewyshipmentloc').val($('#ewybuyerloc').val());
				$('#ewyshipmentPincode').val(parseInt($('#ewybuyerPincode').val()));	
				$('#ewyshipmentStatecode').val($('#ewybuyerStatecode').val());
				$('#ewyshipmentDtls_trdNm').val($('#ewyshipmentTradeName').val());
				$('#ewyshipmentDtls_addr1').val($('#ewyshipmentAddress1').val());
				$('#ewyshipmentDtls_addr2').val($('#ewyshipmentAddress2').val());
				$('#ewyshipmentDtls_loc').val($('#ewyshipmentloc').val());
				$('#ewyshipmentDtls_pin').val($('#ewyshipmentPincode').val());
				$('#ewyshipmentDtls_stcd').val($('#ewyshipmentStatecode').val());
		}
		$('#ewybuyerform').find('input').each(function(){
			if(!$(this).prop('required')){
			}else{
				var bca = $(this).val();
				   if( bca == ''){
					  $(this).parent().addClass('has-error has-danger');
				   }else{
					  $(this).parent().removeClass('has-error has-danger');
					 
				   }
			}
		});
		$('#billedtoname').val($('#ewybuyerTradeName').val());
		$('#ewygstin_lab').val($('#ewybuyerGstno').val());
		$('#toPincode').val($('#ewybuyerPincode').val());
			$('#billedtogstin').val($('#ewybuyerGstno').val()).trigger("change");
			var addr = "";
			if($('#ewybuyerAddress1').val() != ""){
				addr = $('#ewybuyerAddress1').val().substring(1,15)+"...";
			}
			if($('#ewysamedispatchaddress').is(":checked")){
				$('#ewaybuyerAddr,#ewayshipmentAddr').html($('#ewybuyerTradeName').val()+","+addr+","+$('#ewybuyerloc').val()+","+$('#ewybuyerStatecode').val()+","+$('#ewybuyerPincode').val());
			}else{
				$('#ewaybuyerAddr').html($('#ewybuyerTradeName').val()+","+addr+","+$('#ewybuyerloc').val()+","+$('#ewybuyerStatecode').val()+","+$('#ewybuyerPincode').val());
			}
		$('.ewybuyerAddrText').html('Edit');
		
		$('#ewybuyer_save').attr("data-dismiss","modal");
	}
}
$('#ewysamedispatchaddress').on('change', function() {
	
	if($('#ewysamedispatchaddress').is(":checked")){
		$('#ewysamedispatchaddress').val("1");
		var txt = $('#ewaybuyerAddr').html();
		$('#ewayshipmentAddr').html(txt);
		$('#ewyshimentAddrEdit').addClass('d-none');
		$('#ewyshipmentDtls_trdNm').val($('#ewybuyerDtls_lglNm').val());
		$('#ewyshipmentDtls_addr1').val($('#ewybuyerDtls_addr1').val());
		$('#ewyshipmentDtls_addr2').val($('#ewybuyerDtls_addr2').val());
		$('#ewyshipmentDtls_loc').val($('#ewybuyerDtls_loc').val());
		$('#ewyshipmentDtls_pin').val($('#ewybuyerDtls_pin').val());	
		$('#ewyshipmentDtls_stcd').val($('#ewybuyerDtls_state').val());
		}else{
			$('#ewayshipmentAddr').html('');
			$('#ewysamedispatchaddress').val("0");
			$('#ewyshipmentTradeName').val('');
			$('#ewyshipmentAddress1').val('');
			$('#ewyshipmentAddress2').val('');
			$('#ewyshipmentloc').val('');
			$('#ewyshipmentPincode').val('');	
			$('#ewyshipmentStatecode').val('');
			$('#ewyshipmentDtls_trdNm').val('');
			$('#ewyshipmentDtls_addr1').val('');
			$('#ewyshipmentDtls_addr2').val('');
			$('#ewyshipmentDtls_loc').val('');
			$('#ewyshipmentDtls_pin').val('');
			$('#ewyshipmentDtls_stcd').val('');
			$('#ewyewayshipmentAddr').html('');
			$('#ewyshippingAddress').val('');
			$('#ewyshimentAddrEdit').removeClass('d-none');
		}  
});
function ewayshowDispatchModal(){
	$('#ewydispatchaddrModal').modal("show");
	$('#ewydispatchPincode,#ewydispatchStatecode').attr("required",true);
	   var bstateoptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return contextPath+"/stateconfig?query="+ phrase + "&format=json";
				},
				getValue: "name",
				list: {
					onClickEvent: function() {
						var rettype = $('#retType').val();
					},
					onLoadEvent: function() {
						if($("#eac-container-ewydispatchStatecode ul").children().length == 0) {
							$("#ewydispatchStatecodeempty").show();
						}else {
							$("#ewydispatchStatecodeempty").hide();
						}
					},
					maxNumberOfElements: 37
				}
			};
		$("#ewydispatchStatecode").easyAutocomplete(bstateoptions);
		$('#ewydispatchTradeName').val($('#ewydispatcherDtls_nm').val());
		$('#ewydispatchAddress1').val($('#ewydispatcherDtls_addr1').val());
		$('#ewydispatchAddress2').val($('#ewydispatcherDtls_addr2').val());
		$('#ewydispatchloc').val($('#ewydispatcherDtls_loc').val());
		if($('#ewydispatcherDtls_pin').val() != ""){
			$('#ewydispatchPincode').val(parseInt($('#ewydispatcherDtls_pin').val()));	
		}else{
			$('#ewydispatchPincode').val(parseInt(0));	
		}
		$('#ewydispatchStatecode').val($('#ewydispatcherDtls_stcd').val());
}
function ewyshowShipmentModal(){
$('#ewyshipaddrModal').modal("show");
$('#ewyshipmentPincode,#ewyshipmentStatecode').attr("required",true);
 var bstateoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onClickEvent: function() {
					var rettype = $('#retType').val();
				},
				onLoadEvent: function() {
					if($("#eac-container-ewyshipmentStatecode ul").children().length == 0) {
						$("#ewyshipmentStatecodeempty").show();
					}else {
						$("#ewyshipmentStatecodeempty").hide();
					}
				},
				maxNumberOfElements: 37
			}
		};
	$("#ewyshipmentStatecode").easyAutocomplete(bstateoptions);
    $('#ewyshipmentTradeName').val($('#ewyshipmentDtls_trdNm').val());
	$('#ewyshipmentAddress1').val($('#ewyshipmentDtls_addr1').val());
	$('#ewyshipmentAddress2').val($('#ewyshipmentDtls_addr2').val());
	$('#ewyshipmentloc').val($('#ewyshipmentDtls_loc').val());
	if($('#ewyshipmentDtls_pin').val() != ""){
		$('#ewyshipmentPincode').val(parseInt($('#ewyshipmentDtls_pin').val()));	
	}else{
		$('#ewyshipmentPincode').val(parseInt(0));	
	}
	$('#ewyshipmentStatecode').val($('#ewyshipmentDtls_stcd').val());
}
function ewydispatchAddrDetails(){
	$('#ewydispatcherDtls_nm').val($('#ewydispatchTradeName').val());
	$('#ewydispatcherDtls_addr1').val($('#ewydispatchAddress1').val());
	$('#ewydispatcherDtls_addr2').val($('#ewydispatchAddress2').val());
	$('#ewydispatcherDtls_loc').val($('#ewydispatchloc').val());
	$('#ewydispatcherDtls_pin,#fromPincode').val(parseInt($('#ewydispatchPincode').val()));
	$('#ewydispatcherDtls_stcd').val($('#ewydispatchStatecode').val());
	$('#ewdispatchform').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  $(this).parent().addClass('has-error has-danger');
		 		 $('#ewydispatch_save').removeAttr("data-dismiss");
		 	   }else{
		 		  $(this).parent().removeClass('has-error has-danger');
		 		 $('#ewydispatch_save').attr("data-dismiss","modal");
		 	   }
	    }
	});
	var addr = $('#ewydispatchAddress1').val().substring(1,15)+"...";
	$('#ewaydispatchAddr').html($('#ewydispatchTradeName').val()+","+addr+","+$('#ewydispatchloc').val()+","+$('#ewydispatchStatecode').val()+","+$('#ewydispatchPincode').val());
}
function ewyshipmentAddrDetails(){
		$('#ewyshipmentDtls_trdNm').val($('#ewyshipmentTradeName').val());
		$('#ewyshipmentDtls_addr1').val($('#ewyshipmentAddress1').val());
		$('#ewyshipmentDtls_addr2').val($('#ewyshipmentAddress2').val());
		$('#ewyshipmentDtls_loc').val($('#ewyshipmentloc').val());
		$('#ewyshipmentDtls_pin,#toPincode').val($('#ewyshipmentPincode').val());
		$('#ewyshipmentDtls_stcd').val($('#ewyshipmentStatecode').val());
		$('#ewshipmentform').find('input').each(function(){
			if(!$(this).prop('required')){
			}else{
				var bca = $(this).val();
				   if( bca == ''){
					  $(this).parent().addClass('has-error has-danger');
					$('#ewyshipment_save').removeAttr("data-dismiss");
				   }else{
					  $(this).parent().removeClass('has-error has-danger');
					 $('#ewyshipment_save').attr("data-dismiss","modal");
				   }
			}
		});
		var addr = "";
		if($('#ewyshipmentAddress1').val() != ""){
			addr = $('#ewyshipmentAddress1').val().substring(1,15)+"...";
		}
		$('#ewayshipmentAddr').html($('#ewyshipmentTradeName').val()+","+addr+","+$('#ewyshipmentloc').val()+","+$('#ewyshipmentStatecode').val()+","+$('#ewyshipmentPincode').val());
}

function ewayupdatePan(value,type) {
	var rtype = $('#retType').val();var gstno = $('#ewybuyerGstno').val();
	if(value.length == 15) {
		$.ajax({
			url: contextPath+"/srchstatecd?code="+value.substring(0,2),
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response) {
					if(response.name != ''){
						$('.placeofsupply').removeClass("has-error has-danger");$('.placeofsupply .with-errors').html('');$("#billedtostatecodeempty").hide(); 
					}
					$('#ewybuyerStatecode').val(response.name);
					if(type == 'buyer' || type == 'invoice'){
						$('#ewybuyerDtls_state').val(response.name);
					}
					var pos = response.name.split("-");
					$('#ewybuyerDtls_pos').val(pos[0]);
				}
			}
		});
	}
}

function ewayinvokegstnPublicAPI(btn,type) {
	
	var gstnno = $('#ewybuyerGstno').val();
	$('#ewybuyerGstno_msg').text('');
	var userid = $('#userid').val();
	ewayupdatePan(gstnno,type);
		if(gstnno != '' && gstnno.toUpperCase() != 'URP') {
			$(btn).addClass('btn-loader');
			var gstnumber = gstnno.toUpperCase();
			$.ajax({
				url: contextPath+"/publicsearch?gstin="+gstnumber+"&userid="+userid,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response.error && response.error.message) {
						if(response.error.message == 'SWEB_9035'){$('#ewybuyerGstno_msg').text("No Records Found");	
					  	} else{
					  		$('#ewybuyerGstno_msg').text(response.error.message);
					  	}
					} else if(response.status_cd == '0') {
						$('#ewybuyerGstno_msg').text(response.status_desc);
					}
					if(response.status_cd == '1') {
						if(response.data) {
							var address = "";
							$('#ewybuyerDtls_gstin').val(gstnno);
							Object.keys(response.data).forEach(function(key) {
								if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
									$('#ewybuyerDtls_lglNm').val(response.data['lgnm']);
									$('#ewybuyerTradeName').val(response.data['lgnm']);
								}else{
									$('#ewybuyerTradeName').val(response.data['tradeNam']);
									$('#ewybuyerDtls_lglNm').val(response.data['tradeNam']);
								}
							if(key == 'pradr'){
							Object.keys(response.data['pradr']['addr']).forEach(function(key){
								if(response.data['pradr']['addr'][key] != ''){
									
									if(key != 'pncd' && key != 'stcd'){
										address = address.concat(response.data['pradr']['addr'][key]+",");
										$('#ewybuyerDtls_addr1').val(address);
									}
									if(key == 'pncd'){
										$('#ewybuyerPincode').val(response.data['pradr']['addr'][key]);
										if(type == 'buyer' || type == 'invoice'){
											$('#ewybuyerDtls_pin').val(parseInt(response.data['pradr']['addr'][key]));
										}
									}
									/*if(key == 'stcd'){
										//$('#ewybuyerStatecode').val(response.data['pradr']['addr'][key]);
										if(type == 'buyer' || type == 'invoice'){
											$('#ewybuyerDtls_state').val(response.data['pradr']['addr'][key]);
										}
									}*/
									if(key == 'loc'){
										$('#ewybuyerloc').val(response.data['pradr']['addr'][key]);
										if(type == 'buyer' || type == 'invoice'){
											$('#ewybuyerDtls_loc').val(response.data['pradr']['addr'][key]);
										}
									}
								}
							});
						}
						});
						//$('#billingAddress').val(address.slice(0,-1));
							
								if($('#ewysamedispatchaddress').is(":checked")){
									   	$('#ewyshipmentDtls_lglNm').val($('#ewybuyerDtls_lglNm').val());
									   	$('#ewyshipmentDtls_trdNm').val($('#ewybuyerDtls_lglNm').val());
										$('#ewyshipmentDtls_addr1').val($('#ewybuyerDtls_addr1').val());
										$('#ewyshipmentDtls_addr2').val($('#ewybuyerDtls_addr2').val());
										$('#ewyshipmentDtls_loc').val($('#ewybuyerDtls_loc').val());
										$('#ewyshipmentDtls_pin').val($('#ewybuyerDtls_pin').val());	
										$('#ewyshipmentDtls_stcd').val($('#ewybuyerDtls_state').val());
									var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
									$('#einvbuyerAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
									$('#einvshipmentAddr').html($('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
								}else{
									$('#ewyshipmentDtls_gstin').val('');
								   	$('#ewyshipmentDtls_lglNm').val('');
								   	$('#ewyshipmentDtls_trdNm').val('');
									$('#ewyshipmentDtls_addr1').val('');
									$('#ewyshipmentDtls_addr2').val('');
									$('#ewyshipmentDtls_loc').val('');
									$('#ewyshipmentDtls_pin').val('');	
									$('#ewyshipmentDtls_stcd').val('');
									var addr = $('#buyerDtls_addr1').val().substring(1,15)+"...";
									$('#einvbuyerAddr').html($('#buyerDtls_gstin').val()+","+$('#buyerDtls_lglNm').val()+","+addr+","+$('#buyerDtls_loc').val()+","+$('#buyerDtls_state').val()+","+$('#buyerDtls_pin').val());
								}
							
							
						}
					}
					$(btn).removeClass('btn-loader');
				},
				error : function(e, status, error) {
					$(btn).removeClass('btn-loader');
				}
			});
		}
	}
function findHsnOrSac(rowno){
	
	var invType =$('#idInvType').val();
	var retType = $('#retType').val();
	var hsnVal = $('#hsn_text'+rowno).val();
	if(hsnVal != ""){
		if((retType == "GSTR1" || retType == "SalesRegister")){
			$.ajax({
				url: contextPath+"/checkHsnOrSac/"+invType+"/"+retType+"?hsnVal="+hsnVal,
				async: false,
				cache: false,
				contentType: 'application/json',
				success : function(response){
					if(response.length > 0){
						$('#uqc_text'+rowno).val('NA').addClass("disabled").attr("readonly",true);
						$('#qty_text'+rowno).val('1').addClass("disabled").attr("readonly",true);
						 if(invType == "Advances"){
							$('#taxrate_text'+rowno).removeClass("disabled").removeAttr("readonly",true);
							$('#cessrate_text'+rowno).removeClass("disabled");
							$('#cessrate_text'+rowno).removeAttr("readonly",true);
						 }
					}else{
						$('#uqc_text'+rowno).val('').removeClass("disabled").removeAttr("readonly",true);
						$('#qty_text'+rowno).val('1').removeClass("disabled").removeAttr("readonly",true);
						if(invType == "Advances"){
							$('#taxrate_text'+rowno).val('0');
							$('#abb'+rowno).val('0');
							$('#igsttax_text'+rowno).val('0');
							$('#sgsttax_text'+rowno).val('0');
							$('#cgsttax_text'+rowno).val('0');
							$('#taxrate_text'+rowno).addClass("disabled");
							$('#taxrate_text'+rowno).attr("readonly",true);
							$('#cessrate_text'+rowno).val("0");
							$('#cessrate_text'+rowno).addClass("disabled");
							$('#cessrate_text'+rowno).attr("readonly",true);
						}
					}
				}
			});
		}
	}
}
$( ".tcsorTdsType" ).change(function() {
	//$('#tcsval').prop("checked",false);
	//$('#tdstcssection ,#tcs_percent').attr("readonly","true");$('#tdstcssection').attr("disabled",true);
	$('#tcs_percent').val('');
	$('.apply_tcsOtds').remove();
	$('#tdstcssection').children('option').remove();
	$('#tdstcssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
	var tcsTds = $(this).val();
	if(tcsTds == "tcs"){
		$('.apply_type').text("Add TCS");
		$('.perc_type').html("TCS Percentage");
		//$('.tot_net_type').html("TCS Amount");
		$('#tdstcssection').append($("<option></option>").attr("value","206C(1)").text("206C"));
	}else if(tcsTds == "tds"){
		$('.apply_type').text("Add TDS");
		$('.perc_type').html("TDS Percentage");
		purchaseTDSOptions();
	}
	tcscheckval();
});
   $('.adddropdown').click(function () {
		$(this).attr('tabindex', 1).focus();
		$(this).toggleClass('active');
		$(this).find('.dropdown-menu').slideToggle(300);
	});
	$('.adddropdown').focusout(function () {
		$(this).removeClass('active');
		$(this).find('.dropdown-menu').slideUp(300);
	});
	function showTermsList(type){
		if(paymentTerms.length > 0){
			if(type == "invpopup"){
				$('.termMenu').html('');
				$('.termMenu').append('<li id="select" class="term_li" onclick = "termSel(this)">- Select -</li>');
			}
			for(var i =0;i<paymentTerms.length;i++){
				var days;
				if(type == "invpopup"){
					days = paymentTerms[i].termName == "Immediate" ? "Immediate" : paymentTerms[i].noOfDays+ " Days";
					$('.termMenu').append('<li id="term'+days+'" class="term_li" onclick = "termSel(this)">'+days+'</li>');
				}else{
					var no = i +1;
					$('#term_name'+no).val(paymentTerms[i].termName);
					$('#term_days'+no).val(paymentTerms[i].noOfDays);
				}
			}
			if(type == "invpopup"){
				$('.termMenu').append('<li id="addbtn"><a onclick="configTerms()" data-toggle="modal" data-target="#addTermModal"><i class="fa fa-plus-circle"></i>Configure Term</a></li>');
			}
			//$('.termMenu').append('<li id="addbtn" type="button" class="btn btn-primary">add</li>');
		}
	}
	function termSel(li){
			if($(li).text() != "Configure Term"){
				$(li).parents('.adddropdown').find('span').text($(li).text());
				$(li).parents('.adddropdown').find('input').attr('value', $(li).text());
				var input = '<strong>' + $(li).parents('.adddropdown').find('input').val() + '</strong>';
				if($(li).text() != "- Select -"){
					var val = $(li).text();
					var invdt = $('#dateofinvoice').val();
					var mnyr = invdt.split("/");var dt = parseInt(mnyr[0]);var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
					var d = new Date(yr, mn-parseInt(1), dt);
					if(val == 'Immediate'){
						d.setDate(d.getDate());
					}else{
						d.setDate(d.getDate() + parseInt(val));
					}
					var day = d.getDate();
					var month = d.getMonth()+parseInt(1);
					var year=d.getFullYear();
					var mndate = ('0' + day).slice(-2) + '/' + ('0' + (month)).slice(-2) + '/' + year;
					$('#duedate_div').datetimepicker({
						value: mndate,
						timepicker: false,
						format: 'd/m/Y',
						maxDate: false
					});
				}else{
					$('#duedate_div').val("");
					$(li).parents('.adddropdown').find('input').attr('value', '');
				}
			}
	}
	function configTerms(){
		termsRowCount = 0;
		$('#addTermModal').modal('show');
		var rowCount = paymentTerms.length == 0 ? 6 : paymentTerms.length;
		$('#ConfigTemsTable_body').html('');
		for(var i =1;i<=rowCount;i++){
			addTermsrow();
		}
		showTermsList("Termpopup");
		if(paymentTerms.length == 0){
			$('#term_name1').val("Immediate");$('#term_days1').val("0");
			$('#term_name2').val("Net 7");$('#term_days2').val("7");
			$('#term_name3').val("Net 15");$('#term_days3').val("15");
			$('#term_name4').val("Net 30");$('#term_days4').val("30");
			$('#term_name5').val("Net 45");$('#term_days5').val("45");
			$('#term_name6').val("Net 60");$('#term_days6').val("60");
		}
	}
	function addTermsrow(){
		termsRowCount++;
		var table = null;
		var rowPrefix = null;
		var row = null;
		var table_len=termsRowCount;
		table = document.getElementById("configTermsTable");
		//rowPrefix = "<tr id='"+table_len+"' draggable='true' class='rowshadow' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='no_row1'>"+table_len+"</span></td><td align='center'><input type='text' class='form-control' id='term_name"+table_len+"' name='termName'  placeholder='Terms Name'/></td><td align='center'><input type='text' class='form-control' id='term_days"+table_len+"' name='noOfDays'  placeholder='Terms Days'/></td><td align='center' width='2%'><a href='javascript:void(0)' class='term_delete' onclick='deleteTerm("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td></tr>";
		$('#ConfigTemsTable_body').append("<tr id='"+table_len+"' draggable='true' class='rowshadow' style='cursor: move;'><td align='center'><span class='glyphicon glyphicon-th'></span> <span id='no_row1'>"+table_len+"</span></td><td align='center'><input type='text' class='form-control' id='term_name"+table_len+"' name='termName'  placeholder='Terms Name'/></td><td align='center'><input type='text' class='form-control' id='term_days"+table_len+"' name='noOfDays'  placeholder='Terms Days'/></td><td align='center' width='2%'><a href='javascript:void(0)' class='term_delete' onclick='deleteTerm("+table_len+")'> <span class='fa fa-trash-o gstr2adeletefield'></span></a></td></tr>");
		$('form[name="salesinvoceform"]').validator('update');
	}
	function deleteTerm(no){
		var tab=document.getElementById("configTermsTable");
		/*if(no >= termsRowCount){
			no=no-1;
		}*/
		tab.deleteRow(no);
		termsRowCount--;
		$("#ConfigTemsTable_body tr").each(function(index) {
			 $(this).attr('id',index+1);
			 $(this).find("#no_row1").html(index+1);
			 var rowno = (index+1).toString();
			 var rownoo = (index).toString();
			 $(this).find('input').each (function() {
					var inputname1 = $(this).attr('class');
					var inputid1 = $(this).attr('id');
					var inputname = $(this).attr('name');
					if(inputid1 != undefined){
						if(rowno<10){
							inputid1 = inputid1.slice(0, -1);
			   	    		}else{
			   	    			inputid1 = inputid1.slice(0, -2);
			   	    		}
						inputid1 = inputid1+rowno;
						$(this).attr('id',inputid1);
					}
					
				});
			 $(this).find('a').each (function() {
					var aname = $(this).attr('id');
					if(aname != undefined){
						if(rowno<10){
							aname = aname.slice(0, -1);
			   	    		}else{
			   	    			aname = aname.slice(0, -2);
			   	    		}
					aname = aname+rowno;
					$(this).attr('id',aname);
					}
					var det = $(this).attr('class');
					if(det != 'deleteTerm'){
					}else{
						var abcd = $(this).attr('onclick');
				   	    abcd = replaceAt(abcd,18,rowno);
				   	    $(this).attr('onclick',abcd);
					}
				});
		});
	}
	function savePaymentTerms(){
		var termsArray = new Array();
		var trowCount = $('#ConfigTemsTable_body tr').length;
		var name,days;
		if(termsRowCount <= trowCount) {
			for(var i=1;i<=trowCount;i++) {
				name = $('#term_name'+i).val();
				days = $('#term_days'+i).val();
				if(days == "Immediate"){
					days=0;
				}
				var termsData=new Object();
				termsData.termName=name;
				termsData.noOfDays=days;
				termsArray.push(termsData);
			}
		}
	
		$.ajax({
			url : contextPath+'/configureTerms/'+clientId,
			type: "POST",
			data: JSON.stringify(termsArray),
			contentType: 'application/json',
			success : function(response) {
				paymentTerms = response;
				$('.termMenu li').remove();
				$('.termMenu').append('<li id="select" class="term_li" onclick = "termSel(this)">- Select -</li>');
				for(var i=0;i<response.length;i++){
					var days = response[i].termName == "Immediate" ? "Immediate" : response[i].noOfDays+ " Days";
					$('.termMenu').append('<li id="term'+days+'" class="term_li" onclick = "termSel(this)">'+days+'</li>');
				}
				closeTermModal('addTermModal');
				$('.termMenu').append('<li id="addbtn"><a onclick="configTerms()" data-toggle="modal" data-target="#addTermModal"><i class="fa fa-plus-circle"></i>Configure Term</a></li>');
			}
		});
	}
	function closeTermModal(id){
		$('#'+id).modal('hide');
		termsRowCount =1;
	}
	function closeMailModal(){
		$('#successEmailmsg').html("");
		$('#sendEmailErrorMsg').html("");
		$('#customer_emailid').parent().removeClass('has-error has-danger');
		$('#send_EmailModal').modal('hide');
		$('#email_invoiceId,#email_returnType').val("");
		$('.successEmailmsg').addClass("d-none");
		$('.books_email_btn').removeAttr("onclick");
	}
	function sendEmailToCustomers(invoiceid,invoiceno,billedtoname,retType){
		$('#email_returnType,#customer_name').val("");
		$('#send_EmailModal').modal('show');
		$('#email_invoiceId').val(invoiceid);
		$('#email_returnType').val(retType);
		$('.signLink').attr('href',''+contextPath+'/cp_upload'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=');
		if(clientSignatureDetails.clientSignature == ""){
			$('#signtext').html("Edit Email Signature");
			$('#email_custdetails').html(clientSignatureDetails.businessname+"<br>"+clientSignatureDetails.email+"<br>"+clientSignatureDetails.mobilenumber);
		}else{
			$('#signtext').html("Edit Email Signature");
			var clientDetails = clientSignatureDetails.clientSignature;
			$('#email_custdetails').html(clientDetails.replaceAll("#mgst#", "</br>"));
		}
		if(billedtoname != ""){
			$('#customer_name').val(billedtoname);
			$.ajax({
				url : _getContextPath()+'/getCustomerDetails/'+clientId+'/'+billedtoname,
				async: false,
				cache: false,
				contentType: 'application/json',
				success : function(response) {
					if(response.customerEmail != ""){
						$('#customer_emailid').val(response.customerEmail);
					}
				}
			});
		}
		var urlStr = _getContextPath()+'/getinv/'+invoiceid+'/'+retType;
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(invoice) {
				if(invoice.mailSubject != null){
					 $('#email_subject').val(invoice.mailSubject);
				}else{
					$('.email_subject').val("Invoice "+invoiceno+" from "+clientSignatureDetails.businessname+"");
				}
				if(invoice.mailMessage != null){
					 $('#email_meassage').val(invoice.mailMessage);
				}else{
					$('#email_meassage').html("Here's your invoice! We appreciate your prompt payment.");
				}
				if(invoice.customerMailIds != null && invoice.customerMailIds != ''){
					$('#customer_emailid').val(invoice.customerMailIds);
				}
				if(invoice.customerCCMailIds != null){
					$('#customer_emailids').val(invoice.customerCCMailIds);
				}
				if(invoice.isIncludeSignature == false){
					$('.addEmailSignatureDetails').attr("checked",false);
					$('#email_custdetails,.signLink,#preview_custname').css("display","none");
				}else{
					$('.signLink,#preview_custname').css("display","inline-block");
					$('#email_custdetails').css("display","table-caption");
					$('.addEmailSignatureDetails').attr("checked",true);
				 }
				$('#senEmailBtn').removeClass("btn-loader-blue");
			}
		});
	}
	$('#send_EmailModal').on('shown.bs.modal', function () {
		var cname = $('#customer_name').val();
		var cemail = $('#customer_emailid').val();
		if( cname == ''){
			$('#customer_name').focus();
		}else if (cemail == '' ) {
			$('#customer_emailid').focus();
		}
	    
	}); 
	function sendEmails(){
		$('#sendEmailErrorMsg').html('');
		$('#customer_emailid').parent().removeClass('has-error has-danger');
		$('#senEmailBtn').addClass("btn-loader-blue");
		custEmailids = new Array();custCCEmailids = new Array();
		var customername = $('#customer_name').val();
		var customeremail = $('#customer_emailid').val();
		var customerccemail = $('#customer_emailids').val();
		var emailsubject = $('#email_subject').val();
		var emailMsg = $('#email_meassage').val();
		var clientSign = $('#email_custdetails').html();
		var signcheck = $('.addEmailSignatureDetails').is(':checked');
		var invoiceid=$('#email_invoiceId').val();
		var retType = $('#email_returnType').val();
		var flag = true;
		if(customeremail.trim() == ""){
			//$('#sendEmailErrorMsg').html('Please Enter Email id');
			$('#customer_emailid').parent().addClass('has-error has-danger');
			$('#senEmailBtn').removeClass("btn-loader-blue");
			flag = false;
		}
		if(flag){
			if(customeremail.indexOf(',') != -1) {
				customeremail = customeremail.split(',');
				for(var i = 0; i < customeremail.length; i++) {
					custEmailids.push(customeremail[i]);
				}
			} else {
				custEmailids.push(customeremail);
			}
			if(customerccemail.indexOf(',') != -1) {
				customerccemail = customerccemail.split(',');
				for(var i = 0; i < customerccemail.length; i++) {
					custCCEmailids.push(customerccemail[i]);
				}
			} else {
				custCCEmailids.push(customerccemail);
			}
			var mailObject = new Object();
			mailObject.clientid = clientId;
			mailObject.clientName = customername;
			mailObject.email = custEmailids;
			mailObject.cc = custCCEmailids;
			mailObject.subject = emailsubject;
			mailObject.message = emailMsg;
			mailObject.userDetails = clientSign;
			var url = _getContextPath()+"/sendMails/"+ userId + "/"+ invoiceid + "/"+retType+"/"+clientId+"?signcheck=" + signcheck;
			$.ajax({
				url: url,
				data: JSON.stringify(mailObject),
				type: "POST",
				contentType: 'application/json',
				success: function(data) {
					$('.successEmailmsg').removeClass("d-none");
					$('#successEmailmsg').text("Mail Send Succesfully");
					$('#senEmailBtn').removeClass("btn-loader-blue");
				},
				error: function(dat) {
					$('#senEmailBtn').removeClass("btn-loader-blue");
				}
			});
		}
	}
	function email_Preview(){
		var customername = $('#customer_name').val();
		var customeremail = $('#customer_emailid').val();
		var emailsubject = $('#email_subject').val();
		var emailMsg = $('#email_meassage').val();
		$('#email_cust_name').text(customername);
		$('#preview_email').html(emailMsg);
		if(clientSignatureDetails.clientSignature == ""){
			$('#preview_custname').html(clientSignatureDetails.businessname+"<br>"+clientSignatureDetails.email+"<br>"+clientSignatureDetails.mobilenumber);
		}else{
			var clientDetails = clientSignatureDetails.clientSignature;
			$('#preview_custname').html(clientDetails.replaceAll("#mgst#", "</br>"));
		}
	}
	$(".addEmailSignatureDetails").change(function() {
		var signcheck = $('.addEmailSignatureDetails').is(':checked');
		if(signcheck == false){
			 $('#email_custdetails,.signLink,#preview_custname').css("display","none");
		}else{
			 $('.signLink,#preview_custname').css("display","inline-block");
			 $('#email_custdetails').css("display","table-caption");
		}
	});

	function EmailSendToCustomer(invId,returnType){
		 getInvData(invId, returnType, function(invoice) {
			 sendEmailToCustomers(invId,invoice.invoiceno,invoice.billedtoname,returnType);
		 });
	}
	
  function getItemCustomFields(data,type,no){
		var content ="";
		var indexarry = new Array();
		if(data.items){
		$('.addItemCustLink').hide();
		for(var i=0;i<data.items.length;i++){
			var index = i + 1;
			var itemsData = data.items[i];
			if(itemsData != undefined){
				if(itemsData.customFieldType == 'input'){
					var require = itemsData.isMandatory ? 'required' : ''; 
					var astrich = itemsData.isMandatory ? 'astrich' : '';
					content += '<div class="col-md-6 col-sm-12 form-group mb-0" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-5 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/><input class="form-control item_CustomField'+index+''+type+'" name="itemCustomField'+index+'" id="item_CustomField'+index+''+type+'" placeholder="'+itemsData.customFieldName+'" '+require+'><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
					index++;
				}else if(itemsData.customFieldType == 'list'){
					var require = itemsData.isMandatory ? 'required' : ''; 
					var astrich = itemsData.isMandatory ? 'astrich' : '';
					content += '<div class="col-md-6 col-sm-12 form-group mb-0" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-5 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/><select class="form-control item_CustomField'+index+''+type+'" name="itemCustomField'+index+'" data-error="Please enter this value" id="item_CustomField'+index+''+type+'" '+require+'><option value="">-Select-</option>';
					for(var j = 0; j < itemsData.typeData.length; j++){
						content += "<option value=\'"+itemsData.typeData[j]+"\'>"+itemsData.typeData[j]+"</option>";
					}
					content +='</select><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
					index++;
				}else if(itemsData.customFieldType == 'checkB'){
					content += '<div class="col-md-6 col-sm-12 form-group mb-0 customCheck" id="inv_CustField1"><div class="row meterialform p-0"><p id="invcustFieldText1" class="lable-txt pl-3 pt-3 col-md-5">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0 mb-3 pt-3 checkBoxCustomField"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/><input type="hidden" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'"/>';
					/*for(var j = 0; j < itemsData.typeData.length; j++){
						if(itemsData.typeData[j] == $('#itemCustomField_text1'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text2'+no).val()|| itemsData.typeData[j] == $('#itemCustomField_text3'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text4'+no).val()){
								content += '<div class="checkbox"><label><input type="checkbox" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>&nbsp;'+itemsData.typeData[j]+'</label></div>';
							}else{
								content += '<div class="checkbox"><label><input type="checkbox" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'"><i class="helper"></i>&nbsp;'+itemsData.typeData[j]+'</label></div>';
							}
					}*/
					for(var j = 0; j < itemsData.typeData.length; j++){
						    if(itemsData.typeData[j] !=null && $('#itemCustomField_text1'+no).val().includes(itemsData.typeData[j])){
								content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
							}else if(itemsData.typeData[j] !=null && $('#itemCustomField_text2'+no).val().includes(itemsData.typeData[j])){
								content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
							}else if(itemsData.typeData[j] !=null && $('#itemCustomField_text3'+no).val().includes(itemsData.typeData[j])){
								content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
							}else if(itemsData.typeData[j] !=null && $('#itemCustomField_text4'+no).val().includes(itemsData.typeData[j])){
								content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
							}else {
								content += '<div class="checkbox checkbox-inline"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'"><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
							}
					}
					content += '</div></div></div>';
					indexarry.push(index);
					index++;
				}else if(itemsData.customFieldType == 'radio'){
					content += '<div class="col-md-6 col-sm-12 mb-0" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="lable-txt pl-3 pt-3 col-md-5">'+itemsData.customFieldName+'</p><div class="col-md-7 pl-0 mb-3 pt-3"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemsData.customFieldName+'"/>';
					for(var j = 0; j < itemsData.typeData.length; j++){
						if(itemsData.typeData[j] == $('#itemCustomField_text1'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text2'+no).val()|| itemsData.typeData[j] == $('#itemCustomField_text3'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text4'+no).val()){
							content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'" checked = "true">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
						}else{
							content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
						}
					} 
					content += '</div></div></div>';
					indexarry.push(index);
					index++;
				}
			}
			$('.item_custom_Fields'+type+'').html(content);
			if($('#itemCustomField_text1'+no).val() != null && $('#itemCustomField_text1'+no).val() != ""){
				$('#item_CustomField1'+type).val($('#itemCustomField_text1'+no).val());
			}
			if($('#itemCustomField_text2'+no).val() != null && $('#itemCustomField_text2'+no).val() != ""){
				$('#item_CustomField2'+type).val($('#itemCustomField_text2'+no).val());
			}
			if($('#itemCustomField_text3'+no).val() != null && $('#itemCustomField_text3'+no).val() != ""){
				$('#item_CustomField3'+type).val($('#itemCustomField_text3'+no).val());
			}
			if($('#itemCustomField_text4'+no).val() != null && $('#itemCustomField_text4'+no).val() != ""){
				$('#item_CustomField4'+type).val($('#itemCustomField_text4'+no).val());
			}
			if($('#itemId_text'+no).val() != null && $('#itemId_text'+no).val() != ""){
				$('#itemId_text').val($('#itemId_text'+no).val());
			}
			
		}
		}else{
			$('.addItemCustLink').show();
		}
	}
function showAdditionalItemFieldsPopup(no){
	$('#itemCustomFieldsModal').modal("show");
	$('#itemcustomsave').attr("onclick","saveItemCustomFields("+no+")");
	$('#item_notes_text1').val("")
	$('.modal-backdrop.show').css('opacity','0');
	$('.modal-arrow'+no).css('display','block');
	getAddItemCustomFields(customFieldsData,"Item",no);
}
function saveItemCustomFields(i){
	$('#itemCustomField_text1'+i).val($('#item_CustomField1Item').val());
	$('#itemCustomField_text2'+i).val($('#item_CustomField2Item').val());
	$('#itemCustomField_text3'+i).val($('#item_CustomField3Item').val());
	$('#itemCustomField_text4'+i).val($('#item_CustomField4Item').val());
	$('#itemnotes_text'+i).val($('#item_notes_text').val());
	closeAddCustomer('itemCustomFieldsModal');
}
function getAddItemCustomFields(data,type,no){
	var content ="";
	var indexarry = new Array();
	if(data.items){
	$('.addcustFieldsLink').hide();
	for(var i=0;i<data.items.length;i++){
		var index = i + 1;
		var itemsData = data.items[i];
		if(itemsData != undefined){
			if(itemsData.customFieldType == 'input'){
				var require = itemsData.isMandatory ? 'required' : ''; 
				var astrich = itemsData.isMandatory ? 'astrich' : '';
				content += '<div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-4 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0"><input class="form-control item_CustomField'+index+''+type+'" name="itemCustomField'+index+'" id="item_CustomField'+index+''+type+'" placeholder="'+itemsData.customFieldName+'" '+require+'><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div>';
				index++;
			}else if(itemsData.customFieldType == 'list'){
				var require = itemsData.isMandatory ? 'required' : ''; 
				var astrich = itemsData.isMandatory ? 'astrich' : '';
				content += '<div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-4 pl-3">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0"><select class="form-control item_CustomField'+index+''+type+'" name="itemCustomField'+index+'" data-error="Please enter this value" id="item_CustomField'+index+''+type+'" '+require+'><option value="">-Select-</option>';
				for(var j = 0; j < itemsData.typeData.length; j++){
					content += "<option value=\'"+itemsData.typeData[j]+"\'>"+itemsData.typeData[j]+"</option>";
				}
				content +='</select><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div>';
				index++;
			}else if(itemsData.customFieldType == 'checkB'){
				content += '<div class="row meterialform addItemForm p-0"><p id="invcustFieldText1" class="lable-txt pl-3 pt-1 col-md-4">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0 pt-1 checkBoxCustomField"><input type="hidden" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'"/>';
				/*for(var j = 0; j < itemsData.typeData.length; j++){
					if(itemsData.typeData[j] == $('#itemCustomField_text1'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text2'+no).val()|| itemsData.typeData[j] == $('#itemCustomField_text3'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text4'+no).val()){
							content += '<div class="checkbox"><label><input type="checkbox" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>&nbsp;'+itemsData.typeData[j]+'</label></div>';
						}else{
							content += '<div class="checkbox"><label><input type="checkbox" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'"><i class="helper"></i>&nbsp;'+itemsData.typeData[j]+'</label></div>';
						}
				}*/
				for(var j = 0; j < itemsData.typeData.length; j++){
					if(itemsData.typeData[j] !=null && $('#itemCustomField_text1'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && $('#itemCustomField_text2'+no).val().includes(itemsData.typeData[j])){
						
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && $('#itemCustomField_text3'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else if(itemsData.typeData[j] !=null && $('#itemCustomField_text4'+no).val().includes(itemsData.typeData[j])){
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'" checked><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}else {
						content += '<div class="checkbox"><label><input type="checkbox" onchange="changeItemCustCheckBox('+index+')" value="'+itemsData.typeData[j]+'"><i class="helper"></i>'+itemsData.typeData[j]+'</label></div>';
					}
			   }
				content += '</div></div>';
				indexarry.push(index);
				index++;
			}else if(itemsData.customFieldType == 'radio'){
				content += '<div class="row p-0"><p id="invcustFieldText1" class="lable-txt pl-3 pt-1 col-md-4">'+itemsData.customFieldName+'</p><div class="col-md-8 pl-0 mb-3 pt-3">';
				for(var j = 0; j < itemsData.typeData.length; j++){
					if(itemsData.typeData[j] == $('#itemCustomField_text1'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text2'+no).val()|| itemsData.typeData[j] == $('#itemCustomField_text3'+no).val() || itemsData.typeData[j] ==  $('#itemCustomField_text4'+no).val()){
						content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'" checked = "true">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
					}else{
						content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" class="item_CustomField'+index+''+type+'" id="item_CustomField'+index+''+type+'" value="'+itemsData.typeData[j]+'">&nbsp;<label>'+itemsData.typeData[j]+'</label>&nbsp;</div>';
					}
				} 
				content += '</div></div>';
				indexarry.push(index);
				index++;
			}
		}
		$('.item_custom_Fields'+type+'').html(content);
		if($('#itemCustomField_text1'+no).val() != null && $('#itemCustomField_text1'+no).val() != ""){
			$('#item_CustomField1'+type).val($('#itemCustomField_text1'+no).val());
		}
		if($('#itemCustomField_text2'+no).val() != null && $('#itemCustomField_text2'+no).val() != ""){
			$('#item_CustomField2'+type).val($('#itemCustomField_text2'+no).val());
		}
		if($('#itemCustomField_text3'+no).val() != null && $('#itemCustomField_text3'+no).val() != ""){
			$('#item_CustomField3'+type).val($('#itemCustomField_text3'+no).val());
		}
		if($('#itemCustomField_text4'+no).val() != null && $('#itemCustomField_text4'+no).val() != ""){
			$('#item_CustomField4'+type).val($('#itemCustomField_text4'+no).val());
		}
		if($('#itemId_text'+no).val() != null && $('#itemId_text'+no).val() != ""){
			$('#itemId_text').val($('#itemId_text'+no).val());
		}
		
	}

	}else{
		$('.addcustFieldsLink').show();
	}
}
  function changeItemCustCheckBox(no){
	  var checkval="";
	  $('.checkBoxCustomField input:checked').each(function() {
		  var val = $(this).val();
		  if(checkval == ""){
			  checkval = val;
		  }else{
			  checkval = checkval+","+val; 
		  }
	  });
	$('input[name=itemCustomField'+no+']').val(checkval);
  }
function changeStockAmts(no){
	var quantity = $('#qty_text'+no).val();
	var openingStock = $('#opening_stock'+no).val();
	var itemno = $('#product_text'+no).val();
	var saftey_stock = $('#saftey_stock'+no).val();
	var stock=0;
	if(itemno != ""){
		$.ajax({
			url: contextPath+"/checkStock/"+clientId+"/"+itemno,
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response){
				openingStock -= response;
				stock = openingStock;
					/*if(quantity > saftey_stock){
						$('#stockerr_name').text("Only few Items left");
					}*/
					if(quantity > stock){
						$('#qty_text'+no).attr("data-toggle","tooltip");
						$('#qty_text'+no).attr("title","Your stock is exceeded");
					}else{
						$('#stockerr_name').text("");
						$('#qty_text'+no).removeAttr("data-toggle");
				    }
			}
		});
	}
}	
	function closeAddItem(modalId){
		$('.modal-backdrop.show').css("display","none");
		$('#'+modalId).hide();
	}
	function itemCustomFieldsLink(){
		window.location.href = _getContextPath()+'/cp_upload'+paymenturlSuffix+'/'+month+'/'+year+'?type=customfields';
	}
	
	function advadjtaxable(no){
		var tx = document.getElementById('advrcavailadj_text'+no);
		var advavailadj = $('#total_text'+no).val();
		tx.value = parseFloat(advavailadj).toFixed(2);
	}
	
	function findadvadjTaxAmount(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var rtype=$('#retType').val();
		var billname = $('#billedtoname').val();
		var gstno = $('#billedtogstin').val();
		var itype = $("#idInvType").val();
		var invType = $('#invTyp').val();
		var exportType = $("#exportType option:selected").val();
		var cdnurtype = $('#cdnurtyp').val();
		var t = document.getElementById('taxableamount_text'+no);
		if(itype == 'Advances'){
			var rt = document.getElementById('rate_text'+no);
			if(rt){
				$('#taxableamount_text'+no).val(rt.value);
			}
		}
		/*if(dealertype == 'Composition' && revtype == 'Regular'){
			$('#taxrate_text'+no).val("0");
			$('.taxrateval').attr("readonly",true);
			$('#tax_rate').removeClass('astrich');
		}else{*/
			$('#taxrate_text'+no).removeAttr("readonly");
			$('#tax_rate').addClass('astrich');
			if(invType == 'SEWOP' || exportType == 'WOPAY'){
				//$('#taxrate_text'+no).val(0);
				$('#taxrate_text'+no).prop("disabled",false);	
			}else{
				$('#taxrate_text'+no).prop("disabled",false);
			}
		//}
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'CBW' || exportType == 'WPAY' || exportType == 'WOPAY'){
			interStateFlag = false;
		}else if(rtype == 'GSTR1' && (itype == 'Credit/Debit Note for Unregistered Taxpayers' || (itype == 'Credit/Debit Notes' && gstno == ""))){
			if(cdnurtype != 'B2CS'){
				interStateFlag=false;
				if(cdnurtype == 'EXPWOP'){
					$('.taxrateval').attr("readonly",true);
					$('.taxrateval').addClass("disabled");
					$('.taxrateval').val("0");
					$("#includetax").prop("checked",false);
					$("#includetax").prop("disabled",true);
				}else{
					$('.taxrateval').attr("readonly",false);
					$('.taxrateval').removeClass("disabled");
					$("#includetax").prop("disabled",false);
					
				}
			}
			
		} 
		if(itype == 'Advance Adjusted Detail'){
			var rt = document.getElementById('advrcavailadj_text'+no);
			var taxableValue = 0.00;
			if(rt && rt.value){
				taxableValue = parseFloat(rt.value);
			}
			var r = document.getElementById('taxrate_text'+no);
			if(rt && r){
			var val = 0;
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				if(rt && rt.value) {
					taxableValue = parseFloat(rt.value);
				}
				var cs = document.getElementById('cessrate_text'+no);
				if(cs.value == '') {
					cs.value = 0;
				}
				if(r.value){
				  //val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(cs.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				  val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
				}
			} else {
				$('#includetax').val('No');
				var advavailadj = $('#total_text'+no).val();
				val = ((advavailadj*parseFloat(r.value))/100);
				
			}
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					var txval = val;
					var cs = document.getElementById('cessamount_text'+no);
					if(cs.value) {
						txval += parseFloat(cs.value);
					}
					document.getElementById('advrcavailadj_text'+no).value = ((taxableValue-txval).toFixed(2))/1;
				}else{
					var tx = document.getElementById('advrcavailadj_text'+no);
					var advavailadj = $('#total_text'+no).val();
					tx.value = parseFloat(advavailadj).toFixed(2);
				}
				if(val > 0) {
					if(interStateFlag) {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
							
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					} else {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21 = parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
					document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
					var igst21 = document.getElementById('igsttax_text'+no).value;
					var cgst21 = document.getElementById('cgsttax_text'+no).value;
					var sgst21 = document.getElementById('sgsttax_text'+no).value;
					var tot21;
					tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
					document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
				}
			} else {
				document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
				var igst21 = document.getElementById('igsttax_text'+no).value;
				var cgst21 = document.getElementById('cgsttax_text'+no).value;
				var sgst21 = document.getElementById('sgsttax_text'+no).value;
				var tot21;
				tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
				document.getElementById('abb'+no).value = tot21.toFixed(2);
			}
			}
			
			var adavail = 0.00;
			if($('#advrcavail_text'+no).val() != ""){
				adavail = $('#advrcavail_text'+no).val();
			}
			var advavailadj = 0.00;
			if($('#total_text'+no).val() != ""){
				advavailadj = $('#total_text'+no).val();
			}
			var advrecavialable =0.00;
			if($('#advrcavailadj_text'+no).val() != ""){
				advrecavialable = $('#advrcavailadj_text'+no).val();	
			}
			var advtax = 0.00;
			if($('#abb'+no).val() != ""){
				advtax = $('#abb'+no).val();
			}
			var advremaingamt=parseFloat(adavail)-(parseFloat(advrecavialable)+parseFloat(advtax));
			var intadavail = 0.00;var intadvavailadj = 0.00;
			$('#advremaingamount_text'+no).val(parseFloat(advremaingamt).toFixed(2));
			if(adavail != ''){
				intadavail = parseInt(adavail);
			}
			if(advrecavialable && advtax){
				intadvavailadj = parseInt(parseFloat(advrecavialable)+parseFloat(advtax));
			}
			if(intadvavailadj > intadavail){
				$('#advrcavailadj_text'+no).parent().addClass('has-error has-danger');
				$('#adjamt'+no).css("display","block");
			}else{
				$('#adjamt'+no).css("display","none");
			}
			findTotal(no);
		}
	}
	
	
	function findadvadjdiffTaxAmount(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var rtype=$('#retType').val();
		var billname = $('#billedtoname').val();
		var gstno = $('#billedtogstin').val();
		var itype = $("#idInvType").val();
		var invType = $('#invTyp').val();
		var exportType = $("#exportType option:selected").val();
		var cdnurtype = $('#cdnurtyp').val();
		var t = document.getElementById('taxableamount_text'+no);
		if(itype == 'Advances'){
			var rt = document.getElementById('rate_text'+no);
			if(rt){
				$('#taxableamount_text'+no).val(rt.value);
			}
		}
		/*if(dealertype == 'Composition' && revtype == 'Regular'){
			$('#taxrate_text'+no).val("0");
			$('.taxrateval').attr("readonly",true);
			$('#tax_rate').removeClass('astrich');
		}else{*/
			$('#taxrate_text'+no).removeAttr("readonly");
			$('#tax_rate').addClass('astrich');
			if(invType == 'SEWOP' || exportType == 'WOPAY'){
				//$('#taxrate_text'+no).val(0);
				$('#taxrate_text'+no).prop("disabled",false);	
			}else{
				$('#taxrate_text'+no).prop("disabled",false);
			}
		//}
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'CBW' || exportType == 'WPAY' || exportType == 'WOPAY'){
			interStateFlag = false;
		}else if(rtype == 'GSTR1' && (itype == 'Credit/Debit Note for Unregistered Taxpayers' || (itype == 'Credit/Debit Notes' && gstno == ""))){
			if(cdnurtype != 'B2CS'){
				interStateFlag=false;
				if(cdnurtype == 'EXPWOP'){
					$('.taxrateval').attr("readonly",true);
					$('.taxrateval').addClass("disabled");
					$('.taxrateval').val("0");
					$("#includetax").prop("checked",false);
					$("#includetax").prop("disabled",true);
				}else{
					$('.taxrateval').attr("readonly",false);
					$('.taxrateval').removeClass("disabled");
					$("#includetax").prop("disabled",false);
					
				}
			}
			
		} 
		if(itype == 'Advance Adjusted Detail'){
			var rt = document.getElementById('advrcavailadj_text'+no);
			if(rt){
				var taxableValue = parseFloat(rt.value);
			}
			var r = document.getElementById('taxrate_text'+no);
			if(rt && r){
			var val = 0;
			if($('#includetax').is(":checked")) {
				$('#includetax').val('Yes');
				if(rt && rt.value) {
					taxableValue = parseFloat(rt.value);
				}
				var cs = document.getElementById('cessrate_text'+no);
				if(cs.value == '') {
					cs.value = 0;
				}
				//val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(cs.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
			} else {
				$('#includetax').val('No');
				var advavailadj = 0.0;
				if($('#diffPercent').is(":checked")) {
					advavailadj = $('#total_text'+no).val();
				}else{
					advavailadj = $('#advrcavailadj_text'+no).val();
				}
				val = ((advavailadj*parseFloat(r.value))/100);
			}
				if(val > 0) {
					if(interStateFlag) {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
							
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					} else {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21 = parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
					document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
					var igst21 = document.getElementById('igsttax_text'+no).value;
					var cgst21 = document.getElementById('cgsttax_text'+no).value;
					var sgst21 = document.getElementById('sgsttax_text'+no).value;
					var tot21;
					tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
					document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
				}
			} else {
				document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
				document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
				var igst21 = document.getElementById('igsttax_text'+no).value;
				var cgst21 = document.getElementById('cgsttax_text'+no).value;
				var sgst21 = document.getElementById('sgsttax_text'+no).value;
				var tot21;
				tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
				document.getElementById('abb'+no).value = tot21.toFixed(2);
			}
			}
			
			var adavail = 0.00;
			if($('#advrcavail_text'+no).val() != ""){
				adavail = $('#advrcavail_text'+no).val();
			}
			var advavailadj = 0.00;
			if($('#total_text'+no).val() != ""){
				advavailadj = $('#total_text'+no).val();
			}
			var advrecavialable =0.00;
			if($('#advrcavailadj_text'+no).val() != ""){
				advrecavialable = $('#advrcavailadj_text'+no).val();	
			}
			var advtax = 0.00;
			if($('#abb'+no).val() != ""){
				advtax = $('#abb'+no).val();
			}
			var advremaingamt=parseFloat(adavail)-(parseFloat(advrecavialable)+parseFloat(advtax));
			var intadavail = 0.00;var intadvavailadj = 0.00;
			$('#advremaingamount_text'+no).val(parseFloat(advremaingamt).toFixed(2));
			if(adavail != ''){
				intadavail = parseInt(adavail);
			}
			if(advrecavialable && advtax){
				intadvavailadj = parseInt(parseFloat(advrecavialable)+parseFloat(advtax));
			}
			if(intadvavailadj > intadavail){
				$('#advrcavailadj_text'+no).parent().addClass('has-error has-danger');
				$('#adjamt'+no).css("display","block");
			}else{
				$('#adjamt'+no).css("display","none");
			}
			findTotal(no);
		}
	}
	function advTaxableChange(){
		 taxableOrNontaxableChange();
	}
	function taxableOrNontaxableChange(){
		if($('#adv_taxable_type').is(":checked")){
			$('#reversecharge,#diffPer').show();
			$('.advsupport_div').hide();
			$('.rateinclusive').show();
			$('.rateinclusive,.invoiceLevelCess').css("margin-top","54px");
			$('#adv_taxable_type').val("true");
		}else{
			$('#reversecharge,#diffPer').hide();
			$('.advsupport_div').show();
			$('.rateinclusive').hide();
			$('.rateinclusive').css("top","0px");
			$('#adv_taxable_type').val("false");
		}
	}
	function totaltcstds(){
		var rettype = $('#retType').val();
		var tcscalval = $('#tcsamount').val();
		var totinvval = $('#totTotal').text().replace(/,/g , '');
		if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
			$('#invvalwithtcs').html(parseFloat(totinvval)+parseFloat(tcscalval));
		}else{
			if($('#tcs_val').is(":checked")){
				$('#tdsamount').val(formatNumber(parseFloat(tcscalval).toFixed(2)));
				$('#invvalwithtds').html(parseFloat(totinvval)+parseFloat(tcscalval));
			}else{
				$('#tdsamount').val(formatNumber(parseFloat(tcscalval).toFixed(2)));
				$('#invvalwithtds').html(parseFloat(totinvval)-parseFloat(tcscalval));
			}
		}
		OSREC.CurrencyFormatter.formatAll({
			selector: '.tcsindformat'
		});
	}
	
	function findTaxAmountedit(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var rtype=$('#retType').val();
		var billname = $('#billedtoname').val();
		var gstno = $('#billedtogstin').val();
		var itype = $("#idInvType").val();
		var invType = $('#invTyp').val();
		var exportType = $("#exportType option:selected").val();
		var cdnurtype = $('#cdnurtyp').val();
		var t = document.getElementById('taxableamount_text'+no);
		if(itype == 'Advances'){
			var rt = document.getElementById('rate_text'+no);
			if(rt){
				$('#taxableamount_text'+no).val(rt.value);
			}
		}
		$('#taxrate_text'+no).removeAttr("readonly");
		$('#tax_rate').addClass('astrich');
		if(invType == 'SEWOP' || exportType == 'WOPAY'){
			//$('#taxrate_text'+no).val(0);
			$('#taxrate_text'+no).prop("disabled",false);	
		}else{
			$('#taxrate_text'+no).prop("disabled",false);
		}
		if(invType == 'SEWP' || invType == 'SEWPC' || invType == 'CBW' || exportType == 'WPAY' || exportType == 'WOPAY'){
			interStateFlag = false;
		}else if(rtype == 'GSTR1' && (itype == 'Credit/Debit Note for Unregistered Taxpayers' || (itype == 'Credit/Debit Notes' && gstno == ""))){
			if(cdnurtype != 'B2CS'){
				interStateFlag=false;
				if(cdnurtype == 'EXPWOP'){
					$('.taxrateval').attr("readonly",true);
					$('.taxrateval').addClass("disabled");
					$('.taxrateval').val("0");
					$("#includetax").prop("checked",false);
					$("#includetax").prop("disabled",true);
				}else{
					$('.taxrateval').attr("readonly",false);
					$('.taxrateval').removeClass("disabled");
					$("#includetax").prop("disabled",false);
					
				}
			}
		}else if((rtype == 'GSTR2' || rtype == 'Purchase Register')){
			if(itype != 'Advance Adjusted Detail' && itype != 'Import Goods' && itype != 'Import Services' && itype != 'ISD' && itype != 'ITC Reversal'){
				if(gstno == "" && revtype == 'Regular'){
					$('#taxrate_text'+no).val("0");
					$('#taxrate_text'+no).addClass("disabled");
					$('.taxrateval').attr("readonly",true);
					$('#itctype_text'+no).val("no").attr("readonly",true).addClass("disabled");
					$('#itcpercent_text'+no).val("0").attr("readonly",true).addClass("disabled");
					$('#tax_rate').removeClass('astrich');
					document.getElementById("billgstin_name").innerHTML = "GSTIN field is empty, Tax amount will not be applicable";
					$("#includetax").prop("checked",false);
					$("#includetax").prop("disabled",true);
				}else{
					$('#taxrate_text'+no).removeClass("disabled");
					$('.taxrateval').removeAttr("readonly");
					$('#itctype_text'+no).removeClass("disabled").removeAttr("readonly");
					$('#itcpercent_text'+no).removeClass("disabled").removeAttr("readonly");
					$('#tax_rate').addClass('astrich');
					document.getElementById("billgstin_name").innerHTML = "";
					$("#includetax").prop("disabled",false);
				}
			}
		}	 
		if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
			var rt = document.getElementById('advrcavailadj_text'+no);
			if(rt){
				var taxableValue = parseFloat(rt.value);
			}
			var r = document.getElementById('taxrate_text'+no);
			if(rt && r){
				var val = 0;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					if(rt && rt.value) {
						taxableValue = parseFloat(rt.value);
					}
					var cs = document.getElementById('cessrate_text'+no);
					if(cs.value == '') {
						cs.value = 0;
					}
					val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
				} else {
					$('#includetax').val('No');
					val = ((taxableValue*parseFloat(r.value))/100);
				}
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					var txval = val;
					var cs = document.getElementById('cessamount_text'+no);
					if(cs.value) {
						txval += parseFloat(cs.value);
					}
					var taxbleval = taxableValue;
					var totaltax = document.getElementById('abb'+no);
					if(totaltax.value) {
						taxbleval += parseFloat(totaltax.value);
					}
					document.getElementById('advrcavailadj_text'+no).value = ((taxbleval-txval).toFixed(2))/1;
				}
				if(val > 0) {
					if(interStateFlag) {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
							
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					} else {
						if($('#diffPercent').is(":checked")) {
							document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
							document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21 = parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}else{
							document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					}
				} else {
					document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
					var igst21 = document.getElementById('igsttax_text'+no).value;
					var cgst21 = document.getElementById('cgsttax_text'+no).value;
					var sgst21 = document.getElementById('sgsttax_text'+no).value;
					var tot21;
					tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
					document.getElementById('abb'+no).value = tot21.toFixed(2);
				}
			}
			
			var adavail = 0.00;
			if($('#advrcavail_text'+no).val() != ""){
				adavail = $('#advrcavail_text'+no).val();
			}
			var advavailadj = 0.00;
			if($('#total_text'+no).val() != ""){
				advavailadj = $('#total_text'+no).val();
			}
			var advrecavialable =0.00;
			if($('#advrcavailadj_text'+no).val() != ""){
				advrecavialable = $('#advrcavailadj_text'+no).val();	
			}
			var advtax = 0.00;
			if($('#abb'+no).val() != ""){
				advtax = $('#abb'+no).val();
			}
			var advremaingamt=parseFloat(adavail)-(parseFloat(advrecavialable)+parseFloat(advtax));
			var intadavail = 0.00;var intadvavailadj = 0.00;
			$('#advremaingamount_text'+no).val(parseFloat(advremaingamt).toFixed(2));
			if(adavail != ''){
				intadavail = parseInt(adavail);
			}
			if(advrecavialable && advtax){
				intadvavailadj = parseInt(parseFloat(advrecavialable)+parseFloat(advtax));
			}
			if(intadvavailadj > intadavail){
				$('#advrcavailadj_text'+no).parent().addClass('has-error has-danger');
				$('#adjamt'+no).css("display","block");
			}else{
				$('#adjamt'+no).css("display","none");
			}
			findTotaledit(no);
		}else{
			var r = document.getElementById('taxrate_text'+no);
			if(t && t.value && r && r.value) {
				var taxableValue = parseFloat(t.value);
				var val = 0;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					var q = document.getElementById('qty_text'+no);
					var rt = document.getElementById('rate_text'+no);
					var d = document.getElementById('discount_text'+no);
					var ex = document.getElementById('exempted_text'+no);
					if(q && rt){
						if(q.value && rt.value) {
							taxableValue = parseFloat(q.value)*parseFloat(rt.value);
							if(itype !='Credit/Debit Note for Unregistered Taxpayers' && itype !='Credit/Debit Notes'){
								if(d != null){
									if(d.value) {
										var dStr = (d.value).toString();
										if (dStr.indexOf("%") > -1) {
											dStr = dStr.replace("%","");
											if(dStr > 100){
												$('#discount_text'+no).val(parseFloat(100)+"%");
												var gd = $('#discount_text'+no).val();
												dStr = gd.replace("%","");
											}
											taxableValue -= taxableValue * (dStr/100);
										}else{
											if(d.value > taxableValue){
												d.value = taxableValue;
											}
											taxableValue -= d.value;
										}
									}
								}
							}
							if(ex && ex.value){
								if(ex.value == ''){
									ex.value = 0;
								}
								if(ex.value <= taxableValue) {
									taxableValue -= ex.value;
								}
							}
						}
					}
					var cs = document.getElementById('cessrate_text'+no);
					if(cs.value == '') {
						cs.value = 0;
					}
					val = ( (parseFloat(taxableValue)/( 100+parseFloat(r.value)+parseFloat(cs.value)) * (parseFloat(r.value)+parseFloat(cs.value))));
				} else {
					$('#includetax').val('No');
					var q = document.getElementById('qty_text'+no);
					var rt = document.getElementById('rate_text'+no);
					var d = document.getElementById('discount_text'+no);
					var ex = document.getElementById('exempted_text'+no);
					if(q && rt){
						if(q.value && rt.value) {
							taxableValue = parseFloat(q.value)*parseFloat(rt.value);
							if(itype !='Credit/Debit Note for Unregistered Taxpayers' && itype !='Credit/Debit Notes'){
								if(d != null){
									if(d.value) {
										var dStr = (d.value).toString();
										if (dStr.indexOf("%") > -1) {
											dStr = dStr.replace("%","");
											if(dStr > 100){
												$('#discount_text'+no).val(parseFloat(100)+"%");
												var gd = $('#discount_text'+no).val();
												dStr = gd.replace("%","");
											}
											taxableValue -= taxableValue * (dStr/100);
										}else{
											if(d.value > taxableValue){
												d.value = taxableValue;
											}
											taxableValue -= d.value;
										}
									}
								}
							}
							if(ex && ex.value){
								if(ex.value == ''){
									ex.value = 0;
								}
								if(ex.value <= taxableValue) {
									taxableValue -= ex.value;
								}
							}
						}
					}
					var cs = document.getElementById('cessrate_text'+no);
					if(cs.value == '') {
						cs.value = 0;
					}
					val = ((taxableValue*parseFloat(r.value))/100);
				}
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					var txval = val;
					var cs = document.getElementById('cessamount_text'+no);
					if(cs.value) {
						txval += parseFloat(cs.value);
					}
					document.getElementById('taxableamount_text'+no).value = ((taxableValue-txval).toFixed(2))/1;
				}
				if((rtype == 'GSTR2' || 'Purchase Register')){
					if(itype != 'Advance Adjusted Detail' && itype != 'TXPA' && itype != 'Import Goods' && itype != 'Import Services'){
						if(gstno == ""){
							document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
							document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
							var igst21 = document.getElementById('igsttax_text'+no).value;
							var cgst21 = document.getElementById('cgsttax_text'+no).value;
							var sgst21 = document.getElementById('sgsttax_text'+no).value;
							var tot21;
							tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
							document.getElementById('abb'+no).value = tot21.toFixed(2);
						}
					}
				}
				if(invType == 'SEWOP'){
					document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
					var igst21 = document.getElementById('igsttax_text'+no).value;
					var cgst21 = document.getElementById('cgsttax_text'+no).value;
					var sgst21 = document.getElementById('sgsttax_text'+no).value;
					var tot21;
					tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
					document.getElementById('abb'+no).value = tot21.toFixed(2);
				} else if(val > 0) {
					if(interStateFlag) {
						if($('#diffPercent').is(":checked")) {
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
								document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}else{
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
								document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
						}
					} else {
						if($('#diffPercent').is(":checked")) {
							if(exportType != 'WOPAY'){
								document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
								document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
								document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
								var igst21 = document.getElementById('igsttax_text'+no).value;
								var cgst21 = document.getElementById('cgsttax_text'+no).value;
								var sgst21 = document.getElementById('sgsttax_text'+no).value;
								var tot21;
								tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
								document.getElementById('abb'+no).value = tot21.toFixed(2);
							}
							}else{
								if(exportType != 'WOPAY'){
									document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
									document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
									document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
									var igst21 = document.getElementById('igsttax_text'+no).value;
									var cgst21 = document.getElementById('cgsttax_text'+no).value;
									var sgst21 = document.getElementById('sgsttax_text'+no).value;
									var tot21;
									tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
									document.getElementById('abb'+no).value = tot21.toFixed(2);
								}
							}
					}
				} else {
					if((itype == 'B2B' && gstno == "") || itype == 'B2C' || itype == 'Advances'){
						if(interStateFlag) {
							if($('#diffPercent').is(":checked")) {
								if(exportType != 'WOPAY'){
									document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
									document.getElementById('cgsttax_text'+no).value =((val/2)*0.65).toFixed(2);
									document.getElementById('sgsttax_text'+no).value = ((val/2)*0.65).toFixed(2);
									var igst21 = document.getElementById('igsttax_text'+no).value;
									var cgst21 = document.getElementById('cgsttax_text'+no).value;
									var sgst21 = document.getElementById('sgsttax_text'+no).value;
									var tot21;
									tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
									document.getElementById('abb'+no).value = tot21.toFixed(2);
								}
							}else{
								if(exportType != 'WOPAY'){
									document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
									document.getElementById('cgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
									document.getElementById('sgsttax_text'+no).value = (((val/2).toFixed(2))/1).toFixed(2);
									var igst21 = document.getElementById('igsttax_text'+no).value;
									var cgst21 = document.getElementById('cgsttax_text'+no).value;
									var sgst21 = document.getElementById('sgsttax_text'+no).value;
									var tot21;
									tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
									document.getElementById('abb'+no).value = tot21.toFixed(2);
								}
							}
						} else {
							if($('#diffPercent').is(":checked")) {
								if(exportType != 'WOPAY'){
									document.getElementById('igsttax_text'+no).value = ((val)*0.65).toFixed(2);
									document.getElementById('cgsttax_text'+no).value =Number(0).toFixed(2);
									document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
									var igst21 = document.getElementById('igsttax_text'+no).value;
									var cgst21 = document.getElementById('cgsttax_text'+no).value;
									var sgst21 = document.getElementById('sgsttax_text'+no).value;
									var tot21;
									tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
									document.getElementById('abb'+no).value = tot21.toFixed(2);
								}
							}else{
								if(exportType != 'WOPAY'){
									document.getElementById('igsttax_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
									document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
									document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
									var igst21 = document.getElementById('igsttax_text'+no).value;
									var cgst21 = document.getElementById('cgsttax_text'+no).value;
									var sgst21 = document.getElementById('sgsttax_text'+no).value;
									var tot21;
									tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
									document.getElementById('abb'+no).value = tot21.toFixed(2);
								}
							}
						}
					}else{
						document.getElementById('igsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('cgsttax_text'+no).value = Number(0).toFixed(2);
						document.getElementById('sgsttax_text'+no).value = Number(0).toFixed(2);
						var igst21 = document.getElementById('igsttax_text'+no).value;
						var cgst21 = document.getElementById('cgsttax_text'+no).value;
						var sgst21 = document.getElementById('sgsttax_text'+no).value;
						var tot21;
						tot21 =parseFloat(sgst21)+parseFloat(igst21)+parseFloat(cgst21);
						document.getElementById('abb'+no).value = tot21.toFixed(2);
					}
				}
				var rettype = $('#retType').val();
				if(itype == "Advances"){
					findHsnOrSac(no);
				}
				if(rettype != 'GSTR1'){
					findITCValueedit(no);
				}
				findTotaledit(no);
			}
		}
	}
	
	function findTotaledit(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var itype = $("#idInvType").val();
		var t;
		if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
			t = document.getElementById('advrcavailadj_text'+no);
		}else{
			t = document.getElementById('taxableamount_text'+no);
		}
		var totalValue = 0;
		if(t && t.value) {
			totalValue = parseFloat(t.value);
		}
		var cessamt = document.getElementById('cessamount_text'+no);
		if(cessamt && cessamt.value) {
			totalValue += parseFloat(cessamt.value);
		}
		var samt = document.getElementById('sgsttax_text'+no);
		if(samt && samt.value) {
			totalValue += parseFloat(samt.value);
		}
		var iamt = document.getElementById('igsttax_text'+no);
		if(iamt && iamt.value) {
			totalValue += parseFloat(iamt.value);
		}
		var camt = document.getElementById('cgsttax_text'+no);
		if(camt && camt.value) {
			totalValue += parseFloat(camt.value);
		}
		var exempted = document.getElementById('exempted_text'+no);
		var qty = document.getElementById('qty_text'+no);
		if(exempted && exempted.value && qty && qty.value) {
			totalValue += (parseFloat(exempted.value))*(parseFloat(qty.value));
		}
		if(itype != 'Advance Adjusted Detail' && $('#includetax').is(":checked")){
			var advt = document.getElementById('rate_text'+no);
			var tt = document.getElementById('total_text'+no);
			if(itype == 'Advances'){
				if(tt){
					if(advt && advt.value) {
						 document.getElementById('total_text'+no).value = advt.value;
					}
				}
			}else{
				var taxableValue = 0;
				if(advt && advt.value && qty && qty.value) {
					taxableValue = parseFloat(qty.value)*parseFloat(advt.value);
				}
				if(tt){
					document.getElementById('total_text'+no).value = taxableValue;
				}
			}
			updateTotalsedit(no);
		}else if(revtype == 'Reverse'){
			if(t && t.value) {
			  document.getElementById('total_text'+no).value = t.value;
			}
			updateTotalsedit(no);
		}else{
			if(totalValue) {
				var tt = document.getElementById('total_text'+no);
				if(tt){
					tt.value = totalValue.toFixed(2);
				}
				if(itype == 'Exports'){
					var excrate = document.getElementById('exchange_Rate');
					if(excrate){
						var extotalvalue = 0;
						if(excrate.value && excrate.value > 0){
							extotalvalue = parseFloat(totalValue)/parseFloat(excrate.value);
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
						}else{
							extotalvalue = 0;
							if(document.getElementById('curtotal_text'+no)){
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
							}
						}
					}
				}
			}else{
				totalValue = 0.00;
				var tt = document.getElementById('total_text'+no);
				if(tt){
					tt.value = totalValue.toFixed(2);
				}
				if(itype == 'Exports'){
					var excrate = document.getElementById('exchange_Rate');
					if(excrate){
						var extotalvalue = 0;
						if(excrate.value && excrate.value > 0){
							extotalvalue = parseFloat(totalValue)/parseFloat(excrate.value);
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
						}else{
							extotalvalue = 0;
							if(document.getElementById('curtotal_text'+no)){
							document.getElementById('curtotal_text'+no).value = extotalvalue.toFixed(2);
							}
						}
					}
				}
			}
		updateTotalsedit(no);
		}
	}
	
	function updateTotalsedit(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var table=document.getElementById("sortable_table");
		var itype = $("#idInvType").val();
		var retType = $("#retType").val();
		var iRows=rowCount+1;
		if (itype == 'ISD'){
			iRows = 5;
		}else if(itype == 'ITC Reversal'){
			iRows = 8;
		}else{
			iRows=rowCount+1;
		}
		var totTaxable = 0, totIGST = 0, totCGST = 0, totSGST = 0, totCESS = 0, totTotal = 0, totITCCGST = 0, totITCSGST = 0, totITCIGST = 0, totITCCESS = 0, totISDCESS = 0, totAdvRemAmt = 0,totCurTotal = 0;;
		for(var i=1;i<iRows;i++) {
			if(i == no) {
				var totalTxbl , total , totalIGST , totalCGST , totalSGST, totalAdvRemAmount,totalCurencyAmount;
				totalCurencyAmount= document.getElementById('curtotal_text'+i);
				if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
					totalTxbl = document.getElementById('advrcavailadj_text'+i);
					totalAdvRemAmount = document.getElementById('advremaingamount_text'+i);
				}else if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalTxbl = document.getElementById('isdtaxableamount_text'+i);
				}else{
					totalTxbl = document.getElementById('taxableamount_text'+i);
				}
				if(totalTxbl && totalTxbl.value) {
					totTaxable+=parseFloat(totalTxbl.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalIGST = document.getElementById('isdigsttax_text'+i);
					if(totalIGST && totalIGST.value) {
						totIGST+=parseFloat(totalIGST.value);
					}
					totalCGST = document.getElementById('isdcgsttax_text'+i);
					if(totalCGST && totalCGST.value) {
						totCGST+=parseFloat(totalCGST.value);
					}
					totalSGST = document.getElementById('isdsgsttax_text'+i);
					if(totalSGST && totalSGST.value) {
						totSGST+=parseFloat(totalSGST.value);
					}
				}else{
					var totalIGST = document.getElementById('igsttax_text'+i);
					var totalCGST = document.getElementById('cgsttax_text'+i);
					var totalSGST = document.getElementById('sgsttax_text'+i);
					if(totalIGST && totalIGST.value) {
						totIGST+=parseFloat(totalIGST.value);
						totIGST+=parseFloat(totalCGST.value);
						totIGST+=parseFloat(totalSGST.value);
					}
				}
				var totalCESS = document.getElementById('cessamount_text'+i);
				if(totalAdvRemAmount && totalAdvRemAmount.value) {
					totAdvRemAmt+=parseFloat(totalAdvRemAmount.value);
				}
				if(totalCESS && totalCESS.value){
					if(totalCESS.value) {
						totCESS+=parseFloat(totalCESS.value);
					}
				}
				var totalISDCESS = document.getElementById('isdisdcess_text'+i);
				if(totalISDCESS && totalISDCESS.value){
					if(totalISDCESS.value) {
						totISDCESS+=parseFloat(totalISDCESS.value);
					}
				}
				var totalITCCGST = document.getElementById('cgstitc_text'+i);
				var totalITCSGST = document.getElementById('sgstitc_text'+i);
				var totalITCIGST = document.getElementById('igstitc_text'+i);
				var totalITCCESS = document.getElementById('cessitc_text'+i);
				if(totalITCIGST && totalITCIGST.value) {
					totITCIGST+=parseFloat(totalITCCGST.value);
					totITCIGST+=parseFloat(totalITCSGST.value);
					totITCIGST+=parseFloat(totalITCIGST.value);
					totITCIGST+=parseFloat(totalITCCESS.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
					total = document.getElementById('isdtotal_text'+i);
				}else{
					total = document.getElementById('total_text'+i);
				}
				if(total && total.value) {
					totTotal+=parseFloat(total.value);
				}
				if(totalCurencyAmount && totalCurencyAmount.value) {
					totCurTotal+=parseFloat(totalCurencyAmount.value);
				}
			} else {
				var totalTxbl , total , totalIGST , totalCGST , totalSGST, totalAdvRemAmount,totalCurencyAmount;
				totalCurencyAmount= document.getElementById('curtotal_text'+i);
		      	if(itype == 'Advance Adjusted Detail' || itype == 'TXPA'){
					totalTxbl = document.getElementById('advrcavailadj_text'+i);
					totalAdvRemAmount = document.getElementById('advremaingamount_text'+i);
				}else if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalTxbl = document.getElementById('isdtaxableamount_text'+i);
				}else{
					totalTxbl = document.getElementById('taxableamount_text'+i);
				}
		      	if(totalAdvRemAmount && totalAdvRemAmount.value) {
					totAdvRemAmt+=parseFloat(totalAdvRemAmount.value);
				}
				if(totalTxbl && totalTxbl.value) {
					totTaxable+=parseFloat(totalTxbl.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalIGST = document.getElementById('isdigsttax_text'+i);
				}else{
					totalIGST = document.getElementById('igsttax_text'+i);
				}
				if(totalIGST && totalIGST.value) {
					totIGST+=parseFloat(totalIGST.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalCGST = document.getElementById('isdcgsttax_text'+i);
					if(totalCGST && totalCGST.value) {
						totCGST+=parseFloat(totalCGST.value);
					}
				}else{
					totalCGST = document.getElementById('cgsttax_text'+i);
					if(totalCGST && totalCGST.value) {
						totIGST+=parseFloat(totalCGST.value);
					}
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
					totalSGST = document.getElementById('isdsgsttax_text'+i);
					if(totalSGST && totalSGST.value) {
						totSGST+=parseFloat(totalSGST.value);
					}
				}else{
					totalSGST = document.getElementById('sgsttax_text'+i);
					if(totalSGST && totalSGST.value) {
						totIGST+=parseFloat(totalSGST.value);
					}
				}
				var totalCESS = document.getElementById('cessamount_text'+i);
				if(totalCESS && totalCESS.value){
					if(totalCESS.value) {
						totCESS+=parseFloat(totalCESS.value);
					}
				}
				var totalISDCESS = document.getElementById('isdisdcess_text'+i);
				if(totalISDCESS && totalISDCESS.value){
					if(totalISDCESS.value) {
						totISDCESS+=parseFloat(totalISDCESS.value);
					}
				}
				var totalITCIGST = document.getElementById('itc_tax_tot'+i);
				if(totalITCIGST != null && totalITCIGST.value) {
					totITCIGST+=parseFloat(totalITCIGST.value);
				}
				if(itype == 'ISD' || itype == 'ITC Reversal'){
					total = document.getElementById('isdtotal_text'+i);
				}else{
					total = document.getElementById('total_text'+i);
				}
				if(total && total.value) {
					totTotal+=parseFloat(total.value);
				}
				if(totalCurencyAmount && totalCurencyAmount.value) {
					totCurTotal+=parseFloat(totalCurencyAmount.value);
				}
			}
		}
		$('#totTaxable ,#isdtotTaxable').html(parseFloat(totTaxable));
		$('#totIGST ,#isdtotIGST').html(parseFloat(totIGST));
		$('#totCGST ,#isdtotCGST').html(parseFloat(totCGST));
		$('#totSGST ,#isdtotSGST').html(parseFloat(totSGST));
		$('#totCESS').html(parseFloat(totCESS));
		$('#isdtotisdcess').html(parseFloat(totISDCESS));
		$('#totITCCGST').html(parseFloat(totITCCGST));
		$('#totITCSGST').html(parseFloat(totITCSGST));
		$('#totITCIGST').html(parseFloat(totITCIGST));
		$('#totITCCESS').html(parseFloat(totITCCESS));
		$('#totCurAmt').html(parseFloat(totCurTotal));
		$('#totAdvRemaining').html(parseFloat(totAdvRemAmt));
		if(revtype != 'Reverse'){
		$('#totTotal ,#isdtotTotal').html(parseFloat(totTotal));
		}else{
			$('#totTotal ,#isdtotTotal').html(parseFloat(totTaxable));
		}
		$('#cdn_taxableamount').val(parseFloat(totTaxable));
		if($('#roundOffTotalChckbox').is(':checked')) {
			$('#idTotal').html(Math.round(parseFloat(totTotal)).toFixed(2));
			$('#roundOffTotalValue').val((Math.round(totTotal)-totTotal).toFixed(2));
		}else if(!$('#roundOffTotalChckbox').is(':checked')){
			if(retType == "EWAYBILL"){
				var othervalue = document.getElementById('otherValue');
				if(othervalue && othervalue.value){
					if(othervalue.value) {
							totTotal+=parseFloat((othervalue.value).replace(/,/g , ''));
					}
				}
			}
			$('#idTotal').html(parseFloat(totTotal));
			$('#roundOffTotalValue').val('');
		}
		$('#hiddenroundOffTotalValue').val(parseFloat(totTotal));
		$('#cdn_taxableamount').val(parseFloat(totTaxable));
		$(".indformat").each(function(){
			$(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
		//tdstcscal();
	}
	
	function updateRateTypeedit(clientState,returntype) {
		if(returntype == 'Purchase Register'){
			returntype = 'GSTR2';
		}
    	var invType = $('#idInvType').val();
		var billedState = document.getElementById('billedtostatecode');
		if(returntype != 'GSTR2'){
			if(billedState && clientState) {
				var stateCode = billedState.value;
				if(stateCode) {
					if(stateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+stateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									stateCode = states[0].code;
								}
							}
						});
					}
					if(clientState.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+clientState,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									clientState = states[0].code;
								}
							}
						});
					}
					if(stateCode == clientState) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}
		}else{
			var custgstin = document.getElementById('billedtogstin');
			var placeOfSupply = document.getElementById('billedtostatecode');
			var gstin = custgstin.value;
			if(gstin && billedState) {
				var statecode = gstin.substring(0, 2);
				var billedStateCode = billedState.value;
				if(billedStateCode) {
					if(billedStateCode.length > 2) {
						$.ajax({
							url: contextPath+"/stateconfig?query="+billedStateCode,
							async: false,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(states) {
								if(states.length == 1) {
									billedStateCode = states[0].tin;
								}
							}
						});
					}
					if(statecode == billedStateCode) {
						interStateFlag=true;
					} else {
						interStateFlag=false;
					}
				}
			}else{
				if(invType == 'B2B Unregistered' || invType == 'B2B'){
					var suptype = $('#printerintra').val();
					if(suptype == 'Inter'){
						interStateFlag=false;
					}else{
						interStateFlag=true;
					}
				}else{
					interStateFlag=true;
				}
			}
			if(invType == 'Import Services' || invType == 'Import Goods'){
				interStateFlag=false;
			}
			ratetype(billedState,clientState);	
		}
		var i=1;
		if(returntype != 'GSTR2'){
			if(invType != 'Nil Supplies'){
				$.each($("#allinvoice tr"),function() {
					findTaxAmountedit(i);
					findCessAmountedit(i);
					i++;
				});
			}
		}else{
			if(invType != 'Nil Supplies'){
				$.each($("#allinvoice tr"),function() {
					findTaxAmountedit(i);
					if(invType != 'Advance Adjusted Detail' && invType != 'TXPA'){
						findCessAmountedit(i);
					}
					i++;
				});
			}
		}
	}
	
	function findCessAmountedit(no) {
		var revtype = $("#revchargetype>option:selected").val();
		var quantity = document.getElementById('qty_text'+no);
		var cesstype = $('input[name="cessType"]:checked').val();
		var t = document.getElementById('taxableamount_text'+no);
		var r = document.getElementById('cessrate_text'+no);
		var invType = $('#idInvType').val();
		var itype = $("#invTyp").val();
		var rtype=$('#retType').val();
		var exportType = $("#exportType option:selected").val();
		var gstno = $('#billedtogstin').val();
		$('#cessrate_text'+no).removeAttr("readonly");
		$('#rate_as').addClass('astrich');
		if(itype == 'SEWOP' || itype == 'WOPAY'){
			$('#cessrate_text'+no).val(0);
			$('#cessrate_text'+no).prop("disabled",true);	
		}else{
			$('#cessrate_text'+no).prop("disabled",false);
		}
		if(exportType == 'WOPAY'){
			$('#cessrate_text'+no).val("0").prop("disabled",true).attr("readonly",true);
		}else if((rtype == 'GSTR2' || rtype == 'Purchase Register')){
			if(invType != 'Advance Adjusted Detail' && invType != 'TXPA' && invType != 'Import Goods' && invType != 'Import Services' && invType != 'ISD' && invType != 'ITC Reversal'){
				if(gstno == "" && revtype == 'Regular'){
					$('#cessrate_text'+no).val("0");
					$('#cessrate_text'+no).addClass("disabled");
					$('#cessrate_text'+no).attr("readonly",true);
					$('#rate_as').removeClass('astrich');
				}else{
					$('#cessrate_text'+no).removeClass("disabled");
					$('#cessrate_text'+no).removeAttr("readonly");
					$('#rate_as').addClass('astrich');
				}
			}
		}
		if(invType =='Advance Adjusted Detail' || invType == 'TXPA'){
				var rt = document.getElementById('advrcavailadj_text'+no);
				if(rt && rt.value){
					if(quantity && quantity.value){
						var qty = parseFloat(quantity.value);
					}
				var taxableValue = parseFloat(rt.value);
				var val = 0;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					taxableValue = parseFloat(rt.value);
					var tx = document.getElementById('taxrate_text'+no);
					if(tx.value == '') {
						tx.value = 0;
					}
					val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(tx.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				} else {
					$('#includetax').val('No');
					if(cesstype == "Taxable Value"){
						val = ((taxableValue*parseFloat(r.value))/100);
					}else{
						if(qty){
							val = (qty*parseFloat(r.value));
						}
					}
				}
				if(val > 0) {
					val = ((val.toFixed(2))/1).toFixed(2);
				} else {
					val = Number(0).toFixed(2);
				}
				document.getElementById('cessamount_text'+no).value = val;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					findTaxAmountedit(no);
				} else {
					$('#includetax').val('No');
					findTotaledit(no);
				}
			}
		}else{
			if(t && t.value && r.value) {
				var taxableValue = parseFloat(t.value);
				if(quantity && quantity.value){
					var qty = parseFloat(quantity.value);
				}
				var val = 0;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					var q = document.getElementById('qty_text'+no);
					var rt = document.getElementById('rate_text'+no);
					var d = document.getElementById('discount_text'+no);
					var ex = document.getElementById('exempted_text'+no);
					if(q && rt){
						if(q.value && rt.value) {
							taxableValue = parseFloat(q.value)*parseFloat(rt.value);
							if(invType !='Credit/Debit Note for Unregistered Taxpayers' && invType != 'Credit/Debit Notes'){
								if(d.value == ''){
									d.value = 0;
								}
								if(d.value <= taxableValue) {
									taxableValue -= d.value;
								}
							}
							if(ex && ex.value){
								if(ex.value == ''){
									ex.value = 0;
								}
								if(ex.value <= taxableValue) {
									taxableValue -= ex.value;
								}
							}
						}
					}
					var tx = document.getElementById('taxrate_text'+no);
					if(tx.value == '') {
						tx.value = 0;
					}
					val = (((parseFloat(taxableValue)-((parseFloat(taxableValue)*(parseFloat(tx.value)+parseFloat(r.value)))/100))*parseFloat(r.value))/100);
				} else {
					$('#includetax').val('No');
					if(cesstype == "Taxable Value"){
						val = ((taxableValue*parseFloat(r.value))/100);
					}else{
						if(qty){
							val = (qty*parseFloat(r.value));
						}
					}
				}
				if(val > 0) {
					val = ((val.toFixed(2))/1).toFixed(2);
				} else {
					val = Number(0).toFixed(2);
				}
				document.getElementById('cessamount_text'+no).value = val;
				if($('#includetax').is(":checked")) {
					$('#includetax').val('Yes');
					findTaxAmountedit(no);
				} else {
					$('#includetax').val('No');
					findTotaledit(no);
				}
			}
		}
		findITCValueedit(no);
		updateTotalsedit(no);
	}
	
	function findITCValueedit(no) {
		var p = document.getElementById('itcpercent_text'+no);
		if(p != null && p.value>100){
			//$('#itcpercent_text'+no).attr("required","true");
			$('#itcpercent_text'+no).val('100');
		}
		if(p != null && p.value) {
			var iamt = document.getElementById('igsttax_text'+no);
			var camt = document.getElementById('cgsttax_text'+no);
			var csamt = document.getElementById('cessamount_text'+no);
			if(iamt != null && iamt.value) {
				var val = ((parseFloat(p.value)*parseFloat(iamt.value))/100);
				if(val > 0.00) {
					document.getElementById('igstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					igst_itc = document.getElementById('igstitc_text'+no).value;
					var csamt = document.getElementById('cessamount_text'+no);
					if(csamt != null && csamt.value) {
						var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
						if(val > 0.00) {
							document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
						} else {
							document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
						}
					}
					cess_itc = document.getElementById('cessitc_text'+no).value;
					itc_tot = parseFloat(igst_itc) + parseFloat(cess_itc);
					document.getElementById('itc_tax_tot'+no).value = itc_tot.toFixed(2);
				} else {
					document.getElementById('igstitc_text'+no).value = Number(0).toFixed(2);
					document.getElementById('itc_tax_tot'+no).value=Number(0).toFixed(2);
				}
			}
			var camt = document.getElementById('cgsttax_text'+no);
			if(camt != null && camt.value) {
				var val = ((parseFloat(p.value)*parseFloat(camt.value))/100);
				if(val > 0.00) {
					document.getElementById('cgstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					document.getElementById('sgstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					cgst_itc = document.getElementById('cgstitc_text'+no).value;
					sgst_itc = document.getElementById('sgstitc_text'+no).value;
					var csamt = document.getElementById('cessamount_text'+no);
					if(csamt != null && csamt.value) {
						var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
						if(val > 0.00) {
							document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
						} else {
							document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
						}
					}
					cess_itc = document.getElementById('cessitc_text'+no).value;
					itc_tot = parseFloat(cgst_itc) + parseFloat(sgst_itc) + parseFloat(cess_itc);
					document.getElementById('itc_tax_tot'+no).value = itc_tot.toFixed(2);
				} else {
					document.getElementById('cgstitc_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgstitc_text'+no).value = Number(0).toFixed(2);
				}
			}
			var csamt = document.getElementById('cessamount_text'+no);
			if(csamt != null && csamt.value) {
				var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
				if(val > 0.00) {
					document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
				} else {
					document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
				}
			}
		}else{
			var igstitc = document.getElementById('igstitc_text'+no);
			var cgstitc = document.getElementById('cgstitc_text'+no);
			var sgstitc = document.getElementById('sgstitc_text'+no);
			var cessitc = document.getElementById('cessitc_text'+no);
			if(igstitc != null){
				document.getElementById('igstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(cgstitc != null){
				document.getElementById('cgstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(sgstitc != null){
				document.getElementById('sgstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(cessitc != null){
				document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
			}
		}
		updateTotalsedit(no);
	}
	
	function findITCValueedit(no) {
		var p = document.getElementById('itcpercent_text'+no);
		if(p != null && p.value>100){
			//$('#itcpercent_text'+no).attr("required","true");
			$('#itcpercent_text'+no).val('100');
		}
		if(p != null && p.value) {
			var iamt = document.getElementById('igsttax_text'+no);
			var camt = document.getElementById('cgsttax_text'+no);
			var csamt = document.getElementById('cessamount_text'+no);
			if(iamt != null && iamt.value) {
				var val = ((parseFloat(p.value)*parseFloat(iamt.value))/100);
				if(val > 0.00) {
					document.getElementById('igstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					igst_itc = document.getElementById('igstitc_text'+no).value;
					var csamt = document.getElementById('cessamount_text'+no);
					if(csamt != null && csamt.value) {
						var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
						if(val > 0.00) {
							document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
						} else {
							document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
						}
					}
					cess_itc = document.getElementById('cessitc_text'+no).value;
					itc_tot = parseFloat(igst_itc) + parseFloat(cess_itc);
					document.getElementById('itc_tax_tot'+no).value = itc_tot.toFixed(2);
				} else {
					document.getElementById('igstitc_text'+no).value = Number(0).toFixed(2);
					document.getElementById('itc_tax_tot'+no).value=Number(0).toFixed(2);
				}
			}
			var camt = document.getElementById('cgsttax_text'+no);
			if(camt != null && camt.value) {
				var val = ((parseFloat(p.value)*parseFloat(camt.value))/100);
				if(val > 0.00) {
					document.getElementById('cgstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					document.getElementById('sgstitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
					cgst_itc = document.getElementById('cgstitc_text'+no).value;
					sgst_itc = document.getElementById('sgstitc_text'+no).value;
					var csamt = document.getElementById('cessamount_text'+no);
					if(csamt != null && csamt.value) {
						var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
						if(val > 0.00) {
							document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
						} else {
							document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
						}
					}
					cess_itc = document.getElementById('cessitc_text'+no).value;
					itc_tot = parseFloat(cgst_itc) + parseFloat(sgst_itc) + parseFloat(cess_itc);
					document.getElementById('itc_tax_tot'+no).value = itc_tot.toFixed(2);
				} else {
					document.getElementById('cgstitc_text'+no).value = Number(0).toFixed(2);
					document.getElementById('sgstitc_text'+no).value = Number(0).toFixed(2);
				}
			}
			var csamt = document.getElementById('cessamount_text'+no);
			if(csamt != null && csamt.value) {
				var val = ((parseFloat(p.value)*parseFloat(csamt.value))/100);
				if(val > 0.00) {
					document.getElementById('cessitc_text'+no).value = ((val.toFixed(2))/1).toFixed(2);
				} else {
					document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
				}
			}
		}else{
			var igstitc = document.getElementById('igstitc_text'+no);
			var cgstitc = document.getElementById('cgstitc_text'+no);
			var sgstitc = document.getElementById('sgstitc_text'+no);
			var cessitc = document.getElementById('cessitc_text'+no);
			if(igstitc != null){
				document.getElementById('igstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(cgstitc != null){
				document.getElementById('cgstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(sgstitc != null){
				document.getElementById('sgstitc_text'+no).value = Number(0).toFixed(2);
			}
			if(cessitc != null){
				document.getElementById('cessitc_text'+no).value = Number(0).toFixed(2);
			}
		}
		updateTotalsedit(no);
	}
	
function edittcstds(tcstds){
	if(tcstds == "TDS"){
		var tdsamts = $('#tdsfield').text().replace(/,/g , '');
		$('#tdsamt').val(parseFloat(tdsamts).toFixed(2));
		$('#tdsfield').html('<input type="text" onkeyup="changeTdsAmount()" class="form-control text-right" id="tds_amount" value="'+tdsamts+'" style="font-size:14px;font-weight:600;"/>');
	}else{
		var tdsamts = $('#tcsfield').text().replace(/,/g , '');
		$('#tcsamt').val(parseFloat(tdsamts).toFixed(2));
		$('#tcsfield').html('<input type="text" onkeyup="changeTcsAmount()" class="form-control text-right" id="tdsamount" value="'+tdsamts+'" style="font-size:14px;font-weight:600;"/>');
	}
}

function supportTCS(invtype,enableDiscount,enableExempted,enableSalesCess,enableLedger){
	if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
		$('.tot_net_type').attr('colspan','12');
	}else if(invtype == 'Nil Supplies'){
		$('.tot_net_type').attr('colspan','8');
	}else if(invtype == 'Exports'){
		$('.tot_net_type').attr('colspan','10');
	}else if(invtype == 'Nil Supplies' || invtype == 'Advances'){
		$('.tot_net_type').attr('colspan','7');
	}else{
		$('.tot_net_type').attr('colspan','9');
	}
	if((enableLedger == true || enableLedger == "true")){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports" && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers" && invtype != "Advance Adjusted Detail" && invtype == 'TXPA'){
	    	$('.tot_net_type').attr("colspan","12");
	    }else{
	    	if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
				$('.tot_net_type').attr("colspan","10");
			}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
				$('.tot_net_type').attr("colspan","11");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","11");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","8");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","9");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
	    }
	}
	if((enableSalesCess == true || enableSalesCess == "true")){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports" && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers" && invtype != "Advance Adjusted Detail" && invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","13");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
				$('.tot_net_type').attr("colspan","11");
			}else if(invtype == "Exports" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
				$('.tot_net_type').attr("colspan","12");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","9");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","9");
			}else{
				$('.tot_net_type').attr("colspan","11");
			}
		}
	}
	if((enableDiscount == true || enableDiscount == "true")){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports" && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA'){
			$('.tot_net_type').attr("colspan","10");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
				$('.tot_net_type').attr("colspan","9");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","11");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","8");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","7");
			}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
				$('.tot_net_type').attr("colspan","10");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
		}
	}
	if((enableExempted == true || enableExempted == "true")){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports" && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA'){
			$('.tot_net_type').attr("colspan","10");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
				$('.tot_net_type').attr("colspan","10");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","11");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","9");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","7");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
		}
	}
	if(((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true"))){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports" && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA'){
			$('.tot_net_type').attr("colspan","11");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
				$('.tot_net_type').attr("colspan","10");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","12");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","9");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","7");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
		}
	}
	if(((enableDiscount == true || enableDiscount == "true") && (enableLedger == true || enableLedger == "true"))){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports" && invtype != "Credit/Debit Notes" && invtype != "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","11");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
				$('.tot_net_type').attr("colspan","10");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","12");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","10");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","8");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
		}
	}
	if(((enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true"))){
		if(invtype != "Nil Supplies" && invtype != "Advances" && invtype != "Exports"){
			$('.tot_net_type').attr("colspan","11");
		}else{
			if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","12");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","8");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
		}
	}
	if(((enableDiscount == true || enableDiscount == "true") && (enableSalesCess == true || enableSalesCess == "true"))){
		if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","11");
		}else if(invtype == "Exports"){
			$('.tot_net_type').attr("colspan","13");
		}else if(invtype == "Advances" || invtype == "Nil Supplies"){
			$('.tot_net_type').attr("colspan","9");
		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","12");
		}else{
			$('.tot_net_type').attr("colspan","12");
		}
	}
	if(((enableExempted == true || enableExempted == "true") && (enableSalesCess == true || enableSalesCess == "true"))){
		if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","12");
		}else if(invtype == "Exports"){
			$('.tot_net_type').attr("colspan","13");
		}else if(invtype == "Advances"){
			$('.tot_net_type').attr("colspan","9");
		}else if(invtype == "Nil Supplies"){
			$('.tot_net_type').attr("colspan","10");
		}else if(invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","12");
		}else{
			$('.tot_net_type').attr("colspan","12");
		}
	}
	if(((enableLedger == true || enableLedger == "true") && (enableSalesCess == true || enableSalesCess == "true"))){
		if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","12");
		}else if(invtype == "Exports" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","13");
		}else if(invtype == "Nil Supplies"){
			$('.tot_net_type').attr("colspan","9");
		}else if(invtype == "Advances"){
			$('.tot_net_type').attr("colspan","10");
		}else{
			$('.tot_net_type').attr("colspan","12");
		}
	}
	if((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true") && (enableSalesCess == true || enableSalesCess == "true")){
		if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","12");
		}else if(invtype == "Exports"){
			$('.tot_net_type').attr("colspan","14");
		}else if(invtype == "Advances"){
			$('.tot_net_type').attr("colspan","9");
		}else if(invtype == "Nil Supplies"){
			$('.tot_net_type').attr("colspan","10");
		}else{
			$('.tot_net_type').attr("colspan","13");
		}
	}
	if((enableLedger == true || enableLedger == "true") && (enableExempted == true || enableExempted == "true") && (enableSalesCess == true || enableSalesCess == "true")){
		if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","13");
		}else if(invtype == "Exports"){
			$('.tot_net_type').attr("colspan","14");
		}else if(invtype == "Advances"){
			$('.tot_net_type').attr("colspan","10");
		}else if(invtype == "Nil Supplies"){
			$('.tot_net_type').attr("colspan","11");
		}else{
			$('.tot_net_type').attr("colspan","13");
		}
	}
	if((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true")){
		if(invtype != "Credit/Debit Notes" && invtype != "Nil Supplies" && invtype != "Exports" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","12");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA' || invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","11");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","13");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","8");
			}else{
				$('.tot_net_type').attr("colspan","10");
			}
		}
	}
	if((enableDiscount == true || enableDiscount == "true") && (enableSalesCess == true || enableSalesCess == "true") && (enableLedger == true || enableLedger == "true")){
		if(invtype != "Credit/Debit Notes" && invtype != "Nil Supplies" && invtype != "Exports" && invtype != "Advances" && invtype != "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","13");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers"){
				$('.tot_net_type').attr("colspan","12");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","14");
			}else if(invtype == "Advances" || invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","10");
			}else{
				$('.tot_net_type').attr("colspan","11");
			}
		}
	}
	if((enableDiscount == true || enableDiscount == "true") && (enableLedger == true || enableLedger == "true") && (enableExempted == true || enableExempted == "true") && (enableSalesCess == true || enableSalesCess == "true")){
		if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
			$('.tot_net_type').attr("colspan","13");
		}else if(invtype == "Exports"){
			$('.tot_net_type').attr("colspan","15");
		}else if(invtype == "Nil Supplies" || invtype == "Advances"){
			$('.tot_net_type').attr("colspan","10");
		}else{
			$('.tot_net_type').attr("colspan","14");
		}
	}
	if((enableDiscount == true || enableDiscount == "true") && (enableSalesCess == true || enableSalesCess == "true") && (enableLedger == true || enableLedger == "true") && (enableExempted == true || enableExempted == "true")){
		if(invtype != "Credit/Debit Notes" && invtype != "Nil Supplies" && invtype != "Exports" && invtype != "Advances" && invtype != "Advance Adjusted Detail" && invtype != 'TXPA' && invtype != "Credit/Debit Note for Unregistered Taxpayers"){
			$('.tot_net_type').attr("colspan","14");
		}else{
			if(invtype == "Credit/Debit Notes" || invtype == "Credit/Debit Note for Unregistered Taxpayers" || invtype == "Advance Adjusted Detail" || invtype == 'TXPA'){
				$('.tot_net_type').attr("colspan","13");
			}else if(invtype == "Exports"){
				$('.tot_net_type').attr("colspan","15");
			}else if(invtype == "Advances"){
				$('.tot_net_type').attr("colspan","10");
			}else if(invtype == "Nil Supplies"){
				$('.tot_net_type').attr("colspan","11");
			}else{
				$('.tot_net_type').attr("colspan","12");
			}
		}
	}else{
		if(invtype == "Nil Supplies"){
			if(enableLedger == true || enableLedger == "true"){
				$('.tot_net_type').attr("colspan","10");
			}else{
				$('.tot_net_type').attr("colspan","9");
			}
		}else if(invtype == "Advance Adjusted Detail"){
			if(enableLedger == true || enableLedger == "true"){
				if((enableSalesCess == true || enableSalesCess == "true")){
					$('.tot_net_type').attr("colspan","13");
				}else{
					$('.tot_net_type').attr("colspan","11");
				}
			}else{
				if((enableSalesCess == true || enableSalesCess == "true")){
					$('.tot_net_type').attr("colspan","12");
				}else{
					$('.tot_net_type').attr("colspan","10");
				}
			}
		}
	}
}
function changeInvoiceLevelCess(){
	var rettype = $('#retType').val();
	var invtype = $('#idInvType').val();
	var enablecess = true;
	if($('#invoiceLevelCess').is(":checked")) {
		$('.cessFlag').show();
		$('#invoiceLevelCess,#invLevelCess').val('Yes');
		enablecess = true;
	}else{
		var cesstype = $('input[name="cessType"]:checked').val();
		if(cesstype == "Taxable Value"){
			$('.cessval').val('0%');
		}else{
			$('.cessval').val(0);
		}
		$('.cessamtval').val(0.00);
		$('.cessFlag').hide();
		$('#invoiceLevelCess,#invLevelCess').val('No');
		enablecess = false;
	}
	
	$("#allinvoice tr").each(function(index){
		var itype = $("#idInvType").val();
		if(itype != "Advance Adjusted Detail"){
			findCessAmount(index+1);
		}else{
		}
		if(retType == 'GSTR1' || retType == 'SalesRegister'){
		}else{
			findITCValue(index+1);
		}
		index++;
	});
	
	
	if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
		if($('#tcsval').is(":checked")){
			supportTCS(invtype,enableDiscount,enableExempted,enablecess,enableLedger);
		}else{
			supportTCS(invtype,enableDiscount,enableExempted,enablecess,enableLedger);
		}
		if($('#invoiceLevelCess').is(":checked") && $('#tcsval').is(":checked")) {
			supportTCS(invtype,enableDiscount,enableExempted,enablecess,enableLedger);
		}
	}else{
		if($('#tcsval').is(":checked") || $('#tdsval').is(":checked")){
			supportTDS(invtype,enablePurDiscount,enablecess,enablePurLedger);
		}else{
			supportTDS(invtype,enablePurDiscount,enablecess,enablePurLedger);
		}
		if($('#invoiceLevelCess').is(":checked") && ($('#tcsval').is(":checked") || $('#tdsval').is(":checked"))) {
			supportTDS(invtype,enablePurDiscount,enablecess,enablePurLedger);
		}
	}
}
function updatevehicleDetails(value){
	if(value == ""){
		$('.vehicledetails').addClass("astrich");
		$('.ewaybillvehicledetails').attr("required","required");
	}else{
		$('.vehicledetails').removeClass("astrich");
		$('.ewaybillvehicledetails').removeAttr("required");
	}
}