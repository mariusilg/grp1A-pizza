
$("#trigger").click(function() {
    //checkIfUserExists("test")
    checkUser();
});

/*function checkIfUserExists(username) {
    var ajaxCallBack = {
        success : onSuccess,
        error : onError
    }
    jsRoutes.controllers.JavascriptRoutes.checkUser("TEST").ajax(ajaxCallBack);


}

var  onSuccess = function(data) {
    $('#content').html(data)

}

var onError = function(error) {
    alert(error + "error");
}*/

$(".userInfoForm[name=UserName]").keyup(function(){
        checkName(this)
});

$(".editUserForm[name=UserName]").keyup(function(){
    checkOtherName($(".editUserForm[name=ID]"), this)
});

$(".userInfoForm[name=Email]").keyup(function(){
    checkEmail(this)
});
$(".editUserForm[name=Email]").keyup(function(){
    checkOtherEmail($(".editUserForm[name=ID]"), this)
});

function checkName(username) {
    jsRoutes.controllers.JavascriptRoutes.checkName($(username).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageUser();
            } else {
                console.log("true");
            }
        }
    });
}

function checkOtherName(id, username) {
    jsRoutes.controllers.JavascriptRoutes.checkOtherName($(id).val(), $(username).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageUser();
            } else {
                console.log("true");
            }
        }
    });
}

function checkEmail(email) {
    jsRoutes.controllers.JavascriptRoutes.checkEmail($(email).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageMail();
            } else {
                console.log("true");
            }
        }
    });
}

function checkOtherEmail(id, email) {
    jsRoutes.controllers.JavascriptRoutes.checkOtherEmail($(id).val(), $(email).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageMail();
            } else {
                console.log("true");
            }
        }
    });
}



function renderErrorMessageUser() {
    console.log("false");
    $("#contentContainer").prepend("<div class='alert alertUser'><span class='closebtn' onclick='$(this).parent().remove();'>&times;</span>Username bereits vergeben!</div>");
}

function renderErrorMessageMail() {
    console.log("false");
    $("#contentContainer").prepend("<div class='alert alertUser'><span class='closebtn' onclick='$(this).parent().remove();'>&times;</span>Email bereits vorhanden!</div>");
}