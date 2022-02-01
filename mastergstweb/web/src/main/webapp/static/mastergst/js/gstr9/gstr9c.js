function addReconTurnover(tableid,tablebodyid){
	var gstr9tablerowcount = $('#'+tableid+'Body tr').length;
	if(gstr9tablerowcount < 10){
		var t6rowcount = gstr9tablerowcount +1;
		var mnth = tablemnths[gstr9tablerowcount];
		$('#'+tableid+'Body').append('<tr id="'+tableid+'reason'+t6rowcount+'"><td class="text-left"><label id="'+tableid+'label'+t6rowcount+'">('+mnth+')Reason '+t6rowcount+'</label> <input type="hidden" name="auditedData.'+tableid+'.rsn['+gstr9tablerowcount+'].number" value="RSN'+gstr9tablerowcount+'" /></td><td class="text-right form-group gst-3b-error" colspan="2"><input type="text" style="width: 100%;" readonly="true" class="form-control tpone-input" id="auditedData_'+tableid+'_rsn['+gstr9tablerowcount+']_desc" name="auditedData.'+tableid+'.rsn['+gstr9tablerowcount+'].desc" placeholder="Reason description" value=""/><div class="help-block with-errors"></div></td><td class="text-right"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteGSTR9CItem(\''+tableid+'reason'+t6rowcount+'\',\''+tablebodyid+'\',\''+tableid+'\')" class="gstr9c_item_delete"></td></tr>');
	}
}
function deleteGSTR9CItem(index,tbodyid,tableid){
	$('#'+index).remove();
		
	if(tableid == 'table6'){
		table6Count--;
	}else if(tableid == 'table8'){
		table8Count--;
	}else if(tableid == 'table10'){
		table10Count--;
	}
	
	$("#"+tbodyid+" tr").each(function(index) {
		 var rowno = (index+1).toString();
		 var rownoo = (index).toString();
		 var rowid = $(this).attr('id');
		 if(rowid != undefined){
				rowid = replaceAt(rowid,12,rowno);
				$(this).attr('id',rowid);
		}
		 $(this).find('input').each (function() {
				//var inputname1 = $(this).attr('class');
				var inputid1 = $(this).attr('id');
				var inputname = $(this).attr('name');
			
				$('.gstr9cinvoceform input[type="hidden"]').each(function() {
					var hiddenvalue = $(this).attr('value');
					hiddenvalue = replaceAt(hiddenvalue,3,rownoo);
					$(this).attr('value',hiddenvalue);
				});
				
				if(inputid1 != undefined){
					if(rownoo == '9'){
						inputid1 = inputid1.replace('10',' ');
					}
					inputid1 = replaceAt(inputid1,23,rownoo);
					$(this).attr('id',inputid1);
					
			}
				if(inputname != undefined){
						if(rownoo == '9'){
							inputname = inputname.replace('10',' ');
						}
						inputname = replaceAt(inputname,23,rownoo);
						$(this).attr('name',inputname);
				}
	
			});
		 $(this).find('label').each (function() {
			 var labelid = $(this).attr('id');
			 var labeltxt = $(this).html();
			 if(labeltxt != undefined){
				 if(labeltxt.length > 11){
					 labeltxt = labeltxt.replace('10',' ')
					 labeltxt  = replaceAt(labeltxt,10,rowno);
					 labeltxt  = replaceAt(labeltxt,1,tablemnths[rowno-1]);
				 }else{
					 labeltxt  = replaceAt(labeltxt,10,rowno);
					 labeltxt  = replaceAt(labeltxt,1,tablemnths[rowno-1]);
				 }
				 
				 $(this).html(labeltxt);
			 }
			 if(labelid != undefined){
				 labelid = replaceAt(labelid,11,rowno);
				$(this).attr('id',labelid);
			}
		 });
		 $(this).find('img').each (function() {
				var aname = $(this).attr('id');
				if(aname != undefined){
					
				aname = aname+rowno;
				$(this).attr('id',aname);
				}
				var det = $(this).attr('class');
				if(det != 'gstr9c_item_delete'){
				}else{
					var abcd = $(this).attr('onclick');
			   	    abcd = replaceAt(abcd,30,rowno);
			   	    $(this).attr('onclick',abcd);
				}
			});
	});
	$('form[name="gstr9cinvoceform"]').validator('update');
}