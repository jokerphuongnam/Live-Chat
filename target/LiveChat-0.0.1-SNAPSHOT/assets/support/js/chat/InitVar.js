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

//rooms view
var userLi = null

//menu view
var actionMenuBtnUser = null
var actionMenuUser = null
var actionMenuBtnChat = null
var actionMenuChat = null
var actionMenuLi = null

//message view
var inputMess = null
var roomChat = null
var inputMess = null
var msgContainer = null
var sendBtn = null

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

    //rooms view
    userLi = $('.user')

    //menu view
    actionMenuBtnUser = $('.action_menu_btn_user')
    actionMenuUser = $('.action_menu_user')
    actionMenuBtnChat = $('.action_menu_btn_chat')
    actionMenuChat = $('.action_menu_chat')
    actionMenuLi = $('.action_menu').children().children()

    //message view
    inputMess = $('.input-message')
    roomInfo = $('.room_info')
    msgContainer = $('.msg_card_body')
    sendBtn = $('.send_btn')

    //info dialog view
    infoDialog = $('.info-dialog')
})

const currentUserMess = (message, room) => {
    return `
        <li class="d-flex justify-content-end mb-4 ">
            <div class="msg_container_send ">
                 <div> ${message.message}</div>
                 <span class="msg_time_send ">${message.sender}</span>
            </div>
            <div class="img_cont_mini ">
                  <img src="${room.image == null ? null : room.image}" class="rounded-circle user_image_mini ">
            </div>
       </li>
     `
}

const chatPartnerMess = (message, room) => {
    return `
       <li class="d-flex justify-content-start mb-4 ">
           <div class="img_cont_mini ">
               <img src="${room.image == null ? null : room.image}" class="rounded-circle user_image_mini ">
           </div>
           <div class="msg_container ">
               <div> ${message.message}</div> 
               <span class="msg_time ">${message.sender}</span>
           </div>
       </li>
    `
}

const roomChatInfo = (roomName) => {
    return ` 
        <div class = "room_info ">
            <span>${roomName}</span>
        </div>
    `
}