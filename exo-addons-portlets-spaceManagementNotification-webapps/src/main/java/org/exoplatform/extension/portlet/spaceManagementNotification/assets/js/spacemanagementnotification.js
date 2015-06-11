require( ["SHARED/jquery"], function ($)
{
    $(function() {
        var acceptlabel;
        var declinelabel;

        var waitingRequestLabel;
        var spacelabel;
        var publiclabel;
        var privatelabel;


        $(".notificationlabel").each(function () {
            acceptlabel = $(this).data("acceptlabel");
            declinelabel = $(this).data("declinelabel");

            waitingRequestLabel = $(this).data("waitingrequestlabel");
            spacelabel = $(this).data("spacelabel");
            publiclabel = $(this).data("publiclabel");
            privatelabel = $(this).data("privatelabel");

        });

        var manageDisplay = function () {
            var i = 0;
            $(".spaceNotifLi").each(function () {
                var ul = $(this).children("ul:first");
                i += ul.children().length;
                if (!$(this).is(':visible')) {
                    $(this).fadeIn(500, function () {
                    });
                }
                return  ((i) < 4);
            });
        }

        var initNotifications = function () {
            $.getJSON("/rest/spacemanagementinformation/getRequestsToValidate", function (items) {
                var counter = 0;
                if (items.length > 0) {
                    $("#SpaceManagementPortlet").show();
                }

                var reversedItems = items.slice().reverse();
                $.each(reversedItems, function (i, item) {
                    link = "";

                    if (counter == 0)
                        link += "<li class='clearfix spaceNotifLi' id='" + item.spaceId + "'>";
                    else if (counter < 4)
                        link += "<li class='clearfix topBar spaceNotifLi' id='" + item.spaceId + "'>";

                    else
                        link += "<li class='clearfix spaceNotifLi' style='display:none;' id='" + item.spaceId + "'>";
                    var spaceAvatar;
                    if (item.spaceAvatarUrl == undefined)
                        spaceAvatar = "/eXoSkin/skin/images/themes/default/social/skin/ShareImages/UserAvtDefault.png";
                    else
                        spaceAvatar = item.spaceAvatarUrl;
                    link += "<div class='spaceInvitePicture pull-left avatarXSmall'><img src='" + spaceAvatar + "'></div>";
                    link += "<div class='spaceInviteInfo'>";
                    link += "<div class='spaceInviteName'>" + item.displayName + "</div>";
                    if (item.spaceRegistration == "open")
                        visibility = publiclabel;
                    else
                        visibility = privatelabel;
                    if (spacelabel == "Space")
                        link += "<div class='spaceproperties'><div class='spacevisibility'><i class='uiIconSocGroup uiIconSocLightGray'></i> " + visibility + " " + spacelabel + " - " + item.pendingUsers.length + " " + waitingRequestLabel + "</div></div>"
                    else
                        link += "<div class='spaceproperties'><div class='spacevisibility'><i class='uiIconSocGroup uiIconSocLightGray'></i> " + spacelabel + " " + visibility + " - " + item.pendingUsers.length + " " + waitingRequestLabel + "</div></div>"
                    link += "</div>";
                    var pendingUsers = item.pendingUsers;
                    link += "<ul class='requestedUsers' id='requestedUsers-" + item.spaceId + "'>";

                    link += "</ul>";

                    link += "</li>";

                    $("#requestsNotifications").append(link);

                    $.each(pendingUsers, function (j, user) {
                        var peopleAvatar;
                        var userLink = "";
                        if (user.userAvatarUrl == undefined)
                            peopleAvatar = "/eXoSkin/skin/images/themes/default/social/skin/ShareImages/UserAvtDefault.png";
                        else
                            peopleAvatar = user.userAvatarUrl;

                        userLink += "<li class='clearfix' id='" + item.spaceId + "-" + user.username + "'><div class='peopleInvitePicture pull-left avatarXSmall'><img src='" + peopleAvatar + "'></div>";
                        userLink += "<div class='peopleInviteInfo'>";

                        userLink += "<div class='peopleInviteName'><div class='name'>" + user.fullName + "</div></div>";
                        userLink += "<div style='display:none;' class='peopleInviteAction' ><a class='connect btn-primary btn btn-mini' href='#' onclick='return false'>" + acceptlabel + "</a><a class='refuse btn btn-mini' href='#' onclick='return false'>" + declinelabel + "</a></div>";


                        /*if (item.senderPosition != undefined)
                         link += "<div class='peopleInvitePosition'>"+item.senderPosition+"</div>";*/
                        userLink += "</div></li>";


                        $("#requestedUsers-" + item.spaceId).append(userLink);
                        $("#" + item.spaceId + "-" + user.username).mouseover(function () {
                            var $item = $(this);
                            $item.find(".peopleInviteAction").show();
                        });
                        $("#" + item.spaceId + "-" + user.username).mouseout(function () {
                            var $item = $(this);
                            $item.find(".peopleInviteAction").hide();
                        });

                        $("#" + item.spaceId + "-" + user.username + " a.refuse").live("click", function () {
                            $.getJSON("/rest/spacemanagementinformation/request/deny/" + item.spaceId + "/" + user.username, null);

                            if ($("#requestedUsers-" + item.spaceId).children().length == 1) {
                                //plus de user a valider dans spaceId
                                if ($("#requestsNotifications").children().length == 1) {
                                    //plus d'espace
                                    $("#SpaceManagementPortlet").fadeOut(500, function () {
                                        $("#" + item.spaceId).remove();
                                        $("#SpaceManagementPortlet").hide();

                                    });
                                } else {
                                    $("#" + item.spaceId).fadeOut(500, function () {
                                        var count = parseInt($("#inviteCounterNotification").html());
                                        $("#inviteCounterNotification").html(count - 1);
                                        //$('#SpaceManagementPortlet li:hidden:first').fadeIn(500, function() {});
                                        $("#" + item.spaceId).remove();
                                        manageDisplay();

                                    });
                                }
                            }
                            else {
                                $("#" + item.spaceId + "-" + user.username).fadeOut(500, function () {
                                    $("#" + item.spaceId + "-" + user.username).remove();
                                    var count = parseInt($("#inviteCounterNotification").html());
                                    $("#inviteCounterNotification").html(count - 1);
                                    //$("#"+item.spaceId+" li:hidden:first").fadeIn(500, function() {});
                                    manageDisplay();

                                });
                            }
                        });


                        $("#" + item.spaceId + "-" + user.username + " a.connect").live("click", function () {
                            $.getJSON("/rest/spacemanagementinformation/request/confirm/" + item.spaceId + "/" + user.username, null);

                            if ($("#requestedUsers-" + item.spaceId).children().length == 1) {
                                //plus de user a valider dans spaceId
                                if ($("#requestsNotifications").children().length == 1) {
                                    //plus d'espace
                                    $("#SpaceManagementPortlet").fadeOut(500, function () {
                                        $("#" + item.spaceId).remove();
                                        $("#SpaceManagementPortlet").hide();

                                    });
                                } else {
                                    $("#" + item.spaceId).fadeOut(500, function () {
                                        var count = parseInt($("#inviteCounterNotification").html());
                                        $("#inviteCounterNotification").html(count - 1);
                                        //$('#SpaceManagementPortlet li:hidden:first').fadeIn(500, function() {});
                                        $("#" + item.spaceId).remove();
                                        manageDisplay();

                                    });
                                }
                            }
                            else {
                                $("#" + item.spaceId + "-" + user.username).fadeOut(500, function () {
                                    $("#" + item.spaceId + "-" + user.username).remove();
                                    var count = parseInt($("#inviteCounterNotification").html());
                                    $("#inviteCounterNotification").html(count - 1);
                                    //$("#"+item.spaceId+" li:hidden:first").fadeIn(500, function() {});
                                    manageDisplay();

                                });
                            }
                        });

                        counter++;

                        $("#inviteCounterNotification").html(counter);

                    });


                });
            });
        }

        initNotifications();

    });
});
