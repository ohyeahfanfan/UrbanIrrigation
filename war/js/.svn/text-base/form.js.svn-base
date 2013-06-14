var ENTER_BY_NUM = 0; 
var ENTER_BY_IRR_SYS_TYPE = 1;
var ENTER_BY_ET_CONTROL = 2;
var ENTER_NONE = 2
var ENTER_BY_METRIC = 0;
var ENTER_BY_US = 1;
var RAIN_SENSOR = 2;
var SOIL_MOISTURE = 3;
var ET_CONTROL = 4;
var METRIC_DEFAULT_ARR = {
		'rain_sensor_setting':'1.27',
		'irrigation_amount':'1.27',
		'threshold':'0.7',
		'root_depth':'30',
		'lot_size':'1012'
}
var US_DEFAULT_ARR = {
		'rain_sensor_setting':'0.5',
		'irrigation_amount':'0.5',
		'threshold':'0.7',
		'root_depth':'11.81',
		'lot_size':'0.25'
}

var objSize = function(obj) {
    var size = 0, key;
    for (key in obj) {//for Associative Array only
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
}



/* Start of Unit*/
var unitRadioBtnOnChecked = function(type){
	var METRIC_UNIT = 0; var US_UNIT = 1;
	var metric_array = {
			'root_depth':'cm'
				,'lot_size':"m<sup>2</sup>"
					,'rain_sensor_setting':'cm'
						,'irrigation_amount':'cm'
						//	,'et':'cm'
							//	,'rainfall':'cm'
							};
	var us_array = {
			'root_depth':'inch'
				,'lot_size':"acre"
					,'rain_sensor_setting':'inch'
						,'irrigation_amount':'inch'
							//,'et':'inch'
								//,'rainfall':'inch'
							};
	var units_in_use = {};
	var default_values = {};
	if(type == METRIC_UNIT){
		units_in_use = metric_array;
		default_values = METRIC_DEFAULT_ARR;
	}else if(type == US_UNIT){
		units_in_use = us_array;
		default_values = US_DEFAULT_ARR;
	}
	for(var key in units_in_use){
		var val = units_in_use[key];
		$('#' + key+'_unit').html(val);
	}
	for(var key in default_values){
		$("#"+key).val(default_values[key]);
	}
	
	
}
var loadAccountStatus = function(settings){
	var type = 0;
	var size = objSize(settings);
	if(size > 0){
		//user has entered the settings before.
		type= settings['active'];
		if(type=='Y'){
			$("#active_account").attr("checked",true);
		}else if(type=='N'){
			$("#deactive_account").attr("checked",true);
		}else{
			$("#active_account").attr("checked",true);
		}
	}else{
		
		$("#active_account").attr("checked",true);
	}
	
	
}
//Initialize Units Section
var loadUnitPart = function(settings){
	var type = ENTER_BY_US;
	var size = objSize(settings);
	if(size > 0){
		//user has entered the settings before.
		type= settings['units'];
		if(type==ENTER_BY_METRIC){
			$("#metric_unit").attr("checked",true);
		}else if(type==ENTER_BY_US){
			$("#us_unit").attr("checked",true);
		}
	}else{
		//first time enter, english/us as default unita
		type = ENTER_BY_US;
		$("#us_unit").attr("checked",true);
	}
	
	unitRadioBtnOnChecked(type);
}


//Initialize Soil Section 
var loadSoilPart = function(settings){
	var size = objSize(settings);
	if(size > 0){
		$("#root_depth").val(settings['root_depth']);
		$("#lot_size").val(settings['lot_size']);
		$("#soil_type").val(settings['soil_type']);
	}else{
		var metric_checked = $("#metric_unit").attr("checked");
		var array_in_use = {};
		if(metric_checked){
			array_in_use = METRIC_DEFAULT_ARR;
		}else{
			array_in_use = US_DEFAULT_ARR;
		}
		$("#root_depth").val(array_in_use['root_depth']);
	}
}

//Initialize Irrigation Technology Used Section 
var loadIrrTech = function(settings){
	var size = objSize(settings);
	$('#irrigation_depth_entry').hide();
	if(size > 0){
		//User has entered information before.
		var irr_tech_id = settings['irr_tech']
		$("#irr_tech").val(irr_tech_id);
		if(irr_tech_id == SOIL_MOISTURE){
			$("#threshold").val(settings['threshold']);
			$('#threshold_block').show();
			$('#rain_sensor_setting_block').hide();
			$('#irrigation_depth_entry').show();
		}else if(irr_tech_id == RAIN_SENSOR){
			$('#threshold_block').hide();
			$('#rain_sensor_setting_block').show();
			$("#rain_sensor_setting").val(settings['rain_sensor_setting']);
			$('#irrigation_depth_entry').show();
		}else if(irr_tech_id == ET_CONTROL){
			$('#threshold_block').hide();
			$('#rain_sensor_setting_block').hide();
		}else{
			$('#threshold_block').hide();
			$('#rain_sensor_setting_block').hide();
			$('#irrigation_depth_entry').show();
		}
	}else{
		//This is the first time
		$("#irr_tech").val("");
		$('#threshold_block').hide();
		$('#rain_sensor_setting_block').hide();
	}
}

var IrrTechOnChange = function(settings){
	var type = $("#irr_tech").val();
	var array_in_use;
	var threshold = null;
	var rss = null;
	var setting_size = objSize(settings);
	var metric_checked = $("#metric_unit").attr("checked");
	if(metric_checked){
		array_in_use = METRIC_DEFAULT_ARR;
	}else{
		array_in_use = US_DEFAULT_ARR;
	}
	if(setting_size > 0){
		threshold = settings['threshold'];
		rss = settings['rain_sensor_setting'];
	}
	if(type == SOIL_MOISTURE){
		threshold = (threshold==null||threshold=='-9999'?array_in_use['threshold']:threshold)
		$("#threshold").val(threshold);
		$('#threshold_block').show();
		$('#rain_sensor_setting_block').hide();
		$('#irrigation_depth_entry').show();
	}else if(type == RAIN_SENSOR){
		rss = (rss==null||rss=='-9999'?array_in_use['rain_sensor_setting']:rss)
		$('#threshold_block').hide();
		$('#rain_sensor_setting_block').show();
		$("#rain_sensor_setting").val(rss);
		$('#irrigation_depth_entry').show();
	}else if(type == ET_CONTROL){
		$('#irrigation_depth_entry').hide();
		$('#threshold_block').hide();
		$('#rain_sensor_setting_block').hide();
	}else{
		$('#threshold_block').hide();
		$('#rain_sensor_setting_block').hide();
		$('#irrigation_depth_entry').show();
	}
}

//Initialize Irrigation Schedule Section 
var loadIrrSchedule = function(settings){
	var size = objSize(settings);
	if(size > 0){
		//User has entered information before.
		var zip_code = settings['zip_code'];
		var street_number = settings['street_number'];
		var irr_dates = settings['irr_dates'];
		
		$("#zip_code").val(zip_code);
		var inMiami = settings['in_miami'];
		$("#in_miami").val(inMiami);
		if(inMiami!=null && inMiami==1){
			$('#street_number').val(street_number);
			$('#street_number_block').show();
			$('#irrigation_dates_miami').show();
			if (street_number % 2 == 0) {
				$('#irrigation_dates_miami_days').html("Sun & Thurs");
			} else {
				$('#irrigation_dates_miami_days').html("Wed & Sat");
			}
			$('#irrigation_dates_others').hide();
		}else if(inMiami!=null && inMiami==0){
			var dates_arr = irr_dates.split(";");
			$('#irrigation_dates_others').show();
			var a = $('input[name="irr_dates"]');
			a.each(function(){
				for(var i=0; i< dates_arr.length;i++){
					if($(this).val()==dates_arr[i]){
						$(this).attr("checked",true);;
					}
				}
			})
            $('#irrigation_dates_miami').hide();
            $('#street_number_block').hide();
		}else{
			$('#irrigation_dates_others').hide();
            $('#irrigation_dates_miami').hide();
            $('#street_number_block').hide();
		}
		
	}else{
		//This is the first time
		$('#irrigation_dates_others').hide();
        $('#irrigation_dates_miami').hide();
        $('#street_number_block').hide();
	}
}

var zipCodeOnChange = function(){
    var zip_code = $("#zip_code").val();
    var url = "/urbanirrigationapp/zipcode/"+zip_code+"/";
    $.get(url, function(data) {
    	$('#zip_code_error').html("");
    	 if(data=="no"){
    		 $('#in_miami').val("0")
    		 $('#irrigation_dates_others').show();
             $('#irrigation_dates_miami').hide();
              $('#street_number_block').hide();
    	 }else if(data=="yes"){
    		 $('#in_miami').val("1")
    		 $('#street_number_block').show();
    		 var street_number = $("#street_number").val();
    		 if(street_number!=""){
				$('#irrigation_dates_miami').show();
            if(street_number%2 == 0){
                $('#irrigation_dates_miami_days').html("Sun & Thurs");
             }else{
                $('#irrigation_dates_miami_days').html("Wed & Sat");
           }
            }
    		 
             $('#irrigation_dates_others').hide();
    	 }else{
    		 $('#in_miami').val("9999")
    		 $('#zip_code_error').html("We can not find your zip code.");
    	 }
    });
}

var streetNumOnChange = function(){
	 var street_number = $("#street_number").val();
     $('#irrigation_dates_miami').show();
     if(street_number%2 == 0){
         $('#irrigation_dates_miami_days').html("Sun & Thurs");
      }else{
         $('#irrigation_dates_miami_days').html("Wed & Sat");
    }
}

/* Start of Irrigation Depth per Event*/
var irrDepthMethodRadioBtnOnChecked = function(type,settings){
	var metric_checked = $("#metric_unit").attr("checked");
	if(metric_checked){
		array_in_use = METRIC_DEFAULT_ARR;
	}else{
		array_in_use = US_DEFAULT_ARR;
	}
	var size = objSize(settings);
	if(type == ENTER_BY_NUM){
		var irrigation_amount = null;
		if(size > 0 && settings['irrigation_amount']!='-9999'){
			irrigation_amount = settings['irrigation_amount'];
		 }else{
			 irrigation_amount = array_in_use['irrigation_amount'];
		 }
		$("#irrigation_amount").val(irrigation_amount);
		$("#irrigation_amount_entry").show();
		$("#irrigation_minutes_entry").hide();
		$("#irrigation_system_type_entry").hide();
	}else if(type == ENTER_BY_IRR_SYS_TYPE){
		$("#irrigation_amount_entry").hide();
		$("#irrigation_minutes_entry").show();
		
		$("#irrigation_system_type_entry").show();
	}else{
		$("#irrigation_amount_entry").hide();
		$("#irrigation_minutes_entry").hide();
		$("#irrigation_system_type_entry").hide();
	}
}

var loadIrrDepthPart = function(settings){
	var type;
	var size = objSize(settings);
	if(size > 0){
		//user has entered the settings before.
		type= settings['irrigation_depth_method'];
		if(type==ENTER_BY_NUM){
			$("#enter_irrgation_depth").attr("checked",true);
			
		}else if(type==ENTER_BY_IRR_SYS_TYPE){
			$("#enter_irrigation_system_info").attr("checked",true);
			$("#irrigation_minutes").val(settings['irrigation_minutes']);
			var irr_sys = settings['irrigation_system'];
			var a = $('input[name="irrigation_system"]');
			a.each(function(){
				if($(this).val() == irr_sys){
					$(this).attr("checked",true);
				}
				
			})
		}else{
			//enter by ET controler
		}
	}else{//It is the first time that this user enter the settings.
		type = ENTER_NONE
	}
	
	irrDepthMethodRadioBtnOnChecked(type,settings);
}
//Clear up the input fields, before the submission,
//Set unused input to be ""
var beforeSubmit = function(){
	//1.Irrigation Technology Used Section
	var irr_tech_id = $('#irr_tech').val()
	if (irr_tech_id != RAIN_SENSOR) {
		$("#rain_sensor_setting").val("");
	}
	if(irr_tech_id != SOIL_MOISTURE){
		$("#threshold").val("");
	}
	if(irr_tech_id ==ET_CONTROL){
		//irrigation amount is decided by ET controller system itself
		//$("input[name='irrigation_depth_method']").val(ENTER_BY_ET_CONTROL);
		var a = $('input[name="irrigation_depth_method"]');
		a.each(function(){
			$(this).attr("checked",false);;
		})
	}
	//2. Irrigation Schedule Section 
	var in_miami = $('#in_miami').val();
	if(in_miami == 0){
		$('#street_number').val("");
	}else if(in_miami == 1){
		//get irr_dates unchecked
		var a = $('input[name="irr_dates"]');
		a.each(function(){
			$(this).attr("checked",false);;
		})
	}else{
		//invalid zip should be taken care by validation part
	}
	//Irrigation Amount Section $('input:radio[name=bar]:checked').val();
	var enter_irr_depth_method = $('input:radio[name="irrigation_depth_method"]:checked').val();
	if(enter_irr_depth_method == ENTER_BY_NUM){
		$('#irrigation_minutes').val("");
		var a = $('input[name="irrigation_system"]');
		a.each(function(){
			$(this).attr("checked",false);;
		})
	}else if(enter_irr_depth_method == ENTER_BY_IRR_SYS_TYPE){
		$('#irrigation_amount').val("");
		
	}else if(enter_irr_depth_method == null){//no one selected
		//not working $("input[name='irrigation_depth_method']").val(ENTER_BY_ET_CONTROL);
		$('#irrigation_minutes').val("");
		var a = $('input[name="irrigation_system"]');
		a.each(function(){
			$(this).attr("checked",false);;
		})
		$('#irrigation_amount').val("");
	}
	return true;
	
}
var validateForm = function(){
	$("#setting_form").validate({
		rules: {
        	units:"required", 
        	root_depth:"required number",
        	soil_type:"required",
        	lot_size:"required number",
        	irr_tech:"required",
        	zip_code:{
            	required: true,
        		digits:true,
                minlength: 5,
                maxlength: 5,
                min:32003,
                max:34997
            },
            active:"required"
		// ,irrigation_depth_method:"required"
         },submitHandler: function(form) {
        	 //exec after validation and conditial validations
        	 //form.ajaxForm();
        	 //reset everthing
        	 var flag = beforeSubmit();
        	// form.submit();
        	 if(flag==true){
        	 $.ajax({
        		url: '/urbanirrigationapp/save', 
 			    type : "POST",
 			    data:$("#setting_form").serialize(),
 				dataType:"json",
 				success:function(resp) { 
 			    	var settings = resp.data;
 			    	var logout = settings["logOutURL"];
 					var email = settings["userEmail"]
 					var html = "<div class=\"mainContactForm welcome\">" 
 							+"Thank you for submitting your information!<br /> "
 							+"Shortly you will receive two email messages; a confirmation, then an initial evaluation of your lawn for the previous week. " 
 							+"Then, each Monday we will send you an email with an evaluation for the previous week."
					 		+"<br /><a href = \""+ logout + "\" >Click here</a> to log out. " 
 							+"To revise your information <a href = \"/index.html\" >click here</a>." 
 							+"</div> ";
 					//html += "<a href = \"/urbanirrigationapp/calculate/"+ email+ "/\" >View 7 days Calculation</a><br />";
 					//html += "<a href = \"/index.html\" >Revise Settings</a><br />";
 					$("#after_submission").html(html);
 					$("#loading").hide();
 					$("#after_submission").show();
 					$("#setting_form").hide();
 					$.get("/urbanirrigationapp/send?email="+email);
 			    } 
     		 });
        	 }
        	// $(form).ajaxSubmit(options);
         }
         
     });
    
	//Conditional validations starts from here.
	var checked = $("#metric_unit").attr("checked");
	if(checked){
		$("#lot_size").rules("add", {
			required : true,
			number : true,
			min : 0,
			max : 8093.72
		});
	}else{
		$("#lot_size").rules("add", {
			required : true,
			number : true,
			min : 0,
			max : 2
		});
	}
	if ($('#irr_tech').val() == RAIN_SENSOR) {
		$("#rain_sensor_setting").rules("add", {
			required : true,
			number : true,
			min : 0
		});
	} else if ($('#irr_tech').val() == SOIL_MOISTURE) {
		$("#threshold").rules("add", {
			required : true,
			number : true,
			min : 0
		});
	} 
	if ($('#irr_tech').val() != ET_CONTROL) {
		$("#enter_irrgation_depth").rules("add", {
			required : true,
		});
	}
	if ($('#in_miami').val() == "0") {
		$('#irr_dates').rules("add", {
			required:true
        });
	} else if ($('#in_miami').val() == "1") {
		$("#street_number").rules("add", {
			required:true,
        	digits:true,
            minlength: 1,
            maxlength: 5
		});
	}
	if ($('#enter_irrgation_depth').attr("checked")) {
		$("#irrigation_amount").rules("add", {
			required : true,
			number : true,
			min : 0
		});
		$("#irrigation_amount").rules("add", {
			required : true,
			number : true,
			min : 0
		});
	}
	
	if ($('#enter_irrigation_system_info').attr("checked")) {
		$("#irrigation_system").rules("add", {
			required : true
		});
		$("#irrigation_minutes").rules("add", {
			required : true,
			number : true,
			min : 0
		});
	}
   }

var loadHelpBlubs = function(){
	$('.help_blub').each(function(){
		var contents = {
				"root_depth_label":"Rooting depth refers to the depth of soil in which roots are found. In some places this may be limited by the amount of soil and in other places this may be limited by other factors."
				,"soil_type_label":"Select the dominant soil type for your lawn. If your soil type is not provided, pick the type that has soil water holding properties similar to your soil type.Sand is a common soil type in Florida. Sandy soils are well drained, they feel gritty to the touch, and each grain is can be seen by the naked eye. Sandy loams are soils with sand, silt, and clay but mostly sand. Loam is a soil that contains a balance of sand, silt, and clay. Loam soils are not as well drained as sandy soils. Silt loam has relatively small fractions of sand and clay and mostly silt. Dry silt loam may feel soft and floury. Clay loam is similar to loam but with more clay. Clay loam feels sticky when wet and does not drain well. Clay soils are not common in Florida and drain very poorly. Clay is the finest texture of all soil types."
				,"irrigated_area_label":"The irrigated area represents the area which receives irrigation.If the values below 1.0 must include the 0 before the decimal (e.g., 0.5 will work but .5 will not)."
				,"irr_tech_label":"Different options are provided so that users can simulate the type of system they have or try out a new system. The 'time based scheduler' is similar to an automatic timer with no other features. The 'rain sensor' is the automatic timer with a rain sensor. The user is then allowed to set the rain sensor setting as this is usually an adjustable feature. The 'soil moisture sensor' option simulates an automated timer with a soil moisture sensor which acts as a switching device. The sensor permits irrigation below a set water content threshold in the soil and does not allow irrigation if the soil water content is above the set threshold. The user has the ability to modify the threshold setting. The threshold value is used as a percentage value of soil field capacity. The 'ET controller' option simulates the use of a real-time evapotranspiration (ET) controller. It uses daily values from FAWN to update ET and determine an irrigation amount. For more information on FAWN, see <a href='http://fawn.ifas.ufl.edu' target='_blank'>http://fawn.ifas.ufl.edu</a>."
				,"irr_dates_label":"Select the days your system is schedule to irrigate."
			    ,"miami_dade_label":"The zip code entered is in Miami-Dade County which has irrigation restrictions that limit users to twice a week irrigation events. Even number addresses (e.g., 1030) irrigate on Sunday and Thursday while odd number addresses (e.g., 3751) irrigate on Wednesday and Saturday."
				}
	    var id = $(this).attr('id');
		var help = contents[id];
	    
	    // create the tooltip for the current element
	    $( this ).qtip( {
	    	content: help,
	    	  show: 'mouseover',
	    	  hide: 'mouseout',
	    	  position: {
	    	      corner: {
	    	         target: 'topRight',
	    	         tooltip: 'bottomLeft'
	    	      }
	    	   },
	    		style: {
	    		    width: 500,
	    		    padding: 10,
	    		    //background: '#EEEEEE',
	    		    //color: 'black',
	    		    textAlign: 'center',
	    		    border: {
	    		       width: 7,
	    		       radius: 5
	    		       //,color: '#CCCCCC'
	    		    },
	    		    tip:true,
	    		    
	    			name:'cream'
	    		   }
	    		   
	    } );
	} );
	
}

//function to initialize the page
var init = function(){
	$("#setting_form").hide();
	$("#after_submission").hide();
	$("#loading").show();
	$.ajax({
		url : "/urbanirrigationapp/init",
		type : "GET",
		dataType:"json",
		success : function(resp) {
			var settings = resp.data;
			var redirect = settings["redirect"];
			if(redirect=="yes"){
				var url = settings["url"];
				window.location.replace(url);
				return;
			}else{
				if(objSize(settings)==1){
					settings = {};
				}
			}
			loadAccountStatus(settings);
			loadUnitPart(settings);
			loadSoilPart(settings);
			loadIrrTech(settings);
			loadIrrSchedule(settings);
			loadIrrDepthPart(settings);
			loadHelpBlubs();
			$("#loading").hide();
			$("#after_submission").hide();
			$("#setting_form").show();

		}
	});
	
	
}
 
//Google analytics tracking start here

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-13182472-1']);
_gaq.push(['_trackPageview']);

(function() {
  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();


   



