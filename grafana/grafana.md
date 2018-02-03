grafana+influxdb+collectd
=================
&nbsp;&nbsp;&nbsp;&nbsp;granfana是一个open source的图形化数据展示工具，可以自定义datasource，自定义报表、显示数据等。

&nbsp;&nbsp;&nbsp;&nbsp;influxdb是一个开源的分布式时序、时间和指标数据库，使用go语言编写，无需外部依赖。本文将需要监控的数据写入influxdb，利用granfana展示出来。

&nbsp;&nbsp;&nbsp;&nbsp;collectd是C语言编写的一个系统性能采集工具。本文部署搭建的环境将collectd采集的数据写入influxdb，利用granfana展示出系统性能指标。

&nbsp;&nbsp;&nbsp;&nbsp;另外，为了让大家能够在自己的系统中，使用influxdb采集数据，写了一个功能比较全的采集模块，稍微改造下，就可以应用在各自应用中，作为应用监控数据采集模块。


