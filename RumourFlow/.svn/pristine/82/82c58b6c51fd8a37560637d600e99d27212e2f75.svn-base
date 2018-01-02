jQuery.noConflict();
//server

var server = "http://anhdang.research.cs.dal.ca:8080";
var currentMode = "TITLE";

//color scheme picker from mbostock
var brewer = d3.scale.quantize().domain([ 0, 8 ]).range(colorbrewer.YlGnBu[8]);

//number of submissions
var submission_count = 20;
var selectionVis = 1; //1: stream , 2: centrality
var selectionStream = 1;
var showSecondView = false;
var selectedSubmission = undefined;

//edge bundle
var ebundle = false;

//sankey graph
var sankey = false;

//color
var colorKey = undefined;

d3.select("#colors").selectAll(".palette").data(d3.entries(colorbrewer))
		.enter().append("span").attr("class", "palette").attr("title",
				function(d) {
					return d.key;
				}).on(
				"click",
				function(d) {
					brewer = d3.scale.quantize().domain([ 0, selectedRumours.length ]).range(
							colorbrewer[d.key][selectedRumours.length]);
					colorKey = d.key;
					d3.select("#rumourNetwork").style("opacity", 1);
					d3.select("#colors").classed("hidden", true);
					var svg = d3.select("#thesvg");
					svg.selectAll(".streamPath").style("fill", function(d, i) {
						return brewer(d.values[i].index);
					})
					// redraw legend
					var legend = svg.selectAll("rect").style("fill",
							function(d, i) {
								return brewer(i);
							}).style("stroke", "grey");
					//release rumourNetwork
					d3.select("#rumourNetwork").classed("noselect", false);
				}).selectAll(".swatch").data(
				function(d) {
					return d.value[d3.keys(d.value).map(Number).sort(
							d3.descending)[0]];
				}).enter().append("span").attr("class", "swatch").style(
				"background-color", function(d) {
					return d;
				});

d3.select("#colors").classed("hidden", true);
d3.select("#streamSelection").classed("hidden", true);
d3.select("#userSelection").classed("hidden", true);
d3.select("#dataset").classed("hidden", true);
d3.select("#commentSelection").classed("hidden", true);
d3.select("#edgeBundle").classed("hidden", true);

// rumour variables

// Highlight a rumour in the graph. It is a closure within the d3.json() call.
var selectRumour = undefined;

var mode = "exploration";

// Change status of a panel from visible to hidden or viceversa
var toggleDiv = undefined;

// Clear all help boxes and select a rumour in network and in rumour details
// panel
var clearAndSelect = undefined;

// rumour list
var rumourData = undefined;

//get collected rumour list	
var rumourList = [];
var selectedRumours = [];
var selectedRumourLinks = [];
var selectedRumourDebunks = [];
var currentIndex = undefined;

//

var rumourDataList = [];

var currentRumour = undefined;
var currentRumourName = undefined;

// The call to set a zoom value -- currently unused
// (zoom is set via standard mouse-based zooming)
var zoomCall = undefined;

// submission data
var submisisonData = undefined;

// centrality type
var centralityType = undefined;

// stream svg
jQuery("#toggleBundle").hide();

var WIDTH = 930, HEIGHT = 400, SHOW_THRESHOLD = 3;

var force = d3.layout.force().charge(-320).size([ WIDTH, HEIGHT ])
		.linkStrength(function(d, idx) {
			return d.weight;
		});



var rootURL = encodeURI(server
		+ "/RumourFlow/rest/RedditData/rumour/keyword");
$.ajax({
	type : 'GET',
	url : rootURL,
	dataType : "json",
	success : function(data, err) {
		data.forEach(function(row) {
			rumourList.push(row.keyword);
			selectedRumours.push(row.keyword);
			selectedRumourLinks.push(row.link);
			selectedRumourDebunks.push(row.debunk);
		});
		showStreamGraph();
		showTopicCloud();
	},
	error : function(jqXHR, textStatus, errorThrown) {
		alert('Collecting rumour: ' + textStatus);
	}
});

