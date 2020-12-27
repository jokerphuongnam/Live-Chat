const ajaxHelper = (url, data, beforeSend, done, error) => {
	/*$.post(url, data, 'json', 'application/json; charset=utf-8', 10000).done((result) => {
		done(result)
	}).fail((XMLHttpRequest, textStatus, errorThrown) => {
		error(XMLHttpRequest, textStatus, errorThrown)
	})*/
	$.ajax({
		type: data == null ? 'GET' : 'POST',
		contentType: 'application/json',
		url: url,
		data: data == null ? null : JSON.stringify(data),
		dataType: 'json',
		timeout: 10000,
		beforeSend: () => {
			beforeSend()
		},
		success: (data) => {
			done(data)
		},
		error: (XMLHttpRequest, textStatus, errorThrown) => {
			error(XMLHttpRequest, textStatus, errorThrown)
		}
	})
}

const Modes = {
	MESSAGE: 'message',
	ROOM: 'room',
	CREATE_GROUP: 'creategroup',
	LOGOUT: 'logout',
	SEARCH: 'search',
	CLICK_SEARCH: 'clicksearch',
	LEAVE_GROUP: 'leavegroup',
	ADD_GROUP: 'addgroup',
	CLICK_ADD_GROUP: 'clickaddgroup',
	EDIT_GROUP_NAME: 'editgroupname',
	EDIT_NICK_NAME:'editnickname',
	EDIT_IMAGE_GROUP: 'editimagegroup'
}