// -------------------------------------------------------------------
// A number of forward declarations. These variables need to be defined since 
// they are attached to static code in HTML. But we cannot define them yet
// since they need D3.js stuff. So we put placeholders.


// Highlight a rumour in the graph. It is a closure within the d3.json() call.
var selectNode = undefined;

// Change status of a panel from visible to hidden or viceversa
var toggleDivSub = undefined;

// Clear all help boxes and select a rumour in network and in rumour details panel
var clearAndSelectSub = undefined;


// The call to set a zoom value -- currently unused
// (zoom is set via standard mouse-based zooming)
var zoomCallSub = undefined;

var modeSearch	 = 'modeGeneral';

var user = undefined;

var commentData = undefined;

//d3 brewer
var colorScale = d3.scale.quantize()
.domain([0.0, 1.0])
.range(colorbrewer.YlGnBu[5]);

// Some constants
var WIDTH_SUB = 800,
    HEIGHT_SUB = 500,
    SHOW_THRESHOLD_SUB = 2;

//The D3.js forceSub-directed layout
var forceSub = d3.layout.force()
  .charge(-320)
  .size( [WIDTH_SUB, HEIGHT_SUB] )
  .linkStrength( function(d,idx) { return d.weight; } );

// -------------------------------------------------------------------
function initSubGraph(node){
	jQuery("#divUser").show();
	jQuery("#divThres").hide();
	jQuery("#second").text("Comment Tree");
  var rootURLSub = server + "/RumourFlow/rest/RedditData/search/submission/" + node.redditID;
  $.ajax({
		type: 'GET',
		url: rootURLSub,
		dataType: "json",
		success: function(data, err){
			displaySubGraph(data, 0.0);
			commentTree = data;
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('addWine error: ' + textStatus);
		}
	});
}

//is Node1 a child of Node2
function isChild(node1, node2){
	if (node1 == null || !node1.parentID){
		return false;
	}
	if (node1.parentID.redditID == node2.redditID){
		return true;
	}
	return isChild(node1.parentID, node2);
}

//hasLink
function hasNode(node1, listNodes){
	for (var k=0;k<listNodes.length;k++){
		if (node1.id == listNodes[k].id){
			return true;
		}
	}
	return false;
}

function initSubGraphWithID(redditID){
	  var rootURLSub = server + "/RumourFlow/rest/RedditData/search/submission/" + redditID;
	  $.ajax({
			type: 'GET',
			url: rootURLSub,
			dataType: "json",
			success: function(data, err){
				displaySubGraph(data, 0.0);
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert('addWine error: ' + textStatus);
			}
		});
	}