function getSubmissionWithThreshold(mode, threshold) {
	getSubmissionsAboutRumour(rumourData, threshold);
}

function setMode(aMode) {
	toggleDiv('rumourInfo', 'off');
	mode = aMode;
}

$(document).live('pageinit',function(e,data){
    $('#selectOption').selectmenu('refresh'); 
});

jQuery(document).ready(
		function() {
			jQuery(".threshold").on("change", function() {
				getSubmissionsAboutRumour(submisisonData,parseFloat(this.value));								
			});
			
			jQuery(".tthreshold").on("change", function() {
				getSankeyGraph(currentRumourName, parseFloat(this.value));								
			});
			
			jQuery(".thresholdUserCount").on("change", function() {
				getSankeyUserGraph(currentRumourName, parseFloat(this.value));	
			});
			
			jQuery("#divUser").hide();
			jQuery("#divThres").hide();
			jQuery("#divUserCount").hide();
			jQuery("#btnForward").hide();
			jQuery("#btnBack").hide();
			jQuery("#btnRun").hide();

			jQuery(".cThreshold").on("change", function() {
				if (commentData) {
					displaySubGraph(commentData, parseFloat(this.value));
				}
			});
			
			jQuery(".eThreshold").on("change", function() {
				if (ebundle) {
					var svg = d3.select("#thesvg");
					svg.selectAll("path").style('stroke-opacity', parseFloat(this.value));
				}
			});

			jQuery(".secondPanel").prop('disabled', true);

			jQuery(".threshold").val(0.0);
			jQuery(".cThreshold").val(0.0);
			jQuery(".eThreshold").val(0.1);

			// Register listeners
			jQuery('#btnSearchUser').click(
					function() {
						user = jQuery('#searchUser').val();
						displaySubGraph(commentData,
								parseFloat(jQuery(".cThreshold").val()));
						jQuery('#searchUser').val("");
						user = "";
					});
			
			jQuery("#search-user").bind( "change", function(event, ui) {
					user = jQuery('#search-user').val();
					displaySubGraph(commentData,
							parseFloat(jQuery(".cThreshold").val()));
					jQuery('#search-user').val("");
				});
		
			jQuery("#search-top-user").bind( "change", function(event, ui) {
				user = jQuery('#search-top-user').val();
				searchUserMainView(user);
				jQuery('#search-top-user').val("");
			});
			
			jQuery("#btnReset").bind( "click", function(event, ui) {
				if (jQuery("#btnReset").val() == 'Reset'){
					selectedUsers=[];
					d3.selectAll(".clink").style("opacity", function(d,i){
						return "1";
					});
						
					//hide all other nodes
					d3.selectAll(".nodes").style("opacity", function(d,i){
						return "1";
					});
					
					d3.selectAll('.txt')
			        .text(function(d) {
		            	return "";
			        });
					showUserGraph();
				} else if (jQuery("#btnReset").val() == 'Semantic Flow'){
					getSankeyGraph(currentRumourName,0);
				} else if (jQuery("#btnReset").val() == 'User Flow'){
					getSankeyUserGraph(currentRumourName,1);
				}
				
				//d3.event.stopPropagation();
			});
			
			jQuery( document ).bind( 'mobileinit', function(){
				  $.mobile.loader.prototype.options.text = "loading";
				  $.mobile.loader.prototype.options.textVisible = false;
				  $.mobile.loader.prototype.options.theme = "a";
				  $.mobile.loader.prototype.options.html = "";
				});

			function blinker() {
				jQuery('.blink').fadeOut(500);
				jQuery('.blink').fadeIn(500);
			}
			setInterval(blinker, 1000);
						
			jQuery("input[name=modeComment]").click(
					function() {
						modeSearch = this.value;
						if (this.value == "modeEvolution") {
							jQuery("#btnForward").show();
							jQuery("#btnBack").show();
							jQuery("#btnRun").show();
						} else {
							jQuery("#btnForward").hide();
							jQuery("#btnBack").hide();
							jQuery("#btnRun").hide();
						}
						displaySubGraph(commentData,
								parseFloat(jQuery(".cThreshold").val()));
					});

			jQuery("button[name=search-btn]").click(function() {
				init(jQuery("#searchText").val());
			});

			jQuery('#dsOption').on("pagehide", function() {
				    alert("Closed");
				});
	});

