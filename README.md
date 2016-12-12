Addon Space Management Notification
=============================

In eXo, as space manager, it is easy to miss a join request from a user. In fact, you have to go in the space, Space settings, and then you see the user requests list. So if you manage few space, it is a mess to not miss a request.
This addon will collect all requests in space you manage, and display little notification on homepage for each request. Then you will able to accept or refuse all theses request directly on the home page of your intranet.

![ScreenShot](https://raw.githubusercontent.com/exo-addons/space-management-notification/master/assets/spaceNotification.png)

Build & install
=================

Checkout the code, go at root folder and type 

    mvn clean install
  
In packaging/target, a zip is created. Copy it and unzip it in your eXo installation, in extensions folder. Then type

    ./extension.sh -i spaceManagementNotification

to deploy the portlet.

Start your exo server, log with an administrator and go in "Administration" -> "Applications" menu. Add the portlet "Notification for space management" in a category. Then go on your home page and add the portlet on the right column. 

Congratulations, all your space managers can now easily validate or refuse users requests.
