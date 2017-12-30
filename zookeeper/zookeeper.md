zookeeper
=================
&nbsp;&nbsp;&nbsp;&nbsp;zookeeper是一个分布式的，开放源码的分布式应用程序协调服务，它包含一个简单的原语集，分布式应用程序可以基于它实现同步服务，配置维护和命名服务等。在分布式应用中，需要有一种可靠的、可扩展的、分布式的、可配置的协调机制来统一系统的状态，zookeeper目的就在于此。zookeeper从设计模式角度来看，是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理大家都关心的数据，然后接受观察者的注册，一旦这些数据的状态发生变化，zookeeper就将负责通知已经在 Zookeeper 上注册的那些观察者做出相应的反应。

# 1. zookeeper安装

## 1.1 独立模式（standalone）

- 1.解压下载的压缩包zookeeper.tar.gz，进入conf目录，复制一份zoo_sample.cfg配置文件，命名为zoo.cfg，修改配置文件：
~~~txt
tickTime=2000
initLimit=10
syncLimit=5
dataDir=D:\workspace\middleware-building\.build\zookeeper\zk1\data
clientPort=2181
~~~ 

- 2.进入bin目录，启动zookeeper：
~~~sh
zkServer.cmd
~~~

- 3.客户端连接zookeeper：
~~~sh
zkCli.cmd -server 127.0.0.1:2181
~~~

## 1.2 复制模式（replicated）
&nbsp;&nbsp;&nbsp;&nbsp;使用单机搭建伪集群模式的zookeeper集群。zookeeper通过复制来实现高可用性,只要集合体中半数以上的机器处于可用状态,它就能够保证服务继续。

- 1. 搭建3个zookeeper节点，解压下载的压缩包zookeeper.tar.gz，复制三份，分别放在同一个目录下，复制zoo_sample.cfg配置文件，重命名为zoo.cfg，修改配置文件分别如下：

&nbsp;&nbsp;&nbsp;&nbsp;第一个节点：
~~~txt
tickTime=2000
initLimit=10
syncLimit=5
clientPort=2181
dataDir=D:\workspace\middleware-building\.build\zookeeper\zk2\data\zk1
zk1=localhost:2887:3887
zk2=localhost:2888:3888
zk3=localhost:2889:3889
~~~ 

&nbsp;&nbsp;&nbsp;&nbsp;第二个节点：
~~~txt
tickTime=2000
initLimit=10
syncLimit=5
clientPort=2182
dataDir=D:\workspace\middleware-building\.build\zookeeper\zk2\data\zk2
zk1=localhost:2887:3887
zk2=localhost:2888:3888
zk3=localhost:2889:3889
~~~ 

&nbsp;&nbsp;&nbsp;&nbsp;第三个节点：
~~~txt
tickTime=2000
initLimit=10
syncLimit=5
clientPort=2183
dataDir=D:\workspace\middleware-building\.build\zookeeper\zk2\data\zk3
zk1=localhost:2887:3887
zk2=localhost:2888:3888
zk3=localhost:2889:3889
~~~ 

- 2.分别进入bin目录，启动三个zookeeper：
~~~sh
zkServer.cmd
~~~

- 3.测试集群同步，分别用三个客户端连接三个节点：
&nbsp;&nbsp;&nbsp;&nbsp;客户端连接第一个节点：
~~~sh
zkCli.cmd -server 127.0.0.1:2181
~~~

&nbsp;&nbsp;&nbsp;&nbsp;客户端连接第二个节点：
~~~sh
zkCli.cmd -server 127.0.0.1:2182
~~~

&nbsp;&nbsp;&nbsp;&nbsp;客户端连接第三个节点：
~~~sh
zkCli.cmd -server 127.0.0.1:2183
~~~

&nbsp;&nbsp;&nbsp;&nbsp;在第一个节点的客户端中添加一个node：
~~~sh
create /test 123
~~~

&nbsp;&nbsp;&nbsp;&nbsp;在第二个节点的客户端可以查看到第一个节点添加的node：
~~~sh
ls /
~~~
