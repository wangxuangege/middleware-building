grafana+influxdb+collectd
=================
&nbsp;&nbsp;&nbsp;&nbsp;granfana是一个open source的图形化数据展示工具，可以自定义datasource，自定义报表、显示数据等。

&nbsp;&nbsp;&nbsp;&nbsp;influxdb是一个开源的分布式时序、时间和指标数据库，使用go语言编写，无需外部依赖。本文将需要监控的数据写入influxdb，利用granfana展示出来。

&nbsp;&nbsp;&nbsp;&nbsp;collectd是C语言编写的一个系统性能采集工具。本文部署搭建的环境将collectd采集的数据写入influxdb，利用granfana展示出系统性能指标。

&nbsp;&nbsp;&nbsp;&nbsp;另外，为了让大家能够在自己的系统中，使用influxdb采集数据，写了一个功能比较全的采集模块，稍微改造下，就可以应用在各自应用中，作为应用监控数据采集模块。

# 1. influxdb

&nbsp;&nbsp;&nbsp;&nbsp;本人下载使用的是当前最新版本的influxdb-1.4.2_linux_amd64.tar.gz，如果大家想搭建有web控制台的influxdb，需要选择低版本的，此处不做介绍，influxdb官方在高版本不知为啥去掉了该模块，但是利用influxdb命令行控制台依旧可以完成所有工作，而且可以加深大家对influxdb的使用与理解。

## 1.1 安装

- &nbsp;1. 解压
~~~sh
tar -xvf influxdb-1.4.2_linux_amd64.tar.gz
~~~

- &nbsp;2. 解压包中，influxdb配置文件在etc/influxdb/influxdb.conf中（暂时可能不需要改造，后面collectd收集时候，需要更改配置），但是此处有一个“大坑”，该配置无论怎么更改，都不会起作用，因为influxdb启动后，依赖的配置在系统目录层次下/etc/influxdb/influxdb.conf，此处浪费我很多时间，所以大家新建/etc/influxdb目录，然后将influxdb.conf拷贝到该目录下。
~~~sh
su root
mkdir /etc/influxdb
cp /home/xqhuang/workspace/env/influxdb/influxdb-1.4.2-1/etc/influxdb/influxdb.conf /etc/influxdb/influxdb.conf
~~~

- &nbsp;3. 进入到/home/xqhuang/workspace/env/influxdb/usr/bin目录下，启动influxd，即可启动influxdb。
~~~sh
sudo nohup ./influxd &
~~~

- &nbsp;4. 连上influxdb客户端，测试influxdb使用情况。
~~~sh
./influx
~~~

## 1.2 influxdb常用概念与命令

&nbsp;&nbsp;&nbsp;&nbsp;influxdb时序数据库有一套功能强大的类sql语法，但是操作好该语法，需要了解influxdb时序数据库的一些概念。

#### 1.2.1 常用概念

&nbsp;&nbsp;&nbsp;&nbsp;influxdb时序数据库有部分概念与传统数据库概念相似：
<table>
  <tr>
    <th width=40%, bgcolor=yellow >influxdb概念</th>
    <th width=60%, bgcolor=yellow>传统数据库概念</th>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> database  </td>
    <td> 数据源 </td>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> measurement  </td>
    <td> 数据库中的表 </td>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> point </td>
    <td> 表中的一行数据 </td>
  </tr>
</table>

&nbsp;&nbsp;&nbsp;&nbsp;influxdb独有的一些概念：point和series。

&nbsp;&nbsp;&nbsp;&nbsp;point由时间戳（time）、数据（field）、标签（tags）组成，point相当于传统传统数据库里的一行数据，如下表所示：
<table>
  <tr>
    <th width=40%, bgcolor=yellow >point属性</th>
    <th width=60%, bgcolor=yellow>描述</th>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> time </td>
    <td> 每个数据记录生产时间，是数据库的主索引（若插入时候没有则自动生产) </td>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> tag </td>
    <td> 各种有索引的属性 </td>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> field </td>
    <td> 没有索引的属性，各种记录值 </td>
  </tr>
</table>

&nbsp;&nbsp;&nbsp;&nbsp;series表示某张表里面的数据，可以在图表上面画出几条线：通过tag排列组合算出来的。下图为collectd统计cpu_value的series，如下所示：
~~~sh
xqhuang@linux-koj9:~/workspace/env/influxdb/influxdb-1.4.2-1/usr/bin> ./influx
Connected to http://localhost:8086 version 1.4.2
InfluxDB shell version: 1.4.2
> use collectd;
Using database collectd
> show measurements;
name: measurements
name
----
cpu_value
interface_rx
interface_tx
load_longterm
load_midterm
load_shortterm
memory_value
> show series from cpu_value;
key
---
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=idle
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=interrupt
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=nice
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=softirq
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=steal
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=system
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=user
cpu_value,host=linux-koj9.suse,instance=0,type=cpu,type_instance=wait
~~~

#### 1.2.2 常用命令

<table>
  <tr>
    <th width=40%, bgcolor=yellow >命令</th>
    <th width=60%, bgcolor=yellow>操作</th>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> 显示数据库 </td>
    <td>
    ~~~sh
    > show databases;
    name: databases
    name
    ----
    _internal
    collectd
    test
    ~~~
    </td>
  </tr>
  <tr>
    <td bgcolor=#eeeeee> 新建数据库 </td>
    <td> 
     ~~~sh
     
     ~~~
     </td>
  </tr>
</table>