//create event handler for chat.jsp
doc.ready(() => {
	const currentUser = $.cookie('current_user') == null ? null : JSON.parse($.cookie('current_user'))
	var idCurrentRoom = JSON.parse($.cookie('accounts_logged'))[$.cookie('current_user')]

	//connect socket
	const socketHelper = new SocketHelper(currentUser.id, (result) => {
		console.log(result)
	}, (error, mode) => {
		console.log(error)
	})

	//toggle menu

	actionMenuBtnUser.click((event) => {
		actionMenuUser.slideToggle()
		if (body.hasClass('overUser')) {
			body.removeClass('overUser')
		} else {
			body.addClass('overUser')
		}
		if (body.hasClass('overChat')) {
			actionMenuChat.slideToggle()
			body.removeClass('overChat')
		}
		if (body.hasClass('overSearch')) {
			toggleSearch()
			body.removeClass('overSearch')
		}
		return false
	})

	actionMenuBtnChat.click((event) => {
		actionMenuChat.slideToggle()
		if (body.hasClass('overChat')) {
			body.removeClass('overChat')
		} else {
			body.addClass('overChat')
		}
		if (body.hasClass('overUser')) {
			actionMenuUser.slideToggle()
			body.removeClass('overUser')
		}
		if (body.hasClass('overSearch')) {
			toggleSearch()
			body.removeClass('overSearch')
		}
		return false
	})

	body.click((event) => {
		if (event.currentTarget.classList.contains('overUser')) {
			actionMenuUser.slideToggle()
			body.removeClass('overUser')
		}
		if (event.currentTarget.classList.contains('overChat')) {
			actionMenuChat.slideToggle()
			body.removeClass('overChat')
		}
	})

	//toggle search
	const toggleSearch = () => {
		contacts.toggle()
		listOfSearch.toggle()
	}

	const toggleMenu = () => {
		if (body.hasClass('overChat')) {
			actionMenuChat.slideToggle()
			body.removeClass('overChat')
		}
		if (body.hasClass('overUser')) {
			actionMenuUser.slideToggle()
			body.removeClass('overUser')
		}
	}

	searchInput.focus(() => {
		toggleSearch()
		toggleMenu()
		body.addClass('overSearch')
	}).blur(() => {
		toggleSearch()
		body.removeClass('overSearch')
	})

	searchBtn.click(() => {
		const value = searchInput.val()
		if (value == '') return
		searchInput.val('')
	})

	searchInput.bind('keypress', (e) => {
		if (e.keyCode == 13) {
			if (e.shiftKey)
				return true
			searchBtn.click()
			return false
		}
	})

	const actionMenuContentClick = {
		'view_profile': () => {
			infoDialog.toggle()
		},
		'logout': () => {
			//socketHelper.sendData(JSON.stringify(null), currentUser.id.replace('/', '%2F'), Modes.LOGOUT)
			ajaxHelper(Modes.LOGOUT, null, (error) => {
				$.cookie('current_user', null, {
					expires: -1,
					path: '/'
				})
				window.location.replace('login')
			})
		}
	}

	actionMenuLi.click((event) => {
		actionMenuContentClick[event.currentTarget.getAttribute('data-id')]()
	})

	const changeChat = (messages, roomName, room) => {
		roomChat.empty()
		roomChat.html(roomChatInfo(roomName))
		msgContainer.appendChild()
		msgContainer.append(
			messages.map(message => {
				if (message.id != currentUser.id) {
					return chatPartnerMess(message, room)
				}
				return currentUserMess(message, room)
			})
		)
	}

	var roomPaged = 1

	const changeChatById = (id) => {
		changeChat()
	}

	userLi.click((event) => {
		const liRoom = event.currentTarget
		if (liRoom.classList.contains('active')) {
			return false
		}
		$(`li.user.active `).removeClass('active')
		liRoom.addClass('active')
		changeChatById(liRoom.getAttribute('data-id'))
		return true
	})

	sendBtn.click((event) => {
		// socketHelper.sendData(JSON.stringify({
		//     'content': inputMess.val(),
		//     'type': 'text'
		// }), idCurrentRoom, Modes.MESSAGE)
		ajaxHelper(`${Modes.MESSAGE}/${idCurrentRoom}`,
			JSON.stringify({
				'content': inputMess.val(),
				'type': 'text'
			}), () => {
				console.log('chuẩn bị gửi')
			}
			, (result) => {
				console.log(result)
			}, (XMLHttpRequest, textStatus, errorThrown) => {
				console.log(errorThrown)
			})
	})
})