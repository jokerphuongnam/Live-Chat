const ajaxHelper = (url, data,beforeSend, done, error) => {
	/*$.post(url, data, 'json', 'application/json; charset=utf-8', 10000).done((result) => {
		done(result)
	}).fail((XMLHttpRequest, textStatus, errorThrown) => {
		error(XMLHttpRequest, textStatus, errorThrown)
	})*/
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: url,
		data: JSON.stringify(data),
		dataType: 'json',
		timeout: 10000,
        beforeSend: () =>{
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
	LOGOUT: 'logout'
}