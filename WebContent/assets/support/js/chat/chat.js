//create event handler for chat.jsp
var onChange = null
doc.ready(() => {
    const currentUser = $.cookie('current_user') == null ? null : JSON.parse($.cookie('current_user'))
    var idCurrentRoom = JSON.parse($.cookie('accounts_logged'))[currentUser.id]
    var idPartner = null
        //connect socket
    new SocketHelper(currentUser.id, (result) => {
        switch (result.event) {
            case 'sendMessage':
                if (result.messages[0].id_sender == currentUser.id) {
                    if (idCurrentRoom == null) {
                        window.location.replace('chat/' + result.id)
                    }
                    if (idCurrentRoom == result.id) {
                        $('[before-send-id=' + result.id_old_message + ']').remove()
                        msgContainer.append(currentUserMess(result.messages[0]))
                    }
                } else {
                    if (idCurrentRoom == result.id) {
                        msgContainer.append(chatPartnerMess(result.messages[0]))
                    }
                }
                roomToFirst(result)
                break
            case 'edit_nick_name':
            case 'edit_group_name':
                changeRoomName(result)
                break
            case 'add_group':
                var lastMess = {}
                if (result.id_sender != null) {
                    lastMess = {
                        id_sender: result.sender,
                        send_time: result.send_time,
                        content: result.content_message
                    }
                } else {
                    lastMess = null
                }
                roomToFirst({
                    id: result.id_chat,
                    image: result.image_group,
                    name: result.name_group,
                    messages: [lastMess]
                })
                break
            default:
                break
        }
        $('.action_menu').children().children().click((event) => {
            actionMenuContentClick[event.currentTarget.getAttribute('data-id')]()
        })
    }, (error, mode) => {
        console.log(error)
    })

    //toggle menu

    //hide search

    const hideSearch = () => {
        contacts.show()
        listOfSearch.hide()
    }

    const removeSearchs = () => {
        listOfSearch.empty()
        searchInput.val('')
        hideSearch()
        body.removeClass('overSearch')
    }

    const hideHelper = () => {
        body.removeClass('overHelper')
        $('.right_helper_action_menu').slideUp()
        $('.submit_action_menu').unbind('click')
        $('.input_helper').val('')
        $('.roomsjoined').find('.rooms').empty()
    }

    const actionMenuBtnUserClick = () => {
        actionMenuUser.slideToggle()
        if (body.hasClass('overUser')) {
            body.removeClass('overUser')
        } else {
            body.addClass('overUser')
        }
        if (body.hasClass('overChat')) {
            actionMenuChat.slideUp()
            body.removeClass('overChat')
        }
        if (body.hasClass('overSearch')) {
            removeSearchs()
        }
        if (body.hasClass('overHelper')) {
            hideHelper()
        }
    }

    actionMenuBtnUser.click((event) => {
        actionMenuBtnUserClick()
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
            actionMenuUser.slideUp()
            body.removeClass('overUser')
        }
        if (body.hasClass('overSearch')) {
            removeSearchs()
        }
        if (body.hasClass('overHelper')) {
            hideHelper()
        }
        return false
    })

    const bodyClick = () => {
        if (body.hasClass('overUser')) {
            actionMenuUser.slideUp()
            body.removeClass('overUser')
        }
        if (body.hasClass('overChat')) {
            actionMenuChat.slideUp()
            body.removeClass('overChat')
        }
        if (body.hasClass('overSearch')) {
            removeSearchs()
        }
        if (body.hasClass('overHelper')) {
            hideHelper()
        }
    }

    body.click((event) => {
        bodyClick()
    })

    //show search
    const showSearch = () => {
        contacts.hide()
        listOfSearch.show()
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
        if (body.hasClass('overHelper')) {
            hideHelper()
        }
    }

    searchInput.focus(() => {
        showSearch()
        toggleMenu()
        body.addClass('overSearch')
        body.unbind('click')
        return false
    }).blur(() => {
        body.click((event) => {
            bodyClick()
        })
    })

    const searchEvent = () => {
        ajaxHelper(`${Modes.SEARCH}/${searchInput.val()}`, null, () => {}, (result) => {
            result.map(user => {
                listOfSearch.append(usersSearched(user))
            })
            $('li.user-search').click((event) => {
                $(`li.user.active `).removeClass('active')
                const idUser = event.currentTarget.getAttribute('data-id').replace('/', '|')
                ajaxHelper(`${Modes.CLICK_SEARCH}/${idUser}`, null, () => {
                    body.unbind('click')
                }, (result) => {
                    if (result.id != undefined) {
                        window.location.replace('chat/' + result.id)
                    } else {
                        roomChat.empty()
                        inputMess.focus()
                        const currentPartner = $(event.currentTarget)
                        roomChat.html(roomChatInfo({
                            name: currentPartner.find('.room_info_cell').text().trim(),
                            image: currentPartner.find('.img_cont_mini > img').attr('src'),
                            offline: 'online'
                        }))
                        msgContainer.empty()
                        idCurrentRoom = null
                        idPartner = idUser
                    }
                }, (XMLHttpRequest, textStatus, errorThrown) => {

                })
                removeSearchs()
                return true
            })
        }, (XMLHttpRequest, textStatus, errorThrown) => {
            console.log(errorThrown)
        })
    }

    searchBtn.click(() => {
        const value = searchInput.val()
        if (value == '') return
        searchInput.val('')
    })

    searchInput.bind('keypress', (e) => {
        listOfSearch.empty()
        if (e.keyCode == 13) {
            searchBtn.click()
            return false
        } else {
            if (searchInput.val() != '') {
                searchEvent()
            }
        }
    })

    const actionMenuContentClick = {
        'view_profile': () => {
            infoDialog.slideDown()
        },
        'create_group': () => {
            ajaxHelper(`${Modes.CREATE_GROUP}`, null, () => {}, (result) => {
                window.location.replace('chat/' + result.id)
            }, (XMLHttpRequest, textStatus, errorThrown) => {

            })
        },
        'logout': () => {
            ajaxHelper(Modes.LOGOUT, null, () => {

            }, (result) => {

            }, (XMLHttpRequest, textStatus, errorThrown) => {
                window.location.replace('login')
            })
        },
        'edit_nick_name': () => {
            if (idCurrentRoom == null || idCurrentRoom == undefined) {
                return
            }
            inputHeplerEvent(
                `${Modes.EDIT_NICK_NAME}/${idCurrentRoom == null ? idPartner : idCurrentRoom}`,
                'Enter new your nickname...')
        },
        'add_group': () => {
            const tailUrl = `${Modes.ADD_GROUP}/${idCurrentRoom == null ? idPartner : idCurrentRoom}`
            ajaxHelper(tailUrl, null, () => {
                body.unbind('click')
                $('.roomsjoined').slideDown()
                body.addClass('overHelper')
                actionMenuUser.slideUp()
                body.removeClass('overUser')
            }, (results) => {
                $($('.roomsjoined').children()[0]).text('List group can invite')
                if (results.rooms[0].length == 0) {
                    return
                }
                results.rooms.map((result) => {
                    body.click((event) => {
                        bodyClick()
                    })
                    $('.roomsjoined').find('.rooms').append(roomsJoined(result))
                })
                $('.roomsjoined').find('.rooms').find('li').click((event) => {
                    ajaxHelper(tailUrl, [results.id, $(event.currentTarget).attr('data-id')], () => {},
                        (result) => {

                        }, (XMLHttpRequest, textStatus, errorThrown) => {

                        })
                })
                body.click((event) => {
                    bodyClick()
                })
            }, (XMLHttpRequest, textStatus, errorThrown) => {

            })
        },
        'members': () => {
            const tailUrl = `${Modes.MEMBERS}/${idCurrentRoom}`
            ajaxHelper(tailUrl, null, () => {
                body.unbind('click')
                $('.roomsjoined').slideDown()
                body.addClass('overHelper')
                actionMenuUser.slideUp()
                body.removeClass('overUser')
            }, (results) => {
                body.click((event) => {
                    bodyClick()
                })
                $($('.roomsjoined').children()[0]).text('Members in group')
                if (results.length == 0) {
                    return
                }
                results.map((result) => {
                    $('.roomsjoined').find('.rooms').append(roomsJoined({
                        id: result.id_user,
                        image: result.avatar,
                        name: result.first_name + (result.last_name == undefined && result.last_name == null ? '' : ` ${result.last_name}`)
                    }))
                })
                $('.roomsjoined').find('.rooms').find('li').click((event) => {
                    ajaxHelper(tailUrl, $(event.currentTarget).attr('data-id'), () => {},
                        (result) => {
                            if (result != -1) {
                                window.location.replace('chat/' + result)
                            } else {
                                roomChat.empty()
                                inputMess.focus()
                                const currentPartner = $(event.currentTarget)
                                roomChat.html(roomChatInfo({
                                    name: currentPartner.find('.room_info_cell').text().trim(),
                                    image: currentPartner.find('.img_cont_mini > img').attr('src'),
                                    offline: 'online'
                                }))
                                msgContainer.empty()
                                idCurrentRoom = null
                                idPartner = idUser
                            }
                        }, (XMLHttpRequest, textStatus, errorThrown) => {

                        })
                })
            }, (XMLHttpRequest, textStatus, errorThrown) => {

            })
        },
        'edit_group_name': () => {
            inputHeplerEvent(`${Modes.EDIT_GROUP_NAME}/${idCurrentRoom}`, 'Enter new group name...')
        },
        'edit_image_group': () => {
            onChange = (event) => {
                var reader = new FileReader();
                reader.readAsDataURL(event.target.files[0])
                reader.onload = () => {
                    $('.review_avatar').attr('src', reader.result)
                }
            }
            $('.upload_image').find('button').click(() => {
                body.unbind('click')
                setTimeout(() => {
                    body.click((event) => {
                        bodyClick()
                    })
                }, 500)
            })
            $('.review_avatar').attr('src', $('.room-info').find('.user_img ').attr('src'))
            $('.review_avatar').click(() => {
                body.unbind('click')
                setTimeout(() => {
                    body.click((event) => {
                        bodyClick()
                    })
                }, 500)
            })
            $('.upload_image').slideDown()
            $('.upload_image_group').attr('action', 'changeroomimage/' + idCurrentRoom)
            body.addClass('overHelper')
            actionMenuUser.slideUp()
            body.removeClass('overUser')
            actionMenuBtnUser.unbind('click')
            body.unbind('click')
            setTimeout(() => {
                actionMenuBtnUser.click((event) => {
                    actionMenuBtnUserClick()
                    return false
                })
                body.click((event) => {
                    bodyClick()
                })
            }, 500)
        },
        'leave_group': () => {
            ajaxHelper(`${Modes.LEAVE_GROUP}/${idCurrentRoom}`, null, () => {}, (result) => {
                const accountsLogged = JSON.parse($.cookie('accounts_logged'))
                accountsLogged[currentUser.id] = -1
                $.cookie('accounts_logged', JSON.stringify(accountsLogged))
                window.location.replace('chat')
            }, (XMLHttpRequest, textStatus, errorThrown) => {

            })
        }
    }

    const inputHeplerEvent = (url, placeholder) => {
        body.unbind('click')
        $('.input_helper').attr('placeholder', placeholder)
        $('.inputmenu').slideDown()
        body.addClass('overHelper')
        actionMenuUser.slideUp()
        body.removeClass('overUser')
        actionMenuBtnUser.unbind('click')
        setTimeout(() => {
            actionMenuBtnUser.click((event) => {
                actionMenuBtnUserClick()
                return false
            })
            body.click((event) => {
                bodyClick()
            })
        }, 500)
        const unbindAndBindBody = () => {
            body.unbind('click')
            setTimeout(() => {
                body.click((event) => {
                    bodyClick()
                })
            }, 500)
        }
        $('.inputmenu').click(unbindAndBindBody)
        $('.inputmenu').focus(unbindAndBindBody)
        $('.submit_action_menu').click(() => {
            ajaxHelper(url, $('.input_helper').val().trim(), () => {}, (result) => {
                hideHelper()
            }, (XMLHttpRequest, textStatus, errorThrown) => {})
        })
    }

    const changeRoomName = (result) => {
        $('.user-room[data-id=' + result.id + ']').find('span').text(result.name)
        $('.input_helper').val('')
        $('.inputmenu').slideToggle()
    }

    $('.cancel_action_menu').click(() => {
        hideHelper()
    })

    actionMenuLi.click((event) => {
        actionMenuContentClick[event.currentTarget.getAttribute('data-id')]()
    })

    const scrollBottom = () => msgBody.scrollTop(msgContainer.children().length * (msgContainer.children().last().height() + 20))

    scrollBottom()

    const changeChat = (room) => {
        inputMess.val('')
        rightActionMenu.empty()
        rightActionMenu.append(actionMenuWhenChangeRoom(room.isGroup))
        $('.action_menu').children().children().click((event) => {
            actionMenuContentClick[event.currentTarget.getAttribute('data-id')]()
        })
        roomChat.empty()
        roomChat.html(roomChatInfo(room))
        msgContainer.empty()
        if (room.messages != undefined && room.messages != null) {
            msgContainer.append(
                room.messages.map(message => {
                    if (message.id_sender != currentUser.id) {
                        return chatPartnerMess(message)
                    }
                    return currentUserMess(message)
                })
            )
        }
        idCurrentRoom = room.id
        scrollBottom()
        const accountsLogged = JSON.parse($.cookie('accounts_logged'))
        accountsLogged[currentUser.id] = room.id
        $.cookie('accounts_logged', JSON.stringify(accountsLogged))
    }

    var roomPaged = 1
    var messPage = 1

    const roomToFirst = (room) => {
        $('.user-room[data-id=' + room.id + ']').remove()
        contacts.prepend(newRoomInRooms(room, idCurrentRoom, currentUser.id))
        $('.user-room[data-id=' + room.id + ']').click((event) => {
            const liRoom = $(event.currentTarget)
            if (event.currentTarget.classList.contains('active')) {
                return false
            }
            $(`li.user.active `).removeClass('active')
            liRoom.addClass('active')
            changeChatById(liRoom.attr('data-id'))
            return true
        })
    }

    const changeChatById = (id) => {
        ajaxHelper(`${Modes.ROOM}/${id}`, null, () => {

        }, (result) => {
            changeChat(result)
        }, (XMLHttpRequest, textStatus, errorThrown) => {
            console.log(errorThrown)
        })
    }

    $('.user-room').click((event) => {
        const liRoom = $(event.currentTarget)
        if (event.currentTarget.classList.contains('active')) {
            return false
        }
    })

    var idMessSend = 0

    const format = (number) => {
        if (number < 10) {
            return '0' + number
        }
        return number
    }

    sendBtn.click((event) => {
        if (inputMess.val().trim() == '') return
        switch (idCurrentRoom) {
            case undefined:
                return
            case null:
            default:
                const contentSend = inputMess.val().trim()
                inputMess.val('')
                const today = new Date()
                const message = {
                    id_before_send: ++idMessSend,
                    content: contentSend,
                    type: 'text',
                    send_time: `${format(today.getHours() < 12 ? today.getHours() : today.getHours() - 12)}:${format(today.getMinutes())} ${format(today.getDate())}/${format(today.getMonth() + 1)}/${today.getFullYear()}`
                }
                msgContainer.append(currentUserMessBeforeSend(message, message.id_before_send))
                scrollBottom()
                ajaxHelper(`${Modes.MESSAGE}/${idCurrentRoom == null ? idPartner : idCurrentRoom}`, message, () => {}, (result) => {}, (XMLHttpRequest, textStatus, errorThrown) => {
                    console.log(errorThrown)
                })
                break
        }
    })

    msgContainer.click(() => {
        inputMess.focus()
    })

    inputMess.bind('keypress', (e) => {
        if (e.keyCode == 13) {
            if (e.shiftKey)
                return true
            sendBtn.click()
            return false
        }
    })
})
