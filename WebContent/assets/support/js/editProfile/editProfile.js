var changeAvatar = null

$(document).ready(() => {
	changeAvatar = (event) => {	
	    var reader = new FileReader();
        reader.readAsDataURL( event.target.files[0])
        reader.onload = ()=>{
        	$('.makup-avatar').attr('src', reader.result)
        }
	}
})