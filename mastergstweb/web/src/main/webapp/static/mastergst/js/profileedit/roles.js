$(function () {
$('.Invoices-Purchase-View').change(function() {
			if ($(this).is(":checked")) {$('.Invoices-Purchase-Add').prop('checked', true);$('.Invoices-Purchase-Edit').prop('checked', true);$('.Invoices-Purchase-Delete').prop('checked', true);
	        } else { $('.Invoices-Purchase-Add').prop('checked', false);$('.Invoices-Purchase-Edit').prop('checked', false);$('.Invoices-Purchase-Delete').prop('checked', false);
	        }
		});
		$('.Invoices-Sales-View').change(function() {
			if ($(this).is(":checked")) {  $('.Invoices-Sales-Add').prop('checked', true);$('.Invoices-Sales-Edit').prop('checked', true);$('.Invoices-Sales-Delete').prop('checked', true);
	        } else {  $('.Invoices-Sales-Add').prop('checked', false);$('.Invoices-Sales-Edit').prop('checked', false);$('.Invoices-Sales-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Suppliers-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Suppliers-Add').prop('checked', true);$('.Settings-Suppliers-Edit').prop('checked', true);$('.Settings-Suppliers-Delete').prop('checked', true);
	        } else { $('.Settings-Suppliers-Add').prop('checked', false);$('.Settings-Suppliers-Edit').prop('checked', false);$('.Settings-Suppliers-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Customers-View').change(function() {
			if ($(this).is(":checked")) {$('.Settings-Customers-Add').prop('checked', true);$('.Settings-Customers-Edit').prop('checked', true);$('.Settings-Customers-Delete').prop('checked', true);
	        } else { $('.Settings-Customers-Add').prop('checked', false);$('.Settings-Customers-Edit').prop('checked', false);$('.Settings-Customers-Delete').prop('checked', false);
	        }
		});
		$('.Print-View').change(function() {
			if ($(this).is(":checked")) {$('.Print-Add').prop('checked', true);$('.Print-Edit').prop('checked', true);$('.Print-Delete').prop('checked', true);
	        } else { $('.Print-Add').prop('checked', false);$('.Print-Edit').prop('checked', false);$('.Print-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Branches-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Branches-Add').prop('checked', true);$('.Settings-Branches-Edit').prop('checked', true);$('.Settings-Branches-Delete').prop('checked', true);
	        } else { $('.Settings-Branches-Add').prop('checked', false);$('.Settings-Branches-Edit').prop('checked', false);$('.Settings-Branches-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Roles-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Roles-Add').prop('checked', true);$('.Settings-Roles-Edit').prop('checked', true);$('.Settings-Roles-Delete').prop('checked', true);
	        } else {$('.Settings-Roles-Add').prop('checked', false);$('.Settings-Roles-Edit').prop('checked', false);$('.Settings-Roles-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Users-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Users-Add').prop('checked', true);$('.Settings-Users-Edit').prop('checked', true);$('.Settings-Users-Delete').prop('checked', true);
	        } else { $('.Settings-Users-Add').prop('checked', false);$('.Settings-Users-Edit').prop('checked', false);$('.Settings-Users-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Profile-View').change(function() {
			if ($(this).is(":checked")) {$('.Settings-Profile-Add').prop('checked', true);$('.Settings-Profile-Edit').prop('checked', true);$('.Settings-Profile-Delete').prop('checked', true);
	        } else {$('.Settings-Profile-Add').prop('checked', false);$('.Settings-Profile-Edit').prop('checked', false);$('.Settings-Profile-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Bank.Information-View').change(function() {
			if ($(this).is(":checked")) {$('.Settings-Bank.Information-Add').prop('checked', true);$('.Settings-Bank.Information-Edit').prop('checked', true);$('.Settings-Bank.Information-Delete').prop('checked', true);
	        } else {$('.Settings-Bank.Information-Add').prop('checked', false);$('.Settings-Bank.Information-Edit').prop('checked', false);$('.Settings-Bank.Information-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Verticals-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Verticals-Add').prop('checked', true);$('.Settings-Verticals-Edit').prop('checked', true);$('.Settings-Verticals-Delete').prop('checked', true);
	        } else {$('.Settings-Verticals-Add').prop('checked', false);$('.Settings-Verticals-Edit').prop('checked', false);$('.Settings-Verticals-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Configurations-View').change(function() {
			if ($(this).is(":checked")) {$('.configurationTabs').show(); $('.Settings-Configurations-Add').prop('checked', true);$('.Settings-Configurations-Edit').prop('checked', true);$('.Settings-Configurations-Delete').prop('checked', true);
	        } else {$('.configurationTabs').hide(); $('.Settings-Configurations-Add').prop('checked', false);$('.Settings-Configurations-Edit').prop('checked', false);$('.Settings-Configurations-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Configurations-Add,.Settings-Configurations-Edit,.Settings-Configurations-Delete').change(function() {
			if ($(this).is(":checked")) {
				$('.configurationTabs').show();
			}else{
				if($('.Settings-Configurations-View').is(":checked")){
					$('.configurationTabs').show();
				}
			}
		});
		$('.Settings-Accounting-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Accounting-Add').prop('checked', true);$('.Settings-Accounting-Edit').prop('checked', true);$('.Settings-Accounting-Delete').prop('checked', true);
	        } else {$('.Settings-Accounting-Add').prop('checked', false);$('.Settings-Accounting-Edit').prop('checked', false);$('.Settings-Accounting-Delete').prop('checked', false);
	        }
		});
		$('.Settings-E.Commerce.Operator-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-E.Commerce.Operator-Add').prop('checked', true);$('.Settings-E.Commerce.Operator-Edit').prop('checked', true);$('.Settings-E.Commerce.Operator-Delete').prop('checked', true);
	        } else {$('.Settings-E.Commerce.Operator-Add').prop('checked', false);$('.Settings-E.Commerce.Operator-Edit').prop('checked', false);$('.Settings-E.Commerce.Operator-Delete').prop('checked', false);
	        }
		});
		$('.Settings-Items-View').change(function() {
			if ($(this).is(":checked")) { $('.Settings-Items-Add').prop('checked', true);$('.Settings-Items-Edit').prop('checked', true);$('.Settings-Items-Delete').prop('checked', true);
	        } else {$('.Settings-Items-Add').prop('checked', false);$('.Settings-Items-Edit').prop('checked', false);$('.Settings-Items-Delete').prop('checked', false);
	        }
		});
		$('.Invoices-Purchase').change(function() {
		if ($(".Invoices-Purchase-Edit:checked,.Invoices-Purchase-Delete:checked,.Invoices-Purchase-Add:checked").length == 0) { 
		}else{$('.Invoices-Purchase-View').prop('checked', true);}
		});
		$('.Invoices-Sales').change(function() {
		if ($(".Invoices-Sales-Edit:checked,.Invoices-Sales-Delete:checked,.Invoices-Sales-Add:checked").length == 0) { 
		}else{$('.Invoices-Sales-View').prop('checked', true);}
		});
		$('.Settings-Suppliers').change(function() {
		if ($(".Settings-Suppliers-Edit:checked,.Settings-Suppliers-Delete:checked,.Settings-Suppliers-Add:checked").length == 0) { 
		}else{$('.Settings-Suppliers-View').prop('checked', true);}
		});
		$('.Settings-Customers').change(function() {
		 if ($(".Settings-Customers-Edit:checked,.Settings-Customers-Delete:checked,.Settings-Customers-Add:checked").length == 0) { 
		  }else{$('.Settings-Customers-View').prop('checked', true);}
		});
		$('.Settings-Invoice').change(function() {
		 if ($(".Print-Edit:checked,.Print-Delete:checked,.Print-Add:checked").length == 0) { 
		  }else{$('.Print-View').prop('checked', true);}
		});
		$('.Settings-Branches').change(function() {
		 if ($(".Settings-Branches-Edit:checked,.Settings-Branches-Delete:checked,.Settings-Branches-Add:checked").length == 0) { 
		  }else{$('.Settings-Branches-View').prop('checked', true);}
		});
		$('.Settings-Items').change(function() {
		 if ($(".Settings-Items-Edit:checked,.Settings-Items-Delete:checked,.Settings-Items-Add:checked").length == 0) { 
		  }else{$('.Settings-Items-View').prop('checked', true);}
		});
		$('.Settings-Roles').change(function() {
		 if ($(".Settings-Roles-Edit:checked,.Settings-Roles-Delete:checked,.Settings-Roles-Add:checked").length == 0) { 
		  }else{$('.Settings-Roles-View').prop('checked', true);}
		});
		$('.Settings-Users').change(function() {
		 if ($(".Settings-Users-Edit:checked,.Settings-Users-Delete:checked,.Settings-Users-Add:checked").length == 0) { 
		  }else{$('.Settings-Users-View').prop('checked', true);}
		});
		$('.Settings-Verticals').change(function() {
		 if ($(".Settings-Verticals-Edit:checked,.Settings-Verticals-Delete:checked,.Settings-Verticals-Add:checked").length == 0) { 
		  }else{$('.Settings-Verticals-View').prop('checked', true);}
		});
		$('.Settings-Configurations').change(function() {
			 if ($(".Settings-Configurations-Edit:checked,.Settings-Configurations-Delete:checked,.Settings-Configurations-Add:checked").length == 0) { 
			  }else{$('.Settings-Configurations-View').prop('checked', true);}
			});
		$('.Settings-Profile').change(function() {
		 if ($(".Settings-Profile-Edit:checked,.Settings-Profile-Delete:checked,.Settings-Profile-Add:checked").length == 0){ 
		  }else{$('.Settings-Profile-View').prop('checked', true);}
		});
		$('.Settings-Bank.Information').change(function() {
		 if ($(".Settings-Bank.Information-Edit:checked,.Settings-Bank.Information-Delete:checked,.Settings-Bank.Information-Add:checked").length == 0){ 
		  }else{$('.Settings-Bank.Information-View').prop('checked', true);}
		});
		$('.Settings-Accounting').change(function() {
			 if ($(".Settings-Accounting-Edit:checked,.Settings-Accounting-Delete:checked,.Settings-Accounting-Add:checked").length == 0){ 
			  }else{$('.Settings-Accounting-View').prop('checked', true);}
			});
		$('.Settings-E.Commerce').change(function() {
			 if ($(".Settings-E.Commerce-Edit:checked,.Settings-E.Commerce-Delete:checked,.Settings-E.Commerce-Add:checked").length == 0){ 
			  }else{$('.Settings-E.Commerce-View').prop('checked', true);}
			});
		$('#Acknowledgementpermission0').change(function() {
			 if ($("#Acknowledgementpermission0:checked").length == 0){ 
			  }else{$('#Acknowledgementpermission1').prop('checked', false);}
		});
		$('#Acknowledgementpermission1').change(function() {
			 if ($("#Acknowledgementpermission1:checked").length == 0){ 
			  }else{$('#Acknowledgementpermission0').prop('checked', false);}
		});
		$('.Invoices-Purchase-View, .Invoices-Sales-View').hover(function(){
			$(this).attr('title', 'If you off view you can\'t able to add ,edit and delete the invoices');
		});
});