// Do the stuff -- to be called after D3.js has loaded
function displaySubGraph(data, threshold) {
	commentData = data;
	d3.select("#details").html("");
	d3.select("#divUserCount").classed("hidden",true);
	d3.select("#divThres").classed("hidden",true);
	
  // Variables keeping graph state
  var activeRumourSub = undefined;
  var currentOffsetSub = { x : 0, y : 0 };
  var currentZoomSub = 1.0;

  // The D3.js scales
  var xScaleSub = d3.scale.linear()
    .domain([0, WIDTH_SUB])
    .range([0, WIDTH_SUB]);
  var yScaleSub = d3.scale.linear()
    .domain([0, HEIGHT_SUB])
    .range([0, HEIGHT_SUB]);
  var zoomScaleSub = d3.scale.linear()
    .domain([0,10])
    .range([0,10])
    .clamp(true);

/* .......................................................................... */


  // Add to the page the SVG element that will contain the rumour network
  var svgSub = d3.select("#details").append("svg:svg")
    .attr('xmlns','http://www.w3.org/2000/svg')
    .attr("id","graphSub")
    .attr("viewBox", "0 0 " + WIDTH + " " + HEIGHT)
    .attr("preserveAspectRatio", "xMinYMin meet")
    .on("contextmenu",function(){
    	  d3.select("#details").style("opacity",0.4);
		  //show selections
		  d3.select("#commentSelection").classed("hidden", false);
		  d3.event.preventDefault();
      })

  // Rumour panel: the div into which the rumour details info will be written
  rumourInfoDivSub = d3.select("#rumourInfo");

  /* ....................................................................... */

  // Get the current size & offset of the browser's viewport window
  function getViewportSizeSub( w ) {
    var w = w || window;
    if( w.innerWIDTH != null ) 
      return { w: w.innerWIDTH, 
	       h: w.innerHEIGHT,
	       x : w.pageXOffset,
	       y : w.pageYOffset };
    var d = w.document;
    if( document.compatMode == "CSS1Compat" )
      return { w: d.documentElement.clientWIDTH,
	       h: d.documentElement.clientHEIGHT,
	       x: d.documentElement.scrollLeft,
	       y: d.documentElement.scrollTop };
    else
      return { w: d.body.clientWIDTH, 
	       h: d.body.clientHEIGHTB,
	       x: d.body.scrollLeft,
	       y: d.body.scrollTop};
  }


  /* Change status of a panel from visible to hidden or viceversa
     id: identifier of the div to change
     status: 'on' or 'off'. If not specified, the panel will toggle status
  */
  toggleDivSub = function( id, status ) {
    d = d3.select('div#'+id);
    if( status === undefined )
      status = d.attr('class') == 'panel_on' ? 'off' : 'on';
    d.attr( 'class', 'panel_' + status );
    return false;
  }


  /* Clear all help boxes and select a rumour in the network and in the 
     rumour details panel
  */
  clearAndSelectSub = function (id) {
    toggleDivSub('faq','off'); 
    toggleDivSub('help','off'); 
    selectNode(id,true);	// we use here the selectNode() closure
  }

  // *************************************************************************

    // Declare the variables pointing to the node & link arrays
    var tempNodes = data.nodes;
    var nodeArraySub = [];
    var linkArraySub = [];
    //filter threshold
    
    //hack fix
    for (var i = 0; i < data.nodes.length;i++){
    	if (data.nodes[i].id > data.nodes.length){
    		data.nodes[i].id = 0;
    		data.nodes[i].index = 0;
    	}
    }
    
    for (var i = 0; i < data.links.length;i++){
    	if (data.links[i].source > data.nodes.length){
    		data.links[i].source = 0;
    	}
    	
    	if (data.links[i].target > data.nodes.length){
    		data.links[i].target = 0;
    	}
    }
    
    //end ifx
    if (threshold == 0.0){
    	nodeArraySub = data.nodes;
    	linkArraySub = data.links;
    }else{
    	var tempNodeArraySub = [];
    	var tempLinkArraySub = data.links;
    	for (var i = 0; i < tempNodes.length; i++){
    		if (tempNodes[i].id==0){
    			continue;
    		}
        	if (tempNodes[i].simScore < threshold){
        		tempNodeArraySub.push(tempNodes[i]);
        		
        		//push all childen
        		for (var j = 0; j< tempNodes.length;j++){
        			if (tempNodes[j].id==0 || tempNodes[j].id==tempNodes[i].id){
        				continue;
        			}
        			if (isChild(tempNodes[j],tempNodes[i])){
        				tempNodeArraySub.push(tempNodes[j]);
        			}
        		}
        	}
        }
    	//filter nodeArraySub
        
    	for (var i = 0; i < tempNodes.length; i++ ){
    		if (!hasNode(tempNodes[i],tempNodeArraySub)){
    			nodeArraySub.push(tempNodes[i]);
    		}
    	}
       
        //filter links
        for (var i = 0; i < tempLinkArraySub.length; i++){
        	if (hasNode(tempLinkArraySub[i].source,nodeArraySub) && hasNode(tempLinkArraySub[i].target,nodeArraySub)){
        		linkArraySub.push(tempLinkArraySub[i]);
        	}
        }
    }
    
    
    minLinkWeight = 
      Math.min.apply( null, linkArraySub.map( function(n) {return n.weight;} ) );
    maxLinkWeight = 
      Math.max.apply( null, linkArraySub.map( function(n) {return n.weight;} ) );

    // Add the node & link arrays to the layout, and start it
    forceSub
      .nodes(nodeArraySub)
      .links(linkArraySub)
      .start();


    // A couple of scales for node radius & edge WIDTH_SUB
    var node_size = d3.scale.linear()
      .domain([1,10])	// we know score is in this domain
      .range([1,16])
      .clamp(true);
    var edge_WIDTH_SUB = d3.scale.pow().exponent(8)
      .domain( [minLinkWeight,maxLinkWeight] )
      .range([1,3])
      .clamp(true);

    /* Add drag & zoom behaviours */
    svgSub.call( d3.behavior.drag()
	      .on("drag",dragmovesub) );
    svgSub.call( d3.behavior.zoom()
	      .x(xScaleSub)
	      .y(yScaleSub)
	      .scaleExtent([0, 10])
	      .scale(0.5)
	      .on("zoom", doZoomSub) );

    // ------- Create the elements of the layout (links and nodes) ------

    var networkGraphSub = svgSub.append('svg:g').attr('class','grpParentSub');

    // links: simple lines
    var graphLinks = networkGraphSub.append('svg:g').attr('class','grp gLinks')      
      .selectAll("line")
      .data(linkArraySub, function(d) {return d.source.id+'-'+d.target.id;} )
      .enter().append("line")
      .attr('id', function(d) { return "ls" + d.id; } )
      .style('stroke-WIDTH_SUB', function(d) { return edge_WIDTH_SUB(d.weight);} )
      .attr("opacity", function(d){
		  if (modeSearch == "modeEvolution"){
	  			return "0";
	  		}else{
	  			return "1";
	  		}
	  })
      .attr('class', function(d) { 
    	    //loop through all parents
			var cParent = "link";
			var currentNode = d.source;
			
			while (typeof currentNode.parentID !== 'undefined'){
				currentNode = currentNode.parentID;	    
				cParent += " l" + currentNode.redditID;	    	  					  				
			}
			return cParent;	  		    	  		    	   
      } )

    // nodes: an SVG circle
    var firstSearchUser = true;
    firstID = undefined;
    
    var graphNodesSub = networkGraphSub.append('svg:g').attr('class','grp gNodes')
      .selectAll("circle")
      .data( nodeArraySub, function(d){return d.id} )
      .enter().append("svg:circle")
      .attr('id', function(d) 
    		  { 
    	  			return "cs" + d.id; 
    	  	  } )
      .attr('class', function(d) 
    		  { 
	    	  		if (d.parentID == null){
	    	  			return "nRoot";
	    	  		}else{	    	  				    	  			
	    	  			//loop through all parents
	    	  			var cParent = d.title;
	    	  			if (typeof user != undefined && user != ""){
	        	  			if (d.title === user){
	        	  				cParent += " blink";
	        	  				if (firstSearchUser == true){
	        	  					firstID = d.id;
	        	  				}
	        	  				firstSearchUser = false;
	        	  				
	        	  			}
	    	  			}
	    	  			var currentNode = d;
	    	  			while (typeof currentNode.parentID !== 'undefined'){
	    	  				currentNode = currentNode.parentID;	    
	    	  				cParent += " n" + currentNode.redditID;	    	  					  				
	    	  			}
	    	  			return cParent;
	    	  		}    	  		 
    		  })
      .attr('r', function(d) { 
    	  		if (typeof user != "undefined" && user != ""){
    	  			if (d.title === user){
    	  				return node_size(4.0) + 4;
    	  			}
    	  		}
	  			if (modeSearch == "modeTree"){
	  				return 0;
	  			}else if (modeSearch == "modeCount"){
	  				return node_size(d.score);
	  			}
	  			return node_size(4.0);				 
			} )
	  .attr("opacity", function(d){
		  if (modeSearch == "modeEvolution"){
	  			return "0";
	  		}else{
	  			return "1";
	  		}
	  })
      .attr('fill', function(d) {    	  
			if (typeof user != "undefined" && user != ""){
	  			if (d.title === user){
	  				return "red";
	  			}
	  		}
			
			if (modeSearch == "modeColor"){
				if (d.id == 0)
					{
						return "red";
					}
				return colorScale(d.simScore);
  			}
			
			return d.color; 
      } )
      .attr('pointer-events', 'all')
      .on("click", function(d) { 
    	  toggleNodes(d);
    	  d3.event.stopPropagation();
      } )    
      .on("mouseover", function(d) { 
    	  highlightGraphNodeSub(d,true,this);  
    	 	} )
      .on("mouseout",  function(d) { 
    	  highlightGraphNodeSub(d,false,this); 
    		} );
  
    
    graphNodesSub.transition()
	    .delay(function(d,i) { return i * 100; })
	    .duration(3000)
	    .style('opacity', 1);
    
    graphLinks.transition()
    .delay(function(d,i) { return i * 100; })
	    .duration(3000)
	    .style('opacity', 1);
    
    // labels: a group with two SVG text: a title and a shadow (as background)
    var graphLabelsSub = networkGraphSub.append('svg:g').attr('class','grp gLabel')
      .selectAll("g.label")
      .data( nodeArraySub, function(d){
    	  if (modeSearch == "modeShowComment"){
    		  return d.label;
		  }
    	  return d.id + "-" + d.title;    
      })
      .enter().append("svg:g")
      .attr('id', function(d) { return "ls" + d.id; } )
      .attr('class','label');
   
    shadows = graphLabelsSub.append('svg:text')
      .attr('x','-2em')
      .attr('y','-.3em')
      .attr('pointer-events', 'none') // they go to the circle beneath
      .attr('id', function(d) { return "lb" + d.id; } )
      .attr('class','nshadow')
      .text( function(d) { 
    	  if (modeSearch == "modeShowComment"){
    		  return d.label;
		  }
    	  return d.id + "-" + d.title;      
      } );
   

    labels = graphLabelsSub.append('svg:text')
      .attr('x','-2em')
      .attr('y','-.3em')
      .attr('pointer-events', 'none') // they go to the circle beneath
      .attr('id', function(d) { return "lf" + d.id; } )
      .attr('class','nlabel')
      .text( function(d) {
    	  if (modeSearch == "modeShowComment"){
    		  return d.label;
		  }
    	  return d.id + "-" + d.title; 
    } );
    
	//add evolution button
  	if (modeSearch== "modeEvolution"){
  		d3.select("#evolution").classed("hidden", false);
  		d3.select("#evolution")
		.style("left", 400 + "px")
		.style("top", 250 + "px");
	}else{
		d3.select("#evolution").classed("hidden", true);
	}
  	
    function toggleNodes(d) {
    	  if (d3.event.defaultPrevented) return; // ignore drag
    	  var nodes = d3.selectAll("." + "n" + d.redditID);
    	  var sLinks = d3.selectAll("." + "l" + d.redditID);
    	  if (d._childen == null || d._childen == false){
    		  d._childen = true;
    		  nodes.classed( 'node_display', "none" );
    		  sLinks.classed( 'node_display', "none" );
    	  }else{
    		  d._childen = false;
    		  nodes.classed( 'node_display', "" );
    		  sLinks.classed( 'node_display', "" );
    	  }
    	}
    
    selectSubmissionComment = function( new_idx, doMoveTo ) {

        // do we want to center the graph on the node?
        doMoveTo = doMoveTo || false;
        if( doMoveTo ) {
  		s = getViewportSizeSub();
  		width  = s.w<WIDTH ? s.w : WIDTH;
  		height = s.h<HEIGHT ? s.h : HEIGHT;
  		offset = { x : s.x + width/2  - nodeArraySub[new_idx].x*currentZoomSub,
  			   y : s.y + height/2 - nodeArraySub[new_idx].y*currentZoomSub };
  		repositionGraphSub( offset, undefined, 'move' );
        }
        // Now highlight the graph node and show its rumour panel
        highlightGraphNodeSub( nodeArraySub[new_idx], true );
      }
    
    /* --------------------------------------------------------------------- */
    /* Select/unselect a node in the network graph.
       Parameters are: 
       - node: data for the node to be changed,  
       - on: true/false to show/hide the node
    */
    
    function highlightGraphNodeSub( node, on )
    {
      //if( d3.event.shiftKey ) on = false; // for debugging

      // If we are to activate a rumour, and there's already one active,
      // first switch that one off
      if( on && activeRumourSub !== undefined ) {
    	  highlightGraphNodeSub( nodeArraySub[activeRumourSub], false );
      }

      // locate the SVG nodes: circle & label group
      
      if (node.id != 0){
    	  label  = d3.select( '#ls' + node.id );
    	  circle = d3.select( '#cs' + node.id );
    	  //users = d3.selectAll("." + node.title);
    	  // activate/deactivate the node itself
          circle.classed( 'main', on );
          //users.classed( 'main', on );
          label.classed( 'on', on || currentZoomSub >= SHOW_THRESHOLD_SUB );
          label.selectAll('text').classed( 'main', on );

          // activate all siblings
          Object(node.links).forEach( function(id) {
    		d3.select("#cs"+id).classed( 'sibling', on );
    		label = d3.select('#ls'+id);
    		label.classed( 'on', on || currentZoomSub >= SHOW_THRESHOLD_SUB );
    		label.selectAll('text.nlabel').classed( 'sibling', on );
          } );

          // set the value for the current active rumour
          activeRumourSub = on ? node.id : undefined;

      }

           
//      //select all users
//      for (var i = 0; i < rumourData.nodes.length;i++){
//    	  var sub = rumourData.submissions[i];
//    	  if (JSON.stringify(sub.users).toLowerCase().indexOf(node.title.toLowerCase()) >= 0){
//    		  var subNode = d3.select( '#c' +  rumourData.nodes[i].id );
//    		  subNode.classed( 'main', on );
//    	  }
//      }
    }


    /* --------------------------------------------------------------------- */
    /* Show the details panel for a rumour AND highlight its node in 
       the graph. Also called from outside the d3.json context.
       Parameters:
       - new_idx: index of the rumour to show
       - doMoveTo: boolean to indicate if the graph should be centered
         on the rumour
    */
    selectNode = function( new_idx, doMoveTo ) {

      // do we want to center the graph on the node?
      doMoveTo = doMoveTo || false;
      if( doMoveTo ) {
	s = getViewportSizeSub();
	WIDTH_SUB  = s.w<WIDTH_SUB ? s.w : WIDTH_SUB;
	HEIGHT_SUB = s.h<HEIGHT_SUB ? s.h : HEIGHT_SUB;
	offset = { x : s.x + WIDTH_SUB/2  - nodeArraySub[new_idx].x*currentZoomSub,
		   y : s.y + HEIGHT_SUB/2 - nodeArraySub[new_idx].y*currentZoomSub };
	repositionGraphSub( offset, undefined, 'move' );
      }
      // Now highlight the graph node and show its rumour panel
      highlightGraphNodeSub( nodeArraySub[new_idx], true );
      //showRumourPanel( nodeArraySub[new_idx] );
    }

	    
    /* --------------------------------------------------------------------- */
    /* Move all graph elements to its new positions. Triggered:
       - on node repositioning (as result of a forceSub-directed iteration)
       - on translations (user is panning)
       - on zoom changes (user is zooming)
       - on explicit node highlight (user clicks in a rumour panel link)
       Set also the values keeping track of current offset & zoom values
    */
    function repositionGraphSub( off, z, mode ) {

      // do we want to do a transition?
      var doTr = (mode == 'move');

      // drag: translate to new offset
      if( off !== undefined &&
	  (off.x != currentOffsetSub.x || off.y != currentOffsetSub.y ) ) {
	g = d3.select('g.grpParentSub')
	if( doTr )
	  g = g.transition().duration(500);
	g.attr("transform", function(d) { return "translate("+
					  off.x+","+off.y+")" } );
	currentOffsetSub.x = off.x;
	currentOffsetSub.y = off.y;
      }

      // zoom: get new value of zoom
      if( z === undefined ) {
	if( mode != 'tick' )
	  return;	// no zoom, no tick, we don't need to go further
	z = currentZoomSub;
      }
      else
	currentZoomSub = z;

      // move edges
      e = doTr ? graphLinks.transition().duration(500) : graphLinks;
      e
	.attr("x1", function(d) { return z*(d.source.x); })
        .attr("y1", function(d) { return z*(d.source.y); })
        .attr("x2", function(d) { return z*(d.target.x); })
        .attr("y2", function(d) { return z*(d.target.y); });

      // move nodes
      n = doTr ? graphNodesSub.transition().duration(500) : graphNodesSub;
      n
	.attr("transform", function(d) { return "translate("
					 +z*d.x+","+z*d.y+")" } );
      // move labels
      l = doTr ? graphLabelsSub.transition().duration(500) : graphLabelsSub;
      l
	.attr("transform", function(d) { return "translate("
					 +z*d.x+","+z*d.y+")" } );
    }
           

    /* --------------------------------------------------------------------- */
    /* Perform drag
     */
    function dragmovesub(d) {
      offset = { x : currentOffsetSub.x + d3.event.dx,
		 y : currentOffsetSub.y + d3.event.dy };
      repositionGraphSub( offset, undefined, 'drag' );
    }

    function getQStringParameterByName(name) {
	    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
	    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
	  }


    /* --------------------------------------------------------------------- */
    /* Perform zoom. We do "semantic zoom", not geometric zoom
     * (i.e. nodes do not change size, but get spread out or stretched
     * together as zoom changes)
     */
    function doZoomSub( increment ) {
      newZoom = increment === undefined ? d3.event.scale 
					: zoomScaleSub(currentZoomSub+increment);
      if( currentZoomSub == newZoom )
	return;	// no zoom change

      // See if we cross the 'show' threshold in either direction
      if( currentZoomSub<SHOW_THRESHOLD_SUB && newZoom>=SHOW_THRESHOLD_SUB )
	svgSub.selectAll("g.label").classed('on',true);
      else if( currentZoomSub>=SHOW_THRESHOLD_SUB && newZoom<SHOW_THRESHOLD_SUB )
	svgSub.selectAll("g.label").classed('on',false);

      // See what is the current graph window size
      s = getViewportSizeSub();
      WIDTH_SUB  = s.w<WIDTH_SUB  ? s.w : WIDTH_SUB;
      HEIGHT_SUB = s.h<HEIGHT_SUB ? s.h : HEIGHT_SUB;

      // Compute the new offset, so that the graph center does not move
      zoomRatio = newZoom/currentZoomSub;
      newOffset = { x : currentOffsetSub.x*zoomRatio + WIDTH_SUB/2*(1-zoomRatio),
		    y : currentOffsetSub.y*zoomRatio + HEIGHT_SUB/2*(1-zoomRatio) };

      // Reposition the graph
      repositionGraphSub( newOffset, newZoom, "zoom" );
    }

    zoomCallSub = doZoomSub;	// unused, so far

    /* --------------------------------------------------------------------- */

    /* process events from the forceSub-directed graph */
    forceSub.on("tick", function() {
      repositionGraphSub(undefined,undefined,'tick');
    });

    /* A small hack to start the graph with a rumour pre-selected */
    mid = getQStringParameterByName('id')
    if( mid != null )
      clearAndSelectSub( mid );
    
    if (user && firstID)
    	selectSubmissionComment(firstID, true);
}