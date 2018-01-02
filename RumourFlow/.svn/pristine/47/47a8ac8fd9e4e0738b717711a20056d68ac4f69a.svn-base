

function getSubmissionWithUser(data, user) {
	var rootURL = server
			+ "/RumourFlow/rest/RedditData/search/slider/" + mode
			+ "/" + threshold;
	$.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : D3ok,
		error : function(jqXHR, textStatus, errorThrown) {
			// alert('addWine error: ' + textStatus);
		}
	});
}

// -------------------------------------------------------------------
function init(keyword) {
	var rootURL = encodeURI(server
			+ "/RumourFlow/rest/RedditData/search/title/"
			+ keyword);
	$.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			data.submissions.sort(function(a, b) {
				var x = a.created;
				var y = b.created;
				return ((x < y) ? -1 : ((x > y) ? 1 : 0));
			});

			data.nodes.sort(function(a, b) {
				var x = a.timestamp;
				var y = b.timestamp;
				return ((x < y) ? -1 : ((x > y) ? 1 : 0));
			});

			getSubmissionsAboutRumour(data, 0.7);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addWine error: ' + textStatus);
		}
	});
}


// Do the stuff -- to be called after D3.js has loaded
function getSubmissionsAboutRumour(data, threshold) {
	// sort series
	submisisonData = data;
	jQuery("#main").text("Submission Semantic");
	d3.select("#rumourNetwork").html("");

	if (!rumourData)
		rumourData = data;
	// Some constants

	// Variables keeping graph state
	var activeNode = undefined;
	var currentOffset = {
		x : 0,
		y : 0
	};
	var currentZoom = 1.0;

	// The D3.js scales
	var xScale = d3.scale.linear().domain([ 0, WIDTH ]).range([ 0, WIDTH ]);
	var yScale = d3.scale.linear().domain([ 0, HEIGHT ]).range([ 0, HEIGHT ]);
	var zoomScale = d3.scale.linear().domain([ 0, 10 ]).range([ 0, 10 ]).clamp(
			true);

	var margin = {
			top : 10,
			right : 50,
			bottom : 20,
			left : 50
		}, width = WIDTH - margin.left - margin.right, height = HEIGHT - margin.top
				- margin.bottom;
	
	/* .......................................................................... */

	// The D3.js force-directed layout
	// Add to the page the SVG element that will contain the rumour network
	var svg = d3.select("#rumourNetwork").append("svg:svg").attr('xmlns',
			'http://www.w3.org/2000/svg')
			.attr("id", "graph").attr("viewBox",
			"0 0 " + WIDTH + " " + HEIGHT).attr("preserveAspectRatio", "xMinYMin meet")
			.attr("transform",
					"translate(" + margin.left + "," + margin.top + ")");

	// Rumour panel: the div into which the rumour details info will be written
	rumourInfoDiv = d3.select("#rumourInfo");

	/* ....................................................................... */

	// Get the current size & offset of the browser's viewport window
	function getViewportSize(w) {
		var w = w || window;
		if (w.innerWidth != null)
			return {
				w : w.innerWidth,
				h : w.innerHeight,
				x : w.pageXOffset,
				y : w.pageYOffset
			};
		var d = w.document;
		if (document.compatMode == "CSS1Compat")
			return {
				w : d.documentElement.clientWidth,
				h : d.documentElement.clientHeight,
				x : d.documentElement.scrollLeft,
				y : d.documentElement.scrollTop
			};
		else
			return {
				w : d.body.clientWidth,
				h : d.body.clientHeight,
				x : d.body.scrollLeft,
				y : d.body.scrollTop
			};
	}

	function getQStringParameterByName(name) {
		var match = RegExp('[?&]' + name + '=([^&]*)').exec(
				window.location.search);
		return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
	}

	/*
	 * Change status of a panel from visible to hidden or viceversa id:
	 * identifier of the div to change status: 'on' or 'off'. If not specified,
	 * the panel will toggle status
	 */
	toggleDiv = function(id, status) {
		d = d3.select('div#' + id);
		if (status === undefined)
			status = d.attr('class') == 'panel_on' ? 'off' : 'on';
		d.attr('class', 'panel_' + status);
		return false;
	}

	/*
	 * Clear all help boxes and select a rumour in the network and in the rumour
	 * details panel
	 */
	clearAndSelect = function(id) {
		toggleDiv('faq', 'off');
		toggleDiv('help', 'off');
		selectNode(id, true); // we use here the selectNode() closure
	}

	// Declare the variables pointing to the node & link arrays
	var nodeArray = data.nodes;
	var tempArray = data.links;
	var linkArray = [];
	// filter threshold
	for (var i = 0; i < tempArray.length; i++) {
		if (data.links[i].weight >= threshold) {
			linkArray.push(data.links[i]);
		}
	}

	// get URL
	for (var i = 0; i < data.nodes.length; i++) {
		for (var j = 0; j < data.submissions.length; j++) {
			if (data.submissions[j].url)
				data.nodes[i].url = data.submissions[j].url;
		}
	}

	minLinkWeight = Math.min.apply(null, linkArray.map(function(n) {
		return n.weight;
	}));
	maxLinkWeight = Math.max.apply(null, linkArray.map(function(n) {
		return n.weight;
	}));

	// Add the node & link arrays to the layout, and start it
	force.nodes(nodeArray).links(linkArray).start();

	// A couple of scales for node radius & edge width
	var node_size = d3.scale.linear().domain([ 1, 10 ]) // we know score is in
	// this domain
	.range([ 1, 16 ]).clamp(true);
	var edge_width = d3.scale.pow().exponent(8).domain(
			[ minLinkWeight, maxLinkWeight ]).range([ 1, 3 ]).clamp(true);

	/* Add drag & zoom behaviours */
	svg.call(d3.behavior.drag().on("drag", dragmove));
	svg.call(d3.behavior.zoom().x(xScale).y(yScale).scaleExtent([ 1, 6 ]).on(
			"zoom", doZoom));

	// ------- Create the elements of the layout (links and nodes) ------

	var networkGraph = svg.append('svg:g').attr('class', 'grpParent');

	// links: simple lines

	var graphLinks = networkGraph.append('svg:g').attr('class', 'grp gLinks')
			.selectAll("line").data(linkArray, function(d) {
				return d.source.index + '-' + d.target.index;
			}).enter().append("line").style('stroke-width', function(d) {
				return edge_width(d.weight);
			}).attr("class", "link");

	// nodes: an SVG circle
	var graphNodes = networkGraph.append('svg:g').attr('class', 'grp gNodes')
			.selectAll("circle").data(nodeArray, function(d) {
				return d.index;
			}).enter().append("svg:circle").attr('id', function(d) {
				return "c" + d.index;
			}).attr('r', function(d) {
				return node_size(d.score);
			}).attr('fill', function(d) {
				return d.color;
			}).attr('pointer-events', 'all').on("click", function(d) {
				showRumourPanel(d);
				showCommentCloud(d.redditID);
			}).on("mouseover", function(d) {
				highlightGraphNode(d, true, this);
			}).on("mouseout", function(d) {
				highlightGraphNode(d, false, this);
			}).call(force.drag);

	// labels: a group with two SVG text: a title and a shadow (as background)
	var graphLabels = networkGraph.append('svg:g').attr('class', 'grp gLabel')
			.selectAll("g.label").data(nodeArray, function(d) {
				return d.title;
			}).enter().append("svg:g").attr('id', function(d) {
				return "l" + d.index;
			}).attr('class', 'label');

	shadows = graphLabels.append('svg:text').attr('x', '-2em').attr('y',
			'-.3em').attr('pointer-events', 'none') // they go to the circle
	// beneath
	.attr('id', function(d) {
		return "lb" + d.index;
	}).attr('class', 'nshadow').text(function(d) {
		return d.title;
	});

	labels = graphLabels.append('svg:text').attr('x', '-2em')
			.attr('y', '-.3em').attr('pointer-events', 'none') // they go to
			// the circle
			// beneath
			.attr('id', function(d) {
				return "lf" + d.index;
			}).attr('class', 'nlabel').text(function(d) {
				return d.title;
			});

	/* --------------------------------------------------------------------- */
	/*
	 * Select/unselect a node in the network graph. Parameters are: - node: data
	 * for the node to be changed, - on: true/false to show/hide the node
	 */
	function highlightGraphNode(node, on) {
		// if( d3.event.shiftKey ) on = false; // for debugging

		// If we are to activate a rumour, and there's already one active,
		// first switch that one off
		if (on && activeNode !== undefined) {
			highlightGraphNode(nodeArray[activeNode], false);
		}

		// locate the SVG nodes: circle & label group
		circle = d3.select('#c' + node.index);
		label = d3.select('#l' + node.index);

		// activate/deactivate the node itself
		circle.classed('main', on);
		label.classed('on', on || currentZoom >= SHOW_THRESHOLD);
		label.selectAll('text').classed('main', on);

		// activate all siblings
		Object(node.links).forEach(function(id) {
			d3.select("#c" + id).classed('sibling', on);
			label = d3.select('#l' + id);
			label.classed('on', on || currentZoom >= SHOW_THRESHOLD);
			label.selectAll('text.nlabel').classed('sibling', on);
		});

		// set the value for the current active rumour
		activeNode = on ? node.index : undefined;
	}

	/* --------------------------------------------------------------------- */
	/*
	 * Show the details panel for a rumour AND highlight its node in the graph.
	 * Also called from outside the d3.json context. Parameters: - new_idx:
	 * index of the rumour to show - doMoveTo: boolean to indicate if the graph
	 * should be centered on the rumour
	 */
	selectNode = function(new_idx, doMoveTo) {

		// do we want to center the graph on the node?
		doMoveTo = doMoveTo || false;
		if (doMoveTo) {
			s = getViewportSize();
			width = s.w < WIDTH ? s.w : WIDTH;
			height = s.h < HEIGHT ? s.h : HEIGHT;
			offset = {
				x : s.x + width / 2 - nodeArray[new_idx].x * currentZoom,
				y : s.y + height / 2 - nodeArray[new_idx].y * currentZoom
			};
			repositionGraph(offset, undefined, 'move');
		}
		// Now highlight the graph node and show its rumour panel
		highlightGraphNode(nodeArray[new_idx], true);
		showRumourPanel(nodeArray[new_idx]);
	}

	/* --------------------------------------------------------------------- */
	/*
	 * Show the rumour details panel for a given node
	 */
	function showRumourPanel(node) {
		if (d3.event.defaultPrevented) {
			return;
		}
		// Fill it and display the panel
		if (mode == "exploration") {
			rumourInfoDiv.html("").attr("class", "panel_on");
			$(".cThreshold").val(0.0);
			initSubGraph(node, 0.0);
			$(".secondPanel").prop('disabled', false);
		} else {
			rumourInfoDiv.html("").attr("class", "panel_on");		}
	}

	/* --------------------------------------------------------------------- */
	/*
	 * Move all graph elements to its new positions. Triggered: - on node
	 * repositioning (as result of a force-directed iteration) - on translations
	 * (user is panning) - on zoom changes (user is zooming) - on explicit node
	 * highlight (user clicks in a rumour panel link) Set also the values
	 * keeping track of current offset & zoom values
	 */
	function repositionGraph(off, z, tickMode) {

		// do we want to do a transition?
		var doTr = (tickMode == 'move');

		// drag: translate to new offset
		if (off !== undefined
				&& (off.x != currentOffset.x || off.y != currentOffset.y)) {
			g = d3.select('g.grpParent')
			if (doTr)
				g = g.transition().duration(500);
			g.attr("transform", function(d) {
				return "translate(" + off.x + "," + off.y + ")"
			});
			currentOffset.x = off.x;
			currentOffset.y = off.y;
		}

		// zoom: get new value of zoom
		if (z === undefined) {
			if (tickMode != 'tick')
				return; // no zoom, no tick, we don't need to go further
			z = currentZoom;
		} else
			currentZoom = z;

		// move edges
		e = doTr ? graphLinks.transition().duration(500) : graphLinks;
		e.attr("x1", function(d) {
			if (d.source.x) {
				return z * (d.source.x);
			}
		}).attr("y1", function(d) {
			if (d.source.y) {
				return z * (d.source.y);
			}

		}).attr("x2", function(d) {
			if (d.target.x) {
				return z * (d.target.x);
			}

		}).attr("y2", function(d) {
			if (!d.target.y) {
				var test = 1;
				return;
			}
			return z * (d.target.y);
		});

		// move nodes
		n = doTr ? graphNodes.transition().duration(100) : graphNodes;
		n.attr("transform", function(d) {
			if (d.x && d.y) {
				return "translate(" + z * d.x + "," + z * d.y + ")"
			}
		});
		// move labels
		l = doTr ? graphLabels.transition().duration(100) : graphLabels;
		l.attr("transform", function(d) {
			if (d.x && d.y) {
				return "translate(" + z * d.x + "," + z * d.y + ")"
			}

		});
	}

	/* --------------------------------------------------------------------- */
	/*
	 * Perform drag
	 */
	function dragmove(d) {
		offset = {
			x : currentOffset.x + d3.event.dx,
			y : currentOffset.y + d3.event.dy
		};
		repositionGraph(offset, undefined, 'drag');
	}

	/* --------------------------------------------------------------------- */
	/*
	 * Perform zoom. We do "semantic zoom", not geometric zoom (i.e. nodes do
	 * not change size, but get spread out or stretched together as zoom
	 * changes)
	 */
	function doZoom(increment) {
		newZoom = increment === undefined ? d3.event.scale
				: zoomScale(currentZoom + increment);
		if (currentZoom == newZoom)
			return; // no zoom change

		// See if we cross the 'show' threshold in either direction
		if (currentZoom < SHOW_THRESHOLD && newZoom >= SHOW_THRESHOLD)
			svg.selectAll("g.label").classed('on', true);
		else if (currentZoom >= SHOW_THRESHOLD && newZoom < SHOW_THRESHOLD)
			svg.selectAll("g.label").classed('on', false);

		// See what is the current graph window size
		s = getViewportSize();
		width = s.w < WIDTH ? s.w : WIDTH;
		height = s.h < HEIGHT ? s.h : HEIGHT;

		// Compute the new offset, so that the graph center does not move
		zoomRatio = newZoom / currentZoom;
		newOffset = {
			x : currentOffset.x * zoomRatio + width / 2 * (1 - zoomRatio),
			y : currentOffset.y * zoomRatio + height / 2 * (1 - zoomRatio)
		};

		// Reposition the graph
		repositionGraph(newOffset, newZoom, "zoom");
	}

	zoomCall = doZoom; // unused, so far

	/* --------------------------------------------------------------------- */

	/* process events from the force-directed graph */
	force.on("tick", function() {
		repositionGraph(undefined, undefined, 'tick');
	});

	/* A small hack to start the graph with a rumour pre-selected */
	mid = getQStringParameterByName('id')
	if (mid != null)
		clearAndSelect(mid);
	
	drawLegend(selectedRumours);
	
	function drawLegend(varNames) {

		var legend = networkGraph.selectAll(".legend").data(varNames).enter()
				.append("g").attr("class", "legend").attr("transform",
						function(d, i) {
							return "translate(55," + (140 - i * 20) + ")";
						});

		legend.append("rect").attr("x", width - 10).attr("width", 10).attr("class", function(d,i)
				{
					return "rect" + i;
				}).attr(
				"height", 10).style("fill", function(d, i) {
			return brewer(i);
		})
		.on("click", function(d, i) {
			showTopicCloud();
			showCommentCloud(d);
			currentRumour = "rumour" + i;
			currentRumourName = d;
			init(currentRumourName);
			//remove other strokes
			d3.selectAll("rect")
            .attr("stroke", 1);
			
			//add stroke
			d3.select(this)
             .attr("stroke", d3.rgb(d.color).darker(20));	
		})
		.attr("stroke", function(d,i) {
			var name = selectedRumours[i];
			if (currentRumourName === name)
				return d3.rgb(d.color).darker(20);
			else
				return 1;
		});

		legend.append("text").attr("x", width - 12).attr("y", 6).attr("dy",
				".35em").style("text-anchor", "end").text(function(d,i) {
			var name = selectedRumours[i];
			return name;
		});
	}

} // end of getSubmissionsAboutRumour()