openfire
=================
&nbsp;&nbsp;&nbsp;&nbsp;openfire最主要的功能是实现XMPP服务器，简单来说，openfire为我们提供一个固定的地址，我们只需要向openfire服务器发送标准的XMPP信息（即XML文件流），那么openfire服务器应当给予我们回应，这里的openfire服务器也可以看做一个容器，我们在聊天时，需要在这个服务器上注册一个会话，在会话存在的时间，我们可以实现即时聊天的一些常用功能，比如建立自己的组，添加好友，聊天，以及传送文件等等，同时，openfire服务器也需要实现自己的管理界面，这样openfire服务器也扮演一个web容器的角色。

&nbsp;&nbsp;&nbsp;&nbsp;openfire是开源的实时协作服务器（RTC）,它是基于公开协议XMPP（也成为Jabber）消息的，核心功能可以概括为：连接管理、消息解析、消息路由、消息发送。

&nbsp;&nbsp;&nbsp;&nbsp;openfire具有跨平台的能力，openfire与客户端采用的是C/S架构，一个服务器要负责为连接在其上的客户端提供服务。openfire客户端有spark, pidgin, Miranda IM, iChat等，用户如果自己开发客户端，可以采用遵循GPL的开源Client端API--Smack。

&nbsp;&nbsp;&nbsp;&nbsp;openfire服务器端支持插件开发，如果开发者需要添加新的服务，可以开发出自己的插件后，安装至服务器，就可以提供服务，如查找联系人服务就是以插件的形式提供的。