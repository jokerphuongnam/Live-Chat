//init variable
const doc = $(document)
var body = null
var nameUser = null
var roomId = null

//search view
var contacts = null
var listOfSearch = null
var searchInput = null
var searchBtn = null

//menu view
var actionMenuBtnUser = null
var actionMenuUser = null
var actionMenuBtnChat = null
var actionMenuChat = null
var actionMenuLi = null
var rightActionMenu = null

//message view
var inputMess = null
var roomChat = null
var inputMess = null
var msgContainer = null
var sendBtn = null
var msgBody = null

//info dialog view
var infoDialog = null

doc.ready(() => {
    //init variable
    body = $('body')
    name_user = $('.name_user')

    //search view
    contacts = $('.contacts')
    listOfSearch = $('.listOfSearch')
    searchInput = $('.search')
    searchBtn = $('.input-group-text')

    //menu view
    actionMenuBtnUser = $('.action_menu_btn_user')
    actionMenuUser = $('.action_menu_user')
    actionMenuBtnChat = $('.action_menu_btn_chat')
    actionMenuChat = $('.action_menu_chat')
    actionMenuLi = $('.action_menu').children().children()
    rightActionMenu = $(`.right_action_menu`)

    //message view
    inputMess = $('.input-message')
    roomInfo = $('.room_info')
    roomChat = $('.room-info')
    msgContainer = $('.msg_card_body')
    msgBody = $('.messages_body')
    sendBtn = $('.send_btn')

    //info dialog view
    infoDialog = $('.info-dialog')
})

const currentUserMess = (message) => {
    return `
        <li class="d-flex mb-4 justify-content-end" data-id="${message.id}">
				<div class="msg_container_send">	
                   <span>${message.content}</span>
                <span class="msg_time_send">${message.send_time}</span>
			</div>
	    </li>
    `
}

const currentUserMessBeforeSend = (message, beforeSendId) => {
    return `
        <li class="d-flex mb-4 justify-content-end" data-id="${message.id}" before-send-id="${beforeSendId}">
				<div class="msg_container_send before-send-message">	
                   <span>${message.content}</span>
                <span class="msg_time_send">${message.send_time}</span>
			</div>
	    </li>
    `
}

const chatPartnerMess = (message) => {
    var name = null
    if (message.name_group != undefined) {
        name = `<span class="msg_name">${message.name}</span>`
    }
    return `
    <li
		class="d-flex mb-4 justify-content-start" data-id="${message.id}">		
		<div class="img_cont_mini ">
            <img class="rounded-circle user_image_mini" 
                src="${message.avatar == null ? 'assets/images/Login.png' : message.avatar}">
	    </div>		
		<div class="msg_container">		
            ${name == null ? '' : name}
            <span>${message.content}</span>
            <span class="msg_time">${message.send_time}</span>
		</div>
	</li>
    `
}

const roomChatInfo = (room) => {
        if (!room.isGroup) {
            const status = room.offline == 'online' ? 'Online' : `Offline by ${room.offline}`
            return `
            <div class="img_cont ">
                <img
                    src="${room.avatar == undefined ? 'assets/images/Login.png' : partnerUser.avatar}"
                    class="rounded-circle user_img ">
                    <span class="${room.offline == 'online' ? 'online_icon' : 'offline'}"></span>
            </div>
            <div class="room_info">
                <span class="name_user">${room.name == undefined ? 'Insert Group Name' : room.name}</span>
                <p>
                    ${room.offline == 'online' ? 'Online' : `Offline by ${status}`}
                </p>
            </div>
        `
    }
    return ` 
        <div class="img_cont ">
            <img
                src="${room.image == undefined || room.image == null ? 'assets/images/Login.png' : room.image}"
                   class="rounded-circle user_img ">
        </div>
            <div class="room_info">
            <span class="name_user">${room.image == undefined || room.image == null ? 'Insert Group Name' : room.name}</span>
        </div>
    `
}

const usersSearched = (user) => {
    return `
        <li class="user user-search" data-id="${user.id_user}">
            <div class="listOfSearch-user d-flex bd-highlight ">
                <div class="img_cont_mini ">
                <img src="${user.avatar == undefined || user.avatar == null ? 'assets/images/Login.png' : user.avatar}"
                    class="rounded-circle user_image_mini ">
                </div>
                <div class="room_info_cell ">
                    <span>${user.first_name} ${user.last_name == undefined || user.last_name == null ? '' : user.last_name}</span>
                </div>
            </div>
        </li>
    `
}

const roomsJoined = (result) => {
    return `
        <li class="user user-search" data-id="${result.id}">
            <div class="listOfSearch-user d-flex bd-highlight ">
                <div class="img_cont_mini ">
                <img src="${result.image == undefined || result.image == null ? 'assets/images/Login.png' : result.image}"
                    class="rounded-circle user_image_mini ">
                </div>
                <div class="room_info_cell ">
                    <span>${result.name == undefined || result.name == null ? 'Insert Group Name' : result.name}</span>
                </div>
            </div>
        </li>
    `
}

const lastMess = (message, content, idCurrenUser) => {
    if (message != null && message != undefined) {
        return `
            <p>${message.id_sender == idCurrenUser ? 'Bạn: ' : ''}${content}</p>
            <p class="dot_room_chat ">&sdot;</p>
            <p>${message.send_time}</p>
        `
    }
    return `
        <p>Room chat empty messages</p>
    `
}

const newRoomInRooms = (room, idRoom, idCurrenUser) => {
    const message = (room.messages != undefined || room.messages != null) && room.messages.length != 0 ? room.messages[room.messages.length - 1] : null
    var maxLenght = 0
    var content = ''
    if (message != null) {
        if (message.id_sender == idCurrenUser) {
            maxLenght = 10
        } else {
            maxLenght = 15
        }

        if (message.content > maxLenght) {
            content = message.content.substring(0, maxLenght)
        } else {
            content = message.content
        }
    }
    return `
        <li class="user user-room ${idRoom == room.id ? 'active' : ''}" data-id="${room.id}">
            <div class="d-flex bd-highlight ">
                <div class="img_cont ">
                    <img src="${room.image == undefined || room.image == null ? 'assets/images/Login.png' : room.image}" class="rounded-circle user_img ">
                </div>
                <div class="room_info_cell ">
                    <span>${room.name == undefined || room.name == null ? 'Insert Group Name' : room.name}</span>
                    <div>
                        ${lastMess(message, content, idCurrenUser)}
                    </div>
                </div>
            </div>
        </li>
    `
}

const actionMenuWhenChangeRoom = (isGroup) => {
    if (!isGroup) {
        return `
            <li data-id="edit_nick_name"><i class="fas fa-pen-nib"></i>Edit nickname</li>
            <li data-id="add_group"><i class="fas fa-plus "></i> Add to group</li>
        `
    }
    return `
        <li data-id="edit_nick_name"><i class="fas fa-pen-nib"></i>Edit nickname</li>
        <li data-id="members"><i class="fas fa-users"></i>Members</li>
        <li data-id="edit_group_name"><i class="fas fa-pen-nib"></i>Edit group name</li>
        <li data-id="edit_image_group"><i class=" fas fa-user-circle "></i>Change image group</li>
        <li data-id="leave_group"><i class="fas fa-sign-out-alt"></i>Leave group</li>
    `
}