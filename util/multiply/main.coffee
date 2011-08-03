main = ->
	$("#read-result")
		.button()
		.click(onReadResult)

	# store dialog
	for i in [0..9]
		do (i) ->
			$("#memory-dialog-store-" + String(i))
				.button()
				.click -> onChooseStoreNumber i
	$("#memory-dialog-store").dialog {
		autoOpen: false
		buttons: {
			"Cancel": -> $("#memory-dialog-store").dialog 'close'
		},
		width: 270
	}

	# query dialog
	for i in [0..9]
		do (i) ->
			$("#memory-dialog-query-b" + String(i))
				.button()
				.click -> $("#memory-dialog-query").dialog 'close'
	$("#memory-dialog-query").dialog({
		autoOpen: false
		buttons: {
			"Clear": onClearButtonState
			"Close": -> $("#memory-dialog-query").dialog 'close'
		}
		width: 750
	})
	
	# memorize dialog
	$("#memory-dialog-memorize").dialog {
		autoOpen: false
		buttons: {
			"Close": -> $("#memory-dialog-memorize").dialog 'close'
		}
	}
	
	$("#arg0").change -> updateWorkAreas()
	$("#arg1").change -> updateWorkAreas()
	updateWorkAreas()


doError = (s) ->
	alert s
	throw s


verifyIsNumber = (n) ->
	for digit in n
		if digit not in "0123456789"
			return false
	return true


getRowColumnCount = ->
	# get our arguments to multiply
	n0 = $("#arg0")[0].value
	n1 = $("#arg1")[0].value
	if not verifyIsNumber n0
		doError "The first number must consist of numbers!"
	if not verifyIsNumber n1
		doError "The second number must consist of numbers!"

	# compute working area size
	nRows = n1.length + 1
	nCols = String(Number(n0) * Number(n1)).length

	return [nRows, nCols]


getArgs = ->
	return [Number($("#arg0")[0].value), Number($("#arg1")[0].value)]


updateQueryDialog = (salt, row, col) ->
	console.log("update query dialog: #{row}, #{col}")
	for i in [0..9]
		do (i) ->
			img = $("#memory-dialog-query-" + String(i))
			img.attr(src:"http://thevash.com/static/vash-multiply-images/#{salt}-#{row}-#{col}-#{i}.png")


onMemoryButtonClick = (e) ->
	button = e.currentTarget
	row = button.row
	col = button.col

	# always make sure the memory dialog is closed
	$("#memory-dialog-memorize").dialog('close')

	# pick the dialog we need
	if button.selection != null
		dlg = $("#memory-dialog-query")
		# TODO: insert salt here
		updateQueryDialog(1, row, col)
	else
		dlg = $("#memory-dialog-store")

	# attach the clicked button and open the dialog
	dlg[0].targetButton = button
	dlg.dialog('open')


###
# Return the dialog for the given name.
###
getDialog = (name) ->
	return $("#memory-dialog-" + name)


###
# Return the row, col, and button element currently attached 
# to the given dialog type.
###
getContextForDialog = (name) ->
	# extract our current button from the dialog
	dlg = getDialog(name)
	button = dlg[0].targetButton
	# extract row and column from button
	row = button.row
	col = button.col
	return [row, col, button]
	

###
# Store a number into a cell.
###
onChooseStoreNumber = (i) ->
	[row, col, button] = getContextForDialog('store')
	# mark this button as "done"
	button.selection = i
	$("#b-#{row}-#{col}").addClass('ui-state-selected')
	# we are finished with this dialog for now
	getDialog('store').dialog('close')
	# show the selected image in the memorize dialog
	# TODO: apply the salt text here
	$("#memory-dialog-memorize img").attr(src:"http://thevash.com/static/vash-multiply-images/1-#{row}-#{col}-#{i}.png")
	$("#memory-dialog-memorize").dialog('open')
	

###
# Iterate over all of output digits, querying for answers, updating the screen as we go.
###
onReadResult = (e) ->
	[a0, a1] = getArgs()
	[nRows, nCols] = getRowColumnCount()
	nResult = a0 * a1
	sResult = String(nResult)
	salt = 1
	row = nRows - 1
	col = 0
	updateQueryDialog(salt, row, col)
	$("#memory-dialog-query").dialog('open')

	# add click handler to update our selected answer
	selection = ""
	for i in [0..9]
		do (i) ->
			$("#memory-dialog-query-b" + String(i))
				.click -> selection = selection + String(i)

	# store prior button state so we can restore it
	buttons = $("#memory-dialog-query").dialog('option', 'buttons')
	title = $("#memory-dialog-query").dialog('option', 'title')
	cleanupAfter = =>
		$("#memory-dialog-query").dialog('option', 'buttons', buttons)
		$("#memory-dialog-query").dialog('option', 'title', title)
		$("#memory-dialog-query").dialog('option', 'close', null)

	showResult = =>
		if Number(selection) == nResult
			message = "Congratulations!"
		else
			message = "That's not quite right."

		$("#result-area").html """
		<div>Your result: #{selection}</div>
		<div>Your CPU's result: #{nResult}</div>
		<div>#{message}</div>
		"""

	# every time we close the dialog, update and show next, or finish
	postClose = =>
		col += 1

		# if we close the dialog without selecting a button
		if selection.length != col
			cleanupAfter()
			return

		updateQueryDialog(salt, row, col)
		$("#memory-dialog-query").dialog('option', 'title', "Column " + String(nCols - col))
		if col >= nCols
			showResult()
			cleanupAfter()
		else
			$("#memory-dialog-query").dialog('open')
		
	# NOTE: we can't guarantee that our button callback will go first, updating
	#	our selection before we do close processing.  Instead we requeue this at
	#	the end of our current event queue so that we can be sure to get called
	#	after everything has settled.
	$("#memory-dialog-query").dialog('option', 'buttons', {})
	$("#memory-dialog-query").dialog('option', 'title', "Column " + String(nCols))
	$("#memory-dialog-query").dialog('option', 'close', => window.setTimeout(postClose, 1))


###
# Called on "clear" in the query dialog.
###
onClearButtonState = ->
	[row, col, button] = getContextForDialog('query')
	# mark this button as "undone"
	button.selection = null
	$("#b-#{row}-#{col}").removeClass('ui-state-selected')
	# we are finished with this dialog for now
	getDialog('query').dialog('close')
	

updateWorkAreas = ->
	#FIXME: if we in progress, query if user wants to start a new mul

	[nRows, nCols] = getRowColumnCount()

	# reset working areas
	wa1 = $("#work-area-1")
	wa1.html("")
	wa2 = $("#work-area-2")
	wa2.html("")
	# insert buttons
	wa = wa1
	for i in [0..nRows-1]
		for j in [0..nCols-1]
			name = "b-#{i}-#{j}"
			wa.append('<div id="'+name+'">')
			b = $('#'+name)
			b.button(label:"&nbsp;")
				.addClass("memory-button")
				.click(onMemoryButtonClick)
			b[0].row = i
			b[0].col = j
			b[0].selection = null
		wa.append("<br>")
		if i == nRows - 2
			wa = wa2