function showStream(){
	selectionVis = 1;
	d3.select("#edgeBundle").classed("hidden", true);
	svg = undefined;
	jQuery('#centrality').hide();
	jQuery('#streamg').show();
	jQuery("#toggleBundle").hide();
	showStreamGraph();	
}

function showUsers(){
	selectionVis = 2;
	d3.select("#edgeBundle").classed("hidden", false);
	jQuery('#centrality').show();
	jQuery('#streamg').hide();
	centralityType = 0;
	svg = undefined;
	if (!currentRumourName){	
		currentRumourName = selectedRumours[0];
		currentIndex = 0;
	}
	
	getCentralityGraph(currentRumourName, 0);	
	jQuery("#toggleBundle").show();
	currentRumour = undefined;
}

function showGraph(){
	selectionVis = 3;
	d3.select("#edgeBundle").classed("hidden", true);
	if (!currentRumourName){	
		currentRumourName = selectedRumours[0];		
	}
	init(currentRumourName);
}

function getSubmissions(mode) {
	var threshold = jQuery(".threshold").val();
	currentMode = mode;
	var rootURL = server
			+ "/RumourFlow/rest/RedditData/search/slider/" + mode
			+ "/" + threshold;
	jQuery.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			getSubmissionsAboutRumour(data, threshold);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			//alert('addWine error: ' + textStatus);
		}
	});
}

function convertDate(time) {
	var date = new Date(time * 1000);
	var hours = date.getHours();
	var minutes = "0" + date.getMinutes();
	var seconds = "0" + date.getSeconds();
	return date;
}

function getStreamOption(selected){
	d3.select("#streamSelection").classed("hidden", true);
	d3.select("#rumourNetwork").style("opacity", 1);
	var value = selected.options[selected.selectedIndex].value;
	if (value == "rumourShowUsers") {					
		//show users
		if (currentRumour){
			var rumour = d3.selectAll(".seriesPointsUser").filter("." + currentRumour);
			var previous_user = "";
			rumour.selectAll("text").each(function(d,i){
				d3.select(this).text(
						function(d1,i1){
							if (d1.user_name == previous_user){
								return "";
							}else{
								previous_user = d.user_name;
								return d1.user_name;
							}
					});
			});
		}else{
			d3.selectAll(".seriesPointsUser").each(function(){
				var previous_user = "";
				var rumour = d3.select(this);
				rumour.selectAll("text").each(function(d,i){
					d3.select(this).text(
							function(d1,i1){
								if (d1.user_name == previous_user){
									return "";
								}else{
									previous_user = d.user_name;
									return d1.user_name;
								}
						});
				});
			});						
		}
		
		selectionStream = 2;
		showTopicCloud();
	} else if (value == "rumourTopics") {
		if (currentRumour){
			var rumour = d3.selectAll(".seriesPointsUser").filter("." + currentRumour);
			rumour.selectAll("text").each(function(d,i){
				d3.select(this).text(
						function(d1,i1){
								return d1.topic;
					});
			});
		}else{
			d3.selectAll(".seriesPointsUser").each(function(){
				var rumour = d3.select(this);
				rumour.selectAll("text").each(function(d,i){
					d3.select(this).text(
							function(d1,i1){
									return d1.topic;
						});
				});
			});
			
		}
		
		
		selectionStream = 3;
		showTopicCloud();
	} else if (value == "hideAll") {		
		selectionStream = 1;
		showStreamGraph();
		selectedSubmission = undefined;
		currentRumour = undefined;
		currentRumourName = undefined;
		jQuery('.popover').each(function() {
			jQuery(this).remove();
		});
	}
}

