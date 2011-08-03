###
# Copyright 2011, Terrence Cole
# 
# This file is part of VashMultiply 
# 
# VashMultiply is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# VashMultiply is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with VashMultiply.  If not, see <http://www.gnu.org/licenses/>.
###


get '/': ->
	render 'index'

view index: ->

layout ->
	html ->
		head ->
			title "Multiply with Vash"
			meta charset: 'utf-8'
			link rel: 'stylesheet', href: "/css/dark-hive/jquery-ui-1.8.14.custom.css"
			style '''
			body {padding: 0px; margin: 0px;}
			#entry-area {
				font-family: monospace;
				padding-left: 100px;
			}
			input {
				text-align: right;
			}
			.memory-button {
				margin-bottom: 2px;
			}
			.image {
				display: inline-block;
			}
			caption {
				color: #FFF;
				font-weight: bold;
				font-size: 1.1em;
			}
			'''
			script src: "/js/jquery-1.6.2.min.js"
			script src: "/js/jquery-ui-1.8.14.custom.min.js"
			script src: "/release/vash-mul.js"
			coffeescript ->
				$().ready ->
					main()

		body ->
			h2 -> "How to Multiply 10 Digit Numbers using nothing but Vash"
			p ->
				'The problem with multiplying in your head, or doing any big math problem without a pencil and paper, is memory.  In order to multiply two 10-digit numbers, you need to store approximately 11 rows and 20 columns of intermediate numbers in working memory.  This is beyond most people.  It is certainly beyond me.'
			p ->
				'On the other hand, most people can, without any problem at all, identify 100-200 specific individuals in a giant crowd of people.  Clearly then, it is possible for a normal human to hold in memory and recall at will 100-200 unique values.'
			p ->
				'Recently, <a href="http://people.csail.mit.edu/andyd/home.html"> Andrew Drucker</a> of MIT discovered a clever way of making use of visual memory to do multiplication.  The only difficult part of the algorithm is getting your hands on hundreds of unique images.  Naturally, this is something that Vash can help with.'
			
			h3 -> "How to use this tool:"
			ol ->
				li -> 
					p -> 'Pick two numbers to multiply and put them in the text boxes below.  The workspace below will automatically expand to give you room for any size number you want (with limits).'
				li -> 
					p -> 'Perform each step of the multiplication, as you would with pencil and paper, "storing" your intermediate result into the grid in the workspace.'
					ul ->
						li -> 'To store a value, click on the cell to store in.  We will pop up a unique vash image.  Look at it for a bit: you will need to be able to pick it out of a crowd later.  This is easier than it sounds, thanks to your visual memory.'
				li ->
					p -> 'When you have finished all of the intermediate multiplications, you will need to tally up your results by adding down the columns.'
					ul ->
						li -> 'It is unlikely, if you are like me, that you will remember all of the numbers you stored.  When you need help recalling a number you stored in a cell, click on the cell again.  We will pop up a dialog with 10 images in it, 0 through 9.  One of them will be an image you have seen, the other 9 will be new to you.  The number you stored is the number next to the image you recognize.'
				li ->
					p -> 'Once you have filled in the cells in the bottom row, click on "Read Result."  We will query you for each number in the output and write down your results.'
			p -> 'Good Luck!'
			p ->
				'For the full suite of details, see the original <a href="http://people.csail.mit.edu/andyd/rec_method.pdf">excellent paper</a> by Andrew Drucker.'
			
			hr()
			
			div id:'entry-area', ->
				span -> "&nbsp"		
				input id:"arg0", type:"text", width:"250", value:"327"
				br()
				span -> "x"
				input id:"arg1", type:"text", width:"250", value:"246"
				hr width:"250px", align:"left"
				div id:'work-area-1'
				hr width:"250px", align:"left"
				div id:'work-area-2'

				br()
				div id:'read-result', ->
					"Read Result"
				
				br()
				div id:'result-area'
			
			div id:"memory-dialog-store", title:"Pick a Number", ->
				div id:"memory-dialog-store-0", -> "0"
				div id:"memory-dialog-store-1", -> "1"
				div id:"memory-dialog-store-2", -> "2"
				div id:"memory-dialog-store-3", -> "3"
				div id:"memory-dialog-store-4", -> "4"
				div id:"memory-dialog-store-5", -> "5"
				div id:"memory-dialog-store-6", -> "6"
				div id:"memory-dialog-store-7", -> "7"
				div id:"memory-dialog-store-8", -> "8"
				div id:"memory-dialog-store-9", -> "9"

			div id:"memory-dialog-memorize", title:"Memorize This Image", ->
				img src:""

			div id:"memory-dialog-query", title:"Pick the Image you Remember", ->
				div id:"memory-dialog-query-b0", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "0"
						tr -> td ->	img id:"memory-dialog-query-0", src:""
				div id:"memory-dialog-query-b1", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "1"
						tr -> td ->	img id:"memory-dialog-query-1", src:""
				div id:"memory-dialog-query-b2", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "2"
						tr -> td ->	img id:"memory-dialog-query-2", src:""
				div id:"memory-dialog-query-b3", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "3"
						tr -> td ->	img id:"memory-dialog-query-3", src:""
				div id:"memory-dialog-query-b4", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "4"
						tr -> td ->	img id:"memory-dialog-query-4", src:""
				div id:"memory-dialog-query-b5", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "5"
						tr -> td ->	img id:"memory-dialog-query-5", src:""
				div id:"memory-dialog-query-b6", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "6"
						tr -> td ->	img id:"memory-dialog-query-6", src:""
				div id:"memory-dialog-query-b7", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "7"
						tr -> td ->	img id:"memory-dialog-query-7", src:""
				div id:"memory-dialog-query-b8", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "8"
						tr -> td ->	img id:"memory-dialog-query-8", src:""
				div id:"memory-dialog-query-b9", ->
					table 'class':'image', -> 
						caption align:"bottom", -> "9"
						tr -> td ->	img id:"memory-dialog-query-9", src:""