function getUserOption(selected){
	d3.select("#userSelection").classed("hidden", true);
	d3.select("#rumourNetwork").style("opacity", 1);
	var value = selected.options[selected.selectedIndex].value;
	if (value == "rumourUserCloseness") {
		jQuery('#centrality').show();
		jQuery('#streamg').hide();
		jQuery("#toggleBundle").show();
		centralityType = 1;
		getCentralityGraph(currentRumourName, 1);
	} else if (value == "rumourUserBetweeness") {
		jQuery('#centrality').show();
		jQuery('#streamg').hide();
		jQuery("#toggleBundle").show();
		centralityType = 0;
		getCentralityGraph(currentRumourName, 0);
	} else if (value == "rumourUserCommunity") {
		jQuery('#centrality').show();
		jQuery('#streamg').hide();
		jQuery("#toggleBundle").show();
		centralityType = 2;
		getCentralityGraph(currentRumourName, 2);
	}	
}

function getCommentOption(selected){
	d3.select("#commentSelection").classed("hidden", true);
	d3.select("#details").style("opacity", 1);
	modeSearch = selected.options[selected.selectedIndex].value;
	displaySubGraph(commentData, 0.0)	
}

function getDatasetOption(selected){
	jQuery( "#dsOption" ).selectmenu( "disable" );
	jQuery.mobile.loading( 'show', {
		text: 'Waiting',
		textVisible: true,
		theme: 'z',
		html: ""
	});
	
	d3.select("#dataset").classed("hidden", true);
	d3.select("#rumourNetwork").style("opacity", 1);
	//clear legend selection
	
	selectedRumours = [];
	jQuery('#dsOption > option:selected').each(function() {	    
	    selectedRumours.push(jQuery(this).val());
	});
	if (!colorKey)
		colorKey = 'YlGnBu';
	
	var colorSize = selectedRumours.length;
	if (colorSize <= 2){
		colorSize = 3;
	}
	brewer = d3.scale.quantize().domain([ 0, colorSize ]).range(colorbrewer[colorKey][colorSize]);
			
	showTopicCloud();
	showStreamGraph();	
}

function showDatasetOption(){
	d3.select("#dataset").classed("hidden", false);
	d3.select("#rumourNetwork").style("opacity", 0.3);
	//remove all strokes
	currentRumour = undefined;
	currentRumourName = undefined;
	d3.selectAll("rect")
    .attr("stroke", 1);
}


function closeSelect(selected){
	d3.select("#streamSelection").classed("hidden",true);
	d3.select("#userSelection").classed("hidden",true);
	d3.select("#dataset").classed("hidden",true);
	d3.select("#commentSelection").classed("hidden",true);
	d3.select("#rumourNetwork").style("opacity",1);
	d3.select("#details").style("opacity",1);
}

function searchUserMainView(user){
	if (selectionVis == 1){
		d3.selectAll(".users")
		.text(function(d,i){
				return "";
		});
		
		d3.selectAll(".users").filter("." + user)
		.text(function(d,i){				
				return d.user_name;
		});
	}else if (selectionVis == 2){
		//hide all other links
		d3.selectAll(".clink").style("opacity", function(d,i){
			return "0.1";
		});
			
		//hide all other nodes
		d3.selectAll(".nodes").style("opacity", function(d,i){
			return "0.1";
		});
		
		d3.select('.txt' + user)
        .style("text-anchor", "middle").attr("y", 3)
		.style(
		"stroke", "brown").style("font-size", "12px")
        .text(function(d) {
	        	selectedUsers.push(d);
				showUserGraph();
            	return d.id;
        });
        //node
	    d3.selectAll(".node" + user)
		.style("opacity", "1");
	    
	}
}
